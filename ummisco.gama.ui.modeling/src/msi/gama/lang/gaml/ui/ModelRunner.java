/**
 * Created by drogoul, 19 juin 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.ui.internal.GamlActivator;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.IModelBuilder;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class ModelRunner.
 *
 * @author drogoul
 * @since 19 juin 2016
 *
 */
@Singleton
public class ModelRunner extends AbstractServiceFactory implements IModelRunner {

	@Inject
	IModelBuilder builder;

	@Inject
	public ModelRunner() {
	}

	private void editModelInternal(final Object eObject) {
		if (eObject instanceof URI) {
			final URI uri = (URI) eObject;
			final Injector injector = GamlActivator.getInstance().getInjector("msi.gama.lang.gaml.Gaml");
			final IURIEditorOpener opener = injector.getInstance(IURIEditorOpener.class);
			opener.open(uri, true);
		} else if (eObject instanceof EObject) {
			editModelInternal(EcoreUtil.getURI((EObject) eObject));
		} else if (eObject instanceof String) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IFile file = workspace.getRoot().getFile(new Path((String) eObject));
			editModelInternal(file);
		} else if (eObject instanceof IFile) {
			final IFile file = (IFile) eObject;
			if (!file.exists()) {
				GAMA.getGui().debug("File " + file.getFullPath().toString() + " does not exist in the workspace");
				return;
			}
			try {
				final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
						.getDefaultEditor(file.getName());
				WorkbenchHelper.getPage().openEditor(new FileEditorInput(file), desc.getId());
			} catch (final PartInitException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void editModel(final Object eObject) {
		WorkbenchHelper.run(new Runnable() {

			@Override
			public void run() {
				editModelInternal(eObject);
			}
		});
	}

	@Override
	public void runModel(final Object object, final String exp) {
		if (object instanceof IModel) {
			GAMA.runGuiExperiment(exp, (IModel) object);
		} else if (object instanceof IFile) {
			final IFile file = (IFile) object;
			try {
				if (file.findMaxProblemSeverity(IMarker.PROBLEM, true,
						IResource.DEPTH_ZERO) == IMarker.SEVERITY_ERROR) {
					GAMA.getGui().error("Model " + file.getFullPath() + " has errors and cannot be launched");
					return;
				}
			} catch (final CoreException e) {
				e.printStackTrace();
			}
			final URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
			final ResourceSet rs = new /* Synchronized */XtextResourceSet();
			final GamlResource resource = (GamlResource) rs.getResource(uri, true);
			final List<GamlCompilationError> errors = new ArrayList();
			final IModel model = builder.compile(resource, errors);
			if (model == null) {
				GAMA.getGui().error("File " + file.getFullPath().toString() + " cannot be built because of "
						+ errors.size() + " compilation errors");
				return;
			}
			runModel(model, exp);
		}
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

}
