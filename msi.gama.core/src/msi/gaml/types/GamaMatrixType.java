/*********************************************************************************************
 *
 *
 * 'GamaMatrixType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;

@type(name = IKeyword.MATRIX,
	id = IType.MATRIX,
	wraps = { IMatrix.class, GamaIntMatrix.class, GamaFloatMatrix.class, GamaObjectMatrix.class },
	kind = ISymbolKind.Variable.CONTAINER,
	concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.MATRIX })
public class GamaMatrixType extends GamaContainerType<IMatrix> {

	public static IMatrix staticCast(final IScope scope, final Object obj, final Object param, final IType contentType,
		final boolean copy) {
		if ( obj == null && param == null ) { return null; }
		GamaPoint size = param instanceof GamaPoint ? (GamaPoint) param : null;

		if ( size == null ) {
			if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope, contentType, copy); }
			return with(scope, obj, new GamaPoint(1, 1), contentType);
		} else if ( size.x <= 0 ||
			size.y < 0 ) { throw GamaRuntimeException.error("Dimensions of a matrix should be positive.", scope); }

		if ( obj instanceof IContainer ) { return ((IContainer) obj).matrixValue(scope, contentType, size, copy); }
		return with(scope, obj, size, contentType);

	}

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType, final boolean copy) throws GamaRuntimeException {
		IMatrix m = staticCast(scope, obj, param, contentsType, copy);
		return m;
	}

	public static IMatrix from(final IScope scope, final IList list, final IType desiredType,
		final ILocation preferredSize) {
		if ( list == null || list.isEmpty() ) { return new GamaObjectMatrix(0, 0, desiredType); }
		if ( desiredType.id() == IType.INT ) {
			return new GamaIntMatrix(scope, list, preferredSize);
		} else if ( desiredType.id() == IType.FLOAT ) {
			return new GamaFloatMatrix(scope, list, preferredSize);
		} else {
			return new GamaObjectMatrix(scope, list, preferredSize, desiredType);
		}

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
		final ILocation preferredSize, final boolean copy) {
		IType contentsType = matrix.getType().getContentType();
		if ( !GamaType.requiresCasting(desiredType, contentsType) ) { return matrix.copy(scope, preferredSize, copy); }
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
					array[i] = desiredType.cast(scope, array[i], null, false);
				}
				return m;
		}

	}

	public static IMatrix with(final IScope scope, final IExpression val, final GamaPoint p)
		throws GamaRuntimeException {
		return with(scope, val, (int) p.x, (int) p.y);
	}

	public static IMatrix with(final IScope scope, final IExpression fillExpr, final int cols, final int rows) {
		IMatrix result;
		if ( fillExpr == null ) { return new GamaObjectMatrix(cols, rows, Types.NO_TYPE); }
		switch (fillExpr.getType().id()) {
			case IType.FLOAT:
				result = new GamaFloatMatrix(cols, rows);
				double[] dd = ((GamaFloatMatrix) result).getMatrix();
				for ( int i = 0; i < dd.length; i++ ) {
					dd[i] = Cast.asFloat(scope, fillExpr.value(scope));
				}
				break;
			case IType.INT:
				result = new GamaIntMatrix(cols, rows);
				int[] ii = ((GamaIntMatrix) result).getMatrix();
				for ( int i = 0; i < ii.length; i++ ) {
					ii[i] = Cast.asInt(scope, fillExpr.value(scope));
				}
				break;
			default:
				result = new GamaObjectMatrix(cols, rows, fillExpr.getType());
				Object[] contents = ((GamaObjectMatrix) result).getMatrix();
				for ( int i = 0; i < contents.length; i++ ) {
					contents[i] = fillExpr.value(scope);
				}
		}
		return result;
	}

	public static IMatrix with(final IScope scope, final Object val, final GamaPoint p, final IType contentsType)
		throws GamaRuntimeException {
		return withObject(scope, val, (int) p.x, (int) p.y, contentsType);
	}

	public static IMatrix withObject(final IScope scope, final Object val, final int cols, final int rows,
		final IType contentsType) throws GamaRuntimeException {
		if ( contentsType == Types.INT || val instanceof Integer ) {
			final GamaIntMatrix matrix = new GamaIntMatrix(cols, rows);
			matrix.setAllValues(scope, (Integer) Types.INT.cast(scope, val, null, false));
			return matrix;
		} else if ( contentsType == Types.FLOAT || val instanceof Double ) {
			final GamaFloatMatrix matrix = new GamaFloatMatrix(cols, rows);
			matrix.setAllValues(scope, (Double) Types.FLOAT.cast(scope, val, null, false));
			return matrix;
		}
		final IMatrix matrix = new GamaObjectMatrix(cols, rows, contentsType);
		((GamaObjectMatrix) matrix).setAllValues(scope, contentsType.cast(scope, val, null, false));
		return matrix;
	}

	@Override
	public IType getKeyType() {
		return Types.POINT;
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
			} else {
				return cType.getContentType();
			}
		}
		if ( Types.CONTAINER.isAssignableFrom(itemType) ) { return itemType.getContentType(); }
		return itemType;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	// @Override
	// public boolean canCastToConst() {
	// return true;
	// }

}
