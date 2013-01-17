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
package msi.gama.util.matrix;

import java.util.*;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 18 nov. 2008
 * 
 * Abstract implementation of IMatrix, superclass of all matrices in GAML. Accessed by x = cols and
 * y = rows.
 * 
 * @todo Description
 */

public abstract class GamaMatrix<T> implements IMatrix<T> {

	public static IList getLines(final IMatrix m) {
		final GamaList result = new GamaList();
		for ( int i = 0; i < m.getRows(); i++ ) {
			result.add(getLine(m, i));
		}
		return result;
	}

	public static IList getColumns(final IMatrix m) {
		final GamaList result = new GamaList();
		for ( int i = 0, n = m.getCols(); i < n; i++ ) {
			result.add(getColumn(m, i));
		}
		return result;
	}

	public static IList getColumn(final IMatrix m, final Integer num_col) {
		final GamaList result = new GamaList();
		if ( num_col >= m.getCols() || num_col < 0 ) { return result; }
		for ( int i = 0; i < m.getRows(); i++ ) {
			result.add(m.get(num_col, i));
		}
		return result;
	}

	public static IList getLine(final IMatrix m, final Integer num_line) {
		final GamaList result = new GamaList();
		if ( num_line >= m.getRows() || num_line < 0 ) { return result; }
		for ( int i = 0; i < m.getCols(); i++ ) {
			result.add(m.get(i, num_line));
		}
		return result;
	}

	public static IMatrix opPlus(final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw new GamaRuntimeException(
			"ATTENTION : Matrix additions not implemented. Returns nil for the moment");
	}

	public static IMatrix opMinus(final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw new GamaRuntimeException(
			"ATTENTION : Matrix substractions not implemented. Returns nil for the moment");
	}

	public static IMatrix opTimes(final IMatrix a, final IMatrix b) throws GamaRuntimeException {
		throw new GamaRuntimeException(
			"ATTENTION : Matrix multiplications not implemented. Returns nil for the moment");
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
	public int getRows() {
		return numRows;
	}

	@Override
	public int getCols() {
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
	protected GamaMatrix(final List objects, final boolean flat, final ILocation preferredSize) {
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

	@Override
	public T get(final IScope scope, final ILocation p) {
		double px = p.getX();
		double py = p.getY();
		if ( px > numCols - 1 || px < 0 ) { return null; }
		if ( py > numRows - 1 || py < 0 ) { return null; }
		return get((int) px, (int) py);
	}

	@Override
	public T getFromIndicesList(final IScope scope, final IList indices)
		throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty() ) { return null; }
		int size = indices.size();
		if ( size == 1 ) { return get(scope, Cast.asPoint(scope, indices.get(0))); }
		int px = Cast.asInt(scope, indices.get(0));
		int py = Cast.asInt(scope, indices.get(1));
		if ( px > numCols - 1 || px < 0 ) { return null; }
		if ( py > numRows - 1 || py < 0 ) { return null; }
		return get(px, py);
	}

	@Override
	public abstract T get(final int col, final int row);

	@Override
	public abstract void set(final int col, final int row, final Object obj)
		throws GamaRuntimeException;

	// @Override
	// public void put(final int col, final int row, final double obj) throws GamaRuntimeException {
	// put(col, row, new Double(obj));
	// }

	// @Override
	// public void put(final int col, final int row, final int obj) throws GamaRuntimeException {
	// put(col, row, new Integer(obj));
	// }

	@Override
	public abstract Object remove(final int col, final int row);

	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return matrixValue(scope, null);
	}

	@Override
	public ILocation getDimensions() {
		return new GamaPoint(numCols, numRows);
	}

	@Override
	public String stringValue() throws GamaRuntimeException {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		for ( int line = 0; line < numRows; line++ ) {
			for ( int col = 0; col < numCols; col++ ) {
				sb.append(Cast.asString(GAMA.getDefaultScope(), get(col, line)));
				if ( col != numCols - 1 ) {
					sb.append(';');
				}
			}
			sb.append(java.lang.System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	@Override
	public IType type() {
		return Types.get(IType.MATRIX);
	}

	@Override
	public GamaMap mapValue(final IScope scope) {
		GamaMap result = new GamaMap();
		for ( int i = 0; i < numRows; i++ ) {
			// in case the matrix rows < 2, put null in value
			result.put(get(0, i), get(1, i));
		}
		return result;

	}

	@Override
	public final void add(final ILocation index, final T value, final Object param)
		throws GamaRuntimeException {}

	@Override
	public final void add(final T value, final Object param) throws GamaRuntimeException {}

	@Override
	public final Object removeAt(final ILocation p) throws GamaRuntimeException {
		// Normally never called as matrices are of fixed length
		return remove((int) p.getX(), (int) p.getY());
	}

	@Override
	public final void put(final ILocation p, final T value, final Object param)
		throws GamaRuntimeException {
		set((int) p.getX(), (int) p.getY(), value);
	}

	@Override
	public abstract IMatrix copy() throws GamaRuntimeException;

	public static boolean isFlat(final List val) {
		for ( int i = 0; i < val.size(); i++ ) {
			if ( val.get(i) instanceof List ) { return false; }
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public final boolean checkIndex(final Object index) {
		return index instanceof ILocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public final boolean checkBounds(final ILocation index, final boolean forAdding) {
		int x = (int) index.getX();
		int y = (int) index.getY();
		return x >= 0 && x < numCols && y >= 0 && y < numRows;
	}

	@Override
	public final boolean isFixedLength() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#contains(java.lang.Object)
	 */
	@Override
	public final boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return _contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#first()
	 */
	@Override
	public final T first(final IScope scope) throws GamaRuntimeException {
		return (T) _first();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#last()
	 */
	@Override
	public final T last(final IScope scope) throws GamaRuntimeException {
		return (T) _last();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#length()
	 */
	@Override
	public final int length(final IScope scope) {
		return _length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#max()
	 */
	@Override
	public final T max(final IScope scope) throws GamaRuntimeException {
		return (T) _max(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#min()
	 */
	@Override
	public final T min(final IScope scope) throws GamaRuntimeException {
		return (T) _min(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#product()
	 */
	@Override
	public final Object product(final IScope scope) throws GamaRuntimeException {
		return _product(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#sum()
	 */
	@Override
	public final Object sum(final IScope scope) throws GamaRuntimeException {
		return _sum(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isEmpty()
	 */
	@Override
	public final boolean isEmpty(final IScope scope) {
		return _isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#reverse()
	 */
	@Override
	public final IContainer<ILocation, T> reverse(final IScope scope) throws GamaRuntimeException {
		return _reverse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#removeFirst(java.lang.Object)
	 */
	@Override
	public final boolean removeFirst(final T value) throws GamaRuntimeException {
		return _removeFirst(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#removeAll(java.lang.Object)
	 */
	@Override
	public final boolean removeAll(final IContainer value) throws GamaRuntimeException {
		return _removeAll(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final ILocation index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#putAll(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void putAll(final T value, final Object param) throws GamaRuntimeException {
		_putAll(value, param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#clear()
	 */
	@Override
	public final void clear() throws GamaRuntimeException {
		_clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#listValue(msi.gama.interfaces.IScope)
	 */
	@Override
	public final IList listValue(final IScope scope) {
		return _listValue(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IValue#matrixValue(msi.gama.interfaces.IScope,
	 * msi.gama.util.GamaPoint)
	 */
	@Override
	public final IMatrix matrixValue(final IScope scope, final ILocation size)
		throws GamaRuntimeException {
		return _matrixValue(scope, size);
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return listValue(null).iterator();
	}

	@Override
	public Iterable<T> iterable(final IScope scope) {
		return listValue(scope);
	}

	/**
	 * @see msi.gama.interfaces.IMatrix#getRowsList()
	 */
	@Override
	public IList<IList<T>> getRowsList() {
		return getLines(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IMatrix#getColumnsList()
	 */
	@Override
	public IList<IList<T>> getColumnsList() {
		return getColumns(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IMatrix#getRow(java.lang.Integer)
	 */
	@Override
	public IList<T> getRow(final Integer num_line) {
		return getLine(this, num_line);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IMatrix#getColumn(java.lang.Integer)
	 */
	@Override
	public IList<T> getColumn(final Integer num_line) {
		return getColumn(this, num_line);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IMatrix#plus(msi.gama.interfaces.IMatrix)
	 */
	@Override
	public IMatrix plus(final IMatrix other) throws GamaRuntimeException {
		return opPlus(this, other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IMatrix#times(msi.gama.interfaces.IMatrix)
	 */
	@Override
	public IMatrix times(final IMatrix other) throws GamaRuntimeException {
		return opTimes(this, other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IMatrix#minus(msi.gama.interfaces.IMatrix)
	 */
	@Override
	public IMatrix minus(final IMatrix other) throws GamaRuntimeException {
		return opMinus(this, other);
	}

	@Override
	public T any(final IScope scope) {
		int x = GAMA.getRandom().between(0, numCols - 1);
		int y = GAMA.getRandom().between(0, numRows - 1);
		return this.get(x, y);
	}

	// PRIVATE METHODS INTENDED TO ALLOW MATRICES TO IMPLEMENT GAML OPERATORS
	// POLYMORPHISM IS NOT REALLY SUPPORTED BY THE GAML COMPILER AND IS TAKEN
	// IN CHARGE BY JAVA THROUGH THIS TRICK.

	protected abstract IList _listValue(IScope scope);

	protected abstract IMatrix _matrixValue(IScope scope, ILocation size);

	protected abstract void _clear();

	protected abstract boolean _removeFirst(T value) throws GamaRuntimeException;

	protected abstract boolean _removeAll(IContainer<?, T> value) throws GamaRuntimeException;

	protected abstract void _putAll(T value, Object param) throws GamaRuntimeException;

	protected abstract IContainer<ILocation, T> _reverse() throws GamaRuntimeException;

	protected abstract boolean _isEmpty();

	protected abstract boolean _contains(Object o);

	protected abstract Object _min(IScope scope);

	protected abstract Object _max(IScope scope);

	protected abstract Object _sum(IScope scope) throws GamaRuntimeException;

	protected abstract Object _product(IScope scope) throws GamaRuntimeException;

	protected abstract Integer _length();

	protected abstract Object _last();

	protected abstract Object _first();

}
