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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import java.util.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 27 déc. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpressionFactory {

	public static final IExpression TRUE_EXPR = new ConstantExpression(true, Types.get(IType.BOOL),
		Types.get(IType.BOOL));
	public static final IExpression FALSE_EXPR = new ConstantExpression(false,
		Types.get(IType.BOOL), Types.get(IType.BOOL));
	public static final IExpression NIL_EXPR = new ConstantExpression(null, Types.NO_TYPE,
		Types.NO_TYPE);

	public void registerParser(IExpressionCompiler parser);

	public abstract IExpression createConst(final Object val, final IType type)
		throws GamaRuntimeException;

	public abstract IExpression createConst(final Object val, final IType type,
		final IType contentType) throws GamaRuntimeException;

	public abstract IExpression createExpr(final IExpressionDescription s,
		final IDescription context);

	public abstract IExpression createExpr(final String s, IDescription context);

	public abstract IExpression createUnitExpr(final String unit, IDescription context);

	Map<String, IExpressionDescription> createArgumentMap(StatementDescription action,
		IExpressionDescription args, IDescription context);

	// public Set<String> parseLiteralArray(final IExpressionDescription s,
	// final IDescription context, boolean skills);

	public IExpressionCompiler getParser();

	IVarExpression createVar(String name, IType type, IType contentType, IType keyType,
		boolean isConst, int scope, IDescription definitionDescription);

	public IExpression createList(final List<? extends IExpression> elements);

	public IExpression createMap(final List<? extends IExpression> elements);

	IExpression createOperator(String op, IDescription context, IExpression ... exprs);

	IExpression createAction(String op, IDescription callerContext, StatementDescription action,
		IExpression ... exprs);

}