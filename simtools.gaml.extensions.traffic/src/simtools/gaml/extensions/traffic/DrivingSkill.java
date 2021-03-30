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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Coordinate;

import com.google.common.collect.Range;

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

@vars({
	@variable(
		name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("the speed of the agent (in meter/second)")
	),
	@variable(
		name = IKeyword.REAL_SPEED,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("the actual speed of the agent (in meter/second)")
	),
	@variable(
		name = DrivingSkill.CURRENT_PATH,
		type = IType.PATH,
		init = "nil",
		doc = @doc("the current path that tha agent follow")
	),
	@variable(
		name = DrivingSkill.FINAL_TARGET,
		type = IType.POINT,
		init = "nil",
		doc = @doc("the final target of the agent")
	),
	@variable(
		name = DrivingSkill.CURRENT_TARGET,
		type = IType.POINT,
		init = "nil",
		doc = @doc("the current target of the agent")
	),
	@variable(
		name = DrivingSkill.CURRENT_INDEX,
		type = IType.INT,
		init = "0",
		doc = @doc("the index of the current edge (road) in the path")
	),
	@variable(
		name = DrivingSkill.TARGETS,
		type = IType.LIST,
		of = IType.POINT,
		init = "[]",
		doc = @doc("the current list of points that the agent has to reach (path)")
	),
	@variable(
		name = DrivingSkill.SECURITY_DISTANCE_COEFF,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc(
			deprecated = "use safety_distance_coeff instead",
			value = "the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - safety_distance =max(min_safety_distance, safety_distance_coeff `*` min(self.real_speed, other.real_speed) )"
		)
	),
	@variable(
		name = DrivingSkill.SAFETY_DISTANCE_COEFF,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - security_distance =max(min_security_distance, security_distance_coeff `*` min(self.real_speed, other.real_speed) )")
	),
	@variable(
		name = DrivingSkill.MIN_SECURITY_DISTANCE,
		type = IType.FLOAT,
		init = "0.5",
		doc = @doc(
			deprecated = "use min_safety_distance instead",
			value = "the minimal distance to another driver"
		)
	),
	@variable(
		name = DrivingSkill.MIN_SAFETY_DISTANCE,
		type = IType.FLOAT,
		init = "0.5",
		doc = @doc("the minimal distance to another driver")
	),
	@variable(
		name = DrivingSkill.CURRENT_LANE,
		type = IType.INT,
		init = "0",
		doc = @doc("the current lane on which the agent is")
	),
	@variable(
		name = DrivingSkill.STARTING_LANE,
		type = IType.INT,
		init = "0",
		doc = @doc("the lane with the smallest index that the vehicle is in")
	),
	@variable(
		name = DrivingSkill.NUM_LANES_OCCUPIED,
		type = IType.INT,
		init = "1",
		doc = @doc(
			value = "the number of lanes that the vehicle occupies",
			comment = "e.g. if `num_lanes_occupied=3` and `starting_lane=1`, the vehicle will be in lane 1, 2 and 3`"
		)
	),
	@variable(
		name = DrivingSkill.VEHICLE_LENGTH,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("the length of the vehicle (in meters)")
	),
	@variable(
		name = DrivingSkill.SPEED_COEFF,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("speed coefficient for the speed that the driver want to reach (according to the max speed of the road)")
	),
	@variable(
		name = DrivingSkill.MAX_ACCELERATION,
		type = IType.FLOAT,
		init = "0.5",
		doc = @doc("maximum acceleration of the car for a cycle")
	),
	@variable(
		name = DrivingSkill.CURRENT_ROAD,
		type = IType.AGENT,
		doc = @doc("the road which the vehicle is currently on")
	),
	@variable(
		name = DrivingSkill.ON_LINKED_ROAD,
		type = IType.BOOL,
		init = "false",
		doc = @doc(
			deprecated = "use using_linked_road instead",
			value = "is the agent on the linked road?"
		)
	),
	@variable(
		name = DrivingSkill.USING_LINKED_ROAD,
		type = IType.BOOL,
		init = "false",
		doc = @doc("indicates if the driver is occupying at least one lane on the linked road")
	),
	@variable(
		name = DrivingSkill.LINKED_LANE_LIMIT,
		type = IType.INT,
		init = "-1",
		doc = @doc("the maximum number of linked lanes that the vehicle can use; the default value is -1, i.e. the vehicle can use all available linked lanes")
	),
	@variable(
		name = DrivingSkill.LANE_CHANGE_LIMIT,
		type = IType.INT,
		init = "1",
		doc = @doc("the maximum number of lanes that the vehicle can change during a simulation step")
	),
	@variable(
		name = DrivingSkill.LANE_CHANGE_PRIORITY_RANDOMIZED,
		type = IType.BOOL,
		init = "false",
		doc = @doc("whether to randomize the lane choices when changing lane")
	),
	@variable(
		name = DrivingSkill.PROBA_LANE_CHANGE_UP,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("probability to change lane to a upper lane (left lane if right side driving) if necessary")
	),
	@variable(
		name = DrivingSkill.PROBA_LANE_CHANGE_DOWN,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("probability to change lane to a lower lane (right lane if right side driving) if necessary")
	),
	@variable(
		name = DrivingSkill.PROBA_RESPECT_PRIORITIES,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("probability to respect priority (right or left) laws")
	),
	@variable(
		name = DrivingSkill.PROBA_RESPECT_STOPS,
		type = IType.LIST,
		of = IType.FLOAT,
		init = "[]",
		doc = @doc("probability to respect stop laws - one value for each type of stop")
	),
	@variable(
		name = DrivingSkill.PROBA_BLOCK_NODE,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("probability to block a node (do not let other driver cross the crossroad)")
	),
	@variable(
		name = DrivingSkill.PROBA_USE_LINKED_ROAD,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("probability to change lane to a linked road lane if necessary")
	),
	@variable(
		name = DrivingSkill.RIGHT_SIDE_DRIVING,
		type = IType.BOOL,
		init = "true",
		doc = @doc("are drivers driving on the right size of the road?")
	),
	@variable(
		name = DrivingSkill.MAX_SPEED,
		type = IType.FLOAT,
		init = "50.0",
		doc = @doc("maximal speed of the vehicle")
	),
	@variable(
		name = DrivingSkill.DISTANCE_TO_GOAL,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("euclidean distance to the next point of the current segment")
	),
	@variable(
		name = DrivingSkill.SEGMENT_INDEX,
		type = IType.INT,
		init = "-1",
		doc = @doc("current segment index of the agent on the current road ")
	),
})
@skill(
	name = "advanced_driving",
	concept = { IConcept.TRANSPORT, IConcept.SKILL },
	doc = @doc ("A skill that provides driving primitives and operators")
)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrivingSkill extends MovingSkill {
	static {
		DEBUG.OFF();
	}

	@Deprecated public final static String SECURITY_DISTANCE_COEFF = "security_distance_coeff";
	public final static String SAFETY_DISTANCE_COEFF = "safety_distance_coeff";
	@Deprecated public final static String MIN_SECURITY_DISTANCE = "min_security_distance";
	public final static String MIN_SAFETY_DISTANCE = "min_safety_distance";

	public final static String CURRENT_ROAD = "current_road";
	@Deprecated public final static String CURRENT_LANE = "current_lane";
	public final static String STARTING_LANE = "starting_lane";
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
	public final static String MAX_ACCELERATION = "max_acceleration";
	public final static String SPEED_COEFF = "speed_coeff";
	public final static String MAX_SPEED = "max_speed";
	public final static String SEGMENT_INDEX = "segment_index_on_road";
	public final static String NUM_LANES_OCCUPIED = "num_lanes_occupied";
	public final static String LANE_CHANGE_LIMIT = "lane_change_limit";
	public final static String LANE_CHANGE_PRIORITY_RANDOMIZED = "lane_change_priority_randomized";

	@getter(MAX_ACCELERATION)
	public static double getAccelerationMax(final IAgent agent) {
		return (Double) agent.getAttribute(MAX_ACCELERATION);
	}

	@setter(MAX_ACCELERATION)
	public static void setAccelerationMax(final IAgent agent, final Double val) {
		agent.setAttribute(MAX_ACCELERATION, val);
	}

	@getter(SPEED_COEFF)
	public static double getSpeedCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SPEED_COEFF);
	}

	@setter(SPEED_COEFF)
	public static void setSpeedCoeff(final IAgent agent, final Double val) {
		agent.setAttribute(SPEED_COEFF, val);
	}

	@getter(MAX_SPEED)
	public static double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAX_SPEED);
	}

	@setter(MAX_SPEED)
	public static void setMaxSpeed(final IAgent agent, final Double val) {
		agent.setAttribute(MAX_SPEED, val);
	}

	@getter(CURRENT_TARGET)
	public static GamaPoint getCurrentTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(CURRENT_TARGET);
	}

	@setter(CURRENT_TARGET)
	public static void setCurrentTarget(final IAgent agent, final ILocation point) {
		agent.setAttribute(CURRENT_TARGET, point);
	}

	@getter(FINAL_TARGET)
	public static GamaPoint getFinalTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(FINAL_TARGET);
	}

	@setter(FINAL_TARGET)
	public static void setFinalTarget(final IAgent agent, final ILocation point) {
		agent.setAttribute(FINAL_TARGET, point);
	}

	@getter(CURRENT_INDEX)
	public static Integer getCurrentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_INDEX);
	}

	@setter(CURRENT_INDEX)
	public static void setCurrentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(CURRENT_INDEX, index);
	}

	@getter(SEGMENT_INDEX)
	public static Integer getSegmentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(SEGMENT_INDEX);
	}

	@setter(SEGMENT_INDEX)
	public static void setSegmentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(SEGMENT_INDEX, index);
	}

	@Override
	@getter(CURRENT_PATH)
	public IPath getCurrentPath(final IAgent agent) {
		return (IPath) agent.getAttribute(CURRENT_PATH);
	}

	@Override
	@setter(CURRENT_PATH)
	public void setCurrentPath(final IAgent agent, final IPath path) {
		agent.setAttribute(CURRENT_PATH, path);
	}

	@getter(TARGETS)
	public static List<ILocation> getTargets(final IAgent agent) {
		return (List<ILocation>) agent.getAttribute(TARGETS);
	}

	@setter(TARGETS)
	public static void setTargets(final IAgent agent, final List<ILocation> points) {
		agent.setAttribute(TARGETS, points);
	}

	@getter(PROBA_USE_LINKED_ROAD)
	public static double getProbaUseLinkedRoad(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_USE_LINKED_ROAD);
	}

	@setter(PROBA_USE_LINKED_ROAD)
	public static void setProbaUseLinkedRoad(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_USE_LINKED_ROAD, proba);
	}

	@getter(PROBA_LANE_CHANGE_DOWN)
	public static double getProbaLaneChangeDown(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	@setter(PROBA_LANE_CHANGE_DOWN)
	public static void setProbaLaneChangeDown(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	@getter(PROBA_LANE_CHANGE_UP)
	public static double getProbaLaneChangeUp(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	@setter(PROBA_LANE_CHANGE_UP)
	public static void setProbaLaneChangeUp(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_UP, proba);
	}

	@getter(PROBA_RESPECT_PRIORITIES)
	public static double getRespectPriorities(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_RESPECT_PRIORITIES);
	}

	@setter(PROBA_RESPECT_PRIORITIES)
	public static void setRespectPriorities(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_RESPECT_PRIORITIES, proba);
	}

	@getter(PROBA_BLOCK_NODE)
	public static double getProbaBlockNode(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_BLOCK_NODE);
	}

	@setter(PROBA_BLOCK_NODE)
	public static void setProbaBlockNode(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_BLOCK_NODE, proba);
	}

	@getter(PROBA_RESPECT_STOPS)
	public static List<Double> getRespectStops(final IAgent agent) {
		return (List<Double>) agent.getAttribute(PROBA_RESPECT_STOPS);
	}

	@setter(PROBA_RESPECT_STOPS)
	public static void setRespectStops(final IAgent agent, final List<Boolean> probas) {
		agent.setAttribute(PROBA_RESPECT_STOPS, probas);
	}

	@Deprecated
	@getter(ON_LINKED_ROAD)
	public static boolean getOnLinkedRoad(final IAgent agent) {
		return isUsingLinkedRoad(agent);
	}

	@Deprecated
	@setter(ON_LINKED_ROAD)
	public static void setOnLinkedRoad(final IAgent agent, final Boolean onLinkedRoad) {
		// read-only
	}

	@getter(USING_LINKED_ROAD)
	public static boolean isUsingLinkedRoad(IAgent driver) {
		IAgent currentRoad = getCurrentRoad(driver);
		if (currentRoad == null) return false;

		int startingLane = getStartingLane(driver);
		int numLanesCurrent = RoadSkill.getLanes(currentRoad);
		int numLanesOccupied = getNumLanesOccupied(driver);
		return startingLane > numLanesCurrent - numLanesOccupied;
	}

	@setter(USING_LINKED_ROAD)
	public static void setUsingLinkedRoad(IAgent driver, boolean usingLinkedRoad) {
		// read-only
	}

	@getter(LINKED_LANE_LIMIT)
	public static int getLinkedLaneLimit(IAgent driver) {
		return (int) driver.getAttribute(LINKED_LANE_LIMIT);
	}

	@setter(LINKED_LANE_LIMIT)
	public static void setLinkedLaneLimit(IAgent driver, int linkedLaneLimit) {
		driver.setAttribute(LINKED_LANE_LIMIT, linkedLaneLimit);
	}

	@getter(LANE_CHANGE_LIMIT)
	public static int getLaneChangeLimit(IAgent driver) {
		return (int) driver.getAttribute(LANE_CHANGE_LIMIT);
	}

	@getter(LANE_CHANGE_PRIORITY_RANDOMIZED)
	public static boolean isLaneChangePriorityRandomized(IAgent driver) {
		return (boolean) driver.getAttribute(LANE_CHANGE_PRIORITY_RANDOMIZED);
	}

	@getter(RIGHT_SIDE_DRIVING)
	public static boolean getRightSideDriving(final IAgent agent) {
		return (Boolean) agent.getAttribute(RIGHT_SIDE_DRIVING);
	}

	@setter(RIGHT_SIDE_DRIVING)
	public static void setRightSideDriving(final IAgent agent, final Boolean isRight) {
		agent.setAttribute(RIGHT_SIDE_DRIVING, isRight);
	}

	@Deprecated
	@getter(SECURITY_DISTANCE_COEFF)
	public static double getSecurityDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SECURITY_DISTANCE_COEFF);
	}

	@Deprecated
	@setter(SECURITY_DISTANCE_COEFF)
	public static void setSecurityDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SECURITY_DISTANCE_COEFF, ls);
	}

	@getter(SAFETY_DISTANCE_COEFF)
	public static double getSafetyDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SAFETY_DISTANCE_COEFF);
	}

	@setter(SAFETY_DISTANCE_COEFF)
	public static void setSafetyDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SAFETY_DISTANCE_COEFF, ls);
	}

	@getter(CURRENT_ROAD)
	public static IAgent getCurrentRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(CURRENT_ROAD);
	}

	@getter(VEHICLE_LENGTH)
	public static double getVehicleLength(final IAgent agent) {
		return (Double) agent.getAttribute(VEHICLE_LENGTH);
	}

	@Deprecated
	@getter(CURRENT_LANE)
	public static int getCurrentLane(final IAgent agent) {
		return (int) agent.getAttribute(STARTING_LANE);
	}

	@Deprecated
	@setter(CURRENT_LANE)
	public static void setCurrentLane(IAgent agent, int newLane) {
		agent.setAttribute(STARTING_LANE, newLane);
	}

	@getter(STARTING_LANE)
	public static int getStartingLane(final IAgent agent) {
		return (int) agent.getAttribute(STARTING_LANE);
	}

	@getter(DISTANCE_TO_GOAL)
	public static double getDistanceToGoal(final IAgent agent) {
		return (Double) agent.getAttribute(DISTANCE_TO_GOAL);
	}

	@getter(MIN_SECURITY_DISTANCE)
	public static double getMinSecurityDistance(final IAgent agent) {
		return (Double) agent.getAttribute(MIN_SECURITY_DISTANCE);
	}

	@setter(MIN_SECURITY_DISTANCE)
	public static void setMinSecDistance(final IAgent agent, final double msd) {
		agent.setAttribute(MIN_SECURITY_DISTANCE, msd);
	}

	@getter(MIN_SAFETY_DISTANCE)
	public static double getMinSafetyDistance(final IAgent agent) {
		return (Double) agent.getAttribute(MIN_SAFETY_DISTANCE);
	}

	@setter(DISTANCE_TO_GOAL)
	public static void setDistanceToGoal(final IAgent agent, final double dg) {
		agent.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	@getter(NUM_LANES_OCCUPIED)
	public static Integer getNumLanesOccupied(IAgent agent) {
		return (Integer) agent.getAttribute(NUM_LANES_OCCUPIED);
	}

	@setter(NUM_LANES_OCCUPIED)
	public static void setNumLanesOccupied(IAgent agent, Integer value) {
		agent.setAttribute(NUM_LANES_OCCUPIED, value);
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
		// TODO: path is not used anywhere in this action
		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		final Double t = scope.hasArg("time") ? scope.getFloatArg("time") : scope.getClock().getStepInSeconds();
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		return moveToNextLocAlongPathOSM(scope, s, t, path);
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
				// TODO: this should include drivers on linked roads as well
				List<IAgent> otherDrivers = (List) otherInRoad.getAttribute(RoadSkill.ALL_AGENTS);
				for (IAgent otherDriver : otherDrivers) {
					if (otherDriver == null || otherDriver.dead()) {
						continue;
					}
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
			RoadSkill.register(scope, agent, nwRoad, 0);
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
			remainingTime = moveToNextLocAlongPathOSM(scope, speed, remainingTime, null);

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
					RoadSkill.register(scope, driver, newRoad, lane);
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
		double timeSpentMoving = Double.MAX_VALUE;
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
					RoadSkill.register(scope, driver, newRoad, lane);
				} else {
					return;
				}
			}

			// if time is up or the vehicle can not move any further, we are done
			if (remainingTime < 1e-8 || timeSpentMoving < 1e-8) {
				break;
			}

			IAgent road = getCurrentRoad(driver);
			// compute the desired speed
			argsSC.put("new_road", ConstantExpressionDescription.create(road));
			actionSC.setRuntimeArgs(scope, argsSC);
			double desiredSpeed = (Double) actionSC.executeOn(scope);
			setSpeed(driver, desiredSpeed);

			// move towards the end of the road
			timeSpentMoving = moveToNextLocAlongPathOSM(scope, desiredSpeed, remainingTime, path);
			remainingTime -= timeSpentMoving;
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
	 * Chooses a new lane when entering a new road
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
		Integer numCurrentLanes = (Integer) newRoad.getAttribute(RoadSkill.LANES);
		// not enough lanes in new road
		if (numLanesOccupied > numCurrentLanes) {
			throw GamaRuntimeException.error(driver.getName() + " occupies " + numLanesOccupied + " lanes, " +
					"but " + newRoad.getName() + " only has " + numCurrentLanes + " lane(s)!", scope);
		}

		Integer startingLane = getStartingLane(driver);
		IAgent node = (IAgent) newRoad.getAttribute(RoadSkill.SOURCE_NODE);

		// road node blocking information, which is a map: driver -> list of blocked roads
		Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		Collection<IAgent> blockingDrivers = new HashSet<>(blockInfo.keySet());

		// force the driver to return from the linked road
		if (isUsingLinkedRoad(driver)) {
			startingLane = numCurrentLanes - numLanesOccupied;
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

		final int newStartingLane = Math.min(startingLane, numCurrentLanes - numLanesOccupied);
		// check current lane
		if (DrivingOperators.enoughSpaceToEnterRoad(scope, newRoad, newStartingLane, numLanesOccupied, vehicleLength / 2)) {
			return newStartingLane;
		}

		// driver decides if he's going to switch lanes on the new road or not
		double probaLaneChangeDown = getProbaLaneChangeDown(driver);
		double probaLaneChangeUp = getProbaLaneChangeUp(driver);
		boolean changeDown = Random.opFlip(scope, probaLaneChangeDown);
		boolean changeUp = Random.opFlip(scope, probaLaneChangeUp);
		if (changeDown || changeUp) {
			for (int i = 0; i <= numCurrentLanes - numLanesOccupied; i++) {
				if (i < newStartingLane && changeDown &&
						DrivingOperators.enoughSpaceToEnterRoad(scope, newRoad, i, numLanesOccupied, vehicleLength / 2)) {
					return i;
				}
				if (i > newStartingLane && changeUp &&
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
	 * Blocks other incoming roads at the specified node
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

	/**
	 * Computes distance to the closest vehicle ahead on the specified lanes and segment.
	 * If the specified segment is totally clear, we will also consider vehicles on the next segment.
	 *
	 * @param scope
	 * @param remainingDist the remaining distance
	 * @param startingLane the starting lane to check for vehicles
	 * @param segment the segment to check for vehicles
	 * @return the distance to the closest vehicle ahead
	 */
	private double computeDistToVehicleAhead(IScope scope, double remainingDist, int startingLane, int segment) {
		IAgent driver = getCurrentAgent(scope);
		double distanceToGoal = getDistanceToGoal(driver);
		IAgent currentRoad = getCurrentRoad(driver);
		int numLanesOccupied = getNumLanesOccupied(driver);
		int numLanesCurrent = (int) currentRoad.getAttribute(RoadSkill.LANES);

		// if it's going to enter a new segment
		boolean nextSegment = distanceToGoal < remainingDist;

		double safetyDistCoeff = driver.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(driver)
				: getSecurityDistanceCoeff(driver);
		double minSafetyDist =
				driver.hasAttribute(MIN_SAFETY_DISTANCE) ? getMinSafetyDistance(driver) : getMinSecurityDistance(driver);

		int numSegments = 0;
		// collect drivers that we may "crashed into"
		List<IAgent> sameSegmentDrivers = new ArrayList<>();
		List<IAgent> nextSegmentDrivers = new ArrayList<>();
		for (int i = 0; i < numLanesOccupied; i += 1) {
			// determine the correct lane & segment on either main or linked road
			int relLane = startingLane + i;
			boolean laneOnLinkedRoad = relLane >= numLanesCurrent;
			int correctSegment = RoadSkill.computeCorrectSegment(currentRoad, segment, laneOnLinkedRoad);
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
			int nextSegmentIdx = laneOnLinkedRoad ? correctSegment - 1 : correctSegment + 1;
			if (nextSegmentIdx >= 0 && nextSegmentIdx < numSegments) {
				segmentDrivers = (List<IAgent>) laneDrivers.get(nextSegmentIdx);
				for (IAgent otherDriver : segmentDrivers) {
					if (!nextSegmentDrivers.contains(otherDriver)) {
						nextSegmentDrivers.add(otherDriver);
					}
				}
			}
		}
		boolean moreSegment = /*!usingLinkedRoad && */segment <= numSegments - 2;

		boolean contains = sameSegmentDrivers.contains(driver);
		GamaPoint targetLoc = new GamaPoint(currentRoad.getInnerGeometry().getCoordinates()[segment + 1]);
		double vL = getVehicleLength(driver);
		// if there are no vehicles on the current segment, driver can take into account the next segment
		if (contains && sameSegmentDrivers.size() < 2 || !contains && sameSegmentDrivers.isEmpty()) {
			// if entering a new segment on the road
			if (nextSegment && moreSegment) {
				double length = currentRoad.getInnerGeometry().getCoordinates()[segment + 2].distance(targetLoc);
				for (IAgent otherDriver : nextSegmentDrivers) {
					// check if there is enough space to fit in the 2nd segment
					double otherDistToGoal = (getCurrentRoad(driver) == getCurrentRoad(otherDriver)) ?
						getDistanceToGoal(otherDriver) : distance2D((GamaPoint) otherDriver.getLocation(), targetLoc);
					double vLa = 0.5 * vL + 0.5 * getVehicleLength(otherDriver);
					if (length - otherDistToGoal < vLa) {
						return distanceToGoal - (vLa - (length - otherDistToGoal));
					}
				}
			}
			return remainingDist;
		}

		// finding the closest driver ahead
		IAgent closestDriverAhead = null;
		IAgent closestDriverBehind = null;
		double minDiffAhead = Double.MAX_VALUE;
		double minDiffBehind = Double.MAX_VALUE;
		for (IAgent otherDriver : sameSegmentDrivers) {
			if (otherDriver == driver || otherDriver == null || otherDriver.dead()) {
				continue;
			}
			double otherDistToGoal = (getCurrentRoad(driver) == getCurrentRoad(otherDriver)) ?
				getDistanceToGoal(otherDriver) : distance2D((GamaPoint) otherDriver.getLocation(), targetLoc);
			// NOTE: compute diffence this way is faster than computing euclidean dist between two vehicles,
			// and it provides order of vehicles as well.
			// difference between two centroids of the vehicles
			double pointDiff = distanceToGoal - otherDistToGoal;
			// taking into account vehicle lengths
			double realDiff = Math.abs(pointDiff) - 0.5 * vL - 0.5 * getVehicleLength(otherDriver);
			if (pointDiff <= 0.0 && realDiff < minDiffBehind) {
				minDiffBehind = realDiff;
				closestDriverBehind = otherDriver;
			} else if (pointDiff > 0 && realDiff < minDiffAhead) {
				minDiffAhead = realDiff;
				closestDriverAhead = otherDriver;
			}
		}

		// avoid crashing with the closest vehicle behind
		if (closestDriverBehind != null && !closestDriverBehind.dead()) {
			// returning -1 ensures that the vehicle will not switch to this lane
			if (minDiffBehind < 0) return -1;
		}

		// the segment ahead is clear
		if (closestDriverAhead == null || closestDriverAhead.dead()) {
			if (nextSegment && moreSegment) {
				double length = currentRoad.getInnerGeometry().getCoordinates()[segment + 1]
						.distance(currentRoad.getInnerGeometry().getCoordinates()[segment + 2]);

				for (IAgent otherDriver : nextSegmentDrivers) {
					double otherDistToGoal = (getCurrentRoad(driver) == getCurrentRoad(otherDriver)) ?
						getDistanceToGoal(otherDriver) : distance2D((GamaPoint) otherDriver.getLocation(), targetLoc);
					double vLa = 0.5 * vL + 0.5 * getVehicleLength(otherDriver);
					if (otherDistToGoal > length - vLa) {
						return distanceToGoal - (vLa - (length - otherDistToGoal));
					}
				}
			}
			return remainingDist;
		}
		double secDistance = 0.0;
		if (getCurrentRoad(closestDriverAhead) == getCurrentRoad(driver)) {
			secDistance = Math.max(minSafetyDist,
					safetyDistCoeff * Math.min(getRealSpeed(driver), getRealSpeed(closestDriverAhead)));
		} else {
			secDistance = Math.max(minSafetyDist,
					safetyDistCoeff * Math.max(getRealSpeed(driver), getRealSpeed(closestDriverAhead)));
		}
		double realDist = Math.min(remainingDist, minDiffAhead - secDistance);

		// TODO: what is this?
		// realDist = Math.max(0.0, (int) (min_safety_distance + realDist * 1000) / 1000.0);
		realDist = Math.max(0.0, realDist);

		return realDist;
	}

	
	/**
	 * Updates the `agents_on` list of the corresponding roads after the driver
	 * has switched to new lanes and/or a new segment.
	 *
	 * @param scope
	 * @param newStartingLane
	 * @param newSegment
	 */
	private void updateLaneSegment(IScope scope, int newStartingLane, int newSegment) {
		IAgent driver = getCurrentAgent(scope);
		int numLanesOccupied = getNumLanesOccupied(driver);
		int currStartingLane = getStartingLane(driver);
		int currSegment = getSegmentIndex(driver);

		HashSet<Integer> oldRelLanes = IntStream.range(currStartingLane, currStartingLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));
		HashSet<Integer> newRelLanes = IntStream.range(newStartingLane, newStartingLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));

		// TODO: rewrite this part using guava
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

		driver.setAttribute(STARTING_LANE, newStartingLane);
		driver.setAttribute(SEGMENT_INDEX, newSegment);
	}

	/**
	 * Chooses the lanes such that the vehicle can travel the furthest, and then moves the driver
	 * across those lanes (i.e. updates driver's starting lane and segment).
	 *
	 * The default order of lanes to consider (i.e. when `lane_change_priority_randomized=false`) is:
	 * 		lower -> current -> upper
	 *
	 * @param scope
	 * @param remainingDist the remaining distance
	 * @param segment the segment that the vehicle is currently in
	 * @return the maximum distance that the vehicle can travel
	 */
	private double avoidCollision(IScope scope, double remainingDist, int segment) {
		IAgent driver = getCurrentAgent(scope);
		Double probaChangeLaneUp = getProbaLaneChangeUp(driver);
		Double probaChangeLaneDown = getProbaLaneChangeDown(driver);
		Double probaUseLinkedRoad = getProbaUseLinkedRoad(driver);
		int numLanesOccupied = getNumLanesOccupied(driver);
		int laneChangeLimit = getLaneChangeLimit(driver);

		int startingLane = getStartingLane(driver);
		IAgent currentRoad = getCurrentRoad(driver);
		IAgent linkedRoad = RoadSkill.getLinkedRoad(currentRoad);
		int numCurrentLanes = (Integer) currentRoad.getAttribute(RoadSkill.LANES);
		int numLinkedLanes = (linkedRoad != null) ? (int) linkedRoad.getAttribute(RoadSkill.LANES) : 0;
		int linkedLaneLimit = getLinkedLaneLimit(driver);
		linkedLaneLimit = (linkedLaneLimit != -1 && numLinkedLanes > linkedLaneLimit) ?
				linkedLaneLimit : numLinkedLanes;

		Range<Integer> limitedLaneRange;
		if (laneChangeLimit == -1) {
			// can change to all available lanes
			limitedLaneRange = Range.closed(0, numCurrentLanes + linkedLaneLimit - numLanesOccupied);
		} else {
			limitedLaneRange = Range.closed(startingLane - laneChangeLimit, startingLane + laneChangeLimit);
		}
		List<Integer> allLanes = IntStream.rangeClosed(0, numCurrentLanes + linkedLaneLimit - numLanesOccupied)
				.boxed().collect(Collectors.toCollection(ArrayList::new));
		if (isLaneChangePriorityRandomized(driver)) {
			Collections.shuffle(allLanes);
		}
		int bestStartingLane = startingLane;
		double maxDist = 0;

		for (int tmpStartingLane : allLanes) {
			if (!limitedLaneRange.contains(tmpStartingLane)) {
				continue;
			}
			boolean canStayInSameLane = tmpStartingLane == startingLane;
			boolean canChangeDown = tmpStartingLane >= 0 && tmpStartingLane < startingLane &&
					scope.getRandom().next() < probaChangeLaneDown;
			// first two conditions check the valid upper lane idxs, the 3rd one is to restrict moving from current road to linked road
			boolean canChangeUp = tmpStartingLane > startingLane &&
					 tmpStartingLane <= numCurrentLanes + linkedLaneLimit - numLanesOccupied &&
					 !(startingLane <= numCurrentLanes - numLanesOccupied && tmpStartingLane > numCurrentLanes - numLanesOccupied) &&
					 scope.getRandom().next() < probaChangeLaneUp;
			boolean canChangeToLinkedRoad = linkedRoad != null && linkedLaneLimit > 0 &&
					tmpStartingLane > startingLane &&
					tmpStartingLane == numCurrentLanes - numLanesOccupied + 1 &&
					scope.getRandom().next() < probaUseLinkedRoad;

			if (canStayInSameLane || canChangeDown || canChangeUp || canChangeToLinkedRoad) {
				double tmpDist = computeDistToVehicleAhead(scope, remainingDist, tmpStartingLane, segment);
				// if lane is totally clear then just choose it and return immediately
				if (tmpDist == remainingDist) {
					updateLaneSegment(scope, tmpStartingLane, segment);
					return remainingDist;
				}
				if (tmpDist > maxDist) {
					maxDist = tmpDist;
					bestStartingLane = tmpStartingLane;
				}
			}
		}
		updateLaneSegment(scope, bestStartingLane, segment);
		return maxDist;
	}

	/**
	 * Moves the driver from segment to segment on the current road.
	 *
	 * @param scope
	 * @param speed the desired speed of the vehicle
	 * @param time left until a simulation step is finished
	 * @param path no idea what it does for now
	 * @return the actual time that the vehicle spent moving
	 */
	private double moveToNextLocAlongPathOSM(IScope scope, double speed, double remainingTime, IPath path) {
		IAgent driver = getCurrentAgent(scope);
		IAgent currentRoad = getCurrentRoad(driver);
		GamaPoint currentLocation = (GamaPoint) driver.getLocation().copy(scope);

		int indexSegment = getSegmentIndex(driver);
		int endIndexSegment = GeometryUtils.getPointsOf(currentRoad).length - 1;

		// the maximum distance that the vehicle can move, if it does not get blocked
		// by any other vehicle
		double maxDist = speed * remainingTime;
		if (maxDist < 1e-8) {
			return 0.0;
		}
		double remainingDist = maxDist;
		double totalDistMoved = 0;

		IShape line = currentRoad.getGeometry();
		Coordinate coords[] = line.getInnerGeometry().getCoordinates();
		GamaPoint pt = null;
		for (int j = indexSegment; j < endIndexSegment; j++) {
			pt = new GamaPoint(coords[j + 1]);
			double distToGoal = pt.euclidianDistanceTo(currentLocation);
			setDistanceToGoal(driver, distToGoal);
			// NOTE: distMoved is always <= remainingDist
			double distMoved = avoidCollision(scope, remainingDist, indexSegment);

			// if can not reach the end of the segment, we are done
			if (distMoved < distToGoal) {
				double ratio = distMoved / distToGoal;
				double newX = currentLocation.getX() + ratio * (pt.getX() - currentLocation.getX());
				double newY = currentLocation.getY() + ratio * (pt.getY() - currentLocation.getY());
				GamaPoint npt = new GamaPoint(newX, newY);
				totalDistMoved += currentLocation.euclidianDistanceTo(npt);
				currentLocation.setLocation(npt);
				break;
			}
			// else continue to the next segment
			currentLocation = pt;
			remainingDist -= distToGoal;
			totalDistMoved += distToGoal;
			if (j == endIndexSegment) {
				break;
			}
			indexSegment += 1;
		}
		if (pt != null) {
			setDistanceToGoal(driver, pt.distance(currentLocation));
		}
		setLocation(driver, currentLocation);
		if (path != null) {
			path.setSource(currentLocation.copy(scope));
		}

		driver.setAttribute(IKeyword.REAL_SPEED, totalDistMoved / remainingTime);

		return remainingTime * (totalDistMoved / maxDist);
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
