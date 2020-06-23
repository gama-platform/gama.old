/*******************************************************************************************************
 *
 * msi.gama.util.file.http.Request.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.webb;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder for an HTTP request. <br>
 * You can some "real-life" usage examples at
 * <a href="https://github.com/hgoebl/DavidWebb">github.com/hgoebl/DavidWebb</a>. <br>
 *
 * @author hgoebl
 */
public class Request {
	public enum Method {
		GET, POST, PUT, DELETE
	}

	private final Webb webb;
	final Method method;
	final String uri;

	Map<String, Object> params;
	boolean multipleValues;
	Map<String, Object> headers;
	Object payload;
	boolean streamPayload;
	boolean useCaches;
	Integer connectTimeout;
	Integer readTimeout;
	Long ifModifiedSince;
	Boolean followRedirects;
	boolean ensureSuccess;
	boolean compress;
	int retryCount;
	boolean waitExponential;

	Request(final Webb webb, final Method method, final String uri) {
		this.webb = webb;
		this.method = method;
		this.uri = uri;
		this.followRedirects = webb.followRedirects;
	}

	/**
	 * Get the URI of this request.
	 *
	 * @return URI
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Set (or overwrite) a HTTP header value. <br>
	 * Setting a header this way has the highest precedence and overrides a header value set on a {@link Webb} instance
	 * ({@link Webb#setDefaultHeader(String, Object)}) or a global header ({@link Webb#setGlobalHeader(String, Object)}
	 * ). <br>
	 * Using <code>null</code> or empty String is not allowed for name and value.
	 *
	 * @param name
	 *            name of the header (HTTP-headers are not case-sensitive, but if you want to override your own headers,
	 *            you have to use identical strings for the name. There are some frequently used header names as
	 *            constants in {@link Webb}, see HDR_xxx.
	 * @param value
	 *            the value for the header. Following types are supported, all other types use <code>toString</code> of
	 *            the given object:
	 *            <ul>
	 *            <li>{@link java.util.Date} is converted to RFC1123 compliant String</li>
	 *            <li>{@link java.util.Calendar} is converted to RFC1123 compliant String</li>
	 *            </ul>
	 * @return <code>this</code> for method chaining (fluent API)
	 */
	public Request header(final String name, final Object value) {
		if (headers == null) {
			headers = new LinkedHashMap<>();
		}
		headers.put(name, value);
		return this;
	}

	/**
	 * Set the payload for the request. <br>
	 * Using this method together with {@link #param(String, Object)} has the effect of <code>body</code> being ignored
	 * without notice. The method can be called more than once: the value will be stored and converted to bytes later.
	 * <br>
	 * Following types are supported for the body:
	 * <ul>
	 * <li><code>null</code> clears the body</li>
	 * <li>{@link org.json.JSONObject}, HTTP header 'Content-Type' will be set to JSON, if not set</li>
	 * <li>{@link org.json.JSONArray}, HTTP header 'Content-Type' will be set to JSON, if not set</li>
	 * <li>{@link java.lang.String}, HTTP header 'Content-Type' will be set to TEXT, if not set; Text will be converted
	 * to UTF-8 bytes.</li>
	 * <li><code>byte[]</code> the easiest way for DavidWebb - it's just passed through. HTTP header 'Content-Type' will
	 * be set to BINARY, if not set.</li>
	 * <li>{@link java.io.File}, HTTP header 'Content-Type' will be set to BINARY, if not set; The file gets streamed to
	 * the web-server and 'Content-Length' will be set to the number of bytes of the file. There is absolutely no
	 * conversion done. So if you want to upload e.g. a text-file and convert it to another encoding than stored on
	 * disk, you have to do it by yourself.</li>
	 * <li>{@link java.io.InputStream}, HTTP header 'Content-Type' will be set to BINARY, if not set; Similar to
	 * <code>File</code>. Content-Length cannot be set (which has some drawbacks compared to knowing the size of the
	 * body in advance).<br>
	 * <strong>You have to care for closing the stream!</strong></li>
	 * </ul>
	 *
	 * @param body
	 *            the payload
	 * @return <code>this</code> for method chaining (fluent API)
	 */
	public Request body(final Object body) {
		if (method == Method.GET || method == Method.DELETE) {
			throw new IllegalStateException("body not allowed for request method " + method);
		}
		this.payload = body;
		this.streamPayload = body instanceof File || body instanceof InputStream;
		return this;
	}

	/**
	 * See <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setConnectTimeout(int)">
	 * URLConnection.setConnectTimeout</a>
	 *
	 * @param connectTimeout
	 *            sets a specified timeout value, in milliseconds. <code>0</code> means infinite timeout.
	 * @return <code>this</code> for method chaining (fluent API)
	 */
	public Request connectTimeout(final int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	/**
	 * See <a href="http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setReadTimeout(int)"> </a>
	 *
	 * @param readTimeout
	 *            Sets the read timeout to a specified timeout, in milliseconds. <code>0</code> means infinite timeout.
	 * @return <code>this</code> for method chaining (fluent API)
	 */
	public Request readTimeout(final int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	/**
	 * By calling this method, the HTTP status code is checked and a <code>WebbException</code> is thrown if the status
	 * code is not something like 2xx.<br>
	 * <br>
	 * Be careful! If you request resources e.g. with {@link #ifModifiedSince(long)}, an exception will also be thrown
	 * in the positive case of <code>304 Not Modified</code>.
	 *
	 * @return <code>this</code> for method chaining (fluent API)
	 */
	public Request ensureSuccess() {
		this.ensureSuccess = true;
		return this;
	}

	/**
	 * Set the number of retries after the first request failed. <br>
	 * When `waitExponential` is set, then there will be {@link Thread#sleep(long)} between the retries. If the thread
	 * is interrupted, there will be an `InterruptedException` in the thrown `WebbException`. You can check this with
	 * {@link WebbException#getCause()}. The `interrupted` flag will be set to true in this case.
	 *
	 * @param retryCount
	 *            This parameter holds the number of retries that will be made AFTER the initial send in the event of a
	 *            error. If an error occurs on the last attempt an exception will be raised.<br>
	 *            Values &gt; 10 are ignored (we're not gatling)
	 * @param waitExponential
	 *            sleep during retry attempts (exponential backoff). For retry-counts more than 3, <tt>true</tt> is
	 *            mandatory.
	 * @return <code>this</code> for method chaining (fluent API)
	 */
	public Request retry(final int rc, final boolean waitExponential) {
		int retryCount = rc;
		if (retryCount < 0) {
			retryCount = 0;
		}
		if (retryCount > 10) {
			retryCount = 10;
		}
		if (retryCount > 3 && !waitExponential) {
			throw new IllegalArgumentException("retries > 3 only valid with wait");
		}
		this.retryCount = retryCount;
		this.waitExponential = waitExponential;
		return this;
	}

	/**
	 * Execute the request and expect the result to be convertible to <code>InputStream</code>.
	 *
	 * @return the created <code>Response</code> object carrying the payload from the server as <code>InputStream</code>
	 */
	public Response<InputStream> asStream() {
		return webb.execute(this, InputStream.class);
	}

	/**
	 * Execute the request and expect no result payload (only status-code and headers).
	 *
	 * @return the created <code>Response</code> object where no payload is expected or simply will be ignored.
	 */
	public Response<Void> asVoid() {
		return webb.execute(this, Void.class);
	}

}
