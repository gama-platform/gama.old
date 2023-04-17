/*******************************************************************************************************
 *
 * IExpressionCompiler.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.runtime.IExecutionContext;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.statements.Arguments;

/**
 * Written by drogoul Modified on 28 d�c. 2010
 *
 * @todo Description
 *
 */
public interface IExpressionCompiler<T> extends IDisposable {

	/**
	 * Compile.
	 *
	 * @param s the s
	 * @param parsingContext the parsing context
	 * @return the i expression
	 */
	IExpression compile(final IExpressionDescription s, final IDescription parsingContext);

	/**
	 * Compile.
	 *
	 * @param expression the expression
	 * @param parsingContext the parsing context
	 * @param tempContext the temp context
	 * @return the i expression
	 */
	IExpression compile(final String expression, final IDescription parsingContext, IExecutionContext tempContext);

	/**
	 * Parses the arguments.
	 *
	 * @param action the action
	 * @param eObject the e object
	 * @param context the context
	 * @param compileArgValues the compile arg values
	 * @return the arguments
	 */
	Arguments parseArguments(ActionDescription action, EObject eObject, IDescription context, boolean compileArgValues);

	/**
	 * @param context
	 * @param facet
	 * @return
	 */

	List<IDescription> compileBlock(final String string, final IDescription actionContext,
			IExecutionContext tempContext);

}