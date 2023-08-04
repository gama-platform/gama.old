package msi.gama.headless.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;

public class DownloadCommand implements ISocketCommand {
	

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String filepath = map.containsKey("file") ? map.get("file").toString() : null;

		if (filepath == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'upload', mandatory parameter is: 'file'", map, false);
		}
		
		try (	FileInputStream fis 	= new FileInputStream(new File(filepath));
				InputStreamReader isr 	= new InputStreamReader(fis, "UTF-8");
				BufferedReader br 		= new BufferedReader(isr)) {
			
			StringBuilder sc = new StringBuilder();
			String line;
			//read all the lines
			while ((line = br.readLine()) != null) {
				sc.append(line).append("\n");
			}
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, sc.toString(), map,
					false);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		}
	}

}
