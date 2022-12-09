/*******************************************************************************************************
 *
 * GamaGraphMLFile.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;

import java.util.function.Supplier;

import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.graphml.GraphMLImporter;

import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.loader.GamaGraphMLEdgeImporter;
import msi.gama.util.graph.loader.GamaGraphMLNodeImporter;
import msi.gama.util.graph.loader.GraphImporters;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * The Class GamaGraphMLFile.
 */
@file (
		name = "graphml",
		extensions = { "graphml" },
		buffer_type = IType.GRAPH,
		concept = { IConcept.GRAPH, IConcept.FILE },
		doc = @doc ("Represents files that contain Graph information. The internal representation is a graph"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraphMLFile extends GamaGraphFile {
	
	String nodeAttr = null;
	String edgeAttr = null;
	
	/**
	 * Instantiates a new gama graph ML file.
	 *
	 * @param scope the scope
	 * @param pn the pn
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@doc ("References a graphml graph file by its filename")
	public GamaGraphMLFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	/**
	 * Instantiates a new gama graph ML file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param nodeSpecies the node species
	 */
	@doc ("References a graphml graph file by its filename and the species to use to instantiate the nodes")
	public GamaGraphMLFile(final IScope scope, final String pathName, final ISpecies nodeSpecies) {
		super(scope, pathName, nodeSpecies);
	}

	/**
	 * Instantiates a new gama graph ML file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param nodeSpecies the node species
	 * @param edgeSpecies the edge species
	 */
	@doc ("References a graphml graph file by its filename and the 2 species to use to instantiate the nodes and the edges")
	public GamaGraphMLFile(final IScope scope, final String pathName, final ISpecies nodeSpecies,
			final ISpecies edgeSpecies) {
		super(scope, pathName, nodeSpecies, edgeSpecies);
	}

	/**
	 * Instantiates a new gama graph ML file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param nodeSpecies the node species
	 * @param edgeSpecies the edge species
	 */
	@doc ("References a graphml graph file by its filename and the 2 species to use to instantiate the nodes and the edges")
	public GamaGraphMLFile(final IScope scope, final String pathName, final ISpecies nodeSpecies,
			final ISpecies edgeSpecies, final String _nodeAttr, final String _edgeAttr) {
		super(scope, pathName, nodeSpecies, edgeSpecies);
		nodeAttr = _nodeAttr;
		edgeAttr = _edgeAttr;
	}	
	
	@Override
	protected String getFileType() { return "graphml"; }

	
	
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		GraphImporter<GamaGraphMLNodeImporter, GamaGraphMLEdgeImporter> generic_parser = GraphImporters.getGraphImporter(getFileType());
		GraphMLImporter<GamaGraphMLNodeImporter, GamaGraphMLEdgeImporter> parser ;
		if(generic_parser instanceof GraphMLImporter) {
			parser = (GraphMLImporter) generic_parser;
		} else {
			throw GamaRuntimeException.error("GraphML: Wrong importer loaded in fillBuffer", scope);
		}
		
		parser.addVertexAttributeConsumer((p, attrValue) -> {
			GamaGraphMLNodeImporter v = p.getFirst();
            String attrName = p.getSecond();

            v.addAttribute(attrName, attrValue.getValue());
        });
		
		parser.addEdgeAttributeConsumer((p, attrValue) -> { 
			GamaGraphMLEdgeImporter e = p.getFirst();
            String attrName = p.getSecond();

            e.addAttribute(attrName, attrValue.getValue());			
		});
		
		
		DirectedMultigraph<GamaGraphMLNodeImporter, GamaGraphMLEdgeImporter> graph =
				new DirectedMultigraph<>(new GamaGraphMLNodeImporterSupplier(), new GamaGraphMLEdgeImporterSupplier(), true);

		parser.importGraph(graph, this.getFile(scope));
		
		GamaGraph g = new GamaGraph(scope, graph, nodeS, edgeS, nodeAttr, edgeAttr);
		setBuffer(g);
//		setBuffer((GamaGraph<Object, DefaultEdge>) new GamaGraph<GamaGraphMLNodeImporter, GamaGraphMLEdgeImporter>(scope, graph, nodeS, edgeS, nodeAttr, edgeAttr));
	}	

	/**
     * A custom vertex supplier which creates each vertex.
     */
    static class GamaGraphMLNodeImporterSupplier implements Supplier<GamaGraphMLNodeImporter> {

        private int id = 0;

        @Override
        public GamaGraphMLNodeImporter get() {
            return new GamaGraphMLNodeImporter( String.valueOf(id++) );
        }
    }	
	
	/**
     * A custom edge supplier which creates each edge.
     */
    static class GamaGraphMLEdgeImporterSupplier implements Supplier<GamaGraphMLEdgeImporter> {

        @Override
        public GamaGraphMLEdgeImporter get() {
            return new GamaGraphMLEdgeImporter();
        }
    }		
    
}
