/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.ui;

import msi.gama.lang.gaml.validation.GamlJavaValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.validation.ValidatingEditorCallback;

/**
 * The class GamlEditorCallback.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public class GamlValidationEditorCallback extends ValidatingEditorCallback {

	// TODO See if we can trigger immediate validation. Validator already bind (but no luck in
	// validating immediately)

	// FIXME Disabled for the moment

	// @Inject
	// private ToggleXtextNatureAction toggleNature;
	//
	// @Override
	// public void afterCreatePartControl(final XtextEditor editor) {
	// super.afterCreatePartControl(editor);
	// // Add the Xtext nature to a project when editing a contained Xtext resource without asking
	// IResource resource = editor.getResource();
	// if ( resource != null && !toggleNature.hasNature(resource.getProject()) &&
	// resource.getProject().isAccessible() ) {
	// toggleNature.toggleNature(resource.getProject());
	// }
	// }

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {
		// super.afterCreatePartControl(editor);
		// System.out.println("Starting validation on create part control");
		// IFile fileToRun = (IFile) editor.getResource();
		// ResourceSet rs = GamlResourceSet.get(fileToRun.getProject());
		// String p = fileToRun.getFullPath().toString();
		// URI u = URI.createPlatformResourceURI(p, true);
		// Resource resource = rs.getResource(u, true);
		// Convert.validate(resource);
	}

	@Override
	public boolean onValidateEditorInputState(final XtextEditor editor) {

		System.out.println("Starting validation on validate input state");
		IFile fileToRun = (IFile) editor.getResource();
		ResourceSet rs = GamlResourceSet.get(fileToRun.getProject());
		String p = fileToRun.getFullPath().toString();
		URI u = URI.createPlatformResourceURI(p, true);
		Resource resource = rs.getResource(u, true);
		GamlJavaValidator.validate(resource);
		return super.onValidateEditorInputState(editor);
	}
}
