/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package msi.gama.lang.gaml.ui;

import org.eclipse.core.resources.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.*;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.*;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * Validates a .proto file when it is opened or activated.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ValidateFileOnActivation extends AbstractPartListener {

	/**
	 * Validates the active active editor in the given part that contains a .proto file in the
	 * Workspace.
	 * @param ref the part that was activated.
	 */
	@Override
	public void partActivated(final IWorkbenchPartReference ref) {
		GamlEditor activeEditor = activeEditor(ref);
		if ( activeEditor == null ) { return; }
		IProject project = projectOwningFileDisplayedIn(activeEditor);
		if ( project == null ) { return; }
		validate(activeEditor);
	}

	@Override
	public void partVisible(final IWorkbenchPartReference ref) {
		GamlEditor activeEditor = activeEditor(ref);
		if ( activeEditor == null ) { return; }
		IProject project = projectOwningFileDisplayedIn(activeEditor);
		if ( project == null ) { return; }
		// validate(activeEditor);
	}

	@Override
	public void partBroughtToTop(final IWorkbenchPartReference ref) {
		GamlEditor activeEditor = activeEditor(ref);
		if ( activeEditor == null ) { return; }
		IProject project = projectOwningFileDisplayedIn(activeEditor);
		if ( project == null ) { return; }
		// validate(activeEditor);
	}

	private GamlEditor activeEditor(final IWorkbenchPartReference ref) {
		IWorkbenchPage page = ref.getPage();
		if ( page == null ) { return null; }
		IEditorPart part = page.getActiveEditor();
		if ( !(part instanceof GamlEditor) ) { return null; }
		return (GamlEditor) part;
	}

	public IProject projectOwningFileDisplayedIn(final IEditorPart editor) {
		IResource resource = resourceFrom(editor);
		return resource == null ? null : resource.getProject();
	}

	private IResource resourceFrom(final IEditorPart editor) {
		if ( editor == null ) { return null; }
		Object adapter = editor.getEditorInput().getAdapter(IResource.class);
		return adapter == null ? null : (IResource) adapter;
	}

	public static void validate(final XtextEditor editor) {
		final IXtextDocument document = editor.getDocument();
		if ( !(document instanceof XtextDocument) ) { return; }
		document.readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) {
				EObject root = rootOf(resource);
				if ( root == null ) { return; }
				resource.getLinker().linkModel(root, new ListBasedDiagnosticConsumer());
				((XtextDocument) document).checkAndUpdateAnnotations();
			}
		});
	}

	private static EObject rootOf(final XtextResource resource) {
		return resource == null ? null : resource.getParseResult().getRootASTElement();
	}

}
