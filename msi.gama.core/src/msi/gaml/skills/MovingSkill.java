/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.skills;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.AssertionFailedException;

/**
 * MovingSkill : This class is intended to define the minimal set of behaviours required from an
 * agent that is able to move. Each member that has a meaning in GAML is annotated with the
 * respective tags (vars, getter, setter, init, action & args)
 * 
 * @author drogoul 4 juil. 07
 */

@doc("The moving skill is intended to define the minimal set of behaviours required from an "
	+ "agent that is able to move")
@vars({
	@var(name = IKeyword.SPEED, type = IType.FLOAT_STR, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")),
	@var(name = IKeyword.HEADING, type = IType.INT_STR, init = "rnd 359", doc = @doc("the absolute heading of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.DESTINATION, type = IType.POINT_STR, depends_on = { IKeyword.SPEED,
		IKeyword.HEADING, IKeyword.LOCATION }, doc = @doc("continuously updated destination of the agent with respect to its speed and heading (read-only)")) })
@skill(name = IKeyword.MOVING_SKILL)
public class MovingSkill extends GeometricSkill {

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException Gets the destination of the agent. The destination is the next
	 *             absolute coordinates the agent could reach if it keeps the current speed and the
	 *             current heading
	 */
	@getter(IKeyword.DESTINATION)
	public ILocation getDestination(final IAgent agent) {
		final ILocation actualLocation = agent.getLocation();
		// if ( actualLocation == null ) { return null; }
		final double dist = getSpeed(agent) /* agent.getSimulation().getScheduler().getStep() */;
		final ITopology topology = getTopology(agent);
		return topology.getDestination(actualLocation, agent.getHeading(), dist, false);
	}

	@setter(IKeyword.DESTINATION)
	public void setDestination(final IAgent agent, final ILocation p) {
		// READ_ONLY
	}

	@getter(IKeyword.SPEED)
	public double getSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.SPEED);
	}

	@setter(IKeyword.SPEED)
	public void setSpeed(final IAgent agent, final double s) {
		agent.setAttribute(IKeyword.SPEED, s);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(IKeyword.HEADING)
	public int getHeading(final IAgent agent) {
		return agent.getHeading();
	}

	@setter(IKeyword.HEADING)
	public void setHeading(final IAgent agent, final int heading) {
		agent.setHeading(heading);
	}

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException Prim: move randomly. Has to be redefined for every class that
	 *             implements this interface.
	 * 
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. A bounds (geometry, agent, list of agents, list of geometries, species)
	 *            can be specified
	 * @return the path followed
	 */

	protected int computeHeadingFromAmplitude(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		int ampl = scope.hasArg("amplitude") ? scope.getIntArg("amplitude") : 359;
		agent.setHeading(agent.getHeading() + GAMA.getRandom().between(-ampl / 2, ampl / 2));
		return agent.getHeading();
	}

	protected int computeHeading(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		Integer heading = scope.hasArg(IKeyword.HEADING) ? scope.getIntArg(IKeyword.HEADING) : null;
		if ( heading != null ) {
			agent.setHeading(heading);
		}
		return agent.getHeading();
	}

	protected double computeDistance(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		// We do not change the speed of the agent anymore. Only the current primitive is affected
		Double s =
			scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		// 20/1/2012 Change : The speed of the agent is multiplied by the timestep in order to
		// obtain the maximal distance it can cover in one step.
		return s * SimulationClock.getStep()/* getTimeStep(scope) */;
	}

	protected ILocation computeTarget(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		final Object target = scope.getArg("target", IType.NONE);
		ILocation result = null;
		if ( target != null && target instanceof ILocated ) {
			result = ((ILocated) target).getLocation();
		}
		if ( result == null ) {
			scope.setStatus(ExecutionStatus.failure);
		}
		return result;
	}

	protected ITopology computeTopology(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		final Object on = scope.getArg("on", IType.NONE);
		ITopology topo = Cast.asTopology(scope, on);
		if ( topo == null ) { return agent.getTopology(); }
		return topo;
	}

	@action(name = "wander", args = {
		@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "amplitude", type = IType.INT_STR, optional = true, doc = @doc("a restriction placed on the random heading choice. The new heading is chosen in the range (heading - amplitude/2, heading+amplitude/2)")),
		@arg(name = "bounds", type = { IType.AGENT_STR, IType.GEOM_STR }, optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) }, doc = @doc(returns = "the path followed by the agent", examples = { "do action: wander{ \n arg speed value: speed - 10; \n arg amplitude value: 120;\n arg bounds value: agentA;}" }, value = "Moves the agent towards a random location at the maximum distance (with respect to its speed). The heading of the agent is chosen randomly if no amplitude is specified. This action changes the value of heading."))
	@args(names = { IKeyword.SPEED, "amplitude", "bounds" })
	public IPath primMoveRandomly(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		IPath pathFollowed = null;
		ILocation location = agent.getLocation();
		int heading = computeHeadingFromAmplitude(scope, agent);
		double dist = computeDistance(scope, agent);

		ILocation loc = getTopology(agent).getDestination(location, heading, dist, true);

		if ( loc == null ) {
			agent.setHeading(heading - 180);
			// pathFollowed = null;
		} else {
			Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			if ( bounds != null ) {
				IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
				if ( geom != null && geom.getInnerGeometry() != null ) {
					loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
				}
			}
			// pathFollowed = new GamaPath(this.getTopology(agent), GamaList.with(location, loc));
			agent.setLocation(loc);
		}
		scope.setStatus(loc == null ? ExecutionStatus.failure : ExecutionStatus.success);
		return pathFollowed;
	}

	@action(name = "wander_3D", args = {
			@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "amplitude", type = IType.INT_STR, optional = true, doc = @doc("a restriction placed on the random heading choice. The new heading is chosen in the range (heading - amplitude/2, heading+amplitude/2)")),
			@arg(name = "bounds", type = { IType.AGENT_STR, IType.GEOM_STR }, optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) }, doc = @doc(returns = "the path followed by the agent", examples = { "do action: wander{ \n arg speed value: speed - 10; \n arg amplitude value: 120;\n arg bounds value: agentA;}" }, value = "Moves the agent towards a random location at the maximum distance (with respect to its speed). The heading of the agent is chosen randomly if no amplitude is specified. This action changes the value of heading."))
		@args(names = { IKeyword.SPEED, "amplitude", "bounds" })
		public IPath primMoveRandomly3D(final IScope scope) throws GamaRuntimeException {
			IAgent agent = getCurrentAgent(scope);
			IPath pathFollowed = null;
			ILocation location = agent.getLocation();
			int heading = computeHeadingFromAmplitude(scope, agent);
			double dist = computeDistance(scope, agent);

			ILocation loc = getTopology(agent).getDestination(location, heading, dist, true);
			

			if ( loc == null ) {
				agent.setHeading(heading - 180);
				// pathFollowed = null;
			} else {
				Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
				if ( bounds != null ) {
					IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
					if ( geom != null && geom.getInnerGeometry() != null ) {
						loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
					}
				}
				((GamaPoint) loc).z = Math.max(0, ((GamaPoint) location).z + dist * (2 * RandomUtils.getDefault().next() - 1));
				
				// pathFollowed = new GamaPath(this.getTopology(agent), GamaList.with(location, loc));
				agent.setLocation(loc);
			}
			scope.setStatus(loc == null ? ExecutionStatus.failure : ExecutionStatus.success);
			return pathFollowed;
		}

	/**
	 * @throws GamaRuntimeException Prim: move . Move in the direction specified by the heading
	 *             parameter (if none is specified, keep the same heading), by the distance
	 *             specified in the args (if none is provided, computes a distance with the current
	 *             speed). A bounds (geometry, agent, list of agents, list of geometries, species)
	 *             can be specified
	 * 
	 * @param args the args
	 * @return the past followed
	 */

	@action(name = "move", args = {
		@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = IKeyword.HEADING, type = IType.INT_STR, optional = true, doc = @doc("a restriction placed on the random heading choice. The new heading is chosen in the range (heading - amplitude/2, heading+amplitude/2)")),
		@arg(name = "bounds", type = "localized entity or geometry", optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) }, doc = @doc(returns = "the path followed by the agent", examples = { "do action: move { \n arg speed value: speed - 10; \n arg heading value: heading + rnd (30); \n arg bounds value: agentA;}" }, value = "moves the agent forward, the distance being computed with respect to its speed and heading. The value of the corresponding variables are used unless arguments are passed."))
	@args(names = { IKeyword.SPEED, IKeyword.HEADING, "bounds" })
	public IPath primMoveForward(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		IPath pathFollowed = null;
		ILocation location = agent.getLocation();
		double dist = computeDistance(scope, agent);
		int heading = computeHeading(scope, agent);

		ILocation loc = getTopology(agent).getDestination(location, heading, dist, true);
		if ( loc == null ) {
			agent.setHeading(heading - 180);
			// pathFollowed = null;
		} else {
			Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			if ( bounds != null ) {
				IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
				if ( geom != null && geom.getInnerGeometry() != null ) {
					loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
				}
			}
			// pathFollowed = new GamaPath(this.getTopology(agent), GamaList.with(location, loc));
			agent.setLocation(loc);
		}
		scope.setStatus(loc == null ? ExecutionStatus.failure : ExecutionStatus.success);
		return pathFollowed;
	}

	@action(name = "follow", args = {
		@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "path", type = IType.PATH_STR, optional = true, doc = @doc("a path to be followed.")) }, doc = @doc(value = "moves the agent along a given path passed in the arguments.", returns = "the path followed by the agent.", examples = { "do action: follow {\n arg speed value: speed * 2;\n arg path value: road_path;\n}" }))
	@args(names = { IKeyword.SPEED, "path" })
	public IPath primFollow(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		// IPath pathFollowed = null;
		GamaList<IShape> edges = new GamaList();
		ILocation location = agent.getLocation();
		double dist = computeDistance(scope, agent);

		GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		if ( path != null && !path.getEdgeList().isEmpty() ) {
			/* pathFollowed = */moveToNextLocAlongPathSimplified(agent, path, dist);
			scope.setStatus(/*
							 * pathFollowed == null ? ExecutionStatus.failure
							 * :
							 */ExecutionStatus.success);
			return path /* Followed */;
		}
		edges.add(new GamaShape(location));
		// pathFollowed = new GamaPath(getTopology(agent), location, location, edges);
		scope.setStatus(ExecutionStatus.failure);
		return null;
	}

	@action(name = "goto", args = {
		@arg(name = "target", type = "point or agent", optional = false, doc = @doc("the location or entity towards which to move.")),
		@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "on", type = { IType.LIST_STR, IType.AGENT_STR, IType.GRAPH_STR, IType.GEOM_STR }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")) }, doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}" }))
	@args(names = { "target", IKeyword.SPEED, "on", "return_path" })
	public IPath primGoto(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy();
		final double maxDist = computeDistance(scope, agent);
		final ILocation goal = computeTarget(scope, agent);
		if ( goal == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology().equals(topo) ||
			!path.getEndVertex().equals(goal) || !path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(source, goal);
		}

		if ( path == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		if ( returnPath != null && returnPath ) {
			IPath pathFollowed = moveToNextLocAlongPath(agent, path, maxDist);
			if ( pathFollowed == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			scope.setStatus(ExecutionStatus.success);
			return pathFollowed;
		}
		moveToNextLocAlongPathSimplified(agent, path, maxDist);
		scope.setStatus(ExecutionStatus.success);
		return null;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Return the next location toward a target on a line
	 * 
	 * @param coords coordinates of the line
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */

	protected GamaList initMoveAlongPath(final IAgent agent, final IPath path,
		GamaPoint currentLocation) {
		GamaList initVals = new GamaList();
		Integer index = 0;
		Integer indexSegment = 1;
		Integer endIndexSegment = 0;

		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		if ( path.isVisitor(agent) ) {
			index = path.indexOf(agent);
			indexSegment = path.indexSegmentOf(agent);
		} else {
			path.acceptVisitor(agent);
			double distanceS = Double.MAX_VALUE;
			IShape line = null;
			for ( int i = 0; i < nb; i++ ) {
				line = edges.get(i);
				double distS = line.euclidianDistanceTo(currentLocation);
				if ( distS < distanceS ) {
					distanceS = distS;
					index = i;
				}
			}
			line = edges.get(index);

			currentLocation = (GamaPoint) Punctal.opClosestPointTo(currentLocation, line);
			Point pointGeom = (Point) currentLocation.getInnerGeometry();
			if ( line.getInnerGeometry().getNumPoints() >= 3 ) {
				distanceS = Double.MAX_VALUE;
				Coordinate coords[] = line.getInnerGeometry().getCoordinates();
				int nbSp = coords.length;
				Coordinate[] temp = new Coordinate[2];
				for ( int i = 0; i < nbSp - 1; i++ ) {
					temp[0] = coords[i];
					temp[1] = coords[i + 1];
					LineString segment = GeometryUtils.getFactory().createLineString(temp);
					double distS = segment.distance(pointGeom);
					if ( distS < distanceS ) {
						distanceS = distS;
						indexSegment = i + 1;
					}
				}
			}
		}
		IShape lineEnd = edges.get(nb - 1);
		GamaPoint falseTarget = (GamaPoint) Punctal.opClosestPointTo(path.getEndVertex(), lineEnd);
		endIndexSegment = 1;
		Point pointGeom = (Point) falseTarget.getInnerGeometry();
		if ( lineEnd.getInnerGeometry().getNumPoints() >= 3 ) {
			double distanceT = Double.MAX_VALUE;
			Coordinate coords[] = lineEnd.getInnerGeometry().getCoordinates();
			int nbSp = coords.length;
			Coordinate[] temp = new Coordinate[2];
			for ( int i = 0; i < nbSp - 1; i++ ) {
				temp[0] = coords[i];
				temp[1] = coords[i + 1];
				LineString segment = GeometryUtils.getFactory().createLineString(temp);
				double distT = segment.distance(pointGeom);
				if ( distT < distanceT ) {
					distanceT = distT;
					endIndexSegment = i + 1;
				}
			}
		}
		initVals.add(index);
		initVals.add(indexSegment);
		initVals.add(endIndexSegment);
		initVals.add(currentLocation);
		initVals.add(falseTarget);
		return initVals;
	}

	private IPath moveToNextLocAlongPathSimplified(final IAgent agent, final IPath path,
		final double d) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		GamaList indexVals = initMoveAlongPath(agent, path, currentLocation);
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		double distance = d;
		GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			double weight = computeWeigth(graph, path, line);

			for ( int j = indexSegment; j < coords.length; j++ ) {
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = agent.getTopology().distanceBetween(pt, currentLocation);

				dist = weight * dist;
				if ( distance < dist ) {
					double ratio = distance / dist;
					double newX = currentLocation.x + ratio * (pt.x - currentLocation.x);
					double newY = currentLocation.y + ratio * (pt.y - currentLocation.y);
					currentLocation.setLocation(newX, newY);
					distance = 0;
					break;
				} else if ( distance > dist ) {
					currentLocation = pt;
					distance = distance - dist;
					if ( i == nb - 1 && j == endIndexSegment ) {
						break;
					}
					indexSegment++;
				} else {
					currentLocation = pt;
					distance = 0;
					if ( indexSegment < coords.length - 1 ) {
						indexSegment++;
					} else {
						index++;
					}
					break;
				}
			}
			if ( distance == 0 ) {
				break;
			}
			indexSegment = 1;
			index++;
		}
		if ( currentLocation.equals(falseTarget) ) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		agent.setLocation(currentLocation);
		path.setSource(currentLocation.copy());

		return null;
	}

	protected double computeWeigth(final IGraph graph, final IPath path, final IShape line) {
		return graph == null ? 1 : graph.getEdgeWeight(path.getRealObject(line)) /
			line.getGeometry().getPerimeter();
	}

	private IPath moveToNextLocAlongPath(final IAgent agent, final IPath path, final double d) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		GamaList indexVals = initMoveAlongPath(agent, path, currentLocation);
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		double distance = d;
		GamaList<IShape> segments = new GamaList();
		GamaPoint startLocation = (GamaPoint) agent.getLocation().copy();
		GamaMap agents = new GamaMap();
		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			// The weight computed here is absolutely useless.. since getWeight() returns the
			// perimeter. // ANSWER : it is necessary because the weight can be different than the
			// perimeter (see model traffic_tutorial)
			GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

			double weight = computeWeigth(graph, path, line);
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for ( int j = indexSegment; j < coords.length; j++ ) {
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = agent.getTopology().distanceBetween(pt, currentLocation);
				dist = weight * dist;
				if ( distance < dist ) {
					GamaPoint pto = currentLocation.copy();
					double ratio = distance / dist;
					double newX = pto.x + ratio * (pt.x - pto.x);
					double newY = pto.y + ratio * (pt.y - pto.y);
					currentLocation.setLocation(newX, newY);
					IShape gl = GamaGeometryType.buildLine(pto, currentLocation);
					IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);
					distance = 0;
					break;
				} else if ( distance > dist ) {
					IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);
					currentLocation = pt;
					distance = distance - dist;
					if ( i == nb - 1 && j == endIndexSegment ) {
						break;
					}
					indexSegment++;
				} else {
					IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);
					currentLocation = pt;
					distance = 0;
					if ( indexSegment < coords.length - 1 ) {
						indexSegment++;
					} else {
						index++;
					}
					break;
				}
			}
			if ( distance == 0 ) {
				break;
			}
			indexSegment = 1;
			index++;
		}
		if ( currentLocation.equals(falseTarget) ) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		path.setSource(currentLocation.copy());
		if ( segments.isEmpty() ) { return null; }
		IPath followedPath =
			new GamaPath(agent.getTopology(), startLocation, currentLocation, segments);
		followedPath.setRealObjects(agents);
		agent.setLocation(currentLocation);
		return followedPath;
	}

	private ILocation computeLocationForward(final IScope scope, final double dist,
		final ILocation loc, final Geometry geom) {

		Point locPt =
			GeometryUtils.getFactory().createPoint(
				getCurrentAgent(scope).getLocation().toCoordinate());
		Geometry buff = locPt.buffer(dist);
		Geometry test = locPt.buffer(dist / 100, 4);
		Geometry frontier = null;
		try {
			frontier = buff.intersection(geom);
		} catch (AssertionFailedException e) {
			frontier = buff.intersection(geom.buffer(0.0));
		}

		Geometry geomsSimp = null;
		if ( frontier instanceof GeometryCollection ) {
			GeometryCollection gc = (GeometryCollection) frontier;
			int nb = gc.getNumGeometries();
			for ( int i = 0; i < nb; i++ ) {
				if ( !gc.getGeometryN(i).disjoint(test) ) {
					geomsSimp = gc.getGeometryN(i);
					break;
				}
			}
			if ( geomsSimp == null || geomsSimp.isEmpty() ) { return getCurrentAgent(scope)
				.getLocation(); }
			frontier = geomsSimp;
		}
		ILocation computedPt = Punctal.opClosestPointTo(loc, new GamaShape(frontier));
		if ( computedPt != null ) { return computedPt; }
		return getCurrentAgent(scope).getLocation();
	}
}
