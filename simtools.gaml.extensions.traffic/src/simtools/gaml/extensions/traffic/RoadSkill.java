package simtools.gaml.extensions.traffic;

import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.IType;


@vars({
	@var(name = "agents_on", type = IType.LIST, of = IType.LIST),@var(name = "source_node", type = IType.AGENT),@var(name = "target_node", type = IType.AGENT),
	 @var(name = "lanes", type = IType.INT), @var(name = "maxspeed", type = IType.FLOAT)})
@skill(name = "skill_road")
public class RoadSkill extends MovingSkill {

	
	public final static String AGENTS_ON = "agents_on";
	public final static String SOURCE_NODE = "source_node";
	public final static String TARGET_NODE = "target_node";
	public final static String LANES = "lanes";
	public final static String MAXSPEED = "maxspeed";

	@getter(AGENTS_ON)
	public List getAgentsOn(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS_ON);
	}
	
	@getter(SOURCE_NODE)
	public IAgent getSourceNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(SOURCE_NODE);
	}
	
	@getter(TARGET_NODE)
	public IAgent getTargetNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(TARGET_NODE);
	}
	
	@getter(LANES)
	public Integer getLanes(final IAgent agent) {
		return (Integer) agent.getAttribute(LANES);
	}
	
	@getter(MAXSPEED)
	public Double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAXSPEED);
	}
	
	@setter(LANES)
	public void setLanes(final IAgent agent, final int ln) {
		agent.setAttribute(LANES, ln);
	}
	@setter(MAXSPEED)
	public void setMaxSpeed(final IAgent agent, final Double sp) {
		agent.setAttribute(MAXSPEED, sp);
	}
	
	@setter(SOURCE_NODE)
	public void setSourceNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(SOURCE_NODE, nd);
	}
	
	@setter(TARGET_NODE)
	public void setTargetNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(TARGET_NODE, nd);
	}
	
	
	@action(name = "register", args = {
			@arg(name = "agent", type = IType.AGENT, optional = false, doc = @doc("the agent to register on the road.")),
			@arg(name = "lane", type = IType.INT, optional = false, doc = @doc("the lane index on which to register"))}, 
			doc = @doc(value = "register the agent on the road at the given lane",examples = { "do register agent: the_driver lane: 0" }))
	public void primRegister(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		int lane =  scope.getIntArg("lane");
		List agentsOn = getAgentsOn(agent);
		if (lane < agentsOn.size() && driver != null) {
			((List) agentsOn.get(lane)).add(driver);
			if (driver.hasAttribute("current_road") && driver.hasAttribute("current_lane")) {
				IAgent cr = (IAgent) driver.getAttribute("current_road");
				Integer pl = (Integer) driver.getAttribute("current_lane");
				if (cr != null && pl != null) {
					((List) getAgentsOn(cr).get(pl)).remove(driver);
				}
				driver.setAttribute("current_road", agent);
				driver.setAttribute("current_lane", lane);
			}
		}
		
		
	}
		
	@action(name = "unregister", args = {
			@arg(name = "agent", type = IType.AGENT, optional = false, doc = @doc("the agent to unregister on the road."))}, 
			doc = @doc(value = "unregister the agent on the road",examples = { "do unregister agent: the_driver" }))
	public void primUnregister(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		if (driver.hasAttribute("current_road") && driver.hasAttribute("current_lane")) {
			IAgent cr = (IAgent) driver.getAttribute("current_road");
			Integer pl = (Integer) driver.getAttribute("current_lane");
			if (cr != null && pl != null) {
				((List) getAgentsOn(cr).get(pl)).remove(driver);
			}
		}
	}

}
