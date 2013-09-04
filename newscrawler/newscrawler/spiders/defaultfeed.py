from scrapy.http import Request
from scrapy.contrib.spiders import XMLFeedSpider

class DefaultFeedSpider(XMLFeedSpider):
    name = 'defaultfeed'
    start_urls = ['http://www.36kr.com/feed']
    itertag = 'item'

    def parse_node(self, response, node):
        link_value = node.select('link').extract()
        print('----------------------------------------------')
        url = str(list(link_value[0])[17:-8])
        print(url)
        return Request(url)
