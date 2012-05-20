<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:variable name="html_menu" select="''"/>
<xsl:variable name="html_doc" select="''"/>
<xsl:variable name="html_iterator" select="0"/>



<xsl:template match="/">
 	<xsl:text>#summary Operators(Modeling Guide)
 	</xsl:text>
<BR />
<xsl:text>
= </xsl:text> <font color="blue"> Table of Contents </font> =

<wiki:toc max_depth="3" />
<br/>
= <font color="blue">Definition</font> =

<xsl:text>
An operator performs a function on one, two, or three operands. An operator that only requires one operand is called a unary operator. An operator that requires two operands is a binary operator. And finally, a ternary operator is one that requires three operands. The GAML programming language has only one ternary operator, ?:, which is a short-hand if-else statement.
</xsl:text>

= <font color="blue">Operators</font> =
<xsl:call-template name="buildOperators"/>
    
</xsl:template>
 
    
 <xsl:template name="buildOperators"> 
    <xsl:for-each select="doc/operators/operator">
    	<xsl:sort select="@name" />
    
== <xsl:value-of select="@name" /> == 
<xsl:call-template name="buildOperands"/>
  * Special cases:<xsl:for-each select="documentation/specialCases/case">
    * <xsl:value-of select="@item"/> </xsl:for-each>
  * Comment: <xsl:value-of select="documentation/comment"/>
  * See also: <xsl:for-each select="documentation/seeAlso/see">[#<xsl:value-of select="@id"/><xsl:text> </xsl:text><xsl:value-of select="@id"/>] </xsl:for-each>
  {{{  
<xsl:for-each select="documentation/examples/example" >
   <xsl:text>   </xsl:text><xsl:value-of select="@code"/><xsl:text>
</xsl:text>
</xsl:for-each> }}} 

[#Table_of_Contents Top of the page] 

  	</xsl:for-each>
 </xsl:template>   
 
<xsl:template name="buildOperands">
<xsl:choose>
<xsl:when test="count(combinaisonIO/operands) = 1">
	<xsl:for-each select="combinaisonIO/operands"> <xsl:call-template name="buildOperand"/>	</xsl:for-each>
</xsl:when>
<xsl:otherwise>
	<xsl:for-each select="combinaisonIO/operands">
  * Possible use: <xsl:call-template name="buildOperand2"/>
	</xsl:for-each>
</xsl:otherwise>
</xsl:choose>
</xsl:template> 
 
<xsl:template name="buildOperand">
	<xsl:choose>
	<xsl:when test="count(operand) = 1">
  * Operand: <xsl:value-of select="operand/@type"/>
  * Result Type: <xsl:value-of select="@returnType"/>
	</xsl:when>
	<xsl:otherwise>
  * Left operand: <xsl:value-of select="operand/@type"/>
  * Right operand: <xsl:value-of select="operand/@type"/>  
  * Result Type: <xsl:value-of select="@returnType"/>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template> 

<xsl:template name="buildOperand2">
	<xsl:choose>
	<xsl:when test="count(operand) = 1">
    * Operand: <xsl:value-of select="operand/@type"/>
    * Result Type: <xsl:value-of select="@returnType"/>
	</xsl:when>
	<xsl:otherwise>
    * Left operand: <xsl:value-of select="operand/@type"/>
    * Right operand: <xsl:value-of select="operand/@type"/>  
    * Result Type: <xsl:value-of select="@returnType"/>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>


<!-- == is_properties ==
    * Operand: a string.
    * Result: the operator tests whether the operand represents the name of a supported properties file   
    * Return type: bool.
    * Comment: cf. [Types14 Types14] for supported (espacially properties) file extensions.
    * see also: [#is_text is_text], [#is_image is_image], [#is_shape is_shape].

{{{
is_properties("../includes/Stupid_Cell.Data")    →  false;
is_properties("../includes/test.png")            →  false; 
is_properties("../includes/test.properties")     →  true;
is_properties("../includes/test.shp")            →  false;    
}}}
[#Table_of_Contents Top of the page]  -->