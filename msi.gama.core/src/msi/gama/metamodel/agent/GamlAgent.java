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
package msi.gama.metamodel.agent;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.*;
import msi.gaml.variables.IVariable;
import com.vividsolutions.jts.geom.*;

/**
 * The Class GamlAgent. Represents agents that can be manipulated in GAML. They are provided with
 * everything their species defines .
 */
@species(name = IKeyword.AGENT)
public class GamlAgent implements IAgent {

	/** The population that this agent belongs to. */
	protected final IPopulation population;
	protected final GamaMap attributes = new GamaMap();
	protected volatile int index;
	/** The shape of agent with relative coordinate on the environment of host/environment. */
	protected IShape geometry;
	protected String name;
	/**
	 * If true, this means that the agent will soon be dead.
	 * In this case, dead() will return true.
	 * 
	 */
	protected volatile boolean dead = false;
	/**
	 * All the populations that manage the micro-agents. Each population manages agents of a
	 * micro-species. Final so that it is correctly garbaged.
	 */

	// FIXME Could be put in a subclass as most models dont need it.
	protected final Map<ISpecies, IPopulation> microPopulations = new HashMap();
	private volatile boolean lockAcquired = false;

	/**
	 * @param s the population used to prototype the agent.
	 * @throws GamaRuntimeException
	 */
	public GamlAgent(final IPopulation s) throws GamaRuntimeException {
		population = s;
	}

	@Override
	public IPopulation getPopulation() {
		return population;
	}

	@Override
	public Object getDirectVarValue(final IScope scope, final String n) throws GamaRuntimeException {
		final IVariable var = population.getVar(this, n);
		if ( var != null ) { return var.value(scope, this); }
		final IAgent host = this.getHost();
		if ( host != null ) {
			final IVariable varOfHost = host.getPopulation().getVar(host, n);
			if ( varOfHost != null ) { return varOfHost.value(scope, host); }
		}
		return null;
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v) throws GamaRuntimeException {
		population.getVar(this, s).setVal(scope, this, v);
	}

	/**
	 * During the call to init, the agent will search for the action named _init_ and execute it. Its default
	 * implementation is provided in this class as well.
	 * @see GamlAgent#_init_()
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 * @warning This method should NOT be overriden (except for some rare occasions like in SimulationAgent). Always
	 *          override _init_(IScope) instead.
	 */
	@Override
	public void init(final IScope scope) {
		executeCallbackAction(scope, "_init_");
	}

	/**
	 * During the call to step, the agent will search for the action named _step_ and execute it. Its default
	 * implementation is provided in this class as well.
	 * @see GamlAgent#_step_()
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 * @warning This method should NOT be overriden (except for some rare occasions like in SimulationAgent). Always
	 *          override _step_(IScope) instead.
	 */
	@Override
	public void step(final IScope scope) {
		executeCallbackAction(scope, "_step_");
	}

	/**
	 * Callback Actions
	 * 
	 */

	protected Object executeCallbackAction(final IScope scope, final String name) {
		if ( dead() || scope.interrupted() ) { return null; }
		final IStatement action = getSpecies().getAction(name);
		if ( action == null ) { return null; }
		try {
			return scope.execute(action, this);
		} catch (final GamaRuntimeException g) {
			g.addAgent(this.getName());
			GAMA.reportError(g);
			return null;
		}
	}

	@action(name = "_init_")
	public Object _init_(final IScope scope) {
		getSpecies().getArchitecture().init(scope);
		return this;
	}

	@action(name = "_step_")
	public Object _step_(final IScope scope) {
		if ( scope.interrupted() || dead() ) { return null; }
		getPopulation().updateVariables(scope, this);
		// we ask the architecture to execute on this
		getSpecies().getArchitecture().executeOn(scope);
		// we ask the sub-populations to step their agents
		return stepSubPopulations(scope);
	}

	protected Object stepSubPopulations(final IScope scope) {
		try {
			final List<IPopulation> pops = this.getMicroPopulations();
			for ( final IPopulation pop : pops ) {
				if ( scope.interrupted() ) { return null; }
				pop.step(scope);
			}
		} catch (final GamaRuntimeException g) {
			GAMA.reportError(g);
		}
		return this;
	}

	/**
	 * GAML actions
	 */

	@action(name = "debug")
	@args(names = { "message" })
	public final Object primDebug(final IScope scope) throws GamaRuntimeException {
		final String m = (String) scope.getArg("message", IType.STRING);
		GuiUtils.debugConsole(scope.getClock().getCycle(), m + "\nsender: " + Cast.asMap(scope, this));
		return m;
	}

	@action(name = "write")
	@args(names = { "message" })
	public final Object primWrite(final IScope scope) throws GamaRuntimeException {
		final String s = (String) scope.getArg("message", IType.STRING);
		GuiUtils.informConsole(s);
		return s;
	}

	@action(name = IKeyword.ERROR)
	@args(names = { "message" })
	public final Object primError(final IScope scope) throws GamaRuntimeException {
		final String error = (String) scope.getArg("message", IType.STRING);
		GuiUtils.error(error);
		return error;
	}

	@action(name = "tell")
	@args(names = { "message" })
	public final Object primTell(final IScope scope) throws GamaRuntimeException {
		final String s = getName() + " says : " + scope.getArg("message", IType.STRING);
		GuiUtils.tell(s);
		return s;
	}

	@action(name = "die")
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		// FIXME VERIFY THIS STATUS
		scope.setStatus(ExecutionStatus.interrupt);
		dispose();
		return null;
	}

	@Override
	public boolean contains(final IAgent component) {
		if ( component == null ) { return false; }
		return this.equals(component.getHost());
	}

	@Override
	public IAgent copy(final IScope scope) {
		return this;
		// agents are immutable
	}

	@Override
	public boolean isPoint() {
		return geometry != null && geometry.isPoint();
	}

	@Override
	public ITopology getTopology() {
		return population.getTopology();
	}

	@Override
	public IList<IAgent> captureMicroAgents(final IScope scope, final ISpecies microSpecies,
		final IList<IAgent> microAgents) throws GamaRuntimeException {
		if ( microAgents == null || microAgents.isEmpty() || microSpecies == null ||
			!this.getSpecies().getMicroSpecies().contains(microSpecies) ) { return GamaList.EMPTY_LIST; }

		final List<IAgent> candidates = new GamaList<IAgent>();
		for ( final IAgent a : microAgents ) {
			if ( this.canCapture(a, microSpecies) ) {
				candidates.add(a);
			}
		}

		final IList<IAgent> capturedAgents = new GamaList<IAgent>();
		final IPopulation microSpeciesPopulation = this.getPopulationFor(microSpecies);
		for ( final IAgent micro : candidates ) {
			final SavedAgent savedMicro = new SavedAgent(scope, micro);
			micro.dispose();
			capturedAgents.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
		}

		return capturedAgents;
	}

	@Override
	public IAgent captureMicroAgent(final IScope scope, final ISpecies microSpecies, final IAgent microAgent)
		throws GamaRuntimeException {
		if ( this.canCapture(microAgent, microSpecies) ) {
			final IPopulation microSpeciesPopulation = this.getMicroPopulation(microSpecies);
			final SavedAgent savedMicro = new SavedAgent(scope, microAgent);
			microAgent.dispose();
			return savedMicro.restoreTo(scope, microSpeciesPopulation);
		}

		return null;
	}

	@Override
	public IList<IAgent> releaseMicroAgents(final IScope scope, final IList<IAgent> microAgents)
		throws GamaRuntimeException {
		IPopulation originalSpeciesPopulation;
		final IList<IAgent> releasedAgents = new GamaList<IAgent>();

		for ( final IAgent micro : microAgents ) {
			final SavedAgent savedMicro = new SavedAgent(scope, micro);
			originalSpeciesPopulation = micro.getPopulationFor(micro.getSpecies().getParentSpecies());
			micro.dispose();
			releasedAgents.add(savedMicro.restoreTo(scope, originalSpeciesPopulation));
		}

		return releasedAgents;
	}

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's
	 * species.
	 * 
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	@Override
	public IList<IAgent> migrateMicroAgents(final IScope scope, final IList<IAgent> microAgents,
		final ISpecies newMicroSpecies) {
		final IList<IAgent> immigrantCandidates = new GamaList<IAgent>();

		for ( final IAgent m : microAgents ) {
			if ( m.getSpecies().isPeer(newMicroSpecies) ) {
				immigrantCandidates.add(m);
			}
		}

		final IList<IAgent> immigrants = new GamaList<IAgent>();
		if ( !immigrantCandidates.isEmpty() ) {
			final IPopulation microSpeciesPopulation = this.getPopulationFor(newMicroSpecies);
			for ( final IAgent micro : immigrantCandidates ) {
				final SavedAgent savedMicro = new SavedAgent(scope, micro);
				micro.dispose();
				immigrants.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
			}
		}

		return immigrants;
	}

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's
	 * species.
	 * 
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	@Override
	public IList<IAgent> migrateMicroAgents(final IScope scope, final ISpecies oldMicroSpecies,
		final ISpecies newMicroSpecies) {
		final IPopulation oldMicroPop = this.getPopulationFor(oldMicroSpecies);
		final IPopulation newMicroPop = this.getPopulationFor(newMicroSpecies);
		final IList<IAgent> immigrants = new GamaList<IAgent>();

		for ( final IAgent m : oldMicroPop.getAgentsList() ) {
			final SavedAgent savedMicro = new SavedAgent(scope, m);
			m.dispose();
			immigrants.add(savedMicro.restoreTo(scope, newMicroPop));
		}

		return immigrants;
	}

	/** Variables which are not saved during the capture and release process. */
	private static final List<String> UNSAVABLE_VARIABLES = Arrays.asList(IKeyword.PEERS, IKeyword.AGENTS,
		IKeyword.HOST, IKeyword.TOPOLOGY);

	/**
	 * A helper class to save agent and restore/recreate agent as a member of a population.
	 */
	private class SavedAgent {

		Map<String, Object> variables;
		Map<String, List<SavedAgent>> innerPopulations;

		SavedAgent(final IScope scope, final IAgent agent) throws GamaRuntimeException {
			saveAttributes(scope, agent);
			saveMicroAgents(scope, agent);
		}

		/**
		 * Saves agent's attributes to a map.
		 * 
		 * @param agent
		 * @throws GamaRuntimeException
		 */
		private void saveAttributes(final IScope scope, final IAgent agent) throws GamaRuntimeException {
			variables = new HashMap<String, Object>();
			final ISpecies species = agent.getSpecies();
			for ( final String specVar : species.getVarNames() ) {
				if ( UNSAVABLE_VARIABLES.contains(specVar) ) {
					continue;
				}
				if ( specVar.equals(IKeyword.SHAPE) ) {
					// variables.put(specVar, geometry.copy());
					// Changed 3/2/12: is it necessary to make the things below ?
					variables.put(specVar,
						new GamaShape(((GamaShape) species.getVar(specVar).value(scope, agent)).getInnerGeometry()));
					continue;
				}
				// variables.put(specVar, agent.getAttribute(specVar));
				variables.put(specVar, species.getVar(specVar).value(scope, agent));
			}
		}

		/**
		 * Recursively save micro-agents of an agent.
		 * 
		 * @param agent The agent having micro-agents to be saved.
		 * @throws GamaRuntimeException
		 */
		private void saveMicroAgents(final IScope scope, final IAgent agent) throws GamaRuntimeException {
			innerPopulations = new HashMap<String, List<SavedAgent>>();

			for ( final IPopulation microPop : agent.getMicroPopulations() ) {
				final List<SavedAgent> savedAgents = new GamaList<SavedAgent>();

				for ( final IAgent micro : microPop.getAgentsList() ) {
					savedAgents.add(new SavedAgent(scope, micro));
				}

				innerPopulations.put(microPop.getSpecies().getName(), savedAgents);
			}
		}

		/**
		 * @param scope
		 *            Restores the saved agent as a member of the target population.
		 * 
		 * @param targetPopulation The population that the saved agent will be restored to.
		 * @return
		 * @throws GamaRuntimeException
		 */
		IAgent restoreTo(final IScope scope, final IPopulation targetPopulation) throws GamaRuntimeException {
			final List<Map> agentAttrs = new GamaList<Map>();
			agentAttrs.add(variables);
			final List<? extends IAgent> restoredAgents = targetPopulation.createAgents(scope, 1, agentAttrs, true);
			restoreMicroAgents(scope, restoredAgents.get(0));

			return restoredAgents.get(0);
		}

		/**
		 * 
		 * 
		 * @param host
		 * @throws GamaRuntimeException
		 */
		void restoreMicroAgents(final IScope scope, final IAgent host) throws GamaRuntimeException {

			for ( final String microPopName : innerPopulations.keySet() ) {
				final IPopulation microPop = host.getMicroPopulation(microPopName);

				if ( microPop != null ) {
					final List<SavedAgent> savedMicros = innerPopulations.get(microPopName);
					final List<Map> microAttrs = new GamaList<Map>();
					for ( final SavedAgent sa : savedMicros ) {
						microAttrs.add(sa.variables);
					}

					final List<? extends IAgent> microAgents =
						microPop.createAgents(scope, savedMicros.size(), microAttrs, true);

					for ( int i = 0; i < microAgents.size(); i++ ) {
						savedMicros.get(i).restoreMicroAgents(scope, microAgents.get(i));
					}
				}
			}
		}
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		final IShape g = getGeometry();
		return g == null ? null : g.getInnerGeometry();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getEnvelope()
	 */
	@Override
	public Envelope getEnvelope() {
		final IShape g = getGeometry();
		return g == null ? null : g.getEnvelope();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? false : gg.covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? 0d : gg.euclidianDistanceTo(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		final IShape gg = getGeometry();
		return gg == null ? 0d : gg.euclidianDistanceTo(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? false : gg.intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? false : gg.crosses(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return this;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#setAgent(msi.gama.interfaces.IAgent)
	 */
	@Override
	public void setAgent(final IAgent agent) {}

	/**
	 * @see msi.gama.interfaces.IGeometry#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return geometry.getPerimeter();
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry geom) {
		geometry.setInnerGeometry(geom);
	}

	// @Override
	// public ISimulationAgent getSimulation() {
	// return getHost().getSimulation();
	// }

	@Override
	public AgentScheduler getScheduler() {
		return getHost().getScheduler();
	}

	@Override
	public IModel getModel() {
		return getHost().getModel();
	}

	@Override
	public IExperimentAgent getExperiment() {
		return getHost().getExperiment();
	}

	@Override
	public IScope getScope() {
		return getHost().getScope();
	}

	@Override
	public boolean isInstanceOf(final String skill, final boolean direct) {
		return getSpecies().implementsSkill(skill);
	}

	@Override
	public void setExtraAttributes(final Map<Object, Object> map) {
		if ( map == null ) { return; }
		attributes.putAll(map);
	}

	@Override
	public GamaMap getAttributes() {
		return attributes;
	}

	@Override
	public GamaMap getOrCreateAttributes() {
		return attributes;
	}

	@Override
	public boolean hasAttributes() {
		return false;
	}

	@Override
	public boolean hasAttribute(final Object key) {
		return false;
	}

	@Override
	public synchronized Object getAttribute(final Object index) {
		return attributes.get(index);
	}

	@Override
	public final synchronized void setAttribute(final Object name, final Object val) {
		attributes.put(name, val);
	}

	@Override
	public void initializeMicroPopulations(final IScope scope) throws GamaRuntimeException {
		final List<ISpecies> allMicroSpecies = this.getSpecies().getMicroSpecies();

		if ( !allMicroSpecies.isEmpty() ) {
			IPopulation microPop;
			for ( final ISpecies microSpec : allMicroSpecies ) {
				// FIXME Why disallow built-in populations ?
				// TODO Would be better to see if they are actually in use in the model
				// if ( Types.isBuiltIn(microSpec.getName()) ) {
				// continue;
				// }
				microPop = GamaPopulation.createPopulation(scope, this, microSpec);
				microPopulations.put(microSpec, microPop);
				microPop.initializeFor(scope);
			}
		}
	}

	@Override
	public void initializeMicroPopulation(final IScope scope, final String name) {
		final ISpecies microSpec = getModel().getSpecies(name);
		if ( microSpec == null ) { return; }
		// FIXME Why disallow built-in populations ?
		// TODO Would be better to see if they are actually in use in the model
		// if ( Types.isBuiltIn(name) ) { return; }
		final IPopulation microPop = GamaPopulation.createPopulation(scope, this, microSpec);
		microPopulations.put(microSpec, microPop);
		microPop.initializeFor(scope);
	}

	@Override
	public int compareTo(final IAgent a) {
		return Integer.valueOf(getIndex()).compareTo(a.getIndex());
	}

	/**
	 * Solve the synchronization problem between Execution Thread and Event Dispatch Thread.
	 * 
	 * The synchronization problem may happen when 1. The Event Dispatch Thread is drawing an agent
	 * while the Execution Thread tries to it; 2. The Execution Thread is disposing the agent while
	 * the Event Dispatch Thread tries to draw it.
	 * 
	 * To avoid this, the corresponding thread has to invoke "acquireLock" to lock the agent before
	 * drawing or disposing the agent. After finish the task, the thread invokes "releaseLock" to
	 * release the agent's lock.
	 * 
	 * return true if the agent is available for drawing or disposing false otherwise
	 */
	@Override
	public synchronized void acquireLock() {
		while (lockAcquired) {
			try {
				wait();
			} catch (final InterruptedException e) {
				// e.printStackTrace();
			}
		}
		lockAcquired = true;
	}

	@Override
	public synchronized void releaseLock() {
		lockAcquired = false;
		notify();
	}

	@Override
	public void dispose() {
		if ( dead() ) { return; }

		try {
			acquireLock();
			dead = true;
			if ( microPopulations != null ) {
				for ( final IPopulation microPop : microPopulations.values() ) {
					microPop.killMembers();
					microPop.dispose();
				}
				microPopulations.clear();
			}
			final GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
			if ( graph != null ) {

				final Set edgesToModify = graph.edgesOf(this);
				graph.removeVertex(this);

				for ( final Object obj : edgesToModify ) {
					if ( obj instanceof IAgent ) {
						((IAgent) obj).dispose();
					}
				}

			}

			try {
				population.remove(null, null, this, false);

			} catch (final GamaRuntimeException e) {
				GAMA.reportError(e);
			}
			attributes.clear();
			if ( geometry != null ) {
				geometry.dispose();
			}
			// setIndex(-1);
		} finally {
			releaseLock();
		}
	}

	@Override
	public void schedule() {
		// GuiUtils.debug("GamlAgent.schedule : " + this);
		if ( !dead() ) {
			getScheduler().insertAgentToInit(this, getScope());
		}
	}

	@Override
	public final int getIndex() {
		return index;
	}

	@Override
	public String getName() {
		if ( name == null ) {
			if ( dead() ) { return "dead agent"; }
			return getSpeciesName() + getIndex();
		}
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public final void setIndex(final int index) {
		this.index = index;
	}

	@Override
	public boolean dead() {
		return /* getIndex() == -1 || */dead;
	}

	@Override
	public synchronized IShape getGeometry() {
		return geometry;
	}

	@Override
	public synchronized void setGeometry(final IShape newGeometry) {
		if ( newGeometry == null || newGeometry.getInnerGeometry() == null || dead() ) { return; }

		final ITopology topology = population.getTopology();
		final ILocation newGeomLocation = newGeometry.getLocation().copy(getScope());

		// if the old geometry is "shared" with another agent, we create a new one.
		// otherwise, we copy it directly.
		final IAgent other = newGeometry.getAgent();
		final GamaShape newLocalGeom = (GamaShape) (other == null ? newGeometry : newGeometry.copy(getScope()));
		topology.normalizeLocation(newGeomLocation, false);

		if ( !newGeomLocation.equals(newLocalGeom.getLocation()) ) {
			newLocalGeom.setLocation(newGeomLocation);
		}

		newLocalGeom.setAgent(this);
		final IShape previous = geometry;
		geometry = newLocalGeom;

		topology.updateAgent(previous, this);

		// update micro-agents' locations accordingly
		for ( final IPopulation p : microPopulations.values() ) {
			p.hostChangesShape();
		}
	}

	@Override
	public synchronized void setLocation(final ILocation point) {
		if ( point == null || dead() ) { return; }
		final ILocation newLocation = point.copy(getScope());
		final ITopology topology = population.getTopology();
		if ( topology == null ) { return; }
		topology.normalizeLocation(newLocation, false);

		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			setGeometry(GamaGeometryType.createPoint(newLocation));
		} else {
			final ILocation previousPoint = geometry.getLocation();
			if ( newLocation.equals(previousPoint) ) { return; }
			final IShape previous =
				geometry.isPoint() ? previousPoint : new GamaShape(geometry.getInnerGeometry().getEnvelope());
			// Envelope previousEnvelope = geometry.getEnvelope();
			geometry.setLocation(newLocation);
			final Integer newHeading = topology.directionInDegreesTo(getScope(), previousPoint, newLocation);
			if ( newHeading != null && !getTopology().isTorus() ) {
				setHeading(newHeading);
			}
			topology.updateAgent(previous, this);

			// update micro-agents' locations accordingly
			for ( final IPopulation p : microPopulations.values() ) {
				// FIXME DOES NOT WORK FOR THE MOMENT
				p.hostChangesShape();
			}
		}
		final GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
		if ( graph != null ) {
			final Set edgesToModify = graph.edgesOf(this);
			for ( final Object obj : edgesToModify ) {
				if ( obj instanceof IAgent ) {
					final IShape ext1 = (IShape) graph.getEdgeSource(obj);
					final IShape ext2 = (IShape) graph.getEdgeTarget(obj);
					((IAgent) obj).setGeometry(GamaGeometryType.buildLine(ext1.getLocation(), ext2.getLocation()));
				}
			}

		}
	}

	@Override
	public synchronized ILocation getLocation() {
		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			final ILocation randomLocation = population.getTopology().getRandomLocation();
			if ( randomLocation == null ) { return null; }
			setGeometry(GamaGeometryType.createPoint(randomLocation));
			return randomLocation;
		}
		return geometry.getLocation();
	}

	@Override
	public void hostChangesShape() {
		setLocation(new GamaPoint(getLocation()));
	}

	@Override
	public Integer getHeading() {
		Integer h = (Integer) getAttribute(IKeyword.HEADING);
		if ( h == null ) {
			h = RandomUtils.getDefault().between(0, 359);
			setHeading(h);
		}
		return Maths.checkHeading(h);
	}

	@Override
	public void setHeading(final Integer newHeading) {
		setAttribute(IKeyword.HEADING, newHeading);
	}

	@Override
	public ISpecies getSpecies() {
		return population.getSpecies();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		if ( s.getName().equals(IKeyword.AGENT) ) { return true; }
		return population.manages(s, direct);
	}

	@Override
	public String toGaml() {
		if ( dead() ) { return "nil"; }
		final StringBuilder sb = new StringBuilder(30);
		sb.append(getIndex());
		sb.append(" as ");
		sb.append(getSpeciesName());
		return sb.toString();
	}

	@Override
	public String stringValue(final IScope scope) {
		return getName();
	}

	@Override
	public String getSpeciesName() {
		return getSpecies().getName();
	}

	@Override
	public IList<IPopulation> getMicroPopulations() {
		return new GamaList<IPopulation>(microPopulations.values());
	}

	@Override
	public synchronized IPopulation getMicroPopulation(final String microSpeciesName) {
		// FIX : Now catches the species from the model
		final ISpecies microSpecies = getModel().getSpecies(microSpeciesName);
		return microPopulations.get(microSpecies);
	}

	@Override
	public IPopulation getMicroPopulation(final ISpecies microSpecies) {
		return microPopulations.get(microSpecies);
	}

	@Override
	public boolean hasMembers() {
		if ( microPopulations == null || dead() ) { return false; }
		for ( final IPopulation mam : microPopulations.values() ) {
			if ( mam.size() > 0 ) { return true; }
		}
		return false;
	}

	@Override
	public IList<IAgent> getMembers() {
		if ( dead() ) { return GamaList.EMPTY_LIST; }
		int size = 0;
		for ( final IPopulation pop : microPopulations.values() ) {
			size += pop.size();
		}
		if ( size == 0 ) { return GamaList.EMPTY_LIST; }
		final GamaList<IAgent> members = new GamaList(size);
		for ( final IPopulation pop : microPopulations.values() ) {
			members.addAll(pop.getAgentsList());
		}
		return members;
	}

	@Override
	public void setMembers(final IList<IAgent> newMembers) {
		// Directly changing "members" not supported
	}

	@Override
	public void setAgents(final IList<IAgent> agents) {
		// "agents" is read-only attribute
	}

	@Override
	public IList<IAgent> getAgents() {
		if ( !hasMembers() ) { return GamaList.EMPTY_LIST; }

		final List<IAgent> members = getMembers();
		final IList<IAgent> agents = new GamaList<IAgent>();
		agents.addAll(members);
		for ( final IAgent m : members ) {
			if ( m != null ) {
				agents.addAll(m.getAgents());
			}
		}

		return agents;
	}

	@Override
	public void setPeers(final IList<IAgent> peers) {
		// "peers" is read-only attribute
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		final IAgent host = getHost();
		if ( host != null ) {
			final IPopulation pop = host.getPopulationFor(this.getSpecies());
			final IList<IAgent> retVal = pop.getAgentsList();
			retVal.remove(this);
			return retVal;
		}
		return GamaList.EMPTY_LIST;
	}

	@Override
	public IPopulation getPopulationFor(final ISpecies species) {
		final IPopulation microPopulation = this.getMicroPopulation(species);
		if ( microPopulation == null && getHost() != null ) { return getHost().getPopulationFor(species); }
		return microPopulation;
	}

	@Override
	public IPopulation getPopulationFor(final String speciesName) {
		// FIXME : THE BEHAVIOR OF THIS METHOD IS CLEARLY WRONG COMPARED TO THE PREVIOUS ONE
		final IPopulation microPopulation = this.getMicroPopulation(speciesName);
		if ( microPopulation != null ) { return microPopulation; }

		final IAgent host = this.getHost();
		if ( host != null ) { return host.getPopulationFor(speciesName); }
		return null;
	}

	@Override
	public List<IAgent> getMacroAgents() {
		final List<IAgent> retVal = new GamaList<IAgent>();
		IAgent currentMacro = this.getHost();
		while (currentMacro != null) {
			retVal.add(currentMacro);
			currentMacro = currentMacro.getHost();
		}

		return retVal;
	}

	/**
	 * Verifies if this agent can capture other agent as newSpecies.
	 * 
	 * @return true if the following conditions are correct:
	 *         1. newSpecies is one micro-species of this agent's species;
	 *         2. newSpecies is a sub-species of this agent's species or other species is a
	 *         sub-species of this agent's species;
	 *         3. the "other" agent is not macro-agent of this agent;
	 *         4. the "other" agent is not a micro-agent of this agent.
	 */
	@Override
	public boolean canCapture(final IAgent other, final ISpecies newSpecies) {
		if ( other == null || other.dead() || newSpecies == null || !this.getSpecies().containMicroSpecies(newSpecies) ) { return false; }
		if ( this.getMacroAgents().contains(other) ) { return false; }
		if ( other.getHost().equals(this) ) { return false; }
		return true;
	}

	@Override
	public IAgent getHost() {
		return population.getHost();
	}

	@Override
	public SimulationClock getClock() {
		return getHost().getClock();
	}

	@Override
	public void setHost(final IAgent macroAgent) {
		// not supported
	}

	@Override
	public IAgent duplicate() {
		return this;
	}

	@Override
	public IScope obtainNewScope() {
		if ( dead ) { return null; }
		return new Scope();
	}

	@Override
	public void releaseScope(final IScope scope) {
		if ( scope != null ) {
			scope.clear();
		}
	}

	protected class Scope extends AbstractScope {

		public Scope() {
			super(GamlAgent.this);
		}

		@Override
		public boolean interrupted() {
			return dead;
		}

		@Override
		public IAgent getRoot() {
			return GamlAgent.this;
		}

		@Override
		public void setInterrupted(final boolean interrupted) {
			if ( !GamlAgent.this.dead ) {
				GamlAgent.this.dispose();
			}
		}

		@Override
		public IScope copy() {
			return new Scope();
		}

	}
}
