<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="docGama-utils-xml2md.xsl"/>

<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

<xsl:template match="/">
 	<xsl:text># Built-in Species</xsl:text>
 	
<xsl:call-template name="msgIntro"/>

<xsl:text>

It is possible to use in the models a set of built-in agents. These agents allow to directly use some advance features like clustering, multi-criteria analysis, etc. The creation of these agents are similar as for other kinds of agents:

```
create species: my_built_in_agent returns: the_agent;
```
    
So, for instance, to be able to use clustering techniques in the model:

```
create cluster_builder returns: clusterer;
```
	</xsl:text>


## Table of Contents

<xsl:call-template name="buildSpeciesByName"/>

<xsl:call-template name="buildSpecies"/>

<xsl:text>
</xsl:text>

</xsl:template>

<xsl:template name="buildSpecies">

	<xsl:for-each select="doc/speciess/species">
    	<xsl:sort select="@name" />
    	
----
<!-- <xsl:call-template name="keyword">     -->
<!-- 	<xsl:with-param name="category" select="'species'"/> -->
<!-- 	<xsl:with-param name="nameGAMLElement" select="@name"/> -->
<!-- </xsl:call-template> -->

## `<xsl:value-of select="@name"/>`	<xsl:text>
</xsl:text><xsl:value-of select="documentation/result"/> <xsl:text>
</xsl:text>
		<xsl:call-template name="buildVariables"/>
		<xsl:call-template name="buildActions"/>		
	</xsl:for-each>
</xsl:template>

  
  
<xsl:template name="buildSpeciesByName">
	<xsl:for-each select="/doc/speciess/species"> 
		<xsl:sort select="@name" />
			<xsl:text>[</xsl:text> <xsl:value-of select="@name"/> <xsl:text>](#</xsl:text> <xsl:value-of select="translate(@name, $uppercase, $smallcase)"/> <xsl:text>), </xsl:text> 
	</xsl:for-each>  
</xsl:template>  
  
</xsl:stylesheet>
