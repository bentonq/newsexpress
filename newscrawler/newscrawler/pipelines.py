from scrapy.exceptions import DropItem
from bs4 import BeautifulSoup
from bs4.element import Comment
from bs4.element import NavigableString

import codecs
import copy
import os


class PipelinesSettings(object):

    _documents_dir = './'

    @classmethod
    def from_crawler(cls, crawler):
        settings = crawler.settings
        if settings['DOCUMENTS_DIR']:
            cls._documents_dir = settings['DOCUMENTS_DIR']
            try:
                os.makedirs(cls._documents_dir)
            except OSError:
                pass

    @classmethod
    def documents_dir(cls):
        return cls._documents_dir


class ConvertToBeautifulSoupPipeline(object):

    def process_item(self, item, spider):
        dst_header_soup = BeautifulSoup('<header></header>', 'xml')
        header_tag = dst_header_soup.header
        header = item['header']
        for its in header.items():
            key_tag = dst_header_soup.new_tag(its[0])
            value_tag = dst_header_soup.new_string(its[1][0])
            key_tag.append(value_tag)
            header_tag.append(key_tag)
        item['header'] = dst_header_soup


        dst_doc_soup = BeautifulSoup('<doc></doc>', 'xml')
        doc_tag = dst_doc_soup.doc
        src_doc_soup = BeautifulSoup(item['doc'], 'lxml')
        if src_doc_soup.html:
            for tag in src_doc_soup.html.body:
                doc_tag.append(copy.deepcopy(tag))
            item['doc'] = dst_doc_soup
        else:
            raise DropItem('html is empty')

        return item


class NormalizeHeaderPipeline(object):

    def process_item(self, item, spider):
        return item


class CleanHtmlPipeline(object):

    def process_item(self, item, spider):
        doc_soup = item['doc']
        doc_tag = doc_soup.doc
        droplist = []
        for tag in doc_tag:
            droppable = True

            if tag.string:
                string = unicode(tag.string)
                tag.attrs.clear()
                tag.clear()
                tag.append(doc_soup.new_string(string))
                droppable = False
            elif tag.find_all('img'):
                new_tag = doc_soup.new_tag('p')
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
                    tag.append(doc_soup.new_string(string))
                    droppable = False

            if droppable:
                droplist.append(tag)

        for dropitem in droplist:
            dropitem.decompose()

        return item


class ExportPipeline(object):

    def process_item(self, item, spider):
        file_soup = BeautifulSoup('<news></news>', 'xml')
        news_tag = file_soup.news

        id_tag = file_soup.new_tag('id')
        id_tag.append(file_soup.new_string(item['id']))
        news_tag.append(id_tag)

        header_soup = item['header']
        header_tag = copy.deepcopy(header_soup.header)
        news_tag.append(header_tag)

        doc_soup = item['doc']
        doc_tag = copy.deepcopy(doc_soup.doc)
        news_tag.append(doc_tag)

        file_title = item['id']
        file = codecs.open('%s/%s.xml' % (PipelinesSettings.documents_dir(), file_title), 'wb', 'utf-8')
        file.write(file_soup.prettify())

        return item

