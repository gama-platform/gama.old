package msi.gama.headless.listener;

import java.io.FileWriter;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;

public class UploadCommand implements ISocketCommand {

	

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String filepath 	= map.containsKey("file") ? map.get("file").toString() : null;
		final String content	= map.containsKey("content") ? map.get("content").toString() : null;

		if (filepath == null || content == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'upload', mandatory parameters are: 'file' and 'content'", map, false);
		}
		
		try (FileWriter myWriter = new FileWriter(filepath)) {
			myWriter.write("" + content);
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "", map, false);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, ex.getMessage(), map, false);
		}
	
	}
	
}
