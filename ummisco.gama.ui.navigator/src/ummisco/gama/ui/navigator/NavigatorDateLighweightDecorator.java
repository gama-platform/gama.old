/*******************************************************************************************************
 *
 * NavigatorDateLighweightDecorator.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import ummisco.gama.ui.navigator.contents.ResourceManager;

/**
 * Class NavigatorBaseLighweightDecorator.
 * 
 * @author drogoul
 * @since 11 févr. 2015
 * 
 */
public class NavigatorDateLighweightDecorator implements ILightweightLabelDecorator {

	/** The Constant ID. */
	static final public String ID = "msi.gama.application.decorator";

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		final IResource r = ResourceManager.getResource(element);
		if (r != null) {
			final long date = r.getLocalTimeStamp();
			final DateFormat df = DateFormat.getInstance();
			final String dateStr = df.format(new Date(date));
			decoration.addSuffix(" - " + dateStr);
		}
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {}
}
