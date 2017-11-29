/*********************************************************************************************
 *
 * 'NavigatorBaseLighweightDecorator.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import ummisco.gama.ui.navigator.contents.VirtualContent;
import ummisco.gama.ui.utils.PreferencesHelper;

/**
 * Class NavigatorBaseLighweightDecorator.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class NavigatorBaseLighweightDecorator implements ILightweightLabelDecorator {

	private final StringBuilder sb = new StringBuilder();

	void decorate(final IDecoration decoration, final StringBuilder sb) {
		if (sb.length() > 0) {
			decoration.addSuffix(" (");
			decoration.addSuffix(sb.toString());
			decoration.addSuffix(")");
			sb.setLength(0);
		}
	}

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if (element instanceof VirtualContent) {
			((VirtualContent) element).getSuffix(sb);
			decorate(decoration, sb);
		} else if (PreferencesHelper.NAVIGATOR_METADATA.getValue()) {
			final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(element, false, true);
			if (data != null) {
				data.appendSuffix(sb);
				decorate(decoration, sb);
			}
		}
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return true;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {}

}
