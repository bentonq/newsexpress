from scrapy.selector import HtmlXPathSelector
from scrapy.spider import BaseSpider

from newscrawler.items import NewsItem

from uuid import uuid3, NAMESPACE_URL

class NeteaseSpider(BaseSpider):
    name = 'netease'
    allowed_domains = ['news.163.com']
    start_urls = ['http://news.163.com/13/0904/15/97UH2NVF0001124J.html']

    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        content_list = hxs.select("//h1[@id='h1title']").extract()
        content_list.extend(hxs.select("//div[@id='endText']/p").extract())
        string_joiner = ''

        item = NewsItem()
        item['id'] = uuid3(NAMESPACE_URL, response.url).hex
        item['header'] = response.headers
        item['doc'] = string_joiner.join(content_list)

        return item
