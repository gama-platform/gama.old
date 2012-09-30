<?xml version="1.0" encoding="UTF-8"?><!-- DWXMLSource="file:///Macintosh HD/Users/marilleau/Desktop/GAMA_doc_Complet.xml" -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output 
  method="html"
  encoding="ISO-8859-1"
  doctype-public="-//W3C//DTD HTML 4.01//EN"
  doctype-system="http://www.w3.org/TR/html4/strict.dtd"
  indent="yes" />

<xsl:variable name="html_menu" select="''"/>
<xsl:variable name="html_doc" select="''"/>
<xsl:variable name="html_iterator" select="0"/>



<xsl:template match="/">
 	
    <html><body bgcolor="#999999">
    	<xsl:call-template name="buildMenu"/>
   
        <br />
   <xsl:call-template name="buildData"/>
        
    </body></html>
    
</xsl:template>

  
    
 <xsl:template match="operators">
 	<xsl:for-each select="operator">
    	<xsl:sort select="@name" />
	</xsl:for-each>
  </xsl:template>
  
  
 <xsl:template name="buildMenu"> 
      <xsl:for-each select="doc/operators/operator">
    	<xsl:sort select="@name" />
	        <A href="#{@id}">
            <xsl:value-of select="@name" />
            </A>; 
    	</xsl:for-each>
      
      
    </xsl:template>   
    
   <xsl:template name="buildData"> 
      <ul>
      <xsl:for-each select="doc/operators/operator">
     	<xsl:sort select="@name" />
	        <li><A name="{@id}">
            <xsl:value-of select="@name" />
            </A>
                    <br />
            <xsl:apply-templates select="documentation" />
            </li>
    	</xsl:for-each>

      </ul>
      
    </xsl:template>   
 
 
 <xsl:template match="documentation">
  	<font color="red">
    
    <xsl:value-of select="result" />
    <br />
    <xsl:value-of select="comment" />
    </font>
 </xsl:template>
</xsl:stylesheet>