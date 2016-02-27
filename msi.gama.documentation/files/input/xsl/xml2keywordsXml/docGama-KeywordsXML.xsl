<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template match="/">
 <xsl:text disable-output-escaping="yes">&lt;xml version="1.0" encoding="UTF-8"&gt;</xsl:text>
 <xsl:text disable-output-escaping="yes">&lt;xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr"&gt;</xsl:text>

<!-- === Concepts === -->
<!-- concept -->
<!-- operator -->
<!-- statement -->
	<!--  XXXXXX architecture -->
<!-- facet -->
	<!-- statement -->
<!-- architecture -->
<!-- skill -->
<!-- constant -->
<!-- action -->
	<!-- skills -->
	<!-- species -->
<!-- species -->	
<!-- attribute -->	
	<!-- skills -->
	<!-- species -->
<!-- Type -->


<!-- ======================== concept ======================== -->

<xsl:for-each select="/doc/conceptList/concept"> 
	<xsl:sort select="@id" />
	<xsl:variable name="conceptName" select="@id"/>

	<xsl:text disable-output-escaping="yes">
   	&lt;keyword id="concept_</xsl:text><xsl:value-of select="@id"/><xsl:text>"&gt;
   		&lt;name&gt;</xsl:text><xsl:value-of select="@id"/><xsl:text>&lt;/name&gt;
   		&lt;category&gt;concept&lt;/category&gt;
   		&lt;associatedKeywordList&gt;</xsl:text>
	
	<!-- browse the operators -->
	<xsl:for-each select="/doc/operators/operator"> 
		<xsl:variable name="operatorName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;operator_</xsl:text><xsl:value-of select="$operatorName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>

	<!-- browse the statements -->
	<xsl:for-each select="/doc/statements/statement"> 
		<xsl:variable name="statementName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;statement_</xsl:text><xsl:value-of select="$statementName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>

	<!-- browse the skills -->
	<xsl:for-each select="/doc/skills/skill"> 
		<xsl:variable name="skillName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;skill_</xsl:text><xsl:value-of select="$skillName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>

	<!-- browse the constants -->
	<xsl:for-each select="/doc/constants/constant"> 
		<xsl:variable name="constantName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;constant_</xsl:text><xsl:value-of select="$constantName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>

	<!-- browse the species -->
	<xsl:for-each select="/doc/speciess/species"> 
		<xsl:variable name="speciesName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;species_</xsl:text><xsl:value-of select="$speciesName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>

	<!-- browse the types -->
	<xsl:for-each select="/doc/types/type"> 
		<xsl:variable name="typeName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;type_</xsl:text><xsl:value-of select="$typeName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
	
	<!-- browse the architectures -->
	<xsl:for-each select="/doc/architectures/architecture"> 
		<xsl:variable name="typeName" select="@name"/>
		<xsl:for-each select="concepts/concept"> 
			<xsl:if test="$conceptName = @id">
				<xsl:text>
			&lt;associatedKeyword&gt;architecture_</xsl:text><xsl:value-of select="$typeName"/><xsl:text>&lt;/associatedKeyword&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>

	<xsl:text>
		&lt;/associatedKeywordList&gt;	
   	&lt;/keyword&gt; 	
   	</xsl:text>
</xsl:for-each>

<!-- ======================== operator ======================== -->

<xsl:for-each select="/doc/operators/operator"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'operator'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each>


<!-- ======================== statement ======================== -->

<xsl:for-each select="/doc/statements/statement"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'statement'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>	
</xsl:call-template>
</xsl:for-each>


<!-- ======================== facet ======================== -->


<xsl:for-each select="/doc/statements/statement"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="statName" select="@name"/>
	<xsl:for-each select="facets/facet"> 
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'facet'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="$statName"/>	
	<xsl:with-param name="insideEltConcept" select="'statement'"/>				
</xsl:call-template>
</xsl:for-each>

	</xsl:for-each>

 
<!-- ======================== architecture ======================== -->
 
<xsl:for-each select="/doc/architecturess/architecture"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'architecture'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each> 
 
 
<!-- ======================== skill ======================== -->
 
<xsl:for-each select="/doc/skills/skill"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'skill'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each> 

<!-- ======================== constant ======================== -->
 
<xsl:for-each select="/doc/constants/constant"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'constant'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each> 

<!-- ======================== action ( from Skills)  ======================== -->

<xsl:for-each select="/doc/skills/skill"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="skillName" select="@name"/>
	<xsl:for-each select="actions/action"> 
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'action'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="$skillName"/>	
	<xsl:with-param name="insideEltConcept" select="'skill'"/>				
</xsl:call-template>
</xsl:for-each>

	</xsl:for-each>

<!-- ======================== attribute ( from Skills) ======================== -->

<xsl:for-each select="/doc/skills/skill"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="skillName" select="@name"/>
	<xsl:for-each select="vars/var"> 
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'attribute'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="$skillName"/>	
	<xsl:with-param name="insideEltConcept" select="'skill'"/>				
</xsl:call-template>
</xsl:for-each>
</xsl:for-each>

<!-- ======================== species ======================== -->

<xsl:for-each select="/doc/speciess/species"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="speciesName" select="@name"/>
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'species'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each>

<!-- ======================== action ( from species) ======================== -->

<xsl:for-each select="/doc/speciess/species"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="skillName" select="@name"/>
	<xsl:for-each select="actions/action"> 
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'attribute'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="$skillName"/>	
	<xsl:with-param name="insideEltConcept" select="'skill'"/>				
</xsl:call-template>
</xsl:for-each>
</xsl:for-each>

<!-- ======================== attribute ( from species) ======================== -->

<xsl:for-each select="/doc/speciess/species"> 
		<xsl:sort select="@name" />
  		<xsl:variable name="speciesName" select="@name"/>
	<xsl:for-each select="vars/var"> 
	
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'attribute'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
	<xsl:with-param name="insideElt" select="$speciesName"/>	
	<xsl:with-param name="insideEltConcept" select="'species'"/>				
</xsl:call-template>
</xsl:for-each>

	</xsl:for-each>

<!-- ======================== type ======================== -->
 
<xsl:for-each select="/doc/types/type"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'type'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each> 

<!-- ======================== architecture ======================== -->
 
<xsl:for-each select="/doc/architectures/architecture"> 
			<xsl:sort select="@name" />
<xsl:call-template name="keyword">    
	<xsl:with-param name="category" select="'architecture'"/>
	<xsl:with-param name="nameGAMLElement" select="@name"/>
</xsl:call-template>
</xsl:for-each> 


</xsl:template>


 
 <xsl:template name="keyword">
   	<xsl:param name="category"/>
   	<xsl:param name="nameGAMLElement"/>
   	<xsl:param name="insideElt" select="''"/>
   	<xsl:param name="insideEltConcept" select="''"/>
   	<xsl:text disable-output-escaping="yes">
   	&lt;keyword id="</xsl:text><xsl:value-of select="$category"/><xsl:text>_</xsl:text><xsl:value-of select="$nameGAMLElement"/>
   		<xsl:if test="$insideElt != '' and $category != 'concept'"><xsl:text>_</xsl:text><xsl:value-of select="$insideEltConcept"/><xsl:text>_</xsl:text><xsl:value-of select="$insideElt"/></xsl:if>   	
   		<xsl:text>"&gt;
   		&lt;name&gt;</xsl:text><xsl:value-of select="$nameGAMLElement"/><xsl:text>&lt;/name&gt;
   		&lt;category&gt;</xsl:text><xsl:value-of select="$category"/><xsl:text>&lt;/category&gt;</xsl:text>   	
		<xsl:if test="$insideElt != ''"><xsl:text>
		&lt;associatedKeywordList&gt;
			&lt;associatedKeyword&gt;</xsl:text><xsl:value-of select="$insideEltConcept"/><xsl:text>_</xsl:text><xsl:value-of select="$insideElt"/><xsl:text>&lt;/associatedKeyword&gt;	
		&lt;/associatedKeywordList&gt;</xsl:text></xsl:if><xsl:text>		
   	&lt;/keyword&gt; 	
   	</xsl:text>
</xsl:template>
 
</xsl:stylesheet>
