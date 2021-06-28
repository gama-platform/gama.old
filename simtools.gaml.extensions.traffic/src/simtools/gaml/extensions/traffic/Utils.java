package simtools.gaml.extensions.traffic;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.locationtech.jts.geom.Coordinate;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import static simtools.gaml.extensions.traffic.DrivingSkill.*;

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
	 * If either the leading or back vehicle overlaps with the current one, the return value will be null.
	 *
	 * @param scope
	 * @param vehicle          the vehicle whose leading and back vehicle is to be found
	 * @param target           the target of the concerned vehicle
	 * @param road             the road which the vehicle is moving on
	 * @param segment          the index of the current road segment that the vehicle is on
	 * @param distToSegmentEnd the distance from the vehicle to the segment endpoint
	 * @param lowestLane       the current "lowest" lane index of the vehicle
	 * @return a pair containing two triplets, one for the leading vehicle and one
	 *         for the back vehicle
	 */
	public static ImmutablePair<Triple<IAgent, Double, Boolean>, Triple<IAgent, Double, Boolean>>
			findLeadingAndBackVehicle(final IScope scope,
										final IAgent vehicle,
										final IAgent target,
										final IAgent road,
										final int segment,
										final double distToSegmentEnd,
										final int lowestLane) {
		double vL = getVehicleLength(vehicle);
		double minSafetyDist = getMinSafetyDistance(vehicle);
		boolean violatingOneway = isViolatingOneway(vehicle);

		int endPtIdx = !violatingOneway ? segment + 1 : segment;
		GamaPoint segmentEndPt = new GamaPoint(road.getInnerGeometry().getCoordinates()[endPtIdx]);

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
			boolean sameDirection = target == getCurrentTarget(otherVehicle);
			double otherVL = getVehicleLength(otherVehicle);
			double otherDistToSegmentEnd;
			if (sameDirection) {
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
				leadingSameDirection = sameDirection;
			} else if (otherFrontToMyRear > 0 && otherFrontToMyRear < minBackDist) {
				backVehicle = otherVehicle;
				minBackDist = otherFrontToMyRear;
				backSameDirection = sameDirection;
			}
		}

		// We don't look further behind to find a back vehicle for now?
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
			if ((!violatingOneway && segment == numSegments - 1) ||
					(violatingOneway && segment == 0)) {
				// Return a virtual leading vehicle of length 0 to simulate deceleration at intersections
				// NOTE: the added minSafetyDist is necessary for the vehicle to ignore the safety dist when stopping at an endpoint
				IAgent stoppingNode = target;
				leadingTriple = ImmutableTriple.of(stoppingNode, distToSegmentEnd + minSafetyDist, false);
				// Slowing down at final target, since at this point we don't know which road will be taken next
				if (nextRoad == null) {
					return ImmutablePair.of(leadingTriple, backTriple);
				// Might need to slow down at the intersection if it is not possible to enter the next road
				} else {
					if (!readyToCross(scope, vehicle, stoppingNode, nextRoad)) {
						return ImmutablePair.of(leadingTriple, backTriple);
					}
				}
			}

			// TODO: rework this spaghetti bowl
			// Continue to find leading vehicle on next segment or next road in path
			minLeadingDist = distToSegmentEnd - 0.5 * vL;
			IAgent roadToCheck = null;
			IAgent targetToCheck = null;
			int lowestLaneToCheck = 0;
			int segmentToCheck = 0;
			if ((!violatingOneway && segment < numSegments - 1) ||
					(violatingOneway && segment > 0)) {
				roadToCheck = road;
				targetToCheck = target;
				segmentToCheck = !violatingOneway ? segment + 1 : segment - 1;
				lowestLaneToCheck = lowestLane;
			} else if (road != nextRoad) {  // road == nextRoad when vehicle is at an intersection
				roadToCheck = nextRoad;
				boolean willViolateOneway = target == RoadSkill.getTargetNode(nextRoad);
				targetToCheck = !willViolateOneway ?
					RoadSkill.getTargetNode(nextRoad) : RoadSkill.getSourceNode(nextRoad);
				segmentToCheck = !willViolateOneway ? 0 : RoadSkill.getNumSegments(nextRoad) - 1;
				// TODO: is this the right lane to check?
				int numLanesTotal = RoadSkill.getNumLanes(roadToCheck);
				IAgent linkedRoadToCheck = RoadSkill.getLinkedRoad(roadToCheck);
				if (linkedRoadToCheck != null) {
					numLanesTotal += RoadSkill.getNumLanes(linkedRoadToCheck);
				}
				lowestLaneToCheck = Math.min(lowestLane, numLanesTotal - numLanesOccupied);
			}

			if (roadToCheck != null) {
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
					boolean sameDirection = targetToCheck == getCurrentTarget(otherVehicle);
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
}
