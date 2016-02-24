package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IType;

public class GamaMatrixReducer {
	private IType contentTypeMatrixReducer;
	private IList valuesMatrixReducer;
	private int nRows;
	private int nCols;
	
	public GamaMatrixReducer(IScope scope, GamaMatrix m)
	{		
		contentTypeMatrixReducer = m.getType().getContentType();
		nRows = m.getRows(null);
		nCols = m.getCols(null);
		valuesMatrixReducer = m.listValue(scope, contentTypeMatrixReducer, true);
	}
	
	public GamaMatrix constructObject(IScope scope)
	{
		return (GamaMatrix) GamaMatrixType.from(scope, valuesMatrixReducer, contentTypeMatrixReducer, new GamaPoint(nCols, nRows))	;

	}
}
