# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field


class RawItem(Item):

    url = Field()
    header = Field()
    body = Field()
    title = Field()
    author = Field()
    summary = Field()
    published = Field()
    updated = Field()


class NewsItem(Item):

    id = Field()
    header = Field()
    title = Field()
    doc = Field()


class LogItem(Item):

    title = Field()
