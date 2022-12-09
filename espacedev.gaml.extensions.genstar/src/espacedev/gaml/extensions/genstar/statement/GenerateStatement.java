/*******************************************************************************************************
 *
 * GenerateStatement.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.statement;

import static msi.gama.common.interfaces.IKeyword.FROM;
import static msi.gama.common.interfaces.IKeyword.GENERATE;
import static msi.gama.common.interfaces.IKeyword.NUMBER;
import static msi.gama.common.interfaces.IKeyword.RETURNS;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.precompiler.ISymbolKind.SEQUENCE_STATEMENT;
import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import java.util.List;
import java.util.Map;

import espacedev.gaml.extensions.genstar.generator.IGenstarGenerator;
import espacedev.gaml.extensions.genstar.statement.GenerateStatement.GenerateValidator;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant;
import espacedev.gaml.extensions.genstar.utils.GenStarGamaUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.types.SpeciesConstantExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;

/**
 * The Class GenerateStatement.
 */

/**
 * The Class GenerateStatement.
 */

/**
 * The Class GenerateStatement.
 */

/**
 * The Class GenerateStatement.
 */

/**
 * The Class GenerateStatement.
 */
@symbol (
		name = GENERATE,
		kind = SEQUENCE_STATEMENT,
		with_sequence = true,
		breakable = true,
		continuable = true,
		with_args = true,
		category = { IOperatorCategory.GENSTAR },
		concept = { IConcept.SPECIES },
		remote_context = true)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
				name = SPECIES,
				type = { IType.SPECIES, IType.AGENT },
				optional = true,
				doc = @doc ("The species of the agents to be created.")),
				@facet (
						name = FROM,
						type = IType.NONE,
						optional = false,
						doc = @doc (
								value = """
										To specify the input data used to inform the generation process. Various data input can be used:
										 * list of csv_file: can be aggregated or micro data
										 * matrix: describe the joint distribution of two attributes
										 * genstar generator: a dedicated gaml type to enclose various genstar options all in one""")),
				@facet (
						/*
						 * make those attributes like in csv map to directly recognize species' attributes rather than
						 * use string with potential mispells
						 */
						name = GenStarConstant.GSATTRIBUTES,
						type = { IType.MAP },
						optional = false,
						doc = @doc ("To specify the explicit link between agent attributes and file based attributes")),
				@facet (
						name = NUMBER,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = """
										To specify the number of created agents interpreted as an int value.
										If facet is ommited or value is 0 or less, generator will treat data used in the 'from' facet as contingencies
										(i.e. a count of entities) and infer a number to generate (if distribution is used, then only one entity will be created""")),
				@facet (
						name = GenStarConstant.GSGENERATOR,
						type = { IType.STRING },
						optional = true,
						doc = @doc ("To specify the type of generator you want to use: as of now there is only DS (or DirectSampling) available")) },
		omissible = SPECIES)

@doc (
		value = "Allows to create a synthetic population of agent from a set of given rules",
		usages = { @usage (
				value = "The synthax to create a minimal synthetic population from aggregated file is:",
				examples = { @example (
						value = """
								generate species:people number: 10000
								from:[csv_file("../includes/Age & Sexe-Tableau 1.csv",";")]
								attributes:["Age"::["Moins de 5 ans", "5 à 9 ans", "10 à 14 ans", "15 à 19 ans", "20 à 24 ans",
								"25 à 29 ans", "30 à 34 ans", "35 à 39 ans", "40 à 44 ans", "45 à 49 ans",
								"50 à 54 ans", "55 à 59 ans", "60 à 64 ans", "65 à 69 ans", "70 à 74 ans", "75 à 79 ans",
								"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"],
								"Sexe"::["Hommes", "Femmes"]];""",
						isExecutable = false) }) })
@validator (GenerateValidator.class)
public class GenerateStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	/** The init. */
	private Arguments init;

	/** The algorithm. */
	private final IExpression from;
	
	/** The number. */
	private final IExpression number;
	
	/** The species. */
	private final IExpression species;
	
	/** The attributes. */
	private final IExpression attributes;
	
	/** The algorithm. */
	private final IExpression algorithm;

	/** The returns. */
	private final String returns;

	/** The sequence. */
	private final RemoteSequence sequence;

	/**
	 * Instantiates a new generate statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public GenerateStatement(final IDescription desc) {
		super(desc);
		returns = getLiteral(RETURNS);
		from = getFacet(FROM);
		number = getFacet(NUMBER);
		species = getFacet(SPECIES);

		attributes = getFacet(GenStarConstant.GSATTRIBUTES);
		algorithm = getFacet(GenStarConstant.GSGENERATOR);

		sequence = new RemoteSequence(description);
		sequence.setName("commands of generate ");
		setName(GENERATE);
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public IList<? extends IAgent> privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// First, we compute the number of agents to create
		final Integer max = number == null ? null : Cast.asInt(scope, number.value(scope));
		if (from == null && max != null && max <= 0) return GamaListFactory.EMPTY_LIST;

		// Next, we compute the species to instantiate
		final IPopulation pop = findPopulation(scope);
		// A check is made in order to address issues #2621 and #2611
		if (pop == null || pop.getSpecies() == null)
			throw GamaRuntimeException.error("Impossible to determine the species of the agents to generate", scope);
		checkPopulationValidity(pop, scope);

		// We grab whatever initial data are input
		final List<Map<String, Object>> inits = GamaListFactory.create(Types.MAP, max == null ? 10 : max);
		final Object source = from.value(scope);

		// Only one generator according to data input type (type of the source Object)
		StreamEx.of(GenStarGamaUtils.getGamaGenerator()).findFirst(g -> g.sourceMatch(scope, source))
				.orElseThrow(IllegalArgumentException::new).generate(scope, inits, max, source, attributes.value(scope),
						algorithm == null ? null : algorithm.value(scope), init, this);

		// and we create and return the agent(s)
		final IList<? extends IAgent> agents = pop.createAgents(scope, inits.size(), inits, false, false, sequence);
		if (returns != null) { scope.setVarValue(returns, agents); }
		return agents;
	}

	/**
	 * Description PLZZZZZ Call it before calling the ICreateDelegate createFrom method !
	 *
	 * @param scope
	 * @param values
	 */

	/**
	 * Fill with user init.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 */

	/**
	 * Fill with user init.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 */

	/**
	 * Fill with user init.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 */

	/**
	 * Fill with user init.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public void fillWithUserInit(final IScope scope, final Map values) {
		if (init == null) return;
		scope.pushReadAttributes(values);
		try {
			init.forEachFacet((k, v) -> {
				values.put(k, v.getExpression().value(scope));
				return true;
			});
		} finally {
			scope.popReadAttributes();
		}
	}

	// ------------------------------------------------------------------------------------------------ //
	// ------------------------------------------------------------------------------------------------ //
	// //
	// Copy pasted from the CreateStatement way to init agents //
	// //
	// ------------------------------------------------------------------------------------------------ //
	// ------------------------------------------------------------------------------------------------ //

	/**
	 * Find population.
	 *
	 * @param scope
	 *            the scope
	 * @return the i population
	 */
	@SuppressWarnings ("rawtypes")
	private IPopulation findPopulation(final IScope scope) {
		final IAgent executor = scope.getAgent();
		if (species == null) return executor.getPopulationFor(description.getSpeciesContext().getName());
		ISpecies s = Cast.asSpecies(scope, species.value(scope));
		if (s == null) {// A last attempt in order to fix #2466
			final String potentialSpeciesName = species.getDenotedType().getSpeciesName();
			if (potentialSpeciesName != null) { s = scope.getModel().getSpecies(potentialSpeciesName); }
		}
		if (s == null) throw GamaRuntimeException.error(
				"No population of " + species.serialize(false) + " is accessible in the context of " + executor + ".",
				scope);
		return executor.getPopulationFor(s);
	}

	/**
	 * A check made in order to address issues #2621 and #2611
	 *
	 * @param pop
	 * @param scope
	 * @throws GamaRuntimeException
	 */
	@SuppressWarnings ("rawtypes")
	private void checkPopulationValidity(final IPopulation pop, final IScope scope) throws GamaRuntimeException {
		if (pop instanceof SimulationPopulation && !(scope.getAgent() instanceof ExperimentAgent))
			throw error("Simulations can only be created within experiments", scope);
		final SpeciesDescription sd = pop.getSpecies().getDescription();
		final String error = sd.isAbstract() ? "abstract" : sd.isMirror() ? "a mirror" : sd.isBuiltIn() ? "built-in"
				: sd.isGrid() ? "a grid" : null;
		if (error != null) throw error(sd.getName() + "is " + error + " and cannot be instantiated.", scope);
	}

	/**
	 * make the validator coherent with 'must contains' facets
	 *
	 * @author kevinchapuis
	 *
	 */
	public static class GenerateValidator implements IDescriptionValidator<StatementDescription> {

		@SuppressWarnings ({ "unchecked", "rawtypes" })
		@Override
		public void validate(final StatementDescription description) {
			final IExpression species = description.getFacetExpr(SPECIES);
			// If the species cannot be determined, issue an error and leave validation
			if (species == null) {
				description.error("The species is not found", UNKNOWN_SPECIES, SPECIES);
				return;
			}

			final SpeciesDescription sd = species.getGamlType().getDenotedSpecies();
			if (sd == null) {
				description.error("The species to instantiate cannot be determined", UNKNOWN_SPECIES, SPECIES,
						species.getName());
				return;
			}

			if (species instanceof SpeciesConstantExpression) {
				final boolean abs = sd.isAbstract();
				final boolean mir = sd.isMirror();
				final boolean gri = sd.isGrid();
				final boolean bui = sd.isBuiltIn();
				if (abs || mir || gri || bui) {
					final String p = abs ? "abstract" : mir ? "a mirror" : gri ? "a grid" : bui ? "built-in" : "";
					description.error(sd.getName() + " is " + p + " and cannot be instantiated", WRONG_TYPE, SPECIES);
					return;
				}
			} else if (!(sd instanceof ModelDescription)) {
				description.info(
						"The actual species will be determined at runtime. This can lead to errors if it cannot be instantiated",
						WRONG_TYPE, SPECIES);
			}

			if (sd instanceof ModelDescription && !(description.getSpeciesContext() instanceof ExperimentDescription)) {
				description.error("Simulations can only be created within experiments", WRONG_CONTEXT, SPECIES);
				return;
			}

			final SpeciesDescription callerSpecies = description.getSpeciesContext();
			final SpeciesDescription macro = sd.getMacroSpecies();
			if (macro == null) {
				description.error("The macro-species of " + species + " cannot be determined");
				return;
				// hqnghi special case : create instances of model from
				// model
			}
			if (macro instanceof ModelDescription && callerSpecies instanceof ModelDescription) {

				// end-hqnghi
			} else if (callerSpecies != macro && !callerSpecies.hasMacroSpecies(macro)
					&& !callerSpecies.hasParent(macro)) {
				description.error(
						"No instance of " + macro.getName() + " available for creating instances of " + sd.getName());
				return;
			}

			final IExpression exp = description.getFacetExpr(FROM);
			if (exp != null) {
				final IType type = exp.getGamlType();

				if (type.id() != 938373948) {
					boolean found = false;
					List<IType> types = StreamEx.of(GenStarGamaUtils.getGamaGenerator())
							.map(IGenstarGenerator::sourceType).toList();
					for (final IType genType : types) {
						found = genType.isAssignableFrom(type);
						if (found) { break; }
					}
					if (type == Types.MATRIX) {
						// verify that x,y matrix match possible attributes values
					}
					if (!found) {
						description.warning(
								"Facet 'from' expects an expression with one of the following types: " + types,
								WRONG_TYPE, FROM);
					}
				}
			}

			final Facets facets = description.getPassedArgs();
			for (final Facet att : facets.getFacets()) {
				if (!sd.isExperiment() && !sd.hasAttribute(att.key)) {
					description.error("Attribute " + att + " is not defined in species " + species.getName(),
							UNKNOWN_VAR);
					return;
				}
			}

		}

	}

	@Override
	public void setFormalArgs(final Arguments args) { init = args; }

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence.setChildren(com);
	}

	@Override
	public void enterScope(final IScope scope) {
		if (returns != null) { scope.addVarWithValue(returns, null); }
		super.enterScope(scope);
	}

}
