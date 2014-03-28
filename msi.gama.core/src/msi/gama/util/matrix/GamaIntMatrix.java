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
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.*;
import com.google.common.primitives.Ints;

public class GamaIntMatrix extends GamaMatrix<Integer> {

	static public GamaIntMatrix from(final IScope scope, final IMatrix m) {
		if ( m instanceof GamaIntMatrix ) { return (GamaIntMatrix) m; }
		if ( m instanceof GamaObjectMatrix ) { return new GamaIntMatrix(scope, m.getCols(scope), m.getRows(scope),
			((GamaObjectMatrix) m).getMatrix()); }
		if ( m instanceof GamaFloatMatrix ) { return new GamaIntMatrix(m.getCols(scope), m.getRows(scope),
			((GamaFloatMatrix) m).getMatrix()); }
		return null;
	}

	static public GamaIntMatrix from(final IScope scope, final int c, final int r, final IMatrix m) {
		if ( m instanceof GamaIntMatrix ) { return new GamaIntMatrix(c, r, ((GamaIntMatrix) m).getMatrix()); }
		if ( m instanceof GamaObjectMatrix ) { return new GamaIntMatrix(scope, c, r, ((GamaObjectMatrix) m).getMatrix()); }
		if ( m instanceof GamaFloatMatrix ) { return new GamaIntMatrix(c, r, ((GamaFloatMatrix) m).getMatrix()); }
		return null;
	}

	// In case the matrix represents a discretization of an environment
	private double cellSize;

	int[] matrix;

	public GamaIntMatrix(final GamaPoint p) {
		this((int) p.x, (int) p.y);
	}

	public GamaIntMatrix(final int cols, final int rows) {
		super(cols, rows);
		matrix = new int[cols * rows];
	}

	public int[] getMatrix() {
		return matrix;
	}

	public GamaIntMatrix(final int cols, final int rows, final double[] objects) {
		this(cols, rows);
		for ( int i = 0, n = Math.min(objects.length, rows * cols); i < n; i++ ) {
			matrix[i] = (int) objects[i];
		}
	}

	public GamaIntMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(final IScope scope, final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		for ( int i = 0, n = Math.min(objects.length, rows * cols); i < n; i++ ) {
			matrix[i] = Cast.asInt(scope, objects[i]);
		}
	}

	public GamaIntMatrix(final IScope scope, final int[] mat) {
		super(1, mat.length);
		matrix = mat;
	}

	public GamaIntMatrix(final IScope scope, final List objects, final boolean flat, final ILocation preferredSize) {
		super(scope, objects, flat, preferredSize);
		matrix = new int[numRows * numCols];
		if ( preferredSize != null ) {
			for ( int i = 0, stop = Math.min(matrix.length, objects.size()); i < stop; i++ ) {
				matrix[i] = Cast.asInt(scope, objects.get(i));
			}
		} else if ( flat || GamaMatrix.isFlat(objects) ) {
			for ( int i = 0, stop = objects.size(); i < stop; i++ ) {
				matrix[i] = Cast.asInt(scope, objects.get(i));
			}
		} else {
			for ( int i = 0; i < numRows; i++ ) {
				for ( int j = 0; j < numCols; j++ ) {
					set(scope, j, i, Cast.asInt(scope, ((List) objects.get(j)).get(i)));
				}
			}
		}
	}

	public GamaIntMatrix(final IScope scope, final Object[] mat) {
		this(1, mat.length);
		for ( int i = 0; i < mat.length; i++ ) {
			matrix[i] = Cast.asInt(scope, mat[i]);
		}
	}

	public GamaIntMatrix(final RealMatrix rm) {
		super(rm.getColumnDimension(), rm.getRowDimension());
		matrix = new int[rm.getColumnDimension() * rm.getRowDimension()];
		updateMatrix(rm);
	}

	public GamaIntMatrix(final GamaMatrix rm) {
		super(rm.numCols, rm.numRows);
		matrix = new int[rm.numCols * rm.numRows];
		fillMatrix(rm);
	}

	@Override
	public void _clear() {
		Arrays.fill(matrix, 0);
	}

	@Override
	protected void _putAll(final IScope scope, final Object o, final Object param) throws GamaRuntimeException {
		// TODO Verify type
		fillWith((Integer) o);
	}

	@Override
	protected boolean _contains(final IScope scope, final Object o) {
		for ( int i = 0; i < matrix.length; i++ ) {
			if ( o instanceof Integer && matrix[i] == ((Integer) o).intValue() ) { return true; }
		}
		return false;
	}

	@Override
	public Integer _first(final IScope scope) {
		if ( matrix.length == 0 ) { return 0; }
		return matrix[0];
	}

	@Override
	public Integer _last(final IScope scope) {
		if ( matrix.length == 0 ) { return 0; }
		return matrix[matrix.length - 1];
	}

	@Override
	public Integer _length(final IScope scope) {
		return matrix.length;
	}

	/**
	 * Take two matrices (with the same number of columns) and create a big matrix putting the second matrix on the
	 * right side of the first matrix
	 * 
	 * @param two matrix to concatenate
	 * @return the matrix concatenated
	 */
	// @Override
	// @operator(value = IKeyword.APPEND_VERTICALLY, content_type = ITypeProvider.BOTH,
	// category={IOperatorCategory.MATRIX})
	public IMatrix _opAppendVertically(final IScope scope, final IMatrix b) {
		GamaIntMatrix a = this;
		int[] ma = a.getMatrix();
		int[] mb = ((GamaIntMatrix) b).getMatrix();
		int[] mab = ArrayUtils.addAll(ma, mb);

		GamaIntMatrix fl = new GamaIntMatrix(a.getCols(scope), a.getRows(scope) + b.getRows(scope), mab);

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

	// @Override
	// @operator(value = IKeyword.APPEND_HORYZONTALLY, content_type = ITypeProvider.BOTH,
	// category={IOperatorCategory.MATRIX})
	public IMatrix _opAppendHorizontally(final IScope scope, final IMatrix b) {
		GamaIntMatrix a = this;
		GamaIntMatrix aprime = new GamaIntMatrix(a.getRows(scope), a.getCols(scope));
		aprime = (GamaIntMatrix) a._reverse(scope);
		// System.out.println("aprime = " + aprime);
		GamaIntMatrix bprime = new GamaIntMatrix(b.getRows(scope), b.getCols(scope));
		bprime = (GamaIntMatrix) ((GamaIntMatrix) b)._reverse(scope);
		// System.out.println("bprime = " + bprime);
		GamaIntMatrix c = (GamaIntMatrix) aprime.opAppendVertically(scope, bprime);
		// System.out.println("c = " + c);
		GamaIntMatrix cprime = (GamaIntMatrix) c._reverse(scope);
		// System.out.println("cprime = " + cprime);
		return cprime;
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
	public boolean _isEmpty(final IScope scope) {
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
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize, final IType type) {
		return GamaMatrixType.from(scope, this, type, Types.get(IType.INT), preferredSize);
	}

	@Override
	public IMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		final IMatrix result = new GamaIntMatrix(numRows, numCols);
		for ( int i = 0; i < numCols; i++ ) {
			for ( int j = 0; j < numRows; j++ ) {
				result.set(scope, j, i, get(scope, i, j));
				System.out.println("result.get..." + result.get(scope, j, i));
				System.out.println("result = " + result);
			}
		}
		return result;
	}

	@Override
	public GamaIntMatrix copy(final IScope scope, final ILocation preferredSize) {
		if ( preferredSize == null ) {
			return new GamaIntMatrix(numCols, numRows, Arrays.copyOf(matrix, matrix.length));
		} else {
			return new GamaIntMatrix((int) preferredSize.getX(), (int) preferredSize.getX(), Arrays.copyOf(matrix,
				matrix.length));
		}
	}

	@Override
	public boolean equals(final Object m) {
		if ( this == m ) { return true; }
		if ( !(m instanceof GamaIntMatrix) ) { return false; }
		final GamaIntMatrix mat = (GamaIntMatrix) m;
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
	public Integer get(final IScope scope, final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return 0; }
		return matrix[row * numCols + col];
	}

	public double getSize() {
		return cellSize;
	}

	// @Override
	public void set(final IScope scope, final int col, final int row, final int obj) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return; }
		matrix[row * numCols + col] = obj;
	}

	@Override
	public void set(final IScope scope, final int col, final int row, final Object obj) {
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
	public Integer remove(final IScope scope, final int col, final int row) {
		if ( col >= numCols || col < 0 || row >= numRows || row < 0 ) { return 0; }
		final int o = matrix[row * numCols + col];
		matrix[row * numCols + col] = 0;
		return o;
	}

	@Override
	public boolean _removeFirst(final IScope scope, final Integer o) {
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
	public boolean _removeAll(final IScope scope, final IContainer<?, Integer> list) {
		for ( final Integer o : list.iterable(scope) ) {
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

	/**
	 * Method iterator()
	 * @see msi.gama.util.matrix.GamaMatrix#iterator()
	 */
	@Override
	public java.lang.Iterable<Integer> iterable(final IScope scope) {
		return Ints.asList(matrix);
	}

	RealMatrix getRealMatrix() {
		RealMatrix realMatrix = new Array2DRowRealMatrix(this.numRows, this.numCols);
		for ( int i = 0; i < this.numRows; i++ ) {
			for ( int j = 0; j < this.numCols; j++ ) {
				realMatrix.setEntry(i, j, Cast.asFloat(null, this.get(null, j, i)));
			}
		}
		return realMatrix;
	}

	void updateMatrix(final RealMatrix realMatrix) {
		for ( int i = 0; i < this.numRows; i++ ) {
			for ( int j = 0; j < this.numCols; j++ ) {
				getMatrix()[i * numCols + j] = Cast.asInt(null, realMatrix.getEntry(i, j));
			}
		}
	}

	void fillMatrix(final GamaMatrix matrix) {
		for ( int i = 0; i < this.numRows; i++ ) {
			for ( int j = 0; j < this.numCols; j++ ) {
				getMatrix()[i * numCols + j] = Cast.asInt(null, matrix.get(null, j, i));
			}
		}
	}

	@Override
	public IMatrix plus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		GamaIntMatrix matb = from(scope, other);
		if ( matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows ) {
			GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for ( int i = 0; i < matrix.length; i++ ) {
				nm.matrix[i] = matrix[i] + matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix times(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		GamaIntMatrix matb = from(scope, other);
		if ( matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows ) {
			GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for ( int i = 0; i < matrix.length; i++ ) {
				nm.matrix[i] = matrix[i] * matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix minus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		GamaIntMatrix matb = from(scope, other);
		if ( matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows ) {
			GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for ( int i = 0; i < matrix.length; i++ ) {
				nm.matrix[i] = matrix[i] - matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix times(final Double val) throws GamaRuntimeException {
		GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		double[] mm = nm.getMatrix();
		for ( int i = 0; i < matrix.length; i++ ) {
			mm[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public IMatrix times(final Integer val) throws GamaRuntimeException {
		GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for ( int i = 0; i < matrix.length; i++ ) {
			nm.matrix[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final Double val) throws GamaRuntimeException {
		GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		double[] mm = nm.getMatrix();
		for ( int i = 0; i < matrix.length; i++ ) {
			mm[i] = matrix[i] / val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final Integer val) throws GamaRuntimeException {
		GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		double[] mm = nm.getMatrix();
		for ( int i = 0; i < matrix.length; i++ ) {
			mm[i] = matrix[i] / val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		GamaIntMatrix matb = from(scope, other);
		if ( matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows ) {
			GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for ( int i = 0; i < matrix.length; i++ ) {
				nm.matrix[i] = matrix[i] / matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix matrixMultiplication(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		GamaIntMatrix matb = from(scope, other);
		try {
			if ( matb != null ) { return new GamaIntMatrix(getRealMatrix().multiply(matb.getRealMatrix())); }
		} catch (DimensionMismatchException e) {
			throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
		}
		return null;
	}

	@Override
	public IMatrix plus(final Double val) throws GamaRuntimeException {
		GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		double[] mm = nm.getMatrix();
		for ( int i = 0; i < matrix.length; i++ ) {
			mm[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public IMatrix plus(final Integer val) throws GamaRuntimeException {
		GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for ( int i = 0; i < matrix.length; i++ ) {
			nm.matrix[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public IMatrix minus(final Double val) throws GamaRuntimeException {
		GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		double[] mm = nm.getMatrix();
		for ( int i = 0; i < matrix.length; i++ ) {
			mm[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	public IMatrix minus(final Integer val) throws GamaRuntimeException {
		GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for ( int i = 0; i < matrix.length; i++ ) {
			nm.matrix[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	protected Integer getNthElement(final Integer index) {
		if ( index == null ) { return 0; }
		if ( index > getMatrix().length ) { return 0; }
		return getMatrix()[index];
	}
}
