package ummisco.gama.network.common;

import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;

abstract class Message {
	protected String content;
	private static final byte[]  keyChain = {3,5,8,13};
	private static final String UNDEFINED = "undefined";
	private static final int MAX_HEADER_SIZE = 1024;
	
	protected Message(String content) {
		this.content = content;
	}
	
	public static Message createMessage(String data)
	{
		String key = new String(keyChain);
		if(data.substring(0, keyChain.length-1).equals(key))
			return new PlainMessage(data);
		
		String header = data.substring(0,MAX_HEADER_SIZE-1);
		String headSplit[] = header.split(key);
		String from = headSplit[1];
		String to = headSplit[2];
		String content = data.substring(from.length()+to.length()+(3*key.length())-1);
		return new GamaNetworkMessage(from,to,content);
	}
	
	public static String streamMessage(GamaNetworkMessage msg)
	{
		String mKey = new String(keyChain);
		return null; // mKey+msg.from+mKey+msg.to+mKey+msg.content;
	}
	
	
	
	private Message(String from, String to, String content)
	{
		
	}
	
	
	public String getDataStream()
	{
		return content;
	}
	
	public Object getContent(IScope scope)
	{
		return StreamConverter.convertStreamToObject(scope, content);
	}
	

}
