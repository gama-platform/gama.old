/*******************************************************************************************************
 *
 * GamaGraphGML.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * The Class GamaGraphGML.
 */
@file (
		name = "graphgml",
		extensions = { "gml" },
		buffer_type = IType.GRAPH,
		concept = { IConcept.GRAPH, IConcept.FILE },
		doc = @doc ("Represents files that contain Graph information. The internal representation is a graph"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraphGML extends GamaGraphFile {

	/**
	 * Instantiates a new gama graph GML.
	 *
	 * @param scope
	 *            the scope
	 * @param pn
	 *            the pn
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc ("References a gml graph file by its filename")
	public GamaGraphGML(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	/**
	 * Instantiates a new gama graph GML.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param nodeSpecies
	 *            the node species
	 */
	@doc ("References a gml graph file by its filename and the species to use to instantiate the nodes")
	public GamaGraphGML(final IScope scope, final String pathName, final ISpecies nodeSpecies) {
		super(scope, pathName, nodeSpecies);
	}

	/**
	 * Instantiates a new gama graph GML.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param nodeSpecies
	 *            the node species
	 * @param edgeSpecies
	 *            the edge species
	 */
	@doc ("References a gml graph file by its filename and the 2 species to use to instantiate the nodes and the edges")
	public GamaGraphGML(final IScope scope, final String pathName, final ISpecies nodeSpecies,
			final ISpecies edgeSpecies) {
		super(scope, pathName, nodeSpecies, edgeSpecies);
	}

	@Override
	protected String getFileType() { return "gml"; }

}
