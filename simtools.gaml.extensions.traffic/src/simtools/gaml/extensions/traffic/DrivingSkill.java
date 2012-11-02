package simtools.gaml.extensions.traffic;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

import com.vividsolutions.jts.geom.*;

@vars({ @var(name = "living_space", type = IType.FLOAT_STR, init = "1.0", doc = @doc("the min distance between the agent and an obstacle (in meter)")),
	@var(name = "lanes_attribute", type = IType.STRING_STR, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes")),
	@var(name = "tolerance", type = IType.FLOAT_STR, init = "0.1", doc = @doc("the tolerance distance used for the computation (in meter)")),
	@var(name = "obstacle_species", type = IType.LIST_STR, init = "[]", doc = @doc("the list of species that are considered as obstacles")),
	@var(name = IKeyword.SPEED, type = IType.FLOAT_STR, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")) })
@skill(name = "driving")
public class DrivingSkill extends MovingSkill {

	public final static String LIVING_SPACE = "living_space";
	public final static String TOLERANCE = "tolerance";
	public final static String LANES_ATTRIBUTE = "lanes_attribute";
	public final static String OBSTACLE_SPECIES = "obstacle_species";

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

	protected String computeLanesNumber(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return scope.hasArg(LANES_ATTRIBUTE) ? scope.getStringArg(LANES_ATTRIBUTE)
			: getLanesAttribute(agent);
	}

	protected GamaList<ISpecies> computeObstacleSpecies(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return (GamaList<ISpecies>) (scope.hasArg(OBSTACLE_SPECIES) ? scope
			.getListArg(OBSTACLE_SPECIES) : getObstacleSpecies(agent));
	}

	protected double computeTolerance(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return scope.hasArg(TOLERANCE) ? scope.getFloatArg(TOLERANCE) : getTolerance(agent);
	}

	protected double computeLivingSpace(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return scope.hasArg(LIVING_SPACE) ? scope.getFloatArg(LIVING_SPACE) : getLivingSpace(agent);
	}
	
	@action(name = "follow_driving", args = {
			@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "path", type = IType.PATH_STR, optional = true, doc = @doc("a path to be followed.")),
			@arg(name = "return_path", type = IType.BOOL_STR, optional = true, doc = @doc("if true, return the path followed (by default: false)")),
			@arg(name = LIVING_SPACE, type = IType.FLOAT_STR, optional = true, doc = @doc("min distance between the agent and an obstacle (replaces the current value of living_space)")),
			@arg(name = TOLERANCE, type = IType.FLOAT_STR, optional = true, doc = @doc("tolerance distance used for the computation (replaces the current value of tolerance)")),
			@arg(name = LANES_ATTRIBUTE, type = IType.STRING_STR, optional = true, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)"))},
			doc = @doc(value = "moves the agent along a given path passed in the arguments while considering the other agents in the network.", returns = "optional: the path followed by the agent.", examples = { "do follow speed: speed * 2 path: road_path;" }))
		public IPath primFollow(final IScope scope) throws GamaRuntimeException {
			IAgent agent = getCurrentAgent(scope);
			final double maxDist = computeDistance(scope, agent);
			final double tolerance = computeTolerance(scope, agent);
			final double livingSpace = computeLivingSpace(scope, agent);
			Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
			final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
			String laneAttributes = computeLanesNumber(scope, agent);
			if ( laneAttributes == null || "".equals(laneAttributes) ) {
				laneAttributes = "lanes_number";
			}

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
			GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
			if ( path != null && !path.getEdgeList().isEmpty() ) {
				if ( returnPath != null && returnPath ) {
					IPath pathFollowed = moveToNextLocAlongPathTraffic(agent, path, maxDist, livingSpace, tolerance,
							laneAttributes, obsSpecies);
					if ( pathFollowed == null ) {
						scope.setStatus(ExecutionStatus.failure);
						return null;
					}
					scope.setStatus(ExecutionStatus.success);
					return pathFollowed;
				}
				moveToNextLocAlongPathSimplifiedTraffic(agent, path, maxDist, livingSpace, tolerance,
						laneAttributes, obsSpecies);
				scope.setStatus(ExecutionStatus.success);
				return null;
			}
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}

	@action(name = "goto_driving", args = {
		@arg(name = "target", type = "point or agent", optional = false, doc = @doc("the location or entity towards which to move.")),
		@arg(name = IKeyword.SPEED, type = IType.FLOAT_STR, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "on", type = { IType.LIST_STR, IType.AGENT_STR, IType.GRAPH_STR, IType.GEOM_STR }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")) ,
		@arg(name = "return_path", type = IType.BOOL_STR, optional = true, doc = @doc("if true, return the path followed (by default: false)")),
		@arg(name = LIVING_SPACE, type = IType.FLOAT_STR, optional = true, doc = @doc("min distance between the agent and an obstacle (replaces the current value of living_space)")),
		@arg(name = TOLERANCE, type = IType.FLOAT_STR, optional = true, doc = @doc("tolerance distance used for the computation (replaces the current value of tolerance)")),
		@arg(name = LANES_ATTRIBUTE, type = IType.STRING_STR, optional = true, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)"))}, 
		doc = @doc(value = "moves the agent towards the target passed in the arguments while considering the other agents in the network (only for graph topology)", returns = "optional: the path followed by the agent.", examples = { "do gotoTraffic target: one_of (list (species (self))) speed: speed * 2 on: road_network living_space: 2.0;" }))
	public IPath primGotoTraffic(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy();
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
			IPath pathFollowed =
				moveToNextLocAlongPathTraffic(agent, path, maxDist, livingSpace, tolerance,
					laneAttributes, obsSpecies);
			if ( pathFollowed == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			scope.setStatus(ExecutionStatus.success);
			return pathFollowed;
		}
		moveToNextLocAlongPathSimplifiedTraffic(agent, path, maxDist, livingSpace, tolerance,
			laneAttributes, obsSpecies);
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

	private double avoidCollision(final IAgent agent, final double distance,
		final double livingSpace, final double tolerance, final GamaPoint currentLocation,
		final GamaPoint target, final int nbLanes, final GamaList<ISpecies> obsSpecies) {
		// Collision avoiding
		// 1. Determines the agents located on a dist radius circle from the current location
		IList<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(currentLocation, distance + livingSpace,
				Different.with());
		// 2. Selects the agents before the agent on the segment
		double newX = currentLocation.x + 0.01 * (target.x - currentLocation.x);
		double newY = currentLocation.y + 0.01 * (target.y - currentLocation.y);
		Coordinate[] segment2 = { new GamaPoint(newX, newY), target };
		double minDist = distance;
		Geometry basicLine = GeometryUtils.getFactory().createLineString(segment2);
		// Geometry frontRectangle = basicLine.buffer(tolerance, 3, /**TODO To be modified, to find
		// the right constant name**/2);
		// PreparedPolygon fr2 = new PreparedPolygon((Polygonal) frontRectangle);
		for ( IAgent ia : neighbours ) {
			if ( !obsSpecies.contains(ia.getSpecies()) ) {
				continue;
			}
			// if(fr2.intersects(ia.getLocation().getInnerGeometry())){
			double distL = basicLine.distance(ia.getInnerGeometry());
			double currentDistance = currentLocation.euclidianDistanceTo(ia);
			if ( distL < tolerance && distL < currentDistance ) {
				currentDistance -= livingSpace;
				// currentDistance = currentLocation.euclidianDistanceTo(ia) - livingSpace;
				IList<IAgent> ns =
					agent.getTopology().getNeighboursOf(ia, livingSpace / 2.0, Different.with());
				int nbAg = 1;
				for ( IAgent ag : ns ) {
					if ( ag != agent && obsSpecies.contains(ag.getSpecies()) ) {
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

	private void moveToNextLocAlongPathSimplifiedTraffic(final IAgent agent, final IPath path,
		final double _distance, final double livingSpace, final double tolerance,
		final String laneAttributes, final GamaList<ISpecies> obsSpecies) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		GamaList indexVals = initMoveAlongPath(agent, path, currentLocation);
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		double distance = _distance;
		GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			IShape lineAg = path.getRealObject(line);
			int nbLanes = computeNbLanes(lineAg, laneAttributes);
			// current edge
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			// weight is 1 by default, otherwise is the distributed edge's weight by length unity
			double weight =
				graph == null ? 1 : graph.getEdgeWeight(path.getRealObject(line)) /
					line.getGeometry().getPerimeter();
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
					avoidCollision(agent, distance, livingSpace, tolerance, currentLocation, pt,
						nbLanes, obsSpecies);
				// that's the real distance to move
				// Agent moves
				if ( distance == 0 ) {
					break;
				}
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
			// The current edge is over, agent moves to the next one
		}
		if ( currentLocation.equals(falseTarget) ) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		agent.setLocation(currentLocation);
		path.setSource(currentLocation.copy());

	}

	private IPath moveToNextLocAlongPathTraffic(final IAgent agent, final IPath path,
		final double _distance, final double livingSpace, final double tolerance,
		final String laneAttributes, final GamaList<ISpecies> obsSpecies) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		GamaList indexVals = initMoveAlongPath(agent, path, currentLocation);
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		double distance = _distance;
		GamaList<IShape> segments = new GamaList();
		GamaPoint startLocation = (GamaPoint) agent.getLocation().copy();
		GamaMap agents = new GamaMap();
		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			IShape lineAg = path.getRealObject(line);
			int nbLanes = computeNbLanes(lineAg, laneAttributes);
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
				distance =
					avoidCollision(agent, distance, livingSpace, tolerance, currentLocation, pt,
						nbLanes, obsSpecies);

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
}
