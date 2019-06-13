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

package msi.gama.util.file.svg;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
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

import msi.gama.common.util.ImageUtils;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Many SVG files can be loaded at one time. These files will quite likely need to reference one another. The SVG
 * universe provides a container for all these files and the means for them to relate to each other.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGUniverse implements Serializable {
	public static final long serialVersionUID = 0;

	transient private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Maps document URIs to their loaded SVG diagrams. Note that URIs for documents loaded from URLs will reflect their
	 * URLs and URIs for documents initiated from streams will have the scheme <i>svgSalamander</i>.
	 */
	final HashMap loadedDocs = new HashMap();

	// final HashMap loadedFonts = new HashMap();

	// final HashMap loadedImages = new HashMap();

	public static final String INPUTSTREAM_SCHEME = "svgSalamander";

	/**
	 * Current time in this universe. Used for resolving attributes that are influenced by track information. Time is in
	 * milliseconds. Time 0 coresponds to the time of 0 in each member diagram.
	 */
	protected double curTime = 0.0;

	private boolean verbose = false;

	// Cache reader for efficiency
	XMLReader cachedReader;

	/** Creates a new instance of SVGUniverse */
	public SVGUniverse() {}

	public void addPropertyChangeListener(final PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	/**
	 * Release all loaded SVG document from memory
	 */
	public void clear() {
		loadedDocs.clear();
	}

	/**
	 * Returns the current animation time in milliseconds.
	 */
	public double getCurTime() {
		return curTime;
	}

	public void setCurTime(final double curTime) {
		final double oldTime = this.curTime;
		this.curTime = curTime;
		changes.firePropertyChange("curTime", new Double(oldTime), new Double(curTime));
	}

	/**
	 * Updates all time influenced style and presentation attributes in all SVG documents in this universe.
	 */
	// public void updateTime() throws SVGException {
	// for (final Iterator it = loadedDocs.values().iterator(); it.hasNext();) {
	// final SVGDiagram dia = (SVGDiagram) it.next();
	// dia.updateTime(curTime);
	// }
	// }

	/**
	 * Called by the Font element to let the universe know that a font has been loaded and is available.
	 */
	// void registerFont(final Font font) {
	// loadedFonts.put(font.getFontFace().getFontFamily(), font);
	// }
	//
	// public Font getDefaultFont() {
	// for (final Iterator it = loadedFonts.values().iterator(); it.hasNext();) {
	// return (Font) it.next();
	// }
	// return null;
	// }
	//
	// public Font getFont(final String fontName) {
	// return (Font) loadedFonts.get(fontName);
	// }

	// URL registerImage(final URI imageURI) {
	// final String scheme = imageURI.getScheme();
	// if (scheme.equals("data")) {
	// final String path = imageURI.getRawSchemeSpecificPart();
	// final int idx = path.indexOf(';');
	// final String mime = path.substring(0, idx);
	// String content = path.substring(idx + 1);
	//
	// if (content.startsWith("base64")) {
	// content = content.substring(6);
	// try {
	// final byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(content);
	// final ByteArrayInputStream bais = new ByteArrayInputStream(buf);
	// final BufferedImage img = ImageIO.read(bais);
	//
	// URL url;
	// int urlIdx = 0;
	// while (true) {
	// url = new URL("inlineImage", "localhost", "img" + urlIdx);
	// if (!loadedImages.containsKey(url)) {
	// break;
	// }
	// urlIdx++;
	// }
	//
	// final SoftReference ref = new SoftReference(img);
	// loadedImages.put(url, ref);
	//
	// return url;
	// } catch (final IOException ex) {
	// ex.printStackTrace();
	// }
	// }
	// return null;
	// } else {
	// try {
	// final URL url = imageURI.toURL();
	// registerImage(url);
	// return url;
	// } catch (final MalformedURLException ex) {
	// ex.printStackTrace();
	// }
	// return null;
	// }
	// }
	//
	// void registerImage(final URL imageURL) {
	// if (loadedImages.containsKey(imageURL)) { return; }
	//
	// SoftReference ref;
	// try {
	// final String fileName = imageURL.getFile();
	// if (".svg".equals(fileName.substring(fileName.length() - 4).toLowerCase())) {
	// final SVGIcon icon = new SVGIcon();
	// icon.setSvgURI(imageURL.toURI());
	//
	// final BufferedImage img =
	// new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	// final Graphics2D g = img.createGraphics();
	// icon.paintIcon(null, g, 0, 0);
	// g.dispose();
	// ref = new SoftReference(img);
	// } else {
	// final BufferedImage img = ImageIO.read(imageURL);
	// ref = new SoftReference(img);
	// }
	// loadedImages.put(imageURL, ref);
	// } catch (final Exception e) {
	// System.err.println("Could not load image: " + imageURL);
	// e.printStackTrace();
	// }
	// }
	//
	BufferedImage getImage(final URL imageURL) {
		return ImageUtils.getInstance().getImageFromFile(new File(imageURL.getPath()), true, false);
		// SoftReference ref = (SoftReference) loadedImages.get(imageURL);
		// if (ref == null) { return null; }
		//
		// BufferedImage img = (BufferedImage) ref.get();
		// // If image was cleared from memory, reload it
		// if (img == null) {
		// try {
		// img = ImageIO.read(imageURL);
		// } catch (final Exception e) {
		// e.printStackTrace();
		// }
		// ref = new SoftReference(img);
		// loadedImages.put(imageURL, ref);
		// }
		//
		// return img;
	}

	/**
	 * Returns the element of the document at the given URI. If the document is not already loaded, it will be.
	 */
	public SVGElement getElement(final URI path) {
		return getElement(path, true);
	}

	public SVGElement getElement(final URL path) {
		try {
			final URI uri = new URI(path.toString());
			return getElement(uri, true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Looks up a href within our universe. If the href refers to a document that is not loaded, it will be loaded. The
	 * URL #target will then be checked against the SVG diagram's index and the coresponding element returned. If there
	 * is no coresponding index, null is returned.
	 */
	public SVGElement getElement(final URI path, final boolean loadIfAbsent) {
		try {
			// Strip fragment from URI
			final URI xmlBase = new URI(path.getScheme(), path.getSchemeSpecificPart(), null);

			SVGDiagram dia = (SVGDiagram) loadedDocs.get(xmlBase);
			if (dia == null && loadIfAbsent) {
				// System.err.println("SVGUnivserse: " + xmlBase.toString());
				// javax.swing.JOptionPane.showMessageDialog(null, xmlBase.toString());
				final URL url = xmlBase.toURL();

				loadSVG(url, false);
				dia = (SVGDiagram) loadedDocs.get(xmlBase);
				if (dia == null) { return null; }
			}

			final String fragment = path.getFragment();
			return fragment == null ? dia.getRoot() : dia.getElement(fragment);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public SVGDiagram getDiagram(final URI xmlBase) {
		return getDiagram(xmlBase, true);
	}

	/**
	 * Returns the diagram that has been loaded from this root. If diagram is not already loaded, returns null.
	 */
	public SVGDiagram getDiagram(final URI xmlBase, final boolean loadIfAbsent) {
		if (xmlBase == null) { return null; }

		SVGDiagram dia = (SVGDiagram) loadedDocs.get(xmlBase);
		if (dia != null || !loadIfAbsent) { return dia; }

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
			dia = (SVGDiagram) loadedDocs.get(xmlBase);
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
		if ((b1 << 8 | b0) == GZIPInputStream.GZIP_MAGIC) {
			return new GZIPInputStream(bin);
		} else {
			// Plain text
			return bin;
		}
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
			if (loadedDocs.containsKey(uri) && !forceLoad) { return uri; }

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
		if (uri == null) { return null; }
		if (loadedDocs.containsKey(uri) && !forceLoad) { return uri; }

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
		if (uri == null) { return null; }
		if (loadedDocs.containsKey(uri) && !forceLoad) { return uri; }

		return loadSVG(uri, new InputSource(reader));
	}

	/**
	 * Synthesize a URI for an SVGDiagram constructed from a stream.
	 *
	 * @param name
	 *            - Name given the document constructed from a stream.
	 */
	public URI getStreamBuiltURI(String name) {
		if (name == null || name.length() == 0) { return null; }

		if (name.charAt(0) != '/') {
			name = '/' + name;
		}

		try {
			// Dummy URL for SVG documents built from image streams
			return new URI(INPUTSTREAM_SCHEME, name, null);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private XMLReader getXMLReaderCached() throws SAXException {
		if (cachedReader == null) {
			cachedReader = XMLReaderFactory.createXMLReader();
		}
		return cachedReader;
	}

	// protected URI loadSVG(URI xmlBase, InputStream is)
	protected URI loadSVG(final URI xmlBase, final InputSource is) {
		// Use an instance of ourselves as the SAX event handler
		final SVGLoader handler = new SVGLoader(xmlBase, this, verbose);

		// Place this docment in the universe before it is completely loaded
		// so that the load process can refer to references within it's current
		// document
		loadedDocs.put(xmlBase, handler.getLoadedDiagram());

		try {
			// Parse the input
			final XMLReader reader = getXMLReaderCached();
			reader.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0])));
			reader.setContentHandler(handler);
			reader.parse(is);

			return xmlBase;
		} catch (final SAXParseException sex) {
			DEBUG.ERR("Error processing " + xmlBase);
			DEBUG.ERR(sex.getMessage());

			loadedDocs.remove(xmlBase);
			return null;
		} catch (final Throwable t) {
			t.printStackTrace();
		}

		return null;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

}
