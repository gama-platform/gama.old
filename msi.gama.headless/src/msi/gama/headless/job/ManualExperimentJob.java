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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.RichOutput;
import msi.gama.headless.runtime.GamaWebSocketServer;

/**
 * The Class ExperimentJob.
 */
public class ManualExperimentJob extends ExperimentJob {
	protected GamaWebSocketServer server;
	protected WebSocket socket;
	public boolean paused = false;
	public Thread internalThread;

	public ManualExperimentJob(ExperimentJob clone, GamaWebSocketServer s, WebSocket sk) {
		super(clone);
		server = s;
		socket = sk;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doStep() {
		this.step = simulator.step();
		this.exportVariables();
	}

	@Override
	protected void exportVariables() {
		final int size = this.listenedVariables.length;
		if (size == 0)
			return;
		for (int i = 0; i < size; i++) {
			final ListenedVariable v = this.listenedVariables[i];
			if (this.step % v.frameRate == 0) {
				final RichOutput out = simulator.getRichOutput(v);
				if (out == null || out.getValue() == null) {
				} else if (out.getValue() instanceof BufferedImage) {
//					System.out.println(v.getName());
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
					byte[] array1 = (out.getName()+": "+out.getValue().toString()).getBytes();
					byte[] array2 = { (byte) 1 };
					byte[] array3 = { (byte) i };
					byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length + array3.length);
					System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
					System.arraycopy(array3, 0, joinedArray, array1.length + array2.length, array3.length);

					ByteBuffer byteBuffer = ByteBuffer.wrap(joinedArray);
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
