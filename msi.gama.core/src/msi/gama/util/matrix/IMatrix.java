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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 15 d�c. 2010
 * 
 * @todo Description
 * 
 * @param <T>
 */
@vars({ @var(name = IMatrix.DIMENSION, type = IType.POINT), @var(name = IMatrix.ROWS, type = IType.INT),
	@var(name = IMatrix.COLUMNS, type = IType.INT) })
public interface IMatrix<T> extends IModifiableContainer<ILocation, T, ILocation, T>,
	IAddressableContainer<ILocation, T, ILocation, T> {

	/**
	 * Cols, rows instead of row cols because inte nded to work with xSize and ySize dimensions.
	 */

	public static final String DIMENSION = "dimension";

	public static final String ROWS = "rows";

	public static final String COLUMNS = "columns";

	@getter(ROWS)
	public abstract int getRows(IScope scope);

	@getter(COLUMNS)
	public abstract int getCols(IScope scope);

	@getter(DIMENSION)
	public abstract ILocation getDimensions();

	@operator(value = "rows_list", can_be_const = true, content_type = IType.LIST, category={IOperatorCategory.MATRIX})
	@doc(value = "returns a list of the rows of the matrix, with each row as a list of elements", examples = {
		@example(value="rows_list(matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]))",
				 equals=" matrix([[\"el11\",\"el21\",\"el31\"],[\"el12\",\"el22\",\"el32\"],[\"el13\",\"el23\",\"el33\"]])") }, see = "columns_list")
	public abstract IList<IList<T>> getRowsList(IScope scope);

	@operator(value = "columns_list", can_be_const = true, content_type = IType.LIST, category={IOperatorCategory.MATRIX})
	@doc(value = "returns a list of the columns of the matrix, with each column as a list of elements", examples = {
		@example(value="columns_list(matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]))",
				 equals="[[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]") }, see = "rows_list")
	public abstract IList<IList<T>> getColumnsList(IScope scope);

	@operator(value = "row_at", content_type = ITypeProvider.FIRST_CONTENT_TYPE, can_be_const = true, category={IOperatorCategory.MATRIX})
	@doc(value = "returns the row at a num_line (rigth-hand operand)", 
		examples = { @example(value="matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]) row_at 2",equals= "[\"el13\",\"el23\",\"el33\"]") }, 
		see = {"column_at", "columns_list" })
	public abstract IList<T> getRow(IScope scope, Integer num_line);

	@operator(value = "column_at", content_type = ITypeProvider.FIRST_CONTENT_TYPE, can_be_const = true, category={IOperatorCategory.MATRIX})
	@doc(value = "returns the column at a num_col (rigth-hand operand)", 
		examples = { @example(value="matrix([[\"el11\",\"el12\",\"el13\"],[\"el21\",\"el22\",\"el23\"],[\"el31\",\"el32\",\"el33\"]]) column_at 2", equals="[\"el31\",\"el32\",\"el33\"]") }, 
		see = {"row_at", "rows_list" })
	public abstract IList<T> getColumn(IScope scope, Integer num_line);

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix plus(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator(value = IKeyword.MULTIPLY, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix times(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator(value = IKeyword.DIVIDE, can_be_const = true, content_type = IType.FLOAT, category={IOperatorCategory.MATRIX})
	public abstract IMatrix divides(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator(value = ".", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	@doc(value = "Matrix dot product.")
	public abstract IMatrix matrixMultiplication(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix minus(IScope scope, IMatrix other) throws GamaRuntimeException;

	@operator(value = IKeyword.MULTIPLY, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix times(Double val) throws GamaRuntimeException;

	@operator(value = IKeyword.MULTIPLY, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix times(Integer val) throws GamaRuntimeException;

	@operator(value = IKeyword.DIVIDE, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix divides(Double val) throws GamaRuntimeException;

	@operator(value = IKeyword.DIVIDE, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix divides(Integer val) throws GamaRuntimeException;

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix plus(Double val) throws GamaRuntimeException;

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix plus(Integer val) throws GamaRuntimeException;

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix minus(Double val) throws GamaRuntimeException;

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.MATRIX})
	public abstract IMatrix minus(Integer val) throws GamaRuntimeException;

	public abstract T get(IScope scope, final int col, final int row);

	public abstract void set(IScope scope, final int col, final int row, final Object obj) throws GamaRuntimeException;

	// public abstract void put(final int col, final int row, final double obj)
	// throws GamaRuntimeException;

	// public abstract void put(final int col, final int row, final int obj)
	// throws GamaRuntimeException;

	public abstract Object remove(IScope scope, final int col, final int row) throws GamaRuntimeException;

	public abstract void shuffleWith(RandomUtils randomAgent);

	@Override
	public abstract IMatrix copy(IScope scope) throws GamaRuntimeException;

	public abstract IMatrix copy(IScope scope, ILocation preferredSize);

}