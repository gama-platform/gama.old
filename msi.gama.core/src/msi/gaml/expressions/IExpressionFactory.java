/*********************************************************************************************
 * 
 * 
 * 'IExpressionFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 27 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpressionFactory {

	public static final IExpression TRUE_EXPR = new ConstantExpression(true, Types.get(IType.BOOL));
	public static final IExpression FALSE_EXPR = new ConstantExpression(false, Types.get(IType.BOOL));
	public static final IExpression NIL_EXPR = new ConstantExpression(null, Types.NO_TYPE);

	public void registerParserProvider(IExpressionCompilerProvider parser);

	public abstract IExpression createConst(final Object val, final IType type) throws GamaRuntimeException;

	// public abstract IExpression createConst(final Object val, final IType type, final IType contentType)
	// throws GamaRuntimeException;

	public abstract IExpression createExpr(final IExpressionDescription s, final IDescription context);

	public abstract IExpression createExpr(final String s, IDescription context);

	public abstract IExpression createUnitExpr(final String unit, IDescription context);

	Map<String, IExpressionDescription> createArgumentMap(StatementDescription action, IExpressionDescription args,
		IDescription context);

	// public Set<String> parseLiteralArray(final IExpressionDescription s,
	// final IDescription context, boolean skills);

	public IExpressionCompiler getParser();

	IExpression createVar(String name, IType type, boolean isConst, int scope, IDescription definitionDescription);

	public IExpression createList(final List<? extends IExpression> elements);

	public IExpression createMap(final List<? extends IExpression> elements);

	// IExpression createOperator(String op, IDescription context, IExpression ... exprs);

	IExpression createAction(String op, IDescription callerContext, StatementDescription action, IExpression call,
		IExpression args);

	/**
	 * @param op
	 * @param context
	 * @param currentEObject
	 * @param args
	 * @return
	 */
	IExpression createOperator(String op, IDescription context, EObject currentEObject, IExpression ... args);

	/**
	 * @param type
	 * @param keyType
	 * @param contentsType
	 * @return
	 */
	IExpression createTypeExpression(IType type);

	public abstract boolean isInitialized();

	/**
	 * @param symbolDescription
	 * @param facet
	 * @return
	 */
	public EObject getFacetExpression(IDescription context, EObject facet);

	/**
	 *
	 */
	public void resetParser();

}