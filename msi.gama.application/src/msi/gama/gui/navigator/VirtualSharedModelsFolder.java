/*********************************************************************************************
 * 
 *
 * 'VirtualSharedModelsFolder.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

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
