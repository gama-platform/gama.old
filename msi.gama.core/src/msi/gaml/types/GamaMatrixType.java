/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.io.*;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;

@type(value = IType.MATRIX_STR, id = IType.MATRIX, wraps = { IMatrix.class, GamaIntMatrix.class,
	GamaFloatMatrix.class, GamaObjectMatrix.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaMatrixType extends GamaType<IMatrix> {

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return null; }
		if ( param == null || !(param instanceof ILocation) ) {

			if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope, null); }
			if ( obj instanceof String ) { return from((String) obj, null); }
		}
		
		if((((GamaPoint)param).getX() <= 0) || (((GamaPoint)param).getY() < 0)) {
			throw new GamaRuntimeException("Dimensions of a matrix should be positive.");
		}
		
		if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope,
			(GamaPoint) param); }
		if ( obj instanceof String ) { return from((String) obj, (GamaPoint) param); }
		if ( obj instanceof Double ) { return with(((Double) obj).doubleValue(), (GamaPoint) param); }
		if ( obj instanceof Integer ) { return with(((Integer) obj).intValue(), (GamaPoint) param); }		
		return with(obj);
	}

	@Override
	public IMatrix getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

	public static IMatrix from(final String string, final ILocation preferredSize)
		throws GamaRuntimeException {
		try {
			final BufferedReader in = new BufferedReader(new StringReader(string));
			final String delim = ";|,|\\s|\t|\n|\r|\f|\\|";
			final GamaList<String[]> allLines = new GamaList();
			String[] splitStr;
			String str;
			int columns = 0;
			str = in.readLine();
			while (str != null) {
				splitStr = str.split(delim, -1);
				allLines.add(splitStr);
				if ( splitStr.length > columns ) {
					columns = splitStr.length;
				}
				str = in.readLine();
			}
			in.close();
			int columnSize, lineSize;
			if ( preferredSize == null ) {
				lineSize = allLines.size();
				columnSize = columns;
			} else {
				lineSize = Math.min((int) preferredSize.getY(), allLines.size());
				columnSize = Math.min((int) preferredSize.getX(), columns);
			}
			final IMatrix matrix = new GamaObjectMatrix(columnSize, lineSize);
			for ( int i = 0; i < lineSize; i++ ) {
				splitStr = allLines.get(i);
				for ( int j = 0; j < splitStr.length; j++ ) {
					matrix.set(j, i, splitStr[j]);
				}
			}
			return matrix;
		} catch (final IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static IMatrix with(final Object val) throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix(1, 1);
		matrix.set(0, 0, val);
		return matrix;
	}

	public static IMatrix with(final Object val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix((int) p.x, (int) p.y);
		matrix.set(0, 0, val);
		return matrix;
	}

	public static IMatrix with(final int val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaIntMatrix((int) p.x, (int) p.y);
		((GamaIntMatrix)matrix).fillWith(val);
		return matrix;
	}
	
	public static IMatrix with(final double val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaFloatMatrix((int) p.x, (int) p.y);
		((GamaFloatMatrix)matrix)._putAll(val,null);		
		return matrix;
	}
}
