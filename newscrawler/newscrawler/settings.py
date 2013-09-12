# Scrapy settings for newscrawler project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'newscrawler'
LOG_LEVEL = 'INFO'
SPIDER_MODULES = ['newscrawler.spiders']
NEWSPIDER_MODULE = 'newscrawler.spiders'
ITEM_PIPELINES = [
]
EXTENSIONS = {
}
FEED_FORMAT = 'xml'
#USER_AGENT = 'newscrawler (+http://www.newscrawler.com)'


DOCUMENTS_DIR = 'data'

import os
try:
    os.makedirs(DOCUMENTS_DIR)
except OSError:
    pass

