/*********************************************************************************************
 * 
 *
 * 'GamaAction.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The class GamaAction.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public abstract class GamaAction extends Action {

	public GamaAction(final String title, final String tooltip, final int style, final ImageDescriptor image) {
		super(title, style);
		setToolTipText(tooltip);
		setImageDescriptor(image);
	}

	// @Override
	// public abstract void run();
}
