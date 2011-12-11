package msi.gama.lang.gaml.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.ui.resource.XtextResourceSetProvider;

import com.google.inject.Injector;

import msi.gama.lang.gaml.ui.internal.GamlActivator;

public class GamlResourceSet {
	/**
	 * obtain a resource set that is configured with the appropriate project
	 * @param project
	 * @return resource set associated to the project
	 */
	public static ResourceSet get(IProject project) {
		Injector injector = GamlActivator.getInstance().getInjector("msi.gama.lang.gaml.Gaml");
		XtextResourceSetProvider provider = injector.getInstance(XtextResourceSetProvider.class);
		return provider.get(project);
	}
}
