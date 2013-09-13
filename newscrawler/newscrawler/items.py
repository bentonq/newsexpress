# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field


class ResponseItem(Item):

    docid = Field()
    response = Field()
    documents_selects = Field()


class ExportItem(Item):

    title = Field()


class NewsItem(Item):

    id = Field()
    header = Field()
    title = Field()
    doc = Field()


