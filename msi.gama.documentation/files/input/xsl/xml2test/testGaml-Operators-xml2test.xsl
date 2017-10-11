<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="testGaml-template-generateTestFromExample-xml2test.xsl"/>
<xsl:import href="testGaml-template-checkName-xml2test.xsl"/>

<xsl:template match="/">
 	<xsl:text>/**
 *  Op</xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category </xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>.
 */

experiment </xsl:text><xsl:value-of select="doc/@fileName"/><xsl:text>Tests type: test {	

	</xsl:text>

	<xsl:call-template name="buildOperatorsTest"/>

	<xsl:text>
}

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

</xsl:stylesheet>
