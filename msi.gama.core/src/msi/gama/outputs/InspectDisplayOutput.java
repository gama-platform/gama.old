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

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

/**
 * The Class AbstractInspectOutput.
 * 
 * @author drogoul
 */
@SuppressWarnings("unchecked")
@symbol(name = { IKeyword.INSPECT, IKeyword.BROWSE }, kind = ISymbolKind.OUTPUT, with_sequence = false)
@inside(symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.NONE, optional = false, doc = @doc("the identifier of the inspector")),
	@facet(name = IKeyword.REFRESH_EVERY,
		type = IType.INT,
		optional = true,
		doc = @doc(value = "Allows to refresh the inspector every n time steps (default is 1)",
			deprecated = "Use refresh: every(n) instead")),
	@facet(name = IKeyword.REFRESH,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Indicates the condition under which this output should be refreshed (default is true)")),
	@facet(name = IKeyword.VALUE,
		type = IType.NONE,
		optional = true,
		doc = @doc("the set of agents to inspect, could be a species, a list of agents or an agent")),
	@facet(name = IKeyword.ATTRIBUTES,
		type = IType.LIST,
		optional = true,
		doc = @doc("the list of attributes to inspect")),
	@facet(name = IKeyword.TYPE, type = IType.ID, values = { IKeyword.AGENT, IKeyword.SPECIES, IKeyword.POPULATION,
		IKeyword.TABLE }, optional = true, doc = @doc("the way to inspect agents: in a table, or a set of inspectors")) },
	omissible = IKeyword.NAME)
@doc(value = "`" +
	IKeyword.INSPECT +
	"` (and `" +
	IKeyword.BROWSE +
	"`) statements allows modeler to inspect a set of agents, in a table with agents and all their attributes or an agent inspector per agent, depending on the type: chosen. Modeler can choose which attributes to display. When `" +
	IKeyword.BROWSE + "` is used, type: default value is table, whereas when`" + IKeyword.INSPECT +
	"` is used, type: default value is agent.",
	usages = { @usage(value = "An example of syntax is:",
		examples = { @example(value = "inspect \"my_inspector\" value: ant attributes: [\"name\", \"location\"];",
			isExecutable = false) }) })
public class InspectDisplayOutput extends MonitorOutput {

	public static final short INSPECT_AGENT = 0;
	public static final short INSPECT_TABLE = 3;

	static int count = 0;

	static final List<String> types = Arrays.asList(IKeyword.AGENT, IKeyword.DYNAMIC, IKeyword.SPECIES, IKeyword.TABLE);

	String type;
	IExpression attributes;
	private List<String> listOfAttributes;
	IMacroAgent rootAgent;

	public static void browse(final Collection<IAgent> agents) {
		IPopulation pop = null;
		if ( agents instanceof IPopulation ) {
			pop = (IPopulation) agents;
			browse(pop.getHost(), pop.getSpecies());
		} else {
			for ( IAgent agent : agents ) {
				IPopulation agentPop = agent.getPopulation();
				if ( pop == null ) {
					pop = agentPop;
				} else if ( agentPop != pop ) {
					pop = null;
					break;
				}
			}
			browse(pop == null ? GAMA.getSimulation() : pop.getHost(), agents);
		}
	}

	public static void browse(final IMacroAgent root, final Collection<IAgent> agents) {
		new InspectDisplayOutput(root, agents).launch();
	}

	public static void browse(final IMacroAgent root, final ISpecies species) {
		new InspectDisplayOutput(root, species).launch();
	}

	public static void browse(final IMacroAgent root, final IExpression expr) {
		new InspectDisplayOutput(root, expr).launch();
	}

	public InspectDisplayOutput(final IDescription desc) {
		super(desc);
		if ( getValue() == null ) {
			value = getFacet(IKeyword.NAME);
			expressionText = getValue() == null ? "" : getValue().toGaml();
		}
		type = getLiteral(IKeyword.TYPE);
		if ( type == null ) {
			if ( getLiteral(IKeyword.KEYWORD).equals(IKeyword.BROWSE) ) {
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
		if ( type.equals(IKeyword.AGENT) && getValue() != null ) {
			lastValue = getValue().value(scope);
		}
		if ( attributes != null ) {
			listOfAttributes = Cast.asList(scope, attributes.value(scope));
		}
		if ( rootAgent == null || rootAgent.dead() ) {
			rootAgent = scope.getSimulationScope();
			// GuiUtils.debug("InspectDisplayOutput.init rootAgent = " + rootAgent);
		}
		return true;
	}

	public InspectDisplayOutput(final String name, final short type) {
		// Opens directly an inspector
		this(DescriptionFactory.create(IKeyword.INSPECT, IKeyword.NAME,
			StringUtils.toGamlString(name + (type != INSPECT_TABLE ? count++ : "")), IKeyword.TYPE, types.get(type))
			.validate());
	}

	private InspectDisplayOutput(final IMacroAgent rootAgent, final ISpecies species) {
		// Opens a table inspector on the agents of this species
		this(DescriptionFactory.create(
			IKeyword.INSPECT,
			GAML.getExperimentContext(rootAgent),
			IKeyword.NAME,
			species == null ? StringUtils.toGamlString("Custom " + count++) : StringUtils.toGamlString(species
				.getName()), IKeyword.VALUE, species == null ? "nil" : species.getName(), IKeyword.TYPE,
			types.get(INSPECT_TABLE)).validate());
		this.rootAgent = rootAgent;
	}

	private InspectDisplayOutput(final IMacroAgent agent, final Collection<IAgent> agents) {
		// Opens a table inspector on the agents of this container
		this(DescriptionFactory.create(IKeyword.INSPECT, GAML.getExperimentContext(agent), IKeyword.NAME,
			StringUtils.toGamlString("Custom " + count++), IKeyword.VALUE, Cast.toGaml(agents), IKeyword.TYPE,
			types.get(INSPECT_TABLE)).validate());
		lastValue = agents;
		this.rootAgent = agent;
	}

	private InspectDisplayOutput(final IMacroAgent agent, final IExpression agents) {
		// Opens a table inspector on the agents of this container
		this(DescriptionFactory.create(IKeyword.INSPECT, GAML.getExperimentContext(agent), IKeyword.NAME,
			StringUtils.toGamlString("Custom " + count++), IKeyword.VALUE, Cast.toGaml(agents), IKeyword.TYPE,
			types.get(INSPECT_TABLE)).validate());
		// lastValue = agents;
		this.rootAgent = agent;
	}

	public void launch() throws GamaRuntimeException {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				if ( !scope.init(InspectDisplayOutput.this) ) { return; }
				GAMA.getExperiment().getSimulationOutputs().addOutput(InspectDisplayOutput.this);
				resume();
				open();
				step(scope);
				update();
			}
		});

	}

	@Override
	public boolean step(final IScope scope) {
		if ( IKeyword.TABLE.equals(type) ) {
			if ( rootAgent == null || rootAgent.dead() ) { return false; }
			boolean pushed = scope.push(rootAgent);
			try {
				return super.step(scope);
			} finally {
				if ( pushed ) {
					scope.pop(rootAgent);
				}
			}

		}
		return true;
		// super.step(scope);
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
		if ( IKeyword.TABLE.equals(type) ) { return GuiUtils.TABLE_VIEW_ID; }
		return GuiUtils.AGENT_VIEW_ID;

	}

	//
	@Override
	public List<IAgent> getLastValue() {
		if ( IKeyword.TABLE.equals(type) ) {
			if ( rootAgent == null || rootAgent.dead() ) { return Collections.EMPTY_LIST; }
		}
		if ( lastValue instanceof IAgent ) { return GamaList.with((IAgent) lastValue); }
		if ( lastValue instanceof ISpecies && rootAgent != null ) { return rootAgent
			.getMicroPopulation((ISpecies) lastValue); }
		if ( lastValue instanceof IContainer ) { return ((IContainer) lastValue).listValue(getScope(), Types.NO_TYPE); }
		return null;
	}

	public ISpecies getSpecies() {
		if ( getValue() == null ) { return null; }
		return GAMA.getModel().getSpecies(getValue().getType().getContentType().getSpeciesName());
	}

	public List<String> getAttributes() {
		return listOfAttributes;
	}

	public IMacroAgent getRootAgent() {
		return rootAgent;
	}

}
