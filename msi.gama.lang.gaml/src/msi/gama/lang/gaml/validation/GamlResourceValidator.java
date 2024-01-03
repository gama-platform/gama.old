/*******************************************************************************************************
 *
 * GamlResourceValidator.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.validation;

import java.util.ArrayList;
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

import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamlResourceValidator.
 */
public class GamlResourceValidator implements IResourceValidator {

	static {
		DEBUG.ON();
	}

	/** The converter. */
	@Inject IDiagnosticConverter converter;

	/** The error translator. */
	private static ErrorToDiagnoticTranslator errorTranslator = new ErrorToDiagnoticTranslator();

	@Override
	public List<Issue> validate(final Resource resource, final CheckMode mode, final CancelIndicator indicator) {
		// DEBUG.OUT("GamlResourceValidator beginning validation job of " + resource.getURI().lastSegment());
		String name = org.eclipse.emf.common.util.URI.decode(resource.getURI().lastSegment());
		ArrayList<Issue> result = new ArrayList<>();
		DEBUG.TIMER("COMPIL", name, "in", () -> {
			final IAcceptor<Issue> acceptor =
					t -> { if (t.getMessage() != null && !t.getMessage().isEmpty()) { result.add(t); } };
			// We resolve the cross references
			EcoreUtil2.resolveLazyCrossReferences(resource, indicator);
			// DEBUG.OUT("Cross references resolved for " + resource.getURI().lastSegment());
			// And collect the syntax / linking issues
			for (org.eclipse.emf.ecore.resource.Resource.Diagnostic element : resource.getErrors()) {
				converter.convertResourceDiagnostic(element, Severity.ERROR, acceptor);
			}

			// We then ask the resource to validate itself
			final GamlResource r = (GamlResource) resource;
			r.validate();
			// DEBUG.OUT("Resource has been validated: " + resource.getURI().lastSegment());
			// And collect the semantic errors from its error collector
			for (final Diagnostic d : errorTranslator.translate(r.getValidationContext(), r, mode).getChildren()) {
				converter.convertValidatorDiagnostic(d, acceptor);
			}
			GamlResourceServices.discardValidationContext(r);
			// DEBUG.OUT("Validation context has been discarded: " + resource.getURI().lastSegment());
		});
		return result;
	}

}
