/*******************************************************************************************************
 *
 * msi.gama.util.matrix.GamaIntMatrix.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.matrix;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

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
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaIntMatrix extends GamaMatrix<Integer> {

	static public GamaIntMatrix from(final IScope scope, final IMatrix m) {
		if (m instanceof GamaIntMatrix) return (GamaIntMatrix) m;
		if (m instanceof GamaObjectMatrix)
			return new GamaIntMatrix(scope, m.getCols(scope), m.getRows(scope), ((GamaObjectMatrix) m).getMatrix());
		if (m instanceof GamaFloatMatrix)
			return new GamaIntMatrix(m.getCols(scope), m.getRows(scope), ((GamaFloatMatrix) m).getMatrix());
		return null;
	}

	static public GamaIntMatrix from(final IScope scope, final int c, final int r, final IMatrix m) {
		if (m instanceof GamaIntMatrix) return new GamaIntMatrix(c, r, ((GamaIntMatrix) m).getMatrix());
		if (m instanceof GamaObjectMatrix) return new GamaIntMatrix(scope, c, r, ((GamaObjectMatrix) m).getMatrix());
		if (m instanceof GamaFloatMatrix) return new GamaIntMatrix(c, r, ((GamaFloatMatrix) m).getMatrix());
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
		for (int i = 0, n = Math.min(objects.length, rows * cols); i < n; i++) {
			matrix[i] = (int) objects[i];
		}
	}

	public GamaIntMatrix(final int cols, final int rows, final int[] objects) {
		this(cols, rows);
		java.lang.System.arraycopy(objects, 0, matrix, 0, Math.min(objects.length, rows * cols));
	}

	public GamaIntMatrix(final IScope scope, final int cols, final int rows, final Object[] objects) {
		this(cols, rows);
		for (int i = 0, n = Math.min(objects.length, rows * cols); i < n; i++) {
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
			for (int i = 0, stop = Math.min(matrix.length, objects.size()); i < stop; i++) {
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
		for (final int element : matrix) {
			if (o instanceof Integer && element == ((Integer) o).intValue()) return true;
		}
		return false;
	}

	@Override
	public Integer _first(final IScope scope) {
		if (matrix.length == 0) return 0;
		return matrix[0];
	}

	@Override
	public Integer _last(final IScope scope) {
		if (matrix.length == 0) return 0;
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
	public GamaIntMatrix _opAppendVertically(final IScope scope, final GamaIntMatrix b) {
		final int[] mab = ArrayUtils.addAll(getMatrix(), b.getMatrix());
		return new GamaIntMatrix(numCols, numRows + b.getRows(scope), mab);
	}

	/**
	 * Take two matrices (with the same number of rows) and create a big matrix putting the second matrix on the right
	 * side of the first matrix
	 *
	 * @param two
	 *            matrix to concatenate
	 * @return the matrix concatenated
	 */

	public GamaIntMatrix _opAppendHorizontally(final IScope scope, final GamaIntMatrix b) {
		final GamaIntMatrix aprime = _reverse(scope);
		final GamaIntMatrix bprime = b._reverse(scope);
		final GamaIntMatrix c = aprime._opAppendVertically(scope, bprime);
		final GamaIntMatrix cprime = c._reverse(scope);
		return cprime;
	}

	@Override
	public boolean _isEmpty(final IScope scope) {
		for (final int element : matrix) {
			if (element != 0) return false;
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
	public GamaIntMatrix _reverse(final IScope scope) throws GamaRuntimeException {
		final GamaIntMatrix result = new GamaIntMatrix(numRows, numCols);
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
			if (copy)
				return new GamaIntMatrix(numCols, numRows, Arrays.copyOf(matrix, matrix.length));
			else
				return this;
		} else
			return new GamaIntMatrix((int) preferredSize.getX(), (int) preferredSize.getX(),
					Arrays.copyOf(matrix, matrix.length));
	}

	@Override
	public boolean equals(final Object m) {
		if (this == m) return true;
		if (!(m instanceof GamaIntMatrix)) return false;
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
		if (col >= numCols || col < 0 || row >= numRows || row < 0) return 0;
		return matrix[row * numCols + col];
	}

	public double getSize() {
		return cellSize;
	}

	// @Override
	public void set(final IScope scope, final int col, final int row, final int obj) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) return;
		matrix[row * numCols + col] = obj;
	}

	@Override
	public void set(final IScope scope, final int col, final int row, final Object obj) {
		if (col >= numCols || col < 0 || row >= numRows || row < 0) return;
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
		if (col >= numCols || col < 0 || row >= numRows || row < 0) return 0;
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
		randomAgent.shuffleInPlace(getMatrix());
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
						if (col < numCols - 1) { sb.append(','); }
					}
					if (row < numRows - 1) { sb.append(';'); }
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

	// void fillMatrix(final GamaMatrix matrix2) {
	// for ( int i = 0; i < this.numRows; i++ ) {
	// for ( int j = 0; j < this.numCols; j++ ) {
	// matrix[i * numCols + j] = Cast.asInt(null, matrix2.get(null, j, i));
	// }
	// }
	// }

	@Override
	public GamaIntMatrix plus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
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
	public GamaIntMatrix times(final IScope scope, final IMatrix other) throws GamaRuntimeException {
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
	public GamaIntMatrix minus(final IScope scope, final IMatrix other) throws GamaRuntimeException {
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
	public GamaFloatMatrix times(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public GamaIntMatrix times(final Integer val) throws GamaRuntimeException {
		final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] * val;
		}
		return nm;
	}

	@Override
	public GamaFloatMatrix divides(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] / val;
		}
		return nm;
	}

	@Override
	public GamaFloatMatrix divides(final Integer val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] / (double) val;
		}
		return nm;
	}

	@Override
	public GamaIntMatrix divides(final IScope scope, final IMatrix other) throws GamaRuntimeException {
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
	public GamaFloatMatrix plus(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public GamaIntMatrix plus(final Integer val) throws GamaRuntimeException {
		final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] + val;
		}
		return nm;
	}

	@Override
	public GamaFloatMatrix minus(final Double val) throws GamaRuntimeException {
		final GamaFloatMatrix nm = new GamaFloatMatrix(this.numCols, this.numRows);
		final double[] mm = nm.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			mm[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	public GamaIntMatrix minus(final Integer val) throws GamaRuntimeException {
		final GamaIntMatrix nm = new GamaIntMatrix(this.numCols, this.numRows);
		for (int i = 0; i < matrix.length; i++) {
			nm.matrix[i] = matrix[i] - val;
		}
		return nm;
	}

	@Override
	public Integer getNthElement(final Integer index) {
		if (index == null) return 0;
		if (index > getMatrix().length) return 0;
		return getMatrix()[index];
	}

	@Override
	protected void setNthElement(final IScope scope, final int index, final Object value) {
		getMatrix()[index] = Cast.asInt(scope, value);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "matrix<int>(" + getRowsList(null).serialize(includingBuiltIn) + ")";
	}

	@Override
	public StreamEx<Integer> stream(final IScope scope) {
		return IntStreamEx.of(matrix).boxed();
	}

	@Override
	public double[] getFieldData(final IScope scope) {
		double[] result = new double[matrix.length];
		for (int i = 0; i < matrix.length; ++i) {
			result[i] = matrix[i];
		}
		return result;
	}
}
