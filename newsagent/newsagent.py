from bs4 import BeautifulSoup
from sqlalchemy import create_engine, ForeignKey
from sqlalchemy.orm import sessionmaker, relationship, backref
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.exc import *
import uuid
import new
import sys
import datetime
import codecs
import os

engine = create_engine('sqlite:///newsexpress.db')
Session = sessionmaker(bind=engine)
session = Session()
Base = declarative_base()

os.mkdir('news')

class NewsSource(Base):
	__tablename__ = 'news_source'

	id = Column(Integer, primary_key=True)
	name = Column(String)
	url = Column(String, unique=True)
	type_name = Column(String)

	def __init__(self, name, url, type_name):
		self.name = name
		self.url = url
		self.type_name = type_name

	def __repr__(self):
		return "<NewsSource<'%s', '%s'>" % self.name, self.url

class NewsLink(Base):
	__tablename__ = 'news_link'

	id = Column(Integer, primary_key=True)
	link = Column(String, unique=True)

	source_id = Column(Integer, ForeignKey('news_source.id'))
	source = relationship('NewsSource', backref=backref('links', order_by=id))

	news_id = Column(Integer, ForeignKey('news.id'))
	news = relationship('News', backref=backref('link', uselist=False))

	def __init__(self, link):
		self.link = link
	
	def __repr__(self):
		return "<NewsLink<'%s'>" % self.link

class News(Base):
	__tablename__ = 'news'

	id = Column(Integer, primary_key=True)
	updated_time = Column(DateTime, onupdate=datetime.datetime.now)
	published_time = Column(DateTime)
	content = Column(String)

	def __init__(self):
		pass;

	def __repr__(self):
		return "<News>" 

Base.metadata.create_all(engine)

class NewsContent:
	def __init__(self, title, summary, html):
		self.title = title
		self.summary = summary
		self.html = html

def create_news_link(link):
	return NewsLink(link)

def create_news(title, summary, published_time, html):
	print("create_news %s" % title)
	result = News()
	result.published_time = published_time
	result.content = NewsContent(title, summary, html)
	return result

class NewsAgent:
	def __init__(self):
		self.search_methods = []
		self.normalize_methods = []
		self.news_sources = session.query(NewsSource).all()
		for source in self.news_sources:
			print('Load feed source "%s" from db' % source.name)

	def add_source(self, source):
		class_name = source.__class__.__name__
		module_name = source.__class__.__module__
		type_name = module_name + '.' + class_name

		name = source.get_name()
		url = source.get_url()
		news_source = NewsSource(name, url, type_name)

		try:
			session.add(news_source)
			session.commit()
			self.news_sources.append(news_source)
			print('Add feed source: "%s" to db' % name)
		except IntegrityError:
			session.rollback()
			print('Can not add feed source: "%s" to db for existence' % name)

	def register_search_method(self, method):
		self.search_methods.append(method)
		pass

	def register_normalize_method(self, method):
		self.normalize_methods.append(method)
		pass

	def crawl(self):
		result = []
		for source in self.news_sources:
			print('Crawling feed source "%s" ' % source.name)
			module_class = source.type_name.split('.')
			module_name = module_class[0]
			class_name = module_class[1]
			module = sys.modules[module_name]
			clazz = getattr(module, class_name)
			source_obj = new.instance(clazz)
			source_obj.__init__(source.name, source.url)

			news_links = source_obj.fetch_news_links()
			news_links = self._check_link_existence(source, news_links)
			for news_link in news_links:
				news = source_obj.download_news(news_link)
				news_link.news = self._parse_news(news)
				session.commit()
			#for n in news:
				#soup = BeautifulSoup(n.content, "html5lib")
				#n.content = soup
				#result.append(n)
		return result

	def _parse_news(self, news):
		if not news or not isinstance(news.content, NewsContent):
			return None
		
		news_xml = BeautifulSoup('<news></news>', 'xml')	
		news_tag = news_xml.news

		title_tag = news_xml.new_tag('title')
		title_tag.string = news.content.title
		summary_tag = news_xml.new_tag('summary')
		summary_tag.string = news.content.summary

		content_tag = news_xml.new_tag('content')
		try:
			key_tag = BeautifulSoup(news.content.html, 'html5lib')
			for method in self.search_methods:
				key_tag = method.search(key_tag)
			
			for method in self.normalize_methods:
				key_tag = method.normalize(key_tag)

			for content in key_tag.contents:
				content_tag.append(content)
		except AttributeError:
			print("Error parsing " + news.content.title)

		news_tag.append(title_tag)
		news_tag.append(summary_tag)
		news_tag.append(content_tag)

		f = codecs.open('news/%s.xml' % news.content.title, 'w', 'utf-8')
		f.write(news_xml.prettify())
	
	def _check_link_existence(self, source, news_links):
		result = []
		for news_link in news_links:
			exist = session.query(NewsLink).filter(NewsLink.link==news_link.link).count() != 0
			if not exist:
				source.links.append(news_link)
				session.commit()
				result.append(news_link)
		print('Fetch %d news links which %d is newest' % (len(news_links), len(result)))
		return result

class NewsEngine:
	def __init__(self):
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

