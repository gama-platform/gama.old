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

import static java.util.Map.entry;
import static msi.gama.runtime.server.ISocketCommand.BACK;
import static msi.gama.runtime.server.ISocketCommand.DOWNLOAD;
import static msi.gama.runtime.server.ISocketCommand.EVALUATE;
import static msi.gama.runtime.server.ISocketCommand.EXIT;
import static msi.gama.runtime.server.ISocketCommand.EXPRESSION;
import static msi.gama.runtime.server.ISocketCommand.LOAD;
import static msi.gama.runtime.server.ISocketCommand.PAUSE;
import static msi.gama.runtime.server.ISocketCommand.PLAY;
import static msi.gama.runtime.server.ISocketCommand.RELOAD;
import static msi.gama.runtime.server.ISocketCommand.STEP;
import static msi.gama.runtime.server.ISocketCommand.STEPBACK;
import static msi.gama.runtime.server.ISocketCommand.STOP;
import static msi.gama.runtime.server.ISocketCommand.UPLOAD;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

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

	/** The Constant DEFAULT_COMMANDS. */
	private static Map<String, ISocketCommand> DEFAULT_COMMANDS = null;

	/** The commands. */
	protected final Map<String, ISocketCommand> commands;

	/** The command queue. */
	protected volatile LinkedBlockingQueue<Entry<WebSocket, IMap<String, Object>>> commandQueue;

	/** The command execution thread. */
	protected final Thread commandExecutionThread = new Thread(() -> {
		while (true) {
			Entry<WebSocket, IMap<String, Object>> cmd;
			try {
				cmd = commandQueue.take();
				process(cmd.getKey(), cmd.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		commands = GAMA.getGui().getServerCommands();
		commandQueue = new LinkedBlockingQueue<>();
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
		commandQueue.offer(new AbstractMap.SimpleEntry<>(socket, map));
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

	/**
	 * Gets the default commands.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the default commands
	 * @date 15 oct. 2023
	 */
	public static Map<String, ISocketCommand> getDefaultCommands() {
		if (DEFAULT_COMMANDS == null) {

			DEFAULT_COMMANDS = Map.ofEntries(entry(LOAD, DefaultServerCommands::LOAD),
					entry(PLAY, DefaultServerCommands::PLAY), entry(PAUSE, DefaultServerCommands::PAUSE),
					entry(STEP, DefaultServerCommands::STEP), entry(BACK, DefaultServerCommands::BACK),
					entry(STEPBACK, DefaultServerCommands::BACK), entry(STOP, DefaultServerCommands::STOP),
					entry(RELOAD, DefaultServerCommands::RELOAD), entry(EXPRESSION, DefaultServerCommands::EVAL),
					entry(EVALUATE, DefaultServerCommands::EVAL), entry(EXIT, DefaultServerCommands::EXIT),
					entry(DOWNLOAD, DefaultServerCommands::DOWNLOAD), entry(UPLOAD, DefaultServerCommands::UPLOAD));

		}
		return DEFAULT_COMMANDS;
	}

}