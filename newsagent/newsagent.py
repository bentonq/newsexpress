from bs4 import BeautifulSoup
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String
import uuid

engine = create_engine('sqlite:///newsexpress.db')
Session = sessionmaker(bind=engine)
session = Session()
Base = declarative_base()

class NewsItem(Base):
	__tablename__ = 'news_item'

	id = Column(Integer, primary_key=True)
	name = Column(String)

	def __init__(self, name):
		self.name = name
	
	def __repr__(self):
		return "<NewsItem<'%s'>" % self.name

Base.metadata.create_all(engine)

class News:
	def __init__(self, title, summary, content, pub_date, link):
		self.title = title
		self.summary = summary
		self.content = content
		self.pub_date = pub_date
		self.link = link

class NewsAgent:
	def __init__(self):
		self.sources = []

	def add_source(self, source):
		self.sources.append(source)

	def newest_news(self):
		result = []
		for source in self.sources:
			items = source.update_news_items()
			newest_items = self._check_newest_items(items)
			news = source.download_news(newest_items)
			for n in news:
				soup = BeautifulSoup(n.content, "html5lib")
				n.content = soup
				result.append(n)
		return result
	
	def _check_newest_items(self, items):
		result = []
		for item in items:
			exist = session.query(NewsItem).filter(NewsItem.name==item.name).count()
			if not exist:
				session.add(item)
				session.commit()
				result.append(item)
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

