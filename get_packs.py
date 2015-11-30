#!/usr/bin/env python

import sys
import urllib2
from BeautifulSoup4 import BeautifulSoup
import argparse

VERSION = "0.0.1"
START_PAGE = 'http://mtgtop8.com/search'


def get_html_source(address):
	response = urllib2.urlopen(address)
	return response.read()


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument("-v", "--version", action='store_true',help="script versions")
    args = parser.parse_args()

    if args.version:
    	return VERSION

    html = get_html_source(START_PAGE)
    parsed_html = BeautifulSoup(html)
    trs = parsed_html.find_all('tr', {'class' : 'hover_tr'})

    import ipdb;ipdb.set_trace()


if __name__ == '__main__':
    sys.exit(main())