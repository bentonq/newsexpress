from newscrawler.items import LogItem


class ExportLogItem(object):

    def process_item(self, item, spider):
        result = LogItem() 
        result['docid'] = item['docid']
        result['url'] = item['response'].url
        return result

