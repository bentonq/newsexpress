from newsagent import *
from urllib import urlopen
from datetime import datetime
from time import mktime
import feedparser

class FeedSource:
	def __init__(self, name, url):
		self.name = name
		self.url = url
		self.news_entry = {}

	def get_name(self):
		return self.name

	def get_url(self):
		return self.url

	def fetch_news_links(self):
		result = []
		feed = feedparser.parse(self.url)
		for entry in feed.entries:
			result.append(NewsLink(entry.link))
			self.news_entry[entry.link] = entry
		return result

	def download_news(self, news_link):
		try:
			entry = self.news_entry[news_link.link]
			html = urlopen(news_link.link).read()
			published_time = datetime.fromtimestamp(mktime(entry.published_parsed))
			return create_news(entry.title, entry.summary, published_time, html)
		except IOError:
			print('Download news "%s" failed' % news_link.link)
			return None

