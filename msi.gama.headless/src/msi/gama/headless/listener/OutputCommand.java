package msi.gama.headless.listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.common.SaveHelper;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import ummisco.gama.dev.utils.DEBUG;

public class OutputCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		String socket_id = map.get("socket_id").toString();
		final String cmd_type = map.get("type").toString();
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("launch");
		DEBUG.OUT(map.get("model"));
		DEBUG.OUT(map.get("experiment"));

		if (gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id) != null
				&& gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {

			final boolean wasPaused = gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller
					.isPaused();
			gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller.directPause();
			IList<? extends IShape> agents = gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id)
					.getSimulation().getSimulation().getPopulationFor(map.get("species").toString());

			final IList ll = map.get("attributes") != null ? (IList) map.get("attributes") : GamaListFactory.EMPTY_LIST;
			final String crs = map.get("crs") != null ? map.get("crs").toString() : "";
			String res = "";
			GamaServerMessageType status = GamaServerMessageType.CommandExecutedSuccessfully;
			try {
				res = SaveHelper.buildGeoJSon(gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id)
						.getSimulation().getExperimentPlan().getAgent().getScope(), agents, ll, crs);
			} catch (Exception ex) {
				res = ex.getMessage();
				status = GamaServerMessageType.RuntimeError;
			}
			
			if (!wasPaused) {
				gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller.userStart();
			}
			return new CommandResponse(status, res, map);
		}
		else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Unable to find the experiment or simulation", map);
		}
	}
}
