/*******************************************************************************************************
 *
 * Utils.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.httprequest.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.ParseException;
import msi.gaml.types.Types;

/**
 * The Class Utils.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
public class Utils {

	/**
	 * Builds the URI.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param url
	 *            the url
	 * @return the uri
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @date 29 oct. 2023
	 */
	public static URI buildURI(final String host, final String port, final String url) throws URISyntaxException {
		String uri = "";
		String local_port = port != null ? ":" + port : "";

		if (host.startsWith("http://") || host.startsWith("https://")) {
			uri = host + local_port + url;
		} else {
			uri = "http://" + host + local_port + url;
		}

		return new URI(uri);// URLEncoder.encode(uri, StandardCharsets.UTF_8));
	}

	/**
	 * Parses the BODY.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param body
	 *            the body
	 * @return the i list
	 * @date 29 oct. 2023
	 */
	public static IList parseBODY(final IScope scope, final String body) {
		// TODO Transform lee boy en map/list si response en JSON

		return null;
	}

	/**
	 * Format response.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param response
	 *            the response
	 * @return the i map
	 * @date 29 oct. 2023
	 */
	public static IMap<String, Object> formatResponse(final HttpResponse<String> response) {
		IMap<String, Object> responseMap = null;

		try {
			responseMap = GamaMapFactory.create();
			responseMap.put("CODE", response.statusCode());

			IMap<String, List<String>> mapHeaders =
					GamaMapFactory.wrap(Types.STRING, Types.STRING, false, response.headers().map());
			responseMap.put("HEADERS", mapHeaders);

			Object jsonBody = "";
			if (!"".equals(response.body())) {
				List<String> contentType = mapHeaders.get("content-type");
				if (contentType != null) {
					if (contentType.stream().anyMatch(e -> e.contains("json"))) {
						jsonBody = Json.getNew().parse(response.body());
					} else {
						jsonBody = response.body();
					}
				}
			}
			responseMap.put("BODY", jsonBody);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return responseMap;
	}

}
