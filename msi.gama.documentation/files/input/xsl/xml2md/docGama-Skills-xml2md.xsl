<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="docGama-utils-xml2md.xsl"/>

<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

<xsl:template match="/">
 	<xsl:text># Built-in Skills</xsl:text>
 	
<xsl:call-template name="msgIntro"/>

<xsl:text>## Introduction

Skills are built-in modules, written in Java, that provide a set of related built-in variables and built-in actions (in addition to those already provided by GAMA) to the species that declare them. A declaration of skill is done by filling the skills attribute in the species definition:

```
species my_species skills: [skill1, skill2] {
    ...
}
```

Skills have been designed to be mutually compatible so that any combination of them will result in a functional species. An example of skill is the `moving` skill.
  
So, for instance, if a species is declared as:

```
species foo skills: [moving]{
...
}
```

Its agents will automatically be provided with the following variables : `speed`, `heading`, `destination` and the following actions: `move`, `goto`, `wander`, `follow` in addition to those built-in in species and declared by the modeller. Most of these variables, except the ones marked read-only, can be customized and modified like normal variables by the modeller. For instance, one could want to set a maximum for the speed; this would be done by redeclaring it like this:

```
float speed max:100 min:0;
```

Or, to obtain a speed increasing at each simulation step:

```
float speed max:100 min:0  &lt;- 1 update: speed * 1.01;
```

Or, to change the speed in a behavior:

```
if speed = 5 {
    speed &lt;- 10;
}
```

----


## Table of Contents
&lt;wiki:toc max_depth="3" /&gt;

</xsl:text>
	<xsl:call-template name="buildSkillsByName"/>

	<xsl:call-template name="buildSkills"/>

</xsl:template>

<xsl:template name="buildSkillsByName">
	<xsl:for-each select="doc/skills/skill"> 
		<xsl:sort select="@name" />	
			<xsl:text>[</xsl:text> <xsl:value-of select="@name"/> <xsl:text>](#</xsl:text> <xsl:value-of select="translate(@name, $uppercase, $smallcase)" /> <xsl:text>), </xsl:text> 
	</xsl:for-each>  
</xsl:template>

<xsl:template name= "buildSkills">
	<xsl:for-each select="doc/skills/skill">
    	<xsl:sort select="@name" />
    	
----
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'skill'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
## <xsl:value-of select="@name"/><xsl:text>
</xsl:text><xsl:value-of select="documentation/result"/> <xsl:text>
</xsl:text>
		<xsl:call-template name="buildVariables"/>
		<xsl:call-template name="buildActions"/>		
	</xsl:for-each>
</xsl:template>

    
 	<xsl:template name="buildVariables"> 
### Variables
	<xsl:for-each select="vars/var">		
	<xsl:sort select="@name" />   
  * **`<xsl:value-of select="@name"/>`** (`<xsl:value-of select="@type"/>`): <xsl:value-of select="documentation/result"/> 
		</xsl:for-each>
	</xsl:template>

 	<xsl:template name="buildActions"> 
 	
### Actions
	<xsl:for-each select="actions/action">		
	<xsl:sort select="@name" />  
	 
#### **`<xsl:value-of select="@name"/>`**
<xsl:value-of select="documentation/result"/><xsl:text>
</xsl:text>
* returns: <xsl:value-of select="@returnType"/>
  			<xsl:for-each select="args/arg"> 			
* **`<xsl:value-of select="@name"/>`** (<xsl:value-of select="@type"/>): <xsl:value-of select="documentation/result"/> 
  			</xsl:for-each>
		
<xsl:if test="documentation/examples[node()]">

```
<xsl:for-each select="documentation/examples/example" >
<xsl:if test="@code != ''"><xsl:value-of select="@code"/><xsl:text>
</xsl:text>
</xsl:if>
</xsl:for-each>```
</xsl:if>	
		
		</xsl:for-each>	
	</xsl:template>
  
</xsl:stylesheet>
