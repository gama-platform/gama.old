<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template match="/">
 <xsl:text disable-output-escaping="yes">&lt;xml version="1.0" encoding="UTF-8"&gt;</xsl:text>
 <xsl:text disable-output-escaping="yes">&lt;xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr"&gt;</xsl:text>


<!-- ======================== Operators ======================== -->

<xsl:for-each select="/doc/operators/operator"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'operator'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="''"/>		
</xsl:call-template>
</xsl:for-each>


<!-- ======================== Statements ======================== -->

<xsl:for-each select="/doc/statements/statement"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'statement'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="''"/>	
</xsl:call-template>
</xsl:for-each>


<!-- ======================== Facets ======================== -->


<xsl:for-each select="/doc/statements/statement"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="statName" select="@name"/>
	<xsl:for-each select="facets/facet"> 
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'facet'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="$statName"/>		
</xsl:call-template>
</xsl:for-each>

	</xsl:for-each>

 
<!-- ======================== Architecture ======================== -->
 
<xsl:for-each select="/doc/architecturess/architecture"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'architecture'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="''"/>	
</xsl:call-template>
</xsl:for-each> 
 
 
<!-- ======================== Skill ======================== -->
 
<xsl:for-each select="/doc/skills/skill"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'skill'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="''"/>	
</xsl:call-template>
</xsl:for-each> 

<!-- ======================== Constants ======================== -->
 
<xsl:for-each select="/doc/constants/constant"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'constant'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="''"/>	
</xsl:call-template>
</xsl:for-each> 

</xsl:template>


 
 <xsl:template name="keyword">
   	<xsl:param name="category"/>
   	<xsl:param name="nameGAMLElement"/>
   	<xsl:param name="insideElt"/>
   	<xsl:text disable-output-escaping="yes">
   	&lt;keyword id="</xsl:text><xsl:value-of select="$category"/><xsl:text>_</xsl:text><xsl:value-of select="$nameGAMLElement"/><xsl:text>"&gt;
   		&lt;name&gt;</xsl:text><xsl:value-of select="$nameGAMLElement"/><xsl:text>&lt;/name&gt;
   		&lt;category&gt;</xsl:text><xsl:value-of select="$category"/><xsl:text>&lt;/category&gt;</xsl:text>   	
		<xsl:if test="$insideElt != ''"><xsl:text>
		&lt;associatedKeywordList&gt;
			&lt;associatedKeyword&gt;</xsl:text><xsl:value-of select="$insideElt"/><xsl:text>&lt;/associatedKeyword&gt;	
		&lt;/associatedKeywordList&gt;</xsl:text></xsl:if><xsl:text>		
   	&lt;/keyword&gt; 	
   	</xsl:text>
</xsl:template>
 
</xsl:stylesheet>
