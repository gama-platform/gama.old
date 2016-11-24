/*********************************************************************************************
 *
 * 'NavigatorBaseLighweightDecorator.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
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
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import ummisco.gama.ui.utils.PreferencesHelper;

/**
 * Class NavigatorBaseLighweightDecorator.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class NavigatorBaseLighweightDecorator implements ILightweightLabelDecorator {

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		String suffix = "";
		if (PreferencesHelper.NAVIGATOR_METADATA.getValue()) {
			final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(element, false, false);
			if (data != null) {
				suffix = data.getSuffix();
			}
		}

		if (element instanceof IContainer) {
			final int modelCount = countModels((IResource) element);
			if (modelCount > 0) {
				if (suffix != null && !suffix.isEmpty())
					suffix += ", ";
				suffix += modelCount + (modelCount == 1 ? " model" : " models");
			}
		} else if (element instanceof TopLevelFolder) {
			suffix = ((TopLevelFolder) element).getSuffix();
		}

		//
		if (suffix != null && !suffix.isEmpty()) {
			decoration.addSuffix(" (" + suffix + ")");
		}
	}

	public static int countModels(final IResource element) {
		final int modelCount[] = new int[1];
		try {
			element.accept(new IResourceProxyVisitor() {

				@Override
				public boolean visit(final IResourceProxy proxy) throws CoreException {
					if (proxy.getType() == IResource.FILE && proxy.getName().endsWith(".gaml"))
						modelCount[0]++;
					return true;
				}
			}, IResource.NONE);
		} catch (final CoreException e) {
		}
		return modelCount[0];
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return true;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}

}
