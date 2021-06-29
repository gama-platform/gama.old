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


import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.util.SupplierUtil;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.loader.GraphParsers;
import msi.gaml.operators.Spatial;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

public abstract class GamaGraphFile extends GamaFile<IGraph<?, ?>, Object> {
	ISpecies nodeS = null;
	ISpecies edgeS = null;
	
	public GamaGraphFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	
	public GamaGraphFile(final IScope scope, final String pathName, final ISpecies nodeSpecies) {
		super(scope, pathName);
		nodeS = nodeSpecies;
		System.out.println("ldldldl");
	}

	public GamaGraphFile(final IScope scope, final String pathName,final ISpecies nodeSpecies, final ISpecies edgeSpecies) {
		super(scope, pathName);
		nodeS = nodeSpecies;
		edgeS = edgeSpecies;
		System.out.println("dldldldl");
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}
	
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		System.out.println("lalalala");
		GraphImporter parser = GraphParsers.getGraphImporter(getFileType());
		 DirectedMultigraph<String, DefaultEdge> graph = new DirectedMultigraph<>(
		            SupplierUtil.createStringSupplier(), SupplierUtil.DEFAULT_EDGE_SUPPLIER, true);
		       
		parser.importGraph(graph, this.getFile(scope));
		GamaGraph<Object, Object> g = new GamaGraph(scope,nodeS == null ?Types.STRING : Types.AGENT,edgeS == null ?Types.STRING : Types.AGENT);
		Map<String, IAgent> verticesAg = new HashMap();
		for (Object v : graph.vertexSet()) {
			if (nodeS == null)
				g.addVertex(v.toString());
			else {
				IList atts = GamaListFactory.create();
				 final IList<IAgent> listAgt =(IList<IAgent>) nodeS.getPopulation(scope).createAgents(scope, 1,atts,false, false, null);
				 IAgent ag = listAgt.get(0);
				 if (v != null) ag.setName(v.toString());
				 g.addVertex(ag);
				 verticesAg.put(v.toString(), ag);
			}
		}
		for (DefaultEdge e : graph.edgeSet()) {
			Object s = graph.getEdgeSource(e);
			Object t = graph.getEdgeTarget(e);
			
			if (edgeS == null) {
				g.addEdge(s, t, e);
				g.setEdgeWeight(e, graph.getEdgeWeight(e));
			} else {
				IList atts = GamaListFactory.create();
				final IList<IAgent> listAgt =(IList<IAgent>) edgeS.getPopulation(scope).createAgents(scope, 1,atts,false, true, null);
				IAgent ag = listAgt.get(0);
				if(e != null) ag.setName(e.toString());
				 
				if (nodeS != null) {
					IAgent n1 = verticesAg.get(s.toString());
					IAgent n2 = verticesAg.get(t.toString());
					 g.addEdge(n1, n2, ag);
					ag.setGeometry(Spatial.Creation.link(scope, n1,n2));
				} else
					 g.addEdge(s, t, ag);
				 
				g.setEdgeWeight(ag, graph.getEdgeWeight(e));
			}
			
			
		}
			
		setBuffer(g);
	}
	
	abstract protected String getFileType() ;

	@Override
	public IContainerType<?> getGamlType() {
		return Types.GRAPH;
	}

}
