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
    'newscrawler.acquisition.CheckDuplicates',
    'newscrawler.acquisition.ConvertToBeautifulSoup',
    'newscrawler.acquisition.Denoising',
    'newscrawler.acquisition.SaveDocument',
    'newscrawler.atlast.LogItemExport',
]
EXTENSIONS = {
    'newscrawler.acquisition.AcquisitionSettings': 500,
}

DOCUMENTS_DIR = 'data/docs'

# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'newscrawler (+http://www.yourdomain.com)'
