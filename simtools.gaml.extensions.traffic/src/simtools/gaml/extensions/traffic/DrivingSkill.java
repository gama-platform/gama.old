/*******************************************************************************************************
 *
 * simtools.gaml.extensions.traffic.AdvancedDrivingSkill.java, in plugin simtools.gaml.extensions.traffic, is part of
 * the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

import ummisco.gama.dev.utils.DEBUG;

@vars ({ @variable (
		name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc ("the speed of the agent (in meter/second)")),
		@variable (
				name = IKeyword.REAL_SPEED,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the actual speed of the agent (in meter/second)")),
		@variable (
				name = "current_path",
				type = IType.PATH,
				init = "nil",
				doc = @doc ("the current path that tha agent follow")),
		@variable (
				name = "final_target",
				type = IType.POINT,
				init = "nil",
				doc = @doc ("the final target of the agent")),
		@variable (
				name = "current_target",
				type = IType.POINT,
				init = "nil",
				doc = @doc ("the current target of the agent")),
		@variable (
				name = "current_index",
				type = IType.INT,
				init = "0",
				doc = @doc ("the index of the current edge (road) in the path")),
		@variable (
				name = "targets",
				type = IType.LIST,
				of = IType.POINT,
				init = "[]",
				doc = @doc ("the current list of points that the agent has to reach (path)")),
		@variable (
				name = "security_distance_coeff",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc (
						deprecated = "use safety_distance_coeff instead",
						value = "the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - safety_distance =max(min_safety_distance, safety_distance_coeff `*` min(self.real_speed, other.real_speed) )")),
		@variable (
				name = "safety_distance_coeff",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - security_distance =max(min_security_distance, security_distance_coeff `*` min(self.real_speed, other.real_speed) )")),
		@variable (
				name = "min_security_distance",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc (
						deprecated = "use min_safety_distance instead",
						value = "the minimal distance to another driver")),
		@variable (
				name = "min_safety_distance",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("the minimal distance to another driver")),
		@variable (
				name = "current_lane",
				type = IType.INT,
				init = "0",
				doc = @doc ("the current lane on which the agent is")),
		@variable (
				name = DrivingSkill.NUM_LANES_OCCUPIED,
				type = IType.INT,
				init = "1",
				doc = @doc ("the number of lanes that the vehicle occupies")),
		@variable (
				name = "vehicle_length",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the length of the vehicle (in meters)")),
		@variable (
				name = "speed_coeff",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("speed coefficient for the speed that the driver want to reach (according to the max speed of the road)")),
		@variable (
				name = "max_acceleration",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("maximum acceleration of the car for a cycle")),
		@variable (
				name = "current_road",
				type = IType.AGENT,
				doc = @doc ("current road on which the agent is")),
		@variable (
				name = "on_linked_road",
				type = IType.BOOL,
				init = "false",
				doc = @doc (
						deprecated = "use using_linked_road instead",
						value = "is the agent on the linked road?")),
		@variable (
				name = "using_linked_road",
				type = IType.BOOL,
				init = "false",
				doc = @doc ("indicates if the driver is occupying at least one lane on the linked road")),
		@variable (
				name = "linked_lane_limit",
				type = IType.INT,
				init = "-1",
				doc = @doc ("the maximum number of linked lanes that the driver can use")),
		@variable (
				name = "proba_lane_change_up",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to change lane to a upper lane (left lane if right side driving) if necessary")),
		@variable (
				name = "proba_lane_change_down",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to change lane to a lower lane (right lane if right side driving) if necessary")),
		@variable (
				name = "proba_respect_priorities",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to respect priority (right or left) laws")),
		@variable (
				name = "proba_respect_stops",
				type = IType.LIST,
				of = IType.FLOAT,
				init = "[]",
				doc = @doc ("probability to respect stop laws - one value for each type of stop")),
		@variable (
				name = "proba_block_node",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("probability to block a node (do not let other driver cross the crossroad)")),
		@variable (
				name = "proba_use_linked_road",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("probability to change lane to a linked road lane if necessary")),
		@variable (
				name = "right_side_driving",
				type = IType.BOOL,
				init = "true",
				doc = @doc ("are drivers driving on the right size of the road?")),
		@variable (
				name = "max_speed",
				type = IType.FLOAT,
				init = "50.0",
				doc = @doc ("maximal speed of the vehicle")),
		@variable (
				name = "distance_to_goal",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("euclidean distance to the next point of the current segment")),
		@variable (
				name = "segment_index_on_road",
				type = IType.INT,
				init = "-1",
				doc = @doc ("current segment index of the agent on the current road ")), })
@skill (
		name = "advanced_driving",
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill that provides driving primitives and operators"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrivingSkill extends MovingSkill {

	static {
		DEBUG.ON();
	}

	@Deprecated public final static String SECURITY_DISTANCE_COEFF = "security_distance_coeff";
	public final static String SAFETY_DISTANCE_COEFF = "safety_distance_coeff";
	@Deprecated public final static String MIN_SECURITY_DISTANCE = "min_security_distance";
	public final static String MIN_SAFETY_DISTANCE = "min_safety_distance";

	public final static String CURRENT_ROAD = "current_road";
	public final static String CURRENT_LANE = "current_lane";
	public final static String DISTANCE_TO_GOAL = "distance_to_goal";
	public final static String VEHICLE_LENGTH = "vehicle_length";
	public final static String PROBA_LANE_CHANGE_UP = "proba_lane_change_up";
	public final static String PROBA_LANE_CHANGE_DOWN = "proba_lane_change_down";
	public final static String PROBA_RESPECT_PRIORITIES = "proba_respect_priorities";
	public final static String PROBA_RESPECT_STOPS = "proba_respect_stops";
	public final static String PROBA_BLOCK_NODE = "proba_block_node";
	public final static String PROBA_USE_LINKED_ROAD = "proba_use_linked_road";
	public final static String RIGHT_SIDE_DRIVING = "right_side_driving";
	public final static String ON_LINKED_ROAD = "on_linked_road";
	public final static String USING_LINKED_ROAD = "using_linked_road";
	public final static String LINKED_LANE_LIMIT = "linked_lane_limit";
	public final static String TARGETS = "targets";
	public final static String CURRENT_TARGET = "current_target";
	public final static String CURRENT_INDEX = "current_index";
	public final static String FINAL_TARGET = "final_target";
	public final static String CURRENT_PATH = "current_path";
	public final static String ACCELERATION_MAX = "max_acceleration";
	public final static String SPEED_COEFF = "speed_coeff";
	public final static String MAX_SPEED = "max_speed";
	public final static String SEGMENT_INDEX = "segment_index_on_road";
	public final static String NUM_LANES_OCCUPIED = "num_lanes_occupied";

	@getter (ACCELERATION_MAX)
	public static double getAccelerationMax(final IAgent agent) {
		return (Double) agent.getAttribute(ACCELERATION_MAX);
	}

	@setter (ACCELERATION_MAX)
	public static void setAccelerationMax(final IAgent agent, final Double val) {
		agent.setAttribute(ACCELERATION_MAX, val);
	}

	@getter (SPEED_COEFF)
	public static double getSpeedCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SPEED_COEFF);
	}

	@setter (SPEED_COEFF)
	public static void setSpeedCoeff(final IAgent agent, final Double val) {
		agent.setAttribute(SPEED_COEFF, val);
	}

	@getter (MAX_SPEED)
	public static double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAX_SPEED);
	}

	@setter (MAX_SPEED)
	public static void setMaxSpeed(final IAgent agent, final Double val) {
		agent.setAttribute(MAX_SPEED, val);
	}

	@getter (CURRENT_TARGET)
	public static GamaPoint getCurrentTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(CURRENT_TARGET);
	}

	@setter (CURRENT_TARGET)
	public static void setCurrentTarget(final IAgent agent, final ILocation point) {
		agent.setAttribute(CURRENT_TARGET, point);
	}

	@getter (FINAL_TARGET)
	public static GamaPoint getFinalTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(FINAL_TARGET);
	}

	@setter (FINAL_TARGET)
	public static void setFinalTarget(final IAgent agent, final ILocation point) {
		agent.setAttribute(FINAL_TARGET, point);
	}

	@getter (CURRENT_INDEX)
	public static Integer getCurrentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_INDEX);
	}

	@setter (CURRENT_INDEX)
	public static void setCurrentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(CURRENT_INDEX, index);
	}

	@getter (SEGMENT_INDEX)
	public static Integer getSegmentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(SEGMENT_INDEX);
	}

	@setter (SEGMENT_INDEX)
	public static void setSegmentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(SEGMENT_INDEX, index);
	}

	@Override
	@getter (CURRENT_PATH)
	public IPath getCurrentPath(final IAgent agent) {
		return (IPath) agent.getAttribute(CURRENT_PATH);
	}

	@Override
	@setter (CURRENT_PATH)
	public void setCurrentPath(final IAgent agent, final IPath path) {
		agent.setAttribute(CURRENT_PATH, path);
	}

	@getter (TARGETS)
	public static List<ILocation> getTargets(final IAgent agent) {
		return (List<ILocation>) agent.getAttribute(TARGETS);
	}

	@setter (TARGETS)
	public static void setTargets(final IAgent agent, final List<ILocation> points) {
		agent.setAttribute(TARGETS, points);
	}

	@getter (PROBA_USE_LINKED_ROAD)
	public static double getProbaUseLinkedRoad(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_USE_LINKED_ROAD);
	}

	@setter (PROBA_USE_LINKED_ROAD)
	public static void setProbaUseLinkedRoad(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_USE_LINKED_ROAD, proba);
	}

	@getter (PROBA_LANE_CHANGE_DOWN)
	public static double getProbaLaneChangeDown(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	@setter (PROBA_LANE_CHANGE_DOWN)
	public static void setProbaLaneChangeDown(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	@getter (PROBA_LANE_CHANGE_UP)
	public static double getProbaLaneChangeUp(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	@setter (PROBA_LANE_CHANGE_UP)
	public static void setProbaLaneChangeUp(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_UP, proba);
	}

	@getter (PROBA_RESPECT_PRIORITIES)
	public static double getRespectPriorities(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_RESPECT_PRIORITIES);
	}

	@setter (PROBA_RESPECT_PRIORITIES)
	public static void setRespectPriorities(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_RESPECT_PRIORITIES, proba);
	}

	@getter (PROBA_BLOCK_NODE)
	public static double getProbaBlockNode(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_BLOCK_NODE);
	}

	@setter (PROBA_BLOCK_NODE)
	public static void setProbaBlockNode(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_BLOCK_NODE, proba);
	}

	@getter (PROBA_RESPECT_STOPS)
	public static List<Double> getRespectStops(final IAgent agent) {
		return (List<Double>) agent.getAttribute(PROBA_RESPECT_STOPS);
	}

	@setter (PROBA_RESPECT_STOPS)
	public static void setRespectStops(final IAgent agent, final List<Boolean> probas) {
		agent.setAttribute(PROBA_RESPECT_STOPS, probas);
	}

	@Deprecated
	@getter (ON_LINKED_ROAD)
	public static boolean getOnLinkedRoad(final IAgent agent) {
		return isUsingLinkedRoad(agent);
	}

	@Deprecated
	@setter (ON_LINKED_ROAD)
	public static void setOnLinkedRoad(final IAgent agent, final Boolean onLinkedRoad) {
		// read-only
	}

	@getter (USING_LINKED_ROAD)
	public static boolean isUsingLinkedRoad(IAgent driver) {
		IAgent currentRoad = getCurrentRoad(driver);
		if (currentRoad == null) return false;

		int currentStartingLane = getCurrentLane(driver);
		int numLanesCurrent = RoadSkill.getLanes(currentRoad);
		int numLanesOccupied = getNumLanesOccupied(driver);
		return currentStartingLane > numLanesCurrent - numLanesOccupied;
	}

	@setter (USING_LINKED_ROAD)
	public static void setUsingLinkedRoad(IAgent driver, boolean usingLinkedRoad) {
		// read-only
	}

	@getter (LINKED_LANE_LIMIT)
	public static int getLinkedLaneLimit(IAgent driver) {
		return (int) driver.getAttribute(LINKED_LANE_LIMIT);
	}

	@setter (LINKED_LANE_LIMIT)
	public static void setLinkedLaneLimit(IAgent driver, int linkedLaneLimit) {
		driver.setAttribute(LINKED_LANE_LIMIT, linkedLaneLimit);
	}

	@getter (RIGHT_SIDE_DRIVING)
	public static boolean getRightSideDriving(final IAgent agent) {
		return (Boolean) agent.getAttribute(RIGHT_SIDE_DRIVING);
	}

	@setter (RIGHT_SIDE_DRIVING)
	public static void setRightSideDriving(final IAgent agent, final Boolean isRight) {
		agent.setAttribute(RIGHT_SIDE_DRIVING, isRight);
	}

	@Deprecated
	@getter (SECURITY_DISTANCE_COEFF)
	public static double getSecurityDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SECURITY_DISTANCE_COEFF);
	}

	@Deprecated
	@setter (SECURITY_DISTANCE_COEFF)
	public static void setSecurityDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SECURITY_DISTANCE_COEFF, ls);
	}

	@getter (SAFETY_DISTANCE_COEFF)
	public static double getSafetyDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SAFETY_DISTANCE_COEFF);
	}

	@setter (SAFETY_DISTANCE_COEFF)
	public static void setSafetyDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SAFETY_DISTANCE_COEFF, ls);
	}

	@getter (CURRENT_ROAD)
	public static IAgent getCurrentRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(CURRENT_ROAD);
	}

	@getter (VEHICLE_LENGTH)
	public static double getVehicleLength(final IAgent agent) {
		return (Double) agent.getAttribute(VEHICLE_LENGTH);
	}

	@getter (CURRENT_LANE)
	public static int getCurrentLane(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_LANE);
	}

	@getter (DISTANCE_TO_GOAL)
	public static double getDistanceToGoal(final IAgent agent) {
		return (Double) agent.getAttribute(DISTANCE_TO_GOAL);
	}

	@getter (MIN_SECURITY_DISTANCE)
	public static double getMinSecurityDistance(final IAgent agent) {
		return (Double) agent.getAttribute(MIN_SECURITY_DISTANCE);
	}

	@setter (MIN_SECURITY_DISTANCE)
	public static void setMinSecDistance(final IAgent agent, final double msd) {
		agent.setAttribute(MIN_SECURITY_DISTANCE, msd);
	}

	@getter (MIN_SAFETY_DISTANCE)
	public static double getMinSafetyDistance(final IAgent agent) {
		return (Double) agent.getAttribute(MIN_SAFETY_DISTANCE);
	}

	@setter (DISTANCE_TO_GOAL)
	public static void setDistanceToGoal(final IAgent agent, final double dg) {
		agent.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	@getter (NUM_LANES_OCCUPIED)
	public static Integer getNumLanesOccupied(IAgent agent) {
		return (Integer) agent.getAttribute(NUM_LANES_OCCUPIED);
	}

	@setter (NUM_LANES_OCCUPIED)
	public static void setNumLanesOccupied(IAgent agent, Integer value) {
		agent.setAttribute(NUM_LANES_OCCUPIED, value);
	}

	public Double primAdvancedFollow(final IScope scope, final IAgent driver, final double s, final double t,
			final IPath path, final GamaPoint target) throws GamaRuntimeException {

		final double safety_distance_coeff = driver.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(driver)
				: getSecurityDistanceCoeff(driver);
		final int currentLane = getCurrentLane(driver);
		final Double probaChangeLaneUp = getProbaLaneChangeUp(driver);
		final Double probaChangeLaneDown = getProbaLaneChangeDown(driver);
		final Double probaProbaUseLinkedRoad = getProbaUseLinkedRoad(driver);
		final Boolean rightSide = getRightSideDriving(driver);
		final IAgent currentRoad = getCurrentRoad(driver);
		final IAgent linkedRoad = (IAgent) currentRoad.getAttribute(RoadSkill.LINKED_ROAD);
		// final boolean onLinkedRoad = getOnLinkedRoad(agent);
		final double maxDist = s * t;

		if (maxDist == 0) { return 0.0; }
		double tps = 0;
		tps = t * moveToNextLocAlongPathOSM(scope, driver, path, target, maxDist, safety_distance_coeff, currentLane,
				currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown, probaProbaUseLinkedRoad, rightSide);

		if (tps < t) {
			driver.setAttribute(IKeyword.REAL_SPEED, this.getRealSpeed(driver) / (t - tps));
		} else {
			driver.setAttribute(IKeyword.REAL_SPEED, 0.0);
		}

		return tps;
	}

	@action (
			name = "advanced_follow_driving",
			args = { @arg (
					name = "path",
					type = IType.PATH,
					optional = false,
					doc = @doc ("a path to be followed.")),
					@arg (
							name = "target",
							type = IType.POINT,
							optional = true,
							doc = @doc ("the target to reach")),
					@arg (
							name = IKeyword.SPEED,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("the speed to use for this move (replaces the current value of speed)")),
					@arg (
							name = "time",
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("time to travel")) },
			doc = @doc (
					value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)",
					returns = "the remaining time",
					examples = { @example ("do osm_follow path: the_path on: road_network;") }))
	public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		final Double t = scope.hasArg("time") ? scope.getFloatArg("time") : scope.getClock().getStepInSeconds();
		final GamaPoint target = scope.hasArg("target") ? (GamaPoint) scope.getArg("target", IType.NONE) : null;
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		return primAdvancedFollow(scope, agent, s, t, path, target);
	}

	@action (
			name = "is_ready_next_road",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road to test")),
					@arg (
							name = "lane",
							type = IType.INT,
							optional = false,
							doc = @doc ("the lane to test")) },
			doc = @doc (
					value = "action to test if the driver can take the given road at the given lane",
					returns = "true (the driver can take the road) or false (the driver cannot take the road)",
					examples = { @example ("do is_ready_next_road new_road: a_road lane: 0;") }))
	public Boolean primIsReadyNextRoad(final IScope scope) throws GamaRuntimeException {
		final IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		final Integer lane = (Integer) scope.getArg("lane", IType.INT);
		final IAgent driver = getCurrentAgent(scope);
		double vehicleLength = getVehicleLength(driver);
		int numLanesOccupied = getNumLanesOccupied(driver);
		final double probaBlock = getProbaBlockNode(driver);
		final boolean testBlockNode = Random.opFlip(scope, probaBlock);
		final IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		final Map<IAgent, List<IAgent>> block = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		final List<IAgent> ba = GamaListFactory.create(scope, Types.AGENT, block.keySet());
		for (final IAgent dr : ba) {
			if (!dr.getLocation().equals(node.getLocation())) {
				block.remove(dr);
			}
		}
		return isReadyNextRoad(scope, road)
				&& (testBlockNode || DrivingOperators.enoughSpaceToEnterRoad(scope, road, lane, numLanesOccupied, vehicleLength / 2));
	}

	@action (
			name = "test_next_road",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road to test")) },
			doc = @doc (
					value = "action to test if the driver can take the given road",
					returns = "true (the driver can take the road) or false (the driver cannot take the road)",
					examples = { @example ("do test_next_road new_road: a_road;") }))
	public Boolean primTestNextRoad(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	/**
	 * Check if the driver is ready to cross the intersection to get to a new road, concerning:
	 *     1. Traffic lights
	 *     2. Other drivers coming from other incoming roads
	 *
	 * @param scope
	 * @param newRoad
	 * @return true if ready, false otherwise
	 *
	 * @throws GamaRuntimeException
	 */
	public Boolean isReadyNextRoad(IScope scope, IAgent newRoad) throws GamaRuntimeException {
		IAgent sourceNode = (IAgent) newRoad.getAttribute(RoadSkill.SOURCE_NODE);
		Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>)
			sourceNode.getAttribute(RoadNodeSkill.BLOCK);

		IAgent driver = getCurrentAgent(scope);
		// additional conditions to cross the intersection, defined by the user
		ISpecies context = driver.getSpecies();
		IStatement.WithArgs actionTNR = context.getAction("test_next_road");
		Arguments argsTNR = new Arguments();
		argsTNR.put("new_road", ConstantExpressionDescription.create(newRoad));
		actionTNR.setRuntimeArgs(scope, argsTNR);
		if (!(Boolean) actionTNR.executeOn(scope)) { return false; }

		List<List> stops = (List<List>) sourceNode.getAttribute(RoadNodeSkill.STOP);
		List<Double> respectsStops = getRespectStops(driver);

		IAgent currentRoad = (IAgent) driver.getAttribute(CURRENT_ROAD);
		// TODO: why would current road be null?
		if (currentRoad == null) { return true; }

		for (int i = 0; i < stops.size(); i++) {
			Boolean stop = stops.get(i).contains(currentRoad);
			if (stop && (respectsStops.size() <= i || Random.opFlip(scope, respectsStops.get(i)))) { return false; }
		}

		// check if current road is blocked by any driver
		for (List<IAgent> blockedRoads : blockInfo.values()) {
			if (blockedRoads.contains(currentRoad)) { return false; }
		}

		Boolean rightSide = getRightSideDriving(driver);
		List<IAgent> priorityRoads = (List<IAgent>) sourceNode.getAttribute(RoadNodeSkill.PRIORITY_ROADS);
		boolean onPriorityRoad = priorityRoads != null && priorityRoads.contains(currentRoad);

		// compute angle between the current & next road
		double angleRef = Punctal.angleInDegreesBetween(scope, (GamaPoint) sourceNode.getLocation(),
				(GamaPoint) currentRoad.getLocation(), (GamaPoint) newRoad.getLocation());
		List<IAgent> roadsIn = (List) sourceNode.getAttribute(RoadNodeSkill.ROADS_IN);
		if (!Random.opFlip(scope, getRespectPriorities(driver))) { return true; }
		double realSpeed = Math.max(0.5, getRealSpeed(driver) + getAccelerationMax(driver));
		double safetyDistCoeff = driver.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(driver)
				: getSecurityDistanceCoeff(driver);
		double vehicleLength = getVehicleLength(driver);

		for (IAgent otherInRoad : roadsIn) {
			if (otherInRoad == currentRoad) {
				continue;
			}
			double angle = Punctal.angleInDegreesBetween(scope, (GamaPoint) sourceNode.getLocation(),
					(GamaPoint) currentRoad.getLocation(), (GamaPoint) otherInRoad.getLocation());
			boolean otherRoadIsPriortized = priorityRoads != null && priorityRoads.contains(otherInRoad);
			boolean hasPriority = onPriorityRoad && !otherRoadIsPriortized;
			boolean shouldRespectPriority = !onPriorityRoad && otherRoadIsPriortized;
			// be careful of vehicles coming from the right/left side
			if (!hasPriority
					&& (shouldRespectPriority || rightSide && angle > angleRef || !rightSide && angle < angleRef)) {
				List<IAgent> otherDrivers = (List) otherInRoad.getAttribute(RoadSkill.AGENTS);
				for (IAgent otherDriver : otherDrivers) {
					double otherVehicleLength = getVehicleLength(otherDriver);
					double otherRealSpeed = getRealSpeed(otherDriver);
					double dist = otherDriver.euclidianDistanceTo(driver);
					
					if (Maths.round(getRealSpeed(otherDriver), 1) > 0.0 &&
							0.5 + safetyDistCoeff * Math.max(0, realSpeed - otherRealSpeed) > 
							dist - (vehicleLength / 2 + otherVehicleLength / 2)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@action (
			name = "compute_path",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph on wich compute the path")),
					@arg (
							name = "target",
							type = IType.AGENT,
							optional = false,
							doc = @doc ("the target node to reach")),
					@arg (
							name = "source",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("the source node (optional, if not defined, closest node to the agent location)")),
					@arg (
							name = "on_road",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("the road on which the agent is located (optional)")) },
			doc = @doc (
					value = "action to compute a path to a target location according to a given graph",
					returns = "the computed path, return nil if no path can be taken",
					examples = { @example ("do compute_path graph: road_network target: the_node;") }))
	public IPath primComputePath(final IScope scope) throws GamaRuntimeException {
		final ISpatialGraph graph = (ISpatialGraph) scope.getArg("graph", IType.GRAPH);
		final IAgent target = (IAgent) scope.getArg("target", IType.AGENT);
		final IAgent agent = getCurrentAgent(scope);
		IAgent source = (IAgent) scope.getArg("source", IType.AGENT);
		IAgent onRoad = (IAgent) scope.getArg("on_road", IType.AGENT);
		if (source == null) {
			if (onRoad != null) {
				source = RoadSkill.getTargetNode(onRoad);
			} else {
				source = (IAgent) Queries.closest_to(scope, target.getSpecies(), agent);
			}
		}
		if (source.getLocation().equals(agent.getLocation())) {
			onRoad = null;
		}

		final IPath path = ((GraphTopology) graph.getTopology(scope)).pathBetween(scope, source, target, onRoad);
		if (path != null && !path.getEdgeGeometry().isEmpty()) {
			final List<ILocation> targets = getTargets(agent);
			targets.clear();
			for (int i = 0; i < path.getEdgeGeometry().size(); i += 1) {
				IShape egGeom = (IShape) path.getEdgeGeometry().get(i);
				final Coordinate[] coords = egGeom.getInnerGeometry().getCoordinates();
				if (i == 0) {
					targets.add(new GamaPoint(coords[0]));
				}
				final GamaPoint pt = new GamaPoint(coords[coords.length - 1]);
				targets.add(pt);
			}
			setTargets(agent, targets);
			setCurrentIndex(agent, -1);
			setCurrentTarget(agent, targets.get(0));
			setFinalTarget(agent, target.getLocation());
			setCurrentPath(agent, path);
		
			return path;
		}
		setTargets(agent, GamaListFactory.<ILocation> create(Types.POINT));
		setCurrentIndex(agent, -1);
		setCurrentTarget(agent, null);
		setFinalTarget(agent, null);
		setCurrentPath(agent, (IPath) null);
		return null;
	}

	@action (
			name = "path_from_nodes",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph on wich compute the path")),
					@arg (
							name = "nodes",
							type = IType.LIST,
							optional = false,
							doc = @doc ("the list of nodes composing the path")) },
			doc = @doc (
					value = "action to compute a path from a list of nodes according to a given graph",
					returns = "the computed path, return nil if no path can be taken",
					examples = { @example ("do compute_path graph: road_network nodes: [node1, node5, node10];") }))
	public IPath primComputePathFromNodes(final IScope scope) throws GamaRuntimeException {
		// TODO: update this method
		final GamaGraph graph = (GamaGraph) scope.getArg("graph", IType.GRAPH);
		final IList<IAgent> nodes = (IList) scope.getArg("nodes", IType.LIST);

		if (nodes == null || nodes.isEmpty()) { return null; }
		final IAgent source = nodes.firstValue(scope);
		final IAgent target = nodes.lastValue(scope);
		final IList edges = GamaListFactory.create();
		for (int i = 0; i < nodes.size() - 1; i++) {
			final Set<Object> eds = graph.getAllEdges(nodes.get(i), nodes.get(i + 1));
			if (!eds.isEmpty()) {
				double minW = Double.MAX_VALUE;
				Object ed = null;
				for (final Object e : eds) {
					final double w = graph.getEdgeWeight(e);
					if (w < minW) {
						minW = w;
						ed = e;
					}
				}
				edges.add(ed);
			} else {
				return null;
			}
		}
		if (edges.isEmpty()) { return null; }
		final IPath path = PathFactory.newInstance(graph, source, target, edges);
		final IAgent agent = getCurrentAgent(scope);
		if (path != null && !path.getEdgeGeometry().isEmpty()) {
			final List<ILocation> targets = getTargets(agent);
			targets.clear();
			for (final Object edge : path.getEdgeGeometry()) {
				final IShape egGeom = (IShape) edge;
				final Coordinate[] coords = egGeom.getInnerGeometry().getCoordinates();
				final GamaPoint pt = new GamaPoint(coords[coords.length - 1]);
				targets.add(pt);
			}
			setTargets(agent, targets);
			final IAgent nwRoad = (IAgent) path.getEdgeList().get(0);
			setCurrentIndex(agent, 0);
			setCurrentTarget(agent, targets.get(0));
			setFinalTarget(agent, target.getLocation());
			setCurrentPath(agent, path);
			RoadSkill.register(scope, nwRoad, agent, 0);
			return path;

		}
		setTargets(agent, GamaListFactory.<ILocation> create(Types.POINT));
		setCurrentTarget(agent, null);
		setFinalTarget(agent, null);
		setCurrentPath(agent, (IPath) null);
		return null;
	}

	private Double speedChoice(final IAgent agent, final IAgent road) {
		return Math.min(getMaxSpeed(agent), Math.min(getRealSpeed(agent) + getAccelerationMax(agent),
				getSpeedCoeff(agent) * (Double) road.getAttribute(RoadSkill.MAXSPEED)));
	}

	@action (
			name = "drive_random",
			args = { @arg (
					name = "proba_roads",
					type = IType.MAP,
					optional = true,
					doc = @doc ("a map containing for each road (key), the probability to be selected as next road (value)")) },
			doc = @doc (
					value = "action to drive by chosen randomly the next road",
					examples = { @example ("do drive_random;") }))
	public void primDriveRandom(final IScope scope) throws GamaRuntimeException {
		// TODO: update this method
		final IAgent driver = getCurrentAgent(scope);
		final ISpecies context = driver.getSpecies();
		final IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		final Arguments argsEF = new Arguments();
		final IStatement.WithArgs actionLC = context.getAction("lane_choice");
		final Arguments argsLC = new Arguments();
		final IStatement.WithArgs actionSC = context.getAction("speed_choice");
		final Arguments argsSC = new Arguments();
		final Map<IAgent, Double> roadProba = (Map) scope.getArg("proba_roads", IType.MAP);

		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		while (remainingTime > 0.0) {
			final IAgent road = getCurrentRoad(driver);
			final GamaPoint target = GeometryUtils.getLastPointOf(road);
			argsSC.put("new_road", ConstantExpressionDescription.create(road));
			actionSC.setRuntimeArgs(scope, argsSC);
			final double speed = (Double) actionSC.executeOn(scope);
			setSpeed(driver, speed);
			remainingTime = primAdvancedFollow(scope, driver, speed, remainingTime, null, target);

			if (remainingTime > 0.0) {
				IAgent newRoad = null;
				final IAgent targetNode = (IAgent) road.getDirectVarValue(scope, RoadSkill.TARGET_NODE);
				final List<IAgent> nextRoads = (List) targetNode.getDirectVarValue(scope, RoadNodeSkill.ROADS_OUT);
				if (nextRoads.isEmpty()) { return; }
				if (nextRoads.size() == 1) {
					newRoad = nextRoads.get(0);
				}
				if (nextRoads.size() > 1) {
					if (roadProba == null || roadProba.isEmpty()) {
						newRoad = nextRoads.get(scope.getRandom().between(0, nextRoads.size() - 1));
					} else {
						final IList<Double> distribution = GamaListFactory.create(Types.FLOAT);
						for (final IAgent r : nextRoads) {
							final Double val = roadProba.get(r);
							distribution.add(val == null ? 0.0 : val);
						}
						newRoad = nextRoads.get(Random.opRndChoice(scope, distribution));
					}
				}

				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);
				argsLC.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionLC.setRuntimeArgs(scope, argsLC);
				final int lane = (Integer) actionLC.executeOn(scope);

				if (lane >= 0) {
					RoadSkill.unregister(scope, driver);
					RoadSkill.register(scope, newRoad, driver, lane);
				} else {
					return;
				}
			}
		}
	}

	@action (
			name = "drive",
			doc = @doc (
					value = "action to drive toward the final target",
					examples = { @example ("do drive;") }))
	public void primDrive(IScope scope) throws GamaRuntimeException {
		IAgent driver = getCurrentAgent(scope);
		if (driver == null || driver.dead()) return;

		GamaPoint finalTarget = getFinalTarget(driver);
		if (finalTarget == null) { return; }
		IPath path = getCurrentPath(driver);

		// some preparations to execute other actions during the loop
		final ISpecies context = driver.getSpecies();
		final IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		final Arguments argsEF = new Arguments();
		final IStatement.WithArgs actionLC = context.getAction("lane_choice");
		final Arguments argsLC = new Arguments();
		final IStatement.WithArgs actionSC = context.getAction("speed_choice");
		final Arguments argsSC = new Arguments();

		// get the amount of time that the driver is able to travel in one simulation step
		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		// main loop to move the agent until the simulation step ends
		while (true) {
			ILocation loc = driver.getLocation();
			GamaPoint target = getCurrentTarget(driver);
			// final target check
			if (loc.equals(finalTarget)) {
				setFinalTarget(driver, null);
				return;
			}
			// intermediate target check
			if (remainingTime > 0.0 && loc.equals(target)) {
				int currEdgeIdx = getCurrentIndex(driver);
				// TODO: this seems to be checking if driver has reached final target? (already checked above)
				// edit: maybe it is safeguarding the case when an invalid final target is used
				// commented out for now
				// if (currentTargetIdx >= path.getEdgeList().size() - 1) {
				// 	setCurrentPath(driver, (IPath) null);
				// 	setFinalTarget(driver, null);
				// 	return;
				// }

				// get the next road in the path
				IAgent newRoad = (IAgent) path.getEdgeList().get(currEdgeIdx + 1);
				// check traffic lights and vehicles coming from other roads
				if (!isReadyNextRoad(scope, newRoad)) {
					return;
				}

				// external factor that affects remaining time, can be defined by user
				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);

				// choose a lane on the new road
				argsLC.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionLC.setRuntimeArgs(scope, argsLC);
				int lane = (Integer) actionLC.executeOn(scope);
				if (lane >= 0) {
					// updating states like this since there are n + 1 nodes and n edges in a path
					setCurrentIndex(driver, currEdgeIdx + 1);
					setCurrentTarget(driver, getTargets(driver).get(currEdgeIdx + 2));
					RoadSkill.unregister(scope, driver);
					RoadSkill.register(scope, newRoad, driver, lane);
				} else {
					return;
				}
			}

			// LOOP BREAK CONDITION
			if (remainingTime <= 0.0) break;

			IAgent road = getCurrentRoad(driver);
			// compute the desired speed
			argsSC.put("new_road", ConstantExpressionDescription.create(road));
			actionSC.setRuntimeArgs(scope, argsSC);
			double desiredSpeed = (Double) actionSC.executeOn(scope);
			setSpeed(driver, desiredSpeed);

			// move towards the current target and compute the remaining time
			remainingTime = primAdvancedFollow(scope, driver, desiredSpeed, remainingTime, path, target);
		}
	}

	@action (
			name = "external_factor_impact",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to the driver wants to go")),
					@arg (
							name = "remaining_time",
							type = IType.FLOAT,
							optional = false,
							doc = @doc ("the remaining time")) },
			doc = @doc (
					value = "action that allows to define how the remaining time is impacted by external factor",
					returns = "the remaining time",
					examples = { @example ("do external_factor_impact new_road: a_road remaining_time: 0.5;") }))
	public Double primExternalFactorOnRemainingTime(final IScope scope) throws GamaRuntimeException {
		return scope.getFloatArg("remaining_time");
	}

	@action (
			name = "speed_choice",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to choose the speed")) },
			doc = @doc (
					value = "action to choose a speed",
					returns = "the chosen speed",
					examples = { @example ("do speed_choice new_road: the_road;") }))
	public Double primSpeedChoice(final IScope scope) throws GamaRuntimeException {
		final IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		final IAgent agent = getCurrentAgent(scope);
		final double speed = speedChoice(agent, road);
		setSpeed(agent, speed);
		return speed;
	}

	/**
	 * Choose a new lane when entering a new road
	 *
	 * @param scope
	 * @param newRoad
	 * @return a lane index on the new road
	 *
	 * @throws GamaRuntimeException
	 */
	public Integer chooseLane(IScope scope, IAgent newRoad) throws GamaRuntimeException {
		IAgent driver = getCurrentAgent(scope);
		double vehicleLength = getVehicleLength(driver);
		int numLanesOccupied = getNumLanesOccupied(driver);
		Integer numRoadLanes = (Integer) newRoad.getAttribute(RoadSkill.LANES);
		// not enough lanes in new road
		if (numLanesOccupied > numRoadLanes) {
			throw GamaRuntimeException.error(driver.getName() + " occupies " + numLanesOccupied + " lanes, " +
					"but " + newRoad.getName() + " only has " + numRoadLanes + " lane(s)!", scope);
		}

		Integer currentLane = getCurrentLane(driver);
		IAgent node = (IAgent) newRoad.getAttribute(RoadSkill.SOURCE_NODE);

		// road node blocking information, which is a map: driver -> list of blocked roads
		Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		Collection<IAgent> blockingDrivers = new HashSet<>(blockInfo.keySet());

		// force the driver to return from the linked road
		if (isUsingLinkedRoad(driver)) {
			currentLane = numRoadLanes - numLanesOccupied;
		}

		// check if any blocking driver has moved
		for (IAgent otherDriver : blockingDrivers) {
			if (!otherDriver.getLocation().equals(node.getLocation())) {
				blockInfo.remove(otherDriver);
			}
		}

		// driver decides if it is going to ignore the full road ahead and
		// block the intersection anyway
		double probaBlock = getProbaBlockNode(driver);
		boolean goingToBlock = Random.opFlip(scope, probaBlock);

		// TODO: how could a road have no lane? 
		// edit: removing this block causes no issue so far
		// if (numLanes == 0 /* && !onLinkedRoad */ ) {
		// 	int lane = goingToBlock || nextRoadTestLane(driver, newRoad, 0, secDistCoeff, vL) ? 0 : -1;

		// 	if (lane != -1) {
		// 		addBlockingDriver(0, goingToBlock, driver, currentRoad, newRoad, node, roadsIn, blockInfo);
		// 		return lane;
		// 	}
		// 	return lane;
		// }

		final int newLaneIdx = Math.min(currentLane, numRoadLanes - numLanesOccupied);
		// check current lane
		if (DrivingOperators.enoughSpaceToEnterRoad(scope, newRoad, newLaneIdx, numLanesOccupied, vehicleLength / 2)) {
			return newLaneIdx;
		}

		// driver decides if he's going to switch lanes on the new road or not
		double probaLaneChangeDown = getProbaLaneChangeDown(driver);
		double probaLaneChangeUp = getProbaLaneChangeUp(driver);
		boolean changeDown = Random.opFlip(scope, probaLaneChangeDown);
		boolean changeUp = Random.opFlip(scope, probaLaneChangeUp);
		if (changeDown || changeUp) {
			for (int i = 0; i <= numRoadLanes - numLanesOccupied; i++) {
				if (i < newLaneIdx && changeDown &&
						DrivingOperators.enoughSpaceToEnterRoad(scope, newRoad, i, numLanesOccupied, vehicleLength / 2)) {
					return i;
				}
				if (i > newLaneIdx && changeUp &&
						DrivingOperators.enoughSpaceToEnterRoad(scope, newRoad, i, numLanesOccupied, vehicleLength / 2)) {
					return i;
				}
			}
		}

		if (goingToBlock) {
			blockIntersection(scope, getCurrentRoad(driver), newRoad, node);
		}
		return -1;
	}

	@action (
			name = "lane_choice",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to choose the lane")) },
			doc = @doc (
					value = "action to choose a lane",
					returns = "the chosen lane, return -1 if no lane can be taken",
					examples = { @example ("do lane_choice new_road: a_road;") }))
	public Integer primLaneChoice(final IScope scope) throws GamaRuntimeException {
		final IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		return chooseLane(scope, road);
	}

	/**
	 * Block other incoming roads at the specified node
	 *
	 * @param scope
	 * @param currentRoad
	 * @param newRoad
	 * @param node The node agent whose blocking info will be updated
	 */
	public void blockIntersection(IScope scope, IAgent currentRoad, IAgent newRoad, IAgent node) {
		List<IAgent> inRoads = (List<IAgent>) node.getAttribute(RoadNodeSkill.ROADS_IN);
		if (inRoads.size() <= 1) {
			return;
		}
		// list of the roads that will be blocked by this driver
		List<IAgent> blockedRoads = GamaListFactory.create(Types.AGENT);
		for (IAgent road : inRoads) {
			// do not block the linked road associated to the new road
			if (!road.getLocation().equals(newRoad.getLocation())) {
				blockedRoads.add(road);
			}
		}
		if (!blockedRoads.isEmpty()) {
			IAgent driver = getCurrentAgent(scope);
			Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
			blockInfo.put(driver, blockedRoads);
		}
	}

	public double distance2D(final GamaPoint p1, final GamaPoint p2) {
		return p1.distance(p2);
	}

	/*
	 * Compute distance to the closest vehicle ahead
	 */
	private double computeDistToVehicleAhead(final IScope scope, final IAgent driver, final double distance,
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int startingLane,
			final int segmentIndex, final boolean onLinkedRoad, final IAgent currentRoad, final boolean changeLane) {
		final double distanceToGoal = getDistanceToGoal(driver);
		int numLanesOccupied = getNumLanesOccupied(driver);
		int numLanesCurrent = (int) currentRoad.getAttribute(RoadSkill.LANES);

		// if it's going to enter a new segment
		final boolean nextSegment = distanceToGoal < distance;
		final double min_safety_distance =
				driver.hasAttribute(MIN_SAFETY_DISTANCE) ? getMinSafetyDistance(driver) : getMinSecurityDistance(driver);
		// final int newIndexInv = agPrevLane.size() - segment - 1;
		int numSegments = 0;
		// collect drivers that we may "crashed into"
		List<IAgent> sameSegmentDrivers = new ArrayList<>();
		List<IAgent> nextSegmentDrivers = new ArrayList<>();
		for (int i = 0; i < numLanesOccupied; i += 1) {
			// determine the correct lane & segment on either main or linked road
			int relLane = startingLane + i;
			boolean laneOnLinkedRoad = relLane >= numLanesCurrent;
			int correctSegment = RoadSkill.computeCorrectSegment(currentRoad, segmentIndex, laneOnLinkedRoad);
			int absLane = RoadSkill.computeValidLane(currentRoad, relLane);
			IAgent road = laneOnLinkedRoad ? (IAgent) currentRoad.getAttribute(RoadSkill.LINKED_ROAD) :
					currentRoad;

			// collect drivers in same lane, same segment
			List agentsOn = (List) road.getAttribute(RoadSkill.AGENTS_ON);
			List laneDrivers = (List) agentsOn.get(absLane);
			List<IAgent> segmentDrivers = (List<IAgent>) laneDrivers.get(correctSegment);
			numSegments = laneDrivers.size();
			for (IAgent otherDriver : segmentDrivers) {
				if (!sameSegmentDrivers.contains(otherDriver)) {
					sameSegmentDrivers.add(otherDriver);
				}
			}
			
			// collect drivers in same lane, next segment
			if (correctSegment > 0 && correctSegment < numSegments - 1) {
				int nextSegmentIdx = laneOnLinkedRoad ? correctSegment - 1 : correctSegment + 1;
				segmentDrivers = (List<IAgent>) laneDrivers.get(nextSegmentIdx);
				for (IAgent otherDriver : segmentDrivers) {
					if (!nextSegmentDrivers.contains(otherDriver)) {
						nextSegmentDrivers.add(otherDriver);
					}
				}
			}
		}
		final boolean moreSegment = /*!usingLinkedRoad && */segmentIndex <= numSegments - 2;

		final boolean contains = sameSegmentDrivers.contains(driver);
		final GamaPoint targetLoc = new GamaPoint(currentRoad.getInnerGeometry().getCoordinates()[segmentIndex + 1]);
		final double vL = getVehicleLength(driver);
		// if there are no vehicles on the current segment, driver can take into account the next segment
		if (contains && sameSegmentDrivers.size() < 2 || !contains && sameSegmentDrivers.isEmpty()) {
			// this line causes vehicles to not switch lanes with small simulation step size
			// if (changeLane && distance < vL) { return 0; }

			// have to return to the current road to get to the next segment?
			// if (usingLinkedRoad && nextSegment) { return 0; }

			// if entering a new segment on the road
			if (nextSegment && moreSegment) {
				final double length = currentRoad.getInnerGeometry().getCoordinates()[segmentIndex + 2].distance(targetLoc);
				for (final IAgent ag : nextSegmentDrivers) {
					// check if there is enough space to fit in the 2nd segment
					final double otherDistToGoal = (getCurrentRoad(driver) == getCurrentRoad(ag)) ?
						getDistanceToGoal(ag) : distance2D((GamaPoint) ag.getLocation(), targetLoc);
					final double vLa = 0.5 * vL + 0.5 * getVehicleLength(ag);
					if (length - otherDistToGoal < vLa) { 
						return distanceToGoal - (vLa - (length - otherDistToGoal));
					}
				}
			}
			return distance;
		}
		// if (usingLinkedRoad && nextSegment) { return 0; }

		// finding the closest driver ahead
		IAgent closestDriverAhead = null;
		double minDiff = Double.MAX_VALUE;
		for (final IAgent ag : sameSegmentDrivers) {
			if (ag == driver || ag == null || ag.dead()) {
				continue;
			}
			final double otherDistToGoal = (getCurrentRoad(driver) == getCurrentRoad(ag)) ?
				getDistanceToGoal(ag) : distance2D((GamaPoint) ag.getLocation(), targetLoc);
			// compute diff this way is faster than computing euclidean dist between two vehicles
			final double diff = distanceToGoal - otherDistToGoal;
			if (changeLane && Math.abs(diff) < vL) { return 0; }
			if (diff <= 0.0) {
				// the other driver is behind
				continue;
			}
			if (diff < minDiff) {
				minDiff = diff;
				closestDriverAhead = ag;
			}
		}

		// the segment ahead is clear
		if (closestDriverAhead == null) {
			if (nextSegment && moreSegment) {
				final double length = currentRoad.getInnerGeometry().getCoordinates()[segmentIndex + 1]
						.distance(currentRoad.getInnerGeometry().getCoordinates()[segmentIndex + 2]);

				for (final IAgent ag : nextSegmentDrivers) {
					final double otherDistToGoal = (getCurrentRoad(driver) == getCurrentRoad(ag)) ?
						getDistanceToGoal(ag) : distance2D((GamaPoint) ag.getLocation(), targetLoc);
					final double vLa = 0.5 * vL + 0.5 * getVehicleLength(ag);
					if (otherDistToGoal > length - vLa) {
						return distanceToGoal - (vLa - (length - otherDistToGoal));
					}
				}
			}
			return distance;
		}
		double secDistance = 0.0;
		if (getCurrentRoad(closestDriverAhead) == getCurrentRoad(driver)) {
			secDistance = Math.max(min_safety_distance,
					security_distance * Math.min(getRealSpeed(driver), getRealSpeed(closestDriverAhead)));
		} else {
			secDistance = Math.max(min_safety_distance,
					security_distance * Math.max(getRealSpeed(driver), getRealSpeed(closestDriverAhead)));
		}
		double realDist = Math.min(distance, minDiff - secDistance - 0.5 * vL - 0.5 * getVehicleLength(closestDriverAhead));

		// this line causes vehicles to not switch lanes with small simulation step size
		// if (changeLane && realDist < vL) { return 0; }

		// TODO: what is this?
		// realDist = Math.max(0.0, (int) (min_safety_distance + realDist * 1000) / 1000.0);
		realDist = Math.max(0.0, realDist);

		return realDist;
	}

	
	/**
	 * Updates the `agents_on` list of the corresponding roads after the driver has switch lanes and/or segment
	 *
	 * @param scope
	 * @param newStartingLane
	 * @param newSegment
	 */
	private void updateLaneSegment(IScope scope, int newStartingLane, int newSegment) {
		IAgent driver = getCurrentAgent(scope);
		int numLanesOccupied = getNumLanesOccupied(driver);
		int currStartingLane = getCurrentLane(driver);
		int currSegment = getSegmentIndex(driver);

		HashSet<Integer> oldRelLanes = IntStream.range(currStartingLane, currStartingLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));
		HashSet<Integer> newRelLanes = IntStream.range(newStartingLane, newStartingLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));

		HashSet<Integer> removedRelLanes, addedRelLanes;
		if (newSegment != currSegment) {
			// if entering new segment, we have to update the lists for all related lanes and segments
			removedRelLanes = oldRelLanes;
			addedRelLanes = newRelLanes;
		} else {
			// otherwise the driver is still in the same segment, only changing (possibly a few) lanes
			removedRelLanes = (HashSet) oldRelLanes.clone();
			removedRelLanes.removeAll(newRelLanes);
			addedRelLanes = (HashSet) newRelLanes.clone();
			addedRelLanes.removeAll(oldRelLanes);
		}

		IAgent road;
		IAgent currentRoad = getCurrentRoad(driver);
		IAgent linkedRoad = RoadSkill.getLinkedRoad(currentRoad);
		int numLanesCurrent = RoadSkill.getLanes(currentRoad);
		for (int relLane : removedRelLanes) {
			boolean isLinkedLane = relLane >= numLanesCurrent;
			road = isLinkedLane ? linkedRoad : currentRoad;
			RoadSkill.removeDriverFromLaneSegment(scope, driver, road,
					RoadSkill.computeValidLane(currentRoad, relLane),
					RoadSkill.computeCorrectSegment(currentRoad, currSegment, isLinkedLane));
		}
		for (int relLane : addedRelLanes) {
			boolean isLinkedLane = relLane >= numLanesCurrent;
			road = isLinkedLane ? linkedRoad : currentRoad;
			RoadSkill.addDriverToLaneSegment(scope, driver, road,
					RoadSkill.computeValidLane(currentRoad, relLane),
					RoadSkill.computeCorrectSegment(currentRoad, newSegment, isLinkedLane));
		}

		driver.setAttribute(CURRENT_LANE, newStartingLane);
		driver.setAttribute(SEGMENT_INDEX, newSegment);
	}

	/**
	 * Choose the lane in which the driver can travel the longest distance,
	 * and move across that lane (i.e. update driver's lane and segment index)
	 */
	private double avoidCollision(final IScope scope, final IAgent driver, final double distance,
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int currentStartingLane,
			final int segment, final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp,
			final Double probaChangeLaneDown, final Double probaUseLinkedRoad, final Boolean rightSide) {
		int numLanesOccupied = getNumLanesOccupied(driver);
		double distMax = 0;
		int bestStartingLane = currentStartingLane;
		int numLanesLinked = (linkedRoad != null) ? (int) linkedRoad.getAttribute(RoadSkill.LANES) : 0;
		int linkedLaneLimit = getLinkedLaneLimit(driver);
		linkedLaneLimit = linkedLaneLimit != -1 ? linkedLaneLimit : numLanesLinked;
		int numRoadLanes = (Integer) currentRoad.getAttribute(RoadSkill.LANES);
		double val;

		val = computeDistToVehicleAhead(scope, driver, distance, security_distance, currentLocation, target, currentStartingLane, segment,
				false, currentRoad, false);
		if (val == distance) {
			// stay in the lane, but possibly update segment
			updateLaneSegment(scope, currentStartingLane, segment);
			return distance;
		}
		if (val >= distMax) {
			distMax = val;
			bestStartingLane = currentStartingLane;
		}

		if (currentStartingLane > 0 && scope.getRandom().next() < probaChangeLaneDown) {
			val = computeDistToVehicleAhead(scope, driver, distance, security_distance, currentLocation, target,
					currentStartingLane - 1, segment, false, currentRoad, true);
			if (val == distance) {
				updateLaneSegment(scope, currentStartingLane - 1, segment);
				return distance;
			}
			if (val > distMax) {
				bestStartingLane = currentStartingLane - 1;
				distMax = val;
			}
		}

		if ((currentStartingLane < numRoadLanes - numLanesOccupied ||
				(currentStartingLane > numRoadLanes - numLanesOccupied && currentStartingLane < numRoadLanes + linkedLaneLimit - numLanesOccupied))
				&& scope.getRandom().next() < probaChangeLaneUp) {
			val = computeDistToVehicleAhead(scope, driver, distance, security_distance, currentLocation, target, currentStartingLane + 1, segment,
					false, currentRoad, true);
			if (val == distance) {
				updateLaneSegment(scope, currentStartingLane + 1, segment);
				return distance;
			}
			if (val > distMax) {
				distMax = val;
				bestStartingLane = currentStartingLane + 1;
			}
		}

		if (linkedRoad != null &&
				currentStartingLane == numRoadLanes - numLanesOccupied &&
				scope.getRandom().next() < probaUseLinkedRoad) {
			DEBUG.LOG(driver.getName() + " PASSED the linkedRoad check");
			DEBUG.LOG("current starting lane: " + currentStartingLane);
			val = computeDistToVehicleAhead(scope, driver, distance, security_distance, currentLocation, target, currentStartingLane + 1,
					segment, true, currentRoad, true);

			if (val == distance) {
				updateLaneSegment(scope, currentStartingLane + 1, segment);
				return distance;
			}
			if (val > distMax) {
				DEBUG.LOG(driver.getName() + " is choosing the linked road");
				distMax = val;
				bestStartingLane = currentStartingLane + 1;
			}
		}

		updateLaneSegment(scope, bestStartingLane, segment);
		return distMax;
	}

	private double moveToNextLocAlongPathOSM(final IScope scope, final IAgent driver, final IPath path,
			final GamaPoint target, final double _distance, final double security_distance, final int lane,
			final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp,
			final Double probaChangeLaneDown, final Double probaUseLinkedRoad, final Boolean rightSide) {
		int currentLane = lane;
		GamaPoint currentLocation = (GamaPoint) driver.getLocation().copy(scope);
		final GamaPoint falseTarget = target == null ? new GamaPoint(
				currentRoad.getInnerGeometry().getCoordinates()[currentRoad.getInnerGeometry().getCoordinates().length])
				: target;

		int indexSegment = getSegmentIndex(driver);
		final int endIndexSegment = GeometryUtils.getPointsOf(currentRoad).length - 1;

		double distance = _distance;
		double realDistance = 0;
		final IShape line = currentRoad.getGeometry();
		final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
		GamaPoint pt = null;
		for (int j = indexSegment; j < endIndexSegment; j++) {
			pt = new GamaPoint(coords[j + 1]);
			final double dist = pt.euclidianDistanceTo(currentLocation);
			setDistanceToGoal(driver, dist);
			distance = avoidCollision(scope, driver, distance, security_distance, currentLocation, falseTarget,
					currentLane, indexSegment, currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown,
					probaUseLinkedRoad, rightSide);
			currentLane = (Integer) driver.getAttribute(CURRENT_LANE);

			// if can not reach the end of the segment, we are done
			if (distance < dist) {
				final double ratio = distance / dist;
				final double newX = currentLocation.getX() + ratio * (pt.getX() - currentLocation.getX());
				final double newY = currentLocation.getY() + ratio * (pt.getY() - currentLocation.getY());
				final GamaPoint npt = new GamaPoint(newX, newY);
				realDistance += currentLocation.euclidianDistanceTo(npt);
				currentLocation.setLocation(npt);
				distance = 0;
				break;
			}
			// else continue to the next segment
			currentLocation = pt;
			distance = distance - dist;
			realDistance += dist;
			if (j == endIndexSegment) {
				break;
			}
			indexSegment++;
		}
		if (pt != null) {
			setDistanceToGoal(driver, pt.distance(currentLocation));
		}
		setLocation(driver, currentLocation);
		if (path != null) {
			path.setSource(currentLocation.copy(scope));
		}

		// TODO: hmm, I suppose this is intended for simulation step = 1 s
		driver.setAttribute(IKeyword.REAL_SPEED, realDistance);
		return _distance == 0.0 ? 1.0 : distance / _distance;
	}
	
	@action (
			name = "die",
			doc = @doc (
					value = "remove the driving agent from its current road and make it die",
					examples = { @example ("do die") }))
	public void primDieWrapper(final IScope scope) throws GamaRuntimeException {
		final AbstractAgent driver = (AbstractAgent) getCurrentAgent(scope);
		if (! driver.dead() && getCurrentRoad(driver) != null) {
			RoadSkill.unregister(scope, driver);
		}
		driver.primDie(scope);
	}
}
