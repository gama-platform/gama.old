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
package msi.gama.util.matrix;

import java.util.List;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.apache.commons.lang.ArrayUtils;

/**
 * Written by drogoul Modified on 18 nov. 2008
 * 
 * Abstract implementation of IMatrix, superclass of all matrices in GAML. Accessed by x = cols and
 * y = rows.
 * 
 * @todo Description
 */

public abstract class GamaMatrix<T> implements IMatrix<T> {

	@Override
	public IContainer<?, ILocation> buildIndexes(final IScope scope, final IContainer value,
		final IContainerType containerType) {
		IList<ILocation> result = new GamaList();
		for ( Object o : value.iterable(scope) ) {
			result.add(buildIndex(scope, o, containerType));
		}
		return result;
	}

	@Override
	public T buildValue(final IScope scope, final Object object, final IContainerType containerType) {
		return (T) containerType.getContentType().cast(scope, object, null);
	}

	@Override
	public IContainer<?, T> buildValues(final IScope scope, final IContainer objects, final IContainerType containerType) {
		return containerType.cast(scope, objects, null);
	}

	@Override
	public ILocation buildIndex(final IScope scope, final Object object, final IContainerType containerType) {
		return GamaPointType.staticCast(scope, object);
	}

	public static IList getLines(final IScope scope, final IMatrix m) {
		final GamaList result = new GamaList();
		for ( int i = 0; i < m.getRows(scope); i++ ) {
			result.add(getLine(scope, m, i));
		}
		return result;
	}

	public static IList getColumns(final IScope scope, final IMatrix m) {
		final GamaList result = new GamaList();
		for ( int i = 0, n = m.getCols(scope); i < n; i++ ) {
			result.add(getColumn(scope, m, i));
		}
		return result;
	}

	public static IList getColumn(final IScope scope, final IMatrix m, final Integer num_col) {
		final GamaList result = new GamaList();
		if ( num_col >= m.getCols(scope) || num_col < 0 ) { return result; }
		for ( int i = 0; i < m.getRows(scope); i++ ) {
			result.add(m.get(scope, num_col, i));
		}
		return result;
	}

	public static IList getLine(final IScope scope, final IMatrix m, final Integer num_line) {
		final GamaList result = new GamaList();
		if ( num_line >= m.getRows(scope) || num_line < 0 ) { return result; }
		for ( int i = 0; i < m.getCols(scope); i++ ) {
			result.add(m.get(scope, i, num_line));
		}
		return result;
	}

	@Override
	public final String toGaml() {
		return getRowsList(null).toGaml() + " as matrix";
	}

	public static IMatrix opPlus(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException.error("ATTENTION : Matrix additions not implemented. Returns nil for the moment");
	}

	public static IMatrix opMinus(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException
			.error("ATTENTION : Matrix substractions not implemented. Returns nil for the moment");
	}

	public static IMatrix opTimes(final IScope scope, final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw GamaRuntimeException
			.error("ATTENTION : Matrix multiplications not implemented. Returns nil for the moment");
	}

	public int numRows;

	public int numCols;

	/**
	 * Cols, rows instead of row cols because intended to work with xSize and ySize dimensions.
	 * 
	 * @param cols the cols
	 * @param rows the rows
	 */

	@Override
	public int getRows(final IScope scope) {
		return numRows;
	}

	@Override
	public int getCols(final IScope scope) {
		return numCols;
	}

	protected GamaMatrix(final int cols, final int rows) {
		numRows = rows;
		numCols = cols;
	}

	/**
	 * Instantiates a new gama matrix.
	 * 
	 * @param objects the objects
	 * @param flat the flat
	 * @param preferredSize the preferred size
	 */
	protected GamaMatrix(final IScope scope, final List objects, final boolean flat, final ILocation preferredSize) {
		if ( preferredSize != null ) {
			numRows = (int) preferredSize.getY();
			numCols = (int) preferredSize.getX();
		} else if ( objects == null || objects.isEmpty() ) {
			numRows = 1;
			numCols = 1;
		} else if ( flat || GamaMatrix.isFlat(objects) ) {
			numRows = 1;
			numCols = objects.size();
		} else {
			numCols = objects.size();
			numRows = ((List) objects.get(0)).size();
		}
	}

	/**
	 * Take two matrices (with the same number of columns) and create a big matrix putting the second matrix under the
	 * first matrix
	 * 
	 * @param two matrix to concatenate
	 * @return the matrix concatenated
	 */

	@operator(value = { "append_vertically" }, category={IOperatorCategory.MATRIX})
	@doc(value = "A matrix resulting from the concatenation of the columns  of the two given matrices", masterDoc=true,
		examples = { @example(value="opAppendVertically([[1,2,3],[4,5,6]],[[7,8,9],[10,11,12]])", equals="[[1,2,3,4,5,6],[7,8,9,10,11,12]]") })
	public static IMatrix opAppendVertically(final IScope scope, final GamaObjectMatrix a, final GamaObjectMatrix b) {
		Object[] ma = a.getMatrix();
		Object[] mb = b.getMatrix();
		Object[] mab = ArrayUtils.addAll(ma, mb);

		GamaObjectMatrix fl = new GamaObjectMatrix(a.getCols(scope), a.getRows(scope) + b.getRows(scope), mab);

		// throw GamaRuntimeException.error("ATTENTION : Matrix additions not implemented. Returns nil for the moment");
		return fl;
	}

	/**
	 * Take two matrices (with the same number of rows) and create a big matrix putting the second matrix on the right
	 * side of the first matrix
	 * 
	 * @param two matrix to concatenate
	 * @return the matrix concatenated
	 */
	@operator(value = { "append_horizontally" }, category={IOperatorCategory.MATRIX})
	@doc(value = "A matrix resulting from the concatenation of the rows of the two given matrices", masterDoc=true, 
		examples = { @example(value="opAppendHorizontally([[1,2,3],[4,5,6]],[[7,8,9],[10,11,12]])", equals="[[1,2,3,4,5,6],[7,8,9,10,11,12]]") })
	public static IMatrix opAppendHorizontally(final IScope scope, final GamaObjectMatrix a, final GamaObjectMatrix b) {

		IMatrix aprime = new GamaObjectMatrix(a.getRows(scope), a.getCols(scope));
		aprime = a._reverse(scope);
		// System.out.println("aprime = " + aprime);
		IMatrix bprime = new GamaObjectMatrix(b.getRows(scope), b.getCols(scope));
		bprime = b._reverse(scope);
		// System.out.println("bprime = " + bprime);
		IMatrix c = opAppendVertically(scope, (GamaObjectMatrix) aprime, (GamaObjectMatrix) bprime);
		// System.out.println("c = " + c);
		IMatrix cprime = ((GamaObjectMatrix) c)._reverse(scope);
		// System.out.println("cprime = " + cprime);
		return cprime;
	}

	@Override
	public T get(final IScope scope, final ILocation p) {
		final double px = p.getX();
		final double py = p.getY();
		if ( px > numCols - 1 || px < 0 ) { return null; }
		if ( py > numRows - 1 || py < 0 ) { return null; }
		return get(scope, (int) px, (int) py);
	}

	@Override
	public T getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty() ) { return null; }
		final int size = indices.size();
		if ( size == 1 ) {
			Object index = indices.get(0);
			if ( index instanceof GamaPoint ) {
				return get(scope, (GamaPoint) index);
			} else {
				return this.getNthElement(Cast.asInt(scope, index));
			}
		}
		final int px = Cast.asInt(scope, indices.get(0));
		final int py = Cast.asInt(scope, indices.get(1));
		if ( px > numCols - 1 || px < 0 ) { return null; }
		if ( py > numRows - 1 || py < 0 ) { return null; }
		return get(scope, px, py);
	}

	/**
	 * @param asInt
	 * @return
	 */
	protected abstract T getNthElement(Integer index);

	@Override
	public abstract T get(IScope scope, final int col, final int row);

	@Override
	public abstract void set(IScope scope, final int col, final int row, final Object obj) throws GamaRuntimeException;

	@Override
	public abstract Object remove(IScope scope, final int col, final int row);

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType) throws GamaRuntimeException {
		return matrixValue(scope, contentsType, null);
	}

	@Override
	public ILocation getDimensions() {
		return new GamaPoint(numCols, numRows);
	}

	@Override
	public final String stringValue(final IScope scope) throws GamaRuntimeException {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		for ( int line = 0; line < numRows; line++ ) {
			for ( int col = 0; col < numCols; col++ ) {
				sb.append(Cast.asString(scope, get(scope, col, line)));
				if ( col != numCols - 1 ) {
					sb.append(';');
				}
			}
			sb.append(java.lang.System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	//
	// @Override
	// public IType type() {
	// return Types.get(IType.MATRIX);
	// }

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType) {
		final GamaMap result = new GamaMap();
		for ( int i = 0; i < numRows; i++ ) {
			// in case the matrix rows < 2, put null in value
			result.put(GamaType.toType(scope, get(scope, 0, i), keyType),
				GamaType.toType(scope, get(scope, 1, i), contentsType));
		}
		return result;

	}

	//
	// @Override
	// public final Object removeAt(IScope scope, final ILocation p) throws GamaRuntimeException {
	// // Normally never called as matrices are of fixed length
	// return remove(scope, (int) p.getX(), (int) p.getY());
	// }

	// @Override
	// public final void put(IScope scope, final ILocation p, final T value, final Object param)
	// throws GamaRuntimeException {
	// set(scope, (int) p.getX(), (int) p.getY(), value);
	// }
	//
	@Override
	public final IMatrix copy(final IScope scope) {
		return copy(scope, getDimensions());
	}

	@Override
	public abstract IMatrix copy(IScope scope, ILocation size);

	public static boolean isFlat(final List val) {
		for ( int i = 0; i < val.size(); i++ ) {
			if ( val.get(i) instanceof List ) { return false; }
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public final boolean checkBounds(final IScope scope, final Object object, final boolean forAdding) {
		if ( object instanceof ILocation ) {
			ILocation index = (ILocation) object;
			final int x = (int) index.getX();
			final int y = (int) index.getY();
			return x >= 0 && x < numCols && y >= 0 && y < numRows;
		} else if ( object instanceof IContainer ) {
			for ( Object o : ((IContainer) object).iterable(scope) ) {
				if ( !checkBounds(scope, o, forAdding) ) { return false; }
			}
		}
		return false;
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
	// public final Object product(final IScope scope) throws GamaRuntimeException {
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
	public final IContainer<ILocation, T> reverse(final IScope scope) throws GamaRuntimeException {
		return _reverse(scope);
	}

	// 09/01/14:Trying to keep the interface simple.
	// Three methods for add and put operations:
	// The simple method, that simply contains the object to add
	@Override
	public void addValue(final IScope scope, final T value) {}

	// The same but with an index (this index represents the old notion of parameter where it is needed.
	@Override
	public void addValueAtIndex(final IScope scope, final ILocation index, final T value) {}

	// Put, that takes a mandatory index (also replaces the parameter)
	@Override
	public void setValueAtIndex(final IScope scope, final ILocation index, final T value) {
		if ( index instanceof ILocation ) {
			ILocation p = index;
			set(scope, (int) p.getX(), (int) p.getY(), value);
		}

	}

	// Then, methods for "all" operations
	// Adds the values if possible, without replacing existing ones
	@Override
	public void addVallues(final IScope scope, final IContainer values) {}

	// Adds this value to all slots (if this operation is available), otherwise replaces the values with this one
	@Override
	public void setAllValues(final IScope scope, final T value) {
		_putAll(scope, value, null);
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {};

	@Override
	public void removeIndex(final IScope scope, final Object index) {};

	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> indexes) {}

	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {};

	@Override
	public void removeAllOccurencesOfValue(final IScope scope, final Object value) {};

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#listValue(msi.gama.interfaces.IScope)
	 */
	@Override
	public final IList listValue(final IScope scope, final IType contentsType) {
		// WARNING : Double copy of the list
		return _listValue(scope).listValue(scope, contentsType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#matrixValue(msi.gama.interfaces.IScope,
	 * msi.gama.util.GamaPoint)
	 */
	@Override
	public final IMatrix matrixValue(final IScope scope, final IType type, final ILocation size)
		throws GamaRuntimeException {
		return _matrixValue(scope, size, type);
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
	public IMatrix minus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		return opMinus(scope, this, other);
	}

	@Override
	public T anyValue(final IScope scope) {
		final RandomUtils r = GAMA.getRandom();
		final int x = r.between(0, numCols - 1);
		final int y = r.between(0, numRows - 1);
		return this.get(scope, x, y);
	}

	// PRIVATE METHODS INTENDED TO ALLOW MATRICES TO IMPLEMENT GAML OPERATORS
	// POLYMORPHISM IS NOT REALLY SUPPORTED BY THE GAML COMPILER AND IS TAKEN
	// IN CHARGE BY JAVA THROUGH THIS TRICK.

	protected abstract IList _listValue(IScope scope);

	protected abstract IMatrix _matrixValue(IScope scope, ILocation size, IType type);

	protected abstract void _clear();

	protected abstract boolean _removeFirst(IScope scope, T value) throws GamaRuntimeException;

	protected abstract boolean _removeAll(IScope scope, IContainer<?, T> value) throws GamaRuntimeException;

	protected abstract void _putAll(IScope scope, Object value, Object param) throws GamaRuntimeException;

	protected abstract IContainer<ILocation, T> _reverse(IScope scope) throws GamaRuntimeException;

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
	public IMatrix matrixMultiplication(final IScope scope, final IMatrix other) throws GamaRuntimeException {
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

}
