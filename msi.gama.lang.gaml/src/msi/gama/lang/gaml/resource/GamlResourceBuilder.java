/*********************************************************************************************
 * 
 * 
 * 'GamlResourceBuilder.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import java.util.List;
import msi.gama.kernel.model.IModel;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;

/**
 * Class GamlResourceBuilder.
 * 
 * @author drogoul
 * @since 8 avr. 2014
 * 
 */
public class GamlResourceBuilder {

	static SynchronizedXtextResourceSet buildResourceSet = new SynchronizedXtextResourceSet();

	private static GamlResourceBuilder instance = new GamlResourceBuilder();

	public static GamlResourceBuilder getInstance() {
		return instance;
	}

	private GamlResourceBuilder() {};

	/**
	 * Validates the GAML model inside the resource and returns an ErrorCollector (which can later be probed for
	 * internal errors, imported errors, warnings or infos)
	 * @param resource must not be null
	 * @return an instance of ErrorCollector (never null)
	 */
	public ErrorCollector validate(final GamlResource resource) {
		try {
			resource.validate(resource.getResourceSet());
			return resource.getErrorCollector();
		} finally {
			// for ( Resource r : buildResourceSet.getResources() ) {
			// r.unload();
			// }
			// buildResourceSet.getResources().clear();
		}
	}

	/**
	 * Builds an IModel from the resource.
	 * @param resource must not be null
	 * @return an instance of IModel or null if the validation has returned errors (use validate(GamlResource) to
	 *         retrieve them if it is the case, or use the alternate form).
	 */
	public IModel build(final GamlResource resource) {
		try {
			GamlResource r = (GamlResource) buildResourceSet.getResource(resource.getURI(), true);
			return r.build(r.getResourceSet());
		} finally {
			// for ( Resource r : buildResourceSet.getResources() ) {
			// r.unload();
			// }
			buildResourceSet.getResources().clear();
		}
	}

	/**
	 * Builds an IModel from the resource, listing all the errors, warnings and infos that occured
	 * @param resource must not be null
	 * @param a list of errors, warnings and infos that occured during the build. Must not be null and must accept the
	 *            addition of new elements
	 * @return an instance of IModel or null if the validation has returned errors.
	 */
	public IModel build(final GamlResource resource, final List<GamlCompilationError> errors) {
		try {
			GamlResource r = (GamlResource) buildResourceSet.getResource(resource.getURI(), true);
			return resource.build(r.getResourceSet(), errors);
		} finally {
			// for ( Resource r : buildResourceSet.getResources() ) {
			// r.unload();
			// }
			buildResourceSet.getResources().clear();
		}
	}

}
