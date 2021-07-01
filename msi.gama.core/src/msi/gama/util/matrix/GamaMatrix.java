/*******************************************************************************************************
 *
 * msi.gama.util.matrix.GamaMatrix.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.matrix;

import java.util.List;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
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
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class GamaMatrix<T> implements IMatrix<T> {

	private final IContainerType<IMatrix> type;

	@Override
	public IContainerType<?> getGamlType() {
		return type;
	}

	protected T buildValue(final IScope scope, final Object object) {
		return (T) type.getContentType().cast(scope, object, null, false);
	}

	protected IContainer<?, T> buildValues(final IScope scope, final IContainer objects) {
		return type.cast(scope, objects, null, false);
	}

	protected ILocation buildIndex(final IScope scope, final Object object) {
		return GamaPointType.staticCast(scope, object, false);
	}

	public static IList getLines(final IScope scope, final IMatrix m) {
		final IList result = GamaListFactory.create(Types.LIST.of(m.getGamlType().getContentType()));
		for (int i = 0; i < m.getRows(scope); i++) {
			result.add(getLine(scope, m, i));
		}
		return result;
	}

	public static IList getColumns(final IScope scope, final IMatrix m) {
		final IList result = GamaListFactory.create(Types.LIST.of(m.getGamlType().getContentType()));
		for (int i = 0, n = m.getCols(scope); i < n; i++) {
			result.add(getColumn(scope, m, i));
		}
		return result;
	}

	public static IList getColumn(final IScope scope, final IMatrix m, final Integer num_col) {
		final IList result = GamaListFactory.create(m.getGamlType().getContentType());
		if (num_col >= m.getCols(scope) || num_col < 0) return result;
		for (int i = 0; i < m.getRows(scope); i++) {
			result.add(m.get(scope, num_col, i));
		}
		return result;
	}

	public static IList getLine(final IScope scope, final IMatrix m, final Integer num_line) {
		final IList result = GamaListFactory.create(m.getGamlType().getContentType());
		if (num_line >= m.getRows(scope) || num_line < 0) return result;
		for (int i = 0; i < m.getCols(scope); i++) {
			result.add(m.get(scope, i, num_line));
		}
		return result;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "matrix(" + getRowsList(null).serialize(includingBuiltIn) + ")";
	}

	public static IMatrix opPlus(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException.error("ATTENTION : Matrix additions not implemented. Returns nil for the moment",
				scope);
	}

	public static IMatrix opMinus(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException.error("ATTENTION : Matrix subtractions not implemented. Returns nil for the moment",
				scope);
	}

	public static IMatrix opTimes(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException
				.error("ATTENTION : Matrix multiplications not implemented. Returns nil for the moment", scope);
	}

	public int numRows;

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
	protected GamaMatrix(final IScope scope, final List objects, final ILocation preferredSize,
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
	public T get(final IScope scope, final ILocation p) {
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
			if (index instanceof GamaPoint)
				return get(scope, (GamaPoint) index);
			else
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
	public GamaPoint getDimensions() {
		return new GamaPoint(numCols, numRows);
	}

	@Override
	public final String stringValue(final IScope scope) throws GamaRuntimeException {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		for (int line = 0; line < numRows; line++) {
			for (int col = 0; col < numCols; col++) {
				sb.append(Cast.asString(scope, get(scope, col, line)));
				if (col != numCols - 1) { sb.append(';'); }
			}
			sb.append(java.lang.System.getProperty("line.separator"));
		}
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

	//
	// @Override
	// public final Object removeAt(IScope scope, final ILocation p) throws
	// GamaRuntimeException {
	// // Normally never called as matrices are of fixed length
	// return remove(scope, (int) p.getX(), (int) p.getY());
	// }

	// @Override
	// public final void put(IScope scope, final ILocation p, final T value,
	// final Object param)
	// throws GamaRuntimeException {
	// set(scope, (int) p.getX(), (int) p.getY(), value);
	// }
	//
	@Override
	public final IMatrix copy(final IScope scope) {
		return copy(scope, getDimensions(), true);
	}

	public static boolean isFlat(final List val) {
		for (final Object element : val) {
			if (element instanceof List) return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final IScope scope, final Object object, final boolean forAdding) {
		if (object instanceof ILocation) {
			final ILocation index = (ILocation) object;
			final int x = (int) index.getX();
			final int y = (int) index.getY();
			return x >= 0 && x < numCols && y >= 0 && y < numRows;
		} else if (object instanceof IList) {
			IList list = (IList) object;
			if (list.size() != 2) return false;
			int x = Cast.asInt(scope, list.get(0));
			int y = Cast.asInt(scope, list.get(1));
			return x >= 0 && x < numCols && y >= 0 && y < numRows;
		} else if (object instanceof Integer) return (Integer) object < numCols * numRows;
		return false;
	}

	public void fillWith(final IScope scope, final IExpression expr) {

	}

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

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#max()
	 */
	// @Override
	// public final T max(final IScope scope) throws GamaRuntimeException {
	// return (T) _max(scope);
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#min()
	 */
	// @Override
	// public final T min(final IScope scope) throws GamaRuntimeException {
	// return (T) _min(scope);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see msi.gama.interfaces.IGamaContainer#product()
	// */
	// @Override
	// public final Object product(final IScope scope) throws
	// GamaRuntimeException {
	// return _product(scope);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see msi.gama.interfaces.IGamaContainer#sum()
	// */
	// @Override
	// public final Object sum(final IScope scope) throws GamaRuntimeException {
	// return _sum(scope);
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#isEmpty()
	 */
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

	// The same but with an index (this index represents the old notion of
	// parameter where it is needed.
	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final T value) {}

	// set, that takes a mandatory index (also replaces the parameter)
	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final T value) {
		if (index instanceof Integer) {
			setNthElement(scope, (int) index, value);
			return;
		}
		final ILocation p = buildIndex(scope, index);
		set(scope, (int) p.getX(), (int) p.getY(), value);

	}

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
		// for ( int i = 0, n = result.size(); i < n; i++ ) {
		// result.set(i, contentsType.cast(scope, result.get(i), null, false));
		// }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IValue#matrixValue(msi.gama.interfaces.IScope, msi.gama.util.GamaPoint)
	 */
	@Override
	public final IMatrix<T> matrixValue(final IScope scope, final IType type, final ILocation size, final boolean copy)
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
	public IList<IList<T>> getRowsList(final IScope scope) {
		return getLines(scope, this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#getColumnsList()
	 */
	@Override
	public IList<IList<T>> getColumnsList(final IScope scope) {
		return getColumns(scope, this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#getRow(java.lang.Integer)
	 */
	@Override
	public IList<T> getRow(final IScope scope, final Integer num_line) {
		return getLine(scope, this, num_line);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IMatrix#getColumn(java.lang.Integer)
	 */
	@Override
	public IList<T> getColumn(final IScope scope, final Integer num_line) {
		return getColumn(scope, this, num_line);
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

	protected abstract IList<T> _listValue(IScope scope, IType contentsType, boolean cast);

	protected abstract IMatrix<T> _matrixValue(IScope scope, ILocation size, IType type, boolean copy);

	protected abstract void _clear();

	protected abstract boolean _removeFirst(IScope scope, T value) throws GamaRuntimeException;

	protected abstract boolean _removeAll(IScope scope, IContainer<?, T> value) throws GamaRuntimeException;

	protected abstract void _putAll(IScope scope, Object value) throws GamaRuntimeException;

	protected abstract IMatrix<T> _reverse(IScope scope) throws GamaRuntimeException;

	protected abstract boolean _isEmpty(IScope scope);

	protected abstract boolean _contains(IScope scope, Object o);

	protected abstract Integer _length(IScope scope);

	protected abstract Object _last(IScope scope);

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
		return Double.MAX_VALUE;
	}

}
