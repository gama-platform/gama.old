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

import org.eclipse.emf.ecore.EObject;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 27 d�c. 2010
 *
 * @todo Description
 *
 */
public interface IExpressionFactory {

	public static final ConstantExpression TRUE_EXPR = ConstantExpressionDescription.TRUE_EXPR_DESCRIPTION;
	public static final ConstantExpression FALSE_EXPR = ConstantExpressionDescription.FALSE_EXPR_DESCRIPTION;
	public static final ConstantExpression NIL_EXPR = ConstantExpressionDescription.NULL_EXPR_DESCRIPTION;
	public static final String TEMPORARY_ACTION_NAME = "__synthetic__action__";

	// public void registerParserProvider(IExpressionCompilerProvider parser);

	public abstract ConstantExpression createConst(final Object val, final IType type) throws GamaRuntimeException;

	public abstract ConstantExpression createConst(final Object val, final IType type, String name)
			throws GamaRuntimeException;

	public SpeciesConstantExpression createSpeciesConstant(final IType type);

	public abstract IExpression createExpr(final IExpressionDescription s, final IDescription context);

	public abstract IExpression createExpr(final String s, IDescription context);

	public abstract UnitConstantExpression getUnitExpr(final String unit);

	Arguments createArgumentMap(ActionDescription action, IExpressionDescription args, IDescription context);

	public IExpressionCompiler getParser();

	IExpression createVar(String name, IType type, boolean isConst, int scope, IDescription definitionDescription);

	public IExpression createList(final Iterable<? extends IExpression> elements);

	public IExpression createMap(final Iterable<? extends IExpression> elements);

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

	// public abstract boolean isInitialized();

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
	 * 
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
	IExpression createAction(String op, IDescription callerContext, ActionDescription action, IExpression call,
			Arguments arguments);

	public abstract IExpression createTemporaryActionForAgent(IAgent agent, String expression);

}