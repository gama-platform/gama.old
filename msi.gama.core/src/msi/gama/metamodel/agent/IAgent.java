/*********************************************************************************************
 *
 *
 * 'IAgent.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul on Apr. 07, Modified on 24 oct. 2010, 05 Apr. 2013
 *
 * @todo Description
 *
 */
@vars({ @var(name = IKeyword.NAME,
	type = IType.STRING,
	doc = { @doc("Returns the name of the agent (not necessarily unique in its population)") }),
	@var(name = IKeyword.PEERS,
		type = IType.LIST,
		of = ITypeProvider.OWNER_TYPE,
		doc = {
			@doc("Returns the population of agents of the same species, in the same host, minus the receiver agent") }),
	@var(name = IKeyword.HOST,
		type = ITypeProvider.MACRO_TYPE,
		doc = { @doc("Returns the agent that hosts the population of the receiver agent") }),
	@var(name = IKeyword.LOCATION,
		type = IType.POINT,
		depends_on = IKeyword.SHAPE,
		doc = { @doc("Returns the location of the agent") }),
	@var(name = IKeyword.SHAPE, type = IType.GEOMETRY, doc = { @doc("Returns the shape of the receiver agent") }) })
public interface IAgent extends /* ISkill, */ IShape, INamed, Comparable<IAgent>, IStepable, IContainer.Addressable<String, Object>, IVarAndActionSupport {

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
	@getter(value = IKeyword.NAME, initializer = true)
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

	public abstract void schedule(IScope scope);

	// public abstract void scheduleAndExecute(RemoteSequence sequence);

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

	// public void setHeading(Integer heading);

	// public Integer getHeading();

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
	// public abstract void acquireLock();

	/**
	 * Releases the object's intrinsic lock.
	 */
	// public abstract void releaseLock();

	/**
	 * Tells this agent that the host has changed its shape.
	 * This agent will then ask the topology to add its shape to the new ISpatialIndex.
	 */
	// public abstract void hostChangesShape();

	// public ActionExecuter getScheduler();

	public IModel getModel();

	public IExperimentAgent getExperiment();

	public IScope getScope();

	public abstract boolean isInstanceOf(String skill, boolean direct);

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