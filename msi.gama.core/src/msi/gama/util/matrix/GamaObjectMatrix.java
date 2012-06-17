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
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;

public class GamaObjectMatrix extends GamaMatrix<Object> {

	static public GamaObjectMatrix from(final int c, final int r, final IMatrix m) {
		if ( m instanceof GamaFloatMatrix ) { return new GamaObjectMatrix(c, r,
			((GamaFloatMatrix) m).matrix); }
		if ( m instanceof GamaObjectMatrix ) { return new GamaObjectMatrix(c, r,
			((GamaObjectMatrix) m).matrix); }
		if ( m instanceof GamaIntMatrix ) { return new GamaObjectMatrix(c, r,
			((GamaIntMatrix) m).matrix); }
		return null;
	}

	/** The matrix. */
	protected Object[] matrix;

	public GamaObjectMatrix(final ILocation p) {
		this((int) p.getX(), (int) p.getY());
	}

	public GamaObjectMatrix(final int cols, final int rows) {
		super(cols, rows);
		matrix = new Object[cols * rows];
	}

	public GamaObjectMatrix(final int cols, final int rows, final double[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaObjectMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaObjectMatrix(final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaObjectMatrix(final List objects, final boolean flat, final ILocation preferredSize) {
		super(objects, flat, preferredSize);
		matrix = new Object[numRows * numCols];

		if ( preferredSize != null ) {
			for ( int i = 0, stop = Math.min(matrix.length, objects.size()); i < stop; i++ ) {
				matrix[i] = objects.get(i);
			}
		} else if ( flat || GamaMatrix.isFlat(objects) ) {
			for ( int i = 0, stop = objects.size(); i < stop; i++ ) {
				matrix[i] = objects.get(i);
			}
		} else {
			for ( int i = 0; i < numRows; i++ ) {
				for ( int j = 0; j < numCols; j++ ) {
					set(j, i, ((List) objects.get(j)).get(i));
				}
			}
		}
	}

	public GamaObjectMatrix(final Object[] mat) {
		super(1, mat.length);
		matrix = mat;
	}

	@Override
	public void _clear() {
		Arrays.fill(matrix, null);
	}

	@Override
	public boolean _contains(final Object o) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i].equals(o) ) { return true; }
		}
		return false;
	}

	@Override
	public Object _first() {
		if ( matrix.length == 0 ) { return null; }
		return matrix[0];
	}

	@Override
	public Object _last() {
		if ( matrix.length == 0 ) { return null; }
		return matrix[matrix.length - 1];
	}

	@Override
	public Integer _length() {
		return matrix.length;
	}

	@Override
	public Double _max(final IScope scope) {
		Double max = Double.MIN_VALUE;
		for ( int i = 0; i < matrix.length; i++ ) {
			Object o = matrix[i];
			if ( o instanceof Number && ((Number) o).doubleValue() > max ) {
				max = Double.valueOf(((Number) o).doubleValue());
			}
		}
		return max;
	}

	@Override
	public Double _min(final IScope scope) {
		Double min = Double.MAX_VALUE;
		for ( int i = 0; i < matrix.length; i++ ) {
			Object o = matrix[i];
			if ( o instanceof Number && ((Number) o).doubleValue() < min ) {
				min = Double.valueOf(((Number) o).doubleValue());
			}
		}
		return min;
	}

	@Override
	public Double _product(final IScope scope) {
		Double result = 1.0;
		for ( int i = 0, n = matrix.length; i < n; i++ ) {
			Object d = matrix[i];
			if ( d instanceof Number ) {
				result *= ((Number) d).doubleValue();
			}
		}
		return result;
	}

	@Override
	public Double _sum(final IScope scope) {
		Double result = 0.0;
		for ( int i = 0, n = matrix.length; i < n; i++ ) {
			Object d = matrix[i];
			if ( d instanceof Number ) {
				result += ((Number) d).doubleValue();
			}
		}
		return result;
	}

	@Override
	public boolean _isEmpty() {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] != null ) { return false; }
		}
		return true;
	}

	@Override
	protected GamaList _listValue(final IScope scope) {
		return new GamaList(matrix);
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize) {
		if ( preferredSize == null ) { return this; }
		final int cols = (int) preferredSize.getX();
		final int rows = (int) preferredSize.getY();
		return new GamaObjectMatrix(cols, rows, matrix);
	}

	@Override
	public IMatrix _reverse() throws GamaRuntimeException {
		IMatrix result = new GamaObjectMatrix(numRows, numCols);
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				result.set(j, i, get(i, j));
			}
		}
		return result;
	}

	@Override
	public GamaObjectMatrix copy() {
		return new GamaObjectMatrix(numCols, numRows, matrix);
	}

	@Override
	public boolean equals(final Object m) {
		if ( this == m ) { return true; }
		if ( !(m instanceof GamaObjectMatrix) ) { return false; }
		GamaObjectMatrix mat = (GamaObjectMatrix) m;
		return Arrays.equals(this.matrix, mat.matrix);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void _putAll(final Object o, final Object param) {
		Arrays.fill(matrix, o);
	}

	@Override
	public Object get(final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return null; }
		return matrix[row * numCols + col];
	}

	@Override
	public void set(final int col, final int row, final Object obj) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return; }
		matrix[row * numCols + col] = obj;
	}

	@Override
	public Object remove(final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return null; }
		final Object o = matrix[row * numCols + col];
		matrix[row * numCols + col] = null;
		return o;
	}

	@Override
	public boolean _removeFirst(final Object o) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i].equals(o) ) {
				matrix[i] = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean _removeAll(final IContainer<?, Object> list) throws GamaRuntimeException {
		boolean removed = false;
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( list.contains(matrix[i]) ) {
				matrix[i] = null;
				removed = true;
			}
		}
		// TODO Make a test to verify the return
		return removed;
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		matrix = randomAgent.shuffle(matrix);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		sb.append('[');
		for ( int row = 0; row < numRows; row++ ) {
			for ( int col = 0; col < numCols; col++ ) {
				sb.append(get(col, row));
				if ( col < numCols - 1 ) {
					sb.append(',');
				}
			}
			if ( row < numRows - 1 ) {
				sb.append(';');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String toGaml() {
		return new GamaList(this.matrix).toGaml() + " as matrix";
	}

	// @Override
	// public String toJava() {
	// return "GamaMatrixType.from(" + Cast.toJava(new GamaList(this.matrix)) + ", false)";
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return true; // everything accepted including null
	}

}
