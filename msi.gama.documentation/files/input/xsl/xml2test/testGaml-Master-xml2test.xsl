<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template match="/">
 	<xsl:text>/**
 *  masterTest.gaml
 *  Author: automatic generator
 *  Description: the model to run all the tests.
 */

model masterTest

</xsl:text>
    <xsl:for-each select="doc/operatorsCategories/category">
    	<xsl:text>import "operatorsTest/Op</xsl:text><xsl:value-of select="@id"/><xsl:text>Test.gaml"
</xsl:text>
	</xsl:for-each>
	<xsl:text>
import "statementsTest/StatementsTest.gaml"	

global {}

experiment masterExp type: gui {}	
	</xsl:text>

</xsl:template>


</xsl:stylesheet>
