/*********************************************************************************************
 *
 *
 * 'VirtualContent.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.navigator;

import org.eclipse.swt.graphics.*;
import msi.gama.gui.swt.SwtGui;

public abstract class VirtualContent {

	protected static Object[] EMPTY = new Object[0];
	private final Object root;
	private final String name;

	public VirtualContent(final Object root, final String name) {
		this.root = root;
		this.name = name;
	}

	public boolean canBeDecorated() {
		return false;
	}

	/**
	 * Should both perform something and answer whether or not it has performed it, so that the navigator knows whether it should handle double-clicks itself
	 * @return
	 */
	public boolean handleDoubleClick() {
		// Nothing to do by default (default behavior is performed by the navigator, like revealing children, for instance)
		return false;
	}

	public String getName() {
		return name;
	}

	public Object getParent() {
		return root;
	}

	public abstract boolean hasChildren();

	public abstract Object[] getNavigatorChildren();

	public abstract Image getImage();

	public abstract Color getColor();

	public Font getFont() {
		return SwtGui.getNavigFolderFont(); // by default
	}

	// public abstract boolean isParentOf(Object element);

}
