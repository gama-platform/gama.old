/*********************************************************************************************
 *
 * 'GamaFileReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.file.IGamaFile;
import msi.gaml.types.GamaFileType;

@SuppressWarnings({ "rawtypes" })
public class GamaFileReducer {
	private final String path;
	private final IList attributes;

	public GamaFileReducer(final IScope scope, final IGamaFile f) {
		path = f.getPath(scope);
		attributes = f.getAttributes(scope);
	}

	public IGamaFile constructObject(final IScope scope) {
		return GamaFileType.createFile(scope, path, null);// , attributes) ;
		// return (GamaMatrix) GamaMatrixType.from(scope, valuesMatrixReducer,
		// contentTypeMatrixReducer, new GamaPoint(nCols, nRows)) ;

	}
}
