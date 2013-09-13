from newscrawler.items import BeautifulSoupItem
from bs4 import BeautifulSoup


class ConvertToBeautifulSoup(object):

    def process_item(self, item, spider):
        soup = BeautifulSoup(item['response'].body, 'lxml')
        result = BeautifulSoupItem()
        result['soup'] = soup
        result['response_item'] = item
        return result


class ConvertBack(object):

    def process_item(self, item, spider):
        result = item['response_item']
        return result

