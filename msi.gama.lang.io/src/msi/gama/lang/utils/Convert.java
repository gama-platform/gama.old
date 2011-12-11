/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils;

import java.io.*;
import java.net.*;
import java.util.Map;
import msi.gama.lang.utils.internal.*;
import msi.gaml.parser.xml.LineNumberSAXBuilder;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.ecore.resource.*;
import org.jdom.*;
import org.jdom.output.*;

public class Convert {

	// XML -> GAML
	public static String xml2gaml(final String path) {
		return XmlUtils.parse(path);
	}

	// public static String xml2gaml(final Document doc) {
	// return XmlUtils.parse(doc);
	// }

	public static String xml2gaml(final InputStream is) {
		return XmlUtils.parse(is);
	}

	public static String xml2gaml(final IResource source) {
		return xml2gaml(getPath(source));
	}

	public static String toFormatedString(final ISyntacticElement doc) {
		final XMLOutputter xo = new XMLOutputter();
		Format f = xo.getFormat();
		f.setIndent("\t");
		f.setLineSeparator("\n");
		xo.setFormat(f);
		Document xmlDoc = new Document();
		xmlDoc.setRootElement((Element) doc.getUnderlyingElement());
		return xo.outputString(xmlDoc);
	}

	// GAML -> XML
	public static Map<String, ISyntacticElement> getDocMap(final Resource r) throws Exception {
		return GamlUtils.getDocMap(r);
	}

	public static Map<String, ISyntacticElement> getDocMap(final IFile f, final ResourceSet rs)
		throws Exception {
		return GamlUtils.getDocMap(f, rs);
	}

	/**
	 * @see org.eclipse.core.resources.IResource#getLocation()
	 * @see org.eclipse.core.runtime.IPath#toOSString()
	 * @param f File from a Project of the Workspace
	 * @return a platform-dependent string representation of this path
	 */
	public static String getPath(final IResource f) {
		return f.getLocation().toOSString();
	}

	/**
	 * @see org.eclipse.emf.ecore.resource.Resource#getURI()
	 * @see org.eclipse.emf.common.util.URI#toString()
	 * @see #getPath(URL)
	 * @param r
	 * @return
	 */
	public static String getPath(final Resource r) {
		try {
			return getPath(new URL(r.getURI().toString()));
		} catch (MalformedURLException e) {}
		return null;
	}

	/**
	 * @see org.eclipse.core.runtime.FileLocator#resolve(URL)
	 * @see java.io.File#getAbsolutePath();
	 * @param u URL to resolve
	 * @return The absolute pathname string denoting the same file or directory as this URL
	 */
	public static String getPath(final URL u) {
		try {
			URL url = FileLocator.resolve(u);
			return new File(url.getFile()).getAbsolutePath();
		} catch (Exception e) {}
		return null;
	}

	public static ISyntacticElement parseXml(final File file) throws Exception {
		return new LineNumberSAXBuilder().parse(file);
	}
}
