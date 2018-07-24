/*********************************************************************************************
 *
 *
 * 'RoadSkill.java', in plugin 'simtools.gaml.extensions.traffic', is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars ({ @variable (
		name = "agents_on",
		type = IType.LIST,
		of = IType.LIST,
		doc = @doc ("for each lane of the road, the list of agents for each segment")),
		@variable (
				name = "all_agents",
				type = IType.LIST,
				of = IType.AGENT,
				doc = @doc ("the list of agents on the road")),
		@variable (
				name = "source_node",
				type = IType.AGENT,
				doc = @doc ("the source node of the road")),
		@variable (
				name = "target_node",
				type = IType.AGENT,
				doc = @doc ("the target node of the road")),
		@variable (
				name = "lanes",
				type = IType.INT,
				doc = @doc ("the number of lanes")),
		@variable (
				name = "linked_road",
				type = ITypeProvider.OWNER_TYPE,
				doc = @doc ("the linked road: the lanes of this linked road will be usable by drivers on the road")),
		@variable (
				name = "maxspeed",
				type = IType.FLOAT,
				doc = @doc ("the maximal speed on the road")) })
@skill (
		name = "skill_road",
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill for agents representing roads in traffic simulations"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class RoadSkill extends Skill {

	public final static String AGENTS = "all_agents";

	public final static String AGENTS_ON = "agents_on";
	public final static String SOURCE_NODE = "source_node";
	public final static String TARGET_NODE = "target_node";
	public final static String LANES = "lanes";
	public final static String MAXSPEED = "maxspeed";
	public final static String LINKED_ROAD = "linked_road";

	@getter (AGENTS_ON)
	public static List getAgentsOn(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS_ON);
	}

	@getter (AGENTS)
	public static List getAgents(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS);
	}

	@getter (SOURCE_NODE)
	public static IAgent getSourceNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(SOURCE_NODE);
	}

	@getter (TARGET_NODE)
	public static IAgent getTargetNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(TARGET_NODE);
	}

	@getter (LANES)
	public static Integer getLanes(final IAgent agent) {
		return (Integer) agent.getAttribute(LANES);
	}

	@getter (MAXSPEED)
	public static Double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAXSPEED);
	}

	@setter (LANES)
	public static void setLanes(final IAgent agent, final int ln) {
		agent.setAttribute(LANES, ln);
	}

	@setter (MAXSPEED)
	public static void setMaxSpeed(final IAgent agent, final Double sp) {
		agent.setAttribute(MAXSPEED, sp);
	}

	@setter (SOURCE_NODE)
	public static void setSourceNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(SOURCE_NODE, nd);
	}

	@setter (TARGET_NODE)
	public void setTargetNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(TARGET_NODE, nd);
	}

	@getter (LINKED_ROAD)
	public static IAgent getLinkedRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(LINKED_ROAD);
	}

	@setter (LINKED_ROAD)
	public static void setLinkedRoad(final IAgent agent, final IAgent rd) {
		agent.setAttribute(LINKED_ROAD, rd);
	}

	public static void register(final IAgent road, final IAgent driver, int lane) throws GamaRuntimeException {
		final IAgent linkedRoad = (IAgent) road.getAttribute(LINKED_ROAD);
		final boolean agentOnLinkedRoad =
				driver == null ? false : (Boolean) driver.getAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD);
		final int nbLanes = (Integer) road.getAttribute(LANES);

		if (driver != null) {
			final IAgent cr = (IAgent) driver.getAttribute(AdvancedDrivingSkill.CURRENT_ROAD);
			final Integer pl = (Integer) driver.getAttribute(AdvancedDrivingSkill.CURRENT_LANE);
			if (cr != null && pl != null) {
				Integer segmentIndex = (Integer) driver.getAttribute(AdvancedDrivingSkill.SEGMENT_INDEX);
				if (agentOnLinkedRoad) {
					final IAgent lr = getLinkedRoad(cr);
					final List agsLane = (List) getAgentsOn(lr).get(pl);
					if (segmentIndex == null) {
						segmentIndex = getSegmentIndex(lr, driver);
					}
					((List) agsLane.get(agsLane.size() - 1 - segmentIndex)).remove(driver);
					getAgents(cr).remove(driver);
				} else {
					if (segmentIndex == null) {
						segmentIndex = getSegmentIndex(cr, driver);
					}
					((List) ((List) getAgentsOn(cr).get(pl)).get(segmentIndex)).remove(driver);
					getAgents(cr).remove(driver);
				}
			}
			int indexSegment = 0;
			boolean onLinkedRoad = false;
			if (lane >= nbLanes && linkedRoad != null) {
				final int nbLanesLinked = (Integer) linkedRoad.getAttribute(LANES);
				onLinkedRoad = true;
				lane = nbLanesLinked - nbLanes - lane + 1;

				lane = CmnFastMath.max(0, CmnFastMath.min(lane, nbLanesLinked - 1));
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, true);

				final List agentsOn = (List) linkedRoad.getAttribute(AGENTS_ON);
				final List ags = (List) agentsOn.get(lane);
				((List) ags.get(ags.size() - 1)).add(driver);
				getAgents(road).add(driver);
			} else {
				lane = CmnFastMath.min(lane, nbLanes - 1);
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, false);
				indexSegment = getSegmentIndex(road, driver);
				final List agentsOn = (List) road.getAttribute(AGENTS_ON);
				((List) ((List) agentsOn.get(lane)).get(indexSegment)).add(driver);
				getAgents(road).add(driver);
			}
			driver.setAttribute(AdvancedDrivingSkill.DISTANCE_TO_GOAL,
					driver.getLocation().euclidianDistanceTo(road.getPoints().get(indexSegment + 1)));
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_ROAD, road);
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_LANE, lane);
			driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX,
					onLinkedRoad ? road.getInnerGeometry().getNumPoints() - indexSegment - 2 : indexSegment);

		}
	}

	public static int getSegmentIndex(final IAgent road, final IAgent driver) {
		final Coordinate[] coords = road.getInnerGeometry().getCoordinates();
		if (coords.length == 2) { return 0; }

		final ILocation loc = driver.getLocation();
		for (int i = 0; i < coords.length - 1; i++) {
			if (coords[i].equals(loc)) { return i; }
		}
		double distanceS = Double.MAX_VALUE;
		int indexSegment = 0;
		final Point pointS = (Point) loc.getInnerGeometry();
		final int nbSp = coords.length;
		final Coordinate[] temp = new Coordinate[2];
		for (int i = 0; i < nbSp - 1; i++) {
			temp[0] = coords[i];
			temp[1] = coords[i + 1];
			final LineString segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(temp);
			final double distS = segment.distance(pointS);
			if (distS < distanceS) {
				distanceS = distS;
				indexSegment = i;
			}
		}
		return indexSegment;

	}

	@action (
			name = "register",
			args = { @arg (
					name = "agent",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the agent to register on the road.")),
					@arg (
							name = "lane",
							type = IType.INT,
							optional = false,
							doc = @doc ("the lane index on which to register; if lane index >= number of lanes, then register on the linked road")) },
			doc = @doc (
					value = "register the agent on the road at the given lane",
					examples = { @example ("do register agent: the_driver lane: 0") }))
	public void primRegister(final IScope scope) throws GamaRuntimeException {
		final IAgent road = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		final IAgent linkedRoad = getLinkedRoad(road);
		final boolean agentOnLinkedRoad =
				driver == null ? false : (Boolean) driver.getAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD);
		final int nbLanes = getLanes(road);
		int lane = scope.getIntArg("lane");

		if (driver != null) {
			final IAgent cr = (IAgent) driver.getAttribute(AdvancedDrivingSkill.CURRENT_ROAD);
			final Integer pl = (Integer) driver.getAttribute(AdvancedDrivingSkill.CURRENT_LANE);
			if (cr != null && pl != null) {
				Integer segmentIndex = (Integer) driver.getAttribute(AdvancedDrivingSkill.SEGMENT_INDEX);
				if (agentOnLinkedRoad) {
					final IAgent lr = getLinkedRoad(cr);
					final List agsLane = (List) getAgentsOn(lr).get(pl);
					if (segmentIndex == null) {
						segmentIndex = getSegmentIndex(lr, driver);
					}
					((List) agsLane.get(agsLane.size() - 1 - segmentIndex)).remove(driver);
					getAgents(cr).remove(driver);
				} else {
					if (segmentIndex == null) {
						segmentIndex = getSegmentIndex(cr, driver);
					}
					((List) ((List) getAgentsOn(cr).get(pl)).get(segmentIndex)).remove(driver);
					getAgents(cr).remove(driver);
				}
			}
			int indexSegment = 0;
			if (lane >= nbLanes && linkedRoad != null) {
				final int nbLanesLinked = (Integer) linkedRoad.getAttribute(LANES);
				lane = nbLanesLinked - nbLanes - lane + 1;

				lane = CmnFastMath.max(0, CmnFastMath.min(lane, nbLanesLinked - 1));
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, true);

				final List agentsOn = (List) linkedRoad.getAttribute(AGENTS_ON);
				final List ags = (List) agentsOn.get(lane);
				((List) ags.get(ags.size() - 1)).add(driver);
				getAgents(road).add(driver);
			} else {
				lane = CmnFastMath.min(lane, nbLanes - 1);
				driver.setAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD, false);
				indexSegment = getSegmentIndex(road, driver);
				final List agentsOn = (List) road.getAttribute(AGENTS_ON);
				((List) ((List) agentsOn.get(lane)).get(indexSegment)).add(driver);
				getAgents(road).add(driver);
			}
			// System.out.println("register " + driver + " lane : " + lane);
			final Coordinate[] coords = road.getInnerGeometry().getCoordinates();
			final Coordinate pt = (Coordinate) driver.getLocation();
			if (coords[0].equals(pt)) {
				driver.setAttribute(AdvancedDrivingSkill.DISTANCE_TO_GOAL, coords[1].distance(pt));
			} else {
				Coordinate cc = coords[1];
				double min = coords[0].distance(pt) + coords[1].distance(pt);
				for (int i = 1; i < coords.length - 2; i++) {
					final double dt = coords[i].distance(pt) + coords[i + 1].distance(pt);
					if (dt < min) {
						min = dt;
						cc = coords[i + 1];
					}
				}
				driver.setAttribute(AdvancedDrivingSkill.DISTANCE_TO_GOAL, cc.distance(pt));
			}

			driver.setAttribute(AdvancedDrivingSkill.CURRENT_ROAD, road);
			driver.setAttribute(AdvancedDrivingSkill.CURRENT_LANE, lane);
			driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX, indexSegment);
			// driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX, 0);
		}
	}

	@action (
			name = "unregister",
			args = { @arg (
					name = "agent",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the agent to unregister on the road.")) },
			doc = @doc (
					value = "unregister the agent on the road",
					examples = { @example ("do unregister agent: the_driver") }))
	public void primUnregister(final IScope scope) throws GamaRuntimeException {
		// final IAgent agent = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		// driver.setAttribute(AdvancedDrivingSkill.SEGMENT_INDEX, -1);
		final boolean agentOnLinkedRoad = (Boolean) driver.getAttribute(AdvancedDrivingSkill.ON_LINKED_ROAD);
		if (driver.hasAttribute("current_road") && driver.hasAttribute("current_lane")) {
			final IAgent cr = (IAgent) driver.getAttribute("current_road");
			final Integer pl = (Integer) driver.getAttribute("current_lane");
			Integer segmentIndex = (Integer) driver.getAttribute(AdvancedDrivingSkill.SEGMENT_INDEX);
			if (cr != null && pl != null) {
				if (agentOnLinkedRoad) {
					final IAgent lr = getLinkedRoad(cr);
					final List agsLane = (List) getAgentsOn(lr).get(pl);
					if (segmentIndex == null) {
						segmentIndex = getSegmentIndex(lr, driver);
					}
					((List) agsLane.get(agsLane.size() - 1 - segmentIndex)).remove(driver);
					getAgents(cr).remove(driver);
				} else {
					if (segmentIndex == null) {
						segmentIndex = getSegmentIndex(cr, driver);
					}
					((List) ((List) getAgentsOn(cr).get(pl)).get(segmentIndex)).remove(driver);
					getAgents(cr).remove(driver);
				}
			}
		}
	}

}
