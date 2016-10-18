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

import org.eclipse.emf.common.util.URI;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamlResourceInfoProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
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
	private static IGamlResourceInfoProvider infoProvider = null;

	public static <T> T notNull(final IScope scope, final T object) {
		return notNull(scope, object, "Error: nil value detected");
	}

	public static <T> T notNull(final IScope scope, final T object, final String error) {
		if (object == null) {
			throw GamaRuntimeException.error(error, scope);
		}
		return object;
	}

	@SuppressWarnings("rawtypes")
	public static <T extends IContainer> T emptyCheck(final IScope scope, final T container) {
		if (notNull(scope, container).isEmpty(scope)) {
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
		final IExpression expr = compileExpression(expression, a, true);
		if (expr == null) {
			return null;
		}
		final IScope scope = a.getScope().copy("in temporary expression evaluator");
		final Object o = scope.evaluate(expr, a).getValue();
		GAMA.releaseScope(scope);
		return o;
	}

	public static IExpression compileExpression(final String expression, final IAgent agent,
			final boolean onlyExpression) throws GamaRuntimeException {
		if (agent == null)
			throw GamaRuntimeException.error("");
		try {
			final IExpression result = getExpressionFactory().createExpr(expression,
					agent.getSpecies().getDescription());
			return result;
		} catch (final Throwable e) {
			// Maybe it is a statement instead ?
			if (!onlyExpression)
				try {
					final IExpression result = getExpressionFactory().createTemporaryActionForAgent(agent, expression);
					return result;
				} catch (final Throwable e2) {
					throw GamaRuntimeException.create(e2, agent.getScope());
				}
			else {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}
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
		final ITopLevelAgent agent = scope.getExperiment();
		if (agent == null)
			return null;
		return (ExperimentDescription) agent.getSpecies().getDescription();
	}

	public static void registerInfoProvider(final IGamlResourceInfoProvider info) {
		infoProvider = info;
	}

	public static GamlFileInfo getInfo(final URI uri, final long stamp) {
		return infoProvider.getInfo(uri, stamp);
	}

	public static ISyntacticElement getContents(final URI uri) {
		return infoProvider.getContents(uri);
	}

}
