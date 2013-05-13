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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.types.*;
import org.codehaus.janino.ScriptEvaluator;

/**
 * Written by drogoul Modified on 10 dŽc. 2010
 * 
 * @todo Description
 * 
 */
public class System {

	@operator(value = "dead")
	@doc(value = "true if the agent is dead, false otherwise.", examples = "dead(agent_A) 	--: 	true or false")
	public static Boolean opDead(final IScope scope, final IAgent a) {
		return a.dead();
	}

	@operator(value = "every")
	@doc(value = "true every operand time step, false otherwise", comment = "the value of the every operator depends deeply on the time step. It can be used to do something not every step.", examples = {
		"reflex text_every {", "	if every(2) {write \"the time step is even\";}",
		"		else {write \"the time step is odd\";}" })
	public static Boolean opEvery(final IScope scope, final Integer period) {
		final int time = scope.getClock().getCycle();
		return period > 0 && time >= period && time % period == 0;
	}

	@operator(value = { IKeyword._DOT, IKeyword.OF }, type = ITypeProvider.SECOND_TYPE, content_type = ITypeProvider.SECOND_CONTENT_TYPE, index_type = ITypeProvider.SECOND_KEY_TYPE)
	@doc(value = "returns an evaluation of the expresion (right-hand operand) in the scope the given agent.", special_cases = "if the agent is nil or dead, throws an exception", examples = "agent.location 		--: 	returns the location of the agent")
	public static Object opGetValue(final IScope scope, final IAgent a, final IExpression s)
		throws GamaRuntimeException {
		if ( a == null ) { throw GamaRuntimeException.warning("Cannot evaluate " + s.toGaml() +
			" as the target agent is null"); }
		if ( a.dead() ) { throw GamaRuntimeException.warning("Cannot evaluate " + s.toGaml() +
			" as the target agent is dead"); }
		return scope.evaluate(s, a);
	}

	@operator(value = "copy", type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns a copy of the operand.")
	public static Object opCopy(final IScope scope, final Object o) throws GamaRuntimeException {
		if ( o instanceof IValue ) { return ((IValue) o).copy(scope); }
		return o;
	}

	@operator(value = "user_input")
	@doc(value = "asks the user for some values (not defined as parameters)", comment = "This operator takes a map [string::value] as argument, displays a dialog asking the user for these values, and returns the same map with the modified values (if any). "
		+ "The dialog is modal and will interrupt the execution of the simulation until the user has either dismissed or accepted it. It can be used, for instance, in an init section to force the user to input new values instead of relying on the initial values of parameters :", examples = {
		"init {", "	let values <- user_input([\"Number\" :: 100, \"Location\" :: {10, 10}]);",
		"	create node number : int(values at \"Number\") with: [location:: (point(values at \"Location\"))];", "}" })
	public static GamaMap<String, Object> userInput(final IScope scope, final IExpression map) {
		IAgent agent = scope.getAgentScope();
		return userInput(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", map);
	}

	@operator(value = "user_input")
	public static GamaMap<String, Object> userInput(final IScope scope, final String title, IExpression expr) {
		GamaMap<String, Object> initialValues = new GamaMap();
		GamaMap<String, IType> initialTypes = new GamaMap();
		if ( expr instanceof MapExpression ) {
			MapExpression map = (MapExpression) expr;
			for ( Map.Entry<IExpression, IExpression> entry : map.getElements().entrySet() ) {
				String key = Cast.asString(scope, entry.getKey().value(scope));
				IExpression val = entry.getValue();
				initialValues.put(key, val.value(scope));
				initialTypes.put(key, val.getType());
			}
		} else {
			initialValues = Cast.asMap(scope, expr.value(scope));
			for ( Map.Entry<String, Object> entry : initialValues.entrySet() ) {
				initialTypes.put(entry.getKey(), Types.get(entry.getValue().getClass()));
			}
		}
		if ( initialValues.isEmpty() ) { return initialValues; }
		return new GamaMap(GuiUtils.openUserInputDialog(title, initialValues, initialTypes));
	}

	@operator(value = "eval_gaml", can_be_const = false)
	@doc(value = "evaluates the given GAML string.", examples = "eval_gaml(\"2+3\")    --:   5", see = "eval_java")
	public static Object opEvalGaml(final IScope scope, final String gaml) {
		IAgent agent = scope.getAgentScope();
		IDescription d = agent.getSpecies().getDescription();
		try {
			IExpression e = GAMA.getExpressionFactory().createExpr(gaml, d);
			return scope.evaluate(e, agent);
		} catch (GamaRuntimeException e) {
			GuiUtils.informConsole("Error in evaluating Gaml code : '" + gaml + "' in " + scope.getAgentScope() +
				java.lang.System.getProperty("line.separator") + "Reason: " + e.getMessage());

			return null;
		}

	}

	@operator(value = "eval_java", can_be_const = false)
	@doc(value = "evaluates the given java code string.", deprecated = "Does not work", see = { "eval_gaml",
		"evaluate_with" })
	public static Object opEvalJava(final IScope scope, final String code) {
		try {
			ScriptEvaluator se = new ScriptEvaluator();
			se.setReturnType(Object.class);
			se.cook(code);
			// Evaluate script with actual parameter values.
			return se.evaluate(new Object[0]);

			// Version sans arguments pour l'instant.
		} catch (Exception e) {
			GuiUtils.informConsole("Error in evaluating Java code : '" + code + "' in " + scope.getAgentScope() +
				java.lang.System.getProperty("line.separator") + "Reason: " + e.getMessage());
			return null;
		}
	}

	private static final String[] gamaDefaultImports = new String[] {};

	@operator(value = "evaluate_with", can_be_const = false)
	@doc(value = "evaluates the left-hand java expressions with the map of parameters (right-hand operand)", see = {
		"eval_gaml", "eval_java" })
	public static Object opEvalJava(final IScope scope, final String code, final IExpression parameters) {
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
				parameterTypes[i] = entry.getValue().getType().toClass();
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
				e instanceof InvocationTargetException ? ((InvocationTargetException) e).getTargetException() : e;
			GuiUtils.informConsole("Error in evaluating Java code : '" + code + "' in " + scope.getAgentScope() +
				java.lang.System.getProperty("line.separator") + "Reason: " + ee.getMessage());
			return null;
		}
	}
}
