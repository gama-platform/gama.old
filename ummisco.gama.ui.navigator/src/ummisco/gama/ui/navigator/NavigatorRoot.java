/*********************************************************************************************
 * 
 * 
 * 'NavigatorRoot.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.PlatformObject;

public class NavigatorRoot extends PlatformObject {

	@Override
	public Object getAdapter(final Class adapter) {
		if ( adapter == IResource.class || adapter == IContainer.class ) { return ResourcesPlugin.getWorkspace()
			.getRoot(); }
		return null;
	}

}
