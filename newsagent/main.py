from feedsource import FeedSource
from newsagent import NewsAgent, NewsEngine
from testmethod import TestSearchMethod, TestNormalizeMethod

url = 'http://news.163.com/special/00011K6L/rss_newstop.xml'
source = FeedSource(url)

agent = NewsAgent()
agent.add_source(source)

engine = NewsEngine()
engine.register_search_method(TestSearchMethod())
engine.register_normalize_method(TestNormalizeMethod())
for news in agent.newest_news():
	print(news.title)
	engine.parse(news)

