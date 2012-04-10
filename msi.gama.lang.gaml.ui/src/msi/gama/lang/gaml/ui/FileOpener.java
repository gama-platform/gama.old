/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

	public IEditorPart openProtoFileInWorkspace(final URI uri) throws PartInitException {
		IFile file = referredFile(uri);
		IEditorInput editorInput = new FileEditorInput(file);
		return openFile(editorInput);
	}

	private IEditorPart openFile(final IEditorInput editorInput) throws PartInitException {
		IWorkbenchPage page = SwtGui.getPage();
		return page.openEditor(editorInput, "msi.gama.lang.gaml.Gaml");
	}

	public IFile referredFile(final URI uri) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = pathOf(uri);
		return path != null ? root.getFile(path) : null;
	}

	private IPath pathOf(final URI uri) {
		String uriAsText = uri.toPlatformString(true);
		return uriAsText != null ? new Path(uriAsText) : null;
	}

}
