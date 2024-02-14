/*******************************************************************************************************
 *
 * GamlResource.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import static java.util.Collections.singleton;
import static msi.gama.lang.gaml.indexer.GamlResourceIndexer.updateImports;
import static msi.gama.lang.gaml.resource.GamlResourceServices.properlyEncodedURI;
import static msi.gama.lang.gaml.resource.GamlResourceServices.updateState;
import static msi.gaml.compilation.GAML.getModelFactory;
import static msi.gaml.interfaces.IGamlIssue.GENERAL;
import static msi.gaml.interfaces.IGamlIssue.IMPORT_ERROR;
import static org.eclipse.emf.ecore.util.EcoreUtil.resolveAll;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.impl.XtextLinkingDiagnostic;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.util.OnChangeEvictingCache;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.common.base.Function;
import com.google.common.io.CharStreams;

import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.parsing.GamlParser;
import msi.gama.runtime.IExecutionContext;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;
import ummisco.gama.dev.utils.DEBUG;

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
public class GamlResource extends LazyLinkingResource implements IDiagnosticConsumer {
	/* To allow resources to strore bin files : extends StorageAwareResource */

	static {
		DEBUG.OFF();
	}

	/** The element. */
	ISyntacticElement element;

	/** The string contents. */
	String stringContents;

	/**
	 * Gets the validation context.
	 *
	 * @return the validation context
	 */
	public ValidationContext getValidationContext() {
		return GamlResourceServices.getOrCreateValidationContext(this);
	}

	@Override
	protected void setInjectedParser(final IParser parser) {
		super.setInjectedParser(parser);
		if (parser instanceof GamlParser gp) { gp.setResource(this); }
	}

	/**
	 * Checks for semantic errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasSemanticErrors() {
		return getValidationContext().hasErrors();
	}

	/**
	 * Gets the encoding.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the encoding
	 * @date 13 janv. 2024
	 */
	@Override
	public String getEncoding() { return "UTF-8"; }

	/**
	 * To string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @date 13 janv. 2024
	 */
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
		if (element != null) // DEBUG.OUT("Reusing existing contents for " + uri.lastSegment());
			return element;
		setElement(GamlResourceServices.buildSyntacticContents(this));
		return element;
	}

	/** The Constant TO_SYNTACTIC_CONTENTS. */
	final static Function<GamlResource, ISyntacticElement> TO_SYNTACTIC_CONTENTS = input -> {
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
				singleton(getSyntacticContents()), context, null);
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
		// Map<URI, URI> doubleImports = collectMultipleImportsOf(this);
		// doubleImports.forEach((imported, importer) -> {
		// String s = imported.lastSegment() + " is already imported by " + importer.lastSegment();
		// EObject o = getImportObject(getContents().get(0), getURI(), imported);
		// GamlCompilationError error = new GamlCompilationError(s, GENERAL, o, false, true);
		// getValidationContext().add(error);
		// });
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

	/**
	 * Update internal state.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param oldParseResult
	 *            the old parse result
	 * @param newParseResult
	 *            the new parse result
	 * @date 13 janv. 2024
	 */
	@Override
	protected void updateInternalState(final IParseResult oldParseResult, final IParseResult newParseResult) {
		if (oldParseResult != newParseResult) {
			// if (oldParseResult != newParseResult) { DEBUG.OUT("===> Creating a new contents for " +
			// uri.lastSegment()); }
			super.updateInternalState(oldParseResult, newParseResult);
			setElement(null);
		}
	}

	/**
	 * Clear internal state.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 13 janv. 2024
	 */
	@Override
	protected void clearInternalState() {
		super.clearInternalState();
		setElement(null);
	}

	/**
	 * Do unload.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 13 janv. 2024
	 */
	@Override
	protected void doUnload() {
		super.doUnload();
		setElement(null);
		stringContents = null;
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
	protected void doLoad(final InputStream inputStream, final Map<?, ?> options) throws IOException {
		stringContents = CharStreams.toString(new InputStreamReader(inputStream));
		super.doLoad(inputStream, options);
	}

	/**
	 * Gets the cache.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the cache
	 * @date 13 janv. 2024
	 */
	@Override
	public OnChangeEvictingCache getCache() { return (OnChangeEvictingCache) super.getCache(); }

	/**
	 * Do linking.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 13 janv. 2024
	 */
	@Override
	protected void doLinking() {
		// If the imports are not correctly updated, we cannot proceed
		final EObject faulty = updateImports(this);
		if (faulty != null) {
			getErrors()
					.add(new XtextLinkingDiagnostic(getNode(faulty), "Impossible to locate import", IMPORT_ERROR, ""));
			return;
		}
		getLinker().linkModel(getParseResult().getRootASTElement(), this);
	}

	/**
	 * Checks for errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasErrors() {
		return !getErrors().isEmpty() || getParseResult().hasSyntaxErrors();
	}

	/**
	 * Sets the uri.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param uri
	 *            the new uri
	 * @date 13 janv. 2024
	 */
	/*
	 * Javadoc copied from interface.
	 */
	@Override
	public void setURI(final URI uri) {
		super.setURI(properlyEncodedURI(uri));
	}

	/**
	 * Clear cache.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 13 janv. 2024
	 */
	@Override
	public void clearCache() {
		// DEBUG.LINE();
		// DEBUG.TITLE("CLEARING CACHE OF " + uri.lastSegment());
		GamlResourceServices.getResourceDocumenter().invalidate(getURI());
		super.clearCache();
	}

	@Override
	public void consume(final org.eclipse.xtext.diagnostics.Diagnostic diagnostic, final Severity severity) {
		if (isValidationDisabled()) return;
		switch (severity) {
			case ERROR:
				getErrors().add(diagnostic);
				break;
			case WARNING:
				getWarnings().add(diagnostic);
				break;
			default:
				;
		}

	}

	@Override
	public boolean hasConsumedDiagnostics(final Severity severity) {
		return !getErrors().isEmpty() && !getWarnings().isEmpty();
	}

	/**
	 * Gets the string contents.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string contents
	 * @date 14 févr. 2024
	 */
	public String getStringContents() { return stringContents; }

}
