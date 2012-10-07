#!/usr/bin/python
# -*- coding: utf-8 -*-
# import codecs
# import encodings

def search1(encoding):
    print 'search1: Searching for:', encoding
    return None

def search2(encoding): print 'search2: Searching for:', encoding 
return None

codecs.register(search1)
codecs.register(search2)

utf8 = codecs.lookup('utf-8')
print 'UTF-8:', utf8

try:
    unknown = codecs.lookup('no-such-encoding')
except LookupError, err:
    print 'ERROR:', err