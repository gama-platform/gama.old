/*******************************************************************************************************
 *
 * LoadCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.File;
import java.io.IOException;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.server.GamaServerExperimentJob;
import msi.gama.runtime.server.CommandExecutor;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LoadCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class LoadCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		final Object model = map.get("model");
		final Object experiment = map.get("experiment");
		DEBUG.OUT("launch");
		DEBUG.OUT(model);
		DEBUG.OUT(experiment);

		if (model == null || experiment == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'load', mandatory parameters are: 'model' and 'experiment'", map, false);
		try {
			return launchGamlSimulation(gamaWebSocketServer, socket, (IList) map.get("parameters"),
					map.get("until") != null ? map.get("until").toString() : "", map);
		} catch (Exception e) {
			DEBUG.OUT(e);
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, e, map, false);
		}
	}

	/**
	 * Launch gaml simulation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param gamaWebSocketServer
	 *            the gama web socket server
	 * @param socket
	 *            the socket
	 * @param params
	 *            the params
	 * @param end
	 *            the end
	 * @param map
	 *            the map
	 * @return the command response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 * @date 15 oct. 2023
	 */
	public CommandResponse launchGamlSimulation(final GamaWebSocketServer gamaWebSocketServer, final WebSocket socket,
			final IList params, final String end, final IMap<String, Object> map)
			throws IOException, GamaHeadlessException {

		final String pathToModel = map.get("model").toString();
		final String socketId = map.get("socket_id") != null ? map.get("socket_id").toString()
				: GamaWebSocketServer.getSocketId(socket);

		File ff = new File(pathToModel);

		if (!ff.exists()) {
			DEBUG.OUT(ff.getAbsolutePath() + " does not exist");
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + ff.getAbsolutePath() + "' does not exist", map, false);
		}
		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString())) {
			DEBUG.OUT(ff.getAbsolutePath() + " is not a gaml file");
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + ff.getAbsolutePath() + "' is not a gaml file", map, false);
		}

		final String argExperimentName = map.get("experiment").toString();

		GamaServerExperimentJob selectedJob = null;

		var console = map.get("console") != null ? Boolean.parseBoolean("" + map.get("console")) : true;
		var status = map.get("status") != null ? Boolean.parseBoolean("" + map.get("status")) : false;
		var dialog = map.get("dialog") != null ? Boolean.parseBoolean("" + map.get("dialog")) : false;
		var runtime = map.get("runtime") != null ? Boolean.parseBoolean("" + map.get("runtime")) : true;

		// we check that the parameters are properly formed
		var parametersError = CommandExecutor.checkLoadParameters(params, map);
		if (parametersError != null) return parametersError;

		selectedJob = new GamaServerExperimentJob(ff.getAbsoluteFile().toString(), argExperimentName,
				gamaWebSocketServer, socket, params, end, console, status, dialog, runtime);
		selectedJob.load();
		// we check if the experiment is present in the file
		if (selectedJob.simulator.getModel().getExperiment(argExperimentName) == null)
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + argExperimentName + "' is not an experiment present in '" + ff.getAbsolutePath() + "'", map,
					false);

		selectedJob.controller.processOpen(true);
		selectedJob.controller.getExperiment().setStopCondition(end);

		gamaWebSocketServer.addExperiment(socketId, selectedJob.getExperimentID(),
				selectedJob.controller.getExperiment());
		gamaWebSocketServer.execute(selectedJob.controller.executionThread);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, selectedJob.getExperimentID(),
				map, false);
	}

}
