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
import java.net.URL;
import java.util.*;
import msi.gama.lang.gaml.descript.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.precompiler.ISyntacticElement;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.util.EcoreUtil;
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

	public static ISyntacticElement parseXml(final File file) throws Exception {
		return new LineNumberSAXBuilder().parse(file);
	}

	public static Map<String, ISyntacticElement> getDocMap(final Resource r) throws Exception {
		ResourceSet rs = r.getResourceSet();
		if ( !r.getErrors().isEmpty() ) {
			StringBuilder sb =
				new StringBuilder("The Resource contains errors! ").append(r.getURI());
			for ( Diagnostic e : r.getErrors() ) {
				sb.append('\n').append(e.getMessage());
			}
			throw new Exception(sb.toString());
		}
		Map<String, ISyntacticElement> docs = new HashMap();
		// xrs.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		// EcoreUtil.resolveAll(r);
		// get include from 'r'
		Model m = (Model) EcoreUtil.copy(r.getContents().get(0));
		// convert 'r' + include in 'rs'
		for ( Import imp : m.getImports() ) {
			String importUri = imp.getImportURI();
			if ( !importUri.startsWith("platform:") ) {
				URI iu = URI.createURI(importUri).resolve(r.getURI());
				Resource ir = rs.getResource(iu, true);
				// EcoreUtil.resolveAll(ir);
				docs.putAll(getDocMap(ir)); // (!) recursive
			}
		}
		URL url = FileLocator.resolve(new URL(r.getURI().toString()));
		String path = new File(url.getFile()).getAbsolutePath();
		docs.put(path, Gaml2JDOM.doConvert(m));
		return docs;
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
