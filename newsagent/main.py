import newsagent
import testmethod

url = 'http://news.163.com/special/00011K6L/rss_newstop.xml'
source = newsagent.FeedSource(url)

agent = newsagent.NewsAgent()
agent.add_source(source)

engine = newsagent.NewsEngine()
engine.register_search_method(testmethod.TestSearchMethod())
engine.register_normalize_method(testmethod.TestNormalizeMethod())
for news in agent.get_news():
	engine.parse(news)

