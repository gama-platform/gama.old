/*********************************************************************************************
 *
 * 'GamlEditorTickUpdater.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextEditorErrorTickUpdater;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.validation.AnnotationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.IValidationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.ui.editor.validation.MarkerIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.ValidationJob;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;

import com.google.inject.Inject;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The class GamlEditorTickUpdater.
 * 
 * @author drogoul
 * @since 8 sept. 2013
 * 
 */
public class GamlEditorTickUpdater extends XtextEditorErrorTickUpdater {

	@Inject private IResourceValidator resourceValidator;
	@Inject private MarkerCreator markerCreator;
	@Inject private MarkerTypeProvider markerTypeProvider;
	@Inject private IssueResolutionProvider issueResolutionProvider;

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {
		super.afterCreatePartControl(editor);
		if (editor.isEditable()) {
			final ValidationJob validationJob = newValidationJob(editor);
			validationJob.schedule();
		}
	}

	private ValidationJob newValidationJob(final XtextEditor editor) {
		IValidationIssueProcessor issueProcessor;
		if (editor.getResource() == null) {
			issueProcessor = new AnnotationIssueProcessor(editor.getDocument(),
					editor.getInternalSourceViewer().getAnnotationModel(), issueResolutionProvider);
		} else {
			issueProcessor = new MarkerIssueProcessor(editor.getResource(),
					editor.getInternalSourceViewer().getAnnotationModel(), markerCreator, markerTypeProvider);
		}
		final ValidationJob validationJob =
				new ValidationJob(resourceValidator, editor.getDocument(), issueProcessor, CheckMode.NORMAL_AND_FAST);
		return validationJob;
	}

	@Override
	protected void updateEditorImage(final XtextEditor editor) {
		Severity severity = null;
		//
		// try {
		severity = getSeverity(editor);
		//
		// } catch (ResourceException e) {
		// // do nothing, emitted when a marker cannot be found
		// }
		ImageDescriptor descriptor = null;
		if (severity == null || severity == Severity.INFO) {
			descriptor = GamaIcons.create(IGamaIcons.OVERLAY_OK).descriptor();
		} else if (severity == Severity.ERROR) {
			descriptor = GamaIcons.create("overlay.error2").descriptor();
		} else if (severity == Severity.WARNING) {
			descriptor = GamaIcons.create("overlay.warning2").descriptor();
		} else {
			super.updateEditorImage(editor);
			return;
		}
		final DecorationOverlayIcon decorationOverlayIcon =
				new DecorationOverlayIcon(editor.getDefaultImage(), descriptor, IDecoration.BOTTOM_LEFT);
		scheduleUpdateEditor(decorationOverlayIcon);

	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		// TODO A place where we can hook something ? (for instance the feedback
		// to another editor, etc..
		super.modelChanged(model);
	}

}
