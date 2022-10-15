<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

	<xsl:template match="/">
		<xsl:text>
Prism.languages.gaml = {
	'comment': [
		{ pattern: /(^|[^\\])\/\*[\s\S]*?(?:\*\/|$)/, lookbehind: !0, greedy: !0 }, 
		{ pattern: /(^|[^\\:])\/\/.*/, lookbehind: !0, greedy: !0 }],
	'string': 
		{ pattern: /(["'])(?:\\(?:\r\n|[\s\S])|(?!\1)[^\\\r\n])*\1/, greedy: !0 },
	'number': [
		{
			pattern: /\b\d(?:_?\d)*#[\dA-F](?:_?[\dA-F])*(?:\.[\dA-F](?:_?[\dA-F])*)?#(?:E[+-]?\d(?:_?\d)*)?/i
		},
		{
			pattern: /\b\d(?:_?\d)*(?:\.\d(?:_?\d)*)?(?:E[+-]?\d(?:_?\d)*)?\b/i
		}],
</xsl:text>
<xsl:text>	'type_statement': /\b(?:string\s</xsl:text>
		<xsl:for-each select="/doc/types/type">
			<xsl:sort select="@name" />
			<xsl:text>\s|</xsl:text><xsl:value-of select="@name"/>
		</xsl:for-each>
<xsl:text>)(?!(\(|\_))/i,
	'boolean': /\b(?:false|true)\b/i,
</xsl:text>

<xsl:text>	'statement': /\b(?:model|import</xsl:text>  	
		<xsl:for-each select="/doc/statements/statement">
			<xsl:sort select="@name" />
			<xsl:text>|</xsl:text><xsl:value-of select="@name"/>
		</xsl:for-each>
<xsl:text>)\b/i,
</xsl:text>

<xsl:text>	'facet': /\b(?:name</xsl:text>  	
		<xsl:for-each select="/doc/statements/statement">
			<xsl:for-each select="facets/facet">	
				<xsl:text>|</xsl:text><xsl:value-of select="@name"/>			
			</xsl:for-each>		
		</xsl:for-each>
<xsl:text>):/i,
</xsl:text>

<xsl:text>	'operator': /\b(?:-</xsl:text>  	
		<xsl:for-each select="/doc/operators/operator">
			<xsl:sort select="@name" />		
			<xsl:text>|</xsl:text><xsl:call-template name="checkOperator"><xsl:with-param name="name"><xsl:value-of select="@name"/></xsl:with-param></xsl:call-template> 
		</xsl:for-each>
<xsl:text>)\b/i,
</xsl:text>

<xsl:text>
	'keyword':/\b(?:nil|each|self|myself)\b/,
	'constant':/#[A-Za-z]+/i,
	'punctuation': /\.\.?|[,;():]/,
	'variableDefinition': /\b(?&lt;=string</xsl:text>
		<xsl:for-each select="/doc/types/type">
			<xsl:sort select="@name" />
			<xsl:text>|</xsl:text><xsl:value-of select="@name"/>
		</xsl:for-each>
<xsl:text>)\s*[a-z]\w*/i,</xsl:text>
	
<xsl:text>	
	'variable': /\b[a-z](?:\w)*\b/i
};
</xsl:text>

	</xsl:template>


 <xsl:template name="checkOperator">
  	<xsl:param name="name"/>
	<xsl:choose> 
 		<xsl:when test="$name = '*'"><xsl:text>\*</xsl:text></xsl:when>
  		<xsl:when test="$name = '+'"><xsl:text>\+</xsl:text></xsl:when>		
    	<xsl:when test="$name = '^'"><xsl:text>\^</xsl:text></xsl:when>		
    	<xsl:when test="$name = '.'"><xsl:text>\.</xsl:text></xsl:when>		
     	<xsl:when test="$name = '?'"><xsl:text>\?</xsl:text></xsl:when>		
     	<xsl:when test="$name = '/'"><xsl:text>\/</xsl:text></xsl:when>		
 		<xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
 	</xsl:choose> 
 </xsl:template>

</xsl:stylesheet>
