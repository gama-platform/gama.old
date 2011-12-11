/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.skills;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;

/**
 * CarryingSkill. A variables and behaviours holder for agents able to carry other agents. They
 * serve as a temporary place for them. This is a stateless skill.
 * 
 * @author Alexis Drogoul 4 juil. 07
 */
@skill("carrying")
@vars({ @var(name = "capacity", type = IType.INT_STR, init = "1"),
	@var(name = "contents", type = IType.LIST_STR, init = "[]") })
public class CarryingSkill extends Skill {

	@action("load")
	@args({ "agents" })
	public List primLoad(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		Integer cap = (Integer) scope.getAgentVarValue(agent, "capacity");
		List<IAgent> agents = Cast.asList(scope.getArg("agents"));
		if ( agents.isEmpty() ) {
			scope.setStatus(ExecutionStatus.failure);
			return agents;
		}
		final int number = Math.min(cap, agents.size());
		if ( number == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
			return agents;
		}
		agents = agents.subList(0, number);
		List carriedEntities = (List) scope.getAgentVarValue(agent, "contents");
		for ( final IAgent a : agents ) {
			if ( !carriedEntities.contains(a) ) {
				carriedEntities.add(a);
				a.getTopology().removeAgent(a);
			}
		}
		scope.setAgentVarValue(agent, "capacity", (cap - number));
		scope.setStatus(ExecutionStatus.skipped);
		return agents;

	}

	@action("drop")
	@args({ "agents" })
	public GamaList primDrop(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		List contents = (List) scope.getAgentVarValue(agent, "contents");
		GamaList<IAgent> agents = Cast.asList(scope.getArg("agents"));
		if ( agents.isEmpty() ) {
			agents = new GamaList(contents);
		}
		if ( agents.isEmpty() ) {
			scope.setStatus(ExecutionStatus.failure);
			return agents;
		}
		for ( final IAgent a : agents ) {
			if ( contents.contains(a) ) {
				a.getGeometry().setLocation(agent.getLocation()); // ???
				contents.remove(a);
			}
		}
		scope.setAgentVarValue(agent, "capacity",
			((Integer) scope.getAgentVarValue(agent, "capacity") + agents.size()));
		scope.setStatus(ExecutionStatus.skipped);
		return agents;
	}

}
