/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.human_moving;

import msi.gama.environment.Different;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.skills.MovingSkill;
import msi.gama.util.*;
import com.vividsolutions.jts.geom.Geometry;

@vars({})
/*
 * This class present the human moving skills. 1. wanderingAndAvoid: with this skill, the agent move
 * randomly and avoid the others 2. approach : with this skill, the agent move more and more near
 * his goal and avoid the others Created by LE Van Minh - April 2010 Updated at 14:00 May 29, 2010
 */
@skill("human_moving")
public class HumanMovingSkill extends MovingSkill {

	/**
	 * @throws GamaRuntimeException LvMinh 1 Prim: move randomly. Has to be redefined for every
	 *             class that implements this interface.
	 * 
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */

	@action("wanderAndAvoid")
	@args({ "speed", "agent_size", "background", "ignore_type" })
	public GamaPoint primMoveRandomlyAndAvidOthers(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope.getArg("speed")) : null;
		if ( s != null ) {
			setSpeed(scope, agent, s);
		}
		double dist = getSpeed(agent) * getTimeStep(scope);
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope.getArg("agent_size")) : 0;
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		double detectingRange = dist + agentSize;
		double epsilon = 0.4;
		int numberOfSlot = (int) (dist / epsilon);
		int slot = GAMA.getRandom().between(-numberOfSlot, numberOfSlot);
		// OutputManager.debug("no slot " + numberOfSlot);
		// OutputManager.debug("slot " + slot);

		final Object background = scope.getArg("background");
		boolean isInBackgroundAgent;
		IAgent backgroundAgent = null;
		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = (IAgent) background;
		}

		GamaList<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(agent, detectingRange, Different.with());
		if ( isInBackgroundAgent ) {
			neighbours.remove(backgroundAgent);
		}

		final Object ignore = scope.getArg("ignore_type");
		IAgent ignoreAgent = (IAgent) ignore;
		for ( int i = 0; i < neighbours.size(); i++ ) {
			IAgent entity = neighbours.get(i);
			if ( ignoreAgent != null &&
				entity.getSpeciesName().equals(ignoreAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}
		}
		GamaPoint startingPoint = agent.getLocation();
		Geometry point0 =
			GamaGeometry.getFactory().createPoint(startingPoint.toCoordinate()).buffer(agentSize);
		if ( !isExteriorOfAgents(neighbours, point0) ) {
			dist = 2 * agentSize;
		}

		int sign = GAMA.getRandom().between(-2, 2);
		// OutputManager.debug("sign: " + sign);
		double x = startingPoint.x + slot * epsilon;
		// OutputManager.debug("x: " + x);
		double y;
		y =
			startingPoint.y +
				Math.sqrt(dist * dist - (x - startingPoint.x) * (x - startingPoint.x));
		if ( sign % 2 == 0 ) {
			// OutputManager.debug("sign is even ");
			y =
				startingPoint.y -
					Math.sqrt(dist * dist - (x - startingPoint.x) * (x - startingPoint.x));
		}
		// OutputManager.debug("y: " + y);
		GamaPoint px = new GamaPoint(x, y);
		Geometry point = GamaGeometry.getFactory().createPoint(px.toCoordinate()).buffer(agentSize);
		boolean isFoundNextPoint = false;
		if ( isInBackgroundAgent ) {
			isFoundNextPoint =
				isExteriorOfAgents(neighbours, point) &&
					backgroundAgent.getInnerGeometry().contains(point);
		} else {
			isFoundNextPoint = isExteriorOfAgents(neighbours, point);
		}

		if ( !isFoundNextPoint ) {
			scope.setStatus(ExecutionStatus.failure);
			return agent.getLocation();

		}
		agent.setLocation(px);
		scope.setStatus(ExecutionStatus.running);
		return agent.getLocation();
	}

	/**
	 * @throws GamaRuntimeException LvMinh 2 Prim: move to the nearest named object (can be an agent
	 *             or a GIS object) of a type .
	 * 
	 * @param args the args, contain at least a parameter called "target". Another parameter can be
	 *            "speed". if the agent displace inside a specific geometry, several other
	 *            parameters have to be added: either the name of a precomputed graph "graph_name",
	 *            a agent, or a geometry. In case where no graph is available, the choice of the
	 *            discretisation method can be made between a triangulation and a square
	 *            discretisation through the boolean "triangulation". At least for the square
	 *            discretisation, a square size has to be chosen "square_size".
	 * 
	 * @return the success, failure, running state of the action
	 */

	@action("approach")
	@args({ "target", "speed", "agent_size", "background" })
	public GamaPoint primMoveToTargetAndAvoidOthers(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		final Object target = scope.getArg("target");
		if ( target == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return agent.getLocation();
		}

		final Object background = scope.getArg("background");

		IAgent backgroundAgent = (IAgent) background;

		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope.getArg("speed")) : null;
		if ( s != null ) {
			setSpeed(scope, agent, s);
		}

		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope.getArg("agent_size")) : null;
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		final double maxDist = getSpeed(agent) * getTimeStep(scope);
		// OutputManager.debug("maxDist " + maxDist);
		/**
		 * test getNetDestination GamaPoint O = new GamaPoint(0 ,0); GamaPoint X = new GamaPoint(2,
		 * 4); GamaPoint D = getNextDestinationInDirection(O, X, 2);
		 * OutputManager.debug("test: "+D.x + " : " + D.y); /
		 **/
		double detectingRange = agentSize + maxDist;
		double epsilon = 0.4;
		IAgent targetAgent = (IAgent) target;

		if ( targetAgent.getGeometry().contains(agent.getGeometry()) ) { return targetAgent
			.getLocation(); }

		GamaPoint startingPoint = agent.getLocation();

		GamaPoint targetPoint = targetAgent.getGeometry().getLocation();
		// OutputManager.debug("Target " + targetPoint.x + " : " + targetPoint.y);

		GamaList<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(agent, detectingRange, Different.with());
		if ( backgroundAgent != null ) {
			neighbours.remove(backgroundAgent);
		}
		for ( int i = 0; i < neighbours.size(); i++ ) {
			IAgent entity = neighbours.get(i);
			if ( entity.getSpeciesName().equals(targetAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}

		}

		/**/
		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;
		double mininalDoubleDistance = Double.MAX_VALUE;

		for ( double x = startingPoint.x - maxDist; x <= startingPoint.x + maxDist; x = x + epsilon ) {
			double y1 =
				startingPoint.y -
					Math.sqrt(maxDist * maxDist - (x - startingPoint.x) * (x - startingPoint.x));
			double y2 =
				startingPoint.y +
					Math.sqrt(maxDist * maxDist - (x - startingPoint.x) * (x - startingPoint.x));

			GamaPoint px = new GamaPoint(x, y1);
			Geometry point =
				GamaGeometry.getFactory().createPoint(px.toCoordinate()).buffer(agentSize);
			if ( backgroundAgent != null ) {
				if ( isExteriorOfAgents(neighbours, point) &&
					backgroundAgent.getGeometry().contains(point) ) {
					isFoundNextPoint = true;
					double d = getDoubleDistance(px, targetPoint);
					if ( d < mininalDoubleDistance ) {
						mininalDoubleDistance = d;
						nextPoint = px;
					}
				}
			} else if ( isExteriorOfAgents(neighbours, point) ) {
				isFoundNextPoint = true;
				double d = getDoubleDistance(px, targetPoint);
				if ( d < mininalDoubleDistance ) {
					mininalDoubleDistance = d;
					nextPoint = px;
				}
			}

			px = new GamaPoint(x, y2);
			point = GamaGeometry.getFactory().createPoint(px.toCoordinate()).buffer(agentSize);
			if ( backgroundAgent != null ) {
				if ( isExteriorOfAgents(neighbours, point) &&
					backgroundAgent.getGeometry().contains(point) ) {
					isFoundNextPoint = true;
					double d = getDoubleDistance(px, targetPoint);
					if ( d < mininalDoubleDistance ) {
						mininalDoubleDistance = d;
						nextPoint = px;
					}
				}
			} else if ( isExteriorOfAgents(neighbours, point) ) {
				isFoundNextPoint = true;
				double d = getDoubleDistance(px, targetPoint);
				if ( d < mininalDoubleDistance ) {
					mininalDoubleDistance = d;
					nextPoint = px;
				}
			}
		}

		if ( !isFoundNextPoint ) {
			scope.setStatus(ExecutionStatus.failure);
			return agent.getLocation();

		}
		/**/
		// GamaPoint p0 = currentAgent.getLocation();
		// OutputManager.debug("before moving "+p0.x + " " + p0.y);
		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
		}
		scope.setStatus(ExecutionStatus.running);
		return agent.getLocation();
		// p0 = currentAgent.getLocation();
		// OutputManager.debug("after moving "+p0.x + " " + p0.y);

	}

	/**
	 * LvMinh 3
	 * @param O center of the circle
	 * @param X a point outside the circle
	 * @param r radius of the circle
	 * @return a point that is both on the circle and the line OX
	 */
	// private GamaPoint getNextDestinationInDirection(final GamaPoint O, final GamaPoint X,
	// final double r, final double epsilon) {
	// GamaPoint D = new GamaPoint(O.x, O.y);
	// GamaPoint inside = new GamaPoint(O.x, O.y);
	// GamaPoint outside = new GamaPoint(X.x, X.y);
	// boolean isContinue = false;
	// do {
	// D.x = (inside.x + outside.x) / 2;
	// D.y = (inside.y + outside.y) / 2;
	// double dd = (D.x - O.x) * (D.x - O.x) + (D.y - O.y) * (D.y - O.y);
	// if ( dd < r * r ) {
	// inside.x = D.x;
	// inside.y = D.y;
	// } else {
	// outside.x = D.x;
	// outside.y = D.y;
	// }
	// isContinue = Math.abs(dd - r * r) > epsilon * epsilon;
	// } while (isContinue);
	// return D;
	// }

	/**
	 * LvMinh 4 return the square of distance between 2 point
	 * @param A
	 * @param B
	 * @return the square of distance between 2 point
	 */
	private double getDoubleDistance(final GamaPoint A, final GamaPoint B) {
		double AB2 = (A.x - B.x) * (A.x - B.x) + (A.y - B.y) * (A.y - B.y);
		return AB2;
	}

	/**
	 * LvMinh 5 return
	 * @param agentList list of agents
	 * @param point a geometric point
	 * @return true if point is exterior of all agents in the list, and otherwise
	 */
	private boolean isExteriorOfAgents(final GamaList<IAgent> agentList, final Geometry point) {
		for ( IAgent agent : agentList ) {
			if ( agent.getInnerGeometry().intersects(point) ) { return false; }
		}
		return true;
	}

}
