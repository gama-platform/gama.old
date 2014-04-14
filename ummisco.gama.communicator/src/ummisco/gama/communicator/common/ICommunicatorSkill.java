/*********************************************************************************************
 * 
 *
 * 'ICommunicatorSkill.java', in plugin 'ummisco.gama.communicator', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.communicator.common;

public interface ICommunicatorSkill {
	public static String CONNECT_TOPIC = "connectMessenger";
	
	public static String BOX_NAME = "to";
	public static String SERVER_URL = "at";
	
	public static String TOPIC = "topic";
	
	public static String WITHNAME = "withName";
	public static String TO = "to";
	public static String FROM = "from";
	public static String NET_AGENT_NAME = "netAgtName";
	public static String CONTENT = "content";
	
	public static String SENDER = "sender";
	public static String DEST = "dest";
	
	
	public static String BROADCAST = "all";
	
	
	///// SKILL NETWORK
	public static String NETWORK_SKILL = "network";
	public static String SEND_MESSAGE = "sendMessage";
	public static String FETCH_MESSAGE = "fetchMessage";
	public static String EMPTY_MESSAGE_BOX="emptyMessageBox";
	
	
	
	
	
	
	
	
}
