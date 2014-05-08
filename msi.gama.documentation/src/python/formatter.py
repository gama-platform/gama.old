# -*- coding: iso-8859-1 -*-
"""
    MoinMoin - "text/html+css" Formatter

    @copyright: 2000 - 2004 by Jürgen Hermann <jh@web.de>
    @license: GNU GPL, see COPYING for details.
"""


import time
import sys, os

import wikiutil
import config

try:
    # use the built-in set type of Py2.6
    Set = set
except NameError:
    from sets import Set


warnings_history = []
def warning(s):
    if s not in warnings_history:
        print >>sys.stderr, 'warning: %s' % s
        warnings_history.append(s)


class FormatterBase:
    """ This defines the output interface used all over the rest of the code.

        Note that no other means should be used to generate _content_ output,
        while navigational elements (HTML page header/footer) and the like
        can be printed directly without violating output abstraction.
    """

    hardspace = ' '

    def __init__(self, **kw):
        self.request = None

        self._store_pagelinks = kw.get('store_pagelinks', 0)
        self.pagelinks = []
        self.in_p = 0
        self.in_pre = 0
        self._base_depth = 0
        
        self.pagename = kw.get('pagename', '')

    def lang(self, on, lang_name):
        return ""

    def setPage(self, page):
        self.page = page

    def sysmsg(self, on, **kw):
        """ Emit a system message (embed it into the page).

            Normally used to indicate disabled options, or invalid markup.
        """
        return ""

    # Document Level #####################################################
    
    def startDocument(self, pagename):
        self.pagename = pagename
        return ""

    def endDocument(self):
        return ""

    def startContent(self, content_id="content", **kw):
        return ""

    def endContent(self):
        return ""

    # Links ##############################################################
    
    def pagelink(self, on, pagename='', page=None, **kw):
        """ make a link to page <pagename>. Instead of supplying a pagename,
            it is also possible to give a live Page object, then page.page_name
            will be used.
        """
        if not self._store_pagelinks or not on or kw.get('generated'): 
            return ''
        if not pagename and page:
            pagename = page.page_name
        pagename = self.request.normalizePagename(pagename)
        if pagename and pagename not in self.pagelinks:
            self.pagelinks.append(pagename)

    def url(self, on, url=None, css=None, **kw):
        raise NotImplementedError

    def anchordef(self, name):
        return ""

    def anchorlink(self, on, name='', **kw):
        return ""

    def image(self, src=None, **kw):
        """An inline image.

        Extra keyword arguments are according to the HTML <img> tag attributes.
        In particular an 'alt' or 'title' argument should give a description
        of the image.
        """
        title = src
        for titleattr in ('title', 'html__title', 'alt', 'html__alt'):
            if kw.has_key(titleattr):
                title = kw[titleattr]
                break
        if title:
            return '[Image:%s]' % title
        return '[Image]'

    def smiley(self, text):
        return text

    def nowikiword(self, text):
        return self.text(text)

    # Text and Text Attributes ########################################### 
    
    def text(self, text, **kw):
        return self._text(text)

    def _text(self, text):
        raise NotImplementedError

    def strong(self, on, **kw):
        raise NotImplementedError

    def emphasis(self, on, **kw):
        raise NotImplementedError

    def underline(self, on, **kw):
        raise NotImplementedError

    def highlight(self, on, **kw):
        raise NotImplementedError

    def sup(self, on, **kw):
        raise NotImplementedError

    def sub(self, on, **kw):
        raise NotImplementedError

    def strike(self, on, **kw):
        raise NotImplementedError

    def code(self, on, **kw):
        raise NotImplementedError

    def preformatted(self, on, **kw):
        self.in_pre = on != 0

    def small(self, on, **kw):
        raise NotImplementedError

    def big(self, on, **kw):
        raise NotImplementedError

    # special markup for syntax highlighting #############################

    def code_area(self, on, code_id, **kw):
        raise NotImplementedError

    def code_line(self, on):
        raise NotImplementedError

    def code_token(self, tok_text, tok_type):
        raise NotImplementedError

    # Paragraphs, Lines, Rules ###########################################

    def linebreak(self, preformatted=1):
        raise NotImplementedError

    def paragraph(self, on, **kw):
        self.in_p = on != 0

    def rule(self, size=0, **kw):
        raise NotImplementedError

    def icon(self, type):
        return type

    # Lists ##############################################################

    def number_list(self, on, type=None, start=None, **kw):
        raise NotImplementedError

    def bullet_list(self, on, **kw):
        raise NotImplementedError

    def listitem(self, on, **kw):
        raise NotImplementedError

    def definition_list(self, on, **kw):
        raise NotImplementedError

    def definition_term(self, on, compact=0, **kw):
        raise NotImplementedError

    def definition_desc(self, on, **kw):
        raise NotImplementedError

    def heading(self, on, depth, **kw):
        raise NotImplementedError

    # Tables #############################################################
    
    def table(self, on, attrs={}, **kw):
        raise NotImplementedError

    def table_row(self, on, attrs={}, **kw):
        raise NotImplementedError

    def table_cell(self, on, attrs={}, **kw):
        raise NotImplementedError

    # Dynamic stuff / Plugins ############################################
    
    def macro(self, macro_obj, name, args):
        # call the macro
        return macro_obj.execute(name, args)    

    def _get_bang_args(self, line):
        if line[:2] == '#!':
            try:
                name, args = line[2:].split(None, 1)
            except ValueError:
                return ''
            else:
                return args
        return None

    def processor(self, processor_name, lines, is_parser=0):
        """ processor_name MUST be valid!
            writes out the result instead of returning it!
        """
        return ''

    def dynamic_content(self, parser, callback, arg_list=[], arg_dict={},
                        returns_content=1):
        content = parser[callback](*arg_list, **arg_dict)
        if returns_content:
            return content
        else:
            return ''

    # Other ##############################################################
    
    def div(self, on, **kw):
        """ open/close a blocklevel division """
        return ""
    
    def span(self, on, **kw):
        """ open/close a inline span """
        return ""
    
    def rawHTML(self, markup):
        raise NotImplementedError

    def escapedText(self, on, **kw):
        """ This allows emitting text as-is, anything special will
            be escaped (at least in HTML, some text output format
            would possibly do nothing here)
        """
        return ""

    def comment(self, text):
        return ""


prettyprint = False

# These are the HTML elements that we treat as block elements.
_blocks = Set(['dd', 'div', 'dl', 'dt', 'form', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
               'li', 'ol', 'p', 'pre', 'table', 'tbody', 'td', 'tfoot', 'th',
               'thead', 'tr', 'ul', 'blockquote', ])

# These are the HTML elements which are typically only used with
# an opening tag without a separate closing tag.  We do not
# include 'script' or 'style' because sometimes they do have
# content, and also IE has a parsing bug with those two elements (only)
# when they don't have a closing tag even if valid XHTML.

_self_closing_tags = Set(['area', 'base', 'br', 'col', 'frame', 'hr', 'img', 'input',
                          'isindex', 'link', 'meta', 'param'])

# We only open those tags and let the browser auto-close them:
_auto_closing_tags = Set(['p'])

# These are the elements which generally should cause an increase in the
# indention level in the html souce code.
_indenting_tags = Set(['ol', 'ul', 'dl', 'li', 'dt', 'dd', 'tr', 'td'])

# These are the elements that discard any whitespace they contain as
# immediate child nodes.
_space_eating_tags = Set(['colgroup', 'dl', 'frameset', 'head', 'map' 'menu',
                          'ol', 'optgroup', 'select', 'table', 'tbody', 'tfoot',
                          'thead', 'tr', 'ul'])

# These are standard HTML attributes which are typically used without any
# value; e.g., as boolean flags indicated by their presence.
_html_attribute_boolflags = Set(['compact', 'disabled', 'ismap', 'nohref',
                                 'noresize', 'noshade', 'nowrap', 'readonly',
                                 'selected', 'wrap'])

# These are all the standard HTML attributes that are allowed on any element.
_common_attributes = Set(['accesskey', 'class', 'dir', 'disabled', 'id', 'lang',
                          'style', 'tabindex', 'title'])


def rewrite_attribute_name(name, default_namespace='html'):
    """
    Takes an attribute name and tries to make it HTML correct.

    This function takes an attribute name as a string, as it may be
    passed in to a formatting method using a keyword-argument syntax,
    and tries to convert it into a real attribute name.  This is
    necessary because some attributes may conflict with Python
    reserved words or variable syntax (such as 'for', 'class', or
    'z-index'); and also to help with backwards compatibility with
    older versions of MoinMoin where different names may have been
    used (such as 'content_id' or 'css').

    Returns a tuple of strings: (namespace, attribute).

    Namespaces: The default namespace is always assumed to be 'html',
    unless the input string contains a colon or a double-underscore;
    in which case the first such occurance is assumed to separate the
    namespace prefix from name.  So, for example, to get the HTML
    attribute 'for' (as on a <label> element), you can pass in the
    string 'html__for' or even '__for'.

    Hyphens:  To better support hyphens (which are not allowed in Python
    variable names), all occurances of two underscores will be replaced
    with a hyphen.  If you use this, then you must also provide a
    namespace since the first occurance of '__' separates a namespace
    from the name.

    Special cases: Within the 'html' namespace, mainly to preserve
    backwards compatibility, these exceptions ars recognized:
    'content_type', 'content_id', 'css_class', and 'css'.
    Additionally all html attributes starting with 'on' are forced to
    lower-case.  Also the string 'xmlns' is recognized as having
    no namespace.

    Examples:
        'id' -> ('html', 'id')
        'css_class' -> ('html', 'class')
        'content_id' -> ('html', 'id')
        'content_type' -> ('html', 'type')
        'html__for' -> ('html', 'for)
        'xml__space' -> ('xml', 'space')
        '__z__index' -> ('html', 'z-index')
        '__http__equiv' -> ('html', 'http-equiv')
        'onChange' -> ('html', 'onchange')
        'xmlns' -> ('', 'xmlns')
        'xmlns__abc' -> ('xmlns', 'abc')

    (In actuality we only deal with namespace prefixes, not any real
    namespace URI...we only care about the syntax not the meanings.)
    """

    # Handle any namespaces (just in case someday we support XHTML)
    if ':' in name:
        ns, name = name.split(':', 1)
    elif '__' in name:
        ns, name = name.split('__', 1)
    elif name == 'xmlns':
        ns = ''
    else:
        ns = default_namespace

    name.replace('__', '-')
    if ns == 'html':
        # We have an HTML attribute, fix according to DTD
        if name == 'content_type': # MIME type such as in <a> and <link> elements
            name =  'type'
        elif name == 'content_id': # moin historical convention
            name =  'id'
        elif name in ('css_class', 'css'): # to avoid python word 'class'
            name = 'class'
        elif name.startswith('on'): # event handler hook
            name = name.lower()
    return ns, name


def extend_attribute_dictionary(attributedict, ns, name, value):
    """Add a new attribute to an attribute dictionary, merging values where possible.

    The attributedict must be a dictionary with tuple-keys of the form:
    (namespace, attrname).

    The given ns, name, and value will be added to the dictionary.  It
    will replace the old value if it existed, except for a few special
    cases where the values are logically merged instead (CSS class
    names and style rules).

    As a special case, if value is None (not just ''), then the
    attribute is actually deleted from the dictionary.
    """

    key = ns, name
    if value is None:
        if attributedict.has_key(key):
            del attributedict[key]
    else:
        if ns == 'html' and attributedict.has_key(key):
            if name == 'class':
                # CSS classes are appended by space-separated list
                value = attributedict[key] + ' ' + value
            elif name == 'style':
                # CSS styles are appended by semicolon-separated rules list
                value = attributedict[key] + '; ' + value
            elif name in _html_attribute_boolflags:
                # All attributes must have a value. According to XHTML those
                # traditionally used as flags should have their value set to
                # the same as the attribute name.
                value = name
        attributedict[key] = value


class Formatter(FormatterBase):
    """
        Send HTML data.
    """

    #hardspace = '&nbsp;'
    hardspace = '&#160;'
    indentspace = ' '

    def __init__(self, **kw):
        FormatterBase.__init__(self, **kw)

        # inline tags stack. When an inline tag is called, it goes into
        # the stack. When a block element starts, all inline tags in
        # the stack are closed.
        self._inlineStack = []

        # stack of all tags
        self._tag_stack = []
        self._indent_level = 0

        self._in_code = 0 # used by text_gedit
        self._in_code_area = 0
        self._in_code_line = 0
        self._code_area_num = 0
        self._code_area_state = ['', 0, -1, -1, 0]
        self._show_section_numbers = None
        self._content_ids = []
        self.pagelink_preclosed = False
        self._is_included = kw.get('is_included', False)
        self.request = None

    # Primitive formatter functions #####################################

    # all other methods should use these to format tags. This keeps the
    # code clean and handle pathological cases like unclosed p and
    # inline tags.

    def _formatAttributes(self, attr=None, allowed_attrs=None, **kw):
        """ Return HTML attributes formatted as a single string. (INTERNAL USE BY HTML FORMATTER ONLY!)

        @param attr: dict containing keys and values
        @param allowed_attrs: A list of allowable attribute names
        @param kw: other arbitrary attributes expressed as keyword arguments.
        @rtype: string
        @return: formated attributes or empty string

        The attributes and their values can either be given in the
        'attr' dictionary, or as extra keyword arguments.  They are
        both merged together.  See the function
        rewrite_attribute_name() for special notes on how to name
        attributes.

        Setting a value to None rather than a string (or string
        coercible) will remove that attribute from the list.
        
        If the list of allowed_attrs is provided, then an error is
        raised if an HTML attribute is encountered that is not in that
        list (or is not a common attribute which is always allowed or
        is not in another XML namespace using the double-underscore
        syntax).
        """

        # Merge the attr dict and kw dict into a single attributes
        # dictionary (rewriting any attribute names, extracting
        # namespaces, and merging some values like css classes).
        attributes = {} # dict of key=(namespace,name): value=attribute_value
        if attr:
            for a, v in attr.items():
                a_ns, a_name = rewrite_attribute_name(a)
                extend_attribute_dictionary(attributes, a_ns, a_name, v)
        if kw:
            for a, v in kw.items():
                a_ns, a_name = rewrite_attribute_name(a)
                extend_attribute_dictionary(attributes, a_ns, a_name, v)

        # Add title attribute if missing, but it has an alt.
        if attributes.has_key(('html', 'alt')) and not attributes.has_key(('html', 'title')):
            attributes[('html', 'title')] = attributes[('html', 'alt')]

        # Check all the HTML attributes to see if they are known and
        # allowed.  Ignore attributes if in non-HTML namespaces.
        if allowed_attrs:
            for name in [key[1] for key in attributes.keys() if key[0] == 'html']:
                if name in _common_attributes or name in allowed_attrs:
                    pass
                elif name.startswith('on'):
                    pass  # Too many event handlers to enumerate, just let them all pass.
                else:
                    # Unknown or unallowed attribute.
                    err = 'Illegal HTML attribute "%s" passed to formatter' % name
                    raise ValueError(err)

        # Finally, format them all as a single string.
        if attributes:
            # Construct a formatted string containing all attributes
            # with their values escaped.  Any html:* namespace
            # attributes drop the namespace prefix.  We build this by
            # separating the attributes into three categories:
            #
            #  * Those without any namespace (should only be xmlns attributes)
            #  * Those in the HTML namespace (we drop the html: prefix for these)
            #  * Those in any other non-HTML namespace, including xml:

            xmlnslist = ['%s="%s"' % (k[1], wikiutil.escape(v, 1))
                         for k, v in attributes.items() if not k[0]]
            htmllist = ['%s="%s"' % (k[1], wikiutil.escape(v, 1))
                        for k, v in attributes.items() if k[0] == 'html']
            otherlist = ['%s:%s="%s"' % (k[0], k[1], wikiutil.escape(v, 1))
                         for k, v in attributes.items() if k[0] and k[0] != 'html']

            # Join all these lists together in a space-separated string.  Also
            # prefix the whole thing with a space too.
            htmllist.sort()
            otherlist.sort()
            all = [''] + xmlnslist + htmllist + otherlist
            return ' '.join(all)
        return ''

    def _open(self, tag, newline=False, attr=None, allowed_attrs=None, **kw):
        """ Open a tag with optional attributes (INTERNAL USE BY HTML FORMATTER ONLY!)
        
        @param tag: html tag, string
        @param newline: render tag so following data is on a separate line
        @param attr: dict with tag attributes
        @param allowed_attrs: list of allowed attributes for this element
        @param kw: arbitrary attributes and values
        @rtype: string ?
        @return: open tag with attributes as a string
        """
        # If it is self-closing, then don't expect a closing tag later on.
        #is_self_closing = (tag in _self_closing_tags) and ' /' or ''
        # always empty since we're using html, not xhtml
        
        # Ben Modif
        # we want something close to XHTML
        is_self_closing = ''

        if tag in _blocks:
            # Block elements
            result = []
            
            attributes = attr
            
            # Format
            attributes = self._formatAttributes(attributes, allowed_attrs=allowed_attrs, **kw)
            result.append('<%s%s%s>' % (tag, attributes, is_self_closing))
            if newline:
                result.append(self._newline())
            tagstr = ''.join(result)
        else:
            # Inline elements
            # Add to inlineStack
            if not is_self_closing:
                # Only push on stack if we expect a close-tag later
                self._inlineStack.append(tag)
            # Ben modif 
            # To take into account self-closing tags
#                tagstr = '<%s%s%s>' % (tag,
#                                       self._formatAttributes(attr, allowed_attrs, **kw),
#                                       is_self_closing)             
            if tag in _self_closing_tags:
                tagstr = '<%s%s%s>' % (tag,
                                       self._formatAttributes(attr, allowed_attrs, **kw),
                                       '/')              
            else:
                # Format
                tagstr = '<%s%s%s>' % (tag,
                                       self._formatAttributes(attr, allowed_attrs, **kw),
                                       is_self_closing)              
        # XXX SENSE ???
        #if not self.close:
        #    self._tag_stack.append(tag)
        #    if tag in _indenting_tags:
        #        self._indent_level += 1
        return tagstr

    def _close(self, tag, newline=False):
        """ Close tag (INTERNAL USE BY HTML FORMATTER ONLY!)

        @param tag: html tag, string
        @param newline: render tag so following data is on a separate line
        @rtype: string
        @return: closing tag as a string
        """
        if tag in _self_closing_tags: # MODIF ben : or tag in _auto_closing_tags:
            # This tag was already closed
            tagstr = ''
        elif tag in _blocks:
            # Block elements
            # Close all tags in inline stack
            # Work on a copy, because close(inline) manipulate the stack
            result = []
            stack = self._inlineStack[:]
            stack.reverse()
            for inline in stack:
                result.append(self._close(inline))
            # Format with newline
            if newline:
                result.append(self._newline())
            result.append('</%s>' % (tag))
            tagstr = ''.join(result)            
        else:
            # Inline elements 
            # Pull from stack, ignore order, that is not our problem.
            # The code that calls us should keep correct calling order.
            if tag in self._inlineStack:
                self._inlineStack.remove(tag)
            tagstr = '</%s>' % tag

        # XXX see other place marked with "SENSE"
        #if tag in _self_closing_tags:
        #    self._tag_stack.pop()
        #    if tag in _indenting_tags:
        #        # decrease indent level
        #        self._indent_level -= 1
        if newline:
            tagstr += self._newline()
        return tagstr

    # Public methods ###################################################

    def lang(self, on, lang_name):
        """ Insert text with specific lang and direction.
        
            Enclose within span tag if lang_name is different from
            the current lang    
        """
        return ''            
                
    # Links ##############################################################
    
    def pagelink(self, on, pagename='', anchor=None, css=None, **kw):
        """ Link to a page.

            formatter.text_python will use an optimized call with a page!=None
            parameter. DO NOT USE THIS YOURSELF OR IT WILL BREAK.

            See wikiutil.link_tag() for possible keyword parameters.
        """
        apply(FormatterBase.pagelink, (self, on, pagename), kw)
        
        if on:
            attrs = {}

            # Warn about non-existing pages.
            if not os.path.exists(wikiutil.pageName2inputFile(pagename)):
                warning('linked "%s" page does not exists' % pagename)

            if pagename == config.general.indexpagename:
                pagename = 'index'

            # FIXME:
            # Ben modif
            # url = wikiutil.quoteWikinameURL(pagename + ".html")
            # Add anchor
            # if anchor:
            #    url = "%s#%s" % (url, wikiutil.url_quote_plus(anchor))
            ####
            # Add anchor
            if anchor:
                url = "%s#%s" % (wikiutil.quoteWikinameURL(pagename + ".html"), wikiutil.url_quote_plus(anchor))
            else:
                url = "#%s" % (wikiutil.quoteWikinameURL(pagename))

            attrs['href'] = url

            if css:
                attrs['class'] = css
            
            markup = self._open('a', attr=attrs, **kw)
        else:
            markup = self._close('a')
        return markup
        

    def url(self, on, url=None, css=None, do_escape=0, **kw):
        """
        Inserts an <a> element.

        Call once with on=1 to start the link, and again with on=0
        to end it (no other arguments are needed when on==0).

        do_escape: XXX doesn't work yet

        Keyword params:
            url - the URL to link to; will go through Wiki URL mapping.
            css - a space-separated list of CSS classes
            attrs -  just include this string verbatim inside
                     the <a> element; can be used for arbitrary attrs;
                     all escaping and quoting is the caller's responsibility.

        Note that the 'attrs' keyword argument is for backwards compatibility
        only.  It should not be used for new code -- instead just pass
        any attributes in as separate keyword arguments.

        1.5.3: removed ugly "attrs" keyword argument handling code
        """
        if on:
            attrs = {}

            # Handle the URL mapping
            if url is None and kw.has_key('href'):
                url = kw['href']
                del kw['href']
            if url is not None:
                #url = wikiutil.mapURL(self.request, url)
                # TODO just calling url_quote does not work, as it will also quote "http:" to "http%xx" X)
                if 0: # do_escape: # protocol and server part must not get quoted, path should get quoted
                    url = wikiutil.url_quote(url)
                attrs['href'] = url
                
                # Warn about non-existing local files.
                if url[:16].find('://') < 0:
                    name = wikiutil.url_unquote(url.split('#', 1)[0])
                    if name and not os.path.exists(name):
                        warning('linked "%s" file does not exists' % name)

            if css:
                attrs['class'] = css
            
            markup = self._open('a', attr=attrs, **kw)
        else:
            markup = self._close('a')
        return markup

    def anchordef(self, id):
        """Inserts an invisible element used as a link target.

        Inserts an empty <span> element with an id attribute, used as an anchor
        for link references.  We use <span></span> rather than <span/>
        for browser portability.
        """
        # Don't add newlines, \n, as it will break pre and
        # line-numbered code sections (from line_achordef() method).
        #return '<a id="%s"></a>' % (id, ) # do not use - this breaks PRE sections for IE
        return '<span class="anchor" id="%s"></span>' % wikiutil.escape(id, 1)

    def anchorlink(self, on, name='', **kw):
        """Insert an <a> link pointing to an anchor on the same page.

        Call once with on=1 to start the link, and a second time with
        on=0 to end it.  No other arguments are needed on the second
        call.

        The name argument should be the same as the id provided to the
        anchordef() method, or some other elment.  It should NOT start
        with '#' as that will be added automatically.

        The id argument, if provided, is instead the id of this link
        itself and not of the target element the link references.
        """

        attrs = self._langAttr()
        if name:
            attrs['href'] = '#%s' % name
        if kw.has_key('href'):
            del kw['href']
        if on:
            str = self._open('a', attr=attrs, **kw)
        else:
            str = self._close('a')
        return str

    # Text ##############################################################
    
    def _text(self, text):
        text = wikiutil.escape(text)
        if self._in_code:
            text = text.replace(' ', self.hardspace)
        return text

    # Inline ###########################################################
        
    def strong(self, on, **kw):
        """Creates an HTML <strong> element.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'strong'
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)

    def emphasis(self, on, **kw):
        """Creates an HTML <em> element.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'em'
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)

    def underline(self, on, **kw):
        """Creates a text span for underlining (css class "u").

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'span'
        if on:
            return self._open(tag, attr={'class': 'u'}, allowed_attrs=[], **kw)
        return self._close(tag)

    def highlight(self, on, **kw):
        """Creates a text span for highlighting (css class "highlight").

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'strong'
        if on:
            return self._open(tag, attr={'class': 'highlight'}, allowed_attrs=[], **kw)
        return self._close(tag)

    def sup(self, on, **kw):
        """Creates a <sup> element for superscript text.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'sup'
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)

    def sub(self, on, **kw):
        """Creates a <sub> element for subscript text.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'sub'
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)

    def strike(self, on, **kw):
        """Creates a text span for line-through (strikeout) text (css class 'strike').

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        # This does not use <strike> because has been deprecated in standard HTML.
        tag = 'span'
        if on:
            return self._open(tag, attr={'style': 'text-decoration: line-through'}, allowed_attrs=[], **kw)
        return self._close(tag)

    def code(self, on, **kw):
        """Creates a <tt> element for inline code or monospaced text.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.

        Any text within this section will have spaces converted to
        non-break spaces.
        """
        # Ben modif
        # <tt> seems to be deprecated in HTML 5 so I remove it and replaced it by <span style="font-family:Courier New;">
        # tag = 'tt'
        #
        # self._in_code = on        
        # if on:
        #    return self._open(tag, allowed_attrs=[], **kw)
        
        tag = 'span'
        # Maybe we don't need this, because we have tt will be in inlineStack.
        self._in_code = on        
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)
        
    def small(self, on, **kw):
        """Creates a <small> element for smaller font.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'small'
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)

    def big(self, on, **kw):
        """Creates a <big> element for larger font.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'big'
        if on:
            return self._open(tag, allowed_attrs=[], **kw)
        return self._close(tag)


    # Block elements ####################################################

    def preformatted(self, on, **kw):
        """Creates a preformatted text region, with a <pre> element.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        FormatterBase.preformatted(self, on)
        tag = 'pre'
        if on:
            return self._open(tag, newline=1, **kw)
        return self._close(tag)
                
    def code_area(self, on, code_id, code_type='code', show=0, start=-1, step=-1):
        """Creates a formatted code region, with line numbering.

        This region is formatted as a <div> with a <pre> inside it.  The
        code_id argument is assigned to the 'id' of the div element, and
        must be unique within the document.  The show, start, and step are
        used for line numbering.

        Note this is not like most formatter methods, it can not take any
        extra keyword arguments.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        res = []
        ci = self.request.makeUniqueID('CA-%s_%03d' % (code_id, self._code_area_num))
        if on:
            # Open a code area
            self._in_code_area = 1
            self._in_code_line = 0
            self._code_area_state = [ci, show, start, step, start]

            # Open the code div - using left to right always!
            attr = {'class': 'codearea', 'lang': 'en', 'dir': 'ltr'}
            res.append(self._open('div', attr=attr))

            # Open pre - using left to right always!
            attr = {'id': self._code_area_state[0], 'lang': 'en', 'dir': 'ltr'}
            res.append(self._open('pre', newline=True, attr=attr))
        else:
            # Close code area
            res = []
            if self._in_code_line:
                res.append(self.code_line(0))
            res.append(self._close('pre'))
            res.append(self._close('div'))

            # Update state
            self._in_code_area = 0
            self._code_area_num += 1

        return ''.join(res)

    def code_line(self, on):
        res = ''
        if not on or (on and self._in_code_line):
            res += '</span>\n'
        if on:
            res += '<span class="line">'
            if self._code_area_state[1] > 0:
                res += '<span class="LineNumber">%4d </span>' % (self._code_area_state[4], )
                self._code_area_state[4] += self._code_area_state[3]
        self._in_code_line = on != 0
        return res

    def code_token(self, on, tok_type):
        return ['<span class="%s">' % tok_type, '</span>'][not on]

    # Paragraphs, Lines, Rules ###########################################
    
    def _indent_spaces(self):
        """Returns space(s) for indenting the html source so list nesting is easy to read.

        Note that this mostly works, but because of caching may not always be accurate."""
        if prettyprint:
            return self.indentspace * self._indent_level
        else:
            return ''

    def _newline(self):
        """Returns the whitespace for starting a new html source line, properly indented."""
        if prettyprint:
            return '\n' + self._indent_spaces()
        else:
            return ''

    def linebreak(self, preformatted=1):
        """Creates a line break in the HTML output.
        
        If preformatted is true a <br> element is inserted, otherwise
        the linebreak will only be visible in the HTML source.
        """
        if self._in_code_area:
            preformatted = 1
        return ['\n', '<br>\n'][not preformatted] + self._indent_spaces()
        
    def paragraph(self, on, **kw):
        """Creates a paragraph with a <p> element.
        
        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        FormatterBase.paragraph(self, on)
        tag = 'p'
        if on:
            tagstr = self._open(tag, **kw)
        else:
            tagstr = self._close(tag)
        return tagstr

    def rule(self, size=None, **kw):
        """Creates a horizontal rule with an <hr> element.

        If size is a number in the range [1..6], the CSS class of the rule
        is set to 'hr1' through 'hr6'.  The intent is that the larger the
        size number the thicker or bolder the rule will be.
        """
        if size and 1 <= size <= 6:
            # Add hr class: hr1 - hr6
            return self._open('hr', newline=1, attr={'class': 'hr%d' % size}, **kw)
        return self._open('hr', newline=1, **kw)
                
    def icon(self, type):
        return self.request.theme.make_icon(type)

    def image(self, src=None, css=None, **kw):
        """Creates an inline image with an <img> element.

        The src argument must be the URL to the image file.
        """
        if src:
            kw['src'] = src
            
            if css:
                kw['class'] = css
            
            if src[:16].find('://') < 0:
                name = wikiutil.url_unquote(src)
                if not os.path.exists(name):
                    warning('"%s" image does not exists' % name)

        return self._open('img', **kw)

    # Lists ##############################################################

    def number_list(self, on, type=None, start=None, **kw):
        """Creates an HTML ordered list, <ol> element.

        The 'type' if specified can be any legal numbered
        list-style-type, such as 'decimal','lower-roman', etc.

        The 'start' argument if specified gives the numeric value of
        the first list item (default is 1).

        Call once with on=1 to start the list, and a second time
        with on=0 to end it.
        """
        tag = 'ol'
        if on:
            attr = {}
            if type is not None:
                attr['type'] = type
            if start is not None:
                attr['start'] = start
            tagstr = self._open(tag, newline=1, attr=attr, **kw)
        else:
            tagstr = self._close(tag, newline=1)
        return tagstr
    
    def bullet_list(self, on, **kw):
        """Creates an HTML ordered list, <ul> element.

        The 'type' if specified can be any legal unnumbered
        list-style-type, such as 'disc','square', etc.

        Call once with on=1 to start the list, and a second time
        with on=0 to end it.
        """
        tag = 'ul'
        if on:
            tagstr = self._open(tag, newline=1, **kw)
        else:
            tagstr = self._close(tag, newline=1)
        return tagstr

    def listitem(self, on, **kw):
        """Adds a list item, <li> element, to a previously opened
        bullet or number list.

        Call once with on=1 to start the region, and a second time
        with on=0 to end it.
        """
        tag = 'li'
        if on:
            tagstr = self._open(tag, newline=1, **kw)
        else:
            tagstr = self._close(tag, newline=1)
        return tagstr

    def definition_list(self, on, **kw):
        """Creates an HTML definition list, <dl> element.

        Call once with on=1 to start the list, and a second time
        with on=0 to end it.
        """
        tag = 'dl'
        if on:
            tagstr = self._open(tag, newline=1, **kw)
        else:
            tagstr = self._close(tag, newline=1)
        return tagstr

    def definition_term(self, on, **kw):
        """Adds a new term to a definition list, HTML element <dt>.

        Call once with on=1 to start the term, and a second time
        with on=0 to end it.
        """
        tag = 'dt'
        if on:
            tagstr = self._open(tag, newline=1, **kw)
        else:
            tagstr = self._close(tag, newline=0)
        return tagstr

    def definition_desc(self, on, **kw):
        """Gives the definition to a definition item, HTML element <dd>.

        Call once with on=1 to start the definition, and a second time
        with on=0 to end it.
        """
        tag = 'dd'
        if on:
            tagstr = self._open(tag, newline=1, **kw)
        else:
            tagstr = self._close(tag, newline=0)
        return tagstr

    def heading(self, on, depth, **kw):
        # remember depth of first heading, and adapt counting depth accordingly
        if not self._base_depth:
            self._base_depth = depth

        count_depth = max(depth - (self._base_depth - 1), 1)

        # check numbering, possibly changing the default
        if 0:
            if self._show_section_numbers is None:
                self._show_section_numbers = self.cfg.show_section_numbers
                numbering = self.request.getPragma('section-numbers', '').lower()
                if numbering in ['0', 'off']:
                    self._show_section_numbers = 0
                elif numbering in ['1', 'on']:
                    self._show_section_numbers = 1
                elif numbering in ['2', '3', '4', '5', '6']:
                    # explicit base level for section number display
                    self._show_section_numbers = int(numbering)

        heading_depth = depth # + 1

        # closing tag, with empty line after, to make source more readable
        if not on:
            return self._close('h%d' % heading_depth) + '\n'
            
        # create section number
        number = ''
        if self._show_section_numbers:
            # count headings on all levels
            self.request._fmt_hd_counters = self.request._fmt_hd_counters[:count_depth]
            while len(self.request._fmt_hd_counters) < count_depth:
                self.request._fmt_hd_counters.append(0)
            self.request._fmt_hd_counters[-1] = self.request._fmt_hd_counters[-1] + 1
            number = '.'.join(map(str, self.request._fmt_hd_counters[self._show_section_numbers-1:]))
            if number: number += ". "

        # Add space before heading, easier to check source code
        # Ben modif
        attrs = {}
        ## attrs['id'] = 'Table_of_Contents' 
        # Ben modif
        result = '\n' + self._open('h%d' % heading_depth, attr=attrs, **kw)

        # TODO: convert this to readable code
        if 0 and self.request.user.show_topbottom:
            # TODO change top/bottom refs to content-specific top/bottom refs?
            result = ("%s%s%s%s%s%s%s%s" %
                      (result,
                       kw.get('icons', ''),
                       self.url(1, "#bottom", do_escape=0),
                       self.icon('bottom'),
                       self.url(0),
                       self.url(1, "#top", do_escape=0),
                       self.icon('top'),
                       self.url(0)))
        return "%s%s%s" % (result, kw.get('icons', ''), number)

    
    # Tables #############################################################

    _allowed_table_attrs = {
        'table': ['class', 'id', 'style', 'border'],
        'row': ['class', 'id', 'style'],
        '': ['colspan', 'rowspan', 'class', 'id', 'style'],
    }

    def _checkTableAttr(self, attrs, prefix):
        """ Check table attributes

        Convert from wikitable attributes to html 4 attributes.

        @param attrs: attribute dict
        @param prefix: used in wiki table attributes
        @rtype: dict
        @return: valid table attributes
        """
        if not attrs:
            return {}

        result = {}
        s = [] # we collect synthesized style in s
        for key, val in attrs.items():
            # Ignore keys that don't start with prefix
            if prefix and key[:len(prefix)] != prefix:
                continue
            key = key[len(prefix):]
            val = val.strip('"')
            # remove invalid attrs from dict and synthesize style
            if key == 'width':
                s.append("width: %s" % val)
            elif key == 'height':
                s.append("height: %s" % val)
            elif key == 'bgcolor':
                s.append("background-color: %s" % val)
            elif key == 'align':
                s.append("text-align: %s" % val)
            elif key == 'valign':
                s.append("vertical-align: %s" % val)
            # Ignore unknown keys
            if key not in self._allowed_table_attrs[prefix]:
                continue
            result[key] = val
        st = result.get('style', '').split(';')
        st = '; '.join(st + s)
        st = st.strip(';')
        st = st.strip()
        if not st:
            try:
                del result['style'] # avoid empty style attr
            except:
                pass
        else:
            result['style'] = st
        #self.request.log("_checkTableAttr returns %r" % result)
        return result


    def table(self, on, attrs=None, **kw):
        """ Create table

        @param on: start table
        @param attrs: table attributes
        @rtype: string
        @return start or end tag of a table
        """
        result = []
        if on:
            # Open div to get correct alignment with table width smaller
            # than 100%
            result.append(self._open('div', newline=1))

            # Open table
            if not attrs:
                attrs = {}
                # Ben modif
                attrs['border'] = '1'       
            else:
                attrs = self._checkTableAttr(attrs, 'table')
            result.append(self._open('table', newline=1, attr=attrs,
                                     allowed_attrs=self._allowed_table_attrs['table'],
                                     **kw))
            # Ben modif
           # result.append(self._open('tbody', newline=1))
        else:
            # Close tbody, table, and then div
            # Ben modif
            # result.append(self._close('tbody'))
            result.append(self._close('table'))
            result.append(self._close('div'))

        return ''.join(result)    
    
    def table_row(self, on, attrs=None, **kw):
        tag = 'tr'
        if on:
            if not attrs:
                attrs = {}
            else:
                attrs = self._checkTableAttr(attrs, 'row')
            return self._open(tag, newline=1, attr=attrs,
                             allowed_attrs=self._allowed_table_attrs['row'],
                             **kw)
        return self._close(tag) + '\n'
    
    def table_cell(self, on, attrs=None, **kw):
        tag = 'td'
        if on:
            if not attrs:
                attrs = {}
            else:
                attrs = self._checkTableAttr(attrs, '')
            return '  ' + self._open(tag, attr=attrs,
                             allowed_attrs=self._allowed_table_attrs[''],
                             **kw)
        return self._close(tag) + '\n'

    def text(self, text, **kw):
        txt = FormatterBase.text(self, text, **kw)
        if kw:
            return self._open('span', **kw) + txt + self._close('span')
        return txt

    def escapedText(self, text, **kw):
        txt = wikiutil.escape(text)
        if kw:
            return self._open('span', **kw) + txt + self._close('span')
        return txt

    def rawHTML(self, markup):
        return markup

    def sysmsg(self, on, **kw):
        tag = 'div'
        if on:
            return self._open(tag, attr={'class': 'message'}, **kw)
        return self._close(tag)
    
    def div(self, on, **kw):
        tag = 'div'
        if on:
            return self._open(tag, **kw)
        return self._close(tag)

    def span(self, on, **kw):
        tag = 'span'
        if on:
            return self._open(tag, **kw)
        return self._close(tag)
    

# vim:set sw=4 et:
