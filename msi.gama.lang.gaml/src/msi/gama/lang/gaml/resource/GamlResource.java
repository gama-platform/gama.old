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

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.inject.Inject;

import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.documentation.GamlResourceDocManager;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser.GamlParseResult;
import msi.gama.precompiler.GamlProperties;
import msi.gama.util.GAML;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.SyntacticModelElement;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.factories.DescriptionFactory.IDocManager;

/*
 *
 * The class GamlResource.
 *
 * @author drogoul
 *
 * @since 24 avr. 2012
 */
public class GamlResource extends LazyLinkingResource {

	// private IGamlBuilderListener listener;
	private volatile ErrorCollector collector;
	private volatile boolean isValidating;
	private final GamlProperties requires = new GamlProperties();

	@Inject
	IModelIndexer indexer;

	@Inject
	IDocManager documenter;

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

	// @Override
	// protected void addSyntaxErrors() {
	// super.addSyntaxErrors();
	// final GamlParseResult r = getParseResult();
	// // if (r != null && r.hasWarnings()) {
	// // getWarnings().addAll(r.getWarnings());
	// // }
	// }

	@Override
	public GamlParseResult getParseResult() {
		return (GamlParseResult) super.getParseResult();

	}

	@Override
	protected void updateInternalState(final IParseResult oldParseResult, final IParseResult newParseResult) {
		super.updateInternalState(oldParseResult, newParseResult);

	}

	public void updateWith(final ModelDescription model, final boolean newState) {
		indexer.updateState(getURI(), model, newState, getErrorCollector());
	}

	// public void setListener(final IGamlBuilderListener listener) {
	// this.listener = listener;
	// if (listener != null) {
	// updateWith(null, false);
	// }
	// }

	public void removeListener() {
		// setListener(null);
		((OnChangeEvictingCache) getCache()).getOrCreate(this).set(IDocManager.KEY, null);
	}

	public ISyntacticElement getSyntacticContents() {
		final GamlParseResult parseResult = getParseResult();
		return parseResult.getSyntacticContents();
	}

	private ModelDescription buildModelDescription(final TOrderedHashMap<GamlResource, String> resources) {

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
					getErrorCollector(), isEdited(), Collections.<String, ModelDescription> emptyMap(),
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
				isEdited(), compiledMicroModels, allAbsolutePaths);
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

	public TOrderedHashMap<GamlResource, String> loadAllResources() {
		final TOrderedHashMap<GamlResource, String> totalResources = new TOrderedHashMap();
		final TOrderedHashMap<URI, String> uris = indexer.allLabeledImportsOf(this);
		uris.forEachEntry(new TObjectObjectProcedure<URI, String>() {

			@Override
			public boolean execute(final URI uri, final String label) {
				final GamlResource ir = (GamlResource) resourceSet.getResource(uri, true);
				if (ir != null) {
					totalResources.put(ir, label);
				}
				return true;
			}
		});
		return totalResources;
	}

	public void invalidateBecauseOfImportedProblem(final Map<GamlResource, String> problems) {
		for (final Map.Entry<GamlResource, String> problem : problems.entrySet())
			getErrorCollector().add(new GamlCompilationError(problem.getValue(), IGamlIssue.GENERAL,
					problem.getKey().getContents().get(0), false, false));
		updateWith(null, true);
	}

	private ModelDescription buildCompleteDescription() {
		resetErrorCollector();

		// If the imports are not correctly updated, we cannot proceed
		if (indexer != null) {
			if (!indexer.updateImports(this)) {
				updateWith(null, true);
				return null;
			}
		}
		// If one of the resources has already errors, no need to validate
		// We first build the list of resources (including this);
		final TOrderedHashMap<GamlResource, String> imports = loadAllResources();

		final boolean importsOK = imports.forEachKey(new TObjectProcedure<GamlResource>() {

			@Override
			public boolean execute(final GamlResource r) {
				if (!r.getErrors().isEmpty()) {
					final Map<GamlResource, String> problems = new HashMap();
					problems.put(r, "Syntax errors detected");
					invalidateBecauseOfImportedProblem(problems);
					return false;
				}
				return true;
			}

		});
		if (!importsOK)
			return null;

		// AD 08/16: if the model is imported and not edited, we do nothing. If
		// it is imported, then it means it will be validated at one point
		// together with its importer. Otherwise, we do not care about its
		// validation. Saves a lot of memory and validation speed.

		final boolean imported = indexer.isImported(getURI());
		final boolean edited = isEdited();
		if (imported && !edited)
			return null;

		ModelDescription model = null;

		model = buildModelDescription(imports);

		// If, for whatever reason, the description is null, we stop the
		// semantic validation
		if (model == null) {
			final Map<GamlResource, String> problems = new HashMap();
			problems.put(this,
					"Impossible to validate model " + URI.decode(getURI().lastSegment()) + " (check the logs)");
			invalidateBecauseOfImportedProblem(problems);
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
	public void validate() {
		try {
			setValidating(true);
			final ModelDescription model = buildCompleteDescription();

			if (model == null) {
				return;
			}

			// We then validate it and get rid of the description. The
			// documentation is produced only if the resource is
			// marked as 'edited'
			model.validate(isEdited());
			updateWith(model, true);

			model.collectMetaInformation(requires);
			if (isEdited()) {
				GamlResourceDocManager.INSTANCE.addCleanupTask(model);
			} else {
				model.dispose();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			final Map<GamlResource, String> problems = new HashMap();
			problems.put(this, "An exception has occured during the validation of " + URI.decode(getURI().lastSegment())
					+ ": " + e.getMessage());
		} finally {
			setValidating(false);
		}
	}

	ModelDescription buildDescription(final List<GamlCompilationError> errors) {

		// Syntactic errors detected, we cannot build the resource
		if (!getErrors().isEmpty()) {
			getErrorCollector()
					.add(new GamlCompilationError("Syntax errors detected in " + URI.decode(getURI().lastSegment()),
							IGamlIssue.GENERAL, getContents().get(0), false, false));
			return null;
		}

		// We build the description
		final ModelDescription model = buildCompleteDescription();
		// If the description has errors, we cannot build the resource
		if (collector.hasErrors()) {
			errors.addAll(collector.getInternalErrors());
			errors.addAll(collector.getImportedErrors());
			if (model != null)
				model.dispose();
			return null;
		}

		for (final GamlCompilationError e : collector)
			errors.add(e);
		return model;
	}

	public IModel build(final List<GamlCompilationError> errors) {
		// We build the description and fill the errors list
		final ModelDescription model = buildDescription(errors);
		// And compile it before returning it, unless it is null.
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

	public boolean isEdited() {
		return indexer.isEdited(getURI());
	}

	public GamlProperties getRequires() {
		requires.remove(null);
		return requires;
	}

	@Override
	public void load(final Map<?, ?> options) throws IOException {
		super.load(options);
	}

}
