/**
 * Created by drogoul, 5 févr. 2015
 *
 */
package ummisco.gama.ui.navigator;

import java.util.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.*;
import msi.gama.gui.swt.*;
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
	 * @see ummisco.gama.ui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		if ( plugins.isEmpty() ) { return EMPTY; }
		List<WrappedPlugin> files = new ArrayList();
		for ( String s : plugins ) {
			WrappedPlugin proxy = new WrappedPlugin(this, s);
			files.add(proxy);
		}
		return files.toArray();
	}

	/**
	 * Method getImage()
	 * @see ummisco.gama.ui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return GamaIcons.create("gaml/_requires").image();
	}

	/**
	 * Method getColor()
	 * @see ummisco.gama.ui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return IGamaColors.BLACK.color();
	}

	/**
	 * Method isParentOf()
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	// @Override
	// public boolean isParentOf(final Object element) {
	// return false;
	// }
}
