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
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;

public class GamaIntMatrix extends GamaMatrix<Integer> {

	static public GamaIntMatrix from(IScope scope, final IMatrix m) {
		if ( m instanceof GamaIntMatrix ) { return (GamaIntMatrix) m; }
		if ( m instanceof GamaObjectMatrix ) { return new GamaIntMatrix(scope, m.getCols(scope), m.getRows(scope),
			((GamaObjectMatrix) m).matrix); }
		if ( m instanceof GamaFloatMatrix ) { return new GamaIntMatrix(scope, m.getCols(scope), m.getRows(scope),
			((GamaFloatMatrix) m).matrix); }
		return null;
	}

	static public GamaIntMatrix from(IScope scope, final int c, final int r, final IMatrix m) {
		if ( m instanceof GamaFloatMatrix ) { return new GamaIntMatrix(scope, c, r, ((GamaFloatMatrix) m).matrix); }
		if ( m instanceof GamaObjectMatrix ) { return new GamaIntMatrix(scope, c, r, ((GamaObjectMatrix) m).matrix); }
		if ( m instanceof GamaIntMatrix ) { return new GamaIntMatrix(scope, c, r, ((GamaIntMatrix) m).matrix); }
		return null;
	}

	// In case the matrix represents a discretization of an environment
	private double cellSize;

	int[] matrix;

	public GamaIntMatrix(IScope scope, final GamaPoint p) {
		this(scope, (int) p.x, (int) p.y);
	}

	public GamaIntMatrix(IScope scope, final int cols, final int rows) {
		super(scope, cols, rows);
		matrix = new int[cols * rows];
	}

	public GamaIntMatrix(IScope scope, final int cols, final int rows, final double[] objects) {
		this(scope, cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(IScope scope, final int cols, final int rows, final int[] objects) {
		this(scope, cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(IScope scope, final int cols, final int rows, final Object[] objects) {
		this(scope, cols, rows);
		for ( int i = 0, n = Math.min(objects.length, rows * cols); i < n; i++ ) {
			matrix[i] = Cast.asInt(scope, objects[i]);
		}
	}

	public GamaIntMatrix(IScope scope, final int[] mat) {
		super(scope, 1, mat.length);
		matrix = mat;
	}

	public GamaIntMatrix(IScope scope, final List objects, final boolean flat, final GamaPoint preferredSize) {
		super(scope, objects, flat, preferredSize);
		matrix = new int[numRows * numCols];
		if ( preferredSize != null ) {
			for ( int i = 0, stop = Math.min(matrix.length, objects.size()); i < stop; i++ ) {
				matrix[i] = Cast.asInt(null, objects.get(i));
			}
		} else if ( flat || GamaMatrix.isFlat(objects) ) {
			for ( int i = 0, stop = objects.size(); i < stop; i++ ) {
				matrix[i] = Cast.asInt(null, objects.get(i));
			}
		} else {
			for ( int i = 0; i < numRows; i++ ) {
				for ( int j = 0; j < numCols; j++ ) {
					set(scope, j, i, Cast.asInt(null, ((List) objects.get(j)).get(i)));
				}
			}
		}
	}

	public GamaIntMatrix(IScope scope, final Object[] mat) {
		this(scope, 1, mat.length);
		for ( int i = 0; i < mat.length; i++ ) {
			matrix[i] = Cast.asInt(null, mat[i]);
		}
	}

	@Override
	public void _clear() {
		Arrays.fill(matrix, 0);
	}

	@Override
	protected void _putAll(IScope scope, final Object o, final Object param) throws GamaRuntimeException {
		// TODO Verify type
		fillWith((Integer) o);
	}

	@Override
	protected boolean _contains(IScope scope, final Object o) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( o instanceof Integer && matrix[i] == ((Integer) o).intValue() ) { return true; }
		}
		return false;
	}

	@Override
	public Integer _first(IScope scope) {
		if ( matrix.length == 0 ) { return 0; }
		return matrix[0];
	}

	@Override
	public Integer _last(IScope scope) {
		if ( matrix.length == 0 ) { return 0; }
		return matrix[matrix.length - 1];
	}

	@Override
	public Integer _length(IScope scope) {
		return matrix.length;
	}

	// @Override
	// public Integer _max(final IScope scope) {
	// Integer max = Integer.MIN_VALUE;
	// for ( int i = 0; i < matrix.length; i++ ) {
	// if ( matrix[i] > max ) {
	// max = Integer.valueOf(matrix[i]);
	// }
	// }
	// return max;
	// }
	//
	// @Override
	// public Integer _min(final IScope scope) {
	// Integer min = Integer.MAX_VALUE;
	// for ( int i = 0; i < matrix.length; i++ ) {
	// if ( matrix[i] < min ) {
	// min = Integer.valueOf(matrix[i]);
	// }
	// }
	// return min;
	// }
	//
	// @Override
	// public Double _product(final IScope scope) {
	// double result = 1.0;
	// for ( int i = 0, n = matrix.length; i < n; i++ ) {
	// result *= matrix[i];
	// }
	// return result;
	// }
	//
	// @Override
	// public Integer _sum(final IScope scope) {
	// int result = 0;
	// for ( int i = 0, n = matrix.length; i < n; i++ ) {
	// result += matrix[i];
	// }
	// return result;
	// }

	@Override
	public boolean _isEmpty(IScope scope) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] != 0d ) { return false; }
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
		return new GamaIntMatrix(scope, cols, rows, matrix);
	}

	@Override
	public IMatrix _reverse(IScope scope) throws GamaRuntimeException {
		IMatrix result = new GamaIntMatrix(scope, numRows, numCols);
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				result.set(scope, j, i, get(scope, i, j));
			}
		}
		return result;
	}

	@Override
	public GamaIntMatrix copy(IScope scope) {
		return new GamaIntMatrix(scope, numCols, numRows, matrix);
	}

	@Override
	public boolean equals(final Object m) {
		if ( this == m ) { return true; }
		if ( !(m instanceof GamaIntMatrix) ) { return false; }
		GamaIntMatrix mat = (GamaIntMatrix) m;
		return Arrays.equals(this.matrix, mat.matrix);
	}

	@Override
	public int hashCode() {
		return matrix.hashCode();
	}

	public void fillWith(final int o) {
		Arrays.fill(matrix, o);
	}

	@Override
	public Integer get(IScope scope, final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return 0; }
		return matrix[row * numCols + col];
	}

	public double getSize() {
		return cellSize;
	}

	// @Override
	public void set(IScope scope, final int col, final int row, final int obj) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return; }
		matrix[row * numCols + col] = obj;
	}

	@Override
	public void set(IScope scope, final int col, final int row, final Object obj) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return; }
		matrix[row * numCols + col] = Cast.asInt(scope, obj);
		// put(col, row, Cast.asInt(GAMA.getDefaultScope(), obj).intValue());
	}

	public boolean remove(final int o) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] == o ) {
				matrix[i] = 0;
				return true;
			}
		}
		return false;
	}

	@Override
	public Integer remove(IScope scope, final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return 0; }
		final int o = matrix[row * numCols + col];
		matrix[row * numCols + col] = 0;
		return o;
	}

	@Override
	public boolean _removeFirst(IScope scope, final Integer o) {
		return remove(o.intValue());
	}

	public boolean removeAll(final int o) {
		boolean removed = false;
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] == o ) {
				matrix[i] = 0;
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public boolean _removeAll(IScope scope, final IContainer<?, Integer> list) {
		for ( Integer o : list ) {
			removeAll(o.intValue());
		}
		// TODO Make a test to verify the return
		return true;
	}

	public void setCellSize(final double size) {
		cellSize = size;
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		matrix = randomAgent.shuffle(matrix);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		sb.append('[');
		GAMA.run(new InScope.Void() {

			@Override
			public void process(IScope scope) {
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
		return new GamaList(this.matrix).toGaml() + " as matrix";
	}

	//
	// @Override
	// public String toJava() {
	// return "GamaMatrixType.from(" + Cast.toJava(new GamaList(this.matrix)) + ", false)";
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	// @Override
	// public boolean checkValue(final Object value) {
	// return value instanceof Integer;
	// }
}
