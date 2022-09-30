package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;
import ummisco.gama.dev.utils.DEBUG;

public class ExpressionCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		final String exp_id 	= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object socket_id 	= map.get("socket_id");
		final Object model 		= map.get("model");
		final Object experiment	= map.get("experiment");
		final Object expr		= map.get("expr");
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("expresion");
		DEBUG.OUT(model);
		DEBUG.OUT(experiment);

		if (exp_id == "" || socket_id == null || model == null || experiment == null || expr == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'expression', mandatory parameters are: 'exp_id', 'socket_id', 'model', 'experiment' and 'expr'", map, false);
		}
		
		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id.toString(), exp_id);
		if (gama_exp != null && gama_exp.getSimulation() != null) {

			final boolean wasPaused = gama_exp.controller.isPaused();
			gama_exp.controller.directPause();

			IMap<String, Object> res = GamaMapFactory.create();
			res.put("result", processInput(gama_exp.controller.getExperiment().getAgent(), expr.toString()));
			if (!wasPaused) {
				gama_exp.controller.userStart();
			}
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, res, map, false);

		} else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					"Wrong socket_id or exp_id " + socket_id + " " + exp_id, map, false);
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
