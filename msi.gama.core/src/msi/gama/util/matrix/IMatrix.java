/*******************************************************************************************************
 *
 * msi.gama.util.matrix.IMatrix.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.matrix;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IList;
import msi.gama.util.IModifiableContainer;
import msi.gaml.types.IType;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 15 d�c. 2010
 *
 * @todo Description
 *
 * @param <T>
 */
@vars ({ @variable (
		name = IMatrix.DIMENSION,
		type = IType.POINT,
		doc = { @doc ("Returns the dimension (columns x rows) of the receiver matrix") }),
		@variable (
				name = IMatrix.ROWS,
				type = IType.INT,
				doc = { @doc ("Returns the number of rows of the receiver matrix") }),
		@variable (
				name = IMatrix.COLUMNS,
				type = IType.INT,
				doc = { @doc ("Returns the number of columns of the receiver matrix") }) })
@SuppressWarnings ({ "rawtypes" })
public interface IMatrix<T>
		extends IModifiableContainer<ILocation, T, ILocation, T>, IAddressableContainer<ILocation, T, ILocation, T> {

	/**
	 * Cols, rows instead of row cols because intended to work with xSize and ySize dimensions.
	 */

	public static final String DIMENSION = "dimension";

	public static final String ROWS = "rows";

	public static final String COLUMNS = "columns";

	@getter (ROWS)
	public abstract int getRows(IScope scope);

	@getter (COLUMNS)
	public abstract int getCols(IScope scope);

	@getter (DIMENSION)
	public abstract GamaPoint getDimensions();

	@operator (
			value = "rows_list",
			can_be_const = true,
			content_type = IType.LIST,
			content_type_content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "returns a list of the rows of the matrix, with each row as a list of elements",
			examples = { @example (
					value = "rows_list(matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]))",
					equals = "[[\"el11\",\"el21\",\"el31\"],[\"el12\",\"el22\",\"el32\"],[\"el13\",\"el23\",\"el33\"]]") },
			see = "columns_list")
	public abstract IList<IList<T>> getRowsList(IScope scope);

	@operator (
			value = "columns_list",
			can_be_const = true,
			content_type = IType.LIST,
			content_type_content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "returns a list of the columns of the matrix, with each column as a list of elements",
			examples = { @example (
					value = "columns_list(matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]))",
					equals = "[[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]") },
			see = "rows_list")
	public abstract IList<IList<T>> getColumnsList(IScope scope);

	@operator (
			value = "row_at",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			can_be_const = true,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "returns the row at a num_line (right-hand operand)",
			examples = { @example (
					value = "matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]) row_at 2",
					equals = "[\"el13\",\"el23\",\"el33\"]") },
			see = { "column_at", "columns_list" })
	public abstract IList<T> getRow(IScope scope, Integer num_line);

	@operator (
			value = "column_at",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			can_be_const = true,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "returns the column at a num_col (right-hand operand)",
			examples = { @example (
					value = "matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]) column_at 2",
					equals = "[\"el31\",\"el32\",\"el33\"]") },
			see = { "row_at", "rows_list" })
	public abstract IList<T> getColumn(IScope scope, Integer num_line);

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Returns a matrix containing the addition of  the elements of two matrices in argument "))
	@test("matrix([[1,2],[3,4]]) + matrix([[1,2],[3,4]]) = matrix([[2,4],[6,8]])") 
	public abstract IMatrix plus(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Multiplies the two matrices operands"))
	@test("matrix([[1,2],[3,4]]) * matrix([[1,2],[3,4]]) = matrix([[1,4],[9,16]]) ") 
	public abstract IMatrix times(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Divides the two matrices operands"))
	@test("matrix([[1,2],[3,4]]) / matrix([[1,2],[3,4]]) = matrix([[1,1],[1,1]])") 
	public abstract IMatrix divides(IScope scope, IMatrix other) throws GamaRuntimeException;

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
	@test("matrix([[1,1],[1,2]]) . matrix([[1,1],[1,2]]) = matrix([[2,3],[3,5]])")
	public abstract IMatrix matrixMultiplication(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Performs a subtraction between the two matrix operands"))
	@test("matrix([[1,2],[3,4]]) - matrix([[1,2],[3,4]]) = matrix([[0,0],[0,0]])") 
	public abstract IMatrix minus(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Performs a multiplication between the matrix operand and the float operand"))
	@test("matrix([[1,2],[3,4]]) * 2.5 = matrix([[2.5,5.0],[7.5,10]])") 
	public abstract IMatrix times(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Performs a multiplication between the two matrix operands"))
	@test("matrix([[1,2],[3,4]]) * 2 = matrix([[2,4],[6,8]])") 
	public abstract IMatrix times(Integer val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Divides all the elements of the matrix operand by the float operand"))
	@test("matrix([[1,2],[3,4]]) / 2.5 = matrix([[0.4,0.8],[1.2,1.6]])") 
	public abstract IMatrix divides(Double val) throws GamaRuntimeException;
	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Divides all the elements of the matrix operand by the integer operand"))
	@test("matrix([[1,2],[3,4]]) / 2 = matrix([[0.5,1],[1.5,2]])") 
	public abstract IMatrix divides(Integer val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Adds the float operand to all the elements in the matrix"))
	@test("matrix([[1,2],[3,4]]) + 22.5 = matrix([[23.5,24.5],[25.5,26.5]])") 
	public abstract IMatrix plus(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Adds the int operand to all the elements in the matrix"))
	@test("matrix([[1,2],[3,4]]) + 2 = matrix([[3,4],[5,6]])") 
	public abstract IMatrix plus(Integer val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Subtracts the float operand from all the elements in the matrix"))
	@test("matrix([[1,2],[3,4]]) - 1.5 = matrix([[-0.5,0.5],[1.5,2.5]])") 
	public abstract IMatrix minus(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Subtracts the int operand from all the elements in the matrix"))
	@test("matrix([[1,2],[3,4]]) - 1 = matrix([[0,1],[2,3]])") 
	public abstract IMatrix minus(Integer val) throws GamaRuntimeException;

	public abstract T get(IScope scope, final int col, final int row);

	public abstract void set(IScope scope, final int col, final int row, final Object obj) throws GamaRuntimeException;

	@operator (
			value = IKeyword.APPEND_HORIZONTALLY,
			content_type = ITypeProvider.BOTH,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "A matrix resulting from the concatenation of the rows of the two given matrices. If not both numerical or both object matrices, returns the first matrix.",
			masterDoc = true,
			examples = { @example (
					value = "matrix([[1.0,2.0],[3.0,4.0]]) append_horizontally matrix([[1,2],[3,4]])",
					equals = "matrix([[1.0,2.0],[3.0,4.0],[1.0,2.0],[3.0,4.0]])") })
	public abstract IMatrix opAppendHorizontally(final IScope scope, final IMatrix b);

	@operator (
			value = IKeyword.APPEND_VERTICALLY,
			content_type = ITypeProvider.BOTH,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "A matrix resulting from the concatenation of the columns  of the two given matrices. If not both numerical or both object matrices, returns the first matrix.",
			masterDoc = true,
			examples = { @example (
					value = "matrix([[1,2],[3,4]]) append_vertically matrix([[1,2],[3,4]])",
					equals = "matrix([[1,2,1,2],[3,4,3,4]])") })
	public abstract IMatrix opAppendVertically(final IScope scope, final IMatrix b);

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
	public abstract Double getDeterminant(IScope scope) throws GamaRuntimeException;

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
	public abstract Double getTrace(IScope scope) throws GamaRuntimeException;

	@operator (
			value = "eigenvalues",
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX })
	@doc (
			value = "The eigen values (matrix) of the given matrix",
			masterDoc = true,
			examples = { @example (
					value = "eigenvalues(matrix([[5,-3],[6,-4]]))",
					equals = "[2.0000000000000004,-0.9999999999999998]") })
	public abstract IList<Double> getEigen(IScope scope) throws GamaRuntimeException;

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
	public abstract IMatrix transpose(IScope scope) throws GamaRuntimeException;

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
	public abstract IMatrix<Double> inverse(IScope scope) throws GamaRuntimeException;

	@Override
	public abstract StreamEx<T> stream(final IScope scope);

	// public abstract void put(final int col, final int row, final double obj)
	// throws GamaRuntimeException;

	// public abstract void put(final int col, final int row, final int obj)
	// throws GamaRuntimeException;

	public abstract Object remove(IScope scope, final int col, final int row) throws GamaRuntimeException;

	public abstract void shuffleWith(RandomUtils randomAgent);

	@Override
	public abstract IMatrix copy(IScope scope) throws GamaRuntimeException;

	public abstract IMatrix copy(IScope scope, ILocation preferredSize, boolean copy);

}