/*******************************************************************************************************
 *
 * System.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.eclipse.core.runtime.Platform;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.common.util.FileUtils;
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
import msi.gama.util.GamaColor;
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

	/**
	 * Op dead.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @return the boolean
	 */
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

	/**
	 * Checks if is error.
	 *
	 * @param scope
	 *            the scope
	 * @param expr
	 *            the expr
	 * @return the boolean
	 */
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

	/**
	 * Checks if is warning.
	 *
	 * @param scope
	 *            the scope
	 * @param expr
	 *            the expr
	 * @return the boolean
	 */
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

	/**
	 * Checks if the url is reachable.
	 *
	 * @param scope
	 *            the scope
	 * @param expr
	 *            the expr
	 * @return the boolean
	 */
	@operator (
			value = "is_reachable",
			can_be_const = true,
			concept = IConcept.TEST)
	@doc (
			value = "Returns whether or not the given web address is reachable or not before a time_out time in milliseconds",
			examples = { @example (
					value = "write sample(is_reachable(\"www.google.com\", 200));",
					isExecutable = false) })
	@no_test
	public static Boolean is_reachable(final IScope scope, final String address, final int openPort,
			final int timeout) {
		// Any Open port on other machine
		// openPort = 22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
		try (Socket soc = new Socket()) {
			soc.connect(new InetSocketAddress(address, openPort), timeout);
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * Play sound.
	 *
	 * @param scope the scope
	 * @param source the source
	 * @return the boolean
	 */
	@operator (
			value = "play_sound",
			can_be_const = true,
			concept = IConcept.SOUND)
	@doc (
			value = "Play a wave file",
			examples = { @example (
					value = "bool sound_ok <- play_sound('beep.wav');",
					isExecutable = false) })
	@no_test
	public static Boolean playSound(final IScope scope, final String source) {
		try {
			final String soundFilePath = FileUtils.constructAbsoluteFilePath(scope, source, true);
			File f = new File(soundFilePath);
			if (!f.exists()) return false;
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
			clip.open(inputStream);
			clip.start();

		} catch (Exception e) {
			throw GamaRuntimeException.error(e.toString(), scope);
		}
		return true;
	}

	/**
	 * Checks if the url is reachable.
	 *
	 * @param scope
	 *            the scope
	 * @param expr
	 *            the expr
	 * @return the boolean
	 */
	@operator (
			value = "is_reachable",
			concept = IConcept.TEST)
	public static Boolean is_reachable(final IScope scope, final String address, final int timeout) {
		return is_reachable(scope, address, 80, timeout);
	}

	/**
	 * Console.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return the string
	 */
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

	/**
	 * Console.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param directory
	 *            the directory
	 * @return the string
	 */
	@operator (
			value = "command",
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.COMMUNICATION })
	@doc ("command allows GAMA to issue a system command using the system terminal or shell and to receive a string containing the outcome of the command or script executed. By default, commands are blocking the agent calling them, unless the sequence ' &' is used at the end. In this case, the result of the operator is an empty string. The basic form with only one string in argument uses the directory of the model and does not set any environment variables. Two other forms (with a directory and a map<string, string> of environment variables) are available.")
	@no_test
	public static String console(final IScope scope, final String s, final String directory) {
		return console(scope, s, directory, GamaMapFactory.create());
	}

	/**
	 * Console.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param directory
	 *            the directory
	 * @param environment
	 *            the environment
	 * @return the string
	 */
	@operator (
			value = "command",
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.COMMUNICATION })
	@doc ("command allows GAMA to issue a system command using the system terminal or shell and to receive a string containing the outcome of the command or script executed. By default, commands are blocking the agent calling them, unless the sequence ' &' is used at the end. In this case, the result of the operator is an empty string. A map<string, string> containing environment variables values can be passed, replacing, only for this command, the values of existing variables.")
	@no_test
	public static String console(final IScope scope, final String s, final String directory,
			final IMap<String, String> environment) {
		if (s == null || s.isEmpty()) return "";
		final StringBuilder output = new StringBuilder();
		final List<String> commands = new ArrayList<>();
		commands.add(Platform.OS_WIN32.equals(Platform.getOS()) ? "cmd.exe" : "/bin/bash");
		commands.add(Platform.OS_WIN32.equals(Platform.getOS()) ? "/C" : "-c");
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
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				final int returnValue = p.waitFor();
				String line = "";
				while ((line = reader.readLine()) != null) { output.append(line + Strings.LN); }

				if (returnValue != 0)
					throw GamaRuntimeException.error("Error in console command." + output.toString(), scope);
			}
		} catch (final IOException | InterruptedException e) {
			throw GamaRuntimeException.error("Error in console command. " + e.getMessage(), scope);
		}
		return output.toString();

	}

	/**
	 * Op get value.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param s
	 *            the s
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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

	/**
	 * Op copy.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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

	/**
	 * User input.
	 *
	 * @param scope
	 *            the scope
	 * @param map
	 *            the map
	 * @param font
	 *            the font
	 * @return the i map
	 */
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

	/**
	 * User input.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param map
	 *            the map
	 * @param font
	 *            the font
	 * @return the i map
	 */
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
		map.forEach((k, v) -> { parameters.add(enterValue(scope, k, v)); });
		return userInputDialog(scope, title, parameters, font, null);
	}

	/**
	 * User input deprecated.
	 *
	 * @param scope
	 *            the scope
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
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
		return userInputDialog(scope, agent.getSpeciesName() + " #" + agent.getIndex() + " request", parameters, font,
				null);
	}

	/**
	 * User input.
	 *
	 * @param scope
	 *            the scope
	 * @param map
	 *            the map
	 * @return the i map
	 */
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

	/**
	 * User input.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param map
	 *            the map
	 * @return the i map
	 */
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
		map.forEach((k, v) -> { parameters.add(enterValue(scope, k, v)); });
		return userInputDialog(scope, title, parameters);
	}

	/**
	 * User input deprecated.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @return the i map
	 */
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

	/**
	 * User input deprecated.
	 *
	 * @param scope
	 *            the scope
	 * @param parameters
	 *            the parameters
	 * @return the i map
	 */
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

	/**
	 * User input deprecated.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
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
		return userInputDialog(scope, title, parameters, font, null);
	}

	/**
	 * User input deprecated.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			deprecated = "Use `user_input_dialog` instead",
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is used to specify the parameters the user can enter. Finally, the font and the background color of the title can be specified",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})], font('Helvetica', 18));"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	@Deprecated
	public static IMap<String, Object> userInputDeprecated(final IScope scope, final String title,
			final IList parameters, final GamaFont font, final GamaColor color) {
		return userInputDialog(scope, title, parameters, font, color);
	}

	/**
	 * User input dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @return the i map
	 */
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
		return userInputDialog(scope, title, parameters, null, null);
	}

	/**
	 * User input dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param parameters
	 *            the parameters
	 * @return the i map
	 */
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

	/**
	 * User input dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
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
				scope.getGui().openUserInputDialog(scope, title, parameters, font, null, true));
	}

	/**
	 * User input dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT_DIALOG,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is used to specify the parameters the user can enter. Finally, the font of the title can be specified as well as the background color",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input_dialog('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})], font('Helvetica', 18));"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	public static IMap<String, Object> userInputDialog(final IScope scope, final String title, final IList parameters,
			final GamaFont font, final GamaColor color) {
		return userInputDialog(scope, title, parameters, font, color, true);
	}

	/**
	 * User input dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
	@SuppressWarnings ("unchecked")
	@operator (
			value = IKeyword.USER_INPUT_DIALOG,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			concept = {})
	@doc (
			value = "Asks the user for some values and returns a map containing these values. Takes a string and a list of calls to the `enter()` or `choose()` operators as arguments. The string is used to specify the message of the dialog box. The list is used to specify the parameters the user can enter. Finally, the font of the title can be specified, as well as the background color and whether the title and close button of the dialog should be displayed or not",
			masterDoc = true,
			examples = {
					@example ("map<string,unknown> values2 <- user_input_dialog('Enter number of agents and locations',[enter('Number',100), enter('Location',point, {10, 10})], font('Helvetica', 18), #blue, true);"),
					@example (
							value = "create bug number: int(values2 at \"Number\") with: [location:: (point(values2 at \"Location\"))];",
							isExecutable = false) })
	@no_test
	public static IMap<String, Object> userInputDialog(final IScope scope, final String title, final IList parameters,
			final GamaFont font, final GamaColor color, final Boolean showTitle) {
		parameters.removeIf(p -> !(p instanceof IParameter));
		return GamaMapFactory.createWithoutCasting(Types.STRING, Types.NO_TYPE,
				scope.getGui().openUserInputDialog(scope, title, parameters, font, color, showTitle));
	}

	/**
	 * Open wizard.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param finish
	 *            the finish
	 * @param pages
	 *            the pages
	 * @return the i map
	 */
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

	/**
	 * Open wizard.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param pages
	 *            the pages
	 * @return the i map
	 */
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

	/**
	 * Wizard page.
	 *
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @return the i map
	 */
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

	/**
	 * Wizard page.
	 *
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 * @param parameters
	 *            the parameters
	 * @return the i map
	 */
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

	/**
	 * User confirm dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @return the boolean
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param type
	 *            the type
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the i parameter
	 */
	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title, an initial value, a min and a max value. "
					+ "The initial value is clamped if it is lower than min or higher than max.")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Integer init, final Integer min,
			final Integer max) {
		return new InputParameter(title, init, min, max);
	}

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @return the i parameter
	 */
	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title, an initial value, a min, a max and a step value. "
					+ "The initial value is clamped if it is lower than min or higher than max.",
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the i parameter
	 */
	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter an int by specifying a title, an initial value, a min and a max value"
					+ "The initial value is clamped if it is lower than min or higher than max.")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Double init, final Double min,
			final Double max) {
		return new InputParameter(title, init, min, max);
	}

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @return the i parameter
	 */
	@operator (
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM, IOperatorCategory.USER_CONTROL },
			value = IKeyword.ENTER)
	@doc (
			value = "Allows the user to enter a float by specifying a title, an initial value, a min, a max and a step value. "
					+ "The initial value is clamped if it is lower than min or higher than max.")
	@no_test
	public static IParameter enterValue(final IScope scope, final String title, final Double init, final Double min,
			final Double max, final Double step) {
		return new InputParameter(title, init, min, max, step);
	}

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param type
	 *            the type
	 * @param init
	 *            the init
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param init
	 *            the init
	 * @return the i parameter
	 */
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

	/**
	 * Enter value.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param type
	 *            the type
	 * @param init
	 *            the init
	 * @param among
	 *            the among
	 * @return the i parameter
	 */
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

	/**
	 * Op eval gaml.
	 *
	 * @param scope
	 *            the scope
	 * @param gaml
	 *            the gaml
	 * @return the object
	 */
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
			scope.getGui().getConsole().informConsole("Error in evaluating Gaml code : '" + gaml + "' in "
					+ scope.getAgent() + Strings.LN + "Reason: " + e.getMessage(), scope.getRoot());

			return null;
		}

	}

	/**
	 * Copy to clipboard.
	 *
	 * @param scope
	 *            the scope
	 * @param text
	 *            the text
	 * @return the boolean
	 */
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
