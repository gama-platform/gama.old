/*******************************************************************************************************
 *
 * msi.gama.util.file.http.Webb.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.webb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Lightweight Java HTTP-Client for calling JSON REST-Services (especially for Android).
 *
 * @author hgoebl
 */
public class Webb {
	static final String DEFAULT_USER_AGENT = "gama-platform.org/1.0";
	static final String APP_FORM = "application/x-www-form-urlencoded";
	static final String APP_JSON = "application/json";
	static final String APP_BINARY = "application/octet-stream";
	static final String TEXT_PLAIN = "text/plain";
	public static final String HDR_CONTENT_TYPE = "Content-Type";
	static final String HDR_CONTENT_ENCODING = "Content-Encoding";
	static final String HDR_ACCEPT_ENCODING = "Accept-Encoding";
	static final String HDR_ACCEPT = "Accept";
	static final String HDR_USER_AGENT = "User-Agent";
	static final String UTF8 = "utf-8";

	static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	@SuppressWarnings ("rawtypes") static final Class BYTE_ARRAY_CLASS = EMPTY_BYTE_ARRAY.getClass();
	/** Minimal number of bytes the compressed content must be smaller than uncompressed */
	static final int MIN_COMPRESSED_ADVANTAGE = 80;

	static final Map<String, Object> globalHeaders = new LinkedHashMap<>();
	static String globalBaseUri;

	static Integer connectTimeout = 10000; // 10 seconds
	static Integer readTimeout = 3 * 60000; // 5 minutes
	static int jsonIndentFactor = -1;

	Boolean followRedirects = true;
	String baseUri;
	Map<String, Object> defaultHeaders;
	SSLSocketFactory sslSocketFactory;
	HostnameVerifier hostnameVerifier;
	RetryManager retryManager;
	Proxy proxy;

	protected Webb() {}

	/**
	 * Create an instance which can be reused for multiple requests in the same Thread.
	 *
	 * @return the created instance.
	 */
	public static Webb create() {
		return new Webb();
	}

	/**
	 * See <a href=
	 * "http://docs.oracle.com/javase/7/docs/api/java/net/HttpURLConnection.html#setInstanceFollowRedirects(boolean)">
	 * </a>. <br>
	 * Use this method to set the behaviour for all requests created by this instance when receiving redirect responses.
	 * You can overwrite the setting for a single request by calling {@link Request#followRedirects(boolean)}.
	 *
	 * @param auto
	 *            <code>true</code> to automatically follow redirects (HTTP status code 3xx). Default value comes from
	 *            HttpURLConnection and should be <code>true</code>.
	 */
	public void setFollowRedirects(final boolean auto) {
		this.followRedirects = auto;
	}

	/**
	 * Set a custom {@link javax.net.ssl.SSLSocketFactory}, most likely to relax Certification checking.
	 *
	 * @param sslSocketFactory
	 *            the factory to use (see test cases for an example).
	 */
	public void setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	/**
	 * Set a custom {@link javax.net.ssl.HostnameVerifier}, most likely to relax host-name checking.
	 *
	 * @param hostnameVerifier
	 *            the verifier (see test cases for an example).
	 */
	public void setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	/**
	 * Sets a proxy object to be used for opening the connection. See {@link URL#openConnection(Proxy)}
	 *
	 * @param proxy
	 *            the proxy to be used or <tt>null</tt> for no proxy.
	 */
	public void setProxy(final Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * Set the base URI for all requests created from this instance. <br>
	 * For all requests this value is taken as a kind of prefix for the effective URI, so you can address the URIs
	 * relatively. The value takes precedence over the value set in {@link #setGlobalBaseUri(String)}.
	 *
	 * @param baseUri
	 *            the prefix for all URIs of new Requests.
	 * @see #setGlobalBaseUri(String)
	 */
	public void setBaseUri(final String baseUri) {
		this.baseUri = baseUri;
	}

	/**
	 * Returns the base URI of this instance.
	 *
	 * @return base URI
	 */
	public String getBaseUri() {
		return baseUri;
	}

	/**
	 * Registers an alternative {@link com.goebl.david.RetryManager}.
	 *
	 * @param retryManager
	 *            the new manager for deciding whether it makes sense to retry a request.
	 */
	public void setRetryManager(final RetryManager retryManager) {
		this.retryManager = retryManager;
	}

	/**
	 * Creates a <b>GET HTTP</b> request with the specified absolute or relative URI.
	 *
	 * @param pathOrUri
	 *            the URI (will be concatenated with global URI or default URI without further checking). If it starts
	 *            already with http:// or https:// this URI is taken and all base URIs are ignored.
	 * @return the created Request object (in fact it's more a builder than a real request object)
	 */
	public Request get(final String pathOrUri) {
		return new Request(this, Request.Method.GET, buildPath(pathOrUri));
	}

	/**
	 * Creates a <b>POST</b> HTTP request with the specified absolute or relative URI.
	 *
	 * @param pathOrUri
	 *            the URI (will be concatenated with global URI or default URI without further checking) If it starts
	 *            already with http:// or https:// this URI is taken and all base URIs are ignored.
	 * @return the created Request object (in fact it's more a builder than a real request object)
	 */
	public Request post(final String pathOrUri) {
		return new Request(this, Request.Method.POST, buildPath(pathOrUri));
	}

	/**
	 * Creates a <b>DELETE</b> HTTP request with the specified absolute or relative URI.
	 *
	 * @param pathOrUri
	 *            the URI (will be concatenated with global URI or default URI without further checking) If it starts
	 *            already with http:// or https:// this URI is taken and all base URIs are ignored.
	 * @return the created Request object (in fact it's more a builder than a real request object)
	 */
	public Request delete(final String pathOrUri) {
		return new Request(this, Request.Method.DELETE, buildPath(pathOrUri));
	}

	private String buildPath(final String pathOrUri) {
		if (pathOrUri == null) { throw new IllegalArgumentException("pathOrUri must not be null"); }
		if (pathOrUri.startsWith("http://") || pathOrUri.startsWith("https://")) { return pathOrUri; }
		final String myBaseUri = baseUri != null ? baseUri : globalBaseUri;
		return myBaseUri == null ? pathOrUri : myBaseUri + pathOrUri;
	}

	<T> Response<T> execute(final Request request, final Class<T> clazz) {
		Response<T> response = null;

		if (request.retryCount == 0) {
			// no retry -> just delegate to inner method
			response = _execute(request, clazz);
		} else {
			if (retryManager == null) {
				retryManager = RetryManager.DEFAULT;
			}
			for (int tries = 0; tries <= request.retryCount; ++tries) {
				try {
					response = _execute(request, clazz);
					if (tries >= request.retryCount || !retryManager.isRetryUseful(response)) {
						break;
					}
				} catch (final WebbException we) {
					// analyze: is exception recoverable?
					if (tries >= request.retryCount || !retryManager.isRecoverable(we)) { throw we; }
				}
				if (request.waitExponential) {
					retryManager.wait(tries);
				}
			}
		}
		if (response == null) {
			throw new IllegalStateException(); // should never reach this line
		}
		if (request.ensureSuccess) {
			response.ensureSuccess();
		}

		return response;
	}

	private <T> Response<T> _execute(final Request request, final Class<T> clazz) {
		final Response<T> response = new Response<>(request);

		InputStream is = null;
		boolean closeStream = true;
		HttpURLConnection connection = null;

		try {
			String uri = request.uri;
			if (request.method == Request.Method.GET && !uri.contains("?") && request.params != null
					&& !request.params.isEmpty()) {
				uri += "?" + WebbUtils.queryString(request.params);
			}
			final URL apiUrl = new URL(uri);
			if (proxy != null) {
				connection = (HttpURLConnection) apiUrl.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) apiUrl.openConnection();
			}

			prepareSslConnection(connection);
			connection.setRequestMethod(request.method.name());
			if (request.followRedirects != null) {
				connection.setInstanceFollowRedirects(request.followRedirects);
			}
			connection.setUseCaches(request.useCaches);
			setTimeouts(request, connection);
			if (request.ifModifiedSince != null) {
				connection.setIfModifiedSince(request.ifModifiedSince);
			}

			WebbUtils.addRequestProperties(connection, mergeHeaders(request.headers));
			if (clazz == JSONObject.class || clazz == JSONArray.class) {
				WebbUtils.ensureRequestProperty(connection, HDR_ACCEPT, APP_JSON);
			}

			if (request.method != Request.Method.GET && request.method != Request.Method.DELETE) {
				if (request.streamPayload) {
					WebbUtils.setContentTypeAndLengthForStreaming(connection, request, request.compress);
					connection.setDoOutput(true);
					streamBody(connection, request.payload, request.compress);
				} else {
					final byte[] requestBody = WebbUtils.getPayloadAsBytesAndSetContentType(connection, request,
							request.compress, jsonIndentFactor);

					if (requestBody != null) {
						connection.setDoOutput(true);
						writeBody(connection, requestBody);
					}
				}
			} else {
				connection.connect();
			}

			response.connection = connection;
			response.statusCode = connection.getResponseCode();
			response.responseMessage = connection.getResponseMessage();

			// get the response body (if any)
			is = response.isSuccess() ? connection.getInputStream() : connection.getErrorStream();
			is = WebbUtils.wrapStream(connection.getContentEncoding(), is);

			if (clazz == InputStream.class) {
				is = new AutoDisconnectInputStream(connection, is);
			}
			if (response.isSuccess()) {
				WebbUtils.parseResponseBody(clazz, response, is);
			} else {
				WebbUtils.parseErrorResponse(clazz, response, is);
			}
			if (clazz == InputStream.class) {
				closeStream = false;
			}

			return response;

		} catch (final WebbException e) {

			throw e;

		} catch (final Exception e) {

			throw new WebbException(e);

		} finally {
			if (closeStream) {
				if (is != null) {
					try {
						is.close();
					} catch (final Exception ignored) {}
				}
				if (connection != null) {
					try {
						connection.disconnect();
					} catch (final Exception ignored) {}
				}
			}
		}
	}

	private void setTimeouts(final Request request, final HttpURLConnection connection) {
		if (request.connectTimeout != null || connectTimeout != null) {
			connection.setConnectTimeout(request.connectTimeout != null ? request.connectTimeout : connectTimeout);
		}
		if (request.readTimeout != null || readTimeout != null) {
			connection.setReadTimeout(request.readTimeout != null ? request.readTimeout : readTimeout);
		}
	}

	private void writeBody(final HttpURLConnection connection, final byte[] body) throws IOException {
		// Android StrictMode might complain about not closing the connection:
		// "E/StrictMode﹕ A resource was acquired at attached stack trace but never released"
		// It seems like some kind of bug in special devices (e.g. 4.0.4/Sony) but does not
		// happen e.g. on 4.4.2/Moto G.
		// Closing the stream in the try block might help sometimes (it's intermittently),
		// but I don't want to deal with the IOException which can be thrown in close().
		try (OutputStream os = connection.getOutputStream();) {
			os.write(body);
			os.flush();
		}
	}

	private void streamBody(final HttpURLConnection connection, final Object body, final boolean compress)
			throws IOException {
		InputStream is;
		boolean closeStream;

		if (body instanceof File) {
			is = new FileInputStream((File) body);
			closeStream = true;
		} else {
			is = (InputStream) body;
			closeStream = false;
		}

		// "E/StrictMode﹕ A resource was acquired at attached stack trace but never released"
		// see comments about this problem in #writeBody()
		try (final OutputStream os =
				compress ? new GZIPOutputStream(connection.getOutputStream()) : connection.getOutputStream();) {
			WebbUtils.copyStream(is, os);
			os.flush();
		} finally {
			if (is != null && closeStream) {
				try {
					is.close();
				} catch (final Exception ignored) {}
			}
		}
	}

	private void prepareSslConnection(final HttpURLConnection connection) {
		if ((hostnameVerifier != null || sslSocketFactory != null) && connection instanceof HttpsURLConnection) {
			final HttpsURLConnection sslConnection = (HttpsURLConnection) connection;
			if (hostnameVerifier != null) {
				sslConnection.setHostnameVerifier(hostnameVerifier);
			}
			if (sslSocketFactory != null) {
				sslConnection.setSSLSocketFactory(sslSocketFactory);
			}
		}
	}

	Map<String, Object> mergeHeaders(final Map<String, Object> requestHeaders) {
		Map<String, Object> headers = null;
		if (!globalHeaders.isEmpty()) {
			headers = new LinkedHashMap<>();
			headers.putAll(globalHeaders);
		}
		if (defaultHeaders != null) {
			if (headers == null) {
				headers = new LinkedHashMap<>();
			}
			headers.putAll(defaultHeaders);
		}
		if (requestHeaders != null) {
			if (headers == null) {
				headers = requestHeaders;
			} else {
				headers.putAll(requestHeaders);
			}
		}
		return headers;
	}

	/**
	 * Disconnect the underlying <code>HttpURLConnection</code> on close.
	 */
	private static class AutoDisconnectInputStream extends FilterInputStream {

		/**
		 * The underlying <code>HttpURLConnection</code>.
		 */
		private final HttpURLConnection connection;

		/**
		 * Creates an <code>AutoDisconnectInputStream</code> by assigning the argument <code>in</code> to the field
		 * <code>this.in</code> so as to remember it for later use.
		 *
		 * @param connection
		 *            the underlying connection to disconnect on close.
		 * @param in
		 *            the underlying input stream, or <code>null</code> if this instance is to be created without an
		 *            underlying stream.
		 */
		protected AutoDisconnectInputStream(final HttpURLConnection connection, final InputStream in) {
			super(in);
			this.connection = connection;
		}

		@Override
		public void close() throws IOException {
			try {
				super.close();
			} finally {
				connection.disconnect();
			}
		}
	}
}
