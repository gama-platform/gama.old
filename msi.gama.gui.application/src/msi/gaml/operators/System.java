/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.GamaMap;
import msi.gaml.expressions.MapExpression;
import org.codehaus.janino.ScriptEvaluator;

/**
 * Written by drogoul Modified on 10 dŽc. 2010
 * 
 * @todo Description
 * 
 */
public class System {
	
	@operator(value = "dead")
	public static Boolean opDead(final IScope scope, final IAgent a) {
		return a.dead();
	}

	@operator(value = "every")
	public static Boolean opEvery(final IScope scope, final Integer period) {
		final int time = scope.getSimulationScope().getScheduler().getTime();
		return period > 0 && time >= period && time % period == 0;
	}

	@operator(value = { IExpressionParser.DOT, IExpressionParser.OF }, type = ITypeProvider.RIGHT_TYPE, content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.ADDRESSING)
	public static Object opGetValue(final IScope scope, final IAgent a, final IExpression s)
		throws GamaRuntimeException {
		if ( a == null ) { throw new GamaRuntimeWarning("Cannot evaluate " + s.toGaml() +
			" as the target agent is null"); }
		if ( a.dead() ) { throw new GamaRuntimeWarning("Cannot evaluate " + s.toGaml() +
			" as the target agent is dead"); }
		return scope.evaluate(s, a);
	}

	@operator(value = "copy", type = ITypeProvider.CHILD_TYPE, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static Object opCopy(final IScope scope, final Object o) throws GamaRuntimeException {
		if ( o instanceof IValue ) { return ((IValue) o).copy(); }
		return o;
	}

	@operator(value = "eval_gaml", can_be_const = false)
	public static Object opEvalGaml(final IScope scope, final String gaml) {
		IAgent agent = scope.getAgentScope();
		IDescription d = agent.getSpecies().getDescription();
		try {
			IExpression e =
				GAMA.getExpressionFactory().createExpr(new ExpressionDescription(gaml), d);
			return scope.evaluate(e, agent);
		} catch (GamlException e) {
			GUI.informConsole("Error in evaluating Gaml code : '" + gaml + "' in " +
				scope.getAgentScope() + java.lang.System.getProperty("line.separator") +
				"Reason: " + e.getMessage());

			return null;
		}

	}

	@operator(value = "eval_java", can_be_const = false)
	public static Object opEvalJava(final IScope scope, final String code) {
		try {
			ScriptEvaluator se = new ScriptEvaluator();
			se.setReturnType(Object.class);
			se.cook(code);
			// Evaluate script with actual parameter values.
			return se.evaluate(new Object[0]);

			// Version sans arguments pour l'instant.
		} catch (Exception e) {
			GUI.informConsole("Error in evaluating Java code : '" + code + "' in " +
				scope.getAgentScope() + java.lang.System.getProperty("line.separator") +
				"Reason: " + e.getMessage());
			return null;
		}
	}

	private static final String[] gamaDefaultImports = new String[] {};

	@operator(value = "evaluate_with", can_be_const = false)
	public static Object opEvalJava(final IScope scope, final String code,
		final IExpression parameters) {
		try {
			GamaMap param;
			if ( parameters instanceof MapExpression ) {
				param = ((MapExpression) parameters).getElements();
			} else {
				param = new GamaMap();
			}
			String[] parameterNames = new String[param.size() + 1];
			Class[] parameterTypes = new Class[param.size() + 1];
			Object[] parameterValues = new Object[param.size() + 1];
			parameterNames[0] = "scope";
			parameterTypes[0] = IScope.class;
			parameterValues[0] = scope;
			int i = 1;
			for ( Object e : param.entrySet() ) {
				Map.Entry<IExpression, IExpression> entry = (Map.Entry<IExpression, IExpression>) e;
				parameterNames[i] = entry.getKey().literalValue();
				parameterTypes[i] = entry.getValue().type().toClass();
				parameterValues[i] = entry.getValue().value(scope);
				i++;
			}
			ScriptEvaluator se = new ScriptEvaluator();
			se.setReturnType(Object.class);
			se.setDefaultImports(gamaDefaultImports);
			se.setParameters(parameterNames, parameterTypes);
			se.cook(code);
			// Evaluate script with actual parameter values.
			return se.evaluate(parameterValues);

		} catch (Exception e) {
			Throwable ee =
				e instanceof InvocationTargetException ? ((InvocationTargetException) e)
					.getTargetException() : e;
			GUI.informConsole("Error in evaluating Java code : '" + code + "' in " +
				scope.getAgentScope() + java.lang.System.getProperty("line.separator") +
				"Reason: " + ee.getMessage());
			return null;
		}
	}
}
