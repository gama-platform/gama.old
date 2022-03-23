package msi.gama.headless.runtime;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.geotools.feature.SchemaException;
import org.java_websocket.WebSocket;

import msi.gama.headless.common.SaveHelper;
import msi.gama.headless.core.RichOutput;
import msi.gama.headless.job.ListenedVariable;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;

public class OutputEndPoint implements Endpoint {

	@Override
	public void onOpen(WebSocket socket) {
		socket.send("Hello!");
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket socket, String message) {
//		socket.send(message);

		String[] args = message.split("@");
		if ("output".equals(args[0])) {
			final String socket_id=args[1];
			System.out.println(socket_id + ": " + message);
			final String id_exp = args[2];
			if (server.getExperiment(socket_id,id_exp) != null && server.getExperiment(socket_id,id_exp).getSimulation() != null) {
				IList<? extends IShape> agents = server.getExperiment(socket_id,id_exp).getSimulation().getSimulation()
						.getMicroPopulation(args[3]);
//				IList<? extends IShape> agents=GamaListFactory.create();
//				for(IPopulation pop:simulator.getSimulation().getMicroPopulations()) {
//					if(!(pop instanceof GridPopulation)) {
//						agents.addAll(pop);
//					}
//				}
				try {
					socket.send(SaveHelper.buildGeoJSon(
							server.getExperiment(socket_id,id_exp).getSimulation().getSimulation().getScope(), agents));
				} catch (GamaRuntimeException | IOException | SchemaException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
 
			}

		}
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket conn, ByteBuffer message) {
		// TODO Auto-generated method stub

	}

}