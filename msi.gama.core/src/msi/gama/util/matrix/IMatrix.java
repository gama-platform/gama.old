/*******************************************************************************************************
 *
 * msi.gama.util.matrix.IMatrix.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
import msi.gama.util.file.IFieldMatrixProvider;
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
public interface IMatrix<T> extends IModifiableContainer<ILocation, T, ILocation, T>,
		IAddressableContainer<ILocation, T, ILocation, T>, IFieldMatrixProvider {

	/**
	 * Cols, rows instead of row cols because intended to work with xSize and ySize dimensions.
	 */

	String DIMENSION = "dimension";

	String ROWS = "rows";

	String COLUMNS = "columns";

	@Override
	@getter (ROWS)
	int getRows(IScope scope);

	@Override
	@getter (COLUMNS)
	int getCols(IScope scope);

	/**
	 * Redefined to reverse the logic (calls getFieldData())
	 */
	@Override
	default double[] getBand(final IScope scope, final int index) {
		if (index == 0)
			return getFieldData(scope);
		else
			return null;
	}

	// Redefined so as to reverse the calling (getBand() now calls it)
	@Override
	double[] getFieldData(final IScope scope);

	@getter (DIMENSION)
	GamaPoint getDimensions();

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
	IList<IList<T>> getRowsList(IScope scope);

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
	IList<IList<T>> getColumnsList(IScope scope);

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
	IList<T> getRow(IScope scope, Integer num_line);

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
	IList<T> getColumn(IScope scope, Integer num_line);

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Returns a matrix containing the addition of  the elements of two matrices in argument "))
	@test ("matrix([[1,2],[3,4]]) + matrix([[1,2],[3,4]]) = matrix([[2,4],[6,8]])")
	IMatrix plus(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Multiplies the two matrices operands"))
	@test ("matrix([[1,2],[3,4]]) * matrix([[1,2],[3,4]]) = matrix([[1,4],[9,16]]) ")
	IMatrix times(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			content_type = IType.FLOAT,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Divides the two matrices operands"))
	@test ("matrix([[1,2],[3,4]]) / matrix([[1,2],[3,4]]) = matrix([[1,1],[1,1]])")
	IMatrix divides(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Performs a subtraction between the two matrix operands"))
	@test ("matrix([[1,2],[3,4]]) - matrix([[1,2],[3,4]]) = matrix([[0,0],[0,0]])")
	IMatrix minus(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Performs a multiplication between the matrix operand and the float operand"))
	@test ("matrix([[1,2],[3,4]]) * 2.5 = matrix([[2.5,5.0],[7.5,10]])")
	IMatrix times(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.MATRIX },
			doc = @doc ("Performs a multiplication between the two matrix operands"))
	@test ("matrix([[1,2],[3,4]]) * 2 = matrix([[2,4],[6,8]])")
	IMatrix times(Integer val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Divides all the elements of the matrix operand by the float operand"))
	@test ("matrix([[1,2],[3,4]]) / 2.5 = matrix([[0.4,0.8],[1.2,1.6]])")
	IMatrix divides(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Divides all the elements of the matrix operand by the integer operand"))
	@test ("matrix([[1,2],[3,4]]) / 2 = matrix([[0.5,1],[1.5,2]])")
	IMatrix divides(Integer val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Adds the float operand to all the elements in the matrix"))
	@test ("matrix([[1,2],[3,4]]) + 22.5 = matrix([[23.5,24.5],[25.5,26.5]])")
	IMatrix plus(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Adds the int operand to all the elements in the matrix"))
	@test ("matrix([[1,2],[3,4]]) + 2 = matrix([[3,4],[5,6]])")
	IMatrix plus(Integer val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Subtracts the float operand from all the elements in the matrix"))
	@test ("matrix([[1,2],[3,4]]) - 1.5 = matrix([[-0.5,0.5],[1.5,2.5]])")
	IMatrix minus(Double val) throws GamaRuntimeException;

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MATRIX },
			concept = {},
			doc = @doc ("Subtracts the int operand from all the elements in the matrix"))
	@test ("matrix([[1,2],[3,4]]) - 1 = matrix([[0,1],[2,3]])")
	IMatrix minus(Integer val) throws GamaRuntimeException;

	T get(IScope scope, final int col, final int row);

	void set(IScope scope, final int col, final int row, final Object obj) throws GamaRuntimeException;

	@Override
	StreamEx<T> stream(final IScope scope);

	@Override
	default boolean containsKey(final IScope scope, final Object o) {
		if (o instanceof GamaPoint) {
			final GamaPoint p = (GamaPoint) o;
			return p.x >= 0 && p.y >= 0 && p.x < getCols(scope) && p.y < getRows(scope);
		}
		return false;
	}

	Object remove(IScope scope, final int col, final int row) throws GamaRuntimeException;

	void shuffleWith(RandomUtils randomAgent);

	@Override
	IMatrix copy(IScope scope) throws GamaRuntimeException;

	IMatrix copy(IScope scope, ILocation preferredSize, boolean copy);

	@Override
	IMatrix<T> reverse(final IScope scope) throws GamaRuntimeException;

}