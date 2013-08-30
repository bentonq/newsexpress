from bs4 import BeautifulSoup

class TestSearchMethod:
	def __init__(self):
		pass

	def search(self, origin_tag):
		key_tag = origin_tag.find(id='endText')
		return key_tag

class TestNormalizeMethod:
	def __init__(self):
		pass

	def normalize(self, html):
		new_root = BeautifulSoup('<root></root>', 'xml')
		for tag in html.find_all(['p', 'img']):
			if tag.name == 'img' and not tag.has_attr('class'):
				new_root.append(tag)
			elif tag.string:
				tag.string = tag.string.strip()
				new_root.append(tag)
		return new_root
