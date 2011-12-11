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
package msi.gama.gui.application.commands;

import java.io.*;
import java.util.Map;
import msi.gama.lang.gaml.ui.GamlResourceSet;
import msi.gama.lang.utils.*;
import org.eclipse.core.resources.IFile;

public class ConvertGamlToXmlHandler extends ConvertHandler {

	@Override
	protected InputStream getConvertedInputStream(final IFile source) {
		// convert XML (path to an xml file) -> GAML (code as a string)
		Map<String, ISyntacticElement> docs;
		try {
			docs = Convert.getDocMap(source, GamlResourceSet.get(source.getProject()));
			return new ByteArrayInputStream(Convert.toFormatedString(
				docs.get(Convert.getPath(source))).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected String getNewExt() {
		return "xml";
	}
}
