package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.internal.navigator.resources.workbench.ResourceLinkHelper;
import org.eclipse.ui.part.FileEditorInput;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.WrappedFile;

public class NavigatorLinkHelper extends ResourceLinkHelper {

	@Override
	public IStructuredSelection findSelection(final IEditorInput anInput) {
		final IFile file = ResourceUtil.getFile(anInput);
		if (file != null) { return new StructuredSelection(ResourceManager.cache.getIfPresent(file)); }
		return StructuredSelection.EMPTY;
	}

	@Override
	public void activateEditor(final IWorkbenchPage aPage, final IStructuredSelection aSelection) {
		if (aSelection == null || aSelection.isEmpty())
			return;
		final Object o = aSelection.getFirstElement();
		if (o instanceof WrappedFile) {
			final IEditorInput fileInput = new FileEditorInput(((WrappedFile) o).getResource());
			final IEditorPart editor = aPage.findEditor(fileInput);
			if (editor != null)
				aPage.bringToTop(editor);
		}

	}

}
