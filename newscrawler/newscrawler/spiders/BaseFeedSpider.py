from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from scrapy.spider import BaseSpider
from scrapy.http import Request
import feedparser


engine = create_engine('sqlite:///newsexpress.db')
session = sessionmaker(bind=engine)()

Base = declarative_base()


class BaseFeedSpider(BaseSpider):

    def parse(self, response):
        feed = feedparser.parse(response.body)
        for entry in feed.entries:
            yield Request(entry.link, callback=self.parse_feed)

