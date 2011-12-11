/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.matrix;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

public class GamaIntMatrix extends GamaMatrix<Integer> {

	static public GamaIntMatrix from(final IMatrix m) {
		if ( m instanceof GamaIntMatrix ) { return (GamaIntMatrix) m; }
		if ( m instanceof GamaObjectMatrix ) { return new GamaIntMatrix(m.getCols(), m.getRows(),
			((GamaObjectMatrix) m).matrix); }
		if ( m instanceof GamaFloatMatrix ) { return new GamaIntMatrix(m.getCols(), m.getRows(),
			((GamaFloatMatrix) m).matrix); }
		return null;
	}

	static public GamaIntMatrix from(final int c, final int r, final IMatrix m) {
		if ( m instanceof GamaFloatMatrix ) { return new GamaIntMatrix(c, r,
			((GamaFloatMatrix) m).matrix); }
		if ( m instanceof GamaObjectMatrix ) { return new GamaIntMatrix(c, r,
			((GamaObjectMatrix) m).matrix); }
		if ( m instanceof GamaIntMatrix ) { return new GamaIntMatrix(c, r,
			((GamaIntMatrix) m).matrix); }
		return null;
	}

	// In case the matrix represents a discretization of an environment
	private double	cellSize;

	int[]			matrix;

	public GamaIntMatrix(final GamaPoint p) {
		this((int) p.x, (int) p.y);
	}

	public GamaIntMatrix(final int cols, final int rows) {
		super(cols, rows);
		matrix = new int[cols * rows];
	}

	public GamaIntMatrix(final int cols, final int rows, final double[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		for ( int i = 0, n = Math.min(objects.length, rows * cols); i < n; i++ ) {
			matrix[i] = Cast.asInt(null, objects[i]);
		}
	}

	public GamaIntMatrix(final int[] mat) {
		super(1, mat.length);
		matrix = mat;
	}

	public GamaIntMatrix(final List objects, final boolean flat, final GamaPoint preferredSize) {
		super(objects, flat, preferredSize);
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
					put(j, i, Cast.asInt(null, ((List) objects.get(j)).get(i)));
				}
			}
		}
	}

	public GamaIntMatrix(final Object[] mat) {
		this(1, mat.length);
		for ( int i = 0; i < mat.length; i++ ) {
			matrix[i] = Cast.asInt(null, mat[i]);
		}
	}

	@Override
	public void _clear() {
		Arrays.fill(matrix, 0);
	}

	@Override
	protected void _putAll(final Integer o, final Object param) throws GamaRuntimeException {
		fillWith(o);
	}

	@Override
	protected boolean _contains(final Object o) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( o instanceof Integer && matrix[i] == ((Integer) o).intValue() ) { return true; }
		}
		return false;
	}

	@Override
	public Integer _first() {
		if ( matrix.length == 0 ) { return 0; }
		return matrix[0];
	}

	@Override
	public Integer _last() {
		if ( matrix.length == 0 ) { return 0; }
		return matrix[matrix.length - 1];
	}

	@Override
	public Integer _length() {
		return matrix.length;
	}

	@Override
	public Integer _max() {
		Integer max = Integer.MIN_VALUE;
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] > max ) {
				max = Integer.valueOf(matrix[i]);
			}
		}
		return max;
	}

	@Override
	public Integer _min() {
		Integer min = Integer.MAX_VALUE;
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( matrix[i] < min ) {
				min = Integer.valueOf(matrix[i]);
			}
		}
		return min;
	}

	@Override
	public Double _product() {
		double result = 1.0;
		for ( int i = 0, n = matrix.length; i < n; i++ ) {
			result *= matrix[i];
		}
		return result;
	}

	@Override
	public Double _sum() {
		double result = 0d;
		for ( int i = 0, n = matrix.length; i < n; i++ ) {
			result += matrix[i];
		}
		return result;
	}

	@Override
	public boolean _isEmpty() {
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
	protected IMatrix _matrixValue(final IScope scope, final GamaPoint preferredSize) {
		if ( preferredSize == null ) { return this; }
		final int cols = (int) preferredSize.x;
		final int rows = (int) preferredSize.y;
		return new GamaIntMatrix(cols, rows, matrix);
	}

	@Override
	public IMatrix _reverse() throws GamaRuntimeException {
		IMatrix result = new GamaIntMatrix(numRows, numCols);
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				result.put(j, i, get(i, j));
			}
		}
		return result;
	}

	@Override
	public GamaIntMatrix copy() {
		return new GamaIntMatrix(numCols, numRows, matrix);
	}

	@Override
	public boolean equals(final Object m) {
		if ( this == m ) { return true; }
		if ( !(m instanceof GamaIntMatrix) ) { return false; }
		GamaIntMatrix mat = (GamaIntMatrix) m;
		return Arrays.equals(this.matrix, mat.matrix);
	}

	public void fillWith(final int o) {
		Arrays.fill(matrix, o);
	}

	@Override
	public Integer get(final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return 0; }
		return matrix[row * numCols + col];
	}

	public double getSize() {
		return cellSize;
	}

	@Override
	public void put(final int col, final int row, final int obj) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return; }
		matrix[row * numCols + col] = obj;
	}

	@Override
	public void put(final int col, final int row, final Integer obj) {
		put(col, row, obj.intValue());
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
	public Integer remove(final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return 0; }
		final int o = matrix[row * numCols + col];
		matrix[row * numCols + col] = 0;
		return o;
	}

	@Override
	public boolean _removeFirst(final Integer o) {
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
	public boolean _removeAll(final IGamaContainer<?, Integer> list) {
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
	public void shuffleWith(final RandomAgent randomAgent) {
		matrix = randomAgent.shuffle(matrix);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
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

	@Override
	public String toJava() {
		return "GamaMatrixType.from(" + Cast.toJava(new GamaList(this.matrix)) + ", false)";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return value instanceof Integer;
	}
}
