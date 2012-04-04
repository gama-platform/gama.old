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
package msi.gama.util.graph;

import java.util.Collection;
import java.util.Map;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import msi.gaml.types.IType;
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
	@var(name = "edges", type = IType.LIST_STR), @var(name = "vertices", type = IType.LIST_STR), 
	@var(name = "verbose", type = IType.BOOL_STR) })
public interface IGraph<K, V> extends IContainer<K, V>, WeightedGraph, DirectedGraph,
	UndirectedGraph, IGraphEventProvider {

	public abstract double getVertexWeight(final Object v);

	public abstract Double getWeightOf(final Object v);

	public abstract void setVertexWeight(final Object v, final double weight);

	public abstract IValue computeShortestPathBetween(final Object source, final Object target);

	void setWeights(Map<?, Double> weights);

	public Collection _internalEdgeSet();
	public Collection _internalNodesSet();
	
	@getter(var = "edges")
	public abstract IList getEdges();

	@getter(var = "vertices")
	public abstract IList getVertices();

	@getter(var = "spanning_tree")
	public abstract IList getSpanningTree();

	@getter(var = "circuit")
	public abstract IValue getCircuit();

	@getter(var = "connected")
	public abstract Boolean getConnected();

	public abstract boolean isDirected();

	public abstract void setDirected(final boolean b);

	public abstract Object addEdge(Object p);

	public abstract void setOptimizerType(String optiType);
	
	@getter(var = "verbose")
	public abstract Boolean isVerbose();

	public abstract void setVerbose(final Boolean isVerbose);

	
}