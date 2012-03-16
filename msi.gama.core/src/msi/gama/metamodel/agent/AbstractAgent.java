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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.factories.ModelFactory;
import msi.gaml.operators.Maths;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 24 oct. 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractAgent implements IAgent {

	/** The population that this agent belongs to. */
	protected IPopulation population;
	protected Map<String, Object> attributes;
	protected int index;
	/** The shape of agent with relative coordinate on the environment of host/environment. */
	protected IShape geometry;

	protected ISimulation simulation;

	/**
	 * All the populations that manage the micro-agents. Each population manages agents of a
	 * micro-species.
	 */
	protected Map<ISpecies, IPopulation> microPopulations = Collections.EMPTY_MAP;

	public AbstractAgent(final ISimulation sim, final IPopulation p) {
		simulation = sim;
		population = p;
		attributes = new HashMap<String, Object>();
	}

	@Override
	public void initializeMicroPopulations(final IScope scope) throws GamaRuntimeException {
		List<ISpecies> allMicroSpecies = this.getSpecies().getMicroSpecies();

		if ( !allMicroSpecies.isEmpty() ) {
			microPopulations = new HashMap<ISpecies, IPopulation>();
			IPopulation microPop;
			for ( ISpecies microSpec : allMicroSpecies ) {

				// TODO what to do with built-in species?
				if ( ModelFactory.isBuiltIn(microSpec.getName()) ) {
					continue;
				}

				microPop = new GamlPopulation(this, microSpec);
				microPopulations.put(microSpec, microPop);
				microPop.initializeFor(scope);
			}
		}
	}

	@Override
	public int compareTo(final IAgent a) {
		return Integer.valueOf(getIndex()).compareTo(a.getIndex());
	}

	public AbstractAgent(final ISimulation sim) {
		this(sim, null);
	}

	@Override
	public abstract void step(IScope scope) throws GamaRuntimeException;

	private volatile boolean lockAcquired = false;

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
		for ( IPopulation pop : microPopulations.values() ) {
			pop.dispose();
		}
		microPopulations.clear();
		microPopulations = null;

		try {
			population.removeFirst(this);
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
		simulation = null;

		releaseLock();
	}

	@Override
	public final synchronized Object getAttribute(final String name) {
		return attributes.get(name);
	}

	@Override
	public final synchronized void setAttribute(final String name, final Object val) {
		attributes.put(name, val);
	}

	@Override
	public abstract void updateAttributes(final IScope scope) throws GamaRuntimeException;

	@Override
	public void schedule() throws GamaRuntimeException {
		if ( index != -1 ) {
			// GuiUtils.debug("Agent " + getName() + " scheduled");
			simulation.getScheduler().insertAgentToInit(this);
		}
	}

	// public boolean isEnabled(final String behavior) {
	// return true;
	// }

	public void enable(final String behavior, final boolean enabled) {}

	@Override
	public final int getIndex() {
		return index;
	}

	@Override
	public String getName() {
		if ( dead() ) { return "dead agent"; }
		return getSpeciesName() + index;
	}

	@Override
	public void setName(final String name) {

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
	public Object getDirectVarValue(final String s) throws GamaRuntimeException {
		return getAttribute(s);
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v)
		throws GamaRuntimeException {
		setAttribute(s, v);
	}

	@Override
	public void die() throws GamaRuntimeException {
		for ( IPopulation microPop : microPopulations.values() ) {
			microPop.killMembers();
		}
		dispose();
	}

	@Override
	public synchronized boolean dead() {
		return index == -1;
	}

	protected static void error(final String error) {
		GuiUtils.error(error);
	}

	protected static void tell(final String s) {
		GuiUtils.tell(s);
	}

	protected static void write(final String s) {
		GuiUtils.informConsole(s);
	}

	protected void debug(final String s) {
		// ISimulation sim = getSimulationScope();
		GuiUtils.debugConsole(SimulationClock.getCycle(), s);
	}

	@Override
	public synchronized IShape getGeometry() {
		return geometry;
	}

	@Override
	public synchronized void setGeometry(final IShape newGeometry) {
		if ( newGeometry == null || newGeometry.getInnerGeometry() == null ) { return; }
		Envelope previousEnv = null;
		ILocation previousLoc = null;
		boolean previousIsPoint = false;
		if ( geometry != null && geometry.getInnerGeometry() != null ) {
			previousEnv = geometry.getEnvelope();
			previousLoc = geometry.getLocation();
			previousIsPoint = geometry.isPoint();
		}
		ITopology topology = population.getTopology();
		ILocation newGeomLocation = newGeometry.getLocation();
		// if the old geometry is "shared" with another agent, we create a new one.
		// otherwise, we copy it directly.
		IAgent other = newGeometry.getAgent();
		GamaShape newLocalGeom =
			(GamaShape) (other == null ? newGeometry : new GamaShape(newGeometry));
		topology.normalizeLocation(newGeomLocation, false);
		if ( !newGeomLocation.equals(newLocalGeom.getLocation()) ) {
			newLocalGeom.setLocation(newGeomLocation);
		}

		if ( geometry == null ) {
			newLocalGeom.setAgent(this);
			geometry = newLocalGeom;
		} else {
			geometry.setGeometry(newLocalGeom);
		}

		topology.updateAgent(this, previousIsPoint, previousLoc, previousEnv);

		// update micro-agents' locations accordingly
		for ( IPopulation p : microPopulations.values() ) {
			p.hostChangesShape();
		}
	}

	@Override
	public synchronized void setLocation(final ILocation point) {
		// Pourquoi "synchronized" ?
		if ( point == null ) { return; }
		ILocation newLocation = point.copy();
		ITopology topology = population.getTopology();
		if ( topology == null ) { return; }
		topology.normalizeLocation(newLocation, false);

		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			setGeometry(GamaGeometryType.createPoint(newLocation));
		} else {
			ILocation previousPoint = geometry.getLocation();
			if ( newLocation.equals(previousPoint) ) { return; }
			Envelope previousEnvelope = geometry.getEnvelope();
			geometry.setLocation(newLocation);
			Integer newHeading = topology.directionInDegreesTo(previousPoint, newLocation);
			if ( newHeading != null ) {
				setHeading(newHeading);
			}
			topology.updateAgent(this, geometry.isPoint(), previousPoint, previousEnvelope);

			// update micro-agents' locations accordingly
			for ( IPopulation p : microPopulations.values() ) {
				// FIXME DOES NOT WORK FOR THE MOMENT
				p.hostChangesShape();
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
	public IPopulation getPopulation() {
		return population;
	}

	@Override
	public ISpecies getSpecies() {
		return population.getSpecies();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		// if (dead()) return false;
		return population.manages(s, direct);
	}

	public GamaMap toMap() {
		// TODO a particulariser en fonction des sous-types existants (Signal,
		// Message, Conversation, etc.)
		// TODO par d√©faut, on renvoie une map des variables de l'agent/objet
		final GamaMap result = new GamaMap();
		result.putAll(attributes);
		result.put("base", getClass().getSimpleName());
		result.put("species", getPopulation().getSpecies());
		result.put("index", index);
		return result;
	}

	@Override
	public IType type() {
		return getSpecies().getAgentType();
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

	// @Override
	// public String toJava() {
	// final StringBuilder sb = new StringBuilder(30);
	// sb.append(Cast.toJava(getSpecies()));
	// sb.append("getAgent(");
	// sb.append(index).append(')');
	// return sb.toString();
	// }

	@Override
	public String stringValue() {
		return getName();
	}

	@Override
	public String getSpeciesName() {
		return getSpecies().getName();
	}

	@Override
	public ISimulation getSimulation() {
		return simulation;
	}

	@Override
	public boolean contains(final IAgent component) {
		return false;
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
		// Use "capture" and "release" commands to change it.
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

		ISpecies currentSpecies;
		IAgent currentAgent = this;
		while (currentAgent != null) {
			currentSpecies = currentAgent.getSpecies();
			if ( currentSpecies.getName().equals(speciesName) ) { return currentAgent
				.getPopulation(); }

			if ( currentSpecies.getPeerSpecies(speciesName) != null ) { return currentAgent
				.getHost().getMicroPopulation(speciesName); }

			currentAgent = currentAgent.getHost();
		}

		return null;
	}

	@Override
	public ISpecies getVisibleSpecies(final String speciesName) {
		ISpecies s = getSpecies().getMicroSpecies(speciesName);
		if ( s == null ) {
			if ( getHost().getSpecies().getName().equals(speciesName) ) {
				s = getHost().getSpecies();
			} else {
				s = getHost().getVisibleSpecies(speciesName);
			}
		}
		return s;
	}

	/**
	 * Verifies if this agent can capture other agent as newSpecies.
	 * 
	 * @return true if newSpecies is one micro-species of this agent's species
	 *         and newSpecies is a sub-species of this agent's species
	 *         false otherwise
	 */
	@Override
	public boolean canCapture(final IAgent other, final ISpecies newSpecies) {
		if ( other == null || other.dead() || newSpecies == null ||
			!this.getSpecies().containMicroSpecies(newSpecies) ) { return false; }

		ISpecies otherSpecies = other.getSpecies();
		if ( !otherSpecies.equals(newSpecies.getParentSpecies()) ) { return false; }

		return true;
	}

	@Override
	public IAgent getHost() {
		return population.getHost();
	}

	@Override
	public void setHost(final IAgent macroAgent) {
		// not supported
		// use "capture" or "release" command to change macro-agent instead
	}
}