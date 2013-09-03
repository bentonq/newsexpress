from bs4 import BeautifulSoup
from bs4.element import Comment
from bs4.element import NavigableString

class NewscrawlerPipeline(object):
    def process_item(self, item, spider):
        soup = BeautifulSoup(item['doc'], 'lxml')
        print(soup.prettify())
        body = soup.html.body
        droplist = []
        for tag in body.children:
            droppable = True

            if tag.string:
                string = unicode(tag.string)
                tag.attrs.clear()
                tag.clear()
                tag.append(soup.new_string(string))
                droppable = False
            elif tag.find_all('img'):
                new_tag = soup.new_tag('p')
                img_tags = tag.find_all('img')
                for img_tag in img_tags:
                    new_tag.append(img_tag)
                tag.replace_with(new_tag)
                droppable = False
            else:
                string = u''
                for subtag in tag.descendants:
                    if (isinstance(subtag, NavigableString) and
                        not isinstance(subtag, Comment) and
                        not subtag.parent.name == 'script'):
                        string += unicode(subtag).strip()
                if string:
                    tag.attrs.clear()
                    tag.clear()
                    tag.append(soup.new_string(string))
                    droppable = False

            if droppable:
                droplist.append(tag)

        for dropitem in droplist:
            dropitem.decompose()

        print('------------------------------------------------------------')
        print(body.prettify())
        item['doc'] = unicode(soup)
        return item

class NormalizeHeaderPipeline(object):
    def process_item(self, item, spider):
        return item


class ExportPipeline(object):

    def process_item(self, item, spider):
        return item
