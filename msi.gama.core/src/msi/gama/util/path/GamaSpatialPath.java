/*******************************************************************************************************
 *
 * msi.gama.util.path.GamaSpatialPath.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.path;

import static java.lang.Math.min;
import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.getLastPointOf;
import static msi.gama.common.geometry.GeometryUtils.getPointsOf;
import static msi.gama.common.geometry.GeometryUtils.split_at;
import static msi.gaml.operators.Spatial.Punctal._closest_point_to;

import org.apache.commons.lang.ArrayUtils;
import org.jgrapht.Graph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaSpatialPath extends GamaPath<IShape, IShape, IGraph<IShape, IShape>> {

	IList<IShape> segments;
	IShape shape = null;
	boolean threeD = false;
	IMap<IShape, IShape> realObjects; // key = part of the geometry

	public GamaSpatialPath(final GamaSpatialGraph g, final IShape start, final IShape target,
			final IList<IShape> _edges) {
		super(g, start, target, _edges);
	}

	public GamaSpatialPath(final GamaSpatialGraph g, final IShape start, final IShape target,
			final IList<IShape> _edges, final boolean modify_edges) {
		super(g, start, target, _edges, modify_edges);
	}

	public GamaSpatialPath(final IShape start, final IShape target, final IList<? extends IShape> edges) {
		super(null, start, target, edges, false);
	}

	public GamaSpatialPath(final IShape start, final IShape target, final IList<? extends IShape> edges,
			final boolean modify_edges) {
		super(null, start, target, edges, modify_edges);
	}

	public GamaSpatialPath(final IList<IShape> nodes) {
		super(nodes);
	}

	@Override
	protected IShape createEdge(final IShape v, final IShape v2) {
		return GamaGeometryType.buildLine(v.getLocation(), v2.getLocation());
	}

	@Override
	public void init(final IGraph<IShape, IShape> g, final IShape start, final IShape target,
			final IList<? extends IShape> _edges, final boolean modify_edges) {
		super.init(g, start, target, _edges, modify_edges);
		source = start;
		this.target = target;
		this.graph = g;
		this.segments = GamaListFactory.create(Types.GEOMETRY);
		realObjects = GamaMapFactory.createUnordered();
		graphVersion = 0;
		final Geometry firstLine = _edges == null || _edges.isEmpty() ? null : _edges.get(0).getInnerGeometry();
		GamaPoint pt = null, pt0 = null, pt1 = null;
		if (firstLine != null) {
			final GamaPoint[] firstLinePoints = GeometryUtils.getPointsOf(firstLine);
			pt0 = firstLinePoints[0];
			pt1 = firstLinePoints[firstLinePoints.length - 1];
		}
		if (firstLine != null && _edges != null && pt0 != null && pt1 != null) {
			if (_edges.size() > 1) {
				final double Z = pt0.z;
				for (final IShape e : _edges) {
					for (final GamaPoint p : GeometryUtils.getPointsOf(e)) {
						if (p.z != Z) {
							threeD = true;
							break;
						}
						if (threeD) {
							break;
						}
					}
				}
				final IShape secondLine = _edges.get(1).getGeometry();
				if (threeD) {
					pt =  _edges.get(1).getPoints().contains(pt0) ? pt1 : pt0;
				} else {
					pt = pt0.euclidianDistanceTo(secondLine) > pt1.euclidianDistanceTo(secondLine) ? pt0 : pt1;
				}
			} else {
				final IShape lineEnd = edges.get(edges.size() - 1);
				final GamaPoint falseTarget = (GamaPoint) _closest_point_to(getEndVertex().getLocation(), lineEnd);
				pt = start.euclidianDistanceTo(pt0) < falseTarget.euclidianDistanceTo(pt0) ? pt0 : pt1;
			}
			if (graph != null) {
				graphVersion = graph.getVersion();
			}
			int cpt = 0;
			for (final IShape edge : _edges) {
				if (modify_edges) {
					final IAgent ag = edge instanceof IAgent ? (IAgent) edge : null;
					final GamaPoint[] points = getPointsOf(edge);
					final Geometry geom = edge.getInnerGeometry();
					Geometry geom2;
					final GamaPoint c0 = points[0];
					final GamaPoint c1 = points[points.length - 1];
					IShape edge2 = null;
					final GamaPoint[] coords = getContourCoordinates(geom).toCoordinateArray().clone();
					if ((g == null || !g.isDirected()) && pt.euclidianDistanceTo(c0) > pt.euclidianDistanceTo(c1)) {
						ArrayUtils.reverse(coords);
						pt = c0;
					} else {
						pt = c1;
					}
					final ICoordinates cc = GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(coords, false);
					geom2 = GEOMETRY_FACTORY.createLineString(cc);
					// geom2 = geom.reverse();
					edge2 = new GamaShape(geom2);
					boolean threeDGeom = false;
					if (threeD) {
						double zz = edge2.getPoints().get(0).getZ();
						for (int i = 1; i < edge2.getPoints().size();i++) {
							if (edge2.getPoints().get(i).getZ() != zz) {
								threeDGeom = true;
								break;
							}
						}
					}
					if (!threeDGeom) {
						if (cpt == 0 && !source.equals(pt)) {
							GamaPoint falseSource = source.getLocation().toGamaPoint();
							if (source.euclidianDistanceTo(edge2) > min(0.01, edge2.getPerimeter() / 1000)) {
								falseSource = (GamaPoint) _closest_point_to(source, edge2);
								falseSource.z = zVal(falseSource, edge2);
							}
							edge2 = split_at(edge2, falseSource).get(1);
						}
						if (cpt == _edges.size() - 1 && !target.equals(getLastPointOf(edge2))) {
							GamaPoint falseTarget = target.getLocation().toGamaPoint();
							if (target.euclidianDistanceTo(edge2) > min(0.01, edge2.getPerimeter() / 1000)) {
								falseTarget = (GamaPoint) _closest_point_to(target, edge2);
								falseTarget.z = zVal(falseTarget, edge2);
							}
							edge2 = split_at(edge2, falseTarget).get(0);
						}
					} else {
						if (cpt == 0 && !source.equals(pt)) {
							IList<IShape> pts = GamaListFactory.create(Types.GEOMETRY) ;
							pts.add(source);
							if (edge2.getPoints().size() == 2) {
								pts.add(edge2.getPoints().get(edge2.getPoints().size() - 1));
							} else {
								int index = 0; double distMin = Double.MAX_VALUE;
								for (int i = 0; i < edge2.getPoints().size() -1; i++) {
									ILocation p1 = edge2.getPoints().get(i);
									double dist = p1.euclidianDistanceTo(source);
									if (dist < distMin) {
										index = i;
										distMin = dist;
									}
								}
								for (int i = index+1; i < edge2.getPoints().size(); i++) {
									pts.add(edge2.getPoints().get(i));
								}
							}
							edge2 = Spatial.Creation.line(GAMA.getRuntimeScope(), pts);
						}
						if (cpt == _edges.size() - 1 && !target.equals(getLastPointOf(edge2))) {
							IList<IShape> pts = GamaListFactory.create(Types.GEOMETRY) ;
							pts.add(edge2.getPoints().get(0));
							
							
							if (edge2.getPoints().size() == 2) {
								pts.add(edge2.getPoints().get(0));
							} else {
								int index = 0; double distMin = Double.MAX_VALUE;
								for (int i = 1; i < edge2.getPoints().size(); i++) {
									ILocation p1 = edge2.getPoints().get(i);
									double dist = p1.euclidianDistanceTo(target);
									if (dist < distMin) {
										index = i;
										distMin = dist;
									}
								}
								for (int i = 0; i < index ; i++) {
									pts.add(edge2.getPoints().get(i));
								}
							}
							pts.add(target);
							
							edge2 = Spatial.Creation.line(GAMA.getRuntimeScope(), pts);
						}

					}
					
					if (ag != null) {
						realObjects.put(edge2.getGeometry(), ag);
					} else {
						realObjects.put(edge2.getGeometry(), edge);
					}
					segments.add(edge2.getGeometry());

				} else {
					segments.add(edge.getGeometry());
				}
				cpt++;
				// segmentsInGraph.put(agents, agents);
			}

		}
	}

	protected double zVal(final GamaPoint point, final IShape edge) {
		double z = 0.0;
		final int nbSp = getPointsOf(edge).length;
		final Coordinate[] temp = new Coordinate[2];
		final Point pointGeom = (Point) point.getInnerGeometry();
		double distanceS = Double.MAX_VALUE;
		final GamaPoint[] edgePoints = GeometryUtils.getPointsOf(edge);
		for (int i = 0; i < nbSp - 1; i++) {
			temp[0] = edgePoints[i];
			temp[1] = edgePoints[i + 1];
			final LineString segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(temp);
			final double distS = segment.distance(pointGeom);
			if (distS < distanceS) {
				distanceS = distS;
				final GamaPoint pt0 = new GamaPoint(temp[0]);
				final GamaPoint pt1 = new GamaPoint(temp[1]);
				z = pt0.z + (pt1.z - pt0.z) * point.distance(pt0) / segment.getLength();
			}
		}
		return z;
	}

	public GamaSpatialPath(final GamaSpatialGraph g, final IList<? extends IShape> nodes) {
		// FIXME call super super(param...);
		// DEBUG.OUT("GamaSpatialPath nodes: " + nodes);
		if (nodes.isEmpty()) {
			source = new GamaPoint(0, 0);
			target = source;
		} else {
			source = nodes.get(0);
			target = nodes.get(nodes.size() - 1);
		}
		segments = GamaListFactory.<IShape> create(Types.GEOMETRY);
		realObjects = GamaMapFactory.createUnordered();
		graph = g;

		for (int i = 0, n = nodes.size(); i < n - 1; i++) {
			final IShape geom = GamaGeometryType.buildLine(nodes.get(i).getLocation(), nodes.get(i + 1).getLocation());
			segments.add(geom);

			final IAgent ag = nodes.get(i).getAgent();
			if (ag != null) {
				// MODIF: put?
				realObjects.put(nodes.get(i).getGeometry(), ag);
			}
		}
		final IAgent ag = nodes.isEmpty() ? null : nodes.get(nodes.size() - 1).getAgent();
		if (ag != null) {
			// MODIF: put?
			realObjects.put(nodes.get(nodes.size() - 1).getGeometry(), ag);
		}
	}

	// /////////////////////////////////////////////////
	// Implements methods from IValue

	@Override
	public GamaSpatialPath copy(final IScope scope) {
		return new GamaSpatialPath(getGraph(), source, target, edges);
	}

	@Override
	public GamaSpatialGraph getGraph() {
		return (GamaSpatialGraph) graph;
	}

	// /////////////////////////////////////////////////
	// Implements methods from IPath
	//
	// @Override
	// public IList<IShape> getAgentList() {
	// GamaList<IShape> ags = GamaListFactory.create(Types.GEOMETRY);
	// ags.addAll(new HashSet<IShape>(realObjects.values()));
	// return ags;
	// }

	@Override
	public IList getEdgeGeometry() {
		// GamaList<IShape> ags = GamaListFactory.create(Types.GEOMETRY);
		// ags.addAll(new HashSet<IShape>(realObjects.values()));
		// return ags;
		return segments;
	}

	@Override
	public void acceptVisitor(final IAgent agent) {
		agent.setAttribute("current_path", this); // ???
	}

	@Override
	public void forgetVisitor(final IAgent agent) {
		agent.setAttribute("current_path", null); // ???
	}

	@Override
	public int indexOf(final IAgent a) {
		return Cast.asInt(null, a.getAttribute("index_on_path")); // ???
	}

	@Override
	public int indexSegmentOf(final IAgent a) {
		return Cast.asInt(null, a.getAttribute("index_on_path_segment")); // ???
	}

	@Override
	public boolean isVisitor(final IAgent a) {
		return a.getAttribute("current_path") == this;
	}

	@Override
	public void setIndexOf(final IAgent a, final int index) {
		a.setAttribute("index_on_path", index);
	}

	@Override
	public void setIndexSegementOf(final IAgent a, final int indexSegement) {
		a.setAttribute("index_on_path_segment", indexSegement);
	}

	@Override
	public double getDistance(final IScope scope) {
		if (segments == null || segments.isEmpty()) { return Double.MAX_VALUE; }
		final Coordinate[] coordsSource = segments.get(0).getInnerGeometry().getCoordinates();
		final Coordinate[] coordsTarget = segments.get(getEdgeList().size() - 1).getInnerGeometry().getCoordinates();
		if (coordsSource.length == 0 || coordsTarget.length == 0) { return Double.MAX_VALUE; }
		final GamaPoint sourceEdges = new GamaPoint(coordsSource[0]);
		final GamaPoint targetEdges = new GamaPoint(coordsTarget[coordsTarget.length - 1]);
		final boolean keepSource = source.getLocation().equals(sourceEdges);
		final boolean keepTarget = target.getLocation().equals(targetEdges);
		if (keepSource && keepTarget) {
			double d = 0d;
			for (final IShape g : segments) {
				d += g.getInnerGeometry().getLength();
			}
			return d;
		}
		return getDistanceComplex(scope, keepSource, keepTarget);
	}

	private double getDistanceComplex(final IScope scope, final boolean keepSource, final boolean keepTarget) {
		double distance = 0;
		int index = 0;
		int indexSegment = 0;
		ILocation currentLocation = source.getLocation().copy(scope);
		final int nb = segments.size();
		if (!keepSource) {
			double distanceS = Double.MAX_VALUE;
			IShape line = null;
			for (int i = 0; i < nb; i++) {
				line = segments.get(i);
				final double distS = line.euclidianDistanceTo(currentLocation);
				if (distS < distanceS) {
					distanceS = distS;
					index = i;
				}
			}
			line = segments.get(index);
			currentLocation = Punctal._closest_point_to(currentLocation, line);
			final Point pointGeom = (Point) currentLocation.getInnerGeometry();
			if (line.getInnerGeometry().getNumPoints() >= 3) {
				distanceS = Double.MAX_VALUE;
				final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
				final int nbSp = coords.length;
				final Coordinate[] temp = new Coordinate[2];
				for (int i = 0; i < nbSp - 1; i++) {
					temp[0] = coords[i];
					temp[1] = coords[i + 1];
					final LineString segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(temp);
					final double distS = segment.distance(pointGeom);
					if (distS < distanceS) {
						distanceS = distS;
						indexSegment = i + 1;
					}
				}
			}
		}
		final IShape lineEnd = segments.get(nb - 1);
		int endIndexSegment = lineEnd.getInnerGeometry().getNumPoints();
		GamaPoint falseTarget = target.getLocation().toGamaPoint();
		if (!keepTarget) {
			falseTarget = (GamaPoint) Punctal._closest_point_to(getEndVertex(), lineEnd);
			endIndexSegment = 1;
			final Point pointGeom = (Point) falseTarget.getInnerGeometry();
			if (lineEnd.getInnerGeometry().getNumPoints() >= 3) {
				double distanceT = Double.MAX_VALUE;
				final Coordinate coords[] = lineEnd.getInnerGeometry().getCoordinates();
				final int nbSp = coords.length;
				final Coordinate[] temp = new Coordinate[2];
				for (int i = 0; i < nbSp - 1; i++) {
					temp[0] = coords[i];
					temp[1] = coords[i + 1];
					final LineString segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(temp);
					final double distT = segment.distance(pointGeom);
					if (distT < distanceT) {
						distanceT = distT;
						endIndexSegment = i + 1;
					}
				}
			}
		}
		for (int i = index; i < nb; i++) {
			final IShape line = segments.get(i);
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for (int j = indexSegment; j < coords.length; j++) {
				GamaPoint pt = null;
				if (i == nb - 1 && j == endIndexSegment) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				final double dist = currentLocation.euclidianDistanceTo(pt);
				currentLocation = pt;
				distance = distance + dist;
				if (i == nb - 1 && j == endIndexSegment) {
					break;
				}
				indexSegment++;
			}
			indexSegment = 1;
			index++;
		}
		return distance;
	}

	@Override
	public ITopology getTopology(final IScope scope) {
		if (graph == null) { return null; }
		return ((GamaSpatialGraph) graph).getTopology(scope);
	}

	@Override
	public void setRealObjects(final IMap<IShape, IShape> realObjects) {
		this.realObjects = realObjects;
	}

	@Override
	public IShape getRealObject(final Object obj) {
		return realObjects.get(obj);
	}

	@Override
	public IShape getGeometry() {

		if (shape == null && segments.size() > 0) {
			if (segments.size() == 1) {
				shape = new GamaShape(segments.get(0));
			} else {
				final IList<IShape> pts = GamaListFactory.create(Types.POINT);
				for (final IShape ent : segments) {
					for (final GamaPoint p : GeometryUtils.getPointsOf(ent)) {
						if (!pts.contains(p)) {
							pts.add(p);
						}
					}
				}
				if (pts.size() > 0) {
					shape = GamaGeometryType.buildPolyline(pts);
				}
			}

		}
		return shape;
	}

	@Override
	public void setGraph(final IGraph<IShape, IShape> graph) {
		this.graph = graph;
		graphVersion = graph.getVersion();

		for (final IShape edge : edges) {
			final IAgent ag = edge.getAgent();
			if (ag != null) {
				realObjects.put(edge.getGeometry(), ag);
			} else {
				realObjects.put(edge.getGeometry(), edge);
			}
		}

	}

	@Override
	public IList<IShape> getEdgeList() {
		if (edges == null) { return segments; }
		return edges;
	}

	@Override
	public IList<IShape> getVertexList() {
		if (graph == null) {
			try (final Collector.AsList<IShape> vertices = Collector.getList()) {
				IShape g = null;
				for (final Object ed : getEdgeList()) {
					g = (IShape) ed;
					vertices.add(GeometryUtils.getFirstPointOf(g));
				}
				if (g != null) {
					vertices.add(GeometryUtils.getLastPointOf(g));
				}
				return vertices.items();
			}
		}
		return getPathVertexList();
	}

	public IList<IShape> getPathVertexList() {
		final Graph<IShape, IShape> g = getGraph();
		try (final Collector.AsList<IShape> list = Collector.getList()) {
			IShape v = getStartVertex();
			list.add(v);
			IShape vPrev = null;
			for (final IShape e : getEdgeList()) {
				vPrev = v;
				v = getOppositeVertex(g, e, v);
				if (!v.equals(vPrev)) {
					list.add(v);
				}
			}
			return list.items();
		}
	}

	public static IShape getOppositeVertex(final Graph<IShape, IShape> g, final IShape e, final IShape v) {
		final IShape source = g.getEdgeSource(e);
		final IShape target = g.getEdgeTarget(e);
		if (v.equals(source)) {
			return target;
		} else if (v.equals(target)) {
			return source;
		} else {
			return v.euclidianDistanceTo(source) > v.euclidianDistanceTo(target) ? target : source;
		}
	}

	public boolean isThreeD() {
		return threeD;
	}

}
