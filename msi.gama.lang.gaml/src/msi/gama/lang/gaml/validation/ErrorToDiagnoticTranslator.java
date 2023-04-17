/*******************************************************************************************************
 *
 * ErrorToDiagnoticTranslator.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.validation;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.FeatureBasedDiagnostic;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.lang.gaml.gaml.ExperimentFileStructure;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ValidationContext;

/**
 * The Class ErrorToDiagnoticTranslator.
 */
@Singleton
public class ErrorToDiagnoticTranslator {

	/**
	 * Instantiates a new error to diagnotic translator.
	 */
	@Inject
	public ErrorToDiagnoticTranslator() {}

	/**
	 * Translate.
	 *
	 * @param errors the errors
	 * @param r the r
	 * @param mode the mode
	 * @return the diagnostic
	 */
	public Diagnostic translate(final ValidationContext errors, final GamlResource r, final CheckMode mode) {
		final BasicDiagnostic chain = new BasicDiagnostic();
		for (final GamlCompilationError e : errors) {
			final Diagnostic d = translate(e, r, mode);
			if (d != null) {
				chain.add(d);
			}
		}
		return chain;
	}

	/**
	 * Translate.
	 *
	 * @param e the e
	 * @param r the r
	 * @param mode the mode
	 * @return the diagnostic
	 */
	public Diagnostic translate(final GamlCompilationError e, final GamlResource r, final CheckMode mode) {
		final URI errorURI = e.getURI();
		if (!GamlResourceServices.equals(errorURI, r.getURI())) {
			//			final String uri = URI.decode(errorURI.toFileString());
			final String s = URI.decode(errorURI.lastSegment());
			final EObject m = r.getContents().get(0);
			final EObject eObject = findImportWith(m, s);
			final EAttribute feature =
					eObject instanceof Model ? GamlPackage.Literals.GAML_DEFINITION__NAME
							: eObject instanceof HeadlessExperiment
							? GamlPackage.Literals.HEADLESS_EXPERIMENT__IMPORT_URI
									: GamlPackage.Literals.IMPORT__IMPORT_URI;
			return createDiagnostic(CheckMode.NORMAL_ONLY, Diagnostic.ERROR, e.toString() + " (in " + s + ")", eObject,
					feature, ValidationMessageAcceptor.INSIGNIFICANT_INDEX, e.getCode(), e.getData());
		}
		EStructuralFeature feature = null;
		final EObject object = e.getStatement();
		if (object instanceof Statement) {
			final StatementImpl s = (StatementImpl) object;
			if (s.eIsSet(GamlPackage.Literals.STATEMENT__KEY)) {
				feature = GamlPackage.Literals.STATEMENT__KEY;
			} else if (s.eIsSet(GamlPackage.Literals.SDEFINITION__TKEY)) {
				feature = GamlPackage.Literals.SDEFINITION__TKEY;
			}
		} else if (object instanceof Model) {
			feature = GamlPackage.Literals.GAML_DEFINITION__NAME;
		}
		if (!Arrays.contains(e.getData(), null)) {
			final int index = ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
			return createDiagnostic(mode, toDiagnosticSeverity(e), e.toString(), object, feature, index, e.getCode(),
					e.getData());
		}
		return null;
	}

	/**
	 * Creates the diagnostic.
	 *
	 * @param mode the mode
	 * @param diagnosticSeverity the diagnostic severity
	 * @param message the message
	 * @param object the object
	 * @param feature the feature
	 * @param index the index
	 * @param code the code
	 * @param issueData the issue data
	 * @return the diagnostic
	 */
	private Diagnostic createDiagnostic(final CheckMode mode, final int diagnosticSeverity, final String message,
			final EObject object, final EStructuralFeature feature, final int index, final String code,
			final String... issueData) {
		final Diagnostic result = new FeatureBasedDiagnostic(diagnosticSeverity, message, object, feature, index,
				getType(mode), code, issueData);
		return result;
	}

	/**
	 * Gets the type.
	 *
	 * @param mode the mode
	 * @return the type
	 */
	private CheckType getType(final CheckMode mode) {
		if (mode == CheckMode.FAST_ONLY) {
			return CheckType.FAST;
		} else if (mode == CheckMode.EXPENSIVE_ONLY) {
			return CheckType.EXPENSIVE;
		} else if (mode == CheckMode.ALL) {
			return CheckType.FAST;
		} else if (mode == CheckMode.NORMAL_AND_FAST) {
			return CheckType.FAST;
		} else if (mode == CheckMode.NORMAL_ONLY) {
			return CheckType.NORMAL;
		} else {
			return CheckType.FAST;
		}
	}

	/**
	 * To diagnostic severity.
	 *
	 * @param e the e
	 * @return the int
	 */
	protected int toDiagnosticSeverity(final GamlCompilationError e) {
		int diagnosticSeverity = -1;
		if (e.isError()) {
			diagnosticSeverity = Diagnostic.ERROR;
		} else if (e.isWarning()) {
			diagnosticSeverity = Diagnostic.WARNING;
		} else if (e.isInfo()) {
			diagnosticSeverity = Diagnostic.INFO;
		}

		return diagnosticSeverity;
	}

	/**
	 * Find import with.
	 *
	 * @param m the m
	 * @param s the s
	 * @return the e object
	 */
	private EObject findImportWith(final EObject m, final String s) {
		if (m instanceof Model) {
			for (final Import i : ((Model) m).getImports()) {
				if (i.getImportURI().endsWith(s)) {
					return i;
				}
			}
		} else if (m instanceof ExperimentFileStructure) {
			return ((ExperimentFileStructure) m).getExp();
		}
		return m;
	}

}
