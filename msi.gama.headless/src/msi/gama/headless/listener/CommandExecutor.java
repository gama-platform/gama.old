package msi.gama.headless.listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import msi.gama.util.IMap;

public class CommandExecutor {

	private final Map<String, SocketCommand> COMMANDS;

	public CommandExecutor() {
		final Map<String, SocketCommand> cmds = new HashMap<>();
		cmds.put("launch", new LaunchCommand());

		cmds.put("play", new PlayCommand());

		cmds.put("step", new StepCommand());

		cmds.put("stepBack", new StepBackCommand());
		cmds.put("pause", new PauseCommand());
		cmds.put("stop", new StopCommand());
		cmds.put("reload", new ReloadCommand());
		cmds.put("output", new OutputCommand());
		cmds.put("expression", new ExpressionCommand());
		cmds.put("exit", new ExitCommand());

		COMMANDS = Collections.unmodifiableMap(cmds);
	}

	public void process(final WebSocket socket, final IMap<String, Object> map) {
		final String cmd_type = map.get("type").toString();
		SocketCommand command = COMMANDS.get(cmd_type);

		if (command == null) {
			throw new IllegalArgumentException("Invalid player type: " + cmd_type);
		}

		command.execute(socket, map);
	}

}