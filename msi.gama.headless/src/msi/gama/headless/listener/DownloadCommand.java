/*******************************************************************************************************
 *
 * DownloadCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.java_websocket.WebSocket;

import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;

/**
 * The Class DownloadCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class DownloadCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String filepath = map.containsKey(FILE) ? map.get(FILE).toString() : null;

		if (filepath == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'upload', mandatory parameter is: 'file'", map, false);

		try (FileInputStream fis = new FileInputStream(new File(filepath));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr)) {

			StringBuilder sc = new StringBuilder();
			String line;
			// read all the lines
			while ((line = br.readLine()) != null) { sc.append(line).append("\n"); }
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, sc.toString(), map, false);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		}
	}

}
