from scrapy.spider import BaseSpider
from scrapy.http import Request
import feedparser

class DefaultFeedSpider(BaseSpider):
    name = 'defaultfeed'
    start_urls = [
        'http://www.36kr.com/feed',
        'http://news.163.com/special/00011K6L/rss_newstop.xml',
    ]

    def parse(self, response):
        feed = feedparser.parse(response.body)
        for entry in feed.entries:
            yield Request(entry.link)

