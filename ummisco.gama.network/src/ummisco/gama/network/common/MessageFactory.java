package ummisco.gama.network.common;

import ummisco.gama.network.common.CommandMessage.CommandType;

public final class MessageFactory {
	private static final byte[] keyChain = { 3, 5, 8, 13 };
	private static final int MAX_HEADER_SIZE = 1024;
	
	public enum MessageType{
		COMMAND_MESSAGE,
		NETWORK_MESSAGE,
		PLAIN_MESSAGE
	}
	
	
	public static NetworkMessage buildNetworkMessage(final String from, final String to, final String data) {
		return new NetworkMessage(from, to, data);
	}
	
	public static NetworkMessage buildNetworkMessage(final String from,  final String data) {
		return new NetworkMessage(from,  data);
	}
	
	public static CommandMessage buildCommandMessage(final String from, final String to,final CommandType cmd, final String data) {
		return new CommandMessage(from, to, cmd, data);
	}
	
	
	public static String packMessage(final NetworkMessage msg) {
		final String mKey = new String(keyChain);
		return mKey + msg.getSender() + mKey + msg.getReceiver() + mKey + msg.getPlainContents();
	}

	public static String packMessage(final CommandMessage msg) {
		final String mKey = new String(keyChain);
		return mKey + mKey + msg.getSender() + mKey + msg.getReceiver() + mKey+ msg.getCommand().ordinal() + mKey + msg.getPlainContents();
	}
	
	public static MessageType identifyMessageType(final String data)
	{
		final String key = new String(keyChain);
		if (data.substring(0, keyChain.length*2).equals(key+key))
			return MessageType.COMMAND_MESSAGE;
		if (data.substring(0, keyChain.length).equals(key))
			return MessageType.NETWORK_MESSAGE;
		return MessageType.PLAIN_MESSAGE;
	}
	
	
	public static NetworkMessage unPackNetworkMessage(final String sender, final String data) {
		final String key = new String(keyChain);
		if (!data.substring(0, keyChain.length).equals(key))
			return new NetworkMessage(sender, data);

		final int size = MAX_HEADER_SIZE < data.length() ? MAX_HEADER_SIZE : data.length();
		final String header = data.substring(0, size);
		final String headSplit[] = header.split(key);
		final String from = headSplit[1];
		final String to = headSplit[2];
		final String content = data.substring(from.length() + to.length() + 3 * key.length());
		return new NetworkMessage(from, to, content);
	}

	public static CommandMessage unPackCommandMessage(final String sender, final String data) {
		final String key = new String(keyChain);
		if (identifyMessageType(data) != MessageType.COMMAND_MESSAGE)
			return null;

		final int size = MAX_HEADER_SIZE < data.length() ? MAX_HEADER_SIZE : data.length();
		final String header = data.substring(0, size);
		final String headSplit[] = header.split(key);
		final String from = headSplit[2];
		final String to = headSplit[3];
		final int command = Integer.valueOf(headSplit[4]).intValue();
		final String content = data.substring(from.length() + to.length()+headSplit[4].length() + 5 * key.length());
		return new CommandMessage(from, to,CommandType.values()[command], content);
	}


}
