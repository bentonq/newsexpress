from newscrawler.settings import ARCHIVEMENT_DIR

class ArchiveHtml(object):

    def process_item(self, item, spider):
        response = item['response']
        file = open('%s/%s.html' % (ARCHIVEMENT_DIR, item['docid']), 'wb')
        file.write(response.body)
        file.close()
        return item

