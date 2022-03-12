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
import msi.gama.headless.job.Output;
import msi.gama.headless.script.ExperimentationPlanFactory;

/**
 * The Class LaunchEndPoint.
 */
public class LaunchEndPoint implements Endpoint {

	@Override
	public void onOpen(final WebSocket socket) {
		socket.send("You have connected to chat");
	}

	

	public void runGamlSimulation(final GamaWebSocketServer server, final ByteBuffer compiledModel)
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
	 * @param server the server
	 * @param message   the args
	 * @throws IOException           Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public void runGamlSimulation(final GamaWebSocketServer server,WebSocket socket, final List<String> args)
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
		Globals.OUTPUT_PATH = args.get(args.size() - 3); 

		server.simulations.put(selectedJob.getExperimentID(), selectedJob);
		socket.send("exp@"+selectedJob.getExperimentID());
		server.getDefaultApp().processorQueue.pushSimulation(selectedJob);
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

	@Override
	public void onMessage(final GamaWebSocketServer server, final WebSocket socket, final String message) {
//		server.broadcast(message);
		System.out.println(socket + ": " + message);
		String[] args = message.split("@");
		if ("run".equals(args[0])) {
			try {
				Globals.IMAGES_PATH = "C:\\GAMA\\headless\\samples\\toto\\snapshot";
				runGamlSimulation(server,socket, Arrays.asList("-gaml", ".", args[2], args[1]));
			} catch (IOException | GamaHeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			runGamlSimulation(server, compiledModel);
		} catch (IOException | GamaHeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}