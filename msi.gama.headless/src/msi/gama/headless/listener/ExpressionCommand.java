package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;
import ummisco.gama.dev.utils.DEBUG;

public class ExpressionCommand implements ISocketCommand {
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
			String res = "{\"result\":" + Jsoner.serialize(
					processInput(gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller
							.getExperiment().getAgent(), map.get("expr").toString()))
					+ "}";
			if (!wasPaused) {
				gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller.userStart();
			}			
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "", map);

		} else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Wrong socket_id or exp_id " + socket_id + " " + exp_id, map);
		}
	}

	protected String processInput(final IAgent agt, final String s) {
		IAgent agent = agt;// = getListeningAgent();
		if (agent == null) {
			agent = GAMA.getPlatformAgent();
		}
		final IScope scope = new ExecutionScope(agent.getScope().getRoot(), " in console");// agent.getScope();
		if (!agent.dead()) {
			final var entered = s.trim();
			String result = null;
			var error = false;
			if (entered.startsWith("?")) {
				result = GamlIdiomsProvider.getDocumentationOn(entered.substring(1));
			} else {
				try {
					final var expr = GAML.compileExpression(s, agent, false);
					if (expr != null) {
						result = StringUtils.toGaml(scope.evaluate(expr, agent).getValue(), true);
					}
				} catch (final Exception e) {
					error = true;
					result = "> Error: " + e.getMessage();
				} finally {
					agent.getSpecies().removeTemporaryAction();
				}
			}
			if (result == null) {
				result = "nil";
			}
			// append(result, error, true);
			if (!error && GAMA.getExperiment() != null) {
				GAMA.getExperiment().refreshAllOutputs();
			}
			return result;
		}
		return "";

	}
}
