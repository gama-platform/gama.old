package ummisco.gama.ui.navigator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.dnd.*;
import org.eclipse.ui.navigator.*;


public class NavigatorDropAssistant extends CommonDropAdapterAssistant {

	public NavigatorDropAssistant() {}

	@Override
	public IStatus validateDrop(Object target, int operation, TransferData transferType) {
		return null;
	}

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
		return null;
	}

}
