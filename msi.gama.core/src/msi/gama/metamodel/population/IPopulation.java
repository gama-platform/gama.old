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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.commands.IAspect;
import msi.gaml.species.ISpecies;
import msi.gaml.variables.IVariable;

/**
 * A population is a collection of agents of a species.
 * 
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IPopulation extends Comparable<IPopulation>, IContainer<Integer, IAgent> {

	public abstract void dispose();

	public abstract IAgent getAgent(final ILocation value);

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
	public abstract IList<? extends IAgent> createAgents(IScope scope, int number,
		List<Map<String, Object>> initialValues, boolean isRestored) throws GamaRuntimeException;

	public abstract IList<IAgent> getAgentsList();

	public abstract String getName();

	public abstract int size();

	public abstract boolean isGrid();

	public abstract boolean isGlobal();

	public abstract boolean hasAspect(String default1);

	public abstract IAspect getAspect(String default1);

	public abstract IList<String> getAspectNames();

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
	 * @throws GamaRuntimeException
	 *             When the "shape" of host changes, this method is invoked to update the topology.
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
	public abstract void computeAgentsToSchedule(IScope scope, IList list)
		throws GamaRuntimeException;

	/**
	 * @param obj
	 * @return
	 */
	public abstract IAgent getAgent(Integer obj);
	
	public void addListener(IPopulationListener listener);
	public void removeListener(IPopulationListener listener);
	
	
}