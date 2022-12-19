/*******************************************************************************************************
 *
 * HTTPRequestConnector.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.httprequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;

import msi.gama.extensions.messaging.GamaMailbox;
import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.extensions.messaging.MessagingSkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;
import ummisco.gama.network.common.socket.SocketService;
import ummisco.gama.network.httprequest.utils.Utils;

/**
 * The Class HTTPRequestConnector.
 */
public class HTTPRequestConnector extends Connector {

	/** The timeout. */
	public static Integer DEFAULT_TIMEOUT = 5000;

	/** The default host. */
	public static String DEFAULT_HOST = "localhost";

	/** The default port. */
	public static String DEFAULT_PORT = "80";

	/** The ss thread. */
	// MultiThreadedArduinoReceiver ssThread;

	String host;

	/** The port. */
	String port;

	/** The request. */
	//
	private HttpRequest request;

	/**
	 * Instantiates a new HTTPRequest connector.
	 *
	 * @param scope
	 *            the scope
	 */
	public HTTPRequestConnector(final IScope scope) {}

	@Override
	protected void connectToServer(final IAgent agent) throws GamaNetworkException {
		String host_tmp = this.getConfigurationParameter(SERVER_URL);
		String port_tmp = this.getConfigurationParameter(SERVER_PORT);

		host = host_tmp == null ? DEFAULT_HOST : host_tmp;
		port = port_tmp == null ? DEFAULT_PORT : port_tmp;
	}

	@Override
	protected boolean isAlive(final IAgent agent) throws GamaNetworkException {
		
		return false;
	}

	@Override
	protected void subscribeToGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		

	}

	@Override
	protected void unsubscribeGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		

	}

	@Override
	protected void releaseConnection(final IScope scope) throws GamaNetworkException {
		

	}

	@Override
	public void send(final IAgent sender, final String receiver, final GamaMessage content) {
		Object cont = content.getContents(sender.getScope());
		try {
			if (!(cont instanceof GamaList listContent)) throw GamaNetworkException.cannotSendMessage(null,
					"The content expected to be sent is well formatted, a list [method,body,headers] is expected.");
			URI uri = null;
			try {
				uri = Utils.buildURI(host, "" + port, receiver);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Builder requestBuilder = HttpRequest.newBuilder().uri(uri);
			// Management of the content. A list [method,body,headers] or [method] are expected
			String method = (String) listContent.get(0);
			String jsonBody = listContent.size() > 1 ? Jsoner.serialize(listContent.get(1)) : "";
			@SuppressWarnings ("unchecked") IMap<String, String> header =
					listContent.size() > 2 ? (IMap<String, String>) listContent.get(2) : null;

			if (header != null) { for (String key : header.keySet()) { requestBuilder.header(key, header.get(key)); } }

			request = switch (method) {
				case "GET" -> requestBuilder.GET().build();
				case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
				case "PUT" -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
				case "DELETE" -> requestBuilder.DELETE().build();
				default -> throw GamaNetworkException.cannotSendMessage(null, "Bad HTTP action");
			};

			this.sendMessage(sender, receiver, jsonBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void sendMessage(final IAgent sender, final String receiver, final String content)
			throws GamaNetworkException {

		try {

			HttpResponse<String> response =
					HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			// Manage the response of the request
			IMap<String, Object> responseMap = Utils.formatResponse(response);

			@SuppressWarnings ("unchecked") GamaMailbox<GamaMessage> mailbox =
					(GamaMailbox<GamaMessage>) sender.getAttribute(MessagingSkill.MAILBOX_ATTRIBUTE);
			if (mailbox == null) {
				mailbox = new GamaMailbox<>();
				sender.setAttribute(MessagingSkill.MAILBOX_ATTRIBUTE, mailbox);
			}

			GamaMessage msg = new GamaMessage(sender.getScope(), "HTTP", sender.getName(), responseMap);

			mailbox.add(msg);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SocketService getSocketService() { return null; }

}
