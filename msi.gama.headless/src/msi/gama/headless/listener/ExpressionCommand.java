/*******************************************************************************************************
 *
 * ExpressionCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExpressionCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class ExpressionCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object expr = map.get("expr");
		final String socket_id =
				map.get("socket_id") != null ? map.get("socket_id").toString() : "" + socket.hashCode();
		final boolean escaped = map.get("escaped") == null ? false : Boolean.parseBoolean("" + map.get("escaped"));
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("expresion");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(expr);

		if (exp_id == "" || expr == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'expression', mandatory parameters are: 'exp_id' and 'expr'", map, false);

		var exp = gamaWebSocketServer.getExperiment(socket_id, exp_id);
		if (exp == null || exp.getCurrentSimulation() == null)
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, "Wrong exp_id " + exp_id, map,
					false);
		final String res = processInput(exp.getCurrentSimulation(), expr.toString());
		if (res == null || res.length() == 0 || res.startsWith("> Error: "))
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, res, map, false);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, res, map, escaped);
	}

	/**
	 * Process input.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agt
	 *            the agt
	 * @param s
	 *            the s
	 * @return the string
	 * @date 15 oct. 2023
	 */
	protected String processInput(final IAgent agt, final String s) {
		String result = null;
		IAgent agent = agt;// = getListeningAgent();
		if (agent == null) { agent = GAMA.getPlatformAgent(); }
		final IScope scope = new ExecutionScope(agent.getScope().getRoot(), " in console");// agent.getScope();
		if (!agent.dead()) {
			final var entered = s.trim();
			// var error = false;
			if (entered.startsWith("?")) {
				result = GamlIdiomsProvider.getDocumentationOn(entered.substring(1));
			} else {
				try {
					final var expr = GAML.compileExpression(s, agent, false);
					if (expr != null) {
						result = "" + scope.evaluate(expr, agent).getValue();// StringUtils.toGaml(scope.evaluate(expr,
																				// agent).getValue(), true);
					}
				} catch (final Exception e) {
					// error = true;
					result = "> Error: " + e.getMessage();
				} finally {
					agent.getSpecies().removeTemporaryAction();
				}
			}
			// append(result, error, true);
			// if (!error && GAMA.getExperiment() != null) {
			// GAMA.getExperiment().refreshAllOutputs();
			// }
		}
		return result;

	}
}
