package ummisco.gama.network.common;

import msi.gama.runtime.IScope;

public class PlainMessage extends Message {

	
	public PlainMessage(String msg)
	{
		super(msg);
	}
	
	public Object getContent(IScope scope)
	{
		return this.content;
	}
	
}
