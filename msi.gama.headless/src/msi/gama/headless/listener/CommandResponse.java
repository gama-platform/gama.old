package msi.gama.headless.listener;

import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;

public class CommandResponse  extends GamaServerMessage {

	public final IMap<String, Object> commandParameters;
	protected boolean isJson=false;
	
	public CommandResponse(final GamaServerMessageType t,final Object content, final IMap<String, Object> parameters, final boolean isJSON) {
		super(t, content);
		this.commandParameters = parameters;
		this.isJson=isJSON;
	}
	
	@Override
	public String toJson() {
		var params = commandParameters.copy(null);
		params.remove("server");
		return "{ "
				+ "\"type\": \"" + type + "\","
				+ "\"content\": " + ((isJson)?content:Jsoner.serialize(content)) + ","	 
				+ "\"command\": " + Jsoner.serialize(params).toString() 
				+ "}";
	}

}
