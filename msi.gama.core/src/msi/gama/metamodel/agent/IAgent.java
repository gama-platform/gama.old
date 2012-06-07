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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.agent;

import java.util.List;

import msi.gama.common.interfaces.*;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 24 oct. 2010
 * 
 * @todo Description
 * 
 */
public interface IAgent extends ISkill, IShape, INamed, Comparable<IAgent>, IStepable {

	@Override
	public abstract void dispose();

	public abstract void init(IScope scope) throws GamaRuntimeException;

	public abstract void schedule() throws GamaRuntimeException;

	public abstract Object getAttribute(final String name);

	public abstract void setAttribute(final String name, final Object val);

	public abstract int getIndex();

	public abstract void setIndex(int index);

	public String getSpeciesName();

	public abstract ISpecies getSpecies();

	public IPopulation getPopulation();

	public abstract boolean isInstanceOf(final ISpecies s, boolean direct);

	public abstract boolean dead();

	public void setHeading(Integer heading);

	public Integer getHeading();

	public abstract void die() throws GamaRuntimeException;

	public abstract void updateAttributes(IScope scope) throws GamaRuntimeException;

	public abstract Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException;

	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException;

	public abstract ISimulation getSimulation();

	public abstract boolean contains(IAgent component);

	/**
	 * Returns the topology which manages this agent.
	 * 
	 * @return
	 */
	public abstract ITopology getTopology();

	/**
	 * @throws GamaRuntimeException
	 *             Finds the corresponding population of a species from the "viewpoint" of this
	 *             agent.
	 * 
	 *             An agent can "see" the following populations:
	 *             1. populations of its species' direct micro-species;
	 *             2. population of its species; populations of its peer species;
	 *             3. populations of its direct&in-direct macro-species and of their peers.
	 * 
	 * @param microSpecies
	 * @return
	 */
	public abstract IPopulation getPopulationFor(final ISpecies microSpecies)
		throws GamaRuntimeException;

	/**
	 * @throws GamaRuntimeException
	 *             Finds the corresponding population of a species from the "viewpoint" of this agent.
	 * 
	 *             An agent can "see" the following populations:
	 *             1. populations of its species' direct micro-species;
	 *             2. population of its species; populations of its peer species;
	 *             3. populations of its direct&in-direct macro-species and of their peers.
	 * 
	 * @param speciesName the name of the species
	 * @return
	 */
	public abstract IPopulation getPopulationFor(final String speciesName) throws GamaRuntimeException;

	/**
	 * Initialize Populations to manage micro-agents.
	 */
	public abstract void initializeMicroPopulations(IScope scope) throws GamaRuntimeException;

	/**
	 * Returns a list of populations of (direct) micro-species.
	 * 
	 * @return
	 */
	public abstract IList<IPopulation> getMicroPopulations();

	/**
	 * Returns the population of the specified (direct) micro-species.
	 * 
	 * @param microSpeciesName
	 * @return
	 */
	public abstract IPopulation getMicroPopulation(String microSpeciesName);

	/**
	 * Returns the population of the specified (direct) micro-species.
	 * 
	 * @param microSpecies
	 * @return
	 */
	public abstract IPopulation getMicroPopulation(ISpecies microSpecies);

	public abstract void setMembers(IList<IAgent> members);

	/**
	 * Returns all the agents which consider this agent as direct host.
	 * 
	 * @return
	 */
	public abstract IList<IAgent> getMembers();

	public abstract void setAgents(IList<IAgent> agents);

	/**
	 * Returns all the agents which consider this agent as direct or in-direct host.
	 * 
	 * @return
	 */
	public abstract IList<IAgent> getAgents();

	public abstract void setPeers(IList<IAgent> peers);

	/**
	 * @throws GamaRuntimeException
	 *             Returns agents having the same species and sharing the same direct macro-agent
	 *             with this
	 *             agent.
	 * 
	 * @return
	 */
	public abstract IList<IAgent> getPeers() throws GamaRuntimeException;

	/**
	 * Verifies if this agent contains micro-agents or not.
	 * 
	 * @return true if this agent contains micro-agent(s)
	 *         false otherwise
	 */
	public abstract boolean hasMembers();

	/**
	 * Returns the agent which hosts the population of this agent.
	 * 
	 * @return
	 */
	public abstract IAgent getHost();

	public abstract void setHost(final IAgent hostAgent);
	
	public abstract List<IAgent> getMacroAgents();

	/**
	 * Acquires the object's intrinsic lock.
	 * 
	 * Solves the synchronization problem between Execution Thread and Event Dispatch Thread.
	 * 
	 * The synchronization problem may happen when
	 * 1. The Event Dispatch Thread is drawing an agent while the Execution Thread tries to it;
	 * 2. The Execution Thread is disposing the agent while the Event Dispatch Thread tries to draw
	 * it.
	 * 
	 * To avoid this, the corresponding thread has to invoke "acquireLock" to lock the agent before
	 * drawing or disposing the agent.
	 * After finish the task, the thread invokes "releaseLock" to release the agent's lock.
	 * 
	 * return
	 * true if the agent instance is available for use
	 * false otherwise
	 */
	public abstract boolean acquireLock();

	/**
	 * Releases the object's intrinsic lock.
	 */
	public abstract void releaseLock();

	/**
	 * Verifies if this agent can capture other agent as the specified micro-species.
	 * 
	 * An agent A can capture another agent B as newSpecies if the following conditions are correct:
	 * 1. other is not this agent;
	 * 2. other is not "world" agent;
	 * 3. newSpecies is a (direct) micro-species of A's species;
	 * 4. newSpecies is a direct sub-species of B's species.
	 * 
	 * @param other
	 * @return
	 *         true if this agent can capture other agent
	 *         false otherwise
	 */
	public abstract boolean canCapture(IAgent other, ISpecies newSpecies);

	/**
	 * Captures some agents as micro-agents with the specified micro-species as their new species.
	 * 
	 * @param microSpecies the species that the captured agents will become, this must be a
	 *            micro-species of this agent's species.
	 * @param microAgents
	 * @return
	 * @throws GamaRuntimeException
	 */
	public abstract IList<IAgent> captureMicroAgents(final ISpecies microSpecies,
		final IList<IAgent> microAgents) throws GamaRuntimeException;
	
	public abstract IAgent captureMicroAgent(final ISpecies microSpecies, final IAgent microAgent) throws GamaRuntimeException;

	/**
	 * Releases some micro-agents of this agent.
	 * 
	 * @param microAgents
	 * @return
	 * @throws GamaRuntimeException
	 */
	public abstract IList<IAgent> releaseMicroAgents(final IList<IAgent> microAgents)
		throws GamaRuntimeException;

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's species.
	 * 
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	public abstract IList<IAgent> migrateMicroAgents(final IList<IAgent> microAgents, final ISpecies newMicroSpecies);
	
	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's species.
	 * 
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	public abstract IList<IAgent> migrateMicroAgents(final ISpecies oldMicroSpecies, final ISpecies newMicroSpecies);

	/**
	 * Tells this agent that the host has changed its shape.
	 * This agent will then ask the topology to add its to the new ISpatialIndex.
	 */
	public abstract void hostChangesShape();

	public abstract void computeAgentsToSchedule(final IScope scope, final IList<IAgent> list)
		throws GamaRuntimeException;

}