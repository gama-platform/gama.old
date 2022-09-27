package msi.gama.headless.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.GamlFileExtension;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.headless.common.Globals;
import msi.gama.headless.common.SaveHelper;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.runtime.Application;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;
import ummisco.gama.dev.utils.DEBUG;

public class GamaWebSocketServer extends WebSocketServer {

	private GamaListener _listener;

	public GamaListener get_listener() {
		return _listener;
	}

	public void set_listener(final GamaListener _listener) {
		this._listener = _listener;
	}

	private final Application app;

	CommandExecutor cmdHelper;

	public GamaWebSocketServer(final int port, final Application a, final GamaListener l, final boolean ssl) {
		super(new InetSocketAddress(port));
		if (a.verbose) {
			DEBUG.ON();
		}
		cmdHelper = new CommandExecutor();
		if (ssl) {
			// load up the key store
			String STORETYPE = "JKS";
			File currentJavaJarFile = new File(
					GamaListener.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();

			String KEYSTORE = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "") + "/../keystore.jks";
			String STOREPASSWORD = "storepassword";
			String KEYPASSWORD = "storepassword";

			KeyStore ks;
			try {
				ks = KeyStore.getInstance(STORETYPE);
				File kf = new File(KEYSTORE);
				ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, KEYPASSWORD.toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(ks);

				SSLContext sslContext = null;
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

				this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		app = a;
		_listener = l;
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		// conn.send("Welcome " +
		// conn.getRemoteSocketAddress().getAddress().getHostAddress() + " to the
		// server!");
		// broadcast("new connection: " + handshake.getResourceDescriptor()); // This
		// method sends a message to all
		// clients
		// connected
		DEBUG.OUT(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
		conn.send("" + conn.hashCode());
		// String path = URI.create(handshake.getResourceDescriptor()).getPath();
	}

	public Application getDefaultApp() {
		return app;
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		if (_listener.getLaunched_experiments().get("" + conn.hashCode()) != null) {
			for (ManualExperimentJob e : _listener.getLaunched_experiments().get("" + conn.hashCode()).values()) {
				e.controller.directPause();
				e.dispose();
			}
			_listener.getLaunched_experiments().get("" + conn.hashCode()).clear();
		}
		// broadcast(conn + " has left the room!");
		DEBUG.OUT(conn + " has left the room!");
	}

	public IMap<String, Object> extractParam(final WebSocket socket, final String message) {
		IMap<String, Object> map = null;
		try {

			// DEBUG.OUT(socket + ": " + Jsoner.deserialize(message));
			final Object o = Jsoner.deserialize(message);
			if (o instanceof IMap) {
				map = (IMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(IKeyword.CONTENTS, o);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			socket.send(e1.getMessage());
		}
		return map;
	}

	@Override
	public void onMessage(final WebSocket socket, final String message) {
		// server.get_listener().broadcast(message);
		// DEBUG.OUT(socket + ": " + message);
		try {
			// DEBUG.OUT(socket + ": " + Jsoner.deserialize(message));

			IMap<String, Object> map = extractParam(socket, message);
//			String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
//			String socket_id = map.get("socket_id").toString();
//			final String cmd_type = map.get("type").toString();
			map.put("server", this);
			DEBUG.OUT(map.get("type"));
			cmdHelper.process(socket, map);
//			switch (cmd_type) {
//			case "launch":
//				DEBUG.OUT("launch");
//				DEBUG.OUT(map.get("model"));
//				DEBUG.OUT(map.get("experiment"));
//				try {
//					launchGamlSimulation(socket,
//							Arrays.asList("-gaml", ".", map.get("experiment").toString(), map.get("model").toString()),
//							(GamaJsonList) map.get("parameters"),
//							map.get("until") != null ? map.get("until").toString() : "");
//				} catch (IOException | GamaHeadlessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				break;
//			case "play":
//				DEBUG.OUT("play " + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					get_listener().getExperiment(socket_id, exp_id).controller.userStart();
//					ManualExperimentJob job = get_listener().getExperiment(socket_id, exp_id);
//					if ("".equals(job.endCond)) {
//						socket.send(cmd_type);
//					}
//				}
//				break;
//			case "step":
//				DEBUG.OUT("step " + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					get_listener().getExperiment(socket_id, exp_id).controller.userStep();
//				}
//				socket.send(cmd_type);
//				break;
//			case "stepBack":
//				DEBUG.OUT("stepBack " + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					get_listener().getExperiment(socket_id, exp_id).controller.userStepBack();
//				}
//				socket.send(cmd_type);
//				break;
//			case "pause":
//				DEBUG.OUT("pause " + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					get_listener().getExperiment(socket_id, exp_id).controller.directPause();
//				}
//				socket.send(cmd_type);
//				break;
//			case "stop":
//				DEBUG.OUT("stop " + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					get_listener().getExperiment(socket_id, exp_id).controller.directPause();
//					get_listener().getExperiment(socket_id, exp_id).dispose();
//				}
//				socket.send(cmd_type);
//				break;
//			case "reload":
//				DEBUG.OUT("reload " + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					ManualExperimentJob job = get_listener().getExperiment(socket_id, exp_id);
//					job.params = (GamaJsonList) map.get("parameters");
//					job.endCond = map.get("until") != null ? map.get("until").toString() : "";
//					job.controller.userReload();
//					// (GamaJsonList) map.get("parameters"),
//					// job.executionThread.run();
//					// getDefaultApp().processorQueue.execute(((ServerExperimentController)job.controller).executionThread);
//				}
//				break;
//			case "output":
//				DEBUG.OUT("output" + exp_id);
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					final boolean wasPaused = get_listener().getExperiment(socket_id, exp_id).controller.isPaused();
//					get_listener().getExperiment(socket_id, exp_id).controller.directPause();
//					IList<? extends IShape> agents = get_listener().getExperiment(socket_id, exp_id).getSimulation()
//							.getSimulation().getPopulationFor(map.get("species").toString());
//
//					final IList ll = map.get("attributes") != null ? (IList) map.get("attributes")
//							: GamaListFactory.EMPTY_LIST;
//					final String crs = map.get("crs") != null ? map.get("crs").toString() : "";
//					String res = "";
//					try {
//						res = SaveHelper.buildGeoJSon(get_listener().getExperiment(socket_id, exp_id).getSimulation()
//								.getExperimentPlan().getAgent().getScope(), agents, ll, crs);
//					} catch (Exception ex) {
//						res = ex.getMessage();
//					}
//					socket.send(res);
//
//					if (!wasPaused) {
//						get_listener().getExperiment(socket_id, exp_id).controller.userStart();
//					}
//				}
//				break;
//			case "expression":
//				if (get_listener().getExperiment(socket_id, exp_id) != null
//						&& get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
//					final boolean wasPaused = get_listener().getExperiment(socket_id, exp_id).controller.isPaused();
//					get_listener().getExperiment(socket_id, exp_id).controller.directPause();
//					String res = "{\"result\":" + Jsoner.serialize(processInput(
//							get_listener().getExperiment(socket_id, exp_id).controller.getExperiment().getAgent(),
//							map.get("expr").toString())) + "}";
//					if (!wasPaused) {
//						get_listener().getExperiment(socket_id, exp_id).controller.userStart();
//					}
//					socket.send(res);
//				} else {
//					socket.send("Wrong socket_id or exp_id " + socket_id + " " + exp_id);
//				}
//				break;
//			case "exit":
//				System.exit(0);
//				break;
//			case "compile":
//				try {
//					Globals.IMAGES_PATH = "C:\\GAMA\\headless\\samples\\toto\\snapshot";// TODO: does it have any
//																						// purpose ? Why windows
//																						// path ?
//					compileGamlSimulation(socket,
//							Arrays.asList("-gaml", ".", map.get("model").toString(), map.get("experiment").toString()));
//				} catch (IOException | GamaHeadlessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				socket.send(cmd_type);
//				break;
//			default:
//				socket.send("Unknown command");
//				break;
//			}
		} catch (Exception e1) {
			e1.printStackTrace();
			socket.send(e1.getMessage());
		}
	}

	@Override
	public void onMessage(final WebSocket conn, final ByteBuffer message) {
		try {
			runCompiledSimulation(this, message);
		} catch (IOException | GamaHeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onError(final WebSocket conn, final Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {
		DEBUG.OUT("Gama Listener started on port: " + getPort());
		// setConnectionLostTimeout(0);
		// setConnectionLostTimeout(100);
	}

	public void compileGamlSimulation(final WebSocket socket, final List<String> args)
			throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		if (!GamlFileExtension.isGaml(pathToModel)) {
			System.exit(-1);
		}
		final String argExperimentName = args.get(args.size() - 2);
		final String argGamlFile = args.get(args.size() - 1);

		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(argGamlFile);
		ExperimentJob selectedJob = null;
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(argExperimentName)) {
				selectedJob = (ExperimentJob) j;
				break;
			}
		}
		if (selectedJob == null)
			return;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(selectedJob);
			out.flush();
			byte[] yourBytes = bos.toByteArray();
			socket.send(yourBytes);
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}

	}

	public void runCompiledSimulation(final WebSocketServer server, final ByteBuffer compiledModel)
			throws IOException, GamaHeadlessException {
		ByteArrayInputStream bis = new ByteArrayInputStream(compiledModel.array());
		ObjectInput in = null;
		ExperimentJob selectedJob = null;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			selectedJob = (ExperimentJob) o;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		((GamaWebSocketServer) server).getDefaultApp().processorQueue.execute(selectedJob);

	}

//	/**
//	 * Run gaml simulation.
//	 *
//	 * @param server  the server
//	 * @param message the args
//	 * @throws IOException           Signals that an I/O exception has occurred.
//	 * @throws GamaHeadlessException the gama headless exception
//	 */
//	public void launchGamlSimulation(final WebSocket socket, final List<String> args, final GamaJsonList params,
//			final String end) throws IOException, GamaHeadlessException {
//		final String pathToModel = args.get(args.size() - 1);
//
//		File ff = new File(pathToModel);
//		// DEBUG.OUT(ff.getAbsoluteFile().toString());
//		if (!ff.exists()) {
//			DEBUG.OUT(ff.getAbsolutePath() + " does not exist");
//			socket.send("gaml file does not exist");
//			return;
//		}
//		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString())) {
//			// System.exit(-1);
//			DEBUG.OUT(ff.getAbsolutePath() + " is not a gaml file");
//			socket.send(pathToModel + "is not a gaml file");
//			return;
//		}
//		final String argExperimentName = args.get(args.size() - 2);
//		// final String argGamlFile = args.get(args.size() - 1);
//
//		// final List<IExperimentJob> jb =
//		// ExperimentationPlanFactory.buildExperiment(ff.getAbsoluteFile().toString());
//		ManualExperimentJob selectedJob = null;
//		// for (final IExperimentJob j : jb) {
//		// if (j.getExperimentName().equals(argExperimentName)) {
//		selectedJob = new ManualExperimentJob(ff.getAbsoluteFile().toString(), argExperimentName, this, socket, params);
//		// break;
//		// }
//		// }
//		// if (selectedJob == null)
//		// return;
//
//		Globals.OUTPUT_PATH = args.get(args.size() - 3);
//		selectedJob.endCond = end;
//		selectedJob.controller.directOpenExperiment();
//		if (get_listener().getExperimentsOf("" + socket.hashCode()) == null) {
//			final ConcurrentHashMap<String, ManualExperimentJob> exps = new ConcurrentHashMap<>();
//			get_listener().getAllExperiments().put("" + socket.hashCode(), exps);
//
//		}
//		get_listener().getExperimentsOf("" + socket.hashCode()).put(selectedJob.getExperimentID(), selectedJob);
//
//		// final int size = selectedJob.getListenedVariables().length;
//		// String lst_out = "";
//		// if (size != 0) {
//		// for (int i = 0; i < size; i++) {
//		// final ListenedVariable v = selectedJob.getListenedVariables()[i];
//		// lst_out += "@" + v.getName();
//		// }
//		// }
//		// IAgent agt = selectedJob.getSimulation().getSimulation();
//
//		// IShape geom = agt.getGeometry();
//		// Spatial.Projections.transform_CRS(agt.getScope(), agt.getGeometry(),
//		// "EPSG:4326");
//		String res = "{" + " \"type\": \"exp\"," + " \"socket_id\": \"" + socket.hashCode() + "\"," + " \"exp_id\": \""
//				+ selectedJob.getExperimentID() + "\""
//				// + " \"number_displays\": \""+ size +"\","
//				// + " \"lat\": \""+ geom.getLocation().x +"\","
//				// + " \"lon\": \""+ geom.getLocation().y +"\""
//				+ "	}";
//		// DEBUG.OUT("exp@" + "" + socket.hashCode() + "@" +
//		// selectedJob.getExperimentID() + "@" + size + "@"
//		// + geom.getLocation().x + "@" + geom.getLocation().y);
//		// socket.send("exp@" + "" + socket.hashCode() + "@" +
//		// selectedJob.getExperimentID() + "@" + size + "@"
//		// + geom.getLocation().x + "@" + geom.getLocation().y);
//		getDefaultApp().processorQueue.execute(selectedJob.controller.executionThread);
//		socket.send(res);
//	}
//
//	protected String processInput(final IAgent agt, final String s) {
//		IAgent agent = agt;// = getListeningAgent();
//		if (agent == null) {
//			agent = GAMA.getPlatformAgent();
//		}
//		final IScope scope = new ExecutionScope(agent.getScope().getRoot(), " in console");// agent.getScope();
//		if (!agent.dead()) {
//			final var entered = s.trim();
//			String result = null;
//			var error = false;
//			if (entered.startsWith("?")) {
//				result = GamlIdiomsProvider.getDocumentationOn(entered.substring(1));
//			} else {
//				try {
//					final var expr = GAML.compileExpression(s, agent, false);
//					if (expr != null) {
//						result = StringUtils.toGaml(scope.evaluate(expr, agent).getValue(), true);
//					}
//				} catch (final Exception e) {
//					error = true;
//					result = "> Error: " + e.getMessage();
//				} finally {
//					agent.getSpecies().removeTemporaryAction();
//				}
//			}
//			if (result == null) {
//				result = "nil";
//			}
//			// append(result, error, true);
//			if (!error && GAMA.getExperiment() != null) {
//				GAMA.getExperiment().refreshAllOutputs();
//			}
//			return result;
//		}
//		return "";
//
//	}
}
