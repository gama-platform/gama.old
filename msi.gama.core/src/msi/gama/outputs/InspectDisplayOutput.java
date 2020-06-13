/*******************************************************************************************************
 *
 * msi.gama.outputs.InspectDisplayOutput.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
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
import msi.gama.util.IContainer;
import msi.gaml.compilation.GAML;
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
@symbol (
		name = { IKeyword.INSPECT, IKeyword.BROWSE },
		kind = ISymbolKind.OUTPUT,
		with_sequence = false,
		concept = { IConcept.INSPECTOR })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT },
		symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.NONE,
				optional = false,
				doc = @doc ("the identifier of the inspector")),
				@facet (
						name = IKeyword.REFRESH_EVERY,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = "Allows to refresh the inspector every n time steps (default is 1)",
								deprecated = "Use refresh: every(n) instead")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates the condition under which this output should be refreshed (default is true)")),
				@facet (
						name = IKeyword.VALUE,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the set of agents to inspect, could be a species, a list of agents or an agent")),
				@facet (
						name = IKeyword.ATTRIBUTES,
						type = { IType.LIST },
						optional = true,
						doc = @doc ("the list of attributes to inspect. A list that can contain strings or pair<string,type>, or a mix of them. These can be variables of the species, but also attributes present in the attributes table of the agent. The type is necessary in that case")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.ID,
						values = { IKeyword.AGENT, IKeyword.TABLE },
						optional = true,
						doc = @doc ("the way to inspect agents: in a table, or a set of inspectors")) },
		omissible = IKeyword.NAME)
@doc (
		value = "`" + IKeyword.INSPECT + "` (and `" + IKeyword.BROWSE
				+ "`) statements allows modeler to inspect a set of agents, in a table with agents and all their attributes or an agent inspector per agent, depending on the type: chosen. Modeler can choose which attributes to display. When `"
				+ IKeyword.BROWSE + "` is used, type: default value is table, whereas when`" + IKeyword.INSPECT
				+ "` is used, type: default value is agent.",
		usages = { @usage (
				value = "An example of syntax is:",
				examples = { @example (
						value = "inspect \"my_inspector\" value: ant attributes: [\"name\", \"location\"];",
						isExecutable = false) }) })
@SuppressWarnings ({ "rawtypes" })
public class InspectDisplayOutput extends AbstractValuedDisplayOutput implements IStatement {

	public static final short INSPECT_AGENT = 0;
	public static final short INSPECT_TABLE = 3;

	static int count = 0;

	static final List<String> types = Arrays.asList(IKeyword.AGENT, IKeyword.DYNAMIC, IKeyword.SPECIES, IKeyword.TABLE);

	String type;
	IExpression attributes;
	private Map<String, String> listOfAttributes;
	IMacroAgent rootAgent;

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
			listOfAttributes = (Map<String, String>) Types.MAP.of(Types.STRING, Types.STRING).cast(getScope(),
					attributes.value(getScope()), null, true);
		}
		if (rootAgent == null || rootAgent.dead()) {
			rootAgent = getScope().getRoot();
		}
		return true;
	}

	public InspectDisplayOutput(final IAgent a) {
		// Opens directly an inspector
		this(DescriptionFactory.create(IKeyword.INSPECT, IKeyword.NAME, StringUtils.toGamlString("Inspect: "),
				IKeyword.TYPE, types.get(INSPECT_AGENT), IKeyword.VALUE,
				StringUtils.toGamlString(a.getSpeciesName() + " at " + a.getIndex())).validate());

		setValue(GAML.getExpressionFactory().createConst(a, a.getGamlType()));
		lastValue = a;
	}

	public InspectDisplayOutput(final IExperimentAgent a) {
		// Opens directly an inspector
		this(DescriptionFactory.create(IKeyword.INSPECT, IKeyword.NAME, StringUtils.toGamlString("Inspect: "),
				IKeyword.TYPE, types.get(INSPECT_TABLE), IKeyword.VALUE, "experiment.simulations").validate());

		final SimulationPopulation sp = a.getSimulationPopulation();
		setValue(GAML.getExpressionFactory().createConst(sp, sp.getGamlType()));
		lastValue = sp;
		rootAgent = a;
	}

	InspectDisplayOutput(final IMacroAgent rootAgent, final ISpecies species) {
		// Opens a table inspector on the agents of this species
		this(DescriptionFactory
				.create(IKeyword.INSPECT, GAML.getExperimentContext(rootAgent), IKeyword.NAME,
						StringUtils.toGamlString("Browse(" + count++ + ")"), IKeyword.VALUE,
						species == null ? "nil" : species.getName(), IKeyword.TYPE, types.get(INSPECT_TABLE))
				.validate());
		this.rootAgent = rootAgent;
	}

	InspectDisplayOutput(final IMacroAgent agent, final Collection<? extends IAgent> agents) {
		// Opens a table inspector on the agents of this container
		this(DescriptionFactory.create(IKeyword.INSPECT, GAML.getExperimentContext(agent), IKeyword.NAME,
				StringUtils.toGamlString("Browse(" + count++ + ")"), IKeyword.VALUE, Cast.toGaml(agents), IKeyword.TYPE,
				types.get(INSPECT_TABLE)).validate());
		lastValue = agents;
		this.rootAgent = agent;
	}

	InspectDisplayOutput(final IMacroAgent agent, final IExpression agents) {
		// Opens a table inspector on the agents of this container
		this(DescriptionFactory.create(IKeyword.INSPECT, GAML.getExperimentContext(agent), IKeyword.NAME,
				StringUtils.toGamlString("Browse(" + count++ + ")"), IKeyword.VALUE, Cast.toGaml(agents), IKeyword.TYPE,
				types.get(INSPECT_TABLE)).validate());
		// lastValue = agents;
		this.rootAgent = agent;
	}

	public void launch(final IScope scope) throws GamaRuntimeException {
		if (!scope.init(InspectDisplayOutput.this).passed()) { return; }
		// TODO What to do in case of multi-simulations ???
		if (scope.getSimulation() != null) {
			scope.getSimulation().addOutput(InspectDisplayOutput.this);
		} else if (scope.getExperiment() != null) {
			scope.getExperiment().getSpecies().getExperimentOutputs().add(InspectDisplayOutput.this);
		}
		setPaused(false);
		open();
		step(scope);
		update();
	}

	@Override
	public boolean step(final IScope scope) {
		// ((AbstractScope) getScope()).traceAgents = true;
		if (IKeyword.TABLE.equals(type)) {
			if (rootAgent == null || rootAgent.dead()) { return false; }
			if (getValue() == null) { return true; }
			if (getScope().interrupted()) { return false; }
			getScope().setCurrentSymbol(this);
			lastValue = getScope().evaluate(getValue(), rootAgent).getValue();
		}
		return true;
	}

	@Override
	public boolean isUnique() {
		return !type.equals(IKeyword.TABLE);
	}

	@Override
	public String getId() {
		return isUnique() ? getViewId() : getViewId() + getName();
	}

	@Override
	public String getViewId() {
		if (IKeyword.TABLE.equals(type)) { return IGui.TABLE_VIEW_ID; }
		return IGui.AGENT_VIEW_ID;

	}

	final static IAgent[] EMPTY = new IAgent[0];

	@Override
	public IAgent[] getLastValue() {
		if (IKeyword.TABLE.equals(type)) {
			if (rootAgent == null || rootAgent.dead()) { return EMPTY; }
		}
		// DEBUG.LOG("Last value :" + lastValue);
		if (lastValue instanceof IAgent) { return new IAgent[] { (IAgent) lastValue }; }
		if (lastValue instanceof ISpecies && rootAgent != null) {
			final IPopulation pop = rootAgent.getMicroPopulation((ISpecies) lastValue);
			final IAgent[] result = pop.toArray();
			return result;
		}
		if (lastValue instanceof IContainer) { return ((IContainer<?, ?>) lastValue)
				.listValue(getScope(), Types.NO_TYPE, false).toArray(new IAgent[0]); }
		return EMPTY;
	}

	public ISpecies getSpecies() {
		final IExpression valueExpr = getValue();
		if (valueExpr == null) { return null; }
		final IType theType = valueExpr.getGamlType().getContentType();
		if (theType == Types.get(IKeyword.MODEL)) { return getScope().getModel().getSpecies(); }
		final SpeciesDescription sd = theType.getSpecies();
		if (sd == null) { return getScope().getModel().getSpecies(IKeyword.AGENT); }
		if (sd.equals(getScope().getModel().getDescription())) { return getScope().getModel().getSpecies(); }
		String speciesName = sd.getName();
		if (speciesName == null) {
			speciesName = IKeyword.AGENT;
		}
		return rootAgent.getSpecies().getMicroSpecies(speciesName);
	}

	public Map<String, String> getAttributes() {
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
		final IType theType = value.getGamlType();
		if (theType.isAgentType()) {
			GAMA.getGui().setSelectedAgent((IAgent) value.value(scope));
		} else if (theType.isContainer()) {
			ValuedDisplayOutputFactory.browse(scope.getRoot(), value);
		}
		return value.value(scope);
	}

	@Override
	public String getTrace(final IScope scope) {
		return new SymbolTracer().trace(scope, this);
	}

}
