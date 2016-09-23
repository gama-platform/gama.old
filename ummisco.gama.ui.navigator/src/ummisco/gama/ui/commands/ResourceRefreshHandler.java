package ummisco.gama.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.navigator.TopLevelFolder;

public class ResourceRefreshHandler extends AbstractHandler {

	final static IResourceProxyVisitor DISCARDING_VISITOR = new IResourceProxyVisitor() {

		@Override
		public boolean visit(final IResourceProxy proxy) throws CoreException {
			if (proxy.getType() == IResource.FILE) {
				discardMetaData((IFile) proxy.requestResource());
			}
			return true;
		}
	};

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final List files = ((IStructuredSelection) HandlerUtil.getCurrentSelection(event)).toList();
		for (final Object o : files) {
			if (o instanceof IResource) {
				try {
					((IResource) o).accept(DISCARDING_VISITOR, IResource.NONE);
				} catch (final CoreException e) {
				}
			} else if (o instanceof TopLevelFolder) {
				for (final Object obj : ((TopLevelFolder) o).getNavigatorChildren()) {
					final IProject p = (IProject) obj;
					try {
						p.accept(DISCARDING_VISITOR, IResource.NONE);
					} catch (final CoreException e) {
					}
				}
			}
		}
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
