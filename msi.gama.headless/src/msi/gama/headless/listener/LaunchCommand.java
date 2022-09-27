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
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import ummisco.gama.dev.utils.DEBUG;

public class LaunchCommand extends SocketCommand {
	@Override
	public void execute(final WebSocket socket, IMap<String, Object> map) {

		String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		String socket_id = map.get("socket_id").toString();
		final String cmd_type = map.get("type").toString();
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("launch");
		DEBUG.OUT(map.get("model"));
		DEBUG.OUT(map.get("experiment"));
		try {
			launchGamlSimulation(gamaWebSocketServer, socket,
					Arrays.asList("-gaml", ".", map.get("experiment").toString(), map.get("model").toString()),
					(GamaJsonList) map.get("parameters"), map.get("until") != null ? map.get("until").toString() : "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void launchGamlSimulation(final GamaWebSocketServer gamaWebSocketServer, final WebSocket socket,
			final List<String> args, final GamaJsonList params, final String end)
			throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		File ff = new File(pathToModel);
		// DEBUG.OUT(ff.getAbsoluteFile().toString());
		if (!ff.exists()) {
			DEBUG.OUT(ff.getAbsolutePath() + " does not exist");
			socket.send("gaml file does not exist");
			return;
		}
		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString())) {
			// System.exit(-1);
			DEBUG.OUT(ff.getAbsolutePath() + " is not a gaml file");
			socket.send(pathToModel + "is not a gaml file");
			return;
		}
		final String argExperimentName = args.get(args.size() - 2);
		// final String argGamlFile = args.get(args.size() - 1);

		// final List<IExperimentJob> jb =
		// ExperimentationPlanFactory.buildExperiment(ff.getAbsoluteFile().toString());
		ManualExperimentJob selectedJob = null;
		// for (final IExperimentJob j : jb) {
		// if (j.getExperimentName().equals(argExperimentName)) {
		selectedJob = new ManualExperimentJob(ff.getAbsoluteFile().toString(), argExperimentName, gamaWebSocketServer,
				socket, params);
		// break;
		// }
		// }
		// if (selectedJob == null)
		// return;

		Globals.OUTPUT_PATH = args.get(args.size() - 3);
		selectedJob.endCond = end;
		selectedJob.controller.directOpenExperiment();
		if (gamaWebSocketServer.get_listener().getExperimentsOf("" + socket.hashCode()) == null) {
			final ConcurrentHashMap<String, ManualExperimentJob> exps = new ConcurrentHashMap<>();
			gamaWebSocketServer.get_listener().getAllExperiments().put("" + socket.hashCode(), exps);

		}
		gamaWebSocketServer.get_listener().getExperimentsOf("" + socket.hashCode()).put(selectedJob.getExperimentID(), selectedJob);

		// final int size = selectedJob.getListenedVariables().length;
		// String lst_out = "";
		// if (size != 0) {
		// for (int i = 0; i < size; i++) {
		// final ListenedVariable v = selectedJob.getListenedVariables()[i];
		// lst_out += "@" + v.getName();
		// }
		// }
		// IAgent agt = selectedJob.getSimulation().getSimulation();

		// IShape geom = agt.getGeometry();
		// Spatial.Projections.transform_CRS(agt.getScope(), agt.getGeometry(),
		// "EPSG:4326");
		String res = "{" + " \"type\": \"exp\"," + " \"socket_id\": \"" + socket.hashCode() + "\"," + " \"exp_id\": \""
				+ selectedJob.getExperimentID() + "\""
				// + " \"number_displays\": \""+ size +"\","
				// + " \"lat\": \""+ geom.getLocation().x +"\","
				// + " \"lon\": \""+ geom.getLocation().y +"\""
				+ "	}";
		// DEBUG.OUT("exp@" + "" + socket.hashCode() + "@" +
		// selectedJob.getExperimentID() + "@" + size + "@"
		// + geom.getLocation().x + "@" + geom.getLocation().y);
		// socket.send("exp@" + "" + socket.hashCode() + "@" +
		// selectedJob.getExperimentID() + "@" + size + "@"
		// + geom.getLocation().x + "@" + geom.getLocation().y);
		gamaWebSocketServer.getDefaultApp().processorQueue.execute(selectedJob.controller.executionThread);
		socket.send(res);
	}
}
