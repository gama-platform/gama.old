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

import org.eclipse.ui.*;

/**
 * Validates a .gaml file when it is opened or activated.
 * 
 * @author alruiz@google.com (Alex Ruiz), adapted by Alexis Drogoul for GAML
 */
public class ValidateFileOnActivation extends AbstractPartListener {

	private IWorkbenchPartReference previousPart;

	/**
	 * Updates the toolbar of the active editor in the part that contains a .gaml file.
	 * @param ref the part that was activated.
	 */
	@Override
	public void partActivated(final IWorkbenchPartReference ref) {
		// Logger.getLogger(AbstractCleaningLinker.class).setLevel(Level.DEBUG);
		IWorkbenchPart editor = ref.getPart(false);
		if ( !(editor instanceof GamlEditor) ) { return; }
		((GamlEditor) editor).updateToolbar();
	}

	// private GamlEditor activeEditor(final IWorkbenchPartReference ref) {
	// IWorkbenchPart part = ref.getPart(false);
	// if ( !(part instanceof GamlEditor) ) { return null; }
	// if ( part.getSite().getPage().getActiveEditor() != part ) { return null; }
	// return (GamlEditor) part;
	// }

	// public IProject projectOwningFileDisplayedIn(final IEditorPart editor) {
	// IResource resource = resourceFrom(editor);
	// return resource == null ? null : resource.getProject();
	// }
	//
	// private IResource resourceFrom(final IEditorPart editor) {
	// if ( editor == null ) { return null; }
	// Object adapter = editor.getEditorInput().getAdapter(IResource.class);
	// return adapter == null ? null : (IResource) adapter;
	// }
	//
	// public void validate(final IWorkbenchPartReference ref) {
	// if ( !GamaBundleLoader.contributionsLoaded ) { return; }
	// GamlEditor editor = activeEditor(ref);
	// GAMA.getGamlBuilder().validate(editor.getXtextResource());
	// // if ( editor == null || ref == previousPart ) { return; }
	// // previousPart = ref;
	// // IProject project = projectOwningFileDisplayedIn(editor);
	// // if ( project == null ) { return; }
	//
	// // Runnable run = createRunnableFor(editor);
	// // if ( BuiltinGlobalScopeProvider.scopeBuilt ) {
	// // run.run();
	// // } else {
	// // BuiltinGlobalScopeProvider.registerRunnableAfterLoad(run);
	// // }
	// editor.updateExperiments();
	// }
	//
	// /**
	// * @param editor
	// * @return
	// */
	// private Runnable createRunnableFor(final GamlEditor editor) {
	// return new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// // while (!GamaBundleLoader.contributionsLoaded) {
	// // try {
	// // Thread.sleep(10);
	// // } catch (InterruptedException e) {
	// // e.printStackTrace();
	// // }
	// // }
	// final IXtextDocument document = editor.getDocument();
	// XtextResource resource = editor.getXtextResource();
	// EObject root = rootOf(resource);
	// if ( root == null ) { return; }
	// IFile file = ResourceUtil.getUnderlyingFile(resource);
	// try {
	// file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
	// } catch (CoreException e) {
	// e.printStackTrace();
	// }
	// ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
	// resource.getLinker().linkModel(root, consumer);
	// resource.getErrors().addAll(consumer.getResult(Severity.ERROR));
	// resource.getWarnings().addAll(consumer.getResult(Severity.WARNING));
	// ((XtextDocument) document).checkAndUpdateAnnotations();
	// }
	//
	// };
	// }
	//
	// private EObject rootOf(final XtextResource resource) {
	// return resource == null ? null : resource.getParseResult().getRootASTElement();
	// }

}
