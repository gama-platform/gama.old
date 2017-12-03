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

import ummisco.gama.ui.navigator.contents.VirtualContent;

/**
 * Class NavigatorBaseLighweightDecorator.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class NavigatorBaseLighweightDecorator implements ILightweightLabelDecorator {

	private final StringBuilder sb = new StringBuilder();

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if (element instanceof VirtualContent) {
			((VirtualContent<?>) element).getSuffix(sb);
			if (sb.length() > 0) {
				decoration.addSuffix(" (");
				decoration.addSuffix(sb.toString());
				decoration.addSuffix(")");
				sb.setLength(0);
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
