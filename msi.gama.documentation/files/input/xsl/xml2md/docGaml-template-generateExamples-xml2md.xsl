<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

 <xsl:template name="generateExamples">
	<xsl:for-each select="examples/example">
	  	<xsl:if test="@isTestOnly = 'false'">	
		<xsl:choose>
			<xsl:when test="@equals">
				<xsl:choose>
				<xsl:when test="@var">
			<xsl:if test="@type != 'null'"><xsl:value-of select="@type"/><xsl:text> </xsl:text> <xsl:value-of select="@var"/> &lt;- </xsl:if><xsl:value-of select="@code"/><xsl:if test="@type != 'null'">;</xsl:if> 	// <xsl:value-of select="@var"/> equals <xsl:value-of select="@equals"/><xsl:text>
</xsl:text>							
				</xsl:when>
				<xsl:otherwise>
			<xsl:value-of select="@type"/> var<xsl:value-of select="@index"/> &lt;- <xsl:value-of select="@code"/>; 	// var<xsl:value-of select="@index"/> equals <xsl:value-of select="@equals"/><xsl:text>
</xsl:text>				
				</xsl:otherwise>
				
				</xsl:choose>
			</xsl:when>		
			<xsl:otherwise>
				<xsl:value-of select="@code"/><xsl:text>
</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:if>
	</xsl:for-each>
</xsl:template>	

</xsl:stylesheet>
