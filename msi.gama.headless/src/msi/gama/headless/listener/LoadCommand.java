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
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.server.GamaServerExperimentJob;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.runtime.server.CommandExecutor;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
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
			return launchGamlSimulation(gamaWebSocketServer, socket, (GamaJsonList) map.get("parameters"),
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
			final GamaJsonList params, final String end, final IMap<String, Object> map)
			throws IOException, GamaHeadlessException {

		final String pathToModel = map.get("model").toString();
		final String socket_id =
				map.get("socket_id") != null ? map.get("socket_id").toString() : "" + socket.hashCode();

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

		// we check if the experiment is present in the file
		if (!hasExperiment(ff.getAbsolutePath().toString(), argExperimentName))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"'" + argExperimentName + "' is not an experiment present in '" + ff.getAbsolutePath() + "'", map,
					false);

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

		Globals.OUTPUT_PATH = ".";// TODO: why ?

		selectedJob.controller.directOpenExperiment();
		selectedJob.controller.getExperiment().setStopCondition(end);
		// If the client has not ran any experiment yet, we initialize its experiments map
		if (gamaWebSocketServer.getExperimentsOf(socket_id) == null) {
			final ConcurrentHashMap<String, IExperimentPlan> exps = new ConcurrentHashMap<>();
			gamaWebSocketServer.getAllExperiments().put(socket_id, exps);
		}
		gamaWebSocketServer.getExperimentsOf(socket_id).put(selectedJob.getExperimentID(),
				selectedJob.controller.getExperiment());

		gamaWebSocketServer.execute(selectedJob.controller.executionThread);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, selectedJob.getExperimentID(),
				map, false);
	}

	// /**
	// * Gets the experiments in file.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @param modelPath
	// * the model path
	// * @return the experiments in file
	// * @throws IOException
	// * Signals that an I/O exception has occurred.
	// * @throws GamaHeadlessException
	// * the gama headless exception
	// * @date 15 oct. 2023
	// */
	// List<IExperimentJob> getExperimentsInFile(final String modelPath) throws IOException, GamaHeadlessException {
	// IModel model = HeadlessSimulationLoader.loadModel(new File(modelPath), null);
	// Map<JobPlanExperimentID, IExperimentJob> originalJobs = new LinkedHashMap<>();
	// for (final ExperimentDescription expD : model.getDescription().getExperiments()) {
	// final IExperimentJob tj = ExperimentJob.loadAndBuildJob(expD, model.getFilePath(), model);
	// // TODO AD Why 12 ??
	// tj.setSeed(12);
	// originalJobs.put(new JobPlanExperimentID(tj.getModelName(), tj.getExperimentName()), tj);
	// }
	// final List<IExperimentJob> jobs = new ArrayList<>();
	// final long[] seeds = { ExperimentationPlanFactory.DEFAULT_SEED };
	// for (final IExperimentJob locJob : originalJobs.values()) {
	// final List<IExperimentJob> res = new ArrayList<>();
	// for (final long sd : seeds) {
	// final IExperimentJob job = new ExperimentJob((ExperimentJob) locJob);
	// job.setSeed(sd);
	// job.setFinalStep(ExperimentationPlanFactory.DEFAULT_FINAL_STEP);
	// res.add(job);
	// }
	// jobs.addAll(res);
	// }
	// return jobs;
	// }

	/**
	 * Checks for experiment.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param modelPath
	 *            the model path
	 * @param exp
	 *            the exp
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 * @date 15 oct. 2023
	 */
	boolean hasExperiment(final String modelPath, final String exp) throws IOException, GamaHeadlessException {
		IModel model = GamlModelBuilder.getDefaultInstance().compile(new File(modelPath), null, null);
		return model.getDescription().hasExperiment(exp);
	}

}
