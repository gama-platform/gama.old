/*********************************************************************************************
 *
 * 'WrappedFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import msi.gama.application.workbench.ThemeHelper;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * Class ImportFolder.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class Tags extends VirtualContent<WrappedFile> {

	final Collection<String> tags;

	/**
	 * @param root
	 * @param name
	 */
	public Tags(final WrappedFile root, final Collection<String> object, final String name) {
		super(root, name);
		tags = object;
	}

	/**
	 * Method hasChildren()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !tags.isEmpty();
	}

	// @Override
	// public Font getFont() {
	// return GamaFonts.getSmallFont(); // by default
	// }

	@Override
	public WrappedFile getParent() {
		return super.getParent();
	}

	/**
	 * Method getNavigatorChildren()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		if (tags.isEmpty()) return EMPTY;
		return tags.stream().map(each -> new Tag(this, each)).toArray();
	}

	/**
	 * Method getImage()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return GamaIcons.create("gaml/_attributes").image();
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
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public int findMaxProblemSeverity() {
		return 0;
	}

	@Override
	public ImageDescriptor getOverlay() {
		return null;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.CATEGORY;
	}

	@Override
	public String getStatusMessage() {
		return "Tags";
	}

	@Override
	public GamaUIColor getStatusColor() {
		return IGamaColors.GRAY_LABEL;
	}

	@Override
	public Image getStatusImage() {
		return getImage();
	}

}
