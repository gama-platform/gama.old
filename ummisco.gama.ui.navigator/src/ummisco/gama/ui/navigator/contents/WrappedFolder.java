/*******************************************************************************************************
 *
 * WrappedFolder.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The Class WrappedFolder.
 */
public class WrappedFolder extends WrappedContainer<IFolder> {

	/** The image. */
	Image image;

	/** The can be decorated. */
	// Font font;
	boolean canBeDecorated;

	/**
	 * Instantiates a new wrapped folder.
	 *
	 * @param root
	 *            the root
	 * @param wrapped
	 *            the wrapped
	 */
	public WrappedFolder(final WrappedContainer<?> root, final IFolder wrapped) {
		super(root, wrapped);
	}

	@Override
	public WrappedContainer<?> getParent() { return (WrappedContainer<?>) super.getParent(); }

	@Override
	public int countModels() {
		if (modelsCount == NOT_COMPUTED) {
			super.countModels();
			final var isExternal = "external".equals(getName());
			image = GamaIcons.create(isExternal ? "navigator/file.cloud"
					: modelsCount == 0 ? IGamaIcons.FOLDER_RESOURCES : IGamaIcons.FOLDER_MODEL).image();
			// font = modelsCount == 0 ? GamaFonts.getResourceFont() : GamaFonts.getNavigFolderFont();
			canBeDecorated = modelsCount > 0;
		}
		return modelsCount;
	}

	@Override
	public boolean canBeDecorated() {
		countModels();
		return canBeDecorated;
	}

	@Override
	public Image getImage() {
		countModels();
		return image;
	}
	//
	// @Override
	// public Color getColor() {
	// return ThemeHelper.isDark() ? GamaColors.system(SWT.COLOR_WHITE) : GamaColors.system(SWT.COLOR_BLACK);
	// }

	// @Override
	// public Font getFont() {
	// countModels();
	// return font;
	// }

	@Override
	public VirtualContentType getType() { return VirtualContentType.FOLDER; }

}
