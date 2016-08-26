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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.utils.EGaml;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GAMLFile;
import msi.gaml.compilation.GamaBundleLoader;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.IModelBuilder;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;

/**
 * Class GamlResourceBuilder.
 *
 * @author drogoul
 * @since 8 avr. 2014
 *
 */
public class GamlModelBuilder implements IModelBuilder {

	final XtextResourceSet buildResourceSet = new XtextResourceSet();
	GamlResource fakeResource;
	static URI fakeURI = URI.createURI("temp_builder.gaml", false);

	public GamlModelBuilder() {
		buildResourceSet.setClasspathURIContext(GamlModelBuilder.class);
	};

	/**
	 * Validates the GAML model inside the resource and returns an
	 * ErrorCollector (which can later be probed for internal errors, imported
	 * errors, warnings or infos)
	 * 
	 * @param resource
	 *            must not be null
	 * @return an instance of ErrorCollector (never null)
	 */
	@Override
	public ErrorCollector validate(final Resource resource) {
		final GamlResource r = (GamlResource) resource;
		r.validate(resource.getResourceSet());
		return r.getErrorCollector();
	}

	@Override
	public ErrorCollector validate(final URI resource) {
		try {
			final GamlResource r = (GamlResource) buildResourceSet.createResource(resource);
			return validate(r);
		} finally {
			buildResourceSet.getResources().clear();
		}
	}

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
		return compile(resource.getURI());
	}

	@Override
	public IModel compile(final URI uri) {
		return compile(uri, new ArrayList());
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
		return compile(resource.getURI(), errors);
	}

	@Override
	public IModel compile(final URI uri, final List<GamlCompilationError> errors) {
		try {
			final GamlResource r = (GamlResource) buildResourceSet.createResource(uri);
			return r.build(r.getResourceSet(), errors);
		} finally {
			buildResourceSet.getResources().clear();
		}
	}

	/**
	 * Creates a model from an InputStream (which can represent the contents of
	 * a file or a string. Be aware that all the context will be lost when using
	 * this method, i.e. paths relative to the model being compiled will be
	 * resolved against the a fake URI
	 * 
	 * @see msi.gama.common.interfaces.IModelBuilder#compile(java.io.InputStream,
	 *      java.util.List)
	 */

	@Override
	public IModel compile(final InputStream contents, final List<GamlCompilationError> errors) {
		try {
			getFreshResource().load(contents, null);
			return compile(fakeResource, errors);
		} catch (final Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	private synchronized GamlResource getFreshResource() {
		if (fakeResource == null) {
			fakeResource = (GamlResource) buildResourceSet.createResource(fakeURI);
		} else {
			fakeResource.unload();
		}
		return fakeResource;
	}

	@Override
	public ModelDescription buildModelDescription(final URI uri, final List<GamlCompilationError> errors) {
		try {
			final GamlResource r = (GamlResource) buildResourceSet.createResource(uri);
			return r.buildDescription(r.getResourceSet(), errors);
		} finally {
			buildResourceSet.getResources().clear();
		}
	}

	@Override
	public GAMLFile.GamlInfo getInfo(final URI uri, final long stamp) {
		/* Synchronized */final XtextResourceSet infoResourceSet = new /* Synchronized */XtextResourceSet();
		try {

			final GamlResource r = (GamlResource) infoResourceSet.createResource(uri);
			r.load(Collections.EMPTY_MAP);
			final TreeIterator<EObject> tree = r.getAllContents();
			final Set<String> imports = new TLinkedHashSet();
			final Set<String> uses = new TLinkedHashSet();
			final Set<String> exps = new TLinkedHashSet();
			while (tree.hasNext()) {
				final EObject e = tree.next();
				if (e instanceof StringLiteral) {
					final String s = ((StringLiteral) e).getOp();
					if (s.length() > 4) {
						final URI u = URI.createFileURI(s);
						final String ext = u.fileExtension();
						if (GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(ext)) {
							uses.add(s);
						}
					}
				} else if (e instanceof S_Experiment) {
					String s = ((S_Experiment) e).getName();
					final Map<String, Facet> f = EGaml.getFacetsMapOf((Statement) e);
					final Facet typeFacet = f.get(IKeyword.TYPE);
					if (typeFacet != null) {
						final String type = EGaml.getKeyOf(typeFacet.getExpr());
						if (IKeyword.BATCH.equals(type)) {
							s = GAMLFile.GamlInfo.BATCH_PREFIX + s;
						}
					}
					exps.add(s);
				} else if (e instanceof Import) {
					imports.add(((Import) e).getImportURI());
					tree.prune();
				}
			}

			return new GAMLFile.GamlInfo(stamp, imports, uses, exps);
		} catch (final IOException e1) {
			e1.printStackTrace();
			return null;
		} finally {
			infoResourceSet.getResources().clear();
		}
	}

	@Override
	public ModelDescription createModelDescriptionFromFile(final String fileName) {

		// final GamlResource resource = (GamlResource)
		// getContext().getModelDescription().getUnderlyingElement(null)
		// .eResource();

		final URI iu = URI.createURI(fileName, false);
		ModelDescription lastModel = null;
		// final ResourceSet rs = new ResourceSetImpl();
		final GamlResource r = (GamlResource) buildResourceSet.getResource(iu, true);
		try {
			// final GamlJavaValidator validator = new GamlJavaValidator();
			final List<GamlCompilationError> errors = new ArrayList();
			lastModel = buildModelDescription(r.getURI(), errors);
			if (!r.getErrors().isEmpty()) {
				lastModel = null;
			}

		} catch (final GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
		}
		return lastModel;
	}

}
