# -*- coding: iso-8859-1 -*-
"""
    StatWiki - site-wide configuration defaults (NOT per single wiki!)

    @copyright: 2005 by Thomas Waldmann (MoinMoin:ThomasWaldmann), 2009 Arkadiusz Wahlig
    @license: GNU GPL, see COPYING for details.
"""


from ConfigParser import RawConfigParser, NoOptionError
import codecs


# Charset - we support only 'utf-8'. While older encodings might work,
# we don't have the resources to test them, and there is no real
# benefit for the user.
# IMPORTANT: use only lowercase 'utf-8'!
charset = 'utf-8'


class Section(object):
    def __init__(self, parser, section_name):
        object.__init__(self)
        self.__dict__['_parser'] = parser
        self.__dict__['_sectname'] = section_name
        if not parser.has_section(section_name):
            parser.add_section(section_name)
        
    def __getattr__(self, name):
        try:
            return self._parser.get(self._sectname, name)
        except NoOptionError, err:
            raise AttributeError(str(err))
    
    def __setattr__(self, name, value):
        self._parser.set(self._sectname, name, value)
        
    def __delattr__(self, name):
        try:
            self._parser.remove_option(self._sectname, name)
        except NoOptionError, err:
            raise AttributeError(str(err))
    
    def items(self):
        return self._parser.items(self._sectname)
        
    def get(self, name, *default):
        try:
            return self._parser.get(self._sectname, name)
        except NoOptionError, err:
            if default:
                return default[0]
            raise AttributeError(str(err))


def parse(config_filename):
    # overwrite the defaults with values from the config file
    _parser.read(config_filename)
    global filename
    filename = config_filename


# create the config parser
_parser = RawConfigParser()
filename = ''

# create section objects for easy options access through its attributes
general = Section(_parser, 'general')
ftp = Section(_parser, 'ftp')
text = Section(_parser, 'text')
# a special section that may be used by the generator
generator = Section(_parser, 'generator')

# set default values
general.indexpagename = 'index'
general.timeformat = '%H:%M, %d %B %Y' # time format as used in time.strftime()
general.camelcase = 1
general.targetblank = 0
general.generator = '_generator.py'
ftp.port = 21
ftp.passive = 1
text.category = 'Category: %(value)s'
text.categories = 'Categories: %(value)s'
text.subpages = 'Pages in category "%(value)s"'


# vim:set sw=4 et:
