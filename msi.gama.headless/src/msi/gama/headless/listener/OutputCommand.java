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

		final String exp_id 	= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object socket_id	= map.get("socket_id");
		final Object species	= map.get("species");
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("output");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(socket_id);
		

		if (exp_id == "" || socket_id == null || species == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'output', mandatory parameters are: 'exp_id', 'socket_id' and 'species' ", null, false);
		}

		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id.toString(), exp_id); 
		if (gama_exp != null && gama_exp.getSimulation() != null) {

			final boolean wasPaused = gama_exp.controller.isPaused();
			gama_exp.controller.directPause();
			IList<? extends IShape> agents = gama_exp.getSimulation().getSimulation().getPopulationFor(species.toString());

			final IList ll = map.get("attributes") != null ? (IList) map.get("attributes") : GamaListFactory.EMPTY_LIST;
			final String crs = map.get("crs") != null ? map.get("crs").toString() : "";
			String res = "";
			GamaServerMessageType status = GamaServerMessageType.CommandExecutedSuccessfully;
			try {
				res = SaveHelper.buildGeoJSon(gama_exp.getSimulation().getExperimentPlan().getAgent().getScope(), agents, ll, crs);
			} catch (Exception ex) {
				res = ex.getMessage();
				status = GamaServerMessageType.RuntimeError;
			}

			if (!wasPaused) {
				gama_exp.controller.userStart();
			}
			return new CommandResponse(status, res, map, true);
		} else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		}
	}
}
