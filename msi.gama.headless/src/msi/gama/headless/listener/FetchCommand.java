/*******************************************************************************************************
 *
 * FetchCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama2 for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class FetchCommand.
 */
public class FetchCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {
		final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";

		final Object filepath = map.get("file");
		final Object access = map.get("access");
		DEBUG.OUT("stop");
		DEBUG.OUT(exp_id);
		if ("down".equals(access)) {
			File file = new File("" + filepath);
			try (FileInputStream fis = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
					BufferedReader br = new BufferedReader(isr)) {
				StringBuilder sc = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					// process the line
					// System.out.println(line);
					sc.append(line).append("\n");
				}
				return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, sc.toString(), map,
						false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
						"Unable to find the experiment or simulation", map, false);
			}
		}
		if ("up".equals(access)) {
			try (FileWriter myWriter = new FileWriter("" + filepath)) {
				final Object content = map.get("content");
				myWriter.write("" + content);
				return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "saved", map, false);
			} catch (Exception ex) {
				ex.printStackTrace();
				return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, ex.getMessage(), map, false);
			}
		}
		return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "", map, false);

	}
}
