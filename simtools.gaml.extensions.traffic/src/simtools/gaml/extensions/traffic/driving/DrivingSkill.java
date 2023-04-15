/*******************************************************************************************************
 *
 * DrivingSkill.java, in simtools.gaml.extensions.traffic, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import simtools.gaml.extensions.traffic.driving.carfollowing.MOBIL;
import simtools.gaml.extensions.traffic.driving.carfollowing.Utils;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class DrivingSkill.
 */
@vars ({ @variable (
		name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc ("the speed of the agent (in meter/second)")),
		@variable (
				name = IKeyword.REAL_SPEED,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc (
						value = "the actual speed of the agent (in meter/second)",
						deprecated = "there is now only one speed value, which is located in `speed`")),
		@variable (
				name = DrivingSkill.ACCELERATION,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the current acceleration of the vehicle (in m/s^2)")),
		@variable (
				name = DrivingSkill.CURRENT_PATH,
				type = IType.PATH,
				init = "nil",
				doc = @doc ("the path which the agent is currently following")),
		@variable (
				name = DrivingSkill.FINAL_TARGET,
				type = IType.AGENT,
				init = "nil",
				doc = @doc ("the final target of the agent")),
		@variable (
				name = DrivingSkill.CURRENT_TARGET,
				type = IType.AGENT,
				init = "nil",
				doc = @doc ("the current target of the agent")),
		@variable (
				name = DrivingSkill.CURRENT_INDEX,
				type = IType.INT,
				init = "0",
				doc = @doc ("the index of the current edge (road) in the path")),
		@variable (
				name = DrivingSkill.TARGETS,
				type = IType.LIST,
				of = IType.POINT,
				init = "[]",
				doc = @doc (
						value = "the current list of points that the agent has to reach (path)",
						deprecated = "this can be accessed using current_path.vertices")),
		@variable (
				name = DrivingSkill.SECURITY_DISTANCE_COEFF,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc (
						deprecated = "use safety_distance_coeff instead",
						value = "the coefficient for the computation of the the min distance between two vehicles (according to the vehicle speed - safety_distance =max(min_safety_distance, safety_distance_coeff `*` min(self.real_speed, other.real_speed) )")),
		@variable (
				name = DrivingSkill.SAFETY_DISTANCE_COEFF,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("the coefficient for the computation of the the min distance between two vehicles (according to the vehicle speed - security_distance =max(min_security_distance, security_distance_coeff `*` min(self.real_speed, other.real_speed) )")),
		@variable (
				name = DrivingSkill.MIN_SECURITY_DISTANCE,
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc (
						deprecated = "use min_safety_distance instead",
						value = "the minimal distance to another vehicle")),
		@variable (
				name = DrivingSkill.MIN_SAFETY_DISTANCE,
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("the minimum distance of the vehicle's front bumper to the leading vehicle's rear bumper, "
						+ "known as the parameter s0 in the Intelligent Driver Model")),
		@variable (
				name = DrivingSkill.CURRENT_LANE,
				type = IType.INT,
				init = "0",
				doc = @doc ("the current lane on which the agent is")),
		@variable (
				name = DrivingSkill.LOWEST_LANE,
				type = IType.INT,
				init = "0",
				doc = @doc ("the lane with the smallest index that the vehicle is in")),
		@variable (
				name = DrivingSkill.NUM_LANES_OCCUPIED,
				type = IType.INT,
				init = "1",
				doc = @doc (
						value = "the number of lanes that the vehicle occupies",
						comment = "e.g. if `num_lanes_occupied=3` and `lowest_lane=1`, the vehicle will be in lane 1, 2 and 3`")),
		@variable (
				name = DrivingSkill.VEHICLE_LENGTH,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the length of the vehicle (in meters)")),
		@variable (
				name = DrivingSkill.SPEED_COEFF,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("speed coefficient for the speed that the vehicle want to reach (according to the max speed of the road)")),
		@variable (
				name = DrivingSkill.MAX_SPEED,
				type = IType.FLOAT,
				init = "50.0",
				doc = @doc ("the maximum speed that the vehicle can achieve. "
						+ "Known as the parameter 'v0' in the Intelligent Driver Model")),
		@variable (
				name = DrivingSkill.TIME_HEADWAY,
				type = IType.FLOAT,
				init = "1.5",
				doc = @doc ("the time gap that to the leading vehicle that the driver must maintain. "
						+ "Known as the parameter 'T' in the Intelligent Driver Model")),
		@variable (
				name = DrivingSkill.MAX_ACCELERATION,
				type = IType.FLOAT,
				init = "0.3",
				doc = @doc ("the maximum acceleration of the vehicle. "
						+ "Known as the parameter 'a' in the Intelligent Driver Model")),
		@variable (
				name = DrivingSkill.MAX_DECELERATION,
				type = IType.FLOAT,
				init = "3.0",
				doc = @doc ("the maximum deceleration of the vehicle. "
						+ "Known as the parameter 'b' in the Intelligent Driver Model")),
		@variable (
				name = DrivingSkill.DELTA_IDM,
				type = IType.FLOAT,
				init = "4.0",
				doc = @doc ("the exponent used in the computation of free-road acceleration in the Intelligent Driver Model")),
		@variable (
				name = DrivingSkill.POLITENESS_FACTOR,
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("determines the politeness level of the vehicle when changing lanes. "
						+ "Known as the parameter 'p' in the MOBIL lane changing model")),
		@variable (
				name = DrivingSkill.MAX_SAFE_DECELERATION,
				type = IType.FLOAT,
				init = "4",
				doc = @doc ("the maximum deceleration that the vehicle is willing to induce on its back vehicle when changing lanes. "
						+ "Known as the parameter 'b_save' in the MOBIL lane changing model")),
		@variable (
				name = DrivingSkill.ACC_GAIN_THRESHOLD,
				type = IType.FLOAT,
				init = "0.2",
				doc = @doc ("the minimum acceleration gain for the vehicle to switch to another lane, "
						+ "introduced to prevent frantic lane changing. "
						+ "Known as the parameter 'a_th' in the MOBIL lane changing model")),
		@variable (
				name = DrivingSkill.ACC_BIAS,
				type = IType.FLOAT,
				init = "0.25",
				doc = @doc ("the bias term used for asymmetric lane changing, parameter 'a_bias' in MOBIL")),
		@variable (
				name = DrivingSkill.LC_COOLDOWN,
				type = IType.FLOAT,
				init = "4",
				doc = @doc ("the duration that a vehicle must wait before changing lanes again")),
		@variable (
				name = DrivingSkill.TIME_SINCE_LC,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the elapsed time since the last lane change")),
		@variable (
				name = DrivingSkill.IGNORE_ONEWAY,
				type = IType.BOOL,
				init = "false",
				doc = @doc ("if set to `true`, the vehicle will be able to violate one-way traffic rule")),
		@variable (
				name = DrivingSkill.VIOLATING_ONEWAY,
				type = IType.BOOL,
				init = "false",
				doc = @doc ("indicates if the vehicle is moving in the wrong direction on an one-way (unlinked) road")),
		@variable (
				name = DrivingSkill.CURRENT_ROAD,
				type = IType.AGENT,
				init = "nil",
				doc = @doc ("the road which the vehicle is currently on")),
		@variable (
				name = DrivingSkill.NEXT_ROAD,
				type = IType.AGENT,
				init = "nil",
				doc = @doc ("the road which the vehicle will enter next")),
		@variable (
				name = DrivingSkill.ON_LINKED_ROAD,
				type = IType.BOOL,
				init = "false",
				doc = @doc (
						deprecated = "use using_linked_road instead",
						value = "is the agent on the linked road?")),
		@variable (
				name = DrivingSkill.USING_LINKED_ROAD,
				type = IType.BOOL,
				init = "false",
				doc = @doc ("indicates if the vehicle is occupying at least one lane on the linked road")),
		@variable (
				name = DrivingSkill.ALLOWED_LANES,
				type = IType.LIST,
				init = "[]",
				doc = @doc ("a list containing possible lane index values for the attribute lowest_lane")),
		@variable (
				name = DrivingSkill.LINKED_LANE_LIMIT,
				type = IType.INT,
				init = "-1",
				doc = @doc ("the maximum number of linked lanes that the vehicle can use; the default value is -1, i.e. the vehicle can use all available linked lanes")),
		@variable (
				name = DrivingSkill.LANE_CHANGE_LIMIT,
				type = IType.INT,
				init = "1",
				doc = @doc ("the maximum number of lanes that the vehicle can change during a simulation step")),
		@variable (
				name = DrivingSkill.PROBA_LANE_CHANGE_UP,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc (
						value = "probability to change to a upper lane (left lane if right side driving) to gain acceleration, within one second",
						deprecated = "use MOBIL parameters to control this instead")),
		@variable (
				name = DrivingSkill.PROBA_LANE_CHANGE_DOWN,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc (
						value = "probability to change to a lower lane (right lane if right side driving) to gain acceleration, within one second",
						deprecated = "use MOBIL parameters to control this instead")),
		@variable (
				name = DrivingSkill.PROBA_USE_LINKED_ROAD,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("probability to change to a linked lane to gain acceleration, within one second")),
		@variable (
				name = DrivingSkill.PROBA_RESPECT_PRIORITIES,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to respect priority (right or left) laws, within one second")),
		@variable (
				name = DrivingSkill.PROBA_RESPECT_STOPS,
				type = IType.LIST,
				of = IType.FLOAT,
				init = "[]",
				doc = @doc ("probability to respect stop laws - one value for each type of stop, within one second")),
		@variable (
				name = DrivingSkill.PROBA_BLOCK_NODE,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("probability to block a node (do not let other vehicle cross the crossroad), within one second")),
		@variable (
				name = DrivingSkill.RIGHT_SIDE_DRIVING,
				type = IType.BOOL,
				init = "true",
				doc = @doc ("are vehicles driving on the right size of the road?")),
		@variable (
				name = DrivingSkill.DISTANCE_TO_GOAL,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("euclidean distance to the endpoint of the current segment")),
		@variable (
				name = DrivingSkill.DISTANCE_TO_CURRENT_TARGET,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("euclidean distance to the current target node")),
		@variable (
				name = DrivingSkill.SEGMENT_INDEX,
				type = IType.INT,
				init = "-1",
				doc = @doc ("current segment index of the agent on the current road ")),
		@variable (
				name = DrivingSkill.LEADING_VEHICLE,
				type = IType.AGENT,
				init = "nil",
				doc = @doc ("the vehicle which is right ahead of the current vehicle.\n"
						+ "If this is set to nil, the leading vehicle does not exist or might be very far away.")),
		@variable (
				name = DrivingSkill.LEADING_DISTANCE,
				type = IType.FLOAT,
				init = "nil",
				doc = @doc ("the distance to the leading vehicle")),
		@variable (
				name = DrivingSkill.LEADING_SPEED,
				type = IType.FLOAT,
				init = "nil",
				doc = @doc ("the speed of the leading vehicle")),
		@variable (
				name = DrivingSkill.FOLLOWER,
				type = IType.AGENT,
				init = "nil",
				doc = @doc ("the vehicle following this vehicle")) })
@skill (
		name = DrivingSkill.ADVANCED_DRIVING,
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill that provides driving primitives and operators"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrivingSkill extends MovingSkill {
	static {
		DEBUG.OFF();
	}

	/** The Constant ADVANCED_DRIVING. */
	public static final String ADVANCED_DRIVING = "advanced_driving";

	/** The Constant SECURITY_DISTANCE_COEFF. */
	// Attributes' names
	@Deprecated public static final String SECURITY_DISTANCE_COEFF = "security_distance_coeff";

	/** The Constant SAFETY_DISTANCE_COEFF. */
	public static final String SAFETY_DISTANCE_COEFF = "safety_distance_coeff";

	/** The Constant MIN_SECURITY_DISTANCE. */
	@Deprecated public static final String MIN_SECURITY_DISTANCE = "min_security_distance";

	/** The Constant MIN_SAFETY_DISTANCE. */
	public static final String MIN_SAFETY_DISTANCE = "min_safety_distance";

	/** The Constant CURRENT_ROAD. */
	public static final String CURRENT_ROAD = "current_road";

	/** The Constant NEXT_ROAD. */
	public static final String NEXT_ROAD = "next_road";

	/** The Constant CURRENT_LANE. */
	@Deprecated public static final String CURRENT_LANE = "current_lane";

	/** The Constant LOWEST_LANE. */
	public static final String LOWEST_LANE = "lowest_lane";

	/** The Constant DISTANCE_TO_GOAL. */
	public static final String DISTANCE_TO_GOAL = "distance_to_goal";

	/** The Constant DISTANCE_TO_CURRENT_TARGET. */
	public static final String DISTANCE_TO_CURRENT_TARGET = "distance_to_current_target";

	/** The Constant VEHICLE_LENGTH. */
	public static final String VEHICLE_LENGTH = "vehicle_length";

	/** The Constant PROBA_LANE_CHANGE_UP. */
	@Deprecated public static final String PROBA_LANE_CHANGE_UP = "proba_lane_change_up";

	/** The Constant PROBA_LANE_CHANGE_DOWN. */
	@Deprecated public static final String PROBA_LANE_CHANGE_DOWN = "proba_lane_change_down";

	/** The Constant PROBA_RESPECT_PRIORITIES. */
	public static final String PROBA_RESPECT_PRIORITIES = "proba_respect_priorities";

	/** The Constant PROBA_RESPECT_STOPS. */
	public static final String PROBA_RESPECT_STOPS = "proba_respect_stops";

	/** The Constant PROBA_BLOCK_NODE. */
	public static final String PROBA_BLOCK_NODE = "proba_block_node";

	/** The Constant PROBA_USE_LINKED_ROAD. */
	public static final String PROBA_USE_LINKED_ROAD = "proba_use_linked_road";

	/** The Constant RIGHT_SIDE_DRIVING. */
	public static final String RIGHT_SIDE_DRIVING = "right_side_driving";

	/** The Constant IGNORE_ONEWAY. */
	public static final String IGNORE_ONEWAY = "ignore_oneway";

	/** The Constant VIOLATING_ONEWAY. */
	public static final String VIOLATING_ONEWAY = "violating_oneway";

	/** The Constant ON_LINKED_ROAD. */
	public static final String ON_LINKED_ROAD = "on_linked_road";

	/** The Constant USING_LINKED_ROAD. */
	public static final String USING_LINKED_ROAD = "using_linked_road";

	/** The Constant LINKED_LANE_LIMIT. */
	public static final String LINKED_LANE_LIMIT = "linked_lane_limit";

	/** The Constant ALLOWED_LANES. */
	public static final String ALLOWED_LANES = "allowed_lanes";

	/** The Constant TARGETS. */
	@Deprecated public static final String TARGETS = "targets";

	/** The Constant CURRENT_TARGET. */
	public static final String CURRENT_TARGET = "current_target";

	/** The Constant CURRENT_INDEX. */
	public static final String CURRENT_INDEX = "current_index";

	/** The Constant FINAL_TARGET. */
	public static final String FINAL_TARGET = "final_target";

	/** The Constant CURRENT_PATH. */
	public static final String CURRENT_PATH = "current_path";

	/** The Constant ACCELERATION. */
	public static final String ACCELERATION = "acceleration";

	/** The Constant MAX_ACCELERATION. */
	public static final String MAX_ACCELERATION = "max_acceleration";

	/** The Constant MAX_DECELERATION. */
	public static final String MAX_DECELERATION = "max_deceleration";

	/** The Constant TIME_HEADWAY. */
	public static final String TIME_HEADWAY = "time_headway";

	/** The Constant DELTA_IDM. */
	public static final String DELTA_IDM = "delta_idm";

	/** The Constant POLITENESS_FACTOR. */
	public static final String POLITENESS_FACTOR = "politeness_factor";

	/** The Constant MAX_SAFE_DECELERATION. */
	public static final String MAX_SAFE_DECELERATION = "max_safe_deceleration";

	/** The Constant ACC_GAIN_THRESHOLD. */
	public static final String ACC_GAIN_THRESHOLD = "acc_gain_threshold";

	/** The Constant ACC_BIAS. */
	public static final String ACC_BIAS = "acc_bias";

	/** The Constant TIME_SINCE_LC. */
	public static final String TIME_SINCE_LC = "time_since_lane_change";

	/** The Constant LC_COOLDOWN. */
	public static final String LC_COOLDOWN = "lane_change_cooldown";

	/** The Constant SPEED_COEFF. */
	public static final String SPEED_COEFF = "speed_coeff";

	/** The Constant MAX_SPEED. */
	public static final String MAX_SPEED = "max_speed";

	/** The Constant SEGMENT_INDEX. */
	public static final String SEGMENT_INDEX = "segment_index_on_road";

	/** The Constant NUM_LANES_OCCUPIED. */
	public static final String NUM_LANES_OCCUPIED = "num_lanes_occupied";

	/** The Constant LANE_CHANGE_LIMIT. */
	public static final String LANE_CHANGE_LIMIT = "lane_change_limit";

	/** The Constant LEADING_VEHICLE. */
	public static final String LEADING_VEHICLE = "leading_vehicle";

	/** The Constant LEADING_DISTANCE. */
	public static final String LEADING_DISTANCE = "leading_distance";

	/** The Constant LEADING_SPEED. */
	public static final String LEADING_SPEED = "leading_speed";

	/** The Constant FOLLOWER. */
	public static final String FOLLOWER = "follower";

	/** The Constant ACT_CHOOSE_LANE. */
	// Actions' names
	public static final String ACT_CHOOSE_LANE = "choose_lane";

	// NOTE: Due to approximations in IDM, vehicles will never have the exact same location as its target.
	/** The Constant EPSILON. */
	// Therefore we consider the vehicle has reached its goal when distToGoal is smaller than this threshold.
	private static final double EPSILON = 1e-2;

	/**
	 * Gets the speed.
	 *
	 * @param agent
	 *            the agent
	 * @return the speed
	 */
	@getter (IKeyword.SPEED)
	public static double getSpeed(final IAgent agent) {
		// The second condition is used when `agent` is a road_node with a stop signal
		if (agent == null || !agent.hasAttribute(IKeyword.SPEED)) return 0.0;
		return (Double) agent.getAttribute(IKeyword.SPEED);
	}

	/**
	 * Sets the speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param speed
	 *            the speed
	 */
	@setter (IKeyword.SPEED)
	public static void setSpeed(final IAgent vehicle, final double speed) {
		if (vehicle == null) return;
		vehicle.setAttribute(IKeyword.SPEED, speed);
	}

	/**
	 * Gets the real speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the real speed
	 */
	@getter (IKeyword.REAL_SPEED)
	@Deprecated
	public static double getRealSpeed(final IAgent vehicle) {
		return getSpeed(vehicle);
	}

	/**
	 * Sets the real speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param speed
	 *            the speed
	 */
	@setter (IKeyword.REAL_SPEED)
	@Deprecated
	public static void setRealSpeed(final IAgent vehicle, final double speed) {
		setSpeed(vehicle, speed);
	}

	/**
	 * Sets the acceleration read only.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param val
	 *            the val
	 */
	@setter (ACCELERATION)
	public static void setAccelerationReadOnly(final IAgent vehicle, final Double val) {
		// read-only
	}

	/**
	 * Sets the acceleration.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param val
	 *            the val
	 */
	private static void setAcceleration(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(ACCELERATION, val);
	}

	/**
	 * Gets the max acceleration.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the max acceleration
	 */
	@getter (MAX_ACCELERATION)
	public static double getMaxAcceleration(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_ACCELERATION);
	}

	/**
	 * Sets the max acceleration.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param val
	 *            the val
	 */
	@setter (MAX_ACCELERATION)
	public static void setMaxAcceleration(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(MAX_ACCELERATION, val);
	}

	/**
	 * Gets the max deceleration.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the max deceleration
	 */
	@getter (MAX_DECELERATION)
	public static double getMaxDeceleration(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_DECELERATION);
	}

	/**
	 * Gets the time headway.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the time headway
	 */
	@getter (TIME_HEADWAY)
	public static double getTimeHeadway(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(TIME_HEADWAY);
	}

	/**
	 * Gets the delta IDM.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the delta IDM
	 */
	@getter (DELTA_IDM)
	public static double getDeltaIDM(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(DELTA_IDM);
	}

	/**
	 * Gets the politeness factor.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the politeness factor
	 */
	@getter (POLITENESS_FACTOR)
	public static double getPolitenessFactor(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(POLITENESS_FACTOR);
	}

	/**
	 * Gets the max safe deceleration.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the max safe deceleration
	 */
	@getter (MAX_SAFE_DECELERATION)
	public static double getMaxSafeDeceleration(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_SAFE_DECELERATION);
	}

	/**
	 * Gets the acc gain threshold.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the acc gain threshold
	 */
	@getter (ACC_GAIN_THRESHOLD)
	public static double getAccGainThreshold(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(ACC_GAIN_THRESHOLD);
	}

	/**
	 * Gets the acc bias.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the acc bias
	 */
	@getter (ACC_BIAS)
	public static double getAccBias(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(ACC_BIAS);
	}

	/**
	 * Gets the time since LC.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the time since LC
	 */
	@getter (TIME_SINCE_LC)
	public static double getTimeSinceLC(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(TIME_SINCE_LC);
	}

	/**
	 * Sets the time since LC read only.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param time
	 *            the time
	 */
	// @setter(TIME_SINCE_LC)
	public static void setTimeSinceLCReadOnly(final IAgent vehicle, final double time) {
		// read-only
	}

	/**
	 * Sets the time since LC.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param time
	 *            the time
	 */
	@setter (TIME_SINCE_LC)
	public static void setTimeSinceLC(final IAgent vehicle, final double time) {
		vehicle.setAttribute(TIME_SINCE_LC, time);
	}

	/**
	 * Gets the LC cooldown.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the LC cooldown
	 */
	@getter (LC_COOLDOWN)
	public static double getLCCooldown(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(LC_COOLDOWN);
	}

	/**
	 * Gets the speed coeff.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the speed coeff
	 */
	@getter (SPEED_COEFF)
	public static double getSpeedCoeff(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(SPEED_COEFF);
	}

	/**
	 * Sets the speed coeff.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param val
	 *            the val
	 */
	@setter (SPEED_COEFF)
	public static void setSpeedCoeff(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(SPEED_COEFF, val);
	}

	/**
	 * Gets the max speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the max speed
	 */
	@getter (MAX_SPEED)
	public static double getMaxSpeed(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MAX_SPEED);
	}

	/**
	 * Sets the max speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param val
	 *            the val
	 */
	@setter (MAX_SPEED)
	public static void setMaxSpeed(final IAgent vehicle, final Double val) {
		vehicle.setAttribute(MAX_SPEED, val);
	}

	/**
	 * Gets the current target.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the current target
	 */
	@getter (CURRENT_TARGET)
	public static IAgent getCurrentTarget(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(CURRENT_TARGET);
	}

	/**
	 * Sets the current target.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param target
	 *            the target
	 */
	@setter (CURRENT_TARGET)
	public static void setCurrentTarget(final IAgent vehicle, final IAgent target) {
		vehicle.setAttribute(CURRENT_TARGET, target);
	}

	/**
	 * Gets the final target.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the final target
	 */
	@getter (FINAL_TARGET)
	public static IAgent getFinalTarget(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(FINAL_TARGET);
	}

	/**
	 * Sets the final target.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param point
	 *            the point
	 */
	@setter (FINAL_TARGET)
	public static void setFinalTarget(final IAgent vehicle, final IAgent point) {
		vehicle.setAttribute(FINAL_TARGET, point);
	}

	/**
	 * Gets the current index.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the current index
	 */
	@getter (CURRENT_INDEX)
	public static Integer getCurrentIndex(final IAgent vehicle) {
		return (Integer) vehicle.getAttribute(CURRENT_INDEX);
	}

	/**
	 * Sets the current index.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param index
	 *            the index
	 */
	@setter (CURRENT_INDEX)
	public static void setCurrentIndex(final IAgent vehicle, final Integer index) {
		vehicle.setAttribute(CURRENT_INDEX, index);
	}

	/**
	 * Gets the segment index.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the segment index
	 */
	@getter (SEGMENT_INDEX)
	public static Integer getSegmentIndex(final IAgent vehicle) {
		return (Integer) vehicle.getAttribute(SEGMENT_INDEX);
	}

	/**
	 * Sets the segment index.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param index
	 *            the index
	 */
	@setter (SEGMENT_INDEX)
	public static void setSegmentIndex(final IAgent vehicle, final Integer index) {
		vehicle.setAttribute(SEGMENT_INDEX, index);
	}

	/**
	 * Gets the current path.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the current path
	 */
	@getter (CURRENT_PATH)
	public static IPath getCurrentPath(final IAgent vehicle) {
		return (IPath) vehicle.getAttribute(CURRENT_PATH);
	}

	/**
	 * Sets the current path.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param path
	 *            the path
	 */
	@setter (CURRENT_PATH)
	public static void setCurrentPath(final IAgent vehicle, final IPath path) {
		vehicle.setAttribute(CURRENT_PATH, path);

		if (path != null) {
			// Also set other states
			IAgent source = (IAgent) path.getStartVertex();
			IAgent target = (IAgent) path.getEndVertex();
			vehicle.setLocation(source.getLocation());
			setCurrentIndex(vehicle, -1);
			setCurrentTarget(vehicle, (IAgent) path.getVertexList().get(0));
			setFinalTarget(vehicle, target);
		}
	}

	/**
	 * Gets the targets.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the targets
	 */
	public static List<IAgent> getTargets(final IAgent vehicle) {
		IPath path = getCurrentPath(vehicle);
		return path == null ? null : path.getVertexList();
	}

	/**
	 * Sets the targets.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param points
	 *            the points
	 */
	@setter (TARGETS)
	public static void setTargets(final IAgent vehicle, final List<IAgent> points) {
		// read-only
	}

	/**
	 * Gets the proba use linked road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the proba use linked road
	 */
	@getter (PROBA_USE_LINKED_ROAD)
	public static double getProbaUseLinkedRoad(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_USE_LINKED_ROAD);
	}

	/**
	 * Sets the proba use linked road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param proba
	 *            the proba
	 */
	@setter (PROBA_USE_LINKED_ROAD)
	public static void setProbaUseLinkedRoad(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_USE_LINKED_ROAD, proba);
	}

	/**
	 * Gets the proba lane change down.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the proba lane change down
	 */
	@getter (PROBA_LANE_CHANGE_DOWN)
	@Deprecated
	public static double getProbaLaneChangeDown(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	/**
	 * Sets the proba lane change down.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param proba
	 *            the proba
	 */
	@setter (PROBA_LANE_CHANGE_DOWN)
	@Deprecated
	public static void setProbaLaneChangeDown(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	/**
	 * Gets the proba lane change up.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the proba lane change up
	 */
	@getter (PROBA_LANE_CHANGE_UP)
	@Deprecated
	public static double getProbaLaneChangeUp(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	/**
	 * Sets the proba lane change up.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param proba
	 *            the proba
	 */
	@setter (PROBA_LANE_CHANGE_UP)
	@Deprecated
	public static void setProbaLaneChangeUp(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_LANE_CHANGE_UP, proba);
	}

	/**
	 * Gets the proba respect priorities.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the proba respect priorities
	 */
	@getter (PROBA_RESPECT_PRIORITIES)
	public static double getProbaRespectPriorities(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_RESPECT_PRIORITIES);
	}

	/**
	 * Sets the proba respect priorities.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param proba
	 *            the proba
	 */
	@setter (PROBA_RESPECT_PRIORITIES)
	public static void setProbaRespectPriorities(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_RESPECT_PRIORITIES, proba);
	}

	/**
	 * Gets the proba block node.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the proba block node
	 */
	@getter (PROBA_BLOCK_NODE)
	public static double getProbaBlockNode(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_BLOCK_NODE);
	}

	/**
	 * Sets the proba block node.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param proba
	 *            the proba
	 */
	@setter (PROBA_BLOCK_NODE)
	public static void setProbaBlockNode(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_BLOCK_NODE, proba);
	}

	/**
	 * Gets the probas respect stops.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the probas respect stops
	 */
	@getter (PROBA_RESPECT_STOPS)
	public static List<Double> getProbasRespectStops(final IAgent vehicle) {
		return (List<Double>) vehicle.getAttribute(PROBA_RESPECT_STOPS);
	}

	/**
	 * Sets the probas respect stops.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param probas
	 *            the probas
	 */
	@setter (PROBA_RESPECT_STOPS)
	public static void setProbasRespectStops(final IAgent vehicle, final List<Boolean> probas) {
		vehicle.setAttribute(PROBA_RESPECT_STOPS, probas);
	}

	/**
	 * Gets the on linked road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the on linked road
	 */
	@Deprecated
	@getter (ON_LINKED_ROAD)
	public static boolean getOnLinkedRoad(final IAgent vehicle) {
		return isUsingLinkedRoad(vehicle);
	}

	/**
	 * Sets the on linked road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param onLinkedRoad
	 *            the on linked road
	 */
	@Deprecated
	@setter (ON_LINKED_ROAD)
	public static void setOnLinkedRoad(final IAgent vehicle, final Boolean onLinkedRoad) {
		// read-only
	}

	/**
	 * Checks if is using linked road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return true, if is using linked road
	 */
	@getter (USING_LINKED_ROAD)
	public static boolean isUsingLinkedRoad(final IAgent vehicle) {
		IAgent currentRoad = getCurrentRoad(vehicle);
		if (currentRoad == null) return false;

		int lowestLane = getLowestLane(vehicle);
		int numLanesCurrent = RoadSkill.getNumLanes(currentRoad);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		return lowestLane > numLanesCurrent - numLanesOccupied;
	}

	/**
	 * Sets the using linked road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param usingLinkedRoad
	 *            the using linked road
	 */
	@setter (USING_LINKED_ROAD)
	public static void setUsingLinkedRoad(final IAgent vehicle, final boolean usingLinkedRoad) {
		// read-only
	}

	/**
	 * Can ignore oneway.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return true, if successful
	 */
	@getter (IGNORE_ONEWAY)
	public static boolean canIgnoreOneway(final IAgent vehicle) {
		return (boolean) vehicle.getAttribute(IGNORE_ONEWAY);
	}

	/**
	 * Checks if is violating oneway.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return true, if is violating oneway
	 */
	@getter (VIOLATING_ONEWAY)
	public static boolean isViolatingOneway(final IAgent vehicle) {
		return (boolean) vehicle.getAttribute(VIOLATING_ONEWAY);
	}

	/**
	 * Sets the violating oneway.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param violatingOneway
	 *            the violating oneway
	 */
	@setter (VIOLATING_ONEWAY)
	public static void setViolatingOneway(final IAgent vehicle, final boolean violatingOneway) {
		vehicle.setAttribute(VIOLATING_ONEWAY, violatingOneway);
	}

	/**
	 * Gets the allowed lanes.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the allowed lanes
	 */
	@getter (ALLOWED_LANES)
	public static List<Integer> getAllowedLanes(final IAgent vehicle) {
		return (List<Integer>) vehicle.getAttribute(ALLOWED_LANES);
	}

	/**
	 * Gets the linked lane limit.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the linked lane limit
	 */
	@getter (LINKED_LANE_LIMIT)
	public static int getLinkedLaneLimit(final IAgent vehicle) {
		return (int) vehicle.getAttribute(LINKED_LANE_LIMIT);
	}

	/**
	 * Sets the linked lane limit.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param linkedLaneLimit
	 *            the linked lane limit
	 */
	@setter (LINKED_LANE_LIMIT)
	public static void setLinkedLaneLimit(final IAgent vehicle, final int linkedLaneLimit) {
		vehicle.setAttribute(LINKED_LANE_LIMIT, linkedLaneLimit);
	}

	/**
	 * Gets the lane change limit.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the lane change limit
	 */
	@getter (LANE_CHANGE_LIMIT)
	public static int getLaneChangeLimit(final IAgent vehicle) {
		return (int) vehicle.getAttribute(LANE_CHANGE_LIMIT);
	}

	/**
	 * Gets the right side driving.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the right side driving
	 */
	@getter (RIGHT_SIDE_DRIVING)
	public static boolean getRightSideDriving(final IAgent vehicle) {
		return (Boolean) vehicle.getAttribute(RIGHT_SIDE_DRIVING);
	}

	/**
	 * Sets the right side driving.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param isRight
	 *            the is right
	 */
	@setter (RIGHT_SIDE_DRIVING)
	public static void setRightSideDriving(final IAgent vehicle, final Boolean isRight) {
		vehicle.setAttribute(RIGHT_SIDE_DRIVING, isRight);
	}

	/**
	 * Gets the security distance coeff.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the security distance coeff
	 */
	@Deprecated
	@getter (SECURITY_DISTANCE_COEFF)
	public static double getSecurityDistanceCoeff(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(SECURITY_DISTANCE_COEFF);
	}

	/**
	 * Sets the security distance coeff.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param ls
	 *            the ls
	 */
	@Deprecated
	@setter (SECURITY_DISTANCE_COEFF)
	public static void setSecurityDistanceCoeff(final IAgent vehicle, final double ls) {
		vehicle.setAttribute(SECURITY_DISTANCE_COEFF, ls);
	}

	/**
	 * Gets the safety distance coeff.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the safety distance coeff
	 */
	@getter (SAFETY_DISTANCE_COEFF)
	public static double getSafetyDistanceCoeff(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(SAFETY_DISTANCE_COEFF);
	}

	/**
	 * Sets the safety distance coeff.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param ls
	 *            the ls
	 */
	@setter (SAFETY_DISTANCE_COEFF)
	public static void setSafetyDistanceCoeff(final IAgent vehicle, final double ls) {
		vehicle.setAttribute(SAFETY_DISTANCE_COEFF, ls);
	}

	/**
	 * Gets the current road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the current road
	 */
	@getter (CURRENT_ROAD)
	public static IAgent getCurrentRoad(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(CURRENT_ROAD);
	}

	/**
	 * Sets the current road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param road
	 *            the road
	 */
	@setter (CURRENT_ROAD)
	public static void setCurrentRoad(final IAgent vehicle, final IAgent road) {
		vehicle.setAttribute(CURRENT_ROAD, road);
	}

	/**
	 * Gets the next road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the next road
	 */
	@getter (NEXT_ROAD)
	public static IAgent getNextRoad(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(NEXT_ROAD);
	}

	/**
	 * Sets the next road.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param road
	 *            the road
	 */
	@setter (NEXT_ROAD)
	public static void setNextRoad(final IAgent vehicle, final IAgent road) {
		vehicle.setAttribute(NEXT_ROAD, road);
	}

	/**
	 * Gets the vehicle length.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the vehicle length
	 */
	@getter (VEHICLE_LENGTH)
	public static double getVehicleLength(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(VEHICLE_LENGTH);
	}

	/**
	 * Gets the current lane.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the current lane
	 */
	@Deprecated
	@getter (CURRENT_LANE)
	public static int getCurrentLane(final IAgent vehicle) {
		return getLowestLane(vehicle);
	}

	/**
	 * Sets the current lane.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param newLane
	 *            the new lane
	 */
	@Deprecated
	@setter (CURRENT_LANE)
	public static void setCurrentLane(final IAgent vehicle, final int newLane) {
		setLowestLane(vehicle, newLane);
	}

	/**
	 * Gets the lowest lane.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the lowest lane
	 */
	@getter (LOWEST_LANE)
	public static int getLowestLane(final IAgent vehicle) {
		return (int) vehicle.getAttribute(LOWEST_LANE);
	}

	/**
	 * Sets the lowest lane.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param lowestLane
	 *            the lowest lane
	 */
	@setter (LOWEST_LANE)
	public static void setLowestLane(final IAgent vehicle, final int lowestLane) {
		vehicle.setAttribute(LOWEST_LANE, lowestLane);
	}

	/**
	 * Gets the distance to goal.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the distance to goal
	 */
	@getter (DISTANCE_TO_GOAL)
	public static double getDistanceToGoal(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(DISTANCE_TO_GOAL);
	}

	/**
	 * Gets the distance to current target.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the distance to current target
	 */
	@getter (DISTANCE_TO_CURRENT_TARGET)
	public static double getDistanceToCurrentTarget(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(DISTANCE_TO_CURRENT_TARGET);
	}

	/**
	 * Sets the distance to current target.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param dist
	 *            the dist
	 */
	@setter (DISTANCE_TO_CURRENT_TARGET)
	public static void setDistanceToCurrentTarget(final IAgent vehicle, final double dist) {
		vehicle.setAttribute(DISTANCE_TO_CURRENT_TARGET, dist);
	}

	/**
	 * Gets the min security distance.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the min security distance
	 */
	@getter (MIN_SECURITY_DISTANCE)
	public static double getMinSecurityDistance(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MIN_SECURITY_DISTANCE);
	}

	/**
	 * Sets the min sec distance.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param msd
	 *            the msd
	 */
	@setter (MIN_SECURITY_DISTANCE)
	public static void setMinSecDistance(final IAgent vehicle, final double msd) {
		vehicle.setAttribute(MIN_SECURITY_DISTANCE, msd);
	}

	/**
	 * Gets the min safety distance.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the min safety distance
	 */
	@getter (MIN_SAFETY_DISTANCE)
	public static double getMinSafetyDistance(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(MIN_SAFETY_DISTANCE);
	}

	/**
	 * Sets the distance to goal.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param dg
	 *            the dg
	 */
	@setter (DISTANCE_TO_GOAL)
	public static void setDistanceToGoal(final IAgent vehicle, final double dg) {
		vehicle.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	/**
	 * Gets the num lanes occupied.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the num lanes occupied
	 */
	@getter (NUM_LANES_OCCUPIED)
	public static Integer getNumLanesOccupied(final IAgent vehicle) {
		return (Integer) vehicle.getAttribute(NUM_LANES_OCCUPIED);
	}

	/**
	 * Sets the num lanes occupied.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param value
	 *            the value
	 */
	@setter (NUM_LANES_OCCUPIED)
	public static void setNumLanesOccupied(final IAgent vehicle, final Integer value) {
		vehicle.setAttribute(NUM_LANES_OCCUPIED, value);
	}

	/**
	 * Gets the leading vehicle.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the leading vehicle
	 */
	@getter (LEADING_VEHICLE)
	public static IAgent getLeadingVehicle(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(LEADING_VEHICLE);
	}

	/**
	 * Sets the leading vehicle read only.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param leadingVehicle
	 *            the leading vehicle
	 */
	@setter (LEADING_VEHICLE)
	public static void setLeadingVehicleReadOnly(final IAgent vehicle, final IAgent leadingVehicle) {
		// read-only
	}

	/**
	 * Sets the leading vehicle.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param leadingVehicle
	 *            the leading vehicle
	 */
	public static void setLeadingVehicle(final IAgent vehicle, final IAgent leadingVehicle) {
		vehicle.setAttribute(LEADING_VEHICLE, leadingVehicle);
	}

	/**
	 * Gets the leading distance.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the leading distance
	 */
	@getter (LEADING_DISTANCE)
	public static double getLeadingDistance(final IAgent vehicle) {
		Double d = (Double) vehicle.getAttribute(LEADING_DISTANCE);
		if (d == null) return 0.0;
		return d.doubleValue();
	}

	/**
	 * Gets the leading speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @return the leading speed
	 */
	@getter (LEADING_SPEED)
	public static double getLeadingSpeed(final IAgent vehicle) {
		Double d = (Double) vehicle.getAttribute(LEADING_SPEED);
		if (d == null) return 0.0;
		return d.doubleValue();
	}

	/**
	 * Sets the leading distance read only.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param leadingDist
	 *            the leading dist
	 */
	@setter (LEADING_DISTANCE)
	public static void setLeadingDistanceReadOnly(final IAgent vehicle, final double leadingDist) {
		// read-only
	}

	/**
	 * Sets the leading distance.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param leadingDist
	 *            the leading dist
	 */
	public static void setLeadingDistance(final IAgent vehicle, final double leadingDist) {
		vehicle.setAttribute(LEADING_DISTANCE, leadingDist);
	}

	/**
	 * Sets the leading speed read only.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param leadingSpeed
	 *            the leading speed
	 */
	@setter (LEADING_SPEED)
	public static void setLeadingSpeedReadOnly(final IAgent vehicle, final double leadingSpeed) {
		// read-only
	}

	/**
	 * Sets the leading speed.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param leadingSpeed
	 *            the leading speed
	 */
	public static void setLeadingSpeed(final IAgent vehicle, final double leadingSpeed) {
		vehicle.setAttribute(LEADING_SPEED, leadingSpeed);
	}

	/**
	 * Sets the follower.
	 *
	 * @param vehicle
	 *            the vehicle
	 * @param follower
	 *            the follower
	 */
	public static void setFollower(final IAgent vehicle, final IAgent follower) {
		vehicle.setAttribute(FOLLOWER, follower);
	}

	/**
	 * Prim advanced follow.
	 *
	 * @param scope
	 *            the scope
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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
					deprecated = "apparently this action does not do what is described in the documentation",
					value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)",
					returns = "the remaining time",
					examples = { @example ("do osm_follow path: the_path on: road_network;") }))
	@Deprecated
	public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
		// Double t = scope.hasArg("time") ? scope.getFloatArg("time") : scope.getClock().getStepInSeconds();
		// GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		// return moveToNextLocAlongPathOSM(scope, t, path);
		return 0.0;
	}

	/**
	 * Prim ready to cross.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "ready_to_cross",
			args = { @arg (
					name = "node",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road node to test")),
					@arg (
							name = "new_road",
							type = IType.AGENT,
							optional = false,
							doc = @doc ("the road to test")) },
			doc = @doc (
					value = "action to test if the vehicle cross a road node to move to a new road",
					returns = "true if the vehicle can cross the road node, false otherwise",
					examples = { @example ("do is_ready_next_road new_road: a_road lane: 0;") }))
	public Boolean primReadyToCross(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		IAgent node = (IAgent) scope.getArg("node", IType.AGENT);
		IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		return readyToCross(scope, vehicle, node, road);
	}

	/**
	 * Prim test next road.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "test_next_road",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road to test")) },
			doc = @doc (
					value = "action to test if the vehicle can take the given road",
					returns = "true (the vehicle can take the road) or false (the vehicle cannot take the road)",
					examples = { @example ("do test_next_road new_road: a_road;") }))
	public Boolean primTestNextRoad(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	/**
	 * Check if the vehicle is ready to cross the intersection to get to a new road, concerning: 1. Traffic lights 2.
	 * Other vehicles coming from other incoming roads
	 *
	 * @param scope
	 * @param newRoad
	 * @return true if ready, false otherwise
	 *
	 * @throws GamaRuntimeException
	 */
	public static Boolean readyToCross(final IScope scope, final IAgent vehicle, final IAgent node,
			final IAgent newRoad) throws GamaRuntimeException {
		double vehicleLength = getVehicleLength(vehicle);
		ISpecies context = vehicle.getSpecies();

		// additional conditions to cross the intersection, defined by the user
		IStatement.WithArgs actionTNR = context.getAction("test_next_road");
		Arguments argsTNR = new Arguments();
		argsTNR.put("new_road", ConstantExpressionDescription.create(newRoad));
		actionTNR.setRuntimeArgs(scope, argsTNR);
		if (!(Boolean) actionTNR.executeOn(scope)) return false;

		IAgent currentRoad = (IAgent) vehicle.getAttribute(CURRENT_ROAD);
		// Don't need to do these checks if the vehicle was just initialized
		if (currentRoad != null) {
			// Check traffic lights
			List<List> stops = (List<List>) node.getAttribute(RoadNodeSkill.STOP);
			// TODO: it is wrong to rescale proba of 1.0 wrt time step
			// List<Double> probasRespectStops = new ArrayList<>();
			// double timeStep = scope.getSimulation().getClock().getStepInSeconds();
			// for (double p : getProbasRespectStops(vehicle)) {
			// probasRespectStops.add(rescaleProba(p, timeStep));
			// }
			List<Double> probasRespectStops = getProbasRespectStops(vehicle);
			for (int i = 0; i < stops.size(); i++) {
				Boolean stop = stops.get(i).contains(currentRoad);
				if (stop && (probasRespectStops.size() <= i || Random.opFlip(scope, probasRespectStops.get(i))))
					return false;
			}

			// Check for vehicles blocking at intersection
			// road node blocking information, which is a map: vehicle -> list of blocked roads
			Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
			Collection<IAgent> blockingVehicles = new LinkedHashSet<>(blockInfo.keySet());
			// check if any blocking vehicle has moved
			for (IAgent otherVehicle : blockingVehicles) {
				if (!otherVehicle.getLocation().equals(node.getLocation())) { blockInfo.remove(otherVehicle); }
			}
			// find if current road is blocked by any vehicle
			for (List<IAgent> blockedRoads : blockInfo.values()) {
				if (blockedRoads.contains(currentRoad)) return false;
			}

			// TODO: it is wrong to rescale proba of 1.0 wrt time step
			// double probaRespectPriorities = rescaleProba(getProbaRespectPriorities(vehicle), timeStep);
			double probaRespectPriorities = getProbaRespectPriorities(vehicle);
			if (!Random.opFlip(scope, probaRespectPriorities)) return true;

			Boolean rightSide = getRightSideDriving(vehicle);
			List<IAgent> priorityRoads = (List<IAgent>) node.getAttribute(RoadNodeSkill.PRIORITY_ROADS);
			boolean onPriorityRoad = priorityRoads != null && priorityRoads.contains(currentRoad);

			// ab is the line representing the direction of the vehicle
			// when moving from the current road to the next road
			List<GamaPoint> pts = currentRoad.getGeometry().getPoints();
			GamaPoint a = pts.get(pts.size() - 2); // starting point of last segment of current road
			GamaPoint b = newRoad.getGeometry().getPoints().get(1); // end point of first segment of new road

			// Why is 0.5 a lower bound??
			double speed = Math.max(0.5, getSpeed(vehicle));
			double safetyDistCoeff = vehicle.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(vehicle)
					: getSecurityDistanceCoeff(vehicle);
			double distToNode = getDistanceToCurrentTarget(vehicle);
			List<IAgent> roadsIn = (List) node.getAttribute(RoadNodeSkill.ROADS_IN);
			for (IAgent otherInRoad : roadsIn) {
				if (otherInRoad == currentRoad) { continue; }
				List<GamaPoint> otherPts = otherInRoad.getGeometry().getPoints();
				// Starting point of last segment of other incoming road
				GamaPoint p = otherPts.get(otherPts.size() - 2);
				// Check if this road is on the right or left side of the current vehicle's moving direction
				int side = Utils.sideOfPoint(a, b, p);
				boolean otherRoadIsPriortized = priorityRoads != null && priorityRoads.contains(otherInRoad);
				boolean hasPriority = onPriorityRoad && !otherRoadIsPriortized;
				boolean shouldRespectPriority = !onPriorityRoad && otherRoadIsPriortized;
				// be careful of vehicles coming from the right/left side
				if (!hasPriority && (shouldRespectPriority || rightSide && side < 0 || !rightSide && side > 0)) {
					for (OrderedBidiMap<IAgent, Double> vehicleOrderMap : RoadSkill.getVehicleOrdering(otherInRoad)) {
						// The vehicle closest to the end of the road
						OrderedBidiMap<Double, IAgent> distMap = vehicleOrderMap.inverseBidiMap();
						if (distMap.isEmpty()) { continue; }
						double otherDistToNode = distMap.lastKey();
						IAgent otherVehicle = distMap.get(otherDistToNode);
						if (otherVehicle == null || otherVehicle.dead()) { continue; }
						double otherVehicleLength = getVehicleLength(otherVehicle);
						double otherSpeed = getSpeed(otherVehicle);
						if (getCurrentTarget(otherVehicle) != node) {
							// Other vehicle is actually going away from the intersection
							otherSpeed = -otherSpeed;
						}
						double gap = distToNode + otherDistToNode - (vehicleLength / 2 + otherVehicleLength / 2);

						if (getSpeed(otherVehicle) > 0.0
								&& 0.5 + safetyDistCoeff * Math.max(0, speed - otherSpeed) > gap)
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Prim compute path.
	 *
	 * @param scope
	 *            the scope
	 * @return the i path
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "compute_path",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph representing the road network")),
					@arg (
							name = "target",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("the target node to reach")),
					@arg (
							name = "source",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("the source node (optional, if not defined, closest node to the agent location)")),
					@arg (
							name = "nodes",
							type = IType.LIST,
							optional = true,
							doc = @doc ("the nodes forming the resulting path")) },
			doc = @doc (
					value = "Action to compute the shortest path to the target node, or shortest path based on the provided list of nodes",
					returns = "the computed path, or nil if no valid path is found",
					comment = "either `nodes` or `target` must be specified",
					examples = { @example ("do compute_path graph: road_network target: target_node;"),
							@example ("do compute_path graph: road_network nodes: [node1, node5, node10];") }))
	public IPath primComputePath(final IScope scope) throws GamaRuntimeException {
		GamaGraph graph = (GamaGraph) scope.getArg("graph", IType.GRAPH);
		IList<IAgent> nodes = (IList) scope.getArg("nodes", IType.LIST);
		IAgent target = (IAgent) scope.getArg("target", IType.AGENT);
		IAgent source = (IAgent) scope.getArg("source", IType.AGENT);

		IAgent vehicle = getCurrentAgent(scope);
		IPath path;

		// allow reverse travel on road to compute path for agents on road side only
		if (canIgnoreOneway(vehicle)) { graph.setDirected(false); }

		if (target != null) {
			if (!graph.vertexSet().contains(target))
				throw GamaRuntimeException.error(target.getName() + " must be a vertex in the given graph", scope);

			if (source == null) {
				source = (IAgent) Queries.closest_to(scope, graph.getVertices(), vehicle);
			} else if (!graph.vertexSet().contains(source))
				throw GamaRuntimeException.error(source.getName() + " must be a vertex in the given graph", scope);
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
		} else
			throw GamaRuntimeException.error("one of `nodes` or `target` must be non nil", scope);

		// restore graph direction
		graph.setDirected(true);

		if (path != null && !path.getEdgeGeometry().isEmpty()) {
			setCurrentPath(vehicle, path);
			return path;
		}
		clearDrivingStates(scope);
		return null;
	}

	/**
	 * Prim compute path from nodes.
	 *
	 * @param scope
	 *            the scope
	 * @return the i path
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "path_from_nodes",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph representing the road network")),
					@arg (
							name = "nodes",
							type = IType.LIST,
							optional = false,
							doc = @doc ("the list of nodes composing the path")) },
			doc = @doc (
					value = "action to compute a path from a list of nodes according to a given graph",
					returns = "the computed path, return nil if no path can be taken",
					deprecated = "use compute_path with the facet `nodes` instead",
					examples = {
							@example ("do compute_path_from_nodes graph: road_network nodes: [node1, node5, node10];") }))
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

	/**
	 * Prim drive random.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "drive_random",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("a graph representing the road network")),
					@arg (
							name = "proba_roads",
							type = IType.MAP,
							optional = true,
							doc = @doc ("a map containing for each road (key), the probability to be selected as next road (value)")) },
			doc = @doc (
					value = "action to drive by chosen randomly the next road",
					examples = { @example ("do drive_random init_node: some_node;") }))
	public boolean primDriveRandom(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		GamaSpatialGraph graph = (GamaSpatialGraph) scope.getArg("graph", IType.GRAPH);
		Map<IAgent, Double> roadProba = (Map) scope.getArg("proba_roads", IType.MAP);
		if (graph == null) throw GamaRuntimeException.error("The parameter `graph` must be set", scope);

		IAgent initNode = null;
		// initialize starting location
		if (getCurrentRoad(vehicle) == null) {
			IList<IShape> nodes = graph.getVertices();
			IShape shape = Queries.closest_to(scope, nodes, vehicle);
			initNode = shape.getAgent();
			setLocation(vehicle, initNode.getLocation());
			setCurrentTarget(vehicle, initNode);
			setNextRoad(vehicle, chooseNextRoadRandomly(scope, graph, initNode, roadProba));
		}

		return moveAcrossRoads(scope, true, graph, roadProba);
	}

	/**
	 * Prim drive.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "drive",
			doc = @doc (
					value = "action to drive toward the target",
					examples = { @example ("do drive;") }))
	public boolean primDrive(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		IPath path = getCurrentPath(vehicle);
		if (path == null) {
			String msg = String.format(
					"%s is not driving because it has not been assigned a valid path. "
							+ "The action `compute_path` might have been used with the same source and target node.",
					vehicle.getName());
			throw GamaRuntimeException.warning(msg, scope);
		}
		// Initialize the first road
		if (getCurrentIndex(vehicle) == -1) { setNextRoad(vehicle, (IAgent) path.getEdgeList().get(0)); }
		return moveAcrossRoads(scope, false, null, null);
	}

	/**
	 * Prim on entering new road.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "on_entering_new_road",
			doc = @doc ("override this if you want to do something when the vehicle enters a new road (e.g. adjust parameters)"))
	public void primOnEnteringNewRoad(final IScope scope) throws GamaRuntimeException {
		// user-defined
	}

	/**
	 * Prim external factor on remaining time.
	 *
	 * @param scope
	 *            the scope
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "external_factor_impact",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to the vehicle wants to go")),
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
			doc = @doc (
					value = "remove the vehicle from its current roads",
					examples = { @example ("do unregister") }))
	public boolean primUnregister(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = getCurrentAgent(scope);
		return unregister(scope, vehicle);
	}

	/**
	 * Prim speed choice.
	 *
	 * @param scope
	 *            the scope
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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
					examples = { @example ("do speed_choice new_road: the_road;") },
					deprecated = "There's no apparent use case for overriding this, since Intelligent Driving Model was implemented"))
	@Deprecated
	public Double primSpeedChoice(final IScope scope) throws GamaRuntimeException {
		return 0.0;
	}

	/**
	 * Prim lane choice.
	 *
	 * @param scope
	 *            the scope
	 * @return the integer
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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
					examples = { @example ("do lane_choice new_road: a_road;") },
					deprecated = "Use `choose_lane` instead"))
	@Deprecated
	public Integer primLaneChoice(final IScope scope) throws GamaRuntimeException {
		return -1;
	}

	/**
	 * Prim choose lane.
	 *
	 * @param scope
	 *            the scope
	 * @return the integer
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = ACT_CHOOSE_LANE,
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the new road that's the vehicle is going to enter")) },
			doc = @doc (
					value = "Override this if you want to manually choose a lane when entering new road. "
							+ "By default, the vehicle tries to stay in the current lane. "
							+ "If the new road has fewer lanes than the current one and the current lane index is too big, "
							+ "it tries to enter the most uppermost lane.",
					returns = "an integer representing the lane index"))
	public Integer primChooseLane(final IScope scope) throws GamaRuntimeException {
		IAgent newRoad = (IAgent) scope.getArg("new_road", IType.AGENT);
		int numRoadLanes = RoadSkill.getNumLanes(newRoad);
		IAgent vehicle = getCurrentAgent(scope);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		int currentLowestLane = getLowestLane(vehicle);
		int linkedLaneLimit = Utils.computeLinkedLaneLimit(vehicle, newRoad);
		return Math.min(currentLowestLane, numRoadLanes + linkedLaneLimit - numLanesOccupied);
	}

	/**
	 * Select a random road among the outward edges of a given intersection node
	 *
	 * @param scope
	 * @param graph
	 *            the graph on which the vehicle is driving
	 * @param node
	 *            the intersection node whose outward edges will be considered
	 * @param roadProba
	 *            a map that specifies probabilities of choosing certain roads
	 * @return the selected road
	 */
	private IAgent chooseNextRoadRandomly(final IScope scope, final GamaSpatialGraph graph, final IAgent node,
			final Map<IAgent, Double> roadProba) {
		IAgent vehicle = getCurrentAgent(scope);

		IList<IAgent> possibleRoads = GamaListFactory.create();
		possibleRoads.addAll(RoadNodeSkill.getRoadsOut(node));
		if (canIgnoreOneway(vehicle)) {
			possibleRoads.addAll(RoadNodeSkill.getRoadsIn(node));
			possibleRoads.remove(getCurrentRoad(vehicle));
		}
		// Only consider roads in the specified graph
		possibleRoads.removeIf(r -> !graph.getEdges().contains(r));

		if (possibleRoads.isEmpty()) return null;
		if (possibleRoads.size() == 1) return possibleRoads.get(0);
		if (roadProba == null || roadProba.isEmpty()) return possibleRoads.anyValue(scope);
		IList<Double> distribution = GamaListFactory.create(Types.FLOAT);
		for (IAgent r : possibleRoads) {
			Double val = roadProba.get(r);
			distribution.add(val == null ? 0.0 : val);
		}
		return possibleRoads.get(Random.opRndChoice(scope, distribution));
	}

	/**
	 * Blocks vehicles crossing a given intersection from other roads, except the old road and the linked road of the
	 * new one. e.g. think of a vehicle running a redlight at a four-way junction
	 *
	 * @param scope
	 * @param currentRoad
	 *            the old road of the vehicle
	 * @param newRoad
	 *            the new road of the vehicle
	 * @param node
	 *            the intersection node whose blocking info will be updated
	 */
	public void blockIntersection(final IScope scope, final IAgent currentRoad, final IAgent newRoad,
			final IAgent node) {
		List<IAgent> inRoads = (List<IAgent>) node.getAttribute(RoadNodeSkill.ROADS_IN);
		if (inRoads.size() <= 1) return;
		// list of the roads that will be blocked by this vehicle
		List<IAgent> blockedRoads = GamaListFactory.create(Types.AGENT);
		for (IAgent road : inRoads) {
			// do not block the linked road associated to the new road
			if (!road.getLocation().equals(newRoad.getLocation())) { blockedRoads.add(road); }
		}
		if (!blockedRoads.isEmpty()) {
			IAgent vehicle = getCurrentAgent(scope);
			Map<IAgent, List<IAgent>> blockInfo = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
			blockInfo.put(vehicle, blockedRoads);
		}
	}

	/**
	 * Updates the `vehicle_ordering` map with the new distance to target
	 *
	 * @param scope
	 * @param newLowestLane
	 *            the new lowest lane of the vehicle
	 * @param newDistToCurrentTarget
	 *            the new distance to the vehicle's current target
	 */
	private void updateVehicleOrdering(final IScope scope, final int newLowestLane,
			final double newDistToCurrentTarget) {
		IAgent vehicle = getCurrentAgent(scope);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		int currentLowestLane = getLowestLane(vehicle);

		// Get all occupying lanes using `lowest_lane` and `num_lanes_occupied`
		Set<Integer> oldLanes = IntStream.range(currentLowestLane, currentLowestLane + numLanesOccupied).boxed()
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<Integer> newLanes = IntStream.range(newLowestLane, newLowestLane + numLanesOccupied).boxed()
				.collect(Collectors.toCollection(LinkedHashSet::new));

		IAgent correctRoad = getCurrentRoad(vehicle);
		int numLanesCorrect = RoadSkill.getNumLanes(correctRoad);
		double dist;
		for (int lane : oldLanes) { RoadSkill.getVehicleOrderingMap(scope, correctRoad, lane).remove(vehicle); }
		for (int lane : newLanes) {
			if (lane < numLanesCorrect) {
				dist = newDistToCurrentTarget;
			} else {
				dist = RoadSkill.getTotalLength(correctRoad) - newDistToCurrentTarget;
			}
			RoadSkill.getVehicleOrderingMap(scope, correctRoad, lane).put(vehicle, dist);
		}
		setDistanceToCurrentTarget(vehicle, newDistToCurrentTarget);
		setLowestLane(vehicle, newLowestLane);
	}

	/**
	 * Move across roads.
	 *
	 * @param scope
	 *            the scope
	 * @param isDrivingRandomly
	 *            the is driving randomly
	 * @param graph
	 *            the graph
	 * @param roadProba
	 *            the road proba
	 */
	private boolean moveAcrossRoads(final IScope scope, final boolean isDrivingRandomly, final GamaSpatialGraph graph,
			final Map<IAgent, Double> roadProba) {
		if (GamaExecutorService.CONCURRENCY_SPECIES.getValue())
			throw GamaRuntimeException.error("Driving agents cannot be scheduled in parallel. "
					+ "Please disable \"Make species schedule theirs agents in parallel\" "
					+ "in Preferences > Execution > Parallelism.", scope);

		IAgent vehicle = getCurrentAgent(scope);
		ISpecies context = vehicle.getSpecies();

		IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		Arguments argsEF = new Arguments();

		IAgent finalTarget = getFinalTarget(vehicle);
		Pair<Integer, Double> laneAndAccPair;
		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		int cnt = 0;
		while (remainingTime > 0) {
			cnt += 1;
			GamaPoint loc = vehicle.getLocation();
			IAgent currentTarget = getCurrentTarget(vehicle);
			GamaPoint targetLoc = currentTarget.getLocation();

			if (!isDrivingRandomly && getCurrentIndex(vehicle) == getCurrentPath(vehicle).getEdgeList().size() - 1
					&& loc.equals(finalTarget.getLocation())) { // Final node in path
				clearDrivingStates(scope);
				return true;
			}
			if (loc.equals(targetLoc)) { // Intermediate node in path
				IAgent newRoad = getNextRoad(vehicle);
				if (newRoad == null) {
					// Only raise the error if the modeler didn't do anything about the deadend
					// right when the vehicle reached it
					if (cnt > 1) return true;
					String reason = isDrivingRandomly ? "which has no outgoing roads"
							: "because it has reached the end of the path";
					throw GamaRuntimeException.warning(
							vehicle.getName() + " stopped at " + currentTarget.getName() + ", " + reason, scope);
				}
				GamaPoint srcNodeLoc = RoadSkill.getSourceNode(newRoad).getLocation();
				boolean violatingOneway = !loc.equals(srcNodeLoc);
				// check traffic lights and vehicles coming from other roads
				if (!readyToCross(scope, vehicle, currentTarget, newRoad)) {
					setSpeed(vehicle, 0.0);
					return true;
				}

				// Choose a lane on the new road
				IStatement.WithArgs actionCL = context.getAction(ACT_CHOOSE_LANE);
				Arguments argsCL = new Arguments();
				argsCL.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionCL.setRuntimeArgs(scope, argsCL);
				int lowestLane = (int) actionCL.executeOn(scope);
				laneAndAccPair = MOBIL.chooseLane(scope, vehicle, newRoad, lowestLane);
				if (laneAndAccPair == null) return false;
				double newAccel = laneAndAccPair.getRight();
				double newSpeed = computeSpeed(scope, newAccel, newRoad);
				// Check if it is possible to move onto the new road
				if (newSpeed == 0.0) {
					// TODO: this should happen once
					// double probaBlock = rescaleProba(getProbaBlockNode(vehicle), timeStep);
					setSpeed(vehicle, newSpeed);
					double probaBlock = getProbaBlockNode(vehicle);
					boolean goingToBlock = Random.opFlip(scope, probaBlock);
					IAgent currentRoad = getCurrentRoad(vehicle);
					if (currentRoad != null && goingToBlock) {
						blockIntersection(scope, currentRoad, newRoad, currentTarget);
					}
					return true;
				}
				IStatement.WithArgs actionOnNewRoad = context.getAction("on_entering_new_road");
				actionOnNewRoad.executeOn(scope);

				// external factor that affects remaining time when entering a new road
				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);
				if (remainingTime <= 0.0) return false;

				// Find the new next road in advance in order to look for leaders further ahead
				IAgent newTarget;
				IAgent nextNextRoad;
				if (!isDrivingRandomly) {
					int newEdgeIdx = getCurrentIndex(vehicle) + 1;
					newTarget = getTargets(vehicle).get(newEdgeIdx + 1);
					IPath path = getCurrentPath(vehicle);
					if (newEdgeIdx < path.getEdgeList().size() - 1) {
						nextNextRoad = (IAgent) path.getEdgeList().get(newEdgeIdx + 1);
					} else {
						nextNextRoad = null;
					}
					setCurrentIndex(vehicle, newEdgeIdx);
				} else {
					newTarget = !violatingOneway ? RoadSkill.getTargetNode(newRoad) : RoadSkill.getSourceNode(newRoad);
					nextNextRoad = chooseNextRoadRandomly(scope, graph, newTarget, roadProba);
				}
				setNextRoad(vehicle, nextNextRoad);
				setCurrentTarget(vehicle, newTarget);
				setViolatingOneway(vehicle, violatingOneway);

				unregister(scope, vehicle);
				int newLane = laneAndAccPair.getLeft();
				RoadSkill.register(scope, vehicle, newRoad, newLane);
			} else {
				IAgent currentRoad = getCurrentRoad(vehicle);
				int lowestLane = getLowestLane(vehicle);
				laneAndAccPair = MOBIL.chooseLane(scope, vehicle, currentRoad, lowestLane);
			}
			int lowestLane = laneAndAccPair.getLeft();
			double accel = laneAndAccPair.getRight();
			remainingTime = moveAcrossSegments(scope, accel, remainingTime, lowestLane);
		}

		return true;
	}

	/**
	 * Prim force move.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "force_move",
			args = { @arg (
					name = "lane",
					type = IType.INT,
					optional = false,
					doc = @doc ("the lane on which to make the agent move")),
					@arg (
							name = ACCELERATION,
							type = IType.FLOAT,
							optional = false,
							doc = @doc ("acceleration of the vehicle")),
					@arg (
							name = "time",
							type = IType.FLOAT,
							optional = false,
							doc = @doc ("time of move")) },
			doc = @doc (
					value = "action to drive by chosen randomly the next road",
					examples = { @example ("do drive_random init_node: some_node;") }))
	public double primForceMove(final IScope scope) throws GamaRuntimeException {
		Integer lane = scope.getIntArg("lane");
		Double acc = scope.getFloatArg(ACCELERATION);
		Double time = scope.getFloatArg("time");
		return moveAcrossSegments(scope, acc, time, lane);
	}

	/**
	 * Moves the vehicle from segment to segment on the current road.
	 *
	 * @param scope
	 * @param accel
	 *            acceleration
	 * @param time
	 *            the amount of time available to move
	 * @param newLowestLane
	 *            the lane to move on
	 * @return the remaining amount of time in the simulation step
	 */
	public double moveAcrossSegments(final IScope scope, final double accel, final double time,
			final int newLowestLane) {
		IAgent vehicle = getCurrentAgent(scope);
		IAgent currentRoad = getCurrentRoad(vehicle);
		IAgent currentTarget = getCurrentTarget(vehicle);
		int currentSegment = getSegmentIndex(vehicle);
		double distToGoal = getDistanceToGoal(vehicle);
		boolean violatingOneway = isViolatingOneway(vehicle);
		GamaPoint loc = vehicle.getLocation();

		double speed = getSpeed(vehicle);
		double distMoved, newSpeed;
		if (speed == 0.0 && accel == 0.0)
			// Not moving at all
			return 0.0;
		if (speed + accel * time < 0.0) {
			// Special case when there is a stopped vehicle or traffic light
			// causing the speed to have a negative value,
			// and we don't want the vehicle to move backwards
			distMoved = -0.5 * Math.pow(speed, 2) / accel;
			newSpeed = 0.0;
		} else {
			distMoved = speed * time + 0.5 * accel * Math.pow(time, 2);
			newSpeed = computeSpeed(scope, accel, currentRoad);
		}

		double remainingDist = distMoved;
		Coordinate coords[] = currentRoad.getInnerGeometry().getCoordinates();
		GamaPoint endPt =
				!violatingOneway ? new GamaPoint(coords[currentSegment + 1]) : new GamaPoint(coords[currentSegment]);
		while (remainingDist >= distToGoal || distToGoal < EPSILON) {
			if (endPt.equals(currentTarget.getLocation())) {
				// Return to the main loop in `drive` to continue moving across the intersection
				setLocation(vehicle, endPt);
				setDistanceToGoal(vehicle, 0.0);
				setDistanceToCurrentTarget(vehicle, 0.0);
				updateVehicleOrdering(scope, newLowestLane, 0.0);
				return distToGoal < EPSILON ? time : time - distToGoal / newSpeed;
			}
			// Move to a new segment
			remainingDist -= distToGoal;
			loc = endPt;
			currentSegment = !violatingOneway ? currentSegment + 1 : currentSegment - 1;
			endPt = !violatingOneway ? new GamaPoint(coords[currentSegment + 1])
					: new GamaPoint(coords[currentSegment]);
			distToGoal = RoadSkill.getSegmentLengths(currentRoad).get(currentSegment);
		}

		double ratio = remainingDist / distToGoal;
		double newX = loc.getX() + ratio * (endPt.getX() - loc.getX());
		double newY = loc.getY() + ratio * (endPt.getY() - loc.getY());
		setLocation(vehicle, new GamaPoint(newX, newY));
		setSpeed(vehicle, newSpeed);
		setAcceleration(vehicle, accel);
		setDistanceToGoal(vehicle, distToGoal - remainingDist);
		setSegmentIndex(vehicle, currentSegment);

		updateVehicleOrdering(scope, newLowestLane, getDistanceToCurrentTarget(vehicle) - distMoved);
		return 0.0;
	}

	/**
	 * Computes the speed of the vehicle with respect to its acceleration, its maximum speed and the speed limit of the
	 * current road.
	 *
	 * @param scope
	 * @param acceleration
	 *            the acceleration for this simulation step
	 * @param road
	 *            the road which the vehicle is on
	 * @return the resulting speed
	 */
	private double computeSpeed(final IScope scope, final double acceleration, final IAgent road) {
		IAgent vehicle = getCurrentAgent(scope);

		double dt = scope.getSimulation().getClock().getStepInSeconds();
		double speed = getSpeed(vehicle) + acceleration * dt;
		return Math.max(0.0, speed);
	}

	/**
	 * Clears information after the vehicle has reached the final target in its path
	 *
	 * @param scope
	 */
	private void clearDrivingStates(final IScope scope) {
		IAgent vehicle = getCurrentAgent(scope);
		setCurrentIndex(vehicle, -1);
		setCurrentTarget(vehicle, null);
		setFinalTarget(vehicle, null);
		setCurrentPath(vehicle, null);
	}

	/**
	 * Unregister.
	 *
	 * @param scope
	 *            the scope
	 * @param driver
	 *            the driver
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static boolean unregister(final IScope scope, final IAgent driver) throws GamaRuntimeException {
		IAgent currentRoad = getCurrentRoad(driver);
		if (currentRoad == null) return false;

		Integer lowestLane = (Integer) driver.getAttribute(LOWEST_LANE);
		int numLanesOccupied = (int) driver.getAttribute(NUM_LANES_OCCUPIED);
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			RoadSkill.getVehicleOrderingMap(scope, currentRoad, lane).remove(driver);
		}
		setCurrentRoad(driver, null);
		return true;
	}
	
	
	@action (
			name = "goto_drive",
			args = { @arg (
					name = "target",
					type = IType.GEOMETRY,
					optional = false,
					doc = @doc ("the entity towards which to move.")),
					@arg (
							name = IKeyword.SPEED,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("the speed to use for this move (replaces the current value of speed)")),
					@arg (
							name = "on",
							type = IType.NONE,
							optional = true,
							doc = @doc ("graph, topology, list of geometries or map of geometries that restrain this move")),
					@arg (
							name = "recompute_path",
							type = IType.BOOL,
							optional = true,
							doc = @doc ("if false, the path is not recompute even if the graph is modified (by default: true)")),
					@arg (
							name = "return_path",
							type = IType.BOOL,
							optional = true,
							doc = @doc ("if true, return the path followed (by default: false)")),
					@arg (
							name = "following",
							type = IType.PATH,
							optional = true,
							doc = @doc ("Path to follow.")) },
			doc = @doc (
					value = "moves the agent towards the target passed in the arguments.",
					returns = "optional: the path followed by the agent.",
					examples = {
							@example ("do goto_drive target: one_of road on: road_network;") }))
	public IPath primGotoDrive(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final IAgent target = (IAgent)scope.getArg("target", IType.GEOMETRY);
		IPath current_path = getCurrentPath(agent);
		final IAgent finalTarget = getFinalTarget(agent);
	
		if (target == null) {
			throw GamaRuntimeException.create(new IllegalArgumentException("target parameter in goto_drive can not be null"), scope);
		}
		
		
		if(finalTarget != null && !finalTarget.equals(target.getLocation())) { 
			// agent changed course, we have to recompute path
			
			// check if there is a given path to follow
			final IPath path = (IPath)scope.getArg("follow", IType.PATH);
			
			if (path != null && !current_path.equals(path)) {
				//we changed path, let's update it
				setCurrentPath(agent, path);
				setFinalTarget(agent, target);
				current_path = path;
			}else {
				// else we recompute path
				current_path = null;
			}
		}else if(finalTarget == null) {
			// clear current path
			current_path = null;
		}
		
		if (current_path != null) {	
			// follow your path
			primDrive(scope);
		}else {
			// compute your path
			Object o = scope.getArg("on", IType.NONE);
			scope.getExecutionContext().putLocalVar("graph", o);
			primComputePath(scope);
					
		}
		return getCurrentPath(agent);
	}
	
}
