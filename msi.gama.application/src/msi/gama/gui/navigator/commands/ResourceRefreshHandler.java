package msi.gama.gui.navigator.commands;

import java.util.List;
import msi.gama.gui.navigator.FileMetaDataProvider;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ResourceRefreshHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		List files = ((IStructuredSelection) HandlerUtil.getCurrentSelection(event)).toList();
		for ( Object o : files ) {
			if ( o instanceof IFile ) {
				FileMetaDataProvider.getInstance().storeMetadata((IFile) o, null);
			}
		}
		RefreshHandler.run();
		return null;
	}

}
