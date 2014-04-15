/*********************************************************************************************
 * 
 *
 * 'VirtualFolder.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import java.io.IOException;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;

public abstract class VirtualFolder {

	private Object rootElement;
	private final String name;
	private String builtInModelsPath;

	public String getBuiltInModelsPath() {
		if ( builtInModelsPath == null ) {
			try {
				builtInModelsPath =
					FileLocator.toFileURL(new URL("platform:/plugin/msi.gama.models")).getPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return builtInModelsPath;
	}

	public VirtualFolder(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Object getRootElement() {
		return rootElement;
	}

	public void setRootElement(final Object rootElement) {
		this.rootElement = rootElement;
	}

	public abstract Object[] getChildren();

}
