package ummisco.gama.network.common;

public class CommandMessage extends NetworkMessage {
	
	public enum CommandType
	{
		NEW_GROUP,
		REMOVE_GROUP
	}
	
	private CommandType command;
	
	public CommandMessage(final String from, final String to,final CommandType cmd, final String data) {
		super(from, to, data);
		this.command = cmd;
		this.isPlainMessage=true;
	}
	
	public CommandType getCommand()
	{
		return this.command;
	}
	@Override
	public boolean isCommandMessage() {
		return true;
	}
	
}
