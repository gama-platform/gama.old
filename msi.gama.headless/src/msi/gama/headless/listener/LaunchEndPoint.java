/*******************************************************************************************************
 *
 * LaunchEndPoint.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.runtime.SimulationRuntime.DebugStream;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gaml.operators.Spatial;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.websocket.Endpoint;
import ummisco.gama.network.websocket.IGamaWebSocketServer;

/**
 * The Class LaunchEndPoint.
 */
public class LaunchEndPoint implements Endpoint {

	@Override
	public void onOpen(final WebSocket socket) {
		socket.send("You have connected to chat");
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
	public void launchGamlSimulation(final IGamaWebSocketServer server, WebSocket socket, final List<String> args,
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
				selectedJob = new ManualExperimentJob((ExperimentJob) j, server, socket);
				selectedJob.setExport(export);
				break;
			}
		}
		if (selectedJob == null)
			return;
		Globals.OUTPUT_PATH = args.get(args.size() - 3);

		selectedJob.directOpenExperiment();
		if (((GamaWebSocketServer) server).get_listener().getExperimentsOf("" + socket.hashCode()) == null) {
			final ConcurrentHashMap<String, ManualExperimentJob> exps = new ConcurrentHashMap<String, ManualExperimentJob>();
			((GamaWebSocketServer) server).get_listener().getAllExperiments().put("" + socket.hashCode(), exps);

		}
		((GamaWebSocketServer) server).get_listener().getExperimentsOf("" + socket.hashCode()).put(selectedJob.getExperimentID(), selectedJob);

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
		System.out.println("exp@" + "" + socket.hashCode() + "@" + selectedJob.getExperimentID() + "@" + size + "@"
				+ geom.getLocation().x + "@" + geom.getLocation().y);
		socket.send("exp@" + "" + socket.hashCode() + "@" + selectedJob.getExperimentID() + "@" + size + "@"
				+ geom.getLocation().x + "@" + geom.getLocation().y);
		if (export)
			selectedJob.exportVariables();
		((GamaWebSocketServer) server).getDefaultApp().processorQueue.execute(selectedJob.executionThread);
	}

	@Override
	public void onMessage(final IGamaWebSocketServer server, final WebSocket socket, final String message) {
		// server.get_listener().broadcast(message);
		final String socket_id = "" + socket.hashCode();
		System.out.println(socket + ": " + message);
		String[] args = message.split("@");
		String id_exp = args[1];
		switch (args[0]) {
		case "launch":
			try {
				launchGamlSimulation(server, socket, Arrays.asList("-gaml", ".", args[2], args[1]),
						args.length > 3 ? false : true);
			} catch (IOException | GamaHeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "play":
			System.out.println("play " + id_exp);
			if (((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp) != null
					&& ((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
				((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).userStart();
			}
			break;
		case "step":
			System.out.println("step " + id_exp);
			if (((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp) != null
					&& ((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
				((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).userStep();
			}
			break;
		case "pause":
			System.out.println("pause " + id_exp);
			if (((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp) != null
					&& ((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
				((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).directPause();
			}
			break;
		case "stop":
			System.out.println("stop " + id_exp);
			if (((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp) != null
					&& ((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).getSimulation() != null) {
				((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).directPause();
				((GamaWebSocketServer) server).get_listener().getExperiment(socket_id, id_exp).dispose();
			}
			break;
		case "exit":
			System.exit(0);
			break;
		case "compile":
			try {
				Globals.IMAGES_PATH = "C:\\GAMA\\headless\\samples\\toto\\snapshot";
				compileGamlSimulation(socket, Arrays.asList("-gaml", ".", args[2], args[1]));
			} catch (IOException | GamaHeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void onMessage(IGamaWebSocketServer server, WebSocket conn, ByteBuffer compiledModel) {

		try {
			runCompiledSimulation(server, compiledModel);
		} catch (IOException | GamaHeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}