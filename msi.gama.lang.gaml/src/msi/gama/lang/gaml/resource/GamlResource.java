/*******************************************************************************************************
 *
 * GamlResource.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static java.util.Collections.singleton;
import static msi.gama.lang.gaml.indexer.GamlResourceIndexer.collectMultipleImportsOf;
import static msi.gama.lang.gaml.indexer.GamlResourceIndexer.getImportObject;
import static msi.gama.lang.gaml.indexer.GamlResourceIndexer.updateImports;
import static msi.gama.lang.gaml.resource.GamlResourceServices.properlyEncodedURI;
import static msi.gama.lang.gaml.resource.GamlResourceServices.updateState;
import static msi.gaml.compilation.GAML.getModelFactory;
import static msi.gaml.interfaces.IGamlIssue.GENERAL;
import static msi.gaml.interfaces.IGamlIssue.IMPORT_ERROR;
import static org.eclipse.emf.ecore.util.EcoreUtil.resolveAll;
import static org.eclipse.xtext.diagnostics.Severity.ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.util.OnChangeEvictingCache;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.runtime.IExecutionContext;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

/**
 * The Class GamlResource.
 */
/*
 *
 * The class GamlResource.
 *
 * @author drogoul
 *
 * @since 24 avr. 2012
 */
public class GamlResource extends LazyLinkingResource {

	/**
	 * The Class ImportedResources.
	 */
	public static class ImportedResources {
		/** The micromodels. */
		public ListMultimap<String, GamlResource> micromodels;
		/** The imports. */
		public Set<GamlResource> imports;

		/**
		 * Adds the.
		 *
		 * @param alias
		 *            the alias
		 * @param resource
		 *            the resource
		 */
		public void add(final String alias, final GamlResource resource) {
			if (alias == null) {
				addOwnImport(resource);
			} else {
				addMicroModel(alias, resource);
			}
		}

		/**
		 * Adds the micro model.
		 *
		 * @param alias
		 *            the alias
		 * @param resource
		 *            the resource
		 */
		private void addMicroModel(final String alias, final GamlResource resource) {
			if (micromodels == null) { micromodels = ArrayListMultimap.create(); }
			micromodels.put(alias, resource);
		}

		/**
		 * Adds the own import.
		 *
		 * @param resource
		 *            the resource
		 */
		private void addOwnImport(final GamlResource resource) {
			if (imports == null) { imports = Sets.newLinkedHashSet(); }
			imports.add(resource);
		}

		/**
		 * Compute direct imports.
		 *
		 * @param syntacticContents
		 *            the syntactic contents
		 * @return the iterable
		 */
		public Iterable<ISyntacticElement> computeDirectImports(final ISyntacticElement syntacticContents) {
			return imports == null ? singleton(syntacticContents)
					: concat(singleton(syntacticContents), transform(imports, TO_SYNTACTIC_CONTENTS));
		}

		/**
		 * Compute micro models.
		 *
		 * @return the map
		 */
		public Map<String, ModelDescription> computeMicroModels(final String project, final String model,
				final ValidationContext context) {
			if (micromodels == null) return null;
			Map<String, ModelDescription> result = Maps.newHashMap();

			for (final String aliasName : micromodels.keySet()) {
				final ModelDescription mic = getModelFactory().createModelDescription(project, model,
						transform(micromodels.get(aliasName), TO_SYNTACTIC_CONTENTS), context, null);
				mic.setAlias(aliasName);
				result.put(aliasName, mic);
			}
			return result;
		}
	}

	/** The element. */
	ISyntacticElement element;

	/** The imports. */
	// Map<URI, String> imports;

	/**
	 * Gets the validation context.
	 *
	 * @return the validation context
	 */
	public ValidationContext getValidationContext() {
		return GamlResourceServices.getOrCreateValidationContext(this);
	}

	/**
	 * Checks for semantic errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasSemanticErrors() {
		return getValidationContext().hasErrors();
	}

	@Override
	public String getEncoding() { return "UTF-8"; }

	@Override
	public String toString() {
		return "GamlResource[" + getURI().lastSegment() + "]";
	}

	/**
	 * Update with.
	 *
	 * @param model
	 *            the model
	 * @param newState
	 *            the new state
	 */
	public void updateWith(final ModelDescription model, final boolean newState) {
		updateState(getURI(), model, newState, getValidationContext());
	}

	/**
	 * Gets the syntactic contents.
	 *
	 * @return the syntactic contents
	 */
	public ISyntacticElement getSyntacticContents() {
		if (element == null) { setElement(GamlResourceServices.buildSyntacticContents(this)); }
		return element;
	}

	/** The Constant TO_SYNTACTIC_CONTENTS. */
	private final static Function<GamlResource, ISyntacticElement> TO_SYNTACTIC_CONTENTS = input -> {
		input.getResourceSet().getResource(input.getURI(), true);
		return input.getSyntacticContents();
	};

	/**
	 * Builds the model description.
	 *
	 * @param resources
	 *            the resources
	 * @return the model description
	 */
	private ModelDescription buildModelDescription(final ImportedResources resources) {
		GAML.getExpressionFactory().resetParser();
		final String model = GamlResourceServices.getModelPathOf(this);
		final String project = GamlResourceServices.getProjectPathOf(this);
		final ValidationContext context = getValidationContext();
		context.shouldDocument(GamlResourceServices.isEdited(this));
		if (resources == null) return getModelFactory().createModelDescription(project, model,
				singleton(getSyntacticContents()), getValidationContext(), null);
		Iterable<ISyntacticElement> imports = resources.computeDirectImports(getSyntacticContents());
		return getModelFactory().createModelDescription(project, model, imports, context,
				resources.computeMicroModels(project, model, context));
	}

	/**
	 * Invalidate.
	 *
	 * @param r
	 *            the r
	 * @param s
	 *            the s
	 */
	public void invalidate(final GamlResource r, final String s) {
		GamlCompilationError error = null;
		if (GamlResourceServices.equals(r.getURI(), getURI())) {
			error = new GamlCompilationError(s, GENERAL, r.getContents().get(0), false, false);
		} else {
			error = new GamlCompilationError(s, GENERAL, r.getURI(), false, false);
		}
		getValidationContext().add(error);
		updateWith(null, true);
	}

	/**
	 * Builds the complete description.
	 *
	 * @return the model description
	 */
	public ModelDescription buildCompleteDescription() {
		final ImportedResources imports = GamlResourceIndexer.validateImportsOf(this);
		if (hasErrors() || hasSemanticErrors()) return null;
		final ModelDescription model = buildModelDescription(imports);
		// If, for whatever reason, the description is null, we stop the
		// semantic validation
		if (model == null) {
			invalidate(this, "Impossible to validate " + URI.decode(getURI().lastSegment()) + " (check the logs)");
		}
		Map<URI, URI> doubleImports = collectMultipleImportsOf(this);
		doubleImports.forEach((imported, importer) -> {
			String s = imported.lastSegment() + " is already imported by " + importer.lastSegment();
			EObject o = getImportObject(getContents().get(0), getURI(), imported);
			GamlCompilationError error = new GamlCompilationError(s, GENERAL, o, false, true);
			getValidationContext().add(error);
		});
		return model;
	}

	/**
	 * Validates the resource by compiling its contents into a ModelDescription and discarding this ModelDescription
	 * afterwards
	 *
	 * @note The errors will be available as part of the ValidationContext, which can later be retrieved from the
	 *       resource, and which contains semantic errors (as opposed to the ones obtained via resource.getErrors(),
	 *       which are syntactic errors), This collector can be probed for compilation errors via its hasErrors(),
	 *       hasInternalErrors(), hasImportedErrors() methods
	 *
	 */
	public void validate() {
		// DEBUG.LOG("Resource validating itself");
		final ModelDescription model = buildCompleteDescription();
		if (model == null) {
			updateWith(null, true);
			return;
		}
		// We then validate it and get rid of the description.
		try {
			updateWith(model.validate(), true);
		} finally {
			// make sure to get rid of the model only after its documentation has been produced
			if (GamlResourceServices.isEdited(this.getURI())) {
				GamlResourceServices.getResourceDocumenter().addDocumentationTask(d -> model.dispose());
			} else {
				model.dispose();
			}
			// }
		}
	}

	@Override
	protected void updateInternalState(final IParseResult oldParseResult, final IParseResult newParseResult) {
		super.updateInternalState(oldParseResult, newParseResult);
		setElement(null);
	}

	@Override
	protected void clearInternalState() {
		super.clearInternalState();
		setElement(null);
	}

	@Override
	protected void doUnload() {
		super.doUnload();
		setElement(null);
	}

	/**
	 * Sets the element.
	 *
	 * @param model
	 *            the new element
	 */
	private void setElement(final ISyntacticElement model) {
		if (model == element) return;
		if (element != null) { element.dispose(); }
		element = model;
	}

	/**
	 * In the case of synthetic resources, pass the URI they depend on
	 *
	 * @throws IOException
	 */
	public void loadSynthetic(final InputStream is, final IExecutionContext additionalLinkingContext) {
		final OnChangeEvictingCache r = getCache();
		r.getOrCreate(this).set("linking", additionalLinkingContext);
		getCache().execWithoutCacheClear(this, new IUnitOfWork.Void<GamlResource>() {

			@Override
			public void process(final GamlResource state) throws Exception {
				state.load(is, null);
				resolveAll(GamlResource.this);
			}
		});
		r.getOrCreate(this).set("linking", null);

	}

	@Override
	public OnChangeEvictingCache getCache() { return (OnChangeEvictingCache) super.getCache(); }

	@Override
	protected void doLinking() {
		// If the imports are not correctly updated, we cannot proceed
		final EObject faulty = updateImports(this);
		if (faulty != null) {
			final EAttribute attribute = getContents().get(0) instanceof Model ? GamlPackage.Literals.IMPORT__IMPORT_URI
					: GamlPackage.Literals.HEADLESS_EXPERIMENT__IMPORT_URI;
			getErrors().add(new EObjectDiagnosticImpl(ERROR, IMPORT_ERROR, "Impossible to locate import", faulty,
					attribute, -1, null));
			return;
		}
		super.doLinking();
	}

	/**
	 * Checks for errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasErrors() {
		return !getErrors().isEmpty() || getParseResult().hasSyntaxErrors();
	}

	/*
	 * Javadoc copied from interface.
	 */
	@Override
	public void setURI(final URI uri) {
		super.setURI(properlyEncodedURI(uri));
	}

	@Override
	public void clearCache() {
		super.clearCache();
	}

}
