/*
 * SVGUniverse.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on February 18, 2004, 11:43 PM
 */

package msi.gama.ext.svgsalamander;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Many SVG files can be loaded at one time. These files will quite likely need to reference one another. The SVG
 * universe provides a container for all these files and the means for them to relate to each other.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGUniverse implements Serializable {
	private static final SVGUniverse svgUniverse = new SVGUniverse();

	public static SVGUniverse getInstance() {
		return svgUniverse;
	}

	/**
	 * Maps document URIs to their loaded SVG diagrams. Note that URIs for documents loaded from URLs will reflect their
	 * URLs and URIs for documents initiated from streams will have the scheme <i>svgSalamander</i>.
	 */
	final HashMap<URI, SVGRoot> loadedDocs = new HashMap<>();

	// final HashMap loadedFonts = new HashMap();

	// final HashMap loadedImages = new HashMap();

	public static final String INPUTSTREAM_SCHEME = "svgSalamander";

	// Cache reader for efficiency
	XMLReader cachedReader;

	/**
	 * Release all loaded SVG document from memory
	 */
	public void clear() {
		loadedDocs.clear();
	}

	/**
	 * Returns the element of the document at the given URI. If the document is not already loaded, it will be.
	 */

	public SVGRoot getRoot(final URI xmlBase) {
		return getRoot(xmlBase, true);
	}

	/**
	 * Returns the diagram that has been loaded from this root. If diagram is not already loaded, returns null.
	 */
	public SVGRoot getRoot(final URI xmlBase, final boolean loadIfAbsent) {
		if (xmlBase == null) return null;

		SVGRoot dia = loadedDocs.get(xmlBase);
		if (dia != null || !loadIfAbsent) return dia;

		// Load missing diagram
		try {
			URL url;
			if ("jar".equals(xmlBase.getScheme()) && xmlBase.getPath() != null && !xmlBase.getPath().contains("!/")) {
				// Workaround for resources stored in jars loaded by Webstart.
				// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6753651
				url = SVGUniverse.class.getResource("xmlBase.getPath()");
			} else {
				url = xmlBase.toURL();
			}

			loadSVG(url, false);
			dia = loadedDocs.get(xmlBase);
			return dia;
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Wraps input stream in a BufferedInputStream. If it is detected that this input stream is GZIPped, also wraps in a
	 * GZIPInputStream for inflation.
	 *
	 * @param is
	 *            Raw input stream
	 * @return Uncompressed stream of SVG data
	 * @throws java.io.IOException
	 */
	private InputStream createDocumentInputStream(final InputStream is) throws IOException {
		final BufferedInputStream bin = new BufferedInputStream(is);
		bin.mark(2);
		final int b0 = bin.read();
		final int b1 = bin.read();
		bin.reset();

		// Check for gzip magic number
		if ((b1 << 8 | b0) == GZIPInputStream.GZIP_MAGIC)
			return new GZIPInputStream(bin);
		else
			// Plain text
			return bin;
	}

	public URI loadSVG(final URL docRoot) {
		return loadSVG(docRoot, false);
	}

	/**
	 * Loads an SVG file and all the files it references from the URL provided. If a referenced file already exists in
	 * the SVG universe, it is not reloaded.
	 *
	 * @param docRoot
	 *            - URL to the location where this SVG file can be found.
	 * @param forceLoad
	 *            - if true, ignore cached diagram and reload
	 * @return - The URI that refers to the loaded document
	 */
	public URI loadSVG(final URL docRoot, final boolean forceLoad) {
		try {
			final URI uri = new URI(docRoot.toString());
			if (loadedDocs.containsKey(uri) && !forceLoad) return uri;
			final InputStream is = docRoot.openStream();
			return loadSVG(uri, new InputSource(createDocumentInputStream(is)));
		} catch (final Throwable t) {
			t.printStackTrace();
		}

		return null;
	}

	public URI loadSVG(final InputStream is, final String name) throws IOException {
		return loadSVG(is, name, false);
	}

	public URI loadSVG(final InputStream is, final String name, final boolean forceLoad) throws IOException {
		final URI uri = getStreamBuiltURI(name);
		if (uri == null) return null;
		if (loadedDocs.containsKey(uri) && !forceLoad) return uri;

		return loadSVG(uri, new InputSource(createDocumentInputStream(is)));
	}

	public URI loadSVG(final Reader reader, final String name) {
		return loadSVG(reader, name, false);
	}

	/**
	 * This routine allows you to create SVG documents from data streams that may not necessarily have a URL to load
	 * from. Since every SVG document must be identified by a unique URL, Salamander provides a method to fake this for
	 * streams by defining it's own protocol - svgSalamander - for SVG documents without a formal URL.
	 *
	 * @param reader
	 *            - A stream containing a valid SVG document
	 * @param name
	 *            -
	 *            <p>
	 *            A unique name for this document. It will be used to construct a unique URI to refer to this document
	 *            and perform resolution with relative URIs within this document.
	 *            </p>
	 *            <p>
	 *            For example, a name of "/myScene" will produce the URI svgSalamander:/myScene. "/maps/canada/toronto"
	 *            will produce svgSalamander:/maps/canada/toronto. If this second document then contained the href
	 *            "../uk/london", it would resolve by default to svgSalamander:/maps/uk/london. That is, SVG Salamander
	 *            defines the URI scheme svgSalamander for it's own internal use and uses it for uniquely identfying
	 *            documents loaded by stream.
	 *            </p>
	 *            <p>
	 *            If you need to link to documents outside of this scheme, you can either supply full hrefs (eg,
	 *            href="url(http://www.kitfox.com/index.html)") or put the xml:base attribute in a tag to change the
	 *            defaultbase URIs are resolved against
	 *            </p>
	 *            <p>
	 *            If a name does not start with the character '/', it will be automatically prefixed to it.
	 *            </p>
	 * @param forceLoad
	 *            - if true, ignore cached diagram and reload
	 *
	 * @return - The URI that refers to the loaded document
	 */
	public URI loadSVG(final Reader reader, final String name, final boolean forceLoad) {
		// System.err.println(url.toString());
		// Synthesize URI for this stream
		final URI uri = getStreamBuiltURI(name);
		if (uri == null) return null;
		if (loadedDocs.containsKey(uri) && !forceLoad) return uri;
		return loadSVG(uri, new InputSource(reader));
	}

	/**
	 * Synthesize a URI for an SVGDiagram constructed from a stream.
	 *
	 * @param name
	 *            - Name given the document constructed from a stream.
	 */
	public URI getStreamBuiltURI(final String n) {
		String name = n;
		if (name == null || name.length() == 0) return null;

		if (name.charAt(0) != '/') { name = '/' + name; }

		try {
			// Dummy URL for SVG documents built from image streams
			return new URI(INPUTSTREAM_SCHEME, name, null);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private XMLReader getXMLReaderCached() throws SAXException {
		if (cachedReader == null) { cachedReader = XMLReaderFactory.createXMLReader(); }
		return cachedReader;
	}

	// protected URI loadSVG(URI xmlBase, InputStream is)
	protected URI loadSVG(final URI xmlBase, final InputSource is) {
		// Use an instance of ourselves as the SAX event handler
		final SVGLoader handler = new SVGLoader();
		// Place this docment in the universe before it is completely loaded
		// so that the load process can refer to references within it's current
		// document
		loadedDocs.put(xmlBase, handler.getRoot());
		try {
			// Parse the input
			final XMLReader reader = getXMLReaderCached();
			reader.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0])));
			reader.setContentHandler(handler);
			reader.parse(is);
			loadedDocs.put(xmlBase, handler.getRoot());
			return xmlBase;
		} catch (final SAXParseException sex) {
			System.err.println("Error processing " + xmlBase);
			System.err.println(sex.getMessage());
			loadedDocs.remove(xmlBase);
			return null;
		} catch (final Throwable t) {
			t.printStackTrace();
		}

		return null;
	}

}
