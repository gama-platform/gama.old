package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;

public class NetworkMessage  implements ConnectorMessage {
	private static final byte[]  keyChain = {3,5,8,13};
	private static final String UNDEFINED = "undefined";
	private static final int MAX_HEADER_SIZE = 1024;
	private String from;
	private String to;
	private String content;
	private boolean isPlainMessage = false;
	
	
	public NetworkMessage(String from,String data)
	{
		this.content = data;
		this.from=from;
		this.to=UNDEFINED;
		isPlainMessage = true;
	}
	
	public NetworkMessage(String from, String to, String data)
	{
		this.from = from;
		this.to = to;
		this.content = data;
		isPlainMessage = false;
	}
	
	public String getSender()
	{
		return from;
	}
	public String getReceiver()
	{
		return to;
	}
	public boolean isPlainMessage()
	{
		return isPlainMessage;
	}
	
	public GamaMessage getContents(IScope scope)
	{
		return isPlainMessage?getPlainContent(scope):getCompositeContent(scope);
	}

	public GamaMessage getPlainContent(IScope scope)
	{
		GamaMessage message = new GamaMessage(scope, from, to, content);
		message.hasBeenReceived(scope);
		return message;
	}
	
	public GamaMessage getCompositeContent(IScope scope)
	{
		Object messageContent = StreamConverter.convertStreamToObject(scope, content);
		GamaMessage message = null;
		if(messageContent instanceof CompositeGamaMessage)
			message = (GamaMessage) messageContent;
		else
			message = new GamaMessage(scope, from, to, messageContent);
		message.hasBeenReceived(scope);
		return message;
	}
	
	public static NetworkMessage unPackMessage(String sender,String data)
	{
		String key = new String(keyChain);
		if(data.substring(0, keyChain.length-1).equals(key))
			return new NetworkMessage(sender,data);
		
		int size = MAX_HEADER_SIZE<data.length()?MAX_HEADER_SIZE:data.length();
		String header = data.substring(0,size);
		String headSplit[] = header.split(key);
		String from = headSplit[1];
		String to = headSplit[2];
		String content = data.substring(from.length()+to.length()+(3*key.length()));
		return new NetworkMessage(from,to,content);
	}
	
	public static String packMessage(NetworkMessage msg)
	{
		String mKey = new String(keyChain);
		return  mKey+msg.from+mKey+msg.to+mKey+msg.content;
	}
	
	
	
		

}
