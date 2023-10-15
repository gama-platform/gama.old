/*******************************************************************************************************
 *
 * DefaultServerCommands.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;

/**
 * The Class GuiCommands.
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
	public static GamaServerMessage LOAD(final WebSocket socket, final IMap<String, Object> map) {
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
			model = GAML.getModelBuilder().compile(ff, null, null);
		} catch (IllegalArgumentException | IOException e) {
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"Impossible to compile '" + ff.getAbsolutePath() + "'", map, false);
		}
		if (!model.getDescription().hasExperiment(nameOfExperiment))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + nameOfExperiment + "' is not an experiment present in '" + ff.getAbsolutePath() + "'", map,
					false);
		GAMA.runGuiExperiment(nameOfExperiment, model);
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
	public static GamaServerMessage PLAY(final WebSocket socket, final IMap<String, Object> map) {
		GAMA.startFrontmostExperiment();
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
	public static GamaServerMessage PAUSE(final WebSocket socket, final IMap<String, Object> map) {
		GAMA.pauseFrontmostExperiment();
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
	public static GamaServerMessage STEP(final WebSocket socket, final IMap<String, Object> map) {
		GAMA.stepFrontmostExperiment();
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
	public static GamaServerMessage BACK(final WebSocket socket, final IMap<String, Object> map) {
		GAMA.stepBackFrontmostExperiment();
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
	public static GamaServerMessage STOP(final WebSocket socket, final IMap<String, Object> map) {
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
	public static GamaServerMessage RELOAD(final WebSocket socket, final IMap<String, Object> map) {
		GAMA.reloadFrontmostExperiment();
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
	public static GamaServerMessage EVAL(final WebSocket socket, final IMap<String, Object> map) {
		final String entered = map.get("expr").toString().trim();
		final boolean escaped = map.get("escaped") == null ? false : Boolean.parseBoolean("" + map.get("escaped"));
		String res = null;
		ITopLevelAgent agent = GAMA.getExperimentAgent();
		if (agent == null) { agent = GAMA.getPlatformAgent(); }
		if (agent.dead()) return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"Experiment already closed", map, false);
		final IScope scope = agent.getScope().copy("in web socket");
		if (entered.startsWith("?")) {
			res = GamlIdiomsProvider.getDocumentationOn(entered.substring(1));
		} else {
			try {
				final var expr = GAML.compileExpression(entered, agent, false);
				if (expr != null) { res = "" + scope.evaluate(expr, agent).getValue(); }
			} catch (final Exception e) {
				// error = true;
				res = "> Error: " + e.getMessage();
			} finally {
				agent.getSpecies().removeTemporaryAction();
			}
		}
		if (res == null || res.length() == 0 || res.startsWith("> Error: "))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, res, map, false);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, res, map, escaped);
	}

	/**
	 * Download.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the command response
	 * @date 15 oct. 2023
	 */
	public static GamaServerMessage DOWNLOAD(final WebSocket socket, final IMap<String, Object> map) {
		final String filepath = map.containsKey("file") ? map.get("file").toString() : null;
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
	public static GamaServerMessage UPLOAD(final WebSocket socket, final IMap<String, Object> map) {
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
	public static GamaServerMessage EXIT(final WebSocket socket, final IMap<String, Object> map) {
		try {
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
		} finally {
			System.exit(0);
		}
	}

}
