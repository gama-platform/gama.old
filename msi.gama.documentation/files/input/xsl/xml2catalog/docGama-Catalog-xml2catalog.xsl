<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

	<xsl:template match="/">
		<xsl:for-each select="/doc/operators/operator">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, operator, pluginTOCHANGE
		</xsl:for-each>

		<xsl:for-each select="/doc/statements/statement">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, statement, pluginTOCHANGE
		</xsl:for-each>
	
		<xsl:for-each select="/doc/architectures/architecture">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, operator, pluginTOCHANGE
		</xsl:for-each>

		<xsl:for-each select="/doc/constants/constant">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, constant, pluginTOCHANGE
		</xsl:for-each>

		<xsl:for-each select="/doc/skills/skill">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, skill, pluginTOCHANGE
		</xsl:for-each>

		<xsl:for-each select="/doc/speciess/species">
			<xsl:sort select="@name" />
			<xsl:value-of select="@name"/>, species, pluginTOCHANGE
		</xsl:for-each>
		
		== Actions ==
		<xsl:for-each select="/doc/speciess/species">
			<xsl:sort select="@name" />
			<xsl:for-each select="actions/action">
				<xsl:sort select="@name" />
				<xsl:value-of select="@name"/>, actionSpecies, pluginTOCHANGE
			</xsl:for-each>
		</xsl:for-each>

		<xsl:for-each select="/doc/skills/skill">
			<xsl:sort select="@name" />
			<xsl:for-each select="actions/action">
				<xsl:sort select="@name" />
				<xsl:value-of select="@name"/>, actionSkill, pluginTOCHANGE
			</xsl:for-each>
		</xsl:for-each>

		<xsl:for-each select="/doc/skills/skill">
			<xsl:sort select="@name" />
			<xsl:for-each select="vars/var">
				<xsl:sort select="@name" />
				<xsl:value-of select="@name"/>, skillVariables, pluginTOCHANGE
			</xsl:for-each>
		</xsl:for-each>
		
		self, pseudoVariable, msi.gama.core
		myself, pseudoVariable, msi.gama.core
		each, pseudoVariable, msi.gama.core
		
		
		MISSING

		== Types ==
		[G__DataTypes#bool bool], [G__DataTypes#float float], [G__DataTypes#int int],
		[G__DataTypes#string string], [G__DataTypes#agent agent],
		[G__DataTypes#container container]
		, [G__DataTypes#file file], [G__DataTypes#geometry geometry],
		[G__DataTypes#graph graph], [G__DataTypes#list list],
		[G__DataTypes#map map], [G__DataTypes#matrix matrix]
		, [G__DataTypes#pair pair], [G__DataTypes#path path],
		[G__DataTypes#point point], [G__DataTypes#rgb rgb], [G__DataTypes#rgb
		color], [G__DataTypes#species species], [G__DataTypes#topology
		topology]


		== [G__GlobalSpecies the world] ==
		[G__GlobalSpecies torus], [G__GlobalSpecies#Environment_Size Environment Size],
		[G__GlobalSpecies#world world], [G__GlobalSpecies#time time]
		[G__GlobalSpecies#cycle cycle], [G__GlobalSpecies#step step],
		[G__GlobalSpecies#time time], [G__GlobalSpecies#duration duration],
		[G__GlobalSpecies#total_duration total_duration]
		[G__GlobalSpecies#average_duration average_duration],
		[G__GlobalSpecies#machine_time machine_time], [G__GlobalSpecies#agents
		agents], [G__GlobalSpecies#halt stop], [G__GlobalSpecies#halt halt],
		[G__GlobalSpecies#pause pause], [G__GlobalSpecies#scheduling
		scheduling]


		== Grid ==
		[G__GridSpecies#grid_x grid_x], [G__GridSpecies#grid_y grid_y], [G__GridSpecies#agents
		agents], [G__GridSpecies#color color], [G__GridSpecies#grid_value
		grid_value]



	</xsl:template>


</xsl:stylesheet>
