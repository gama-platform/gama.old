/**
 * Created by drogoul, 24 avr. 2013
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.*;
import msi.gama.lang.gaml.trials.GamlDescriptionUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.editor.validation.*;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.validation.*;
import com.google.inject.Inject;

/**
 * The class GamlEditorCallback.
 * 
 * @author drogoul
 * @since 24 avr. 2013
 * 
 */

public class GamlEditorCallback extends /* NatureAddingEditorCallback */AbstractDirtyStateAwareEditorCallback {

	@Inject
	private IResourceValidator resourceValidator;

	@Inject
	private MarkerCreator markerCreator;

	@Inject
	private MarkerTypeProvider markerTypeProvider;

	private final DescriptionUtils descriptionUtils = new GamlDescriptionUtils();

	@Inject
	IResourceDescriptions index;

	// @Inject
	// private DirtyStateEditorSupport editorSupport;

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {

		super.afterCreatePartControl(editor);
		if ( editor.isEditable() ) {
			ValidationJob validationJob = newValidationJob(editor);
			validationJob.schedule();
		}

		// getDocument().addModelListener(new IXtextModelListener() {
		//
		// @Override
		// public void modelChanged(final XtextResource resource) {
		// GuiUtils.debug("    >>>>model changed: " + resource.getURI().lastSegment());
		// Set<URI> uris = findDependentResources(resource);
		// GuiUtils.debug("    >>>>affected: " + uris);
		// // ((GamlResource) resource).eraseSyntacticContents();
		// ResourceSet rs = resource.getResourceSet();
		// // for ( URI uri : uris ) {
		// // GamlResource gr = (GamlResource) rs.getResource(uri, true);
		// // if ( gr != null ) {
		// // gr.eraseSyntacticContents();
		// // }
		// // }
		// }
		// });
		// super.afterCreatePartControl(editor);
	}

	private Set<URI> findDependentResources(final XtextResource resource) {
		URI uri = resource.getURI();
		Set<URI> result = new LinkedHashSet();
		for ( IResourceDescription rd : index.getAllResourceDescriptions() ) {
			Set<URI> uris = descriptionUtils.collectOutgoingReferences(rd);
			if ( uris.contains(uri) ) {
				result.add(rd.getURI());
			}
		}
		return result;
	}

	// @Override
	// public void afterSave(XtextEditor editor) {
	// // GuiUtils.debug("GamlEditorCallback.afterSave");
	// // getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
	// //
	// // @Override
	// // public void process(XtextResource state) throws Exception {
	// // ((GamlResource) state).eraseSyntacticContents();
	// // }
	// // });
	// // super.afterSave(editor);
	// }

	private ValidationJob newValidationJob(final XtextEditor editor) {
		MarkerIssueProcessor markerIssueProcessor =
			new MarkerIssueProcessor(editor.getResource(), markerCreator, markerTypeProvider);
		ValidationJob validationJob =
			new ValidationJob(resourceValidator, editor.getDocument(), markerIssueProcessor, CheckMode.FAST_ONLY);
		return validationJob;
	}

}
