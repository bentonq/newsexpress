from newscrawler.spiders.basefeed import BaseFeedSpider


class Feed36krSpider(BaseFeedSpider):

    name = 'feed_36kr'
    allowed_domains = ['www.36kr.com']
    start_urls = ['http://www.36kr.com/feed']
    documents_selects = [
            "//h1[@class='entry-title sep10']",
            "//div[@class='mainContent sep-10']/p",
    ]

    def parse_feed(self, response):
        self.update_feed_table(response)
        return self.gen_response_item(response)

