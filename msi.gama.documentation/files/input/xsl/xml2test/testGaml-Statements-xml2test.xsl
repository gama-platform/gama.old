<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="testGaml-template-generateTestFromExample-xml2test.xsl"/>
<xsl:import href="testGaml-template-checkName-xml2test.xsl"/>

<xsl:template match="/">
 	<xsl:text>/**
 *  StatementsTest
 *  Author: automatic generator
 *  Description: Unity Test of all statements.
 */

model StatementsTest

global {
	init {
		create testStatements number: 1;
		ask testStatements {do _step_;}
	}
}

entities {
	species testStatements {

	</xsl:text>

	<xsl:call-template name="buildStatementsTest"/>

	<xsl:text>
	}
}

experiment testStatementsExp type: gui {}	
	</xsl:text>

</xsl:template>

 <xsl:template name="buildStatementsTest"> 
    <xsl:for-each select="doc/statements/statement">
    	<xsl:sort select="@name" />
		test <xsl:call-template name="checkName"/>Statement {
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
