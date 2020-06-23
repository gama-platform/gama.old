/*******************************************************************************************************
 *
 * msi.gama.util.path.GamaPath.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.path;

import org.jgrapht.GraphPath;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

// Si construit � partir d'une liste de points, cr�e la g�om�trie correspondante
// Si construit � partir d'un graphe spatial, cr�e la g�om�trie � partir des edges pass�s.
// Si
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaPath<V, E, G extends IGraph<V, E>> implements Comparable, GraphPath<V, E>, IPath<V, E, G> {

	V source, target;
	IList<E> edges;

	double weight = 0.0;

	// The graph attribute is override in GamaSpatialPath by a GamaSpatialGraph
	G graph;
	int graphVersion;

	// FIXME virer le constructeur par d�faut... used for the inheritance...
	public GamaPath() {}

	@Override
	public IType getGamlType() {
		return Types.PATH;
	}

	public GamaPath(final G g, final V start, final V target, final IList<? extends E> _edges) {
		init(g, start, target, _edges, true);
		this.graph = g;
	}

	public GamaPath(final G g, final V start, final V target, final IList<? extends E> _edges,
			final boolean modify_edges) {
		init(g, start, target, _edges, modify_edges);
		this.graph = g;
	}

	public GamaPath(final IList<? extends V> nodes) {
		final IList<E> _edges = GamaListFactory.create();
		for (int i = 0; i < nodes.size() - 1; i++) {
			final E edge = createEdge(nodes.get(i), nodes.get(i + 1));
			if (edge != null) {
				_edges.add(edge);
			}
		}
		init(null, nodes.get(0), nodes.get(nodes.size() - 1), _edges, false);
		this.graph = null;
	}

	protected E createEdge(final V v, final V v2) {
		// TODO to define !
		return null;
	}

	public void init(final G g, final V start, final V target, final IList<? extends E> _edges,
			final boolean modify_edges) {
		this.source = start;
		this.target = target;
		this.edges = GamaListFactory.create();
		graphVersion = 0;

		if (_edges != null && _edges.size() > 0) {
			for (final E edge : _edges) {
				edges.add(edge);
			}
		}
	}

	public GamaPath(final G g, final IList<? extends V> nodes) {
		if (!(g instanceof GamaSpatialGraph) && nodes.isEmpty()) {
			throw new ClassCastException("We cannot create an empty path in a non-spatial graph");
		} else if (nodes.isEmpty()) {
			source = null;
			target = null;
		} else {
			source = nodes.get(0);
			target = nodes.get(nodes.size() - 1);
		}
		edges = GamaListFactory.create();

		for (int i = 0, n = nodes.size(); i < n - 1; i++) {
			edges.add(g.getEdge(nodes.get(i), nodes.get(i + 1)));
		}
		graph = g;
	}

	// /////////////////////////////////////////////////
	// Implements methods from GraphPath

	@Override
	public G getGraph() {
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

	public void setWeight(final double weight) {
		this.weight = weight;
	}

	@Override
	public double getWeight() {
		final G graph = getGraph();
		if (graph == null) { return weight; }
		return graph.computeWeight(this);
	}

	// /////////////////////////////////////////////////
	// Implements methods from IValue

	@Override
	public String stringValue(final IScope scope) {
		return serialize(false);
	}

	@Override
	public GamaPath copy(final IScope scope) {
		return new GamaPath(graph, source, target, edges);
	}

	// /////////////////////////////////////////////////
	// Implements methods from IPath

	// @Override
	// public IList<IShape> getAgentList() {
	// GamaList<IShape> ags = GamaListFactory.create(Types.GEOMETRY);
	// ags.addAll(new HashSet<IShape>(realObjects.values()));
	// return ags;
	// }

	@Override
	public IList<V> getVertexList() {
		if (graph == null) { return GamaListFactory.EMPTY_LIST; }
		return GamaListFactory.<V> wrap(getGamlType().getKeyType(), GraphPath.super.getVertexList());
	}

	// TODO :to check
	@Override
	public double getWeight(final IShape line) throws GamaRuntimeException {
		return line.getGeometry().getPerimeter(); // workaround for the moment
	}

	/**
	 * Private method intended to compute the geometry of the path (a polyline) from the list of segments. While the
	 * path is not invalidated, this list of segments should not be changed and the geometry can be cached.
	 */
	// FIXME BEN
	// private void computeGeometry() {
	// if ( super.getInnerGeometry() == null ) {
	// try {
	// setGeometry(GamaGeometryType.geometriesToGeometry(null, segments)); //
	// Verify null
	// // parameter
	// } catch (GamaRuntimeException e) {
	// GAMA.reportError(e);
	// e.printStackTrace();
	// }
	// // Faire une methode geometriesToPolyline ? linesToPolyline ?
	// }
	// }

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
	public String serialize(final boolean includingBuiltIn) {
		return "(" + getEdgeList().serialize(includingBuiltIn) + ") as path";
	}

	@Override
	public int getLength() {
		return edges.size();
	}

	@Override
	public double getDistance(final IScope scope) {
		if (getEdgeList() == null || getEdgeList().isEmpty()) { return 0; }
		return getWeight();
	}

	@Override
	public ITopology getTopology(final IScope scope) {
		return graph instanceof GamaSpatialGraph ? ((GamaSpatialGraph) graph).getTopology(scope) : null;
	}

	@Override
	public void setRealObjects(final IMap<IShape, IShape> realObjects) {
		;
	}

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

	@Override
	public IShape getGeometry() {
		return null;
	}

	@Override
	public void setGraph(final G graph) {
		this.graph = graph;
		graphVersion = graph.getVersion();

	}

	@Override
	public int compareTo(final Object o) {
		return (int) (this.getWeight() - ((GamaPath) o).getWeight());
	}
}
