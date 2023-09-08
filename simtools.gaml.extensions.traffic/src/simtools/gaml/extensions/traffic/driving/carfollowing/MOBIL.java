/*******************************************************************************************************
 *
 * MOBIL.java, in simtools.gaml.extensions.traffic, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving.carfollowing;

import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getAccBias;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getAccGainThreshold;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getAllowedLanes;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getLCCooldown;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getLaneChangeLimit;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getLeadingDistance;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getMaxSafeDeceleration;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getNumLanesOccupied;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getPolitenessFactor;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getProbaUseLinkedRoad;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getRightSideDriving;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getSpeed;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getTimeSinceLC;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.getVehicleLength;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.setFollower;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.setLeadingDistance;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.setLeadingSpeed;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.setLeadingVehicle;
import static simtools.gaml.extensions.traffic.driving.DrivingSkill.setTimeSinceLC;
import static simtools.gaml.extensions.traffic.driving.carfollowing.Utils.findFollower;
import static simtools.gaml.extensions.traffic.driving.carfollowing.Utils.findLeader;
import static simtools.gaml.extensions.traffic.driving.carfollowing.Utils.rescaleProba;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import simtools.gaml.extensions.traffic.driving.RoadSkill;

/**
 * The Class MOBIL.
 */
public class MOBIL {
	/**
	 * Choose a new lane according to the lane change model MOBIL (https://traffic-simulation.de/info/info_MOBIL.html).
	 *
	 * @param scope
	 * @param road
	 *            the road which the vehicle is moving on
	 * @param segment
	 *            the index of the current road segment
	 * @param distToSegmentEnd
	 *            the distance to the endpoint of the segment
	 * @return a pair composed of the optimal lowest lane index and the resulting acceleration of the vehicle
	 */
	public static ImmutablePair<Integer, Double> chooseLane(final IScope scope, final IAgent vehicle, final IAgent road,
			final int currentLowestLane) {
		double VL = getVehicleLength(vehicle);
		int numLanesOccupied = getNumLanesOccupied(vehicle);

		// Rescale probabilities based on step duration
		double timeStep = scope.getSimulation().getClock().getStepInSeconds();
		Double probaUseLinkedRoad = rescaleProba(getProbaUseLinkedRoad(vehicle), timeStep);

		int numCurrentLanes = RoadSkill.getNumLanes(road);
		int linkedLaneLimit = Utils.computeLinkedLaneLimit(vehicle, road);
		List<Integer> allowedLanes = getAllowedLanes(vehicle);

		int numValidRoadLanes = numCurrentLanes + linkedLaneLimit;
		if (numLanesOccupied > numValidRoadLanes) {
			String msg = String.format(
					"%s occupies %d lanes, and it is unable to enter %s where there are only %d lane(s) available",
					vehicle.getName(), numLanesOccupied, road.getName(), numValidRoadLanes);
			throw GamaRuntimeException.error(msg, scope);
		}

		// Determine the lanes which is considered for switching
		int laneChangeLimit = getLaneChangeLimit(vehicle);
		int lower = 0;
		int upper = numCurrentLanes + linkedLaneLimit - numLanesOccupied;
		if (laneChangeLimit != -1) {
			lower = Math.max(lower, currentLowestLane - laneChangeLimit);
			upper = Math.min(upper, currentLowestLane + laneChangeLimit);
		}
		List<Integer> limitedLaneRange = IntStream.rangeClosed(lower, upper).boxed().toList();

		// Compute acceleration if the vehicle stays on the same lane
		Triple<IAgent, Double, Boolean> leaderTriple = findLeader(scope, vehicle, currentLowestLane);
		Triple<IAgent, Double, Boolean> followerTriple = findFollower(scope, vehicle, currentLowestLane);
		IAgent currentBackVehicle = followerTriple != null ? followerTriple.getLeft() : null;
		double stayAccelM;
		if (leaderTriple.getMiddle() < 0) { // || followerTriple == null) {
			stayAccelM = -Double.MAX_VALUE;
		} else {
			// Find the leading vehicle on current lanes
			IAgent leadingVehicle = leaderTriple.getLeft();
			double leadingDist = leaderTriple.getMiddle();
			boolean leadingSameDirection = leaderTriple.getRight();
			double leadingSpeed = getSpeed(leadingVehicle);
			leadingSpeed = leadingSameDirection ? leadingSpeed : -leadingSpeed;
			setLeadingVehicle(vehicle, leadingVehicle);
			setLeadingDistance(vehicle, leadingDist);
			setLeadingSpeed(vehicle, leadingSpeed);
			setFollower(vehicle, currentBackVehicle);
			// Calculate acc(M) - Acceleration of current vehicle M if no lane change occurs
			stayAccelM = IDM.computeAcceleration(scope, vehicle, road, leadingDist, leadingSpeed);
			// Do not allow changing lane when approaching intersections
			// Reason: in some cases the vehicle is forced to slow down (e.g. approaching final target in path),
			// but it can gain acceleration by switching lanes to follow another moving vehicle.
			if (leadingVehicle != null && leadingVehicle.getSpecies().implementsSkill("intersection")
					|| getTimeSinceLC(vehicle) < getLCCooldown(vehicle)) {
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
			if (tmpLowestLane == currentLowestLane
					|| !allowedLanes.isEmpty() && !allowedLanes.contains(tmpLowestLane)) {
				continue;
			}

			if ((currentLowestLane <= numCurrentLanes - numLanesOccupied
					&& tmpLowestLane > numCurrentLanes - numLanesOccupied)
					&& (scope.getRandom().next() > probaUseLinkedRoad)) {
				continue;
			}

			Triple<IAgent, Double, Boolean> newLeaderTriple = findLeader(scope, vehicle, tmpLowestLane);
			Triple<IAgent, Double, Boolean> newFollowerTriple = findFollower(scope, vehicle, tmpLowestLane);

			if (newLeaderTriple.getMiddle() < 0 || newFollowerTriple.getMiddle() < 0) {
				// Will crash into another vehicle if switch to this lane
				continue;
			}

			// Find the leading vehicle of M on this new lane
			IAgent leadingVehicle = newLeaderTriple.getLeft();
			double leadingDist = newLeaderTriple.getMiddle();
			boolean leadingSameDirection = newLeaderTriple.getRight();
			double leadingSpeed = getSpeed(leadingVehicle);
			leadingSpeed = leadingSameDirection ? leadingSpeed : -leadingSpeed;

			// Calculate acc'(M) - acceleration of M on new lane
			double changeAccelM = IDM.computeAcceleration(scope, vehicle, road, leadingDist, leadingSpeed);

			// Find back vehicle B' on new lane
			double stayAccelB;
			double changeAccelB;
			// Ignore follower in incentive criterion if:
			// 1. No follower was found
			// 2. New follower if switch lanes is still the old one
			// 3. The follower is actually following another vehicle
			if (newFollowerTriple.getLeft() == null || newFollowerTriple.getLeft() == currentBackVehicle
					|| getLeadingDistance(newFollowerTriple.getLeft()) < newFollowerTriple.getMiddle()) {
				stayAccelB = 0;
				changeAccelB = 0;
			} else {
				IAgent backVehicle = newFollowerTriple.getLeft();
				double backDist = newFollowerTriple.getMiddle();
				// Calculate acc(B') - acceleration of B' if M does not change to this lane
				// NOTE: in this case, the leading vehicle is the one we have found above for M
				stayAccelB =
						IDM.computeAcceleration(scope, backVehicle, road, backDist + VL + leadingDist, leadingSpeed);
				// Calculate acc'(B') - acceleration of B' if M changes to this lane
				// NOTE: in this case, M is the new leading vehicle of B'
				changeAccelB = IDM.computeAcceleration(scope, backVehicle, road, backDist, getSpeed(vehicle));
			}

			// MOBIL params
			double p = getPolitenessFactor(vehicle);
			double bSave = getMaxSafeDeceleration(vehicle);
			double aThr = getAccGainThreshold(vehicle);
			double aBias = getAccBias(vehicle);

			// Safety criterion
			if (changeAccelB <= -bSave) { continue; }

			// Incentive criterion
			boolean biasCond = getRightSideDriving(vehicle) ? tmpLowestLane < currentLowestLane
					: tmpLowestLane > currentLowestLane;
			int biasSign = biasCond ? 1 : -1;
			double incentive = changeAccelM - stayAccelM + p * (changeAccelB - stayAccelB) + aBias * biasSign;
			if (incentive > aThr && incentive > bestIncentive) {
				bestIncentive = incentive;
				bestLowestLane = tmpLowestLane;
				bestAccel = changeAccelM;
				setLeadingVehicle(vehicle, leadingVehicle);
				setLeadingDistance(vehicle, leadingDist);
				setLeadingSpeed(vehicle, leadingSpeed);
				setFollower(vehicle, newFollowerTriple.getLeft());
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
