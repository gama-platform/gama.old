package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IType;

public class GamaFileReducer {
	private String path;
	private IList attributes;
	
	public GamaFileReducer(IScope scope, IGamaFile f)
	{		
		path = f.getPath();
		attributes = f.getAttributes(scope);
	}
	
	public IGamaFile constructObject(IScope scope)
	{
		return (IGamaFile) GamaFileType.createFile(scope, path);//, attributes) ;
		// return (GamaMatrix) GamaMatrixType.from(scope, valuesMatrixReducer, contentTypeMatrixReducer, new GamaPoint(nCols, nRows))	;

	}
}
