/*******************************************************************************************************
 *
 * IAgent.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.INamed;
import msi.gama.common.interfaces.IScoped;
import msi.gama.common.interfaces.IStepable;
import msi.gama.common.interfaces.IVarAndActionSupport;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul on Apr. 07, Modified on 24 oct. 2010, 05 Apr. 2013
 *
 * @todo Description
 *
 */
@vars ({ @variable (
		name = IKeyword.NAME,
		type = IType.STRING,
		doc = { @doc ("Returns the name of the agent (not necessarily unique in its population)") }),
		@variable (
				name = IKeyword.PEERS,
				type = IType.LIST,
				of = ITypeProvider.OWNER_TYPE,
				doc = { @doc ("Returns the population of agents of the same species, in the same host, minus the receiver agent") }),
		@variable (
				name = IKeyword.HOST,
				type = ITypeProvider.MACRO_TYPE,
				doc = { @doc ("Returns the agent that hosts the population of the receiver agent") }),
		@variable (
				name = IKeyword.LOCATION,
				type = IType.POINT,
				depends_on = IKeyword.SHAPE,
				doc = { @doc ("Returns the location of the agent") }),

		@variable (
				name = IKeyword.SHAPE,
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the shape of the receiver agent") }) })
@doc ("The species hierarchy derives from a single built-in species, which is 'agent'. All its components (attributes, actions) will then be inherited by all direct "
		+ "or indirect children species (including 'model' and 'experiment' except species that explicitly set 'use_minimal_agents' facet to 'true', which inherit from"
		+ " a stripped-down version of 'agent'. ")
public interface IAgent extends /* ISkill, */ IShape, INamed, Comparable<IAgent>, IStepable,
		IContainer.Addressable<String, Object>, IVarAndActionSupport, IScoped {

	/**
	 * Returns the topology which manages this agent.
	 *
	 * @return
	 */
	ITopology getTopology();

	/**
	 * Sets the peers.
	 *
	 * @param peers the new peers
	 */
	@setter (IKeyword.PEERS)
	void setPeers(IList<IAgent> peers);

	/**
	 * Returns agents having the same species and sharing the same direct host with this agent.
	 *
	 * @return
	 */
	@getter (IKeyword.PEERS)
	IList<IAgent> getPeers() throws GamaRuntimeException;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	@getter (
			value = IKeyword.NAME,
			initializer = true)
	String getName();

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	@Override
	@setter (IKeyword.NAME)
	void setName(String name);

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	@Override
	@getter (
			value = IKeyword.LOCATION,
			initializer = true)
	GamaPoint getLocation();

	/**
	 * Sets the location.
	 *
	 * @param l the l
	 * @return the gama point
	 */
	@Override
	@setter (IKeyword.LOCATION)
	GamaPoint setLocation(final GamaPoint l);

	/**
	 * Gets the geometry.
	 *
	 * @return the geometry
	 */
	@Override
	@getter (IKeyword.SHAPE)
	IShape getGeometry();

	/**
	 * Sets the geometry.
	 *
	 * @param newGeometry the new geometry
	 */
	@Override
	@setter (IKeyword.SHAPE)
	void setGeometry(final IShape newGeometry);

	/**
	 * Dead.
	 *
	 * @return true, if successful
	 */
	boolean dead();

	/**
	 * Returns the agent which hosts the population of this agent.
	 *
	 * @return
	 */
	@getter (IKeyword.HOST)
	IMacroAgent getHost();

	/**
	 * Sets the host.
	 *
	 * @param macroAgent the new host
	 */
	@setter (IKeyword.HOST)
	void setHost(final IMacroAgent macroAgent);

	/**
	 * Schedule.
	 *
	 * @param scope the scope
	 */
	void schedule(IScope scope);

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	int getIndex();

	/**
	 * Gets the species name.
	 *
	 * @return the species name
	 */
	String getSpeciesName();

	/**
	 * Gets the species.
	 *
	 * @return the species
	 */
	ISpecies getSpecies();

	/**
	 * Gets the population.
	 *
	 * @return the population
	 */
	IPopulation<? extends IAgent> getPopulation();

	/**
	 * Checks if is instance of.
	 *
	 * @param s the s
	 * @param direct the direct
	 * @return true, if is instance of
	 */
	boolean isInstanceOf(final ISpecies s, boolean direct);

	/**
	 * Gets the direct var value.
	 *
	 * @param scope the scope
	 * @param s the s
	 * @return the direct var value
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException;

	/**
	 * Sets the direct var value.
	 *
	 * @param scope the scope
	 * @param s the s
	 * @param v the v
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException;

	/**
	 * A utility method to notify any variable listener of a value change
	 *
	 * @param varName
	 * @param newValue
	 */
	default void notifyVarValueChange(final String varName, final Object newValue) {
		IVariable var = getSpecies().getVar(varName);
		if (var == null) return;
		var.notifyOfValueChange(getScope(), this, null, newValue);
	}

	/**
	 * Gets the macro agents.
	 *
	 * @return the macro agents
	 */
	List<IAgent> getMacroAgents();

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	IModel getModel();

	/**
	 * Checks if is instance of.
	 *
	 * @param skill the skill
	 * @param direct the direct
	 * @return true, if is instance of
	 */
	boolean isInstanceOf(String skill, boolean direct);

	/**
	 * @throws GamaRuntimeException
	 *             Finds the corresponding population of a species from the "viewpoint" of this agent.
	 *
	 *             An agent can "see" the following populations: 1. populations of its species' direct micro-species; 2.
	 *             population of its species; populations of its peer species; 3. populations of its direct&in-direct
	 *             macro-species and of their peers.
	 *
	 * @param microSpecies
	 * @return
	 */
	IPopulation<? extends IAgent> getPopulationFor(final ISpecies microSpecies);

	/**
	 * @throws GamaRuntimeException
	 *             Finds the corresponding population of a species from the "viewpoint" of this agent.
	 *
	 *             An agent can "see" the following populations: 1. populations of its species' direct micro-species; 2.
	 *             population of its species; populations of its peer species; 3. populations of its direct&in-direct
	 *             macro-species and of their peers.
	 *
	 * @param speciesName
	 *            the name of the species
	 * @return
	 */
	IPopulation<? extends IAgent> getPopulationFor(final String speciesName);

	/**
	 * Update with.
	 *
	 * @param s the s
	 * @param sa the sa
	 */
	void updateWith(final IScope s, final SavedAgent sa);
}