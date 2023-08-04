package msi.gama.headless.listener;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.java_websocket.WebSocket;
import org.java_websocket.enums.ReadyState;

import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;

public class CommandExecutor {

	protected final Map<String, ISocketCommand> COMMANDS;

	protected volatile Queue<Entry<WebSocket, IMap<String, Object>>> commandQueue;
		
	protected final Thread commandExecutionThread = new Thread(() -> {
		while (true) {
			while(!commandQueue.isEmpty()) {
				var cmd = commandQueue.poll();
				process(cmd.getKey(), cmd.getValue());
			}
		}
	});
	
	
	public CommandExecutor() {
		final Map<String, ISocketCommand> cmds = new HashMap<>();
		cmds.put("load", new LoadCommand());

		cmds.put("play", new PlayCommand());

		cmds.put("step", new StepCommand());

		cmds.put("stepBack", new StepBackCommand());
		cmds.put("pause", new PauseCommand());
		cmds.put("stop", new StopCommand());
		cmds.put("reload", new ReloadCommand());
		cmds.put("expression", new ExpressionCommand());
		cmds.put("exit", new ExitCommand());
		cmds.put("fetch", new FetchCommand());

		COMMANDS = Collections.unmodifiableMap(cmds);
		
		commandQueue = new LinkedList<Map.Entry<WebSocket,IMap<String,Object>>>();
		commandExecutionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		commandExecutionThread.start();
	}

	public void pushCommand(final WebSocket socket, final IMap<String, Object> map) {
		commandQueue.add(new AbstractMap.SimpleEntry<WebSocket, IMap<String, Object>>(socket, map));
	}
	
	protected void process(final WebSocket socket, final IMap<String, Object> map) {
		final String cmd_type = map.get("type").toString();
		ISocketCommand command = COMMANDS.get(cmd_type);

		if (command == null) {
			throw new IllegalArgumentException("Invalid command type: " + cmd_type);
		}

		// Executes the command in a separate thread so the executor can 
		// continue with the next one without waiting for it to finish
		new Thread(() -> {
			var res = command.execute(socket, map);
			if(res!=null) {			
				if(socket.getReadyState().equals(ReadyState.OPEN)) {
					socket.send(Jsoner.serialize(res));										
				}
			}
		}).start();
	}


	
}