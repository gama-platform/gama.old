/*******************************************************************************************************
 *
 * AskCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.metamodel.agent.AgentReference;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class AskCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class AskCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get(SERVER);
		final String exp_id = map.get(EXP_ID) != null ? map.get(EXP_ID).toString() : "";
		final String socket_id =
				map.get(SOCKET_ID) != null ? map.get(SOCKET_ID).toString().trim() : String.valueOf(socket.hashCode());
		IExperimentPlan plan = gamaWebSocketServer.getExperiment(socket_id, exp_id);
		if (plan == null || plan.getAgent() == null || plan.getAgent().dead())
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"No experiment agent found for experiment " + exp_id, map, false);

		final String action = map.get(IKeyword.ACTION) != null ? map.get(IKeyword.ACTION).toString().trim() : null;
		final String ref = map.get(IKeyword.AGENT) != null ? map.get(IKeyword.AGENT).toString().trim() : null;
		final boolean escaped = map.get(ESCAPED) == null ? false : Boolean.parseBoolean("" + map.get(ESCAPED));

		DEBUG.OUT("ask");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(ref);
		DEBUG.OUT(action);

		if (exp_id == "" || action == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'expression', mandatory parameters are: 'exp_id' and 'action'", map, false);

		final ExperimentAgent exp = plan.getAgent();
		IScope scope = exp.getScope();
		final IAgent agent = ref == null ? exp : AgentReference.of(ref).getReferencedAgent(scope);
		final IExecutable exec = agent.getSpecies().getAction(action);

		// TODO Verify that it is not a JSON string...Otherwise, use Json.getNew().parse(...)
		final IMap<String, Object> args = Cast.asMap(scope, map.get("args"), false);
		ExecutionResult er = ExecutionResult.PASSED;
		try {
			er = agent.getScope().execute(exec, agent, true, new Arguments(args));
		} catch (GamaRuntimeException e) {
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, e.getMessage(), map, false);
		}
		if (!er.passed()) return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"Error in the execution of " + action, map, false);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, escaped);
	}

}
