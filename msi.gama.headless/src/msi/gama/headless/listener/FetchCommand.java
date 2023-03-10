package msi.gama.headless.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Scanner;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class FetchCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {
		final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final String socket_id = map.get("socket_id") != null ? map.get("socket_id").toString()
				: ("" + socket.hashCode());

		final Object filepath = map.get("file");
		final Object access = map.get("access");
		DEBUG.OUT("stop");
		DEBUG.OUT(exp_id);
		if ("down".equals(access)) {
			try {

				File file = new File("" + filepath);
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String sc = "";
				String line;
				while ((line = br.readLine()) != null) {
					// process the line
//				     System.out.println(line);
					sc += line + "\n";
				}
				br.close();

				return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, sc, map, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
						"Unable to find the experiment or simulation", map, false);
			}
		} else if ("up".equals(access)) {
			try {

				FileWriter myWriter = new FileWriter("" + filepath);
				final Object content = map.get("content");
				myWriter.write("" + content);
				myWriter.close();
				return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "saved", map, false);
			} catch (Exception ex) {
				ex.printStackTrace();
				return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, ex.getMessage(), map, false);
			}
		}
		return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "", map, false);

	}
}
