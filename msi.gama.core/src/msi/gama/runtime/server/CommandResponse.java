/*******************************************************************************************************
 *
 * CommandResponse.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;

/**
 * The Class CommandResponse.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class CommandResponse extends GamaServerMessage {

	/** The command parameters. */
	public final IMap<String, Object> commandParameters;

	/** The is json. */
	protected boolean isJson = false;

	/**
	 * Instantiates a new command response.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param parameters
	 *            the parameters
	 * @param isJSON
	 *            the is JSON
	 * @date 15 oct. 2023
	 */
	public CommandResponse(final GamaServerMessage.Type t, final Object content, final IMap<String, Object> parameters,
			final boolean isJSON) {
		super(t, content);
		this.commandParameters = parameters;
		this.isJson = isJSON;
	}

	@Override
	public String serializeToJson() {
		var params = commandParameters.copy(null);
		params.remove("server");
		return "{ " + "\"type\": \"" + type + "\"," + "\"content\": " + (isJson ? content : Jsoner.serialize(content))
				+ "," + "\"command\": " + Jsoner.serialize(params) + "}";
	}

}
