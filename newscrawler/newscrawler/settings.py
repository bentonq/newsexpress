# Scrapy settings for newscrawler project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'newscrawler'
#LOG_LEVEL = 'INFO'
SPIDER_MODULES = ['newscrawler.spiders']
NEWSPIDER_MODULE = 'newscrawler.spiders'
ITEM_PIPELINES = [
    'newscrawler.pipelines.archive.ArchiveHtml',
    'newscrawler.pipelines.extract.ExtractDocument',
    'newscrawler.pipelines.transform.ConvertToBeautifulSoup',
    'newscrawler.pipelines.transform.ConvertBack',
    'newscrawler.pipelines.log.ExportLogItem',
]
EXTENSIONS = {
}
FEED_FORMAT = 'xml'
#USER_AGENT = 'newscrawler (+http://www.newscrawler.com)'


DATABASE_DIR = 'data/db'
ARCHIVEMENT_DIR = 'data/archived'

import os
try:
    os.makedirs(DATABASE_DIR)
    os.makedirs(ARCHIVEMENT_DIR)
except OSError:
    pass

