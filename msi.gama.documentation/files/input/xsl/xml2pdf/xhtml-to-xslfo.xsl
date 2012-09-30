<?xml version="1.0"?>
<xsl:stylesheet 
  version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:fox="http://xml.apache.org/fop/extensions">

  <!-- ============================================
    This stylesheet transforms most of the common
    HTML elements into XSL formatting objects.  

    This version written 1 November 2002 by
    Doug Tidwell, dtidwell@us.ibm.com.

    Brought to you by your friends at developerWorks:
    ibm.com/developerWorks.
    =============================================== -->

  <!-- ============================================
    This variable sets the size of the page.  If 
    it's 'ltr', we an 8.5 x 11 inch page; otherwise,
    we use an A4-sized page.  'ltr' is the default.
    To change the value, you can make the following
    line read select="'A4'", or you can check the
    documentation for your XSLT processor to see 
    how to set variables externally to the style
    sheet.  (For Xalan, add "-PARAM page-size a4"
    to the command.)
    =============================================== -->

  <xsl:variable name="page-size" select="'ltr'"/>

  <xsl:template match="html">

  <!-- ============================================
    Because character entities aren't built into 
    the XSL-FO vocabulary, they're included here.  
    =============================================== -->

    <xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE fo:root [
  &lt;!ENTITY tilde  "&amp;#126;"&gt;
  &lt;!ENTITY florin "&amp;#131;"&gt;
  &lt;!ENTITY elip   "&amp;#133;"&gt;
  &lt;!ENTITY dag    "&amp;#134;"&gt;
  &lt;!ENTITY ddag   "&amp;#135;"&gt;
  &lt;!ENTITY cflex  "&amp;#136;"&gt;
  &lt;!ENTITY permil "&amp;#137;"&gt;
  &lt;!ENTITY uscore "&amp;#138;"&gt;
  &lt;!ENTITY OElig  "&amp;#140;"&gt;
  &lt;!ENTITY lsquo  "&amp;#145;"&gt;
  &lt;!ENTITY rsquo  "&amp;#146;"&gt;
  &lt;!ENTITY ldquo  "&amp;#147;"&gt;
  &lt;!ENTITY rdquo  "&amp;#148;"&gt;
  &lt;!ENTITY bullet "&amp;#149;"&gt;
  &lt;!ENTITY endash "&amp;#150;"&gt;
  &lt;!ENTITY emdash "&amp;#151;"&gt;
  &lt;!ENTITY trade  "&amp;#153;"&gt;
  &lt;!ENTITY oelig  "&amp;#156;"&gt;
  &lt;!ENTITY Yuml   "&amp;#159;"&gt;

  &lt;!ENTITY nbsp   "&amp;#160;"&gt;
  &lt;!ENTITY iexcl  "&amp;#161;"&gt;
  &lt;!ENTITY cent   "&amp;#162;"&gt;
  &lt;!ENTITY pound  "&amp;#163;"&gt;
  &lt;!ENTITY curren "&amp;#164;"&gt;
  &lt;!ENTITY yen    "&amp;#165;"&gt;
  &lt;!ENTITY brvbar "&amp;#166;"&gt;
  &lt;!ENTITY sect   "&amp;#167;"&gt;
  &lt;!ENTITY uml    "&amp;#168;"&gt;
  &lt;!ENTITY copy   "&amp;#169;"&gt;
  &lt;!ENTITY ordf   "&amp;#170;"&gt;
  &lt;!ENTITY laquo  "&amp;#171;"&gt;
  &lt;!ENTITY not    "&amp;#172;"&gt;
  &lt;!ENTITY shy    "&amp;#173;"&gt;
  &lt;!ENTITY reg    "&amp;#174;"&gt;
  &lt;!ENTITY macr   "&amp;#175;"&gt;
  &lt;!ENTITY deg    "&amp;#176;"&gt;
  &lt;!ENTITY plusmn "&amp;#177;"&gt;
  &lt;!ENTITY sup2   "&amp;#178;"&gt;
  &lt;!ENTITY sup3   "&amp;#179;"&gt;
  &lt;!ENTITY acute  "&amp;#180;"&gt;
  &lt;!ENTITY micro  "&amp;#181;"&gt;
  &lt;!ENTITY para   "&amp;#182;"&gt;
  &lt;!ENTITY middot "&amp;#183;"&gt;
  &lt;!ENTITY cedil  "&amp;#184;"&gt;
  &lt;!ENTITY sup1   "&amp;#185;"&gt;
  &lt;!ENTITY ordm   "&amp;#186;"&gt;
  &lt;!ENTITY raquo  "&amp;#187;"&gt;
  &lt;!ENTITY frac14 "&amp;#188;"&gt;
  &lt;!ENTITY frac12 "&amp;#189;"&gt;
  &lt;!ENTITY frac34 "&amp;#190;"&gt;
  &lt;!ENTITY iquest "&amp;#191;"&gt;
  &lt;!ENTITY Agrave "&amp;#192;"&gt;
  &lt;!ENTITY Aacute "&amp;#193;"&gt;
  &lt;!ENTITY Acirc  "&amp;#194;"&gt;
  &lt;!ENTITY Atilde "&amp;#195;"&gt;
  &lt;!ENTITY Auml   "&amp;#196;"&gt;
  &lt;!ENTITY Aring  "&amp;#197;"&gt;
  &lt;!ENTITY AElig  "&amp;#198;"&gt;
  &lt;!ENTITY Ccedil "&amp;#199;"&gt;
  &lt;!ENTITY Egrave "&amp;#200;"&gt;
  &lt;!ENTITY Eacute "&amp;#201;"&gt;
  &lt;!ENTITY Ecirc  "&amp;#202;"&gt;
  &lt;!ENTITY Euml   "&amp;#203;"&gt;
  &lt;!ENTITY Igrave "&amp;#204;"&gt;
  &lt;!ENTITY Iacute "&amp;#205;"&gt;
  &lt;!ENTITY Icirc  "&amp;#206;"&gt;
  &lt;!ENTITY Iuml   "&amp;#207;"&gt;
  &lt;!ENTITY ETH    "&amp;#208;"&gt;
  &lt;!ENTITY Ntilde "&amp;#209;"&gt;
  &lt;!ENTITY Ograve "&amp;#210;"&gt;
  &lt;!ENTITY Oacute "&amp;#211;"&gt;
  &lt;!ENTITY Ocirc  "&amp;#212;"&gt;
  &lt;!ENTITY Otilde "&amp;#213;"&gt;
  &lt;!ENTITY Ouml   "&amp;#214;"&gt;
  &lt;!ENTITY times  "&amp;#215;"&gt;
  &lt;!ENTITY Oslash "&amp;#216;"&gt;
  &lt;!ENTITY Ugrave "&amp;#217;"&gt;
  &lt;!ENTITY Uacute "&amp;#218;"&gt;
  &lt;!ENTITY Ucirc  "&amp;#219;"&gt;
  &lt;!ENTITY Uuml   "&amp;#220;"&gt;
  &lt;!ENTITY Yacute "&amp;#221;"&gt;
  &lt;!ENTITY THORN  "&amp;#222;"&gt;
  &lt;!ENTITY szlig  "&amp;#223;"&gt;
  &lt;!ENTITY agrave "&amp;#224;"&gt;
  &lt;!ENTITY aacute "&amp;#225;"&gt;
  &lt;!ENTITY acirc  "&amp;#226;"&gt;
  &lt;!ENTITY atilde "&amp;#227;"&gt;
  &lt;!ENTITY auml   "&amp;#228;"&gt;
  &lt;!ENTITY aring  "&amp;#229;"&gt;
  &lt;!ENTITY aelig  "&amp;#230;"&gt;
  &lt;!ENTITY ccedil "&amp;#231;"&gt;
  &lt;!ENTITY egrave "&amp;#232;"&gt;
  &lt;!ENTITY eacute "&amp;#233;"&gt;
  &lt;!ENTITY ecirc  "&amp;#234;"&gt;
  &lt;!ENTITY euml   "&amp;#235;"&gt;
  &lt;!ENTITY igrave "&amp;#236;"&gt;
  &lt;!ENTITY iacute "&amp;#237;"&gt;
  &lt;!ENTITY icirc  "&amp;#238;"&gt;
  &lt;!ENTITY iuml   "&amp;#239;"&gt;
  &lt;!ENTITY eth    "&amp;#240;"&gt;
  &lt;!ENTITY ntilde "&amp;#241;"&gt;
  &lt;!ENTITY ograve "&amp;#242;"&gt;
  &lt;!ENTITY oacute "&amp;#243;"&gt;
  &lt;!ENTITY ocirc  "&amp;#244;"&gt;
  &lt;!ENTITY otilde "&amp;#245;"&gt;
  &lt;!ENTITY ouml   "&amp;#246;"&gt;
  &lt;!ENTITY oslash "&amp;#248;"&gt;
  &lt;!ENTITY ugrave "&amp;#249;"&gt;
  &lt;!ENTITY uacute "&amp;#250;"&gt;
  &lt;!ENTITY ucirc  "&amp;#251;"&gt;
  &lt;!ENTITY uuml   "&amp;#252;"&gt;
  &lt;!ENTITY yacute "&amp;#253;"&gt;
  &lt;!ENTITY thorn  "&amp;#254;"&gt;
  &lt;!ENTITY yuml   "&amp;#255;"&gt;
]&gt;
    </xsl:text>

  <!-- ============================================
    The XSL-FO section starts here....
    =============================================== -->

    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
      xmlns:fox="http://xml.apache.org/fop/extensions"> 

  <!-- ============================================
    Define the page layouts.  There are two sets of
    three page mastters here; we use one set for 
    letter-sized paper, the other set for A4.
    =============================================== -->

      <fo:layout-master-set>
        <xsl:choose>
          <xsl:when test="$page-size='ltr'">
            <fo:simple-page-master master-name="first"
              page-height="11in" page-width="8.5in"
              margin-right="72pt" margin-left="72pt"
              margin-bottom="36pt" margin-top="72pt">
              <fo:region-body margin-bottom="50pt"/>
              <fo:region-after region-name="ra-right" 
                extent="25pt"/>
            </fo:simple-page-master>
            
            <fo:simple-page-master master-name="left"
              page-height="11in" page-width="8.5in"
              margin-right="72pt" margin-left="72pt" 
              margin-bottom="36pt" margin-top="36pt">
              <fo:region-before region-name="rb-left" 
                extent="25pt"/>
              <fo:region-body margin-top="50pt" 
                margin-bottom="50pt"/>
              <fo:region-after region-name="ra-left" 
                extent="25pt"/>
            </fo:simple-page-master>
            
            <fo:simple-page-master master-name="right"
              page-height="11in" page-width="8.5in"
              margin-right="72pt" margin-left="72pt" 
              margin-bottom="36pt" margin-top="36pt">
              <fo:region-before region-name="rb-right" 
                extent="25pt"/>
              <fo:region-body margin-top="50pt" 
                margin-bottom="50pt"/>
              <fo:region-after region-name="ra-right" 
                extent="25pt"/>
            </fo:simple-page-master>
          </xsl:when>

  <!-- ============================================
    Page layouts for A4-sized paper
    =============================================== -->

          <xsl:otherwise>
            <fo:simple-page-master master-name="first"
              page-height="29.7cm" page-width="21cm"
              margin-right="72pt" margin-left="72pt"
              margin-bottom="36pt" margin-top="72pt">
              <fo:region-body margin-top="1.5cm" 
                margin-bottom="1.5cm"/>
              <fo:region-after region-name="ra-right" 
                extent="1cm"/>
            </fo:simple-page-master>
            
            <fo:simple-page-master master-name="left"
              page-height="29.7cm" page-width="21cm"
              margin-right="72pt" margin-left="72pt" 
              margin-bottom="36pt" margin-top="36pt">
              <fo:region-before region-name="rb-left" 
                extent="3cm"/>
              <fo:region-body margin-top="1.5cm" 
                margin-bottom="1.5cm"/>
              <fo:region-after region-name="ra-left" 
                extent="1cm"/>
            </fo:simple-page-master>
            
            <fo:simple-page-master master-name="right"
              page-height="29.7cm" page-width="21cm"
              margin-right="72pt" margin-left="72pt" 
              margin-bottom="36pt" margin-top="36pt">
              <fo:region-before region-name="rb-right" 
                extent="3cm"/>
              <fo:region-body margin-top="1.5cm" 
                margin-bottom="1.5cm"/>
              <fo:region-after region-name="ra-right" 
                extent="1cm"/>
            </fo:simple-page-master>
          </xsl:otherwise>
        </xsl:choose>
        
  <!-- ============================================
    Now we define how we use the page layouts.  One
    is for the first page, one is for the even-
    numbered pages, and one is for odd-numbered pages.
    =============================================== -->

        <fo:page-sequence-master master-name="standard">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference 
              master-reference="first" 
              page-position="first"/>
            <fo:conditional-page-master-reference 
              master-reference="left" 
              odd-or-even="even"/>
            <fo:conditional-page-master-reference 
              master-reference="right" 
              odd-or-even="odd"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>
        
      </fo:layout-master-set>

  <!-- ============================================
    Now that we've defined all of the page layouts,
    we generate the bookmarks for the PDF file. 
    =============================================== -->
      
      <xsl:call-template name="generate-bookmarks"/>
        
  <!-- ============================================
    This is where the actual content of the document
    starts. 
    =============================================== -->

      <fo:page-sequence master-reference="standard">

  <!-- ============================================
    We define the static content for the headers 
    and footers of the various page layouts first. 
    =============================================== -->

        <fo:static-content flow-name="rb-right">
          <fo:block font-size="10pt" text-align-last="end">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="396pt"/>
              <fo:table-column column-width="72pt"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block text-align="start">
                      developerWorks loves you!
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block text-align="end" font-weight="bold" 
                      font-family="monospace">
                      ibm.com/developerWorks
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="ra-right">
          <fo:block font-size="10pt" text-align-last="end">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="396pt"/>
              <fo:table-column column-width="72pt"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block text-align="start">
                      <xsl:value-of select="/html/head/title"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block text-align="end">Page 
                      <fo:page-number/> of 
                      <fo:page-number-citation 
                        ref-id="TheVeryLastPage"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="rb-left">
          <fo:block font-size="10pt" text-align-last="end">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="72pt"/>
              <fo:table-column column-width="396pt"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block text-align="start" font-weight="bold" 
                      font-family="monospace">
                      ibm.com/developerWorks
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block text-align="end">
                      developerWorks loves you!
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="ra-left">
          <fo:block font-size="10pt" text-align-last="end">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="72pt"/>
              <fo:table-column column-width="396pt"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block text-align="start">Page 
                      <fo:page-number/> 
                      of <fo:page-number-citation 
                      ref-id="TheVeryLastPage"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block text-align="end">
                      <xsl:value-of select="/html/head/title"/> 
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table> 
          </fo:block>
        </fo:static-content>

          <xsl:apply-templates select="body"/>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <!-- ============================================
    Templates for individual HTML elements begin
    here. 
    =============================================== -->

  <!-- ============================================
    Processing for the anchor tag is complex.  First
    of all, if this is a named anchor, we write an empty
    <fo:block> with the appropriate id.  (In the special
    case that the next element is an <h1>, we ignore
    the element altogether and put the id on the <h1>.)
    Next, if this is a regular anchor and the href
    starts with a hash mark (#), we create a link with
    an internal-destination.  Otherwise, we create a
    link with an external destination. 
    =============================================== -->

  <xsl:template match="a">
    <xsl:choose>
      <xsl:when test="@name">
        <xsl:if test="not(name(following-sibling::*[1]) = 'h1')">
          <fo:block line-height="0pt" space-after="0pt" 
            font-size="0pt" id="{@name}"/>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@href">
        <fo:basic-link color="blue">
          <xsl:choose>
            <xsl:when test="starts-with(@href, '#')">
              <xsl:attribute name="internal-destination">
                <xsl:value-of select="substring(@href, 2)"/>
              </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="external-destination">
                <xsl:value-of select="@href"/>
              </xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:apply-templates select="*|text()"/>
        </fo:basic-link>
        <xsl:if test="starts-with(@href, '#')">
          <xsl:text> on page </xsl:text>
          <fo:page-number-citation ref-id="{substring(@href, 2)}"/>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- ============================================
      We render an address element in italics.
    =============================================== -->

  <xsl:template match="address">
    <fo:block font-style="italic" space-after="12pt">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    For bold elements, we just change the font-weight.
    =============================================== -->

  <xsl:template match="b">
    <fo:inline font-weight="bold">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    The big element is handled with a relative 
    font size.  That means a <big> element inside
    another <big> element will be even bigger, just
    as it is in HTML. 
    =============================================== -->

  <xsl:template match="big">
    <fo:inline font-size="120%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    A blockquote is indented on both sides.
    =============================================== -->

  <xsl:template match="blockquote">
    <fo:block start-indent="1.5cm" end-indent="1.5cm"
      space-after="12pt">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The HTML <body> element contains everything in
    the main part of the document.  This is analogous
    to the <fo:flow flow-name="xsl-region-body">
    element, so we put the main document processing here.  
    =============================================== -->

  <xsl:template match="body">

  <!-- ============================================
    Start generating the content for the main page 
    area (xsl-region-body).
    =============================================== -->
        
    <fo:flow flow-name="xsl-region-body">
      <xsl:apply-templates select="/html/head/title"/>
      <fo:block space-after="12pt" line-height="17pt" 
        font-size="14pt" text-align="center">
        developerWorks loves you!
      </fo:block>
      <fo:block space-after="24pt" line-height="17pt" 
        font-size="14pt" text-align="center" font-weight="bold" 
        font-family="monospace">
        ibm.com/developerWorks
      </fo:block>
          
  <!-- ============================================
    Now we call the template to build the table
    of contents.
    =============================================== -->

      <xsl:call-template name="toc"/>
            
  <!-- ============================================
    This one line of code processes everything in 
    the body of the document.  The template that
    processes the <body> element in turn processes
    everything that's inside it.
    =============================================== -->

      <xsl:apply-templates select="*|text()"/>

  <!-- ============================================
    We put an ID at the end of the document so we 
    can do "Page x of y" numbering.
    =============================================== -->
      <fo:block id="TheVeryLastPage" font-size="0pt"
        line-height="0pt" space-after="0pt"/>

    </fo:flow>
  </xsl:template>

  <!-- ============================================
    We handle a break element by inserting an 
    empty <fo:block>.
    =============================================== -->

  <xsl:template match="br">
    <fo:block> </fo:block>
  </xsl:template>

  <!-- ============================================
    We're handling <center> as a block element; if
    you use it, it creates a new paragraph that's 
    centered on the page. 
    =============================================== -->

  <xsl:template match="center">
    <fo:block text-align="center">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The <cite> element is rendered in italics, 
    unless it's inside an italic (<i>) element. 
    We use the parent axis to check this. 
    =============================================== -->

  <xsl:template match="cite">
    <xsl:choose>
      <xsl:when test="parent::i">
        <fo:inline font-style="normal">
          <xsl:apply-templates select="*|text()"/>
        </fo:inline>
      </xsl:when>
      <xsl:otherwise>
        <fo:inline font-style="italic">
          <xsl:apply-templates select="*|text()"/>
        </fo:inline>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ============================================
    We render <code> inline in a monospaced font.
    =============================================== -->

  <xsl:template match="code">
    <fo:inline font-family="monospace">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We don't do anything with the <dl> element, we
    just handle the elements it contains.  Notice
    that we're ignoring any text that appears 
    in the <dl> itself; I'm not sure if that's
    the right call.
    =============================================== -->

  <xsl:template match="dl">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <!-- ============================================
    A definition term is rendered in bold.  We 
    specify keep-with-next here, although it doesn't
    always work with FOP.
    =============================================== -->

  <xsl:template match="dt">
    <fo:block font-weight="bold" space-after="2pt"
      keep-with-next="always">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    We handle each <dd> element as an indented block
    beneath the defined term.  If the following 
    element is another <dd>, that means it's another
    definition for the same term.  In that case, 
    we don't put as much vertical space after the 
    block. 
    =============================================== -->

  <xsl:template match="dd">
    <fo:block start-indent="1cm">
      <xsl:attribute name="space-after">
        <xsl:choose>
          <xsl:when test="name(following::*[1]) = 'dd'">
            <xsl:text>3pt</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>12pt</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The HTML <em> element is typically rendered in 
    italics. 
    =============================================== -->

  <xsl:template match="em">
    <fo:inline font-style="italic">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    For the <font> element, we handle the color, 
    face, and size attributes.  Color will work if
    it's one of the twelve colors supported by XSL-FO
    or it's a hex value like x0033ff.  (In other words,
    if you tell FOP to set the color to PapayaWhip, 
    you're out of luck.)  The face attribute will 
    work if FOP supports it.  (There are ways to add 
    fonts to FOP, see the FOP documentation for more 
    info.)  Size is supported for values like 
    size="14pt", size="3", and size="+3".
    =============================================== -->

  <xsl:template match="font">
    <xsl:variable name="color">
      <xsl:choose>
        <xsl:when test="@color">
          <xsl:value-of select="@color"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>black</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="face">
      <xsl:choose>
        <xsl:when test="@face">
          <xsl:value-of select="@face"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>sans-serif</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="size">
      <xsl:choose>
        <xsl:when test="@size">
          <xsl:choose>
            <xsl:when test="contains(@size, 'pt')">
              <xsl:text>@size</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+1'">
              <xsl:text>110%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+2'">
              <xsl:text>120%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+3'">
              <xsl:text>130%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+4'">
              <xsl:text>140%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+5'">
              <xsl:text>150%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+6'">
              <xsl:text>175%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+7'">
              <xsl:text>200%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-1'">
              <xsl:text>90%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-2'">
              <xsl:text>80%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-3'">
              <xsl:text>70%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-4'">
              <xsl:text>60%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-5'">
              <xsl:text>50%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-6'">
              <xsl:text>40%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-7'">
              <xsl:text>30%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '1'">
              <xsl:text>8pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '2'">
              <xsl:text>10pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '3'">
              <xsl:text>12pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '4'">
              <xsl:text>14pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '5'">
              <xsl:text>18pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '6'">
              <xsl:text>24pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '7'">
              <xsl:text>36pt</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>12pt</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:text>12pt</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <fo:inline font-size="{$size}" font-family="{$face}"
      color="{$color}">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We render the <h1> by putting a horizontal rule
    and a page break before it.  We also process 
    the id attribute; if the <h1> tag has one, we 
    use it.  If not, we see if the preceding element
    is a named anchor (<a name="x"/>).  If there is
    a named anchor before the <h1>, we use the name
    of the anchor point as the id.
    =============================================== -->

  <xsl:template match="h1">
    <fo:block break-before="page">
      <fo:leader leader-pattern="rule"/>
    </fo:block>
    <fo:block font-size="28pt" line-height="32pt"
      keep-with-next="always"
      space-after="22pt" font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:when test="name(preceding-sibling::*[1]) = 'a' and
                          preceding-sibling::*[1][@name]">
            <xsl:value-of select="preceding-sibling::*[1]/@name"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h2> is in a slightly smaller font than an <h1>,
    and it doesn't have a page break or a line.
    =============================================== -->

  <xsl:template match="h2">
    <fo:block font-size="24pt" line-height="28pt"
      keep-with-next="always" space-after="18pt"
      font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h3> is slightly smaller than <h2>.
    =============================================== -->

  <xsl:template match="h3">
    <fo:block font-size="21pt" line-height="24pt"
      keep-with-next="always" space-after="14pt"
      font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h4> is smaller than <h3>.  For the bookmarks
    and table of contents, <h4> is the lowest level
    we include.
    =============================================== -->

  <xsl:template match="h4">
    <fo:block font-size="18pt" line-height="21pt"
      keep-with-next="always" space-after="12pt"
      font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h5> is pretty small, and is underlined to 
    help it stand out. 
    =============================================== -->

  <xsl:template match="h5">
    <fo:block font-size="16pt" line-height="19pt"
      keep-with-next="always" space-after="12pt"
      font-family="serif" text-decoration="underline">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h6> is the smallest heading of all, and is
    underlined and italicized.  
    =============================================== -->

  <xsl:template match="h6">
    <fo:block font-size="14pt" line-height="17pt"
      keep-with-next="always" space-after="12pt"
      font-family="serif" font-style="italic"
      text-decoration="underline">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    We render an <hr> with a leader.  Because <hr>
    is empty, we don't have to process any child
    elements. 
    =============================================== -->

  <xsl:template match="hr">
    <fo:block>
      <fo:leader leader-pattern="rule"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    Italics.  You can't get much simpler than that.
    =============================================== -->

  <xsl:template match="i">
    <fo:inline font-style="italic">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    For the <img> element, we use the src attribute
    as it comes from HTML.  We also check for any 
    width and height attributes.  If those attributes
    are there, we try to use them; height="300px" is
    used as-is, while height="300" is converted to 
    the value "300px".
    =============================================== -->

  <xsl:template match="img">
    <fo:block space-after="12pt">
      <fo:external-graphic src="{@src}">
        <xsl:if test="@width">
          <xsl:attribute name="width">
            <xsl:choose>
              <xsl:when test="contains(@width, 'px')">
                <xsl:value-of select="@width"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="concat(@width, 'px')"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@height">
          <xsl:attribute name="height">
            <xsl:choose>
              <xsl:when test="contains(@height, 'px')">
                <xsl:value-of select="@height"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="concat(@height, 'px')"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </xsl:if>
      </fo:external-graphic>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The <kbd> element is in a slightly larger 
    monospaced text.
    =============================================== -->

  <xsl:template match="kbd">
    <fo:inline font-family="monospace" font-size="110%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We handle the <li> elements under the <ol> and 
    <ul> elements, so there's no <li> template here.
    =============================================== -->

  <!-- ============================================
    For the <nobr> element, we simply turn off the
    wrap-option. 
    =============================================== -->

  <xsl:template match="nobr">
    <fo:inline wrap-option="no-wrap">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We handle an ordered list with two complications:
    If the list appears inside another list (either 
    an <ol> or <ul>), we don't put any vertical space
    after it.  The other issue is that we indent the
    list according to how deeply nested the list is. 
    =============================================== -->

  <xsl:template match="ol">
    <fo:list-block provisional-distance-between-starts="1cm"
      provisional-label-separation="0.5cm">
      <xsl:attribute name="space-after">
        <xsl:choose>
          <xsl:when test="ancestor::ul or ancestor::ol">
            <xsl:text>0pt</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>12pt</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="start-indent">
        <xsl:variable name="ancestors">
          <xsl:choose>
            <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
              <xsl:value-of select="1 + 
                                    (count(ancestor::ol) + 
                                     count(ancestor::ul)) * 
                                    1.25"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>1</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="concat($ancestors, 'cm')"/>
      </xsl:attribute>
      <xsl:apply-templates select="*"/>
    </fo:list-block>
  </xsl:template>

  <!-- ============================================
    When we handle items in an ordered list, we need
    to check if the list has a start attribute.  If
    it does, we change the starting number accordingly.
    Once we've figured out where to start counting,
    we check the type attribute to see what format
    the numbers should use.  
    =============================================== -->

  <xsl:template match="ol/li">
    <fo:list-item>
      <fo:list-item-label end-indent="label-end()">
        <fo:block>
          <xsl:variable name="value-attr">
            <xsl:choose>
              <xsl:when test="../@start">
                <xsl:number value="position() + ../@start - 1"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:number value="position()"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:choose>
            <xsl:when test="../@type='i'">
              <xsl:number value="$value-attr" format="i. "/>
            </xsl:when>
            <xsl:when test="../@type='I'">
              <xsl:number value="$value-attr" format="I. "/>
            </xsl:when>
            <xsl:when test="../@type='a'">
              <xsl:number value="$value-attr" format="a. "/>
            </xsl:when>
            <xsl:when test="../@type='A'">
              <xsl:number value="$value-attr" format="A. "/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:number value="$value-attr" format="1. "/>
            </xsl:otherwise>
          </xsl:choose>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block>
          <xsl:apply-templates select="*|text()"/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <!-- ============================================
    Your basic paragraph.
    =============================================== -->

  <xsl:template match="p">
    <fo:block font-size="12pt" line-height="15pt"
      space-after="12pt">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    Preformatted text is rendered in a monospaced
    font.  We also have to set the wrap-option
    and white-space-collapse properties.  
    =============================================== -->

  <xsl:template match="pre">
    <fo:block font-family="monospace"
      white-space-collapse="false" wrap-option="no-wrap">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    Sample text is rendered in a slightly larger
    monospaced font. 
    =============================================== -->

  <xsl:template match="samp">
    <fo:inline font-family="monospace" font-size="110%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    The <small> element is rendered with a relative
    font size.  That means putting one <small>
    element inside another creates really small 
    text.  
    =============================================== -->

  <xsl:template match="small">
    <fo:inline font-size="80%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    For strikethrough text, we use the text-decoration
    property.  
    =============================================== -->

  <xsl:template match="strike">
    <fo:inline text-decoration="line-through">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    Strongly emphasized text is simply rendered
    in bold. 
    =============================================== -->

  <xsl:template match="strong">
    <fo:inline font-weight="bold">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    For subscript text, we use the vertical-align
    property and decrease the font size.  
    =============================================== -->

  <xsl:template match="sub">
    <fo:inline vertical-align="sub" font-size="75%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    Superscript text changes the vertical-align
    property also, and uses a smaller font.
    =============================================== -->

  <xsl:template match="sup">
    <fo:inline vertical-align="super" font-size="75%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    Tables are a hassle.  The main problem we have
    is converting the cols attribute into some 
    number of <fo:table-column> elements.  We do 
    this with a named template called build-columns.
    Once we've processed the cols attribute, we 
    invoke all of the templates for the children 
    of this element. 
    =============================================== -->

  <xsl:template match="table">
    <fo:table table-layout="fixed">
      <xsl:choose>
        <xsl:when test="@cols">
         <xsl:call-template name="build-columns">
           <xsl:with-param name="cols" 
             select="concat(@cols, ' ')"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <fo:table-column column-width="200pt"/>
        </xsl:otherwise>
      </xsl:choose>
      <fo:table-body>
        <xsl:apply-templates select="*"/>
      </fo:table-body>
    </fo:table>
  </xsl:template>

  <!-- ============================================
    For a table cell, we put everything inside a
    <fo:table-cell> element.  We set the padding
    property correctly, then we set the border 
    style.  For the border style, we look to see if
    any of the ancestor elements we care about 
    specified a solid border.  Next, we check for the 
    rowspan, colspan, and align attributes.  Notice 
    that for align, we check this element, then go
    up the ancestor chain until we find the <table>
    element or we find something that specifies the 
    alignment. 
    =============================================== -->

  <xsl:template match="td">
    <fo:table-cell 
      padding-start="3pt" padding-end="3pt"
      padding-before="3pt" padding-after="3pt">
      <xsl:if test="@colspan">
        <xsl:attribute name="number-columns-spanned">
          <xsl:value-of select="@colspan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@rowspan">
        <xsl:attribute name="number-rows-spanned">
          <xsl:value-of select="@rowspan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@border='1' or 
                    ancestor::tr[@border='1'] or
                    ancestor::thead[@border='1'] or
                    ancestor::table[@border='1']">
        <xsl:attribute name="border-style">
          <xsl:text>solid</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="border-color">
          <xsl:text>black</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="border-width">
          <xsl:text>1pt</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:variable name="align">
        <xsl:choose>
          <xsl:when test="@align">
            <xsl:choose>
              <xsl:when test="@align='center'">
                <xsl:text>center</xsl:text>
              </xsl:when>
              <xsl:when test="@align='right'">
                <xsl:text>end</xsl:text>
              </xsl:when>
              <xsl:when test="@align='justify'">
                <xsl:text>justify</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>start</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:when test="ancestor::tr[@align]">
            <xsl:choose>
              <xsl:when test="ancestor::tr/@align='center'">
                <xsl:text>center</xsl:text>
              </xsl:when>
              <xsl:when test="ancestor::tr/@align='right'">
                <xsl:text>end</xsl:text>
              </xsl:when>
              <xsl:when test="ancestor::tr/@align='justify'">
                <xsl:text>justify</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>start</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:when test="ancestor::thead">
            <xsl:text>center</xsl:text>
          </xsl:when>
          <xsl:when test="ancestor::table[@align]">
            <xsl:choose>
              <xsl:when test="ancestor::table/@align='center'">
                <xsl:text>center</xsl:text>
              </xsl:when>
              <xsl:when test="ancestor::table/@align='right'">
                <xsl:text>end</xsl:text>
              </xsl:when>
              <xsl:when test="ancestor::table/@align='justify'">
                <xsl:text>justify</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>start</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>start</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <fo:block text-align="{$align}">
        <xsl:apply-templates select="*|text()"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <!-- ============================================
    The rarely-used <tfoot> element contains some
    number of <tr> elements; we just invoke the 
    template for <tr> here. 
    =============================================== -->

  <xsl:template match="tfoot">
    <xsl:apply-templates select="tr"/>
  </xsl:template>

  <!-- ============================================
    If there's a <th> element, we process it just 
    like a normal <td>, except the font-weight is 
    always bold and the text-align is always center. 
    =============================================== -->

  <xsl:template match="th">
    <fo:table-cell
      padding-start="3pt" padding-end="3pt"
      padding-before="3pt" padding-after="3pt">
      <xsl:if test="@border='1' or 
                    ancestor::tr[@border='1'] or
                    ancestor::table[@border='1']">
        <xsl:attribute name="border-style">
          <xsl:text>solid</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="border-color">
          <xsl:text>black</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="border-width">
          <xsl:text>1pt</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <fo:block font-weight="bold" text-align="center">
        <xsl:apply-templates select="*|text()"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <!-- ============================================
    Just like <tfoot>, the rarely-used <thead> element
    contains some number of table rows.  We just 
    invoke the template for <tr> here. 
    =============================================== -->

  <xsl:template match="thead">
    <xsl:apply-templates select="tr"/>
  </xsl:template>

  <!-- ============================================
    The title of the document is rendered in a large
    bold font, centered on the page.  This is the 
    <title> element in the <head> in <html>.
    =============================================== -->

  <xsl:template match="title">
    <fo:block space-after="18pt" line-height="27pt" 
      font-size="24pt" font-weight="bold" text-align="center">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    For an HTML table row, we create an XSL-FO table
    row, then invoke the templates for everything 
    inside it. 
    =============================================== -->

  <xsl:template match="tr">
    <fo:table-row>
      <xsl:apply-templates select="*|text()"/>
    </fo:table-row>
  </xsl:template>

  <!-- ============================================
    Teletype text is rendered in a monospaced font.
    =============================================== -->

  <xsl:template match="tt">
    <fo:inline font-family="monospace">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>
  
  <!-- ============================================
    For underlined text, we use the text-decoration
    property.
    =============================================== -->

  <xsl:template match="u">
    <fo:inline text-decoration="underline">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    The unordered list is pretty straightforward; 
    the only complication is calculating the space-
    after and start-indent properties.  If this 
    list is inside another list, we don't put any 
    space after this one, and we calculate the 
    indentation based on the nesting level of this 
    list. 
    =============================================== -->

  <xsl:template match="ul">
    <fo:list-block provisional-distance-between-starts="1cm"
      provisional-label-separation="0.5cm">
      <xsl:attribute name="space-after">
        <xsl:choose>
          <xsl:when test="ancestor::ul or ancestor::ol">
            <xsl:text>0pt</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>12pt</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="start-indent">
        <xsl:variable name="ancestors">
          <xsl:choose>
            <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
              <xsl:value-of select="1 + 
                                    (count(ancestor::ol) + 
                                     count(ancestor::ul)) * 
                                    1.25"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>1</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="concat($ancestors, 'cm')"/>
      </xsl:attribute>
      <xsl:apply-templates select="*"/>
    </fo:list-block>
  </xsl:template>

  <!-- ============================================
    List items inside unordered lists are easy; we
    just have to use the correct Unicode character
    for the bullet.  
    =============================================== -->

  <xsl:template match="ul/li">
    <fo:list-item>
      <fo:list-item-label end-indent="label-end()">
        <fo:block>&#x2022;</fo:block>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block>
          <xsl:apply-templates select="*|text()"/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <!-- ============================================
    The <var> element is rendered in italics. 
    =============================================== -->

  <xsl:template match="var">
    <fo:inline font-style="italic">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    Named templates
    =============================================== -->

  <!-- ============================================
    This template creates the table of contents.  
    It indents each entry according to the heading
    level (<h1> isn't indented, <h2> is indented 
    1 cm, etc.).  We insert the title of each
    section as a link, put a leader of dots, then 
    the page number.  Notice that we generate an
    id for the heading if it doesn't have one
    already; this is done with the XSLT generate-id()
    function. 
    =============================================== -->

  <xsl:template name="toc">
    <fo:block>
      <fo:leader leader-pattern="rule" space-after="18pt"/>
    </fo:block>
    <fo:block space-after="12pt" id="TableOfContents" 
      line-height="21pt" font-size="18pt" text-align="start">
      Table of Contents
    </fo:block>
    <fo:block line-height="11pt" font-size="8pt" 
      space-after="6pt">
      If you're viewing this document online, you can 
      click any of the topics below to link directly to 
      that section.
    </fo:block>
    <xsl:for-each select="/html/body//h1 |
                          /html/body//h2 | 
                          /html/body//h3 |
                          /html/body//h4">
      <fo:block text-align-last="justify" line-height="17pt"
        font-size="14pt" space-after="3pt" text-align="start"
        text-indent="-1cm">
        <xsl:attribute name="start-indent">
          <xsl:choose>
            <xsl:when test="name() = 'h1'">
              <xsl:text>1cm</xsl:text>
            </xsl:when>
            <xsl:when test="name() = 'h2'">
              <xsl:text>1.5cm</xsl:text>
            </xsl:when>
            <xsl:when test="name() = 'h3'">
              <xsl:text>2cm</xsl:text>
            </xsl:when>
            <xsl:when test="name() = 'h4'">
              <xsl:text>2.5cm</xsl:text>
            </xsl:when>
          </xsl:choose>
        </xsl:attribute>
        <fo:basic-link color="blue">
          <xsl:attribute name="internal-destination">
            <xsl:choose>
              <xsl:when test="@id">
                <xsl:value-of select="@id"/>
              </xsl:when>
              <xsl:when test="name(preceding-sibling::*[1]) = 'a' and
                              preceding-sibling::*[1][@name]">
                <xsl:value-of select="preceding-sibling::*[1]/@name"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="generate-id()"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:apply-templates select="*|text()"/>
        </fo:basic-link>
        <fo:leader leader-pattern="dots"
          leader-pattern-width="5pt"/>
        <fo:page-number-citation>
          <xsl:attribute name="ref-id">
            <xsl:choose>
              <xsl:when test="@id">
                <xsl:value-of select="@id"/>
              </xsl:when>
              <xsl:when test="name(preceding-sibling::*[1]) = 'a' and
                              preceding-sibling::*[1][@name]">
                <xsl:value-of select="preceding-sibling::*[1]/@name"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="generate-id()"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </fo:page-number-citation>
      </fo:block>
    </xsl:for-each>
  </xsl:template>

  <!-- ============================================
    This complicated template creates the PDF bookmarks
    supported by FOP.  For each <h1> element, it 
    creates a bookmark for that element, then it
    looks for all of the <h2> elements beneath it.
    For each <h2>, it looks for all the <h3> 
    elements beneath it, etc.  We use generate-id()
    extensively so we can determine whether a given
    <h1> or <h2> or <h3> is one we've seen before. 
    The end result of all this hard work is a set of
    hierarchical bookmarks that are nested within each 
    other.
    =============================================== -->

  <xsl:template name="generate-bookmarks">
    <fox:outline internal-destination="TableOfContents">
      <fox:label>Table of Contents</fox:label>
    </fox:outline>
    <xsl:for-each select="/html/body//h1">
      <xsl:variable name="current-h1" select="generate-id()"/>
      <fox:outline>
        <xsl:attribute name="internal-destination">
          <xsl:choose>
            <xsl:when test="@id">
              <xsl:value-of select="@id"/>
            </xsl:when>
            <xsl:when test="name(preceding-sibling::*[1]) = 'a' and
                            preceding-sibling::*[1][@name]">
              <xsl:value-of select="preceding-sibling::*[1]/@name"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="generate-id()"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <fox:label>
          <xsl:value-of select="."/>
        </fox:label>
        <xsl:for-each select="following-sibling::h2">
          <xsl:variable name="current-h2" select="generate-id()"/>
          <xsl:if 
            test="generate-id(preceding-sibling::h1[1]) = $current-h1">
            <fox:outline>
              <xsl:attribute name="internal-destination">
                <xsl:choose>
                  <xsl:when test="@id">
                    <xsl:value-of select="@id"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$current-h2"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <fox:label>
                <xsl:value-of select="."/>
              </fox:label>
              <xsl:for-each select="following-sibling::h3">
                <xsl:variable name="current-h3" select="generate-id()"/>
                <xsl:if 
                  test="generate-id(preceding-sibling::h2[1]) = $current-h2">
                  <fox:outline>
                    <xsl:attribute name="internal-destination">
                      <xsl:choose>
                        <xsl:when test="@id">
                          <xsl:value-of select="@id"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="$current-h2"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:attribute>
                    <fox:label>
                      <xsl:value-of select="."/>
                    </fox:label>
                    <xsl:for-each select="following-sibling::h4">
                      <xsl:if 
                        test="generate-id(preceding-sibling::h3[1]) = $current-h3">
                        <fox:outline>
                          <xsl:attribute name="internal-destination">
                            <xsl:choose>
                              <xsl:when test="@id">
                                <xsl:value-of select="@id"/>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:value-of select="$current-h3"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:attribute>
                          <fox:label>
                            <xsl:value-of select="."/>
                          </fox:label>
                        </fox:outline>
                      </xsl:if>
                    </xsl:for-each>
                  </fox:outline>
                </xsl:if>
              </xsl:for-each>
            </fox:outline>
          </xsl:if>
        </xsl:for-each>
      </fox:outline>
    </xsl:for-each>
  </xsl:template>

  <!-- ============================================
    This template generates an <fo:table-column>
    element for each token in the cols attribute of
    the HTML <table> tag.  The template processes
    the first token, then invokes itself with the 
    rest of the string. 
    =============================================== -->

  <xsl:template name="build-columns">
    <xsl:param name="cols"/>

    <xsl:if test="string-length(normalize-space($cols))">
      <xsl:variable name="next-col">
        <xsl:value-of select="substring-before($cols, ' ')"/>
      </xsl:variable>
      <xsl:variable name="remaining-cols">
        <xsl:value-of select="substring-after($cols, ' ')"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="contains($next-col, 'pt')">
          <fo:table-column column-width="{$next-col}"/>
        </xsl:when>
        <xsl:when test="number($next-col) &gt; 0">
          <fo:table-column column-width="{concat($next-col, 'pt')}"/>
        </xsl:when>
        <xsl:otherwise>
          <fo:table-column column-width="50pt"/>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:call-template name="build-columns">
        <xsl:with-param name="cols" select="concat($remaining-cols, ' ')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
