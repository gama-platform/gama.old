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
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.ContinuousTopology;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.metamodel.topology.grid.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.graph.AbstractGraphNodeAgent;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
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

	public static GamaPopulation createPopulation(final IScope scope, final IMacroAgent host, final ISpecies species) {
		if ( species.isGrid() ) {
			final IExpression exp = species.getFacet("use_regular_agents");
			final boolean useRegularAgents = exp == null || Cast.asBool(scope, exp.value(scope));
			// if ( useRegularAgents ) { return new GamaPopulation(host, species); }
			// In case of grids, we build the topology first if we use the minimal agents
			final ITopology t = buildGridTopology(scope, species, host);
			final GamaSpatialMatrix m = (GamaSpatialMatrix) t.getPlaces();
			return m.new GridPopulation(t, host, species, useRegularAgents);
		}
		return new GamaPopulation(host, species);
	}

	/** The agent hosting this population which is considered as the direct macro-agent. */
	protected IMacroAgent host;

	/** The object describing how the agents of this population are spatially organized */
	protected ITopology topology;
	protected final ISpecies species;
	protected final String[] orderedVarNames;
	protected final IVariable[] updatableVars;
	protected int currentAgentIndex;
	protected final IArchitecture architecture;
	protected IList<IAgent> agents = new GamaList();
	protected final IExpression scheduleFrequency;

	/**
	 * Listeners, created in a lazy way
	 */
	private LinkedList<IPopulation.Listener> listeners = null;

	class PopulationManagement extends GamaHelper {

		final IExpression listOfTargetAgents;

		PopulationManagement(final IExpression exp) {
			listOfTargetAgents = exp;
		}

		@Override
		public Object run(final IScope scope) throws GamaRuntimeException {
			final IPopulation pop = GamaPopulation.this;
			final IList<IAgent> targets = Cast.asList(scope, listOfTargetAgents.value(scope));
			final IList<IAgent> toKill = new GamaList();
			for ( final IAgent agent : pop ) {
				final IAgent target = Cast.asAgent(scope, agent.getAttribute("target"));
				if ( targets.contains(target) ) {
					targets.remove(target);
				} else {
					toKill.add(agent);
				}
			}
			for ( final IAgent agent : toKill ) {
				agent.dispose();
			}
			final List<Map> attributes = new ArrayList();
			for ( final IAgent target : targets ) {
				final Map<String, Object> att = new HashMap();
				att.put("target", target);
				attributes.add(att);
			}
			return pop.createAgents(scope, targets.size(), attributes, false);
		}

	}

	protected GamaPopulation(final IMacroAgent host, final ISpecies species) {
		this.host = host;
		this.species = species;
		architecture = species.getArchitecture();
		final TypeDescription ecd = (TypeDescription) species.getDescription();
		orderedVarNames = ecd.getVarNames().toArray(new String[0]);
		final List<String> updatableVarNames = ecd.getUpdatableVarNames();
		final int updatableVarsSize = updatableVarNames.size();
		updatableVars = new IVariable[updatableVarNames.size()];
		for ( int i = 0; i < updatableVarsSize; i++ ) {
			final String s = updatableVarNames.get(i);
			updatableVars[i] = species.getVar(s);
		}
		if ( species.isMirror() ) {
			host.getScheduler().insertEndAction(new PopulationManagement(species.getFacet(IKeyword.MIRRORS)));
		}

		// Add an attribute to the agents (dans SpeciesDescription)
		scheduleFrequency = species.getFrequency();
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {

		final List<IAgent> agentsToSchedule = computeAgentsToSchedule(scope);

		for ( int i = 0, n = agentsToSchedule.size(); i < n; i++ ) {
			if ( scope.interrupted() ) { return; }
			final IAgent a = agentsToSchedule.get(i);
			if ( a != null && !a.dead() ) {
				scope.push(a);
				try {
					a.step(scope);

				} catch (final GamaRuntimeException g) {
					g.addAgent(a.getName());
					GAMA.reportError(g);
				} finally {
					scope.pop(a);
				}
			}
		}
	}

	@Override
	public void updateVariables(final IScope scope, final IAgent a) {
		for ( int j = 0; j < updatableVars.length; j++ ) {
			updatableVars[j].updateFor(scope, a);
		}
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {

		// // Do whatever the population has to do at the first step ?
		// Ideally, the list of agents to init should be there rather than in the scheduler
	}

	@Override
	public void createVariablesFor(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		for ( final String s : orderedVarNames ) {
			final IVariable var = species.getVar(s);
			var.initializeWith(scope, agent, null);
		}
	}

	/**
	 * 
	 * @see msi.gama.interfaces.IPopulation#computeAgentsToSchedule(msi.gama.interfaces.IScope, msi.gama.util.GamaList)
	 */
	// @Override
	public List<IAgent> computeAgentsToSchedule(final IScope scope) {
		final int frequency = scheduleFrequency == null ? 1 : Cast.asInt(scope, scheduleFrequency.value(scope));
		final int step = scope.getClock().getCycle();
		if ( frequency == 0 || step % frequency != 0 ) { return Collections.EMPTY_LIST; }
		final IExpression ags = getSpecies().getSchedule();
		final List<IAgent> agents = ags == null ? getAgentsList() : Cast.asList(scope, ags.value(scope));
		return agents;
	}

	@Override
	public IAgent getAgent(final Integer index) {
		for ( final IAgent a : this ) {
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
		killMembers();
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
		final int number = geometries.length(scope);
		if ( number == 0 ) { return GamaList.EMPTY_LIST; }
		final IList<IAgent> list = new GamaList(number);
		final IAgentConstructor constr = ((SpeciesDescription) species.getDescription()).getAgentConstructor();
		for ( final IShape geom : geometries ) {
			// WARNING Should be redefined somehow
			final IAgent a = constr.createOneAgent(this);
			final int ind = currentAgentIndex++;
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
		for ( final IAgent a : list ) {
			a.schedule();
		}
		createVariablesFor(scope, list, Collections.EMPTY_LIST);
		fireAgentsAdded(list);
		return list;

	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number, final List<Map> initialValues,
		final boolean isRestored) throws GamaRuntimeException {
		if ( number == 0 ) { return GamaList.EMPTY_LIST; }
		final IList<IAgent> list = new GamaList(number);
		final IAgentConstructor constr = ((SpeciesDescription) species.getDescription()).getAgentConstructor();
		for ( int i = 0; i < number; i++ ) {
			final IAgent a = constr.createOneAgent(this);
			final int ind = currentAgentIndex++;
			a.setIndex(ind);
			// Try to grab the location earlier
			if ( initialValues != null && !initialValues.isEmpty() ) {
				final Map<Object, Object> init = initialValues.get(i);
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

		for ( final IAgent a : list ) {

			// if agent is restored (on the capture or release); then don't need to run the "init"
			// reflex
			if ( !isRestored ) {
				a.schedule();
			}
		}
		fireAgentsAdded(list);
		return list;
	}

	public void createVariablesFor(final IScope scope, final List<? extends IAgent> agents,
		final List<Map> initialValues) throws GamaRuntimeException {
		final boolean empty = initialValues.isEmpty();
		Map<String, Object> inits;
		for ( int i = 0, n = agents.size(); i < n; i++ ) {
			final IAgent a = agents.get(i);
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
		final IExpression expr = species.getFacet(IKeyword.TOPOLOGY);
		final boolean fixed = species.isGraph() || species.isGrid();
		if ( expr != null ) {
			if ( !fixed ) {
				topology = GamaTopologyType.staticCast(scope, scope.evaluate(expr, host), null);
				return;
			}
			throw GamaRuntimeException.warning("Impossible to assign a topology to " + species.getName() +
				" as it already defines one.");
		}
		if ( species.isGrid() ) {
			topology = buildGridTopology(scope, species, getHost());
		} else if ( species.isGraph() ) {
			final IExpression spec = species.getFacet(IKeyword.EDGE_SPECIES);
			final String edgeName = spec == null ? "base_edge" : spec.literalValue();
			final ISpecies edgeSpecies = scope.getSimulationScope().getModel().getSpecies(edgeName);
			// TODO Specifier directed quelque part dans l'espèce
			final GamaSpatialGraph g =
				new GamaSpatialGraph(GamaList.EMPTY_LIST, false, false, new AbstractGraphNodeAgent.NodeRelation(),
					edgeSpecies, scope);
			this.addListener(g);
			g.postRefreshManagementAction(scope);
			topology = new GraphTopology(scope, this.getHost(), g);
		} else {
			topology = new ContinuousTopology(scope, this.getHost());
		}

	}

	protected static ITopology buildGridTopology(final IScope scope, final ISpecies species, final IAgent host) {
		IExpression exp = species.getFacet(IKeyword.WIDTH);
		final int rows = exp == null ? 100 : Cast.asInt(scope, exp.value(scope));
		exp = species.getFacet(IKeyword.HEIGHT);
		final int columns = exp == null ? 100 : Cast.asInt(scope, exp.value(scope));
		exp = species.getFacet(IKeyword.TORUS);
		final boolean isTorus = exp != null && Cast.asBool(scope, exp.value(scope));
		exp = species.getFacet("use_individual_shapes");
		final boolean useIndividualShapes = exp == null || Cast.asBool(scope, exp.value(scope));
		exp = species.getFacet(IKeyword.NEIGHBOURS);
		final boolean usesVN = exp == null || Cast.asInt(scope, exp.value(scope)) == 4;
		final boolean isHexagon = exp != null && Cast.asInt(scope, exp.value(scope)) == 6;
		exp = species.getFacet(IKeyword.FILE);
		final GamaGridFile file = (GamaGridFile) (exp != null ? exp.value(scope) : null);
		if ( file == null ) { return new GridTopology(scope, host, rows, columns, isTorus, usesVN, isHexagon,
			useIndividualShapes); }
		return new GridTopology(scope, host, file, isTorus, usesVN, useIndividualShapes);
	}

	@Override
	public IMacroAgent getHost() {
		return host;
	}

	@Override
	public void killMembers() throws GamaRuntimeException {
		for ( final IAgent a : this.getAgentsList() ) {
			a.dispose();
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
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return agents.stringValue(scope);
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#copy()
	 */
	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
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
		return this;
	}

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
	public void add(final IScope scope, final Integer index, final Object value, final Object param, final boolean all,
		final boolean add) {
		if ( all && add && value instanceof IContainer ) {
			for ( final Object o : (IContainer) value ) {
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
	public void remove(final IScope scope, final Object index, final Object value, final boolean all) {
		if ( all && value instanceof IContainer ) {
			for ( final Object o : (IContainer) value ) {
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
			for ( final IPopulation.Listener l : listeners ) {
				l.notifyAgentAdded(this, agent);
			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentsAdded(final IContainer container) {
		if ( !hasListeners() ) { return; }
		// create list
		final Collection agents = new LinkedList();
		final Iterator it = container.iterator();
		while (it.hasNext()) {
			agents.add(it.next());
		}
		// send event
		try {
			for ( final IPopulation.Listener l : listeners ) {
				l.notifyAgentsAdded(this, agents);
			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentRemoved(final IAgent agent) {
		if ( !hasListeners() ) { return; }
		try {
			for ( final IPopulation.Listener l : listeners ) {
				l.notifyAgentRemoved(this, agent);
			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void fireAgentsRemoved(final IContainer container) {
		if ( !hasListeners() ) { return; }
		// create list
		final Collection agents = new LinkedList();
		final Iterator it = container.iterator();
		while (it.hasNext()) {
			agents.add(it.next());
		}
		// send event
		try {
			for ( final IPopulation.Listener l : listeners ) {
				l.notifyAgentsRemoved(this, agents);
			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected void firePopulationCleared() {
		if ( !hasListeners() ) { return; }
		// send event
		try {
			for ( final IPopulation.Listener l : listeners ) {
				l.notifyPopulationCleared(this);
			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

}