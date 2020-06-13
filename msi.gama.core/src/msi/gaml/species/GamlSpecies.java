/*******************************************************************************************************
 *
 * msi.gaml.species.GamlSpecies.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.species;

import java.util.Collection;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
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
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.GamlSpecies.SpeciesValidator;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import one.util.streamex.StreamEx;

/**
 * The Class GamlSpecies. A species specified by GAML attributes
 *
 * @author drogoul
 */
@symbol (
		name = { IKeyword.SPECIES, IKeyword.GLOBAL, IKeyword.GRID },
		kind = ISymbolKind.SPECIES,
		with_sequence = true,
		concept = { IConcept.SPECIES })
@inside (
		kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT, ISymbolKind.SPECIES })
@facets (
		value = { @facet (
				name = IKeyword.PARALLEL,
				type = { IType.BOOL, IType.INT },
				optional = true,
				doc = @doc ("(experimental) setting this facet to 'true' will allow this species to use concurrency when scheduling its agents; setting it to an integer will set the threshold under which they will be run sequentially (the default is initially 20, but can be fixed in the preferences). This facet has a default set in the preferences (Under Performances > Concurrency)")),
				@facet (
						name = IKeyword.WIDTH,
						type = IType.INT,
						optional = true,
						doc = @doc ("(grid only), the width of the grid (in terms of agent number)")),
				@facet (
						name = IKeyword.HEIGHT,
						type = IType.INT,
						optional = true,
						doc = @doc ("(grid only),  the height of the grid (in terms of agent number)")),
				@facet (
						name = IKeyword.CELL_WIDTH,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("(grid only), the width of the cells of the grid")),
				@facet (
						name = IKeyword.CELL_HEIGHT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("(grid only), the height of the cells of the grid")),
				@facet (
						name = IKeyword.NEIGHBOURS,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = "(grid only), the chosen neighborhood (4, 6 or 8)",
								deprecated = "use 'neighbors' instead")),
				@facet (
						name = IKeyword.NEIGHBORS,
						type = IType.INT,
						optional = true,
						doc = @doc ("(grid only), the chosen neighborhood (4, 6 or 8)")),
				@facet (
						name = "horizontal_orientation",
						type = IType.BOOL,
						optional = true,
						doc = { @doc (
								value = "(hexagonal grid only),(true by default). Allows use a hexagonal grid with a horizontal or vertical orientation. ") }),
				@facet (
						name = "use_individual_shapes",
						type = IType.BOOL,
						optional = true,
						doc = { @doc (
								value = "(grid only),(true by default). Allows to specify whether or not the agents of the grid will have distinct geometries. If set to false, they will all have simpler proxy geometries",
								see = "use_regular_agents",
								comment = "This facet, when set to true, allows to save memory by generating only one reference geometry and proxy geometries for the agents") }),
				@facet (
						name = "use_regular_agents",
						type = IType.BOOL,
						optional = true,
						doc = { @doc (
								value = "(grid only),(true by default). Allows to specify if the agents of the grid are regular agents (like those of any other species) or minimal ones (which can't have sub-populations, can't inherit from a regular species, etc.)") }),
				@facet (
						name = "optimizer",
						type = IType.STRING,
						optional = true,
						doc = { @doc (
								value = "(grid only),(\"A*\" by default). Allows to specify the algorithm for the shortest path computation (\"BF\", \"Dijkstra\", \"A*\" or \"JPS*\"") }),
				@facet (
						name = "use_neighbors_cache",
						type = IType.BOOL,
						optional = true,
						doc = { @doc (
								value = "(grid only),(true by default). Allows to turn on or off the use of the neighbors cache used for grids. Note that if a diffusion of variable occurs, GAMA will emit a warning and automatically switch to a caching version") }),
				@facet (
						name = IKeyword.FILE,
						type = IType.FILE,
						optional = true,
						doc = @doc ("(grid only), a bitmap file that will be loaded at runtime so that the value of each pixel can be assigned to the attribute 'grid_value'")),
				@facet (
						name = IKeyword.FILES,
						type = IType.LIST,
						of = IType.FILE,
						optional = true,
						doc = @doc ("(grid only), a list of bitmap file that will be loaded at runtime so that the value of each pixel of each file can be assigned to the attribute 'bands'")),
				@facet (
						name = IKeyword.TORUS,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("is the topology toric (defaut: false). Needs to be defined on the global species.")),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = false,
						doc = @doc ("the identifier of the species")),
				@facet (
						name = IKeyword.PARENT,
						type = IType.SPECIES,
						optional = true,
						doc = @doc ("the parent class (inheritance)")),
				@facet (
						name = IKeyword.EDGE_SPECIES,
						type = IType.SPECIES,
						optional = true,
						doc = @doc ("In the case of a species defining a graph topology for its instances (nodes of the graph), specifies the species to use for representing the edges")),
				@facet (
						name = IKeyword.SKILLS,
						type = IType.LIST,
						of = IType.SKILL,
						optional = true,
						doc = @doc ("The list of skills that will be made available to the instances of this species. Each new skill provides attributes and actions that will be added to the ones defined in this species")),
				@facet (
						name = IKeyword.MIRRORS,
						type = { IType.LIST, IType.SPECIES },
						of = IType.AGENT,
						optional = true,
						doc = @doc ("The species this species is mirroring. The population of this current species will be dependent of that of the species mirrored (i.e. agents creation and death are entirely taken in charge by GAMA with respect to the demographics of the species mirrored). In addition, this species is provided with an attribute called 'target', which allows each agent to know which agent of the mirrored species it is representing.")),
				@facet (
						name = IKeyword.CONTROL,
						type = IType.SKILL,
						optional = true,
						doc = @doc ("defines the architecture of the species (e.g. fsm...)")),
				@facet (
						name = "compile",
						type = IType.BOOL,
						optional = true,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.FREQUENCY,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = "The execution frequency of the species (default value: 1). For instance, if frequency is set to 10, the population of agents will be executed only every 10 cycles.",
								see = { "schedules" })),
				@facet (
						name = IKeyword.SCHEDULES,
						type = IType.CONTAINER,
						of = IType.AGENT,
						optional = true,
						doc = @doc ("A container of agents (a species, a dynamic list, or a combination of species and containers) , which represents which agents will be actually scheduled when the population is scheduled for execution. Note that the world (or the simulation) is *always* scheduled first, so there is no need to explicitly mention it. Doing so would result in a runtime error. For instance, 'species a schedules: (10 among a)' will result in a population that schedules only 10 of its own agents every cycle. 'species b schedules: []' will prevent the agents of 'b' to be scheduled. Note that the scope of agents covered here can be larger than the population, which allows to build complex scheduling controls; for instance, defining 'global schedules: [] {...} species b schedules: []; species c schedules: b; ' allows to simulate a model where only the world and the agents of b are scheduled, without even having to create an instance of c.")),
				@facet (
						name = IKeyword.TOPOLOGY,
						type = IType.TOPOLOGY,
						optional = true,
						doc = @doc ("The topology of the population of agents defined by this species. In case of nested species, it can for example be the shape of the macro-agent. In case of grid or graph species, the topology is automatically computed and cannot be redefined")),
				@facet (
						name = IKeyword.VIRTUAL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("whether the species is virtual (cannot be instantiated, but only used as a parent) (false by default)")) },
		omissible = IKeyword.NAME)
@doc (
		value = "The species statement allows modelers to define new species in the model. `" + IKeyword.GLOBAL
				+ "` and `" + IKeyword.GRID + "` are speciel cases of species: `" + IKeyword.GLOBAL
				+ "` being the definition of the global agent (which has automatically one instance, world) and `"
				+ IKeyword.GRID + "` being a species with a grid topology.",
		usages = { @usage (
				value = "Here is an example of a species definition with a FSM architecture and the additional skill moving:",
				examples = { @example (
						value = "species ant skills: [moving] control: fsm {",
						isExecutable = false) }),
				@usage (
						value = "In the case of a species aiming at mirroring another one:",
						examples = { @example (
								value = "species node_agent mirrors: list(bug) parent: graph_node edge_species: edge_agent {",
								isExecutable = false) }),
				@usage (
						value = "The definition of the single grid of a model will automatically create gridwidth x gridheight agents:",
						examples = { @example (
								value = "grid ant_grid width: gridwidth height: gridheight file: grid_file neighbors: 8 use_regular_agents: false { ",
								isExecutable = false) }),
				@usage (
						value = "Using a file to initialize the grid can replace width/height facets:",
						examples = { @example (
								value = "grid ant_grid file: grid_file neighbors: 8 use_regular_agents: false { ",
								isExecutable = false) }) })
@validator (SpeciesValidator.class)
public class GamlSpecies extends AbstractSpecies {

	public static class SpeciesValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			final IExpression width = desc.getFacetExpr(WIDTH);
			final IExpression height = desc.getFacetExpr(HEIGHT);

			final SpeciesDescription sd = (SpeciesDescription) desc;
			final IExpression cellWidth = desc.getFacetExpr(CELL_WIDTH);
			final IExpression cellHeight = desc.getFacetExpr(CELL_HEIGHT);
			if (cellWidth != null && cellHeight == null || cellWidth == null && cellHeight != null) {
				sd.error("'cell_width' and 'cell_height' must be defined together", IGamlIssue.CONFLICTING_FACETS,
						cellWidth == null ? CELL_HEIGHT : CELL_WIDTH);
				return;
			}
			final IExpression neighbours = desc.getFacetExpr(IKeyword.NEIGHBOURS);
			final IExpression neighbors = desc.getFacetExpr(IKeyword.NEIGHBORS);

			if (neighbours != null && neighbors != null) {
				sd.error("'neighbours' and 'neighbors' cannot be defined at the same time",
						IGamlIssue.CONFLICTING_FACETS, NEIGHBOURS);
				return;
			}
			if (neighbours != null && neighbors == null) {
				sd.setFacet(NEIGHBORS, neighbours);
				sd.removeFacets(NEIGHBOURS);
			}
			// Issue 1311
			if (cellWidth != null && width != null) {
				sd.error("'cell_width' and 'width' cannot be defined at the same time", IGamlIssue.CONFLICTING_FACETS,
						WIDTH);
				return;
			}
			if (cellHeight != null && height != null) {
				sd.error("'cell_width' and 'width' cannot be defined at the same time", IGamlIssue.CONFLICTING_FACETS,
						HEIGHT);
				return;
			}

			if (cellHeight != null || cellWidth != null || width != null || height != null || neighbors != null
					|| neighbours != null) {
				if (!desc.getKeyword().equals(IKeyword.GRID)) {
					sd.warning("Facets related to dimensions and neighboring can only be defined in 'grids' definition",
							IGamlIssue.CONFLICTING_FACETS);
				}
			}

			final IExpression file = desc.getFacetExpr(FILE);
			final IExpression files = desc.getFacetExpr(FILES);
			if (file != null && files != null) {
				sd.error(
						"The use of the 'files' facet prohibits the use of the 'files' facet: if several files have to be loaded in the grid, use the 'files' facet, otherwise use the 'file' facet",
						IGamlIssue.CONFLICTING_FACETS, FILE);
			}
			if ((file != null || files != null)
					&& (height != null || width != null || cellWidth != null || cellHeight != null)) {

				sd.error(
						"The use of the 'file' and 'files' facets prohibit the use of dimension facets ('width', 'height', 'cell_width', 'cell_height')",
						IGamlIssue.CONFLICTING_FACETS, FILE);
			}

			// Issue 1138
			final IExpression freq = desc.getFacetExpr(FREQUENCY);
			if (freq != null && freq.isConst() && Integer.valueOf(0).equals(freq.getConstValue())) {
				for (final VariableDescription vd : sd.getAttributes()) {
					if (vd.getFacet(UPDATE, VALUE) != null) {
						vd.warning(vd.getName() + " will never be updated because " + desc.getName()
								+ " has a scheduling frequency of 0", IGamlIssue.WRONG_CONTEXT);
					}
				}
				for (final IDescription bd : sd.getBehaviors()) {
					bd.warning(bd.getName() + " will never be run because " + desc.getName()
							+ " has a scheduling frequency of 0", IGamlIssue.WRONG_CONTEXT);

				}
			}

			// If torus is declared on a species other than "global", emit a
			// warning
			final IExpression torus = desc.getFacetExpr(TORUS);
			if (torus != null) {
				if (desc.getKeyword().equals(IKeyword.SPECIES) || desc.getKeyword().equals(IKeyword.GRID)) {
					desc.warning("The 'torus' facet can only be specified for the model topology (i.e. in 'global')",
							IGamlIssue.WRONG_CONTEXT, TORUS);
				}
			}
			final String name = desc.getName();
			if (AbstractGamlAdditions.isUnaryOperator(name)) {
				desc.error("The name '" + name + "' cannot be used for naming this " + desc.getKeyword()
						+ ", as the derived casting operator (" + name
						+ "(...)) would conflict with an existing unary operator");
			}

		}
	}

	private final IExpression concurrency;
	private final IExpression schedule;
	private final IExpression frequency;

	public GamlSpecies(final IDescription desc) {
		super(desc);
		concurrency = this.getFacet(IKeyword.PARALLEL);
		if (isMirror() && !hasFacet(IKeyword.SCHEDULES)) {
			// See Issue #2731 -- mirror species have a default scheduling rule
			schedule = scope -> {
				final IList<IAgent> agents = GamaListFactory.create();
				for (final IAgent agent : getPopulation(scope)) {
					final Object obj = agent.getDirectVarValue(scope, IKeyword.TARGET);
					if (obj instanceof IAgent) {
						final IAgent target = (IAgent) obj;
						if (!target.dead()) {
							agents.add(agent);
						}
					}

				}
				return agents;
			};
		} else {
			schedule = this.getFacet(IKeyword.SCHEDULES);
		}
		frequency = this.getFacet(IKeyword.FREQUENCY);
	}

	@Override
	public String getArchitectureName() {
		return getLiteral(IKeyword.CONTROL);
	}

	@Override
	public IExpression getFrequency() {
		return frequency;
	}

	@Override
	public IExpression getSchedule() {
		return schedule;
	}

	@Override
	public IExpression getConcurrency() {
		return concurrency;
	}

	/**
	 * Method getSpecies()
	 *
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getSpecies()
	 */
	@Override
	public ISpecies getSpecies() {
		return this;
	}

	/**
	 * Method getAgents()
	 *
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getAgents()
	 */
	@Override
	public IContainer<?, ? extends IAgent> getAgents(final IScope scope) {
		return this;
	}

	@Override
	public boolean hasAgentList() {
		return true;
	}

	/**
	 * Method accept()
	 *
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#accept(msi.gama.runtime.IScope,
	 *      msi.gama.metamodel.shape.IShape, msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		final IPopulation<? extends IAgent> pop = getPopulation(scope);
		return pop == null ? false : pop.accept(scope, source, a);
	}

	@Override
	public boolean containsKey(final IScope scope, final Object o) {
		final IPopulation<? extends IAgent> pop = getPopulation(scope);
		return pop == null ? false : pop.containsKey(scope, o);
	}

	@Override
	public StreamEx<IAgent> stream(final IScope scope) {
		final IPopulation<IAgent> pop = getPopulation(scope);
		return pop == null ? StreamEx.empty() : pop.stream(scope);
	}

	/**
	 * Method filter()
	 *
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.runtime.IScope,
	 *      msi.gama.metamodel.shape.IShape, java.util.Collection)
	 */
	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		final IPopulation<? extends IAgent> pop = getPopulation(scope);
		if (pop != null) {
			pop.filter(scope, source, results);
		}
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.util.IContainer#getGamlType()
	 */
	@Override
	public IContainerType<?> getGamlType() {
		return (IContainerType<?>) getDescription().getSpeciesExpr().getGamlType();
	}

	public boolean belongsToAMicroModel() {
		return getDescription().belongsToAMicroModel();
	}

}
