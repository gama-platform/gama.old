/**
 * Created by drogoul, 5 févr. 2015
 *
 */
package ummisco.gama.ui.navigator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import msi.gaml.compilation.GamaBundleLoader;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;

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
	 * @see ummisco.gama.ui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	/**
	 * Method getImage()
	 * @see ummisco.gama.ui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		// should be handled by the label provider
		if ( GamaBundleLoader.contains(getName()) ) {
			return GamaIcons.create("gaml/_present").image();
		} else {
			return GamaIcons.create("gaml/_missing").image();
		}
	}

	/**
	 * Method getColor()
	 * @see ummisco.gama.ui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return GamaColors.system(SWT.COLOR_BLACK);
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
