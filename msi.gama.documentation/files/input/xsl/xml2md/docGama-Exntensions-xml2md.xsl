<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

 
<xsl:template match="/">

<xsl:text># Extension </xsl:text> <xsl:value-of select="doc/@plugin"></xsl:value-of>

<xsl:text>

### </xsl:text> 
		<xsl:for-each select="/doc/operators/operator"> 
			<xsl:sort select="@name" />
			<xsl:variable name="nameOp" select="@name"/>
				<xsl:text>[</xsl:text><xsl:value-of select="$nameOp"/><xsl:text>](#</xsl:text><xsl:value-of select="$nameOp"/><xsl:text>), </xsl:text> 		
			</xsl:for-each>  	
		
</xsl:template>


</xsl:stylesheet>