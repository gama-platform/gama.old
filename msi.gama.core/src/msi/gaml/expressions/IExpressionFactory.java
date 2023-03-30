/*******************************************************************************************************
 *
 * IExpressionFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import org.eclipse.emf.ecore.EObject;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.types.SpeciesConstantExpression;
import msi.gaml.expressions.units.UnitConstantExpression;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;
import msi.gaml.types.Signature;

/**
 * Written by drogoul Modified on 27 d�c. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes" })
public interface IExpressionFactory {

	/** The true expr. */
	ConstantExpression TRUE_EXPR = ConstantExpressionDescription.TRUE_EXPR_DESCRIPTION;

	/** The false expr. */
	ConstantExpression FALSE_EXPR = ConstantExpressionDescription.FALSE_EXPR_DESCRIPTION;

	/** The nil expr. */
	ConstantExpression NIL_EXPR = ConstantExpressionDescription.NULL_EXPR_DESCRIPTION;

	/** The temporary action name. */
	String TEMPORARY_ACTION_NAME = "__synthetic__action__";

	// public void registerParserProvider(IExpressionCompilerProvider parser);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param val
	 *            the val
	 * @param type
	 *            the type
	 * @return the constant expression
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	ConstantExpression createConst(final Object val, final IType type) throws GamaRuntimeException;

	/**
	 * Creates a new IExpression object.
	 *
	 * @param val
	 *            the val
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @return the constant expression
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	ConstantExpression createConst(final Object val, final IType type, String name) throws GamaRuntimeException;

	/**
	 * Creates a new IExpression object.
	 *
	 * @param type
	 *            the type
	 * @return the species constant expression
	 */
	SpeciesConstantExpression createSpeciesConstant(final IType type);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param s
	 *            the s
	 * @param context
	 *            the context
	 * @return the i expression
	 */
	IExpression createExpr(final IExpressionDescription s, final IDescription context);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param s
	 *            the s
	 * @param context
	 *            the context
	 * @return the i expression
	 */
	IExpression createExpr(final String s, IDescription context);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param s
	 *            the s
	 * @param context
	 *            the context
	 * @param additionalContext
	 *            the additional context
	 * @return the i expression
	 */
	IExpression createExpr(final String s, final IDescription context, final IExecutionContext additionalContext);

	/**
	 * Gets the unit expr.
	 *
	 * @param unit
	 *            the unit
	 * @return the unit expr
	 */
	UnitConstantExpression getUnitExpr(final String unit);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param action
	 *            the action
	 * @param args
	 *            the args
	 * @param context
	 *            the context
	 * @return the arguments
	 */
	Arguments createArgumentMap(ActionDescription action, IExpressionDescription args, IDescription context);

	/**
	 * Gets the parser.
	 *
	 * @return the parser
	 */
	// IExpressionCompiler getParser();

	/**
	 * Creates a new IExpression object.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isConst
	 *            the is const
	 * @param scope
	 *            the scope
	 * @param definitionDescription
	 *            the definition description
	 * @return the i expression
	 */
	IExpression createVar(String name, IType type, boolean isConst, int scope, IDescription definitionDescription);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param elements
	 *            the elements
	 * @return the i expression
	 */
	IExpression createList(final Iterable<? extends IExpression> elements);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param elements
	 *            the elements
	 * @return the i expression
	 */
	IExpression createMap(final Iterable<? extends IExpression> elements);

	/**
	 * @param op
	 * @param context
	 * @param currentEObject
	 * @param args
	 * @return
	 */
	IExpression createOperator(String op, IDescription context, EObject currentEObject, IExpression... args);

	/**
	 * @param type
	 * @param keyType
	 * @param contentsType
	 * @return
	 */
	IExpression createTypeExpression(IType type);

	/**
	 *
	 */
	void resetParser();

	/**
	 * Creates a new unit expression
	 *
	 * @param value
	 * @param t
	 * @param doc
	 * @return
	 */
	UnitConstantExpression createUnit(Object value, IType t, String name, String doc, String deprecated, boolean isTime,
			String[] names);

	/**
	 * @param op
	 * @param callerContext
	 * @param action
	 * @param call
	 * @param arguments
	 * @return
	 */
	IExpression createAction(String op, IDescription callerContext, ActionDescription action, IExpression call,
			Arguments arguments);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param agent
	 *            the agent
	 * @param expression
	 *            the expression
	 * @param tempContext
	 *            the temp context
	 * @return the i expression
	 */
	IExpression createTemporaryActionForAgent(IAgent agent, String expression, IExecutionContext tempContext);

	/**
	 * Checks for operator.
	 *
	 * @param op
	 *            the op
	 * @param context
	 *            the context
	 * @param object
	 *            the object
	 * @param compiledArgs
	 *            the compiled args
	 * @return true, if successful
	 */
	boolean hasOperator(String op, IExpression... compiledArgs);

	/**
	 * Checks for exact operator. The type must exactly correspond.
	 *
	 * @param op
	 *            the op
	 * @param compiledArgs
	 *            the compiled args
	 * @return true, if successful
	 */
	boolean hasExactOperator(String op, IExpression compiledArg);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param context
	 *            the context
	 * @param toCast
	 *            the to cast
	 * @param createTypeExpression
	 *            the create type expression
	 * @return the i expression
	 */
	IExpression createAs(IDescription context, IExpression toCast, IExpression createTypeExpression);

	/**
	 * Creates a new IExpression object.
	 *
	 * @param context
	 *            the context
	 * @param toCast
	 *            the to cast
	 * @param type
	 *            the type
	 * @return the i expression
	 */
	default IExpression createAs(final IDescription context, final IExpression toCast, final IType<?> type) {
		return createAs(context, toCast, createTypeExpression(type));
	}

	/**
	 * Checks for operator.
	 *
	 * @param op
	 *            the op
	 * @param sig
	 *            the sig
	 * @return true, if successful
	 */
	boolean hasOperator(String op, Signature sig);

}