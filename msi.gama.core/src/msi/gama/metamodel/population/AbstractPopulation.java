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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.*;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IAspect;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 6 sept. 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractPopulation /* extends GamaList<IAgent> */implements IPopulation {

	/** The agent hosting this population which is considered as the direct macro-agent. */
	protected IAgent host;

	/** The object describing how the agents of this population are spatially organized */
	protected ITopology topology;
	protected final ISpecies species;
	protected final String[] orderedVarNames;
	protected final IVariable[] updatableVars;
	protected int currentAgentIndex;

	/**
	 * Listeners, created in a lazy way
	 */
	private LinkedList<IPopulationListener> listeners = null;

	public AbstractPopulation(final IAgent host, final ISpecies species) {
		this.host = host;
		this.species = species;
		SpeciesDescription ecd = (SpeciesDescription) species.getDescription();
		orderedVarNames = ecd.getVarNames().toArray(new String[0]);
		List<String> updatableVarNames = ecd.getUpdatableVarNames();
		int updatableVarsSize = updatableVarNames.size();
		updatableVars = new IVariable[updatableVarNames.size()];
		for ( int i = 0; i < updatableVarsSize; i++ ) {
			String s = updatableVarNames.get(i);
			updatableVars[i] = species.getVar(s);
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
		if ( topology != null ) {
			topology.dispose();
			topology = null;
		}
		firePopulationCleared();
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
	public IList<? extends IAgent> createAgents(final IScope scope,
		final IContainer<?, IShape> geometries) {
		int number = geometries.length();
		if ( number == 0 ) { return GamaList.EMPTY_LIST; }
		IList<IAgent> list = new GamaList(number);
		IAgentConstructor constr = species.getAgentConstructor();
		ISimulation sim = scope.getSimulationScope();
		for ( IShape geom : geometries ) {
			IAgent a = constr.createOneAgent(sim, this);
			int ind = currentAgentIndex++;
			a.setIndex(ind);
			a.setGeometry(geom);
			list.add(a);
		}
		addAll(list, null);
		for ( IAgent a : list ) {
			a.initializeMicroPopulations(scope);
		}
		createVariablesFor(scope, list, Collections.EMPTY_LIST);
		return list;

	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope sim, final int number,
		final List<Map<String, Object>> initialValues, final boolean isRestored)
		throws GamaRuntimeException {
		if ( number == 0 ) { return GamaList.EMPTY_LIST; }
		IList<IAgent> list = new GamaList(number);
		IAgentConstructor constr = species.getAgentConstructor();
		ISimulation simulation = sim.getSimulationScope();
		for ( int i = 0; i < number; i++ ) {
			IAgent a = constr.createOneAgent(simulation, this);
			int ind = currentAgentIndex++;
			a.setIndex(ind);
			// Try to grab the location earlier
			if ( initialValues != null && !initialValues.isEmpty() ) {
				Map<String, Object> init = initialValues.get(i);
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
		addAll(list, null);

		for ( IAgent a : list ) {
			a.initializeMicroPopulations(sim);
		}

		createVariablesFor(sim, list, initialValues);

		for ( IAgent a : list ) {

			// if agent is restored (on the capture or release); then don't need to run the "init"
			// reflex
			if ( !isRestored ) {
				a.schedule();
			}
		}

		return list;
	}

	public void createVariablesFor(final IScope scope, final List<? extends IAgent> agents,
		final List<Map<String, Object>> initialValues) throws GamaRuntimeException {
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
		System.out.println("topology : " + topology);
		if ( topology != null ) {
			topology.initialize(this);
		}
	}

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
	public boolean isGlobal() {
		return species.isGlobal();
	}

	public void updateVariablesFor(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		for ( int i = 0; i < updatableVars.length; i++ ) {
			updatableVars[i].updateFor(scope, agent);
		}
	}

	@Override
	public IAgent getAgent(final ILocation coord) {
		return topology.getAgentClosestTo(coord, In.population(this));
	}

	@Override
	public boolean removeFirst(final IAgent a) {
		topology.removeAgent(a);
		fireAgentRemoved(a);
		return true;
	}

	/**
	 * Initializes the appropriate topology.
	 * 
	 * @param scope
	 * @return
	 * @throws GamaRuntimeException
	 */
	protected void computeTopology(final IScope scope) throws GamaRuntimeException {
		if ( this.isGlobal() ) {
			topology = new AmorphousTopology();
		} else if ( species.isGrid() ) {
			IExpression exp = species.getFacet(IKeyword.WIDTH);
			int rows = exp == null ? 100 : Cast.asInt(scope, exp.value(scope));
			exp = species.getFacet(IKeyword.HEIGHT);
			int columns = exp == null ? 100 : Cast.asInt(scope, exp.value(scope));
			exp = species.getFacet(IKeyword.TORUS);
			 boolean isTorus = exp != null && Cast.asBool(scope, exp.value(scope));
			exp = species.getFacet(IKeyword.NEIGHBOURS);
			boolean usesVN = exp == null || Cast.asInt(scope, exp.value(scope)) == 4;
			topology = new GridTopology(scope, this.getHost(), rows, columns, isTorus, usesVN);
		} else {
			IExpression exp = species.getFacet(IKeyword.TORUS);
			 boolean isTorus = exp != null && Cast.asBool(scope, exp.value(scope));
			topology = new ContinuousTopology(scope, this.getHost(),this.getHost().isTorus() );
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

	@Override
	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException {
		fireAgentsAdded(value);
	}

	@Override
	public void addAll(final Integer index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		fireAgentsAdded(value);
	}

	@Override
	public void add(final IAgent value, final Object param) throws GamaRuntimeException {
		fireAgentAdded(value);
	}

	@Override
	public void add(final Integer index, final IAgent value, final Object param)
		throws GamaRuntimeException {
		fireAgentAdded(value);
	}

	@Override
	public boolean removeAll(final IContainer<?, IAgent> value) throws GamaRuntimeException {
		fireAgentsRemoved(value);
		return false;
	}

	@Override
	public Object removeAt(final Integer index) throws GamaRuntimeException {
		fireAgentRemoved(get(index));
		return null;
	}

	@Override
	public void clear() throws GamaRuntimeException {
		firePopulationCleared();
	}

	private boolean hasListeners() {
		return listeners != null && !listeners.isEmpty();
	}

	@Override
	public void addListener(final IPopulationListener listener) {
		if ( listeners == null ) {
			listeners = new LinkedList<IPopulationListener>();
		}
		if ( !listeners.contains(listener) ) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(final IPopulationListener listener) {
		if ( listeners == null ) { return; }
		listeners.remove(listener);
	}

	protected void fireAgentAdded(final IAgent agent) {
		if ( !hasListeners() ) { return; }
		try {
			for ( IPopulationListener l : listeners ) {
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
			for ( IPopulationListener l : listeners ) {
				l.notifyAgentsAdded(this, agents);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentRemoved(final IAgent agent) {
		if ( !hasListeners() ) { return; }
		System.out.println("agent removed " + agent);
		try {
			for ( IPopulationListener l : listeners ) {
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
			for ( IPopulationListener l : listeners ) {
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
			for ( IPopulationListener l : listeners ) {
				l.notifyPopulationCleared(this);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

}