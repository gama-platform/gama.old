/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.agent;

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.util.IList;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.types.IType;

/**
 * A basic entity has a species, variables which it can update, skills which it can use (mainly for
 * updating the variables, since it does not have any behaviour) and at least a "name" among its
 * variables.
 * 
 * @author Alexis Drogoul, 9 juin 07
 */

@vars({ @var(name = IKeyword.NAME, type = IType.STRING_STR),
	@var(name = IKeyword.MEMBERS, type = IType.LIST_STR),
	@var(name = IKeyword.PEERS, type = IType.LIST_STR),
	@var(name = IKeyword.AGENTS, type = IType.LIST_STR),
	@var(name = IKeyword.HOST, type = IType.NONE_STR),
	@var(name = IKeyword.LOCATION, type = IType.POINT_STR, depends_on = IKeyword.SHAPE),
	@var(name = IKeyword.SHAPE, type = IType.GEOM_STR),
	@var(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY_STR) })
public interface IGamlAgent extends IAgent {

	@Override
	@getter(var = IKeyword.MEMBERS)
	public abstract IList<IAgent> getMembers();

	@Override
	@getter(var = IKeyword.TOPOLOGY)
	public abstract ITopology getTopology();

	@setter(IKeyword.TOPOLOGY)
	public abstract void setTopology(ITopology t);

	@Override
	@setter(IKeyword.MEMBERS)
	public abstract void setMembers(IList<IAgent> members);

	@Override
	@setter(IKeyword.AGENTS)
	public abstract void setAgents(IList<IAgent> agents);

	@Override
	@getter(var = IKeyword.AGENTS)
	public abstract IList<IAgent> getAgents();

	@Override
	@setter(IKeyword.PEERS)
	public abstract void setPeers(IList<IAgent> peers);

	/**
	 * Returns agents having the same species and sharing the same direct host with this agent.
	 * 
	 * @return
	 */
	@Override
	@getter(var = IKeyword.PEERS)
	public abstract IList<IAgent> getPeers();

	@Override
	@getter(var = IKeyword.NAME)
	public abstract String getName();

	@Override
	@setter(IKeyword.NAME)
	public abstract void setName(String name);

	@Override
	@getter(var = IKeyword.LOCATION, initializer = true)
	public ILocation getLocation();

	@Override
	@setter(IKeyword.LOCATION)
	public void setLocation(final ILocation l);

	@Override
	@getter(var = IKeyword.SHAPE)
	public IShape getGeometry();

	@Override
	@setter(IKeyword.SHAPE)
	public void setGeometry(final IShape newGeometry);

	@Override
	public abstract boolean dead();

	@Override
	@getter(var = IKeyword.HOST)
	public abstract IAgent getHost();

	@Override
	@setter(IKeyword.HOST)
	public abstract void setHost(final IAgent macroAgent);

}