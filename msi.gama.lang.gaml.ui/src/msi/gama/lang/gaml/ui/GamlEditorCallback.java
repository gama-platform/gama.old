/**
 * Created by drogoul, 24 avr. 2013
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.*;
import msi.gama.common.util.GuiUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.ui.editor.validation.ValidatingEditorCallback;
import com.google.inject.Inject;

/**
 * The class GamlEditorCallback.
 * 
 * @author drogoul
 * @since 24 avr. 2013
 * 
 */
@Deprecated
public class GamlEditorCallback extends ValidatingEditorCallback {

	@Inject
	private DescriptionUtils descriptionUtils;

	@Inject
	IResourceDescriptions index;

	@Override
	public void afterCreatePartControl(XtextEditor editor) {
		getDocument().addModelListener(new IXtextModelListener() {

			@Override
			public void modelChanged(XtextResource resource) {
				GuiUtils.debug("    >>>>model changed: " + resource.getURI().lastSegment());
				Set<URI> uris = findDependentResources(resource);
				GuiUtils.debug("    >>>>affected: " + uris);
				// ((GamlResource) resource).eraseSyntacticContents();
				ResourceSet rs = resource.getResourceSet();
				// for ( URI uri : uris ) {
				// GamlResource gr = (GamlResource) rs.getResource(uri, true);
				// if ( gr != null ) {
				// gr.eraseSyntacticContents();
				// }
				// }
			}
		});
		super.afterCreatePartControl(editor);
	}

	private Set<URI> findDependentResources(XtextResource resource) {
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

	@Override
	public void afterSave(XtextEditor editor) {
		GuiUtils.debug("GamlEditorCallback.afterSave");
		// getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
		//
		// @Override
		// public void process(XtextResource state) throws Exception {
		// ((GamlResource) state).eraseSyntacticContents();
		// }
		// });
		// super.afterSave(editor);
	}
}
