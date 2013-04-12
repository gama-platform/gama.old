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
package msi.gama.util.path;

import java.util.*;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.*;
import org.jgrapht.*;
import org.jgrapht.Graphs;

// Si construit à partir d'une liste de points, crée la géométrie correspondante
// Si construit à partir d'un graphe spatial, crée la géométrie à partir des edges passés.
// Si

public class GamaPath<V,E> implements GraphPath<V,E>, IPath<V,E>, IValue {

	V source, target;
	GamaList<E> edges;

	// The graph attribute is override in GamaSpatialPath by a GamaSpatialGraph
	IGraph<V,E> graph;
	int graphVersion;

	// FIXME virer le constructeur par défaut... used for the inheritance...
	public GamaPath(){}

	public GamaPath(final IGraph<V,E> g, final V start, final V target,
		final IList<E> edges) {
		init(g, start, target, edges, true);
		this.graph = g;
	}

	public GamaPath(final IGraph<V,E> g, final V start, final V target,
		final IList<E> edges, final boolean modify_edges) {
		init(g, start, target, edges, modify_edges);
		this.graph = g;
	}

	public void init(final IGraph<V,E> g, final V start, final V target,
		final IList<E> edges, final boolean modify_edges) {
		this.source = start;
		this.target = target;
		this.edges = new GamaList<E>();
		graphVersion = 0;
		
		if(edges != null && edges.size() > 0){
			for(E edge : edges){
				edges.add(edge);
			}
		}			
	}

	public GamaPath(final IGraph<V,E> g, final List<V> nodes) {
		if(!(g instanceof GamaSpatialGraph) && nodes.isEmpty()){
			throw new ClassCastException("We cannot create an empty path in a non-spatial graph");
		}
		else if ( nodes.isEmpty() ) {
			source = null;
			target = null;
		} else {
			source = nodes.get(0);
			target = nodes.get(nodes.size() - 1);
		}
		edges = new GamaList<E>();

		for(int i = 0, n = nodes.size(); i < n -1; i ++){
			edges.add(g.getEdge(nodes.get(i), nodes.get(i+1)));
		}
		graph = g;
	}

	///////////////////////////////////////////////////
	// Implements methods from GraphPath
	
	@Override
	public IGraph<V,E> getGraph() {
		return graph;
	}

	@Override
	public V getStartVertex() {
		return source;
	}

	@Override
	public V getEndVertex() {
		return target;
	}

	@Override
	public IList<E> getEdgeList() {
		return edges;
	}

	@Override
	public double getWeight() {
		IGraph<V,E> graph = getGraph();
		if ( graph == null ) { return 0.0; }
		return graph.computeWeight(this);
	}	

	///////////////////////////////////////////////////
	// Implements methods from IValue
	
	@Override
	public String stringValue(IScope scope) {
		return toGaml();
	}
	
	@Override
	public GamaPath<V,E> copy(IScope scope) {
		return new GamaPath<V,E>(graph, source, target, edges);
	}
	
	///////////////////////////////////////////////////
	// Implements methods from IPath
	
//	@Override
//	public IList<IShape> getAgentList() {
//		GamaList<IShape> ags = new GamaList<IShape>();
//		ags.addAll(new HashSet<IShape>(realObjects.values()));
//		return ags;
//	}

	@Override
	public IList<V> getVertexList() {
		return new GamaList<V>(Graphs.getPathVertexList(this));
		// return getPoints();
	}
	
	
	// TODO  :to check 
	@Override
	public double getWeight(final IShape line) throws GamaRuntimeException {
		return line.getGeometry().getPerimeter(); // workaround for the moment
	}

	/**
	 * Private method intended to compute the geometry of the path (a polyline) from the list of
	 * segments.
	 * While the path is not invalidated, this list of segments should not be changed and the
	 * geometry can be cached.
	 */
	// FIXME BEN
//	private void computeGeometry() {
//		if ( super.getInnerGeometry() == null ) {
//			try {
//				setGeometry(GamaGeometryType.geometriesToGeometry(null, segments)); // Verify null
//																					// parameter
//			} catch (GamaRuntimeException e) {
//				GAMA.reportError(e);
//				e.printStackTrace();
//			}
//			// Faire une methode geometriesToPolyline ? linesToPolyline ?
//		}
//	}

	@Override
	public String toString() {
		return "path between " + getStartVertex().toString() + " and " + getEndVertex().toString();
	}
	
	@Override
	// FIXME
	public void acceptVisitor(final IAgent agent) {
		agent.setAttribute("current_path", this); // ???
	}

	@Override
	// FIXME
	public void forgetVisitor(final IAgent agent) {
		agent.setAttribute("current_path", null); // ???
	}

	@Override
	// FIXME
	public int indexOf(final IAgent a) {
		return Cast.asInt(null, a.getAttribute("index_on_path")); // ???
	}

	@Override
	// FIXME
	public int indexSegmentOf(final IAgent a) {
		return Cast.asInt(null, a.getAttribute("index_on_path_segment")); // ???
	}

	@Override
	// FIXME
	public boolean isVisitor(final IAgent a) {
		return a.getAttribute("current_path") == this;
	}

	@Override
	// FIXME
	public void setIndexOf(final IAgent a, final int index) {
		a.setAttribute("index_on_path", index);
	}

	@Override
	// FIXME
	public void setIndexSegementOf(final IAgent a, final int indexSegement) {
		a.setAttribute("index_on_path_segment", indexSegement);
	}

	@Override
	public String toGaml() {
		return "(" + edges.toGaml() + ") as path";
	}

	@Override
	public int getLength() {
		return edges.size();
	}

	@Override
	// FIXME est-ce pertinent ?
	public double getDistance(IScope scope) {
		if ( getEdgeList() == null || getEdgeList().isEmpty() ) { return Double.MAX_VALUE; }
		return getWeight();
	}
	
	@Override
	public ITopology getTopology() {
		return (graph instanceof GamaSpatialGraph) ? ((GamaSpatialGraph) graph).getTopology() : null;
	}

	@Override
	public void setRealObjects(final Map<IShape,IShape> realObjects) {;}

	@Override
	public IShape getRealObject(final Object obj) {
		return null;
	}	

	@Override
	public void setSource(final V source) {
		this.source = source;
	}

	@Override
	public void setTarget(final V target) {
		this.target = target;
	}

	@Override
	public int getGraphVersion() {
		return graphVersion;
	}

	@Override
	public IList<IShape> getEdgeGeometry() {
		return null;
	}
}
