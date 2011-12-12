/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.graph;

import java.util.*;
import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.GamaGeometryType;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import org.jgrapht.*;
import com.vividsolutions.jts.geom.*;

// Si construit à partir d'une liste de points, crée la géométrie correspondante
// Si construit à partir d'un graphe spatial, crée la géométrie à partir des edges passés.
// Si
@vars({ @var(name = GamaPath.TARGET, type = IType.POINT_STR),
	@var(name = GamaPath.SOURCE, type = IType.POINT_STR),
	@var(name = GamaPath.GRAPH, type = IType.GRAPH_STR),
	@var(name = GamaPath.SEGMENTS, type = IType.LIST_STR, of = IType.GEOM_STR),
	@var(name = GamaPath.AGENTS, type = IType.LIST_STR, of = IType.AGENT_STR)
// Could be replaced by "geometries"
/*
 * Normally not necessary as it is inherited from GamaGeometry @var(name = GamaPath.POINTS, type =
 * IType.LIST_STR, of = IType.POINT_STR)
 */
})
public class GamaPath extends GamaGeometry implements GraphPath {

	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String SEGMENTS = "segments";
	public static final String GRAPH = "graph";
	public static final String AGENTS = "agents";

	GamaList<IGeometry> segments;
	GamaMap agents;
	final IGeometry source, target;
	final ITopology topology;
	GamaMap segmentsInGraph;

	public GamaPath(final ITopology t, final IGeometry start, final IGeometry target,
		final List<IGeometry> edges) {
		source = start;
		this.target = target;
		topology = t;
		segments = new GamaList<IGeometry>();
		segmentsInGraph = new GamaMap();
		agents = new GamaMap();

		Geometry firstLine =
			edges == null || edges.isEmpty() ? null : edges.get(0).getInnerGeometry();
		Coordinate pt = null;
		GamaPoint pt0 = firstLine == null ? null : new GamaPoint(firstLine.getCoordinates()[0]);
		GamaPoint pt1 =
			firstLine == null ? null : new GamaPoint(
				firstLine.getCoordinates()[firstLine.getNumPoints() - 1]);
		if ( firstLine != null ) {
			if ( edges.size() > 1 ) {
				IGeometry secondLine = edges.get(1).getGeometry();
				pt =
					pt0.euclidianDistanceTo(secondLine) > pt1.euclidianDistanceTo(secondLine) ? pt0
						: pt1;
			} else {
				pt = start.euclidianDistanceTo(pt0) < target.euclidianDistanceTo(pt0) ? pt0 : pt1;
			}
			for ( IGeometry edge : edges ) {
				IAgent ag = edge.getAgent();
				Geometry geom = edge.getInnerGeometry();
				Coordinate c0 = geom.getCoordinates()[0];
				Coordinate c1 = geom.getCoordinates()[geom.getNumPoints() - 1];
				IGeometry edge2 = null;
				if ( pt.distance(c0) > pt.distance(c1) ) {
					geom = geom.reverse();
					edge2 = new GamaGeometry(geom);
					pt = c0;
				} else {
					edge2 = edge;
					pt = c1;
				}
				if ( ag != null ) {
					agents.put(edge2, ag);
				}

				segments.add(edge2);
				segmentsInGraph.put(edge2, edge);
			}
		}
	}

	public GamaPath(final ITopology t, final List<IGeometry> nodes) {
		if ( nodes.isEmpty() ) {
			source = new GamaPoint(0, 0);
			target = source;
		} else {
			source = nodes.get(0);
			target = nodes.get(nodes.size() - 1);
		}
		segments = new GamaList();
		for ( int i = 0, n = nodes.size(); i < n - 1; i++ ) {
			segments.add(GamaGeometry.buildLine(nodes.get(i).getLocation(), nodes.get(i + 1)
				.getLocation()));
			IAgent ag = nodes.get(i).getAgent();
			if ( ag != null ) {
				agents.putAll(nodes.get(i).getGeometry(), ag);
			}
		}
		IAgent ag = nodes.get(nodes.size() - 1).getAgent();
		if ( ag != null ) {
			agents.putAll(nodes.get(nodes.size() - 1).getGeometry(), ag);
		}
		topology = t;

	}

	@Override
	@getter(var = GamaPath.GRAPH)
	public GamaSpatialGraph getGraph() {
		return topology instanceof GraphTopology ? (GamaSpatialGraph) topology.getPlaces() : null;
	}

	@Override
	@getter(var = GamaPath.SOURCE)
	public GamaPoint getStartVertex() {
		// return GamaPoint ? GamaGeometry ?
		return source.getLocation();
		// if ( graph != null ) { return ((_SpatialVertex)
		// segments.get(0).getSource()).getLocation(); }
		// return new GamaPoint(segments.get(0).geometry.getPoints().first());
		// Double-check this. I'm not sure it has a sense.
	}

	@Override
	@getter(var = GamaPath.TARGET)
	public GamaPoint getEndVertex() {
		// return GamaPoint ? GamaGeometry ?
		return target.getLocation();
		// if ( graph != null ) { return ((_SpatialVertex) segments.get(segments.size() - 1)
		// .getTarget()).getLocation(); }
		// return new GamaPoint(segments.get(segments.size() - 1).geometry.getPoints().last());
		// Double-check this. I'm not sure it has a sense.
	}

	@Override
	@getter(var = GamaPath.SEGMENTS)
	public GamaList<IGeometry> getEdgeList() {
		return segments;
	}

	@getter(var = GamaPath.AGENTS)
	public GamaList<IAgent> getAgentList() {
		GamaList<IAgent> ags = new GamaList<IAgent>();
		ags.addAll(new HashSet<IAgent>(agents.values()));
		return ags;
	}

	public GamaList<GamaPoint> getVertexList() {
		return new GamaList(Graphs.getPathVertexList(this));
		// return getPoints();
	}

	@Override
	public double getWeight() {
		IGraph graph = getGraph();
		if ( graph == null ) { return getPerimeter(); }
		return ((GamaSpatialGraph) graph).computeWeight(this);
	}

	public double getWeight(final IGeometry line) {
		if ( getGraph() == null || !getGraph().containsEdge(segmentsInGraph.get(line)) ) { return line
			.getGeometry().getPerimeter(); }
		return getGraph().getEdgeWeight(segmentsInGraph.get(line));
	}

	@Override
	public Geometry getInnerGeometry() {
		computeGeometry();
		return super.getInnerGeometry();
	}

	@Override
	public GamaPoint getLocation() {
		computeGeometry();
		return super.getLocation();
	}

	/**
	 * Private method intended to compute the geometry of the path (a polyline) from the list of
	 * segments.
	 * While the path is not invalidated, this list of segments should not be changed and the
	 * geometry can be cached.
	 */
	private void computeGeometry() {
		if ( super.getInnerGeometry() == null ) {
			try {
				setGeometry(GamaGeometryType.geometriesToGeometry(segments));
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
				e.printStackTrace();
			}
			// Faire une methode geometriesToPolyline ? linesToPolyline ?
		}
	}

	@Override
	public String toString() {
		return "path between " + getStartVertex().toString() + " and " + getEndVertex().toString();
	}

	@Override
	public GamaPath copy() {
		return new GamaPath(topology, source, target, segments);
	}

	public void acceptVisitor(final IAgent agent) {
		agent.setAttribute("current_path", this); // ???
	}

	public void forgetVisitor(final IAgent agent) {
		agent.setAttribute("current_path", null); // ???
	}

	public int indexOf(final IAgent a) {
		return Cast.asInt(a.getAttribute("index_on_path")); // ???
	}

	public int indexSegmentOf(final IAgent a) {
		return Cast.asInt(a.getAttribute("index_on_path_segment")); // ???
	}

	public boolean isVisitor(final IAgent a) {
		return a.getAttribute("current_path") == this;
	}

	public void setIndexOf(final IAgent a, final int index) {
		a.setAttribute("index_on_path", index);
	}

	public void setIndexSegementOf(final IAgent a, final int indexSegement) {
		a.setAttribute("index_on_path_segment", indexSegement);
	}

	@Override
	public String stringValue() {
		return toGaml();
	}

	@Override
	public String toGaml() {
		return "(" + segments.toGaml() + ") as path";
	}

	public int getLength() {
		return segments.size();
	}

	public double getDistance() {
		double d = 0d;
		for ( IGeometry g : segments ) {
			d += g.getInnerGeometry().getLength();
		}
		return d;
	}

	public ITopology getTopology() {
		return topology;
	}

	public void setAgents(final GamaMap agents) {
		this.agents = agents;
	}

	public IAgent getAgent(final Object obj) {
		return (IAgent) agents.get(obj);
	}

}
