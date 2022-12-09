/*******************************************************************************************************
 *
 * GamaGraphFile.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gama.util.graph.loader.GraphImporters;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

/**
 * The Class GamaGraphFile.
 */
public abstract class GamaGraphFile extends GamaFile<IGraph<?, ?>, Object> {
	
	/** The node S. */
	ISpecies nodeS = null;
	
	/** The edge S. */
	ISpecies edgeS = null;

	/**
	 * Instantiates a new gama graph file.
	 *
	 * @param scope the scope
	 * @param pn the pn
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public GamaGraphFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	/**
	 * Instantiates a new gama graph file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param nodeSpecies the node species
	 */
	public GamaGraphFile(final IScope scope, final String pathName, final ISpecies nodeSpecies) {
		super(scope, pathName);
		nodeS = nodeSpecies;
	}

	/**
	 * Instantiates a new gama graph file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param nodeSpecies the node species
	 * @param edgeSpecies the edge species
	 */
	public GamaGraphFile(final IScope scope, final String pathName, final ISpecies nodeSpecies,
			final ISpecies edgeSpecies) {
		super(scope, pathName);
		nodeS = nodeSpecies;
		edgeS = edgeSpecies;
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		GraphImporter<String, DefaultEdge> parser = GraphImporters.getGraphImporter(getFileType());
		DirectedMultigraph<String, DefaultEdge> graph =
				new DirectedMultigraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.DEFAULT_EDGE_SUPPLIER, true);

		parser.importGraph(graph, this.getFile(scope));
		setBuffer(new GamaGraph<>(scope, graph, nodeS, edgeS));
	}

	/**
	 * Gets the file type.
	 *
	 * @return the file type
	 */
	abstract protected String getFileType();

	@Override
	public IContainerType<?> getGamlType() { return Types.GRAPH; }

}
