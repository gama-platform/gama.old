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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.skills;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Points;
import msi.gaml.types.*;
import org.jgrapht.Graph;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.AssertionFailedException;

/**
 * MovingSkill : This class is intended to define the minimal set of behaviours required from an
 * agent that is able to move. Each member that has a meaning in GAML is annotated with the
 * respective tags (vars, getter, setter, init, action & args)
 * 
 * @author drogoul 4 juil. 07
 */
@vars({
	@var(name = IKeyword.SPEED, type = IType.FLOAT_STR, init = "1.0"),
	@var(name = IKeyword.HEADING, type = IType.INT_STR, init = "rnd 359"),
	@var(name = IKeyword.DESTINATION, type = IType.POINT_STR, depends_on = { IKeyword.SPEED,
		IKeyword.DESTINATION, IKeyword.LOCATION }) })
@skill({ IKeyword.SKILL_NAME })
public class MovingSkill extends GeometricSkill {

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException Gets the destination of the agent. The destination is the next
	 *             absolute coordinates the agent could reach if it keeps the current speed and the
	 *             current heading
	 */
	@getter(var = IKeyword.DESTINATION)
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

	@getter(var = IKeyword.SPEED)
	public double getSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.SPEED);
	}

	@setter(IKeyword.SPEED)
	public void setSpeed(final IAgent agent, final double s) {
		agent.setAttribute(IKeyword.SPEED, s);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(var = IKeyword.HEADING)
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

	private int computeHeadingFromAmplitude(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		int ampl = scope.hasArg("amplitude") ? scope.getIntArg("amplitude") : 359;
		agent.setHeading(agent.getHeading() + GAMA.getRandom().between(-ampl / 2, ampl / 2));
		return agent.getHeading();
	}

	private int computeHeading(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		Integer heading = scope.hasArg(IKeyword.HEADING) ? scope.getIntArg(IKeyword.HEADING) : null;
		if ( heading != null ) {
			agent.setHeading(heading);
		}
		return agent.getHeading();
	}

	private double computeDistance(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		// We do not change the speed of the agent anymore. Only the current primitive is affected
		Double s =
			scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		// 20/1/2012 Change : The speed of the agent is multiplied by the timestep in order to
		// obtain the maximal distance it can cover in one step.
		return s * SimulationClock.getStep()/* getTimeStep(scope) */;
	}

	private ILocation computeTarget(final IScope scope, final IAgent agent)
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

	private ITopology computeTopology(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		final Object on = scope.getArg("on", IType.NONE);
		ITopology topo = Cast.asTopology(scope, on);
		if ( topo == null ) { return agent.getTopology(); }
		return topo;
	}

	@action("wander")
	@args({ IKeyword.SPEED, "amplitude", "bounds" })
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

	@action("move")
	@args({ IKeyword.SPEED, IKeyword.HEADING, "bounds" })
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

	@action("follow")
	@args({ IKeyword.SPEED, "path" })
	public IPath primFollow(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		IPath pathFollowed = null;
		GamaList<IShape> edges = new GamaList();
		ILocation location = agent.getLocation();
		double dist = computeDistance(scope, agent);

		GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		if ( path != null && !path.getEdgeList().isEmpty() ) {
			pathFollowed = moveToNextLocAlongPathSimplified(agent, path, dist);
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

	@action("goto")
	@args({ "target", IKeyword.SPEED, "on", "return_path" })
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
		if ( path == null || !path.getTopology().equals(topo) || !path.getEndVertex().equals(goal) || !( path.getStartVertex().equals(source))) {
			path = topo.pathBetween(source, goal);
		}

		if ( path == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		if (returnPath != null && returnPath) {
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
		return null ;
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

	private IPath moveToNextLocAlongPathSimplified(final IAgent agent, final IPath path,
		final double d) {
		int index = 0;
		int indexSegment = 1;
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		double distance = d;
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

			currentLocation = (GamaPoint) Points.opClosestPointTo(currentLocation, line);
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
		GamaPoint falseTarget = (GamaPoint) Points.opClosestPointTo(path.getEndVertex(), lineEnd);
		int endIndexSegment = 1;
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
		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for ( int j = indexSegment; j < coords.length; j++ ) {
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = agent.getTopology().distanceBetween(pt, currentLocation);
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

	private IPath moveToNextLocAlongPath(final IAgent agent, final IPath path, final double d)
		throws GamaRuntimeException {
		int index = 0;
		int indexSegment = 1;
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		GamaPoint startLocation = (GamaPoint) agent.getLocation().copy();
		IList<IShape> edges = path.getEdgeList();
		Coordinate[] temp = new Coordinate[2];
		int nb = edges.size();
		// instead of getGeometries() ?? Faster, more reliable. But is it the same ?
		double distance = d;
		GamaList<IShape> segments = new GamaList();
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

			currentLocation = (GamaPoint) Points.opClosestPointTo(currentLocation, line);
			Point pointGeom = (Point) currentLocation.getInnerGeometry();
			if ( line.getInnerGeometry().getNumPoints() >= 3 ) {
				distanceS = Double.MAX_VALUE;
				Coordinate coords[] = line.getInnerGeometry().getCoordinates();
				int nbSp = coords.length;
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
		GamaPoint falseTarget = (GamaPoint) Points.opClosestPointTo(path.getEndVertex(), lineEnd);
		int endIndexSegment = 1;
		Point pointGeom = (Point) falseTarget.getInnerGeometry();
		if ( lineEnd.getInnerGeometry().getNumPoints() >= 3 ) {
			double distanceT = Double.MAX_VALUE;
			Coordinate coords[] = lineEnd.getInnerGeometry().getCoordinates();
			int nbSp = coords.length;
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
		GamaMap agents = new GamaMap();
		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			// The weight computed here is absolutely useless.. since getWeight() returns the
			// perimeter.
			// double weight = path.getWeight(line) / line.getGeometry().getPerimeter();
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for ( int j = indexSegment; j < coords.length; j++ ) {
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = agent.getTopology().distanceBetween(pt, currentLocation);
				// dist = weight * dist;
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
		followedPath.setAgents(agents);
		agent.setLocation(currentLocation);
		return followedPath;
	}

	/**
	 * @throws GamaRuntimeException Prim: move to the nearest named object (can be an agent or a GIS
	 *             object) of a type .
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
	/*
	 * @action("goto")
	 * 
	 * @args({ "target", SPEED, ISymbol.AGENT, "geometry", "graph", "matrix", "triangulation",
	 * "square_size" })
	 * public GamaPoint primMoveTo(final IScope scope) throws GamaRuntimeException {
	 * final Object target = scope.getArg("target");
	 * final IAgent agent = getCurrentAgent(scope);
	 * if ( target == null || !(target instanceof ILocation) ||
	 * ((ILocation) target).getLocation() == null ) {
	 * scope.setStatus(ExecutionStatus.failure);
	 * return agent.getLocation();
	 * }
	 * final Double s = scope.hasArg(SPEED) ? Casting.asFloat(scope,scope.getArg(SPEED)) : null;
	 * if ( s != null ) {
	 * setSpeed(scope, agent, s);
	 * }
	 * final double maxDist = getSpeed(agent) * getTimeStep(scope);
	 * 
	 * final IAgent entity = Cast.asAgent(scope.getArg(ISymbol.AGENT));
	 * final GamaList geomList = Cast.asList(scope.getArg("geometry"));
	 * GamaGeometry geom = null;
	 * if ( entity != null ) {
	 * geom = entity.getGeometry();
	 * }
	 * if ( geom == null && !geomList.isEmpty() ) {
	 * geom = new GamaGeometry(GeometricFunctions.buildGeometryJTS(geomList));
	 * }
	 * final ITopology env = getTopology(agent);
	 * 
	 * final GamaSpatialGraph graph = (GamaSpatialGraph) Cast.asGraph(scope.getArg("graph"));
	 * 
	 * final GamaIntMatrix matrix = (GamaIntMatrix) Cast.asMatrix(scope.getArg("matrix"));
	 * 
	 * GamaPoint loc = null;
	 * final GamaPoint goal = ((ILocation) target).getLocation();
	 * if ( graph == null && matrix == null && geom == null ) {
	 * GamaPoint source = agent.getLocation();
	 * if ( env.distanceBetween(source, goal) <= maxDist ) {
	 * loc = goal;
	 * } else {
	 * final int wantedDirection = env.directionInDegreesTo(source, goal);
	 * loc = env.getDestination(source, wantedDirection, maxDist, true);
	 * }
	 * scope.setStatus(loc == null ? ExecutionStatus.failure : ExecutionStatus.success);
	 * if ( loc != null ) {
	 * agent.setLocation(loc);
	 * return loc;
	 * }
	 * return agent.getLocation();
	 * }
	 * 
	 * final Boolean triangulation =
	 * scope.hasArg("triangulation") ? Cast.asBool(scope.getArg("triangulation")) : null;
	 * final Double squareSize =
	 * scope.hasArg("square_size") ? Casting.asFloat(scope,scope.getArg("square_size")) : null;
	 * 
	 * GamaPoint cs =
	 * gotoTargetInsideGeom(scope, goal, geom, graph, matrix, triangulation, squareSize);
	 * return cs;
	 * 
	 * }
	 */

	/**
	 * Method used by square discretisation pathfinder to find the valid neighborhood of a location
	 * 
	 * @param i index of the x position of the current position
	 * @param j index of the y position of the current position
	 * @param matrix representing the background geometry
	 * @param iMax max i index of the matrix
	 * @param jMax max j index of the matrix
	 * @return the "valid" neighborhood of the current position (Van Neuman)
	 */
	private Set<IntPoint> getNeighs(final int i, final int j, final GamaIntMatrix matrix,
		final int iMax, final int jMax) {
		Set<IntPoint> neighb = new HashSet<IntPoint>();
		if ( i > 0 && matrix.get(i - 1, j) == Integer.MAX_VALUE ) {
			neighb.add(new IntPoint(i - 1, j));
		}
		if ( j > 0 && matrix.get(i, j - 1) == Integer.MAX_VALUE ) {
			neighb.add(new IntPoint(i, j - 1));
		}
		if ( i < iMax && matrix.get(i + 1, j) == Integer.MAX_VALUE ) {
			neighb.add(new IntPoint(i + 1, j));
		}
		if ( j < jMax && matrix.get(i, j + 1) == Integer.MAX_VALUE ) {
			neighb.add(new IntPoint(i, j + 1));
		}
		return neighb;
	}

	/**
	 * Method used by square discretisation pathfinder to obtain the best path
	 * 
	 * @param cpt current distance to the target (in number of cells)
	 * @param i index of the x position of the current position
	 * @param j index of the y position of the current position
	 * @param matrix representing the background geometry
	 * @param iMax max i index of the matrix
	 * @param jMax max j index of the matrix
	 * @return the next position of the shortest path
	 * @return
	 */
	private IntPoint getNeighDesc(final int cpt, final int i, final int j,
		final GamaIntMatrix matrix, final int iMax, final int jMax) {
		if ( i > 0 && matrix.get(i - 1, j) == cpt ) { return new IntPoint(i - 1, j); }
		if ( j > 0 && matrix.get(i, j - 1) == cpt ) { return new IntPoint(i, j - 1); }
		if ( i < iMax && matrix.get(i + 1, j) == cpt ) { return new IntPoint(i + 1, j); }
		if ( j < jMax && matrix.get(i, j + 1) == cpt ) { return new IntPoint(i, j + 1); }
		return null;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Find the next location toward a target in a geometry using a triangulation
	 * 
	 * @param geom background geometry
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	private ILocation findnextLocTowardPolygonUsingTriangulation(final IScope scope, IPath path,
		final Polygon geom, final ILocation source, final ILocation target, final double distance)
		throws GamaRuntimeException {
		if ( path == null ) {
			IList<Polygon> triangles = new GamaList(GeometryUtils.triangulation(geom));
			GamaGraph graph = buildPolygonGraph(triangles);
			// graph.setPolygon(true);
			path = GamaPathType.pathBetween(scope, source, target, graph);
		}
		if ( path == null ) { return null; }
		ILocation gp = nextLocationTriangle(scope, path, source, target, distance);
		// path.setSource(gp);
		// Pas possible de changer la source d'un path... Cela est-il nécessaire ?
		getCurrentAgent(scope).setAttribute("currentPath", path);
		return gp;
	}

	/**
	 * Find the next location toward a target in a geometry from a graph
	 * 
	 * @param graphGeom graph representing the background geometry
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	// private GamaPoint findnextLocTowardPolygon(final IScope scope, GamaPath path,
	// final GamaGraph graph, final GamaPoint source, final GamaPoint target, final double distance)
	// {
	// if ( path == null ) {
	// path = GeometricFunctions.pathBetween(scope, source, target, graph);
	// }
	// if ( path == null ) { return null; }
	// GamaPoint gp = nextLocationTriangle(scope, path, source, target, distance);
	// // path.setSource(gp);
	// // Pas possible de changer la source d'un path... Cela est-il nécessaire ?
	// getCurrentAgent(scope).setAttribute("currentPath", path);
	//
	// return gp;
	// }

	/**
	 * @throws GamaRuntimeException Find the next location toward a target in a network (lineString)
	 * 
	 * @param geom background geometry
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	private ILocation findnextLocTowardNetwork(final IScope scope, final GamaPath path,
		final MultiLineString lines, final ILocation source, final ILocation target,
		final double distance) throws GamaRuntimeException {
		GamaList<LineString> linesList = new GamaList<LineString>();
		int nb = lines.getNumGeometries();
		for ( int i = 0; i < nb; i++ ) {
			linesList.add((LineString) lines.getGeometryN(i));
		}
		return findnextLocTowardNetwork(scope, path, linesList, source, target, distance);
	}

	/**
	 * @throws GamaRuntimeException Find the next location toward a target in a network (lineString)
	 * 
	 * @param geom background geometry
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	private ILocation findnextLocTowardNetwork(final IScope scope, IPath path,
		final IList<LineString> lines, final ILocation source, final ILocation target,
		final double distance) throws GamaRuntimeException {
		if ( path == null ) {
			GamaSpatialGraph graph = buildNetworkGraph(scope, lines, true);
			// graph.setPolygon(false);
			path = GamaPathType.pathBetweenPoints(scope, source, target, graph);
		}
		if ( path == null ) { return null; }
		ILocation gp = nextLocationLine(scope, path, source, target, distance);
		return gp;
	}

	/**
	 * @throws GamaRuntimeException Find the next location toward a target in a network (lineString)
	 *             from a graph
	 * 
	 * @param graph graph representing the background geometry
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	private ILocation findnextLocTowardNetwork(final IScope scope, IPath path,
		final GamaSpatialGraph graph, final ILocation source, final ILocation target,
		final double distance) throws GamaRuntimeException {
		if ( path == null ) {
			path = GamaPathType.pathBetweenPoints(scope, source, target, graph);
			// System.out.println("PATH COMPUTED : " + path);
		}
		if ( path == null ) { return null; }
		ILocation gp = nextLocationLine(scope, path, source, target, distance);
		// path.setSource(gp);
		// Pas possible de changer la source d'un path... Cela est-il nécessaire ?
		getCurrentAgent(scope).setAttribute("currentPath", path);
		return gp;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Find the next location toward a target in a geometry from a path
	 * 
	 * @param path path to reach the target
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	public ILocation nextLocationLine(final IScope scope, final IPath path, ILocation source,
		final ILocation target, double distance) {
		double distRemain = distance;

		if ( path == null || path.getEdgeList().isEmpty() ) {
			double distInit = target.euclidianDistanceTo(source);
			if ( distInit < distRemain ) { return target; }
			double ratio = distRemain / distInit;
			double x_s = source.getX();
			double y_s = source.getY();
			double x_t = target.getX();
			double y_t = target.getY();
			x_s = x_s + ratio * (x_t - x_s);
			y_s = y_s + ratio * (y_t - y_s);
			return new GamaPoint(x_s, y_s);
		}
		// System.out.println("edges : " + path.getEdges().size() + " -> " + path.getEdges());
		// System.out.println("nodes : " + path.getNodes().size() + " -> " + path.getNodes()) ;

		if ( path.getVertexList().isEmpty() ) { return findnextLocTowardLine(scope, path
			.getEdgeList().get(0).getInnerGeometry().getCoordinates(), source, target,
			new Distance(distRemain)); }
		ILocation ns = path.getStartVertex();

		/*
		 * double distInit = path.getDistS(); if ( distInit < distRemain ) { distRemain -= distInit;
		 * } else { return findnextLocTowardLine(path.getCoordsS(), source, (Point) ns.getGeom(),
		 * distRemain); }
		 */
		Distance distD = new Distance(distance);
		// System.out.println("AVANT : distance : " + distance);
		GamaPoint pt =
			findnextLocTowardLine(scope, path.getEdgeList().get(0).getInnerGeometry()
				.getCoordinates(), source, ns.getLocation(), distD);
		if ( distD.val == 0 ) { return pt; }
		distance = distD.val;
		// System.out.println("APRES : distance : " + distance);
		distRemain = distance;
		source = ns.getLocation();
		GamaPoint nt = null;
		List<GamaPoint> nodes = new GamaList<GamaPoint>(path.getVertexList());
		int nb = nodes.size();
		for ( int i = 1; i < nb; i++ ) {
			// path.getNodes().remove(0);
			// Impossible de retirer un noeud de cette façon... Est-ce nécessaire ?
			nt = nodes.get(i);
			IShape edge = (GamaShape) path.getGraph().getEdge(ns, nt);
			path.getEdgeList().remove(0);
			double dist = path.getGraph().getEdgeWeight(edge);
			if ( dist < distRemain ) {
				Coordinate[] coords = edge.getInnerGeometry().getCoordinates();
				return findnextLocTowardLine(scope, coords, ns.getLocation(), nt.getLocation(),
					new Distance(distRemain));
			} else if ( dist < distRemain ) {
				ns = nt;
				distRemain -= dist;
				// path.getEdges().remove(0);
				source = ns.getLocation();
			} else {
				return nt.getLocation();
			}

		}
		if ( distRemain > 0 ) {
			// path.getNodes().remove(0);
			// Impossible de retirer un noeud de cette façon... Est-ce nécessaire ?
			path.getEdgeList().remove(0);

			// double dist = path.getDistT();
			// if ( dist <= distRemain ) { return new GamaPoint(target.getCoordinate()); }
			// System.out.println("ICI ! : " + distRemain);
			// System.out.println("source : " + source + "  target : " + target + " edge : " +
			// path.getEdges().get(path.getEdges().size() - 1).getGeometry());
			return findnextLocTowardLine(scope, path.getEdgeList().get(0).getGeometry()
				.getInnerGeometry().getCoordinates(), source, target, new Distance(distRemain));
		}
		return null;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Find the next location toward a target in a geometry from a path
	 * 
	 * @param path path to reach the target
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */
	public ILocation nextLocationTriangle(final IScope scope, final IPath path, ILocation source,
		final ILocation target, final double distance) {
		// ITopology m = scope.getAgentScope().getTopology();
		ILocation loc = null;
		Graph<IShape, IShape> g = path.getGraph();
		double distRemain = distance;
		List<IShape> edges = new GamaList(path.getEdgeList());
		int nb = edges.size();

		for ( int i = 0; i < nb; i++ ) {
			ILocation targ = null;
			IShape geom = edges.get(i);
			IShape ga = g.getEdgeSource(geom);
			IShape gb = g.getEdgeTarget(geom);
			/*
			 * Geometry geom = ga.intersection(gb); geom = BasicTransformations.homothetie(geom,
			 * 0.9);
			 */
			if ( i == nb - 1 ) {
				targ = Points.opClosestPointTo(geom, target);
			} else {
				if ( !ga.intersects(source) ) {
					targ = Points.opClosestPointTo(geom, ga.getLocation());
				} else {
					targ = Points.opClosestPointTo(geom, gb.getLocation());
				}
			}
			double dist = targ.euclidianDistanceTo(source);
			if ( dist > distRemain ) {
				double ratio = distRemain / dist;
				double x_s = source.getX();
				double y_s = source.getY();
				double x_t = targ.getX();
				double y_t = targ.getY();
				x_s = x_s + ratio * (x_t - x_s);
				y_s = y_s + ratio * (y_t - y_s);
				return new GamaPoint(x_s, y_s);

			} else if ( dist < distRemain ) {
				source = targ;
				path.getEdgeList().remove(geom);
				distRemain -= dist;
			} else {
				path.getEdgeList().remove(geom);
				return targ;
			}

		}
		if ( distRemain > 0 ) {
			double dist = target.euclidianDistanceTo(source);
			if ( dist <= distRemain ) { return target; }
			double ratio = distRemain / dist;
			double x_s = source.getX();
			double y_s = source.getY();
			double x_t = target.getX();
			double y_t = target.getY();
			x_s = x_s + ratio * (x_t - x_s);
			y_s = y_s + ratio * (y_t - y_s);
			return new GamaPoint(x_s, y_s);
		}
		return loc;
	}

	private static class Distance {

		double val;

		public Distance(final double val) {
			super();
			this.val = val;
		}

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
	private GamaPoint findnextLocTowardLine(final IScope scope, final Coordinate[] coords,
		final ILocation source, final ILocation target, final Distance distance) {
		IAgent agent = getCurrentAgent(scope);
		ITopology m = scope.getAgentScope().getTopology();
		int nb = coords.length;
		double x_s = source.getX();
		double y_s = source.getY();
		double x_t = target.getX();
		double y_t = target.getY();
		int indexSource = -1;
		int indexTarget = -1;
		double distanceS = Double.MAX_VALUE;
		double distanceT = Double.MAX_VALUE;
		for ( int i = 0; i < nb - 1; i++ ) {
			Coordinate s = coords[i];
			Coordinate t = coords[i + 1];
			Coordinate[] seg = { s, t };
			IShape segment = new GamaShape(GeometryUtils.getFactory().createLineString(seg));
			double distS = m.distanceBetween(segment, source);
			if ( distS < distanceS ) {
				distanceS = distS;
				indexSource = i;
			}
			double distT = m.distanceBetween(segment, target);
			if ( distT < distanceT ) {
				distanceT = distT;
				indexTarget = i;
			}
		}
		// System.out.println("source : " + source + "  target : " + target);

		// System.out.println("indexSource : " + indexSource + "  indexTarget : " + indexTarget);
		int nbSp = Math.abs(indexSource - indexTarget) + 2;
		Coordinate[] coordsSimp = new Coordinate[nbSp];
		coordsSimp[0] = source.toCoordinate();
		if ( indexSource > indexTarget ) {
			int k = 1;
			for ( int i = indexSource; i >= indexTarget; i-- ) {
				coordsSimp[k] = coords[i];
				k++;
			}
		} else {
			int k = 1;
			for ( int i = indexSource + 1; i <= indexTarget + 1; i++ ) {
				coordsSimp[k] = coords[i];
				k++;
			}
		}
		// System.out.println("coordsSimp[" + 0 + "]" + coordsSimp[0]);
		for ( int i = 1; i < nbSp; i++ ) {
			// System.out.println("coordsSimp[" + i + "]" + coordsSimp[i]);
			Coordinate t = coordsSimp[i];
			double x_t_it = t.x;
			double y_t_it = t.y;
			if ( i == nbSp - 1 ) {
				x_t_it = x_t;
				y_t_it = y_t;
			}
			double dist = Math.sqrt(Maths.pow(x_s - x_t_it, 2) + Maths.pow(y_s - y_t_it, 2));
			if ( distance.val < dist ) {
				double ratio = distance.val / dist;
				x_s = x_s + ratio * (x_t_it - x_s);
				y_s = y_s + ratio * (y_t_it - y_s);
				distance.val = 0;
				break;
			} else if ( distance.val > dist ) {
				x_s = x_t_it;
				y_s = y_t_it;
				distance.val = distance.val - dist;
			} else {
				x_s = x_t_it;
				y_s = y_t_it;
				distance.val = 0;
				break;
			}
		}
		return new GamaPoint(Math.min(x_s, getTopology(agent).getWidth()), Math.min(y_s,
			getTopology(agent).getHeight()));
	}

	/**
	 * @param cl the line
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return true if the line contains both the source and the target
	 */
	private boolean isGoodLineString(final LineString cl, final ILocation source,
		final ILocation target, final double distance) {
		return cl.intersects(GeometryUtils.getFactory().createPoint(source.toCoordinate())
			.buffer(distance / 1000.0)) &&
			cl.intersects(GeometryUtils.getFactory().createPoint(target.toCoordinate())
				.buffer(distance / 1000.0));
	}

	/**
	 * @param cp the polygon
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return true if the polygon contains both the source and the target
	 */
	private boolean isGoodPolygon(final IScope scope, final IShape cp, final ILocation source,
		final ILocation target) {
		return cp.intersects(source) && cp.intersects(target);
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
		ILocation computedPt = Points.opClosestPointTo(loc, new GamaShape(frontier));
		if ( computedPt != null ) { return computedPt; }
		return getCurrentAgent(scope).getLocation();
	}

	//
	// protected Integer getTimeStep(final IScope scope) {
	// return scope.getSimulationScope().getScheduler().getStep();
	// }

	/**
	 * @author Patrick Simple Class used by the square discretisation pathfinder
	 */
	public class IntPoint {

		public int i;
		public int j;

		public IntPoint(final int i, final int j) {
			super();
			this.i = i;
			this.j = j;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + i;
			result = prime * result + j;
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if ( this == obj ) { return true; }
			if ( obj == null ) { return false; }
			if ( getClass() != obj.getClass() ) { return false; }
			IntPoint other = (IntPoint) obj;
			if ( !getOuterType().equals(other.getOuterType()) ) { return false; }
			if ( i != other.i ) { return false; }
			if ( j != other.j ) { return false; }
			return true;
		}

		private GeometricSkill getOuterType() {
			return MovingSkill.this;
		}

		@Override
		public String toString() {
			return "[" + i + "," + j + "]";
		}

	}

}
