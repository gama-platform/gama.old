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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import com.google.inject.Inject;

import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.parsing.GamlCompatibilityConverter;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser.GamlParseResult;
import msi.gama.util.GAML;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.SyntacticModelElement;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

/*
 *
 * The class GamlResource.
 *
 * @author drogoul
 *
 * @since 24 avr. 2012
 */
public class GamlResource extends LazyLinkingResource {

	// private volatile boolean isValidating;

	@Inject IModelIndexer indexer;

	@Inject IDocManager documenter;

	@Inject GamlCompatibilityConverter converter;

	public GamlResource() {
	}

	public ValidationContext getErrorCollector() {
		return indexer.getValidationContext(this);
	}

	public boolean hasSemanticErrors() {
		return getErrorCollector().hasErrors();
	}

	@Override
	public String getEncoding() {
		return "UTF-8";
	}

	public void resetErrorCollector() {
		indexer.discardValidationContext(this);
	}

	@Override
	public String toString() {
		return "GAML resource" + "[" + getURI() + "]";
	}

	public void updateWith(final ModelDescription model, final boolean newState) {
		indexer.updateState(getURI(), model, newState, getErrorCollector());
	}

	public ISyntacticElement getSyntacticContents() {
		final GamlParseResult parseResult = (GamlParseResult) getParseResult();
		return parseResult.getSyntacticContents(converter);
	}

	private class ModelsGatherer implements TObjectObjectProcedure<GamlResource, String> {

		Map<ISyntacticElement, String> microModels = null;
		final List<ISyntacticElement> models = new ArrayList();

		ModelsGatherer() {
			models.add(getSyntacticContents());
		}

		@Override
		public boolean execute(final GamlResource resource, final String alias) {

			final SyntacticModelElement m = (SyntacticModelElement) resource.getSyntacticContents();
			if (m != null) {
				if (alias == null) {
					models.add(m);
				} else {
					if (microModels == null)
						microModels = new TOrderedHashMap();
					microModels.put(m, alias);
				}
			}
			return true;
		}
	}

	private ModelDescription buildModelDescription(final TOrderedHashMap<GamlResource, String> resources) {
		final ModelsGatherer gatherModels = new ModelsGatherer();
		resources.forEachEntry(gatherModels);
		GAML.getExpressionFactory().resetParser();
		final String modelPath = GamlResourceFileHelper.getModelPathOf(this);
		final String projectPath = GamlResourceFileHelper.getProjectPathOf(this);
		Map<String, ModelDescription> compiledMicroModels = null;
		String aliasName = null;
		while (gatherModels.microModels != null && !gatherModels.microModels.isEmpty()) {
			aliasName = (String) gatherModels.microModels.values().toArray()[0];
			final List<ISyntacticElement> res = getListMicroSyntacticElement(gatherModels.microModels, aliasName);
			gatherModels.microModels.keySet().removeAll(res);
			final ModelDescription mic = GAML.getModelFactory().createModelDescription(projectPath, modelPath, res,
					getErrorCollector(), isEdited(), Collections.<String, ModelDescription> emptyMap());
			mic.setAlias(aliasName);
			if (compiledMicroModels == null)
				compiledMicroModels = new TOrderedHashMap<String, ModelDescription>();
			compiledMicroModels.put(aliasName, mic);
		}
		return GAML.getModelFactory().createModelDescription(projectPath, modelPath, gatherModels.models,
				getErrorCollector(), isEdited(), compiledMicroModels);
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

	public void invalidate(final GamlResource r, final String s) {
		GamlCompilationError error = null;
		if (indexer.equals(r.getURI(), getURI())) {
			error = new GamlCompilationError(s, IGamlIssue.GENERAL, r.getContents().get(0), false, false);
		} else {
			error = new GamlCompilationError(s, IGamlIssue.GENERAL, r.getURI(), false, false);
		}
		getErrorCollector().add(error);
		updateWith(null, true);
	}

	ModelDescription buildCompleteDescription() {
		// try {
		// resetErrorCollector();
		final TOrderedHashMap<GamlResource, String> imports = indexer.validateImportsOf(this);

		if (hasErrors())
			return null;

		if (!shouldValidate())
			return null;

		if (imports == null || hasSemanticErrors())
			return null;

		final ModelDescription model = buildModelDescription(imports);

		// If, for whatever reason, the description is null, we stop the
		// semantic validation
		if (model == null) {
			invalidate(this, "Impossible to validate " + URI.decode(getURI().lastSegment()) + " (check the logs)");
		}

		return model;
		// } finally {
		// indexer.removeResourcesToBuild(getURI());
		// }
	}

	/**
	 * Validates the resource by compiling its contents into a ModelDescription
	 * and discarding this ModelDescription afterwards
	 * 
	 * @note The errors will be available as part of the ErrorCollector, which
	 *       can later be retrieved from the resource, and which contains
	 *       semantic errors (as opposed to the ones obtained via
	 *       resource.getErrors(), which are syntactic errors), This collector
	 *       can be probed for compilation errors via its hasErrors(),
	 *       hasInternalErrors(), hasImportedErrors() methods
	 *
	 */
	public void validate() {
		final ModelDescription model = buildCompleteDescription();
		if (model == null) {
			updateWith(null, true);
			return;
		}

		// We then validate it and get rid of the description. The
		// documentation is produced only if the resource is
		// marked as 'edited'
		final boolean edited = isEdited();
		try {
			model.validate(edited);
			updateWith(model, true);
		} finally {
			if (edited) {
				documenter.addCleanupTask(model);
			} else {
				model.dispose();
			}
		}

	}

	public boolean isEdited() {
		return indexer.isEdited(getURI());
	}

	// public GamlProperties getRequires() {
	// requires.remove(null);
	// return requires;
	// }

	@Override
	public void resolveLazyCrossReferences(final CancelIndicator mon) {
		final TreeIterator<Object> iterator = EcoreUtil.getAllContents(this, true);
		while (iterator.hasNext()) {
			final InternalEObject source = (InternalEObject) iterator.next();
			final EStructuralFeature[] eStructuralFeatures = ((EClassImpl.FeatureSubsetSupplier) source.eClass()
					.getEAllStructuralFeatures()).crossReferences();
			if (eStructuralFeatures != null) {
				for (final EStructuralFeature crossRef : eStructuralFeatures) {
					resolveLazyCrossReference(source, crossRef);
				}
			}
		}
	}

	@Override
	protected void doLoad(final InputStream inputStream, final Map<?, ?> options) throws IOException {
		setEncodingFromOptions(options);
		final IParseResult result = getParser().parse(createReader(inputStream));
		updateInternalState(getParseResult(), result);

		if (options != null && Boolean.TRUE.equals(options.get(OPTION_RESOLVE_ALL)))
			EcoreUtil.resolveAll(this);
	}

	@Override
	protected void doLinking() {

		if (getParseResult() == null || getParseResult().getRootASTElement() == null)
			return;
		// If the imports are not correctly updated, we cannot proceed
		final EObject faulty = indexer.updateImports(this);
		if (faulty != null) {
			getErrors().add(new EObjectDiagnosticImpl(Severity.ERROR, IGamlIssue.IMPORT_ERROR,
					"Impossible to locate import", faulty, GamlPackage.Literals.IMPORT__IMPORT_URI, -1, null));
			return;
		}
		final ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
		getLinker().linkModel(getParseResult().getRootASTElement(), consumer);
		if (!isValidationDisabled()) {
			getErrors().addAll(consumer.getResult(Severity.ERROR));
			getWarnings().addAll(consumer.getResult(Severity.WARNING));
		}
	}

	public boolean hasErrors() {
		return !getErrors().isEmpty() || getParseResult().hasSyntaxErrors();
	}

	public boolean shouldValidate() {
		return indexer.needsToBuild(getURI());
	}

	public IDocManager getDocumentationManager() {
		return documenter;
	}

}
