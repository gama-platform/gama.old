# -*- coding: iso-8859-1 -*-
"""
    MoinMoin - Wiki Utility Functions

    @copyright: 2000 - 2004 by Jürgen Hermann <jh@web.de>
    @license: GNU GPL, see COPYING for details.
"""
    

import sys
import os
import re
import urllib


import config
import chartypes


# Exceptions
class InvalidFileNameError(Exception):
    """ Called when we find an invalid file name """ 
    pass


# this is a thin wrapper around urllib (urllib only handles str, not unicode)
# with py <= 2.4.1, it would give incorrect results with unicode
# with py == 2.4.2, it crashes with unicode, if it contains non-ASCII chars
def url_quote(s, safe='/', want_unicode=False):
    """
    Wrapper around urllib.quote doing the encoding/decoding as usually wanted:
    
    @param s: the string to quote (can be str or unicode, if it is unicode,
              config.charset is used to encode it before calling urllib)
    @param safe: just passed through to urllib
    @param want_unicode: for the less usual case that you want to get back
                         unicode and not str, set this to True
                         Default is False.
    """
    if isinstance(s, unicode):
        s = s.encode(config.charset)
    elif not isinstance(s, str):
        s = str(s)
    s = urllib.quote(s, safe)
    if want_unicode:
        s = s.decode(config.charset) # ascii would also work
    return s

def url_quote_plus(s, safe='/', want_unicode=False):
    """
    Wrapper around urllib.quote_plus doing the encoding/decoding as usually wanted:
    
    @param s: the string to quote (can be str or unicode, if it is unicode,
              config.charset is used to encode it before calling urllib)
    @param safe: just passed through to urllib
    @param want_unicode: for the less usual case that you want to get back
                         unicode and not str, set this to True
                         Default is False.
    """
    if isinstance(s, unicode):
        s = s.encode(config.charset)
    elif not isinstance(s, str):
        s = str(s)
    s = urllib.quote_plus(s, safe)
    if want_unicode:
        s = s.decode(config.charset) # ascii would also work
    return s

def url_unquote(s, want_unicode=True):
    """
    Wrapper around urllib.unquote doing the encoding/decoding as usually wanted:
    
    @param s: the string to unquote (can be str or unicode, if it is unicode,
              config.charset is used to encode it before calling urllib)
    @param want_unicode: for the less usual case that you want to get back
                         str and not unicode, set this to False.
                         Default is True.
    """
    if isinstance(s, unicode):
        s = s.encode(config.charset) # ascii would also work
    s = urllib.unquote(s)
    if want_unicode:
        s = s.decode(config.charset)
    return s


# FIXME: better name would be quoteURL, as this is useful for any
# string, not only wiki names.
def quoteWikinameURL(pagename, charset=config.charset):
    """ Return a url encoding of filename in plain ascii

    Use urllib.quote to quote any character that is not always safe. 

    @param pagename: the original pagename (unicode)
    @param charset: url text encoding, 'utf-8' recommended. Other charsert
                    might not be able to encode the page name and raise
                    UnicodeError. (default config.charset ('utf-8')).
    @rtype: string
    @return: the quoted filename, all unsafe characters encoded
    """
    pagename = pagename.replace(u' ', u'_')
    pagename = pagename.encode(charset)
    return urllib.quote(pagename)


def escape(s, quote=0):
    """ Escape possible html tags
    
    Replace special characters '&', '<' and '>' by SGML entities.
    (taken from cgi.escape so we don't have to include that, even if we
    don't use cgi at all)

    FIXME: should return string or unicode?
    
    @param s: (unicode) string to escape
    @param quote: bool, should transform '\"' to '&quot;'
    @rtype: (unicode) string
    @return: escaped version of s
    """
    if not isinstance(s, (str, unicode)):
        s = str(s)

    # Must first replace &
    s = s.replace("&", "&amp;")

    # Then other...
    s = s.replace("<", "&lt;")
    s = s.replace(">", "&gt;")
    if quote:
        s = s.replace('"', "&quot;")
    return s

def clean_comment(comment):
    """ Clean comment - replace CR, LF, TAB by whitespace, delete control chars
        TODO: move this to config, create on first call then return cached.
    """
    # we only have input fields with max 200 chars, but spammers send us more
    if len(comment) > 201:
        comment = u''
    remap_chars = {
        ord(u'\t'): u' ',
        ord(u'\r'): u' ',
        ord(u'\n'): u' ',
    }
    control_chars = u'\x00\x01\x02\x03\x04\x05\x06\x07\x08\x0b\x0c\x0e\x0f' \
                    '\x10\x11\x12\x13\x14\x15\x16\x17\x18\x19\x1a\x1b\x1c\x1d\x1e\x1f'
    for c in control_chars:
        remap_chars[c] = None
    comment = comment.translate(remap_chars)
    return comment

def make_breakable(text, maxlen):
    """ make a text breakable by inserting spaces into nonbreakable parts
    """
    text = text.split(" ")
    newtext = []
    for part in text:
        if len(part) > maxlen:
            while part:
                newtext.append(part[:maxlen])
                part = part[maxlen:]
        else:
            newtext.append(part)
    return " ".join(newtext)


########################################################################
### Storage
########################################################################

# FIXME: These functions might be moved to storage module, when we have
# one. Then they will be called transparently whenever a page is saved.

# Precompiled patterns for file name [un]quoting
UNSAFE = re.compile(r'[^a-zA-Z0-9_]+')
QUOTED = re.compile(r'\(([a-fA-F0-9]+)\)')


# FIXME: better name would be quoteWikiname
def quoteWikinameFS(wikiname, charset=config.charset):
    """ Return file system representation of a Unicode WikiName.
            
    Warning: will raise UnicodeError if wikiname can not be encoded using
    charset. The default value of config.charset, 'utf-8' can encode any
    character.
        
    @param wikiname: Unicode string possibly containing non-ascii characters
    @param charset: charset to encode string
    @rtype: string
    @return: quoted name, safe for any file system
    """
    wikiname = wikiname.replace(u' ', u'_') # " " -> "_"
    filename = wikiname.encode(charset)
    
    quoted = []    
    location = 0
    for needle in UNSAFE.finditer(filename):
        # append leading safe stuff
        quoted.append(filename[location:needle.start()])
        location = needle.end()                    
        # Quote and append unsafe stuff           
        quoted.append('(')
        for character in needle.group():
            quoted.append('%02x' % ord(character))
        quoted.append(')')
    
    # append rest of string
    quoted.append(filename[location:])    
    return ''.join(quoted)


# FIXME: better name would be unquoteFilename
def unquoteWikiname(filename, charsets=[config.charset]):
    """ Return Unicode WikiName from quoted file name.
    
    We raise an InvalidFileNameError if we find an invalid name, so the
    wiki could alarm the admin or suggest the user to rename a page.
    Invalid file names should never happen in normal use, but are rather
    cheap to find. 
    
    This function should be used only to unquote file names, not page
    names we receive from the user. These are handled in request by
    urllib.unquote, decodePagename and normalizePagename.
    
    Todo: search clients of unquoteWikiname and check for exceptions. 

    @param filename: string using charset and possibly quoted parts
    @param charsets: list of charsets used by string
    @rtype: Unicode String
    @return: WikiName
    """
    ### Temporary fix start ###
    # From some places we get called with Unicode strings
    if isinstance(filename, type(u'')):
        filename = filename.encode(config.charset)
    ### Temporary fix end ###
        
    parts = []    
    start = 0
    for needle in QUOTED.finditer(filename):  
        # append leading unquoted stuff
        parts.append(filename[start:needle.start()])
        start = needle.end()            
        # Append quoted stuff
        group =  needle.group(1)
        # Filter invalid filenames
        if (len(group) % 2 != 0):
            raise InvalidFileNameError(filename) 
        try:
            for i in range(0, len(group), 2):
                byte = group[i:i+2]
                character = chr(int(byte, 16))
                parts.append(character)
        except ValueError:
            # byte not in hex, e.g 'xy'
            raise InvalidFileNameError(filename)
    
    # append rest of string
    if start == 0:
        wikiname = filename
    else:
        parts.append(filename[start:len(filename)])   
        wikiname = ''.join(parts)

    # This looks wrong, because at this stage "()" can be both errors
    # like open "(" without close ")", or unquoted valid characters in
    # the file name. FIXME: check this.
    # Filter invalid filenames. Any left (xx) must be invalid
    #if '(' in wikiname or ')' in wikiname:
    #    raise InvalidFileNameError(filename)
    
    wikiname = decodeUserInput(wikiname, charsets)
    wikiname = wikiname.replace(u'_', u' ') # "_" -> " "
    return wikiname

# time scaling
def timestamp2version(ts):
    """ Convert UNIX timestamp (may be float or int) to our version
        (long) int.
        We don't want to use floats, so we just scale by 1e6 to get
        an integer in usecs. 
    """
    return long(ts*1000000L) # has to be long for py 2.2.x

def version2timestamp(v):
    """ Convert version number to UNIX timestamp (float).
        This must ONLY be used for display purposes.
    """
    return v/1000000.0


#############################################################################
### Page types (based on page names)
#############################################################################

def isSystemPage(request, pagename):
    """ Is this a system page? Uses AllSystemPagesGroup internally.
    
    @param request: the request object
    @param pagename: the page name
    @rtype: bool
    @return: true if page is a system page
    """
    return (request.dicts.has_member('SystemPagesGroup', pagename) or
        isTemplatePage(request, pagename))


def isTemplatePage(request, pagename):
    """ Is this a template page?
    
    @param pagename: the page name
    @rtype: bool
    @return: true if page is a template page
    """
    filter = re.compile(request.cfg.page_template_regex, re.UNICODE)
    return filter.search(pagename) is not None


def filterCategoryPages(request, pagelist):
    """ Return category pages in pagelist

    WARNING: DO NOT USE THIS TO FILTER THE FULL PAGE LIST! Use
    getPageList with a filter function.
        
    If you pass a list with a single pagename, either that is returned
    or an empty list, thus you can use this function like a `isCategoryPage`
    one.
       
    @param pagelist: a list of pages
    @rtype: list
    @return: only the category pages of pagelist
    """
    func = re.compile(request.cfg.page_category_regex, re.UNICODE).search
    return filter(func, pagelist)


def getFrontPage(request):
    """ Convenience function to get localized front page

    @param request: current request
    @rtype: Page object
    @return localized page_front_page, if there is a translation
    """
    return getSysPage(request, request.cfg.page_front_page)
    

def AbsPageName(request, context, pagename):
    """
    Return the absolute pagename for a (possibly) relative pagename.

    @param context: name of the page where "pagename" appears on
    @param pagename: the (possibly relative) page name
    @rtype: string
    @return: the absolute page name
    """
    if pagename.startswith(PARENT_PREFIX):
        pagename = '/'.join(filter(None, context.split('/')[:-1] + [pagename[PARENT_PREFIX_LEN:]]))
    elif pagename.startswith(CHILD_PREFIX):
        pagename = context + '/' + pagename[CHILD_PREFIX_LEN:]
    return pagename


#############################################################################
### Misc
#############################################################################

def parseAttributes(request, attrstring, endtoken=None, extension=None):
    """
    Parse a list of attributes and return a dict plus a possible
    error message.
    If extension is passed, it has to be a callable that returns
    a tuple (found_flag, msg). found_flag is whether it did find and process
    something, msg is '' when all was OK or any other string to return an error
    message.
    
    @param request: the request object
    @param attrstring: string containing the attributes to be parsed
    @param endtoken: token terminating parsing
    @param extension: extension function -
                      gets called with the current token, the parser and the dict
    @rtype: dict, msg
    @return: a dict plus a possible error message
    """
    import shlex, StringIO

    _ = request.getText

    parser = shlex.shlex(StringIO.StringIO(attrstring))
    parser.commenters = ''
    msg = None
    attrs = {}

    while not msg:
        try:
            key = parser.get_token()
        except ValueError, err:
            msg = str(err)
            break
        if not key: break
        if endtoken and key == endtoken: break

        # call extension function with the current token, the parser, and the dict
        if extension:
            found_flag, msg = extension(key, parser, attrs)
            #request.log("%r = extension(%r, parser, %r)" % (msg, key, attrs))
            if found_flag:
                continue
            elif msg:
                break
            #else (we found nothing, but also didn't have an error msg) we just continue below:

        try:
            eq = parser.get_token()
        except ValueError, err:
            msg = str(err)
            break
        if eq != "=":
            msg = _('Expected "=" to follow "%(token)s"') % {'token': key}
            break

        try:
            val = parser.get_token()
        except ValueError, err:
            msg = str(err)
            break
        if not val:
            msg = _('Expected a value for key "%(token)s"') % {'token': key}
            break

        key = escape(key) # make sure nobody cheats

        # safely escape and quote value
        if val[0] in ["'", '"']:
            val = escape(val)
        else:
            val = '"%s"' % escape(val, 1)

        attrs[key.lower()] = val

    return attrs, msg or ''


def taintfilename(basename):
    """
    Make a filename that is supposed to be a plain name secure, i.e.
    remove any possible path components that compromise our system.
    
    @param basename: (possibly unsafe) filename
    @rtype: string
    @return: (safer) filename
    """
    for x in (os.pardir, ':', '/', '\\', '<', '>'):
        basename = basename.replace(x, '_')

    return basename


def mapURL(request, url):
    """
    Map URLs according to 'cfg.url_mappings'.
    
    @param url: a URL
    @rtype: string
    @return: mapped URL
    """
    # check whether we have to map URLs
    if request.cfg.url_mappings:
        # check URL for the configured prefixes
        for prefix in request.cfg.url_mappings.keys():
            if url.startswith(prefix):
                # substitute prefix with replacement value
                return request.cfg.url_mappings[prefix] + url[len(prefix):]

    # return unchanged url
    return url


def getUnicodeIndexGroup(name):
    """
    Return a group letter for `name`, which must be a unicode string.
    Currently supported: Hangul Syllables (U+AC00 - U+D7AF)
    
    @param name: a string
    @rtype: string
    @return: group letter or None
    """
    c = name[0]
    if u'\uAC00' <= c <= u'\uD7AF': # Hangul Syllables
        return unichr(0xac00 + (int(ord(c) - 0xac00) / 588) * 588)
    else:
        return c.upper() # we put lower and upper case words into the same index group


def isStrictWikiname(name, word_re=re.compile(ur"^(?:[%(u)s][%(l)s]+){2,}$" % {'u':chartypes.chars_upper, 'l':chartypes.chars_lower})):
    """
    Check whether this is NOT an extended name.
    
    @param name: the wikiname in question
    @rtype: bool
    @return: true if name matches the word_re
    """
    return word_re.match(name)


def isPicture(url):
    """
    Is this a picture's url?
    
    @param url: the url in question
    @rtype: bool
    @return: true if url points to a picture
    """
    extpos = url.rfind(".")
    return extpos > 0 and url[extpos:].lower() in ['.gif', '.jpg', '.jpeg', '.png', '.bmp', '.ico', ]


def link_tag(params, text=None, formatter=None, on=None, **kw):
    """ Create a link.

    TODO: cleanup css_class

    @param request: the request object
    @param params: parameter string appended to the URL after the scriptname/
    @param text: text / inner part of the <a>...</a> link - does NOT get
                 escaped, so you can give HTML here and it will be used verbatim
    @param formatter: the formatter object to use
    @param on: opening/closing tag only
    @keyword attrs: additional attrs (HTMLified string) (removed in 1.5.3)
    @rtype: string
    @return: formatted link tag
    """
    if kw.has_key('css_class'):
        css_class = kw['css_class']
        del kw['css_class'] # one time is enough
    else:
        css_class = None
    id = kw.get('id', None)
    name = kw.get('name', None)
    if text is None:
        text = params # default
    if formatter:
        url = "%s/%s" % (request.getScriptname(), params)
        if on != None:
            return formatter.url(on, url, css_class, **kw)
        return (formatter.url(1, url, css_class, **kw) +
                formatter.rawHTML(text) +
                formatter.url(0))
    if on != None and not on:
        return '</a>'
    
    attrs = ''
    if css_class:
        attrs += ' class="%s"' % css_class
    if id:
        attrs += ' id="%s"' % id
    if name:
        attrs += ' name="%s"' % name
    result = '<a%s href="%s/%s">' % (attrs, request.getScriptname(), params)
    if on:
        return result
    else:
        return "%s%s</a>" % (result, text)


def inputFile2pageName(filename):
    assert filename.endswith('.wiki')
    return filename[:-5]
    

def pageName2inputFile(pagename):
    assert not pagename.endswith('.wiki')
    return pagename + '.wiki'
    
    
def pageName2outputFile(pagename):
    assert not pagename.endswith('.wiki')
    if pagename == config.general.indexpagename:
        pagename = 'index'
    return pagename + '.html'
    

def inputFile2outputFile(filename):
    return pageName2outputFile(inputFile2pageName(filename))


def assertFileNameCase(filename):
    if sys.platform.startswith('win'):
        path, name = os.path.split(filename)
        if path:
            assert name in os.listdir(path), '%s: file does not exist in %s' % (name, path)
        else:
            assert name in os.listdir('.'), '%s: file does not exist' % name


def fixFileNameCase(filename):
    if sys.platform.startswith('win'):
        path, name = os.path.split(filename)
        if path:
            lst = os.listdir(path)
        else:
            lst = os.listdir('.')
        lstlow = [x.lower() for x in lst]
        try:
            return os.path.join(path, lst[lstlow.index(name.lower())])
        except ValueError:
            pass
    return filename


# vim:set sw=4 et:
