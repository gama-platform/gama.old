/**
 * Created by drogoul, 24 nov. 2011
 * 
 */
package msi.gama.util.graph;

import java.util.Map;
import msi.gama.environment.ITopology;
import msi.gama.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.GamaList;
import org.jgrapht.*;

/**
 * Written by drogoul
 * Modified on 24 nov. 2011
 * 
 * An interface for the different kinds of graphs encountered in GAML
 * 
 */
@vars({ @var(name = "spanning_tree", type = IType.LIST_STR),
	@var(name = "circuit", type = IType.PATH_STR), @var(name = "connected", type = IType.BOOL_STR),
	@var(name = "edges", type = IType.LIST_STR), @var(name = "vertices", type = IType.LIST_STR) })
public interface IGraph<K, V> extends IGamaContainer<K, V>, WeightedGraph, DirectedGraph,
	UndirectedGraph {

	public abstract double getVertexWeight(final Object v);

	public abstract Double getWeightOf(final Object v);

	public abstract void setVertexWeight(final Object v, final double weight);

	public abstract IValue computeShortestPathBetween(final ITopology topology,
		final Object source, final Object target);

	void setWeights(Map<?, Double> weights);

	@getter(var = "edges")
	public abstract GamaList getEdges();

	@getter(var = "vertices")
	public abstract GamaList getVertices();

	@getter(var = "spanning_tree")
	public abstract GamaList getSpanningTree();

	@getter(var = "circuit")
	public abstract GamaPath getCircuit();

	@getter(var = "connected")
	public abstract Boolean getConnected();

	public abstract boolean isDirected();

	public abstract void setDirected(final boolean b);

}