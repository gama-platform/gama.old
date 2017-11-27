/*********************************************************************************************
 *
 * 'WrappedPlugin.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;

/**
 * Class WrappedFile.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class WrappedPlugin extends VirtualContent {

	/**
	 * @param root
	 * @param name
	 */
	public WrappedPlugin(final VirtualContent root, final String name) {
		super(root, name);
	}

	@Override
	public boolean canBeDecorated() {
		return false;
	}

	/**
	 * Method hasChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigLinkFont(); // by default
	}

	/**
	 * Method getNavigatorChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	/**
	 * Method getImage()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return null;
		// should be handled by the label provider
		// if ( GamaBundleLoader.contains(getName()) ) {
		// return GamaIcons.create("gaml/_present").image();
		// } else {
		// return GamaIcons.create("gaml/_missing").image();
		// }
	}

	/**
	 * Method getColor()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return GamaColors.system(SWT.COLOR_BLACK);
	}

	/**
	 * Method isParentOf()
	 * 
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	// @Override
	// public boolean isParentOf(final Object element) {
	// return false;
	// }

}
