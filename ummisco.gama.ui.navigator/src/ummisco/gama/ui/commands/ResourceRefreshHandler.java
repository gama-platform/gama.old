package ummisco.gama.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import msi.gama.runtime.GAMA;

public class ResourceRefreshHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final List files = ((IStructuredSelection) HandlerUtil.getCurrentSelection(event)).toList();
		for (final Object o : files) {
			if (o instanceof IFile) {
				discardMetaData((IFile) o);
			}
		}
		// RefreshHandler.run();
		return null;
	}

	public static void discardMetaData(final IFile file) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				GAMA.getGui().getMetaDataProvider().storeMetadata(file, null, false);
			}
		});
	}

}
