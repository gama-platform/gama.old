package msi.gama.headless.listener;

import msi.gama.util.file.json.Jsonable;

public class GamaServerMessage implements Jsonable {
	
	public final GamaServerMessageType type;
	public final Object content;
	
	public GamaServerMessage(GamaServerMessageType t, Object content) {
		this.type 		= t;
		this.content 	= content;
	}

	@Override
	public String toJson() {
		return "{ "
				+ "\"type\": \"" + type + "\","
				+ "\"content\": \"" + content + "\","	
				+ "}";
	}
	
	
	


}
