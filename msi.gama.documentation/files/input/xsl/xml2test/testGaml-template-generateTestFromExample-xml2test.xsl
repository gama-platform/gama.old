<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template name="generateTestFromExample">
	<xsl:for-each select="examples/example">
	 <!-- <xsl:if test="@isExecutable = 'true'">  --> 	
		<xsl:choose>
			<xsl:when test="@equals">
			
				<xsl:choose>
				<xsl:when test="@var">
				<xsl:text>			</xsl:text>
			<xsl:if test="@isExecutable = 'false'">//</xsl:if>	<xsl:if test="@type != 'null'"><xsl:value-of select="@type"/><xsl:text> </xsl:text> <xsl:value-of select="@var"/> &lt;- </xsl:if><xsl:value-of select="@code"/><xsl:if test="@type != 'null'">;</xsl:if> 	// <xsl:value-of select="@var"/> equals <xsl:value-of select="@equals"/><xsl:if test="@test = 'false'"><xsl:text>
</xsl:text></xsl:if>
			<xsl:if test="@test = 'true'">
			assert <xsl:value-of select="@var"/> = <xsl:value-of select="@equals"/>;<xsl:text> 
</xsl:text> </xsl:if>								
				</xsl:when>
				<xsl:otherwise>
				<xsl:text>			</xsl:text>
			<xsl:if test="@isExecutable = 'false'">//</xsl:if>	<xsl:value-of select="@type"/> var<xsl:value-of select="@index"/> &lt;- <xsl:value-of select="@code"/>; 	// var<xsl:value-of select="@index"/> equals <xsl:value-of select="@equals"/><xsl:if test="@test = 'false'"><xsl:text>
</xsl:text></xsl:if>
			<xsl:if test="@test = 'true'">
			assert var<xsl:value-of select="@index"/> = <xsl:value-of select="@equals"/>;<xsl:text> 
</xsl:text> </xsl:if>					
				</xsl:otherwise>
				
				</xsl:choose>

			</xsl:when>
			<xsl:when test="@raises">
			assert <xsl:value-of select="@code"/> raises: "<xsl:value-of select="@raises"/>";<xsl:text> 
</xsl:text>			
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>			</xsl:text>
				<xsl:if test="@isExecutable = 'false'">//</xsl:if>	<xsl:value-of select="@code"/><xsl:text>
</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	<!-- </xsl:if>  -->	
	</xsl:for-each>
</xsl:template>		

</xsl:stylesheet>
