from bs4 import BeautifulSoup

class NewscrawlerPipeline(object):
    def process_item(self, item, spider):
        html = item['html']
        soup = BeautifulSoup('<doc></doc>', 'xml')
        print(soup.prettify())
        return item
