from newscrawler.settings import DOCUMENTS_DIR
from sqlalchemy import create_engine, Column, String, Integer, BigInteger, DateTime, Table, MetaData
from sqlalchemy.orm import sessionmaker, mapper
from sqlalchemy.exc import IntegrityError
from scrapy.spider import BaseSpider
from scrapy.http import Request
from datetime import datetime
from time import mktime
from uuid import uuid3, NAMESPACE_URL
import feedparser


class FeedColumn(object):

    def __init__(self, link, crawl_time):
        self.link = link
        self.crawl_time = crawl_time
        self.status = 0
        self.docid = ''
        self.url = ''


class BaseFeedSpider(BaseSpider):

    def __init__(self, name=None, **wargs):
        super(BaseFeedSpider, self).__init__(name, **wargs)
        engine = create_engine('sqlite:///%s/feeds.db' % DOCUMENTS_DIR)
        metadata = MetaData()
        feed_table = Table(self.name, metadata,
                Column('link', String, primary_key=True),
                Column('crawl_time', DateTime),
                Column('status', Integer),
                Column('docid', String),
                Column('url', String))
        mapper(FeedColumn, feed_table)
        metadata.create_all(engine)
        self.session = sessionmaker(bind=engine)()

    def parse(self, response):
        feed = feedparser.parse(response.body)
        for entry in feed.entries:
            feed_column = FeedColumn(entry.link, datetime.now())
            try:
                self.session.add(feed_column)
                self.session.commit()
                rawinfo = { 'feed_column': feed_column }
                if hasattr(entry, 'title'):
                    rawinfo['title'] = entry.title
                if hasattr(entry, 'author'):
                    rawinfo['author'] = entry.author
                if hasattr(entry, 'summary'):
                    rawinfo['summary'] = entry.summary
                if hasattr(entry, 'published_parsed'):
                    rawinfo['published_time'] = datetime.fromtimestamp(mktime(entry.published_parsed))
                if hasattr(entry, 'updated_parsed'):
                    rawinfo['updated_time'] = datetime.fromtimestamp(mktime(entry.updated_parsed)),
                yield Request(entry.link, callback=self._pre_parse_feed, meta=rawinfo)
            except IntegrityError:
                self.session.rollback()

    def _pre_parse_feed(self, response):
        feed_column = response.meta['feed_column']
        feed_column.status = response.status
        feed_column.docid = uuid3(NAMESPACE_URL, response.url).hex
        feed_column.url = response.url
        self.session.commit()
        self.parse_feed(response)


