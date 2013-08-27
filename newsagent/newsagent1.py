from bs4 import BeautifulSoup
from bs4.diagnose import diagnose
from urllib import urlopen
import feedparser

class NewsGuid:
	def __init__(self, id, source):
		self.id = id
		self.feed_id = source.get_guid()

class News:
	def __init__(self, title, summary, content, pub_date):
		self.title = title
		self.summary = summary
		self.content = content
		self.pub_date = pub_date

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
		# FIXME: test here
		news_item = self.items[guids[0].id]
		html = urlopen(news_item.link).read()
		content = BeautifulSoup(html, "html5lib")
		result.append(News(news_item.title, news_item.summary, content,
						   news_item.published_parsed))
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

class TestSearchMethod:
	def __init__(self):
		pass

	def search(self, html):
		key_tag = html.find(id='epContentLeft')
		return key_tag

class TestNormalizeMethod:
	def __init__(self):
		pass

	def normalize(self, html):
		for tag in html.find_all(['p', 'img']):
			if tag.name == 'img':
				if tag.has_key('src'):
					print tag['src']
			else:
				if tag.string:
					print(tag.string)

class NewsEngine:
	def __init__(self):
		self.search_method = []
		self.normalize_method = []
		pass

	def register_search_method(self, method):
		self.search_method.append(method)
		pass

	def register_normalize_method(self, method):
		self.normalize_method.append(method)
		pass

	def parse(self, news):
		search_method = self._choose_search_method(news)
		normalize_method = self._choose_normalize_method(news)
		key = search_method.search(news.content)
		normalize_method.normalize(key)

	def _choose_search_method(self, news):
		return TestSearchMethod()

	def _choose_normalize_method(self, news):
		return TestNormalizeMethod()


url = 'http://news.163.com/special/00011K6L/rss_newstop.xml'
source = FeedSource(url)

agent = NewsAgent()
agent.add_source(source)

engine = NewsEngine()
for news in agent.get_news():
	engine.parse(news)
