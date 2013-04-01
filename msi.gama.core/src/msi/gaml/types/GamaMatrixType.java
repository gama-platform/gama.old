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
import java.util.regex.Pattern;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;

@type(name = IType.MATRIX_STR, id = IType.MATRIX, wraps = { IMatrix.class, GamaIntMatrix.class,
	GamaFloatMatrix.class, GamaObjectMatrix.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaMatrixType extends GamaContainerType<IMatrix> {

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return null; }
		if ( param == null || !(param instanceof ILocation) ) {
			if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope, null); }
			if ( obj instanceof String ) { return from(scope, (String) obj, null); }
			return with(scope, obj);
		}

		if ( ((GamaPoint) param).getX() <= 0 || ((GamaPoint) param).getY() < 0 ) { throw new GamaRuntimeException(
			"Dimensions of a matrix should be positive."); }

		if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope,
			(GamaPoint) param); }
		if ( obj instanceof String ) { return from(scope, (String) obj, (GamaPoint) param); }
		if ( obj instanceof Double ) { return with(scope, ((Double) obj).doubleValue(),
			(GamaPoint) param); }
		if ( obj instanceof Integer ) { return with(scope, ((Integer) obj).intValue(),
			(GamaPoint) param); }
		return with(scope, obj);
	}

	// Simplified pattern : only ';', ',', tab and white space are accepted
	public static Pattern csvPattern = Pattern.compile(";|,|\t|\\s");

	public static IMatrix from(IScope scope, final String string, final ILocation preferredSize)
		throws GamaRuntimeException {

		try {
			StringReader sr = new StringReader(string);
			final BufferedReader in = new BufferedReader(sr);
			final GamaList<String[]> allLines = new GamaList();
			String[] splitStr;
			String str;
			int columns = 0;
			str = in.readLine();
			while (str != null) {
				splitStr = csvPattern.split(str, -1);
				allLines.add(splitStr);
				if ( splitStr.length > columns ) {
					columns = splitStr.length;
				}
				str = in.readLine();
			}
			sr.close();
			in.close();
			int columnSize, lineSize;
			if ( preferredSize == null ) {
				lineSize = allLines.size();
				columnSize = columns;
			} else {
				lineSize = Math.min((int) preferredSize.getY(), allLines.size());
				columnSize = Math.min((int) preferredSize.getX(), columns);
			}
			final IMatrix matrix = new GamaObjectMatrix(scope, columnSize, lineSize);
			for ( int i = 0; i < lineSize; i++ ) {
				splitStr = allLines.get(i);
				for ( int j = 0; j < splitStr.length; j++ ) {
					matrix.set(scope, j, i, splitStr[j]);
				}
			}
			return matrix;
		} catch (final IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static IMatrix with(IScope scope, final Object val) throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix(scope, 1, 1);
		matrix.set(scope, 0, 0, val);
		return matrix;
	}

	public static IMatrix with(IScope scope, final Object val, final GamaPoint p)
		throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix(scope, (int) p.x, (int) p.y);
		matrix.set(scope, 0, 0, val);
		return matrix;
	}

	public static IMatrix with(IScope scope, final int val, final GamaPoint p)
		throws GamaRuntimeException {
		final IMatrix matrix = new GamaIntMatrix(scope, (int) p.x, (int) p.y);
		((GamaIntMatrix) matrix).fillWith(val);
		return matrix;
	}

	public static IMatrix with(IScope scope, final double val, final GamaPoint p)
		throws GamaRuntimeException {
		final IMatrix matrix = new GamaFloatMatrix(scope, (int) p.x, (int) p.y);
		((GamaFloatMatrix) matrix)._putAll(scope, val, null);
		return matrix;
	}

	@Override
	public IType defaultKeyType() {
		return Types.get(IType.POINT);
	}

	@Override
	public boolean isFixedLength() {
		return true;
	}

}
