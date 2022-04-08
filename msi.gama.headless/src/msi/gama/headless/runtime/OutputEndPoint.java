package msi.gama.headless.runtime;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.geotools.feature.SchemaException;
import org.java_websocket.WebSocket;

import msi.gama.common.util.StringUtils;
import msi.gama.headless.common.SaveHelper;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;

public class OutputEndPoint implements Endpoint {

	@Override
	public void onOpen(WebSocket socket) {
		socket.send("Hello!");
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket socket, String message) {
//		socket.send(message);

		String[] args = message.split("@");
		if ("output".equals(args[0])) {
			final String socket_id = args[1];
//			System.out.println(socket_id + ": " + message);
			final String id_exp = args[2];
			if (server.getExperiment(socket_id, id_exp) != null
					&& server.getExperiment(socket_id, id_exp).getSimulation() != null) {
				final boolean wasPaused = server.getExperiment(socket_id, id_exp).isPaused();
				server.getExperiment(socket_id, id_exp).directPause();
				IList<? extends IShape> agents = server.getExperiment(socket_id, id_exp).getSimulation().getSimulation()
						.getPopulationFor(args[3]);
//				IList<? extends IShape> agents=GamaListFactory.create();
//				for(IPopulation pop:simulator.getSimulation().getMicroPopulations()) {
//					if(!(pop instanceof GridPopulation)) {
//						agents.addAll(pop);
//					}
//				}
				try {
					socket.send(SaveHelper.buildGeoJSon(
							server.getExperiment(socket_id, id_exp).getSimulation().getExperimentPlan().getAgent().getScope(),
							agents));
				} catch (GamaRuntimeException | IOException | SchemaException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					if (!wasPaused)
						server.getExperiment(socket_id, id_exp).userStart();
				}
			}

		} else if ("expression".equals(args[0])) {
			final String socket_id = args[1];
//			System.out.println(socket_id + ": " + message);
			final String id_exp = args[2];
			if (server.getExperiment(socket_id, id_exp) != null
					&& server.getExperiment(socket_id, id_exp).getSimulation() != null) {
				final boolean wasPaused = server.getExperiment(socket_id, id_exp).isPaused();
				server.getExperiment(socket_id, id_exp).directPause();
				try {
					socket.send(processInput(
							server.getExperiment(socket_id, id_exp).getExperiment().getCurrentSimulation(), args[3]));
				} catch (GamaRuntimeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					if (!wasPaused)
						server.getExperiment(socket_id, id_exp).userStart();
				}
			}

		}
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket conn, ByteBuffer message) {
		// TODO Auto-generated method stub

	}

	protected String processInput(final IAgent agent, final String s) {
		final IScope scope = new ExecutionScope(agent.getScope().getRoot(), " in console");// agent.getScope();
//		final var agent = getListeningAgent();
		if (agent == null || agent.dead()) {
//			setExecutorAgent(null);
		} else {
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
//			append(result, error, true);
			if (!error && GAMA.getExperiment() != null) {
				GAMA.getExperiment().refreshAllOutputs();
			}
			return result;
		}
		return "";

	}

//	/**
//	 * Gets the listening agent.
//	 *
//	 * @return the listening agent
//	 */
//	private IAgent getListeningAgent() {
//		if (scope == null) {
//			setExecutorAgent(GAMA.getPlatformAgent());
//		}
//		return scope.getRoot();
//	}
//
//	/**
//	 * Sets the executor agent.
//	 *
//	 * @param agent the new executor agent
//	 */
//	private void setExecutorAgent(final ITopLevelAgent agent) {
//		if (scope != null) {
//			scope.clear();
//			scope = null;
//		}
//		if (agent == null) {
//
//		} else {
//			scope = new ExecutionScope(agent, " in console");
////			agent.getSpecies().getDescription().attachAlternateVarDescriptionProvider(this);
//
//		}
//
//	}
}