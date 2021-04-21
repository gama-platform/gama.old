/*******************************************************************************************************
 *
 * msi.gaml.operators.System.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 *
 * @todo Description
 *
 */
public class System {

	@operator (
			value = "dead",
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.SPECIES })
	@doc (
			value = "true if the agent is dead (or null), false otherwise.",
			examples = @example (
					value = "dead(agent_A)",
					equals = "true or false",
					isExecutable = false))
	@test ("dead(simulation) = false")
	public static Boolean opDead(final IScope scope, final IAgent a) {
		return a == null || a.dead();
	}

	@operator (
			value = "is_error",
			can_be_const = true,
			concept = IConcept.TEST)
	@doc ("Returns whether or not the argument raises an error when evaluated")
	@test ("is_error(1.0 = 1) = false")
	public static Boolean is_error(final IScope scope, final IExpression expr) {
		try {
			expr.value(scope);
		} catch (final GamaRuntimeException e) {
			return !e.isWarning();
		} catch (final Exception e1) {}
		return false;
	}

	@operator (
			value = "is_warning",
			can_be_const = true,
			concept = IConcept.TEST)
	@doc ("Returns whether or not the argument raises a warning when evaluated")
	@test ("is_warning(1.0 = 1) = false")
	public static Boolean is_warning(final IScope scope, final IExpression expr) {
		try {
			expr.value(scope);
		} catch (final GamaRuntimeException e) {
			return e.isWarning();
		} catch (final Exception e1) {}
		return false;
	}

	@operator (
			value = "command",
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.COMMUNICATION })
	@doc (
			value = "command allows GAMA to issue a system command using the system terminal or shell and to receive a string containing the outcome of the command or script executed. By default, commands are blocking the agent calling them, unless the sequence ' &' is used at the end. In this case, the result of the operator is an empty string. The basic form with only one string in argument uses the directory of the model and does not set any environment variables. Two other forms (with a directory and a map<string, string> of environment variables) are available.",
			masterDoc = true)
	@no_test
	public static String console(final IScope scope, final String s) {
		return console(scope, s, scope.getSimulation().getExperiment().getWorkingPath());
	}

	@operator (
			value = "command",
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.COMMUNICATION })
	@doc ("command allows GAMA to issue a system command using the system terminal or shell and to receive a string containing the outcome of the command or script executed. By default, commands are blocking the agent calling them, unless the sequence ' &' is used at the end. In this case, the result of the operator is an empty string. The basic form with only one string in argument uses the directory of the model and does not set any environment variables. Two other forms (with a directory and a map<string, string> of environment variables) are available.")
	@no_test
	public static String console(final IScope scope, final String s, final String directory) {
		return console(scope, s, directory, GamaMapFactory.create());
	}

	@operator (
			value = "command",
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.COMMUNICATION })
	@doc ("command allows GAMA to issue a system command using the system terminal or shell and to receive a string containing the outcome of the command or script executed. By default, commands are blocking the agent calling them, unless the sequence ' &' is used at the end. In this case, the result of the operator is an empty string")
	@no_test
	public static String console(final IScope scope, final String s, final String directory,
			final IMap<String, String> environment) {
		if (s == null || s.isEmpty()) return "";
		final StringBuilder output = new StringBuilder();
		final List<String> commands = new ArrayList<>();
		commands.add(Platform.getOS().equals(Platform.OS_WIN32) ? "cmd.exe" : "/bin/bash");
		commands.add(Platform.getOS().equals(Platform.OS_WIN32) ? "/C" : "-c");
		commands.add(s.trim());
		// commands.addAll(Arrays.asList(s.split(" ")));
		final boolean nonBlocking = commands.get(commands.size() - 1).endsWith("&");
		if (nonBlocking) {
			// commands.(commands.size() - 1);
		}
		final ProcessBuilder b = new ProcessBuilder(commands);
		b.redirectErrorStream(true);
		b.directory(new File(directory));
		b.environment().putAll(environment);
		try {
			final Process p = b.start();
			if (nonBlocking) return "";
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			final int returnValue = p.waitFor();
			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + Strings.LN);
			}

			if (returnValue != 0)
				throw GamaRuntimeException.error("Error in console command." + output.toString(), scope);
		} catch (final IOException | InterruptedException e) {
			throw GamaRuntimeException.error("Error in console command. " + e.getMessage(), scope);
		}
		return output.toString();

	}

	@operator (
			value = { IKeyword._DOT, IKeyword.OF },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 2,
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.ATTRIBUTE })
	@doc (
			value = "It has two different uses: it can be the dot product between 2 matrices or return an evaluation of the expression (right-hand operand) in the scope the given agent.",
			masterDoc = true,
			special_cases = "if the agent is nil or dead, throws an exception",
			usages = @usage (
					value = "if the left operand is an agent, it evaluates of the expression (right-hand operand) in the scope the given agent",
					examples = { @example (
							value = "agent1.location",
							equals = "the location of the agent agent1",
							isExecutable = false),
					// @example (value = "map(nil).keys", raises = "exception", isTestOnly = false)
					}))
	@no_test
	public static Object opGetValue(final IScope scope, final IAgent a, final IExpression s)
			throws GamaRuntimeException {
		if (a == null) {
			if (!scope.interrupted()) throw GamaRuntimeException
					.warning("Cannot evaluate " + s.serialize(false) + " as the target agent is nil", scope);
			return null;
		}
		if (a.dead()) {
			// scope.getGui().debug("System.opGetValue");
			if (!scope.interrupted()) // scope.getGui().debug("System.opGetValue error");
				throw GamaRuntimeException
						.warning("Cannot evaluate " + s.serialize(false) + " as the target agent is dead", scope);
			return null;
		}
		return scope.evaluate(s, a).getValue();
	}

	@operator (
			value = "copy",
			type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM })
	@doc (
			value = "returns a copy of the operand.")
	@no_test
	public static Object opCopy(final IScope scope, final Object o) throws GamaRuntimeException {
		if (o instanceof IValue) return ((IValue) o).copy(scope);
		return o;
	}

	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = { IConcept.SYSTEM, IConcept.GUI })
	@doc (
			deprecated = "Use the `user_input_dialog` operator with a list of `enter()` or `choose()` operators rather than this deprecated form",
			value = "Asks the user for some values. Takes a string (optional) and a map as arguments. The string is used to specify the message of the dialog box. The map is to specify the parameters you want the user to change before the simulation starts, with the name of the parameter in string key, and the default value as value.",
			masterDoc = true,
			comment = "This operator takes a map [string::value] as argument, displays a dialog asking the user for these values, and returns the same map with the modified values (if any). "
					+ "The dialog is modal and will interrupt the execution of the simulation until the user has either dismissed or accepted it. It can be used, for instance, in an init section to force the user to input new values instead of relying on the initial values of parameters :",
			examples = {
					@example ("map<string,unknown> values <- user_input([enter(\"Number\",100), enter(\"Location\",{10, 10})], font('Helvetica', 18));"),
					@example (
							value = "(values at \"Number\") as int",
							equals = "100",
							returnType = "int",
							isTestOnly = true),
					@example (
							value = " (values at \"Location\") as point",
							equals = "{10,10}",
							returnType = "point",
							isTestOnly = true),
					@example (
							value = "create bug number: int(values at \"Number\") with: [location:: (point(values at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInput(final IScope scope, final IMap<String, Object> map,
			final GamaFont font) {
		final IAgent agent = scope.getAgent();
		return userInput(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", map, font);
	}

	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use the `user_input_dialog` operator with a list of `enter()` or `choose()` operators rather than this deprecated form",
			value = "Asks the user for some values. Takes a string (optional) and a map as arguments. The string is used to specify the message of the dialog box. The map is to specify the parameters you want the user to change before the simulation starts, with the name of the parameter in string key, and the default value as value.",
			examples =

			{ @example ("map<string,unknown> values2 <- user_input(\"Enter numer of agents and locations\",[enter(\"Number\",100), enter(\"Location\",{10, 10})], font('Helvetica', 18));"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInput(final IScope scope, final String title, final IMap<String, Object> map,
			final GamaFont font) {
		final IList<IParameter> parameters = GamaListFactory.create();
		map.forEach((k, v) -> {
			parameters.add(enterValue(scope, k, v));
		});
		return userInputDialog(scope, title, parameters, font);
	}

	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use `user_input_dialog` instead",
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is to specify the parameters the user can enter",
			examples = {
					@example ("map<string,unknown> values_no_title <- user_input([enter('Number',100), enter('Location',point, {10, 10})], font('Helvetica', 18));"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInputDeprecated(final IScope scope, final IList parameters,
			final GamaFont font) {
		final IAgent agent = scope.getAgent();
		return userInputDialog(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", parameters, font);
	}

	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = { IConcept.SYSTEM, IConcept.GUI })
	@doc (
			deprecated = "Use a list of `enter()` or `choose()` operators rather than a map",
			value = "Asks the user for some values. Takes a string (optional) and a map as arguments. The string is used to specify the message of the dialog box. The map is to specify the parameters you want the user to change before the simulation starts, with the name of the parameter in string key, and the default value as value.",
			masterDoc = true,
			comment = "This operator takes a map [string::value] as argument, displays a dialog asking the user for these values, and returns the same map with the modified values (if any). "
					+ "The dialog is modal and will interrupt the execution of the simulation until the user has either dismissed or accepted it. It can be used, for instance, in an init section to force the user to input new values instead of relying on the initial values of parameters :",
			examples = {
					@example ("map<string,unknown> values <- user_input([enter(\"Number\",100), enter(\"Location\",{10, 10})]);"),
					@example (
							value = "(values at \"Number\") as int",
							equals = "100",
							returnType = "int",
							isTestOnly = true),
					@example (
							value = " (values at \"Location\") as point",
							equals = "{10,10}",
							returnType = "point",
							isTestOnly = true),
					@example (
							value = "create bug number: int(values at \"Number\") with: [location:: (point(values at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInput(final IScope scope, final IMap<String, Object> map) {
		final IAgent agent = scope.getAgent();
		return userInput(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", map);
	}

	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use a list of `enter()` or `choose()` operators rather than a map",
			value = "Asks the user for some values. Takes a string (optional) and a map as arguments. The string is used to specify the message of the dialog box. The map is to specify the parameters you want the user to change before the simulation starts, with the name of the parameter in string key, and the default value as value.",
			examples =

			{ @example ("map<string,unknown> values2 <- user_input(\"Enter numer of agents and locations\",[enter(\"Number\",100), enter(\"Location\",{10, 10})]);"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInput(final IScope scope, final String title,
			final IMap<String, Object> map) {
		final IList<IParameter> parameters = GamaListFactory.create();
		map.forEach((k, v) -> {
			parameters.add(enterValue(scope, k, v));
		});
		return userInputDialog(scope, title, parameters);
	}

	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use `user_input_dialog` instead",
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is to specify the parameters the user can enter",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})]);"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInputDeprecated(final IScope scope, final String title,
			final IList parameters) {
		return userInputDialog(scope, title, parameters);
	}

	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use `user_input_dialog` instead",
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is to specify the parameters the user can enter",
			examples = {
					@example ("map<string,unknown> values_no_title <- user_input([enter('Number',100), enter('Location',point, {10, 10})]);"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInputDeprecated(final IScope scope, final IList parameters) {
		return userInputDialog(scope, parameters);
	}

	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use `user_input_dialog` instead",
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is used to specify the parameters the user can enter. Finally, the font of the title can be specified",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})], font('Helvetica', 18));"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInputDeprecated(final IScope scope, final String title,
			final IList parameters, final GamaFont font) {
		return userInputDialog(scope, title, parameters, font);
	}

	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT_DIALOG,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is to specify the parameters the user can enter",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input_dialog('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})]);"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	public static IMap<String, Object> userInputDialog(final IScope scope, final String title, final IList parameters) {
		parameters.removeIf(p -> !(p instanceof IParameter));
		return userInputDialog(scope, title, parameters, null);
	}

	@operator (
			value = IKeyword.USER_INPUT_DIALOG,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is to specify the parameters the user can enter",
			examples = {
					@example ("map<string,unknown> values_no_title <- user_input_dialog([enter('Number',100), enter('Location',point, {10, 10})]);"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	public static IMap<String, Object> userInputDialog(final IScope scope, final IList parameters) {
		final IAgent agent = scope.getAgent();
		return userInputDialog(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", parameters);
	}

	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT_DIALOG,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is used to specify the parameters the user can enter. Finally, the font of the title can be specified",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input_dialog('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})], font('Helvetica', 18));"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	public static IMap<String, Object> userInputDialog(final IScope scope, final String title, final IList parameters,
			final GamaFont font) {
		parameters.removeIf(p -> !(p instanceof IParameter));
		return GamaMapFactory.createWithoutCasting(Types.STRING, Types.NO_TYPE,
				scope.getGui().openUserInputDialog(scope, title, parameters, font));
	}

	@operator (
			value = IKeyword.WIZARD,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = { IConcept.SYSTEM, IConcept.GUI })
	@doc (
			value = "Build a wizard and return the values enter by the user as a map of map [\"title page 1\"::[\"var1\"::1,\"var2\"::2]]. Takes a string, an action and a list of calls to the `wizard_page()` operator. The first string is used to specify the title. The action to describe when the wizard is supposed to be finished. A classic way of defining the action is "
					+ "bool eval_finish(map<string,map> input_map) {return input_map[\"page1\"][\"file\"] != nil;}. The list is to specify the wizard pages.",
			examples = { @example (
					value = "map results <-  wizard(\"My wizard\",eval_finish, [wizard_page(\"page1\",\"enter info\" ,[enter(\"var1\",string)], font(\"Arial\", 10))]);",
					isExecutable = false) })
	@no_test

	public static IMap<String, IMap<String, Object>> openWizard(final IScope scope, final String title,
			final ActionDescription finish, final IList<IMap<String, Object>> pages) {
		return scope.getGui().openWizard(scope, title, finish, pages);
	}

	@operator (
			value = IKeyword.WIZARD,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = { IConcept.SYSTEM, IConcept.GUI })
	@doc (
			value = "Build a wizard and return the values enter by the user as a map of map [\"title page 1\"::[\"var1\"::1,\"var2\"::2]]. Takes a string, a list of calls to the `wizard_page()` operator. The first string is used to specify the title. The list is to specify the wizard pages.",
			examples = { @example (
					value = "map results <-  wizard(\"My wizard\",[wizard_page(\"page1\",\"enter info\" ,[enter(\"var1\",string)], font(\"Arial\", 10))]);",
					isExecutable = false) })
	@no_test
	public static IMap<String, IMap<String, Object>> openWizard(final IScope scope, final String title,
			final IList<IMap<String, Object>> pages) {
		return scope.getGui().openWizard(scope, title, null, pages);
	}

	@operator (
			value = IKeyword.WIZARD_PAGE,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = { IConcept.SYSTEM, IConcept.GUI })
	@doc (
			value = "Build a wizard page. Takes two strings, a list of calls to the `enter()` or `choose()` operators and a font as arguments. The first string is used to specify the title, the second the description of the dialog box. The list is to specify the parameters the user can enter. The font is used to specify the font",
			examples = { @example (
					value = "map results <-  wizard(\"My wizard\",[wizard_page(\"page1\",\"enter info\" ,[enter(\"var1\",string)], font(\"Arial\", 10))]);",
					isExecutable = false) })
	@no_test
	public static IMap<String, Object> wizardPage(final String title, final String description, final IList parameters,
			final GamaFont font) {
		IMap<String, Object> results = GamaMapFactory.create();
		results.put(IKeyword.TITLE, title);
		results.put(IKeyword.DESCRIPTION, description);
		results.put(IKeyword.PARAMETERS, parameters);
		results.put(IKeyword.FONT, font);
		return results;
	}

	@operator (
			value = IKeyword.WIZARD_PAGE,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = { IConcept.SYSTEM, IConcept.GUI })
	@doc (
			value = "Build a wizard page. Takes two strings and a list of calls to the `enter()` or `choose()` operators. The first string is used to specify the title, the second the description of the dialog box. The list is to specify the parameters the user can enter",
			examples = { @example (
					value = "map results <-  wizard(\"My wizard\",[wizard_page(\"page1\",\"enter info\" ,[enter(\"var1\",string)])]);",
					isExecutable = false) })
	@no_test
	public static IMap<String, Object> wizardPage(final String title, final String description,
			final IList parameters) {
		IMap<String, Object> results = GamaMapFactory.create();
		results.put(IKeyword.TITLE, title);
		results.put(IKeyword.DESCRIPTION, description);
		results.put(IKeyword.PARAMETERS, parameters);
		return results;
	}

	@operator (
			value = IKeyword.USER_CONFIRM,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			value = "Asks the user to confirm a choice. The two string are used to specify the title and the message of the dialog box. ",
			examples =

			{ @example ("bool confirm <- user_confirm(\"Confirm\",\"Please confirm\";") })
	@no_test
	public static Boolean userConfirmDialog(final IScope scope, final String title, final String message) {
		return scope.getGui().openUserInputDialogConfirm(scope, title, message);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a value by specifying a title and a type")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final IType type) {
		return enterValue(scope, title, type, type.getDefault());
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title and an initial value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Integer init) {
		return enterValue(scope, title, Types.INT, init);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title, an initial value, a min and a max value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Integer init, final Integer min,
			final Integer max) {
		return new InputParameter(title, init, min, max);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title, an initial value, a min, a max and a step value",
			usages = { @usage (
					value = "The GUI is then a slider when an init value, a min (int or float), a max (int or float) (and eventually a  step (int or float) ) operands.",
					examples = { @example (
							value = "map resMinMax <- user_input([enter(\"Title\",5,0)])",
							test = false),
							@example (
									value = "map resMinMax <- user_input([enter(\"Title\",5,0,10)])",
									test = false),
							@example (
									value = "map resMMStepFF <- user_input([enter(\"Title\",5,0.1,10.1,0.5)]);",
									test = false) }) })
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Integer init, final Integer min,
			final Integer max, final Integer step) {
		return new InputParameter(title, init, min, max, step);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title, an initial value, a min and a max value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Double init, final Double min,
			final Double max) {
		return new InputParameter(title, init, min, max);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a float by specifying a title, an initial value, a min, a max and a step value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Double init, final Double min,
			final Double max, final Double step) {
		return new InputParameter(title, init, min, max, step);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a float by specifying a title and an initial value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Double init) {
		return enterValue(scope, title, Types.FLOAT, init);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a boolean value by specifying a title and an initial value",
			usages = { @usage (
					value = "When the second operand is the boolean type or a boolean value, the GUI is then a switch",
					examples = { @example (
							value = "map<string,unknown> m <- user_input(enter(\"Title\",true));",
							test = false),
							@example (
									value = "map<string,unknown> m2 <- user_input(enter(\"Title\",bool));",
									test = false) }) })
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Boolean init) {
		return enterValue(scope, title, Types.BOOL, init);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a string by specifying a title and an initial value",
			masterDoc = true)
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final String init) {
		return enterValue(scope, title, Types.STRING, init);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a value by specifying a title, a type, and an initial value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final IType type, final Object init) {
		return new InputParameter(title, init, type);
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a value by specifying a title and an initial value. The type will be deduced from the value")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Object init) {
		return new InputParameter(title, init, GamaType.of(init));
	}

	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.CHOOSE)
	@doc (
			value = "Allows the user to choose a value by specifying a title, a type, and a list of possible values")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final IType type, final Object init,
			final IList among) {
		return new InputParameter(title, init, type, among);
	}

	@operator (
			value = "eval_gaml",
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM })
	@doc (
			value = "evaluates the given GAML string.",
			examples = { @example (
					value = "eval_gaml(\"2+3\")",
					equals = "5") })
	public static Object opEvalGaml(final IScope scope, final String gaml) {
		final IAgent agent = scope.getAgent();
		final IDescription d = agent.getSpecies().getDescription();
		try {
			final IExpression e = GAML.getExpressionFactory().createExpr(gaml, d);
			return scope.evaluate(e, agent).getValue();
		} catch (final GamaRuntimeException e) {
			scope.getGui().getConsole().informConsole(
					"Error in evaluating Gaml code : '" + gaml + "' in " + scope.getAgent()
							+ java.lang.System.getProperty("line.separator") + "Reason: " + e.getMessage(),
					scope.getRoot());

			return null;
		}

	}

	@operator (
			value = "copy_to_clipboard",
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM })
	@doc (
			examples = @example ("bool copied  <- copy_to_clipboard('text to copy');"),
			value = "Tries to copy the text in parameter to the clipboard and returns whether it has been correctly copied or not (for instance it might be impossible in a headless environment)")
	@no_test ()
	public static Boolean copyToClipboard(final IScope scope, final String text) {
		return scope.getGui().copyToClipboard(text);
	}
	// @operator(value = "eval_java", can_be_const = false)
	// @doc(value = "evaluates the given java code string.", deprecated = "Does
	// not work", see = { "eval_gaml",
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
	// scope.getGui().informConsole("Error in evaluating Java code : '" + code +
	// "' in " + scope.getAgentScope() +
	// java.lang.System.getProperty("line.separator") + "Reason: " +
	// e.getMessage());
	// return null;
	// }
	// }

	// private static final String[] gamaDefaultImports = new String[] {};

}
