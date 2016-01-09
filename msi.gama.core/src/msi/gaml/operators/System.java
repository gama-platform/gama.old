/*********************************************************************************************
 *
 *
 * 'System.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 *
 * @todo Description
 *
 */
public class System {

	@operator(value = "dead", category = { IOperatorCategory.SYSTEM })
	@doc(value = "true if the agent is dead, false otherwise.", examples = @example(value = "dead(agent_A)",
	equals = "true or false",
	isExecutable = false))
	public static Boolean opDead(final IScope scope, final IAgent a) {
		return a == null || a.dead();
	}

	@operator(value = "every", category = { IOperatorCategory.SYSTEM })
	@doc(value = "true every operand * cycle, false otherwise",
	comment = "the value of the every operator depends on the cycle. It can be used to do something every x cycle.",
	examples = { @example("if every(2) {write \"the time step is even\";}"),
		@example("	     else {write \"the time step is odd\";}") })
	public static
	Boolean opEvery(final IScope scope, final Integer period) {
		final int time = scope.getClock().getCycle();
		return period > 0 && time >= period && time % period == 0;
	}

	@operator(value = { IKeyword._DOT, IKeyword.OF },
		type = ITypeProvider.SECOND_TYPE,
		content_type = ITypeProvider.SECOND_CONTENT_TYPE,
		index_type = ITypeProvider.SECOND_KEY_TYPE,
		category = { IOperatorCategory.SYSTEM })
	@doc(value = "It has two different uses: it can be the dot product between 2 matrices or return an evaluation of the expresion (right-hand operand) in the scope the given agent.",
	masterDoc = true,
	special_cases = "if the agent is nil or dead, throws an exception",
	usages = @usage(value = "if the left operand is an agent, it evaluates of the expresion (right-hand operand) in the scope the given agent",
			examples = {
		@example(value = "agent1.location", equals = "the location of the agent agent1", isExecutable = false),
		@example(value = "map(nil).keys", raises = "exception", isTestOnly = false) }))
	public static
		Object opGetValue(final IScope scope, final IAgent a, final IExpression s) throws GamaRuntimeException {
		if ( a == null ) {
			if ( !scope.interrupted() ) { throw GamaRuntimeException.warning("Cannot evaluate " + s.serialize(false) +
				" as the target agent is null"); }
			return null;
		}
		if ( a.dead() ) {
			// GuiUtils.debug("System.opGetValue");
			if ( !scope.interrupted() ) {
				// GuiUtils.debug("System.opGetValue error");
				throw GamaRuntimeException.warning("Cannot evaluate " + s.serialize(false) +
					" as the target agent is dead");
			}
			return null;
		}
		return scope.evaluate(s, a);
	}

	@operator(value = "copy",
		type = ITypeProvider.FIRST_TYPE,
		content_type = ITypeProvider.FIRST_CONTENT_TYPE,
		category = { IOperatorCategory.SYSTEM })
	@doc(value = "returns a copy of the operand.")
	public static Object opCopy(final IScope scope, final Object o) throws GamaRuntimeException {
		if ( o instanceof IValue ) { return ((IValue) o).copy(scope); }
		return o;
	}

	@operator(value = "user_input", category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL })
	@doc(value = "asks the user for some values (not defined as parameters). Takes a string (optionnal) and a map as arguments. The string is used to specify the message of the dialog box. The map is to specify the parameters you want the user to change before the simulation starts, with the name of the parameter in string key, and the default value as value.",
	masterDoc = true,
	comment = "This operator takes a map [string::value] as argument, displays a dialog asking the user for these values, and returns the same map with the modified values (if any). "
		+ "The dialog is modal and will interrupt the execution of the simulation until the user has either dismissed or accepted it. It can be used, for instance, in an init section to force the user to input new values instead of relying on the initial values of parameters :",
		examples = {
		@example("map<string,unknown> values <- user_input([\"Number\" :: 100, \"Location\" :: {10, 10}]);"),
		@example(value = "assert (values at \"Number\") equals: 100;", isTestOnly = true),
		@example(value = "assert (values at \"Location\") equals: {10,10};", isTestOnly = true),
		@example(value = "create bug number: int(values at \"Number\") with: [location:: (point(values at \"Location\"))];",
		isExecutable = false) })
	public static
	GamaMap<String, Object> userInput(final IScope scope, final IExpression map) {
		final IAgent agent = scope.getAgentScope();
		return userInput(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", map);
	}

	@operator(value = "user_input", category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL })
	@doc(value = "asks the user for some values (not defined as parameters). Takes a string (optionnal) and a map as arguments. The string is used to specify the message of the dialog box. The map is to specify the parameters you want the user to change before the simulation starts, with the name of the parameter in string key, and the default value as value.",
	examples = {
		@example("map<string,unknown> values2 <- user_input(\"Enter numer of agents and locations\",[\"Number\" :: 100, \"Location\" :: {10, 10}]);"),
		@example(value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
		isExecutable = false) })
	public static
	GamaMap<String, Object> userInput(final IScope scope, final String title, final IExpression expr) {
		Map<String, Object> initialValues = new TOrderedHashMap();
		final Map<String, IType> initialTypes = new TOrderedHashMap();
		if ( expr instanceof MapExpression ) {
			final MapExpression map = (MapExpression) expr;
			for ( final Map.Entry<IExpression, IExpression> entry : map.getElements().entrySet() ) {
				final String key = Cast.asString(scope, entry.getKey().value(scope));
				final IExpression val = entry.getValue();
				initialValues.put(key, val.value(scope));
				initialTypes.put(key, val.getType());
			}
		} else {
			initialValues = Cast.asMap(scope, expr.value(scope), false);
			for ( final Map.Entry<String, Object> entry : initialValues.entrySet() ) {
				initialTypes.put(entry.getKey(), GamaType.of(entry.getValue()));
			}
		}
		if ( initialValues.isEmpty() ) { return GamaMapFactory.create(Types.STRING, Types.NO_TYPE); }
		return GamaMapFactory.create(scope, Types.STRING, Types.NO_TYPE,
			GuiUtils.openUserInputDialog(title, initialValues, initialTypes));
	}

	@operator(value = "eval_gaml", can_be_const = false, category = { IOperatorCategory.SYSTEM })
	@doc(value = "evaluates the given GAML string.",
	examples = { @example(value = "eval_gaml(\"2+3\")", equals = "5") })
	public static Object opEvalGaml(final IScope scope, final String gaml) {
		final IAgent agent = scope.getAgentScope();
		final IDescription d = agent.getSpecies().getDescription();
		try {
			final IExpression e = GAML.getExpressionFactory().createExpr(gaml, d);
			return scope.evaluate(e, agent);
		} catch (final GamaRuntimeException e) {
			GuiUtils.informConsole("Error in evaluating Gaml code : '" + gaml + "' in " + scope.getAgentScope() +
				java.lang.System.getProperty("line.separator") + "Reason: " + e.getMessage());

			return null;
		}

	}

	// @operator(value = "eval_java", can_be_const = false)
	// @doc(value = "evaluates the given java code string.", deprecated = "Does not work", see = { "eval_gaml",
	// "evaluate_with" })
	// public static Object opEvalJava(final IScope scope, final String code) {
	// try {
	// final ScriptEvaluator se = new ScriptEvaluator();
	// se.setReturnType(Object.class);
	// se.cook(code);
	// // Evaluate script with actual parameter values.
	// return se.evaluate(new Object[0]);
	//
	// // Version sans arguments pour l'instant.
	// } catch (final Exception e) {
	// GuiUtils.informConsole("Error in evaluating Java code : '" + code + "' in " + scope.getAgentScope() +
	// java.lang.System.getProperty("line.separator") + "Reason: " + e.getMessage());
	// return null;
	// }
	// }

	// private static final String[] gamaDefaultImports = new String[] {};

}
