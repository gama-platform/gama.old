package simtools.gaml.extensions.traffic.carfollowing;

import static simtools.gaml.extensions.traffic.DrivingSkill.getCurrentTarget;
import static simtools.gaml.extensions.traffic.DrivingSkill.getMinSafetyDistance;
import static simtools.gaml.extensions.traffic.DrivingSkill.getNextRoad;
import static simtools.gaml.extensions.traffic.DrivingSkill.getNumLanesOccupied;
import static simtools.gaml.extensions.traffic.DrivingSkill.getVehicleLength;
import static simtools.gaml.extensions.traffic.DrivingSkill.isViolatingOneway;
import static simtools.gaml.extensions.traffic.DrivingSkill.readyToCross;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import simtools.gaml.extensions.traffic.RoadNodeSkill;
import simtools.gaml.extensions.traffic.RoadSkill;

public class Utils {
	/**
	 * Attempts to make lane changing probabilities timestep-agnostic
	 *
	 * @param probaInOneSecond a probability with respect to one second
	 * @param timeStep         the duration of a simulation step
	 * @return the rescaled probability
	 */
	public static double rescaleProba(final double probaInOneSecond,
			final double timeStep) {
		return Math.min(probaInOneSecond * timeStep, 1.0);
	}
	
	public static Triple<IAgent, Double, Boolean> findLeader(final IScope scope,
										final IAgent vehicle,
										final IAgent target,
										final IAgent road,
										final int segment,
										final int lowestLane,
										final double distToSegmentEnd,
										final double distToCurrentTarget) {
		double vL = getVehicleLength(vehicle);
		double minSafetyDist = getMinSafetyDistance(vehicle);
		boolean violatingOneway = isViolatingOneway(vehicle);
		int numRoadLanes = RoadSkill.getNumLanes(road);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		
		
		double tmpDistQuery;
		IAgent leader = null;
		double minGap = Double.MAX_VALUE;
		boolean sameDirection = false;
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			OrderedBidiMap<Double, IAgent> distMap = 
					RoadSkill.getVehiclesOnLaneSegment(scope, road, lane).inverseBidiMap();
			boolean wrongDirection = lane < numRoadLanes ? false : true;
			wrongDirection = violatingOneway ? !wrongDirection : wrongDirection;
			tmpDistQuery = !wrongDirection ? distToCurrentTarget : 
					RoadSkill.getTotalLength(road) - distToCurrentTarget;
			
			// Another vehicle already occupied the exact same longitudinal spot
			// in this lane, which will lead to a crash if switch
			if (distMap.containsKey(tmpDistQuery) && distMap.get(tmpDistQuery) != vehicle) {
				return null;
			}
			
			Double k = !wrongDirection ? distMap.nextKey(tmpDistQuery)
					: distMap.previousKey(tmpDistQuery);
			// No leader on this lane
			if (k == null) {
				continue;
			} 
			double tmpLeaderDist = k;
			IAgent tmpLeader = distMap.get(tmpLeaderDist);
			if (tmpLeader == null || tmpLeader.dead()) {
				continue;
			}
			double otherVL = getVehicleLength(tmpLeader);
			double gap = Math.abs(tmpLeaderDist - tmpDistQuery) - 0.5 * vL - 0.5 * otherVL;

			if (gap < 0) {
				return null;
			} else if (gap < minGap) {
				minGap = gap;
				leader = tmpLeader;
				sameDirection = target == getCurrentTarget(tmpLeader);
			}
			
		}
		if (leader != null && !leader.dead()) {
			 return ImmutableTriple.of(leader, minGap, sameDirection);
		}

		// The methods continue down here if no leading vehicle is found on the current road

		IAgent nextRoad = getNextRoad(vehicle);
		// Check if vehicle is approaching an intersection
		// Slowing down at final target, since at this point we don't know which road will be taken next
		// OR might need to slow down at the intersection if it is not possible to enter the next road
		if (nextRoad == null || !readyToCross(scope, vehicle, target, nextRoad)) {
			// Return a virtual leading vehicle of length 0 to simulate deceleration at intersections
			// NOTE: the added minSafetyDist is necessary for the vehicle to ignore the safety dist when stopping at an endpoint
			// TODO: make the vehicles stop in front of the lights,
			// this would require changes in the drive loop as well
			return ImmutableTriple.of(target, distToCurrentTarget + minSafetyDist, false);
		} else if (nextRoad != road) {
			boolean willViolateOneway = target == RoadSkill.getTargetNode(nextRoad);
			IAgent nextTarget = !willViolateOneway ?
				RoadSkill.getTargetNode(nextRoad) : RoadSkill.getSourceNode(nextRoad);
			int numLanesNext = RoadSkill.getNumLanes(nextRoad);
			int numLanesTotalNext = RoadSkill.getNumLanesTotal(nextRoad);
			int lowestLaneToCheck = Math.min(lowestLane, numLanesTotalNext - numLanesOccupied);
			
			for (int i = 0; i < numLanesOccupied; i += 1) {
				int lane = lowestLaneToCheck + i;
				OrderedBidiMap<Double, IAgent> distMap = 
						RoadSkill.getVehiclesOnLaneSegment(scope, nextRoad, lane).inverseBidiMap();
				boolean wrongDirection = lane < numLanesNext ? false : true;
				wrongDirection = willViolateOneway ? !wrongDirection : wrongDirection;

				if (distMap.isEmpty()) {
					continue;
				}
				
				double tmpLeaderDist = !wrongDirection ? distMap.lastKey() : distMap.firstKey();
				IAgent tmpLeader = distMap.get(tmpLeaderDist);
				if (tmpLeader == null || tmpLeader.dead()) {
					continue;
				}
				double extraGap = !wrongDirection ? 
						RoadSkill.getTotalLength(nextRoad) - tmpLeaderDist : tmpLeaderDist;
				double otherVL = getVehicleLength(tmpLeader);
				double gap = distToCurrentTarget + extraGap - 0.5 * vL - 0.5 * otherVL;
				
				if (gap < 0) {
					return null;
				}
				if (gap < minGap) {
					minGap = gap;
					leader = tmpLeader;
					sameDirection = nextTarget == getCurrentTarget(tmpLeader);
				}
			}
		}	

		if (leader != null) {
			return ImmutableTriple.of(leader, minGap, sameDirection);
		} else {
			// the road ahead seems to be completely clear
			return ImmutableTriple.of(null, 1e6, false);
		}
	}
	
	
	public static Triple<IAgent, Double, Boolean> findFollower(final IScope scope,
										final IAgent vehicle,
										final IAgent target,
										final IAgent road,
										final int segment,
										final int lowestLane,
										final double distToSegmentEnd,
										final double distToCurrentTarget) {
		double vL = getVehicleLength(vehicle);
		boolean violatingOneway = isViolatingOneway(vehicle);
		int numRoadLanes = RoadSkill.getNumLanes(road);
		int numLanesOccupied = getNumLanesOccupied(vehicle);

		double tmpDistQuery;
		IAgent follower = null;
		double minGap = Double.MAX_VALUE;
		boolean sameDirection = false;
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			OrderedBidiMap<Double, IAgent> distMap = 
					RoadSkill.getVehiclesOnLaneSegment(scope, road, lane).inverseBidiMap();
			boolean wrongDirection = lane < numRoadLanes ? false : true;
			wrongDirection = violatingOneway ? !wrongDirection : wrongDirection;
			tmpDistQuery = !wrongDirection ? distToCurrentTarget : 
					RoadSkill.getTotalLength(road) - distToCurrentTarget;

			if (distMap.containsKey(tmpDistQuery) && distMap.get(tmpDistQuery) != vehicle) {
				return null;
			}
			
			Double k = !wrongDirection ? distMap.previousKey(tmpDistQuery)
					: distMap.nextKey(tmpDistQuery);

			// No follower on this lane
			if (k == null) {
				continue;
			} 
			double tmpLeaderDist = k;
			IAgent tmpFollower = distMap.get(tmpLeaderDist);
			if (tmpFollower == null || tmpFollower.dead()) {
				continue;
			}
			double otherVL = getVehicleLength(tmpFollower);
			double gap = Math.abs(tmpLeaderDist - tmpDistQuery) - 0.5 * vL - 0.5 * otherVL;

			if (gap < 0) {
				return null;
			} else if (gap < minGap) {
				minGap = gap;
				follower = tmpFollower;
				sameDirection = target == getCurrentTarget(tmpFollower);
			}
			
		}
		if (follower != null && !follower.dead()) {
			return ImmutableTriple.of(follower, minGap, sameDirection);
		}

		// Find followers on previous roads
		IAgent sourceNode = (target == RoadSkill.getTargetNode(road)) ?
				RoadSkill.getSourceNode(road) : target;
		for (IAgent prevRoad : RoadNodeSkill.getRoadsIn(sourceNode)) {
			int numLanes = RoadSkill.getNumLanes(prevRoad);
			int numLanesTotal = RoadSkill.getNumLanesTotal(prevRoad);
			for (int i = 0; i < numLanesOccupied; i += 1) {
				int lane = lowestLane + i;
				if (lane >= numLanesTotal - numLanesOccupied) {
					break;
				}
				
				boolean isLinkedLane = lane >= numLanes;
				if (isLinkedLane) {
					prevRoad = RoadSkill.getLinkedRoad(prevRoad);
				}
				OrderedBidiMap<Double, IAgent> distMap = 
						RoadSkill.getVehiclesOnLaneSegment(scope, prevRoad, lane).inverseBidiMap();
				if (distMap.isEmpty()) {
					continue;
				}
				double distQuery = !isLinkedLane ? distMap.lastKey() : distMap.firstKey();

				IAgent tmpFollower = distMap.get(distQuery);
				if (getCurrentTarget(tmpFollower) != sourceNode) {
					// This vehicle is not following the current one, but it's going the other way
					continue;
				}
				
				double extraGap = !isLinkedLane ? distQuery : RoadSkill.getTotalLength(prevRoad) - distQuery;
				double gap = distToCurrentTarget + extraGap - 0.5 * vL - 0.5 * getVehicleLength(tmpFollower);
				if (gap < 0) {
					return null;
				} else if (gap < minGap) {
					minGap = gap;
					follower = tmpFollower;
					sameDirection = sourceNode == getCurrentTarget(tmpFollower);
				}
			}
		}
			
		if (follower != null) {
			return ImmutableTriple.of(follower, minGap, sameDirection);
		} else {
			return ImmutableTriple.of(null, null, null);
		}
	}
}
