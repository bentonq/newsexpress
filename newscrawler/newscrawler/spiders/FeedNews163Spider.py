from scrapy.selector import HtmlXPathSelector
from scrapy.http import Request

from uuid import uuid3, NAMESPACE_URL

import feedparser

from newscrawler.spiders.BaseFeedSpider import BaseFeedSpider
from newscrawler.items import NewsItem


class FeedNews163Spider(BaseFeedSpider):

    name = 'feed_news_163'
    allowed_domains = ['news.163.com', 'rss.feedsportal.com']
    start_urls = ['http://news.163.com/special/00011K6L/rss_newstop.xml']

    def parse_feed(self, response):
        hxs = HtmlXPathSelector(response)
        content_list = hxs.select("//h1[@id='h1title']").extract()
        content_list.extend(hxs.select("//div[@id='endText']/p").extract())
        string_joiner = ''

        item = NewsItem()
        item['id'] = uuid3(NAMESPACE_URL, response.url).hex
        item['header'] = response.headers
        item['doc'] = string_joiner.join(content_list)

        return item
