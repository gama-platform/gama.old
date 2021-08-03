/*******************************************************************************************************
 *
 * simtools.gaml.extensions.traffic.RoadSkill.java, in plugin simtools.gaml.extensions.traffic, is part of the source
 * code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
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
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({
	@variable(
		name = RoadSkill.AGENTS_ON,
		type = IType.LIST,
		of = IType.LIST,
		doc = @doc("for each lane of the road, the list of agents for each segment")
	),
	@variable(
		name = RoadSkill.ALL_AGENTS,
		type = IType.LIST,
		of = IType.AGENT,
		doc = @doc("the list of agents on the road")
	),
	@variable(
		name = RoadSkill.SOURCE_NODE,
		type = IType.AGENT,
		doc = @doc("the source node of the road")
	),
	@variable(
		name = RoadSkill.TARGET_NODE,
		type = IType.AGENT,
		doc = @doc("the target node of the road")
	),
	@variable(
		name = RoadSkill.LANES,
		type = IType.INT,
		doc = @doc(
			value = "the number of lanes",
			deprecated = "use num_lanes instead"
		)
	),
	@variable(
		name = RoadSkill.NUM_LANES,
		type = IType.INT,
		init = "2",
		doc = @doc("the number of lanes")
	),
	@variable(
		name = RoadSkill.NUM_SEGMENTS,
		type = IType.INT,
		doc = @doc("the number of road segments")
	),
	@variable(
		name = RoadSkill.LINKED_ROAD,
		type = ITypeProvider.OWNER_TYPE,
		doc = @doc("the linked road: the lanes of this linked road will be usable by drivers on the road")
	),
	@variable(
		name = RoadSkill.MAXSPEED,
		type = IType.FLOAT,
		init = "50#km/#h",
		doc = @doc("the maximal speed on the road")
	),
	@variable(
		name = RoadSkill.SEGMENT_LENGTHS,
		type = IType.LIST,
		depends_on = IKeyword.SHAPE,
		doc = @doc("stores the length of each road segment. " +
			"The index of each element corresponds to the segment index.")
	),
	@variable(
		name = RoadSkill.VEHICLE_ORDERING,
		type = IType.MAP,
		depends_on = {RoadSkill.NUM_LANES},
		doc = @doc("provides information about the ordering of vehicle on any given lane")
	),
})
@skill(
	name = RoadSkill.SKILL_ROAD,
	concept = { IConcept.TRANSPORT, IConcept.SKILL },
	doc = @doc ("A skill for agents representing roads in traffic simulations")
)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class RoadSkill extends Skill {
	public static final String SKILL_ROAD = "skill_road";

	// TODO: rename these two lists?
	public static final String ALL_AGENTS = "all_agents";
	public static final String AGENTS_ON = "agents_on";
	public static final String SOURCE_NODE = "source_node";
	public static final String TARGET_NODE = "target_node";
	// TODO: rename to speed_limit
	public static final String MAXSPEED = "maxspeed";
	public static final String LINKED_ROAD = "linked_road";
	@Deprecated public static final String LANES = "lanes";
	public static final String NUM_LANES = "num_lanes";
	public static final String NUM_SEGMENTS = "num_segments";
	public static final String SEGMENT_LENGTHS = "segment_lengths";
	public static final String VEHICLE_ORDERING = "vehicle_ordering";

	@getter(AGENTS_ON)
	public static List getAgentsOn(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS_ON);
	}

	@setter(AGENTS_ON)
	public static void setAgentsOn(final IAgent agent, final List agents) {
		agent.setAttribute(AGENTS_ON, agents);
	}

	@getter(ALL_AGENTS)
	public static List getAgents(final IAgent agent) {
		return (List) agent.getAttribute(ALL_AGENTS);
	}

	@setter(ALL_AGENTS)
	public static void setAgents(final IAgent agent, final List agents) {
		agent.setAttribute(ALL_AGENTS, agents);
	}

	@getter(SOURCE_NODE)
	public static IAgent getSourceNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(SOURCE_NODE);
	}

	@setter(SOURCE_NODE)
	public static void setSourceNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(SOURCE_NODE, nd);
	}

	@getter(TARGET_NODE)
	public static IAgent getTargetNode(final IAgent agent) {
		return (IAgent) agent.getAttribute(TARGET_NODE);
	}

	@setter(TARGET_NODE)
	public void setTargetNode(final IAgent agent, final IAgent nd) {
		agent.setAttribute(TARGET_NODE, nd);
	}

	@getter(LANES)
	public static int getLanes(final IAgent agent) {
		return getNumLanes(agent);
	}

	@setter(LANES)
	public static void setLanes(final IAgent agent, final int numLanes) {
		setNumLanes(agent, numLanes);
	}

	@getter(NUM_LANES)
	public static int getNumLanes(final IAgent agent) {
		return (int) agent.getAttribute(NUM_LANES);
	}

	@setter(NUM_LANES)
	public static void setNumLanes(final IAgent agent, final int numLanes) {
		if (numLanes == 0) {
			GamaRuntimeException.warning(agent.getName() + " has zero lanes",
					GAMA.getRuntimeScope());
		}
		if (agent.getAttribute(NUM_LANES) == null ||
				numLanes != getNumLanes(agent) ||
				getVehicleOrdering(agent) == null) {
			List<TreeBidiMap<IAgent, Double>> res = new LinkedList<>();
			//TODO: should this be initialized somewhere else?	
			for (int i = 0; i < numLanes; i += 1) {
				res.add(new TreeBidiMap<IAgent, Double>());
			}
			setVehicleOrdering(agent, res);
		}
		agent.setAttribute(NUM_LANES, numLanes);
	}

	@getter(MAXSPEED)
	public static Double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAXSPEED);
	}

	@setter(MAXSPEED)
	public static void setMaxSpeed(final IAgent agent, final Double sp) {
		agent.setAttribute(MAXSPEED, sp);
	}

	@getter(LINKED_ROAD)
	public static IAgent getLinkedRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(LINKED_ROAD);
	}

	@setter(LINKED_ROAD)
	public static void setLinkedRoad(final IAgent agent, final IAgent rd) {
		agent.setAttribute(LINKED_ROAD, rd);
	}

	@getter(NUM_SEGMENTS)
	public static int getNumSegments(final IAgent road) {
		return GeometryUtils.getPointsOf(road).length - 1;
	}

	@setter(NUM_SEGMENTS)
	public static void setNumSegments(final IAgent road, final int numSegments) {
		// read-only
	}

	@getter(value = SEGMENT_LENGTHS, initializer = true)
	public static List<Double> getSegmentLengths(final IAgent road) {
		List<Double> res = (List<Double>) road.getAttribute(SEGMENT_LENGTHS);
		if (res == null) {
			res = GamaListFactory.create();
			Coordinate[] coords = road.getInnerGeometry().getCoordinates();
			for (int i = 0; i < coords.length - 1; i += 1)  {
				res.add(coords[i].distance(coords[i + 1]));
			}
		}
		return res;
	}

	@getter(value = VEHICLE_ORDERING, initializer = true)
	public static List<TreeBidiMap<IAgent, Double>> getVehicleOrdering(final IAgent road) {
		return (List<TreeBidiMap<IAgent, Double>>) road.getAttribute(VEHICLE_ORDERING);
	}

	// @setter(VEHICLE_ORDERING, initializer = true)
	public static void setVehicleOrdering(final IAgent road, List<TreeBidiMap<IAgent, Double>> list) {
		road.setAttribute(VEHICLE_ORDERING, list);
	}

	//TODO: update doc string
	/**
	 * Converts the "overflow" lane to the correct lane index on the linked road.
	 * Simply returns the input lane if it is already a valid lane on the current road.
	 * Computes the correct segment index on the linked road.
	 * @param scope
	 * @param lane the input lane index
	 * @return
	 */
	public static List<IAgent> getVehiclesOnLaneSegment(final IScope scope,
			final IAgent correctRoad,
			final int lane,
			final int segment) {
		int numLanesCorrect = getNumLanes(correctRoad);
		IAgent actualRoad;
		int actualLane, actualSegment;

		if (lane < numLanesCorrect) {
			actualRoad = correctRoad;
			actualLane = lane;
			actualSegment = segment;
		} else {
			IAgent linkedRoad = getLinkedRoad(correctRoad);
			int numLanesLinked = getNumLanes(linkedRoad);
			actualRoad = linkedRoad;
			actualLane = numLanesCorrect + numLanesLinked - 1 - lane;
			actualSegment = getNumSegments(actualRoad) - segment - 1;
		}

		List<List<List<IAgent>>> vehicles = RoadSkill.getAgentsOn(actualRoad);
		return vehicles.get(actualLane).get(actualSegment);
	}

	@action(
		name = "register",
		args = {
			@arg(
				name = "agent",
				type = IType.AGENT,
				optional = false,
				doc = @doc("the agent to register on the road.")
			),
			@arg(
				name = "lane",
				type = IType.INT,
				optional = false,
				doc = @doc("the lane index on which to register; if lane index >= number of lanes, then register on the linked road")
			)
		},
		doc = @doc(
			value = "register the agent on the road at the given lane",
			examples = { @example ("do register agent: the_driver lane: 0") }
		)
	)
	public void primRegister(final IScope scope) throws GamaRuntimeException {
		final IAgent road = getCurrentAgent(scope);
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		int lane = scope.getIntArg("lane");

		register(scope, driver, road, lane);
	}

	/**
	 * Registers the driver on the specified road and starting lane.
	 *
	 * @param scope
	 * @param vehicle the agent to register
	 * @param road the new road
	 * @param lowestLane the new starting lane
	 *
	 * @throws GamaRuntimeException
	 */
	public static void register(IScope scope, IAgent vehicle, IAgent road, int lowestLane)
			throws GamaRuntimeException {
		if (vehicle == null) return;

		int numLanesOccupied = DrivingSkill.getNumLanesOccupied(vehicle);

		GamaPoint roadEndPt = (GamaPoint) getTargetNode(road).getLocation();
		boolean violatingOneway = !DrivingSkill.getCurrentTarget(vehicle).getLocation().equals(roadEndPt);

		// TODO: why not just set to 0
		int indexSegment = !violatingOneway ? 0 : getNumSegments(road) - 1;
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			List<IAgent> newVehicleList = getVehiclesOnLaneSegment(scope, road, lane, indexSegment);
			newVehicleList.add(vehicle);
		}
		getAgents(road).add(vehicle);

		DrivingSkill.setViolatingOneway(vehicle, violatingOneway);
		int numSegments = getNumSegments(road);
		List<Double> lengths = getSegmentLengths(road);
		if (!violatingOneway) {
			DrivingSkill.setDistanceToGoal(vehicle, lengths.get(0));
		} else {
			DrivingSkill.setDistanceToGoal(vehicle, lengths.get(numSegments - 1));
		}
		DrivingSkill.setCurrentRoad(vehicle, road);
		DrivingSkill.setLowestLane(vehicle, lowestLane);
		DrivingSkill.setSegmentIndex(vehicle, indexSegment);
	}

	@action(
		name = "unregister",
		args = {
			@arg(
				name = "agent",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("the agent to unregister on the road.")
			)
		},
		doc = @doc(
			value = "unregister the agent on the road",
			examples = { @example ("do unregister agent: the_driver") }
		)
	)
	public void primUnregister(final IScope scope) throws GamaRuntimeException {
		IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
		unregister(scope, driver);
	}

	/**
	 * Unregisters the driver from all the roads that it's currently on.
	 *
	 * @param scope
	 * @param driver the agent that we want to unregister.
	 *
	 * @throws GamaRuntimeException
	 */
	public static void unregister(IScope scope, final IAgent driver)
			throws GamaRuntimeException {
		IAgent currentRoad = (IAgent) driver.getAttribute(DrivingSkill.CURRENT_ROAD);
		if (currentRoad == null) return;

		Integer lowestLane = (Integer) driver.getAttribute(DrivingSkill.LOWEST_LANE);
		int numLanesOccupied = (int) driver.getAttribute(DrivingSkill.NUM_LANES_OCCUPIED);
		int currentSegment = DrivingSkill.getSegmentIndex(driver);
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			List<IAgent> oldVehicleList = getVehiclesOnLaneSegment(
					scope, currentRoad, lane, currentSegment);
			oldVehicleList.remove(driver);
		}
		getAgents(currentRoad).remove(driver);
	}

	@action(
		name = "update_lanes",
		args = {
			@arg(
				name = "lanes",
				type = IType.INT,
				optional = false,
				doc = @doc("the new number of lanes.")
			)
		},
		doc = @doc(
			value = "change the number of lanes of the road",
			examples = { @example ("do update_lanes lanes: 2") }
		)
	)
	public void primChangeLaneNumber(final IScope scope) throws GamaRuntimeException {
		// TODO: update this for multi-lane
		final IAgent road = getCurrentAgent(scope);
		final Integer lanes = scope.getIntArg("lanes");
		setLanes(road, lanes);
		if (lanes == 0) {
			return;
		}
		int prev = getNumLanes(road);
		final IList agentsOn = (IList) road.getAttribute(RoadSkill.AGENTS_ON);
		if (prev == 0){
			for (int i = 0; i < prev; i++) {
				final int nbSeg = road.getInnerGeometry().getNumPoints() - 1;
				final IList lisSg = GamaListFactory.create(Types.NO_TYPE);
				for (int j = 0; j < nbSeg; j++) {
					lisSg.add(GamaListFactory.create(Types.NO_TYPE));
				}
				agentsOn.add(lisSg);
			}
		} else if (prev < lanes) {
			IList<IList<IList<IAgent>>> newAgentsOn = GamaListFactory.create();
			int nb_seg = ((IList) agentsOn.get(0)).size();
			for(int i = 0; i <lanes; i++) {
				IList<IList<IAgent>> agsPerLanes = null;
				if (i < prev) {
					agsPerLanes = (IList<IList<IAgent>>) agentsOn.get(i);
				} else {
					agsPerLanes = GamaListFactory.create();
					for (int j = 0; j <nb_seg; j++) {
						agsPerLanes.add(GamaListFactory.create());
					}
				}
				newAgentsOn.add(agsPerLanes);
			}
			setAgentsOn(road, newAgentsOn);
		} else if (prev > lanes) {
			IList newAgentsOn = GamaListFactory.create();
			int nb_seg = ((IList) agentsOn.get(0)).size();
			for (int i = 0; i <prev; i++){
				IList agsPerLanes =  (IList) agentsOn.get(i);
				if (i < lanes) {
					newAgentsOn.add(agsPerLanes);
				} else {
					for (int j = 0; j < nb_seg; j++) {
						IList<IAgent> ags = (IList<IAgent>) agsPerLanes.get(j);
						for (IAgent ag: ags) {
							((List)((List)newAgentsOn.get(lanes - 1)).get(j)).add(ag);
							ag.setAttribute(DrivingSkill.LOWEST_LANE, lanes - 1);
						}
					} 	
				}
			}
			setAgentsOn(road, newAgentsOn);		
		}
	}
}
