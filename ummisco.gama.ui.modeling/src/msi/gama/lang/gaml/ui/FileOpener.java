/*********************************************************************************************
 * 
 * 
 * 'FileOpener.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.google.inject.Singleton;

import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Utility methods related to open file from different type of locations.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class FileOpener {

	// org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider p;

	public IEditorPart openFileInWorkspace(final URI uri) throws PartInitException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final String uriAsText = uri.toPlatformString(true);
		final IPath path = uriAsText != null ? new Path(uriAsText) : null;
		final IFile file = path != null ? root.getFile(path) : null;
		if (file == null) {
			return null;
		}
		final IEditorInput editorInput = new FileEditorInput(file);
		final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		if (desc == null) {
			return null;
		}
		final IWorkbenchPage page = WorkbenchHelper.getPage();

		return page.openEditor(editorInput, desc.getId());
	}

}
