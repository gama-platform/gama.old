package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

public class WrappedFolder extends WrappedContainer<IFolder> {

	Image image;
	Font font;
	boolean canBeDecorated;

	public WrappedFolder(final WrappedContainer<?> root, final IFolder wrapped) {
		super(root, wrapped);
	}

	@Override
	public WrappedContainer<?> getParent() {
		return (WrappedContainer<?>) super.getParent();
	}

	@Override
	public int countModels() {
		if (modelsCount == NOT_COMPUTED) {
			super.countModels();
			final boolean isExternal = getName().equals("external");
			image = GamaIcons.create(
					isExternal ? "file.svn2" : modelsCount == 0 ? IGamaIcons.FOLDER_RESOURCES : IGamaIcons.FOLDER_MODEL)
					.image();
			font = modelsCount == 0 ? GamaFonts.getResourceFont() : GamaFonts.getNavigFolderFont();
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

	@Override
	public Color getColor() {
		return IGamaColors.BLACK.color();
	}

	@Override
	public Font getFont() {
		countModels();
		return font;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.FOLDER;
	}

}
