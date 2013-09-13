from newscrawler.spiders.basefeed import BaseFeedSpider


class FeedNews163Spider(BaseFeedSpider):

    name = 'feed_news_163'
    allowed_domains = ['news.163.com', 'rss.feedsportal.com']
    start_urls = ['http://news.163.com/special/00011K6L/rss_newstop.xml']
    document_selects = [
            "//h1[@id='h1title']",
            "//div[@id='endText']/p",
    ]

    def parse_feed(self, response):
        self.update_feed_table(response)
        return self.gen_response_item(response)

