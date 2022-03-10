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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.java_websocket.WebSocket;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.script.ExperimentationPlanFactory;

/**
 * The Class LaunchEndPoint.
 */
public class LaunchEndPoint implements Endpoint {

	@Override
	public void onOpen(final WebSocket socket) {
		socket.send("You have connected to chat");
	}

	/**
	 * Run gaml simulation.
	 *
	 * @param server
	 *            the server
	 * @param args
	 *            the args
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public void runGamlSimulation(final GamaWebSocketServer server, final List<String> args)
			throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		if (!GamlFileExtension.isGaml(pathToModel)) { System.exit(-1); }
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
		if (selectedJob == null) return;
		Globals.OUTPUT_PATH = args.get(args.size() - 3);

		// selectedJob.setBufferedWriter(new XMLWriter(Globals.OUTPUT_PATH + "/" + Globals.OUTPUT_FILENAME + ".xml"));

		// if (args.contains(THREAD_PARAMETER)) {
		// this.numberOfThread = Integer.parseInt(after(args, THREAD_PARAMETER));
		// } else {
		// numberOfThread = SimulationRuntime.UNDEFINED_QUEUE_SIZE;
		// }
		// server.getDefaultApp().processorQueue =
		// new ExecutorBasedSimulationRuntime(SimulationRuntime.UNDEFINED_QUEUE_SIZE);

		server.getDefaultApp().processorQueue.pushSimulation(selectedJob);

	}

	@Override
	public void onMessage(final GamaWebSocketServer server, final WebSocket socket, final String message) {
		server.broadcast(message);
		System.out.println(socket + ": " + message);
		if ("run".equals(message)) {
			try {
				runGamlSimulation(server, Arrays.asList("-gaml", "C:\\GAMA\\headless\\samples\\toto",
						"prey_predatorExp", "C:\\GAMA\\headless\\samples\\predatorPrey\\predatorPrey.gaml"));
			} catch (IOException | GamaHeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}