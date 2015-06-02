<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="docGama-Operators-xml2md.xsl"/>
<xsl:import href="docGama-Statements-xml2md.xsl"/>

 
<xsl:template match="/">

<xsl:text># Extension </xsl:text> <xsl:value-of select="doc/@plugin"></xsl:value-of>

## Table of Contents
### Operators
<xsl:call-template name="buildOperatorsByName"/>
### Statements
<xsl:call-template name="buildStatementsByName"/>

----

## Operators by categories
	<xsl:call-template name="buildOperatorsByCategories"/>
	
----

## Operators
	<xsl:call-template name="buildOperators"/>

----

## Statements
	<xsl:call-template name="buildStatements"/>	
		
</xsl:template>


</xsl:stylesheet>