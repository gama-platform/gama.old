/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.graph;

import java.util.*;
import msi.gama.environment.ITopology;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import org.geotools.graph.build.*;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.structure.*;

/**
 * Written by drogoul
 * Modified on 24 nov. 2011
 * 
 * @todo Description
 * 
 */
public abstract class __GamaGeotoolsGraphWrapper implements IGraph<GamaGeometry, GamaGeometry> {

	private static final double DEFAULT_EDGE_WEIGHT = 0;
	// protected Graph graph;
	// protected GraphBuilder builder;
	protected GraphGenerator generator;
	protected boolean edgeBased;
	protected Map<Graphable, Double> weights;
	LineStringGraphGenerator gg;

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#type()
	 */
	@Override
	public IType type() {
		return Types.get(IType.GRAPH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue() throws GamaRuntimeException {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#toGaml()
	 */
	@Override
	public String toGaml() {
		return null;
		// A voir. En fonction de "byEdge"
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#toJava()
	 */
	@Override
	public String toJava() {
		return null;
		// A voir.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#copy()
	 */
	@Override
	public Object copy() throws GamaRuntimeException {
		try {

			return ((GraphBuilder) generator.getGraphBuilder().clone(true)).getGraph();
			// Seems to be a little bit complicated...
		} catch (Exception e) {
			throw new GamaRuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GamaGeometry> iterator() {
		return edgeBased ? generator.getGraph().getEdges().iterator() : generator.getGraph()
			.getNodes().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#get(java.lang.Object)
	 */
	@Override
	public GamaGeometry get(final GamaGeometry index) throws GamaRuntimeException {
		Graphable g = generator.get(index);
		if ( g != null ) { return (GamaGeometry) g.getObject(); }
		return null;
		// Actually, since the index is the geometry itself... it will return the geometry or null
		// if it is not in the graph. Not sure this function has any interest here.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) throws GamaRuntimeException {
		return generator.get(o) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#first()
	 */
	@Override
	public GamaGeometry first() throws GamaRuntimeException {
		return iterator().hasNext() ? iterator().next() : null;
		// Does not mean anything in this context.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#last()
	 */
	@Override
	public GamaGeometry last() throws GamaRuntimeException {
		return null;
		// Does not mean anything in this context. Or the last "added" ?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#length()
	 */
	@Override
	public int length() {
		return edgeBased ? generator.getGraph().getEdges().size() : generator.getGraph().getNodes()
			.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#max()
	 */
	@Override
	public GamaGeometry max() throws GamaRuntimeException {
		double max = -Double.MAX_VALUE;
		Graphable maxObj = null;
		for ( Map.Entry<Graphable, Double> entry : weights.entrySet() ) {
			if ( entry.getValue() > max ) {
				max = entry.getValue();
				maxObj = entry.getKey();
			}
		}
		return (GamaGeometry) maxObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#min()
	 */
	@Override
	public GamaGeometry min() throws GamaRuntimeException {
		double min = Double.MAX_VALUE;
		Graphable minObj = null;
		for ( Map.Entry<Graphable, Double> entry : weights.entrySet() ) {
			if ( entry.getValue() < min ) {
				min = entry.getValue();
				minObj = entry.getKey();
			}
		}
		return (GamaGeometry) minObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#product()
	 */
	@Override
	public Object product() throws GamaRuntimeException {
		Double result = 0.0;
		for ( Map.Entry<Graphable, Double> entry : weights.entrySet() ) {
			result *= entry.getValue();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#sum()
	 */
	@Override
	public Object sum() throws GamaRuntimeException {
		Double result = 0.0;
		for ( Map.Entry<Graphable, Double> entry : weights.entrySet() ) {
			result += entry.getValue();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return generator.getGraph().getNodes().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#reverse()
	 */
	@Override
	public IContainer<GamaGeometry, GamaGeometry> reverse() throws GamaRuntimeException {
		return this;
		// No time to write this. Besides, graphs are not supposed to be directed all the time...
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return value instanceof GamaGeometry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final GamaGeometry index, final boolean forAdding) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException {
		final double[] weights = new double[value.length()];
		if ( param instanceof Number ) {
			Arrays.fill(weights, ((Number) param).doubleValue());
		} else if ( param instanceof List && ((List) param).size() == value.length() ) {
			for ( int i = 0, n = value.length(); i < n; i++ ) {
				weights[i] = Cast.asFloat(((List) param).get(i));
			}
		}
		int i = 0;
		for ( Object obj : value ) {
			add((GamaGeometry) obj, weights[i]);
			i++;
			// Is that all ? Seems strange. Must probably derive my own generator to build
			// LineSegments for networks ? And polygons for the others ?
		}
	}

	// NE FAIRE QUE DES AJOUTS DE GEOMETRIE
	//
	// LAISSER GEOTOOLS GERER LES "RELATIONS" ENTRE GEOMETRIES
	//
	// NE PAS ACCEPTER LES "PAIRES" DE GEOMETRIES COMME DES EDGES. SOIT ON RAJOUTE DES LIGNES, SOIT
	// DES POLYGONES.
	//
	// LA FONCTION DE RELATION PEUT ËTRE AFFECTEE PAR LA DECLARATION DU GRAPHE:
	//
	// list_of_geoms as_intersecting_graph tolerance ? -> node based, polygons only.
	// list_of_geoms as_network_graph tolerance ? -> edge based, lines only.
	// list_of_geoms as_distance_graph distance ?
	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final GamaGeometry index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		addAll(value, param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add(final GamaGeometry value, final Object param) throws GamaRuntimeException {
		add(null, value, param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#add(java.lang.Object, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void add(final GamaGeometry index, final GamaGeometry value, final Object param)
		throws GamaRuntimeException {
		double weight =
			param instanceof Number ? ((Number) param).doubleValue() : DEFAULT_EDGE_WEIGHT;
		add(value, weight);
	}

	/**
	 * @param value
	 * @return
	 */
	protected abstract void add(GamaGeometry value, double weight);

	protected abstract void remove(GamaGeometry value);

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#removeFirst(java.lang.Object)
	 */
	@Override
	public boolean removeFirst(final GamaGeometry value) throws GamaRuntimeException {
		return removeAt(value) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#removeAll(msi.gama.interfaces.IGamaContainer)
	 */
	@Override
	public boolean removeAll(final IContainer<?, GamaGeometry> value)
		throws GamaRuntimeException {
		boolean removed = true;
		for ( GamaGeometry o : value ) {
			removed = removed && removeFirst(o);
		}
		return removed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#removeAt(java.lang.Object)
	 */
	@Override
	public Object removeAt(final GamaGeometry index) throws GamaRuntimeException {
		Graphable g = generator.remove(index);
		if ( g != null ) {
			weights.remove(g);
			return g.getObject();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#putAll(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void putAll(final GamaGeometry value, final Object param) throws GamaRuntimeException {
		add(value, param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#put(java.lang.Object, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void put(final GamaGeometry index, final GamaGeometry value, final Object param)
		throws GamaRuntimeException {
		add(index, value, param); // ??
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#clear()
	 */
	@Override
	public void clear() throws GamaRuntimeException {
		try {
			generator.setGraphBuilder((GraphBuilder) generator.getGraphBuilder().clone(false));
		} catch (Exception e) {
			throw new GamaRuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#listValue(msi.gama.interfaces.IScope)
	 */
	@Override
	public GamaList listValue(final IScope scope) throws GamaRuntimeException {
		return _listValue(scope);
	}

	/**
	 * @param scope
	 * @return
	 */
	protected abstract GamaList _listValue(IScope scope);

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#matrixValue(msi.gama.interfaces.IScope)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return null;
		// Regarder les méthodes permettant de passer le graphe en matrice.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#matrixValue(msi.gama.interfaces.IScope,
	 * msi.gama.util.GamaPoint)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final GamaPoint preferredSize)
		throws GamaRuntimeException {
		return null;
		// Regarder les méthodes permettant de passer le graphe en matrice.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#mapValue(msi.gama.interfaces.IScope)
	 */
	@Override
	public GamaMap mapValue(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#edgesOf(java.lang.Object)
	 */
	@Override
	public Set edgesOf(final Object vertex) {
		Graphable g = generator.get(vertex);
		HashSet s = new HashSet();
		if ( g instanceof Node ) {
			List l = ((Node) g).getEdges();
			for ( Object o : l ) {
				s.add(((Graphable) o).getObject());
			}
			return s;
		}
		return Collections.EMPTY_SET;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getEdgeSource(java.lang.Object)
	 */
	@Override
	public Object getEdgeSource(final Object e) {
		Graphable g = generator.get(e);
		if ( g instanceof Edge ) { return ((Edge) g).getNodeA().getObject(); }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getEdgeTarget(java.lang.Object)
	 */
	@Override
	public Object getEdgeTarget(final Object e) {
		Graphable g = generator.get(e);
		if ( g instanceof Edge ) { return ((Edge) g).getNodeB().getObject(); }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getEdgeWeight(java.lang.Object)
	 */
	@Override
	public double getEdgeWeight(final Object e) {
		Graphable g = generator.get(e);
		if ( g instanceof Edge ) { return weights.get(g); }
		return 0d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getVertexWeight(java.lang.Object)
	 */
	@Override
	public double getVertexWeight(final Object v) {
		Graphable g = generator.get(v);
		if ( g instanceof Node ) { return weights.get(g); }
		return 0d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#incomingEdgesOf(java.lang.Object)
	 */
	@Override
	public Set incomingEdgesOf(final Object vertex) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#inDegreeOf(java.lang.Object)
	 */
	@Override
	public int inDegreeOf(final Object vertex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#outDegreeOf(java.lang.Object)
	 */
	@Override
	public int outDegreeOf(final Object vertex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#degreeOf(java.lang.Object)
	 */
	@Override
	public int degreeOf(final Object v) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#outgoingEdgesOf(java.lang.Object)
	 */
	@Override
	public Set outgoingEdgesOf(final Object vertex) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#setEdgeWeight(java.lang.Object, double)
	 */
	@Override
	public void setEdgeWeight(final Object e, final double weight) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#setVertexWeight(java.lang.Object, double)
	 */
	@Override
	public void setVertexWeight(final Object v, final double weight) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#computeShortestPathBetween(msi.gama.interfaces.IScope,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	public IValue computeShortestPathBetween(final ITopology scope, final Object source,
		final Object target) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#setWeights(java.util.Map)
	 */
	@Override
	public void setWeights(final Map weights) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getEdges()
	 */
	@Override
	public GamaList getEdges() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getVertices()
	 */
	@Override
	public GamaList getVertices() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getSpanningTree()
	 */
	@Override
	public GamaList getSpanningTree() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getCircuit()
	 */
	@Override
	public GamaPath getCircuit() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#getConnected()
	 */
	@Override
	public Boolean getConnected() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#isDirected()
	 */
	@Override
	public boolean isDirected() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#setDirected(boolean)
	 */
	@Override
	public void setDirected(final boolean b) {}

}
