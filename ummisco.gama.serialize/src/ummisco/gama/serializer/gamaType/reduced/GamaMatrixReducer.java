/*********************************************************************************************
 *
 * 'GamaMatrixReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IType;

@SuppressWarnings({ "rawtypes" })
public class GamaMatrixReducer {
	private final IType contentTypeMatrixReducer;
	private final IList valuesMatrixReducer;
	private final int nRows;
	private final int nCols;

	public GamaMatrixReducer(final IScope scope, final GamaMatrix m) {
		contentTypeMatrixReducer = m.getGamlType().getContentType();
		nRows = m.getRows(null);
		nCols = m.getCols(null);
		valuesMatrixReducer = m.listValue(scope, contentTypeMatrixReducer, true);
	}

	public GamaMatrix constructObject(final IScope scope) {
		return (GamaMatrix) GamaMatrixType.from(scope, valuesMatrixReducer, contentTypeMatrixReducer,
				new GamaPoint(nCols, nRows));

	}
}
