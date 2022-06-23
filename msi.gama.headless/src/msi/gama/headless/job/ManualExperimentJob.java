/*******************************************************************************************************
 *
 * ExperimentJob.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.interfaces.IGui;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.RichOutput;
import msi.gama.headless.listener.GamaWebSocketServer;
import msi.gama.headless.listener.ServerExperimentController;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types; 

/**
 * The Class ExperimentJob.
 */
public class ManualExperimentJob extends ExperimentJob {
	protected WebSocketServer server;
	public WebSocket socket; 
	public GamaJsonList params; 
	public IExperimentController controller;

	public ManualExperimentJob(ExperimentJob j, WebSocketServer gamaWebSocketServer, WebSocket sk, final GamaJsonList p) {
		super(j);
		server = gamaWebSocketServer;
		socket = sk;
		params = p;  
		controller=new ServerExperimentController(j.getSimulation().getExperimentPlan(),this);
	}
	
	@Override
	public void doStep() {
//		this.step = simulator.step();
	}
 	
	public void loadAndBuildWithJson( ) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, GamaHeadlessException {

		this.load();
		this.setup();
//		initParam(p);
		simulator.setup(experimentName, this.seed, params, this);
		initEndContion("");

	}

	public void initParam(GamaJsonList p) {
		params = p;
		if (params != null) {
			final ExperimentPlan curExperiment = (ExperimentPlan) simulator.getExperimentPlan();
			for (var O : ((GamaJsonList) params).listValue(null, Types.MAP, false)) {
				IMap<String, Object> m = (IMap<String, Object>) O;
				curExperiment.setParameterValueByTitle(curExperiment.getExperimentScope(), m.get("name").toString(),
						m.get("value"));
			}
		}
	}

	// Initialize the enCondition
	public void initEndContion(String cond) {
		if (cond == null || "".equals(cond)) {
			endCondition = IExpressionFactory.FALSE_EXPR;
		} else {
			endCondition = GAML.getExpressionFactory().createExpr(cond, simulator.getModel().getDescription());
			// endCondition = GAML.compileExpression(untilCond, simulator.getSimulation(),
			// true);
		}
		if (endCondition.getGamlType() != Types.BOOL)
			throw GamaRuntimeException.error("The until condition of the experiment should be a boolean",
					simulator.getSimulation().getScope());
	}

	@Override
	public void exportVariables() {
		final int size = this.listenedVariables.length;
		if (size == 0)
			return;
		for (int i = 0; i < size; i++) {
			final ListenedVariable v = this.listenedVariables[i];
			if (this.step % v.frameRate == 0) {
				final RichOutput out = simulator.getRichOutput(v);
				if (out == null || out.getValue() == null) {
				} else if (out.getValue() instanceof BufferedImage) {
					try {
						BufferedImage bi = (BufferedImage) out.getValue();
						ByteArrayOutputStream out1 = new ByteArrayOutputStream();
						ImageIO.write(bi, "png", out1);

						byte[] array1 = out1.toByteArray();
						byte[] array2 = { (byte) 0 };
						byte[] array3 = { (byte) i };
						byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length + array3.length);
						System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
						System.arraycopy(array3, 0, joinedArray, array1.length + array2.length, array3.length);

						ByteBuffer byteBuffer = ByteBuffer.wrap(joinedArray);
						if (!socket.isClosing() && !socket.isClosed())
							socket.send(byteBuffer);
//						server.broadcast(byteBuffer);
						out1.close();
						byteBuffer.clear();

					} catch (IOException e) {
						e.printStackTrace();
					}
//					v.setValue(writeImageInFile((BufferedImage) out.getValue(), v.getName(), v.getPath()), step,
//							out.getType());
				} else {
					byte[] array1 = (out.getName() + ": " + out.getValue().toString()).getBytes();
					byte[] array2 = { (byte) 1 };
					byte[] array3 = { (byte) i };
					byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length + array3.length);
					System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
					System.arraycopy(array3, 0, joinedArray, array1.length + array2.length, array3.length);

					ByteBuffer byteBuffer = ByteBuffer.wrap(joinedArray);
					if (!socket.isClosing() && !socket.isClosed())
						socket.send(byteBuffer);
					v.setValue(out.getValue(), out.getStep(), out.getType());
				}
			} else {
				v.setValue(null, this.step);
			}
		}
//		if (this.outputFile != null) {
//			this.outputFile.writeResultStep(this.step, this.listenedVariables);
//		}

	}

}
