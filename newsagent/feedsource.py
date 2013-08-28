from newsagent import News
from newsagent import NewsItem
from urllib import urlopen
import feedparser

class FeedSource:
	def __init__(self, url):
		self.url = url
		self.news = {}

	def update_news_items(self):
		result = []
		feed = feedparser.parse(self.url)
		for entry in feed.entries:
			name = None
			if 'id' in entry:
				name = entry.id
			else:
				name = entry.link
			self.news[name] = entry
			result.append(NewsItem(name))
		return result

	def download_news(self, news_items):
		result = []
		for news_item in news_items:
			entry = self.news[news_item.name]
			html = urlopen(entry.link).read()
			result.append(News(entry.title, entry.summary, html,
							   entry.published_parsed, entry.link))
		return result

