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
import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import ummisco.gama.dev.utils.DEBUG;

public class LoadCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		final Object model 		= map.get("model");
		final Object experiment	= map.get("experiment");
		DEBUG.OUT("launch");
		DEBUG.OUT(model);
		DEBUG.OUT(experiment);

		if (model == null || experiment == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'load', mandatory parameters are: 'model' and 'experiment'", map, false);
		}
		try {
			return launchGamlSimulation(gamaWebSocketServer, socket,
										(GamaJsonList) map.get("parameters"), 
										map.get("until") != null ? map.get("until").toString() : "",
										map);
		} catch (Exception e) {
			DEBUG.OUT(e);
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, e, map, false);
		}
	}


	public CommandResponse launchGamlSimulation(final GamaWebSocketServer gamaWebSocketServer, final WebSocket socket,
			final GamaJsonList params, final String end, IMap<String, Object> map)
			throws IOException, GamaHeadlessException {
		
		final String pathToModel = map.get("model").toString();

		File ff = new File(pathToModel);

		if (!ff.exists()) {
			DEBUG.OUT(ff.getAbsolutePath() + " does not exist");
			return new CommandResponse(	GamaServerMessageType.UnableToExecuteRequest,
										"'" + ff.getAbsolutePath() + "' does not exist", 
										map, 
										false);
		}
		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString())) {
			DEBUG.OUT(ff.getAbsolutePath() + " is not a gaml file");
			return new CommandResponse(	GamaServerMessageType.UnableToExecuteRequest, 
										"'" + ff.getAbsolutePath() + "' is not a gaml file",
										map, 
										false);
		}
		
		final String argExperimentName = map.get("experiment").toString(); 
		
		//we check if the experiment is present in the file
		if ( ! ExperimentationPlanFactory.buildExperiment(ff.getAbsolutePath().toString()).stream()
				.anyMatch((jb) -> jb.getExperimentName().equals(argExperimentName))) {
			return new CommandResponse(	GamaServerMessageType.UnableToExecuteRequest, 
										"'" + argExperimentName + "' is not an experiment present in '" + ff.getAbsolutePath() + "'",
										map, 
										false);
		}
		

		ManualExperimentJob selectedJob = null;

		var console = map.get("console") != null? (boolean) map.get("console") : true;
		var status 	= map.get("status") != null	? (boolean) map.get("status") : false;
		var dialog 	= map.get("dialog") != null	? (boolean) map.get("dialog") : false;
		
		selectedJob = new ManualExperimentJob(	ff.getAbsoluteFile().toString(), 
												argExperimentName, 
												gamaWebSocketServer,
												socket, 
												params,
												console,
												status,
												dialog);

		Globals.OUTPUT_PATH = ".";//TODO: why ?

		selectedJob.endCond = end;
		selectedJob.controller.directOpenExperiment();
		//If the client has not ran any experiment yet, we initialize its experiments map
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
