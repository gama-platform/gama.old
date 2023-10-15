/*******************************************************************************************************
 *
 * UploadCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.FileWriter;

import org.java_websocket.WebSocket;

import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;

/**
 * The Class UploadCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class UploadCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String filepath = map.containsKey("file") ? map.get("file").toString() : null;
		final String content = map.containsKey("content") ? map.get("content").toString() : null;

		if (filepath == null || content == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'upload', mandatory parameters are: 'file' and 'content'", map, false);

		try (FileWriter myWriter = new FileWriter(filepath)) {
			myWriter.write(content);
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest, ex.getMessage(), map, false);
		}

	}

}
