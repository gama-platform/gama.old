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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.algorithm.CGAlgorithms;

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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

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
				name = RoadSkill.NUM_SEGMENTS,
				type = IType.INT,
				doc = @doc ("the number of road segments")),
		@variable (
				name = "linked_road",
				type = ITypeProvider.OWNER_TYPE,
				doc = @doc ("the linked road: the lanes of this linked road will be usable by drivers on the road")),
		@variable (
				name = "maxspeed",
				type = IType.FLOAT,
				init = "50#km/#h",
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
	public final static String NUM_SEGMENTS = "num_segments";

	@getter (AGENTS_ON)
	public static List getAgentsOn(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS_ON);
	}
	
	public static void setAgentsOn(final IAgent agent, final List agents) {
		agent.setAttribute(AGENTS_ON, agents);
	}
	

	@getter (AGENTS)
	public static List getAgents(final IAgent agent) {
		return (List) agent.getAttribute(AGENTS);
	}
	
	public static void setAgents(final IAgent agent, final List agents) {
		agent.setAttribute(AGENTS, agents);
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

	@getter (NUM_SEGMENTS)
	public static int getNumSegments(IAgent road) {
		return GeometryUtils.getPointsOf(road).length - 1;
	}

	@setter (NUM_SEGMENTS)
	public static void setNumSegments(IAgent road, int numSegments) {
		// read-only
	}

	/**
	 * Adds the vehicle to the specified lane and segment in the specified road's `AGENTS_ON` list.
	 *
	 * @param scope
	 * @param driver the driver agent to be added.
	 * @param road the road to be updated.
	 * @param lane a valid lane index on the road.
	 * @param segment a valid segment index on the road.
	 */
	public static void addDriverToLaneSegment(IScope scope, IAgent driver, IAgent road, int lane, int segment) {
		List agentsOn = (List) road.getAttribute(AGENTS_ON);
		List laneDrivers, segmentDrivers;
		laneDrivers = (List) agentsOn.get(lane);
		segmentDrivers = (List) laneDrivers.get(segment);
		segmentDrivers.add(driver);
	}

	/**
	 * Removes the vehicle from the specified lane and segment in the specified road's `AGENTS_ON` list.
	 *
	 * @param scope
	 * @param driver the driver agent to be removed.
	 * @param road the road to be updated.
	 * @param lane a valid lane index on the road.
	 * @param segment a valid segment index on the road.
	 */
	public static void removeDriverFromLaneSegment(IScope scope, IAgent driver, IAgent road, int lane, int segment) {
		List agentsOn = (List) road.getAttribute(AGENTS_ON);
		List laneDrivers, segmentDrivers;
		laneDrivers = (List) agentsOn.get(lane);
		segmentDrivers = (List) laneDrivers.get(segment);
		segmentDrivers.remove(driver);
	}

	/**
	 * Converts the "overflow" lane to the correct lane index on the linked road.
	 * Simply returns the input lane if it is already a valid lane on the current road.
	 * 
	 * @param scope
	 * @param lane the input lane index
	 * @return
	 */
	public static int computeValidLane(IAgent road, int lane) {
		IAgent linkedRoad = getLinkedRoad(road);
		int numLanesMain = getLanes(road);
		if (linkedRoad == null || lane < numLanesMain) {
			return lane;
		} else {
			int numLanesLinked = getLanes(linkedRoad);
			return numLanesMain + numLanesLinked - 1 - lane;
		}
	}

	/**
	 * Computes the correct segment index on the linked road.
	 *
	 * @param road
	 * @param segment
	 * @param isLinkedLane if true, simply returns the input segment index
	 * @return the correct segment index
	 */
	public static int computeCorrectSegment(IAgent road, int segment, boolean isLinkedLane) {
		if (!isLinkedLane) {
			return segment;
		} else {
			return getNumSegments(road) - segment - 1;
		}
	}

	/**
	 * Registers the driver on the specified road and starting lane.
	 *
	 * @param scope
	 * @param driver the agent to register
	 * @param road the new road
	 * @param startingLane the new starting lane
	 *
	 * @throws GamaRuntimeException
	 */
	public static void register(IScope scope, IAgent driver, IAgent road, int startingLane)
			throws GamaRuntimeException {
		if (driver == null) return;

		int numLanesOccupied = (int) driver.getAttribute(DrivingSkill.NUM_LANES_OCCUPIED);

		int indexSegment = 0;
		boolean onLinkedRoad = false;
		driver.setAttribute(DrivingSkill.ON_LINKED_ROAD, false);
		indexSegment = getSegmentIndex(road, driver);
		for (int i = 0; i < numLanesOccupied; i += 1) {
			addDriverToLaneSegment(scope, driver, road, startingLane + i, indexSegment);
		}
		getAgents(road).add(driver);

		driver.setAttribute(DrivingSkill.DISTANCE_TO_GOAL,
				driver.getLocation().euclidianDistanceTo(GeometryUtils.getPointsOf(road)[indexSegment + 1]));
		driver.setAttribute(DrivingSkill.CURRENT_ROAD, road);
		driver.setAttribute(DrivingSkill.STARTING_LANE, startingLane);
		driver.setAttribute(DrivingSkill.SEGMENT_INDEX,
				onLinkedRoad ? road.getInnerGeometry().getNumPoints() - indexSegment - 2 : indexSegment);
	}

	public static int getSegmentIndex(final IAgent road, final IAgent driver) {
		final GamaPoint[] coords = GeometryUtils.getPointsOf(road);
		if (coords.length == 2) { return 0; }

		final GamaPoint loc = driver.getLocation().toGamaPoint();
		for (int i = 0; i < coords.length - 1; i++) {
			if (coords[i].equals(loc)) { return i; }
		}
		double distanceS = Double.MAX_VALUE;
		int indexSegment = 0;
		final int nbSp = coords.length;
		for (int i = 0; i < nbSp - 1; i++) {
			final double distS = CGAlgorithms.distancePointLine(loc, coords[i], coords[i + 1]);
			if (distS < distanceS) {
				distanceS = distS;
				indexSegment = i;
			}
		}
		return indexSegment;

	}
	
	
	@action (
			name = "update_lanes",
			args = { @arg (
					name = "lanes",
					type = IType.INT,
					optional = false,
					doc = @doc ("the new number of lanes."))},
			doc = @doc (
					value = "change the number of lanes of the road",
					examples = { @example ("do update_lanes lanes: 2") }))
	public void primChangeLaneNumber(final IScope scope) throws GamaRuntimeException {
		// TODO: update this for multi-lane
		final IAgent road = getCurrentAgent(scope);
		final Integer lanes = scope.getIntArg("lanes");
		setLanes(road, lanes);
		if (lanes == 0) {
			return;
		}
		int prev = getLanes(road);
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
							ag.setAttribute(DrivingSkill.STARTING_LANE, lanes - 1);
						}
					} 	
				}
			}
			setAgentsOn(road, newAgentsOn);		
		}
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
		int lane = scope.getIntArg("lane");

		register(scope, driver, road, lane);
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
		final IAgent driver = (IAgent) scope.getArg("agent", IType.AGENT);
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

		int numLanesCurrent = getLanes(currentRoad);
		IAgent linkedRoad = getLinkedRoad(currentRoad);
		final Integer startingLane = (Integer) driver.getAttribute(DrivingSkill.STARTING_LANE);
		int numLanesOccupied = (int) driver.getAttribute(DrivingSkill.NUM_LANES_OCCUPIED);
		int currentSegment = DrivingSkill.getSegmentIndex(driver);

		HashSet<Integer> oldRelLanes = IntStream.range(startingLane, startingLane + numLanesOccupied)
				.boxed().collect(Collectors.toCollection(HashSet::new));

		IAgent road;
		for (int relLane : oldRelLanes) {
			boolean isLinkedLane = relLane >= numLanesCurrent;
			road = isLinkedLane ? linkedRoad : currentRoad;
			RoadSkill.removeDriverFromLaneSegment(scope, driver, road,
					RoadSkill.computeValidLane(currentRoad, relLane),
					RoadSkill.computeCorrectSegment(currentRoad, currentSegment, isLinkedLane));
		}
	}
}
