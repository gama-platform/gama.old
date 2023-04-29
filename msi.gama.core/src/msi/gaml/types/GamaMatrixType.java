/*******************************************************************************************************
 *
 * GamaMatrixType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.Arrays;
import java.util.stream.IntStream;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IField;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.MapExpression;
import msi.gaml.operators.Cast;

/**
 * The Class GamaMatrixType.
 */
@type (
		name = IKeyword.MATRIX,
		id = IType.MATRIX,
		wraps = { IMatrix.class, GamaIntMatrix.class, GamaFloatMatrix.class, GamaObjectMatrix.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.MATRIX },
		doc = @doc ("Matrices are 2-dimensional containers that can contain any type of date (not only floats or integers). They can be accessed with a point index or by rows / columns"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMatrixType extends GamaContainerType<IMatrix> {

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param contentType
	 *            the content type
	 * @param copy
	 *            the copy
	 * @return the i matrix
	 */
	public static IMatrix staticCast(final IScope scope, final Object obj, final Object param, final IType contentType,
			final boolean copy) {
		if (obj == null && param == null) return null;
		final GamaPoint size = param instanceof GamaPoint ? (GamaPoint) param : null;

		if (size == null) {
			if (obj instanceof IContainer) return ((IContainer) obj).matrixValue(scope, contentType, copy);
			return with(scope, obj, new GamaPoint(1, 1), contentType);
		}
		if (size.x <= 0 || size.y < 0)
			throw GamaRuntimeException.error("Dimensions of a matrix should be positive.", scope);

		if (obj instanceof IContainer) return ((IContainer) obj).matrixValue(scope, contentType, size, copy);
		return with(scope, obj, size, contentType);

	}

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, param, contentsType, copy);
	}

	/**
	 * From.
	 *
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @param desiredType
	 *            the desired type
	 * @param preferredSize
	 *            the preferred size
	 * @return the i matrix
	 */
	public static IMatrix from(final IScope scope, final IList list, final IType desiredType,
			final GamaPoint preferredSize) {
		if (list == null || list.isEmpty()) return new GamaObjectMatrix(0, 0, desiredType);
		if (desiredType.id() == IType.INT) return new GamaIntMatrix(scope, list, preferredSize);
		if (desiredType.id() == IType.FLOAT) return new GamaFloatMatrix(scope, list, preferredSize);
		return new GamaObjectMatrix(scope, list, preferredSize, desiredType);

	}

	/**
	 * @param scope
	 *            the global scope
	 * @param matrix
	 *            the matrix to copy
	 * @param desiredType
	 *            the type of the contents of the copy
	 * @param contentsType
	 *            the type of the contents of the original
	 * @param preferredSize
	 *            the new size if any (can be null)
	 * @return
	 */
	public static IMatrix from(final IScope scope, final IMatrix matrix, final IType desiredType,
			final GamaPoint preferredSize, final boolean copy) {
		final IType contentsType = matrix.getGamlType().getContentType();
		if (!GamaType.requiresCasting(desiredType, contentsType)) {
			if (matrix instanceof IField f) return GamaFloatMatrix.from(scope, f);
			return matrix.copy(scope, preferredSize, copy);
		}

		int cols, rows;
		if (preferredSize == null) {
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
				final GamaObjectMatrix m = GamaObjectMatrix.from(cols, rows, matrix);
				final Object[] array = m.getMatrix();
				for (int i = 0; i < array.length; i++) { array[i] = desiredType.cast(scope, array[i], null, false); }
				return m;
		}

	}

	/**
	 * With.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @param p
	 *            the p
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IMatrix with(final IScope scope, final IExpression val, final GamaPoint p, final boolean parallel)
			throws GamaRuntimeException {
		return with(scope, val, (int) p.x, (int) p.y, parallel);
	}

	/**
	 * With.
	 *
	 * @param scope
	 *            the scope
	 * @param fillExpr
	 *            the fill expr
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @return the i matrix
	 */
	public static IMatrix with(final IScope scope, final IExpression fillExpr, final int cols, final int rows,
			final boolean parallel) {
		IMatrix result;
		if (fillExpr == null) return new GamaObjectMatrix(cols, rows, Types.NO_TYPE);
		switch (fillExpr.getGamlType().id()) {
			case IType.FLOAT:
				result = new GamaFloatMatrix(cols, rows);
				final double[] dd = ((GamaFloatMatrix) result).getMatrix();
				if (fillExpr.isConst()) {
					Arrays.fill(dd, Cast.asFloat(scope, fillExpr.value(scope)));
				} else if (!parallel) {
					for (int i = 0; i < dd.length; i++) { dd[i] = Cast.asFloat(scope, fillExpr.value(scope)); }
				} else {
					GamaExecutorService.executeThreaded(() -> IntStream.range(0, dd.length).parallel().forEach(i -> {
						dd[i] = Cast.asFloat(scope, fillExpr.value(scope));
					}));
				}
				break;
			case IType.INT:
				result = new GamaIntMatrix(cols, rows);
				final int[] ii = ((GamaIntMatrix) result).getMatrix();
				if (fillExpr.isConst()) {
					Arrays.fill(ii, Cast.asInt(scope, fillExpr.value(scope)));
				} else if (!parallel) {
					for (int i = 0; i < ii.length; i++) { ii[i] = Cast.asInt(scope, fillExpr.value(scope)); }
				} else {
					GamaExecutorService.executeThreaded(() -> IntStream.range(0, ii.length).parallel().forEach(i -> {
						ii[i] = Cast.asInt(scope, fillExpr.value(scope));
					}));
				}
				break;
			default:
				result = new GamaObjectMatrix(cols, rows, fillExpr.getGamlType());
				final Object[] contents = ((GamaObjectMatrix) result).getMatrix();
				if (fillExpr.isConst()) {
					Arrays.fill(contents, fillExpr.value(scope));
				} else if (!parallel) {
					for (int i = 0; i < contents.length; i++) { contents[i] = fillExpr.value(scope); }
				} else {
					GamaExecutorService
							.executeThreaded(() -> IntStream.range(0, contents.length).parallel().forEach(i -> {
								contents[i] = fillExpr.value(scope);
							}));
				}
		}
		return result;
	}

	/**
	 * With.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @param p
	 *            the p
	 * @param contentsType
	 *            the contents type
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IMatrix with(final IScope scope, final Object val, final GamaPoint p, final IType contentsType)
			throws GamaRuntimeException {
		return withObject(scope, val, (int) p.x, (int) p.y, contentsType);
	}

	/**
	 * With object.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param contentsType
	 *            the contents type
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IMatrix withObject(final IScope scope, final Object val, final int cols, final int rows,
			final IType contentsType) throws GamaRuntimeException {
		if (contentsType == Types.INT || val instanceof Integer) {
			final GamaIntMatrix matrix = new GamaIntMatrix(cols, rows);
			matrix.setAllValues(scope, Types.INT.cast(scope, val, null, false));
			return matrix;
		}
		if (contentsType == Types.FLOAT || val instanceof Double) {
			final GamaFloatMatrix matrix = new GamaFloatMatrix(cols, rows);
			matrix.setAllValues(scope, Types.FLOAT.cast(scope, val, null, false));
			return matrix;
		}
		final IMatrix matrix = new GamaObjectMatrix(cols, rows, contentsType);
		((GamaObjectMatrix) matrix).setAllValues(scope, contentsType.cast(scope, val, null, false));
		return matrix;
	}

	@Override
	public IType getKeyType() { return Types.POINT; }

	@Override
	public boolean isFixedLength() { return true; }

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		final IType itemType = exp.getGamlType();
		final IType cType = itemType.getContentType();
		if (itemType.id() == IType.LIST && cType.id() == IType.LIST) {
			// cf. issue #3792 -- the computation of type is now taken in charge by ListExpression itself
			// if (exp instanceof ListExpression) {
			// final IExpression[] array = ((ListExpression) exp).getElements();
			// if (array.length == 0) return Types.NO_TYPE;
			// return array[0].getGamlType().getContentType();
			// }
			if (!(exp instanceof MapExpression me)) return cType.getContentType();
			final IExpression[] array = me.valuesArray();
			if (array.length == 0) return Types.NO_TYPE;
			return array[0].getGamlType().getContentType();
		}
		if (Types.CONTAINER.isAssignableFrom(itemType)) return itemType.getContentType();
		return itemType;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
