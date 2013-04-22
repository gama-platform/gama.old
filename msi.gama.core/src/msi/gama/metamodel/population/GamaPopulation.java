/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - BenoÔøΩt Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.ContinuousTopology;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.graph.AbstractGraphNodeAgent;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IAspect;
import msi.gaml.types.GamaTopologyType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 6 sept. 2010
 * 
 * @todo Description
 * 
 */
public class GamaPopulation implements IPopulation {

	/** The agent hosting this population which is considered as the direct macro-agent. */
	protected IAgent host;

	/** The object describing how the agents of this population are spatially organized */
	protected ITopology topology;
	protected final ISpecies species;
	protected final String[] orderedVarNames;
	protected final IVariable[] updatableVars;
	protected int currentAgentIndex;

	protected IList<IAgent> agents = new GamaList();
	IExpression scheduleFrequency = new ConstantExpression(1);

	/**
	 * Listeners, created in a lazy way
	 */
	private LinkedList<IPopulation.Listener> listeners = null;

	class PopulationManagement extends ScheduledAction {

		final IExpression listOfTargetAgents;

		PopulationManagement(final IExpression exp) {
			listOfTargetAgents = exp;
		}

		@Override
		public void execute(final IScope scope) throws GamaRuntimeException {
			IPopulation pop = GamaPopulation.this;
			IList<IAgent> targets = Cast.asList(scope, listOfTargetAgents.value(scope));
			IList<IAgent> toKill = new GamaList();
			for ( IAgent agent : pop ) {
				IAgent target = Cast.asAgent(scope, agent.getAttribute("target"));
				if ( targets.contains(target) ) {
					targets.remove(target);
				} else {
					toKill.add(agent);
				}
			}
			for ( IAgent agent : toKill ) {
				agent.die();
			}
			List<Map> attributes = new ArrayList();
			for ( IAgent target : targets ) {
				Map<String, Object> att = new HashMap();
				att.put("target", target);
				attributes.add(att);
			}
			pop.createAgents(scope, targets.size(), attributes, false);
		}

	}

	public GamaPopulation(final IAgent host, final ISpecies species) {
		this.host = host;
		this.species = species;
		TypeDescription ecd = (TypeDescription) species.getDescription();
		orderedVarNames = ecd.getVarNames().toArray(new String[0]);
		List<String> updatableVarNames = ecd.getUpdatableVarNames();
		int updatableVarsSize = updatableVarNames.size();
		updatableVars = new IVariable[updatableVarNames.size()];
		for ( int i = 0; i < updatableVarsSize; i++ ) {
			String s = updatableVarNames.get(i);
			updatableVars[i] = species.getVar(s);
		}
		if ( species.isMirror() ) {
			host.getScheduler().insertEndAction(new PopulationManagement(species.getFacet(IKeyword.MIRRORS)));
		}

		// Add an attribute to the agents (dans SpeciesDescription)
		IExpression exp = species.getFrequency();
		if ( exp != null ) {
			scheduleFrequency = exp;
		}
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		IArchitecture c = species.getArchitecture();
		c.executeOn(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		IArchitecture control = species.getArchitecture();
		control.init(scope);
	}

	@Override
	public void createVariablesFor(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		for ( final String s : orderedVarNames ) {
			final IVariable var = species.getVar(s);
			var.initializeWith(scope, agent, null);
		}
	}

	@Override
	public void updateVariablesFor(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		for ( int i = 0; i < updatableVars.length; i++ ) {
			updatableVars[i].updateFor(scope, agent);
		}
	}

	/**
	 * 
	 * @see msi.gama.interfaces.IPopulation#computeAgentsToSchedule(msi.gama.interfaces.IScope, msi.gama.util.GamaList)
	 */
	@Override
	public void computeAgentsToSchedule(final IScope scope, final IList list) throws GamaRuntimeException {
		int frequency = Cast.asInt(scope, scheduleFrequency.value(scope));
		int step = scope.getClock().getCycle();
		IExpression ags = getSpecies().getSchedule();
		List<IAgent> allAgents = getAgentsList();
		List<IAgent> agents = ags == null ? allAgents : Cast.asList(scope, ags.value(scope));
		if ( step % frequency == 0 ) {
			list.addAll(agents);
		}
		if ( species.hasMicroSpecies() ) {
			for ( IAgent agent : allAgents ) {
				// FIXME: shouldn't it be "agents" rather than "allAgents" ?
				agent.computeAgentsToSchedule(scope, list);
			}
		}
	}

	@Override
	public IAgent getAgent(final Integer index) {
		for ( IAgent a : this ) {
			if ( index.equals(a.getIndex()) ) { return a; }
		}
		return null;
	}

	@Override
	public int compareTo(final IPopulation o) {
		return getName().compareTo(o.getName());
	}

	@Override
	public ITopology getTopology() {
		return topology;
	}

	@Override
	public String getName() {
		return species.getName();
	}

	@Override
	public boolean isGrid() {
		return species.isGrid();
	}

	@Override
	public ISpecies getSpecies() {
		return species;
	}

	@Override
	public void dispose() {
		IAgent[] ags = agents.toArray(new IAgent[0]);
		for ( int i = 0, n = ags.length; i < n; i++ ) {
			ags[i].dispose();
		}
		agents.clear();
		firePopulationCleared();
		if ( topology != null ) {
			topology.dispose();
			topology = null;
		}
	}

	/**
	 * Special case for creating agents directly from geometries
	 * @param scope
	 * @param number
	 * @param initialValues
	 * @param geometries
	 * @return
	 */
	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final IContainer<?, IShape> geometries) {
		int number = geometries.length(scope);
		if ( number == 0 ) { return GamaList.EMPTY_LIST; }
		IList<IAgent> list = new GamaList(number);
		IAgentConstructor constr = species.getAgentConstructor();
		for ( IShape geom : geometries ) {
			IAgent a = constr.createOneAgent(this);
			int ind = currentAgentIndex++;
			a.setIndex(ind);
			a.setGeometry(geom);
			list.add(a);
		}
		agents.addAll(list);
		// March 2013: Populations should now be initialized using the init of the corresponding
		// variable added to the species description
		// for ( IAgent a : list ) {
		// a.initializeMicroPopulations(scope);
		// }
		for ( IAgent a : list ) {
			a.schedule(scope);
		}
		createVariablesFor(scope, list, Collections.EMPTY_LIST);
		fireAgentsAdded(list);
		return list;

	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number, final List<Map> initialValues,
		final boolean isRestored) throws GamaRuntimeException {
		if ( number == 0 ) { return GamaList.EMPTY_LIST; }
		IList<IAgent> list = new GamaList(number);
		IAgentConstructor constr = species.getAgentConstructor();
		for ( int i = 0; i < number; i++ ) {
			IAgent a = constr.createOneAgent(this);
			int ind = currentAgentIndex++;
			a.setIndex(ind);
			// Try to grab the location earlier
			if ( initialValues != null && !initialValues.isEmpty() ) {
				Map<Object, Object> init = initialValues.get(i);
				if ( init.containsKey(IKeyword.SHAPE) ) {
					a.setGeometry((GamaShape) init.get(IKeyword.SHAPE));
					init.remove(IKeyword.SHAPE);
				} else if ( init.containsKey(IKeyword.LOCATION) ) {
					a.setLocation((GamaPoint) init.get(IKeyword.LOCATION));
					init.remove(IKeyword.LOCATION);
				}
			}
			list.add(a);
		}
		agents.addAll(list);

		// March 2013: Populations should now be initialized using the init of the corresponding
		// variable added to the species description
		// for ( IAgent a : list ) {
		// a.initializeMicroPopulations(scope);
		// }

		createVariablesFor(scope, list, initialValues);

		for ( IAgent a : list ) {

			// if agent is restored (on the capture or release); then don't need to run the "init"
			// reflex
			if ( !isRestored ) {
				a.schedule(scope);
			}
		}
		fireAgentsAdded(list);
		return list;
	}

	public void createVariablesFor(final IScope scope, final List<? extends IAgent> agents,
		final List<Map> initialValues) throws GamaRuntimeException {
		boolean empty = initialValues.isEmpty();
		Map<String, Object> inits;
		for ( int i = 0, n = agents.size(); i < n; i++ ) {
			IAgent a = agents.get(i);
			inits = empty ? Collections.EMPTY_MAP : initialValues.get(i);
			for ( final String s : orderedVarNames ) {
				final IVariable var = species.getVar(s);
				var.initializeWith(scope, a, empty ? null : inits.get(s));
			}
		}
	}

	@Override
	public void initializeFor(final IScope scope) throws GamaRuntimeException {
		dispose();
		computeTopology(scope);
		if ( topology != null ) {
			topology.initialize(this);
		}
	}

	@Override
	public boolean hasVar(final String n) {
		return species.getVar(n) != null;
	}

	@Override
	public boolean hasAspect(final String default1) {
		return species.hasAspect(default1);
	}

	@Override
	public IAspect getAspect(final String default1) {
		return species.getAspect(default1);
	}

	@Override
	public IList<String> getAspectNames() {
		return species.getAspectNames();
	}

	@Override
	public IVariable getVar(final IAgent a, final String s) {
		return species.getVar(s);
	}

	@Override
	public boolean manages(final ISpecies s, final boolean direct) {
		return direct ? species == s : species.extendsSpecies(s);
	}

	@Override
	public boolean hasUpdatableVariables() {
		return updatableVars.length > 0;
	}

	@Override
	public IAgent getAgent(final ILocation coord) {
		return topology.getAgentClosestTo(coord, In.population(this));
	}

	/**
	 * Initializes the appropriate topology.
	 * 
	 * @param scope
	 * @return
	 * @throws GamaRuntimeException
	 */
	protected void computeTopology(final IScope scope) throws GamaRuntimeException {
		IExpression expr = species.getFacet(IKeyword.TOPOLOGY);
		boolean fixed = species.isGraph() || species.isGrid();
		if ( expr != null ) {
			if ( !fixed ) {
				topology = GamaTopologyType.staticCast(scope, scope.evaluate(expr, host), null);
				return;
			}
			throw new GamaRuntimeException("Impossible to assign a topology to " + species.getName() +
				" as it already defines one.", true);
		}
		if ( species.isGrid() ) {
			IExpression exp = species.getFacet(IKeyword.WIDTH);
			int rows = exp == null ? 100 : Cast.asInt(scope, exp.value(scope));
			exp = species.getFacet(IKeyword.HEIGHT);
			int columns = exp == null ? 100 : Cast.asInt(scope, exp.value(scope));
			exp = species.getFacet(IKeyword.TORUS);
			boolean isTorus = exp != null && Cast.asBool(scope, exp.value(scope));
			exp = species.getFacet(IKeyword.NEIGHBOURS);
			boolean usesVN = exp == null || Cast.asInt(scope, exp.value(scope)) == 4;
			boolean isHexagon = exp != null && Cast.asInt(scope, exp.value(scope)) == 6;
			exp = species.getFacet(IKeyword.FILE);
			
			GamaGridFile file = (GamaGridFile) (exp != null ? exp.value(scope) : null);
			if (file == null) topology =
				new GridTopology(scope, this.getHost(), rows, columns, isTorus, usesVN, isHexagon);
			else  topology =
					new GridTopology(scope, this.getHost(), file, isTorus, usesVN);
		} else if ( species.isGraph() ) {
			IExpression spec = species.getFacet(IKeyword.EDGE_SPECIES);
			String edgeName = spec == null ? "base_edge" : spec.literalValue();
			ISpecies edgeSpecies = scope.getSimulationScope().getModel().getSpecies(edgeName);
			// TODO Specifier directed quelque part dans l'espèce
			GamaSpatialGraph g =
				new GamaSpatialGraph(GamaList.EMPTY_LIST, false, false, new AbstractGraphNodeAgent.NodeRelation(),
					edgeSpecies, scope);
			this.addListener(g);
			g.postRefreshManagementAction(scope);
			topology = new GraphTopology(scope, this.getHost(), g);
		} else {
			topology = new ContinuousTopology(scope, this.getHost(), scope.getSimulationScope().getModel().isTorus());
		}

	}

	@Override
	public IAgent getHost() {
		return host;
	}

	@Override
	public void killMembers() throws GamaRuntimeException {
		for ( IAgent a : this.getAgentsList() ) {
			a.die();
		}
	}

	@Override
	public void hostChangesShape() {
		topology.shapeChanged(this);
	}

	@Override
	public String toString() {
		return "Population of " + species.getName();
	}

	/**
	 * @see msi.gama.metamodel.population.IPopulation#size()
	 */
	@Override
	public int size() {
		return agents.size();
	}

	@Override
	public GamaList<IAgent> getAgentsList() {
		return new GamaList(agents);
	}

	@Override
	public IAgent getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		return (IAgent) agents.getFromIndicesList(scope, indices);
	}

	/**
	 * @see msi.gama.util.IContainer#get(java.lang.Object)
	 */
	@Override
	public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
		return agents.get(scope, index);
	}

	/**
	 * @see msi.gama.util.IContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return agents.contains(scope, o);
	}

	/**
	 * @see msi.gama.util.IContainer#first()
	 */
	@Override
	public IAgent first(final IScope scope) throws GamaRuntimeException {
		return agents.first(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#last()
	 */
	@Override
	public IAgent last(final IScope scope) throws GamaRuntimeException {
		return agents.last(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#length()
	 */
	@Override
	public int length(final IScope scope) {
		return agents.length(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#max(msi.gama.runtime.IScope)
	 */
	// @Override
	// public IAgent max(final IScope scope) throws GamaRuntimeException {
	// return agents.max(scope);
	// }
	//
	// /**
	// * @see msi.gama.util.IContainer#min(msi.gama.runtime.IScope)
	// */
	// @Override
	// public IAgent min(final IScope scope) throws GamaRuntimeException {
	// return agents.min(scope);
	// }
	//
	// /**
	// * @see msi.gama.util.IContainer#product(msi.gama.runtime.IScope)
	// */
	// @Override
	// public Object product(final IScope scope) throws GamaRuntimeException {
	// return agents.product(scope);
	// }
	//
	// /**
	// * @see msi.gama.util.IContainer#sum(msi.gama.runtime.IScope)
	// */
	// @Override
	// public Object sum(final IScope scope) throws GamaRuntimeException {
	// return agents.sum(scope);
	// }

	/**
	 * @see msi.gama.util.IContainer#isEmpty()
	 */
	@Override
	public boolean isEmpty(final IScope scope) {
		return agents.isEmpty(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#reverse()
	 */
	@Override
	public IContainer<Integer, IAgent> reverse(final IScope scope) throws GamaRuntimeException {
		return agents.reverse(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#any()
	 */
	@Override
	public IAgent any(final IScope scope) {
		return agents.any(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		return agents.checkBounds(index, forAdding);
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return agents.stringValue(scope);
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#copy()
	 */
	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return listValue(scope);
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlable#toGaml()
	 */
	@Override
	public String toGaml() {
		return "list(" + species.getName() + ")";
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IAgent> iterator() {
		return agents.iterator();
	}

	@Override
	public Iterable<IAgent> iterable(final IScope scope) {
		return agents;
	}

	/**
	 * @see msi.gama.util.IContainer#putAll(java.lang.Object, java.lang.Object)
	 */
	// @Override
	// public void putAll(IScope scope, final IAgent value, final Object param)
	// throws GamaRuntimeException {
	// agents.putAll(scope, value, param);
	// }
	//
	// /**
	// * @see msi.gama.util.IContainer#put(java.lang.Object, java.lang.Object, java.lang.Object)
	// */
	// @Override
	// public void put(IScope scope, final Integer index, final IAgent value, final Object param)
	// throws GamaRuntimeException {
	// agents.put(scope, index, value, param);
	// }

	/**
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IList listValue(final IScope scope) throws GamaRuntimeException {
		return agents.listValue(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return agents.matrixValue(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope, msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) throws GamaRuntimeException {
		return agents.matrixValue(scope, preferredSize);
	}

	/**
	 * @see msi.gama.util.IContainer#mapValue(msi.gama.runtime.IScope)
	 */
	@Override
	public GamaMap mapValue(final IScope scope) throws GamaRuntimeException {
		return agents.mapValue(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add(IScope scope, final Integer index, final Object value, final Object param, boolean all, boolean add) {
		if ( all && add && value instanceof IContainer ) {
			for ( Object o : (IContainer) value ) {
				add(scope, null, o, null, false, true);
			}
		} else if ( value instanceof IAgent ) {
			fireAgentAdded((IAgent) value);
			agents.add((IAgent) value);
		}
	}

	/**
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.util.IContainer)
	 */
	@Override
	public void remove(IScope scope, final Object index, final Object value, final boolean all) {
		if ( all && value instanceof IContainer ) {
			for ( Object o : (IContainer) value ) {
				remove(scope, null, o, false);
			}
		} else if ( value instanceof IAgent && agents.remove(value) ) {
			topology.removeAgent((IAgent) value);
			fireAgentRemoved((IAgent) value);
		}
	}

	private boolean hasListeners() {
		return listeners != null && !listeners.isEmpty();
	}

	@Override
	public void addListener(final IPopulation.Listener listener) {
		if ( listeners == null ) {
			listeners = new LinkedList<IPopulation.Listener>();
		}
		if ( !listeners.contains(listener) ) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(final IPopulation.Listener listener) {
		if ( listeners == null ) { return; }
		listeners.remove(listener);
	}

	protected void fireAgentAdded(final IAgent agent) {
		if ( !hasListeners() ) { return; }
		try {
			for ( IPopulation.Listener l : listeners ) {
				l.notifyAgentAdded(this, agent);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentsAdded(final IContainer container) {
		if ( !hasListeners() ) { return; }
		// create list
		Collection agents = new LinkedList();
		Iterator it = container.iterator();
		while (it.hasNext()) {
			agents.add(it.next());
		}
		// send event
		try {
			for ( IPopulation.Listener l : listeners ) {
				l.notifyAgentsAdded(this, agents);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentRemoved(final IAgent agent) {
		if ( !hasListeners() ) { return; }
		try {
			for ( IPopulation.Listener l : listeners ) {
				l.notifyAgentRemoved(this, agent);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentsRemoved(final IContainer container) {
		if ( !hasListeners() ) { return; }
		// create list
		Collection agents = new LinkedList();
		Iterator it = container.iterator();
		while (it.hasNext()) {
			agents.add(it.next());
		}
		// send event
		try {
			for ( IPopulation.Listener l : listeners ) {
				l.notifyAgentsRemoved(this, agents);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void firePopulationCleared() {
		if ( !hasListeners() ) { return; }
		// send event
		try {
			for ( IPopulation.Listener l : listeners ) {
				l.notifyPopulationCleared(this);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

}