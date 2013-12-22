package simtools.gaml.extensions.traffic;


import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.IType;


@vars({
	 @var(name = "roads_in", type = IType.LIST, of = IType.AGENT), @var(name = "roads_out", type = IType.LIST, of = IType.AGENT),@var(name = "stop", type = IType.LIST, of =  IType.LIST),@var(name = "block", type = IType.MAP)})
@skill(name = "skill_road_node")
public class RoadNodeSkill extends MovingSkill {

	public final static String ROADS_IN = "roads_in";
	public final static String ROADS_OUT = "roads_out";
	public final static String STOP = "stop";
	public final static String BLOCK = "block";

	@getter(ROADS_IN)
	public List getRoadsIn(final IAgent agent) {
		return (List) agent.getAttribute(ROADS_IN);
	}
	
	@getter(ROADS_OUT)
	public List getRoadsOut(final IAgent agent) {
		return (List) agent.getAttribute(ROADS_OUT);
	}
	
	@setter(ROADS_IN)
	public void setSourceNode(final IAgent agent, final List rds) {
		agent.setAttribute(ROADS_IN, rds);
	}
	
	@setter(ROADS_OUT)
	public void setTargetNode(final IAgent agent, final List rds) {
		agent.setAttribute(ROADS_OUT, rds);
	}
	
	@getter(STOP)
	public List<List> getStop(final IAgent agent) {
		return (List<List>) agent.getAttribute(STOP);
	}
	@setter(STOP)
	public void setStop(final IAgent agent, final List<List> stop) {
		agent.setAttribute(STOP, stop);
	}
	
	@getter(BLOCK)
	public Map<IAgent,List> getBlock(final IAgent agent) {
		return (Map<IAgent,List>) agent.getAttribute(BLOCK);
	}
	@setter(BLOCK)
	public void setBlock(final IAgent agent, final Map<IAgent,List> block) {
		agent.setAttribute(BLOCK, block);
	}
	
}
