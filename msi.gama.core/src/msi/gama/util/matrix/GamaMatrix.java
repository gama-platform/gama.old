/*******************************************************************************************************
 *
 * GamaMatrix.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.matrix;

import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;

import msi.gama.common.interfaces.ISafeConsumer;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.GamaType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 18 nov. 2008
 *
 * Abstract implementation of IMatrix, superclass of all matrices in GAML. Accessed by x = cols and y = rows.
 *
 * @todo Description
 */

/**
 * The Class GamaMatrix.
 *
 * @param <T>
 *            the generic type
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class GamaMatrix<T> implements IMatrix<T> {

	/** The type. */
	private final IContainerType<IMatrix> type;

	@Override
	public IContainerType<?> getGamlType() { return type; }

	/**
	 * Builds the value.
	 *
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @return the t
	 */
	protected T buildValue(final IScope scope, final Object object) {
		return (T) type.getContentType().cast(scope, object, null, false);
	}

	/**
	 * Builds the values.
	 *
	 * @param scope
	 *            the scope
	 * @param objects
	 *            the objects
	 * @return the i container
	 */
	protected IContainer<?, T> buildValues(final IScope scope, final IContainer objects) {
		return type.cast(scope, objects, null, false);
	}

	/**
	 * Builds the index.
	 *
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @return the gama point
	 */
	protected GamaPoint buildIndex(final IScope scope, final Object object) {
		return GamaPointType.staticCast(scope, object, false);
	}

	@Override
	public final String serialize(final boolean includingBuiltIn) {
		return this.getGamlType().serialize(true) + "(" + getColumnsList().serialize(includingBuiltIn) + ")";
	}

	/**
	 * Op plus.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IMatrix opPlus(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException.error("ATTENTION : Matrix additions not implemented. Returns nil for the moment",
				scope);
	}

	/**
	 * Op minus.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IMatrix opMinus(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException.error("ATTENTION : Matrix subtractions not implemented. Returns nil for the moment",
				scope);
	}

	/**
	 * Op times.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IMatrix opTimes(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException
				.error("ATTENTION : Matrix multiplications not implemented. Returns nil for the moment", scope);
	}

	/** The num rows. */
	public int numRows;

	/** The num cols. */
	public int numCols;

	/**
	 * Cols, rows instead of row cols because intended to work with xSize and ySize dimensions.
	 *
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 */

	@Override
	public int getRows(final IScope scope) {
		return numRows;
	}

	@Override
	public int getCols(final IScope scope) {
		return numCols;
	}

	/**
	 * Instantiates a new gama matrix.
	 *
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param contentsType
	 *            the contents type
	 */
	protected GamaMatrix(final int cols, final int rows, final IType contentsType) {
		numRows = rows;
		numCols = cols;
		this.type = Types.MATRIX.of(contentsType);
	}

	/**
	 * Instantiates a new gama matrix.
	 *
	 * @param objects
	 *            the objects
	 * @param flat
	 *            whether the list is flat or not (i.e. no sublist)
	 * @param preferredSize
	 *            the preferred size
	 */
	protected GamaMatrix(final IScope scope, final List objects, final GamaPoint preferredSize,
			final IType contentsType) {
		if (preferredSize != null) {
			numRows = (int) preferredSize.getY();
			numCols = (int) preferredSize.getX();
		} else if (objects == null || objects.isEmpty()) {
			numRows = 1;
			numCols = 1;
		} else if (GamaMatrix.isFlat(objects)) {
			numRows = 1;
			numCols = objects.size();
		} else {
			try {
				numCols = objects.size();
				numRows = ((List) objects.get(0)).size();
			} catch (final Exception e) {
				throw GamaRuntimeException
						.error("" + objects.get(0) + " cannot be casted to a List (in matrix creation)", scope);
			}
		}
		this.type = Types.MATRIX.of(contentsType);
	}

	@Override
	public T get(final IScope scope, final GamaPoint p) {
		final double px = p.getX();
		final double py = p.getY();
		if (px > numCols - 1 || px < 0)
			throw GamaRuntimeException.error("Access to a matrix element out of its bounds: " + px, scope);
		if (py > numRows - 1 || py < 0)
			throw GamaRuntimeException.error("Access to a matrix element out of its bounds: " + py, scope);
		return get(scope, (int) px, (int) py);
	}

	@Override
	public T getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		if (indices == null || indices.isEmpty()) return null;
		final int size = indices.size();
		if (size == 1) {
			final Object index = indices.get(0);
			if (index instanceof GamaPoint) return get(scope, (GamaPoint) index);
			return this.getNthElement(Cast.asInt(scope, index));
		}
		final int px = Cast.asInt(scope, indices.get(0));
		final int py = Cast.asInt(scope, indices.get(1));
		if (px > numCols - 1 || px < 0)
			throw GamaRuntimeException.error("Access to a matrix element out of its bounds: " + px, scope);
		if (py > numRows - 1 || py < 0)
			throw GamaRuntimeException.error("Access to a matrix element out of its bounds: " + py, scope);
		return get(scope, px, py);
	}

	/**
	 * @param asInt
	 * @return
	 */
	public abstract T getNthElement(Integer index);

	@Override
	public abstract Object remove(IScope scope, final int col, final int row);

	@Override
	public IMatrix<?> matrixValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		return matrixValue(scope, contentsType, null, copy);
	}

	@Override
	public GamaPoint getDimensions() { return new GamaPoint(numCols, numRows); }

	@Override
	public final String stringValue(final IScope scope) throws GamaRuntimeException {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		rowByRow(scope, v -> sb.append(Cast.asString(scope, v)), () -> sb.append(';'), () -> sb.append(Strings.LN));
		return sb.toString();
	}

	/**
	 * Row by row. Allows to process the values row by row, optionnaly doing something after each value has been
	 * processed (except the last one of each row) and each row has been processed (except the last row).
	 *
	 * @param forEachValue
	 *            A consumer that is fed by each value of the matrix
	 * @param afterEachValue
	 *            the after each value
	 * @param afterEachRow
	 *            the after each row
	 */
	public void rowByRow(final IScope scope, final ISafeConsumer<T> forEachValue, final ISafeRunnable afterEachValue,
			final ISafeRunnable afterEachRow) throws GamaRuntimeException {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				if (forEachValue != null) {
					try {
						forEachValue.accept(get(scope, col, row));
					} catch (Throwable e) {
						throw GamaRuntimeException.create(e, scope);
					}
				}
				if (col < numCols - 1 && afterEachValue != null) {
					try {
						afterEachValue.run();
					} catch (Exception e) {
						throw GamaRuntimeException.create(e, scope);
					}
				}
			}
			if (row < numRows - 1 && afterEachRow != null) {
				try {
					afterEachRow.run();
				} catch (Exception e) {
					throw GamaRuntimeException.create(e, scope);
				}
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		sb.append('[');
		rowByRow(GAMA.getRuntimeScope(), v -> sb.append(v), () -> sb.append(','), () -> sb.append(';'));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public IMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {
		final IType kt = GamaType.findSpecificType(keyType, type.getContentType());
		final IType ct = GamaType.findSpecificType(contentsType, type.getContentType());
		final IMap result = GamaMapFactory.create(kt, ct);
		for (int i = 0; i < numRows; i++) {
			// in case the matrix rows < 2, put null in value
			result.put(GamaType.toType(scope, get(scope, 0, i), kt, copy),
					GamaType.toType(scope, get(scope, 1, i), ct, copy));
		}
		return result;

	}

	@Override
	public final IMatrix copy(final IScope scope) {
		return copy(scope, getDimensions(), true);
	}

	/**
	 * Checks if is flat.
	 *
	 * @param val
	 *            the val
	 * @return true, if is flat
	 */
	public static boolean isFlat(final List val) {
		for (final Object element : val) { if (element instanceof List) return false; }
		return true;
	}

	/**
	 * Fill with.
	 *
	 * @param scope
	 *            the scope
	 * @param expr
	 *            the expr
	 */
	public void fillWith(final IScope scope, final IExpression expr) {}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#contains(java.lang.Object)
	 */
	@Override
	public final boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return _contains(scope, o);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#first()
	 */
	@Override
	public final T firstValue(final IScope scope) throws GamaRuntimeException {
		return (T) _first(scope);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#last()
	 */
	@Override
	public final T lastValue(final IScope scope) throws GamaRuntimeException {
		return (T) _last(scope);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#length()
	 */
	@Override
	public final int length(final IScope scope) {
		return _length(scope);
	}

	@Override
	public final boolean isEmpty(final IScope scope) {
		return _isEmpty(scope);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#reverse()
	 */
	@Override
	public final IMatrix<T> reverse(final IScope scope) throws GamaRuntimeException {
		return _reverse(scope);
	}

	// 09/01/14:Trying to keep the interface simple.
	// Three methods for add and put operations:
	// The simple method, that simply contains the object to add
	@Override
	public void addValue(final IScope scope, final T value) {}

	// The same but with an index
	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final T value) {}

	// set, that takes a mandatory index (also replaces the parameter)
	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final T value) {
		if (index instanceof Integer) {
			setNthElement(scope, (int) index, value);
			return;
		}
		final GamaPoint p = buildIndex(scope, index);
		set(scope, (int) p.getX(), (int) p.getY(), value);

	}

	/**
	 * Sets the nth element.
	 *
	 * @param scope
	 *            the scope
	 * @param index
	 *            the index
	 * @param value
	 *            the value
	 */
	protected abstract void setNthElement(IScope scope, int index, Object value);

	// Then, methods for "all" operations
	// Adds the values if possible, without replacing existing ones
	// AD July 2020: Addition of the index (see #2985)
	@Override
	public void addValues(final IScope scope, final Object index, final IContainer values) {
		// Nothing to do for matrices
	}

	// Adds this value to all slots (if this operation is available), otherwise
	// replaces the values with this one
	@Override
	public void setAllValues(final IScope scope, final T value) {
		_putAll(scope, value);
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {}

	@Override
	public void removeIndex(final IScope scope, final Object index) {}

	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, ?> indexes) {}

	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {}

	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IValue#listValue(msi.gama.interfaces.IScope)
	 */
	@Override
	public final IList<T> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		final IType originalContentsType = type.getContentType();
		if (!GamaType.requiresCasting(contentsType, originalContentsType)) // no need to take "copy" into account as the
																			// list is created anyway
			return _listValue(scope, originalContentsType, false);
		return _listValue(scope, contentsType, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IValue#matrixValue(msi.gama.interfaces.IScope, msi.gama.util.GamaPoint)
	 */
	@Override
	public final IMatrix<T> matrixValue(final IScope scope, final IType type, final GamaPoint size, final boolean copy)
			throws GamaRuntimeException {
		return _matrixValue(scope, size, type, copy);
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	// @Override
	// public abstract Iterator<T> iterator();

	// @Override
	// public final Iterable<T> iterable(final IScope scope) {
	// return this;
	// }

	/**
	 * @see msi.gama.interfaces.IMatrix#getRowsList()
	 */
	@Override
	public IList<IList<T>> getRowsList() {
		final IList result = GamaListFactory.create(Types.LIST.of(getGamlType().getContentType()));
		for (int i = 0; i < numRows; i++) { result.add(getRow(i)); }
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#getColumnsList()
	 */
	@Override
	public IList<IList<T>> getColumnsList() {
		final IList result = GamaListFactory.create(Types.LIST.of(getGamlType().getContentType()));
		for (int i = 0, n = numCols; i < n; i++) { result.add(getColumn(i)); }
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#getRow(java.lang.Integer)
	 */
	@Override
	public IList<T> getRow(final Integer n) {
		final IList result = GamaListFactory.create(getGamlType().getContentType());
		if (n >= numRows || n < 0) return result;
		for (int i = 0; i < numCols; i++) { result.add(getNthElement(n * numCols + i)); }
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#getColumn(java.lang.Integer)
	 */
	@Override
	public IList<T> getColumn(final Integer n) {
		final IList result = GamaListFactory.create(getGamlType().getContentType());
		if (n >= numCols || n < 0) return result;
		for (int i = 0; i < numRows; i++) { result.add(getNthElement(i * numCols + n)); }
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#plus(msi.gama.interfaces.IMatrix)
	 */
	@Override
	public IMatrix plus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		return opPlus(scope, this, other);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#times(msi.gama.interfaces.IMatrix)
	 */
	@Override
	public IMatrix times(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		return opTimes(scope, this, other);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#minus(msi.gama.interfaces.IMatrix)
	 */
	@Override
	public IMatrix<T> minus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		return opMinus(scope, this, other);
	}

	@Override
	public T anyValue(final IScope scope) {
		final RandomUtils r = scope.getRandom();
		final int x = r.between(0, numCols - 1);
		final int y = r.between(0, numRows - 1);
		return this.get(scope, x, y);
	}

	// PRIVATE METHODS INTENDED TO ALLOW MATRICES TO IMPLEMENT GAML OPERATORS
	// POLYMORPHISM IS NOT REALLY SUPPORTED BY THE GAML COMPILER AND IS TAKEN
	// IN CHARGE BY JAVA THROUGH THIS TRICK.

	/**
	 * List value.
	 *
	 * @param scope
	 *            the scope
	 * @param contentsType
	 *            the contents type
	 * @param cast
	 *            the cast
	 * @return the i list
	 */
	protected abstract IList<T> _listValue(IScope scope, IType contentsType, boolean cast);

	/**
	 * Matrix value.
	 *
	 * @param scope
	 *            the scope
	 * @param size
	 *            the size
	 * @param type
	 *            the type
	 * @param copy
	 *            the copy
	 * @return the i matrix
	 */
	protected abstract IMatrix<T> _matrixValue(IScope scope, GamaPoint size, IType type, boolean copy);

	/**
	 * Clear.
	 */
	protected abstract void _clear();

	/**
	 * Removes the first.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract boolean _removeFirst(IScope scope, T value) throws GamaRuntimeException;

	/**
	 * Removes the all.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract boolean _removeAll(IScope scope, IContainer<?, T> value) throws GamaRuntimeException;

	/**
	 * Put all.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract void _putAll(IScope scope, Object value) throws GamaRuntimeException;

	/**
	 * Reverse.
	 *
	 * @param scope
	 *            the scope
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract IMatrix<T> _reverse(IScope scope) throws GamaRuntimeException;

	/**
	 * Checks if is empty.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	protected abstract boolean _isEmpty(IScope scope);

	/**
	 * Contains.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	protected abstract boolean _contains(IScope scope, Object o);

	/**
	 * Length.
	 *
	 * @param scope
	 *            the scope
	 * @return the integer
	 */
	protected abstract Integer _length(IScope scope);

	/**
	 * Last.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 */
	protected abstract Object _last(IScope scope);

	/**
	 * First.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 */
	protected abstract Object _first(IScope scope);

	@Override
	public IMatrix divides(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix times(final Double val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix times(final Integer val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix divides(final Double val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix divides(final Integer val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix plus(final Double val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix plus(final Integer val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix minus(final Double val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public IMatrix minus(final Integer val) throws GamaRuntimeException {
		return this;
	}

	@Override
	public double getNoData(final IScope scope) {
		return IField.NO_NO_DATA;
	}

}
