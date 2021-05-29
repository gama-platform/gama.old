/*******************************************************************************************************
 *
 * msi.gaml.statements.CreateStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import static msi.gama.common.interfaces.IKeyword.AS;
import static msi.gama.common.interfaces.IKeyword.CREATE;
import static msi.gama.common.interfaces.IKeyword.FROM;
import static msi.gama.common.interfaces.IKeyword.HEADER;
import static msi.gama.common.interfaces.IKeyword.NUMBER;
import static msi.gama.common.interfaces.IKeyword.RETURNS;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword.WITH;
import static msi.gama.precompiler.ISymbolKind.SEQUENCE_STATEMENT;
import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.ExperimentPlan.ExperimentPopulation;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.SpeciesConstantExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Files;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.CreateStatement.CreateSerializer;
import msi.gaml.statements.CreateStatement.CreateValidator;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * This command is used to create agents.
 *
 * Considering the invoking agent as the execution context, species of the created agents can be 1. The same species of
 * the invoking agent or any peer species of the invoking agent's species. The newly created agent(s) will take the
 * invoking agent's macro-agent as its/their macro-agent.
 *
 * 2. The direct micro-species of the invoking agent's species. The newly create agent(s) will take the invoking agent
 * as its/their macro-agent.
 *
 * 3. The direct macro-species of the invoking agent's species or any peer species of this direct macro-species. The
 * newly created agent(s) will take the macro-agent of invoking agent's macro-agent as its/their macro-agent.
 *
 * Creation of agents from CSV files: create toto from: "toto.csv" header: true with:[att1::read("NAME"),
 * att2::read("TYPE")]; or, without header: create toto from: "toto.csv"with:[att1::read(0), att2::read(1)]; //with the
 * read(int), the index of the column.
 */
@symbol (
		name = CREATE,
		kind = SEQUENCE_STATEMENT,
		with_sequence = true,
		with_args = true,
		concept = { IConcept.SPECIES },
		remote_context = true)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
				name = SPECIES,
				type = { IType.SPECIES, IType.AGENT },
				optional = true,
				doc = @doc ("an expression that evaluates to a species, the species of the agents to be created. In the case of simulations, the name 'simulation', which represents the current instance of simulation, can also be used as a proxy to their species")),
				@facet (
						name = RETURNS,
						type = IType.NEW_TEMP_ID,
						optional = true,
						doc = @doc ("a new temporary variable name containing the list of created agents (a list, even if only one agent has been created)")),
				@facet (
						name = FROM,
						type = IType.NONE,
						optional = true,
						doc = @doc ("an expression that evaluates to a localized entity, a list of localized entities, a string (the path of a file), a file (shapefile, a .csv, a .asc or a OSM file) or a container returned by a request to a database")),
				@facet (
						name = NUMBER,
						type = IType.INT,
						optional = true,
						doc = @doc ("an expression that evaluates to an int, the number of created agents")),
				@facet (
						name = AS,
						type = { IType.SPECIES },
						optional = true,
						doc = @doc ("")),
				@facet (
						name = WITH,
						type = { IType.MAP },
						of = IType.NONE,
						index = IType.STRING,
						optional = true,
						doc = @doc ("an expression that evaluates to a map, for each pair the key is a species attribute and the value the assigned value")),
				@facet (
						name = HEADER,
						type = { IType.BOOL },
						optional = true,
						doc = @doc (
								deprecated = "Use a file constructor that specifies the header instead, e.g. csv_file('...', true)",
								value = "an expression that evaluates to a boolean, when creating agents from csv file, specify whether the file header is loaded")) },
		omissible = IKeyword.SPECIES)
@doc (
		value = "Allows an agent to create `number` agents of species `species`, to create agents of species `species` from a shapefile or to create agents of species `species` from one or several localized entities (discretization of the localized entity geometries).",
		usages = { @usage (
				value = "Its simple syntax to create `an_int` agents of species `a_species` is:",
				examples = { @example (
						value = "create a_species number: an_int;",
						isExecutable = false),
						@example (
								value = "create species_of(self) number: 5 returns: list5Agents;",
								isTestOnly = false),
						@example (
								var = "list5Agents",
								returnType = "list",
								value = "5",
								isExecutable = false) }),
				@usage ("If `number` equals 0 or species is not a species, the statement is ignored."), @usage (
						value = "In GAML modelers can create agents of species `a_species  (with two attributes `type` and `nature` with types corresponding to the types of the shapefile attributes) from a shapefile `the_shapefile` while reading attributes 'TYPE_OCC' and 'NATURE' of the shapefile. One agent will be created by object contained in the shapefile:",
						examples = @example (
								value = "create a_species from: the_shapefile with: [type:: read('TYPE_OCC'), nature::read('NATURE')];",
								isExecutable = false)),
				@usage (
						value = "In order to create agents from a .csv file, facet `header` can be used to specified whether we can use columns header:",
						examples = { @example (
								value = "create toto from: \"toto.csv\" header: true with:[att1::read(\"NAME\"), att2::read(\"TYPE\")];",
								isExecutable = false),
								@example (
										value = "or",
										isExecutable = false),
								@example (
										value = "create toto from: \"toto.csv\" with:[att1::read(0), att2::read(1)]; //with read(int), the index of the column",
										isExecutable = false) }),
				@usage (
						value = "Similarly to the creation from shapefile, modelers can create agents from a set of geometries. In this case, one agent per geometry will be created (with the geometry as shape)",
						examples = { @example (
								value = "create species_of(self) from: [square(4),circle(4)]; 	// 2 agents have been created, with shapes respectively square(4) and circle(4)"),
								@example (
										value = "create species_of(self) from: [square(4),circle(4)] returns: new_agt;",
										isTestOnly = true),
								@example (
										value = "new_agt[0].shape",
										equals = "square(4)",
										returnType = "geometry",
										isTestOnly = true),
								@example (
										value = "new_agt[1].shape",
										equals = "circle(4)",
										returnType = "geometry",
										isTestOnly = true) }),
				@usage (
						value = "Created agents are initialized following the rules of their species. If one wants to refer to them after the statement is executed, the returns keyword has to be defined: the agents created will then be referred to by the temporary variable it declares. For instance, the following statement creates 0 to 4 agents of the same species as the sender, and puts them in the temporary variable children for later use.",
						examples = { @example (
								value = "create species (self) number: rnd (4) returns: children;",
								test = false),
								@example (
										value = "ask children {",
										test = true),
								@example (
										value = "        // ...",
										test = false),
								@example (
										value = "}",
										test = false) }),
				@usage (
						value = "If one wants to specify a special initialization sequence for the agents created, create provides the same possibilities as ask. This extended syntax is:",
						examples = { @example (
								value = "create a_species number: an_int {",
								isExecutable = false),
								@example (
										value = "     [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "The same rules as in ask apply. The only difference is that, for the agents created, the assignments of variables will bypass the initialization defined in species. For instance:",
						examples = { @example (
								value = "create species(self) number: rnd (4) returns: children {",
								isExecutable = false),
								@example (
										value = "     set location <- myself.location + {rnd (2), rnd (2)}; // tells the children to be initially located close to me",
										isExecutable = false),
								@example (
										value = "     set parent <- myself; // tells the children that their parent is me (provided the variable parent is declared in this species) ",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "Desprecated uses: ",
						examples = { @example (
								value = "// Simple syntax",
								isExecutable = false),
								@example (
										value = "create species: a_species number: an_int;",
										isExecutable = false),
								@example (
										value = "",
										isExecutable = false) }) })
@validator (CreateValidator.class)
@serializer (CreateSerializer.class)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CreateStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	public static class CreateValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription cd) {
			final IExpression species = cd.getFacetExpr(SPECIES);
			// If the species cannot be determined, issue an error and leave validation
			if (species == null) {
				cd.error("The species to instantiate cannot be determined", UNKNOWN_SPECIES, SPECIES);
				return;
			}

			final SpeciesDescription sd = species.getGamlType().getDenotedSpecies();
			if (sd == null) {
				cd.error("The species to instantiate cannot be determined", UNKNOWN_SPECIES, SPECIES);
				return;
			}

			if (species instanceof SpeciesConstantExpression) {
				final boolean abs = sd.isAbstract();
				final boolean mir = sd.isMirror();
				final boolean gri = sd.isGrid();
				final boolean bui = sd.isBuiltIn();
				if (abs || mir || gri || bui) {
					final String p = abs ? "abstract" : mir ? "a mirror" : gri ? "a grid" : bui ? "built-in" : "";
					cd.error(sd.getName() + " is " + p + " and cannot be instantiated", WRONG_TYPE, SPECIES);
					return;
				}
			} else {
				if (!(sd instanceof ModelDescription)) {
					cd.info("The actual species will be determined at runtime. This can lead to errors if it cannot be instantiated",
							WRONG_TYPE, SPECIES);
				}
			}

			if (sd instanceof ModelDescription && !(cd.getSpeciesContext() instanceof ExperimentDescription)) {
				cd.error("Simulations can only be created within experiments", WRONG_CONTEXT, SPECIES);
				return;
			}

			final SpeciesDescription callerSpecies = cd.getSpeciesContext();
			final SpeciesDescription macro = sd.getMacroSpecies();
			if (macro == null) {
				cd.error("The macro-species of " + species + " cannot be determined");
				return;
				// hqnghi special case : create instances of model from
				// model
			} else if (macro instanceof ModelDescription && callerSpecies instanceof ModelDescription) {

				// end-hqnghi
			} else if (callerSpecies != macro && !callerSpecies.hasMacroSpecies(macro)
					&& !callerSpecies.hasParent(macro)) {
				cd.error("No instance of " + macro.getName() + " available for creating instances of " + sd.getName());
				return;
			}
			final IExpression exp = cd.getFacetExpr(FROM);
			if (exp != null) {
				final IType type = exp.getGamlType();
				boolean found = false;
				for (final IType delegateType : delegateTypes) {
					found = delegateType.isAssignableFrom(type);
					if (found) { break; }
				}
				if (!found) {
					cd.warning("Facet 'from' expects an expression with one of the following types: " + delegateTypes,
							WRONG_TYPE, FROM);
				}
			}
			final Facets facets = cd.getPassedArgs();
			for (final Facet att : facets.getFacets()) {
				if (!sd.isExperiment() && !sd.hasAttribute(att.key)) {
					cd.error("Attribute " + att + " is not defined in species " + species.getName(), UNKNOWN_VAR);
					return;
				}
			}

		}

	}

	public static class CreateSerializer extends StatementSerializer {

		@Override
		protected void serializeArgs(final SymbolDescription s, final StringBuilder sb, final boolean ncludingBuiltIn) {
			final StatementDescription desc = (StatementDescription) s;
			final Facets args = desc.getPassedArgs();
			if (args == null || args.isEmpty()) return;
			sb.append("with: [");
			args.forEachFacet((name, exp) -> {
				sb.append(name).append("::").append(exp.serialize(false));
				sb.append(", ");
				return true;
			});
			sb.setLength(sb.length() - 2);
			sb.append("]");
		}
	}

	// private final ThreadLocal<Arguments> init = new ThreadLocal();
	private Arguments init;
	private final IExpression from, number, species, header;
	private final String returns;
	private final RemoteSequence sequence;
	static List<ICreateDelegate> delegates = new ArrayList<>();
	static List<IType> delegateTypes = new ArrayList<>();

	/**
	 * @param createExecutableExtension
	 */
	public static void addDelegate(final ICreateDelegate delegate) {
		delegates.add(delegate);
		final IType delegateType = delegate.fromFacetType();
		if (delegateType != null && delegateType != Types.NO_TYPE) { delegateTypes.add(delegate.fromFacetType()); }
	}

	public static void removeDelegate(final ICreateDelegate cd) {
		delegates.remove(cd);
	}

	public CreateStatement(final IDescription desc) {
		super(desc);
		returns = getLiteral(RETURNS);
		from = getFacet(FROM);
		number = getFacet(NUMBER);
		species = getFacet(SPECIES);
		header = getFacet(HEADER);
		sequence = new RemoteSequence(description);
		sequence.setName("commands of create ");
		setName("create");
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence.setChildren(com);
	}

	@Override
	public void enterScope(final IScope scope) {
		if (returns != null) { scope.addVarWithValue(returns, null); }
		super.enterScope(scope);
	}

	IPopulation findPopulation(final IScope scope) {
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
		IPopulation pop = executor.getPopulationFor(s);
		// hqnghi population of micro-model's experiment is not exist, we
		// must create the new one
		if (pop == null && s instanceof ExperimentPlan && executor instanceof IMacroAgent) {
			final ExperimentPlan ep = (ExperimentPlan) s;
			pop = ep.new ExperimentPopulation(s);
			final IScope sc = ep.getExperimentScope();
			pop.initializeFor(sc);
			((IMacroAgent) executor).addExternMicroPopulation(
					s.getDescription().getModelDescription().getAlias() + "." + s.getName(), pop);
		}
		// end-hqnghi
		return pop;
	}

	@Override
	public IList<? extends IAgent> privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		// First, we compute the number of agents to create
		final Integer max = number == null ? null : Cast.asInt(scope, number.value(scope));
		if (from == null && max != null && max <= 0) return GamaListFactory.EMPTY_LIST;

		// Next, we compute the species to instantiate
		final IPopulation pop = findPopulation(scope);
		// A check is made in order to address issues #2621 and #2611
		if (pop == null || pop.getSpecies() == null)
			throw GamaRuntimeException.error("Impossible to determine the species of the agents to create", scope);
		checkPopulationValidity(pop, scope);

		// We grab whatever initial values are defined (from CSV, GIS, or user)
		final List<Map<String, Object>> inits = GamaListFactory.create(Types.MAP, max == null ? 10 : max);
		final Object source = getSource(scope);
		for (final ICreateDelegate delegate : delegates) {
			if (delegate.acceptSource(scope, source)) { delegate.createFrom(scope, inits, max, source, init, this); }
		}
		// and we create and return the agent(s)
		final IList<? extends IAgent> agents = createAgents(scope, pop, inits);
		if (returns != null) { scope.setVarValue(returns, agents); }
		return agents;
	}

	/**
	 * A check made in order to address issues #2621 and #2611
	 *
	 * @param pop
	 * @param scope
	 * @throws GamaRuntimeException
	 */
	private void checkPopulationValidity(final IPopulation pop, final IScope scope) throws GamaRuntimeException {
		if (pop instanceof SimulationPopulation && !(scope.getAgent() instanceof ExperimentAgent))
			throw error("Simulations can only be created within experiments", scope);
		final SpeciesDescription sd = pop.getSpecies().getDescription();
		final String error = sd.isAbstract() ? "abstract"
				: sd.isMirror() ? "a mirror" : sd.isBuiltIn() ? "built-in" : sd.isGrid() ? "a grid" : null;
		if (error != null) throw error(sd.getName() + "is " + error + " and cannot be instantiated.", scope);
	}

	private Object getSource(final IScope scope) {
		Object source = from == null ? null : from.value(scope);
		if (source instanceof String) {
			source = Files.from(scope, (String) source);
		} else if (source instanceof GamaShape) { source = GamaListFactory.wrap(Types.GEOMETRY, source); }
		return source;
	}

	private IList<? extends IAgent> createAgents(final IScope scope, final IPopulation<? extends IAgent> population,
			final List<Map<String, Object>> inits) {
		if (population == null) return GamaListFactory.EMPTY_LIST;
		// final boolean hasSequence = sequence != null && !sequence.isEmpty();
		boolean shouldBeScheduled = false;
		// If we create simulations within a single experiment, we must schedule
		// them
		if (population.getHost() instanceof ExperimentAgent) {
			final ExperimentAgent exp = (ExperimentAgent) population.getHost();
			if (exp.isScheduled()) { shouldBeScheduled = true; }
		}
		// As we are in the create statement, the agents are not restored
		final IList<? extends IAgent> list =
				population.createAgents(scope, inits.size(), inits, false, shouldBeScheduled, sequence);
		// AD Commented this out
		// final IMacroAgent currentMacro = population.getHost();
		// if (currentMacro != null)
		// currentMacro.addSubAgents(list.size());

		// hqnghi in case of creating experiment of micro-models, we must
		// implicitely initialize it and its simulation output
		if (population instanceof ExperimentPopulation) {
			population.setHost(scope.getExperiment());
			for (final IAgent a : population) {
				((ExperimentAgent) a)._init_(scope);
				final SimulationAgent sim = ((ExperimentAgent) a).getSimulation();
				sim.adoptTopologyOf(scope.getSimulation());

				if (!sim.getScheduled()) {
					// ((ExperimentAgent)
					// a).getActionExecuter().init(sim.getScope());
					// } else {
					sim._init_(sim.getScope());
				}
				if (sim.getOutputManager() != null) { sim.getOutputManager().init(sim.getScope()); }
			}
		}
		// end-hqnghi
		// if (sequence != null && !sequence.isEmpty()) {
		// for (final IAgent remoteAgent : list.iterable(scope)) {
		// if (!scope.execute(sequence, remoteAgent, null).passed()) {
		// break;
		// }
		// }
		// }
		return list;
	}

	// TODO Call it before calling the ICreateDelegate createFrom method !
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

	@Override
	public void setFormalArgs(final Arguments args) {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final IScope scope, final Arguments args) {}

	/**
	 * @return
	 */
	public IExpression getHeader() {
		return header;
	}

}