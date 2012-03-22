package msi.gaml.extensions.traffic;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.ExecutionStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPath;
import msi.gama.util.IList;
import msi.gama.util.IPath;
import msi.gaml.operators.Spatial.Points;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

@skill("driving")
public class DrivingSkill extends MovingSkill{
	@action("gotoTraffic")
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

	private void moveToNextLocAlongPathSimplified(final IAgent agent, final IPath path,
		final double _distance) {
		int index = 0;
		int indexSegment = 1;
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy();
		
		IList<IShape> edges = path.getEdgeList();
		int nb = edges.size();
		double distance = _distance;
		//This block is used to find the current location of the agent on the
		//graph and the target location
		if ( path.isVisitor(agent) ) {
			index = path.indexOf(agent);
			indexSegment = path.indexSegmentOf(agent);
		} else {
			path.acceptVisitor(agent);
			double distanceS = Double.MAX_VALUE;
			IShape line = null;
			//Retrieving the nerarest edge
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
			//Retrieving the nearest segment of the nearest edge (line var)
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
		//The target should be on an element placed outside the graph, 
		// the nearest point to the target is chosen on the graph
		GamaPoint falseTarget = (GamaPoint) Points.opClosestPointTo(path.getEndVertex(), lineEnd);
		int endIndexSegment = 1;
		Point pointGeom = (Point) falseTarget.getInnerGeometry();
		if ( lineEnd.getInnerGeometry().getNumPoints() >= 3 ) {
			double distanceT = Double.MAX_VALUE;
			Coordinate coords[] = lineEnd.getInnerGeometry().getCoordinates();
			int nbSp = coords.length;
			Coordinate[] temp = new Coordinate[2];
			//Retrieving the last segment index
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

		//Given the current location and the target location
		//the agent moves
		GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();
		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			//current edge
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			//weight is 1 by default, otherwise is the distributed edge's weight by length unity
			double weight =
				graph == null ? 1 : graph.getEdgeWeight(path.getRealObject(line)) / line.getGeometry().getPerimeter();
			//
			for ( int j = indexSegment; j < coords.length; j++ ) {
				// pt is the next target
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					//The agents has arrived to the target, and he is located in the
					// nearest location to the real target on the graph
					pt = falseTarget;
				} else {
					//otherwise is the extremity of the segment
					pt = new GamaPoint(coords[j]);
				}
				//distance from current location to next target
				double dist = agent.getTopology().distanceBetween(pt, currentLocation);
				//For the while, for a high weight, the vehicle moves slowly
				dist = weight * dist;
				//Collision avoiding
				//1. Determines the agents located on a dist radius circle from the current location
				IList<IAgent> neighbours = agent.getTopology().getNeighboursOf(agent, Math.min(dist, distance), Different.with());
				//2. Selects the agents before the agent on the segment 
				Coordinate[] segment = {currentLocation, pt};
				double minDist = distance;
				Geometry frontRectangle = GeometryUtils.getFactory().createLinearRing(segment).buffer(0.1, 4, /**TODO To be modified, to find the right constant name**/2);
				for(IAgent ia : neighbours){
					if(ia.getSpecies().equals(line.getAgent().getSpecies())){
						continue;
					}
					if(frontRectangle.intersects(ia.getInnerGeometry())){
						double currentDistance = agent.getTopology().distanceBetween(agent, ia);
						if(currentDistance < distance){
							minDist = currentDistance;
						}
					}
						
				}
				//3. Determines the distance to the nearest agent in front of him
				distance = minDist;
				// that's the real distance to move
				//Agent moves
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
			//The current edge is over, agent moves to the next one
		}
		if ( currentLocation.equals(falseTarget) ) {
			currentLocation = (GamaPoint) path.getEndVertex();
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);
		agent.setLocation(currentLocation);
		path.setSource(currentLocation.copy());

	}

	private IPath moveToNextLocAlongPath(final IAgent agent, final IPath path, final double d) {
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
			// perimeter. // ANSWER : it is necessary because the weight can be different than the
			// perimeter (see model traffic_tutorial)
			GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();
			
			double weight =
				graph == null ? 1 : graph.getEdgeWeight(path.getRealObject(line)) / line.getGeometry().getPerimeter();
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
	
}
