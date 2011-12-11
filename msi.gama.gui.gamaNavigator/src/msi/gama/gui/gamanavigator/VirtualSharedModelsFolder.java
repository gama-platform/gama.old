/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.gui.gamanavigator;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;

public class VirtualSharedModelsFolder extends VirtualFolder {

	public VirtualSharedModelsFolder(String name) {
		super(name);
	}

	@Override
	public Object[] getChildren() {

		URL url = Platform.getInstanceLocation().getURL();
		File sharedModelsRep = new File(url.getPath() + ".svn_models");

		if (!sharedModelsRep.exists())
			sharedModelsRep.mkdir();

		FileBean gFile = new FileBean(sharedModelsRep);

		return gFile.getFirstChildren();
	}
}
