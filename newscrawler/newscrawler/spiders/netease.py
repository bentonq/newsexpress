from scrapy.selector import HtmlXPathSelector
from scrapy.spider import BaseSpider

from newscrawler.items import NewsItem

class NeteaseSpider(BaseSpider):
    name = "netease"
    allowed_domains = ["news.163.com"]
    start_urls = ["http://news.163.com/13/0902/02/97O2909Q00014AED.html"]

    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        i = NewsItem()
        i["id"] = response.url
        i["header"] = response.headers
        i["html"] = hxs.select("//h1[@id='h1title']").extract()
        i["html"].extend(hxs.select("//div[@id='endText']/p").extract())
        return i
