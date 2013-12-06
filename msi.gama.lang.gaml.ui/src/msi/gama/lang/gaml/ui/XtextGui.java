/**
 * Created by drogoul, 21 janv. 2013
 * 
 */
package msi.gama.lang.gaml.ui;

import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.ui.internal.GamlActivator;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.runtime.GAMA;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import com.google.inject.Injector;

/**
 * The class XtextGui.
 * 
 * @author drogoul
 * @since 21 janv. 2013
 * 
 */
public class XtextGui extends msi.gama.gui.swt.SwtGui {

	@Override
	public void editModel(final Object eObject) {
		if ( !(eObject instanceof EObject) ) {
			super.editModel(eObject);
			return;
		}
		URI uri = EcoreUtil.getURI((EObject) eObject);
		Injector injector = GamlActivator.getInstance().getInjector("msi.gama.lang.gaml.Gaml");
		IURIEditorOpener opener = injector.getInstance(IURIEditorOpener.class);
		opener.open(uri, true);
	}

	@Override
	public void runModel(final Object object, final String exp) throws CoreException {
		if ( object instanceof IFile ) {
			IFile file = (IFile) object;
			if ( file.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO) == IMarker.SEVERITY_ERROR ) {
				error("Model " + file.getFullPath() + " has errors and cannot be launched");
				return;
			}
			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
			ResourceSet rs = new SynchronizedXtextResourceSet();
			GamlResource resource = (GamlResource) rs.getResource(uri, true);
			GamlJavaValidator validator =
				IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(resource.getURI()).get(
					GamlJavaValidator.class);
			IModel model = validator.build(resource);
			if ( model == null ) {
				error("File " + file.getFullPath().toString() + " cannot be built");
				return;
			}
			GuiUtils.openSimulationPerspective();
			GAMA.controller.newExperiment(exp, model);
		}
	}
}
