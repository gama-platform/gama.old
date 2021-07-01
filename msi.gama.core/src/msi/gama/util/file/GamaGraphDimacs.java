/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGraphMLFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

@file (
		name = "graphdimacs",
		extensions = { "dimacs" },
		buffer_type = IType.GRAPH,
		concept = { IConcept.GRAPH, IConcept.FILE },
		doc = @doc ("Represents files that contain Graph information. The internal representation is a graph"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraphDimacs extends GamaGraphFile {

	public GamaGraphDimacs(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	public GamaGraphDimacs(final IScope scope, final String pathName, final ISpecies nodeSpecies) {
		super(scope, pathName, nodeSpecies);
	}

	public GamaGraphDimacs(final IScope scope, final String pathName, final ISpecies nodeSpecies, final ISpecies edgeSpecies) {
		super(scope, pathName,nodeSpecies,edgeSpecies);
	}
	

	@Override
	protected String getFileType() {
		return "dimacs";
	}

}
