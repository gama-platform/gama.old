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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.path;

import java.util.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.GamaGeometryType;
import com.vividsolutions.jts.geom.*;

// Si construit � partir d'une liste de points, cr�e la g�om�trie correspondante
// Si construit � partir d'un graphe spatial, cr�e la g�om�trie � partir des edges pass�s.
// Si

public class GamaSpatialPath extends GamaPath<IShape, IShape> {

	GamaList<IShape> segments;
	IShape shape = null;
	Map<IShape, IShape> realObjects; // cle = bout de geometrie

	// WARNING Cant hide an attribute like this !
	// GamaSpatialGraph graph;

	public GamaSpatialPath(final GamaSpatialGraph g, final IShape start, final IShape target, final IList<IShape> _edges) {
		super(g, start, target, _edges);
		this.init(g, start, target, _edges, true);
	}

	public GamaSpatialPath(final GamaSpatialGraph g, final IShape start, final IShape target,
		final IList<IShape> _edges, final boolean modify_edges) {
		super(g, start, target, _edges, modify_edges);
		this.init(g, start, target, _edges, modify_edges);
	}

	public GamaSpatialPath(final IShape start, final IShape target, final IList<IShape> edges) {
		super(null, start, target, edges, false);
		this.init(null, start, target, edges, false);
	}

	public void init(final GamaSpatialGraph g, final IShape start, final IShape target, final IList<IShape> _edges,
		final boolean modify_edges) {
		source = start;
		this.target = target;
		this.graph = g;
		this.segments = new GamaList<IShape>();

		realObjects = new HashMap<IShape, IShape>();
		graphVersion = 0;

		Geometry firstLine = _edges == null || _edges.isEmpty() ? null : _edges.get(0).getInnerGeometry();
		Coordinate pt = null;
		GamaPoint pt0 = firstLine == null ? null : new GamaPoint(firstLine.getCoordinates()[0]);
		GamaPoint pt1 =
			firstLine == null ? null : new GamaPoint(firstLine.getCoordinates()[firstLine.getNumPoints() - 1]);
		if ( firstLine != null ) {
			if ( _edges.size() > 1 ) {
				IShape secondLine = _edges.get(1).getGeometry();
				pt = pt0.euclidianDistanceTo(secondLine) > pt1.euclidianDistanceTo(secondLine) ? pt0 : pt1;
			} else {
				final IShape lineEnd = edges.get(edges.size()-1);
				GamaPoint falseTarget = (GamaPoint) Punctal._closest_point_to(((IShape) getEndVertex()).getLocation(), lineEnd);
				
				pt = start.euclidianDistanceTo(pt0) < falseTarget.euclidianDistanceTo(pt0) ? pt0 : pt1;
			}
			GamaSpatialGraph graph = this.getGraph();
			if ( graph != null ) {
				graphVersion = graph.getVersion();
			}
			int cpt = 0;
			for ( IShape edge : _edges ) {
				if ( modify_edges ) {
					IAgent ag = edge.getAgent();
					Geometry geom = edge.getInnerGeometry();
					Coordinate c0 = geom.getCoordinates()[0];
					Coordinate c1 = geom.getCoordinates()[geom.getNumPoints() - 1];
					IShape edge2 = null;
					if ( !g.isDirected() && pt.distance(c0) > pt.distance(c1) ) {
						geom = geom.reverse();
						edge2 = new GamaShape(geom);
						pt = c0;
					} else {
						edge2 = edge;
						pt = c1;
					}
					if (cpt == 0 && ! source.equals(pt)) {
						GamaPoint falseSource = (GamaPoint) Punctal._closest_point_to(source, edge2);
						edge2 = GeometryUtils.split_at(edge2, falseSource).get(1);
					}
					if ((cpt == _edges.size() - 1) && ! target.equals(edge2.getInnerGeometry().getCoordinates()[geom.getNumPoints() - 1])) {
						GamaPoint falseTarget = (GamaPoint) Punctal._closest_point_to(target, edge2);
						edge2 = GeometryUtils.split_at(edge2, falseTarget).get(0);
					}
					if ( ag != null && graph != null && graph.isAgentEdge() ) {
						realObjects.put(edge2, ag);
					} else {
						realObjects.put(edge2, edge);
					}
					segments.add(edge2);
					
				} else {
					segments.add(edge);
				}
				cpt++;
				// segmentsInGraph.put(agents, agents);
			}
		}
	}

	public GamaSpatialPath(final GamaSpatialGraph g, final List<IShape> nodes) {
		// FIXME call super super(param...);
		if ( nodes.isEmpty() ) {
			source = new GamaPoint(0, 0);
			target = source;
		} else {
			source = nodes.get(0);
			target = nodes.get(nodes.size() - 1);
		}
		segments = new GamaList<IShape>();
		realObjects = new GamaMap<IShape, IShape>();
		graph = g;

		for ( int i = 0, n = nodes.size(); i < n - 1; i++ ) {
			segments.add(GamaGeometryType.buildLine(nodes.get(i).getLocation(), nodes.get(i + 1).getLocation()));
			IAgent ag = nodes.get(i).getAgent();
			if ( ag != null ) {
				// MODIF: put?
				realObjects.put(nodes.get(i).getGeometry(), ag);
			}
		}
		IAgent ag = nodes.get(nodes.size() - 1).getAgent();
		if ( ag != null ) {
			// MODIF: put?
			realObjects.put(nodes.get(nodes.size() - 1).getGeometry(), ag);
		}
	}

	// /////////////////////////////////////////////////
	// Implements methods from IValue

	@Override
	public GamaSpatialPath copy(final IScope scope) {
		return new GamaSpatialPath(getGraph(), source, target, segments);
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
	// GamaList<IShape> ags = new GamaList<IShape>();
	// ags.addAll(new HashSet<IShape>(realObjects.values()));
	// return ags;
	// }

	@Override
	public IList<IShape> getEdgeGeometry() {
		// GamaList<IShape> ags = new GamaList<IShape>();
		// ags.addAll(new HashSet<IShape>(realObjects.values()));
		// return ags;
		return segments;
	}

	//
	// /**
	// * Private method intended to compute the geometry of the path (a polyline) from the list of
	// * segments.
	// * While the path is not invalidated, this list of segments should not be changed and the
	// * geometry can be cached.
	// */
	// private void computeGeometry() {
	// if ( super.getInnerGeometry() == null ) {
	// try {
	// setGeometry(GamaGeometryType.geometriesToGeometry(null, segments)); // Verify null
	// // parameter
	// } catch (GamaRuntimeException e) {
	// GAMA.reportError(e);
	// e.printStackTrace();
	// }
	// // Faire une methode geometriesToPolyline ? linesToPolyline ?
	// }
	// }

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
		if ( getEdgeList() == null || getEdgeList().isEmpty() ) { return Double.MAX_VALUE; }
		Coordinate[] coordsSource = getEdgeList().get(0).getInnerGeometry().getCoordinates();
		Coordinate[] coordsTarget = getEdgeList().get(getEdgeList().size() - 1).getInnerGeometry().getCoordinates();
		if ( coordsSource.length == 0 || coordsTarget.length == 0 ) { return Double.MAX_VALUE; }
		GamaPoint sourceEdges = new GamaPoint(coordsSource[0]);
		GamaPoint targetEdges = new GamaPoint(coordsTarget[coordsTarget.length - 1]);
		boolean keepSource = source.getLocation().equals(sourceEdges);
		boolean keepTarget = target.getLocation().equals(targetEdges);
		if ( keepSource && keepTarget ) {
			double d = 0d;
			for ( IShape g : segments ) {
				d += g.getInnerGeometry().getLength();
			}
			return d;
		}
		return getDistanceComplex(scope, keepSource, keepTarget);
	}

	private double getDistanceComplex(final IScope scope, final boolean keepSource, final boolean keepTarget) {
		double distance = 0;
		int index = 0;
		int indexSegment = 1;
		ILocation currentLocation = source.getLocation().copy(scope);
		IList<IShape> _edges = getEdgeList();
		int nb = _edges.size();
		if ( !keepSource ) {
			double distanceS = Double.MAX_VALUE;
			IShape line = null;
			for ( int i = 0; i < nb; i++ ) {
				line = _edges.get(i);
				double distS = line.euclidianDistanceTo(currentLocation);
				if ( distS < distanceS ) {
					distanceS = distS;
					index = i;
				}
			}
			line = _edges.get(index);
			currentLocation = Punctal._closest_point_to(currentLocation, line);
			Point pointGeom = (Point) currentLocation.getInnerGeometry();
			if ( line.getInnerGeometry().getNumPoints() >= 3 ) {
				distanceS = Double.MAX_VALUE;
				Coordinate coords[] = line.getInnerGeometry().getCoordinates();
				int nbSp = coords.length;
				Coordinate[] temp = new Coordinate[2];
				for ( int i = 0; i < nbSp - 1; i++ ) {
					temp[0] = coords[i];
					temp[1] = coords[i + 1];
					LineString segment = GeometryUtils.factory.createLineString(temp);
					double distS = segment.distance(pointGeom);
					if ( distS < distanceS ) {
						distanceS = distS;
						indexSegment = i + 1;
					}
				}
			}
		}
		IShape lineEnd = _edges.get(nb - 1);
		int endIndexSegment = lineEnd.getInnerGeometry().getNumPoints();
		GamaPoint falseTarget = new GamaPoint(target.getLocation());
		if ( !keepTarget ) {
			falseTarget = (GamaPoint) Punctal._closest_point_to(getEndVertex(), lineEnd);
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
					LineString segment = GeometryUtils.factory.createLineString(temp);
					double distT = segment.distance(pointGeom);
					if ( distT < distanceT ) {
						distanceT = distT;
						endIndexSegment = i + 1;
					}
				}
			}
		}
		for ( int i = index; i < nb; i++ ) {
			IShape line = _edges.get(i);
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();

			for ( int j = indexSegment; j < coords.length; j++ ) {
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
				double dist = currentLocation.euclidianDistanceTo(pt);
				currentLocation = pt;
				distance = distance + dist;
				if ( i == nb - 1 && j == endIndexSegment ) {
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
	public ITopology getTopology() {
		if (graph == null) {
			return null;
		}
		return ((GamaSpatialGraph) graph).getTopology();
	}

	@Override
	public void setRealObjects(final Map<IShape, IShape> realObjects) {
		this.realObjects = realObjects;
	}

	@Override
	public IShape getRealObject(final Object obj) {
		return realObjects.get(obj);
	}
	
	@Override
	public IShape getGeometry() {
		if (shape == null && segments.size() > 0) {
			final Geometry geoms[] = new Geometry[segments.size()];
			int cpt = 0;
			for ( final IShape ent : segments ) {
				geoms[cpt] = ent.getInnerGeometry();
				cpt++;
			}
			final Geometry geom = GeometryUtils.factory.createGeometryCollection(geoms);
			shape = new GamaShape(geom.union());
		}
		return shape;
	}
}
