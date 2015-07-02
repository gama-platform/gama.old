/*********************************************************************************************
 *
 *
 * 'GamlEditorCallback.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import msi.gama.lang.gaml.resource.GamlResource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.validation.*;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.*;
import com.google.inject.Inject;

/**
 * The class GamlEditorCallback. Calls the functionalities of ValidatingEditorCallback and marks the resource as "edited" or not, which allows them to process the online doc, etc.
 *
 * @author drogoul
 * @since 11 avr. 2014
 *
 */
public class GamlEditorCallback extends ValidatingEditorCallback {

	@Inject
	private IResourceValidator resourceValidator;

	@Inject
	private MarkerCreator markerCreator;

	@Inject
	private MarkerTypeProvider markerTypeProvider;
	@Inject
	private IssueResolutionProvider issueResolutionProvider;

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				if ( state == null ) { return; }
				((GamlResource) state).setEdited(true);
				((GamlResource) state).setListener((GamlEditor) editor);
			}
		});

		super.afterCreatePartControl(editor);
	}

	@Override
	public void beforeDispose(final XtextEditor editor) {

		if ( editor == null || editor.getDocument() == null ) { return; }
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				((GamlResource) state).setEdited(false);
			}
		});
		super.beforeDispose(editor);
	}

	@Override
	public void afterSave(final XtextEditor editor) {
		// return;
		// TODO see if its necessary
		// super.afterSave(editor);
		if ( editor.isEditable() ) {
			ValidationJob validationJob = newValidationJob(editor);
			validationJob.setName("Validating " + editor.getPartName());
			validationJob.setUser(false);
			// validationJob.

			// validationJob.schedule(1000);
			validationJob.schedule();
		}
	}

	private ValidationJob newValidationJob(final XtextEditor editor) {
		IValidationIssueProcessor issueProcessor;
		if ( editor.getResource() == null ) {
			issueProcessor =
				new AnnotationIssueProcessor(editor.getDocument(), editor.getInternalSourceViewer()
					.getAnnotationModel(), issueResolutionProvider);
		} else {
			// GamlResource r = (GamlResource) editor.getResource();
			// if ( r.isValidating() ) { return null; }
			issueProcessor = new MarkerIssueProcessor(editor.getResource(), markerCreator, markerTypeProvider);
		}
		ValidationJob validationJob =
			new ValidationJob(resourceValidator, editor.getDocument(), issueProcessor, CheckMode.NORMAL_AND_FAST);
		return validationJob;
	}

}
