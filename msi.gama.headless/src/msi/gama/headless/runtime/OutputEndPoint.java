package msi.gama.headless.runtime;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.RichOutput;
import msi.gama.headless.job.ListenedVariable;

public class OutputEndPoint implements Endpoint {

	@Override
	public void onOpen(WebSocket socket) {
		socket.send("Hello!");
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket socket, String message) {
//		socket.send(message);

		System.out.println(socket + ": " + message);
		String[] args = message.split("@");
		if ("output".equals(args[0])) {
			String id_exp = args[1];
			if (server.simulations.get(id_exp) != null && server.simulations.get(id_exp).getSimulation() != null) {
				for (ListenedVariable l : server.simulations.get(id_exp).getListenedVariables()) {
					if (l.getName().equals(args[2])) {

						final RichOutput out = (RichOutput) server.simulations.get(id_exp).getSimulation()
								.getRichOutput(l);
						if (out != null && out.getValue() instanceof BufferedImage) {
							try {
								BufferedImage bi = (BufferedImage) out.getValue();
								ByteArrayOutputStream out1 = new ByteArrayOutputStream();
								ImageIO.write(bi, "png", out1);
								ByteBuffer byteBuffer = ByteBuffer.wrap(out1.toByteArray());
								socket.send(byteBuffer);
								out1.close();
								byteBuffer.clear();

							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

		}
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket conn, ByteBuffer message) {
		// TODO Auto-generated method stub

	}

}