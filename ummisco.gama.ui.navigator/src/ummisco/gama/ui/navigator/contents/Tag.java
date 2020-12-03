/*********************************************************************************************
 *
 * 'WrappedFile.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gama.application.workbench.ThemeHelper;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;

/**
 * Class LinkedFile.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class Tag extends VirtualContent<Tags> {

	/**
	 * @param root
	 * @param name
	 */
	public Tag(final Tags root, final String wrapped) {
		super(root, wrapped);
	}

	/**
	 * Method hasChildren()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#hasChildren()
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
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	/**
	 * Method getImage()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return null;
	}

	/**
	 * Method getColor()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return ThemeHelper.isDark() ? GamaColors.system(SWT.COLOR_WHITE) : GamaColors.system(SWT.COLOR_BLACK);
	}

	@Override
	public boolean handleDoubleClick() {
		return true;
	}

	@Override
	public int findMaxProblemSeverity() {
		return 0;
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		// sb.append(suffix);
	}

	@Override
	public ImageDescriptor getOverlay() {
		return null;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.CATEGORY;
	}

}
