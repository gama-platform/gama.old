/*******************************************************************************************************
 *
 * MatrixOperators.java, in ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.matrix;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class MatrixOperators.
 */
public class MatrixOperators {

	/**
	 * Matrix multiplication.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = ".",
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			usages = @usage (
					value = "if both operands are matrix, returns the dot product of them",
					examples = @example (
							value = "matrix([[1,1],[1,2]]) . matrix([[1,1],[1,2]])",
							equals = "matrix([[2,3],[3,5]])")))
	@test ("matrix([[1,1],[1,2]]) . matrix([[1,1],[1,2]]) = matrix([[2,3],[3,5]])")
	public static IMatrix matrixMultiplication(final IScope scope, final IMatrix a, final IMatrix b)
			throws GamaRuntimeException {
		try {
			if (a instanceof GamaIntMatrix && b instanceof GamaIntMatrix)
				return toGamaIntMatrix(getRealMatrix(a).multiply(getRealMatrix(b)));
			return toGamaFloatMatrix(getRealMatrix(a).multiply(getRealMatrix(b)));
		} catch (final DimensionMismatchException e) {
			throw GamaRuntimeException.error(" The dimensions of the matrices do not correspond", scope);
		}
	}

	/**
	 * Gets the determinant.
	 *
	 * @param scope
	 *            the scope
	 * @param m
	 *            the m
	 * @return the determinant
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = { "determinant", "det" },
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "The determinant of the given matrix",
			masterDoc = true,
			examples = { @example (
					value = "determinant(matrix([[1,2],[3,4]]))",
					equals = "-2") })
	public static Double getDeterminant(final IScope scope, final IMatrix m) throws GamaRuntimeException {
		return new LUDecomposition(getRealMatrix(m)).getDeterminant();
	}

	/**
	 * Gets the trace.
	 *
	 * @param scope
	 *            the scope
	 * @param m
	 *            the m
	 * @return the trace
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "trace",
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "The trace of the given matrix (the sum of the elements on the main diagonal).",
			masterDoc = true,
			examples = { @example (
					value = "trace(matrix([[1,2],[3,4]]))",
					equals = "5") })
	public static Double getTrace(final IScope scope, final IMatrix m) throws GamaRuntimeException {
		return getRealMatrix(m).getTrace();
	}

	/**
	 * Gets the eigen.
	 *
	 * @param scope
	 *            the scope
	 * @param m
	 *            the m
	 * @return the eigen
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "eigenvalues",
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "The list of the eigen values of the given matrix",
			masterDoc = true,
			examples = { @example (
					value = "eigenvalues(matrix([[5,-3],[6,-4]]))",
					equals = "[2.0000000000000004,-0.9999999999999998]") })
	public static IList<Double> getEigen(final IScope scope, final IMatrix m) throws GamaRuntimeException {
		return fromApacheMatrixtoDiagList(scope, new EigenDecomposition(getRealMatrix(m)).getD());
	}

	/**
	 * Transpose.
	 *
	 * @param scope
	 *            the scope
	 * @param m
	 *            the m
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "transpose",
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "The transposition of the given matrix",
			masterDoc = true,
			examples = { @example (
					value = "transpose(matrix([[5,-3],[6,-4]]))",
					equals = "matrix([[5,6],[-3,-4]])") })
	public static IMatrix transpose(final IScope scope, final IMatrix m) throws GamaRuntimeException {
		return m.reverse(scope);
	}

	/**
	 * Inverse.
	 *
	 * @param scope
	 *            the scope
	 * @param m
	 *            the m
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "inverse",
			can_be_const = true,
			content_type = IType.FLOAT,
			// ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "The inverse matrix of the given matrix. If no inverse exists, returns a matrix that has properties that resemble that of an inverse.",
			masterDoc = true,
			examples = { @example (
					value = "inverse(matrix([[4,3],[3,2]]))",
					equals = "matrix([[-2.0,3.0],[3.0,-4.0]])") })
	public static IMatrix<Double> inverse(final IScope scope, final IMatrix m) throws GamaRuntimeException {
		return toGamaFloatMatrix(new LUDecomposition(getRealMatrix(m)).getSolver().getInverse());
	}

	/**
	 * Op append vertically.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the i matrix
	 */
	@operator (
			value = IKeyword.APPEND_VERTICALLY,
			content_type = ITypeProvider.BOTH,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "A matrix resulting from the concatenation of the columns  of the two given matrices. ",
			masterDoc = false,
			examples = { @example (
					value = "matrix([[1,2],[3,4]]) append_vertically matrix([[1,2],[3,4]])",
					equals = "matrix([[1,2,1,2],[3,4,3,4]])") })
	public static IMatrix opAppendVertically(final IScope scope, final IMatrix a, final IMatrix b) {
		if (a instanceof GamaIntMatrix) {
			if (b instanceof GamaIntMatrix) return ((GamaIntMatrix) a)._opAppendVertically(scope, (GamaIntMatrix) b);
			if (b instanceof GamaFloatMatrix)
				return GamaFloatMatrix.from(scope, b)._opAppendVertically(scope, (GamaFloatMatrix) b);
		} else if (a instanceof GamaFloatMatrix) {
			if (b instanceof GamaIntMatrix)
				return ((GamaFloatMatrix) a)._opAppendVertically(scope, GamaFloatMatrix.from(scope, b));
			if (b instanceof GamaFloatMatrix)
				return ((GamaFloatMatrix) a)._opAppendVertically(scope, (GamaFloatMatrix) b);
		}
		if (a instanceof GamaObjectMatrix && b instanceof GamaObjectMatrix)
			return ((GamaObjectMatrix) a)._opAppendVertically(scope, b);
		return a;
	}

	/**
	 * Take two matrices (with the same number of rows) and create a big matrix putting the second matrix on the right
	 * side of the first matrix
	 *
	 * @param two
	 *            matrix to concatenate
	 * @return the matrix concatenated
	 */

	@operator (
			value = IKeyword.APPEND_HORIZONTALLY,
			content_type = ITypeProvider.BOTH,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "A matrix resulting from the concatenation of the rows of the two given matrices.",
			masterDoc = false)
	@no_test
	public static IMatrix opAppendHorizontally(final IScope scope, final IMatrix a, final IMatrix b) {
		if (a instanceof GamaIntMatrix) {
			if (b instanceof GamaIntMatrix) return ((GamaIntMatrix) a)._opAppendHorizontally(scope, (GamaIntMatrix) b);
			if (b instanceof GamaFloatMatrix)
				return GamaFloatMatrix.from(scope, a)._opAppendHorizontally(scope, (GamaFloatMatrix) b);
		} else if (a instanceof GamaFloatMatrix) {
			if (b instanceof GamaIntMatrix)
				return ((GamaFloatMatrix) a)._opAppendHorizontally(scope, GamaFloatMatrix.from(scope, b));
			if (b instanceof GamaFloatMatrix)
				return GamaFloatMatrix.from(scope, a)._opAppendHorizontally(scope, (GamaFloatMatrix) b);
		}
		if (a instanceof GamaObjectMatrix && b instanceof GamaObjectMatrix)
			return ((GamaObjectMatrix) a)._opAppendHorizontally(scope, b);
		return a;
	}

	/**
	 * Gets the real matrix.
	 *
	 * @param m
	 *            the m
	 * @return the real matrix
	 */
	public static RealMatrix getRealMatrix(final IMatrix m) {
		var rows = m.getRows(null);
		var cols = m.getCols(null);
		final RealMatrix realMatrix = new Array2DRowRealMatrix(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) { realMatrix.setEntry(i, j, Cast.asFloat(null, m.get(null, j, i))); }
		}
		return realMatrix;
	}

	/**
	 * Update matrix.
	 *
	 * @param m
	 *            the m
	 * @param realMatrix
	 *            the real matrix
	 */
	public static void updateMatrix(final IMatrix m, final RealMatrix realMatrix) {
		var rows = m.getRows(null);
		var cols = m.getCols(null);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) { m.set(null, j, i, realMatrix.getEntry(i, j)); }
		}
	}

	/**
	 * To gama int matrix.
	 *
	 * @param m
	 *            the m
	 * @return the gama int matrix
	 */
	public static GamaIntMatrix toGamaIntMatrix(final RealMatrix m) {
		GamaIntMatrix result = new GamaIntMatrix(m.getColumnDimension(), m.getRowDimension());
		updateMatrix(result, m);
		return result;
	}

	/**
	 * To gama float matrix.
	 *
	 * @param m
	 *            the m
	 * @return the gama float matrix
	 */
	public static GamaFloatMatrix toGamaFloatMatrix(final RealMatrix m) {
		GamaFloatMatrix result = new GamaFloatMatrix(m.getColumnDimension(), m.getRowDimension());
		updateMatrix(result, m);
		return result;
	}

	/**
	 * From apache matrixto diag list.
	 *
	 * @param scope
	 *            the scope
	 * @param rm
	 *            the rm
	 * @return the i list
	 */
	public static IList<Double> fromApacheMatrixtoDiagList(final IScope scope, final RealMatrix rm) {
		final IList<Double> vals = GamaListFactory.create(Types.FLOAT);
		for (int i = 0; i < rm.getColumnDimension(); i++) { vals.add(rm.getEntry(i, i)); }
		return vals;
	}

}
