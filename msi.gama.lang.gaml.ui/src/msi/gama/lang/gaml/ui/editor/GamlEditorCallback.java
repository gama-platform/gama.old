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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.validation.AnnotationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.IValidationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.ui.editor.validation.MarkerIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.ValidatingEditorCallback;
import org.eclipse.xtext.ui.editor.validation.ValidationJob;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.util.concurrent.IReadAccess;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;

import com.google.inject.Inject;

import msi.gama.gui.navigator.commands.RefreshHandler;
import msi.gama.lang.gaml.resource.GamlResource;

/**
 * The class GamlEditorCallback. Calls the functionalities of
 * ValidatingEditorCallback and marks the resource as "edited" or not, which
 * allows them to process the online doc, etc.
 *
 * @author drogoul
 * @since 11 avr. 2014
 *
 */
public class GamlEditorCallback extends ValidatingEditorCallback {

	class GamlValidationJob extends ValidationJob {

		public GamlValidationJob(final IResourceValidator xtextResourceChecker,
				final IReadAccess<XtextResource> xtextDocument,
				final IValidationIssueProcessor validationIssueProcessor, final CheckMode checkMode) {
			super(xtextResourceChecker, xtextDocument, validationIssueProcessor, checkMode);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final IStatus status = super.run(monitor);
			RefreshHandler.run(null);
			return status;
		}

	}

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
				if (state == null) {
					return;
				}
				((GamlResource) state).setEdited(true);
				((GamlResource) state).setListener((GamlEditor) editor);
			}
		});

		super.afterCreatePartControl(editor);
	}

	@Override
	public void beforeDispose(final XtextEditor editor) {

		if (editor == null || editor.getDocument() == null) {
			return;
		}
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
		if (editor.isEditable()) {
			final ValidationJob validationJob = newValidationJob(editor);
			validationJob.setName("Validating " + editor.getPartName());
			validationJob.setUser(false);
			validationJob.schedule();
		}
	}

	private ValidationJob newValidationJob(final XtextEditor editor) {
		IValidationIssueProcessor issueProcessor;
		if (editor.getResource() == null) {
			issueProcessor = new AnnotationIssueProcessor(editor.getDocument(),
					editor.getInternalSourceViewer().getAnnotationModel(), issueResolutionProvider);
		} else {
			issueProcessor = new MarkerIssueProcessor(editor.getResource(), markerCreator, markerTypeProvider);
		}
		final ValidationJob validationJob = new GamlValidationJob(resourceValidator, editor.getDocument(),
				issueProcessor, CheckMode.NORMAL_AND_FAST);
		return validationJob;
	}

}
