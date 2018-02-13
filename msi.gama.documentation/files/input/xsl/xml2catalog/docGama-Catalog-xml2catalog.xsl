<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

	<xsl:template match="/">
		<xsl:for-each select="/doc/operators/operator">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, operator, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>
		</xsl:for-each>

		<xsl:for-each select="/doc/statements/statement">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, statement, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>		
		</xsl:for-each>
	
		<xsl:for-each select="/doc/architectures/architecture">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, operator, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>			
		</xsl:for-each>

		<xsl:for-each select="/doc/constants/constant">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, constant, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>
		</xsl:for-each>

		<xsl:for-each select="/doc/skills/skill">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, skill, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>		
		</xsl:for-each>

		<xsl:for-each select="/doc/speciess/species">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, species, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>
		</xsl:for-each>

		<xsl:for-each select="/doc/types/type">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, type, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>		
		</xsl:for-each>
		
		<xsl:for-each select="/doc/files/file">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, file, <xsl:value-of select="@projectName"/>
			<xsl:text>
</xsl:text>		
		</xsl:for-each>		

	</xsl:template>


</xsl:stylesheet>
