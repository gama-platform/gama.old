/*******************************************************************************************************
 *
 * CompileEndPoint.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gaml.operators.Files;

/**
 * The Class CompileEndPoint.
 */
public class CompileEndPoint {

	/**
	 * On open.
	 *
	 * @param socket the socket
	 */
	// @Override
	public void onOpen(final WebSocket socket) {
		// socket.send("Hello!");
	}

	/**
	 * On message.
	 *
	 * @param server the server
	 * @param socket the socket
	 * @param message the message
	 */
	// @Override
	public void onMessage(final WebSocketServer server, final WebSocket socket, final String message) {
		System.out.println(socket + ": String " + message);
		// socket.send(message);

	}

	/**
	 * Builds the from zip.
	 *
	 * @param socket the socket
	 * @param compiledModel the compiled model
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public void buildFromZip(final WebSocket socket, final ByteBuffer compiledModel)
			throws IOException, GamaHeadlessException {
		try (FileOutputStream fos = new FileOutputStream(Globals.TEMP_PATH + "/tmp" + socket.hashCode() + ".zip")) {
			fos.write(compiledModel.array());
		}
		System.out.println(Globals.TEMP_PATH + "/tmp" + socket);
		Files.extractFolder(null, Globals.TEMP_PATH + "/tmp" + socket.hashCode() + ".zip",
				Globals.TEMP_PATH + "/tmp" + socket.hashCode());
		// socket.send(Globals.TEMP_PATH+"/tmp"+socket.hashCode());
		// ByteArrayInputStream bis = new ByteArrayInputStream(compiledModel.array());
		// ObjectInput in = null;
		// ExperimentJob selectedJob = null;
		// try {
		// in = new ObjectInputStream(bis);
		// Object o = in.readObject();
		// selectedJob = (ExperimentJob) o;
		// } catch (ClassNotFoundException ex) {
		// ex.printStackTrace();
		// } finally {
		// try {
		// if (in != null) {
		// in.close();
		// }
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
		// }

	}

	/**
	 * On message.
	 *
	 * @param server the server
	 * @param socket the socket
	 * @param message the message
	 */
	// @Override
	public void onMessage(final WebSocketServer server, final WebSocket socket, final ByteBuffer message) {

		System.out.println(socket + ": " + message);
		try {
			buildFromZip(socket, message);
		} catch (IOException | GamaHeadlessException e) {
			
			e.printStackTrace();
		}

	}

}