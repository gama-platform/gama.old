package simtools.gaml.extensions.traffic;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.*;
import msi.gaml.operators.Spatial.Operators;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

import com.vividsolutions.jts.geom.*;

@vars({
	@var(name = "living_space", type = IType.FLOAT, init = "1.0", doc = @doc("the min distance between the agent and an obstacle (in meter)")),
	@var(name = "lanes_attribute", type = IType.STRING, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes")),
	@var(name = "tolerance", type = IType.FLOAT, init = "0.1", doc = @doc("the tolerance distance used for the computation (in meter)")),
	@var(name = "obstacle_species", type = IType.LIST, init = "[]", doc = @doc("the list of species that are considered as obstacles")),
	@var(name = IKeyword.SPEED, type = IType.FLOAT, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")),
	@var(name = "security_distance", type = IType.FLOAT, init = "1.0", doc = @doc("the min distance between two drivers")),
	@var(name = "real_speed", type = IType.FLOAT, init = "0.0", doc = @doc("real speed of the agent (in meter/second)")),
	@var(name = "current_lane", type = IType.INT, init = "0", doc = @doc("the current lane on which the agent is")),
	@var(name = "vehicle_length", type = IType.FLOAT, init = "0.0", doc = @doc("the length of the agent geometry")),
	@var(name = "current_road", type = IType.AGENT, doc = @doc("current road on which the agent is"))})
@skill(name = "driving")
public class DrivingSkill extends MovingSkill {

	public final static String LIVING_SPACE = "living_space";
	public final static String TOLERANCE = "tolerance";
	public final static String LANES_ATTRIBUTE = "lanes_attribute";
	public final static String OBSTACLE_SPECIES = "obstacle_species";
	public final static String SECURITY_DISTANCE = "security_distance";
	public final static String REAL_SPEED = "real_speed";
	public final static String CURRENT_ROAD = "current_road";
	public final static String CURRENT_LANE = "current_lane";
	public final static String DISTANCE_TO_GOAL = "distance_to_goal";
	public final static String VEHICLE_LENGTH = "vehicle_length";


	@getter(LIVING_SPACE)
	public double getLivingSpace(final IAgent agent) {
		return (Double) agent.getAttribute(LIVING_SPACE);
	}

	@setter(LANES_ATTRIBUTE)
	public void setLanesAttribute(final IAgent agent, final String latt) {
		agent.setAttribute(LANES_ATTRIBUTE, latt);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(LANES_ATTRIBUTE)
	public String getLanesAttribute(final IAgent agent) {
		return (String) agent.getAttribute(LANES_ATTRIBUTE);
	}

	@setter(LIVING_SPACE)
	public void setLivingSpace(final IAgent agent, final double ls) {
		agent.setAttribute(LIVING_SPACE, ls);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(TOLERANCE)
	public double getTolerance(final IAgent agent) {
		return (Double) agent.getAttribute(TOLERANCE);
	}

	@setter(TOLERANCE)
	public void setTolerance(final IAgent agent, final double t) {
		agent.setAttribute(TOLERANCE, t);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(OBSTACLE_SPECIES)
	public GamaList<ISpecies> getObstacleSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(OBSTACLE_SPECIES);
	}

	@setter(OBSTACLE_SPECIES)
	public void setObstacleSpecies(final IAgent agent, final GamaList<ISpecies> os) {
		agent.setAttribute(OBSTACLE_SPECIES, os);
	}
	

	@getter(SECURITY_DISTANCE)
	public double getSecurityDistance(final IAgent agent) {
		return (Double) agent.getAttribute(SECURITY_DISTANCE);
	}

	@setter(SECURITY_DISTANCE)
	public void setSecurityDistance(final IAgent agent, final double ls) {
		agent.setAttribute(SECURITY_DISTANCE, ls);
	}

	@getter(CURRENT_ROAD)
	public IAgent getCurrentRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(CURRENT_ROAD);
	}
	
	@getter(REAL_SPEED)
	public double getRealSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(REAL_SPEED);
	}
	
	@getter(VEHICLE_LENGTH)
	public double getVehiculeLength(final IAgent agent) {
		return (Double) agent.getAttribute(VEHICLE_LENGTH);
	}
	
	@getter(CURRENT_LANE)
	public int getCurrentLane(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_LANE);
	}
	
	@getter(DISTANCE_TO_GOAL)
	public double getDistanceToGoal(final IAgent agent) {
		return (Double) agent.getAttribute(DISTANCE_TO_GOAL);
	}
	
	@setter(DISTANCE_TO_GOAL)
	public void setDistanceToGoal(final IAgent agent, final double dg) {
		agent.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	protected String computeLanesNumber(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		return scope.hasArg(LANES_ATTRIBUTE) ? scope.getStringArg(LANES_ATTRIBUTE) : getLanesAttribute(agent);
	}

	protected GamaList<ISpecies> computeObstacleSpecies(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return (GamaList<ISpecies>) (scope.hasArg(OBSTACLE_SPECIES) ? scope.getListArg(OBSTACLE_SPECIES)
			: getObstacleSpecies(agent));
	}

	protected double computeTolerance(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		return scope.hasArg(TOLERANCE) ? scope.getFloatArg(TOLERANCE) : getTolerance(agent);
	}

	protected double computeLivingSpace(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		return scope.hasArg(LIVING_SPACE) ? scope.getFloatArg(LIVING_SPACE) : getLivingSpace(agent);
	}

	@Override
	@action(name = "follow_driving", args = {
		@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "path", type = IType.PATH, optional = true, doc = @doc("a path to be followed.")),
		@arg(name = "return_path", type = IType.BOOL, optional = true, doc = @doc("if true, return the path followed (by default: false)")),
		@arg(name = "move_weights", type = IType.MAP, optional = true, doc = @doc("Weigths used for the moving.")),
		@arg(name = LIVING_SPACE, type = IType.FLOAT, optional = true, doc = @doc("min distance between the agent and an obstacle (replaces the current value of living_space)")),
		@arg(name = TOLERANCE, type = IType.FLOAT, optional = true, doc = @doc("tolerance distance used for the computation (replaces the current value of tolerance)")),
		@arg(name = LANES_ATTRIBUTE, type = IType.STRING, optional = true, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)")) }, doc = @doc(value = "moves the agent along a given path passed in the arguments while considering the other agents in the network.", returns = "optional: the path followed by the agent.", examples = { "do follow speed: speed * 2 path: road_path;" }))
	public IPath primFollow(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final double maxDist = computeDistance(scope, agent);
		final double tolerance = computeTolerance(scope, agent);
		final double livingSpace = computeLivingSpace(scope, agent);
		final Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		final GamaMap weigths = (GamaMap) computeMoveWeights(scope);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		String laneAttributes = computeLanesNumber(scope, agent);
		if ( laneAttributes == null || "".equals(laneAttributes) ) {
			laneAttributes = "lanes_number";
		}

		final ILocation goal = computeTarget(scope, agent);
		if ( goal == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		if ( path != null && !path.getEdgeList().isEmpty() ) {
			if ( returnPath != null && returnPath ) {
				final IPath pathFollowed =
					moveToNextLocAlongPathTraffic(scope, agent, path, maxDist, weigths, livingSpace, tolerance,
						laneAttributes, obsSpecies);
				if ( pathFollowed == null ) {
					// scope.setStatus(ExecutionStatus.failure);
					return null;
				}
				// scope.setStatus(ExecutionStatus.success);
				return pathFollowed;
			}
			moveToNextLocAlongPathSimplifiedTraffic(scope, agent, path, maxDist, weigths, livingSpace, tolerance,
				laneAttributes, obsSpecies);
			// scope.setStatus(ExecutionStatus.success);
			return null;
		}
		// scope.setStatus(ExecutionStatus.failure);
		return null;
	}

	@action(name = "goto_driving", args = {
		@arg(name = "target", type = { IType.POINT, IType.GEOMETRY, IType.AGENT }, optional = false, doc = @doc("the location or entity towards which to move.")),
		@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "on", type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")),
		@arg(name = "return_path", type = IType.BOOL, optional = true, doc = @doc("if true, return the path followed (by default: false)")),
		@arg(name = "move_weights", type = IType.MAP, optional = true, doc = @doc("Weigths used for the moving.")),
		@arg(name = LIVING_SPACE, type = IType.FLOAT, optional = true, doc = @doc("min distance between the agent and an obstacle (replaces the current value of living_space)")),
		@arg(name = TOLERANCE, type = IType.FLOAT, optional = true, doc = @doc("tolerance distance used for the computation (replaces the current value of tolerance)")),
		@arg(name = LANES_ATTRIBUTE, type = IType.STRING, optional = true, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)")) }, doc = @doc(value = "moves the agent towards the target passed in the arguments while considering the other agents in the network (only for graph topology)", returns = "optional: the path followed by the agent.", examples = { "do gotoTraffic target: one_of (list (species (self))) speed: speed * 2 on: road_network living_space: 2.0;" }))
	public IPath primGotoTraffic(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation source = agent.getLocation().copy(scope);
		final double maxDist = computeDistance(scope, agent);
		final double tolerance = computeTolerance(scope, agent);
		final double livingSpace = computeLivingSpace(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		String laneAttributes = computeLanesNumber(scope, agent);
		if ( laneAttributes == null || "".equals(laneAttributes) ) {
			laneAttributes = "lanes_number";
		}

		final ILocation goal = computeTarget(scope, agent);
		if ( goal == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology().equals(topo) || !path.getEndVertex().equals(goal) ||
			!path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(scope, source, goal);
		} else {

			if ( topo instanceof GraphTopology ) {
				if ( ((GraphTopology) topo).getPlaces() != path.getGraph() ||
					((GraphTopology) topo).getPlaces().getVersion() != path.getGraphVersion() ) {
					path = topo.pathBetween(scope, source, goal);
				}
			}
		}

		if ( path == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		final GamaMap weigths = (GamaMap) computeMoveWeights(scope);
		if ( returnPath != null && returnPath ) {
			final IPath pathFollowed =
				moveToNextLocAlongPathTraffic(scope, agent, path, maxDist, weigths, livingSpace, tolerance,
					laneAttributes, obsSpecies);
			if ( pathFollowed == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			// scope.setStatus(ExecutionStatus.success);
			return pathFollowed;
		}
		moveToNextLocAlongPathSimplifiedTraffic(scope, agent, path, maxDist, weigths, livingSpace, tolerance,
			laneAttributes, obsSpecies);
		// scope.setStatus(ExecutionStatus.success);
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

	private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
		final double livingSpace, final double tolerance, final GamaPoint currentLocation, final GamaPoint target,
		final int nbLanes, final GamaList<ISpecies> obsSpecies) {
		// Collision avoiding

		// 2. Selects the agents before the agent on the segment
		final double newX = currentLocation.x + 2 * tolerance * (target.x - currentLocation.x);
		final double newY = currentLocation.y + 2 * tolerance * (target.y - currentLocation.y);
		GamaPoint target_pt = null;
		if ( target.distance(currentLocation) < distance ) {
			target_pt = target;
		} else {
			final double targetX = currentLocation.x + (distance - tolerance) * (target.x - currentLocation.x);
			final double targetY = currentLocation.y + (distance - tolerance) * (target.y - currentLocation.y);
			target_pt = new GamaPoint(targetX, targetY);
		}
		final Coordinate[] segment2 = { new GamaPoint(newX, newY), target_pt };
		double minDist = distance;
		final Geometry basicLine = GeometryUtils.factory.createLineString(segment2);
		GamaList<IAgent> result = new GamaList<IAgent>();
		for ( ISpecies species : obsSpecies ) {
			// final IPopulation pop = agent.getPopulationFor(species);
			result.addAll(new GamaList<IAgent>(scope.getTopology().getNeighboursOf(scope, new GamaShape(basicLine),
				tolerance, species)));
		}
		for ( IAgent ia : result ) {
			if ( ia == agent || ia.intersects(currentLocation) ) {
				continue;
			}
			// if(fr2.intersects(ia.getLocation().getInnerGeometry())){
			// final double distL = basicLine.distance(ia.getInnerGeometry());
			double currentDistance = ia.euclidianDistanceTo(currentLocation);
			if ( currentDistance > tolerance ) {
				currentDistance -= livingSpace;
				// currentDistance = currentLocation.euclidianDistanceTo(ia) - livingSpace;
				final List<IAgent> ns =
					new GamaList<IAgent>(scope.getTopology().getNeighboursOf(scope, ia, livingSpace / 2.0,
						In.list(scope, result)));
				int nbAg = 1;
				for ( IAgent ag : ns ) {
					if ( ag != agent ) {
						nbAg++;
					}
				}
				if ( nbAg >= nbLanes && currentDistance < minDist ) {
					minDist = Math.max(0, currentDistance);
				}

			}

		}

		// 3. Determines the distance to the nearest agent in front of him
		return minDist;
	}

	private int computeNbLanes(final IShape lineAg, final String laneAttributes) {
		return lineAg == null || !(lineAg instanceof IAgent) ? 1 : (Integer) ((IAgent) lineAg)
			.getAttribute(laneAttributes);

	}

	private void moveToNextLocAlongPathSimplifiedTraffic(final IScope scope, final IAgent agent, final IPath path,
		final double _distance, final GamaMap weigths, final double livingSpace, final double tolerance,
		final String laneAttributes, final GamaList<ISpecies> obsSpecies) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		final GamaList indexVals = initMoveAlongPath(agent, path, currentLocation);
		if ( indexVals == null ) { return; }
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		final int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		final GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		final IList<IShape> edges = path.getEdgeGeometry();
		final int nb = edges.size();
		double distance = _distance;
		final GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

		for ( int i = index; i < nb; i++ ) {
			final IShape line = edges.get(i);
			final IShape lineAg = path.getRealObject(line);
			final int nbLanes = computeNbLanes(lineAg, laneAttributes);
			// current edge
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			// weight is 1 by default, otherwise is the distributed edge's weight by length unity
			double weight;
			if ( weigths == null ) {
				weight = computeWeigth(graph, path, line);
			} else {
				IShape realShape = path.getRealObject(line);
				final Double w =
					realShape == null ? null : (Double) weigths.get(realShape) / realShape.getGeometry().getPerimeter();
				weight = w == null ? computeWeigth(graph, path, line) : w;
			}

			//
			for ( int j = indexSegment; j < coords.length; j++ ) {
				// pt is the next target
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					// The agents has arrived to the target, and he is located in the
					// nearest location to the real target on the graph
					pt = falseTarget;
				} else {
					// otherwise is the extremity of the segment
					pt = new GamaPoint(coords[j]);
				}
				// distance from current location to next target
				double dist = pt.euclidianDistanceTo(currentLocation);
				// For the while, for a high weight, the vehicle moves slowly
				dist = weight * dist;
				distance =
					avoidCollision(scope, agent, distance, livingSpace, tolerance, currentLocation, pt, nbLanes,
						obsSpecies);

				// that's the real distance to move
				// Agent moves
				if ( distance == 0 ) {
					break;
				}
				if ( distance < dist ) {
					final double ratio = distance / dist;
					final double newX = currentLocation.x + ratio * (pt.x - currentLocation.x);
					final double newY = currentLocation.y + ratio * (pt.y - currentLocation.y);
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
			// The current edge is over, agent moves to the next one
		}
		if ( currentLocation.equals(falseTarget) ) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		agent.setLocation(currentLocation);
		path.setSource(currentLocation.copy(scope));

	}

	private IPath moveToNextLocAlongPathTraffic(final IScope scope, final IAgent agent, final IPath path,
		final double _distance, final GamaMap weigths, final double livingSpace, final double tolerance,
		final String laneAttributes, final GamaList<ISpecies> obsSpecies) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		final GamaList indexVals = initMoveAlongPath(agent, path, currentLocation);
		if ( indexVals == null ) { return null; }
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		final int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		final GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		final IList<IShape> edges = path.getEdgeGeometry();
		final int nb = edges.size();
		double distance = _distance;
		final GamaList<IShape> segments = new GamaList();
		final GamaPoint startLocation = (GamaPoint) agent.getLocation().copy(scope);
		final GamaMap agents = new GamaMap();
		for ( int i = index; i < nb; i++ ) {
			final IShape line = edges.get(i);
			final IShape lineAg = path.getRealObject(line);
			final int nbLanes = computeNbLanes(lineAg, laneAttributes);
			final GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

			double weight;
			if ( weigths == null ) {
				weight = computeWeigth(graph, path, line);
			} else {
				IShape realShape = path.getRealObject(line);
				final Double w =
					realShape == null ? null : (Double) weigths.get(realShape) / realShape.getGeometry().getPerimeter();
				weight = w == null ? computeWeigth(graph, path, line) : w;
			}
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for ( int j = indexSegment; j < coords.length; j++ ) {
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = scope.getTopology().distanceBetween(scope, pt, currentLocation);
				dist = weight * dist;
				distance =
					avoidCollision(scope, agent, distance, livingSpace, tolerance, currentLocation, pt, nbLanes,
						obsSpecies);

				if ( distance < dist ) {
					final GamaPoint pto = currentLocation.copy(scope);
					final double ratio = distance / dist;
					final double newX = pto.x + ratio * (pt.x - pto.x);
					final double newY = pto.y + ratio * (pt.y - pto.y);
					currentLocation.setLocation(newX, newY);
					final IShape gl = GamaGeometryType.buildLine(pto, currentLocation);
					final IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);
					distance = 0;
					break;
				} else if ( distance > dist ) {
					final IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					final IAgent a = line.getAgent();
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
					final IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					final IAgent a = line.getAgent();
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
		path.setSource(currentLocation.copy(scope));
		if ( segments.isEmpty() ) { return null; }
		final IPath followedPath =
			PathFactory.newInstance(scope.getTopology(), startLocation, currentLocation, segments);
		// new GamaPath(scope.getTopology(), startLocation, currentLocation, segments);
		followedPath.setRealObjects(agents);
		agent.setLocation(currentLocation);
		return followedPath;
	}
	
	@action(name = "follow_driving_complex", args = {
			@arg(name = "path", type = IType.PATH, optional = true, doc = @doc("a path to be followed.")),
			@arg(name = "target", type = IType.POINT, optional = true, doc = @doc("the target to reach")),
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "time", type = IType.FLOAT, optional = true, doc = @doc("time to travel"))}, 
			doc = @doc(value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)", returns = "the remaining time", examples = { "do osm_follow path: the_path on: road_network;" }))
		public Double primOSMFollow(final IScope scope) throws GamaRuntimeException {
			final IAgent agent = getCurrentAgent(scope);
			final double security_distance = getSecurityDistance(agent);
			final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
			final Double t = scope.hasArg("time") ? scope.getFloatArg("time") : 1.0;
			
			final double maxDist = computeDistance(scope, agent,s,t);
			final int currentLane = getCurrentLane(agent);
			
			final IAgent currentRoad = (IAgent) getCurrentRoad(agent);
			final ITopology topo = computeTopology(scope, agent);
			if ( topo == null ) {
				return 0.0;
			}
			final GamaPoint target = scope.hasArg("target") ? (GamaPoint) scope.getArg("target", IType.NONE) : null;
			final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
			if ( path != null && !path.getEdgeList().isEmpty() ) {
				double tps = t * moveToNextLocAlongPathOSM(scope, agent, path, target, maxDist, security_distance, currentLane, currentRoad);	
				if (tps < 1.0)
					agent.setAttribute(REAL_SPEED, this.getRealSpeed(agent) / ((1 - tps)));
				
				return tps;
			}
			return 0.0;
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

		protected double computeDistance(final IScope scope, final IAgent agent, final double s,final double t) throws GamaRuntimeException {
			
			return s * t * scope.getClock().getStep();
		}

		private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
				final double security_distance, final GamaPoint currentLocation, final GamaPoint target,
				final int lane, final IAgent currentRoad) {
				IList agents = (IList) ((GamaList) currentRoad.getAttribute("agents_on")).get(lane);
				if (agents.size() < 2)
					return distance;
				
				double distanceMax = distance + security_distance +  getVehiculeLength(agent);
				
				List<IAgent> agsFiltered = new GamaList(agent.getTopology().getNeighboursOf(scope,agent.getLocation(), distanceMax, In.list(scope, agents)));
				
				if (agsFiltered.isEmpty())
					return distance;
				
				double distanceToGoal = agent.euclidianDistanceTo(target);//getDistanceToGoal(agent);
				//double distanceMax = distance + security_distance +  0.5 * getVehiculeLength(agent);
				IAgent nextAgent = null;
				double minDiff = Double.MAX_VALUE;
				for (IAgent ag : agsFiltered) {
					double dist = ag.euclidianDistanceTo(target);
					double diff = (distanceToGoal - dist) ;
					if (diff > 0 && diff <  minDiff) {
						minDiff = diff;
						nextAgent = ag;
					}
				}
				
				if (nextAgent == null)
					return distance;
				
				double realDist = Math.min(distance, minDiff - security_distance - 0.5 * getVehiculeLength(agent) - 0.5 * getVehiculeLength(nextAgent) );
				
				return Math.max(0.0,realDist );
			}
		

		private GamaPoint computeRealTarget( final IAgent agent, 
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target,
			final int lane, final IAgent currentRoad) {
			
		//	System.out.println("currentRoad : " + currentRoad);
		//	System.out.println("currentRoad.getAttribute(agents_on)) : " + currentRoad.getAttribute("agents_on"));
			
			List<IAgent> agents = (List<IAgent>) ((GamaList) currentRoad.getAttribute("agents_on")).get(lane);
			if (agents.size() < 2)
				return target;
			//System.out.println("agents : " + agents);
			double distanceToGoal = agent.euclidianDistanceTo(target);//getDistanceToGoal(agent);
			IAgent nextAgent = null;
			double minDiff = Double.MAX_VALUE;
			for (IAgent ag : agents) {
				if (ag == agent) continue;
				double dist = ag.euclidianDistanceTo(target);//getDistanceToGoal(ag);
				double diff = (distanceToGoal - dist) ;
				if (dist < distanceToGoal && diff <  minDiff ) {
					minDiff = diff;
					nextAgent = ag;
				}
			}
			if (nextAgent == null)
				return target;
			//System.out.println("currentRoad.getAttribute(agents_on)).get(index - 1) : " + ((GamaList) ((GamaList) currentRoad.getAttribute("agents_on")).get(lane)).get(index - 1));
			double buff_dist = getVehiculeLength(agent) + getVehiculeLength(nextAgent) + security_distance;
			IShape shape = Transformations.enlarged_by(nextAgent.getLocation(), buff_dist);
			IShape shapeInter = Operators.inter(currentRoad, shape);
			//System.out.println("ICI\nnextAgent.getLocation() : " + nextAgent.getLocation());
			//System.out.println("currentRoad : " + currentRoad.getGeometry());
			
			
			//return target; 
			if (shapeInter != null) {
				return (GamaPoint) Punctal._closest_point_to(currentLocation, shapeInter);
			}
			return target;
		}

		private double moveToNextLocAlongPathOSM(final IScope scope, final IAgent agent, final IPath path, final GamaPoint target, final double _distance, final double security_distance,final int currentLane, final  IAgent currentRoad) {
			
			GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
			GamaPoint falseTarget = target == null ? new GamaPoint(currentRoad.getInnerGeometry().getCoordinates()[currentRoad.getInnerGeometry().getCoordinates().length]) : target;
			/*for (Object ag : path.getEdgeList()) {
				System.out.println(ag + " -> " + ((IAgent) ag).getGeometry());
			}*/
		
			//final GamaPoint falseTarget = pt_target; //computeRealTarget(agent, security_distance, currentLocation, pt_target, currentLane, currentRoad);
			final GamaList indexVals =  initMoveAlongPath(agent, path, currentLocation, falseTarget, currentRoad);
			if (indexVals == null) return 0.0;
			int indexSegment = (Integer) indexVals.get(0);
			final int endIndexSegment = (Integer) indexVals.get(1);
			//System.out.println("currentRoad : " + currentRoad.getGeometry());
			//System.out.println("currentLocation : " + currentLocation + " falseTarget : " + falseTarget);
			//System.out.println("indexSegment : " + indexSegment + " endIndexSegment : " + endIndexSegment);
			
			if (indexSegment > endIndexSegment) {
				return 0.0;
			}
			double distance = _distance;
			final GamaGraph graph = (GamaGraph) path.getGraph();
			double realDistance = 0;
			final IShape line = currentRoad.getGeometry();
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates(); 
			
			
			for ( int j = indexSegment; j <= endIndexSegment; j++ ) {
				GamaPoint pt = null;
				if ( j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
			//	System.out.println("j : " + j + " endIndexSegment : " + endIndexSegment + " pt : " + pt);
				
				double dist = pt.euclidianDistanceTo(currentLocation);
				
				distance =
						avoidCollision(scope, agent, distance, security_distance, currentLocation, falseTarget, currentLane, currentRoad);
				if ( distance < dist ) {
					final double ratio = distance / dist;
					final double newX = currentLocation.getX() + ratio * (pt.getX() - currentLocation.getX());
					final double newY = currentLocation.getY() + ratio * (pt.getY() - currentLocation.getY());
					GamaPoint npt = new GamaPoint(newX,newY);
					realDistance += currentLocation.euclidianDistanceTo(npt);
					currentLocation.setLocation(npt);
					distance = 0;
					break;
				} else {
					currentLocation = pt;
					distance = distance - dist;
					realDistance += dist; 
					if (j == endIndexSegment ) {
						break;
					}
					indexSegment++;
				}
			}
			//path.setIndexSegementOf(agent, indexSegment);
			agent.setLocation(currentLocation);
			path.setSource(currentLocation.copy(scope));
			agent.setAttribute(REAL_SPEED, realDistance / scope.getClock().getStep());
			setDistanceToGoal(agent, currentLocation.euclidianDistanceTo(falseTarget));
			//System.out.println("_distance : " + _distance);
			//System.out.println("distance : " + distance);
			return _distance == 0.0 ? 1.0 : (distance / _distance) ;
		}
		
		protected GamaList initMoveAlongPath(final IAgent agent, final IPath path, final GamaPoint currentLocation,final GamaPoint falseTarget, final IAgent currentRoad) {
			final GamaList initVals = new GamaList();
			Integer indexSegment = 0;
			Integer endIndexSegment = 0;
			final IList<IShape> edges = path.getEdgeGeometry();
			if (edges.isEmpty()) return null;
			final int nb = edges.size();
			if ( currentRoad.getInnerGeometry().getNumPoints() == 2 ) {
				indexSegment = 0;
				endIndexSegment = 0;
				
			} else {
				double distanceS = Double.MAX_VALUE;
				double distanceT = Double.MAX_VALUE;
				IShape line = currentRoad.getGeometry();
				final Point pointS = (Point) currentLocation.getInnerGeometry();
				final Point pointT = (Point) falseTarget.getInnerGeometry();
				final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
				final int nbSp = coords.length;
				final Coordinate[] temp = new Coordinate[2];
				for ( int i = 0; i < nbSp - 1; i++ ) {
					temp[0] = coords[i];
					temp[1] = coords[i + 1];
					final LineString segment = GeometryUtils.factory.createLineString(temp);
					final double distS = segment.distance(pointS);
					if ( distS < distanceS ) {
						distanceS = distS;
						indexSegment = i + 1;
					}
					final double distT = segment.distance(pointT);
					if ( distT < distanceT ) {
						distanceT = distT;
						endIndexSegment = i + 1;
					}
				}
			}
			initVals.add(indexSegment);
			initVals.add(endIndexSegment);
			return initVals;
		}
}
