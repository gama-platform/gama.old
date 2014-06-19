# -*- coding: iso-8859-1 -*-
"""
    MoinMoin - StatWiki Wiki Markup Parser

    @copyright: 2000, 2001, 2002 by Jürgen Hermann <jh@web.de>
    @license: GNU GPL, see COPYING for details.
"""


import os
import re

import config
import chartypes
import wikiutil


class Parser:
    """
    Object that turns Wiki markup into HTML.

    All formatting commands can be parsed one line at a time, though
    some state is carried over between lines.

    Methods named like _*_repl() are responsible to handle the named regex
    patterns defined in print_html().
    """

    # known processing instructions
    #processing_instr = ("##", "#format", "#refresh", "#redirect", "#deprecated",
    #    "#pragma", "#form", "#acl", "#language")
    processing_instr = ('#summary', '#sidebar', '#category', '#execute', '#include')

    # some common strings
    punct_pattern = re.escape(u'''"\'}]|:,.)?!''')
    # Ben modif
    #schema_pattern = ur'\w+'    
    schema_pattern = ur'(http|https|ftp|ftps|file)'

    # some common rules
    word_rule = ur'(?:(?<![%(u)s%(l)s])|^)(?:[%(u)s][%(l)s]+){2,}(?:(?![%(u)s%(l)s])|$)' % {
        'u': chartypes.chars_upper,
        'l': chartypes.chars_lower,
    }
    # Ben modif
    url_rule = ur'%(url_guard)s(%(schema)s)\:([^\s\<\\%(punct)s*]|([%(punct)s][^\s\<%(punct)s]))+' % {
        'url_guard': u'(^|(?<!\w))',
        # Ben Modif 
        'schema': schema_pattern,
        # 'schema': ur'[a-zA-Z_]+',
        'punct': punct_pattern,
    }

    ol_rule = ur"^\s+#"
    # Ben modif 
    # dl_rule = ur"^\s+.*?::\s"
    pair_rule = ur"(\w|\")*\:\:\s*\w+"

    # the big, fat, ugly one ;)
    # BEN: much more ugly: remove the line (?P<emph>_) and (?P<dl>%(dl_rule)s) [after P<ol>]
    #     add: (?P<ent_symbolic>%(pair_rule)s)
    #     modified: (?P<heading>^\s*(?P<hmarker>=+)\s.*\s(?P=hmarker)\s*$)
    #               (?P<li>^\s+\*\s*)
    #               ol_rule = ur"^\s+#\s*"
    formatting_rules = ur"""(?P<ent_numeric>&#(\d{1,5}|x[0-9a-fA-F]+);)
(?:(?P<asterisk>\\\*)
(?P<HTMLtag>\<img\ssrc=\"%(url_rule)s\"\/\>|\<br\s?\/\>)
(?P<uselessWikiTags>\<wiki\:video\surl=\"%(url_rule)s\"\/\>)
(?P<underscore>\\_)
(?P<newline>\\n)
(?P<bold>\*)
(?P<emph>\W_[a-zA-Z0-9_]+_)
(?P<u>__)
(?P<sup>\^.*?\^)
(?P<sub>,,[^,]{1,40},,)
(?P<tt>\{\{\{.*?\}\}\})
(?P<processor>(\{\{\{(#\s*$)))
(?P<pre>(\{\{\{ ?|\}\}\}))
(?P<small>(\~- ?|-\~))
(?P<big>(\~\+ ?|\+\~))
(?P<strike>\~\~)
(?P<rule>-{4,})
(?P<anchor>^\#[^\s\#]+\s$)
(?P<comment>^\#.*$))
(?P<ol>%(ol_rule)s)
(?P<li>^\s+\*)
(?P<li_none>^\s+\.\s*)
(?P<indent>^\s+)
(?P<tableZ>\|\| $)
(?P<table>(?:\|\|)+(?:<[^>]*?>)?(?!\|? $))
(?P<heading>^\s*(?P<hmarker>=+).*(?P=hmarker)\s*$)
(?P<pair>%(pair_rule)s)
(?P<word>%(word_rule)s)
(?P<bracket>\[[^\s\]]+(\s[^\]]+)?\])
(?P<url>%(url_rule)s)
(?P<email>[-\w._+]+\@[\w-]+(\.[\w-]+)+)
(?P<ent_symbolic>&[a-zA-Z]+;)
(?P<ent>[<>&])
(?P<tt_bt>`.*?`)"""  % {

        'punct': punct_pattern,
        'ol_rule': ol_rule,
        #'dl_rule': dl_rule,
        'pair_rule': pair_rule,
        'url_rule': url_rule,
        'word_rule': word_rule,
      }

    # Don't start p before these 
    no_new_p_before = ("heading rule table tableZ tr td "
                       "ul ol dl dt dd li li_none indent "
                       "macro processor pre")
    no_new_p_before = no_new_p_before.split()
    no_new_p_before = dict(zip(no_new_p_before, [1] * len(no_new_p_before)))

    def __init__(self, raw, request, **kw):
        self.raw = raw
        self.request = request
        self.macro = None

        self.is_em = 0
        self.is_b = 0
        self.is_u = 0
        self.is_strike = 0
        self.lineno = 0
        self.in_list = 0 # between <ul/ol/dl> and </ul/ol/dl>
        self.in_li = 0 # between <li> and </li>
        self.in_dd = 0 # between <dd> and </dd>
        self.in_pre = 0
        self.in_table = 0
        self.in_bquote = 0 # between <blockquote> and </blockquote>
        self.is_big = False
        self.is_small = False
        self.inhibit_p = 0 # if set, do not auto-create a <p>aragraph
        self.titles = {}#request._page_headings
        self.table_row_count = 0

        # holds the nesting level (in chars) of open lists
        self.list_indents = []
        self.list_types = []
        
        #self.formatting_rules = self.formatting_rules % {'macronames': u'|'.join(wikimacro.getNames(self.cfg))}

    def _close_item(self, result):
        #result.append("<!-- close item begin -->\n")
        if self.in_table:
            result.append(self.formatter.table(0))
            self.in_table = 0
        if self.in_li:
            self.in_li = 0
            if self.formatter.in_p:
                result.append(self.formatter.paragraph(0))
            result.append(self.formatter.listitem(0))
        if self.in_dd:
            self.in_dd = 0
            if self.formatter.in_p:
                result.append(self.formatter.paragraph(0))
            result.append(self.formatter.definition_desc(0))
        #result.append("<!-- close item end -->\n")

    def _asterisk_repl(self, word):
        return u'*'

    def _underscore_repl(self, word):
        return u'_'

    def _HTMLtag_repl(self, word):
        return word

    def _uselessWikiTags_repl(self, word):
        return ''

    def _newline_repl(self, word):
        return self.formatter.linebreak(False)

    def _u_repl(self, word):
        """Handle underline."""
        self.is_u = not self.is_u
        return self.formatter.underline(self.is_u)

    def _strike_repl(self, word):
        """Handle strikethrough."""
        self.is_strike = not self.is_strike
        return self.formatter.strike(self.is_strike)

    def _small_repl(self, word):
        """Handle small."""
        if word.strip() == '~-' and self.is_small:
            return self.formatter.text(word)
        if word.strip() == '-~' and not self.is_small:
            return self.formatter.text(word)
        self.is_small = not self.is_small
        return self.formatter.small(self.is_small)

    def _big_repl(self, word):
        """Handle big."""
        if word.strip() == '~+' and self.is_big:
            return self.formatter.text(word)
        if word.strip() == '+~' and not self.is_big:
            return self.formatter.text(word)
        self.is_big = not self.is_big
        return self.formatter.big(self.is_big)

    def _bold_repl(self, word):
        """Handle bold."""
        self.is_b = not self.is_b
        return self.formatter.strong(self.is_b)

    def _emph_repl(self, word):
        """Handle emphasis."""
#        self.is_em = not self.is_em
#        return self.formatter.emphasis(self.is_em)
        return word[0] + \
            self.formatter.emphasis(1) + \
            self.formatter.text(word[2:-1]) + \
            self.formatter.emphasis(0)


    def _sup_repl(self, word):
        """Handle superscript."""
        return self.formatter.sup(1) + \
            self.formatter.text(word[1:-1]) + \
            self.formatter.sup(0)

    def _sub_repl(self, word):
        """Handle subscript."""
        return self.formatter.sub(1) + \
            self.formatter.text(word[2:-2]) + \
            self.formatter.sub(0)

    def _rule_repl(self, word):
        """Handle sequences of dashes."""
        result = self._undent() + self._closeP()
        if len(word) <= 4:
            result = result + self.formatter.rule()
        else:
            # Create variable rule size 1 - 6. Actual size defined in css.
            size = min(len(word), 10) - 4
            result = result + self.formatter.rule(size)
        return result

    def _word_repl(self, word, text=None, css=None):
        """Handle WikiNames."""

        if not text:
            # TODO: if a simple, self-referencing link, emit it as plain text
            #if word == self.formatter.page.page_name:
            #    return self.formatter.text(word)
            text = word

        # handle anchors
        parts = word.split("#", 1)
        anchor = ""
        if len(parts)==2:
            word, anchor = parts
            
        schemes = []
        if css:
            schemes.append(css)
        # mark a link to current page with special "current" class
        if word == self.formatter.pagename:
            schemes.append('current')
        if schemes:
            css = ' '.join(schemes)
            
        result = []
        # Ben modif
        if not os.path.exists(wikiutil.pageName2inputFile(word)):
            if(word != text):
                result.append('['+word+' '+text+']')
            else:
                result.append('['+word+']')
        else:
        # fin Ben modif
            result.append(self.formatter.pagelink(1, word, anchor=anchor, css=css))
            result.append(self.formatter.text(text))
            result.append(self.formatter.pagelink(0, word))
        return ''.join(result)

    def _notword_repl(self, word):
        """Handle !NotWikiNames."""
        text = self.formatter.nowikiword(word[1:])
        if self.in_li or self.in_dd:
            return '<span>' + text + '</span>'
        return text

    def _url_repl(self, word):
        """Handle literal URLs including inline images."""
        scheme = word.split(":", 1)[0]

        attrs = {}

        # Handle literal URLs to local resources using a special 'file' scheme. This allows
        # to insert local images without using brackets and making them a link to themselfes
        # as a side effect.
        if scheme == 'file':
            # file:///image.gif -> image.gif
            word = word[7:].split('/', 1)[-1]
            text = wikiutil.url_unquote(os.path.basename(word))
        else:
            text = word
            if config.general.targetblank:
                attrs = dict(target='_blank')

        # CSS class split
        schsep = word.split('|')
        word = schsep[0]
        if len(schsep) > 1:
            scheme = '%s %s' % (scheme, ' '.join(schsep[1:]))

        if wikiutil.isPicture(word):
            # Get image name http://here.com/dir/image.gif -> image
            name = wikiutil.url_unquote(os.path.splitext(os.path.basename(word))[0])
            return self.formatter.image(src=word, alt=name, css=scheme)
        else:
            return (self.formatter.url(1, word, css=scheme, **attrs) +
                    self.formatter.text(text) +
                    self.formatter.url(0))

    def _bracket_repl(self, word):
        """Handle bracketed URLs, WikiNames, etc."""

        # CSS class split
        schsep = word[1:-1].split('|')
        scheme = None
        if len(schsep) > 1:
            scheme = ' '.join(schsep[1:])

        # Traditional split on whitespace
        words = schsep[0].strip().split(None, 1)
        lenwords = len(words)
        if lenwords == 1:
            words = words * 2

        if words[0][0] == '#':
            # anchor link
            return (self.formatter.url(1, words[0]) +
                    self.formatter.text(words[1]) +
                    self.formatter.url(0))

        # Handle file:// URLs as local file names.
        href = []
        text = []
        for w in words:
            if w.startswith('file://'):
                href.append(w[7:].split('/', 1)[-1])
                text.append(os.path.basename(w))
            else:
                href.append(w)
                text.append(w)

        if re.match(self.url_rule, href[0]) and config.general.targetblank:
            attrs = dict(target='_blank')
        else:
            attrs = dict()

        if re.match(self.url_rule, words[1]) and wikiutil.isPicture(words[1]) and lenwords > 1:
            if re.match(self.url_rule, words[0]):
                return (self.formatter.url(1, href[0], do_escape=0, **attrs) +
                        self.formatter.image(title=text[0], alt=text[0], src=href[1], css=scheme) +
                        self.formatter.url(0))
            else:
                # This is similar to _word_repl() but creates an image link.
                parts = words[0].split('#', 1)
                anchor = ''
                if len(parts) == 2:
                    words[0], anchor = parts
                return (self.formatter.pagelink(1, words[0], anchor=anchor) +
                        self.formatter.image(title=words[0], alt=words[0], src=href[1], css=scheme) +
                        self.formatter.pagelink(0, words[0]))
        # Ben modif
        # elif re.match(self.url_rule, words[0]):
        elif re.match(self.url_rule, words[0]) and not re.match(self.pair_rule, words[0]):
            urlscheme = words[0].split(':', 1)[0]
            if scheme:
                scheme = '%s %s' % (urlscheme, scheme)
            else:
                scheme = urlscheme
            return (self.formatter.url(1, href[0], do_escape=0, css=scheme, **attrs) +
                    self.formatter.text(text[1]) +
                    self.formatter.url(0))
        else:
            return self._word_repl(words[0], text=text[1], css=scheme)

    def _email_repl(self, word):
        """Handle email addresses (without a leading mailto:)."""
        return (self.formatter.url(1, "mailto:" + word, css='mailto') +
                self.formatter.text(word) +
                self.formatter.url(0))


    def _ent_repl(self, word):
        """Handle SGML entities."""
        return self.formatter.text(word)
        #return {'&': '&amp;',
        #        '<': '&lt;',
        #        '>': '&gt;'}[word]
    
    def _pair_repl(self,word):
        """Handle GAML pairs."""
        return self.formatter.rawHTML(word)      

    def _ent_numeric_repl(self, word):
        """Handle numeric (decimal and hexadecimal) SGML entities."""
        return self.formatter.rawHTML(word)

    def _ent_symbolic_repl(self, word):
        """Handle symbolic SGML entities."""
        return self.formatter.rawHTML(word)
    
    def _indent_repl(self, match):
        """Handle pure indentation (no - * 1. markup)."""
        # Removed. Handled by 'blockquote' now.
        return ''

    def _li_none_repl(self, match):
        """Handle type=none (" .") lists."""
        result = []
        self._close_item(result)
        self.in_li = 1
        css_class = None
        if self.line_was_empty and not self.first_list_item:
            css_class = 'gap'
        result.append(self.formatter.listitem(1, css_class=css_class, style="list-style-type:none"))
        return ''.join(result)

    def _li_repl(self, match):
        """Handle bullet (" *") lists."""
        result = []
        self._close_item(result)
        self.in_li = 1
        css_class = None
        if self.line_was_empty and not self.first_list_item:
            css_class = 'gap'
        result.append(self.formatter.listitem(1, css_class=css_class))
        return ''.join(result)

    def _ol_repl(self, match):
        """Handle numbered lists."""
        return self._li_repl(match)

    def _dl_repl(self, match):
        """Handle definition lists."""
        result = []
        self._close_item(result)
        self.in_dd = 1
        result.extend([
            self.formatter.definition_term(1),
            self.formatter.text(match[1:-3].lstrip(' ')),
            self.formatter.definition_term(0),
            self.formatter.definition_desc(1),
        ])
        return ''.join(result)

    def _indent_level(self):
        """Return current char-wise indent level."""
        return len(self.list_indents) and self.list_indents[-1]

    def _indent_to(self, new_level, list_type, numtype, numstart):
        """Close and open lists."""
        open = []   # don't make one out of these two statements!
        close = []

        if self._indent_level() != new_level and self.in_table:
            close.append(self.formatter.table(0))
            self.in_table = 0
        
        while self._indent_level() > new_level:
            self._close_item(close)
            if self.list_types[-1] == 'ol':
                tag = self.formatter.number_list(0)
            elif self.list_types[-1] == 'dl':
                tag = self.formatter.definition_list(0)
            elif self.list_types[-1] == 'ul':
                tag = self.formatter.bullet_list(0)
            else:
                # Ben modif
                # tag = '</blockquote>'
                tag = ''
            close.append(tag)

            del self.list_indents[-1]
            del self.list_types[-1]
            
            if self.list_types: # we are still in a list
                if self.list_types[-1] == 'dl':
                    self.in_dd = 1
                else:
                    self.in_li = 1
                
        # Open new list, if necessary
        if self._indent_level() < new_level:
            self.list_indents.append(new_level)
            self.list_types.append(list_type)

            if self.formatter.in_p:
                close.append(self.formatter.paragraph(0))
            
            if list_type == 'ol':
                tag = self.formatter.number_list(1, numtype, numstart)
            elif list_type == 'dl':
                tag = self.formatter.definition_list(1)
            elif list_type == 'ul':
                tag = self.formatter.bullet_list(1)
            else:
                # Ben modif
                # tag = '<blockquote>'
                tag = ''
            open.append(tag)
            
            self.first_list_item = 1
            self.in_li = 0
            self.in_dd = 0
            
        # If list level changes, close an open table
        if self.in_table and (open or close):
            close[0:0] = [self.formatter.table(0)]
            self.in_table = 0
        
        self.in_list = self.list_types != []
        return ''.join(close) + ''.join(open)

    def _undent(self):
        """Close all open lists."""
        result = []
        #result.append("<!-- _undent start -->\n")
        self._close_item(result)
        for type in self.list_types[::-1]:
            if type == 'ol':
                result.append(self.formatter.number_list(0))
            elif type == 'dl':
                result.append(self.formatter.definition_list(0))
            elif type == 'ul':
                result.append(self.formatter.bullet_list(0))
            else:
                # Ben modif
                # result.append('</blockquote>')
                result.append('')
        #result.append("<!-- _undent end -->\n")
        self.list_indents = []
        self.list_types = []
        return ''.join(result)

    def _tt_repl(self, word):
        """Handle inline code."""
        return self.formatter.code(1) + \
            self.formatter.text(word[3:-3]) + \
            self.formatter.code(0)

    def _tt_bt_repl(self, word):
        """Handle backticked inline code."""
        # if len(word) == 2: return "" // removed for FCK editor
        # Ben modif
        return self.formatter.code(1, css="backtick") + \
            self.formatter.text(word[1:-1]) + \
            self.formatter.code(0)

    def _getTableAttrs(self, attrdef):
        return {}, ''

    def _tableZ_repl(self, word):
        """Handle table row end."""
        if self.in_table:
            result = ''
            # REMOVED: check for self.in_li, p should always close
            if self.formatter.in_p:
                result = self.formatter.paragraph(0)
            result += self.formatter.table_cell(0) + self.formatter.table_row(0)
            return result
        else:
            return self.formatter.text(word)

    def _table_repl(self, word):
        """Handle table cell separator."""
        if self.in_table:
            result = []
            # check for attributes
            attrs, attrerr = self._getTableAttrs(word)

            # start the table row?
            if self.table_rowstart:
                self.table_rowstart = 0
                self.table_row_count += 1
                if self.table_row_count % 2:
                    css = 'row-a'
                else:
                    css = 'row-b'
                result.append(self.formatter.table_row(1, attrs, **{'class': css}))
            else:
                # Close table cell, first closing open p
                # REMOVED: check for self.in_li, paragraph should close always!
                if self.formatter.in_p:
                    result.append(self.formatter.paragraph(0))
                result.append(self.formatter.table_cell(0))

            # check for adjacent cell markers
            if word.count("|") > 2:
                if not attrs.has_key('align') and \
                   not (attrs.has_key('style') and 'text-align' in attrs['style'].lower()):
                    # add center alignment if we don't have some alignment already
                    attrs['align'] = '"center"'
                if not attrs.has_key('colspan'):
                    attrs['colspan'] = '"%d"' % (word.count("|")/2)

            # return the complete cell markup
            result.append(self.formatter.table_cell(1, attrs) + attrerr)         
            return ''.join(result) 
        else:
            return self.formatter.text(word)

    def _heading_repl(self, word):
        """Handle section headings."""
        h = word.strip()
        level = 1
        while h[level:level+1] == '=':
            level += 1
        depth = min(6,level)

        # this is needed for Included pages
        # TODO but it might still result in unpredictable results
        # when included the same page multiple times
        title_text = h[level:-level].strip()
        #pntt = self.formatter.page.page_name + title_text
        pntt = title_text

        # --- XXX: we don't need the id's in statwiki
        result = [self._closeP()]
        result.append(self.formatter.heading(1, depth))
        result.append(self.formatter.text(title_text))
        result.append(self.formatter.heading(0, depth))
        return ''.join(result)
        # ---

        self.titles.setdefault(pntt, 0)
        self.titles[pntt] += 1

        try:
            # Py2.5+
            from hashlib import sha1
        except ImportError:
            # older versions
            from sha import new as sha1

        unique_id = ''
        if self.titles[pntt] > 1:
            unique_id = '-%d' % self.titles[pntt]
        result = self._closeP()
        result += self.formatter.heading(1, depth, id="head-"+sha1(pntt.encode(config.charset)).hexdigest()+unique_id)
                                     
        return (result + self.formatter.text(title_text) +
                self.formatter.heading(0, depth))
    
    def _processor_repl(self, word):
        """Handle processed code displays."""
        if word[:3] == '{{{':
            word = word[3:]

        self.processor = None
        self.processor_name = None
        self.processor_is_parser = 0
        s_word = word.strip()
        if s_word == '#!':
            # empty bang paths lead to a normal code display
            # can be used to escape real, non-empty bang paths
            word = ''
            self.in_pre = 3
            return self._closeP() + self.formatter.preformatted(1)
        elif s_word[:2] == '#!':
            # First try to find a processor for this (will go away in 2.0)
            processor_name = s_word[2:].split()[0]
            self.setProcessor(processor_name)

        if self.processor:
            self.processor_name = processor_name
            self.in_pre = 2
            self.colorize_lines = [word]
            return ''
        elif s_word:
            self.in_pre = 3
            return self._closeP() + self.formatter.preformatted(1) + \
                   self.formatter.text(s_word + ' (-)')
        else:
            self.in_pre = 1
            return ''

    def _pre_repl(self, word):
        """Handle code displays."""
        word = word.strip()
        if word == '{{{' and not self.in_pre:
            self.in_pre = 3
            return self._closeP() + self.formatter.preformatted(self.in_pre)
        elif word == '}}}' and self.in_pre:
            self.in_pre = 0
            self.inhibit_p = 0
            return self.formatter.preformatted(self.in_pre)
        return self.formatter.text(word)

    def _anchor_repl(self, word):
        """Handle anchors definitions."""
        self.line_is_empty = 1 # markup following anchor lines treats them as if they were empty
        return (self.formatter.url(1, None, name=word[1:].rstrip()) +
                self.formatter.url(0, None))
        
    def _comment_repl(self, word):
        # if we are in a paragraph, we must close it so that normal text following
        # in the line below the comment will reopen a new paragraph.
        result = []
        if self.formatter.in_p:
            result.append(self.formatter.paragraph(0))
        self.line_is_empty = 1 # markup following comment lines treats them as if they were empty
        result.append(self.formatter.comment(word))
        return ''.join(result)

    def _closeP(self):
        if self.formatter.in_p:
            return self.formatter.paragraph(0)
        return ''
        
    def _macro_repl(self, word):
        """Handle macros ([[macroname]])."""
        macro_name = word[2:-2]
        self.inhibit_p = 0 # 1 fixes UserPreferences, 0 fixes paragraph formatting for macros

        # check for arguments
        args = None
        if macro_name.count("("):
            macro_name, args = macro_name.split('(', 1)
            args = args[:-1]

        # create macro instance
        if self.macro is None:
            assert 0
            self.macro = wikimacro.Macro(self)
        return self.formatter.macro(self.macro, macro_name, args)

    def scan(self, scan_re, line):
        """ Scans one line
        
        Append text before match, invoke replace() with match, and add text after match.
        """
        result = []
        lastpos = 0

        ###result.append(u'<span class="info">[scan: <tt>"%s"</tt>]</span>' % line)
      
        for match in scan_re.finditer(line):
            # Add text before the match
            if lastpos < match.start():
                
                ###result.append(u'<span class="info">[add text before match: <tt>"%s"</tt>]</span>' % line[lastpos:match.start()])
                
                if not (self.inhibit_p or self.in_pre or self.formatter.in_p or self.in_li or self.in_dd):
                    result.append(self.formatter.paragraph(1))
                text = self.formatter.text(line[lastpos:match.start()])
                if text.strip() and (self.in_li or self.in_dd):
                    text = '<span>' + text + '</span>'
                result.append(text)
            
            # Replace match with markup
            # XXX: Is this needed? replace() inserts a paragraph too!
            #if not (self.inhibit_p or self.in_pre or self.formatter.in_p or
            #        self.in_table or self.in_list):
            #    result.append(self.formatter.paragraph(1))
            result.append(self.replace(match))
            lastpos = match.end()
        
        ###result.append('<span class="info">[no match, add rest: <tt>"%s"<tt>]</span>' % line[lastpos:])
        
        # Add paragraph with the remainder of the line
        if not (self.in_pre or self.in_li or self.in_dd or self.inhibit_p or
                self.formatter.in_p) and lastpos < len(line):
            result.append(self.formatter.paragraph(1))
        text = self.formatter.text(line[lastpos:])
        if text.strip() and (self.in_li or self.in_dd):
            text = '<span>' + text + '</span>'
        result.append(text)
        return u''.join(result)

    def replace(self, match):
        """ Replace match using type name """
        result = []
        for type, hit in match.groupdict().items():
            if hit is not None and type != "hmarker":

                ###result.append(u'<span class="info">[replace: %s: "%s"]</span>' % (type, hit))
                if self.in_pre and type not in ['pre', 'ent']:
                    return self.formatter.text(hit) 
                else:
                    # Open p for certain types
                    if not (self.inhibit_p or self.formatter.in_p or self.in_li or self.in_dd
                            or self.in_pre or (type in self.no_new_p_before)):
                        result.append(self.formatter.paragraph(1))
                    
                    # Get replace method and replece hit
                    replace = getattr(self, '_' + type + '_repl')
                    result.append(replace(hit))
                    return ''.join(result)
            else:
                continue
                # We should never get here
                import pprint
                raise Exception("Can't handle match " + `match`
                    + "\n" + pprint.pformat(match.groupdict())
                    + "\n" + pprint.pformat(match.groups()) )

        return ""

    def format(self, formatter):
        """ For each line, scan through looking for magic
            strings, outputting verbatim any intervening text.
        """
        self.formatter = formatter

        # prepare regex patterns
        rules = self.formatting_rules.replace('\n', '|')
        if int(config.general.camelcase):
            rules = ur'(?P<notword>!%(word_rule)s)|%(rules)s' % {
                'word_rule': self.word_rule,
                'rules': rules}
        else:
            rules = rules.replace('(?P<word>%s)|' % self.word_rule, '')
        scan_re = re.compile(rules, re.UNICODE)
        number_re = re.compile(self.ol_rule, re.UNICODE)
        # Ben modif 
        # term_re = re.compile(self.dl_rule, re.UNICODE)
        indent_re = re.compile("^\s*", re.UNICODE)
        eol_re = re.compile(r'\r?\n', re.UNICODE)

        # get text and replace TABs
        rawtext = self.raw.expandtabs()

        # go through the lines
        self.lineno = 0
        self.lines = eol_re.split(rawtext)
        self.line_is_empty = 0

        self.in_processing_instructions = 1

        # Main loop
        for line in self.lines:
            self.lineno += 1
            self.table_rowstart = 1
            self.line_was_empty = self.line_is_empty
            self.line_is_empty = 0
            self.first_list_item = 0
            self.inhibit_p = 0

            # ignore processing instructions
            if self.in_processing_instructions:
                for pi in self.processing_instr:
                    if line.lower().startswith(pi):
                        self.request.write(self.formatter.comment(line))
                        break
                else:
                    # first unknown instr. ends the processing instr. block
                    self.in_processing_instructions = 0
                if self.in_processing_instructions:
                    continue # do not parse this line
            if self.in_pre:
                # TODO: move this into function
                # still looking for processing instructions
                # TODO: use strings for pre state, not numbers
                if self.in_pre == 1:
                    self.processor = None
                    self.processor_is_parser = 0
                    processor_name = ''
                    if (line.strip()[:2] == "#!"):
                        processor_name = line.strip()[2:].split()[0]
                        self.setProcessor(processor_name)

                    if self.processor:
                        self.in_pre = 2
                        self.colorize_lines = [line]
                        self.processor_name = processor_name
                        continue
                    else:
                        self.request.write(self._closeP() +
                                           self.formatter.preformatted(1))
                        self.in_pre = 3
                if self.in_pre == 2:
                    # processing mode
                    endpos = line.find("}}}")
                    if endpos == -1:
                        self.colorize_lines.append(line)
                        continue
                    if line[:endpos]:
                        self.colorize_lines.append(line[:endpos])
                    
                    # Close p before calling processor
                    # TODO: do we really need this?
                    self.request.write(self._closeP())
                    res = self.formatter.processor(self.processor_name,
                                                   self.colorize_lines, 
                                                   self.processor_is_parser)
                    self.request.write(res)
                    del self.colorize_lines
                    self.in_pre = 0
                    self.processor = None

                    # send rest of line through regex machinery
                    line = line[endpos+3:]
                    if not line.strip(): # just in the case "}}} " when we only have blanks left...
                        continue
            else:
                # we don't have \n as whitespace any more
                # This is the space between lines we join to one paragraph
                line += ' '
                
                # Paragraph break on empty lines
                if not line.strip():
                    if self.in_table:
                        self.request.write(self.formatter.table(0))
                        self.in_table = 0
                    # CHANGE: removed check for not self.list_types
                    # p should close on every empty line
                    if self.formatter.in_p:
                        self.request.write(self.formatter.paragraph(0))
                    self.line_is_empty = 1
                    continue

                # Check indent level
                indent = indent_re.match(line)
                indlen = len(indent.group(0))
                indtype = "ul"
                numtype = None
                numstart = None
                if indlen:
                    match = number_re.match(line)
                    if match:
                        numstart = None
                        indtype = "ol"
                # Ben modif
                #    else:
                #        match = term_re.match(line)
                #        if match:
                #            indtype = "dl"
                #        elif not (line.lstrip().startswith('* ') or line.lstrip().startswith('. ')):
                #            indtype = 'blockquote'
                    elif not (line.lstrip().startswith('* ') or line.lstrip().startswith('. ')):
                        indtype = 'blockquote'

                # output proper indentation tags
                self.request.write(self._indent_to(indlen, indtype, numtype, numstart))

                # Table mode
                # TODO: move into function?                
                if (not self.in_table and line[indlen:indlen + 2] == "||"
                    and line[-3:] == "|| " and len(line) >= 5 + indlen):
                    # Start table
                    if self.list_types and not self.in_li:
                        self.request.write(self.formatter.listitem(1, style="list-style-type:none"))
                        ## CHANGE: no automatic p on li
                        ##self.request.write(self.formatter.paragraph(1))
                        self.in_li = 1
                        
                    # CHANGE: removed check for self.in_li
                    # paragraph should end before table, always!
                    if self.formatter.in_p:
                        self.request.write(self.formatter.paragraph(0))
                    attrs, attrerr = self._getTableAttrs(line[indlen+2:])
                    self.request.write(self.formatter.table(1, attrs) + attrerr)
                    self.in_table = True # self.lineno
                    self.table_row_count = 0
                elif (self.in_table and not
                      # intra-table comments should not break a table
                      (line[:2] == "##" or  
                       line[indlen:indlen + 2] == "||" and
                       line[-3:] == "|| " and
                       len(line) >= 5 + indlen)):
                    
                    # Close table
                    self.request.write(self.formatter.table(0))
                    self.in_table = 0
                                            
            # Scan line, format and write
            formatted_line = self.scan(scan_re, line)
            self.request.write(formatted_line)

            if self.in_pre == 3:
                self.request.write(self.formatter.linebreak())

        # Close code displays, paragraphs, tables and open lists
        self.request.write(self._undent())
        if self.in_pre: self.request.write(self.formatter.preformatted(0))
        if self.formatter.in_p: self.request.write(self.formatter.paragraph(0))
        if self.in_table: self.request.write(self.formatter.table(0))

    # --------------------------------------------------------------------
    # Private helpers
    
    def setProcessor(self, name):
        """ Set processer to either processor or parser named 'name' """
        pass


class PragmaParser(object):
    def __init__(self, raw):
        self.instr = []
        for line in re.compile(r'\r?\n', re.UNICODE).split(raw.expandtabs()):
            for pname in Parser.processing_instr:
                if len(line.strip()) and len(line) and line.split()[0].lower() == pname:
                    args = line[len(pname):].strip()
                    self.instr.append((pname, args))
                    break
            else:
                # first unknown pragma ends the block
                break
        
    def all(self):
        return self.instr
        
    def multiple(self, name):
        args = []
        for pname, pargs in self.instr:
            if name == pname:
                args.append(pargs)
        return args

    def single(self, name, default=None):
        args = self.multiple(name)
        if len(args) > 1:
            raise AssertionError('expected at most one %s pragma, got %d' % (repr(name), len(args)))
        if args:
            return args[0]
        if default is not None:
            return default
        raise ValueError('required %s pragma not found' % repr(name))


# vim:set sw=4 et:
