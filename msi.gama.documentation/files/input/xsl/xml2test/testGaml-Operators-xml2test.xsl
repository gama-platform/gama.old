<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template match="/">
 	<xsl:text>/**
 *  Op</xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category </xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>.
 */

model </xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>

global {
	init {
		create test</xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text> number: 1;
		ask test</xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text> {do _step_;}
	}
}

entities {
	species test</xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text> {

	</xsl:text>

	<xsl:call-template name="buildOperatorsTest"/>

	<xsl:text>
	}
}

experiment test</xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>Exp type: gui {}	
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
	 <!-- <xsl:if test="@isExecutable = 'true'">  --> 	
		<xsl:choose>
			<xsl:when test="@equals">
			
				<xsl:choose>
				<xsl:when test="@var">
				<xsl:text>			</xsl:text>
			<xsl:if test="@isExecutable = 'false'">//</xsl:if>	<xsl:value-of select="@type"/><xsl:text> </xsl:text> <xsl:value-of select="@var"/> &lt;- <xsl:value-of select="@code"/>; 	// <xsl:value-of select="@var"/> equals <xsl:value-of select="@equals"/><xsl:if test="@test = 'false'"><xsl:text>
</xsl:text></xsl:if>
			<xsl:if test="@test = 'true'">
			assert var<xsl:value-of select="@index"/> equals: <xsl:value-of select="@equals"/>;<xsl:text> 
</xsl:text> </xsl:if>								
				</xsl:when>
				<xsl:otherwise>
				<xsl:text>			</xsl:text>
			<xsl:if test="@isExecutable = 'false'">//</xsl:if>	<xsl:value-of select="@type"/> var<xsl:value-of select="@index"/> &lt;- <xsl:value-of select="@code"/>; 	// var<xsl:value-of select="@index"/> equals <xsl:value-of select="@equals"/><xsl:if test="@test = 'false'"><xsl:text>
</xsl:text></xsl:if>
			<xsl:if test="@test = 'true'">
			assert var<xsl:value-of select="@index"/> equals: <xsl:value-of select="@equals"/>;<xsl:text> 
</xsl:text> </xsl:if>					
				</xsl:otherwise>
				
				</xsl:choose>

			</xsl:when>
			<xsl:when test="@raises">
			assert <xsl:value-of select="@code"/> raises: "<xsl:value-of select="@raises"/>";<xsl:text> 
</xsl:text>			
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>			</xsl:text>
				<xsl:if test="@isExecutable = 'false'">//</xsl:if>	<xsl:value-of select="@code"/><xsl:text>
</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	<!-- </xsl:if>  -->	
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
