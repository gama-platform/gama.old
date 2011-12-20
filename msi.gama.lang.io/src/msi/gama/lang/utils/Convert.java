/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils;

import java.io.*;
import java.util.Map;
import msi.gama.lang.gaml.descript.*;
import msi.gama.lang.utils.internal.*;
import msi.gaml.parser.xml.LineNumberSAXBuilder;
import org.eclipse.core.resources.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom.*;
import org.jdom.output.*;

public class Convert implements IUpdateOnChange {

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
		return xml2gaml(GamlDescriptIO.getPath(source));
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
		return GamlUtils.getDocMap(r, r.getResourceSet());
	}

	public static Map<String, ISyntacticElement> getDocMap(final IFile f) throws Exception {
		return GamlUtils.getDocMap(f);
	}

	public static ISyntacticElement parseXml(final File file) throws Exception {
		return new LineNumberSAXBuilder().parse(file);
	}

	/**
	 * @see msi.gama.lang.gaml.descript.IUpdateOnChange#update(java.lang.String,
	 *      org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void update(final String filePath, final Resource r) throws Exception {
		getDocMap(r);
	}
}
