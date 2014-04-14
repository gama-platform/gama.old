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
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.editor.validation.*;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.*;
import com.google.inject.Inject;

/**
 * The class GamlEditorCallback. Calls the functionalities of ValidatingEditorCallback for those projects who dont have
 * the "XText" nature turned on. Only when saving files.
 * 
 * @author drogoul
 * @since 11 avr. 2014
 * 
 */
public class GamlEditorCallback extends AbstractDirtyStateAwareEditorCallback {

	@Inject
	private IResourceValidator resourceValidator;

	@Inject
	private MarkerCreator markerCreator;

	@Inject
	private MarkerTypeProvider markerTypeProvider;

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {
		super.afterCreatePartControl(editor);
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				((GamlResource) state).setEdited(true);
			}
		});

		if ( editor.isEditable() ) {
			ValidationJob validationJob = newValidationJob(editor);
			validationJob.schedule();
		}
	}

	@Override
	public void beforeDispose(final XtextEditor editor) {
		super.beforeDispose(editor);
		if ( editor == null || editor.getDocument() == null ) { return; }
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				((GamlResource) state).setEdited(false);
			}
		});
	}

	@Override
	public void afterSave(final XtextEditor editor) {
		if ( editor.isEditable() ) {
			ValidationJob validationJob = newValidationJob(editor);
			validationJob.schedule();
		}
		super.afterSave(editor);
	}

	@Override
	public boolean onValidateEditorInputState(final XtextEditor editor) {
		return super.onValidateEditorInputState(editor);
	}

	@Override
	public void afterSetInput(final XtextEditor editor) {
		super.afterSetInput(editor);
	}

	private ValidationJob newValidationJob(final XtextEditor editor) {
		MarkerIssueProcessor markerIssueProcessor =
			new MarkerIssueProcessor(editor.getResource(), markerCreator, markerTypeProvider);
		ValidationJob validationJob =
			new ValidationJob(resourceValidator, editor.getDocument(), markerIssueProcessor, CheckMode.NORMAL_AND_FAST);
		return validationJob;
	}

}
