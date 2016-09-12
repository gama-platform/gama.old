package msi.gama.lang.gaml.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IDiagnosticConverter;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;

import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourcesHelper;

public class GamlResourceValidator implements IResourceValidator, IAcceptor<Issue> {

	private List<Issue> result;
	@Inject IModelIndexer indexer;
	@Inject IDiagnosticConverter converter;
	@Inject ErrorToDiagnoticTranslator errorTranslator;

	@Override
	public List<Issue> validate(final Resource resource, final CheckMode mode, final CancelIndicator indicator) {
		if (result != null)
			result.clear();

		// We only validate GAML resources
		if (!(resource instanceof GamlResource))
			return Collections.EMPTY_LIST;

		// We wait for the indexer to have finished
		// indexer.waitToBeReady();

		// We resolve the cross references
		EcoreUtil2.resolveLazyCrossReferences(resource, indicator);

		// And collect the syntax / linking issues
		for (int i = 0; i < resource.getErrors().size(); i++) {
			converter.convertResourceDiagnostic(resource.getErrors().get(i), Severity.ERROR, this);
		}

		// We then ask the resource to validate itself
		final GamlResource r = (GamlResource) resource;

		r.validate();

		// And collect the semantic errors from its error collector
		for (final Diagnostic d : errorTranslator.translate(r.getErrorCollector(), r, mode).getChildren()) {
			converter.convertValidatorDiagnostic(d, this);
		}

		GamlResourcesHelper.discardValidationContext(r);
		return result == null ? Collections.EMPTY_LIST : result;
	}

	@Override
	public void accept(final Issue issue) {
		if (issue != null) {
			if (result == null)
				result = new ArrayList();
			result.add(issue);
		}

	}

}
