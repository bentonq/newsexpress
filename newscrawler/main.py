from twisted.internet import reactor
from scrapy.crawler import Crawler
from scrapy.utils.project import get_project_settings
from scrapy import log, signals

from newscrawler.spiders.defaultfeed import DefaultFeedSpider
from newscrawler.spiders.kr36 import Kr36Spider

spider = DefaultFeedSpider()
crawler = Crawler(get_project_settings())
crawler.signals.connect(reactor.stop, signal=signals.spider_closed)
crawler.configure()
crawler.crawl(spider)
crawler.start()
log.start()
reactor.run()
