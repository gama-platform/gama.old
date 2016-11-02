/*********************************************************************************************
 *
 * 'WrappedPlugins.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * Class ImportFolder.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class WrappedPlugins extends VirtualContent {

	final Collection<String> plugins;

	/**
	 * @param root
	 * @param name
	 */
	public WrappedPlugins(final IFile root, final Collection<String> object, final String name) {
		super(root, name);
		plugins = object;
	}

	/**
	 * Method hasChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !plugins.isEmpty();
	}

	@Override
	public Font getFont() {
		return GamaFonts.getSmallFont(); // by default
	}

	/**
	 * Method getNavigatorChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		if (plugins.isEmpty()) {
			return EMPTY;
		}
		final List<WrappedPlugin> files = new ArrayList<>();
		for (final String s : plugins) {
			final WrappedPlugin proxy = new WrappedPlugin(this, s);
			files.add(proxy);
		}
		return files.toArray();
	}

	/**
	 * Method getImage()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return GamaIcons.create("gaml/_requires").image();
	}

	/**
	 * Method getColor()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return IGamaColors.BLACK.color();
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
