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

import msi.gama.gui.swt.SwtGui;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import com.google.inject.Singleton;

/**
 * Utility methods related to open file from different type of locations.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class FileOpener {

	// org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider p;

	public IEditorPart openFileInWorkspace(final URI uri) throws PartInitException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String uriAsText = uri.toPlatformString(true);
		IPath path = uriAsText != null ? new Path(uriAsText) : null;
		IFile file = path != null ? root.getFile(path) : null;
		if ( file == null ) { return null; }
		IEditorInput editorInput = new FileEditorInput(file);
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		if ( desc == null ) { return null; }
		IWorkbenchPage page = SwtGui.getPage();

		return page.openEditor(editorInput, desc.getId());
	}

}
