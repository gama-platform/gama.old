package msi.gama.headless.listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.JobListFactory.JobPlanExperimentID;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.kernel.model.IModel;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gaml.descriptions.ExperimentDescription;
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
		
		final String pathToModel= map.get("model").toString();		
		final String socket_id	= map.get("socket_id") != null ? map.get("socket_id").toString() : ("" + socket.hashCode());


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
		var listExperimentsInFile = getExperimentsInFile(ff.getAbsolutePath().toString());
		if ( ! listExperimentsInFile.stream()
				.anyMatch((jb) -> jb.getExperimentName().equals(argExperimentName))) {
			return new CommandResponse(	GamaServerMessageType.UnableToExecuteRequest, 
										"'" + argExperimentName + "' is not an experiment present in '" + ff.getAbsolutePath() + "'",
										map, 
										false);
		}
		

		ManualExperimentJob selectedJob = null;

		var console = map.get("console")!= null ? Boolean.parseBoolean("" + map.get("console")) : true;
		var status 	= map.get("status") != null	? Boolean.parseBoolean("" + map.get("status")) : false;
		var dialog 	= map.get("dialog") != null	? Boolean.parseBoolean("" + map.get("dialog")) : false;
		var runtime	= map.get("runtime")!= null	? Boolean.parseBoolean("" + map.get("runtime")) : true;
		
		selectedJob = new ManualExperimentJob(	ff.getAbsoluteFile().toString(), 
												argExperimentName, 
												gamaWebSocketServer,
												socket, 
												params,
												console,
												status,
												dialog,
												runtime);

		Globals.OUTPUT_PATH = ".";//TODO: why ?

		selectedJob.endCond = end;
		selectedJob.controller.directOpenExperiment();
		//If the client has not ran any experiment yet, we initialize its experiments map
		if (gamaWebSocketServer.get_listener().getExperimentsOf(socket_id) == null) {
			final ConcurrentHashMap<String, ManualExperimentJob> exps = new ConcurrentHashMap<>();
			gamaWebSocketServer.get_listener().getAllExperiments().put(socket_id, exps);
		}
		gamaWebSocketServer.get_listener().getExperimentsOf(socket_id).put(selectedJob.getExperimentID(), selectedJob);

		gamaWebSocketServer.getDefaultApp().processorQueue.execute(selectedJob.controller.executionThread);
		return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, selectedJob.getExperimentID(), map, false);
	}
	
	List<IExperimentJob> getExperimentsInFile(String modelPath) throws IOException, GamaHeadlessException {
		IModel model = HeadlessSimulationLoader.loadModel(new File(modelPath), null);
		Map<JobPlanExperimentID, IExperimentJob> originalJobs = new LinkedHashMap<>();
		for (final ExperimentDescription expD : model.getDescription().getExperiments()) {
			final IExperimentJob tj = ExperimentJob.loadAndBuildJob(expD, model.getFilePath(), model);
			// TODO AD Why 12 ??
			tj.setSeed(12);
			originalJobs.put(new JobPlanExperimentID(tj.getModelName(), tj.getExperimentName()), tj);
		}
		final List<IExperimentJob> jobs = new ArrayList<>();
		final long[] seeds = { ExperimentationPlanFactory.DEFAULT_SEED };
		for (final IExperimentJob locJob : originalJobs.values()) {
			final List<IExperimentJob> res = new ArrayList<>();
			for (final long sd : seeds) {
				final IExperimentJob job = new ExperimentJob((ExperimentJob) locJob);
				job.setSeed(sd);
				job.setFinalStep(ExperimentationPlanFactory.DEFAULT_FINAL_STEP);
				res.add(job);
			}
			jobs.addAll(res);
		}
		return jobs;
	}
	
}
