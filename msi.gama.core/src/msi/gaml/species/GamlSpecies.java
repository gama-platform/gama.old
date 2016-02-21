/*********************************************************************************************
 *
 *
 * 'GamlSpecies.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.species;

import java.util.Collection;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.GamlSpecies.SpeciesValidator;
import msi.gaml.types.*;

/**
 * The Class GamlSpecies. A species specified by GAML attributes
 *
 * @author drogoul
 */
@symbol(name = { IKeyword.SPECIES, IKeyword.GLOBAL, IKeyword.GRID }, kind = ISymbolKind.SPECIES, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT, ISymbolKind.SPECIES })
@facets(
	value = { @facet(name = IKeyword.WIDTH,
		type = IType.INT,
		optional = true,
		doc = @doc("(grid only), the width of the grid (in terms of agent number)")),
		@facet(name = IKeyword.HEIGHT,
			type = IType.INT,
			optional = true,
			doc = @doc("(grid only),  the height of the grid (in terms of agent number)")),
		@facet(name = IKeyword.CELL_WIDTH,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("(grid only), the width of the cells of the grid")),
		@facet(name = IKeyword.CELL_HEIGHT,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("(grid only), the height of the cells of the grid")),
		@facet(name = IKeyword.NEIGHBOURS,
			type = IType.INT,
			optional = true,
			doc = @doc(value = "(grid only), the chosen neighbourhood (4, 6 or 8)",
				deprecated = "use neighbors instead")),
		@facet(name = IKeyword.NEIGHBORS,
			type = IType.INT,
			optional = true,
			doc = @doc("(grid only), the chosen neighbourhood (4, 6 or 8)")),
		@facet(name = "use_individual_shapes",
			type = IType.BOOL,
			optional = true,
			doc = { @doc(
				value = "(grid only),(true by default). Allows to specify whether or not the agents of the grid will have distinct geometries. If set to false, they will all have simpler proxy geometries",
				see = "use_regular_agents",
				comment = "This facet, when set to true, allows to save memory by generating only one reference geometry and proxy geometries for the agents") }),
		@facet(name = "use_regular_agents",
			type = IType.BOOL,
			optional = true,
			doc = { @doc(
				value = "(grid only),(true by default). Allows to specify if the agents of the grid are regular agents (like those of any other species) or minimal ones (which can't have sub-populations, can't inherit from a regular species, etc.)") }),
		@facet(name = "use_neighbours_cache",
			type = IType.BOOL,
			optional = true,
			doc = { @doc(
				value = "(grid only),(true by default). Allows to turn on or off the use of the neighbours cache used for grids. Note that if a diffusion of variable occurs, GAMA will emit a warning and automatically switch to a caching version") }),
		@facet(name = IKeyword.FILE,
			type = IType.FILE,
			optional = true,
			doc = @doc("(grid only), a bitmap file that will be loaded at runtime so that the value of each pixel  can be assigned to the attribute 'grid_value'")),
		@facet(name = IKeyword.TORUS,
			type = IType.BOOL,
			optional = true,
			doc = @doc("is the topology toric (defaut: false). Needs to be defined on the global species.")),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = false, doc = @doc("the identifier of the species")),
		@facet(name = IKeyword.PARENT,
			type = IType.SPECIES,
			optional = true,
			doc = @doc("the parent class (inheritance)")),
		@facet(name = IKeyword.EDGE_SPECIES,
			type = IType.SPECIES,
			optional = true,
			doc = @doc("In the case of a species defining a graph topology for its instances (nodes of the graph), specifies the species to use for representing the edges")),
		@facet(name = IKeyword.SKILLS,
			type = IType.LIST,
			of = IType.SKILL,
			optional = true,
			doc = @doc("The list of skills that will be made available to the instances of this species. Each new skill provides attributes and actions that will be added to the ones defined in this species")),
		@facet(name = IKeyword.MIRRORS,
			type = { IType.LIST, IType.SPECIES },
			of = IType.AGENT,
			optional = true,
			doc = @doc("The species this species is mirroring. The population of this current species will be dependent of that of the species mirrored (i.e. agents creation and death are entirely taken in charge by GAMA with respect to the demographics of the species mirrored). In addition, this species is provided with an attribute called 'target', which allows each agent to know which agent of the mirrored species it is representing.")),
		@facet(name = IKeyword.CONTROL,
			type = IType.SKILL,
			optional = true,
			doc = @doc("defines the architecture of the species (e.g. fsm...)")),
		@facet(name = "compile", type = IType.BOOL, optional = true, doc = @doc(""), internal = true),
		@facet(name = IKeyword.FREQUENCY,
			type = IType.INT,
			optional = true,
			doc = @doc(
				value = "The execution frequency of the species (default value: 1). For instance, if frequency is set to 10, the population of agents will be executed only every 10 cycles.",
				see = { "schedules" })),
		@facet(name = IKeyword.SCHEDULES,
			type = IType.CONTAINER,
			optional = true,
			doc = @doc("A container of agents (a species, a dynamic list, or a combination of species and containers) , which represents which agents will be actually scheduled when the population is scheduled for execution. For instance, 'species a schedules: (10 among a)' will result in a population that schedules only 10 of its own agents every cycle. 'species b schedules: []' will prevent the agents of 'b' to be scheduled. Note that the scope of agents covered here can be larger than the population, which allows to build complex scheduling controls; for instance, defining 'global schedules: [] {...} species b schedules: []; species c schedules: b + world; ' allows to simulate a model where the agents of b are scheduled first, followed by the world, without even having to create an instance of c.")),
		@facet(name = IKeyword.TOPOLOGY,
			type = IType.TOPOLOGY,
			optional = true,
			doc = @doc("The topology of the population of agents defined by this species. In case of nested species, it can for example be the shape of the macro-agent. In case of grid or graph species, the topology is automatically computed and cannot be redefined")) },
	omissible = IKeyword.NAME)
@doc(
	value = "The species statement allows modelers to define new species in the model. `" + IKeyword.GLOBAL +
		"` and `" + IKeyword.GRID + "` are speciel cases of species: `" + IKeyword.GLOBAL +
		"` being the definition of the global agent (which has automatically one instance, world) and `" +
		IKeyword.GRID + "` being a species with a grid topology.",
	usages = {
		@usage(
			value = "Here is an example of a species definition with a FSM architecture and the additional skill moving:",
			examples = { @example(value = "species ant skills: [moving] control: fsm {", isExecutable = false) }),
		@usage(value = "In the case of a species aiming at mirroring another one:",
			examples = {
				@example(value = "species node_agent mirrors: list(bug) parent: graph_node edge_species: edge_agent {",
					isExecutable = false) }),
		@usage(
			value = "The definition of the single grid of a model will automatically create gridwidth x gridheight agents:",
			examples = { @example(
				value = "grid ant_grid width: gridwidth height: gridheight file: grid_file neighbours: 8 use_regular_agents: false { ",
				isExecutable = false) }),
		@usage(value = "Using a file to initialize the grid can replace width/height facets:",
			examples = { @example(value = "grid ant_grid file: grid_file neighbours: 8 use_regular_agents: false { ",
				isExecutable = false) }) })
@validator(SpeciesValidator.class)
public class GamlSpecies extends AbstractSpecies {

	public static class SpeciesValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			IExpression width = desc.getFacets().getExpr(WIDTH);
			IExpression height = desc.getFacets().getExpr(HEIGHT);

			SpeciesDescription sd = (SpeciesDescription) desc;
			IExpression cellWidth = desc.getFacets().getExpr(CELL_WIDTH);
			IExpression cellHeight = desc.getFacets().getExpr(CELL_HEIGHT);
			if ( cellWidth != null && cellHeight == null || cellWidth == null && cellHeight != null ) {
				sd.error("'cell_width' and 'cell_height' must be defined together", IGamlIssue.CONFLICTING_FACETS,
					cellWidth == null ? CELL_HEIGHT : CELL_WIDTH);
				return;
			}
			IExpression neighbours = desc.getFacets().getExpr(IKeyword.NEIGHBOURS);
			IExpression neighbors = desc.getFacets().getExpr(IKeyword.NEIGHBORS);

			if ( neighbours != null && neighbors != null ) {
				sd.error("'neighbours' and 'neighbors' cannot be defined at the same time",
					IGamlIssue.CONFLICTING_FACETS, NEIGHBOURS);
				return;
			}
			// Issue 1311
			if ( cellWidth != null && width != null ) {
				sd.error("'cell_width' and 'width' cannot be defined at the same time", IGamlIssue.CONFLICTING_FACETS,
					WIDTH);
				return;
			}
			if ( cellHeight != null && height != null ) {
				sd.error("'cell_width' and 'width' cannot be defined at the same time", IGamlIssue.CONFLICTING_FACETS,
					HEIGHT);
				return;
			}

			IExpression file = desc.getFacets().getExpr(FILE);

			if ( file != null && (height != null || width != null || cellWidth != null || cellHeight != null) ) {
				sd.error(
					"The use of the 'file' facet prohibits the use of dimension facets ('width', 'height', 'cell_width', 'cell_height')",
					IGamlIssue.CONFLICTING_FACETS, FILE);
			}

			// Issue 1138
			IExpression freq = desc.getFacets().getExpr(FREQUENCY);
			if ( freq != null && freq.isConst() && Integer.valueOf(0).equals(freq.value(null)) ) {
				for ( VariableDescription vd : sd.getVariables().values() ) {
					if ( vd.getFacets().getDescr(UPDATE, VALUE) != null ) {
						vd.warning(vd.getName() + " will never be updated because " + desc.getName() +
							" has a scheduling frequency of 0", IGamlIssue.WRONG_CONTEXT);
					}
				}
				for ( IDescription bd : sd.getBehaviors() ) {
					bd.warning(bd.getName() + " will never be run because " + desc.getName() +
						" has a scheduling frequency of 0", IGamlIssue.WRONG_CONTEXT);

				}
			}
			// If torus is declared on a species other than "global", emit a warning
			IExpression torus = desc.getFacets().getExpr(TORUS);
			if ( torus != null ) {
				if ( desc.getKeyword().equals(IKeyword.SPECIES) || desc.getKeyword().equals(IKeyword.GRID) ) {
					desc.warning("The 'torus' facet can only be specified for the model topology (i.e. in 'global')",
						IGamlIssue.WRONG_CONTEXT, TORUS);
				}
			}
			String name = desc.getName();
			if ( AbstractGamlAdditions.isUnaryOperator(name) ) {
				desc.error("The name '" + name + "' cannot be used for naming this " + desc.getKeyword() +
					", as the derived casting operator (" + name +
					"(...)) would conflict with an existing unary operator");
			}
		}
	}

	public GamlSpecies(final IDescription desc) {
		super(desc);
	}

	@Override
	public String getArchitectureName() {
		return getLiteral(IKeyword.CONTROL);
	}

	@Override
	public IExpression getFrequency() {
		return this.getFacet(IKeyword.FREQUENCY);
	}

	@Override
	public IExpression getSchedule() {
		return this.getFacet(IKeyword.SCHEDULES);
	}

	/**
	 * Method getSpecies()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getSpecies()
	 */
	@Override
	public ISpecies getSpecies() {
		return this;
	}

	/**
	 * Method getAgents()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getAgents()
	 */
	@Override
	public IContainer<?, ? extends IShape> getAgents(final IScope scope) {
		return this;
	}

	/**
	 * Method accept()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#accept(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		return getPopulation(scope).accept(scope, source, a);
	}

	/**
	 * Method filter()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, java.util.Collection)
	 */
	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		getPopulation(scope).filter(scope, source, results);
	}

	/**
	 * Method getType()
	 * @see msi.gama.util.IContainer#getType()
	 */
	@Override
	public IContainerType getType() {
		return Types.SPECIES;
	}

}
