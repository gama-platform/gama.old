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
		this.contents = StreamConverter.convertObjectToStream(scope, (message.getContents(scope)));
		this.emissionTimeStamp = message.getEmissionTimestamp();
		this.setUnread(true);
		deserializeContent=null;
	}
	
	private CompositeGamaMessage(IScope scope, Object sender, Object receivers, Object content,Object deserializeContent,int timeStamp) {
		super(scope, sender, receivers, content);
		this.emissionTimeStamp = timeStamp;
		this.setUnread(true);
		this.deserializeContent = deserializeContent;
	}
	
	@Override
	public Object getContents(IScope scope) {
		this.setUnread(false);
		if(deserializeContent == null)
			deserializeContent = StreamConverter.convertStreamToObject(scope, (String)contents);//StreamConverter.convertStreamToObject(scope, (String)(super.getContents(scope)));
		return deserializeContent; 
	}
}
