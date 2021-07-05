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

import com.google.common.collect.Sets;

import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Coordinate;

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
import simtools.gaml.extensions.traffic.lanechange.MOBIL;
import ummisco.gama.dev.utils.DEBUG;

@vars({
	@variable(
		name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("the speed of the agent (in meter/second)")
	),
	@variable(
		name = IKeyword.REAL_SPEED,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc(
			value = "the actual speed of the agent (in meter/second)",
			deprecated = "there is now only one speed value, which is located in `speed`"
		)
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
		doc = @doc("the path which the agent is currently following")
	),
	@variable(
		name = DrivingSkill.FINAL_TARGET,
		type = IType.AGENT,
		init = "nil",
		doc = @doc("the final target of the agent")
	),
	@variable(
		name = DrivingSkill.CURRENT_TARGET,
		type = IType.AGENT,
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
		doc = @doc(
			value = "the current list of points that the agent has to reach (path)",
			deprecated = "this can be accessed using current_path.vertices"
		)
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
		name = DrivingSkill.TIME_HEADWAY,
		type = IType.FLOAT,
		init = "1.5",
		doc = @doc("the time gap that to the leading vehicle that the driver must maintain. " +
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
			"Known as the parameter 'a_th' in the MOBIL lane changing model"
		)
	),
	@variable(
		name = DrivingSkill.ACC_BIAS,
		type = IType.FLOAT,
		init = "0.25",
		doc = @doc("the bias term used for asymmetric lane changing, parameter 'a_bias' in MOBIL")
	),
	@variable(
		name = DrivingSkill.LC_COOLDOWN,
		type = IType.FLOAT,
		init = "4",
		doc = @doc("the duration that a vehicle must wait before changing lanes again")
	),
	@variable(
		name = DrivingSkill.TIME_SINCE_LC,
		type = IType.FLOAT,
		init = "0.0",
		doc = @doc("the elapsed time since the last lane change")
	),
	@variable(
		name = DrivingSkill.IGNORE_ONEWAY,
		type = IType.BOOL,
		init = "false",
		doc = @doc("if set to `true`, the vehicle will be able to violate one-way traffic rule")
	),
	@variable(
		name = DrivingSkill.VIOLATING_ONEWAY,
		type = IType.BOOL,
		init = "false",
		doc = @doc("indicates if the vehicle is moving in the wrong direction on an one-way (unlinked) road")
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
		name = DrivingSkill.PROBA_LANE_CHANGE_UP,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc(
			value = "probability to change to a upper lane (left lane if right side driving) to gain acceleration, within one second",
			deprecated = "use MOBIL parameters to control this instead"
		)
	),
	@variable(
		name = DrivingSkill.PROBA_LANE_CHANGE_DOWN,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc(
			value = "probability to change to a lower lane (right lane if right side driving) to gain acceleration, within one second",
			deprecated = "use MOBIL parameters to control this instead"
		)
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
	@Deprecated public static final String PROBA_LANE_CHANGE_UP = "proba_lane_change_up";
	@Deprecated public static final String PROBA_LANE_CHANGE_DOWN = "proba_lane_change_down";
	public static final String PROBA_RESPECT_PRIORITIES = "proba_respect_priorities";
	public static final String PROBA_RESPECT_STOPS = "proba_respect_stops";
	public static final String PROBA_BLOCK_NODE = "proba_block_node";
	public static final String PROBA_USE_LINKED_ROAD = "proba_use_linked_road";
	public static final String RIGHT_SIDE_DRIVING = "right_side_driving";
	public static final String IGNORE_ONEWAY = "ignore_oneway";
	public static final String VIOLATING_ONEWAY = "violating_oneway";
	public static final String ON_LINKED_ROAD = "on_linked_road";
	public static final String USING_LINKED_ROAD = "using_linked_road";
	public static final String LINKED_LANE_LIMIT = "linked_lane_limit";
	public static final String ALLOWED_LANES = "allowed_lanes";
	@Deprecated public static final String TARGETS = "targets";
	public static final String CURRENT_TARGET = "current_target";
	public static final String CURRENT_INDEX = "current_index";
	public static final String FINAL_TARGET = "final_target";
	public static final String CURRENT_PATH = "current_path";
	public static final String ACCELERATION = "acceleration";
	public static final String MAX_ACCELERATION = "max_acceleration";
	public static final String MAX_DECELERATION = "max_deceleration";
	public static final String TIME_HEADWAY = "time_headway";
	public static final String DELTA_IDM = "delta_idm";
	public static final String POLITENESS_FACTOR = "politeness_factor";
	public static final String MAX_SAFE_DECELERATION = "max_safe_deceleration";
	public static final String ACC_GAIN_THRESHOLD = "acc_gain_threshold";
	public static final String ACC_BIAS = "acc_bias";
	public static final String TIME_SINCE_LC = "time_since_lane_change";
	public static final String LC_COOLDOWN = "lane_change_cooldown";
	public static final String SPEED_COEFF = "speed_coeff";
	public static final String MAX_SPEED = "max_speed";
	public static final String SEGMENT_INDEX = "segment_index_on_road";
	public static final String NUM_LANES_OCCUPIED = "num_lanes_occupied";
	public static final String LANE_CHANGE_LIMIT = "lane_change_limit";
	public static final String LEADING_VEHICLE = "leading_vehicle";
	public static final String LEADING_DISTANCE = "leading_distance";
	public static final String LEADING_SPEED = "leading_speed";

	@getter (IKeyword.SPEED)
	public static double getSpeed(final IAgent vehicle) {
		if (vehicle == null || vehicle.getSpecies().implementsSkill(RoadNodeSkill.SKILL_ROAD_NODE)) {
			return 0.0;
		} else {
			return (Double) vehicle.getAttribute(IKeyword.SPEED);
		}
	}

	@setter (IKeyword.SPEED)
	public static void setSpeed(final IAgent vehicle, final double speed) {
		if (vehicle == null) { return; }
		vehicle.setAttribute(IKeyword.SPEED, speed);
	}

	@getter(IKeyword.REAL_SPEED)
	@Deprecated
	public static double getRealSpeed(final IAgent vehicle) {
		return getSpeed(vehicle);
	}

	@setter(IKeyword.REAL_SPEED)
	@Deprecated
	public static void setRealSpeed(final IAgent vehicle, final double speed) {
		setSpeed(vehicle, speed);
	}

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

	@getter(TIME_HEADWAY)
	public static double getTimeHeadway(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(TIME_HEADWAY);
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

	@getter(ACC_BIAS)
	public static double getAccBias(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(ACC_BIAS);
	}

	@getter(TIME_SINCE_LC)
	public static double getTimeSinceLC(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(TIME_SINCE_LC);
	}

	// @setter(TIME_SINCE_LC)
	public static void setTimeSinceLCReadOnly(final IAgent vehicle, final double time) {
		// read-only
	}

	@setter(TIME_SINCE_LC)
	public static void setTimeSinceLC(final IAgent vehicle, final double time) {
		vehicle.setAttribute(TIME_SINCE_LC, time);
	}

	@getter(LC_COOLDOWN)
	public static double getLCCooldown(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(LC_COOLDOWN);
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
	public static IAgent getCurrentTarget(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(CURRENT_TARGET);
	}

	@setter(CURRENT_TARGET)
	public static void setCurrentTarget(final IAgent vehicle, final IAgent target) {
		vehicle.setAttribute(CURRENT_TARGET, target);
	}

	@getter(FINAL_TARGET)
	public static IAgent getFinalTarget(final IAgent vehicle) {
		return (IAgent) vehicle.getAttribute(FINAL_TARGET);
	}

	@setter(FINAL_TARGET)
	public static void setFinalTarget(final IAgent vehicle, final IAgent point) {
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

	@getter(CURRENT_PATH)
	public static IPath getCurrentPath(final IAgent vehicle) {
		return (IPath) vehicle.getAttribute(CURRENT_PATH);
	}

	@setter(CURRENT_PATH)
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

	public static List<IAgent> getTargets(final IAgent vehicle) {
		IPath path = getCurrentPath(vehicle);
		return path == null ? null : path.getVertexList();
	}

	@setter(TARGETS)
	public static void setTargets(final IAgent vehicle, final List<IAgent> points) {
		// read-only
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
	@Deprecated
	public static double getProbaLaneChangeDown(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	@setter(PROBA_LANE_CHANGE_DOWN)
	@Deprecated
	public static void setProbaLaneChangeDown(final IAgent vehicle, final Double proba) {
		vehicle.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	@getter(PROBA_LANE_CHANGE_UP)
	@Deprecated
	public static double getProbaLaneChangeUp(final IAgent vehicle) {
		return (Double) vehicle.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	@setter(PROBA_LANE_CHANGE_UP)
	@Deprecated
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

	@getter(IGNORE_ONEWAY)
	public static boolean canIgnoreOneway(final IAgent vehicle) {
		return (boolean) vehicle.getAttribute(IGNORE_ONEWAY);
	}

	@getter(VIOLATING_ONEWAY)
	public static boolean isViolatingOneway(final IAgent vehicle) {
		return (boolean) vehicle.getAttribute(VIOLATING_ONEWAY);
	}

	@setter(VIOLATING_ONEWAY)
	public static void setViolatingOneway(final IAgent vehicle, final boolean violatingOneway) {
		vehicle.setAttribute(VIOLATING_ONEWAY, violatingOneway);
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
			deprecated = "apparently this action does not do what is described in the documentation",
			value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)",
			returns = "the remaining time",
			examples = { @example ("do osm_follow path: the_path on: road_network;") }
		)
	)
	@Deprecated
	public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
		// Double t = scope.hasArg("time") ? scope.getFloatArg("time") : scope.getClock().getStepInSeconds();
		// GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		// return moveToNextLocAlongPathOSM(scope, t, path);
		return 0.0;
	}

	@action(
		name = "ready_to_cross",
		args = {
			@arg(
				name = "node",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road node to test")
			),
			@arg(
				name = "new_road",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the road to test")
			)
		},
		doc = @doc(
			value = "action to test if the vehicle cross a road node to move to a new road",
			returns = "true if the vehicle can cross the road node, false otherwise",
			examples = { @example ("do is_ready_next_road new_road: a_road lane: 0;") }
		)
	)
	public Boolean primReadyToCross(final IScope scope) throws GamaRuntimeException {
		IAgent vehicle = (IAgent) getCurrentAgent(scope);
		IAgent node = (IAgent) scope.getArg("node", IType.AGENT);
		IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		return readyToCross(scope, vehicle, node, road);
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
	public static Boolean readyToCross(final IScope scope,
			final IAgent vehicle,
			final IAgent node,
			final IAgent newRoad) throws GamaRuntimeException {
		double vehicleLength = getVehicleLength(vehicle);
		ISpecies context = vehicle.getSpecies();

		// TODO: refactor this
		// additional conditions to cross the intersection, defined by the user
		// IStatement.WithArgs actionTNR = context.getAction("test_next_road");
		// Arguments argsTNR = new Arguments();
		// argsTNR.put("new_road", ConstantExpressionDescription.create(newRoad));
		// actionTNR.setRuntimeArgs(scope, argsTNR);
		// if (!(Boolean) actionTNR.executeOn(scope)) { return false; }

		IAgent currentRoad = (IAgent) vehicle.getAttribute(CURRENT_ROAD);
		// Don't need to do these checks if the vehicle was just initialized
		if (currentRoad != null) {
			// Check traffic lights
			List<List> stops = (List<List>) node.getAttribute(RoadNodeSkill.STOP);
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
				node.getAttribute(RoadNodeSkill.BLOCK);
			Collection<IAgent> blockingVehicles = new HashSet<>(blockInfo.keySet());
			// check if any blocking vehicle has moved
			for (IAgent otherVehicle : blockingVehicles) {
				if (!otherVehicle.getLocation().equals(node.getLocation())) {
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
			List<IAgent> priorityRoads = (List<IAgent>) node.getAttribute(RoadNodeSkill.PRIORITY_ROADS);
			boolean onPriorityRoad = priorityRoads != null && priorityRoads.contains(currentRoad);

			// compute angle between the current & next road
			// double angleRef = Punctal.angleInDegreesBetween(scope, (GamaPoint) intersectionNode.getLocation(),
			// 		(GamaPoint) currentRoad.getLocation(), (GamaPoint) newRoad.getLocation());

			// TODO: adjust the speed diff condition
			// TODO: always return false if vehicle decides to make an U-turn
			// double speed = Math.max(0.5, getSpeed(vehicle) + getMaxAcceleration(vehicle));
			// double safetyDistCoeff = vehicle.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(vehicle)
			// 		: getSecurityDistanceCoeff(vehicle);

			// List<IAgent> roadsIn = (List) intersectionNode.getAttribute(RoadNodeSkill.ROADS_IN);
			// for (IAgent otherInRoad : roadsIn) {
			// 	if (otherInRoad == currentRoad) {
			// 		continue;
			// 	}
			// 	double angle = Punctal.angleInDegreesBetween(scope, (GamaPoint) intersectionNode.getLocation(),
			// 			(GamaPoint) currentRoad.getLocation(), (GamaPoint) otherInRoad.getLocation());
			// 	boolean otherRoadIsPriortized = priorityRoads != null && priorityRoads.contains(otherInRoad);
			// 	boolean hasPriority = onPriorityRoad && !otherRoadIsPriortized;
			// 	boolean shouldRespectPriority = !onPriorityRoad && otherRoadIsPriortized;
			// 	// be careful of vehicles coming from the right/left side
			// 	if (!hasPriority
			// 			&& (shouldRespectPriority || rightSide && angle > angleRef || !rightSide && angle < angleRef)) {
			// 		List<IAgent> otherVehicles = (List) otherInRoad.getAttribute(RoadSkill.ALL_AGENTS);
			// 		for (IAgent otherVehicle : otherVehicles) {
			// 			if (otherVehicle == null || otherVehicle.dead()) {
			// 				continue;
			// 			}
			// 			double otherVehicleLength = getVehicleLength(otherVehicle);
			// 			double otherSpeed = getSpeed(otherVehicle);
			// 			double dist = otherVehicle.euclidianDistanceTo(vehicle);

			// 			if (Maths.round(getSpeed(otherVehicle), 1) > 0.0 &&
			// 					0.5 + safetyDistCoeff * Math.max(0, speed - otherSpeed) >
			// 					dist - (vehicleLength / 2 + otherVehicleLength / 2)) {
			// 				return false;
			// 			}
			// 		}
			// 	}
			// }
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

		// allow reverse travel on road to compute path for agents on road side only
		if (canIgnoreOneway(vehicle)) {
			graph.setDirected(false);
		}

		if (target != null) {
			// TODO: why do we make source an arg again?
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

		// restore graph direction
		graph.setDirected(true);

		if (path != null && !path.getEdgeGeometry().isEmpty()) {
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
			setCurrentTarget(vehicle, initNode);
			setNextRoad(vehicle, chooseNextRoadRandomly(scope, graph, initNode, roadProba));
		}

		Pair<Integer, Double> laneAndAccPair;
		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		while (remainingTime > 0) {
			ILocation loc = vehicle.getLocation();
			IAgent target = getCurrentTarget(vehicle);
			GamaPoint targetLoc = (GamaPoint) target.getLocation();

			if (loc.equals(targetLoc)) {
				// At the end point of a road
				IAgent newRoad = getNextRoad(vehicle);
				if (!readyToCross(scope, vehicle, target, newRoad)) {
					return;
				}
				GamaPoint srcNodeLoc = (GamaPoint) RoadSkill.getSourceNode(newRoad).getLocation();
				boolean violatingOneway = !loc.equals(srcNodeLoc);
				IAgent newTarget = !violatingOneway ? RoadSkill.getTargetNode(newRoad) :
						RoadSkill.getSourceNode(newRoad);

				// Choose a lane on the new road
				int firstSegment = !violatingOneway ? 0 : RoadSkill.getNumSegments(newRoad) - 1;
				int secondPtIdx = !violatingOneway ? 1 : RoadSkill.getNumSegments(newRoad) - 1;
				GamaPoint firstSegmentEndPt = new GamaPoint(newRoad.getInnerGeometry().getCoordinates()[secondPtIdx]);
				double firstSegmentLength = loc.euclidianDistanceTo(firstSegmentEndPt);
				laneAndAccPair = MOBIL.chooseLane(scope, vehicle, newTarget, newRoad, firstSegment, firstSegmentLength);
				if (laneAndAccPair == null) {
					return;
				}
				double newAccel = laneAndAccPair.getValue();
				double speed = computeSpeed(scope, newAccel, newRoad);
				// Check if it is possible to move onto the new road
				if (speed == 0.0) {
					// TODO: this should only happen once
					// double probaBlock = rescaleProba(getProbaBlockNode(vehicle), timeStep);
					double probaBlock = getProbaBlockNode(vehicle);
					boolean goingToBlock = Random.opFlip(scope, probaBlock);
					IAgent currentRoad = getCurrentRoad(vehicle);
					if (currentRoad != null && goingToBlock) {
						blockIntersection(scope, currentRoad, newRoad, getCurrentTarget(vehicle));
					}
					return;
				}
				IStatement.WithArgs actionOnNewRoad = context.getAction("on_entering_new_road");
				actionOnNewRoad.executeOn(scope);

				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);

				int newLane = laneAndAccPair.getKey();

				setCurrentTarget(vehicle, newTarget);
				RoadSkill.unregister(scope, vehicle);
				RoadSkill.register(scope, vehicle, newRoad, newLane);
				// Choose the next road in advance
				IAgent nextRoad = chooseNextRoadRandomly(scope, graph, newTarget, roadProba);
				setNextRoad(vehicle, nextRoad);
				setViolatingOneway(vehicle, violatingOneway);
			} else {
				IAgent currentRoad = getCurrentRoad(vehicle);
				IAgent currentTarget = getCurrentTarget(vehicle);
				int currentSegment = getSegmentIndex(vehicle);
				double distToGoal = getDistanceToGoal(vehicle);
				laneAndAccPair = MOBIL.chooseLane(scope, vehicle, currentTarget, currentRoad, currentSegment, distToGoal);
			}
			int lowestLane = laneAndAccPair.getLeft();
			double accel = laneAndAccPair.getRight();
			remainingTime = move(scope, accel, remainingTime, lowestLane);
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
		IAgent vehicle = getCurrentAgent(scope);

		Set<IAgent> possibleRoads = new HashSet<>();
		possibleRoads.addAll(RoadNodeSkill.getRoadsOut(node));
		if (canIgnoreOneway(vehicle)) {
			possibleRoads.addAll(RoadNodeSkill.getRoadsIn(node));
			possibleRoads.remove(getCurrentRoad(vehicle));
		}
		// Only consider roads in the specified graph
		possibleRoads.removeIf(r -> !graph.getEdges().contains(r));

		if (possibleRoads.isEmpty()) {
			return null;
		} else if (possibleRoads.size() == 1) {
			return possibleRoads.iterator().next();
		} else {
			List<IAgent> roadList = new ArrayList<>(possibleRoads);
			if (roadProba == null || roadProba.isEmpty()) {
				return roadList.get(scope.getRandom().between(0, roadList.size() - 1));
			} else {
				IList<Double> distribution = GamaListFactory.create(Types.FLOAT);
				for (IAgent r : roadList) {
					Double val = roadProba.get(r);
					distribution.add(val == null ? 0.0 : val);
				}
				return roadList.get(Random.opRndChoice(scope, distribution));
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
		if (vehicle == null || vehicle.dead() || getFinalTarget(vehicle) == null) {
			return;
		}

		GamaPoint finalTargetLoc = (GamaPoint) getFinalTarget(vehicle).getLocation();
		IPath path = getCurrentPath(vehicle);

		// some preparations to execute other actions during the loop
		ISpecies context = vehicle.getSpecies();
		IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		Arguments argsEF = new Arguments();

		// Initialize the first road
		if (getCurrentIndex(vehicle) == -1) {
			setNextRoad(vehicle, (IAgent) path.getEdgeList().get(0));
		}

		Pair<Integer, Double> laneAndAccPair;
		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		// main loop to move the agent until the simulation step ends
		while (remainingTime > 0) {
			ILocation loc = vehicle.getLocation();
			IAgent target = getCurrentTarget(vehicle);
			GamaPoint targetLoc = (GamaPoint) target.getLocation();

			if (loc.equals(finalTargetLoc)) {  // Final node in path
				clearDrivingStates(scope);
				return;
			} else if (loc.equals(targetLoc)) {  // Intermediate node in path
				// get next road in path
				IAgent newRoad = getNextRoad(vehicle);
				int newEdgeIdx = getCurrentIndex(vehicle) + 1;
				IAgent newTarget = getTargets(vehicle).get(newEdgeIdx + 1);
				// check traffic lights and vehicles coming from other roads
				if (!readyToCross(scope, vehicle, target, newRoad)) {
					return;
				}

				GamaPoint srcNodeLoc = (GamaPoint) RoadSkill.getSourceNode(newRoad).getLocation();
				boolean violatingOneway = !loc.equals(srcNodeLoc);
				int firstSegment = !violatingOneway ? 0 : RoadSkill.getNumSegments(newRoad) - 1;
				int secondPtIdx = !violatingOneway ? 1 : RoadSkill.getNumSegments(newRoad) - 1;
				GamaPoint firstSegmentEndPt = new GamaPoint(newRoad.getInnerGeometry().getCoordinates()[secondPtIdx]);
				double firstSegmentLength = loc.euclidianDistanceTo(firstSegmentEndPt);
				// Choose a lane on the new road
				laneAndAccPair = MOBIL.chooseLane(scope, vehicle, newTarget, newRoad, firstSegment, firstSegmentLength);
				if (laneAndAccPair == null) {
					return;
				}
				double newAccel = laneAndAccPair.getRight();
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
				IStatement.WithArgs actionOnNewRoad = context.getAction("on_entering_new_road");
				actionOnNewRoad.executeOn(scope);

				// external factor that affects remaining time when entering a new road
				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);

				int newLane = laneAndAccPair.getLeft();

				setCurrentIndex(vehicle, newEdgeIdx);
				setCurrentTarget(vehicle, newTarget);
				RoadSkill.unregister(scope, vehicle);
				RoadSkill.register(scope, vehicle, newRoad, newLane);

				setViolatingOneway(vehicle, violatingOneway);

				if (newEdgeIdx < path.getEdgeList().size() - 1) {
					setNextRoad(vehicle, (IAgent) path.getEdgeList().get(newEdgeIdx + 1));
				} else {
					setNextRoad(vehicle, null);
				}
			} else {
				IAgent currentRoad = getCurrentRoad(vehicle);
				IAgent currentTarget = getCurrentTarget(vehicle);
				int currentSegment = getSegmentIndex(vehicle);
				double distToGoal = getDistanceToGoal(vehicle);
				laneAndAccPair = MOBIL.chooseLane(scope, vehicle, currentTarget, currentRoad, currentSegment, distToGoal);
			}
			int lowestLane = laneAndAccPair.getLeft();
			double accel = laneAndAccPair.getRight();
			remainingTime = move(scope, accel, remainingTime, lowestLane);
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
			deprecated = "There's no apparent use case for overriding this, since Intelligent Driving Model was implemented"
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
			deprecated = "There's no apparent use case for overriding this, since MOBIL lane changing model was implemented"
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
	 * @param accel         acceleration
	 * @param time          the amount of time available to move
	 * @param newLowestLane the lane to move on
	 * @return
	 */
	private double move(final IScope scope,
			final double accel,
			final double time,
			final int newLowestLane) {
		IAgent vehicle = getCurrentAgent(scope);
		IAgent currentRoad = getCurrentRoad(vehicle);
		IAgent currentTarget = getCurrentTarget(vehicle);
		int currentSegment = getSegmentIndex(vehicle);
		double distToGoal = getDistanceToGoal(vehicle);
		boolean violatingOneway = isViolatingOneway(vehicle);
		GamaPoint loc = (GamaPoint) vehicle.getLocation();

		double speed = getSpeed(vehicle);
		double distMoved, newSpeed;
		if (speed + accel * time > 0.0) {
			distMoved = speed * time + 0.5 * accel * Math.pow(time, 2);
			newSpeed = computeSpeed(scope, accel, currentRoad);
		} else {
			// Special case when there is a stopped vehicle or traffic light
			distMoved = -0.5 * Math.pow(speed, 2) / accel;
			newSpeed = 0.0;
		}

		Coordinate coords[] = currentRoad.getInnerGeometry().getCoordinates();
		GamaPoint endPt = !violatingOneway ?
			new GamaPoint(coords[currentSegment + 1]) : new GamaPoint(coords[currentSegment]);
		if (distMoved > distToGoal) {
			if (endPt.equals(currentTarget.getLocation())) {
				// Move to a new road
				// NOTE: although distToGoal but won't never reach exactly zero as the vehicle move towards a stopping leader,
				// this block will eventually be triggered when distMoved > distToGoal, due to approximation errors in IDM
				setLocation(vehicle, endPt);
				setDistanceToGoal(vehicle, 0.0);
				// Return to the main loop in `drive` to continue moving across the intersection
				return distToGoal / speed;
			} else {
				// Move to a new segment
				distMoved -= distToGoal;
				loc = endPt;
				currentSegment = !violatingOneway ? currentSegment + 1 : currentSegment - 1;
				endPt = !violatingOneway ?
					new GamaPoint(coords[currentSegment + 1]) : new GamaPoint(coords[currentSegment]);
				distToGoal = loc.distance(endPt);
			}
		}

		double ratio = distMoved / distToGoal;
		double newX = loc.getX() + ratio * (endPt.getX() - loc.getX());
		double newY = loc.getY() + ratio * (endPt.getY() - loc.getY());
		setLocation(vehicle, new GamaPoint(newX, newY));
		updateLaneSegment(scope, newLowestLane, currentSegment);
		setDistanceToGoal(vehicle, distToGoal - distMoved);
		setSpeed(vehicle, newSpeed);
		setAcceleration(vehicle, accel);
		return 0.0;
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

		double dt = scope.getSimulation().getClock().getStepInSeconds();
		double speed = getSpeed(vehicle) + acceleration * dt;
		speed = Math.min(speed, getSpeedCoeff(vehicle) * RoadSkill.getMaxSpeed(road));
		speed = Math.max(0.0, speed);
		return speed;
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

	// TODO: this action is not overridden
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
