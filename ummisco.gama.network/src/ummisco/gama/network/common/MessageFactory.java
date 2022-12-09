/*******************************************************************************************************
 *
 * MessageFactory.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.common;

import ummisco.gama.network.common.CommandMessage.CommandType;

/**
 * A factory for creating Message objects.
 */
public final class MessageFactory {
	
	/** The Constant keyChain. */
	private static final byte[] keyChain = { 3, 5, 8, 13 };
	
	/** The Constant MAX_HEADER_SIZE. */
	private static final int MAX_HEADER_SIZE = 1024;
	
	/**
	 * The Enum MessageType.
	 */
	public enum MessageType{
		
		/** The command message. */
		COMMAND_MESSAGE,
		
		/** The network message. */
		NETWORK_MESSAGE,
		
		/** The plain message. */
		PLAIN_MESSAGE
	}
	
	
	/**
	 * Builds the network message.
	 *
	 * @param from the from
	 * @param to the to
	 * @param data the data
	 * @return the network message
	 */
	public static NetworkMessage buildNetworkMessage(final String from, final String to, final String data) {
		return new NetworkMessage(from, to, data);
	}
	
	/**
	 * Builds the network message.
	 *
	 * @param from the from
	 * @param data the data
	 * @return the network message
	 */
	public static NetworkMessage buildNetworkMessage(final String from,  final String data) {
		return new NetworkMessage(from,  data);
	}
	
	/**
	 * Builds the command message.
	 *
	 * @param from the from
	 * @param to the to
	 * @param cmd the cmd
	 * @param data the data
	 * @return the command message
	 */
	public static CommandMessage buildCommandMessage(final String from, final String to,final CommandType cmd, final String data) {
		return new CommandMessage(from, to, cmd, data);
	}
	
	
	/**
	 * Pack message.
	 *
	 * @param msg the msg
	 * @return the string
	 */
	public static String packMessage(final NetworkMessage msg) {
		final String mKey = new String(keyChain);
		return mKey + msg.getSender() + mKey + msg.getReceiver() + mKey + msg.getPlainContents();
	}

	/**
	 * Pack message.
	 *
	 * @param msg the msg
	 * @return the string
	 */
	public static String packMessage(final CommandMessage msg) {
		final String mKey = new String(keyChain);
		return mKey + mKey + msg.getSender() + mKey + msg.getReceiver() + mKey+ msg.getCommand().ordinal() + mKey + msg.getPlainContents();
	}
	
	/**
	 * Identify message type.
	 *
	 * @param data the data
	 * @return the message type
	 */
	public static MessageType identifyMessageType(final String data)
	{
		final String key = new String(keyChain);
		if (data != null) {
 			if(data.length() >= keyChain.length*2 && data.substring(0, keyChain.length*2).equals(key+key))
				return MessageType.COMMAND_MESSAGE;
			if (data.length() >= keyChain.length && data.substring(0, keyChain.length).equals(key))
				return MessageType.NETWORK_MESSAGE;			
		}
		return MessageType.PLAIN_MESSAGE;
	}
	
	
	/**
	 * Un pack network message.
	 *
	 * @param reciever the reciever
	 * @param data the data
	 * @return the network message
	 */
	public static NetworkMessage unPackNetworkMessage(final String sender, final String reciever, final String data) {
		final String key = new String(keyChain);
		MessageType ctype = identifyMessageType(data);
		
		if (ctype == MessageType.COMMAND_MESSAGE)
			return null;
		if (ctype==MessageType.PLAIN_MESSAGE)
			return new NetworkMessage(sender, reciever, data, true);

		final int size = MAX_HEADER_SIZE < data.length() ? MAX_HEADER_SIZE : data.length();
		final String header = data.substring(0, size);
		final String headSplit[] = header.split(key);
		final String from = headSplit[1];
		final String to = headSplit[2];
		final String content = data.substring(from.length() + to.length() + 3 * key.length());
		return new NetworkMessage(from, to, content);
	}

	/**
	 * Un pack command message.
	 *
	 * @param sender the sender
	 * @param data the data
	 * @return the command message
	 */
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
	
	
	/**
	 * Unpack receiver name.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String unpackReceiverName(final String data)
	{
		final String key = new String(keyChain);
		String start  ="";
		MessageType ctype = identifyMessageType(data);
		
		if(ctype == MessageType.COMMAND_MESSAGE)
			start = key + key;
		
		if (ctype == MessageType.NETWORK_MESSAGE)
			start = key ;
		
		if (ctype==MessageType.PLAIN_MESSAGE)
			return NetworkMessage.UNDEFINED;
		
		String sbcontent = data.substring(start.length());
		return sbcontent.split(key)[1];


	}


}
