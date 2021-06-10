package simtools.gaml.extensions.traffic.lanechange;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import simtools.gaml.extensions.traffic.RoadNodeSkill;
import simtools.gaml.extensions.traffic.RoadSkill;
import simtools.gaml.extensions.traffic.carfollowing.IDM;
import static simtools.gaml.extensions.traffic.DrivingSkill.*;
import static simtools.gaml.extensions.traffic.Utils.findLeadingAndBackVehicle;
import static simtools.gaml.extensions.traffic.Utils.rescaleProba;

public class MOBIL {
	/**
	 * Choose a new lane according to the lane change model MOBIL
	 * (https://traffic-simulation.de/info/info_MOBIL.html).
	 *
	 * @param scope
	 * @param road             the road which the vehicle is moving on
	 * @param segment          the index of the current road segment
	 * @param distToSegmentEnd the distance to the endpoint of the segment
	 * @return a pair composed of the optimal lowest lane index and
	 *         the resulting acceleration of the vehicle
	 */
	public static ImmutablePair<Integer, Double> chooseLane(final IScope scope,
			final IAgent vehicle,
			final IAgent target,
			final IAgent road,
			final int segment,
			final double distToSegmentEnd) {
		double VL = getVehicleLength(vehicle);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		int currentLowestLane = getLowestLane(vehicle);

		// Rescale probabilities based on step duration
		double timeStep = scope.getSimulation().getClock().getStepInSeconds();
		Double probaChangeLaneUp = rescaleProba(getProbaLaneChangeUp(vehicle), timeStep);
		Double probaChangeLaneDown = rescaleProba(getProbaLaneChangeDown(vehicle), timeStep);
		Double probaUseLinkedRoad = rescaleProba(getProbaUseLinkedRoad(vehicle), timeStep);

		IAgent linkedRoad = RoadSkill.getLinkedRoad(road);
		int numCurrentLanes = RoadSkill.getNumLanes(road);
		int numLinkedLanes = (linkedRoad != null) ? RoadSkill.getNumLanes(linkedRoad) : 0;
		int linkedLaneLimit = getLinkedLaneLimit(vehicle);
		linkedLaneLimit = (linkedLaneLimit != -1 && numLinkedLanes > linkedLaneLimit) ?
				linkedLaneLimit : numLinkedLanes;
		List<Integer> allowedLanes = getAllowedLanes(vehicle);
		// Restrict the lane index when entering a new road
		currentLowestLane = Math.min(currentLowestLane,
				numCurrentLanes + linkedLaneLimit - numLanesOccupied);

		// Determine the lanes which is considered for switching
		int laneChangeLimit = getLaneChangeLimit(vehicle);
		int lower = 0;
		int upper = numCurrentLanes + linkedLaneLimit - numLanesOccupied;
		if (laneChangeLimit != -1) {
			lower = Math.max(lower, currentLowestLane - laneChangeLimit);
			upper = Math.min(upper, currentLowestLane + laneChangeLimit);
		}
		List<Integer> limitedLaneRange = IntStream.rangeClosed(lower, upper).
			boxed().collect(Collectors.toList());
		if (isLaneChangePriorityRandomized(vehicle)) {
			Collections.shuffle(limitedLaneRange);
		}

		// Compute acceleration if the vehicle stays on the same lane
		ImmutablePair<Triple<IAgent, Double, Boolean>, Triple<IAgent, Double, Boolean>> pair =
			findLeadingAndBackVehicle(scope, vehicle, target, road, segment, distToSegmentEnd, currentLowestLane);
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
			double leadingSpeed = getSpeed(leadingVehicle);
			leadingSpeed = leadingSameDirection ? leadingSpeed : -leadingSpeed;
			setLeadingVehicle(vehicle, leadingVehicle);
			setLeadingDistance(vehicle, leadingDist);
			setLeadingSpeed(vehicle, leadingSpeed);
			// Calculate acc(M) - Acceleration of current vehicle M if no lane change occurs
			stayAccelM = IDM.computeAcceleration(scope, vehicle, leadingDist, leadingSpeed);
			// Do not allow changing lane when approaching intersections
			// Reason: in some cases the vehicle is forced to slow down (e.g. approaching final target in path),
			// but it can gain acceleration by switching lanes to follow a fast vehicle.
			if ((leadingVehicle != null &&
					leadingVehicle.getSpecies().implementsSkill(RoadNodeSkill.SKILL_ROAD_NODE)) ||
					getTimeSinceLC(vehicle) < getLCCooldown(vehicle)) {
				double t = getTimeSinceLC(vehicle);
				setTimeSinceLC(vehicle, t + timeStep);
				return ImmutablePair.of(currentLowestLane, stayAccelM);
			}
		}
		int bestLowestLane = currentLowestLane;
		double bestAccel = stayAccelM;
		double bestIncentive = 0;

		// Examine all lanes within range
		for (int tmpLowestLane : limitedLaneRange) {
			if (tmpLowestLane == currentLowestLane ||
					(!allowedLanes.isEmpty() && !allowedLanes.contains(tmpLowestLane))) {
				continue;
			}

			// Evaluate probabilities to switch to tmpLowestLane
			// boolean canChangeDown = tmpLowestLane < lowestLane &&
			// 		scope.getRandom().next() < probaChangeLaneDown;
			// // NOTE: in canChangeUp, the 2nd condition prevents moving from current road to linked road
			// boolean canChangeUp = tmpLowestLane > lowestLane &&
			// 		 !(lowestLane <= numCurrentLanes - numLanesOccupied && tmpLowestLane > numCurrentLanes - numLanesOccupied) &&
			// 		 scope.getRandom().next() < probaChangeLaneUp;
			if (currentLowestLane <= numCurrentLanes - numLanesOccupied &&
					tmpLowestLane > numCurrentLanes - numLanesOccupied) {
				if (scope.getRandom().next() > probaUseLinkedRoad) {
					continue;
				}
			}

			pair = findLeadingAndBackVehicle(scope, vehicle, target, road, segment, distToSegmentEnd, tmpLowestLane);
			if (pair == null) {
				// Will crash into another vehicle if switch to this lane
				continue;
			}

			// Find the leading vehicle of M on this new lane
			Triple<IAgent, Double, Boolean> leadingTriple = pair.getKey();
			IAgent leadingVehicle = leadingTriple.getLeft();
			double leadingDist = leadingTriple.getMiddle();
			boolean leadingSameDirection = leadingTriple.getRight();
			double leadingSpeed = getSpeed(leadingVehicle);
			leadingSpeed = leadingSameDirection ? leadingSpeed : -leadingSpeed;

			// Calculate acc'(M) - acceleration of M on new lane
			double changeAccelM = IDM.computeAcceleration(scope, vehicle, leadingDist, leadingSpeed);

			// Find back vehicle B' on new lane
			double stayAccelB;
			double changeAccelB;
			Triple<IAgent, Double, Boolean> backTriple = pair.getValue();
			if (backTriple == null || !backTriple.getRight() ||
					backTriple.getLeft() == currentBackVehicle ||
					getLeadingVehicle(backTriple.getLeft()) != vehicle) {
				// IF no back vehicle OR back vehicle is moving in opposite direction OR
				// back vehicle on new lanes is the same one on old lanes OR
				// back vehicle's leading vehicle is not the current vehicle
				// THEN acceleration change of B is irrelevant
				stayAccelB = 0;
				changeAccelB = 0;
			} else {
				IAgent backVehicle = backTriple.getLeft();
				double backDist = backTriple.getMiddle();
				// Calculate acc(B') - acceleration of B' if M does not change to this lane
				// NOTE: in this case, the leading vehicle is the one we have found above for M
				stayAccelB = IDM.computeAcceleration(scope, backVehicle, backDist + VL + leadingDist, leadingSpeed);
				// Calculate acc'(B') - acceleration of B' if M changes to this lane
				// NOTE: in this case, M is the new leading vehicle of B'
				changeAccelB = IDM.computeAcceleration(scope, backVehicle, backDist, getSpeed(vehicle));
			}

			// MOBIL params
			double p = getPolitenessFactor(vehicle);
			double bSave = getMaxSafeDeceleration(vehicle);
			double aThr = getAccGainThreshold(vehicle);
			double aBias = getAccBias(vehicle);

			// Safety criterion
			if (changeAccelB < -bSave) {
				continue;
			}

			// Incentive criterion
			boolean biasCond = getRightSideDriving(vehicle) ?
				tmpLowestLane < currentLowestLane : tmpLowestLane > currentLowestLane;
			int biasSign = biasCond ? 1 : -1;
			double incentive = changeAccelM - stayAccelM + p * (changeAccelB - stayAccelB) + aBias * biasSign;
			if (incentive > aThr && incentive > bestIncentive) {
				bestIncentive = incentive;
				bestLowestLane = tmpLowestLane;
				bestAccel = changeAccelM;
				setLeadingVehicle(vehicle, leadingVehicle);
				setLeadingDistance(vehicle, leadingDist);
				setLeadingSpeed(vehicle, leadingSpeed);
			}
		}

		if (bestLowestLane != currentLowestLane) {
			setTimeSinceLC(vehicle, 0.0);
		} else {
			double t = getTimeSinceLC(vehicle);
			setTimeSinceLC(vehicle, t + timeStep);
		}
		return ImmutablePair.of(bestLowestLane, bestAccel);
	}
}
