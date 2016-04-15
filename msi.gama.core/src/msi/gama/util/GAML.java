/*********************************************************************************************
 *
 *
 * 'GAML.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelFactory;

/**
 * Class GAML. Static support for various GAML constructs and functions
 *
 * @author drogoul
 * @since 16 mai 2013
 *
 */
public class GAML {

	public static IExpressionFactory expressionFactory = null;
	public static ModelFactory modelFactory = null;

	public static <T> T nullCheck(final IScope scope, final T object) {
		return nullCheck(scope, object, "Error: nil value detected");
	}

	public static <T> T nullCheck(final IScope scope, final T object, final String error) {
		if (object == null) {
			throw GamaRuntimeException.error(error, scope);
		}
		return object;
	}

	public static <T extends IContainer> T emptyCheck(final IScope scope, final T container) {
		if (nullCheck(scope, container).isEmpty(scope)) {
			throw GamaRuntimeException.error("Error: the container is empty", scope);
		}
		return container;
	}

	/**
	 *
	 * Parsing and compiling GAML utilities
	 *
	 */

	public static ModelFactory getModelFactory() {
		if (modelFactory == null) {
			modelFactory = DescriptionFactory.getModelFactory();
		}
		return modelFactory;
	}

	public static IExpressionFactory getExpressionFactory() {
		if (expressionFactory == null) {
			expressionFactory = new GamlExpressionFactory();
		}
		return expressionFactory;
	}

	public static Object evaluateExpression(final String expression, final IAgent a) throws GamaRuntimeException {
		if (a == null) {
			return null;
		}
		if (expression == null || expression.isEmpty()) {
			throw GamaRuntimeException.error("Enter a valid expression", a.getScope());
		}
		final IExpression expr = compileExpression(expression, a);
		if (expr == null) {
			return null;
		}
		final IScope scope = a.getScope().copy("in temporary expression evaluator");
		final Object o = scope.evaluate(expr, a);
		GAMA.releaseScope(scope);
		return o;
	}

	public static IExpression compileExpression(final String expression, final IAgent agent)
			throws GamaRuntimeException {
		return getExpressionFactory().createExpr(expression, agent.getSpecies().getDescription());
	}

	public static ModelDescription getModelContext() {
		if (GAMA.getFrontmostController() == null) {
			return null;
		}
		return (ModelDescription) GAMA.getFrontmostController().getExperiment().getModel().getDescription();
	}

	public static ExperimentDescription getExperimentContext(final IAgent a) {
		if (a == null) {
			return null;
		}
		final IScope scope = a.getScope();
		return (ExperimentDescription) scope.getExperimentContext();
	}

}
