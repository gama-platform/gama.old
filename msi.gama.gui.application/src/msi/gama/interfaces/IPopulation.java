/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import java.util.*;
import msi.gama.environment.ITopology;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

/**
 * A population is a collection of agents of a species.
 * 
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IPopulation extends Comparable<IPopulation>, IGamaContainer<Integer, IAgent> {

	public abstract void dispose();

	public abstract IAgent getAgent(final GamaPoint value);

	/**
	 * Create agents as members of this population.
	 * 
	 * @param scope
	 * @param number The number of agent to create.
	 * @param initialValues The initial values of agents' variables.
	 * @param isRestored Indicates that the agents are newly created or they are restored (on a
	 *            capture or release).
	 *            If agents are restored on a capture or release then don't run their "init" reflex
	 *            again.
	 * 
	 * @return
	 * @throws GamaRuntimeException
	 */
	public abstract List<? extends IAgent> createAgents(IScope scope, int number,
		List<Map<String, Object>> initialValues, boolean isRestored) throws GamaRuntimeException;

	public abstract GamaList<IAgent> getAgentsList();

	public abstract String getName();

	public abstract int size();

	public abstract boolean isGrid();

	public abstract boolean isGlobal();

	public abstract boolean hasAspect(String default1);

	public abstract IAspect getAspect(String default1);

	public abstract List<String> getAspectNames();

	public abstract ISpecies getSpecies();

	public abstract boolean manages(ISpecies s, boolean direct);

	public abstract IVariable getVar(final IAgent a, final String s);

	boolean hasUpdatableVariables();

	/**
	 * Returns the topology associated to this population.
	 * 
	 * @return
	 */
	public abstract ITopology getTopology();

	public abstract void initializeFor(IScope scope) throws GamaRuntimeException;

	/**
	 * Returns the macro-agent hosting this population.
	 * 
	 * @return
	 */
	public abstract IAgent getHost();

	/**
	 * When the "shape" of host changes, this method is invoked to update the topology.
	 */
	public abstract void hostChangesShape();

	/**
	 * Kills all the agents managed by this population.
	 * 
	 * @throws GamaRuntimeException
	 */
	public abstract void killMembers() throws GamaRuntimeException;

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param list
	 */
	public abstract void computeAgentsToSchedule(IScope scope, GamaList list)
		throws GamaRuntimeException;

	/**
	 * @param obj
	 * @return
	 */
	public abstract IAgent getAgent(Integer obj);
}