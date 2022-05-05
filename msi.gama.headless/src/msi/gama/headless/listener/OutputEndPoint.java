package msi.gama.headless.listener;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.geotools.feature.SchemaException;
import org.java_websocket.WebSocket;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.headless.common.SaveHelper;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;
import ummisco.gama.network.websocket.Endpoint;
import ummisco.gama.network.websocket.IGamaWebSocketServer;

public class OutputEndPoint implements Endpoint {

	@Override
	public void onOpen(WebSocket socket) {
		socket.send("Hello!");
	}

	@Override
	public void onMessage(IGamaWebSocketServer server, WebSocket socket, String message) {
//		socket.send(message);

//		String[] args = message.split("@");

//		System.out.println(socket + ": " + message);
		final IMap<String, Object> map;
		try {
//			System.out.println(socket + ": " + Jsoner.deserialize(message));
			final Object o = Jsoner.deserialize(message);
			if (o instanceof IMap) {
				map = (IMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(IKeyword.CONTENTS, o);
			}
//			System.out.println(map.get("type"));
//			System.out.println(map.get("model"));
//			System.out.println(map.get("experiment"));
//			String id_exp = map.get("id_exp") != null ? map.get("id_exp").toString() : "";
			final String socket_id = map.get("socket_id").toString();
			final String id_exp = map.get("id_exp") != null ? map.get("id_exp").toString() : "";
			if ("output".equals(map.get("type").toString())) {
//			System.out.println(socket_id + ": " + message);
//				final String id_exp = args[2];
				if (((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp) != null
						&& ((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp)
								.getSimulation() != null) {
					final boolean wasPaused = ((GamaWebSocketServer) server).get_listener()
							.getExperiment(socket_id, id_exp).isPaused();
					((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).directPause();
//					System.out.println(map.get("species").toString());
					IList<? extends IShape> agents = ((GamaWebSocketServer) server).get_listener()
							.getExperiment(socket_id, id_exp).getSimulation().getSimulation().getPopulationFor(map.get("species").toString());
//				IList<? extends IShape> agents=GamaListFactory.create();
//				for(IPopulation pop:simulator.getSimulation().getMicroPopulations()) {
//					if(!(pop instanceof GridPopulation)) {
//						agents.addAll(pop);
//					}
//				}
					try {
						IList ll= (IList) map.get("attributes");
						socket.send(SaveHelper.buildGeoJSon(
								((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp)
										.getSimulation().getExperimentPlan().getAgent().getScope(),
								agents,ll));
					} catch (GamaRuntimeException | IOException | SchemaException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						if (!wasPaused)
							((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).userStart();
					}
				}

			} else if ("expression".equals(map.get("type").toString())) {
				if (((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp) != null
						&& ((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp)
								.getSimulation() != null) {
					final boolean wasPaused = ((GamaWebSocketServer) server).get_listener()
							.getExperiment(socket_id, id_exp).isPaused();
					((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).directPause();
					try {
						socket.send(processInput(((GamaWebSocketServer) server).get_listener()
								.getExperiment(socket_id, id_exp).getExperiment().getCurrentSimulation(), map.get("species").toString()));
					} catch (GamaRuntimeException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						if (!wasPaused)
							((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).userStart();
					}
				}

			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			socket.send(e1.getMessage());
		}
	}

	@Override
	public void onMessage(IGamaWebSocketServer server, WebSocket conn, ByteBuffer message) {
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