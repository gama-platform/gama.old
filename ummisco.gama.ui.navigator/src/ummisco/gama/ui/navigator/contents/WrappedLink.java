package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Font;

import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class WrappedLink extends WrappedFile {

	public WrappedLink(final WrappedContainer<?> root, final IFile wrapped) {
		super(root, wrapped);
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigLinkFont();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean handleDoubleClick() {
		if (!getManager().validateLocation(getResource())) {
			MessageDialog.openError(WorkbenchHelper.getShell(), "Unknown file",
					"The file at location '" + getResource().getLocation() + " does not exist");
			return true;
		}
		return false;

	}

	@Override
	public int findMaxProblemSeverity() {
		if (!getManager().validateLocation(getResource())) { return LINK_BROKEN; }
		return LINK_OK;
	}

	@Override
	public boolean canBeDecorated() {
		return true;
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		super.getSuffix(sb);

		if (sb.length() > 0) {
			sb.append(" - ");
		}
		sb.append(getResource().getLocation());

	}

}
