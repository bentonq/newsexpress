from twisted.internet import reactor
from scrapy.crawler import Crawler
from scrapy.settings import Settings
from scrapy import log, signals

from newscrawler.spiders import defaultfeed

crawler = Crawler(Settings())
crawler.signals.connect(reactor.stop, signal=signals.spider_closed)
crawler.configure()
crawler.crawl(DefaultFeedSpider())
crawler.start()
log.start()
reactor.run()
