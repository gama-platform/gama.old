/*******************************************************************************************************
 *
 * msi.gama.util.matrix.GamaIntMatrix.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.matrix;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import com.google.common.primitives.Ints;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaIntMatrix extends GamaMatrix<Integer> {

	static public GamaIntMatrix from(final IScope scope, final IMatrix m) {
		if (m instanceof GamaIntMatrix) { return (GamaIntMatrix) m; }
		if (m instanceof GamaObjectMatrix) { return new GamaIntMatrix(scope, m.getCols(scope), m.getRows(scope),
				((GamaObjectMatrix) m).getMatrix()); }
		if (m instanceof GamaFloatMatrix) { return new GamaIntMatrix(m.getCols(scope), m.getRows(scope),
				((GamaFloatMatrix) m).getMatrix()); }
		return null;
	}

	static public GamaIntMatrix from(final IScope scope, final int c, final int r, final IMatrix m) {
		if (m instanceof GamaIntMatrix) { return new GamaIntMatrix(c, r, ((GamaIntMatrix) m).getMatrix()); }
		if (m instanceof GamaObjectMatrix) { return new GamaIntMatrix(scope, c, r,
				((GamaObjectMatrix) m).getMatrix()); }
		if (m instanceof GamaFloatMatrix) { return new GamaIntMatrix(c, r, ((GamaFloatMatrix) m).getMatrix()); }
		return null;
	}

	// In case the matrix represents a discretization of an environment
	private double cellSize;

	int[] matrix;

	public GamaIntMatrix(final GamaPoint p) {
		this((int) p.x, (int) p.y);
	}

	@Override
	public IContainerType getGamlType() {
		return Types.MATRIX.of(Types.INT);
	}

	public GamaIntMatrix(final int cols, final int rows) {
		super(cols, rows, Types.INT);
		matrix = new int[cols * rows];
	}

	public int[] getMatrix() {
		return matrix;
	}

	public GamaIntMatrix(final int cols, final int rows, final double[] objects) {
		this(cols, rows);
		for (int i = 0, n = CmnFastMath.min(objects.length, rows * cols); i < n; i++) {
			matrix[i] = (int) objects[i];
		}
	}

	public GamaIntMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, CmnFastMath.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(final IScope scope, final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		for (int i = 0, n = CmnFastMath.min(objects.length, rows * cols); i < n; i++) {
			matrix[i] = Cast.asInt(scope, objects[i]);
		}
	}

	public GamaIntMatrix(final IScope scope, final int[] mat) {
		super(1, mat.length, Types.INT);
		matrix = mat;
	}

	public GamaIntMatrix(final IScope scope, final List objects, final ILocation preferredSize) {
		super(scope, objects, preferredSize, Types.INT);
		matrix = new int[numRows * numCols];
		if (preferredSize != null) {
			for (int i = 0, stop = CmnFastMath.min(matrix.length, objects.size()); i < stop; i++) {
				matrix[i] = Cast.asInt(scope, objects.get(i));
			}
		} else if (GamaMatrix.isFlat(objects)) {
			for (int i = 0, stop = objects.size(); i < stop; i++) {
				matrix[i] = Cast.asInt(scope, objects.get(i));
			}
		} else {
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					set(scope, j, i, Cast.asInt(scope, ((List) objects.get(j)).get(i)));
				}
			}
		}
	}

	public GamaIntMatrix(final IScope scope, final Object[] mat) {
		this(1, mat.length);
		for (int i = 0; i < mat.length; i++) {
			matrix[i] = Cast.asInt(scope, mat[i]);
		}
	}

	public GamaIntMatrix(final RealMatrix rm) {
		super(rm.getColumnDimension(), rm.getRowDimension(), Types.INT);
		matrix = new int[rm.getColumnDimension() * rm.getRowDimension()];
		updateMatrix(rm);
	}

	// public GamaIntMatrix(final GamaMatrix rm) {
	// super(rm.numCols, rm.numRows);
	// matrix = new int[rm.numCols * rm.numRows];
	// fillMatrix(rm);
	// }

	@Override
	public void _clear() {
		Arrays.fill(matrix, 0);
	}

	@Override
	protected void _putAll(final IScope scope, final Object o) throws GamaRuntimeException {
		fillWith(Types.INT.cast(scope, o, null, false));
	}

	@Override
	protected boolean _contains(final IScope scope, final Object o) {
		for (int i = 0; i < matrix.length; i++) {
			if (o instanceof Integer && matrix[i] == ((Integer) o).intValue()) { return true; }
		}
		return false;
	}

	@Override
	public Integer _first(final IScope scope) {
		if (matrix.length == 0) { return 0; }
		return matrix[0];
	}

	@Override
	public Integer _last(final IScope scope) {
		if (matrix.length == 0) { return 0; }
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
	 * @param two
	 *            matrix to concatenate
	 * @return the matrix concatenated
	 */
	// @Override
	// @operator(value = IKeyword.APPEND_VERTICALLY, content_type =
	// ITypeProvider.BOTH,
	// category={IOperatorCategory.MATRIX})
	public IMatrix _opAppendVertically(final IScope scope, final IMatrix b) {
		final GamaIntMatrix a = this;
		final int[] ma = a.getMatrix();
		final int[] mb = ((GamaIntMatrix) b).getMatrix();
		final int[] mab = ArrayUtils.addAll(ma, mb);

		final GamaIntMatrix fl = new GamaIntMatrix(a.getCols(scope), a.getRows(scope) + b.getRows(scope), mab);

		// throw GamaRuntimeException.error("ATTENTION : Matrix additions not
		// implemented. Returns nil for the moment");
		return fl;
	}

	/**
	 * Take two matrices (with the same number of rows) and create a big matrix putting the second matrix on the right
	 * side of the first matrix
	 *
	 * @param two
	 *            matrix to concatenate
	 * @return the matrix concatenated
	 */

	// @Override
	// @operator(value = IKeyword.APPEND_HORYZONTALLY, content_type =
	// ITypeProvider.BOTH,
	// category={IOperatorCategory.MATRIX})
	public IMatrix _opAppendHorizontally(final IScope scope, final IMatrix b) {
		final GamaIntMatrix a = this;
		GamaIntMatrix aprime = new GamaIntMatrix(a.getRows(scope), a.getCols(scope));
		aprime = (GamaIntMatrix) a._reverse(scope);
		// DEBUG.LOG("aprime = " + aprime);
		GamaIntMatrix bprime = new GamaIntMatrix(b.getRows(scope), b.getCols(scope));
		bprime = (GamaIntMatrix) ((GamaIntMatrix) b)._reverse(scope);
		// DEBUG.LOG("bprime = " + bprime);
		final GamaIntMatrix c = (GamaIntMatrix) aprime.opAppendVertically(scope, bprime);
		// DEBUG.LOG("c = " + c);
		final GamaIntMatrix cprime = (GamaIntMatrix) c._reverse(scope);
		// DEBUG.LOG("cprime = " + cprime);
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
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i] != 0) { return false; }
		}
		return true;
	}

	@Override
	protected IList _listValue(final IScope scope, final IType contentsType, final boolean cast) {
		return cast ? GamaListFactory.create(scope, contentsType, matrix)
				: GamaListFactory.createWithoutCasting(contentsType, matrix);
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize, final IType type,
			final boolean copy) {
		return GamaMatrixType.from(scope, this, type, preferredSize, copy);
	}

	@Override
	public IMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		final IMatrix result = new GamaIntMatrix(numRows, numCols);
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				result.set(scope, j, i, get(scope, i, j));
			}
		}
		return result;
	}

	@Override
	public GamaIntMatrix copy(final IScope scope, final ILocation preferredSize, final boolean copy) {
		if (preferredSize == null) {
			if (copy) {
				return new GamaIntMatrix(numCols, numRows, Arrays.copyOf(matrix, matrix.length));
			} else {
				return this;
			}
		} else {
			return new GamaIntMatrix((int) preferredSize.getX(), (int) preferredSize.getX(),
					Arrays.copyOf(matrix, matrix.length));
		}
	}

	@Override
	public boolean equals(final Object m) {
		if (this == m) { return true; }
		if (!(m instanceof GamaIntMatrix)) { return false; }
		final GamaIntMatrix mat = (GamaIntMatrix) m;
		return Arrays.equals(this.matrix, mat.matrix);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(matrix);
	}

	public void fillWith(final int o) {
		Arrays.fill(matrix, o);
	}

	@Override
	public Integer get(final IScope scope, final int col, final int row) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) { return 0; }
		return matrix[row * numCols + col];
	}

	public double getSize() {
		return cellSize;
	}

	// @Override
	public void set(final IScope scope, final int col, final int row, final int obj) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) { return; }
		matrix[row * numCols + col] = obj;
	}

	@Override
	public void set(final IScope scope, final int col, final int row, final Object obj) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) { return; }
		matrix[row * numCols + col] = Cast.asInt(scope, obj);
	}

	public boolean remove(final int o) {
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i] == o) {
				matrix[i] = 0;
				return true;
			}
		}
		return false;
	}

	@Override
	public Integer remove(final IScope scope, final int col, final int row) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) { return 0; }
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
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i] == o) {
				matrix[i] = 0;
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public boolean _removeAll(final IScope scope, final IContainer<?, Integer> list) {
		for (final Integer o : list.iterable(scope)) {
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
				for (int row = 0; row < numRows; row++) {
					for (int col = 0; col < numCols; col++) {
						sb.append(get(scope, col, row));
						if (col < numCols - 1) {
							sb.append(',');
						}
					}
					if (row < numRows - 1) {
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
	 * 
	 * @see msi.gama.util.matrix.GamaMatrix#iterator()
	 */
	@Override
	public java.lang.Iterable<Integer> iterable(final IScope scope) {
		return Ints.asList(matrix);
	}

	RealMatrix getRealMatrix() {
		final RealMatrix realMatrix = new Array2DRowRealMatrix(this.numRows, this.numCols);
		for (int i = 0; i < this.numRows; i++) {
			for (int j = 0; j < this.numCols; j++) {
				realMatrix.setEntry(i, j, Cast.asFloat(null, this.get(null, j, i)));
			}
		}
		return realMatrix;
	}

	void updateMatrix(final RealMatrix realMatrix) {
		for (int i = 0; i < this.numRows; i++) {
			for (int j = 0; j < this.numCols; j++) {
				getMatrix()[i * numCols + j] = Cast.asInt(null, realMatrix.getEntry(i, j));
			}
		}
	}

	// void fillMatrix(final GamaMatrix matrix2) {
	// for ( int i = 0; i < this.numRows; i++ ) {
	// for ( int j = 0; j < this.numCols; j++ ) {
	// matrix[i * numCols + j] = Cast.asInt(null, matrix2.get(null, j, i));
	// }
	// }
	// }

	@Override
	public IMatrix plus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaIntMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] + matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix times(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaIntMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] * matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix minus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaIntMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] - matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix times(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public IMatrix times(final Integer val) throws GamaRuntimeException {
		final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] / val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final Integer val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] / (double) val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaIntMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] / matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix matrixMultiplication(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaIntMatrix matb = from(scope, other);
		try {
			if (matb != null) { return new GamaIntMatrix(getRealMatrix().multiply(matb.getRealMatrix())); }
		} catch (final DimensionMismatchException e) {
			throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
		}
		return null;
	}

	@Override
	public IMatrix plus(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public IMatrix plus(final Integer val) throws GamaRuntimeException {
		final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public IMatrix minus(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	public IMatrix minus(final Integer val) throws GamaRuntimeException {
		final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	protected Integer getNthElement(final Integer index) {
		if (index == null) { return 0; }
		if (index > getMatrix().length) { return 0; }
		return getMatrix()[index];
	}

	@Override
	public Double getDeterminant(final IScope scope) throws GamaRuntimeException {
		final RealMatrix rm = toApacheMatrix(scope);
		final LUDecomposition ld = new LUDecomposition(rm);
		return ld.getDeterminant();
	}

	@Override
	public Double getTrace(final IScope scope) throws GamaRuntimeException {
		final RealMatrix rm = toApacheMatrix(scope);
		return rm.getTrace();
	}

	@Override
	public IList<Double> getEigen(final IScope scope) throws GamaRuntimeException {
		final RealMatrix rm = toApacheMatrix(scope);
		final EigenDecomposition ed = new EigenDecomposition(rm);
		return fromApacheMatrixtoDiagList(scope, ed.getD());
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "matrix<int>(" + getRowsList(null).serialize(includingBuiltIn) + ")";
	}

	IList<Double> fromApacheMatrixtoDiagList(final IScope scope, final RealMatrix rm) {
		final IList<Double> vals = GamaListFactory.create(Types.FLOAT);
		for (int i = 0; i < rm.getColumnDimension(); i++) {
			vals.add(rm.getEntry(i, i));
		}
		return vals;
	}

	public RealMatrix toApacheMatrix(final IScope scope) {
		final RealMatrix rm = new Array2DRowRealMatrix(numRows, numCols);
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				final int val = get(scope, i, j);
				rm.setEntry(j, i, val);
			}
		}
		return rm;
	}

	public IMatrix fromApacheMatrix(final IScope scope, final RealMatrix rm) {
		if (rm == null) { return null; }
		final GamaFloatMatrix matrix = new GamaFloatMatrix(rm.getColumnDimension(), rm.getRowDimension());
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				matrix.set(scope, i, j, rm.getEntry(j, i));
			}
		}
		return matrix;

	}

	@Override
	public IMatrix<Double> inverse(final IScope scope) throws GamaRuntimeException {
		final RealMatrix rm = toApacheMatrix(scope);
		final LUDecomposition ld = new LUDecomposition(rm);
		return fromApacheMatrix(scope, ld.getSolver().getInverse());
	}

	@Override
	public StreamEx<Integer> stream(final IScope scope) {
		return IntStreamEx.of(matrix).boxed();
	}
}
