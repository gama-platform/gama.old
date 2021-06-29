/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGraphFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.util.SupplierUtil;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.loader.GraphParsers;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

public abstract class GamaGraphFile extends GamaFile<IGraph<?, ?>, Object> {
	
	
	public GamaGraphFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	public GamaGraphFile(final IScope scope, final String pathName, final IGraph<?, ?> container) {
		super(scope, pathName, container);
		
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}
	
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		GraphImporter parser = GraphParsers.getGraphImporter(getFileType());
		 DirectedMultigraph<String, DefaultEdge> graph = new DirectedMultigraph<>(
		            SupplierUtil.createStringSupplier(), SupplierUtil.DEFAULT_EDGE_SUPPLIER, true);
		       
		parser.importGraph(graph, this.getFile(scope));
		GamaGraph<Object, Object> g = new GamaGraph(scope,Types.STRING,Types.STRING);
		for (Object v : graph.vertexSet())
			g.addVertex(v.toString());
		for (DefaultEdge e : graph.edgeSet()) {
			Object s = graph.getEdgeSource(e);
			Object t = graph.getEdgeTarget(e);
			g.addEdge(s, t, e);
			g.setEdgeWeight(e, graph.getEdgeWeight(e));
		}
			
		setBuffer(g);
	}
	
	abstract protected String getFileType() ;

	@Override
	public IContainerType<?> getGamlType() {
		return Types.GRAPH;
	}

}
