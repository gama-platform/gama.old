#
# This is a statwiki HTML generator. It is run by statwiki for every wiki file it processes.
#
# These globals are always in a generator before it is started by statwiki:
# - out: file object the generator should write the HTML output to, the file accepts unicode
# - pagename: wiki pagename of currently generated page
# - summary: #summary pragma value for the current page, empty if not specified
# - genconfig: config.Section object for the [generator] section of the wiki config file
# - content: statwiki.Context object which holds the current page's content
# - modifiy_time: current page's *.wiki modification time, as string
# - generate_time: current time, as string
#
# This generator supports per-page %-formatting in the wikitext. Use the following pragma
# to enable it:
#   #execute enable_content_formatting = True
#
# Note that you will have to escape %-signs with %% if formatting is enabled.
#
# The following settings are available in the [generator] section of your wiki's config file:
# - name: name of the wiki, displayed in browser window's titlebar
# - gray_title: left (gray) part of the title displayed at the top of the pages
# - orange_title: right (orange) part of the title
# - title_sep: number of spaces between gray and orange titles
# - slogan: slogan displayed under the title at the top od the pages
# - year: year part of the copyright string displayed at the bottom of the pages
# - by: name part of the copyright string
# - cse_id: if defined, enables the Google Custom Search bar; set to your custom search ID
# - menu: defines the menu bar under the titlebar; should be a series of wiki [] links,
#   please use text links only
#

# We use this helper function to format the menu.
from statwiki import format_text

# Configuration. Set the values in your wiki's config file's [generator] section.
_name = genconfig.get('name', u'Wiki Name')
_title1 = genconfig.get('gray_title', u'Stat')
_title2 = genconfig.get('orange_title', u'Wiki')
_titlesep = u'&nbsp;' * int(genconfig.get('title_sep', '0'))
_slogan = genconfig.get('slogan', u'a static wiki')
_menu = genconfig.get('menu', u'')
_year = genconfig.get('year', u'2014')
_by = genconfig.get('by', u'The GAMA Team')
_url = genconfig.get('statwiki_url', u'http://code.google.com/p/statwiki/')
_cseid = genconfig.get('cse_id', u'')

# Header.
out.write(u'''<html>
  <head>
    <meta http-equiv="content-type" content="text/html; CHARSET=utf-8"/>
    <title>%(pagename)s - %(_name)s</title>
    <link rel="stylesheet" type="text/css" href="template/default.css"/>
  </head>
  <body>''' % globals())
if _cseid:
    out.write(u'''        <form action="http://www.google.com/cse" class="searchform" id="cse-search-box">
         <p>
          <input type="hidden" name="cx" value="%(_cseid)s">
          <input type="hidden" name="ie" value="UTF-8">
          <input type="text" name="q" size="31" class="textbox">
          <input type="submit" name="sa" value="Search" class="button">
         </p>
        </form>
        <script type="text/javascript" src="http://www.google.com/coop/cse/brand?form=cse-search-box&lang=en"></script>
    ''' % globals())
#out.write(u'''      </div>
#      <div id="menu">''')

# Create the menu bar.
if _menu:
    out.write(format_text(_menu.replace(u'[', u'\n * ['), pagename))

#out.write(u'''      </div>
#      <div id="content-wrap">''')

# Allow %(...)s formatting in the content if enabled per-page.
if globals().get('enable_content_formatting', False):
    content.wikitext %= globals()

# Insert the content.
if 'sidebar' in globals():
    # With sidebar.
    out.write(u'''<div id="sidebar">%(sidebar)s</div>
        <div id="mainbar">%(content)s</div>''' % globals())
else:
    # Without sidebar.
    out.write(u'<div id="main">%(content)s</div>' % globals())

# Footer.
modify_time = modify_time.replace(u' ', u'&nbsp;')
_by = _by.replace(u' ', u'&nbsp;')
out.write(u'''
  </body>
</html>''' % globals())
