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

import java.util.*;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import org.apache.commons.lang.ArrayUtils;
import com.google.common.collect.ImmutableList;

public class GamaObjectMatrix extends GamaMatrix<Object> {

	static public GamaObjectMatrix from(final int c, final int r, final IMatrix m) {
		if ( m instanceof GamaFloatMatrix ) { return new GamaObjectMatrix(c, r, ((GamaFloatMatrix) m).getMatrix()); }
		if ( m instanceof GamaObjectMatrix ) { return new GamaObjectMatrix(c, r, ((GamaObjectMatrix) m).getMatrix()); }
		if ( m instanceof GamaIntMatrix ) { return new GamaObjectMatrix(c, r, ((GamaIntMatrix) m).matrix); }
		return null;
	}

	/** The matrix. */
	private Object[] matrix;

	public GamaObjectMatrix(final ILocation p) {
		this((int) p.getX(), (int) p.getY());
	}

	public GamaObjectMatrix(final int cols, final int rows) {
		super(cols, rows);
		setMatrix(new Object[cols * rows]);
	}

	public GamaObjectMatrix(final int cols, final int rows, final double[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, getMatrix(), 0, Math.min(objects.length, rows * cols));
	}

	public GamaObjectMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, getMatrix(), 0, Math.min(objects.length, rows * cols));
	}

	public GamaObjectMatrix(final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, getMatrix(), 0, Math.min(objects.length, rows * cols));
	}

	public GamaObjectMatrix(final IScope scope, final List objects, final boolean flat, final ILocation preferredSize) {
		super(scope, objects, flat, preferredSize);
		setMatrix(new Object[numRows * numCols]);

		if ( preferredSize != null ) {
			for ( int i = 0, stop = Math.min(getMatrix().length, objects.size()); i < stop; i++ ) {
				getMatrix()[i] = objects.get(i);
			}
		} else if ( flat || GamaMatrix.isFlat(objects) ) {
			for ( int i = 0, stop = objects.size(); i < stop; i++ ) {
				getMatrix()[i] = objects.get(i);
			}
		} else {
			for ( int i = 0; i < numRows; i++ ) {
				for ( int j = 0; j < numCols; j++ ) {
					set(scope, j, i, ((List) objects.get(j)).get(i));
				}
			}
		}
	}

	/**
	 * Take two matrices (with the same number of columns) and create a big matrix putting the second matrix on the
	 * right side of the first matrix
	 * 
	 * @param two matrix to concatenate
	 * @return the matrix concatenated
	 */
	@operator(value = { "opAppendVertically" })
	@doc(value = "A matrix resulting from the concatenation of the columns  of the two given matrices", examples = { "opAppendVertically([1,2,3;4,5,6],[7,8,9;10,11,12]) = [1,2,3;4,5,6;7,8,9;10,11,12]" })
	public/* static */IMatrix opAppendVertically(final IScope scope, final IMatrix a, final IMatrix b) {
		Object[] ma = ((GamaObjectMatrix) a).getMatrix();
		Object[] mb = ((GamaObjectMatrix) b).getMatrix();
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
	@Override
	@operator(value = { "opAppendHorizontally" })
	@doc(value = "A matrix resulting from the concatenation of the rows of the two given matrices", examples = { "opAppendHorizontally([1,2,3;4,5,6],[7,8,9;10,11,12]) = [1,2,3,7,8,9;4,5,6,10,11,12]" })
	public/* static */IMatrix opAppendHorizontally(final IScope scope, final GamaObjectMatrix a, final GamaObjectMatrix b) {

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

	public GamaObjectMatrix(final IScope scope, final Object[] mat) {
		super(1, mat.length);
		setMatrix(mat);
	}

	@Override
	public void _clear() {
		Arrays.fill(getMatrix(), null);
	}

	@Override
	public boolean _contains(final IScope scope, final Object o) {
		for ( int i = 0; i < getMatrix().length; i++ ) {
			if ( getMatrix()[i].equals(o) ) { return true; }
		}
		return false;
	}

	@Override
	public Object _first(final IScope scope) {
		if ( getMatrix().length == 0 ) { return null; }
		return getMatrix()[0];
	}

	@Override
	public Object _last(final IScope scope) {
		if ( getMatrix().length == 0 ) { return null; }
		return getMatrix()[getMatrix().length - 1];
	}

	@Override
	public Integer _length(final IScope scope) {
		return getMatrix().length;
	}

	// @Override
	// public Double _max(final IScope scope) {
	// Double max = -Double.MAX_VALUE;
	// for ( int i = 0; i < matrix.length; i++ ) {
	// Object o = matrix[i];
	// if ( o instanceof Number && ((Number) o).doubleValue() > max ) {
	// max = Double.valueOf(((Number) o).doubleValue());
	// }
	// }
	// return max;
	// }
	//
	// @Override
	// public Double _min(final IScope scope) {
	// Double min = Double.MAX_VALUE;
	// for ( int i = 0; i < matrix.length; i++ ) {
	// Object o = matrix[i];
	// if ( o instanceof Number && ((Number) o).doubleValue() < min ) {
	// min = Double.valueOf(((Number) o).doubleValue());
	// }
	// }
	// return min;
	// }
	//
	// @Override
	// public Double _product(final IScope scope) {
	// Double result = 1.0;
	// for ( int i = 0, n = matrix.length; i < n; i++ ) {
	// Object d = matrix[i];
	// if ( d instanceof Number ) {
	// result *= ((Number) d).doubleValue();
	// }
	// }
	// return result;
	// }
	//
	// @Override
	// public Double _sum(final IScope scope) {
	// Double result = 0.0;
	// for ( int i = 0, n = matrix.length; i < n; i++ ) {
	// Object d = matrix[i];
	// if ( d instanceof Number ) {
	// result += ((Number) d).doubleValue();
	// }
	// }
	// return result;
	// }
	//
	@Override
	public boolean _isEmpty(final IScope scope) {
		for ( int i = 0; i < getMatrix().length; i++ ) {
			if ( getMatrix()[i] != null ) { return false; }
		}
		return true;
	}

	@Override
	protected GamaList _listValue(final IScope scope) {
		return new GamaList(getMatrix());
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize) {
		if ( preferredSize == null ) { return this; }
		final int cols = (int) preferredSize.getX();
		final int rows = (int) preferredSize.getY();
		return new GamaObjectMatrix(cols, rows, getMatrix());
	}

	@Override
	public IMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		final IMatrix result = new GamaObjectMatrix(numRows, numCols);
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				result.set(scope, j, i, get(scope, i, j));
			}
		}
		return result;
	}

	@Override
	public GamaObjectMatrix copy(final IScope scope) {
		return new GamaObjectMatrix(numCols, numRows, getMatrix());
	}

	@Override
	public boolean equals(final Object m) {
		if ( this == m ) { return true; }
		if ( !(m instanceof GamaObjectMatrix) ) { return false; }
		final GamaObjectMatrix mat = (GamaObjectMatrix) m;
		return Arrays.equals(this.getMatrix(), mat.getMatrix());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void _putAll(final IScope scope, final Object o, final Object param) {
		Arrays.fill(getMatrix(), o);
	}

	@Override
	public Object get(final IScope scope, final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return null; }
		return getMatrix()[row * numCols + col];
	}

	@Override
	public void set(final IScope scope, final int col, final int row, final Object obj) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return; }
		getMatrix()[row * numCols + col] = obj;
	}

	@Override
	public Object remove(final IScope scope, final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return null; }
		final Object o = getMatrix()[row * numCols + col];
		getMatrix()[row * numCols + col] = null;
		return o;
	}

	@Override
	public boolean _removeFirst(final IScope scope, final Object o) {
		for ( int i = 0; i < getMatrix().length; i++ ) {
			if ( getMatrix()[i].equals(o) ) {
				getMatrix()[i] = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean _removeAll(final IScope scope, final IContainer<?, Object> list) throws GamaRuntimeException {
		boolean removed = false;
		for ( int i = 0; i < getMatrix().length; i++ ) {
			if ( list.contains(scope, getMatrix()[i]) ) { // VERIFY NULL SCOPE
				getMatrix()[i] = null;
				removed = true;
			}
		}
		// TODO Make a test to verify the return
		return removed;
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		setMatrix(randomAgent.shuffle(getMatrix()));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		sb.append('[');
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				for ( int row = 0; row < numRows; row++ ) {
					for ( int col = 0; col < numCols; col++ ) {
						sb.append(get(scope, col, row));
						if ( col < numCols - 1 ) {
							sb.append(',');
						}
					}
					if ( row < numRows - 1 ) {
						sb.append(';');
					}
				}
			}
		});

		sb.append(']');
		return sb.toString();
	}

	@Override
	public String toGaml() {
		return new GamaList(this.getMatrix()).toGaml() + " as matrix";
	}

	/**
	 * Method iterator()
	 * @see msi.gama.util.matrix.GamaMatrix#iterator()
	 */
	@Override
	public Iterable<Object> iterable(final IScope scope) {
		return ImmutableList.copyOf(getMatrix());
	}

	protected Object[] getMatrix() {
		return matrix;
	}

	protected void setMatrix(final Object[] matrix) {
		this.matrix = matrix;
	}

}
