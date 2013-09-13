class ExtractDocument(object):

    def process_item(self, item, spider):
        if item['document_selects']:
            return item
        else:
            return self.extract(item)

    def extract(self, response_item):
        # TODO implement common extract method
        return response_item

