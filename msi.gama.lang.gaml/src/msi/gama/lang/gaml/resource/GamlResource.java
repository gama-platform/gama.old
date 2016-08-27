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
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Provider;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ImportImpl;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
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
	// in case of a synthetic resource
	private GamlResource linkToRealModelResource;

	// @Inject
	// private ImportUriResolver resolver;

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

	public void resetErrorCollector() {
		requires.clear();
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
		if (r != null && r.hasWarnings()) {
			getWarnings().addAll(r.getWarnings());
		}
	}

	@Override
	public GamlParseResult getParseResult() {
		final GamlParseResult r = (GamlParseResult) super.getParseResult();
		if (r == null) {
			return null;
		}
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
		final TreeIterator<EObject> allContents = getAllContents();

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
		final Set<URI> allAbsolutePaths = new LinkedHashSet();
		final Map<ISyntacticElement, String> microModels = new TOrderedHashMap();
		final List<ISyntacticElement> models = new ArrayList();
		for (final Map.Entry<GamlResource, String> entry : resources.entrySet()) {
			entry.getKey().getParseResult().computeAbsoluteAlternatePathsUsing(entry.getKey());
			final SyntacticModelElement m = (SyntacticModelElement) entry.getKey().getSyntacticContents();
			if (m != null) {
				allAbsolutePaths.addAll(m.getAbsoluteAlternatePaths());
				if (entry.getValue() == null) {
					models.add(m);
				} else {
					microModels.put(m, entry.getValue());
				}
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
		// hqnghi build micro-model
		// hqnghi 11/May/15: Micro-model manipulate their own imported resources
		// of micro-model , they are not supposed to be merged with co-model
		// (main-model)
		Map<String, ModelDescription> compiledMicroModels = null;
		String aliasName = null;
		while (!microModels.isEmpty()) {
			aliasName = (String) microModels.values().toArray()[0];
			final List<ISyntacticElement> res = getListMicroSyntacticElement(microModels, aliasName);
			microModels.keySet().removeAll(res);
			final ModelDescription mic = GAML.getModelFactory().createModelDescription(projectPath, modelPath, res,
					getErrorCollector(), isEdited, Collections.<String, ModelDescription> emptyMap(),
					((SyntacticModelElement) res.get(0)).getAbsoluteAlternatePaths());
			mic.setAlias(aliasName);
			if (compiledMicroModels == null)
				compiledMicroModels = new TOrderedHashMap<String, ModelDescription>();
			compiledMicroModels.put(aliasName, mic);
		}
		// end-hqnghi
		// System.out.println("Building description for model " +
		// this.getPath().lastSegment());
		if (models.isEmpty())
			return null;
		return GAML.getModelFactory().createModelDescription(projectPath, modelPath, models, getErrorCollector(),
				isEdited, compiledMicroModels, allAbsolutePaths);
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

	private void computeAllImportedURIs(final LinkedHashMap<URI, String> result, final String alias,
			final ResourceSet set) {
		if (!result.containsKey(getURI())) {
			result.put(getURI(), alias);
			final TOrderedHashMap<GamlResource, Import> imports = loadImports(set);
			for (final Map.Entry<GamlResource, Import> entry : imports.entrySet()) {
				final ImportImpl impl = (ImportImpl) entry.getValue();
				if (impl != null && impl.eIsSet(GamlPackage.IMPORT__NAME)) {
					// System.out.println("Import de " + entry.getKey() + " as "
					// + impl.getName());
					entry.getKey().computeAllImportedURIs(result, impl.getName(), set);
				} else {
					// System.out.println("Import de " + entry.getKey());
					entry.getKey().computeAllImportedURIs(result, alias, set);
				}
			}
		}
	}

	@Override
	public OnChangeEvictingCache getCache() {
		return (OnChangeEvictingCache) super.getCache();
	}

	public LinkedHashMap<URI, String> computeAllImportedURIs(final ResourceSet set) {
		return getCache().get("ImportedURIs", this, new Provider<LinkedHashMap<URI, String>>() {

			@Override
			public LinkedHashMap<URI, String> get() {
				final LinkedHashMap<URI, String> result = new LinkedHashMap();
				computeAllImportedURIs(result, null, set);
				return result;
			}
		});

	}

	public void setRealResource(final GamlResource r) {
		linkToRealModelResource = r;
	}

	public GamlResource getRealResource() {
		return linkToRealModelResource;
	}

	public TOrderedHashMap<GamlResource, Import> loadImports(final ResourceSet resourceSet) {
		final TOrderedHashMap<GamlResource, Import> localImports = new TOrderedHashMap();
		final EObject e = getContents().get(0);
		if (!(e instanceof Model)) {
			localImports.put(getRealResource(), null);
			return localImports;
		}
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

	private void invalidateBecauseOfImportedProblem(final String msg, final GamlResource resource) {
		getErrorCollector()
				.add(new GamlCompilationError(msg, IGamlIssue.GENERAL, resource.getContents().get(0), false, false));
		updateWith(null);
	}

	private boolean validateURIs() {
		final EObject object = getContents().get(0);
		if (object instanceof ModelImpl) {
			final ModelImpl model = (ModelImpl) object;
			if (model.eIsSet(GamlPackage.MODEL__IMPORTS)) {
				for (final Import imp : model.getImports()) {
					if (!checkImportUriIsValid(imp))
						return false;
				}
			}
		}
		return true;
	}

	private boolean checkImportUriIsValid(final Import object) {
		final String importURI = object.getImportURI();
		if (importURI != null && !EcoreUtil2.isValidUri(object, URI.createURI(importURI))) {
			getErrorCollector().add(new GamlCompilationError("Imported model could not be found.", IGamlIssue.GENERAL,
					object, false, false));
			return false;
		}
		return true;
	}

	private boolean validatePlugins() {
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
						return false;
					}
				}
			}
			hasValidatedPlugins = true;
		}
		return true;
	}

	private ModelDescription buildCompleteDescription(final ResourceSet set) {
		resetErrorCollector();
		if (!validateURIs()) {
			updateWith(null);
		}

		// AD Trick: if the resource does not contain any experiment, there is
		// no need to validate it entirely. It will likely be imported in a
		// model that will validate it. Unless it is edited.
		// TODO A similar trick could be used if the model is imported or not in
		// another one. If it is imported, then it means it will be validated at
		// one point in the validation of its container or if it is edited

		if (getSyntacticContents() != null && Iterables.size(getSyntacticContents().getExperiments()) == 0 && !isEdited)
			return null;

		// We make sure the resource is loaded in the ResourceSet passed
		// TODO Does it validate it ?
		// set.getResource(getURI(), true);
		ModelDescription model = null;

		// If one of the resources has already errors, no need to validate
		// We first build the list of resources (including this);
		final Map<GamlResource, String> imports = loadAllResources(set);
		boolean previousErrors = false;
		for (final GamlResource r : imports.keySet()) {
			if (!r.getErrors().isEmpty()) {
				if (r != this) {
					previousErrors = true;
					invalidateBecauseOfImportedProblem("Syntax errors detected ", r);
				}
			}
		}
		if (previousErrors)
			return null;

		model = buildModelDescription(imports);

		// If, for whatever reason, the description is null, we stop the
		// semantic validation
		if (model == null) {
			invalidateBecauseOfImportedProblem(
					"Impossible to validate model " + URI.decode(getURI().lastSegment()) + " (check the logs)", this);
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
		// if (isValidating) {
		// return;
		// }

		try {
			setValidating(true);
			// We first build the model description
			// final long begin = System.nanoTime();
			// final long mem = Runtime.getRuntime().freeMemory();
			// final long mb = 1024;

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
				GamlResourceDocManager.addCleanupTask(model);
			} else {
				model.dispose();
			}

			//
			// System.out.println("****************************************************");
			// System.out.println("Thread [" + Thread.currentThread().getName()
			// + "] | Resource set ["
			// + getResourceSet().getResources().size() + " resources]");
			// System.out.println("[" + Thread.currentThread().getName() + "] '"
			// + getURI().lastSegment()
			// + "' validated in " + (System.nanoTime() - begin) / 1000000d + "
			// ms [ ~"
			// + (mem - Runtime.getRuntime().freeMemory()) / mb + " kb used ]");
			// System.out.println("****************************************************");

			// return !getErrorCollector().hasInternalErrors();
		} catch (final Exception e) {
			e.printStackTrace();
			invalidateBecauseOfImportedProblem("An exception has occured during the validation of "
					+ URI.decode(getURI().lastSegment()) + ": " + e.getMessage(), this);
			// return false;
		} finally {
			setValidating(false);
		}
	}

	ModelDescription buildDescription(final ResourceSet set, final List<GamlCompilationError> errors) {

		// We make sure the resource is loaded in the ResourceSet passed
		set.getResource(getURI(), true);

		// System.out.println("Thread [" + Thread.currentThread().getName() +
		// "]");
		// System.out.println("Resource " + getURI().lastSegment() + "
		// building");
		// System.out.println("****************************************************");

		// Syntactic errors detected, we cannot build the resource
		if (!getErrors().isEmpty()) {
			getErrorCollector()
					.add(new GamlCompilationError("Syntax errors detected in " + URI.decode(getURI().lastSegment()),
							IGamlIssue.GENERAL, getContents().get(0), false, false));
			return null;
		}

		// We build the description
		final ModelDescription model = buildCompleteDescription(set);
		// If the description has errors, we cannot build the resource
		if (collector.hasErrors()) {
			errors.addAll(collector.getInternalErrors());
			errors.addAll(collector.getImportedErrors());
			if (model != null)
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

	public IPath getAbsoluteContainerFolderPath() {
		URI uri = getURI();
		if (uri.isFile()) {
			uri = uri.trimSegments(1);
			return Path.fromOSString(uri.path());
		}
		final IPath path = getAbsolutePath();
		if (path == null)
			return null;
		return path.uptoSegment(path.segmentCount() - 1);
	}

	public IPath getAbsolutePath() {
		if (getURI().isFile())
			return getPath();
		final IPath path = getPath();
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		final IPath fullPath = file.getLocation();
		return fullPath;
	}

	public boolean isValidating() {
		return isValidating;
	}

	private void setValidating(final boolean v) {
		isValidating = v;
	}

	public void setEdited(final boolean b) {
		isEdited = b;
		if (!b)
			getCache().clear(this);
		// GamlResourceDocManager.getInstance().document(this, b);
	}

	public boolean isEdited() {
		return isEdited;
	}

	public GamlProperties getRequires() {
		requires.remove(null);
		return requires;
	}

}
