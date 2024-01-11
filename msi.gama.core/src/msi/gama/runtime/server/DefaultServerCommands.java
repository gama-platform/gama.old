/*******************************************************************************************************
 *
 * DefaultServerCommands.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import static msi.gama.runtime.server.ISocketCommand.ESCAPED;
import static msi.gama.runtime.server.ISocketCommand.EVALUATE;
import static msi.gama.runtime.server.ISocketCommand.EXPR;
import static msi.gama.runtime.server.ISocketCommand.NB_STEP;
import static msi.gama.runtime.server.ISocketCommand.PARAMETERS;
import static msi.gama.runtime.server.ISocketCommand.SYNC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.AgentReference;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.server.ISocketCommand.CommandException;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.GamlIdiomsProvider;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class DefaultServerCommands.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class DefaultServerCommands {

	/**
	 * Load.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage LOAD(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		final Object modelPath = map.get("model");
		final Object experiment = map.get("experiment");
		if (modelPath == null || experiment == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'load', mandatory parameters are: 'model' and 'experiment'", map, false);
		String pathToModel = modelPath.toString().trim();
		String nameOfExperiment = experiment.toString().trim();
		File ff = new File(pathToModel);
		if (!ff.exists()) return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"'" + ff.getAbsolutePath() + "' does not exist", map, false);
		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString()))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + ff.getAbsolutePath() + "' is not a gaml file", map, false);
		IModel model = null;
		try {
			List<GamlCompilationError> errors = new ArrayList<>();
			model = GAML.getModelBuilder().compile(ff, errors, null);
		} catch (IllegalArgumentException | IOException e) {
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"Impossible to compile '" + ff.getAbsolutePath() + "' because of " + e.getMessage(), map, false);
		}
		if (!model.getDescription().hasExperiment(nameOfExperiment))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + nameOfExperiment + "' is not an experiment present in '" + ff.getAbsolutePath() + "'", map,
					false);
		final IModel mm = model;
		GAMA.getGui().run("openExp", () -> GAMA.runGuiExperiment(nameOfExperiment, mm), false);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, nameOfExperiment, map, false);
	}

	/**
	 * Play.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage PLAY(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		GAMA.startFrontmostExperiment(true);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}

	/**
	 * Pause.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage PAUSE(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		plan.getController().processPause(true);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}

	/**
	 * Step.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage STEP(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		final int nb_step = map.get(NB_STEP) != null ? Integer.parseInt("" + map.get(NB_STEP)) : 1;
		final boolean sync = map.get(SYNC) != null ? Boolean.parseBoolean("" + map.get(SYNC)) : false;
		for (int i = 0; i < nb_step; i++) {
			try {
				plan.getController().processStep(sync);
			} catch (RuntimeException e) {
				DEBUG.OUT(e.getStackTrace());
				return new CommandResponse(GamaServerMessage.Type.GamaServerError, e, map, false);
			}
		}
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}

	/**
	 * Back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage BACK(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		final int nb_step = map.get(NB_STEP) != null ? Integer.parseInt(map.get(NB_STEP).toString()) : 1;
		final boolean sync = map.get(SYNC) != null ? Boolean.parseBoolean("" + map.get(SYNC)) : false;
		for (int i = 0; i < nb_step; i++) {
			try {
				plan.getController().processBack(sync);
			} catch (RuntimeException e) {
				DEBUG.OUT(e.getStackTrace());
				return new CommandResponse(GamaServerMessage.Type.GamaServerError, e, map, false);
			}
		}
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}

	/**
	 * Stop.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage STOP(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		GAMA.closeAllExperiments(true, false);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}

	/**
	 * Reload.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage RELOAD(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		IList params = (IList) map.get(PARAMETERS);
		// checking the parameters' format
		var parametersError = CommandExecutor.checkLoadParameters(params, map);
		if (parametersError != null) return parametersError;
		plan.setParameterValues(params);
		plan.setStopCondition((String) map.get(ISocketCommand.UNTIL));
		// actual reload
		plan.getController().processReload(true);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}

	/**
	 * Eval.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage EVAL(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		final Object expr = map.get(EXPR);
		if (expr == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For " + EVALUATE + ", mandatory parameter is: " + EXPR, map, false);
		String entered = expr.toString().trim();
		String res = null;
		ITopLevelAgent agent = plan.getAgent();
		if (agent == null) { agent = GAMA.getPlatformAgent(); }
		final IScope scope = agent.getScope().copy("in web socket");
		if (entered.startsWith("?")) {
			res = GamlIdiomsProvider.getDocumentationOn(entered.substring(1));
		} else {
			try {
				final var expression = GAML.compileExpression(entered, agent, false);
				if (expression != null) { res = "" + scope.evaluate(expression, agent).getValue(); }
			} catch (final Exception e) {
				// error = true;
				res = "> Error: " + e.getMessage();
			} finally {
				agent.getSpecies().removeTemporaryAction();
			}
		}
		if (res == null || res.length() == 0 || res.startsWith("> Error: "))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, res, map, false);
		final boolean escaped = map.get(ESCAPED) == null ? false : Boolean.parseBoolean("" + map.get(ESCAPED));
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, res, map, escaped);
	}

	/**
	 * Ask.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the gama server message
	 * @date 26 nov. 2023
	 */
	public static GamaServerMessage ASK(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		final String action = map.get(IKeyword.ACTION) != null ? map.get(IKeyword.ACTION).toString().trim() : null;
		if (action == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For " + ISocketCommand.ASK + ", mandatory parameter is: 'action'", map, false);
		final String ref = map.get(IKeyword.AGENT) != null ? map.get(IKeyword.AGENT).toString().trim() : null;
		final ExperimentAgent exp = plan.getAgent();
		IScope scope = exp.getScope();
		final IAgent agent = ref == null ? exp : AgentReference.of(ref).getReferencedAgent(scope);
		if (agent == null) return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"Agent does not exist: " + ref, map, false);
		final IExecutable exec = agent.getSpecies().getAction(action);
		if (exec == null) return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"Action " + action + " does not exist in agent " + ref, map, false);
		// TODO Verify that it is not a JSON string...Otherwise, use Json.getNew().parse(...)
		final IMap<String, Object> args = Cast.asMap(scope, map.get("args"), false);
		ExecutionResult er = ExecutionResult.PASSED;
		IScope newScope = agent.getScope().copy("Ask command of gama-server");
		try {
			er = newScope.execute(exec, agent, new Arguments(args));
		} catch (GamaRuntimeException e) {
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, e.getMessage(), map, false);
		} finally {
			GAMA.releaseScope(newScope);
		}
		if (!er.passed()) return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"Error in the execution of " + action, map, false);
		final boolean escaped = map.get(ISocketCommand.ESCAPED) == null ? false
				: Boolean.parseBoolean("" + map.get(ISocketCommand.ESCAPED));
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, escaped);
	}

	/**
	 * n Download.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static CommandResponse DOWNLOAD(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		final String filepath = map.containsKey(IKeyword.FILE) ? map.get(IKeyword.FILE).toString() : null;
		if (filepath == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'download', mandatory parameter is: 'file'", map, false);
		try (FileInputStream fis = new FileInputStream(new File(filepath));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr)) {
			StringBuilder sc = new StringBuilder();
			String line;
			// read all the lines
			while ((line = br.readLine()) != null) { sc.append(line).append("\n"); }
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, sc.toString(), map, false);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, "Unable to download file", map,
					false);
		}
	}

	/**
	 * Upload.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage UPLOAD(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		final String filepath = map.containsKey("file") ? map.get("file").toString() : null;
		final String content = map.containsKey("content") ? map.get("content").toString() : null;
		if (filepath == null || content == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'upload', mandatory parameters are: 'file' and 'content'", map, false);
		try (FileWriter myWriter = new FileWriter(filepath)) {
			myWriter.write(content);
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, ex.getMessage(), map, false);
		}
	}

	/**
	 * Exit.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the gama server message
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage EXIT(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		try {
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
		} finally {
			System.exit(0);
		}
	}

}
