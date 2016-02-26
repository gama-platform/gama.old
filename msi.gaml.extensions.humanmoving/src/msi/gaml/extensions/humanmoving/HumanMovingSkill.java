package msi.gaml.extensions.humanmoving;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.IType;

// @vars( {} )
/*
 * This class present the human moving skills. 1. wanderingAndAvoid: with this skill, the agent move
 * randomly and avoid the others 2. approach : with this skill, the agent move more and more near
 * his goal and avoid the others Created by LE Van Minh - April 2010 Updated at 14:00 May 29, 2010
 */
@skill(name = "humanmoving", concept = { IConcept.SKILL })
public class HumanMovingSkill extends MovingSkill {

	private final double MIN_DISTANCE = 1;

	/**
	 * LvMinh 1 Prim: move randomly. Has to be redefined for every class that implements this
	 * interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */

	@action(name = "wanderAndAvoid",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background"), @arg(name = "ignore_type") })
	public GamaPoint primMoveRandomlyAndAvidOthers(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		if ( s != null ) {
			setSpeed(agent, s);
		}
		double dist = computeDistance(scope, agent);
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : 0;
		// if ( agentSize != null ) {} else {
		// agentSize = new Double(2);
		// }
		// final Double s = args.floatValue("speed");
		// if ( s != null ) {
		// setSpeed(s);
		// }
		// double dist = getSpeed() * timeStep;
		// Double agentSize = args.floatValue("agent_size");
		// if ( agentSize != null ) {} else {
		// agentSize = new Double(2);
		// }

		// double detectingRange = dist + agentSize;
		final double detectingRange = dist + agentSize;
		final double epsilon = 0.4;
		final int numberOfSlot = (int) (dist / epsilon);
		final int slot = scope.getRandom().between(-numberOfSlot, numberOfSlot);

		final Object background = scope.getArg("background", IType.NONE);
		final IAgent backgroundAgent = (IAgent) background;

		// OutputManager.debug("no slot " + numberOfSlot);
		// OutputManager.debug("slot " + slot);

		// final Object background = args.value("background");
		// boolean isInBackgroundAgent;
		// IAgent backgroundAgent = null;
		// if ( background == null ) {
		// isInBackgroundAgent = false;
		// } else {
		// isInBackgroundAgent = true;
		// backgroundAgent = (LocalizedEntity) background;
		// }

		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		if ( backgroundAgent != null ) {
			neighbours.remove(backgroundAgent);
		}
		final Object ignore = scope.getArg("ignore_type", IType.AGENT);
		final IAgent ignoreAgent = (IAgent) ignore;
		// final Object ignore = args.value("ignore_type");
		// LocalizedEntity ignoreAgent = (LocalizedEntity) ignore;
		for ( int i = 0; i < neighbours.size(); i++ ) {
			final IAgent entity = neighbours.get(i);
			if ( ignoreAgent != null && entity.getSpeciesName().equals(ignoreAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}
		}

		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final Geometry point0 =
			((Geometry) GeometryUtils.FACTORY.createPoint(startingPoint.getLocation())).buffer(agentSize);
		if ( !isExteriorOfAgents(neighbours, point0) ) {
			dist = 2 * agentSize;
		}

		final int sign = scope.getRandom().between(-2, 2);
		// OutputManager.debug("sign: " + sign);
		final double x = startingPoint.getX() + slot * epsilon;
		// OutputManager.debug("x: " + x);
		double y;
		y = startingPoint.y + FastMath.sqrt(dist * dist - (x - startingPoint.x) * (x - startingPoint.x));
		if ( sign % 2 == 0 ) {
			// OutputManager.debug("sign is even ");
			y = startingPoint.y - FastMath.sqrt(dist * dist - (x - startingPoint.x) * (x - startingPoint.x));
		}
		// OutputManager.debug("y: " + y);
		final GamaPoint px = new GamaPoint(x, y);
		final Geometry point = ((Geometry) GeometryUtils.FACTORY.createPoint(px.getLocation())).buffer(agentSize);
		boolean isFoundNextPoint = false;
		if ( backgroundAgent != null ) {
			isFoundNextPoint =
				isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point);
		} else {
			isFoundNextPoint = isExteriorOfAgents(neighbours, point);
		}

		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
			// setReturn(getLocation());
			// return CommandStatus.failure;
		}
		agent.setLocation(px);
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
		// setLocation(px);
		// setReturn(getLocation());
		// return CommandStatus.running;
	}

	/**
	 * nmhung 1 Prim: move randomly event above other agents. Has to be redefined for every class
	 * that implements this
	 * interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */
	@action(name = "wanderAbove",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")) })
	// @action("wanderAbove")
	// @args( { "speed", "agent_size"})
	public GamaPoint primMoveRandomlyAbove(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;

		if ( s != null ) {
			setSpeed(agent, s);
		}
		final double dist = getSpeed(agent) * scope.getClock().getStep();// timeStep;
		// OutputManager.debug("dist: " + dist);
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		// double detectingRange = dist + agentSize;
		// double detectingRange = dist;
		final Vector<GamaPoint> candidatePoint = new Vector<GamaPoint>();
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint.add((GamaPoint) agent.getLocation());
		candidatePoint.add(new GamaPoint(startingPoint.x + dist / sqrt2, startingPoint.y + dist / sqrt2));
		candidatePoint.add(new GamaPoint(startingPoint.x - dist / sqrt2, startingPoint.y - dist / sqrt2));
		candidatePoint.add(new GamaPoint(startingPoint.x + dist, startingPoint.y));
		candidatePoint.add(new GamaPoint(startingPoint.x - dist, startingPoint.y));
		candidatePoint.add(new GamaPoint(startingPoint.x, startingPoint.y + dist));
		candidatePoint.add(new GamaPoint(startingPoint.x, startingPoint.y - dist));
		candidatePoint.add(new GamaPoint(startingPoint.x + dist / sqrt2, startingPoint.y - dist / sqrt2));
		candidatePoint.add(new GamaPoint(startingPoint.x - dist / sqrt2, startingPoint.y + dist / sqrt2));

		final int index = scope.getRandom().between(0, candidatePoint.size() - 1);

		final GamaPoint px = candidatePoint.elementAt(index);
		// OutputManager.debug(px.x + " - " + px.y);

		// Geometry point =
		// ModelFactory.getGeometryFactory().createPoint(px.toCoordinate()).buffer(agentSize);
		// boolean isFoundNextPoint = false;
		// if ( isInBackgroundAgent ) {
		// isFoundNextPoint =
		// isExteriorOfAgents(neighbours, point) &&
		// backgroundAgent.getBody().getGeometry().contains(point);
		// } else {
		// boolean isFoundNextPoint = isExteriorOfAgents(neighbours, point);
		// }

		// if ( px == null ) {
		// setReturn(getLocation());
		// return CommandStatus.failure;
		// } else {
		agent.setLocation(px);
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
		// setReturn(getLocation());
		// return CommandStatus.running;
		// }
	}

	/**
	 * nmhung 1 Prim: move randomly in smokes or blackness. Has to be redefined for every class that
	 * implements this
	 * interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */
	@action(name = "blindWander",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	// @action("blindWander")
	// @args( { "speed", "agent_size", "background", "target"})
	public GamaPoint primMoveRandomlyBlind(final IScope scope) throws GamaRuntimeException {
		// preciser le type du target
		final Object target = scope.getArg("target", IType.AGENT);
		final IAgent targetAgent = (IAgent) target;
		// final Object target = args.value("target");
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) targetAgent.getLocation();
			// setReturn(getLocation());
			// return CommandStatus.failure;
		}
		// LocalizedEntity targetAgent = (LocalizedEntity) target;
		if ( targetAgent.getInnerGeometry().contains((Geometry) targetAgent.getGeometry()) ) {
			;
			// A verifier
			// if ( targetAgent.getInnerGeometry().contains(agent.getInnerGeometry()) ) { return
			// CommandStatus.success; }
		}

		final Object background = scope.getArg("background", IType.AGENT);
		// final Object background = args.value("background");
		final IAgent backgroundAgent = (IAgent) background;
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		// final Double s = args.floatValue("speed");
		if ( s != null ) {
			setSpeed(getCurrentAgent(scope), s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		// Double agentSize = args.floatValue("agent_size");
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		final double maxDist = getSpeed(getCurrentAgent(scope)) * scope.getClock().getStep();// timeStep;
		final GamaPoint startingPoint = (GamaPoint) getCurrentAgent(scope).getLocation();

		final double detectingRange = agentSize + maxDist;
		final GamaList<IAgent> neighbours = (GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope,
			getCurrentAgent(scope), detectingRange, Different.with());
		if ( backgroundAgent != null ) {
			neighbours.remove(backgroundAgent);
		}
		// neighbours.remove(body);
		for ( int i = 0; i < neighbours.size(); i++ ) {
			final IAgent entity = neighbours.get(i);
			if ( entity.getSpeciesName().equals(targetAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}

		}

		// int ii=0;
		// while (ii<neighbours.size()){
		// try{
		// LocalizedEntity entity = neighbours.get(ii);
		// if (entity.getSpecies() == targetAgent.getSpecies()){
		// neighbours.remove(ii);
		// }
		// else{
		// ii++;
		// }
		// }
		// catch(Exception e){
		// OutputManager.debug(e.getMessage());
		// }
		// }

		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;

		final GamaPoint candidatePoint[] = new GamaPoint[8];
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[6] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[0] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		final int index = new Random().nextInt(8);

		for ( int i = 0; i < 9; i++ ) {
			final Geometry point = GeometryUtils.FACTORY
				.createPoint(candidatePoint[(i + index) % 8].getLocation().toCoordinate()).buffer(agentSize);
			// Geometry point =
			// ModelFactory.getGeometryFactory().createPoint(candidatePoint[(i+index)%8].toCoordinate()).buffer(agentSize);
			if ( backgroundAgent != null ) {
				if ( isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point) ) {
					{
						isFoundNextPoint = true;
						nextPoint = candidatePoint[(i + index) % 8];
						// body.getAgent().setVal("heading", GamaMath.checkHeading((i+index)%8));
						break;
					}
				} // else if ( !isExteriorOfAgents(neighbours, point) ) {
					// isFreeZone = false;
					// break;
					// }
			}
		}

		if ( !isFoundNextPoint ) {
			// OutputManager.debug("failed");
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) targetAgent.getLocation();
		}
		/**/
		// GamaPoint p0 = body.getLocation();
		// OutputManager.debug("before moving "+p0.x + " " + p0.y);
		if ( nextPoint != null ) {
			targetAgent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) targetAgent.getLocation();
		// setReturn(getLocation());
		// p0 = body.getLocation();
		// OutputManager.debug("after moving "+p0.x + " " + p0.y);
		// return CommandStatus.running;
	}

	// Version 1.5
	/**
	 * nmhung 1 Prim: move randomly in smokes or blackness. Has to be redefined for every class that
	 * implements this
	 * interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */
	@action(name = "blindWander2",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	public GamaPoint primMoveRandomlyBlindSimple(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		if ( agent.getAttribute("target") == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		boolean isInBackgroundAgent = false;
		final Object background = scope.getArg("background", IType.NONE);
		IAgent backgroundAgent = null;
		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = Cast.asAgent(scope, background);
		}

		// *****
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		if ( agentSize == null ) {
			agentSize = new Double(0);
		}

		final double maxDist = computeDistance(scope, agent);
		final double detectingRange = maxDist;
		final IAgent obj = (IAgent) agent.getAttribute("target");
		final ILocation target = obj.getLocation();// computeTarget(scope, agent);
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		// GamaPoint targetPoint = (GamaPoint) target;
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		neighbours.remove(backgroundAgent);
		// *****
		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;

		final GamaPoint candidatePoint[] = new GamaPoint[8];
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[6] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[0] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		final int index = new Random().nextInt(8);
		boolean freeSpace;
		PreparedPolygon backgdGeom = null;
		if ( isInBackgroundAgent ) {
			backgdGeom = new PreparedPolygon((Polygonal) backgroundAgent.getGeometry().getInnerGeometry());
		}
		for ( int i = 0; i < 9; i++ ) {

			final GamaPoint point = candidatePoint[(i + index) % 8];
			final Geometry geomCand = GeometryUtils.FACTORY.createPoint(point).buffer(agentSize);
			// System.out.println("point : " + point+" candidat numero "+i);

			if ( backgdGeom != null && !backgdGeom.contains(geomCand) ) {
				// System.out.println("le point n'est pas dans le background");
				continue;
			}
			// System.out.println("le point est dans le background !");

			final PreparedPolygon geomCandOpt = new PreparedPolygon((Polygonal) geomCand);
			freeSpace = true;
			for ( final IAgent ag : neighbours ) {
				if ( !geomCandOpt.disjoint(ag.getInnerGeometry()) ) {
					freeSpace = false;
					break;
				}
			}
			// ************************
			// System.out.println("passedList avant d'ajouter le point" +passedList);
			// *************************
			if ( freeSpace ) {
				isFoundNextPoint = true;
				nextPoint = candidatePoint[(i + index) % 8];
				// body.getAgent().setVal("heading", GamaMath.checkHeading((i+index)%8));
				break;
			}
		}
		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.success);
		return (GamaPoint) agent.getLocation();

	}

	// end of version 1.5

	/**
	 * nmhung 1 Prim: move randomly in smokes or blackness. Has to be redefined for every class that
	 * implements this
	 * interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */

	@action(name = "blindStraightWander",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "direction", type = IType.INT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	// @action("blindStraightWander")
	// @args( { "speed", "agent_size", "background", "direction", "target"})
	public GamaPoint primMoveStraightBlind(final IScope scope) throws GamaRuntimeException {
		final Object target = scope.getArg("target", IType.AGENT);
		final IAgent targetAgent = (IAgent) target;
		final IAgent agent = getCurrentAgent(scope);
		// final Object target = args.value("target");
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) targetAgent.getLocation();
		}

		// if ( targetAgent.getBody().getGeometry().contains(body.getGeometry()) ) { return
		// CommandStatus.success; }

		final Object background = scope.getArg("background", IType.AGENT);
		// final Object background = args.value("background");
		boolean isInBackgroundAgent;
		IAgent backgroundAgent = null;

		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = (IAgent) background;
		}

		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		// final Double s = args.floatValue("speed");
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		// Double agentSize = args.floatValue("agent_size");
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		// int direction = args.intValue("direction");
		final int direction = scope.getIntArg("direction");

		final double maxDist = getSpeed(agent) * scope.getClock().getStep();// timeStep;
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();

		final double detectingRange = agentSize + maxDist;
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		if ( isInBackgroundAgent ) {
			neighbours.remove(backgroundAgent);
		}
		// neighbours.remove(body);
		for ( int i = 0; i < neighbours.size(); i++ ) {
			final IAgent entity = neighbours.get(i);
			if ( entity.getSpeciesName().equals(targetAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}

		}

		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;

		final GamaPoint candidatePoint[] = new GamaPoint[8];
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[6] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[0] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		int count = 0;
		final boolean[] ok = new boolean[8];
		for ( int i = 0; i < 8; i++ ) {
			ok[i] = false;
			final Geometry point =
				GeometryUtils.FACTORY.createPoint(candidatePoint[i].toCoordinate()).buffer(agentSize);

			if ( isInBackgroundAgent ) {
				if ( isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point) ) {
					count++;
					ok[i] = true;
				}
			}
		}

		if ( ok[direction % 8] && ok[(1 + direction) % 8] && ok[(7 + direction) % 8] ) {
			final int tmp = new Random().nextInt(7);
			switch (tmp) {
				case 0:
					nextPoint = candidatePoint[(1 + direction) % 8];
					agent.setDirectVarValue(scope, "direction", (1 + direction) % 8);
					// body.getAgent().setVal("direction", (1+direction)%8);
					break;
				case 1:
					nextPoint = candidatePoint[(7 + direction) % 8];
					agent.setDirectVarValue(scope, "direction", (7 + direction) % 8);
					// body.getAgent().setVal("direction", (7+direction)%8);
					break;
				default:
					nextPoint = candidatePoint[direction % 8];
					agent.setDirectVarValue(scope, "direction", direction % 8);
					// body.getAgent().setVal("direction", (direction)%8);
					break;
			}
			isFoundNextPoint = true;

		} else {

			final int tmp = new Random().nextInt(2);
			int index = -1;
			if ( tmp > 0 ) {
				index = 1;
			}

			for ( int i = 0; i < 8; i++ ) {
				if ( ok[(8 + i * index + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(8 + i * index + direction) % 8];
					targetAgent.setDirectVarValue(scope, "direction", (8 + i * index + direction) % 8);
					// body.getAgent().setVal("direction", (8+i*index+direction)%8);
					break;
				}
			}
		}

		if ( !isFoundNextPoint ) {
			// OutputManager.debug("failed");
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) targetAgent.getLocation();
		}
		/**/
		// GamaPoint p0 = body.getLocation();
		// OutputManager.debug("before moving "+p0.x + " " + p0.y);
		if ( nextPoint != null ) {
			targetAgent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) targetAgent.getLocation();
	}

	@action(name = "blindStraightWander2",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "direction", type = IType.INT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	public GamaPoint primMoveStraightBlindSimple(final IScope scope) throws GamaRuntimeException {
		// *****************************************************
		final IAgent agent = getCurrentAgent(scope);
		if ( agent.getAttribute("target") == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		final Object background = scope.getArg("background", IType.NONE);
		final IAgent backgroundAgent = Cast.asAgent(scope, background);

		// *****
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		if ( agentSize == null ) {
			agentSize = new Double(0);
		}

		final double maxDist = computeDistance(scope, agent);
		final double detectingRange = maxDist;
		final IAgent obj = (IAgent) agent.getAttribute("target");
		final ILocation target = obj.getLocation();// computeTarget(scope, agent);
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		// GamaPoint targetPoint = (GamaPoint) target;
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		neighbours.remove(backgroundAgent);
		// *****
		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;

		final GamaPoint candidatePoint[] = new GamaPoint[8];
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[6] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[0] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		// ********************************************************************************************
		boolean freeSpace;
		PreparedPolygon backgdGeom = null;
		if ( backgroundAgent != null ) {
			backgdGeom = new PreparedPolygon((Polygonal) backgroundAgent.getGeometry().getInnerGeometry());
		}
		final boolean[] ok = new boolean[8];
		for ( int i = 0; i < 8; i++ ) {
			ok[i] = false;
			final GamaPoint point = candidatePoint[i];
			final Geometry geomCand = GeometryUtils.FACTORY.createPoint(point).buffer(agentSize);
			// System.out.println("point : " + point+" candidat numero "+i);

			if ( backgdGeom != null && !backgdGeom.contains(geomCand) ) {
				// System.out.println("le point n'est pas dans le background");
				continue;
			}
			// System.out.println("le point est dans le background !");

			final PreparedPolygon geomCandOpt = new PreparedPolygon((Polygonal) geomCand);
			freeSpace = true;
			for ( final IAgent ag : neighbours ) {
				if ( !geomCandOpt.disjoint(ag.getInnerGeometry()) ) {
					freeSpace = false;
					break;
				}
			}

			if ( freeSpace ) {
				ok[i] = true;
			}
		}

		final int direction = (Integer) agent.getAttribute("direction");
		if ( ok[direction % 8] && ok[(1 + direction) % 8] && ok[(7 + direction) % 8] ) {
			final int tmp = new Random().nextInt(7);
			switch (tmp) {
				case 0:
					nextPoint = candidatePoint[(1 + direction) % 8];
					agent.setAttribute("direction", (1 + direction) % 8);
					break;
				case 1:
					nextPoint = candidatePoint[(7 + direction) % 8];
					agent.setAttribute("direction", (7 + direction) % 8);
					break;
				default:
					nextPoint = candidatePoint[direction % 8];
					agent.setAttribute("direction", direction % 8);
					break;
			}
			isFoundNextPoint = true;

		} else {

			final int tmp = new Random().nextInt(2);
			int index = -1;
			if ( tmp > 0 ) {
				index = 1;
			}

			for ( int i = 0; i < 8; i++ ) {
				if ( ok[(8 + i * index + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(8 + i * index + direction) % 8];
					agent.setAttribute("direction", (8 + i * index + direction) % 8);
					break;
				}
			}
		}

		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}

		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
	}

	/**
	 * nmhung 1 Prim: move randomly in smokes or blackness. Has to be redefined for every class that
	 * implements this
	 * interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. if the agent displace inside a specific geometry, the geometry (or an
	 *            agent with a geometry) has to be specified
	 * @return the prim CommandStatus
	 */
	@action(name = "blindWallTracking",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "passedList", type = IType.LIST, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	// @action("blindWallTracking")
	// @args( { "target", "speed", "agent_size", "background", "passedList"})
	// @setter("passedList")
	public GamaPoint primMoveWallTrackingBlind(final IScope scope) throws GamaRuntimeException {
		final Object target = scope.getArg("target", IType.AGENT);
		final IAgent targetAgent = (IAgent) target;
		final IAgent agent = getCurrentAgent(scope);
		// final Object target = args.value("target");
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) targetAgent.getLocation();
		}
		// if ( targetAgent.getBody().getGeometry().contains(body.getGeometry()) ) { return
		// CommandStatus.success; }

		final Object background = scope.getArg("background", IType.AGENT);
		boolean isInBackgroundAgent;
		IAgent backgroundAgent = null;

		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = (IAgent) background;
		}

		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		// final Double s = args.floatValue("speed");
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		// Double agentSize = args.floatValue("agent_size");
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		int direction = 0;
		final GamaList<GamaPoint> passedList = (GamaList<GamaPoint>) scope.getListArg("passedList");// args.listValue("passedList");

		if ( passedList == null ) {
			// passedList = new GamaList<GamaPoint>();
			// OutputManager.debug("passedList: null");
		} else {
			// OutputManager.debug("passedList length: "+passedList.size());
		}

		final double maxDist = getSpeed(agent) * scope.getClock().getStep();// timeStep;
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final GamaPoint targetPoint = (GamaPoint) targetAgent.getLocation();

		final double distanceToTarget =
			Math.sqrt((targetPoint.y - startingPoint.y) * (targetPoint.y - startingPoint.y) +
				(targetPoint.x - startingPoint.x) * (targetPoint.x - startingPoint.x));

		double tmpx = startingPoint.x + maxDist * (targetPoint.x - startingPoint.x) / distanceToTarget;
		double tmpy = startingPoint.y + maxDist * (targetPoint.y - startingPoint.y) / distanceToTarget;

		if ( passedList != null ) {
			if ( passedList.size() > 2 ) {
				final GamaPoint pp0 = passedList.get(0);
				final GamaPoint pp2 = passedList.get(1);
				final double ds = FastMath.sqrt((pp0.y - pp2.y) * (pp0.y - pp2.y) + (pp0.x - pp2.x) * (pp0.x - pp2.x));
				tmpx = startingPoint.x + maxDist * (pp0.x - pp2.x) / ds;
				tmpy = startingPoint.y + maxDist * (pp0.y - pp2.y) / ds;
			}
		}

		if ( tmpx > startingPoint.x ) {
			if ( tmpy > startingPoint.y ) {
				direction = 3;
			} else if ( tmpy < startingPoint.y ) {
				direction = 1;
			} else {
				direction = 2;
			}
		} else if ( tmpx < startingPoint.x ) {
			if ( tmpy > startingPoint.y ) {
				direction = 5;
			} else if ( tmpy < startingPoint.y ) {
				direction = 7;
			} else {
				direction = 6;
			}
		} else {
			if ( tmpy > startingPoint.y ) {
				direction = 4;
			} else {
				direction = 0;
			}
		}

		final double detectingRange = agentSize + maxDist;
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		if ( isInBackgroundAgent ) {
			neighbours.remove(backgroundAgent);
		}
		// neighbours.remove(body);
		for ( int i = 0; i < neighbours.size(); i++ ) {
			final IAgent entity = neighbours.get(i);
			if ( entity.getSpeciesName().equals(targetAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}

		}

		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;

		final GamaPoint candidatePoint[] = new GamaPoint[8];
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[6] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[0] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		int count = 0;
		final boolean[] ok = new boolean[8];

		for ( int i = 0; i < 8; i++ ) {
			ok[i] = false;
			final Geometry point =
				GeometryUtils.FACTORY.createPoint(candidatePoint[i].toCoordinate()).buffer(agentSize);

			if ( isInBackgroundAgent ) {
				if ( isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point) ) {
					count++;
					ok[i] = true;
				}
			}
		}

		if ( count == 8 || ok[direction % 8] && ok[(1 + direction) % 8] && ok[(7 + direction) % 8] ) {
			// int tmp = (new Random()).nextInt(15);
			// switch(tmp){
			// case 1: nextPoint = candidatePoint[(1+direction)%8]; break;
			// case 2: nextPoint = candidatePoint[(7+direction)%8]; break;
			// default:
			nextPoint = candidatePoint[direction % 8]; // break;
			// }
			isFoundNextPoint = true;

		} else {

			for ( int i = 0; i < 5; i++ ) {
				if ( ok[(i + direction) % 8] && !ok[(i + 1 + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(i + direction) % 8];
					break;
				}
				if ( !ok[(i + direction) % 8] && ok[(i + 1 + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(i + 1 + direction) % 8];
					break;
				}
				if ( ok[(8 - i + direction) % 8] && !ok[(7 - i + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(8 - i + direction) % 8];
					break;
				}
				if ( !ok[(8 - i + direction) % 8] && ok[(7 - i + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(7 - i + direction) % 8];
					break;
				}
			}
		}

		if ( !isNotRepeat(nextPoint, passedList) ) {
			tmpx = startingPoint.x + maxDist * (targetPoint.x - startingPoint.x) / distanceToTarget;
			tmpy = startingPoint.y + maxDist * (targetPoint.y - startingPoint.y) / distanceToTarget;
			/*
			 * if(passedList != null){
			 * if(passedList.size()>4){
			 * GamaPoint pp0 = passedList.get(0);
			 * GamaPoint pp2 = passedList.get(3);
			 * double ds = FastMath.sqrt((pp0.y - pp2.y) * (pp0.y - pp2.y) + (pp0.x - pp2.x) * (pp0.x -
			 * pp2.x));
			 * tmpx = startingPoint.x + maxDist*(pp0.x - pp2.x)/ds;
			 * tmpy = startingPoint.y + maxDist*(pp0.y - pp2.y)/ds;
			 * }
			 * }
			 */

			if ( tmpx > startingPoint.x ) {
				if ( tmpy > startingPoint.y ) {
					direction = 3;
				} else if ( tmpy < startingPoint.y ) {
					direction = 1;
				} else {
					direction = 2;
				}
			} else if ( tmpx < startingPoint.x ) {
				if ( tmpy > startingPoint.y ) {
					direction = 5;
				} else if ( tmpy < startingPoint.y ) {
					direction = 7;
				} else {
					direction = 6;
				}
			} else {
				if ( tmpy > startingPoint.y ) {
					direction = 4;
				} else {
					direction = 0;
				}
			}

			for ( int i = 0; i < 8; i++ ) {
				ok[i] = false;
				final Geometry point =
					GeometryUtils.FACTORY.createPoint(candidatePoint[i].toCoordinate()).buffer(agentSize);

				if ( isInBackgroundAgent ) {
					if ( isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point) ) {
						count++;
						ok[i] = true;
					}
				}
			}

			if ( count == 8 || ok[direction % 8] && ok[(1 + direction) % 8] && ok[(7 + direction) % 8] &&
				(ok[(2 + direction) % 8] || ok[(6 + direction) % 8]) ) {
				final int tmp = new Random().nextInt(15);
				switch (tmp) {
					case 1:
						nextPoint = candidatePoint[(1 + direction) % 8];
						break;
					case 2:
						nextPoint = candidatePoint[(7 + direction) % 8];
						break;
					default:
						nextPoint = candidatePoint[direction % 8];
						break;
				}
				isFoundNextPoint = true;

			} else {

				for ( int i = 0; i < 5; i++ ) {
					if ( ok[(i + direction) % 8] && !ok[(i + 1 + direction) % 8] ) {
						isFoundNextPoint = true;
						nextPoint = candidatePoint[(i + direction) % 8];
						break;
					}
					if ( !ok[(i + direction) % 8] && ok[(i + 1 + direction) % 8] ) {
						isFoundNextPoint = true;
						nextPoint = candidatePoint[(i + 1 + direction) % 8];
						break;
					}
					if ( ok[(8 - i + direction) % 8] && !ok[(7 - i + direction) % 8] ) {
						isFoundNextPoint = true;
						nextPoint = candidatePoint[(8 - i + direction) % 8];
						break;
					}
					if ( !ok[(8 - i + direction) % 8] && ok[(7 - i + direction) % 8] ) {
						isFoundNextPoint = true;
						nextPoint = candidatePoint[(7 - i + direction) % 8];
						break;
					}
				}
			}
		}

		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		/**/
		// GamaPoint p0 = body.getLocation();
		// OutputManager.debug("before moving "+p0.x + " " + p0.y);
		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
	}

	// ************
	@action(name = "blindWallTracking2",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "passedList", type = IType.LIST, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	public GamaPoint primMoveWallTrackingBlindSimple(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		if ( agent.getAttribute("target") == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		boolean isInBackgroundAgent = false;
		final Object background = scope.getArg("background", IType.NONE);
		IAgent backgroundAgent = null;
		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = Cast.asAgent(scope, background);
		}

		// *****
		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		if ( agentSize == null ) {
			agentSize = new Double(0);
		}
		final IAgent obj = (IAgent) agent.getAttribute("target");
		final ILocation target = obj.getLocation();// computeTarget(scope, agent);
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		int direction = 0;
		ArrayList<GamaPoint> passedList = (ArrayList<GamaPoint>) agent.getAttribute("passedList");// args.listValue("passedList");

		if ( passedList == null ) {
			passedList = new ArrayList<GamaPoint>();
		} else {
			// System.out.println("longueur de passedList : "+passedList.length());
			if ( passedList.size() == 5 ) {
				passedList = new ArrayList<GamaPoint>();
			}
		}

		final double maxDist = getSpeed(agent) * scope.getClock().getStep();// timeStep;
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final GamaPoint targetPoint = (GamaPoint) target.getLocation();

		final double distanceToTarget =
			Math.sqrt((targetPoint.y - startingPoint.y) * (targetPoint.y - startingPoint.y) +
				(targetPoint.x - startingPoint.x) * (targetPoint.x - startingPoint.x));

		final double tmpx = startingPoint.x + maxDist * (targetPoint.x - startingPoint.x) / distanceToTarget;
		final double tmpy = startingPoint.y + maxDist * (targetPoint.y - startingPoint.y) / distanceToTarget;

		if ( tmpx > startingPoint.x ) {
			if ( tmpy > startingPoint.y ) {
				direction = 3;
			} else if ( tmpy < startingPoint.y ) {
				direction = 1;
			} else {
				direction = 2;
			}
		} else if ( tmpx < startingPoint.x ) {
			if ( tmpy > startingPoint.y ) {
				direction = 5;
			} else if ( tmpy < startingPoint.y ) {
				direction = 7;
			} else {
				direction = 6;
			}
		} else {
			if ( tmpy > startingPoint.y ) {
				direction = 4;
			} else {
				direction = 0;
			}
		}

		final double detectingRange = agentSize + maxDist;
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		neighbours.remove(backgroundAgent);

		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;

		final GamaPoint candidatePoint[] = new GamaPoint[8];
		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[6] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[0] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		int count = 0;
		final boolean[] ok = new boolean[8];
		boolean freeSpace;
		PreparedPolygon backgdGeom = null;
		if ( isInBackgroundAgent ) {
			backgdGeom = new PreparedPolygon((Polygonal) backgroundAgent.getGeometry().getInnerGeometry());
		}
		for ( int i = 0; i < 8; i++ ) {
			ok[i] = false;
			final GamaPoint point = candidatePoint[i];
			final Geometry geomCand = GeometryUtils.FACTORY.createPoint(point).buffer(agentSize);
			// System.out.println("point : " + point+" candidat numero "+i);

			if ( backgdGeom != null && !backgdGeom.contains(geomCand) ) {
				// System.out.println("le point n'est pas dans le background");
				continue;
			}
			// System.out.println("le point est dans le background !");

			final PreparedPolygon geomCandOpt = new PreparedPolygon((Polygonal) geomCand);
			freeSpace = true;
			for ( final IAgent ag : neighbours ) {
				if ( !geomCandOpt.disjoint(ag.getInnerGeometry()) ) {
					freeSpace = false;
					break;
				}
			}

			if ( freeSpace ) {
				count++;
				ok[i] = true;
			}
		}

		if ( count == 8 || ok[direction % 8] && ok[(1 + direction) % 8] && ok[(7 + direction) % 8] ) {
			nextPoint = candidatePoint[direction % 8]; // break;
			isFoundNextPoint = true;

		} else {
			for ( int i = 0; i < 5; i++ ) {
				if ( ok[(i + direction) % 8] && !ok[(i + 1 + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(i + direction) % 8];
					break;
				}
				if ( !ok[(i + direction) % 8] && ok[(i + 1 + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(i + 1 + direction) % 8];
					break;
				}
				if ( ok[(8 - i + direction) % 8] && !ok[(7 - i + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(8 - i + direction) % 8];
					break;
				}
				if ( !ok[(8 - i + direction) % 8] && ok[(7 - i + direction) % 8] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[(7 - i + direction) % 8];
					break;
				}
			}
		}
		// System.out.println("voici le next point" + nextPoint);
		if ( isInLoop(scope, passedList) ) {
			while (direction < 7 && direction >= 0) {
				direction++;
				if ( ok[direction] ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[direction % 8];
					break;
				}

			}
		}
		/*
		 * if(!isNotRepeat(nextPoint, passedList)){
		 * tmpx = startingPoint.x + maxDist*(targetPoint.x - startingPoint.x)/distanceToTarget;
		 * tmpy = startingPoint.y + maxDist*(targetPoint.y - startingPoint.y)/distanceToTarget;
		 * /*
		 * if(passedList != null){
		 * if(passedList.size()>4){
		 * GamaPoint pp0 = passedList.get(0);
		 * GamaPoint pp2 = passedList.get(3);
		 * double ds = FastMath.sqrt((pp0.y - pp2.y) * (pp0.y - pp2.y) + (pp0.x - pp2.x) * (pp0.x -
		 * pp2.x));
		 * tmpx = startingPoint.x + maxDist*(pp0.x - pp2.x)/ds;
		 * tmpy = startingPoint.y + maxDist*(pp0.y - pp2.y)/ds;
		 * }
		 * }*
		 *
		 * if(tmpx > startingPoint.x){
		 * if(tmpy > startingPoint.y)
		 * direction = 3;
		 * else if(tmpy < startingPoint.y)
		 * direction = 1;
		 * else
		 * direction = 2;
		 * }else if(tmpx < startingPoint.x){
		 * if(tmpy > startingPoint.y)
		 * direction = 5;
		 * else if(tmpy < startingPoint.y)
		 * direction = 7;
		 * else
		 * direction = 6;
		 * }else{
		 * if(tmpy > startingPoint.y)
		 * direction = 4;
		 * else
		 * direction = 0;
		 * }
		 *
		 * for(int i=0; i<8; i++){
		 * ok[i] = false;
		 * GamaPoint point = candidatePoint[i];
		 * Geometry geomCand = GeometryUtils.getFactory().createPoint(point).buffer(agentSize);
		 * //System.out.println("point : " + point+" candidat numero "+i);
		 *
		 * if (backgdGeom != null && ! backgdGeom.contains(geomCand)) {
		 * System.out.println("le point n'est pas dans le background");
		 * continue;
		 * }
		 * System.out.println("le point est dans le background !");
		 *
		 * PreparedPolygon geomCandOpt = new PreparedPolygon((Polygonal)geomCand);
		 * freeSpace = true;
		 * for (IAgent ag : neighbours) {
		 * if (! geomCandOpt.disjoint(ag.getInnerGeometry())) {
		 * freeSpace = false;
		 * break;
		 * }
		 * }
		 *
		 * if (freeSpace) {
		 * count++;
		 * ok[i] = true;
		 * }
		 * }
		 *
		 * if((count == 8)||(ok[(direction)%8] && ok[(1+direction)%8] && ok[(7+direction)%8] &&
		 * (ok[(2+direction)%8] || ok[(6+direction)%8]))){
		 * int tmp = (new Random()).nextInt(15);
		 * switch(tmp){
		 * case 1: nextPoint = candidatePoint[(1+direction)%8]; break;
		 * case 2: nextPoint = candidatePoint[(7+direction)%8]; break;
		 * default: nextPoint = candidatePoint[(direction)%8]; break;
		 * }
		 * isFoundNextPoint = true;
		 *
		 * }else{
		 *
		 * for(int i=0; i<5; i++){
		 * if ( (ok[(i+direction)%8]) && (!ok[(i+1+direction)%8])) {
		 * isFoundNextPoint = true;
		 * nextPoint = candidatePoint[(i+direction)%8];
		 * break;
		 * }
		 * if ( (!ok[(i+direction)%8]) && ok[(i+1+direction)%8]) {
		 * isFoundNextPoint = true;
		 * nextPoint = candidatePoint[(i+1+direction)%8];
		 * break;
		 * }
		 * if ( ok[(8-i+direction)%8] && (!ok[(7-i+direction)%8])) {
		 * isFoundNextPoint = true;
		 * nextPoint = candidatePoint[(8-i+direction)%8];
		 * break;
		 * }
		 * if ( (!ok[(8-i+direction)%8]) && ok[(7-i+direction)%8]) {
		 * isFoundNextPoint = true;
		 * nextPoint = candidatePoint[(7-i+direction)%8];
		 * break;
		 * }
		 * }
		 * }
		 * }
		 */

		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}

		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
			passedList.add(nextPoint);
			agent.setAttribute("passedList", passedList);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
	}

	// *************
	private boolean isNotRepeat(final GamaPoint startingPoint, final GamaList<GamaPoint> passedList) {

		if ( startingPoint != null && passedList != null ) {
			for ( int index = 0; index < passedList.size() - 1; index++ ) {
				final GamaPoint pp = passedList.get(index);
				final double dx = getDoubleDistance(startingPoint, pp);
				// System.out.println(pp.x + " - " + pp.y + ": " + dx);
				if ( dx < MIN_DISTANCE ) { return false; }
			}
		}
		return true;
	}

	/**
	 * LvMinh 2 Prim: move to the nearest named object (can be an agent or a GIS object) of a type .
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
	@action(name = "approach",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true) })
	// @action("approach")
	// @args( { "target", "speed", "agent_size", "background" })
	public GamaPoint primMoveToTargetAndAvoidOthers(final IScope scope) throws GamaRuntimeException {

		final Object target = scope.getArg("target", IType.AGENT);
		final IAgent targetAgent = (IAgent) target;
		final IAgent agent = getCurrentAgent(scope);
		// final Object target = args.value("target");
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) targetAgent.getLocation();
		}
		// if ( targetAgent.getBody().getGeometry().contains(body.getGeometry()) ) { return
		// CommandStatus.success; }

		final Object background = scope.getArg("background", IType.AGENT);
		boolean isInBackgroundAgent;
		IAgent backgroundAgent = null;

		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = (IAgent) background;
		}

		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		// final Double s = args.floatValue("speed");
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		// Double agentSize = args.floatValue("agent_size");
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		final double maxDist = getSpeed(agent) * scope.getClock().getStep();// timeStep;
		// OutputManager.debug("maxDist " + maxDist);
		/**
		 * test getNetDestination GamaPoint O = new GamaPoint(0 ,0); GamaPoint X = new GamaPoint(2,
		 * 4); GamaPoint D = getNextDestinationInDirection(O, X, 2);
		 * OutputManager.debug("test: "+D.x + " : " + D.y); /
		 **/
		// double detectingRange = agentSize + maxDist;
		final double detectingRange = agentSize + maxDist;
		final double epsilon = maxDist / 2;

		// if ( targetAgent.getBody().getGeometry().contains(body.getGeometry()) ) { return
		// CommandStatus.success; }

		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();

		final GamaPoint targetPoint = (GamaPoint) targetAgent.getLocation();
		// OutputManager.debug("Target " + targetPoint.x + " : " + targetPoint.y);
		// OutputManager.debug("Detecting range : "+ detectingRange);
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		if ( isInBackgroundAgent ) {
			neighbours.remove(backgroundAgent);
		}
		// neighbours.remove(body);
		for ( int i = 0; i < neighbours.size(); i++ ) {
			final IAgent entity = neighbours.get(i);
			if ( entity.getSpeciesName().equals(targetAgent.getSpeciesName()) ) {
				neighbours.remove(i);
			}

		}
		/**
		 * for ( int i = 0; i < neighbours.size(); i++ ) {
		 * try {
		 * LocalizedEntity entity = neighbours.get(i);
		 * if ( entity.getSpecies() == targetAgent.getSpecies() ) {
		 * neighbours.remove(i);
		 * }
		 * } catch (Exception e) {
		 * OutputManager.debug(e.getMessage());
		 * }
		 * }
		 * /
		 **/
		/**/
		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;
		double mininalDoubleDistance = Double.MAX_VALUE;

		for ( double x = startingPoint.x - maxDist; x <= startingPoint.x + maxDist; x = x + epsilon ) {
			final double y1 =
				startingPoint.y - FastMath.sqrt(maxDist * maxDist - (x - startingPoint.x) * (x - startingPoint.x));
			final double y2 =
				startingPoint.y + FastMath.sqrt(maxDist * maxDist - (x - startingPoint.x) * (x - startingPoint.x));

			GamaPoint px = new GamaPoint(x, y1);
			Geometry point = GeometryUtils.FACTORY.createPoint(px.toCoordinate()).buffer(agentSize);
			if ( isInBackgroundAgent ) {
				if ( isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point) ) {
					isFoundNextPoint = true;
					final double d = getDoubleDistance(px, targetPoint);
					if ( d < mininalDoubleDistance ) {
						mininalDoubleDistance = d;
						nextPoint = px;
					}
				}
			} else if ( isExteriorOfAgents(neighbours, point) ) {
				isFoundNextPoint = true;
				final double d = getDoubleDistance(px, targetPoint);
				if ( d < mininalDoubleDistance ) {
					mininalDoubleDistance = d;
					nextPoint = px;
				}
			}

			px = new GamaPoint(x, y2);
			point = GeometryUtils.FACTORY.createPoint(px.toCoordinate()).buffer(agentSize);
			if ( isInBackgroundAgent ) {
				if ( isExteriorOfAgents(neighbours, point) && backgroundAgent.getInnerGeometry().contains(point) ) {
					isFoundNextPoint = true;
					final double d = getDoubleDistance(px, targetPoint);
					if ( d < mininalDoubleDistance ) {
						mininalDoubleDistance = d;
						nextPoint = px;
					}
				}
			} else if ( isExteriorOfAgents(neighbours, point) ) {
				isFoundNextPoint = true;
				final double d = getDoubleDistance(px, targetPoint);
				if ( d < mininalDoubleDistance ) {
					mininalDoubleDistance = d;
					nextPoint = px;
				}
			}
		}

		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		/**/
		final GamaPoint p0 = (GamaPoint) agent.getLocation();
		// OutputManager.debug("before moving "+p0.x + " " + p0.y);
		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
	}

	/**
	 * nmhung 2 Prim: move to the nearest named object (can be an agent or a GIS object) of a type
	 * and avoid the passed postions .
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
	@action(name = "approachAvoidPassedPosition",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true),
			@arg(name = "passedList", type = IType.LIST, optional = true), })
	public GamaPoint primMoveToTargetAndAvoidPassedPosition(final IScope scope) throws GamaRuntimeException {

		final IAgent test = scope.getAgentScope();
		final Object target = test.getAgent().getAttribute("");
		final IAgent targetAgent = (IAgent) target;

		final IAgent agent = getCurrentAgent(scope);

		// final Object target = args.value("target");
		if ( agent.getAttribute("goal") == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		// final Object background = agent.getAgent().getAttribute("metro");
		// IAgent back = (IAgent)background;
		// if ( targetAgent.getBody().getGeometry().contains(body.getGeometry()) ) { return
		// CommandStatus.success; }
		/*
		 * final Object background = scope.getVarValue("background");
		 * Object vitesse = scope.getVarValue("speed");
		 * Object agent_size = scope.getVarValue("agent_size");
		 * Object backg = scope.getVarValue("goal");
		 *
		 * Object age1 = scope.getArg("background",IType.SPECIES);
		 * GamlAgent age3 = (GamlAgent)scope.getArg("background",IType.AGENT);
		 * Object age4 = scope.getArg("vitesse",IType.FLOAT);
		 *
		 * Double fl = scope.getFloatArg("speed");
		 * // IAgent ia = scope.getAgentVarValue(agent, "background");
		 * // float flo = (Float)age4;
		 * GamlSpecies agg = (GamlSpecies) background;IAgent c= agg.;
		 * boolean boul = agg.containMicroSpecies(agent.getSpecies());
		 * IAgent iagg = agent.getAgent();
		 */
		boolean isInBackgroundAgent = false;
		final Object background = scope.getArg("background", IType.NONE);
		IAgent backgroundAgent = null;
		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = Cast.asAgent(scope, background);
		}

		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		// Double agentSize = args.floatValue("agent_size");
		if ( agentSize != null ) {} else {
			agentSize = new Double(2);
		}

		final double maxDist = getSpeed(agent) * scope.getClock().getStep();// timeStep;

		// OutputManager.debug("maxDist " + maxDist);
		/**
		 * test getNetDestination GamaPoint O = new GamaPoint(0 ,0); GamaPoint X = new GamaPoint(2,
		 * 4); GamaPoint D = getNextDestinationInDirection(O, X, 2);
		 * OutputManager.debug("test: "+D.x + " : " + D.y); /
		 **/
		// double detectingRange = agentSize + maxDist;
		final double detectingRange = agentSize + maxDist;
		// double epsilon = maxDist/3;

		/*
		 * if ( targetAgent.getInnerGeometry().contains(targetAgent.getInnerGeometry()) ) {
		 * //scope.setStatus(ExecutionStatus.running);
		 * return (GamaPoint) agent.getLocation();
		 * }
		 */

		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final IAgent metro = null;
		final GamaPoint targetPoint = (GamaPoint) targetAgent.getLocation();
		// OutputManager.debug("Target " + targetPoint.x + " : " + targetPoint.y);
		// OutputManager.debug("Detecting range : "+ detectingRange);
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		// if (neighbours.contains())
		// neighbours.remove("metro0");
		// if ( isInBackgroundAgent ) {
		// ;// neighbours.remove(backgroundAgent);
		// }
		// neighbours.remove(body);
		/*
		 * for ( int i = 0; i < neighbours.size(); i++ ) {
		 * IAgent entity = neighbours.get(i);
		 * if ( entity.getSpeciesName().equals(targetAgent.getSpeciesName()) ) {
		 * neighbours.remove(i);
		 * }
		 *
		 * if (entity.getSpeciesName().equals("metro")) {
		 * metro = neighbours.get(i);
		 * neighbours.remove(i);
		 * }
		 *
		 * }
		 */
		neighbours.remove(backgroundAgent);
		/**
		 * for ( int i = 0; i < neighbours.size(); i++ ) {
		 * try {
		 * LocalizedEntity entity = neighbours.get(i);
		 * if ( entity.getSpecies() == targetAgent.getSpecies() ) {
		 * neighbours.remove(i);
		 * }
		 * } catch (Exception e) {
		 * OutputManager.debug(e.getMessage());
		 * }
		 * }
		 * /
		 **/
		/**/
		boolean isFoundNextPoint = false;
		GamaPoint nextPoint = null;
		// double mininalDoubleDistance = Double.MAX_VALUE;
		/*
		 * boolean isLooped = false;
		 *
		 * if(passedList != null){
		 * for(int index=0; index < passedList.size(); index++){
		 * GamaPoint pp = passedList.get(index);
		 * double dx = getDoubleDistance(startingPoint, pp);
		 * //System.out.println(pp.x + " - " + pp.y + ": " + dx);
		 * if(dx < 1.3*MIN_DISTANCE){
		 * isLooped = true;
		 * break;
		 * }
		 * }
		 * }
		 */

		final GamaPoint candidatePoint[] = new GamaPoint[9];
		final double distanceToTarget =
			Math.sqrt((targetPoint.y - startingPoint.y) * (targetPoint.y - startingPoint.y) +
				(targetPoint.x - startingPoint.x) * (targetPoint.x - startingPoint.x));

		double tmpx = startingPoint.x + maxDist * (targetPoint.x - startingPoint.x) / distanceToTarget; // cos(alpha)
		double tmpy = startingPoint.y + maxDist * (targetPoint.y - startingPoint.y) / distanceToTarget; // sin(alpha)
		candidatePoint[0] = new GamaPoint(tmpx, tmpy);

		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[3] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[4] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		boolean isFreeZone = true;
		/*****/

		// boolean bool = metro.contains(agent);
		/****/
		for ( int i = 0; i < 9; i++ ) {
			final Geometry point = GeometryUtils.FACTORY.createPoint(candidatePoint[i]).buffer(agentSize);
			if ( isInBackgroundAgent ) {
				final String ss = point.toString();
				if ( !isExteriorOfAgents(neighbours, point) || !backgroundAgent.getInnerGeometry().contains(point) ) {
					isFreeZone = false;
					break;
				}
			} // else if ( !isExteriorOfAgents(neighbours, point) ) {
				// isFreeZone = false;
				// break;
				// }
		}

		final GamaList<GamaPoint> passedList = (GamaList<GamaPoint>) scope.getListArg("passedList");
		if ( !isFreeZone && passedList != null ) {
			if ( passedList.size() > 3 ) {
				final GamaPoint pp0 = passedList.get(0);
				final GamaPoint pp2 = passedList.get(3);
				final double ds = FastMath.sqrt((pp0.y - pp2.y) * (pp0.y - pp2.y) + (pp0.x - pp2.x) * (pp0.x - pp2.x));
				tmpx = startingPoint.x + maxDist * (pp0.x - pp2.x) / ds;
				tmpy = startingPoint.y + maxDist * (pp0.y - pp2.y) / ds;
				candidatePoint[0] = new GamaPoint(tmpx, tmpy);
			}
		}

		// if(!isLooped){

		if ( tmpx > startingPoint.x ) {
			if ( tmpy > startingPoint.y ) {
				candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
				candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
				candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
				candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
				candidatePoint[3] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
				candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
				candidatePoint[4] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
				candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
			} else {
				candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
				candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
				candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
				candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
				candidatePoint[3] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
				candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
				candidatePoint[4] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
				candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
			}
		} else {
			if ( tmpy > startingPoint.y ) {
				candidatePoint[1] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
				candidatePoint[8] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
				candidatePoint[2] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
				candidatePoint[7] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
				candidatePoint[3] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
				candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
				candidatePoint[4] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
				candidatePoint[5] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
			} else {
				candidatePoint[1] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
				candidatePoint[8] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
				candidatePoint[2] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
				candidatePoint[7] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
				candidatePoint[3] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
				candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
				candidatePoint[4] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
				candidatePoint[5] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
			}
		}
		/*
		 * }else{
		 * candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		 * candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		 * candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist/sqrt2, startingPoint.y +
		 * maxDist/sqrt2);
		 * candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist/sqrt2, startingPoint.y +
		 * maxDist/sqrt2);
		 * candidatePoint[3] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		 * candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		 * candidatePoint[4] = new GamaPoint(startingPoint.x + maxDist/sqrt2, startingPoint.y -
		 * maxDist/sqrt2);
		 * candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist/sqrt2, startingPoint.y -
		 * maxDist/sqrt2);
		 * }
		 */

		for ( int i = 0; i < 9; i++ ) {
			boolean isPassedPoint = false;

			if ( passedList != null ) {
				for ( int index = 0; index < passedList.size() - 1; index++ ) {
					final GamaPoint pp = passedList.get(index);
					final double dx = getDoubleDistance(candidatePoint[i], pp);
					// System.out.println(pp.x + " - " + pp.y + ": " + dx);
					if ( dx < MIN_DISTANCE ) {
						isPassedPoint = true;
						break;
					}
				}
			}

			if ( !isPassedPoint ) {
				final Geometry point =
					GeometryUtils.FACTORY.createPoint(candidatePoint[i].toCoordinate()).buffer(agentSize);
				if ( isInBackgroundAgent ) {
					if ( isExteriorOfAgents(neighbours, point) /*
																 * &&
																 * backgroundAgent.getInnerGeometry()
																 * .contains(point)
																 */ ) {
						isFoundNextPoint = true;
						nextPoint = candidatePoint[i];
						break;
					}
				} else if ( isExteriorOfAgents(neighbours, point) ) {
					isFoundNextPoint = true;
					nextPoint = candidatePoint[i];
					break;
				}
			}
		}

		/*
		 * for ( double x = startingPoint.x - maxDist; x <= startingPoint.x + maxDist; x = x +
		 * epsilon ) {
		 * double y1 =
		 * startingPoint.y -
		 * FastMath.sqrt(maxDist * maxDist - (x - startingPoint.x) * (x - startingPoint.x));
		 * double y2 =
		 * startingPoint.y +
		 * FastMath.sqrt(maxDist * maxDist - (x - startingPoint.x) * (x - startingPoint.x));
		 *
		 * GamaPoint px = new GamaPoint(x, y1);
		 * boolean isPassedPoint = false;
		 * if(passedList != null){
		 * for(int index=0; index < passedList.size(); index++){
		 * GamaPoint pp = passedList.get(index);
		 * double dx = getDoubleDistance(px, pp);
		 * //System.out.println(pp.x + " - " + pp.y + ": " + dx);
		 * if(dx < MIN_DISTANCE){
		 * isPassedPoint = true;
		 * break;
		 * }
		 * }
		 * }
		 *
		 * if(!isPassedPoint){
		 * Geometry point =
		 * ModelFactory.getGeometryFactory().createPoint(px.toCoordinate()).buffer(agentSize);
		 * if ( isInBackgroundAgent ) {
		 * if ( isExteriorOfAgents(neighbours, point) &&
		 * backgroundAgent.getBody().getGeometry().contains(point) ) {
		 * isFoundNextPoint = true;
		 * double d = getDoubleDistance(px, targetPoint);
		 * if ( d < mininalDoubleDistance ) {
		 * mininalDoubleDistance = d;
		 * nextPoint = px;
		 * }
		 * }
		 * } else if ( isExteriorOfAgents(neighbours, point) ) {
		 * isFoundNextPoint = true;
		 * double d = getDoubleDistance(px, targetPoint);
		 * if ( d < mininalDoubleDistance ) {
		 * mininalDoubleDistance = d;
		 * nextPoint = px;
		 * }
		 * }
		 * }
		 *
		 *
		 *
		 * px = new GamaPoint(x, y2);
		 * isPassedPoint = false;
		 * if(passedList != null){
		 * for(int index=0; index < passedList.size(); index++){
		 * GamaPoint pp = passedList.get(index);
		 * double dx = getDoubleDistance(px, pp);
		 * if(dx < MIN_DISTANCE){
		 * isPassedPoint = true;
		 * break;
		 * }
		 * }
		 * }
		 *
		 * if(!isPassedPoint){
		 * Geometry point =
		 * ModelFactory.getGeometryFactory().createPoint(px.toCoordinate()).buffer(agentSize);
		 * if ( isInBackgroundAgent ) {
		 * if ( isExteriorOfAgents(neighbours, point) &&
		 * backgroundAgent.getBody().getGeometry().contains(point) ) {
		 * isFoundNextPoint = true;
		 * double d = getDoubleDistance(px, targetPoint);
		 * if ( d < mininalDoubleDistance ) {
		 * mininalDoubleDistance = d;
		 * nextPoint = px;
		 * }
		 * }
		 * } else if ( isExteriorOfAgents(neighbours, point) ) {
		 * isFoundNextPoint = true;
		 * double d = getDoubleDistance(px, targetPoint);
		 * if ( d < mininalDoubleDistance ) {
		 * mininalDoubleDistance = d;
		 * nextPoint = px;
		 * }
		 * }
		 * }
		 *
		 *
		 * }
		 */

		if ( !isFoundNextPoint ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		/**/
		// GamaPoint p0 = body.getLocation();
		// OutputManager.debug("before moving "+p0.x + " " + p0.y);
		if ( nextPoint != null ) {
			agent.setLocation(nextPoint);
		}
		// scope.setStatus(ExecutionStatus.running);
		return (GamaPoint) agent.getLocation();
	}

	@Override
	protected double computeDistance(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		// We do not change the speed of the agent anymore. Only the current primitive is affected
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		// 20/1/2012 Change : The speed of the agent is multiplied by the timestep in order to
		// obtain the maximal distance it can cover in one step.
		return s * scope.getClock().getStep()/* getTimeStep(scope) */;
	}

	/**
	 * nmhung 2 Prim: move to the nearest named object (can be an agent or a GIS object) of a type
	 * and avoid the passed postions .
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
	@action(name = "approachAvoidPassedPosition2",
		args = {
			@arg(name = "speed",
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "agent_size",
				type = IType.INT,
				optional = true,
				doc = @doc("specifiaction of size of the agent")),
			@arg(name = "background", type = IType.AGENT, optional = true),
			@arg(name = "target", type = IType.AGENT, optional = true),
			@arg(name = "passedList", type = IType.LIST, optional = true), })
	public GamaPoint primMoveToTargetAndAvoidPassedPositionSimple(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		if ( agent.getAttribute("target") == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		boolean isInBackgroundAgent = false;
		final Object background = scope.getArg("background", IType.NONE);
		IAgent backgroundAgent = null;
		if ( background == null ) {
			isInBackgroundAgent = false;
		} else {
			isInBackgroundAgent = true;
			backgroundAgent = Cast.asAgent(scope, background);
		}

		final Double s = scope.hasArg("speed") ? Cast.asFloat(scope, scope.getArg("speed", IType.FLOAT)) : null;
		if ( s != null ) {
			setSpeed(agent, s);
		}
		Double agentSize =
			scope.hasArg("agent_size") ? Cast.asFloat(scope, scope.getArg("agent_size", IType.FLOAT)) : null;
		if ( agentSize == null ) {
			agentSize = new Double(0);
		}

		final double maxDist = computeDistance(scope, agent);
		final double detectingRange = maxDist;
		final IAgent obj = (IAgent) agent.getAttribute("target");
		final ILocation target = obj.getLocation();// computeTarget(scope, agent);
		if ( target == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final GamaPoint targetPoint = (GamaPoint) target;
		final GamaPoint startingPoint = (GamaPoint) agent.getLocation();
		final GamaList<IAgent> neighbours =
			(GamaList<IAgent>) scope.getTopology().getNeighboursOf(scope, agent, detectingRange, Different.with());
		neighbours.remove(backgroundAgent);
		final GamaPoint candidatePoint[] = new GamaPoint[9];
		final double distanceToTarget =
			Math.sqrt((targetPoint.y - startingPoint.y) * (targetPoint.y - startingPoint.y) +
				(targetPoint.x - startingPoint.x) * (targetPoint.x - startingPoint.x));// FastMath.sqrt(FastMath.pow((targetPoint.y
																						// -
																						// startingPoint.y),
																						// 2) +
																						// FastMath.pow((targetPoint.x
																						// -
																						// startingPoint.x),
																						// 2));

		final double tmpx = startingPoint.x + maxDist * (targetPoint.x - startingPoint.x) / distanceToTarget;
		final double tmpy = startingPoint.y + maxDist * (targetPoint.y - startingPoint.y) / distanceToTarget;
		candidatePoint[0] = new GamaPoint(tmpx, tmpy);

		final double sqrt2 = FastMath.sqrt(2);

		candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);
		candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist, startingPoint.y);
		candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y);
		candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y + maxDist);
		candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y - maxDist);
		candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist / sqrt2, startingPoint.y - maxDist / sqrt2);
		candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist / sqrt2, startingPoint.y + maxDist / sqrt2);

		// modification of candidates point

		// ////
		PreparedPolygon backgdGeom = null;
		if ( isInBackgroundAgent ) {
			backgdGeom = new PreparedPolygon((Polygonal) backgroundAgent.getGeometry().getInnerGeometry());
		}
		GamaPoint newLocation = null;
		// System.out.println("**************************************\nlocation : " + startingPoint
		// + " target : " + target);
		// ******
		ArrayList<GamaPoint> passedList = (ArrayList<GamaPoint>) agent.getAttribute("passedList");// agent.getAttribute("passedList"));

		// ****
		boolean freeSpace = false;
		int i = -1;
		while (i < 8) {
			i++;
			final GamaPoint point = candidatePoint[i];
			final Geometry geomCand = GeometryUtils.FACTORY.createPoint(point).buffer(agentSize);
			// System.out.println("point : " + point+" candidat numero "+i);

			if ( backgdGeom != null && !backgdGeom.contains(geomCand) ) {
				// System.out.println("le point n'est pas dans le background");
				continue;
			}
			// System.out.println("le point est dans le background !");

			final PreparedPolygon geomCandOpt = new PreparedPolygon((Polygonal) geomCand);
			freeSpace = true;
			for ( final IAgent ag : neighbours ) {
				if ( !geomCandOpt.disjoint(ag.getInnerGeometry()) ) {
					freeSpace = false;
					break;
				}
			}
			// ************************
			// *************************
			if ( freeSpace ) {
				// System.out.println("le place est libre !");
				if ( passedList == null ) {
					passedList = new ArrayList<GamaPoint>();
				}
				if ( alreadyVisited(scope, passedList) ) {
					// System.out.println("on a une boucle "+passedList);
					agent.setAttribute("changeDirection", true);

					// System.out.println("changement de l'attribut en true puis break");
					/*
					 * i = -1;
					 * //********************
					 * obj = (IAgent)agent.getAttribute("target");
					 * target =obj.getLocation();//computeTarget(scope, agent);
					 * if ( target == null ) {
					 * //scope.setStatus(ExecutionStatus.failure);
					 * return null;
					 * }
					 * targetPoint = (GamaPoint) target;
					 * startingPoint = (GamaPoint) agent.getLocation();
					 * neighbours = (GamaList<IAgent>) agent.getTopology().getNeighboursOf(agent,
					 * detectingRange, Different.with());
					 * neighbours.remove(backgroundAgent);
					 * candidatePoint = new GamaPoint[9];
					 * distanceToTarget = FastMath.sqrt((targetPoint.y - startingPoint.y) *
					 * (targetPoint.y - startingPoint.y) +
					 * (targetPoint.x - startingPoint.x) * (targetPoint.x -
					 * startingPoint.x));//Math.sqrt(FastMath.pow((targetPoint.y - startingPoint.y), 2)
					 * + FastMath.pow((targetPoint.x - startingPoint.x), 2));
					 *
					 * tmpx = startingPoint.x + maxDist*(targetPoint.x -
					 * startingPoint.x)/distanceToTarget;
					 * tmpy = startingPoint.y + maxDist*(targetPoint.y -
					 * startingPoint.y)/distanceToTarget;
					 * candidatePoint[0] = new GamaPoint(tmpx,tmpy);
					 *
					 * sqrt2 = FastMath.sqrt(2);
					 *
					 * candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist/sqrt2,
					 * startingPoint.y + maxDist/sqrt2);
					 * candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist/sqrt2,
					 * startingPoint.y - maxDist/sqrt2);
					 * candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist,
					 * startingPoint.y);
					 * candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y
					 * );
					 * candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y +
					 * maxDist);
					 * candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y -
					 * maxDist);
					 * candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist/sqrt2,
					 * startingPoint.y - maxDist/sqrt2);
					 * candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist/sqrt2,
					 * startingPoint.y + maxDist/sqrt2);
					 */// ************************
					/*
					 * GamaPoint pp0 = passedList.get(0);
					 * GamaPoint pp2 = passedList.get(3);
					 * double ds = FastMath.sqrt((pp0.y - pp2.y) * (pp0.y - pp2.y) + (pp0.x - pp2.x) *
					 * (pp0.x - pp2.x));
					 * tmpx = startingPoint.x + maxDist*(targetPoint.x -
					 * startingPoint.x)/distanceToTarget;
					 * tmpy = startingPoint.y + maxDist*(targetPoint.y -
					 * startingPoint.y)/distanceToTarget;
					 * candidatePoint[0] = new GamaPoint(tmpx,tmpy);
					 * // loop
					 * candidatePoint[1] = new GamaPoint(startingPoint.x + maxDist/sqrt2,
					 * startingPoint.y + maxDist/sqrt2);
					 * candidatePoint[8] = new GamaPoint(startingPoint.x - maxDist/sqrt2,
					 * startingPoint.y - maxDist/sqrt2);
					 * candidatePoint[2] = new GamaPoint(startingPoint.x + maxDist,
					 * startingPoint.y);
					 * candidatePoint[7] = new GamaPoint(startingPoint.x - maxDist, startingPoint.y
					 * );
					 * candidatePoint[4] = new GamaPoint(startingPoint.x, startingPoint.y +
					 * maxDist);
					 * candidatePoint[6] = new GamaPoint(startingPoint.x, startingPoint.y -
					 * maxDist);
					 * candidatePoint[3] = new GamaPoint(startingPoint.x + maxDist/sqrt2,
					 * startingPoint.y - maxDist/sqrt2);
					 * candidatePoint[5] = new GamaPoint(startingPoint.x - maxDist/sqrt2,
					 * startingPoint.y + maxDist/sqrt2);
					 *
					 * ///////
					 * i =0;
					 */
					passedList = new ArrayList<GamaPoint>();
					agent.setAttribute("passedList", passedList);
					break;
				} else {
					if ( passedList.size() == 4 ) {
						passedList = new ArrayList<GamaPoint>();
					}
					final GamaPoint gm = new GamaPoint(i, 0);//
					passedList.add(gm);
					// System.out.println("stockage dans la passedlist "+passedList+" longueur "+passedList.length());
					agent.setAttribute("passedList", passedList);

				}
				newLocation = point;
				break;
			}

		}

		if ( newLocation == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return (GamaPoint) agent.getLocation();
		}
		agent.setLocation(newLocation);
		// scope.setStatus(ExecutionStatus.success);
		return (GamaPoint) agent.getLocation();
	}

	private boolean alreadyVisited(final IScope scope, final ArrayList<GamaPoint> list) {
		if ( list.size() == 4 ) {
			;
			if ( list.get(0).getX() == list.get(2).getX() && list.get(1).getX() == list.get(3).getX() &&
				list.get(0).getX() != list.get(1).getX() ) { return true; }
		}
		return false;
	}

	private boolean isInLoop(final IScope scope, final ArrayList<GamaPoint> list) {
		if ( list.size() == 4 ) {
			;
			if ( list.get(0).getX() == list.get(2).getX() && list.get(1).getX() == list.get(3).getX() ) { return true; }
		}
		return false;
	}

	/**
	 * LvMinh 3
	 * @param O center of the circle
	 * @param X a point outside the circle
	 * @param r radius of the circle
	 * @return a point that is both on the circle and the line OX
	 */
	// private GamaPoint getNextDestinationInDirection(final GamaPoint O, final GamaPoint X, final double r,
	// final double epsilon) {
	// final GamaPoint D = new GamaPoint(O.x, O.y);
	// final GamaPoint inside = new GamaPoint(O.x, O.y);
	// final GamaPoint outside = new GamaPoint(X.x, X.y);
	// boolean isContinue = false;
	// do {
	// D.x = (inside.x + outside.x) / 2;
	// D.y = (inside.y + outside.y) / 2;
	// final double dd = (D.x - O.x) * (D.x - O.x) + (D.y - O.y) * (D.y - O.y);
	// if ( dd < r * r ) {
	// inside.x = D.x;
	// inside.y = D.y;
	// } else {
	// outside.x = D.x;
	// outside.y = D.y;
	// }
	// isContinue = FastMath.abs(dd - r * r) > epsilon * epsilon;
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
		final double AB2 = (A.x - B.x) * (A.x - B.x) + (A.y - B.y) * (A.y - B.y);
		return AB2;
	}

	/**
	 * LvMinh 5 return
	 * @param agentList list of agents
	 * @param point a geometric point
	 * @return true if point is exterior of all agents in the list, and otherwise
	 */
	private boolean isExteriorOfAgents(final GamaList<IAgent> agentList, final Geometry point) {
		for ( final IAgent agent : agentList ) {
			if ( agent.getInnerGeometry().intersects(point) ) { return false; }
		}
		return true;
	}

	/**
	 *
	 */

}