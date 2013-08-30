from feedsource import FeedSource
from newsagent import NewsAgent, NewsEngine
from testmethod import TestSearchMethod, TestNormalizeMethod

agent = NewsAgent()

agent.register_search_method(TestSearchMethod())
agent.register_normalize_method(TestNormalizeMethod())

agent.add_source(FeedSource('163 newstop', 'http://news.163.com/special/00011K6L/rss_newstop.xml'))
agent.add_source(FeedSource('163 gn', 'http://news.163.com/special/00011K6L/rss_gn.xml'))

agent.crawl()

'''
engine = NewsEngine()
for news in agent.newest_news():
	print(news.title)
	engine.parse(news)
'''
