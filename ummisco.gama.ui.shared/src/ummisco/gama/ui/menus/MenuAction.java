/*********************************************************************************************
 *
 * 'MenuAction.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.menus;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

public class MenuAction {

	public SelectionListener listener;
	public Image image;
	public String text;

	public MenuAction(final SelectionListener listener, final Image image, final String text) {
		super();
		this.listener = listener;
		this.image = image;
		this.text = text;
	}

}