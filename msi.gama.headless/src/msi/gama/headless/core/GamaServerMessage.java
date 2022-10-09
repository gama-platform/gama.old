package msi.gama.headless.core;

import msi.gama.util.file.json.Jsonable;
import msi.gama.util.file.json.Jsoner;

public class GamaServerMessage implements Jsonable {
	
	public final GamaServerMessageType type;
	public final Object content;
	
	/**
	 * The exp_id in case the message is linked to a running experiment
	 */
	public final String exp_id;
	
	public GamaServerMessage(GamaServerMessageType t, Object content, String exp_id) {
		this.type 		= t;
		this.content 	= content;
		this.exp_id		= exp_id;
	}
	
	public GamaServerMessage(GamaServerMessageType t, Object content) {
		this(t, content, null);
	}

	@Override
	public String toJson() {
		return "{ "
				+ "\"type\": \"" + type + "\","
				+ "\"content\": " + Jsoner.serialize(content) 
				+ (exp_id != null ? (",\"exp_id\":\"" + exp_id +"\"") : "")
				+ "}";
	}
	
	
	


}
