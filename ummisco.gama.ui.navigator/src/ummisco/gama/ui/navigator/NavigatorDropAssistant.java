/*********************************************************************************************
 *
 * 'NavigatorDropAssistant.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

import ummisco.gama.ui.commands.PasteIntoModelsHandler;

public class NavigatorDropAssistant extends CommonDropAdapterAssistant {

	public NavigatorDropAssistant() {
	}

	@Override
	public IStatus validateDrop(final Object target, final int operation, final TransferData transferType) {
		return target instanceof UserProjectsFolder || target instanceof NavigatorRoot ? Status.OK_STATUS
				: Status.CANCEL_STATUS;
	}

	@Override
	public IStatus handleDrop(final CommonDropAdapter adapter, final DropTargetEvent event, final Object target) {
		if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
			final String[] files = (String[]) event.data;
			if (files != null && files.length > 0) {
				PasteIntoModelsHandler.handlePaste(files);
				return Status.OK_STATUS;
			}
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public boolean isSupportedType(final TransferData aTransferType) {
		return super.isSupportedType(aTransferType) || FileTransfer.getInstance().isSupportedType(aTransferType);
	}

}
