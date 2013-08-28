from bs4 import BeautifulSoup
from urllib import urlopen
import feedparser

class NewsGuid:
	def __init__(self, id, source):
		self.id = id
		self.feed_id = source.get_guid()

class News:
	def __init__(self, title, summary, content, pub_date, link):
		self.title = title
		self.summary = summary
		self.content = content
		self.pub_date = pub_date
		self.link = link

class FeedSource:
	def __init__(self, url):
		self.url = url
		self.feed = None
		self.items = {}

	def get_guid(self):
		self._fetch_feed()
		if 'id' in self.feed.feed:
			return self.feed.feed.id
		else:
			return self.feed.feed.title

	def get_news_guids(self):
		result = []
		self._fetch_feed()
		for entry in self.feed.entries:
			id = None
			if 'id' in entry:
				id = entry.id
			else:
				id = entry.link
			self.items[id] = entry
			result.append(NewsGuid(id, self))
		return result

	def get_news(self, guids):
		result = []
		for guid in guids:
			news_item = self.items[guid.id]
			html = urlopen(news_item.link).read()
			content = BeautifulSoup(html, "html5lib")
			result.append(News(news_item.title, news_item.summary, content,
							   news_item.published_parsed, news_item.link))
		return result

	def _fetch_feed(self):
		if not self.feed:
			self.feed = feedparser.parse(self.url)

class NewsAgent:
	def __init__(self):
		self.sources = []

	def add_source(self, source):
		self.sources.append(source)

	def get_news(self):
		result = []
		for source in self.sources:
			guids = source.get_news_guids()
			# TODO: filter the guids
			result.extend(source.get_news(guids))
		return result

class NewsEngine:
	def __init__(self):
		self.search_methods = []
		self.normalize_methods = []
		pass

	def register_search_method(self, method):
		self.search_methods.append(method)
		pass

	def register_normalize_method(self, method):
		self.normalize_methods.append(method)
		pass

	def parse(self, news):
		key_tag = news.content
		try:
			for method in self.search_methods:
				key_tag = method.search(key_tag)

			for method in self.normalize_methods:
				key_tag = method.normalize(key_tag)
		except:
			print("Error parsing " + news.title + " link: " +
				   news.link)

