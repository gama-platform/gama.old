<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">
<xsl:import href="../xml2md/docGama-utils-xml2md.xsl" />

<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

<xsl:variable name="fileOperatorsAA" select="'OperatorsAA'" />
<xsl:variable name="fileOperatorsBC" select="'OperatorsBC'" />
<xsl:variable name="fileOperatorsDH" select="'OperatorsDH'" />
<xsl:variable name="fileOperatorsIM" select="'OperatorsIM'" />
<xsl:variable name="fileOperatorsNR" select="'OperatorsNR'" />
<xsl:variable name="fileOperatorsSZ" select="'OperatorsSZ'" />
<xsl:variable name="fileStatements" select="'Statements'" />
<xsl:variable name="fileUnitsConstants" select="'UnitsAndConstants'" />
<xsl:variable name="fileControl" select="'BuiltInArchitectures'" />
<xsl:variable name="fileArchi" select="'BuiltInArchitectures'" />
<xsl:variable name="fileSpecies" select="'BuiltInSpecies'" />
<xsl:variable name="fileSkills" select="'BuiltInSkills'" />
<xsl:variable name="fileTypes" select="'DataTypes'" />

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
<xsl:text>
</xsl:text>
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
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/architectures/architecture">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Architectures", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:value-of select="$fileArchi" />
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>

<!-- Constants and colors -->
<!-- { "tag": ["constant", "colors"], "title" : " -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/constants/constant">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Constant and Colors", "title" : [ "</xsl:text>
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
<xsl:text>
</xsl:text>
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
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/speciess/species">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Built-in Species", "title" : "</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>", "url": "</xsl:text>
	<xsl:value-of select="$fileSpecies" />
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@name" />
	<xsl:text>" }, </xsl:text>
</xsl:for-each>
{"tag": "species", "title": "world", "url": "BuiltInSpecies#model"},

<!-- Actions -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/speciess/species">
	<xsl:sort select="@name" />
	<xsl:for-each select="actions/action">
		<xsl:text>{ "tag": "Actions of species", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSpecies" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/skills/skill">
	<xsl:sort select="@name" />
	<xsl:for-each select="actions/action">
		<xsl:text>{ "tag": "Actions of skills", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSkills" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<!-- Variables of skills -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/skills/skill">
	<xsl:sort select="@name" />
	<xsl:variable name="skillName" select="@name"/>
	<xsl:for-each select="vars/var">
		<xsl:text>{ "tag": "Variables of skills", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSkills" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="$skillName" />		
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<!-- Variables of architectures -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/architectures/architecture">
	<xsl:sort select="@name" />
	<xsl:variable name="archiName" select="@name"/>	
	<xsl:for-each select="vars/var">
		<xsl:text>{ "tag": "Variables of architecture", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileControl" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="$archiName" />		
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<!-- Variables of species -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/speciess/species">
	<xsl:sort select="@name" />
	<xsl:variable name="speciesName" select="@name"/>	
	<xsl:for-each select="vars/var">
		<xsl:text>{ "tag": "Variables of species", "title" : "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text>
		<xsl:value-of select="$fileSpecies" />
		<xsl:text>#</xsl:text>
		<xsl:value-of select="$speciesName" />		
		<xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>

<!-- Pseudo-Variables -->

{"tag":"Pseudo-Variables","title":"self","url":"PseudoVariables#self"},{"tag":"Pseudo-Variables","title":"myself","url":"PseudoVariables#myself"},{"tag":"Pseudo-Variables","title":"each","url":"PseudoVariables#each"},

<!-- Types -->
<xsl:for-each select="/doc/types/type">
	<xsl:sort select="@name" />
	<xsl:text>{ "tag": "Types", "title" : "</xsl:text>
	<xsl:value-of select="@name" /> <xsl:text>", "url": "</xsl:text><xsl:value-of select="$fileTypes"/><xsl:text>#</xsl:text>
	<xsl:value-of select="@name" /><xsl:text>" }, </xsl:text>
</xsl:for-each>

<!-- Concepts -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/conceptList/concept[not(@id = (preceding-sibling::*/@id))]">
	<xsl:sort select="@id" />
	<xsl:variable name="conceptName" select="@id"/>
<!-- Concepts for operators-->
	<xsl:for-each select="/doc/operators/operator"> 
	<!--  [not(@id = (preceding-sibling::*/@id))]  -->
		<xsl:sort select="@name" />
		<xsl:variable name="nameOp" select="@name"/>
		<xsl:variable name="alphabet" select="@alphabetOrder"/>						
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptOperator" select="@id"/>
			<xsl:if test="$conceptOperator = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text><xsl:value-of select="$conceptOperator" />
				<xsl:text>", "subcat": "Operator", "title" : "</xsl:text><xsl:value-of select="$nameOp" />
				<xsl:text>", "url": "</xsl:text>
				<xsl:choose>
					<xsl:when test="$alphabet = 'aa'">
						<xsl:value-of select="$fileOperatorsAA" />
					</xsl:when>
					<xsl:when test="$alphabet = 'bc'">
						<xsl:value-of select="$fileOperatorsBC" />
					</xsl:when>
					<xsl:when test="$alphabet = 'dh'">
						<xsl:value-of select="$fileOperatorsDH" />
					</xsl:when>
					<xsl:when test="$alphabet = 'im'">
						<xsl:value-of select="$fileOperatorsIM" />
					</xsl:when>
					<xsl:when test="$alphabet = 'nr'">
						<xsl:value-of select="$fileOperatorsNR" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$fileOperatorsSZ" />
					</xsl:otherwise>
				</xsl:choose><xsl:text>#</xsl:text><xsl:value-of select="translate($nameOp, $uppercase, $smallcase)"/><xsl:text>" }, 
				</xsl:text> 	
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
<!-- Concepts for statements-->
	<xsl:for-each select="/doc/statements/statement"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameStatement" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptStatement" select="@id"/>
			<xsl:if test="$conceptStatement = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Statement", "title" : "</xsl:text> <xsl:value-of select="$nameStatement" />
				<xsl:text>", "url": "</xsl:text> <xsl:value-of select="$fileStatements" /><xsl:text>#</xsl:text><xsl:value-of select="translate($nameStatement, $uppercase, $smallcase)" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
<!-- Concepts for constants-->
	<xsl:for-each select="/doc/constants/constant"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameConstant" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptConstant" select="@id"/>
			<xsl:if test="$conceptConstant = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Constant", "title": "</xsl:text> <xsl:value-of select="$nameConstant" />
				<xsl:text>", "url": "</xsl:text> <xsl:value-of select="$fileUnitsConstants" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
<!-- Concepts for skills-->
	<xsl:for-each select="/doc/skills/skill"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameSkill" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptSkill" select="@id"/>
			<xsl:if test="$conceptSkill = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Skill", "title": "</xsl:text> <xsl:value-of select="$nameSkill" />
				<xsl:text>", "url": "</xsl:text> <xsl:value-of select="$fileSkills" /><xsl:text>#</xsl:text><xsl:value-of select="$nameSkill" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>	
<!-- Concepts for architectures-->
	<xsl:for-each select="/doc/architectures/architecture"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameArchi" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptArchi" select="@id"/>
			<xsl:if test="$conceptArchi = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Architecture", "title": "</xsl:text> <xsl:value-of select="$nameArchi" />
				<xsl:text>", "url": "</xsl:text><xsl:value-of select="$fileArchi" /><xsl:text>#</xsl:text><xsl:value-of select="$nameArchi" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>	
<!-- Concepts for species-->
	<xsl:for-each select="/doc/speciess/species"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameSpecies" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptSpecies" select="@id"/>
			<xsl:if test="$conceptSpecies = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Built-In Species", "title": "</xsl:text> <xsl:value-of select="$nameSpecies" />
				<xsl:text>", "url": "</xsl:text><xsl:value-of select="$fileSpecies" /><xsl:text>#</xsl:text><xsl:value-of select="$nameSpecies" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>	
<!-- Concepts for file-->
<!-- <xsl:for-each select="/doc/files/file"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameFile" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptFile" select="@id"/>
			<xsl:if test="$conceptFile = $conceptName "> 
				<xsl:text>{ "tag": "</xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Type", "title": "</xsl:text> <xsl:value-of select="$nameFile" />
				<xsl:text>", "url": "</xsl:text><xsl:value-of select="$fileFile" /><xsl:text>#</xsl:text><xsl:value-of select="$nameSpecies" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>		-->
<!-- Concepts for types-->
	<xsl:for-each select="/doc/types/type"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameType" select="@name"/>
		<xsl:for-each select="concepts/concept">
			<xsl:variable name="conceptTytpe" select="@id"/>
			<xsl:if test="$conceptTytpe = $conceptName "> 
				<xsl:text>{ "tag": "Concept </xsl:text> <xsl:value-of select="$conceptName" />
				<xsl:text>", "subcat": "Type", "title": "</xsl:text> <xsl:value-of select="$nameType" />
				<xsl:text>", "url": "</xsl:text><xsl:value-of select="$fileTypes" /><xsl:text>#</xsl:text><xsl:value-of select="$nameType" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>		
</xsl:for-each>

<!-- Categories for operators -->
<!-- <xsl:for-each select="/doc/operatorsCategories/category[not(@id = (preceding-sibling::*/@id))]">
	<xsl:sort select="@id" />
	<xsl:variable name="catName" select="@id"/>
	<xsl:for-each select="/doc/operators/operator"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameOp" select="@name"/>
		<xsl:variable name="alphabet" select="@alphabetOrder"/>						
		<xsl:for-each select="operatorCategories/category">
			<xsl:variable name="catOperator" select="@id"/>
			<xsl:if test="$catOperator = $catName "> 
				<xsl:text>{ "tag": "Category </xsl:text><xsl:value-of select="$catOperator" />
				<xsl:text>", "subcat": "Operator", "title" : "</xsl:text><xsl:value-of select="$nameOp" />
				<xsl:text>", "url": "</xsl:text>
				<xsl:choose>
					<xsl:when test="$alphabet = 'aa'">
						<xsl:value-of select="$fileOperatorsAA" />
					</xsl:when>
					<xsl:when test="$alphabet = 'bc'">
						<xsl:value-of select="$fileOperatorsBC" />
					</xsl:when>
					<xsl:when test="$alphabet = 'dh'">
						<xsl:value-of select="$fileOperatorsDH" />
					</xsl:when>
					<xsl:when test="$alphabet = 'im'">
						<xsl:value-of select="$fileOperatorsIM" />
					</xsl:when>
					<xsl:when test="$alphabet = 'nr'">
						<xsl:value-of select="$fileOperatorsNR" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$fileOperatorsSZ" />
					</xsl:otherwise>
				</xsl:choose><xsl:text>#</xsl:text><xsl:value-of select="translate($nameOp, $uppercase, $smallcase)"/><xsl:text>" }, 
				</xsl:text> 	
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
</xsl:for-each>
-->
 
<!-- Categories for constants -->
<xsl:for-each select="/doc/constantsCategories/category[not(@id = (preceding-sibling::*/@id))]">
	<xsl:sort select="@id" />
	<xsl:variable name="catName" select="@id"/>

	<xsl:for-each select="/doc/constants/constant"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameConstant" select="@name"/>
		<xsl:for-each select="categories/category">
			<xsl:variable name="catConstant" select="@id"/>
			<xsl:if test="$catConstant = $catName "> 
				<xsl:text>{ "tag": "Category </xsl:text> <xsl:value-of select="$catName" />
				<xsl:text>", "subcat": "Constant", "title": "</xsl:text> <xsl:value-of select="$nameConstant" />
				<xsl:text>", "url": "</xsl:text> <xsl:value-of select="$fileUnitsConstants" /><xsl:text>" }, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:for-each>
</xsl:for-each>

<!-- Facets -->
<xsl:text>
</xsl:text>
<xsl:for-each select="/doc/statements/statement[not(@id = (preceding-sibling::*/@id))]">
	<xsl:sort select="@id" />
	<xsl:variable name="statName" select="@id"/>
	<xsl:for-each select="facets/facet"> 
		<xsl:sort select="@name" />
		<xsl:variable name="nameConstant" select="@name"/>
		<xsl:text>{ "tag": "Facet", "subcat": "</xsl:text><xsl:value-of select="$statName"/>
		<xsl:text>", "title": "</xsl:text> <xsl:value-of select="@name" />
		<xsl:text>", "url": "</xsl:text><xsl:value-of select="$fileStatements" /><xsl:text>#</xsl:text><xsl:value-of select="$statName" /><xsl:text>" }, </xsl:text>
	</xsl:for-each>
</xsl:for-each>


<!-- Global Species -->

{"tag":"Global Species","title":"the world","url":"GlobalSpecies"},{"tag":"Global Species","title":"torus","url":"GlobalSpecies#torus"},{"tag":"Global Species","title":"Environment Size","url":"GlobalSpecies#Environment_Size"},{"tag":"Global Species","title":"world","url":"GlobalSpecies#world"},{"tag":"Global Species","title":"time","url":"GlobalSpecies#time"},{"tag":"Global Species","title":"cycle","url":"GlobalSpecies#cycle"},{"tag":"Global Species","title":"step","url":"GlobalSpecies#step"},{"tag":"Global Species","title":"duration","url":"GlobalSpecies#duration"},{"tag":"Global Species","title":"total_duration","url":"GlobalSpecies#total_duration"},{"tag":"Global Species","title":"average_duration","url":"GlobalSpecies#average_duration"},{"tag":"Global Species","title":"machine_time","url":"GlobalSpecies#machine_time"},{"tag":"Global Species","title":"agents","url":"GlobalSpecies#agents"},{"tag":"Global Species","title":"stop","url":"GlobalSpecies#halt"},{"tag":"Global Species","title":"halt","url":"GlobalSpecies#halt"},{"tag":"Global Species","title":"pause","url":"GlobalSpecies#pause"},{"tag":"Global Species","title":"scheduling","url":"GlobalSpecies#scheduling"},

<!-- Grid -->

{"tag":"Grid","title":"Grid Species","url":"GridSpecies"},{"tag":"Grid","title":"grid_x","url":"GridSpecies#grid-_x"},{"tag":"Grid","title":"grid_y","url":"GridSpecies#grid-_y"},{"tag":"Grid","title":"agents","url":"GridSpecies#agents"},{"tag":"Grid","title":"color","url":"GridSpecies#color"},{"tag":"Grid","title":"grid_value","url":"GridSpecies#grid-_value"},

{"tag":"Graph","title":"Graph Species","url":"GraphSpecies"},
{"tag":"Mirror","title":"Mirror Species","url":"MirrorSpecies"},

<!-- Other concepts -->

{"tag":"Other concepts","title":"scheduling","url":"RuntimeConcepts#Scheduling_of_Agents"},{"tag":"Other concepts","title":"step","url":"RuntimeConcepts#Agents_Step"},{"tag":"Other concepts","title":"Key concepts","url":"KeyConcepts"},{"tag":"Other concepts","title":"Operators statements type species","url":"KeyConcepts#Translation_into_a_concrete_syntax"}

]

</xsl:template>


</xsl:stylesheet>
