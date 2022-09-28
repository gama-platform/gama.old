package msi.gama.headless.listener;

import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;

public class CommandResponse  extends GamaServerMessage {

	public final IMap<String, Object> commandParameters;
	
	public CommandResponse(final GamaServerMessageType t,final Object content, final IMap<String, Object> parameters) {
		super(t, content);
		this.commandParameters = parameters;
	}
	
	@Override
	public String toJson() {
		var params = commandParameters.copy(null);
		params.remove("server");
		return "{ "
				+ "\"type\": \"" + type + "\","
				+ "\"content\": " + Jsoner.serialize(content) + ","	
				+ "\"command\": " + Jsoner.serialize(params).toString() + ","
				+ "}";
	}

}
