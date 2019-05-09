<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="docGama-utils-xml2md.xsl" />


<xsl:variable name="fileOperatorsAA" select="'OperatorsAA'" />
<xsl:variable name="fileOperatorsBC" select="'OperatorsBC'" />
<xsl:variable name="fileOperatorsDH" select="'OperatorsDH'" />
<xsl:variable name="fileOperatorsIM" select="'OperatorsIM'" />
<xsl:variable name="fileOperatorsNR" select="'OperatorsNR'" />
<xsl:variable name="fileOperatorsSZ" select="'OperatorsSZ'" />
<xsl:variable name="fileStatements" select="'Statements'" />
<xsl:variable name="fileUnitsConstants" select="'UnitsAndConstants'" />
<xsl:variable name="fileControl" select="'BuiltInControlArchitectures'" />
<xsl:variable name="fileSpecies" select="'BuiltInSpecies'" />
<xsl:variable name="fileSkills" select="'BuiltInSkills'" />

<!--

	{
		"tag": "",
		"title": "",
		"url": "#"
	}

-->

<xsl:template match="/">

[

<!-- Operators -->
<xsl:for-each select="/doc/operators/operator">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Operators", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:choose>
		<xsl:when test="@alphabetOrder = 'aa'">
			<xsl:value-of select="$fileOperatorsAA" />
		</xsl:when>
		<xsl:when test="@alphabetOrder = 'bc'">
			<xsl:value-of select="$fileOperatorsBC" />
		</xsl:when>
		<xsl:when test="@alphabetOrder = 'dh'">
			<xsl:value-of select="$fileOperatorsDH" />
		</xsl:when>
		<xsl:when test="@alphabetOrder = 'im'">
			<xsl:value-of select="$fileOperatorsIM" />
		</xsl:when>
		<xsl:when test="@alphabetOrder = 'nr'">
			<xsl:value-of select="$fileOperatorsNR" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$fileOperatorsSZ" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>


<!-- Statements -->
<xsl:for-each select="/doc/statements/statement">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Statements", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:value-of select="$fileStatements" />
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>

<!-- Architectures -->
<xsl:for-each select="/doc/architectures/architecture">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Architectures", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:value-of select="$fileControl" />
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>

<!-- Constants and colors -->
<!-- { "tag": ["constant", "colors"], "title" : " -->
<xsl:for-each select="/doc/constants/constant">
	<xsl:sort select="@name" />
	<xsl:text>"tag": "Constant and Colors", "title" : [ "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" </xsl:text>
	<xsl:if test="@altNames">
		<xsl:text>, "</xsl:text>
		<xsl:value-of select="@altNames" />
		<xsl:text>" </xsl:text>
	</xsl:if>
		<xsl:text> ], "url": " </xsl:text>
	<xsl:value-of select="$fileUnitsConstants" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>

<!-- Skills -->
<xsl:for-each select="/doc/skills/skill">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Skills", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:value-of select="$fileSkills" />
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>

<!-- Species -->
<xsl:for-each select="/doc/speciess/species">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Species", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:value-of select="$fileSpecies" />
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>
{
	"tag": "species",
	"title": "world",
	"url": "BuiltInSpecies#model"
},

<!-- Actions -->
<xsl:for-each select="/doc/speciess/species">
	<xsl:sort select="@name" />
	<xsl:for-each select="actions/action">
		<xsl:text>{ "tag": "Actions", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSpecies" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<xsl:for-each select="/doc/skills/skill">
	<xsl:sort select="@name" />
	<xsl:for-each select="actions/action">
		<xsl:text>{ "tag": "Actions", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSkills" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<!-- Variables -->
<xsl:for-each select="/doc/skills/skill">
	<xsl:sort select="@name" />
	<xsl:for-each select="vars/var">
		<xsl:text>{ "tag": "Variables", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSkills" />
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<!-- Pseudo-Variables -->

{"tag":"Pseudo-Variables","title":"self","url":"PseudoVariables#self"},{"tag":"Pseudo-Variables","title":"myself","url":"PseudoVariables#myself"},{"tag":"Pseudo-Variables","title":"each","url":"PseudoVariables#each"},

<!-- Types -->

{"tag":"Types","title":"bool","url":"DataTypes#bool"},{"tag":"Types","title":"float","url":"DataTypes#float"},{"tag":"Types","title":"string","url":"DataTypes#string"},{"tag":"Types","title":"agent","url":"DataTypes#agent"},{"tag":"Types","title":"container","url":"DataTypes#container"},{"tag":"Types","title":"file","url":"DataTypes#file"},{"tag":"Types","title":"geometry","url":"DataTypes#geometry"},{"tag":"Types","title":"graph","url":"DataTypes#graph"},{"tag":"Types","title":"list","url":"DataTypes#list"},{"tag":"Types","title":"map","url":"DataTypes#map"},{"tag":"Types","title":"matrix","url":"DataTypes#matrix"},{"tag":"Types","title":"pair","url":"DataTypes#pair"},{"tag":"Types","title":"path","url":"DataTypes#path"},{"tag":"Types","title":"point","url":"DataTypes#point"},{"tag":"Types","title":"rgb","url":"DataTypes#rgb"},{"tag":"Types","title":"color","url":"DataTypes#color"},{"tag":"Types","title":"species","url":"DataTypes#species"},{"tag":"Types","title":"topology","url":"DataTypes#topology"},

<!-- Global Species -->

{"tag":"Global Species","title":"the world","url":"GlobalSpecies"},{"tag":"Global Species","title":"torus","url":"GlobalSpecies#torus"},{"tag":"Global Species","title":"Environment Size","url":"GlobalSpecies#Environment_Size"},{"tag":"Global Species","title":"world","url":"GlobalSpecies#world"},{"tag":"Global Species","title":"time","url":"GlobalSpecies#time"},{"tag":"Global Species","title":"cycle","url":"GlobalSpecies#cycle"},{"tag":"Global Species","title":"step","url":"GlobalSpecies#step"},{"tag":"Global Species","title":"duration","url":"GlobalSpecies#duration"},{"tag":"Global Species","title":"total_duration","url":"GlobalSpecies#total_duration"},{"tag":"Global Species","title":"average_duration","url":"GlobalSpecies#average_duration"},{"tag":"Global Species","title":"machine_time","url":"GlobalSpecies#machine_time"},{"tag":"Global Species","title":"agents","url":"GlobalSpecies#agents"},{"tag":"Global Species","title":"stop","url":"GlobalSpecies#halt"},{"tag":"Global Species","title":"halt","url":"GlobalSpecies#halt"},{"tag":"Global Species","title":"pause","url":"GlobalSpecies#pause"},{"tag":"Global Species","title":"scheduling","url":"GlobalSpecies#scheduling"},

<!-- Grid -->

{"tag":"Grid","title":"grid_x","url":"GridSpecies#grid_x"},{"tag":"Grid","title":"grid_y","url":"GridSpecies#grid_y"},{"tag":"Grid","title":"agents","url":"GridSpecies#agents"},{"tag":"Grid","title":"color","url":"GridSpecies#color"},{"tag":"Grid","title":"grid_value","url":"GridSpecies#grid_value"},

<!-- Other concepts -->

{"tag":"Other concepts","title":"scheduling","url":"RuntimeConcepts#Scheduling_of_Agents"},{"tag":"Other concepts","title":"step","url":"RuntimeConcepts#Agents_Step"},{"tag":"Other concepts","title":"Key concepts","url":"KeyConcepts"},{"tag":"Other concepts","title":"Operators statements type species","url":"KeyConcepts#Translation_into_a_concrete_syntax"}

]

</xsl:template>


</xsl:stylesheet>
