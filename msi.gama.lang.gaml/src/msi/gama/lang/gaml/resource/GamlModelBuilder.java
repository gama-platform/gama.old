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

import java.io.*;
import java.util.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gama.util.file.GAMLFile;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;

/**
 * Class GamlResourceBuilder.
 *
 * @author drogoul
 * @since 8 avr. 2014
 *
 */
public class GamlModelBuilder implements IModelBuilder {

	XtextResourceSet buildResourceSet = new XtextResourceSet();

	GamlResource fakeResource;
	static URI fakeURI = URI.createURI("temp_builder.gaml", false);

	// private static GamlModelBuilder instance = new GamlModelBuilder();
	//
	// public static GamlModelBuilder getInstance() {
	// return instance;
	// }

	public GamlModelBuilder() {

		buildResourceSet.setClasspathURIContext(GamlModelBuilder.class);
	};

	/**
	 * Validates the GAML model inside the resource and returns an ErrorCollector (which can later be probed for
	 * internal errors, imported errors, warnings or infos)
	 * @param resource must not be null
	 * @return an instance of ErrorCollector (never null)
	 */
	@Override
	public ErrorCollector validate(final Resource resource) {
		GamlResource r = (GamlResource) resource;
		r.validate(resource.getResourceSet());
		return r.getErrorCollector();
	}

	@Override
	public ErrorCollector validate(final URI resource) {
		try {
			GamlResource r = (GamlResource) buildResourceSet.createResource(resource);
			return validate(r);
		} finally {
			buildResourceSet.getResources().clear();
		}
	}

	/**
	 * Builds an IModel from the resource.
	 * @param resource must not be null
	 * @return an instance of IModel or null if the validation has returned errors (use validate(GamlResource) to
	 * retrieve them if it is the case, or use the alternate form).
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
	 * Builds an IModel from the resource, listing all the errors, warnings and infos that occured
	 * @param resource must not be null
	 * @param a list of errors, warnings and infos that occured during the build. Must not be null and must accept the
	 * addition of new elements
	 * @return an instance of IModel or null if the validation has returned errors.
	 */
	@Override
	public IModel compile(final Resource resource, final List<GamlCompilationError> errors) {
		return compile(resource.getURI(), errors);
	}

	@Override
	public IModel compile(final URI uri, final List<GamlCompilationError> errors) {
		try {
			GamlResource r = (GamlResource) buildResourceSet.createResource(uri);
			return r.build(r.getResourceSet(), errors);
		} finally {
			buildResourceSet.getResources().clear();
		}
	}

	/**
	 * Creates a model from an InputStream (which can represent the contents of a file or a string. Be aware that all the context will be lost when using this method, i.e. paths relative to the model
	 * being compiled will be resolved against the a fake URI
	 * @see msi.gama.common.interfaces.IModelBuilder#compile(java.io.InputStream, java.util.List)
	 */

	@Override
	public IModel compile(final InputStream contents, final List<GamlCompilationError> errors) {
		try {
			getFreshResource().load(contents, null);
			return compile(fakeResource, errors);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	private synchronized GamlResource getFreshResource() {
		if ( fakeResource == null ) {
			fakeResource = (GamlResource) buildResourceSet.createResource(fakeURI);
		} else {
			fakeResource.unload();
		}
		return fakeResource;
	}

	@Override
	public ModelDescription buildModelDescription(final URI uri, final List<GamlCompilationError> errors) {
		try {
			GamlResource r = (GamlResource) buildResourceSet.createResource(uri);
			return r.buildDescription(r.getResourceSet(), errors);
		} finally {
			buildResourceSet.getResources().clear();
		}
	}

	// private static final Set<String> EXTS = GamaFileType.extensionsToFullType.keySet();

	@Override
	public GAMLFile.GamlInfo getInfo(final URI uri, final long stamp) {
		/* Synchronized */XtextResourceSet infoResourceSet = new /* Synchronized */XtextResourceSet();
		try {

			GamlResource r = (GamlResource) infoResourceSet.createResource(uri);
			r.load(Collections.EMPTY_MAP);
			TreeIterator<EObject> tree = r.getAllContents();
			Set<String> imports = new TLinkedHashSet();
			Set<String> uses = new TLinkedHashSet();
			Set<String> exps = new TLinkedHashSet();
			while (tree.hasNext()) {
				EObject e = tree.next();
				if ( e instanceof StringLiteral ) {
					String s = ((StringLiteral) e).getOp();
					if ( s.length() > 4 ) {
						URI u = URI.createFileURI(s);
						String ext = u.fileExtension();
						if ( GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(ext) ) {
							uses.add(s);
						}
					}
				} else if ( e instanceof S_Experiment ) {
					String s = ((S_Experiment) e).getName();
					Map<String, Facet> f = EGaml.getFacetsMapOf((Statement) e);
					Facet typeFacet = f.get(IKeyword.TYPE);
					if ( typeFacet != null ) {
						String type = EGaml.getKeyOf(typeFacet.getExpr());
						if ( IKeyword.BATCH.equals(type) ) {
							s = GAMLFile.GamlInfo.BATCH_PREFIX + s;
						}
					}
					exps.add(s);
				} else if ( e instanceof Import ) {
					imports.add(((Import) e).getImportURI());
					tree.prune();
				}
			}

			return new GAMLFile.GamlInfo(stamp, imports, uses, exps);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} finally {
			infoResourceSet.getResources().clear();
		}
	}
}
