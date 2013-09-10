from scrapy.spider import BaseSpider
from scrapy.http import Request
import feedparser

class BaseFeedSpider(BaseSpider):

    def parse(self, response):
        feed = feedparser.parse(response.body)
        for entry in feed.entries:
            yield Request(entry.link, callback=self.parse_feed)

