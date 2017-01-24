/*********************************************************************************************
 *
 *
 * 'DrivingSkill.java', in plugin 'simtools.gaml.extensions.traffic', is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.Collection;
import java.util.Collections;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars ({ @var (
		name = "living_space",
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc ("the min distance between the agent and an obstacle (in meter)")),
		@var (
				name = "lanes_attribute",
				type = IType.STRING,
				doc = @doc ("the name of the attribut of the road agent that determine the number of road lanes")),
		@var (
				name = "tolerance",
				type = IType.FLOAT,
				init = "0.1",
				doc = @doc ("the tolerance distance used for the computation (in meter)")),
		@var (
				name = "obstacle_species",
				type = IType.LIST,
				init = "[]",
				doc = @doc ("the list of species that are considered as obstacles")),
		@var (
				name = IKeyword.SPEED,
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("the speed of the agent (in meter/second)")) })
@skill (
		name = "driving",
		concept = { IConcept.TRANSPORT, IConcept.SKILL, IConcept.AGENT_MOVEMENT })
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrivingSkill extends MovingSkill {

	public final static String LIVING_SPACE = "living_space";
	public final static String TOLERANCE = "tolerance";
	public final static String LANES_ATTRIBUTE = "lanes_attribute";
	public final static String OBSTACLE_SPECIES = "obstacle_species";

	@getter (LIVING_SPACE)
	public double getLivingSpace(final IAgent agent) {
		return (Double) agent.getAttribute(LIVING_SPACE);
	}

	@setter (LANES_ATTRIBUTE)
	public void setLanesAttribute(final IAgent agent, final String latt) {
		agent.setAttribute(LANES_ATTRIBUTE, latt);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter (LANES_ATTRIBUTE)
	public String getLanesAttribute(final IAgent agent) {
		return (String) agent.getAttribute(LANES_ATTRIBUTE);
	}

	@setter (LIVING_SPACE)
	public void setLivingSpace(final IAgent agent, final double ls) {
		agent.setAttribute(LIVING_SPACE, ls);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter (TOLERANCE)
	public double getTolerance(final IAgent agent) {
		return (Double) agent.getAttribute(TOLERANCE);
	}

	@setter (TOLERANCE)
	public void setTolerance(final IAgent agent, final double t) {
		agent.setAttribute(TOLERANCE, t);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter (OBSTACLE_SPECIES)
	public GamaList<ISpecies> getObstacleSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(OBSTACLE_SPECIES);
	}

	@setter (OBSTACLE_SPECIES)
	public void setObstacleSpecies(final IAgent agent, final GamaList<ISpecies> os) {
		agent.setAttribute(OBSTACLE_SPECIES, os);
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
	@action (
			name = "follow_driving",
			args = { @arg (
					name = IKeyword.SPEED,
					type = IType.FLOAT,
					optional = true,
					doc = @doc ("the speed to use for this move (replaces the current value of speed)")),
					@arg (
							name = "path",
							type = IType.PATH,
							optional = true,
							doc = @doc ("a path to be followed.")),
					@arg (
							name = "return_path",
							type = IType.BOOL,
							optional = true,
							doc = @doc ("if true, return the path followed (by default: false)")),
					@arg (
							name = "move_weights",
							type = IType.MAP,
							optional = true,
							doc = @doc ("Weigths used for the moving.")),
					@arg (
							name = LIVING_SPACE,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("min distance between the agent and an obstacle (replaces the current value of living_space)")),
					@arg (
							name = TOLERANCE,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("tolerance distance used for the computation (replaces the current value of tolerance)")),
					@arg (
							name = LANES_ATTRIBUTE,
							type = IType.STRING,
							optional = true,
							doc = @doc ("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)")) },
			doc = @doc (
					value = "moves the agent along a given path passed in the arguments while considering the other agents in the network.",
					returns = "optional: the path followed by the agent.",
					examples = { @example ("do follow speed: speed * 2 path: road_path;") }))
	public IPath primFollow(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final double maxDist = computeDistance(scope, agent);
		final double tolerance = computeTolerance(scope, agent);
		final double livingSpace = computeLivingSpace(scope, agent);
		final Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		final GamaMap weigths = (GamaMap) computeMoveWeights(scope);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		String laneAttributes = computeLanesNumber(scope, agent);
		if (laneAttributes == null || "".equals(laneAttributes)) {
			laneAttributes = "lanes_number";
		}

		final IShape goal = computeTarget(scope, agent);
		if (goal == null) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final ITopology topo = computeTopology(scope, agent);
		if (topo == null) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		if (path != null && !path.getEdgeList().isEmpty()) {
			if (returnPath != null && returnPath) {
				final IPath pathFollowed = moveToNextLocAlongPathTraffic(scope, agent, path, maxDist, weigths,
						livingSpace, tolerance, laneAttributes, obsSpecies);
				if (pathFollowed == null) {
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

	@action (
			name = "goto_driving",
			args = { @arg (
					name = "target",
					type = IType.GEOMETRY,
					optional = false,
					doc = @doc ("the location or entity towards which to move.")),
					@arg (
							name = IKeyword.SPEED,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("the speed to use for this move (replaces the current value of speed)")),
					@arg (
							name = "on",
							optional = true,
							doc = @doc ("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")),
					@arg (
							name = "return_path",
							type = IType.BOOL,
							optional = true,
							doc = @doc ("if true, return the path followed (by default: false)")),
					@arg (
							name = "move_weights",
							type = IType.MAP,
							optional = true,
							doc = @doc ("Weigths used for the moving.")),
					@arg (
							name = LIVING_SPACE,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("min distance between the agent and an obstacle (replaces the current value of living_space)")),
					@arg (
							name = TOLERANCE,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("tolerance distance used for the computation (replaces the current value of tolerance)")),
					@arg (
							name = LANES_ATTRIBUTE,
							type = IType.STRING,
							optional = true,
							doc = @doc ("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)")) },
			doc = @doc (
					value = "moves the agent towards the target passed in the arguments while considering the other agents in the network (only for graph topology)",
					returns = "optional: the path followed by the agent.",
					examples = {
							@example ("do gotoTraffic target: one_of (list (species (self))) speed: speed * 2 on: road_network living_space: 2.0;") }))
	public IPath primGotoTraffic(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation source = agent.getLocation().copy(scope);
		final double maxDist = computeDistance(scope, agent);
		final double tolerance = computeTolerance(scope, agent);
		final double livingSpace = computeLivingSpace(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		String laneAttributes = computeLanesNumber(scope, agent);
		if (laneAttributes == null || "".equals(laneAttributes)) {
			laneAttributes = "lanes_number";
		}

		final IShape goal = computeTarget(scope, agent);
		if (goal == null) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final ITopology topo = computeTopology(scope, agent);
		if (topo == null) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if (path == null || !path.getTopology(scope).equals(topo) || !path.getEndVertex().equals(goal)
				|| !path.getStartVertex().equals(source)) {
			path = topo.pathBetween(scope, source, goal);
		} else {

			if (topo instanceof GraphTopology) {
				if (((GraphTopology) topo).getPlaces() != path.getGraph()
						|| ((GraphTopology) topo).getPlaces().getVersion() != path.getGraphVersion()) {
					path = topo.pathBetween(scope, source, goal);
				}
			}
		}

		if (path == null) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		final GamaMap weigths = (GamaMap) computeMoveWeights(scope);
		if (returnPath != null && returnPath) {
			final IPath pathFollowed = moveToNextLocAlongPathTraffic(scope, agent, path, maxDist, weigths, livingSpace,
					tolerance, laneAttributes, obsSpecies);
			if (pathFollowed == null) {
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
	 * @param coords
	 *            coordinates of the line
	 * @param source
	 *            current location
	 * @param target
	 *            location to reach
	 * @param distance
	 *            max displacement distance
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
		if (target.distance(currentLocation) < distance) {
			target_pt = target;
		} else {
			final double targetX = currentLocation.x + (distance - tolerance) * (target.x - currentLocation.x);
			final double targetY = currentLocation.y + (distance - tolerance) * (target.y - currentLocation.y);
			target_pt = new GamaPoint(targetX, targetY);
		}
		final Coordinate[] segment2 = { new GamaPoint(newX, newY), target_pt };
		double minDist = distance;
		final Geometry basicLine = GeometryUtils.GEOMETRY_FACTORY.createLineString(segment2);
		final IList<IAgent> result = GamaListFactory.create(Types.AGENT);
		for (final ISpecies species : obsSpecies) {
			// final IPopulation pop = agent.getPopulationFor(species);
			result.addAll(scope.getTopology().getNeighborsOf(scope, new GamaShape(basicLine), tolerance, species));
		}
		for (final IAgent ia : result) {
			if (ia == agent || ia.intersects(currentLocation)) {
				continue;
			}
			// if(fr2.intersects(ia.getLocation().getInnerGeometry())){
			// final double distL = basicLine.distance(ia.getInnerGeometry());
			double currentDistance = ia.euclidianDistanceTo(currentLocation);
			if (currentDistance > tolerance) {
				currentDistance -= livingSpace;
				// currentDistance = currentLocation.euclidianDistanceTo(ia) -
				// livingSpace;
				final IAgentFilter filter = In.list(scope, result);
				final Collection<IAgent> ns = filter == null ? Collections.EMPTY_LIST
						: scope.getTopology().getNeighborsOf(scope, ia, livingSpace / 2.0, filter);
				int nbAg = 1;
				for (final IAgent ag : ns) {
					if (ag != agent) {
						nbAg++;
					}
				}
				if (nbAg >= nbLanes && currentDistance < minDist) {
					minDist = FastMath.max(0, currentDistance);
				}

			}

		}

		// 3. Determines the distance to the nearest agent in front of him
		return minDist;
	}

	private int computeNbLanes(final IShape lineAg, final String laneAttributes) {
		return lineAg == null || !(lineAg instanceof IAgent) ? 1
				: (Integer) ((IAgent) lineAg).getAttribute(laneAttributes);

	}

	private void moveToNextLocAlongPathSimplifiedTraffic(final IScope scope, final IAgent agent, final IPath path,
			final double _distance, final GamaMap weigths, final double livingSpace, final double tolerance,
			final String laneAttributes, final GamaList<ISpecies> obsSpecies) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		final IList indexVals = initMoveAlongPath(agent, path, currentLocation);
		if (indexVals == null) { return; }
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		final int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		final GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		final IList<IShape> edges = path.getEdgeGeometry();
		final int nb = edges.size();
		double distance = _distance;
		final GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

		for (int i = index; i < nb; i++) {
			final IShape line = edges.get(i);
			final IShape lineAg = path.getRealObject(line);
			final int nbLanes = computeNbLanes(lineAg, laneAttributes);
			// current edge
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			// weight is 1 by default, otherwise is the distributed edge's
			// weight by length unity
			double weight;
			if (weigths == null) {
				weight = computeWeigth(graph, path, line);
			} else {
				final IShape realShape = path.getRealObject(line);
				final Double w = realShape == null ? null
						: (Double) weigths.get(realShape) / realShape.getGeometry().getPerimeter();
				weight = w == null ? computeWeigth(graph, path, line) : w;
			}

			//
			for (int j = indexSegment; j < coords.length; j++) {
				// pt is the next target
				GamaPoint pt = null;
				if (i == nb - 1 && j == endIndexSegment) {
					// The agents has arrived to the target, and he is located
					// in the
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
				distance = avoidCollision(scope, agent, distance, livingSpace, tolerance, currentLocation, pt, nbLanes,
						obsSpecies);

				// that's the real distance to move
				// Agent moves
				if (distance == 0) {
					break;
				}
				if (distance < dist) {
					final double ratio = distance / dist;
					final double newX = currentLocation.x + ratio * (pt.x - currentLocation.x);
					final double newY = currentLocation.y + ratio * (pt.y - currentLocation.y);
					currentLocation.setLocation(newX, newY);
					distance = 0;
					break;
				} else if (distance > dist) {
					currentLocation = pt;
					distance = distance - dist;
					if (i == nb - 1 && j == endIndexSegment) {
						break;
					}
					indexSegment++;
				} else {
					currentLocation = pt;
					distance = 0;
					if (indexSegment < coords.length - 1) {
						indexSegment++;
					} else {
						index++;
					}
					break;
				}
			}
			if (distance == 0) {
				break;
			}
			indexSegment = 1;
			index++;
			// The current edge is over, agent moves to the next one
		}
		if (currentLocation.equals(falseTarget)) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		setLocation(agent, currentLocation);
		path.setSource(currentLocation.copy(scope));

	}

	private IPath moveToNextLocAlongPathTraffic(final IScope scope, final IAgent agent, final IPath path,
			final double _distance, final GamaMap weigths, final double livingSpace, final double tolerance,
			final String laneAttributes, final GamaList<ISpecies> obsSpecies) {
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		final IList indexVals = initMoveAlongPath(agent, path, currentLocation);
		if (indexVals == null) { return null; }
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		final int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		final GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		final IList<IShape> edges = path.getEdgeGeometry();
		final int nb = edges.size();
		double distance = _distance;
		final IList<IShape> segments = GamaListFactory.create(Types.GEOMETRY);
		final GamaPoint startLocation = (GamaPoint) agent.getLocation().copy(scope);
		final THashMap agents = new THashMap<>();
		for (int i = index; i < nb; i++) {
			final IShape line = edges.get(i);
			final IShape lineAg = path.getRealObject(line);
			final int nbLanes = computeNbLanes(lineAg, laneAttributes);
			final GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();

			double weight;
			if (weigths == null) {
				weight = computeWeigth(graph, path, line);
			} else {
				final IShape realShape = path.getRealObject(line);
				final Double w = realShape == null ? null
						: (Double) weigths.get(realShape) / realShape.getGeometry().getPerimeter();
				weight = w == null ? computeWeigth(graph, path, line) : w;
			}
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for (int j = indexSegment; j < coords.length; j++) {
				GamaPoint pt = null;
				if (i == nb - 1 && j == endIndexSegment) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = scope.getTopology().distanceBetween(scope, pt, currentLocation);
				dist = weight * dist;
				distance = avoidCollision(scope, agent, distance, livingSpace, tolerance, currentLocation, pt, nbLanes,
						obsSpecies);

				if (distance < dist) {
					final GamaPoint pto = currentLocation.copy(scope);
					final double ratio = distance / dist;
					final double newX = pto.x + ratio * (pt.x - pto.x);
					final double newY = pto.y + ratio * (pt.y - pto.y);
					currentLocation.setLocation(newX, newY);
					final IShape gl = GamaGeometryType.buildLine(pto, currentLocation);
					final IAgent a = line.getAgent();
					if (a != null) {
						agents.put(gl, a);
					}
					segments.add(gl);
					distance = 0;
					break;
				} else if (distance > dist) {
					final IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					final IAgent a = line.getAgent();
					if (a != null) {
						agents.put(gl, a);
					}
					segments.add(gl);
					currentLocation = pt;
					distance = distance - dist;
					if (i == nb - 1 && j == endIndexSegment) {
						break;
					}
					indexSegment++;
				} else {
					final IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					final IAgent a = line.getAgent();
					if (a != null) {
						agents.put(gl, a);
					}
					segments.add(gl);
					currentLocation = pt;
					distance = 0;
					if (indexSegment < coords.length - 1) {
						indexSegment++;
					} else {
						index++;
					}
					break;
				}
			}
			if (distance == 0) {
				break;
			}
			indexSegment = 1;
			index++;
		}
		if (currentLocation.equals(falseTarget)) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		path.setSource(currentLocation.copy(scope));
		if (segments.isEmpty()) { return null; }
		final IPath followedPath =
				PathFactory.newInstance(scope, scope.getTopology(), startLocation, currentLocation, segments);
		// new GamaPath(scope.getTopology(), startLocation, currentLocation,
		// segments);
		followedPath.setRealObjects(agents);
		setLocation(agent, currentLocation);
		return followedPath;
	}

	protected IList initMoveAlongPath(final IAgent agent, final IPath path, final GamaPoint currentLocation,
			final GamaPoint falseTarget, final IAgent currentRoad) {
		final IList initVals = GamaListFactory.create(Types.INT);
		Integer indexSegment = 0;
		Integer endIndexSegment = 0;
		final IList<IShape> edges = path.getEdgeGeometry();
		if (edges.isEmpty()) { return null; }
		// final int nb = edges.size();
		if (currentRoad.getInnerGeometry().getNumPoints() == 2) {
			indexSegment = 0;
			endIndexSegment = 0;

		} else {
			double distanceS = Double.MAX_VALUE;
			double distanceT = Double.MAX_VALUE;
			final IShape line = currentRoad.getGeometry();
			final Point pointS = (Point) currentLocation.getInnerGeometry();
			final Point pointT = (Point) falseTarget.getInnerGeometry();
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			final int nbSp = coords.length;
			final Coordinate[] temp = new Coordinate[2];
			for (int i = 0; i < nbSp - 1; i++) {
				temp[0] = coords[i];
				temp[1] = coords[i + 1];
				final LineString segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(temp);
				final double distS = segment.distance(pointS);
				if (distS < distanceS) {
					distanceS = distS;
					indexSegment = i + 1;
				}
				final double distT = segment.distance(pointT);
				if (distT < distanceT) {
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
