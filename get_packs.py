import urllib2
from BeautifulSoup import BeautifulSoup

response = urllib2.urlopen('http://mtgtop8.com/search')
html = response.read()
parsed_html = BeautifulSoup(html)

import ipdb;ipdb.set_trace()
desck = parsed_html.find_all("tr", class_="hover_tr")



