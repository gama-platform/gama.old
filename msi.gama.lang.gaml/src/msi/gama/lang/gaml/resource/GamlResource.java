/*********************************************************************************************
 *
 *
 * 'GamlResource.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import static msi.gaml.factories.DescriptionFactory.getModelFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;

import com.google.common.collect.ImmutableList;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ImportImpl;
import msi.gama.lang.gaml.parsing.GamlCompatibilityConverter;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser.GamlParseResult;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.lang.gaml.validation.IGamlBuilderListener.IGamlBuilderListener2;
import msi.gama.precompiler.GamlProperties;
import msi.gama.util.GAML;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamaBundleLoader;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.SyntacticModelElement;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;

/*
 *
 * The class GamlResource.
 *
 * @author drogoul
 *
 * @since 24 avr. 2012
 */
public class GamlResource extends LazyLinkingResource {

	private IGamlBuilderListener listener;
	private volatile ErrorCollector collector;
	private volatile boolean isValidating;
	private volatile boolean isEdited;
	private final GamlProperties requires = new GamlProperties();
	private volatile boolean hasValidatedPlugins = false;

	public ErrorCollector getErrorCollector() {
		if (collector == null) {
			collector = new ErrorCollector(this);
		}
		return collector;
	}

	@Override
	public String getEncoding() {
		return "UTF-8";
	}

	public void resetErrorCollector(final ResourceSet resourceSet) {
		requires.clear();
		// resourceSet.getLoadOptions().remove(this);
		// imports.remove(resourceSet);
		if (collector == null) {
			getErrorCollector();
		} else {
			collector.clear();
		}
	}

	@Override
	public String toString() {
		return "GAML resource" + "[" + getURI() + "]";
	}

	@Override
	protected void addSyntaxErrors() {
		super.addSyntaxErrors();
		final GamlParseResult r = getParseResult();
		if (r != null) {
			getWarnings().addAll(r.getWarnings());
		}
	}

	@Override
	public GamlParseResult getParseResult() {
		final GamlParseResult r = (GamlParseResult) super.getParseResult();
		if (r == null) {
			return null;
		}
		r.fixURIsWith(this);
		return r;
	}

	public void updateWith(final ModelDescription model) {

		if (listener != null) {
			if (listener instanceof IGamlBuilderListener2) {
				((IGamlBuilderListener2) listener)
						.validationEnded(model == null ? Collections.EMPTY_SET : model.getExperiments(), collector);
			} else {
				listener.validationEnded(model == null ? Collections.EMPTY_SET : model.getExperimentNames(), collector);
			}
		}
	}

	public void setListener(final IGamlBuilderListener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilderListener getListener() {
		return listener;
	}

	public ISyntacticElement getSyntacticContents() {
		final GamlParseResult parseResult = getParseResult();
		if (parseResult == null) { // Should not happen, but in case...
			final Set<org.eclipse.xtext.diagnostics.Diagnostic> errors = new LinkedHashSet();
			final ISyntacticElement result = GamlCompatibilityConverter.buildSyntacticContents(getContents().get(0),
					errors);
			getWarnings().addAll(errors);
			return result;
		}
		return parseResult.getSyntacticContents();
	}

	private ModelDescription buildModelDescription(final Map<GamlResource, String> resources) {

		// AD -> Nghi: microModels to use
		final Map<ISyntacticElement, String> microModels = new TOrderedHashMap();
		final List<ISyntacticElement> models = new ArrayList();
		for (final Map.Entry<GamlResource, String> entry : resources.entrySet()) {
			if (entry.getValue() == null) {
				models.add(entry.getKey().getSyntacticContents());
			} else {
				microModels.put(entry.getKey().getSyntacticContents(), entry.getValue());
			}
		}
		GAML.getExpressionFactory().resetParser();
		final IPath path = getPath();
		String modelPath, projectPath;
		if (getURI().isFile()) {
			modelPath = path.toOSString();
			projectPath = modelPath;
		} else {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			IPath fullPath = file.getLocation();
			modelPath = fullPath == null ? "" : fullPath.toOSString();
			fullPath = file.getProject().getLocation();
			projectPath = fullPath == null ? "" : fullPath.toOSString();
		}
		// GamlResourceDocManager.clearCache();
		// We document only when the resource is marked as 'edited'
		// hqnghi build micro-model
		// hqnghi 11/May/15: Micro-model manipulate their own imported resources
		// of micro-model , they are not supposed to be merged with co-model
		// (main-model)
		final Map<String, ModelDescription> mm = new TOrderedHashMap<String, ModelDescription>();
		String aliasName = null;
		while (!microModels.isEmpty()) {
			aliasName = (String) microModels.values().toArray()[0];
			final List<ISyntacticElement> res = getListMicroSyntacticElement(microModels, aliasName);
			microModels.keySet().removeAll(res);
			final ModelDescription mic = getModelFactory().createModelDescription(projectPath, modelPath, res,
					getErrorCollector(), isEdited, Collections.<String, ModelDescription> emptyMap(),
					((SyntacticModelElement) res.get(0)).getImports());
			mic.setAlias(aliasName);
			mm.put(aliasName, mic);
		}
		// end-hqnghi
		return getModelFactory().createModelDescription(projectPath, modelPath, models, getErrorCollector(), isEdited,
				mm, ((SyntacticModelElement) getSyntacticContents()).getImports());
	}

	private List<ISyntacticElement> getListMicroSyntacticElement(final Map<ISyntacticElement, String> microModels,
			final String aliasName) {
		final List<ISyntacticElement> res = new ArrayList<ISyntacticElement>();
		for (final Entry<ISyntacticElement, String> entry : microModels.entrySet()) {
			if (entry.getValue().equals(aliasName)) {
				res.add(entry.getKey());
			}
		}
		return res;
	}

	public LinkedHashMap<URI, String> computeAllImportedURIs(final ResourceSet set) {
		// TODO A Revoir pour éviter trop de créations de listes/map, etc.

		final LinkedHashMap<URI, String> result = new LinkedHashMap();
		// Map<Resource, Boolean> = if true, resources are used to compose the
		// model; if false, they are used as sub-models
		final LinkedHashMap<GamlResource, String> newResources = new LinkedHashMap();
		// The current resource is considered as imported "normally"
		newResources.put(this, null);
		while (!newResources.isEmpty()) {
			final Map<GamlResource, String> resourcesToConsider = new LinkedHashMap(newResources);
			newResources.clear();
			for (final GamlResource gr : resourcesToConsider.keySet()) {
				if (!result.containsKey(gr.getURI())) {
					result.put(gr.getURI(), resourcesToConsider.get(gr));
					final TOrderedHashMap<GamlResource, Import> imports = gr.loadImports(set);
					for (final Map.Entry<GamlResource, Import> entry : imports.entrySet()) {
						final ImportImpl impl = (ImportImpl) entry.getValue();
						if (impl.eIsSet(GamlPackage.IMPORT__NAME)) {
							newResources.put(entry.getKey(), impl.getName());
						} else {
							// "normal" Import (no "as")
							newResources.put(entry.getKey(), resourcesToConsider.get(gr));
						}
					}
				}
			}
		}
		return result;

	}

	public TOrderedHashMap<GamlResource, Import> loadImports(final ResourceSet resourceSet) {
		final TOrderedHashMap<GamlResource, Import> localImports = new TOrderedHashMap();
		final Model model = (Model) getContents().get(0);
		for (final Import imp : model.getImports()) {
			final String importUri = imp.getImportURI();
			if (importUri != null) {
				final URI iu = URI.createURI(importUri, false).resolve(getURI());
				if (EcoreUtil2.isValidUri(this, iu)) {
					final GamlResource ir = (GamlResource) resourceSet.getResource(iu, true);
					if (ir != null) {

						localImports.put(ir, imp);
					}
				}
			}
		}
		return localImports;
	}

	public LinkedHashMap<GamlResource, String> loadAllResources(final ResourceSet resourceSet) {
		final LinkedHashMap<GamlResource, String> totalResources = new LinkedHashMap();
		final Map<URI, String> uris = computeAllImportedURIs(resourceSet);
		for (final URI uri : uris.keySet()) {
			// if ( uris.get(uri) ) {
			final GamlResource ir = (GamlResource) resourceSet.getResource(uri, true);
			if (ir != null) {
				totalResources.put(ir, uris.get(uri));
			}
			// }
		}
		return totalResources;
	}

	// public EObject findImport(final URI uri) {
	// Model m = (Model) getContents().get(0);
	// for ( final Import imp : m.getImports() ) {
	// final URI iu = URI.createURI(imp.getImportURI(),
	// false).resolve(getURI());
	// if ( uri.equals(iu) ) { return imp; }
	// }
	// return null;
	// }

	private void invalidateBecauseOfImportedProblem(final String msg, final GamlResource resource) {
		getErrorCollector()
				.add(new GamlCompilationError(msg, IGamlIssue.GENERAL, resource.getContents().get(0), false, false));
		updateWith(null);
	}

	private ModelDescription buildCompleteDescription(final ResourceSet set) {
		resetErrorCollector(resourceSet);
		// We make sure the resource is loaded in the ResourceSet passed
		// TODO Does it validate it ?
		set.getResource(getURI(), true);
		ModelDescription model = null;

		if (!hasValidatedPlugins) {
			// If plugins, required for building this model, are missing in the
			// current version of GAMA
			// raise an error and abort the build.
			IPath path = getPath();
			Set<String> plugins = null;
			final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (resource != null) {
				final IProject project = resource.getProject();
				if (project != null) {
					path = resource.getProjectRelativePath();
					final String s = ".metadata/" + path.toPortableString() + ".meta";
					path = Path.fromPortableString(s);
					final IResource r = project.findMember(path);
					if (r != null && r instanceof IFile) {
						final IFile m = (IFile) r;
						BufferedReader in = null;
						try {
							in = new BufferedReader(new InputStreamReader(m.getContents()));
							final GamlProperties req = new GamlProperties(in);
							plugins = req.get(GamlProperties.PLUGINS);

						} catch (final CoreException e) {
							e.printStackTrace();
						} finally {
							if (in != null) {
								try {
									in.close();
								} catch (final IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			if (plugins != null && !plugins.isEmpty()) {
				for (final String plugin : plugins) {
					if (!GamaBundleLoader.contains(plugin)) {
						invalidateBecauseOfImportedProblem("The plugin " + plugin + " is required to run this model",
								this);
						return null;
					}
				}
			}
			hasValidatedPlugins = true;
		}

		// If one of the resources has already errors, no need to validate
		// We first build the list of resources (including this);
		final Map<GamlResource, String> imports = loadAllResources(set);
		for (final GamlResource r : imports.keySet()) {
			if (!r.getErrors().isEmpty()) {
				invalidateBecauseOfImportedProblem("Syntax errors detected ", r);
				return null;
			}
		}

		model = buildModelDescription(imports);

		// If, for whatever reason, the description is null, we stop the
		// semantic validation
		if (model == null) {
			invalidateBecauseOfImportedProblem(
					"Impossible to validate model " + getURI().lastSegment() + " (check the logs)", this);
		}
		return model;
	}

	/**
	 * Validates the resource by compiling its contents into a ModelDescription.
	 * 
	 * @return errors an ErrorCollector which contains semantic errors (as
	 *         opposed to the ones obtained via resource.getErrors(), which are
	 *         syntactic errors), This collector can be probed for compilation
	 *         errors via its hasErrors(), hasInternalErrors(),
	 *         hasImportedErrors() methods
	 *
	 */
	public void validate(final ResourceSet set) {
		// if ( isValidating ) { return; }

		try {
			setValidating(true);
			// We first build the model description
			final long begin = System.nanoTime();
			final long mem = Runtime.getRuntime().freeMemory();
			final long mb = 1024;

			final ModelDescription model = buildCompleteDescription(set);

			if (model == null) {
				return;
			}

			// We then validate it and get rid of the description. The
			// documentation is produced only if the resource is
			// marked as 'edited'
			model.validate(isEdited);
			updateWith(model);
			model.collectMetaInformation(requires);
			if (isEdited) {
				GamlResourceDocManager.addCleanupTask(new Runnable() {

					@Override
					public void run() {
						model.dispose();

					}
				});
			} else {
				model.dispose();
			}

			//
			System.out.println("****************************************************");
			System.out.println("Thread [" + Thread.currentThread().getName() + "] | Resource set ["
					+ getResourceSet().getResources().size() + " resources]");
			System.out.println("'" + getURI().lastSegment() + "' validated in " + (System.nanoTime() - begin) / 1000000d
					+ " ms [ ~" + (mem - Runtime.getRuntime().freeMemory()) / mb + " kb used ]");
			System.out.println("****************************************************");

			// return !getErrorCollector().hasInternalErrors();
		} catch (final Exception e) {
			e.printStackTrace();
			invalidateBecauseOfImportedProblem("An exception has occured during the validation of "
					+ getURI().lastSegment() + ": " + e.getMessage(), this);
			// return false;
		} finally {
			setValidating(false);
		}
	}

	ModelDescription buildDescription(final ResourceSet set, final List<GamlCompilationError> errors) {

		// We make sure the resource is loaded in the ResourceSet passed
		set.getResource(getURI(), true);

		System.out.println("Thread [" + Thread.currentThread().getName() + "]");
		System.out.println("Resource " + getURI().lastSegment() + " building");
		System.out.println("****************************************************");

		// Syntactic errors detected, we cannot build the resource
		if (!getErrors().isEmpty()) {
			getErrorCollector().add(new GamlCompilationError("Syntactic errors detected in " + getURI().lastSegment(),
					IGamlIssue.GENERAL, getContents().get(0), false, false));
			return null;
		}

		// We build the description
		final ModelDescription model = buildCompleteDescription(set);
		// If the description has errors, we cannot build the resource
		if (collector.hasErrors()) {
			errors.addAll(collector.getInternalErrors());
			errors.addAll(collector.getImportedErrors());
			model.dispose();
			return null;
		}

		errors.addAll(ImmutableList.copyOf(collector));
		return model;
	}

	public IModel build(final ResourceSet set, final List<GamlCompilationError> errors) {
		// We build the description
		final ModelDescription model = buildDescription(set, errors);
		// And compile it before returning it.
		return model == null ? null : (IModel) model.compile();
	}

	/**
	 * Returns the path from the root of the workspace
	 * 
	 * @return
	 */
	public IPath getPath() {
		IPath path;
		final URI uri = getURI();
		if (uri.isPlatform()) {
			path = new Path(getURI().toPlatformString(false));
		} else if (uri.isFile()) {
			path = new Path(uri.toFileString());
		} else {
			path = new Path(uri.path());
		}
		path = new Path(URLDecoder.decode(path.toOSString()));
		return path;
	}

	public boolean isValidating() {
		return isValidating;
	}

	private void setValidating(final boolean v) {
		isValidating = v;
	}

	public void setEdited(final boolean b) {
		isEdited = b;
		GamlResourceDocManager.getInstance().document(this, b);
	}

	public boolean isEdited() {
		return isEdited;
	}

	public GamlProperties getRequires() {
		requires.remove(null);
		return requires;
	}

}
