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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import com.google.inject.Inject;

import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.parsing.GamlSyntacticConverter;
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

	@Inject IDocManager documenter;
	@Inject GamlSyntacticConverter converter;
	SyntacticModelElement element;

	public ValidationContext getValidationContext() {
		return GamlResourceServices.getValidationContext(this);
	}

	public boolean hasSemanticErrors() {
		return getValidationContext().hasErrors();
	}

	@Override
	public String getEncoding() {
		return "UTF-8";
	}

	@Override
	public String toString() {
		return "GamlResource[" + getURI().lastSegment() + "]";
	}

	public void updateWith(final ModelDescription model, final boolean newState) {
		GamlResourceServices.updateState(getURI(), model, newState, GamlResourceServices.getValidationContext(this));
	}

	public ISyntacticElement getSyntacticContents() {
		if (element == null)
			element = converter.buildSyntacticContents(getParseResult().getRootASTElement(), null);
		return element;
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
		final String modelPath = GamlResourceServices.getModelPathOf(this);
		final String projectPath = GamlResourceServices.getProjectPathOf(this);
		final boolean isEdited = GamlResourceServices.isEdited(this);
		final ValidationContext context = GamlResourceServices.getValidationContext(this);
		Map<String, ModelDescription> compiledMicroModels = null;
		String aliasName = null;

		while (gatherModels.microModels != null && !gatherModels.microModels.isEmpty()) {
			aliasName = (String) gatherModels.microModels.values().toArray()[0];
			final List<ISyntacticElement> res = getListMicroSyntacticElement(gatherModels.microModels, aliasName);
			gatherModels.microModels.keySet().removeAll(res);
			final ModelDescription mic = GAML.getModelFactory().createModelDescription(projectPath, modelPath, res,
					context, isEdited, Collections.<String, ModelDescription> emptyMap());
			mic.setAlias(aliasName);
			if (compiledMicroModels == null)
				compiledMicroModels = new TOrderedHashMap<String, ModelDescription>();
			compiledMicroModels.put(aliasName, mic);
		}
		return GAML.getModelFactory().createModelDescription(projectPath, modelPath, gatherModels.models, context,
				isEdited, compiledMicroModels);
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
		if (GamlResourceIndexer.equals(r.getURI(), getURI())) {
			error = new GamlCompilationError(s, IGamlIssue.GENERAL, r.getContents().get(0), false, false);
		} else {
			error = new GamlCompilationError(s, IGamlIssue.GENERAL, r.getURI(), false, false);
		}
		getValidationContext().add(error);
		updateWith(null, true);
	}

	public ModelDescription buildCompleteDescription() {
		final TOrderedHashMap<GamlResource, String> imports = GamlResourceIndexer.validateImportsOf(this);
		if (hasErrors() || imports == null || hasSemanticErrors())
			return null;
		final ModelDescription model = buildModelDescription(imports);
		// If, for whatever reason, the description is null, we stop the
		// semantic validation
		if (model == null) {
			invalidate(this, "Impossible to validate " + URI.decode(getURI().lastSegment()) + " (check the logs)");
		}
		return model;
	}

	/**
	 * Validates the resource by compiling its contents into a ModelDescription
	 * and discarding this ModelDescription afterwards
	 * 
	 * @note The errors will be available as part of the ValidationContext,
	 *       which can later be retrieved from the resource, and which contains
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
		final boolean edited = GamlResourceServices.isEdited(this.getURI());
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

	@Override
	protected void updateInternalState(final IParseResult oldParseResult, final IParseResult newParseResult) {
		super.updateInternalState(oldParseResult, newParseResult);
		element = null;
	}

	@Override
	protected void clearInternalState() {
		super.clearInternalState();
		element = null;
	}

	@Override
	protected void doUnload() {
		super.doUnload();
		element = null;
	}

	@Override
	protected void doLinking() {
		// If the imports are not correctly updated, we cannot proceed
		final EObject faulty = GamlResourceIndexer.updateImports(this);
		if (faulty != null) {
			getErrors().add(new EObjectDiagnosticImpl(Severity.ERROR, IGamlIssue.IMPORT_ERROR,
					"Impossible to locate import", faulty, GamlPackage.Literals.IMPORT__IMPORT_URI, -1, null));
			return;
		}
		super.doLinking();
	}

	public boolean hasErrors() {
		return !getErrors().isEmpty() || getParseResult().hasSyntaxErrors();
	}

	public IDocManager getDocumentationManager() {
		return documenter;
	}

}
