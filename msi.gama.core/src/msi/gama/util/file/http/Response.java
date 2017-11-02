package msi.gama.util.file.http;

import java.net.HttpURLConnection;

/**
 * Holds data about the response message returning from HTTP request.
 *
 * @author hgoebl
 */
public class Response<T> {
	final Request request;

	int statusCode;
	String responseMessage;
	T body;
	Object errorBody;
	HttpURLConnection connection;

	Response(final Request request) {
		this.request = request;
	}

	void setBody(final Object body) {
		this.body = (T) body;
	}

	/**
	 * Access to the <code>Request</code> object (which will not be very useful in most cases).
	 * 
	 * @return the request object which was responsible for creating this response.
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Returns the payload of the response converted to the given type.
	 * 
	 * @return the converted payload (can be null).
	 */
	public T getBody() {
		return body;
	}

	/**
	 * Get the body which was returned in case of error (HTTP-Code &gt;= 400). <br>
	 * The type of the error body depends on following factors:
	 * <ul>
	 * <li><code>Content-Type</code> header (overrules the expected return type of the response)</li>
	 * <li>The expected type (see <code>asXyz()</code>). We try to coerce the error body to this type. In case of REST
	 * services, where often a JSONObject is the normal response body, the error body will be converted to JSONObject if
	 * possible. <code>JSONArray</code> is not expected to be the error body.</li>
	 * </ul>
	 * If converting the error body is not successful, <code>String</code> and <code>byte[]</code> is used as a
	 * fallback. You have to check the type with <code>instanceof</code> or try/catch the cast.
	 * 
	 * @return the error body converted to an object (see above) or <code>null</code> if there is no body or no error.
	 */
	public Object getErrorBody() {
		return errorBody;
	}

	/**
	 * See <a href="http://docs.oracle.com/javase/7/docs/api/java/net/HttpURLConnection.html#responseCode">
	 * HttpURLConnection.responseCode</a>
	 * 
	 * @return An int representing the three digit HTTP Status-Code.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * The first line returned by the web-server, like "HTTP/1.1 200 OK".
	 * 
	 * @return first header
	 */
	public String getStatusLine() {
		return connection.getHeaderField(null);
	}

	/**
	 * Was the request successful (returning a 2xx status code)?
	 * 
	 * @return <code>true</code> when status code is between 200 and 299, else <code>false</code>
	 */
	public boolean isSuccess() {
		return statusCode / 100 == 2; // 200, 201, 204, ...
	}

	/**
	 * Returns the text explaining the status code.
	 * 
	 * @return e.g. "Moved Permanently", "Created", ...
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Returns the MIME-type of the response body. <br>
	 * See
	 * <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getContentType()"> URLConnection.
	 * getContentType()</a>
	 *
	 * @return e.g. "application/json", "text/plain", ...
	 */
	public String getContentType() {
		return connection.getContentType();
	}

	/**
	 * Returns the date when the request was created (server-time). <br>
	 * See
	 * <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getDate()"> URLConnection.getDate()
	 * </a>
	 *
	 * @return the parsed "Date" header as millis or <code>0</code> if this header was not set.
	 */
	public long getDate() {
		return connection.getDate();
	}

	/**
	 * Returns the value of the expires header field. <br>
	 * See
	 * <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getExpiration()"> URLConnection.
	 * getExpiration()</a>
	 *
	 * @return the expiration date of the resource, or 0 if not known.
	 */
	public long getExpiration() {
		return connection.getExpiration();
	}

	/**
	 * Returns the value of the last-modified header field. <br>
	 * See
	 * <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getLastModified()"> URLConnection.
	 * getLastModified()</a>
	 *
	 * @return the date the resource was last modified, or 0 if not known.
	 */
	public long getLastModified() {
		return connection.getLastModified();
	}

	/**
	 * Returns the value of the named header field. <br>
	 * See
	 * <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getHeaderField(java.lang.String)">
	 * URLConnection.getHeaderField()</a>
	 *
	 * @param name
	 *            name of the header field
	 * @return the value of the named header field, or null
	 */
	public String getHeaderField(final String name) {
		return connection.getHeaderField(name);
	}

	/**
	 * Returns the value of the named field parsed as date (Millis since 1970). <br>
	 * See <a href=
	 * "http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getHeaderFieldDate(java.lang.String,+long)">
	 * URLConnection.getHeaderFieldDate()</a>
	 *
	 * @param field
	 *            name of the header field
	 * @param defaultValue
	 *            the default value if the field is not present or malformed
	 * @return the value of the named header field, or the given default value
	 */
	public long getHeaderFieldDate(final String field, final long defaultValue) {
		return connection.getHeaderFieldDate(field, defaultValue);
	}

	/**
	 * Returns the value of the named field parsed as a number. <br>
	 * See <a href=
	 * "http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getHeaderFieldInt(java.lang.String,+int)">
	 * URLConnection.getHeaderFieldInt()</a>
	 *
	 * @param field
	 *            name of the header field
	 * @param defaultValue
	 *            the default value if the field is not present or malformed
	 * @return the value of the named header field, or the given default value
	 */
	public int getHeaderFieldInt(final String field, final int defaultValue) {
		return connection.getHeaderFieldInt(field, defaultValue);
	}

	/**
	 * Get the "real" connection, typically to call some getters which are not provided by this Response object.
	 * 
	 * @return the connection object (many methods throw IllegalStateException depending on the internal state).
	 */
	public HttpURLConnection getConnection() {
		return connection;
	}

	/**
	 * A shortcut to check for successful status codes and throw exception in case of non-2xx status codes. <br>
	 * In many cases you will call {@link com.goebl.david.Request#ensureSuccess()} instead of this method. But there
	 * might be cases where you want to inspect the response-object first (check header values) and then have a short
	 * exit where the response-code is not suitable for further normal processing.
	 */
	public void ensureSuccess() {
		if (!isSuccess()) { throw new WebbException("Request failed: " + statusCode + " " + responseMessage, this); }
	}
}
