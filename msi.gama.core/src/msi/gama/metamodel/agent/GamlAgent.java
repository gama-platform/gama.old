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
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
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
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
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
	protected int index;
	/** The shape of agent with relative coordinate on the environment of host/environment. */
	protected IShape geometry;
	protected String name;
	/**
	 * If true, this means that the agent will soon be dead.
	 * In this case, dead() will return true.
	 * 
	 */
	protected boolean dying = false;
	/**
	 * All the populations that manage the micro-agents. Each population manages agents of a
	 * micro-species. Final so that it is correctly garbaged.
	 */
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
		IVariable var = population.getVar(this, n);
		if ( var != null ) { return var.value(scope, this); }
		IAgent host = this.getHost();
		if ( host != null ) {
			IVariable varOfHost = host.getPopulation().getVar(host, n);
			if ( varOfHost != null ) { return varOfHost.value(scope, host); }
		}

		return null;
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v)
		throws GamaRuntimeException {
		population.getVar(this, s).setVal(scope, this, v);
	}

	@Override
	public void updateAttributes(final IScope scope) throws GamaRuntimeException {
		getPopulation().updateVariablesFor(scope, this);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		// callback from the scheduler once the agent has been scheduled.
		getPopulation().init(scope);
	}

	@Override
	// TODO move up to AbstractAgent
	public void step(final IScope scope) throws GamaRuntimeException {
		if ( dead()/* || !simulation.isAlive() */) { return; }
		scope.push(this);
		try {
			updateAttributes(scope);
			getPopulation().step(scope);
		} catch (GamaRuntimeException g) {
			g.addAgent(this.getName());
			GAMA.reportError(g);
		} finally {
			scope.pop(this);
		}
	}

	@Override
	public void computeAgentsToSchedule(final IScope scope, final IList list)
		throws GamaRuntimeException {
		List<IPopulation> pops = this.getMicroPopulations();

		try {
			scope.push(this);

			for ( IPopulation pop : pops ) {
				pop.computeAgentsToSchedule(scope, list);
			}
		} finally {
			scope.pop(this);
		}
	}

	@action(name = "debug")
	@args(names = { "message" })
	public final Object primDebug(final IScope scope) throws GamaRuntimeException {
		final String m = (String) scope.getArg("message", IType.STRING);
		GuiUtils.debugConsole(scope.getClock().getCycle(),
			m + "\nsender: " + Cast.asMap(scope, this));
		return m;
	}

	@action(name = "write")
	@args(names = { "message" })
	public final Object primWrite(final IScope scope) throws GamaRuntimeException {
		String s = (String) scope.getArg("message", IType.STRING);
		GuiUtils.informConsole(s);
		return s;
	}

	@action(name = IKeyword.ERROR)
	@args(names = { "message" })
	public final Object primError(final IScope scope) throws GamaRuntimeException {
		String error = (String) scope.getArg("message", IType.STRING);
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
		scope.setStatus(ExecutionStatus.interrupt);
		die();
		return null;
	}

	@Override
	public boolean contains(final IAgent component) {
		if ( component == null ) { return false; }
		return this.equals(component.getHost());
	}

	@Override
	public IAgent copy(IScope scope) {
		return this;
		// agents are immutable
	}

	@Override
	public boolean isPoint() {
		return geometry != null && geometry.isPoint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IAgent#getTopology()
	 */
	@Override
	public ITopology getTopology() {
		return population.getTopology();
	}

	@Override
	public IList<IAgent> captureMicroAgents(final IScope scope, final ISpecies microSpecies,
		final IList<IAgent> microAgents) throws GamaRuntimeException {
		if ( microAgents == null || microAgents.isEmpty() || microSpecies == null ||
			!this.getSpecies().getMicroSpecies().contains(microSpecies) ) { return GamaList.EMPTY_LIST; }

		List<IAgent> candidates = new GamaList<IAgent>();
		for ( IAgent a : microAgents ) {
			if ( this.canCapture(a, microSpecies) ) {
				candidates.add(a);
			}
		}

		IList<IAgent> capturedAgents = new GamaList<IAgent>();
		IPopulation microSpeciesPopulation = this.getPopulationFor(microSpecies);
		for ( IAgent micro : candidates ) {
			SavedAgent savedMicro = new SavedAgent(scope, micro);
			micro.die();
			capturedAgents.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
		}

		return capturedAgents;
	}

	@Override
	public IAgent captureMicroAgent(final IScope scope, final ISpecies microSpecies,
		final IAgent microAgent) throws GamaRuntimeException {
		if ( this.canCapture(microAgent, microSpecies) ) {
			IPopulation microSpeciesPopulation = this.getMicroPopulation(microSpecies);
			SavedAgent savedMicro = new SavedAgent(scope, microAgent);
			microAgent.die();
			return savedMicro.restoreTo(scope, microSpeciesPopulation);
		}

		return null;
	}

	@Override
	public IList<IAgent> releaseMicroAgents(final IScope scope, final IList<IAgent> microAgents)
		throws GamaRuntimeException {
		IPopulation originalSpeciesPopulation;
		IList<IAgent> releasedAgents = new GamaList<IAgent>();

		for ( IAgent micro : microAgents ) {
			SavedAgent savedMicro = new SavedAgent(scope, micro);
			originalSpeciesPopulation =
				micro.getPopulationFor(micro.getSpecies().getParentSpecies());
			micro.die();
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
		IList<IAgent> immigrantCandidates = new GamaList<IAgent>();

		for ( IAgent m : microAgents ) {
			if ( m.getSpecies().isPeer(newMicroSpecies) ) {
				immigrantCandidates.add(m);
			}
		}

		IList<IAgent> immigrants = new GamaList<IAgent>();
		if ( !immigrantCandidates.isEmpty() ) {
			IPopulation microSpeciesPopulation = this.getPopulationFor(newMicroSpecies);
			for ( IAgent micro : immigrantCandidates ) {
				SavedAgent savedMicro = new SavedAgent(scope, micro);
				micro.die();
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
		IPopulation oldMicroPop = this.getPopulationFor(oldMicroSpecies);
		IPopulation newMicroPop = this.getPopulationFor(newMicroSpecies);
		IList<IAgent> immigrants = new GamaList<IAgent>();

		for ( IAgent m : oldMicroPop.getAgentsList() ) {
			SavedAgent savedMicro = new SavedAgent(scope, m);
			m.die();
			immigrants.add(savedMicro.restoreTo(scope, newMicroPop));
		}

		return immigrants;
	}

	/** Variables which are not saved during the capture and release process. */
	private static final List<String> UNSAVABLE_VARIABLES = Arrays.asList(IKeyword.PEERS,
		IKeyword.AGENTS, IKeyword.HOST, IKeyword.TOPOLOGY);

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
		private void saveAttributes(final IScope scope, final IAgent agent)
			throws GamaRuntimeException {
			variables = new HashMap<String, Object>();
			ISpecies species = agent.getSpecies();
			for ( String specVar : species.getVarNames() ) {
				if ( UNSAVABLE_VARIABLES.contains(specVar) ) {
					continue;
				}
				if ( specVar.equals(IKeyword.SHAPE) ) {
					// variables.put(specVar, geometry.copy());
					// Changed 3/2/12: is it necessary to make the things below ?
					variables.put(specVar, new GamaShape(((GamaShape) species.getVar(specVar)
						.value(scope, agent)).getInnerGeometry()));
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
		private void saveMicroAgents(final IScope scope, final IAgent agent)
			throws GamaRuntimeException {
			innerPopulations = new HashMap<String, List<SavedAgent>>();

			for ( IPopulation microPop : agent.getMicroPopulations() ) {
				List<SavedAgent> savedAgents = new GamaList<SavedAgent>();

				for ( IAgent micro : microPop.getAgentsList() ) {
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
		IAgent restoreTo(final IScope scope, final IPopulation targetPopulation)
			throws GamaRuntimeException {
			List<Map> agentAttrs = new GamaList<Map>();
			agentAttrs.add(variables);
			List<? extends IAgent> restoredAgents =
				targetPopulation.createAgents(scope, 1, agentAttrs, true);
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

			for ( String microPopName : innerPopulations.keySet() ) {
				IPopulation microPop = host.getMicroPopulation(microPopName);

				if ( microPop != null ) {
					List<SavedAgent> savedMicros = innerPopulations.get(microPopName);
					List<Map> microAttrs = new GamaList<Map>();
					for ( SavedAgent sa : savedMicros ) {
						microAttrs.add(sa.variables);
					}

					List<? extends IAgent> microAgents =
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
		IShape g = getGeometry();
		return g == null ? null : g.getInnerGeometry();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getEnvelope()
	 */
	@Override
	public Envelope getEnvelope() {
		IShape g = getGeometry();
		return g == null ? null : g.getEnvelope();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		IShape gg = getGeometry();
		return gg == null ? false : gg.covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		IShape gg = getGeometry();
		return gg == null ? 0d : gg.euclidianDistanceTo(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		IShape gg = getGeometry();
		return gg == null ? 0d : gg.euclidianDistanceTo(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		IShape gg = getGeometry();
		return gg == null ? false : gg.intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		IShape gg = getGeometry();
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

	@Override
	public ISimulation getSimulation() {
		return getHost().getSimulation();
	}

	@Override
	public IScheduler getScheduler() {
		return getHost().getScheduler();
	}

	@Override
	public IModel getModel() {
		return getHost().getModel();
	}

	@Override
	public IExperiment getExperiment() {
		return getHost().getExperiment();
	}

	@Override
	public IScope getScope() {
		ISimulation sim = getSimulation();
		if ( sim == null ) { return null; }
		return sim.getExecutionScope();
	}

	@Override
	public boolean isInstanceOf(final String skill, final boolean direct) {
		return getSpecies().implementsSkill(skill);
	}

	@Override
	public void setExtraAttributes(Map<Object, Object> map) {
		if ( map == null ) { return; }
		attributes.putAll(map);
	}

	@Override
	public GamaMap getAttributes() {
		return attributes;
	}

	@Override
	public synchronized Object getAttribute(Object index) {
		return attributes.get(index);
	}

	@Override
	public final synchronized void setAttribute(final String name, final Object val) {
		attributes.put(name, val);
	}

	@Override
	public void initializeMicroPopulations(final IScope scope) throws GamaRuntimeException {
		List<ISpecies> allMicroSpecies = this.getSpecies().getMicroSpecies();

		if ( !allMicroSpecies.isEmpty() ) {
			// microPopulations = new HashMap<ISpecies, IPopulation>();
			IPopulation microPop;
			for ( ISpecies microSpec : allMicroSpecies ) {

				// TODO what to do with built-in species?
				if ( AbstractGamlAdditions.isBuiltIn(microSpec.getName()) ) {
					continue;
				}

				microPop = new GamaPopulation(this, microSpec);
				microPopulations.put(microSpec, microPop);
				microPop.initializeFor(scope);
			}
		}
	}

	@Override
	public void initializeMicroPopulation(final IScope scope, final String name) {
		ISpecies microSpec = getSpecies().getMicroSpecies(name);
		if ( microSpec == null ) { return; }
		if ( AbstractGamlAdditions.isBuiltIn(name) ) { return; }
		IPopulation microPop = new GamaPopulation(this, microSpec);
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
	public synchronized boolean acquireLock() {
		if ( index == -1 || lockAcquired ) { return false; }
		// GUI.debug("Lock acquired by " + this);
		lockAcquired = true;
		return true;
	}

	@Override
	public synchronized void releaseLock() {
		lockAcquired = false;
		// GUI.debug("Lock released by " + this);
		notify();
	}

	@Override
	public synchronized void dispose() {
		// If agent is being drawn then wait
		// acquireLock();
		// while (!acquireLock()) {
		// try {
		// wait();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }

		// dispose components
		if ( microPopulations != null ) {
			for ( IPopulation pop : microPopulations.values() ) {
				pop.dispose();
			}
			microPopulations.clear();
		}
		// Do not put it to null yet. Let the GC do its job, while
		// keeping some degree of compatibility with the other processes
		// that might want to access the field.
		// microPopulations = null;

		try {
			// TODO Check null for scope
			population.remove(null, null, this, false);

		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			e.printStackTrace();
		}
		acquireLock();
		// population = null;
		attributes.clear();
		// attributes = null;
		if ( geometry != null ) {
			geometry.dispose();
		}
		index = -1;
		// simulation = null;

		releaseLock();
	}

	@Override
	public void schedule(final IScope scope) throws GamaRuntimeException {
		if ( index != -1 ) {
			scope.getSimulationScope().getScheduler().insertAgentToInit(this, scope);
		}
	}

	public void enable(final String behavior, final boolean enabled) {}

	@Override
	public final int getIndex() {
		return index;
	}

	@Override
	public String getName() {
		// if ( dead() ) { return "dead agent"; }
		if ( name == null ) {
			if ( dead() ) { return "dead agent"; }
			return getSpeciesName() + index;
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
	public void die() throws GamaRuntimeException {

		if ( dead() ) { return; }

		dying = true;

		if ( microPopulations != null ) {
			for ( IPopulation microPop : microPopulations.values() ) {
				microPop.killMembers();
			}
		}
		GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
		if ( graph != null ) {

			Set edgesToModify = graph.edgesOf(this);
			graph.removeVertex(this);

			for ( Object obj : edgesToModify ) {
				if ( obj instanceof IAgent ) {
					((IAgent) obj).die();
				}
			}

		}
		dispose();
	}

	@Override
	public synchronized boolean dead() {
		return index == -1 || dying;
	}

	@Override
	public synchronized IShape getGeometry() {
		return geometry;
	}

	@Override
	public synchronized void setGeometry(final IShape newGeometry) {
		if ( newGeometry == null || newGeometry.getInnerGeometry() == null ) { return; }

		ITopology topology = population.getTopology();
		ILocation newGeomLocation = newGeometry.getLocation().copy(getScope());

		// if the old geometry is "shared" with another agent, we create a new one.
		// otherwise, we copy it directly.
		IAgent other = newGeometry.getAgent();
		GamaShape newLocalGeom =
			(GamaShape) (other == null ? newGeometry : newGeometry.copy(getScope()));
		topology.normalizeLocation(newGeomLocation, false);

		if ( !newGeomLocation.equals(newLocalGeom.getLocation()) ) {
			newLocalGeom.setLocation(newGeomLocation);
		}

		newLocalGeom.setAgent(this);
		IShape previous = geometry;
		geometry = newLocalGeom;

		topology.updateAgent(previous, this);
		// topology.updateAgent(this, previousIsPoint, previousLoc, previousEnv);

		// update micro-agents' locations accordingly
		for ( IPopulation p : microPopulations.values() ) {
			p.hostChangesShape();
		}
	}

	@Override
	public synchronized void setLocation(final ILocation point) {
		// Pourquoi "synchronized" ?
		if ( point == null ) { return; }
		ILocation newLocation = point.copy(getScope());
		ITopology topology = population.getTopology();
		if ( topology == null ) { return; }
		topology.normalizeLocation(newLocation, false);

		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			setGeometry(GamaGeometryType.createPoint(newLocation));
		} else {
			ILocation previousPoint = geometry.getLocation();
			if ( newLocation.equals(previousPoint) ) { return; }
			IShape previous =
				geometry.isPoint() ? previousPoint : new GamaShape(geometry.getInnerGeometry()
					.getEnvelope());
			// Envelope previousEnvelope = geometry.getEnvelope();
			geometry.setLocation(newLocation);
			Integer newHeading =
				topology.directionInDegreesTo(getScope(), previousPoint, newLocation);
			if ( newHeading != null && !getTopology().isTorus() ) {
				setHeading(newHeading);
			}
			topology.updateAgent(previous, this);

			// update micro-agents' locations accordingly
			for ( IPopulation p : microPopulations.values() ) {
				// FIXME DOES NOT WORK FOR THE MOMENT
				p.hostChangesShape();
			}
		}
		GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
		if ( graph != null ) {
			Set edgesToModify = graph.edgesOf(this);
			for ( Object obj : edgesToModify ) {
				if ( obj instanceof IAgent ) {
					IShape ext1 = (IShape) graph.getEdgeSource(obj);
					IShape ext2 = (IShape) graph.getEdgeTarget(obj);
					((IAgent) obj).setGeometry(GamaGeometryType.buildLine(ext1.getLocation(),
						ext2.getLocation()));
				}
			}

		}
	}

	@Override
	public synchronized ILocation getLocation() {
		// Pourquoi "synchronized" ?
		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			ILocation randomLocation = population.getTopology().getRandomLocation();
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
		// if (dead()) return false;
		if ( s.getName().equals(IKeyword.AGENT) ) { return true; }
		return population.manages(s, direct);
	}

	@Override
	public String toGaml() {
		if ( dead() ) { return "nil"; }
		final StringBuilder sb = new StringBuilder(30);
		sb.append(index);
		sb.append(" as ");
		sb.append(getSpeciesName());
		return sb.toString();
	}

	@Override
	public String stringValue(IScope scope) {
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
		ISpecies microSpecies = this.getSpecies().getMicroSpecies(microSpeciesName);
		return microPopulations.get(microSpecies);
	}

	@Override
	public IPopulation getMicroPopulation(final ISpecies microSpecies) {
		return microPopulations.get(microSpecies);
	}

	@Override
	public boolean hasMembers() {
		if ( microPopulations == null ) { return false; }

		for ( IPopulation mam : microPopulations.values() ) {
			if ( mam.size() > 0 ) { return true; }
		}

		return false;
	}

	@Override
	public IList<IAgent> getMembers() {
		if ( dead() ) { return GamaList.EMPTY_LIST; }
		int size = 0;
		for ( IPopulation pop : microPopulations.values() ) {
			size += pop.size();
		}
		if ( size == 0 ) { return GamaList.EMPTY_LIST; }
		GamaList<IAgent> members = new GamaList(size);
		for ( IPopulation pop : microPopulations.values() ) {
			members.addAll(pop.getAgentsList());
		}
		return members;
	}

	@Override
	public void setMembers(final IList<IAgent> newMembers) {
		// Directly change "members" not supported
	}

	@Override
	public void setAgents(final IList<IAgent> agents) {
		// "agents" is read-only attribute
	}

	@Override
	public IList<IAgent> getAgents() {
		if ( !hasMembers() ) { return GamaList.EMPTY_LIST; }

		List<IAgent> members = getMembers();
		IList<IAgent> agents = new GamaList<IAgent>();
		agents.addAll(members);
		for ( IAgent m : members ) {
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
		IAgent host = getHost();
		if ( host != null ) {
			IPopulation pop = host.getPopulationFor(this.getSpecies());
			IList<IAgent> retVal = pop.getAgentsList();
			retVal.remove(this);
			return retVal;
		}
		return GamaList.EMPTY_LIST;
	}

	@Override
	public IPopulation getPopulationFor(final ISpecies species) throws GamaRuntimeException {
		return getPopulationFor(species.getName());
	}

	@Override
	public IPopulation getPopulationFor(final String speciesName) throws GamaRuntimeException {
		IPopulation microPopulation = this.getMicroPopulation(speciesName);
		if ( microPopulation != null ) { return microPopulation; }

		IAgent host = this.getHost();
		if ( host != null ) { return host.getPopulationFor(speciesName); }
		return null;
	}

	@Override
	public List<IAgent> getMacroAgents() {
		List<IAgent> retVal = new GamaList<IAgent>();
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
		if ( other == null || other.dead() || newSpecies == null ||
			!this.getSpecies().containMicroSpecies(newSpecies) ) { return false; }
		if ( this.getMacroAgents().contains(other) ) { return false; }
		if ( other.getHost().equals(this) ) { return false; }
		return true;
	}

	@Override
	public IAgent getHost() {
		return population.getHost();
	}

	@Override
	public void setHost(final IAgent macroAgent) {
		// not supported
	}

	@Override
	public IAgent duplicate() {
		return this;
	}

}
