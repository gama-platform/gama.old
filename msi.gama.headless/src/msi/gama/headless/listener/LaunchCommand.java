package msi.gama.headless.listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import ummisco.gama.dev.utils.DEBUG;

public class LaunchCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		final Object model 		= map.get("model");
		final Object experiment	= map.get("experiment");
		DEBUG.OUT("launch");
		DEBUG.OUT(model);
		DEBUG.OUT(experiment);

		if (model == null || experiment == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'launch', mandatory parameters are: 'model' and 'experiment'", null, false);
		}
		try {
			return launchGamlSimulation(gamaWebSocketServer, socket,
					Arrays.asList("-gaml", ".", experiment.toString(), model.toString()),
					(GamaJsonList) map.get("parameters"), map.get("until") != null ? map.get("until").toString() : "",
					map);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommandResponse(GamaServerMessageType.RuntimeError, e, map, false);
		}
	}

	// TODO: would be cleaner without the socket
	public CommandResponse launchGamlSimulation(final GamaWebSocketServer gamaWebSocketServer, final WebSocket socket,
			final List<String> args, final GamaJsonList params, final String end, IMap<String, Object> map)
			throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		File ff = new File(pathToModel);
		// DEBUG.OUT(ff.getAbsoluteFile().toString());
		if (!ff.exists()) {
			DEBUG.OUT(ff.getAbsolutePath() + " does not exist");
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					ff.getAbsolutePath() + " does not exist", map, false);
		}
		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString())) {
			// System.exit(-1);
			DEBUG.OUT(ff.getAbsolutePath() + " is not a gaml file");
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, pathToModel + "is not a gaml file",
					map, false);
		}
		final String argExperimentName = args.get(args.size() - 2);

		ManualExperimentJob selectedJob = null;
		selectedJob = new ManualExperimentJob(ff.getAbsoluteFile().toString(), argExperimentName, gamaWebSocketServer,
				socket, params);

		Globals.OUTPUT_PATH = args.get(args.size() - 3);
		selectedJob.endCond = end;
		selectedJob.controller.directOpenExperiment();
		if (gamaWebSocketServer.get_listener().getExperimentsOf("" + socket.hashCode()) == null) {
			final ConcurrentHashMap<String, ManualExperimentJob> exps = new ConcurrentHashMap<>();
			gamaWebSocketServer.get_listener().getAllExperiments().put("" + socket.hashCode(), exps);

		}
		gamaWebSocketServer.get_listener().getExperimentsOf("" + socket.hashCode()).put(selectedJob.getExperimentID(),
				selectedJob);

		IMap<String, Object> res = GamaMapFactory.create();
		res.put("type", "exp");
		res.put("socket_id", socket.hashCode());
		res.put("exp_id", selectedJob.getExperimentID());
		gamaWebSocketServer.getDefaultApp().processorQueue.execute(selectedJob.controller.executionThread);
		return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, res, map, false);
	}
}
