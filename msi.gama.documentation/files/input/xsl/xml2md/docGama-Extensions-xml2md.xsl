<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="docGama-Operators-xml2md.xsl"/>
<xsl:import href="docGama-Statements-xml2md.xsl"/>
<xsl:import href="docGama-Species-xml2md.xsl"/> 
<xsl:import href="docGama-Skills-xml2md.xsl"/> 
<xsl:import href="docGama-Architectures-xml2md.xsl"/>

<xsl:template match="/">

<xsl:text># Extension

----

 </xsl:text> <xsl:value-of select="doc/@plugin"></xsl:value-of>

## Table of Contents

### Operators
<xsl:call-template name="buildOperatorsByName"/>

### Statements
<xsl:call-template name="buildStatementsByName"/>

### Skills
<xsl:call-template name="buildSkillsByName"/>

### Architectures

<xsl:call-template name="buildSkillsByName"/>

### Species
<xsl:call-template name="buildSpeciesByName"/>


----

## Operators
	<xsl:call-template name="buildOperators"/>

----

## Skills
	<xsl:call-template name="buildSkills"/>

----

## Statements
	<xsl:call-template name="buildStatements"/>	
	
----

## Species
	<xsl:call-template name="buildSpecies"/>
	
----

## Architectures 
	<xsl:call-template name="buildSkills"/>
	
<xsl:text>
</xsl:text>	
		
</xsl:template>


</xsl:stylesheet>