/*********************************************************************************************
 * 
 * 
 * 'GamaViewItem.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.controls.GamaToolbar;
import msi.gama.gui.views.IToolbarDecoratedView;
import org.eclipse.swt.widgets.ToolItem;

/**
 * The class GamaToolItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public abstract class GamaToolItem/* implements SelectionListener */{

	protected abstract ToolItem createItem(GamaToolbar toolbar, final IToolbarDecoratedView view);

	public void dispose() {}

}
