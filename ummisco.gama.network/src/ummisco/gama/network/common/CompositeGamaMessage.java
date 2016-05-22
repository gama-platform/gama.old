package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.serializer.factory.StreamConverter;

public class CompositeGamaMessage extends GamaMessage {
	protected Object deserializeContent;
	
	public CompositeGamaMessage(IScope scope, Object sender, Object receivers, Object content)
			throws GamaRuntimeException {
		super(scope, sender, receivers, content);
		this.setUnread(true);
		deserializeContent=null;
	}
	
	public CompositeGamaMessage(IScope scope,GamaMessage message)
	{
		super(scope,message.getSender(),message.getReceivers(),message.getContents(scope));
		this.contents = StreamConverter.convertObjectToStream(scope, (String)(message.getContents(scope)));
		this.emissionTimeStamp = message.getEmissionTimestamp();
		this.setUnread(true);
		deserializeContent="";
	}
	
	private CompositeGamaMessage(IScope scope, Object sender, Object receivers, Object content,Object deserializeContent,int timeStamp) {
		super(scope, sender, receivers, content);
		this.emissionTimeStamp = timeStamp;
		this.setUnread(true);
		this.deserializeContent = deserializeContent;
	}
	
	@Override
	public Object getContents(IScope scope) {
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " );
		this.setUnread(false);
		if(deserializeContent == null)
			deserializeContent = StreamConverter.convertStreamToObject(scope, (String)(super.getContents(scope)));
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + deserializeContent+"\n\n\n\n\n\n\n"+deserializeContent);
		return deserializeContent; 
	}
}
