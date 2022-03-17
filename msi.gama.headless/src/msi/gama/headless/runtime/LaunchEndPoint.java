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
package msi.gama.headless.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.ListenedVariable;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.job.Output;
import msi.gama.headless.script.ExperimentationPlanFactory;
import ummisco.gama.dev.utils.DEBUG;

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

	public void runModelSimulation(final GamaWebSocketServer server, final ByteBuffer compiledModel)
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
		server.getDefaultApp().processorQueue.pushSimulation(selectedJob);

	}

	/**
	 * Run gaml simulation.
	 *
	 * @param server  the server
	 * @param message the args
	 * @throws IOException           Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public void launchGamlSimulation(final GamaWebSocketServer server, WebSocket socket, final List<String> args)
			throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		if (!GamlFileExtension.isGaml(pathToModel)) {
			System.exit(-1);
		}
		final String argExperimentName = args.get(args.size() - 2);
		final String argGamlFile = args.get(args.size() - 1);

		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(argGamlFile);
		ManualExperimentJob selectedJob = null;
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(argExperimentName)) {
				selectedJob = new ManualExperimentJob((ExperimentJob) j, server, socket);
				break;
			}
		}
		if (selectedJob == null)
			return;
		Globals.OUTPUT_PATH = args.get(args.size() - 3);

		try {
			selectedJob.loadAndBuild();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException
				| GamaHeadlessException e) {
			e.printStackTrace();
		}
		server.simulations.put(selectedJob.getExperimentID(), selectedJob);
		final int size = selectedJob.getListenedVariables().length;
//		String lst_out = "";
//		if (size != 0) {
//			for (int i = 0; i < size; i++) {
//				final ListenedVariable v = selectedJob.getListenedVariables()[i];
//				lst_out += "@" + v.getName();
//			}
//		}
		socket.send("exp@" + selectedJob.getExperimentID() + "@" + size);

		// server.getDefaultApp().processorQueue.pushSimulation(selectedJob);
	}

	@Override
	public void onMessage(final GamaWebSocketServer server, final WebSocket socket, final String message) {
		// server.broadcast(message);
		System.out.println(socket + ": " + message);
		String[] args = message.split("@");
		if ("launch".equals(args[0])) {
			try {
				Globals.IMAGES_PATH = "C:\\GAMA\\headless\\samples\\toto\\snapshot";
				launchGamlSimulation(server, socket, Arrays.asList("-gaml", ".", args[2], args[1]));
			} catch (IOException | GamaHeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("play".equals(args[0])) {
			String id_exp = args[1];
			System.out.println("play " + id_exp);
			if (server.simulations.get(id_exp) != null && server.simulations.get(id_exp).getSimulation() != null) {
				((ManualExperimentJob) server.simulations.get(id_exp)).paused = false;
				// DEBUG.TIMER("Simulation duration", () -> {
				if (((ManualExperimentJob) server.simulations.get(id_exp)).internalThread == null) {
					((ManualExperimentJob) server.simulations.get(id_exp)).internalThread = new Thread() {
						@Override
						public void run() {

							while (!server.simulations.get(id_exp).getSimulation().isInterrupted()) {
								if (!((ManualExperimentJob) server.simulations.get(id_exp)).paused)
									server.simulations.get(id_exp).doStep();
							}
						}
					};
					((ManualExperimentJob) server.simulations.get(id_exp)).internalThread.start();
				}
			}
		}

		if ("pause".equals(args[0])) {
			String id_exp = args[1];
			System.out.println("pause " + id_exp);
			if (server.simulations.get(id_exp) != null && server.simulations.get(id_exp).getSimulation() != null) {
				((ManualExperimentJob) server.simulations.get(id_exp)).paused = true;
			}
		}
		if ("stop".equals(args[0])) {
			String id_exp = args[1];
			System.out.println("stop " + id_exp);
			if (server.simulations.get(id_exp) != null && server.simulations.get(id_exp).getSimulation() != null) {
				((ManualExperimentJob) server.simulations.get(id_exp)).paused = true;
				((ManualExperimentJob) server.simulations.get(id_exp)).dispose();
			}
		}
		if ("exit".equals(args[0])) {
			System.exit(0);
		}
		if ("compile".equals(args[0])) {
			try {
				Globals.IMAGES_PATH = "C:\\GAMA\\headless\\samples\\toto\\snapshot";
				compileGamlSimulation(socket, Arrays.asList("-gaml", ".", args[2], args[1]));
			} catch (IOException | GamaHeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket conn, ByteBuffer compiledModel) {

		try {
			runModelSimulation(server, compiledModel);
		} catch (IOException | GamaHeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}