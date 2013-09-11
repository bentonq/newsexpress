#! /usr/bin/python
import urllib2
import time


SERVER_ADDRESS = 'http://localhost:6800'


def schedule_spider(spider):
    schedule_address = SERVER_ADDRESS + '/schedule.json'
    data = 'project=newscrawler&spider=%s'%spider
    response = urllib2.urlopen(schedule_address, data)
    result = eval(response.read())
    if result['status'] == 'ok':
        print('Successful to schedule "%s" spider [%s]' % (spider, result['jobid']))
    else:
        print('Failed to schedule "%s" spider' % spider)

def list_spiders():
    listspiders_address = SERVER_ADDRESS + '/listspiders.json?project=newscrawler'
    response = urllib2.urlopen(listspiders_address)
    result = eval(response.read())
    spiders = result['spiders']
    print('Available spiders: %s' % spiders)
    return spiders


spiders = list_spiders()
while True:
    for spider in spiders:
        schedule_spider(spider)
    time.sleep(300)
