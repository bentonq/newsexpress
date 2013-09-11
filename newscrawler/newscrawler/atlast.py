from newscrawler.items import LogItem

class LogItemExport(object):

    def process_item(self, item, spider):
        log_item = LogItem()
        log_item['title'] = item['title'].encode('utf-8')

        return log_item
