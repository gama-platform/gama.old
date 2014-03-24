<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template match="/">
 	<xsl:text>/**
 *  Op</xsl:text><xsl:value-of select="doc/operators/operator/@category"/><xsl:text>Test.gaml
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category </xsl:text><xsl:value-of select="doc/operators/operator/@category"/><xsl:text>.
 */

model Op</xsl:text><xsl:value-of select="doc/operators/operator/operatorCategories/category/@id"/><xsl:text>Test

global {
	init {
		create testOp</xsl:text><xsl:value-of select="doc/operators/operator/operatorCategories/category/@id"/><xsl:text>Test number: 1;
	}
}

entities {
	species testOp</xsl:text><xsl:value-of select="doc/operators/operator/operatorCategories/category/@id"/><xsl:text>Test {

	</xsl:text>

	<xsl:call-template name="buildOperatorsTest"/>

	<xsl:text>
	}
}

experiment testOp</xsl:text><xsl:value-of select="doc/operators/operator/operatorCategories/category/@id"/><xsl:text>Exp type: gui {}	
	</xsl:text>

</xsl:template>

 <xsl:template name="buildOperatorsTest"> 
    <xsl:for-each select="doc/operators/operator">
    	<xsl:sort select="@name" />
		test <xsl:call-template name="checkName"/>Op {
<xsl:for-each select="documentation/usages/usage" >  
<xsl:call-template name="generateTestFromExample"/>  
</xsl:for-each>
<xsl:for-each select="documentation/usagesExamples/usage" >  
<xsl:call-template name="generateTestFromExample"/>  
</xsl:for-each>
		}
	</xsl:for-each>
</xsl:template> 
 
<xsl:template name="generateTestFromExample">
	<xsl:for-each select="examples/example">
	  	<xsl:if test="@isExecutable = 'true'">
		<xsl:choose>
			<xsl:when test="@equals">
				<xsl:text>			</xsl:text>
				<xsl:value-of select="@type"/> var<xsl:value-of select="@index"/> &lt;- <xsl:value-of select="@code"/>; 	// var<xsl:value-of select="@index"/> equals <xsl:value-of select="@equals"/>
			assert var<xsl:value-of select="@index"/> equals: <xsl:value-of select="@equals"/>;<xsl:text>
</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>			</xsl:text>
				<xsl:value-of select="@code"/><xsl:text>
</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:if>
	</xsl:for-each>
</xsl:template>		
 
 <xsl:template name="checkName">
 	<xsl:choose>
 		<xsl:when test="@name = '*'"><xsl:text>`*`</xsl:text></xsl:when>
 		<xsl:when test="@name = '**'"><xsl:text>`**`</xsl:text></xsl:when>
 		<xsl:when test="@name = '&lt;-&gt;'"><xsl:text>`&lt;-&gt;`</xsl:text></xsl:when> 		
 		<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
 	</xsl:choose>
 </xsl:template>
</xsl:stylesheet>
