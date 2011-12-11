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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import java.util.List;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.operators.Maths;

/**
 * Written by drogoul Modified on 15 déc. 2010
 * 
 * @todo Description
 * 
 * @param <T>
 */
@vars({ @var(name = IMatrix.DIMENSION, type = IType.POINT_STR),
	@var(name = IMatrix.ROWS, type = IType.INT_STR),
	@var(name = IMatrix.COLUMNS, type = IType.INT_STR) })
public interface IMatrix<T> extends IGamaContainer<GamaPoint, T> {

	/**
	 * Cols, rows instead of row cols because inte nded to work with xSize and ySize dimensions.
	 */

	public static final String DIMENSION = "dimension";

	public static final String ROWS = "rows";

	public static final String COLUMNS = "columns";

	@getter(var = ROWS)
	public abstract int getRows();

	@getter(var = COLUMNS)
	public abstract int getCols();

	@getter(var = DIMENSION)
	public abstract GamaPoint getDimensions();

	@operator(value = "rows_list", can_be_const = true, content_type = IType.LIST)
	public abstract List<List<T>> getRowsList();

	@operator(value = "columns_list", can_be_const = true, content_type = IType.LIST)
	public abstract List<List<T>> getColumnsList();

	@operator(value = "row_at", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public abstract List<T> getRow(Integer num_line);

	@operator(value = "column_at", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public abstract List<T> getColumn(Integer num_line);

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public abstract IMatrix plus(IMatrix other) throws GamaRuntimeException;

	@operator(value = Maths.TIMES, priority = IPriority.PRODUCT, can_be_const = true)
	public abstract IMatrix times(IMatrix other) throws GamaRuntimeException;

	@operator(value = Maths.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public abstract IMatrix minus(IMatrix other) throws GamaRuntimeException;

	public abstract T get(final int col, final int row);

	public abstract void put(final int col, final int row, final T obj) throws GamaRuntimeException;

	public abstract void put(final int col, final int row, final double obj)
		throws GamaRuntimeException;

	public abstract void put(final int col, final int row, final int obj)
		throws GamaRuntimeException;

	public abstract Object remove(final int col, final int row) throws GamaRuntimeException;

	public abstract void shuffleWith(RandomAgent randomAgent);

}