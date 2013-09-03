from scrapy.selector import HtmlXPathSelector
from scrapy.spider import BaseSpider

from newscrawler.items import NewsItem

from uuid import uuid3, NAMESPACE_URL

class NeteaseSpider(BaseSpider):
    name = 'netease'
    allowed_domains = ['news.163.com']
    start_urls = ['http://news.163.com/13/0902/02/97O2909Q00014AED.html',
                  'http://news.163.com/13/0903/09/97RCQTUG00014JB5.html',
                  'http://news.163.com/13/0903/11/97RHS2NS0001121M.html']

    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        content_list = hxs.select("//h1[@id='h1title']").extract()
        content_list.extend(hxs.select("//div[@id='endText']/p").extract())
        string_joiner = ''

        item = NewsItem()
        item['id'] = uuid3(NAMESPACE_URL, response.url).hex
        item['header'] = response.headers.to_string()
        item['doc'] = string_joiner.join(content_list)

        return item
