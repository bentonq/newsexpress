from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey, Table, MetaData
from sqlalchemy.orm import sessionmaker, mapper
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.exc import IntegrityError
from scrapy.spider import BaseSpider
from scrapy.http import Request
import feedparser


engine = create_engine('sqlite:///data/feeds.db')
session = sessionmaker(bind=engine)()
metadata = MetaData()

class Feed(object):
    def __init__(self, url):
        self.url = url

class BaseFeedSpider(BaseSpider):

    def __init__(self, name=None, **wargs):
        super(BaseFeedSpider, self).__init__(name, **wargs)
        feed_table = Table(self.name, metadata,
            Column('id', Integer, primary_key=True),
            Column('url', String, unique=True))
        mapper(Feed, feed_table)
        metadata.create_all(engine)

    def parse(self, response):
        feed = feedparser.parse(response.body)
        for entry in feed.entries:
            feed = Feed(entry.link)
            try:
                session.add(feed)
                session.commit()
                yield Request(entry.link, callback=self.parse_feed,
                              meta={'news_title': entry.title})
            except IntegrityError:
                session.rollback()

