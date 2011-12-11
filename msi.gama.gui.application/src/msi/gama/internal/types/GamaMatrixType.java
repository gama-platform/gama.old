/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import java.io.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.*;
import msi.gama.util.matrix.*;

@type(value = IType.MATRIX_STR, id = IType.MATRIX, wraps = { IMatrix.class, GamaIntMatrix.class,
	GamaFloatMatrix.class, GamaObjectMatrix.class })
public class GamaMatrixType extends GamaType<IMatrix> {

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return null; }
		if ( param == null || !(param instanceof GamaPoint) ) {

			if ( obj instanceof IGamaContainer ) { return ((IGamaContainer) obj).matrixValue(scope,
				null); }
			if ( obj instanceof String ) { return from((String) obj, null); }
		}

		if ( obj instanceof IGamaContainer ) { return ((IGamaContainer) obj).matrixValue(scope,
			(GamaPoint) param); }
		if ( obj instanceof String ) { return from((String) obj, (GamaPoint) param); }
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

	public static IMatrix from(final String string, final GamaPoint preferredSize)
		throws GamaRuntimeException {
		try {
			final BufferedReader in = new BufferedReader(new StringReader(string));
			final String delim = ";|,|\\s|\t|\n|\r|\f|\\|";
			final GamaList<String> allLines = new GamaList();
			String[] splitStr;
			String str;
			int columns = 0;
			str = in.readLine();
			while (str != null) {
				allLines.add(str);
				splitStr = str.split(delim, -1);
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
				lineSize = Math.min((int) preferredSize.y, allLines.size());
				columnSize = Math.min((int) preferredSize.x, columns);
			}
			final IMatrix matrix = new GamaObjectMatrix(columnSize, lineSize);
			for ( int i = 0; i < lineSize; i++ ) {
				str = allLines.get(i);
				splitStr = str.split(delim, -1);
				for ( int j = 0; j < splitStr.length; j++ ) {
					matrix.put(j, i, splitStr[j]);
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
		matrix.put(0, 0, val);
		return matrix;
	}

	public static IMatrix with(final Object val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix((int) p.x, (int) p.y);
		matrix.put(0, 0, val);
		return matrix;
	}

	public static IMatrix with(final int val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaIntMatrix((int) p.x, (int) p.y);
		matrix.put(0, 0, val);
		return matrix;
	}
}
