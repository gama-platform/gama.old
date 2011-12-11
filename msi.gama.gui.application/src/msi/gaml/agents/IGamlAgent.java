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
package msi.gaml.agents;

import java.util.List;
import msi.gama.environment.ITopology;
import msi.gama.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;

/**
 * A basic entity has a species, variables which it can update, skills which it can use (mainly for
 * updating the variables, since it does not have any behaviour) and at least a "name" among its
 * variables.
 * 
 * @author Alexis Drogoul, 9 juin 07
 */

@vars({ @var(name = INamed.NAME, type = IType.STRING_STR),
	@var(name = IGamlAgent.MEMBERS, type = IType.LIST_STR),
	@var(name = IGamlAgent.PEERS, type = IType.LIST_STR),
	@var(name = IGamlAgent.AGENTS, type = IType.LIST_STR),
	@var(name = IGamlAgent.HOST, type = IType.NONE_STR),
	@var(name = IGamlAgent.LOCATION, type = IType.POINT_STR, depends_on = IGamlAgent.SHAPE),
	@var(name = IGamlAgent.SHAPE, type = IType.GEOM_STR),
	@var(name = ISymbol.TOPOLOGY, type = IType.TOPOLOGY_STR) })
public interface IGamlAgent extends IAgent {

	public static final String BEHAVIORS = "behaviors";

	public static final String HOST = "host";

	public static final String MEMBERS = "members";

	public static final String AGENTS = "agents";

	public static final String PEERS = "peers";

	public static final String SHAPE = "shape";

	public static final String LOCATION = "location";

	@Override
	@getter(var = IGamlAgent.MEMBERS)
	public abstract List<IAgent> getMembers();

	@Override
	@getter(var = ISymbol.TOPOLOGY)
	public abstract ITopology getTopology();

	@setter(ISymbol.TOPOLOGY)
	public abstract void setTopology(ITopology t);

	@Override
	@setter(IGamlAgent.MEMBERS)
	public abstract void setMembers(List<IAgent> members);

	@Override
	@setter(IGamlAgent.AGENTS)
	public abstract void setAgents(List<IAgent> agents);

	@Override
	@getter(var = IGamlAgent.AGENTS)
	public abstract List<IAgent> getAgents();

	@Override
	@setter(IGamlAgent.PEERS)
	public abstract void setPeers(List<IAgent> peers);

	/**
	 * Returns agents having the same species and sharing the same direct host with this agent.
	 * 
	 * @return
	 */
	@Override
	@getter(var = IGamlAgent.PEERS)
	public abstract List<IAgent> getPeers();

	@Override
	@getter(var = INamed.NAME)
	public abstract String getName();

	@setter(INamed.NAME)
	public abstract void setName(String name);

	@Override
	@getter(var = LOCATION, initializer = true)
	public GamaPoint getLocation();

	@Override
	@setter(LOCATION)
	public void setLocation(final GamaPoint l);

	@Override
	@getter(var = SHAPE)
	public GamaGeometry getGeometry();

	@Override
	@setter(SHAPE)
	public void setGeometry(final GamaGeometry newGeometry);

	@Override
	public abstract boolean dead();

	@Override
	@getter(var = HOST)
	public abstract IAgent getHost();

	@Override
	@setter(HOST)
	public abstract void setHost(final IAgent macroAgent);

}