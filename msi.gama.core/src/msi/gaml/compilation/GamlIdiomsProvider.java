/*******************************************************************************************************
 *
 * msi.gaml.compilation.GamlIdiomsProvider.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gaml.compilation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.UnitConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.IUnits;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class GamlIdiomsProvider<T extends IGamlDescription> {

	public final static GamlIdiomsProvider<SpeciesDescription> SPECIES =
			new GamlIdiomsProvider<SpeciesDescription>("species", IKeyword.SPECIES, "Built-in species",
					Types.getBuiltInSpecies()).with((each) -> each.getDocumentationWithoutMeta());
	public final static GamlIdiomsProvider<VariableDescription> SPECIES_ATTRIBUTES = new GamlIdiomsProvider<>(
			"variables", "species_attribute", "Built-in species attribute",
			Iterables.concat(Iterables.transform(Types.getBuiltInSpecies(), (each) -> each.getOwnAttributes())));
	public final static GamlIdiomsProvider<ActionDescription> SPECIES_ACTIONS =
			new GamlIdiomsProvider<>("actions", "species_action", "Built-in species action",
					Iterables.concat(Iterables.transform(Types.getBuiltInSpecies(), (each) -> each.getOwnActions())));
	public final static GamlIdiomsProvider<SkillDescription> SKILLS = new GamlIdiomsProvider<>("skills", IKeyword.SKILL,
			"Skill", GamaSkillRegistry.INSTANCE.getRegisteredSkills());
	public final static GamlIdiomsProvider<VariableDescription> SKILLS_ATTRIBUTES =
			new GamlIdiomsProvider<>("variables", "skill_attribute", "Skill Attribute",
					GamaSkillRegistry.INSTANCE.getRegisteredSkillsAttributes());
	public final static GamlIdiomsProvider<ActionDescription> SKILLS_ACTIONS = new GamlIdiomsProvider<>("actions",
			"skill_action", "Skill Action", GamaSkillRegistry.INSTANCE.getRegisteredSkillsActions());
	public final static GamlIdiomsProvider<SymbolProto> STATEMENTS =
			new GamlIdiomsProvider<>("statements", "statement", "Statements", DescriptionFactory.getStatementProtos());
	public final static GamlIdiomsProvider<UnitConstantExpression> CONSTANTS =
			new GamlIdiomsProvider<>("constant", IKeyword.CONST, "Constant & Units", IUnits.UNITS_EXPR.values());
	public final static GamlIdiomsProvider<OperatorProto> OPERATORS = new GamlIdiomsProvider<>("operators", "operator",
			"Operators",
			Iterables.concat(Iterables.transform(IExpressionCompiler.OPERATORS.values(), (each) -> each.values())));
	public final static GamlIdiomsProvider<IType<?>> TYPES =
			new GamlIdiomsProvider<>("types", "type", "Types", Types.builtInTypes.getAllTypes());
	public final static GamlIdiomsProvider<FacetProto> FACETS =
			new GamlIdiomsProvider<>("facets", "facet", "Facets", DescriptionFactory.getFacetsProtos());
	public final static GamlIdiomsProvider<OperatorProto> FIELDS =
			new GamlIdiomsProvider<>("attributes", "field", "Fields", Types.getAllFields());

	public final static List<GamlIdiomsProvider<?>> PROVIDERS =
			Arrays.asList(SPECIES, SPECIES_ATTRIBUTES, SPECIES_ACTIONS, SKILLS, SKILLS_ATTRIBUTES, SKILLS_ACTIONS,
					STATEMENTS, CONSTANTS, OPERATORS, TYPES, FACETS, FIELDS);

	public final String id, name, search;
	public final Iterable<? extends T> elements;
	public final Map<T, String> titles;
	public IGamlDescription[] sortedElements;
	public Multimap<String, ? extends T> byName;
	// default
	public Function<T, String> documenter = (each) -> each.getDocumentation();

	public GamlIdiomsProvider(final String search, final String id, final String name,
			final Iterable<? extends T> elmts) {
		this(search, id, name, elmts, null);
	}

	public GamlIdiomsProvider(final String search, final String id, final String name,
			final Iterable<? extends T> elmts, final Map<T, String> titles) {
		this.search = search;
		this.id = id;
		this.name = name;
		this.elements = elmts;
		this.titles = titles;
	}

	@SuppressWarnings ("unchecked")
	public String document(final IGamlDescription element) {
		return documenter.apply((T) element);
	}

	public GamlIdiomsProvider<T> with(final Function<T, String> doc) {
		documenter = doc;
		return this;
	}

	public String getSearchCategory() {
		return search;
	}

	public Collection<? extends T> get(final String name) {
		if (byName == null) {
			init();
		}
		return byName.get(name);
	}

	public IGamlDescription[] getSortedElements() {
		if (sortedElements == null) {
			init();
		}
		return sortedElements;
	}

	private void init() {

		sortedElements = Iterables.toArray(elements, IGamlDescription.class);
		if (titles == null) {
			Arrays.sort(sortedElements, (e1, e2) -> e1.getTitle().compareTo(e2.getTitle()));
		} else {
			Arrays.sort(sortedElements, (e1, e2) -> titles.get(e1).compareTo(titles.get(e2)));
		}
		byName = Multimaps.index(elements, (each) -> each.getName());

	}

	public static Multimap<GamlIdiomsProvider<?>, IGamlDescription> forName(final String name) {
		final Multimap<GamlIdiomsProvider<?>, IGamlDescription> result = ArrayListMultimap.create();
		for (final GamlIdiomsProvider<?> p : PROVIDERS) {
			result.replaceValues(p, p.get(name));
		}
		return result;
	}

}
