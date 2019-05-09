/*******************************************************************************************************
 *
 * msi.gama.util.matrix.GamaFloatMatrix.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8)
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

import com.google.common.primitives.Doubles;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
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
import one.util.streamex.DoubleStreamEx;
import one.util.streamex.StreamEx;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaFloatMatrix extends GamaMatrix<Double> {

	static public GamaFloatMatrix from(final IScope scope, final IMatrix m) {
		if (m instanceof GamaFloatMatrix) { return (GamaFloatMatrix) m; }
		if (m instanceof GamaObjectMatrix) {
			return new GamaFloatMatrix(scope, m.getCols(scope), m.getRows(scope), ((GamaObjectMatrix) m).getMatrix());
		}
		if (m instanceof GamaIntMatrix) {
			return new GamaFloatMatrix(m.getCols(scope), m.getRows(scope), ((GamaIntMatrix) m).matrix);
		}
		return null;
	}

	static public GamaFloatMatrix from(final IScope scope, final int c, final int r, final IMatrix m) {
		if (m instanceof GamaFloatMatrix) { return new GamaFloatMatrix(c, r, ((GamaFloatMatrix) m).getMatrix()); }
		if (m instanceof GamaObjectMatrix) {
			return new GamaFloatMatrix(scope, c, r, ((GamaObjectMatrix) m).getMatrix());
		}
		if (m instanceof GamaIntMatrix) { return new GamaFloatMatrix(c, r, ((GamaIntMatrix) m).matrix); }
		return null;
	}

	private double[] matrix;

	public GamaFloatMatrix(final RealMatrix rm) {
		super(rm.getColumnDimension(), rm.getRowDimension(), Types.FLOAT);
		matrix = new double[rm.getColumnDimension() * rm.getRowDimension()];
		updateMatrix(rm);
	}

	public GamaFloatMatrix(final double[] mat) {
		super(1, mat.length, Types.FLOAT);
		setMatrix(mat);
	}

	public GamaFloatMatrix(final GamaPoint p) {
		this((int) p.x, (int) p.y);
	}

	public GamaFloatMatrix(final int cols, final int rows) {
		super(cols, rows, Types.FLOAT);
		setMatrix(new double[cols * rows]);
	}

	public GamaFloatMatrix(final int cols, final int rows, final double[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, getMatrix(), 0, CmnFastMath.min(objects.length, rows * cols));
	}

	public GamaFloatMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		for (int i = 0, n = CmnFastMath.min(objects.length, rows * cols); i < n; i++) {
			matrix[i] = objects[i];
		}
		// java.lang.System.arraycopy(objects, 0, getMatrix(), 0,
		// FastMath.min(objects.length, rows * cols));
	}

	public GamaFloatMatrix(final IScope scope, final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		for (int i = 0, n = CmnFastMath.min(objects.length, rows * cols); i < n; i++) {
			matrix[i] = Cast.asFloat(scope, objects[i]);
		}
	}

	public GamaFloatMatrix(final IScope scope, final List objects, final ILocation preferredSize)
			throws GamaRuntimeException {
		super(scope, objects, preferredSize, Types.FLOAT);
		setMatrix(new double[numRows * numCols]);
		if (preferredSize != null) {
			for (int i = 0, stop = CmnFastMath.min(getMatrix().length, objects.size()); i < stop; i++) {
				getMatrix()[i] = Cast.asFloat(scope, objects.get(i));
			}
		} else if (GamaMatrix.isFlat(objects)) {
			for (int i = 0, stop = objects.size(); i < stop; i++) {
				getMatrix()[i] = Cast.asFloat(scope, objects.get(i));
			}
		} else {
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					set(scope, j, i, Cast.asFloat(scope, ((List) objects.get(j)).get(i)));
				}
			}
		}
	}

	public GamaFloatMatrix(final IScope scope, final Object[] mat) {
		this(1, mat.length);
		for (int i = 0; i < mat.length; i++) {
			getMatrix()[i] = Cast.asFloat(scope, mat[i]);
		}
	}

	@Override
	protected IList _listValue(final IScope scope, final IType contentsType, final boolean cast) {
		return cast ? GamaListFactory.create(scope, contentsType, matrix)
				: GamaListFactory.createWithoutCasting(contentsType, matrix);
	}

	@Override
	protected void _clear() {
		Arrays.fill(getMatrix(), 0d);
	}

	@Override
	public boolean _contains(final IScope scope, final Object o) {
		if (o instanceof Double) {
			final Double d = (Double) o;
			for (int i = 0; i < getMatrix().length; i++) {
				if (IntervalSize.isZeroWidth(getMatrix()[i], d)) { return true; }
			}
		}
		return false;
	}

	@Override
	public Double _first(final IScope scope) {
		if (getMatrix().length == 0) { return 0d; }
		return getMatrix()[0];
	}

	@Override
	public Double _last(final IScope scope) {
		if (getMatrix().length == 0) { return 0d; }
		return getMatrix()[getMatrix().length - 1];
	}

	@Override
	public Integer _length(final IScope scope) {
		return getMatrix().length;
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
	// ITypeProvider.BOTH, category={IOperatorCategory.MATRIX})
	public IMatrix _opAppendVertically(final IScope scope, final IMatrix b) {
		final GamaFloatMatrix a = this;
		final double[] ma = a.getMatrix();
		final double[] mb = ((GamaFloatMatrix) b).getMatrix();
		final double[] mab = ArrayUtils.addAll(ma, mb);

		final GamaFloatMatrix fl = new GamaFloatMatrix(a.getCols(scope), a.getRows(scope) + b.getRows(scope), mab);

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
	// ITypeProvider.BOTH, category={IOperatorCategory.MATRIX})
	public IMatrix _opAppendHorizontally(final IScope scope, final IMatrix b) {
		final GamaFloatMatrix a = this;
		// GamaFloatMatrix aprime = new GamaFloatMatrix(a.getRows(scope),
		// a.getCols(scope));
		final GamaFloatMatrix aprime = (GamaFloatMatrix) a._reverse(scope);
		// DEBUG.LOG("aprime = " + aprime);
		// GamaFloatMatrix bprime = new GamaFloatMatrix(b.getRows(scope),
		// b.getCols(scope));
		final GamaFloatMatrix bprime = (GamaFloatMatrix) ((GamaFloatMatrix) b)._reverse(scope);
		// DEBUG.LOG("bprime = " + bprime);
		final GamaFloatMatrix c = (GamaFloatMatrix) aprime.opAppendVertically(scope, bprime);
		// DEBUG.LOG("c = " + c);
		final GamaFloatMatrix cprime = (GamaFloatMatrix) c._reverse(scope);
		// DEBUG.LOG("cprime = " + cprime);
		return cprime;
	}

	// @Override
	// public Double _max(final IScope scope) {
	// Double max = -Double.MAX_VALUE;
	// for ( int i = 0; i < matrix.length; i++ ) {
	// if ( matrix[i] > max ) {
	// max = Double.valueOf(matrix[i]);
	// }
	// }
	// return max;
	// }
	//
	// @Override
	// public Double _min(final IScope scope) {
	// Double min = Double.MAX_VALUE;
	// for ( int i = 0; i < matrix.length; i++ ) {
	// if ( matrix[i] < min ) {
	// min = Double.valueOf(matrix[i]);
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
	// public Double _sum(final IScope scope) {
	// double result = 0.0;
	// for ( int i = 0, n = matrix.length; i < n; i++ ) {
	// result += matrix[i];
	// }
	// return result;
	// }
	//
	@Override
	public boolean _isEmpty(final IScope scope) {
		for (int i = 0; i < getMatrix().length; i++) {
			if (getMatrix()[i] != 0d) { return false; }
		}
		return true;
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize, final IType type,
			final boolean copy) {
		return GamaMatrixType.from(scope, this, type, preferredSize, copy);
	}

	@Override
	public IMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		final GamaFloatMatrix result = new GamaFloatMatrix(numRows, numCols);
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				final double val = get(scope, i, j);
				result.set(scope, j, i, val);
			}
		}
		return result;
	}

	@Override
	public GamaFloatMatrix copy(final IScope scope, final ILocation size, final boolean copy) {
		if (size == null) {
			if (copy) {
				return new GamaFloatMatrix(numCols, numRows, Arrays.copyOf(getMatrix(), matrix.length));
			} else {
				return this;
			}
		}
		return new GamaFloatMatrix((int) size.getX(), (int) size.getY(), Arrays.copyOf(getMatrix(), matrix.length));
	}

	@Override
	public boolean equals(final Object m) {
		if (this == m) { return true; }
		if (!(m instanceof GamaFloatMatrix)) { return false; }
		final GamaFloatMatrix mat = (GamaFloatMatrix) m;
		return Arrays.equals(this.getMatrix(), mat.getMatrix());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getMatrix());
	}

	@Override
	public void _putAll(final IScope scope, final Object o) throws GamaRuntimeException {
		// TODO Exception if o == null
		// TODO Verify the type
		Arrays.fill(getMatrix(), Types.FLOAT.cast(scope, o, null, false));

	}

	@Override
	public Double get(final IScope scope, final int col, final int row) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) { return 0d; }
		return getMatrix()[row * numCols + col];
	}

	// public void put(final int col, final int row, final double obj) {
	// if ( !(col >= numCols || col < 0 || row >= numRows || row < 0) ) {
	// matrix[row * numCols + col] = obj;
	// }
	// }

	@Override
	public void set(final IScope scope, final int col, final int row, final Object obj) throws GamaRuntimeException {
		if (!(col >= numCols || col < 0 || row >= numRows || row < 0)) {
			final double val = Cast.asFloat(scope, obj);
			getMatrix()[row * numCols + col] = val;
		}
		// put(col, row, Cast.asFloat(GAMA.getDefaultScope(),
		// obj).doubleValue());
	}

	private boolean remove(final double o) {
		for (int i = 0; i < getMatrix().length; i++) {
			if (new Double(getMatrix()[i]).equals(new Double(o))) {
				getMatrix()[i] = 0d;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean _removeFirst(final IScope scope, final Double o) throws GamaRuntimeException {
		// Exception if o == null
		return remove(o.doubleValue());
	}

	@Override
	public Double remove(final IScope scope, final int col, final int row) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) { return 0d; }
		final double o = getMatrix()[row * numCols + col];
		getMatrix()[row * numCols + col] = 0d;
		return o;
	}

	private boolean removeAll(final double o) {
		boolean removed = false;
		for (int i = 0; i < getMatrix().length; i++) {
			if (new Double(getMatrix()[i]).equals(new Double(o))) {
				getMatrix()[i] = 0d;
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public boolean _removeAll(final IScope scope, final IContainer<?, Double> list) {
		// TODO Exception if o == null
		for (final Double o : list.iterable(scope)) {
			removeAll(o.doubleValue());
		}
		// TODO Make a test to verify the return
		return true;
	}

	@Override
	public void shuffleWith(final RandomUtils randomAgent) {
		randomAgent.shuffleInPlace(getMatrix());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(numRows * numCols * 5);
		sb.append('[');
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				sb.append(get(null, col, row));
				if (col < numCols - 1) {
					sb.append(',');
				}
			}
			if (row < numRows - 1) {
				sb.append(';');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Method iterator()
	 *
	 * @see msi.gama.util.matrix.GamaMatrix#iterator()
	 */
	// @Override
	// public Iterator<Double> iterator() {
	// return Doubles.asList(getMatrix()).iterator();
	// }

	@Override
	public java.lang.Iterable<Double> iterable(final IScope scope) {
		return Doubles.asList(getMatrix());
	}

	public double[] getMatrix() {
		return matrix;
	}

	void setMatrix(final double[] matrix) {
		this.matrix = matrix;
	}

	RealMatrix getRealMatrix() {
		final RealMatrix realMatrix = new Array2DRowRealMatrix(this.numRows, this.numCols);
		for (int i = 0; i < this.numRows; i++) {
			for (int j = 0; j < this.numCols; j++) {
				realMatrix.setEntry(i, j, this.get(null, j, i));
			}
		}
		return realMatrix;
	}

	void updateMatrix(final RealMatrix realMatrix) {
		for (int i = 0; i < this.numRows; i++) {
			for (int j = 0; j < this.numCols; j++) {
				getMatrix()[i * numCols + j] = realMatrix.getEntry(i, j);
			}
		}
	}

	@Override
	public IMatrix plus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaFloatMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] + matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix times(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaFloatMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] * matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix minus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaFloatMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
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
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public IMatrix times(final Integer val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] / val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final Integer val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] / val;
		}
		return nm;
	}

	@Override
	public IMatrix divides(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaFloatMatrix matb = from(scope, other);
		if (matb != null && this.numCols == matb.numCols && this.numRows == matb.numRows) {
			final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
			for (int i = 0; i < matrix.length; i++) {
				nm.matrix[i] = matrix[i] / matb.matrix[i];
			}
			return nm;
		}
		throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
	}

	@Override
	public IMatrix matrixMultiplication(final IScope scope, final IMatrix other) throws GamaRuntimeException {
		final GamaFloatMatrix matb = from(scope, other);
		try {
			if (matb != null) { return new GamaFloatMatrix(getRealMatrix().multiply(matb.getRealMatrix())); }
		} catch (final DimensionMismatchException e) {
			throw GamaRuntimeException.error("The dimensions of the matrices do not correspond", scope);
		}
		return null;
	}

	@Override
	public IMatrix plus(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public IMatrix plus(final Integer val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public IMatrix minus(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	public IMatrix minus(final Integer val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	public Double getNthElement(final Integer index) {
		if (index == null) { return 0d; }
		if (index > getMatrix().length) { return 0d; }
		return getMatrix()[index];
	}

	@Override
	protected void setNthElement(final IScope scope, final int index, final Object value) {
		getMatrix()[index] = Cast.asFloat(scope, value);
	}

	public RealMatrix toApacheMatrix(final IScope scope) {
		final RealMatrix rm = new Array2DRowRealMatrix(numRows, numCols);
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				final double val = get(scope, i, j);
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

	IList<Double> fromApacheMatrixtoDiagList(final IScope scope, final RealMatrix rm) {
		final IList<Double> vals = GamaListFactory.create(Types.FLOAT);
		for (int i = 0; i < rm.getColumnDimension(); i++) {
			vals.add(rm.getEntry(i, i));
		}
		return vals;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "matrix<float>(" + getRowsList(null).serialize(includingBuiltIn) + ")";
	}

	@Override
	public IContainerType getGamlType() {
		return Types.MATRIX.of(Types.FLOAT);
	}

	@Override
	public IMatrix<Double> inverse(final IScope scope) throws GamaRuntimeException {
		final RealMatrix rm = toApacheMatrix(scope);
		final LUDecomposition ld = new LUDecomposition(rm);
		return fromApacheMatrix(scope, ld.getSolver().getInverse());
	}

	@Override
	public StreamEx<Double> stream(final IScope scope) {
		return DoubleStreamEx.of(matrix).boxed();
	}

}
