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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GAMLFile.GamlInfo;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.IModelBuilder;
import msi.gaml.descriptions.ModelDescription;

/**
 * Class GamlResourceBuilder.
 *
 * @author drogoul
 * @since 8 avr. 2014
 *
 */
@Singleton
public class GamlModelBuilder implements IModelBuilder {

	public static GamlModelBuilder INSTANCE = new GamlModelBuilder();

	@Inject IModelIndexer indexer;

	@Inject SynchronizedXtextResourceSet buildResourceSet;

	@Inject GamlResourceInfoProvider info;

	@Inject
	public GamlModelBuilder() {
	};

	/**
	 * Builds an IModel from the resource.
	 * 
	 * @param resource
	 *            must not be null
	 * @return an instance of IModel or null if the validation has returned
	 *         errors (use validate(GamlResource) to retrieve them if it is the
	 *         case, or use the alternate form).
	 */
	@Override
	public IModel compile(final Resource resource) {
		return compile(resource, new ArrayList());
	}

	/**
	 * Builds an IModel from the resource, listing all the errors, warnings and
	 * infos that occured
	 * 
	 * @param resource
	 *            must not be null
	 * @param a
	 *            list of errors, warnings and infos that occured during the
	 *            build. Must not be null and must accept the addition of new
	 *            elements
	 * @return an instance of IModel or null if the validation has returned
	 *         errors.
	 */
	@Override
	public IModel compile(final Resource resource, final List<GamlCompilationError> errors) {
		// We build the description and fill the errors list
		final ModelDescription model = buildModelDescription(resource.getURI(), errors);
		// And compile it before returning it, unless it is null.
		return model == null ? null : (IModel) model.compile();
	}

	@Override
	public ModelDescription buildModelDescription(final URI uri, final List<GamlCompilationError> errors) {
		try {
			final GamlResource r = (GamlResource) buildResourceSet.getResource(uri, true);
			ModelDescription model = null;
			// Syntactic errors detected, we cannot build the resource
			if (r.hasErrors()) {
				errors.add(new GamlCompilationError("Syntax errors detected ", IGamlIssue.GENERAL,
						r.getContents().get(0), false, false));
			} else {

				// We build the description
				model = r.buildCompleteDescription();

				// If the description has errors, we cannot build the resource
				// if (collector != null) {
				Iterables.addAll(errors, r.getErrorCollector());
				if (r.hasSemanticErrors()) {
					if (model != null) {
						model.dispose();
						model = null;
					}
				}
			}

			return model;
		} finally {
			clearResourceSet(buildResourceSet);
		}
	}

	protected void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

	@Override
	public ModelDescription createModelDescriptionFromFile(final String fileName) {

		final URI iu = URI.createURI(fileName, false);
		ModelDescription lastModel = null;

		try {
			final GamlResource r = (GamlResource) buildResourceSet.getResource(iu, true);
			final List<GamlCompilationError> errors = new ArrayList();
			lastModel = buildModelDescription(r.getURI(), errors);
			if (r.hasErrors()) {
				lastModel = null;
			}

		} catch (final GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			clearResourceSet(buildResourceSet);
		}
		return lastModel;
	}

	@Override
	public GamlInfo getInfo(final URI uri, final long modificationStamp) {
		return info.getInfo(uri, modificationStamp);
	}

}
