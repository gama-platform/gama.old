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

import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.IScope;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
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

		final String exp_id = map.get(EXP_ID) != null ? map.get(EXP_ID).toString() : "";
		final Object expr = map.get(EXPR);
		final String socket_id = map.get(SOCKET_ID) != null ? map.get(SOCKET_ID).toString() : "" + socket.hashCode();
		final boolean escaped = map.get(ESCAPED) == null ? false : Boolean.parseBoolean("" + map.get(ESCAPED));
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get(SERVER);
		DEBUG.OUT("expresion");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(expr);

		if (exp_id == "" || expr == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'expression', mandatory parameters are: 'exp_id' and 'expr'", map, false);

		IExperimentPlan exp = gamaWebSocketServer.getExperiment(socket_id, exp_id);
		if (exp == null || exp.getAgent() == null || exp.getAgent().dead())
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"No experiment agent found for experiment " + exp_id, map, false);
		final String res = processInput(exp.getAgent(), expr.toString());
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
	protected String processInput(final IExperimentAgent agt, final String s) {
		String result = null;
		IExperimentAgent agent = agt;
		final IScope scope = agent.getScope().copy(" in console");
		final var entered = s.trim();
		try {
			final var expr = GAML.compileExpression(entered, agent, false);
			if (expr != null) { result = String.valueOf(scope.evaluate(expr, agent).getValue()); }
		} catch (final Exception e) {
			result = "> Error: " + e.getMessage();
		} finally {
			agent.getSpecies().removeTemporaryAction();
		}
		return result;

	}
}
