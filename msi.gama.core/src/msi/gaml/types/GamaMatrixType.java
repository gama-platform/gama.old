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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;
import msi.gaml.expressions.*;

@type(name = IKeyword.MATRIX, id = IType.MATRIX, wraps = { IMatrix.class, GamaIntMatrix.class, GamaFloatMatrix.class,
	GamaObjectMatrix.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaMatrixType extends GamaContainerType<IMatrix> {

	public static IMatrix staticCast(final IScope scope, final Object obj, final Object param, final IType contentType) {

		if ( obj == null && param == null ) { return null; }
		if ( param == null || !(param instanceof ILocation) ) {
			if ( obj instanceof IList ) { return GamaMatrixType.from(scope, (IList) obj); }
			if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope, contentType); }
			if ( obj instanceof String ) { return from(scope, (String) obj, null); }
			return with(scope, obj);
		}

		if ( ((GamaPoint) param).getX() <= 0 || ((GamaPoint) param).getY() < 0 ) { throw GamaRuntimeException
			.error("Dimensions of a matrix should be positive."); }

		if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope, contentType, (GamaPoint) param); }
		if ( obj instanceof String ) { return from(scope, (String) obj, (GamaPoint) param); }
		if ( obj instanceof Double ) { return with(scope, ((Double) obj).doubleValue(), (GamaPoint) param); }
		if ( obj instanceof Integer ) { return with(scope, ((Integer) obj).intValue(), (GamaPoint) param); }
		return with(scope, obj, (GamaPoint) param);

	}

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType) throws GamaRuntimeException {
		IMatrix m = staticCast(scope, obj, param, contentsType);
		return m;
	}

	// Simplified pattern : only ';', ',', tab and white space are accepted
	public static Pattern csvPattern = Pattern.compile(";|,|\t|\\s");

	public static IMatrix from(final IScope scope, final String string, final ILocation preferredSize)
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
			final IMatrix matrix = new GamaObjectMatrix(columnSize, lineSize);
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

	public static IMatrix from(final IScope scope, final IList list, final IType type, final ILocation preferredSize) {
		if ( list == null || list.isEmpty() ) { return new GamaObjectMatrix(0, 0); }
		// 3 cases to consider: whether the list is flat or not,
		boolean flat = GamaMatrix.isFlat(list);
		// Which type: Object, Int or Float ?
		// TODO Add IShape to create GamaSpatialMatrix
		IList castedList;
		if ( flat ) {
			castedList = list.listValue(scope, type);
		} else {
			castedList = new GamaList();
			for ( Object o : list.iterable(scope) ) {
				castedList.add(((IList) o).listValue(scope, type));
			}
		}
		if ( type.id() == IType.INT ) {
			return new GamaIntMatrix(scope, castedList, flat, preferredSize);
		} else if ( type.id() == IType.FLOAT ) {
			return new GamaFloatMatrix(scope, castedList, flat, preferredSize);
		} else {
			return new GamaObjectMatrix(scope, castedList, flat, preferredSize);
		}

	}

	public static IMatrix from(final IScope scope, final IList list) {
		if ( list == null || list.isEmpty() ) { return new GamaObjectMatrix(0, 0); }
		// 3 cases to consider: whether the list is flat or not, whether it is int/float or other objects.
		// Is the list flat ? True if one of the contained objects is not a list / they are not the same size
		boolean flat = GamaMatrix.isFlat(list);
		// Which type: Object, Int or Float ?
		// TODO Add IShape to create GamaSpatialMatrix

		List first = (List) (flat ? list : list.get(0));
		boolean allInt = true;
		boolean allFloat = true;
		for ( Object o : first ) {
			allInt = allInt && o instanceof Integer;
			if ( !allInt ) {
				allFloat = allFloat && o instanceof Double;
			}
		}
		if ( allInt ) {
			return new GamaIntMatrix(scope, list, flat, null);
		} else if ( allFloat ) {
			return new GamaFloatMatrix(scope, list, flat, null);
		} else {
			return new GamaObjectMatrix(scope, list, flat, null);
		}
	}

	public static IMatrix fromCSV(final IScope scope, final List<String[]> allLines) {
		int columns = 0;
		for ( String[] strings : allLines ) {
			if ( strings.length > columns ) {
				columns = strings.length;
			}
		}
		final int lineSize = allLines.size();
		final int columnSize = columns;
		final IMatrix matrix = new GamaObjectMatrix(columnSize, lineSize);
		for ( int i = 0; i < lineSize; i++ ) {
			String[] splitStr = allLines.get(i);
			for ( int j = 0; j < splitStr.length; j++ ) {
				matrix.set(scope, j, i, splitStr[j]);
			}
		}
		return matrix;
	}

	/**
	 * @param scope the global scope
	 * @param matrix the matrix to copy
	 * @param desiredType the type of the contents of the copy
	 * @param contentsType the type of the contents of the original
	 * @param preferredSize the new size if any (can be null)
	 * @return
	 */
	public static IMatrix from(final IScope scope, final IMatrix matrix, final IType desiredType,
		final IType contentsType, final ILocation preferredSize) {
		if ( desiredType == null || desiredType.id() == IType.NONE || desiredType.isAssignableFrom(contentsType) ) { return matrix
			.copy(scope, preferredSize); }
		int cols, rows;
		if ( preferredSize == null ) {
			cols = matrix.getCols(scope);
			rows = matrix.getRows(scope);
		} else {
			cols = (int) preferredSize.getX();
			rows = (int) preferredSize.getY();
		}
		switch (desiredType.id()) {
			case IType.INT:
				return GamaIntMatrix.from(scope, cols, rows, matrix);
			case IType.FLOAT:
				return GamaFloatMatrix.from(scope, cols, rows, matrix);
			default:
				GamaObjectMatrix m = GamaObjectMatrix.from(cols, rows, matrix);
				Object[] array = m.getMatrix();
				for ( int i = 0; i < array.length; i++ ) {
					array[i] = desiredType.cast(scope, array[i], null);
				}
				return m;
		}

	}

	public static IMatrix with(final IScope scope, final Object val) throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix(1, 1);
		matrix.set(scope, 0, 0, val);
		return matrix;
	}

	public static IMatrix with(final IScope scope, final Object val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaObjectMatrix((int) p.x, (int) p.y);
		((GamaObjectMatrix) matrix).fillWith(scope, val);
		return matrix;
	}

	public static IMatrix with(final IScope scope, final int val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaIntMatrix((int) p.x, (int) p.y);
		((GamaIntMatrix) matrix).fillWith(val);
		return matrix;
	}

	public static IMatrix with(final IScope scope, final double val, final GamaPoint p) throws GamaRuntimeException {
		final IMatrix matrix = new GamaFloatMatrix((int) p.x, (int) p.y);
		((GamaFloatMatrix) matrix)._putAll(scope, val, null);
		return matrix;
	}

	@Override
	public IType getKeyType() {
		return Types.get(IType.POINT);
	}

	@Override
	public boolean isFixedLength() {
		return true;
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		IType itemType = exp.getType();
		IType cType = itemType.getContentType();
		if ( itemType.id() == IType.LIST && cType.id() == IType.LIST ) {
			if ( exp instanceof ListExpression ) {
				final IExpression[] array = ((ListExpression) exp).getElements();
				if ( array.length == 0 ) { return Types.NO_TYPE; }
				return array[0].getType().getContentType();
			} else if ( exp instanceof MapExpression ) {
				final IExpression[] array = ((MapExpression) exp).valuesArray();
				if ( array.length == 0 ) { return Types.NO_TYPE; }
				return array[0].getType().getContentType();
			}
		}
		if ( Types.get(IKeyword.CONTAINER).isAssignableFrom(itemType) ) { return itemType.getContentType(); }
		return itemType;
	}

}
