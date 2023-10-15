/*******************************************************************************************************
 *
 * CommandExecutor.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.java_websocket.WebSocket;
import org.java_websocket.enums.ReadyState;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.types.Types;

/**
 * The Class CommandExecutor.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class CommandExecutor {

	/** The commands. */
	protected final Map<String, ISocketCommand> commands;

	/** The command queue. */
	protected volatile Queue<Entry<WebSocket, IMap<String, Object>>> commandQueue;

	/** The command execution thread. */
	protected final Thread commandExecutionThread = new Thread(() -> {
		while (true) {
			while (!commandQueue.isEmpty()) {
				var cmd = commandQueue.poll();
				process(cmd.getKey(), cmd.getValue());
			}
		}
	});

	/**
	 * Instantiates a new command executor.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 15 oct. 2023
	 */

	public CommandExecutor() {

		commands = GAMA.getApplication().getServerCommands();

		commandQueue = new LinkedList<>();
		commandExecutionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		commandExecutionThread.start();
	}

	/**
	 * Push command.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @date 15 oct. 2023
	 */
	public void pushCommand(final WebSocket socket, final IMap<String, Object> map) {
		commandQueue.add(new AbstractMap.SimpleEntry<>(socket, map));
	}

	/**
	 * Process.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @date 15 oct. 2023
	 */
	protected void process(final WebSocket socket, final IMap<String, Object> map) {
		final String cmd_type = map.get("type").toString();
		ISocketCommand command = commands.get(cmd_type);

		if (command == null) throw new IllegalArgumentException("Invalid command type: " + cmd_type);

		// Executes the command in a separate thread so the executor can
		// continue with the next one without waiting for it to finish
		new Thread(() -> {
			var res = command.execute(socket, map);
			if (res != null && ReadyState.OPEN.equals(socket.getReadyState())) { socket.send(Jsoner.serialize(res)); }
		}).start();
	}

	/**
	 * Checks that the parameters follow the proper format asked.
	 *
	 * @param parameters
	 * @param commandMap
	 * @return If the parameters are properly formatted or null, returns null. Else returns a CommandResponse with a
	 *         more precise error message.
	 */
	public static CommandResponse checkLoadParameters(final GamaJsonList parameters,
			final IMap<String, Object> commandMap) {
		if (parameters != null) {
			int i = 1;
			for (var param : parameters.listValue(null, Types.MAP, false)) {
				@SuppressWarnings ("unchecked") IMap<String, Object> m = (IMap<String, Object>) param;
				// field "type" is optional, "name" and "value" are mandatory
				var name = m.get("name");
				var value = m.get("value");
				if (name == null) return new CommandResponse(
						GamaServerMessage.Type.MalformedRequest, "Parameter number " + i
								+ " is missing its `name` field. Parameter received: " + Jsoner.serialize(m),
						commandMap, false);
				if (value == null) return new CommandResponse(
						GamaServerMessage.Type.MalformedRequest, "Parameter number " + i
								+ " is missing its `value` field. Parameter received: " + Jsoner.serialize(m),
						commandMap, false);
				i++;
			}
		}
		return null;
	}

}