package ummisco.gama.network.common;

public class GamaNetworkMessage extends Message {
	private String from;
	private String to;
	
	public GamaNetworkMessage(String from, String to, String content)
	{
		super(content);
		this.from=from;
		this.to = to;
	}
	
	public String getSenderName() {
		return from;
	}
	public String getReceiverName() {
		return to;
	}

}
