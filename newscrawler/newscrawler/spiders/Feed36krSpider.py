from scrapy.selector import HtmlXPathSelector
from scrapy.http import Request

from uuid import uuid3, NAMESPACE_URL

import feedparser

from newscrawler.spiders.BaseFeedSpider import BaseFeedSpider
from newscrawler.items import NewsItem


class Feed36krSpider(BaseFeedSpider):
    name = 'feed_36kr'
    allowed_domains = ['www.36kr.com']
    start_urls = ['http://www.36kr.com/feed']

    def parse_feed(self, response):
        hxs = HtmlXPathSelector(response)
        content_list = hxs.select("//h1[@class='entry-title sep10']").extract()
        content_list.extend(hxs.select("//div[@class='mainContent sep-10']/p").extract())
        string_joiner = ''

        item = NewsItem()
        item['id'] = uuid3(NAMESPACE_URL, response.url).hex
        item['header'] = response.headers
        item['doc'] = string_joiner.join(content_list)

        return item
