from bs4 import BeautifulSoup

class TestSearchMethod:
	def __init__(self):
		pass

	def search(self, html):
		key_tag = html.find(id='endText')
		return key_tag

class TestNormalizeMethod:
	def __init__(self):
		pass

	def normalize(self, html):
		new_xml = BeautifulSoup('<news></news>', 'xml')
		root = new_xml.news
		for tag in html.find_all(['p', 'img']):
			if tag.name == 'img' and not tag.has_attr('class'):
				root.append(tag)
			elif tag.string:
				tag.string = tag.string.strip()
				root.append(tag)
		return new_xml
