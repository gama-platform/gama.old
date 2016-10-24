/*********************************************************************************************
 *
 *
 * 'InspectDisplayOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.IContainer;
import msi.gaml.compilation.SymbolTracer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class AbstractInspectOutput.
 *
 * @author drogoul
 */
@symbol(name = { IKeyword.INSPECT, IKeyword.BROWSE }, kind = ISymbolKind.OUTPUT, with_sequence = false, concept = {
		IConcept.INSPECTOR })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = { IKeyword.OUTPUT,
		IKeyword.PERMANENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.NONE, optional = false, doc = @doc("the identifier of the inspector")),
		@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true, doc = @doc(value = "Allows to refresh the inspector every n time steps (default is 1)", deprecated = "Use refresh: every(n) instead")),
		@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true, doc = @doc("Indicates the condition under which this output should be refreshed (default is true)")),
		@facet(name = IKeyword.VALUE, type = IType.NONE, optional = true, doc = @doc("the set of agents to inspect, could be a species, a list of agents or an agent")),
		@facet(name = IKeyword.ATTRIBUTES, type = IType.LIST, optional = true, doc = @doc("the list of attributes to inspect")),
		@facet(name = IKeyword.TYPE, type = IType.ID, values = { IKeyword.AGENT,
				IKeyword.TABLE }, optional = true, doc = @doc("the way to inspect agents: in a table, or a set of inspectors")) }, omissible = IKeyword.NAME)
@doc(value = "`" + IKeyword.INSPECT + "` (and `" + IKeyword.BROWSE
		+ "`) statements allows modeler to inspect a set of agents, in a table with agents and all their attributes or an agent inspector per agent, depending on the type: chosen. Modeler can choose which attributes to display. When `"
		+ IKeyword.BROWSE + "` is used, type: default value is table, whereas when`" + IKeyword.INSPECT
		+ "` is used, type: default value is agent.", usages = { @usage(value = "An example of syntax is:", examples = {
				@example(value = "inspect \"my_inspector\" value: ant attributes: [\"name\", \"location\"];", isExecutable = false) }) })
@SuppressWarnings({ "rawtypes" })
public class InspectDisplayOutput extends MonitorOutput implements IStatement {

	public static final short INSPECT_AGENT = 0;
	public static final short INSPECT_TABLE = 3;

	static int count = 0;

	static final List<String> types = Arrays.asList(IKeyword.AGENT, IKeyword.DYNAMIC, IKeyword.SPECIES, IKeyword.TABLE);

	String type;
	IExpression attributes;
	private List<String> listOfAttributes;
	IMacroAgent rootAgent;

	public static void browse(final Collection<? extends IAgent> agents) {
		IPopulation pop = null;
		IMacroAgent root = null;
		if (agents instanceof IPopulation) {
			pop = (IPopulation) agents;
			root = pop.getHost();
			browse(pop.getHost(), pop.getSpecies());
		} else {
			for (final IAgent agent : agents) {
				final IPopulation agentPop = agent.getPopulation();
				root = agentPop.getHost();
				if (root != null)
					break;
			}
			if (root == null)
				return;
			browse(root, agents);
		}
	}

	public static void browse(final IMacroAgent root, final Collection<? extends IAgent> agents) {
		final IMacroAgent realRoot = findRootOf(root, agents);
		if (realRoot == null) {
			GamaRuntimeException.error("Impossible to find a common host agent for " + agents, root.getScope());
			return;
		}
		new InspectDisplayOutput(realRoot, agents).launch(realRoot.getScope());
	}

	private static IMacroAgent findRootOf(final IMacroAgent root, final Collection<? extends IAgent> agents) {
		if (agents instanceof IPopulation) {
			return ((IPopulation) agents).getHost();
		}
		IMacroAgent result = null;
		for (final IAgent a : agents) {
			if (result == null) {
				result = a.getHost();
			} else {
				if (a.getHost() != result)
					return null;
			}
		}
		return result;

	}

	public static void browse(final IMacroAgent root, final ISpecies species) {
		if (!root.getSpecies().getMicroSpecies().contains(species)) {
			if (root instanceof ExperimentAgent) {
				final IMacroAgent realRoot = ((ExperimentAgent) root).getSimulation();
				browse(realRoot, species);
			} else {
				GamaRuntimeException.error("Agent " + root + " has no access to populations of " + species.getName(),
						root.getScope());
			}
			return;
		}
		new InspectDisplayOutput(root, species).launch(root.getScope());
	}

	public static void browse(final IMacroAgent root, final IExpression expr) {
		final SpeciesDescription species = expr.getType().isContainer() ? expr.getType().getContentType().getSpecies()
				: expr.getType().getSpecies();
		if (species == null) {
			GamaRuntimeException.error("Expression '" + expr.serialize(true) + "' does not reference agents",
					root.getScope());
			return;
		}
		final ISpecies rootSpecies = root.getSpecies();
		if (rootSpecies.getMicroSpecies(species.getName()) == null) {
			if (root instanceof ExperimentAgent) {
				final IMacroAgent realRoot = ((ExperimentAgent) root).getSimulation();
				browse(realRoot, expr);
			} else {
				GamaRuntimeException.error("Agent " + root + " has no access to populations of " + species.getName(),
						root.getScope());
			}
			return;
		}
		new InspectDisplayOutput(root, expr).launch(root.getScope());
	}

	public InspectDisplayOutput(final IDescription desc) {
		super(desc);
		if (getValue() == null) {
			value = getFacet(IKeyword.NAME);
			expressionText = getValue() == null ? "" : getValue().serialize(false);
		}
		type = getLiteral(IKeyword.TYPE);
		if (type == null) {
			if (getKeyword().equals(IKeyword.BROWSE)) {
				type = IKeyword.TABLE;
			} else {
				type = IKeyword.AGENT;
			}
		}
		attributes = getFacet(IKeyword.ATTRIBUTES);
	}

	@Override
	public boolean init(final IScope scope) {
		super.init(scope);
		if (type.equals(IKeyword.AGENT) && getValue() != null) {
			lastValue = getValue().value(getScope());
		}
		if (attributes != null) {
			listOfAttributes = Cast.asList(getScope(), attributes.value(getScope()));
		}
		if (rootAgent == null || rootAgent.dead()) {
			rootAgent = getScope().getRoot();
			// scope.getGui().debug("InspectDisplayOutput.init rootAgent = " +
			// rootAgent);
		}
		return true;
	}

	public InspectDisplayOutput(final IAgent a) {
		// Opens directly an inspector
		this(DescriptionFactory.create(IKeyword.INSPECT, IKeyword.NAME, StringUtils.toGamlString("Inspect: "),
				IKeyword.TYPE, types.get(INSPECT_AGENT)).validate());
		setValue(null);
		lastValue = a;
	}

	private InspectDisplayOutput(final IMacroAgent rootAgent, final ISpecies species) {
		// Opens a table inspector on the agents of this species
		this(DescriptionFactory
				.create(IKeyword.INSPECT, GAML.getExperimentContext(rootAgent), IKeyword.NAME,
						StringUtils.toGamlString("Browse(" + count++ + ")"), IKeyword.VALUE,
						species == null ? "nil" : species.getName(), IKeyword.TYPE, types.get(INSPECT_TABLE))
				.validate());
		this.rootAgent = rootAgent;
	}

	private InspectDisplayOutput(final IMacroAgent agent, final Collection<? extends IAgent> agents) {
		// Opens a table inspector on the agents of this container
		this(DescriptionFactory.create(IKeyword.INSPECT, GAML.getExperimentContext(agent), IKeyword.NAME,
				StringUtils.toGamlString("Browse(" + count++ + ")"), IKeyword.VALUE, Cast.toGaml(agents), IKeyword.TYPE,
				types.get(INSPECT_TABLE)).validate());
		lastValue = agents;
		this.rootAgent = agent;
	}

	private InspectDisplayOutput(final IMacroAgent agent, final IExpression agents) {
		// Opens a table inspector on the agents of this container
		this(DescriptionFactory.create(IKeyword.INSPECT, GAML.getExperimentContext(agent), IKeyword.NAME,
				StringUtils.toGamlString("Browse(" + count++ + ")"), IKeyword.VALUE, Cast.toGaml(agents), IKeyword.TYPE,
				types.get(INSPECT_TABLE)).validate());
		// lastValue = agents;
		this.rootAgent = agent;
	}

	public void launch(final IScope scope) throws GamaRuntimeException {
		if (!scope.init(InspectDisplayOutput.this).passed()) {
			return;
		}
		// What to do in case of multi-simulations ???
		scope.getSimulation().addOutput(InspectDisplayOutput.this);
		setPaused(false);
		open();
		step(scope);
		update();
	}

	@Override
	public boolean step(final IScope scope) {
		// ((AbstractScope) getScope()).traceAgents = true;
		if (IKeyword.TABLE.equals(type)) {
			if (rootAgent == null || rootAgent.dead()) {
				return false;
			}
			if (getValue() == null) {
				return true;
			}
			if (getScope().interrupted()) {
				return false;
			}
			getScope().setCurrentSymbol(this);
			lastValue = getScope().evaluate(getValue(), rootAgent).getValue();
		}
		return true;
	}

	@Override
	public boolean isUnique() {
		return /* target != INSPECT_DYNAMIC && */!type.equals(IKeyword.TABLE);
	}

	@Override
	public String getId() {
		return isUnique() ? getViewId() : getViewId() + getName();
	}

	@Override
	public String getViewId() {
		if (IKeyword.TABLE.equals(type)) {
			return IGui.TABLE_VIEW_ID;
		}
		return IGui.AGENT_VIEW_ID;

	}

	final static IAgent[] EMPTY = new IAgent[0];

	@Override
	public IAgent[] getLastValue() {
		if (IKeyword.TABLE.equals(type)) {
			if (rootAgent == null || rootAgent.dead()) {
				return EMPTY;
			}
		}
		// System.out.println("Last value :" + lastValue);
		if (lastValue instanceof IAgent) {
			return new IAgent[] { (IAgent) lastValue };
		}
		if (lastValue instanceof ISpecies && rootAgent != null) {
			final IPopulation pop = rootAgent.getMicroPopulation((ISpecies) lastValue);
			final IAgent[] result = pop.toArray();
			return result;
		}
		if (lastValue instanceof IContainer) {
			return ((IContainer<?, ?>) lastValue).listValue(getScope(), Types.NO_TYPE, false).toArray(new IAgent[0]);
		}
		return EMPTY;
	}

	public ISpecies getSpecies() {
		if (getValue() == null) {
			return null;
		}
		final SpeciesDescription sd = getValue().getType().getContentType().getSpecies();
		if (sd == null) {
			return getScope().getModel().getSpecies(IKeyword.AGENT);
		}
		if (sd.equals(getScope().getModel().getDescription())) {
			return getScope().getModel().getSpecies();
		}
		String speciesName = sd.getName();
		if (speciesName == null) {
			speciesName = IKeyword.AGENT;
		}
		return rootAgent.getSpecies().getMicroSpecies(speciesName);
	}

	public List<String> getAttributes() {
		return listOfAttributes;
	}

	public IMacroAgent getRootAgent() {
		return rootAgent;
	}

	@Override
	public void dispose() {
		super.dispose();
		rootAgent = null;
		attributes = null;
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		final IType type = value.getType();
		if (type.isAgentType()) {
			GAMA.getGui().setSelectedAgent((IAgent) value.value(scope));
		} else if (type.isContainer()) {
			browse(scope.getRoot(), value);
		}
		return value.value(scope);
	}

	@Override
	public String getTrace(final IScope scope) {
		return new SymbolTracer().trace(scope, this);
	}

}
