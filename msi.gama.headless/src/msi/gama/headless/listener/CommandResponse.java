package msi.gama.headless.listener;

import msi.gama.util.IMap;

public class CommandResponse  extends GamaServerMessage {

	public final IMap<String, Object> commandParameters;
	
	public CommandResponse(final GamaServerMessageType t,final Object content, final IMap<String, Object> parameters) {
		super(t, content);
		this.commandParameters = parameters;
	}
	
	@Override
	public String toJson() {
		return "{ "
				+ "\"type\": \"" + type + "\","
				+ "\"content\": \"" + content + "\","	
				+ "\"command\": {" + commandParameters.toString() + "},"
				+ "}";
	}

}
