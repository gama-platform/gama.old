<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
 
 <xsl:template name="checkName">
 	<xsl:choose>
 		<xsl:when test="@name = '*'"><xsl:text>`*`</xsl:text></xsl:when>
 		<xsl:when test="@name = '**'"><xsl:text>`**`</xsl:text></xsl:when>
 		<xsl:when test="@name = '&lt;-&gt;'"><xsl:text>`&lt;-&gt;`</xsl:text></xsl:when>
 		<xsl:when test="@name = '='"><xsl:text>`=`</xsl:text></xsl:when>  		
 		<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
 	</xsl:choose>
 </xsl:template>

 </xsl:stylesheet>