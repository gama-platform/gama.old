package msi.gama.headless.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.geotools.feature.SchemaException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

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
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial;
import ummisco.gama.network.websocket.Endpoint;
import ummisco.gama.network.websocket.IGamaWebSocketServer; 

public class GamaWebSocketServer extends IGamaWebSocketServer {

	private GamaListener _listener;

	public GamaListener get_listener() {
		return _listener;
	}

	public void set_listener(GamaListener _listener) {
		this._listener = _listener;
	}

	private Application app;
//	Map<String, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<>());
//	Map<WebSocket, Endpoint> saved_endpoints = Collections.synchronizedMap(new HashMap<>());  

	public GamaWebSocketServer(int port, Application a, GamaListener l) {
		super(new InetSocketAddress(port));
		app = a;
		_listener = l;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " to the server!");
		broadcast("new connection: " + handshake.getResourceDescriptor()); // This method sends a message to all clients
																			// connected
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");

		String path = URI.create(handshake.getResourceDescriptor()).getPath();
//		Endpoint endpoint = endpoints.get(path);
//		if (endpoint != null) {
//			saved_endpoints.put(conn, endpoint);
//			endpoint.onOpen(conn);
//		}
	}

	public Application getDefaultApp() {
		return app;
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		if (_listener.getLaunched_experiments().get("" + conn.hashCode()) != null) {
			for (ManualExperimentJob e : _listener.getLaunched_experiments().get("" + conn.hashCode()).values()) {
				e.directPause();
				e.dispose();
			}
			_listener.getLaunched_experiments().get("" + conn.hashCode()).clear();
		}
		broadcast(conn + " has left the room!");
		System.out.println(conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket socket, String message) {
		// server.get_listener().broadcast(message);
		String socket_id = "" + socket.hashCode();
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
			String id_exp = map.get("id_exp") != null ? map.get("id_exp").toString() : "";
			switch (map.get("type").toString()) {
			case "launch":
				System.out.println(map.get("model"));
				System.out.println(map.get("experiment"));
				try {
					launchGamlSimulation( socket,
							Arrays.asList("-gaml", ".", map.get("experiment").toString(), map.get("model").toString()),
							Boolean.parseBoolean(map.get("auto-export").toString()) ? false : true);
				} catch (IOException | GamaHeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "play":
				System.out.println("play " + id_exp);
				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					get_listener().getExperiment(socket_id, id_exp).userStart();
				}
				break;
			case "step":
				System.out.println("step " + id_exp);
				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					get_listener().getExperiment(socket_id, id_exp).userStep();
				}
				break;
			case "stepBack":
				System.out.println("stepBack " + id_exp);
				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					get_listener().getExperiment(socket_id, id_exp).userStepBack();
				}
				break;
			case "pause":
				System.out.println("pause " + id_exp);
				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					get_listener().getExperiment(socket_id, id_exp).directPause();
				}
				break;
			case "stop":
				System.out.println("stop " + id_exp);
				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					get_listener().getExperiment(socket_id, id_exp).directPause();
					get_listener().getExperiment(socket_id, id_exp).dispose();
				}
				break;
			case "reload":
				System.out.println("reload " + id_exp);
				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					get_listener().getExperiment(socket_id, id_exp).userReload();
				}
				break;
			case "output":
				socket_id = map.get("socket_id").toString();

//				System.out.println(map.get("species"));
				if (get_listener().getExperiment(socket_id, id_exp) != null
				&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					final boolean wasPaused = get_listener().getExperiment(socket_id, id_exp).isPaused();
					get_listener().getExperiment(socket_id, id_exp).directPause();
					IList<? extends IShape> agents = get_listener().getExperiment(socket_id, id_exp).getSimulation()
							.getSimulation().getPopulationFor(map.get("species").toString());
					
					try {
						IList ll = (IList) map.get("attributes");
						socket.send(SaveHelper.buildGeoJSon(get_listener().getExperiment(socket_id, id_exp)
								.getSimulation().getExperimentPlan().getAgent().getScope(), agents, ll));
					} catch (GamaRuntimeException | IOException | SchemaException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						if (!wasPaused)
							get_listener().getExperiment(socket_id, id_exp).userStart();
					}
				}
				break;
			case "expression":

				if (get_listener().getExperiment(socket_id, id_exp) != null
						&& get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
					final boolean wasPaused = get_listener().getExperiment(socket_id, id_exp).isPaused();
					get_listener().getExperiment(socket_id, id_exp).directPause();
					try {
						socket.send(processInput(
								get_listener().getExperiment(socket_id, id_exp).getExperiment().getAgent(),
								map.get("species").toString()));
					} catch (GamaRuntimeException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						if (!wasPaused)
							get_listener().getExperiment(socket_id, id_exp).userStart();
					}
				}
				break;
			case "exit":
				System.exit(0);
				break;
			case "compile":
				try {
					Globals.IMAGES_PATH = "C:\\GAMA\\headless\\samples\\toto\\snapshot";
					compileGamlSimulation(socket,
							Arrays.asList("-gaml", ".", map.get("model").toString(), map.get("experiment").toString()));
				} catch (IOException | GamaHeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			socket.send(e1.getMessage());
		}
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		try {
			runCompiledSimulation(this, message);
		} catch (IOException | GamaHeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
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

	public void runCompiledSimulation(final IGamaWebSocketServer server, final ByteBuffer compiledModel)
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
		((GamaWebSocketServer) server).getDefaultApp().processorQueue.pushSimulation(selectedJob);

	}

	/**
	 * Run gaml simulation.
	 *
	 * @param server  the server
	 * @param message the args
	 * @throws IOException           Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public void launchGamlSimulation(WebSocket socket, final List<String> args,
			final boolean export) throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		File ff = new File(pathToModel);
		System.out.println(ff.getAbsoluteFile().toString());
		if (!GamlFileExtension.isGaml(ff.getAbsoluteFile().toString())) {
			System.exit(-1);
		}
		final String argExperimentName = args.get(args.size() - 2);
//		final String argGamlFile = args.get(args.size() - 1); 
		if (!ff.exists())
			return;
		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(ff.getAbsoluteFile().toString());
		ManualExperimentJob selectedJob = null;
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(argExperimentName)) {
				selectedJob = new ManualExperimentJob((ExperimentJob) j, this, socket);
				selectedJob.setExport(export);
				break;
			}
		}
		if (selectedJob == null)
			return;
		Globals.OUTPUT_PATH = args.get(args.size() - 3);

		selectedJob.directOpenExperiment();
		if (get_listener().getExperimentsOf("" + socket.hashCode()) == null) {
			final ConcurrentHashMap<String, ManualExperimentJob> exps = new ConcurrentHashMap<String, ManualExperimentJob>();
			get_listener().getAllExperiments().put("" + socket.hashCode(), exps);

		}
		get_listener().getExperimentsOf("" + socket.hashCode())
				.put(selectedJob.getExperimentID(), selectedJob);

		final int size = selectedJob.getListenedVariables().length;
//		String lst_out = "";
//		if (size != 0) {
//			for (int i = 0; i < size; i++) {
//				final ListenedVariable v = selectedJob.getListenedVariables()[i];
//				lst_out += "@" + v.getName();
//			}
//		}
		IAgent agt = selectedJob.getSimulation().getSimulation();

		IShape geom = Spatial.Projections.transform_CRS(agt.getScope(), agt.getGeometry(), "EPSG:4326");
		String res="{"
				+ "		'type': 'exp',"
				+ "		'socket_id': '"+ socket.hashCode() +"',"
				+ "		'exp_id': '"+ selectedJob.getExperimentID() +"',"
				+ "		'number_displays': '"+ size +"',"
				+ "		'lat': '"+ geom.getLocation().x +"',"
				+ "		'lon': '"+ geom.getLocation().y +"'"  
				+ "	}";
//		System.out.println("exp@" + "" + socket.hashCode() + "@" + selectedJob.getExperimentID() + "@" + size + "@"
//				+ geom.getLocation().x + "@" + geom.getLocation().y);
//		socket.send("exp@" + "" + socket.hashCode() + "@" + selectedJob.getExperimentID() + "@" + size + "@"
//				+ geom.getLocation().x + "@" + geom.getLocation().y);
		socket.send(res);
		if (export)
			selectedJob.exportVariables();
		getDefaultApp().processorQueue.execute(selectedJob.executionThread);
	}
	
//	private void setExecutorAgent(final ITopLevelAgent agent) {
//		if (scope != null) {
//			scope.clear();
//			scope = null;
//		}
//		if (agent == null) { 
//		} else {
//			scope = new ExecutionScope(agent, " in console"); 
//		}
//
//	}
//
//	private IAgent getListeningAgent() {
//		if (scope == null) {
//			setExecutorAgent(GAMA.getPlatformAgent());
//		}
//		return scope.getRoot();
//	}
 
	protected String processInput(final IAgent agt, final String s) {
		IAgent agent=agt;// = getListeningAgent();
		if(agent==null) {
			agent=GAMA.getPlatformAgent();
		}
		final IScope scope = new ExecutionScope(agent.getScope().getRoot(), " in console");// agent.getScope();
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
//						result = StringUtils.toGaml(scope.evaluate(expr, agent).getValue(), true);
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
}
