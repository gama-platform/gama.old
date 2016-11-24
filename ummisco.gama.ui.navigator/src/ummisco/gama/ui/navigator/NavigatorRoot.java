/*********************************************************************************************
 *
 * 'NavigatorRoot.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.PlatformObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NavigatorRoot extends PlatformObject {

	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == IResource.class || adapter == IContainer.class) {
			return ResourcesPlugin.getWorkspace().getRoot();
		}
		return super.getAdapter(adapter);
	}

}
