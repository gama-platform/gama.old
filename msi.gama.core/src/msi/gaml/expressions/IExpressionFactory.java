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
import org.eclipse.emf.ecore.EObject;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.operators.IUnits;
import msi.gaml.statements.Arguments;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 27 d�c. 2010
 *
 * @todo Description
 *
 */
public interface IExpressionFactory {

	public static final ConstantExpression TRUE_EXPR = new ConstantExpression(true, Types.BOOL);
	public static final ConstantExpression FALSE_EXPR = new ConstantExpression(false, Types.BOOL);
	public static final ConstantExpression NIL_EXPR = new ConstantExpression(null, Types.NO_TYPE);
	public final static Map<String, UnitConstantExpression> UNITS_EXPR = IUnits.UNITS_EXPR;

	// public void registerParserProvider(IExpressionCompilerProvider parser);

	public abstract ConstantExpression createConst(final Object val, final IType type) throws GamaRuntimeException;

	public abstract ConstantExpression createConst(final Object val, final IType type, String name)
		throws GamaRuntimeException;

	public SpeciesConstantExpression createSpeciesConstant(final IType type);

	public abstract IExpression createExpr(final IExpressionDescription s, final IDescription context);

	public abstract IExpression createExpr(final String s, IDescription context);

	public abstract ConstantExpression getUnitExpr(final String unit);

	Map<String, IExpressionDescription> createArgumentMap(StatementDescription action, IExpressionDescription args,
		IDescription context);

	public IExpressionCompiler getParser();

	IExpression createVar(String name, IType type, boolean isConst, int scope, IDescription definitionDescription);

	public IExpression createList(final List<? extends IExpression> elements);

	public IExpression createMap(final List<? extends IExpression> elements);

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

	/**
	 * Creates a new unit expression
	 * @param value
	 * @param t
	 * @param doc
	 * @return
	 */
	public UnitConstantExpression createUnit(Object value, IType t, String name, String doc, String[] names);

	/**
	 * @param op
	 * @param callerContext
	 * @param action
	 * @param call
	 * @param arguments
	 * @return
	 */
	IExpression createAction(String op, IDescription callerContext, StatementDescription action, IExpression call,
		Arguments arguments);

}