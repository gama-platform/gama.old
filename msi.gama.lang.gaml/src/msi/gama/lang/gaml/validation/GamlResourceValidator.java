/*********************************************************************************************
 *
 * 'GamlResourceValidator.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceServices;

public class GamlResourceValidator implements IResourceValidator {
	public final static boolean DEBUG = false;

	public static void DEBUG(final String s) {
		if (DEBUG) {
			System.out.println(s);
		}
	}

	@Inject IDiagnosticConverter converter;
	private static ErrorToDiagnoticTranslator errorTranslator = new ErrorToDiagnoticTranslator();

	class LazyAcceptor implements IAcceptor<Issue> {
		List<Issue> result;

		@Override
		public void accept(final Issue t) {
			if (t == null) { return; }
			if (result == null) {
				result = new ArrayList<>();
			}
			result.add(t);
		}
	}

	@Override
	public List<Issue> validate(final Resource resource, final CheckMode mode, final CancelIndicator indicator) {
		DEBUG("GamlResourceValidato begginning validation job of " + resource.getURI().lastSegment());

		final LazyAcceptor acceptor = new LazyAcceptor();
		// We resolve the cross references
		EcoreUtil2.resolveLazyCrossReferences(resource, indicator);
		DEBUG("Cross references resolved for " + resource.getURI().lastSegment());
		// And collect the syntax / linking issues
		for (int i = 0; i < resource.getErrors().size(); i++) {
			converter.convertResourceDiagnostic(resource.getErrors().get(i), Severity.ERROR, acceptor);
		}

		// We then ask the resource to validate itself
		final GamlResource r = (GamlResource) resource;
		r.validate();
		DEBUG("Resource has been validated: " + resource.getURI().lastSegment());
		// And collect the semantic errors from its error collector
		for (final Diagnostic d : errorTranslator.translate(r.getValidationContext(), r, mode).getChildren()) {
			converter.convertValidatorDiagnostic(d, acceptor);
		}
		GamlResourceServices.discardValidationContext(r);
		DEBUG("Validation context has been discarded: " + resource.getURI().lastSegment());
		return acceptor.result == null ? Collections.EMPTY_LIST : acceptor.result;
	}

}
