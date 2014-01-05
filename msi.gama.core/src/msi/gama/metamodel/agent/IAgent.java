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
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul on Apr. 07, Modified on 24 oct. 2010, 05 Apr. 2013
 * 
 * @todo Description
 * 
 */
@vars({ @var(name = IKeyword.NAME, type = IType.STRING), @var(name = IKeyword.PEERS, type = IType.LIST),
	@var(name = IKeyword.HOST, type = IType.AGENT),
	@var(name = IKeyword.LOCATION, type = IType.POINT, depends_on = IKeyword.SHAPE),
	@var(name = IKeyword.SHAPE, type = IType.GEOMETRY) })
public interface IAgent extends ISkill, IShape, INamed, Comparable<IAgent>, IStepable {

	/**
	 * Returns the topology which manages this agent.
	 * 
	 * @return
	 */
	// @getter(IKeyword.TOPOLOGY)
	public abstract ITopology getTopology();

	@setter(IKeyword.PEERS)
	public abstract void setPeers(IList<IAgent> peers);

	/**
	 * Returns agents having the same species and sharing the same direct host with this agent.
	 * 
	 * @return
	 */
	@getter(IKeyword.PEERS)
	public abstract IList<IAgent> getPeers() throws GamaRuntimeException;

	@Override
	@getter(IKeyword.NAME)
	public abstract String getName();

	@Override
	@setter(IKeyword.NAME)
	public abstract void setName(String name);

	@Override
	@getter(value = IKeyword.LOCATION, initializer = true)
	public ILocation getLocation();

	@Override
	@setter(IKeyword.LOCATION)
	public void setLocation(final ILocation l);

	@Override
	@getter(IKeyword.SHAPE)
	public IShape getGeometry();

	@Override
	@setter(IKeyword.SHAPE)
	public void setGeometry(final IShape newGeometry);

	public abstract boolean dead();

	/**
	 * Returns the agent which hosts the population of this agent.
	 * 
	 * @return
	 */
	@getter(IKeyword.HOST)
	public abstract IMacroAgent getHost();

	@setter(IKeyword.HOST)
	public abstract void setHost(final IMacroAgent macroAgent);

	public abstract void schedule();

	/**
	 * Allows to set attributes that will be accessed by the "read" or "get" operators. Used for
	 * GIS/CSV attributes
	 * @param map
	 */
	public abstract void setExtraAttributes(final Map<Object, Object> map);

	public abstract int getIndex();

	public abstract void setIndex(int index);

	public String getSpeciesName();

	public abstract ISpecies getSpecies();

	public IPopulation getPopulation();

	public abstract boolean isInstanceOf(final ISpecies s, boolean direct);

	public void setHeading(Integer heading);

	public Integer getHeading();

	public abstract Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException;

	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException;

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
	public abstract void acquireLock();

	/**
	 * Releases the object's intrinsic lock.
	 */
	public abstract void releaseLock();

	/**
	 * Tells this agent that the host has changed its shape.
	 * This agent will then ask the topology to add its shape to the new ISpatialIndex.
	 */
	public abstract void hostChangesShape();

	public AgentScheduler getScheduler();

	public IModel getModel();

	public IExperimentAgent getExperiment();

	public IScope getScope();

	public abstract boolean isInstanceOf(String skill, boolean direct);

	public SimulationClock getClock();

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
	public abstract IPopulation getPopulationFor(final ISpecies microSpecies);

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
	 * @param speciesName the name of the species
	 * @return
	 */
	public abstract IPopulation getPopulationFor(final String speciesName);

}