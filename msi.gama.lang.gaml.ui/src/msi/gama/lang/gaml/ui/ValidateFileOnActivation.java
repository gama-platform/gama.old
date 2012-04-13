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
import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider;
import org.apache.log4j.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.*;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.impl.AbstractCleaningLinker;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.ui.editor.model.*;
import org.eclipse.xtext.ui.util.ResourceUtil;

/**
 * Validates a .gaml file when it is opened or activated.
 * 
 * @author alruiz@google.com (Alex Ruiz), adapted by Alexis Drogoul for GAML
 */
public class ValidateFileOnActivation extends AbstractPartListener {

	private IWorkbenchPartReference previousPart;

	@Override
	public void partOpened(final IWorkbenchPartReference ref) {
		GuiUtils.debug("Editor " + ref.getTitle() + " has been opened");
		// validate(ref);
	}

	/**
	 * Validates the active active editor in the given part that contains a .gaml file in the
	 * Workspace.
	 * @param ref the part that was activated.
	 */
	@Override
	public void partActivated(final IWorkbenchPartReference ref) {
		Logger.getLogger(AbstractCleaningLinker.class).setLevel(Level.DEBUG);
		GuiUtils.debug("Editor " + ref.getTitle() + " has been activated");
		validate(ref);
	}

	@Override
	public void partBroughtToTop(final IWorkbenchPartReference ref) {
		GuiUtils.debug("Editor " + ref.getTitle() + " has been brought to top");
		validate(ref);
	}

	@Override
	public void partDeactivated(final IWorkbenchPartReference ref) {
		IWorkbenchPart editor = ref.getPart(false);
		if ( !(editor instanceof GamlEditor) ) { return; }
		GuiUtils.debug("Editor " + ref.getTitle() + " has been deactivated");
		((GamlEditor) editor).forgetModel();
	}

	private GamlEditor activeEditor(final IWorkbenchPartReference ref) {
		IWorkbenchPart part = ref.getPart(false);
		if ( !(part instanceof GamlEditor) ) { return null; }
		if ( part.getSite().getPage().getActiveEditor() != part ) { return null; }
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

	public synchronized void validate(final IWorkbenchPartReference ref) {
		GamlEditor editor = activeEditor(ref);
		if ( editor == null || ref == previousPart ) { return; }
		previousPart = ref;
		IProject project = projectOwningFileDisplayedIn(editor);
		if ( project == null ) { return; }

		Runnable run = createRunnableFor(editor);
		if ( BuiltinGlobalScopeProvider.scopeBuilt ) {
			run.run();
			// new Thread(run, "Validation of " + ref.getTitle()).start();
		} else {
			BuiltinGlobalScopeProvider.registerRunnableAfterLoad(run);
		}
	}

	/**
	 * @param editor
	 * @return
	 */
	private Runnable createRunnableFor(final GamlEditor editor) {
		return new Runnable() {

			@Override
			public void run() {
				// while (!GamaBundleLoader.contributionsLoaded) {
				// try {
				// Thread.sleep(10);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// }
				final IXtextDocument document = editor.getDocument();
				XtextResource resource = editor.getXtextResource();
				EObject root = rootOf(resource);
				if ( root == null ) { return; }
				IFile file = ResourceUtil.getUnderlyingFile(resource);
				try {
					file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
				resource.getLinker().linkModel(root, consumer);
				resource.getErrors().addAll(consumer.getResult(Severity.ERROR));
				resource.getWarnings().addAll(consumer.getResult(Severity.WARNING));
				((XtextDocument) document).checkAndUpdateAnnotations();
			}

		};
	}

	private EObject rootOf(final XtextResource resource) {
		return resource == null ? null : resource.getParseResult().getRootASTElement();
	}

}
