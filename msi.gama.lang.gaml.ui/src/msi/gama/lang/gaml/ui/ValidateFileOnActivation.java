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

import msi.gama.common.util.GuiUtils;
import org.eclipse.core.resources.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.*;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.ui.editor.model.*;

/**
 * Validates a .gaml file when it is opened or activated.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ValidateFileOnActivation extends AbstractPartListener {

	@Override
	public void partOpened(final IWorkbenchPartReference ref) {
		// validate(ref);
	}

	/**
	 * Validates the active active editor in the given part that contains a .gaml file in the
	 * Workspace.
	 * @param ref the part that was activated.
	 */
	@Override
	public void partActivated(final IWorkbenchPartReference ref) {
		// Logger.getLogger(AbstractCleaningLinker.class).setLevel(Level.DEBUG);
		GuiUtils.debug("Editor " + ref.getTitle() + " has been activated");
		validate(ref);
	}

	@Override
	public void partDeactivated(final IWorkbenchPartReference ref) {
		GamlEditor editor = activeEditor(ref);
		if ( editor == null ) { return; }
		GuiUtils.debug("Editor " + ref.getTitle() + " has been deactivated");
		editor.forgetModel();
	}

	private GamlEditor activeEditor(final IWorkbenchPartReference ref) {
		IWorkbenchPart part = ref.getPart(false);
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

	public void validate(final IWorkbenchPartReference ref) {
		GamlEditor editor = activeEditor(ref);
		if ( editor == null ) { return; }
		IProject project = projectOwningFileDisplayedIn(editor);
		if ( project == null ) { return; }
		final IXtextDocument document = editor.getDocument();
		XtextResource resource = editor.getXtextResource();
		EObject root = rootOf(resource);
		if ( root == null ) { return; }

		resource.getLinker().linkModel(root, new ListBasedDiagnosticConsumer());
		((XtextDocument) document).checkAndUpdateAnnotations();
		// Job job = ((XtextDocument) document).getValidationJob();
		// if ( job != null && job.getState() != Job.RUNNING ) {
		// job.schedule();
		// ((XtextDocument) document).checkAndUpdateAnnotations();

		/*
		 * document.readOnly(new IUnitOfWork.Void<XtextResource>() {
		 * 
		 * @Override
		 * public void process(final XtextResource resource) {
		 * // Diagnostician.INSTANCE.validate(resource.getParseResult().getRootASTElement());
		 * EObject root = rootOf(resource);
		 * if ( root == null ) { return; }
		 * resource.getLinker().linkModel(root, new ListBasedDiagnosticConsumer());
		 * Job job = ((XtextDocument) document).getValidationJob();
		 * if ( job != null && job.getState() != Job.RUNNING ) {
		 * job.schedule();
		 * // ((XtextDocument) document).checkAndUpdateAnnotations();
		 * }
		 * }
		 * });
		 */

	}

	private EObject rootOf(final XtextResource resource) {
		return resource == null ? null : resource.getParseResult().getRootASTElement();
	}

}
