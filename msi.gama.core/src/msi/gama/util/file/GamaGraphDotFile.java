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
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IType;

@file (
		name = "graphdot",
		extensions = { "dot" },
		buffer_type = IType.GRAPH,
		concept = { IConcept.GRAPH, IConcept.FILE },
		doc = @doc ("Represents files that contain Graph information. The internal representation is a graph"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraphDotFile extends GamaGraphFile {

	public GamaGraphDotFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	public GamaGraphDotFile(final IScope scope, final String pathName, final IGraph<?, ?> container) {
		super(scope, pathName, container);
	}

	@Override
	protected String getFileType() {
		return "dot";
	}

}
