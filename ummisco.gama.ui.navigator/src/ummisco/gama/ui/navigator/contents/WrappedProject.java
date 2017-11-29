package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

public class WrappedProject extends WrappedContainer<IProject> implements IAdaptable {

	final String plugin;

	public WrappedProject(final Object parent, final IProject wrapped) {
		super(parent, wrapped);
		final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(getResource(), false, false);
		if (data != null) {
			plugin = data.getSuffix();
		} else
			plugin = "";
	}

	@Override
	public boolean canBeDecorated() {
		return true;
	}

	@Override
	public boolean isOpen() {
		return super.isOpen() && getResource().isOpen();
	}

	@Override
	public boolean handleDoubleClick() {
		if (!isOpen()) {
			try {
				getResource().open(null);
			} catch (final CoreException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		return isOpen() ? super.getNavigatorChildren() : EMPTY;
	}

	@Override
	public Image getImage() {
		return GamaIcons.create(IGamaIcons.FOLDER_PROJECT).image();
	}

	@Override
	public Color getColor() {
		return IGamaColors.GRAY_LABEL.color();
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigHeaderFont();
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		if (!isOpen()) {
			sb.append("closed");
			return;
		}
		if (plugin != null && !plugin.isEmpty())
			sb.append(plugin).append(", ");
		super.getSuffix(sb);
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.PROJECT;
	}

}
