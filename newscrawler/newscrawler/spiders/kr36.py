from scrapy.selector import HtmlXPathSelector
from scrapy.spider import BaseSpider

from newscrawler.items import NewsItem

from uuid import uuid3, NAMESPACE_URL

class Kr36Spider(BaseSpider):
    name = 'kr36'
    allowed_domains = ['www.36kr.com']
    start_urls = ['http://www.36kr.com/p/205962.html']

    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        content_list = hxs.select("//h1[@class='entry-title sep10']").extract()
        content_list.extend(hxs.select("//div[@class='mainContent sep-10']/p").extract())
        string_joiner = ''

        item = NewsItem()
        item['id'] = uuid3(NAMESPACE_URL, response.url).hex
        item['header'] = response.headers
        item['doc'] = string_joiner.join(content_list)

        return item
