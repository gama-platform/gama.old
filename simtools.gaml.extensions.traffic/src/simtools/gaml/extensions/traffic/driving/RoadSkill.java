/*******************************************************************************************************
 *
 * RoadSkill.java, in simtools.gaml.extensions.traffic, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.OrderedBidiMap;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Containers;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import simtools.gaml.extensions.traffic.driving.carfollowing.CustomDualTreeBidiMap;

/**
 * The Class RoadSkill.
 */
@vars ({ @variable (
		name = RoadSkill.AGENTS_ON,
		type = IType.LIST,
		of = IType.LIST,
		doc = @doc ("for each lane of the road, the list of agents for each segment")),
		@variable (
				name = RoadSkill.ALL_AGENTS,
				type = IType.LIST,
				of = IType.AGENT,
				doc = @doc ("the list of agents on the road")),
		@variable (
				name = RoadSkill.SOURCE_NODE,
				type = IType.AGENT,
				doc = @doc ("the source node of the road")),
		@variable (
				name = RoadSkill.TARGET_NODE,
				type = IType.AGENT,
				doc = @doc ("the target node of the road")),
		@variable (
				name = RoadSkill.NUM_LANES,
				type = IType.INT,
				init = "2",
				doc = @doc ("the number of lanes")),
		@variable (
				name = RoadSkill.NUM_SEGMENTS,
				type = IType.INT,
				doc = @doc ("the number of road segments")),
		@variable (
				name = RoadSkill.LINKED_ROAD,
				type = ITypeProvider.OWNER_TYPE,
				doc = @doc ("the linked road: the lanes of this linked road will be usable by drivers on the road")),
		@variable (
				name = RoadSkill.MAXSPEED,
				type = IType.FLOAT,
				init = "50#km/#h",
				doc = @doc ("the maximal speed on the road")),
		@variable (
				name = RoadSkill.SEGMENT_LENGTHS,
				type = IType.LIST,
				doc = @doc ("stores the length of each road segment. "
						+ "The index of each element corresponds to the segment index.")),
		@variable (
				name = RoadSkill.VEHICLE_ORDERING,
				type = IType.LIST,
				depends_on = { RoadSkill.NUM_LANES },
				doc = @doc ("provides information about the ordering of vehicle on any given lane")), })
@skill (
		name = RoadSkill.SKILL_ROAD,
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc (
				value = "A skill for agents representing roads in traffic simulations",
				deprecated = "please use the name `road` instead"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class RoadSkill extends Skill {

	/**
	 * The Class NewRoadSkill.
	 */
	@skill (
			name = "road_skill",
			concept = { IConcept.TRANSPORT, IConcept.SKILL },
			doc = @doc ("A skill for agents representing roads in traffic simulations"))
	public static class NewRoadSkill extends RoadSkill {}

	/** The Constant SKILL_ROAD. */
	public static final String SKILL_ROAD = "skill_road";

	/** The Constant ALL_AGENTS. */
	// TODO: rename these two lists?
	public static final String ALL_AGENTS = "all_agents";

	/** The Constant AGENTS_ON. */
	public static final String AGENTS_ON = "agents_on";

	/** The Constant SOURCE_NODE. */
	public static final String SOURCE_NODE = "source_node";

	/** The Constant TARGET_NODE. */
	public static final String TARGET_NODE = "target_node";

	/** The Constant MAXSPEED. */
	// TODO: rename to speed_limit
	public static final String MAXSPEED = "maxspeed";

	/** The Constant LINKED_ROAD. */
	public static final String LINKED_ROAD = "linked_road";

	/** The Constant LANES. */
	@Deprecated public static final String LANES = "lanes";

	/** The Constant NUM_LANES. */
	public static final String NUM_LANES = "num_lanes";

	/** The Constant NUM_SEGMENTS. */
	public static final String NUM_SEGMENTS = "num_segments";

	/** The Constant SEGMENT_LENGTHS. */
	public static final String SEGMENT_LENGTHS = "segment_lengths";

	/** The Constant VEHICLE_ORDERING. */
	public static final String VEHICLE_ORDERING = "vehicle_ordering";

	/**
	 * Gets the agents on.
	 *
	 * @param agent
	 *            the agent
	 * @return the agents on
	 */
	@getter (AGENTS_ON)
	public static List getAgentsOn(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS_ON);
	}

	/**
	 * Sets the agents on.
	 *
	 * @param agent
	 *            the agent
	 * @param agents
	 *            the agents
	 */
	@setter (AGENTS_ON)
	public static void setAgentsOn(final IAgent agent, final List agents) {
		agent.setAttribute(AGENTS_ON, agents);
	}

	/**
	 * Gets the agents.
	 *
	 * @param agent
	 *            the agent
	 * @return the agents
	 */
	@getter (ALL_AGENTS)
	public static IList<IAgent> getAgents(final IAgent agent) {
		IList<IAgent> res = GamaListFactory.create(Types.AGENT);
		for (OrderedBidiMap<IAgent, Double> map : getVehicleOrdering(agent)) { res.addAll(map.keySet()); }
		return Containers.remove_duplicates(GAMA.getRuntimeScope(), res);
	}

	/**
	 * Sets the agents.
	 *
	 * @param agent
	 *            the agent
	 * @param agents
	 *            the agents
	 */
	@setter (ALL_AGENTS)
	public static void setAgents(final IAgent agent, final List agents) {
		// read-only
	}

	/**
	 * Gets the source node.
	 *
	 * @param agent
	 *            the agent
	 * @return the source node
	 */
	@getter (SOURCE_NODE)
	public static IAgent getSourceNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(SOURCE_NODE);
	}

	/**
	 * Sets the source node.
	 *
	 * @param agent
	 *            the agent
	 * @param nd
	 *            the nd
	 */
	@setter (SOURCE_NODE)
	public static void setSourceNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(SOURCE_NODE, nd);
	}

	/**
	 * Gets the target node.
	 *
	 * @param agent
	 *            the agent
	 * @return the target node
	 */
	@getter (TARGET_NODE)
	public static IAgent getTargetNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(TARGET_NODE);
	}

	/**
	 * Sets the target node.
	 *
	 * @param agent
	 *            the agent
	 * @param nd
	 *            the nd
	 */
	@setter (TARGET_NODE)
	public static void setTargetNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(TARGET_NODE, nd);
	}

	/**
	 * Gets the num lanes.
	 *
	 * @param agent
	 *            the agent
	 * @return the num lanes
	 */
	@getter (NUM_LANES)
	public static int getNumLanes(final IAgent agent) {
		return (int) agent.getAttribute(NUM_LANES);
	}

	/**
	 * Sets the num lanes.
	 *
	 * @param agent
	 *            the agent
	 * @param numLanes
	 *            the num lanes
	 */
	@setter (NUM_LANES)
	public static void setNumLanes(final IAgent agent, final int numLanes) {
		// TODO: this check should be put somewhere else
		// if (numLanes == 0) {
		// throw GamaRuntimeException.error(agent.getName() + " has zero lanes",
		// GAMA.getRuntimeScope());
		// }
		agent.setAttribute(NUM_LANES, numLanes);
	}

	/**
	 * Gets the num lanes total.
	 *
	 * @param road
	 *            the road
	 * @return the num lanes total
	 */
	public static int getNumLanesTotal(final IAgent road) {
		IAgent linkedRoad = getLinkedRoad(road);
		int numLanesLinked = linkedRoad != null ? getNumLanes(linkedRoad) : 0;
		return getNumLanes(road) + numLanesLinked;
	}

	/**
	 * Gets the max speed.
	 *
	 * @param agent
	 *            the agent
	 * @return the max speed
	 */
	@getter (MAXSPEED)
	public static Double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAXSPEED);
	}

	/**
	 * Sets the max speed.
	 *
	 * @param agent
	 *            the agent
	 * @param sp
	 *            the sp
	 */
	@setter (MAXSPEED)
	public static void setMaxSpeed(final IAgent agent, final Double sp) {
		agent.setAttribute(MAXSPEED, sp);
	}

	/**
	 * Gets the linked road.
	 *
	 * @param agent
	 *            the agent
	 * @return the linked road
	 */
	@getter (LINKED_ROAD)
	public static IAgent getLinkedRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(LINKED_ROAD);
	}

	/**
	 * Sets the linked road.
	 *
	 * @param agent
	 *            the agent
	 * @param rd
	 *            the rd
	 */
	@setter (LINKED_ROAD)
	public static void setLinkedRoad(final IAgent agent, final IAgent rd) {
		agent.setAttribute(LINKED_ROAD, rd);
	}

	/**
	 * Gets the num segments.
	 *
	 * @param road
	 *            the road
	 * @return the num segments
	 */
	@getter (NUM_SEGMENTS)
	public static int getNumSegments(final IAgent road) {
		return GeometryUtils.getPointsOf(road).length - 1;
	}

	/**
	 * Sets the num segments.
	 *
	 * @param road
	 *            the road
	 * @param numSegments
	 *            the num segments
	 */
	@setter (NUM_SEGMENTS)
	public static void setNumSegments(final IAgent road, final int numSegments) {
		// read-only
	}

	/**
	 * Gets the segment lengths.
	 *
	 * @param road
	 *            the road
	 * @return the segment lengths
	 */
	@getter (
			value = SEGMENT_LENGTHS)
	public static List<Double> getSegmentLengths(final IAgent road) {
		List<Double> res = (List<Double>) road.getAttribute(SEGMENT_LENGTHS);
		if (res.isEmpty()) {
			Geometry geom = road.getInnerGeometry();
			if (road.getInnerGeometry() == null) throw GamaRuntimeException
					.error("The shape of the road has not been initialized", GAMA.getRuntimeScope());
			Coordinate[] coords = geom.getCoordinates();
			for (int i = 0; i < coords.length - 1; i += 1) { res.add(coords[i].distance(coords[i + 1])); }
		}
		return res;
	}

	/**
	 * Gets the total length.
	 *
	 * @param road
	 *            the road
	 * @return the total length
	 */
	public static double getTotalLength(final IAgent road) {
		List<Double> lengths = getSegmentLengths(road);
		return lengths.stream().reduce(0.0, Double::sum);
	}

	/**
	 * Gets the vehicle ordering.
	 *
	 * @param road
	 *            the road
	 * @return the vehicle ordering
	 */
	@getter (VEHICLE_ORDERING)
	public static List<OrderedBidiMap<IAgent, Double>> getVehicleOrdering(final IAgent road) {
		List<OrderedBidiMap<IAgent, Double>> res =
				(List<OrderedBidiMap<IAgent, Double>>) road.getAttribute(VEHICLE_ORDERING);
		if (res.isEmpty()) {
			for (int i = 0; i < getNumLanes(road); i += 1) {
				res.add(new CustomDualTreeBidiMap<IAgent, Double>((a, b) -> {
					int r = a.getSpeciesName().compareTo(b.getSpeciesName());
					if (r != 0) return r;
					return Integer.compare(a.getIndex(), b.getIndex());
				}, Collections.reverseOrder()));
			}
		}
		return res;
	}

	/**
	 * Sets the vehicle ordering.
	 *
	 * @param road
	 *            the road
	 * @param list
	 *            the list
	 */
	@setter (VEHICLE_ORDERING)
	public static void setVehicleOrdering(final IAgent road, final List<OrderedBidiMap<IAgent, Double>> list) {
		road.setAttribute(VEHICLE_ORDERING, list);
	}

	/**
	 * Helper method that allows access to the ordered maps of vehicles' longitudinal positions
	 *
	 * @param scope
	 * @param correctRoad
	 *            the road where the vehicle is supposed to be
	 * @param lane
	 *            the lane index
	 * @return the ordered tree map that corresponds to a lane on a certain road
	 */
	public static OrderedBidiMap<IAgent, Double> getVehicleOrderingMap(final IScope scope, final IAgent correctRoad,
			final int lane) {
		int numLanesTotal = getNumLanesTotal(correctRoad);
		if (lane >= numLanesTotal) {
			String msg = String.format("Trying to access lane with index %d on road %s, which only have %d valid lanes",
					lane, correctRoad.getName(), numLanesTotal);
			throw GamaRuntimeException.error(msg, scope);
		}

		int numLanesCorrect = getNumLanes(correctRoad);
		IAgent actualRoad;
		int actualLane;
		// Convert the "overflowed" lane to the correct lane index on the linked road.
		if (lane < numLanesCorrect) {
			actualRoad = correctRoad;
			actualLane = lane;
		} else {
			IAgent linkedRoad = getLinkedRoad(correctRoad);
			int numLanesLinked = getNumLanes(linkedRoad);
			actualRoad = linkedRoad;
			actualLane = numLanesCorrect + numLanesLinked - 1 - lane;
		}

		return getVehicleOrdering(actualRoad).get(actualLane);
	}

	/**
	 * Prim register.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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
					examples = { @example ("do register agent: the_driver lane: 0;") }))
	public boolean primRegister(final IScope scope) throws GamaRuntimeException {
		final IAgent road = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		int lane = scope.getIntArg("lane");

		return register(scope, driver, road, lane);
	}

	/**
	 * Registers the driver on the specified road and starting lane.
	 *
	 * @param scope
	 * @param vehicle
	 *            the agent to register
	 * @param road
	 *            the new road
	 * @param lowestLane
	 *            the new starting lane
	 *
	 * @throws GamaRuntimeException
	 */
	public static boolean register(final IScope scope, final IAgent vehicle, final IAgent road, final int lowestLane)
			throws GamaRuntimeException {
		if (vehicle == null) return false;

		int numLanesOccupied = DrivingSkill.getNumLanesOccupied(vehicle);
		int numSegments = getNumSegments(road);
		List<Double> lengths = getSegmentLengths(road);
		GamaPoint roadEndPt = getTargetNode(road).getLocation();
		boolean violatingOneway = !DrivingSkill.getCurrentTarget(vehicle).getLocation().equals(roadEndPt);
		int segmentIdx = !violatingOneway ? 0 : getNumSegments(road) - 1;
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			getVehicleOrderingMap(scope, road, lane).put(vehicle, getTotalLength(road));
		}

		DrivingSkill.setViolatingOneway(vehicle, violatingOneway);
		if (!violatingOneway) {
			DrivingSkill.setDistanceToGoal(vehicle, lengths.get(0));
		} else {
			DrivingSkill.setDistanceToGoal(vehicle, lengths.get(numSegments - 1));
		}
		DrivingSkill.setDistanceToCurrentTarget(vehicle, getTotalLength(road));
		DrivingSkill.setCurrentRoad(vehicle, road);
		DrivingSkill.setLowestLane(vehicle, lowestLane);
		DrivingSkill.setSegmentIndex(vehicle, segmentIdx);
		return true;
	}

	/**
	 * Prim unregister.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "unregister",
			args = { @arg (
					name = "agent",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the agent to unregister on the road.")) },
			doc = @doc (
					value = "unregister the agent on the road",
					examples = { @example ("do unregister agent: the_driver;") },
					deprecated = "use the `unregister` action in advanced_driving skill instead"))
	@Deprecated
	public boolean primUnregister(final IScope scope) throws GamaRuntimeException {
		IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		return DrivingSkill.unregister(scope, driver);
	}
}
