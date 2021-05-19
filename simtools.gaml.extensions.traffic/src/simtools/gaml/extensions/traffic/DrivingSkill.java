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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.locationtech.jts.geom.Coordinate;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
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
		name = DrivingSkill.ACCELERATION,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("the current acceleration of the vehicle (in m/s^2)")
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
			value = "the coefficient for the computation of the the min distance between two vehicles (according to the vehicle speed - safety_distance =max(min_safety_distance, safety_distance_coeff `*` min(self.real_speed, other.real_speed) )"
		)
	),
	@variable(
		name = DrivingSkill.SAFETY_DISTANCE_COEFF,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("the coefficient for the computation of the the min distance between two vehicles (according to the vehicle speed - security_distance =max(min_security_distance, security_distance_coeff `*` min(self.real_speed, other.real_speed) )")
	),
	@variable(
		name = DrivingSkill.MIN_SECURITY_DISTANCE,
		type = IType.FLOAT,
		init = "0.5",
		doc = @doc(
			deprecated = "use min_safety_distance instead",
			value = "the minimal distance to another vehicle"
		)
	),
	@variable(
		name = DrivingSkill.MIN_SAFETY_DISTANCE,
		type = IType.FLOAT,
		init = "0.5",
		doc = @doc("the minimum distance of the vehicle's front bumper to the leading vehicle's rear bumper, " +
			"known as the parameter s0 in the Intelligent Driver Model")
	),
	@variable(
		name = DrivingSkill.CURRENT_LANE,
		type = IType.INT,
		init = "0",
		doc = @doc("the current lane on which the agent is")
	),
	@variable(
		name = DrivingSkill.LOWEST_LANE,
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
			comment = "e.g. if `num_lanes_occupied=3` and `lowest_lane=1`, the vehicle will be in lane 1, 2 and 3`"
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
		doc = @doc("speed coefficient for the speed that the vehicle want to reach (according to the max speed of the road)")
	),
	@variable(
		name = DrivingSkill.MAX_SPEED,
		type = IType.FLOAT,
		init = "50.0",
		doc = @doc("the maximum speed that the vehicle can achieve. " +
			"Known as the parameter 'v0' in the Intelligent Driver Model"
		)
	),
	@variable(
		name = DrivingSkill.MAX_TIME_HEADWAY,
		type = IType.FLOAT,
		init = "1.5",
		// TODO: this might be a bit confusing
		doc = @doc("the maximum time difference between the front bumpers of this vehicle and its leading vehicle. " +
			"Known as the parameter 'T' in the Intelligent Driver Model"
		)
	),
	@variable(
		name = DrivingSkill.MAX_ACCELERATION,
		type = IType.FLOAT,
		init = "0.3",
		doc = @doc("the maximum acceleration of the vehicle. " +
			"Known as the parameter 'a' in the Intelligent Driver Model"
		)
	),
	@variable(
		name = DrivingSkill.MAX_DECELERATION,
		type = IType.FLOAT,
		init = "3.0",
		doc = @doc("the maximum deceleration of the vehicle. " +
			"Known as the parameter 'b' in the Intelligent Driver Model"
		)
	),
	@variable(
		name = DrivingSkill.DELTA_IDM,
		type = IType.FLOAT,
		init = "4.0",
		doc = @doc("the exponent used in the computation of free-road acceleration in the Intelligent Driver Model")
	),
	@variable(
		name = DrivingSkill.POLITENESS_FACTOR,
		type = IType.FLOAT,
		init = "0.5",
		doc = @doc("determines the politeness level of the vehicle when changing lanes. " +
			"Known as the parameter 'p' in the MOBIL lane changing model"
		)
	),
	@variable(
		name = DrivingSkill.MAX_SAFE_DECELERATION,
		type = IType.FLOAT,
		init = "4",
		doc = @doc("the maximum deceleration that the vehicle is willing to induce on its back vehicle when changing lanes. " +
			"Known as the parameter 'b_save' in the MOBIL lane changing model"
		)
	),
	@variable(
		name = DrivingSkill.ACC_GAIN_THRESHOLD,
		type = IType.FLOAT,
		init = "0.2",
		doc = @doc("the minimum acceleration gain for the vehicle to switch to another lane, " +
			"introduced to prevent frantic lane changing. " +
			"Known as the parameter 'p' in the MOBIL lane changing model"
		)
	),
	@variable(
		name = DrivingSkill.CURRENT_ROAD,
		type = IType.AGENT,
		init = "nil",
		doc = @doc("the road which the vehicle is currently on")
	),
	@variable(
		name = DrivingSkill.NEXT_ROAD,
		type = IType.AGENT,
		init = "nil",
		doc = @doc("the road which the vehicle will enter next")
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
		doc = @doc("indicates if the vehicle is occupying at least one lane on the linked road")
	),
	@variable(
		name = DrivingSkill.ALLOWED_LANES,
		type = IType.LIST,
		init = "[]",
		doc = @doc("a list containing possible lane index values for the attribute lowest_lane")
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
		doc = @doc("probability to change to a upper lane (left lane if right side driving) to gain acceleration, within one second")
	),
	@variable(
		name = DrivingSkill.PROBA_LANE_CHANGE_DOWN,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("probability to change to a lower lane (right lane if right side driving) to gain acceleration, within one second")
	),
	@variable(
		name = DrivingSkill.PROBA_USE_LINKED_ROAD,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("probability to change to a linked lane to gain acceleration, within one second")
	),
	@variable(
		name = DrivingSkill.PROBA_RESPECT_PRIORITIES,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("probability to respect priority (right or left) laws, within one second")
	),
	@variable(
		name = DrivingSkill.PROBA_RESPECT_STOPS,
		type = IType.LIST,
		of = IType.FLOAT,
		init = "[]",
		doc = @doc("probability to respect stop laws - one value for each type of stop, within one second")
	),
	@variable(
		name = DrivingSkill.PROBA_BLOCK_NODE,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("probability to block a node (do not let other vehicle cross the crossroad), within one second")
	),
	@variable(
		name = DrivingSkill.RIGHT_SIDE_DRIVING,
		type = IType.BOOL,
		init = "true",
		doc = @doc("are vehicles driving on the right size of the road?")
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
	@variable(
		name = DrivingSkill.LEADING_VEHICLE,
		type = IType.AGENT,
		init = "nil",
		doc = @doc("the vehicle which is right ahead of the current vehicle.\n" +
			"If this is set to nil, the leading vehicle does not exist or might be very far away."
		)
	),
	@variable(
		name = DrivingSkill.LEADING_DISTANCE,
		type = IType.FLOAT,
		init = "nil",
		doc = @doc("the distance to the leading vehicle")
	),
	@variable(
		name = DrivingSkill.LEADING_SPEED,
		type = IType.FLOAT,
		init = "nil",
		doc = @doc("the speed of the leading vehicle")
	)
})
@skill(
	name = DrivingSkill.ADVANCED_DRIVING,
	concept = { IConcept.TRANSPORT, IConcept.SKILL },
	doc = @doc ("A skill that provides driving primitives and operators")
)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrivingSkill extends MovingSkill {
	static {
		DEBUG.OFF();
	}

	public static final String ADVANCED_DRIVING = "advanced_driving";

	@Deprecated public static final String SECURITY_DISTANCE_COEFF = "security_distance_coeff";
	public static final String SAFETY_DISTANCE_COEFF = "safety_distance_coeff";
	@Deprecated public static final String MIN_SECURITY_DISTANCE = "min_security_distance";
	public static final String MIN_SAFETY_DISTANCE = "min_safety_distance";
	public static final String CURRENT_ROAD = "current_road";
	public static final String NEXT_ROAD = "next_road";
	@Deprecated public static final String CURRENT_LANE = "current_lane";
	public static final String LOWEST_LANE = "lowest_lane";
	public static final String DISTANCE_TO_GOAL = "distance_to_goal";
	public static final String VEHICLE_LENGTH = "vehicle_length";
	public static final String PROBA_LANE_CHANGE_UP = "proba_lane_change_up";
	public static final String PROBA_LANE_CHANGE_DOWN = "proba_lane_change_down";
	public static final String PROBA_RESPECT_PRIORITIES = "proba_respect_priorities";
	public static final String PROBA_RESPECT_STOPS = "proba_respect_stops";
	public static final String PROBA_BLOCK_NODE = "proba_block_node";
	public static final String PROBA_USE_LINKED_ROAD = "proba_use_linked_road";
	public static final String RIGHT_SIDE_DRIVING = "right_side_driving";
	public static final String ON_LINKED_ROAD = "on_linked_road";
	public static final String USING_LINKED_ROAD = "using_linked_road";
	public static final String LINKED_LANE_LIMIT = "linked_lane_limit";
	public static final String ALLOWED_LANES = "allowed_lanes";
	public static final String TARGETS = "targets";
	public static final String CURRENT_TARGET = "current_target";
	public static final String CURRENT_INDEX = "current_index";
	public static final String FINAL_TARGET = "final_target";
	public static final String CURRENT_PATH = "current_path";
	public static final String ACCELERATION = "acceleration";
	public static final String MAX_ACCELERATION = "max_acceleration";
	public static final String MAX_DECELERATION = "max_deceleration";
	public static final String MAX_TIME_HEADWAY = "max_time_headway";
	public static final String DELTA_IDM = "delta_idm";
	public static final String POLITENESS_FACTOR = "politeness_factor";
	public static final String MAX_SAFE_DECELERATION = "max_safe_deceleration";
	public static final String ACC_GAIN_THRESHOLD = "acc_gain_threshold";
	public static final String SPEED_COEFF = "speed_coeff";
	public static final String MAX_SPEED = "max_speed";
	public static final String SEGMENT_INDEX = "segment_index_on_road";
	public static final String NUM_LANES_OCCUPIED = "num_lanes_occupied";
	public static final String LANE_CHANGE_LIMIT = "lane_change_limit";
	public static final String LANE_CHANGE_PRIORITY_RANDOMIZED = "lane_change_priority_randomized";
	public static final String LEADING_VEHICLE = "leading_vehicle";
	public static final String LEADING_DISTANCE = "leading_distance";
	public static final String LEADING_SPEED = "leading_speed";

	// A small threshold representing zero (used to handle approximations in IDM)
	// TODO: not sure if this is the right threshold
	private static final Double EPSILON = 1e-4;

	@setter(ACCELERATION)
	public static void setAccelerationReadOnly(final IAgent vehicle, final Double val) {
		// read-only
	}

	private static void setAcceleration(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(ACCELERATION, val);
	}

	@getter(MAX_ACCELERATION)
	public static double getMaxAcceleration(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_ACCELERATION);
	}

	@setter(MAX_ACCELERATION)
	public static void setMaxAcceleration(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(MAX_ACCELERATION, val);
	}

	@getter(MAX_DECELERATION)
	public static double getMaxDeceleration(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_DECELERATION);
	}

	@getter(MAX_TIME_HEADWAY)
	public static double getMaxTimeHeadway(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_TIME_HEADWAY);
	}

	@getter(DELTA_IDM)
	public static double getDeltaIDM(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(DELTA_IDM);
	}

	@getter(POLITENESS_FACTOR)
	public static double getPolitenessFactor(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(POLITENESS_FACTOR);
	}

	@getter(MAX_SAFE_DECELERATION)
	public static double getMaxSafeDeceleration(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_SAFE_DECELERATION);
	}

	@getter(ACC_GAIN_THRESHOLD)
	public static double getAccGainThreshold(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(ACC_GAIN_THRESHOLD);
	}

	@getter(SPEED_COEFF)
	public static double getSpeedCoeff(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(SPEED_COEFF);
	}

	@setter(SPEED_COEFF)
	public static void setSpeedCoeff(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(SPEED_COEFF, val);
	}

	@getter(MAX_SPEED)
	public static double getMaxSpeed(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_SPEED);
	}

	@setter(MAX_SPEED)
	public static void setMaxSpeed(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(MAX_SPEED, val);
	}

	@getter(CURRENT_TARGET)
	public static GamaPoint getCurrentTarget(final IAgent vehicle) {
		return (GamaPoint) vehicle.getAttribute(CURRENT_TARGET);
	}

	@setter(CURRENT_TARGET)
	public static void setCurrentTarget(final IAgent vehicle, final ILocation point) {
		vehicle.setAttribute(CURRENT_TARGET, point);
	}

	@getter(FINAL_TARGET)
	public static GamaPoint getFinalTarget(final IAgent vehicle) {
		return (GamaPoint) vehicle.getAttribute(FINAL_TARGET);
	}

	@setter(FINAL_TARGET)
	public static void setFinalTarget(final IAgent vehicle, final ILocation point) {
		vehicle.setAttribute(FINAL_TARGET, point);
	}

	@getter(CURRENT_INDEX)
	public static Integer getCurrentIndex(final IAgent vehicle) {
		return (Integer) vehicle.getAttribute(CURRENT_INDEX);
	}

	@setter(CURRENT_INDEX)
	public static void setCurrentIndex(final IAgent vehicle, final Integer index) {
		vehicle.setAttribute(CURRENT_INDEX, index);
	}

	@getter(SEGMENT_INDEX)
	public static Integer getSegmentIndex(final IAgent vehicle) {
		return (Integer) vehicle.getAttribute(SEGMENT_INDEX);
	}

	@setter(SEGMENT_INDEX)
	public static void setSegmentIndex(final IAgent vehicle, final Integer index) {
		vehicle.setAttribute(SEGMENT_INDEX, index);
	}

	@Override
	@getter(CURRENT_PATH)
	public IPath getCurrentPath(final IAgent vehicle) {
		return (IPath) vehicle.getAttribute(CURRENT_PATH);
	}

	@Override
	@setter(CURRENT_PATH)
	public void setCurrentPath(final IAgent vehicle, final IPath path) {
		vehicle.setAttribute(CURRENT_PATH, path);
	}

	@getter(TARGETS)
	public static List<ILocation> getTargets(final IAgent vehicle) {
		return (List<ILocation>) vehicle.getAttribute(TARGETS);
	}

	@setter(TARGETS)
	public static void setTargets(final IAgent vehicle, final List<ILocation> points) {
		vehicle.setAttribute(TARGETS, points);
	}

	@getter(PROBA_USE_LINKED_ROAD)
	public static double getProbaUseLinkedRoad(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_USE_LINKED_ROAD);
	}

	@setter(PROBA_USE_LINKED_ROAD)
	public static void setProbaUseLinkedRoad(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_USE_LINKED_ROAD, proba);
	}

	@getter(PROBA_LANE_CHANGE_DOWN)
	public static double getProbaLaneChangeDown(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	@setter(PROBA_LANE_CHANGE_DOWN)
	public static void setProbaLaneChangeDown(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	@getter(PROBA_LANE_CHANGE_UP)
	public static double getProbaLaneChangeUp(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	@setter(PROBA_LANE_CHANGE_UP)
	public static void setProbaLaneChangeUp(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_LANE_CHANGE_UP, proba);
	}

	@getter(PROBA_RESPECT_PRIORITIES)
	public static double getProbaRespectPriorities(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_RESPECT_PRIORITIES);
	}

	@setter(PROBA_RESPECT_PRIORITIES)
	public static void setProbaRespectPriorities(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_RESPECT_PRIORITIES, proba);
	}

	@getter(PROBA_BLOCK_NODE)
	public static double getProbaBlockNode(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_BLOCK_NODE);
	}

	@setter(PROBA_BLOCK_NODE)
	public static void setProbaBlockNode(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_BLOCK_NODE, proba);
	}

	@getter(PROBA_RESPECT_STOPS)
	public static List<Double> getProbasRespectStops(final IAgent vehicle) {
		return (List<Double>) vehicle.getAttribute(PROBA_RESPECT_STOPS);
	}

	@setter(PROBA_RESPECT_STOPS)
	public static void setProbasRespectStops(final IAgent vehicle, final List<Boolean> probas) {
		vehicle.setAttribute(PROBA_RESPECT_STOPS, probas);
	}

	@Deprecated
	@getter(ON_LINKED_ROAD)
	public static boolean getOnLinkedRoad(final IAgent vehicle) {
		return isUsingLinkedRoad(vehicle);
	}

	@Deprecated
	@setter(ON_LINKED_ROAD)
	public static void setOnLinkedRoad(final IAgent vehicle, final Boolean onLinkedRoad) {
		// read-only
	}

	@getter(USING_LINKED_ROAD)
	public static boolean isUsingLinkedRoad(final IAgent vehicle) {
		IAgent currentRoad = getCurrentRoad(vehicle);
		if (currentRoad == null) return false;

		int lowestLane = getLowestLane(vehicle);
		int numLanesCurrent = RoadSkill.getNumLanes(currentRoad);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		return lowestLane > numLanesCurrent - numLanesOccupied;
	}

	@setter(USING_LINKED_ROAD)
	public static void setUsingLinkedRoad(final IAgent vehicle, final boolean usingLinkedRoad) {
		// read-only
	}

	@getter(ALLOWED_LANES)
	public static List<Integer> getAllowedLanes(final IAgent vehicle) {
		return (List<Integer>) vehicle.getAttribute(ALLOWED_LANES);
	}

	@getter(LINKED_LANE_LIMIT)
	public static int getLinkedLaneLimit(final IAgent vehicle) {
		return (int) vehicle.getAttribute(LINKED_LANE_LIMIT);
	}

	@setter(LINKED_LANE_LIMIT)
	public static void setLinkedLaneLimit(final IAgent vehicle, final int linkedLaneLimit) {
		vehicle.setAttribute(LINKED_LANE_LIMIT, linkedLaneLimit);
	}

	@getter(LANE_CHANGE_LIMIT)
	public static int getLaneChangeLimit(final IAgent vehicle) {
		return (int) vehicle.getAttribute(LANE_CHANGE_LIMIT);
	}

	@getter(LANE_CHANGE_PRIORITY_RANDOMIZED)
	public static boolean isLaneChangePriorityRandomized(final IAgent vehicle) {
		return (boolean) vehicle.getAttribute(LANE_CHANGE_PRIORITY_RANDOMIZED);
	}

	@getter(RIGHT_SIDE_DRIVING)
	public static boolean getRightSideDriving(final IAgent vehicle) {
		return (Boolean) vehicle.getAttribute(RIGHT_SIDE_DRIVING);
	}

	@setter(RIGHT_SIDE_DRIVING)
	public static void setRightSideDriving(final IAgent vehicle, final Boolean isRight) {
		vehicle.setAttribute(RIGHT_SIDE_DRIVING, isRight);
	}

	@Deprecated
	@getter(SECURITY_DISTANCE_COEFF)
	public static double getSecurityDistanceCoeff(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(SECURITY_DISTANCE_COEFF);
	}

	@Deprecated
	@setter(SECURITY_DISTANCE_COEFF)
	public static void setSecurityDistanceCoeff(final IAgent vehicle, final double ls) {
		vehicle.setAttribute(SECURITY_DISTANCE_COEFF, ls);
	}

	@getter(SAFETY_DISTANCE_COEFF)
	public static double getSafetyDistanceCoeff(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(SAFETY_DISTANCE_COEFF);
	}

	@setter(SAFETY_DISTANCE_COEFF)
	public static void setSafetyDistanceCoeff(final IAgent vehicle, final double ls) {
		vehicle.setAttribute(SAFETY_DISTANCE_COEFF, ls);
	}

	@getter(CURRENT_ROAD)
	public static IAgent getCurrentRoad(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(CURRENT_ROAD);
	}

	@setter(CURRENT_ROAD)
	public static void setCurrentRoad(final IAgent vehicle, final IAgent road) {
		vehicle.setAttribute(CURRENT_ROAD, road);
	}

	@getter(NEXT_ROAD)
	public static IAgent getNextRoad(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(NEXT_ROAD);
	}

	@setter(NEXT_ROAD)
	public static void setNextRoad(final IAgent vehicle, final IAgent road) {
		vehicle.setAttribute(NEXT_ROAD, road);
	}

	@getter(VEHICLE_LENGTH)
	public static double getVehicleLength(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(VEHICLE_LENGTH);
	}

	@Deprecated
	@getter(CURRENT_LANE)
	public static int getCurrentLane(final IAgent vehicle) {
		return getLowestLane(vehicle);
	}

	@Deprecated
	@setter(CURRENT_LANE)
	public static void setCurrentLane(final IAgent vehicle, final int newLane) {
		setLowestLane(vehicle, newLane);
	}

	@getter(LOWEST_LANE)
	public static int getLowestLane(final IAgent vehicle) {
		return (int) vehicle.getAttribute(LOWEST_LANE);
	}

	@setter(LOWEST_LANE)
	public static void setLowestLane(final IAgent vehicle, final int lowestLane) {
		vehicle.setAttribute(LOWEST_LANE, lowestLane);
	}

	@getter(DISTANCE_TO_GOAL)
	public static double getDistanceToGoal(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(DISTANCE_TO_GOAL);
	}

	@getter(MIN_SECURITY_DISTANCE)
	public static double getMinSecurityDistance(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MIN_SECURITY_DISTANCE);
	}

	@setter(MIN_SECURITY_DISTANCE)
	public static void setMinSecDistance(final IAgent vehicle, final double msd) {
		vehicle.setAttribute(MIN_SECURITY_DISTANCE, msd);
	}

	@getter(MIN_SAFETY_DISTANCE)
	public static double getMinSafetyDistance(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MIN_SAFETY_DISTANCE);
	}

	@setter(DISTANCE_TO_GOAL)
	public static void setDistanceToGoal(final IAgent vehicle, final double dg) {
		vehicle.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	@getter(NUM_LANES_OCCUPIED)
	public static Integer getNumLanesOccupied(final IAgent vehicle) {
		return (Integer) vehicle.getAttribute(NUM_LANES_OCCUPIED);
	}

	@setter(NUM_LANES_OCCUPIED)
	public static void setNumLanesOccupied(final IAgent vehicle, final Integer value) {
		vehicle.setAttribute(NUM_LANES_OCCUPIED, value);
	}

	@getter(LEADING_VEHICLE)
	public static IAgent getLeadingVehicle(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(LEADING_VEHICLE);
	}

	@setter(LEADING_VEHICLE)
	public static void setLeadingVehicleReadOnly(final IAgent vehicle, final IAgent leadingVehicle) {
		// read-only
	}

	public static void setLeadingVehicle(final IAgent vehicle, final IAgent leadingVehicle) {
		vehicle.setAttribute(LEADING_VEHICLE, leadingVehicle);
	}

	@setter(LEADING_DISTANCE)
	public static void setLeadingDistanceReadOnly(final IAgent vehicle, final double leadingDist) {
		// read-only
	}

	public static void setLeadingDistance(final IAgent vehicle, final double leadingDist) {
		vehicle.setAttribute(LEADING_DISTANCE, leadingDist);
	}

	@setter(LEADING_SPEED)
	public static void setLeadingSpeedReadOnly(final IAgent vehicle, final double leadingSpeed) {
		// read-only
	}

	public static void setLeadingSpeed(final IAgent vehicle, final double leadingSpeed) {
		vehicle.setAttribute(LEADING_SPEED, leadingSpeed);
	}

	@action(
		name = "advanced_follow_driving",
		args = {
			@arg(
				name = "path",
				type = IType.PATH,
				optional = false,
				doc = @doc ("a path to be followed.")
			),
			@arg(
				name = "target",
				type = IType.POINT,
				optional = true,
				doc = @doc ("the target to reach")
			),
			@arg(
				name = IKeyword.SPEED,
				type = IType.FLOAT,
				optional = true,
				doc = @doc ("the speed to use for this move (replaces the current value of speed)")
			),
			@arg(
				name = "time",
				type = IType.FLOAT,
				optional = true,
				doc = @doc ("time to travel")
			)
		},
		doc = @doc(
			value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)",
			returns = "the remaining time",
			examples = { @example ("do osm_follow path: the_path on: road_network;") }
		)
	)
	public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
		// TODO: path is not used anywhere in this action
		Double t = scope.hasArg("time") ? scope.getFloatArg("time") : scope.getClock().getStepInSeconds();
		GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		return moveToNextLocAlongPathOSM(scope, t, path);
	}

	@action(
		name = "is_ready_next_road",
		args = {
			@arg(
				name = "new_road",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road to test")
			),
			@arg(
				name = "lane",
				type = IType.INT,
				optional = false,
				doc = @doc ("the lane to test"))
		},
		doc = @doc(
			value = "action to test if the vehicle can take the given road at the given lane",
			returns = "true (the vehicle can take the road) or false (the vehicle cannot take the road)",
			examples = { @example ("do is_ready_next_road new_road: a_road lane: 0;") }
		)
	)
	public Boolean primIsReadyNextRoad(final IScope scope) throws GamaRuntimeException {
		IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		Integer lane = (Integer) scope.getArg("lane", IType.INT);
		IAgent vehicle = getCurrentAgent(scope);
		double vehicleLength = getVehicleLength(vehicle);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		double probaBlock = getProbaBlockNode(vehicle);
		boolean testBlockNode = Random.opFlip(scope, probaBlock);
		IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		Map<IAgent, List<IAgent>> block = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		List<IAgent> ba = GamaListFactory.create(scope, Types.AGENT, block.keySet());
		for (IAgent dr : ba) {
			if (!dr.getLocation().equals(node.getLocation())) {
				block.remove(dr);
			}
		}
		return isReadyNextRoad(scope, road)
				&& (testBlockNode || DrivingOperators.enoughSpaceToEnterRoad(scope, road, lane, numLanesOccupied, vehicleLength / 2));
	}

	@action(
		name = "test_next_road",
		args = {
			@arg(
				name = "new_road",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road to test")
			)
		},
		doc = @doc(
			value = "action to test if the vehicle can take the given road",
			returns = "true (the vehicle can take the road) or false (the vehicle cannot take the road)",
			examples = { @example ("do test_next_road new_road: a_road;") }
		)
	)
	public Boolean primTestNextRoad(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	/**
	 * Check if the vehicle is ready to cross the intersection to get to a new road, concerning:
	 *     1. Traffic lights
	 *     2. Other vehicles coming from other incoming roads
	 *
	 * @param scope
	 * @param newRoad
	 * @return true if ready, false otherwise
	 *
	 * @throws GamaRuntimeException
	 */
	public Boolean isReadyNextRoad(final IScope scope,
			final IAgent newRoad) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		double vehicleLength = getVehicleLength(vehicle);
		ISpecies context = vehicle.getSpecies();

		// additional conditions to cross the intersection, defined by the user
		IStatement.WithArgs actionTNR = context.getAction("test_next_road");
		Arguments argsTNR = new Arguments();
		argsTNR.put("new_road", ConstantExpressionDescription.create(newRoad));
		actionTNR.setRuntimeArgs(scope, argsTNR);
		if (!(Boolean) actionTNR.executeOn(scope)) { return false; }

		IAgent currentRoad = (IAgent) vehicle.getAttribute(CURRENT_ROAD);
		IAgent sourceNode = (IAgent) newRoad.getAttribute(RoadSkill.SOURCE_NODE);
		// Don't need to do these checks if the vehicle was just initialized
		if (currentRoad != null) {
			// Check traffic lights
			List<List> stops = (List<List>) sourceNode.getAttribute(RoadNodeSkill.STOP);
			// TODO: it is wrong to rescale proba of 1.0 wrt time step
			// List<Double> probasRespectStops = new ArrayList<>();
			// double timeStep = scope.getSimulation().getClock().getStepInSeconds();
			// for (double p : getProbasRespectStops(vehicle)) {
			// 	probasRespectStops.add(rescaleProba(p, timeStep));
			// }
			List<Double> probasRespectStops = getProbasRespectStops(vehicle);
			for (int i = 0; i < stops.size(); i++) {
				Boolean stop = stops.get(i).contains(currentRoad);
				if (stop && (probasRespectStops.size() <= i || Random.opFlip(scope, probasRespectStops.get(i)))) { return false; }
			}

			// Check for vehicles blocking at intersection
			// road node blocking information, which is a map: vehicle -> list of blocked roads
			Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>)
				sourceNode.getAttribute(RoadNodeSkill.BLOCK);
			Collection<IAgent> blockingVehicles = new HashSet<>(blockInfo.keySet());
			// check if any blocking vehicle has moved
			for (IAgent otherVehicle : blockingVehicles) {
				if (!otherVehicle.getLocation().equals(sourceNode.getLocation())) {
					blockInfo.remove(otherVehicle);
				}
			}
			// find if current road is blocked by any vehicle
			for (List<IAgent> blockedRoads : blockInfo.values()) {
				if (blockedRoads.contains(currentRoad)) { return false; }
			}

			// TODO: it is wrong to rescale proba of 1.0 wrt time step
			// double probaRespectPriorities = rescaleProba(getProbaRespectPriorities(vehicle), timeStep);
			double probaRespectPriorities = getProbaRespectPriorities(vehicle);
			if (!Random.opFlip(scope, probaRespectPriorities)) {
				return true;
			}

			// Check for vehicles coming from the rightside road
			Boolean rightSide = getRightSideDriving(vehicle);
			List<IAgent> priorityRoads = (List<IAgent>) sourceNode.getAttribute(RoadNodeSkill.PRIORITY_ROADS);
			boolean onPriorityRoad = priorityRoads != null && priorityRoads.contains(currentRoad);

			// compute angle between the current & next road
			double angleRef = Punctal.angleInDegreesBetween(scope, (GamaPoint) sourceNode.getLocation(),
					(GamaPoint) currentRoad.getLocation(), (GamaPoint) newRoad.getLocation());

			// TODO: adjust the speed diff condition
			double realSpeed = Math.max(0.5, getRealSpeed(vehicle) + getMaxAcceleration(vehicle));
			double safetyDistCoeff = vehicle.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(vehicle)
					: getSecurityDistanceCoeff(vehicle);

			List<IAgent> roadsIn = (List) sourceNode.getAttribute(RoadNodeSkill.ROADS_IN);
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
					List<IAgent> otherVehicles = (List) otherInRoad.getAttribute(RoadSkill.ALL_AGENTS);
					for (IAgent otherVehicle : otherVehicles) {
						if (otherVehicle == null || otherVehicle.dead()) {
							continue;
						}
						double otherVehicleLength = getVehicleLength(otherVehicle);
						double otherRealSpeed = getRealSpeed(otherVehicle);
						double dist = otherVehicle.euclidianDistanceTo(vehicle);

						if (Maths.round(getRealSpeed(otherVehicle), 1) > 0.0 &&
								0.5 + safetyDistCoeff * Math.max(0, realSpeed - otherRealSpeed) >
								dist - (vehicleLength / 2 + otherVehicleLength / 2)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@action(
		name = "compute_path",
		args = {
			@arg(
				name = "graph",
				type = IType.GRAPH,
				optional = false,
				doc = @doc ("the graph representing the road network")
			),
			@arg(
				name = "target",
				type = IType.AGENT,
				optional = true,
				doc = @doc ("the target node to reach")
			),
			@arg(
				name = "source",
				type = IType.AGENT,
				optional = true,
				doc = @doc ("the source node (optional, if not defined, closest node to the agent location)")
			),
			@arg(
				name = "nodes",
				type = IType.LIST,
				optional = true,
				doc = @doc("the nodes forming the resulting path")
			)
		},
		doc = @doc(
			value = "Action to compute the shortest path to the target node, or shortest path based on the provided list of nodes",
			returns = "the computed path, or nil if no valid path is found",
			comment = "either `nodes` or `target` must be specified",
			examples = {
				@example("do compute_path graph: road_network target: target_node;"),
				@example("do compute_path graph: road_network nodes: [node1, node5, node10];")
			}
		)
	)
	public IPath primComputePath(final IScope scope) throws GamaRuntimeException {
		GamaGraph graph = (GamaGraph) scope.getArg("graph", IType.GRAPH);
		IList<IAgent> nodes = (IList) scope.getArg("nodes", IType.LIST);
		IAgent target = (IAgent) scope.getArg("target", IType.AGENT);
		IAgent source = (IAgent) scope.getArg("source", IType.AGENT);

		IAgent vehicle = getCurrentAgent(scope);
		IPath path;
		if (target != null) {
			if (source == null) {
				source = (IAgent) Queries.closest_to(scope, target.getSpecies(), vehicle);
			}
			path = graph.computeShortestPathBetween(scope, source, target);
		} else if (nodes != null && !nodes.isEmpty()) {
			source = nodes.firstValue(scope);
			target = nodes.lastValue(scope);
			IList edges = GamaListFactory.create();
			for (int i = 0; i < nodes.size() - 1; i++) {
				List<Object> interEdges = graph.computeBestRouteBetween(scope, nodes.get(i), nodes.get(i + 1));
				edges.addAll(interEdges);
			}
			path = PathFactory.newInstance(graph, source, target, edges);
		} else {
			throw GamaRuntimeException.error("either `nodes` or `target` must be specified", scope);
		}

		if (path != null && !path.getEdgeGeometry().isEmpty()) {
			List<ILocation> targets = getTargets(vehicle);
			for (int i = 0; i < path.getEdgeGeometry().size(); i += 1) {
				IShape edgeGeom = (IShape) path.getEdgeGeometry().get(i);
				Coordinate[] coords = edgeGeom.getInnerGeometry().getCoordinates();
				if (i == 0) {
					targets.add(new GamaPoint(coords[0]));
				}
				GamaPoint pt = new GamaPoint(coords[coords.length - 1]);
				targets.add(pt);
			}

			vehicle.setLocation(source.getLocation());
			setTargets(vehicle, targets);
			setCurrentIndex(vehicle, -1);
			setCurrentTarget(vehicle, targets.get(0));
			setFinalTarget(vehicle, target.getLocation());
			setCurrentPath(vehicle, path);
			return path;
		} else {
			clearDrivingStates(scope);
			return null;
		}
	}

	@action(
		name = "path_from_nodes",
		args = {
			@arg(
				name = "graph",
				type = IType.GRAPH,
				optional = false,
				doc = @doc("the graph representing the road network")
			),
			@arg(
				name = "nodes",
				type = IType.LIST,
				optional = false,
				doc = @doc("the list of nodes composing the path"))
		},
		doc = @doc(
			value = "action to compute a path from a list of nodes according to a given graph",
			returns = "the computed path, return nil if no path can be taken",
			deprecated = "use compute_path with the facet `nodes` instead",
			examples = { @example ("do compute_path_from_nodes graph: road_network nodes: [node1, node5, node10];") }
		)
	)
	@Deprecated
	public IPath primComputePathFromNodes(final IScope scope) throws GamaRuntimeException {
		GamaGraph graph = (GamaGraph) scope.getArg("graph", IType.GRAPH);
		IList<IAgent> nodes = (IList) scope.getArg("nodes", IType.LIST);

		IAgent vehicle = getCurrentAgent(scope);
		ISpecies context = vehicle.getSpecies();

		IStatement.WithArgs actionComputePath = context.getAction("compute_path");
		Arguments args = new Arguments();
		args.put("graph", ConstantExpressionDescription.create(graph));
		args.put("nodes", ConstantExpressionDescription.create(nodes));
		actionComputePath.setRuntimeArgs(scope, args);

		return (IPath) actionComputePath.executeOn(scope);
	}

	@action(
		name = "drive_random",
		args = {
			@arg(
				name = "graph",
				type = IType.GRAPH,
				optional = false,
				doc = @doc("a graph representing the road network")
			),
			@arg(
				name = "proba_roads",
				type = IType.MAP,
				optional = true,
				doc = @doc("a map containing for each road (key), the probability to be selected as next road (value)")
			)
		},
		doc = @doc(
			value = "action to drive by chosen randomly the next road",
			examples = { @example ("do drive_random init_node: some_node;") }
		)
	)
	public void primDriveRandom(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		ISpecies context = vehicle.getSpecies();
		IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		Arguments argsEF = new Arguments();
		GamaSpatialGraph graph = (GamaSpatialGraph) scope.getArg("graph", IType.GRAPH);
		Map<IAgent, Double> roadProba = (Map) scope.getArg("proba_roads", IType.MAP);
		if (graph == null) {
			throw GamaRuntimeException.error("The parameter `graph` must be set", scope);
		}

		IAgent initNode = null;
		// initialize vehicle's location
		if (getNextRoad(vehicle) == null) {
			IList<IShape> nodes = graph.getVertices();
			IShape shape = Queries.closest_to(scope, nodes, vehicle);
			initNode = shape.getAgent();
			setLocation(vehicle, initNode.getLocation());
			setCurrentTarget(vehicle, initNode.getLocation());
			setNextRoad(vehicle, chooseNextRoadRandomly(scope, graph, initNode, roadProba));
		}

		double timeStep = scope.getSimulation().getClock().getStepInSeconds();
		double remainingTime = timeStep;

		while (true) {
			ILocation loc = vehicle.getLocation();
			GamaPoint target = getCurrentTarget(vehicle);

			if (remainingTime < EPSILON) {
				return;
			} else if (loc.equals(target)) {
				IStatement.WithArgs actionOnNewRoad = context.getAction("on_entering_new_road");
				actionOnNewRoad.executeOn(scope);

				IAgent newRoad = getNextRoad(vehicle);
				if (!isReadyNextRoad(scope, newRoad)) {
					return;
				}

				// Choose a lane on the new road
				GamaPoint firstSegmentEndPt = new GamaPoint(
					newRoad.getInnerGeometry().getCoordinates()[1]
				);
				double firstSegmentLength = loc.euclidianDistanceTo(firstSegmentEndPt);
				Pair<Integer, Double> pair = chooseLaneMOBIL(scope, newRoad, 0, firstSegmentLength);
				if (pair == null) {
					return;
				}
				double newAccel = pair.getValue();
				double newSpeed = computeSpeed(scope, newAccel, newRoad);
				// Check if it is possible to move onto the new road
				if (newSpeed == 0.0) {
					// TODO: this should only happen once
					// double probaBlock = rescaleProba(getProbaBlockNode(vehicle), timeStep);
					double probaBlock = getProbaBlockNode(vehicle);
					boolean goingToBlock = Random.opFlip(scope, probaBlock);
					IAgent currentRoad = getCurrentRoad(vehicle);
					if (currentRoad != null && goingToBlock) {
						IAgent sourceNode = RoadSkill.getSourceNode(newRoad);
						blockIntersection(scope, currentRoad, newRoad, sourceNode);
					}
					return;
				}
				int newLane = pair.getKey();

				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);

				setCurrentTarget(vehicle, RoadSkill.getTargetNode(newRoad).getLocation());
				RoadSkill.unregister(scope, vehicle);
				RoadSkill.register(scope, vehicle, newRoad, newLane);
				// Choose the next road in advance
				IAgent nextRoad = chooseNextRoadRandomly(scope, graph, RoadSkill.getTargetNode(newRoad), roadProba);
				setNextRoad(vehicle, nextRoad);
			} else {
				remainingTime = moveToNextLocAlongPathOSM(scope, remainingTime, null);
			}
		}
	}

	/**
	 * Select a random road among the outward edges of a given intersection node
	 *
	 * @param scope
	 * @param graph     the graph on which the vehicle is driving
	 * @param node      the intersection node whose outward edges will be considered
	 * @param roadProba a map that specifies probabilities of choosing certain roads
	 * @return the selected road
	 */
	private IAgent chooseNextRoadRandomly(final IScope scope,
			final GamaSpatialGraph graph,
			final IAgent node,
			final Map<IAgent, Double> roadProba) {
		List<IAgent> possibleRoads = RoadNodeSkill.getRoadsOut(node);
		// Only consider roads in the specified graph
		List<IAgent> filteredRoads = new ArrayList<>();
		for (IAgent road : possibleRoads) {
			if (graph.getEdges().contains(road)) {
				filteredRoads.add(road);
			}
		}

		if (filteredRoads.isEmpty()) {
			return null;
		} else if (filteredRoads.size() == 1) {
			return filteredRoads.get(0);
		} else {
			if (roadProba == null || roadProba.isEmpty()) {
				return filteredRoads.get(scope.getRandom().between(0, filteredRoads.size() - 1));
			} else {
				IList<Double> distribution = GamaListFactory.create(Types.FLOAT);
				for (IAgent r : filteredRoads) {
					Double val = roadProba.get(r);
					distribution.add(val == null ? 0.0 : val);
				}
				return filteredRoads.get(Random.opRndChoice(scope, distribution));
			}
		}
	}

	@action(
		name = "drive",
		doc = @doc(
			value = "action to drive toward the target",
			examples = { @example ("do drive;") }
		)
	)
	public void primDrive(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		if (vehicle == null || vehicle.dead()) return;

		GamaPoint finalTarget = getFinalTarget(vehicle);
		if (finalTarget == null) { return; }
		IPath path = getCurrentPath(vehicle);

		// some preparations to execute other actions during the loop
		ISpecies context = vehicle.getSpecies();
		IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		Arguments argsEF = new Arguments();

		// Initialize the first road
		if (getCurrentIndex(vehicle) == -1) {
			setNextRoad(vehicle, (IAgent) path.getEdgeList().get(0));
		}

		double timeStep = scope.getSimulation().getClock().getStepInSeconds();
		double remainingTime = timeStep;
		// main loop to move the agent until the simulation step ends
		while (true) {
			ILocation loc = vehicle.getLocation();
			GamaPoint target = getCurrentTarget(vehicle);
			int currentEdgeIdx = getCurrentIndex(vehicle);

			if (remainingTime < EPSILON) {
				return;
			} else if (loc.equals(finalTarget)) {  // Final node in path
				clearDrivingStates(scope);
				return;
			} else if (loc.equals(target)) {  // Intermediate node in path
				IStatement.WithArgs actionOnNewRoad = context.getAction("on_entering_new_road");
				actionOnNewRoad.executeOn(scope);

				// get next road in path
				IAgent newRoad = getNextRoad(vehicle);

				// check traffic lights and vehicles coming from other roads
				if (!isReadyNextRoad(scope, newRoad)) {
					return;
				}

				// Choose a lane on the new road
				GamaPoint firstSegmentEndPt = new GamaPoint(
					newRoad.getInnerGeometry().getCoordinates()[1]
				);
				double firstSegmentLength = loc.euclidianDistanceTo(firstSegmentEndPt);
				Pair<Integer, Double> pair = chooseLaneMOBIL(scope, newRoad, 0, firstSegmentLength);
				if (pair == null) {
					return;
				}
				double newAccel = pair.getValue();
				double newSpeed = computeSpeed(scope, newAccel, newRoad);
				// Check if it is possible to move onto the new road
				if (newSpeed == 0.0) {
					// TODO: this should happen once
					// double probaBlock = rescaleProba(getProbaBlockNode(vehicle), timeStep);
					double probaBlock = getProbaBlockNode(vehicle);
					boolean goingToBlock = Random.opFlip(scope, probaBlock);
					IAgent currentRoad = getCurrentRoad(vehicle);
					if (currentRoad != null && goingToBlock) {
						IAgent sourceNode = RoadSkill.getSourceNode(newRoad);
						blockIntersection(scope, currentRoad, newRoad, sourceNode);
					}
					return;
				}
				int newLane = pair.getKey();

				// external factor that affects remaining time when entering a new road
				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);

				currentEdgeIdx += 1;
				setCurrentIndex(vehicle, currentEdgeIdx);
				setCurrentTarget(vehicle, getTargets(vehicle).get(currentEdgeIdx + 1));
				RoadSkill.unregister(scope, vehicle);
				RoadSkill.register(scope, vehicle, newRoad, newLane);
				if (currentEdgeIdx < path.getEdgeList().size() - 1) {
					setNextRoad(vehicle, (IAgent) path.getEdgeList().get(currentEdgeIdx + 1));
				} else {
					setNextRoad(vehicle, null);
				}
			} else {
				remainingTime = moveToNextLocAlongPathOSM(scope, remainingTime, path);
			}
		}
	}

	@action(
		name = "on_entering_new_road",
		doc = @doc("override this if you want to do something when the vehicle enters a new road (e.g. adjust parameters)")
	)
	public void primOnEnteringNewRoad(final IScope scope) throws GamaRuntimeException {
		// user-defined
	}

	@action(
		name = "external_factor_impact",
		args = {
			@arg(
				name = "new_road",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road on which to the vehicle wants to go")
			),
			@arg(
				name = "remaining_time",
				type = IType.FLOAT,
				optional = false,
				doc = @doc ("the remaining time")
			)
		},
		doc = @doc(
			value = "action that allows to define how the remaining time is impacted by external factor",
			returns = "the remaining time",
			examples = { @example ("do external_factor_impact new_road: a_road remaining_time: 0.5;") }
		)
	)
	public Double primExternalFactorOnRemainingTime(final IScope scope) throws GamaRuntimeException {
		return scope.getFloatArg("remaining_time");
	}

	@action(
		name = "speed_choice",
		args = {
			@arg(
				name = "new_road",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road on which to choose the speed")
			)
		},
		doc = @doc(
			value = "action to choose a speed",
			returns = "the chosen speed",
			examples = { @example ("do speed_choice new_road: the_road;") },
			deprecated = "You can't override this action anymore since speed computation is now based on Intelligent Vehicle Model"
		)
	)
	@Deprecated
	public Double primSpeedChoice(final IScope scope) throws GamaRuntimeException {
		return 0.0;
	}

	@action(
		name = "lane_choice",
		args = {
			@arg(
				name = "new_road",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road on which to choose the lane")
			)
		},
		doc = @doc(
			value = "action to choose a lane",
			returns = "the chosen lane, return -1 if no lane can be taken",
			examples = { @example ("do lane_choice new_road: a_road;") },
			deprecated = "You can't override this action anymore since lane choice is now based on the MOBIL lane changing model"
		)
	)
	@Deprecated
	public Integer primLaneChoice(final IScope scope) throws GamaRuntimeException {
		return -1;
	}

	/**
	 * Blocks vehicles crossing a given intersection from other roads, except the
	 * old road and the linked road of the new one. e.g. think of a vehicle running
	 * a redlight at a four-way junction
	 *
	 * @param scope
	 * @param currentRoad the old road of the vehicle
	 * @param newRoad     the new road of the vehicle
	 * @param node        the intersection node whose blocking info will be updated
	 */
	public void blockIntersection(final IScope scope, final IAgent currentRoad,
			final IAgent newRoad, final IAgent node) {
		List<IAgent> inRoads = (List<IAgent>) node.getAttribute(RoadNodeSkill.ROADS_IN);
		if (inRoads.size() <= 1) {
			return;
		}
		// list of the roads that will be blocked by this vehicle
		List<IAgent> blockedRoads = GamaListFactory.create(Types.AGENT);
		for (IAgent road : inRoads) {
			// do not block the linked road associated to the new road
			if (!road.getLocation().equals(newRoad.getLocation())) {
				blockedRoads.add(road);
			}
		}
		if (!blockedRoads.isEmpty()) {
			IAgent vehicle = getCurrentAgent(scope);
			Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
			blockInfo.put(vehicle, blockedRoads);
		}
	}

	/**
	 * Updates the `agents_on` list of the corresponding roads if the vehicle has
	 * switched to new lanes and/or a new segment.
	 *
	 * @param scope
	 * @param newLowestLane the new lane will the lowest index that the vehicle
	 *                      occupies
	 * @param newSegment    the new segment index
	 */
	private void updateLaneSegment(final IScope scope, final int newLowestLane,
			final int newSegment) {
		IAgent vehicle = getCurrentAgent(scope);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		int currentLowestLane = getLowestLane(vehicle);
		int currentSegment = getSegmentIndex(vehicle);

		// Get all occupying lanes using `lowest_lane` and `num_lanes_occupied`
		Set<Integer> oldLanes = IntStream.range(currentLowestLane, currentLowestLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));
		Set<Integer> newLanes = IntStream.range(newLowestLane, newLowestLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));

		// Small optimization to not touch the lists associated with unchanged lanes
		Set<Integer> lanesToRemove, lanesToAdd;
		if (newSegment != currentSegment) {
			// if entering new segment, we have to update the lists for all related lanes and segments
			lanesToRemove = oldLanes;
			lanesToAdd = newLanes;
		} else {
			// otherwise the vehicle is still in the same segment, so we only update (possibly few) relevant lists
			lanesToRemove = Sets.difference(oldLanes, newLanes);
			lanesToAdd = Sets.difference(newLanes, oldLanes);
		}

		IAgent correctRoad = getCurrentRoad(vehicle);
		for (int lane : lanesToRemove) {
			List<IAgent> oldVehicleList = RoadSkill.getVehiclesOnLaneSegment(
				scope, correctRoad, lane, currentSegment);
			oldVehicleList.remove(vehicle);
		}
		for (int lane : lanesToAdd) {
			List<IAgent> newVehicleList = RoadSkill.getVehiclesOnLaneSegment(
				scope, correctRoad, lane, newSegment);
			newVehicleList.add(vehicle);
		}

		setLowestLane(vehicle, newLowestLane);
		setSegmentIndex(vehicle, newSegment);
	}

	/**
	 * Moves the vehicle from segment to segment on the current road.
	 *
	 * @param scope
	 * @param remainingTime the remaining time in a simulation step
	 * @param path          no idea what it does for now
	 * @return the time that the vehicle spent moving along segments
	 */
	private double moveToNextLocAlongPathOSM(final IScope scope,
			final double remainingTime, IPath path) {
		IAgent vehicle = getCurrentAgent(scope);
		IAgent currentRoad = getCurrentRoad(vehicle);
		GamaPoint currentLocation = (GamaPoint) vehicle.getLocation().copy(scope);

		int initSegment = getSegmentIndex(vehicle);
		int numSegments = RoadSkill.getNumSegments(currentRoad);

		Coordinate coords[] = currentRoad.getInnerGeometry().getCoordinates();
		int prevSegment = -1;
		int currentSegment = initSegment;
		GamaPoint segmentEndPt = new GamaPoint(coords[currentSegment + 1]);
		double distToGoal = getDistanceToGoal(vehicle);
		boolean atSegmentEnd = false;

		double time = remainingTime;
		while (time > 0.0) {
			// Due to approximations in IDM, distToGoal will never be exactly 0
			if (distToGoal < EPSILON) {
				// the vehicle is at the end of the segment
				atSegmentEnd = true;
				currentLocation = segmentEndPt;
				if (currentSegment < numSegments - 1) {
					// try moving to the next segment
					prevSegment = currentSegment;
					currentSegment += 1;
					segmentEndPt = new GamaPoint(coords[currentSegment + 1]);
					distToGoal = currentLocation.distance(segmentEndPt);
				} else {
					// at the end of the final segment
					distToGoal = 0.0;
					break;
				}
			}

			double oldSpeed = getRealSpeed(vehicle);

			// Choose an optimal lane
			Pair<Integer, Double> pair = chooseLaneMOBIL(scope, currentRoad, currentSegment, distToGoal);
			int lowestLane = pair.getKey();
			double accel = pair.getValue();
			double speed = computeSpeed(scope, accel, currentRoad);

			setAcceleration(vehicle, accel);
			setRealSpeed(vehicle, speed);

			// TODO: this loop is confusing AF!!!
			// refactor so that speed is computed only once during a simulation step,
			// then loop over next segment/road if distMoved > distToGoal
			double distMoved;
			if (speed == 0.0) {
				// Edge case when there is a stopped vehicle or traffic light
				distMoved = -0.5 * Math.pow(oldSpeed, 2) / accel;
			} else {
				distMoved = speed * time;
			}

			double oldDistToGoal = distToGoal;
			distToGoal -= distMoved;
			if (distMoved < oldDistToGoal) {
				double ratio = distMoved / oldDistToGoal;
				double newX = currentLocation.getX() + ratio * (segmentEndPt.getX() - currentLocation.getX());
				double newY = currentLocation.getY() + ratio * (segmentEndPt.getY() - currentLocation.getY());
				GamaPoint newLocation = new GamaPoint(newX, newY);
				currentLocation.setLocation(newLocation);
				updateLaneSegment(scope, lowestLane, currentSegment);
				time = 0.0;
				break;
			} else {
				time -= oldDistToGoal / speed;
			}
		}
		setLocation(vehicle, currentLocation);
		// NOTE: This check handles the edge case where the vehicle is at a segment endpoint.
		// distanceToGoal is only updated when the vehicle has moved successfully to a new segment,
		// i.e its segment index has been updated by updateLaneSegment().
		if (atSegmentEnd && prevSegment == getSegmentIndex(vehicle)) {
			setDistanceToGoal(vehicle, 0.0);
		} else {
			setDistanceToGoal(vehicle, distToGoal);
		}
		if (path != null) {
			path.setSource(currentLocation.copy(scope));
		}

		return time;
	}

	/**
	 * Attempts to make lane changing probabilities timestep-agnostic
	 *
	 * @param probaInOneSecond a probability with respect to one second
	 * @param timeStep         the duration of a simulation step
	 * @return the rescaled probability
	 */
	private double rescaleProba(final double probaInOneSecond,
			final double timeStep) {
		return Math.min(probaInOneSecond * timeStep, 1.0);
	}

	/**
	 * Choose a new lane according to the lane change model MOBIL
	 * (https://traffic-simulation.de/info/info_MOBIL.html).
	 *
	 * @param scope
	 * @param road             the road which the vehicle is moving on
	 * @param segment          the index of the current road segment
	 * @param distToSegmentEnd the distance to the endpoint of the segment
	 * @return a pair composed of the optimal "lowest" lane index and the
	 *         acceleration of the vehicle if it moves on the lanes specified by
	 *         that lowest lane index and the number of lanes occupied by the
	 *         vehicle
	 */
	private ImmutablePair<Integer, Double> chooseLaneMOBIL(final IScope scope,
			final IAgent road,
			final int segment,
			final double distToSegmentEnd) {
		IAgent vehicle = getCurrentAgent(scope);
		double VL = getVehicleLength(vehicle);

		// Rescale probabilities based on step duration
		double timeStep = scope.getSimulation().getClock().getStepInSeconds();
		Double probaChangeLaneUp = rescaleProba(getProbaLaneChangeUp(vehicle), timeStep);
		Double probaChangeLaneDown = rescaleProba(getProbaLaneChangeDown(vehicle), timeStep);
		Double probaUseLinkedRoad = rescaleProba(getProbaUseLinkedRoad(vehicle), timeStep);

		IAgent linkedRoad = RoadSkill.getLinkedRoad(road);
		int numCurrentLanes = (Integer) road.getAttribute(RoadSkill.LANES);
		int numLinkedLanes = (linkedRoad != null) ? (int) linkedRoad.getAttribute(RoadSkill.LANES) : 0;
		int linkedLaneLimit = getLinkedLaneLimit(vehicle);
		linkedLaneLimit = (linkedLaneLimit != -1 && numLinkedLanes > linkedLaneLimit) ?
				linkedLaneLimit : numLinkedLanes;
		List<Integer> allowedLanes = getAllowedLanes(vehicle);

		Range<Integer> limitedLaneRange;
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		int lowestLane = getLowestLane(vehicle);
		// Restrict the lane index when entering a new road
		lowestLane = Math.min(lowestLane,
				numCurrentLanes + linkedLaneLimit - numLanesOccupied);
		int laneChangeLimit = getLaneChangeLimit(vehicle);
		if (laneChangeLimit == -1) {
			// can change to all available lanes
			limitedLaneRange = Range.closed(0, numCurrentLanes + linkedLaneLimit - numLanesOccupied);
		} else {
			limitedLaneRange = Range.closed(lowestLane - laneChangeLimit, lowestLane + laneChangeLimit);
		}
		List<Integer> allLanes = IntStream.rangeClosed(0, numCurrentLanes + linkedLaneLimit - numLanesOccupied)
				.boxed().collect(Collectors.toCollection(ArrayList::new));
		if (isLaneChangePriorityRandomized(vehicle)) {
			Collections.shuffle(allLanes);
		}

		ImmutablePair<Triple<IAgent, Double, Boolean>, Triple<IAgent, Double, Boolean>> pair =
			findLeadingAndBackVehicle(scope, road, segment, distToSegmentEnd, lowestLane);
		IAgent currentBackVehicle = null;
		double stayAccelM;
		if (pair == null) {
			stayAccelM = -Double.MAX_VALUE;
		} else {
			if (pair.getValue() != null) {
				currentBackVehicle = pair.getValue().getLeft();
			}
			// Find the leading vehicle on current lanes
			IAgent leadingVehicle = pair.getKey().getLeft();
			double leadingDist = pair.getKey().getMiddle();
			boolean leadingSameDirection = pair.getKey().getRight();
			double leadingSpeed;
			// TODO: embed this into getter?
			if (leadingVehicle == null ||
					!leadingVehicle.getSpecies().implementsSkill(ADVANCED_DRIVING)) {
				leadingSpeed = 0.0;
			} else {
				leadingSpeed = getRealSpeed(leadingVehicle);
				leadingSpeed = leadingSameDirection ? leadingSpeed : -leadingSpeed;
			}
			setLeadingVehicle(vehicle, leadingVehicle);
			setLeadingDistance(vehicle, leadingDist);
			setLeadingSpeed(vehicle, leadingSpeed);
			// Calculate acc(M) - Acceleration of current vehicle M if no lane change occurs
			stayAccelM = computeAccelerationIDM(scope, vehicle, leadingDist, leadingSpeed);
			// Do not allow changing lane when approaching intersections
			// Reason: in some cases the vehicle is forced to slow down (e.g. approaching final target in path),
			// but it can gain acceleration by switching lanes to follow a fast vehicle.
			if (leadingVehicle != null &&
					leadingVehicle.getSpecies().implementsSkill(RoadNodeSkill.ROAD_NODE)) {
				return ImmutablePair.of(lowestLane, stayAccelM);
			}
		}

		// Examine all lanes within range
		for (int tmpLowestLane : allLanes) {
			if (tmpLowestLane == lowestLane ||
					!limitedLaneRange.contains(tmpLowestLane) ||
					(!allowedLanes.isEmpty() && !allowedLanes.contains(tmpLowestLane))) {
				continue;
			}

			// Evaluate probabilities to switch to tmpLowestLane
			boolean canChangeDown = tmpLowestLane >= 0 && tmpLowestLane < lowestLane &&
					scope.getRandom().next() < probaChangeLaneDown;
			// NOTE: in canChangeUp, the 2nd condition prevents moving from current road to linked road
			boolean canChangeUp = tmpLowestLane > lowestLane &&
					 !(lowestLane <= numCurrentLanes - numLanesOccupied && tmpLowestLane > numCurrentLanes - numLanesOccupied) &&
					 scope.getRandom().next() < probaChangeLaneUp;
			boolean canChangeToLinkedRoad = linkedRoad != null && linkedLaneLimit > 0 &&
					lowestLane <= numCurrentLanes - numLanesOccupied &&
					tmpLowestLane > numCurrentLanes - numLanesOccupied &&
					scope.getRandom().next() < probaUseLinkedRoad;
			if (!canChangeDown && !canChangeUp && !canChangeToLinkedRoad) {
				continue;
			}

			pair = findLeadingAndBackVehicle(scope, road, segment, distToSegmentEnd, tmpLowestLane);
			if (pair == null) {
				// Will crash into another vehicle if change
				continue;
			}

			// Find the leading vehicle of M on this new lane
			Triple<IAgent, Double, Boolean> leadingTriple = pair.getKey();
			IAgent leadingVehicle = leadingTriple.getLeft();
			double leadingDist = leadingTriple.getMiddle();
			boolean leadingSameDirection = leadingTriple.getRight();
			double leadingSpeed;
			// TODO: embed this into getter?
			if (leadingVehicle == null ||
					!leadingVehicle.getSpecies().implementsSkill(ADVANCED_DRIVING)) {
				leadingSpeed = 0.0;
			} else {
				leadingSpeed = getRealSpeed(leadingVehicle);
				leadingSpeed = leadingSameDirection ? leadingSpeed : -leadingSpeed;
			}
			// Calculate acc'(M) - acceleration of M on new lane
			double changeAccelM = computeAccelerationIDM(scope, vehicle, leadingDist, leadingSpeed);

			// Find back vehicle B' on new lane
			double stayAccelB;
			double changeAccelB;
			Triple<IAgent, Double, Boolean> backTriple = pair.getValue();
			if (backTriple == null || !backTriple.getRight() ||
					backTriple.getLeft() == currentBackVehicle ||
					getLeadingVehicle(backTriple.getLeft()) != vehicle) {
				// IF no back vehicle OR back vehicle is moving backwards OR
				// back vehicle on new lanes is the same one on old lanes OR
				// back vehicle's leading vehicle is not the current vehicle
				// THEN there is no acceleration change of B
				stayAccelB = 0;
				changeAccelB = 0;
			} else {
				IAgent backVehicle = backTriple.getLeft();
				double backDist = backTriple.getMiddle();
				// Calculate acc(B') - acceleration of B' if M does not change to this lane
				// NOTE: in this case, the leading vehicle is the one we have found above for M
				stayAccelB = computeAccelerationIDM(scope, backVehicle, backDist + VL + leadingDist, leadingSpeed);
				// Calculate acc'(B') - acceleration of B' if M changes to this lane
				// NOTE: in this case, M is the new leading vehicle of B'
				changeAccelB = computeAccelerationIDM(scope, backVehicle, backDist, getRealSpeed(vehicle));
			}

			double step = scope.getSimulation().getClock().getStepInSeconds();
			// MOBIL params
			double p = getPolitenessFactor(vehicle);
			double bSave = getMaxSafeDeceleration(vehicle) * step;
			double aThr = getAccGainThreshold(vehicle) * step;

			// Safety criterion & Incentive criterion
			if (changeAccelB > -bSave &&
					changeAccelM - stayAccelM >= p * (stayAccelB - changeAccelB) + aThr) {
				setLeadingVehicle(vehicle, leadingVehicle);
				setLeadingDistance(vehicle, leadingDist);
				setLeadingSpeed(vehicle, leadingSpeed);
				return ImmutablePair.of(tmpLowestLane, changeAccelM);
			}
		}

		// If no other lane satisfies the MOBIL criterions, stay on the same lane
		return ImmutablePair.of(lowestLane, stayAccelM);
	}

	/**
	 * Computes the acceleration according to the Intelligent Driver Model
	 * (https://traffic-simulation.de/info/info_IDM.html)
	 *
	 * @param scope
	 * @param vehicle      the vehicle whose acceleration will be computed
	 * @param leadingDist  the bumper-to-bumper gap with its leading vehicle
	 * @param leadingSpeed the speed of the leading vehicle
	 * @return the resulting acceleration (deceleration if it is < 0)
	 */
	private double computeAccelerationIDM(final IScope scope,
			final IAgent vehicle,
			final double leadingDist,
			final double leadingSpeed) {
		// IDM params
		double T = getMaxTimeHeadway(vehicle);
		double a = getMaxAcceleration(vehicle);
		double b = getMaxDeceleration(vehicle);
		double v0 = getMaxSpeed(vehicle);
		double s0 = getMinSafetyDistance(vehicle);
		double delta = getDeltaIDM(vehicle);

		double s = leadingDist;
		double v = getRealSpeed(vehicle);
		double dv = v - leadingSpeed;

		double sStar = s0 + Math.max(0, v * T + v * dv / 2 / Math.sqrt(a * b));
		double accel = a * (1 - Math.pow(v / v0, delta) - Math.pow(sStar / s, 2));

		double dt = scope.getSimulation().getClock().getStepInSeconds();
		return accel * dt;
	}

	/**
	 * Computes the speed of the vehicle with respect to its acceleration, its
	 * maximum speed and the speed limit of the current road.
	 *
	 * @param scope
	 * @param acceleration the acceleration for this simulation step
	 * @param road         the road which the vehicle is on
	 * @return the resulting speed
	 */
	private double computeSpeed(final IScope scope,
			final double acceleration,
			final IAgent road) {
		IAgent vehicle = getCurrentAgent(scope);

		double speed = getRealSpeed(vehicle) + acceleration;
		speed = Math.min(speed, getSpeedCoeff(vehicle) * RoadSkill.getMaxSpeed(road));
		speed = Math.min(speed, getMaxSpeed(vehicle));
		speed = Math.max(0.0, speed);
		return speed;
	}

	/**
	 * Find the leading vehicle (closest vehicle ahead) and the back vehicle
	 * (closest vehicle behind) which are moving on the same lanes as the current
	 * vehicle.
	 *
	 * For each of the above vehicle, the method returns a triplet containing:
	 *    1. The vehicle agent itself
	 *    2. The bumper-to-bumper gap between that vehicle and the current vehicle
	 *    3. Whether that vehicle is moving in the same direction
	 *
	 * If no leading vehicle is found on the current segment,
	 * it tries to find one in the next segment or the first segment of the next road.
	 * If none is found still, the bumper-to-bumper gap is set to a big value (to eliminate the deceleration term in IDM).
	 *
	 * On the other hand, if no back vehicle is found on the current segment,
	 * the method does NOT consider the previous segment or the last segment of the previous road.
	 * (a possible TODO?)
	 *
	 * If either the leading or back vehicle overlap the current one, the return value will be just null.
	 *
	 * @param scope
	 * @param road             the road which the vehicle is moving on
	 * @param segment          the index of the current road segment
	 * @param distToSegmentEnd the distance from the vehicle to the segment endpoint
	 * @param lowestLane       the current "lowest" lane index of the vehicle
	 * @return a pair containing two triplets, one for the leading vehicle and one
	 *         for the back vehicle
	 */
	private ImmutablePair<Triple<IAgent, Double, Boolean>, Triple<IAgent, Double, Boolean>>
			findLeadingAndBackVehicle(final IScope scope,
										final IAgent road,
										final int segment,
										final double distToSegmentEnd,
										final int lowestLane) {
		IAgent vehicle = getCurrentAgent(scope);
		double vL = getVehicleLength(vehicle);
		double minSafetyDist = getMinSafetyDistance(vehicle);

		GamaPoint segmentEndPt = new GamaPoint(road.getInnerGeometry().getCoordinates()[segment + 1]);

		int numLanesOccupied = getNumLanesOccupied(vehicle);
		Set<IAgent> neighbors = new HashSet<>();
		for (int i = 0; i < numLanesOccupied; i += 1) {
			neighbors.addAll(
				RoadSkill.getVehiclesOnLaneSegment(scope, road, lowestLane + i, segment)
			);
		}

		// finding the closest vehicle ahead & behind
		IAgent leadingVehicle = null;
		double minLeadingDist = Double.MAX_VALUE;
		boolean leadingSameDirection = false;
		IAgent backVehicle = null;
		double minBackDist = Double.MAX_VALUE;
		boolean backSameDirection = false;
		Triple<IAgent, Double, Boolean> leadingTriple;
		Triple<IAgent, Double, Boolean> backTriple;

		for (IAgent otherVehicle : neighbors) {
			if (otherVehicle == vehicle || otherVehicle == null || otherVehicle.dead()) {
				continue;
			}
			double otherVL = getVehicleLength(otherVehicle);
			double otherDistToSegmentEnd;
			if (road == getCurrentRoad(otherVehicle)) {
				otherDistToSegmentEnd = getDistanceToGoal(otherVehicle);
			} else {
				GamaPoint otherLocation = (GamaPoint) otherVehicle.getLocation();
				otherDistToSegmentEnd = otherLocation.distance(segmentEndPt);
			}

			// Calculate bumper-to-bumper distances
			double otherFrontToMyRear = otherDistToSegmentEnd - 0.5 * otherVL - (distToSegmentEnd + 0.5 * vL);
			double myFrontToOtherRear = distToSegmentEnd - 0.5 * vL - (otherDistToSegmentEnd + 0.5 * otherVL);

			if ((otherFrontToMyRear <= 0 && -otherFrontToMyRear < vL) ||
					(myFrontToOtherRear <= 0 && -myFrontToOtherRear < otherVL)) {
				// Overlap with another vehicle
				return null;
			} else if (myFrontToOtherRear > 0 && myFrontToOtherRear < minLeadingDist) {
				leadingVehicle = otherVehicle;
				minLeadingDist = myFrontToOtherRear;
				leadingSameDirection = road == getCurrentRoad(otherVehicle);
			} else if (otherFrontToMyRear > 0 && otherFrontToMyRear < minBackDist) {
				backVehicle = otherVehicle;
				minBackDist = otherFrontToMyRear;
				backSameDirection = road == getCurrentRoad(otherVehicle);
			}
		}

		// We don't need to look further behind to find a back vehicle for now
		if (backVehicle == null) {
			backTriple = null;
		} else {
			backTriple = ImmutableTriple.of(backVehicle, minBackDist, backSameDirection);
		}

		// No leading vehicle is found on the current segment
		if (leadingVehicle == null) {
			IAgent nextRoad = getNextRoad(vehicle);
			// Check if vehicle is approaching an intersection
			int numSegments = RoadSkill.getNumSegments(road);
			if (segment == numSegments - 1) {
				// Return a virtual leading vehicle of length 0 to simulate deceleration at intersections
				// NOTE: the added minSafetyDist is necessary for the vehicle to ignore the safety dist when stopping at an endpoint
				IAgent stoppingNode = RoadSkill.getTargetNode(road);
				leadingTriple = ImmutableTriple.of(stoppingNode, distToSegmentEnd + minSafetyDist, false);
				// Slowing down at final target, since at this point we don't know which road will be taken next
				if (nextRoad == null) {
					return ImmutablePair.of(leadingTriple, backTriple);
				// Might need to slow down at the intersection if it is not possible to enter the next road
				} else {
					if (!isReadyNextRoad(scope, nextRoad)) {
						return ImmutablePair.of(leadingTriple, backTriple);
					}
				}
			}

			// Continue to find leading vehicle on next segment or next road in path
			minLeadingDist = distToSegmentEnd - 0.5 * vL;
			IAgent roadToCheck;
			int lowestLaneToCheck;
			int segmentToCheck;
			if (segment < numSegments - 1) {
				roadToCheck = road;
				segmentToCheck = segment + 1;
				lowestLaneToCheck = lowestLane;
			} else {
				roadToCheck = nextRoad;
				segmentToCheck = 0;
				// TODO: is this the right lane to check?
				IAgent linkedRoadToCheck = RoadSkill.getLinkedRoad(roadToCheck);
				lowestLaneToCheck = Math.min(lowestLane,
						RoadSkill.getNumLanes(roadToCheck) + RoadSkill.getNumLanes(linkedRoadToCheck) - numLanesOccupied);
			}
			Set<IAgent> furtherVehicles = new HashSet<>();
			for (int i = 0; i < numLanesOccupied; i += 1) {
				furtherVehicles.addAll(
					RoadSkill.getVehiclesOnLaneSegment(scope,
						roadToCheck, lowestLaneToCheck + i, segmentToCheck)
				);
			}

			double minGap = Double.MAX_VALUE;
			for (IAgent otherVehicle : furtherVehicles) {
				if (otherVehicle == vehicle || otherVehicle == null || otherVehicle.dead()) {
					continue;
				}
				// check if the other vehicle going in opposite direction
				double gap;
				boolean sameDirection = getCurrentRoad(otherVehicle) == roadToCheck;
				if (sameDirection) {
					Coordinate coords[] = roadToCheck.getInnerGeometry().getCoordinates();
					double segmentLength = coords[segmentToCheck].distance(coords[segmentToCheck + 1]);
					gap = segmentLength - getDistanceToGoal(otherVehicle);
				} else {
					gap = getDistanceToGoal(otherVehicle);
				}
				gap -= 0.5 * getVehicleLength(otherVehicle);

				if (gap < minGap) {
					leadingVehicle = otherVehicle;
					minGap = gap;
					leadingSameDirection = sameDirection;
				}
			}
			minLeadingDist += minGap;
		}

		if (leadingVehicle == null || leadingVehicle.dead()) {
			// the road ahead seems to be completely clear
			leadingTriple = ImmutableTriple.of(null, 1e6, false);
			return ImmutablePair.of(leadingTriple, backTriple);
		} else {
			// Found a leading vehicle on the next segment/next road
			leadingTriple = ImmutableTriple.of(leadingVehicle, minLeadingDist, leadingSameDirection);
			return ImmutablePair.of(leadingTriple, backTriple);
		}
	}

	/**
	 * Clears information after the vehicle has reached the final target in its path
	 *
	 * @param scope
	 */
	private void clearDrivingStates(final IScope scope) {
		IAgent vehicle = getCurrentAgent(scope);
		getTargets(vehicle).clear();
		setCurrentIndex(vehicle, -1);
		setCurrentTarget(vehicle, null);
		setFinalTarget(vehicle, null);
		setCurrentPath(vehicle, null);
	}

	@action(
		name = "die",
		doc = @doc(
			value = "remove the driving agent from its current road and make it die",
			examples = { @example("do die") }
		)
	)
	public void primDieWrapper(final IScope scope) throws GamaRuntimeException {
		AbstractAgent vehicle = (AbstractAgent) getCurrentAgent(scope);
		if (!vehicle.dead() && getCurrentRoad(vehicle) != null) {
			RoadSkill.unregister(scope, vehicle);
		}
		vehicle.primDie(scope);
	}
}
