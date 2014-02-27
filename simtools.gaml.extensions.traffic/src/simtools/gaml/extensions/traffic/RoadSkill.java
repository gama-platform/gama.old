package simtools.gaml.extensions.traffic;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

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
	@var(name = "agents_on", type = IType.LIST, of = IType.LIST, doc = @doc("for each lane of the road, the list of agents on the lane")),
	@var(name = "source_node", type = IType.AGENT, doc = @doc("the source node of the road")),
	@var(name = "target_node", type = IType.AGENT, doc = @doc("the target node of the road")),
	@var(name = "lanes", type = IType.INT, doc = @doc("the number of lanes")), 
	@var(name = "linked_road", type = IType.AGENT, doc = @doc("the linked road: the lanes of this linked road will be usable by drivers on the road")), 
	@var(name = "maxspeed", type = IType.FLOAT, doc = @doc("the maximal speed on the road"))})
@skill(name = "skill_road")
public class RoadSkill extends MovingSkill {

	
	public final static String AGENTS_ON = "agents_on";
	public final static String SOURCE_NODE = "source_node";
	public final static String TARGET_NODE = "target_node";
	public final static String LANES = "lanes";
	public final static String MAXSPEED = "maxspeed";
	public final static String LINKED_ROAD = "linked_road";

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
	
	@getter(LINKED_ROAD)
	public IAgent getLinkedRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(LINKED_ROAD);
	}
	@setter(LINKED_ROAD)
	public void setLinkedRoad(final IAgent agent, final IAgent rd) {
		agent.setAttribute(LINKED_ROAD, rd);
	}
	
	public static void register(IAgent road,IAgent driver,int lane ) throws GamaRuntimeException {
		final IAgent linkedRoad = (IAgent) road.getAttribute(LINKED_ROAD);
		final boolean agentOnLinkedRoad = (Boolean) driver.getAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD);
		final int nbLanes = (Integer) road.getAttribute(LANES);
		if (driver != null) {
			IAgent cr = (IAgent) driver.getAttribute(AdvancedDrivingSkill.CURRENT_ROAD);
			Integer pl = (Integer) driver.getAttribute(AdvancedDrivingSkill.CURRENT_LANE);
			if (cr != null && pl != null) {
				if (agentOnLinkedRoad) {
					IAgent lr =(IAgent) cr.getAttribute(LINKED_ROAD);
					((List) ((List) lr.getAttribute(AGENTS_ON)).get(pl)).remove(driver);
				} else {
					((List) ((List) cr.getAttribute(AGENTS_ON)).get(pl)).remove(driver);
				}
			}
			if (lane >= nbLanes && linkedRoad != null) {
				int nbLanesLinked = (Integer) linkedRoad.getAttribute(LANES);
				lane = nbLanesLinked - nbLanes - lane + 1;
				lane = Math.max(0, Math.min(lane, nbLanesLinked-1));
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, true);
				List agentsOn = (List) linkedRoad.getAttribute(AGENTS_ON);
				((List) agentsOn.get(lane)).add(driver);
			} else {
				lane = Math.min(lane, nbLanes -1);
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, false);
				List agentsOn = (List) road.getAttribute(AGENTS_ON);
				
				((List) agentsOn.get(lane)).add(driver);
			}
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_ROAD, road);
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_LANE, lane);
			//driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX, 0);
		}
	}
	
	@action(name = "register", args = {
			@arg(name = "agent", type = IType.AGENT, optional = false, doc = @doc("the agent to register on the road.")),
			@arg(name = "lane", type = IType.INT, optional = false, doc = @doc("the lane index on which to register; if lane index >= number of lanes, then register on the linked road"))}, 
			doc = @doc(value = "register the agent on the road at the given lane",examples = { "do register agent: the_driver lane: 0" }))
	public void primRegister(final IScope scope) throws GamaRuntimeException {
		IAgent road = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		final IAgent linkedRoad = getLinkedRoad(road);
		final boolean agentOnLinkedRoad = (Boolean) driver.getAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD);
		final int nbLanes = getLanes(road);
		int lane = scope.getIntArg("lane");
		if (driver != null) {
			IAgent cr = (IAgent) driver.getAttribute(AdvancedDrivingSkill.CURRENT_ROAD);
			Integer pl = (Integer) driver.getAttribute(AdvancedDrivingSkill.CURRENT_LANE);
			if (cr != null && pl != null) {
				if (agentOnLinkedRoad) {
					IAgent lr = getLinkedRoad(cr);
					((List) getAgentsOn(lr).get(pl)).remove(driver);
				} else {
					((List) getAgentsOn(cr).get(pl)).remove(driver);
				}
			}
			if (lane >= nbLanes && linkedRoad != null) {
				int nbLanesLinked = getLanes(linkedRoad);
				lane = nbLanesLinked - nbLanes - lane + 1;
				lane = Math.max(0, Math.min(lane, nbLanesLinked-1));
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, true);
				List agentsOn = getAgentsOn(linkedRoad);
				((List) agentsOn.get(lane)).add(driver);
			} else {
				lane = Math.min(lane, nbLanes -1);
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, false);
				List agentsOn = getAgentsOn(road);
				((List) agentsOn.get(lane)).add(driver);
			}
			//System.out.println("register " + driver + " lane : " + lane);
			Coordinate[] coords = road.getInnerGeometry().getCoordinates();
			Coordinate pt = (Coordinate) driver.getLocation();
			if (coords[0].equals(pt)) 
				driver.setAttribute(AdvancedDrivingSkill.DISTANCE_TO_GOAL, coords[1].distance(pt));
			else {
				Coordinate cc = coords[1];
				double min = coords[0].distance(pt) + coords[1].distance(pt);
				for (int i = 1; i < coords.length - 2; i++) {
					double dt = coords[i].distance(pt) + coords[i+1].distance(pt);
					if (dt < min) {
						min = dt;
						cc = coords[i+1];
					}
				}
				driver.setAttribute(AdvancedDrivingSkill.DISTANCE_TO_GOAL, cc.distance(pt));
			}
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_ROAD, road);
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_LANE, lane);
			//driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX, 0);
		}
	}
		
	@action(name = "unregister", args = {
			@arg(name = "agent", type = IType.AGENT, optional = false, doc = @doc("the agent to unregister on the road."))}, 
			doc = @doc(value = "unregister the agent on the road",examples = { "do unregister agent: the_driver" }))
	public void primUnregister(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		//driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX, -1);
		final boolean agentOnLinkedRoad = (Boolean) driver.getAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD);
		if (driver.hasAttribute("current_road") && driver.hasAttribute("current_lane")) {
			IAgent cr = (IAgent) driver.getAttribute("current_road");
			Integer pl = (Integer) driver.getAttribute("current_lane");
			if (cr != null && pl != null) {
				if (agentOnLinkedRoad) {
					IAgent lr = getLinkedRoad(cr);
					((List) getAgentsOn(lr).get(pl)).remove(driver);
				} else {
					((List) getAgentsOn(cr).get(pl)).remove(driver);
				}
			}
		}
	}

}
