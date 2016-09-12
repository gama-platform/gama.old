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

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ValidationContext;

@Singleton
public class ErrorToDiagnoticTranslator {

	@Inject
	public ErrorToDiagnoticTranslator() {
	}

	public Diagnostic translate(final ValidationContext errors, final GamlResource r, final CheckMode mode) {
		final BasicDiagnostic chain = new BasicDiagnostic();
		for (final GamlCompilationError e : errors) {
			final Diagnostic d = translate(e, r, mode);
			if (d != null)
				chain.add(d);
		}
		return chain;
	}

	public Diagnostic translate(final GamlCompilationError e, final GamlResource r, final CheckMode mode) {
		final URI errorURI = e.getURI();
		if (!GamlResourceIndexer.equals(errorURI, r.getURI())) {
			final String uri = URI.decode(errorURI.toFileString());
			final String s = URI.decode(errorURI.lastSegment());
			final Model m = (Model) r.getContents().get(0);
			final EObject eObject = findImportWith(m, s);
			final EAttribute feature = eObject instanceof Model ? GamlPackage.Literals.GAML_DEFINITION__NAME
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

	private Diagnostic createDiagnostic(final CheckMode mode, final int diagnosticSeverity, final String message,
			final EObject object, final EStructuralFeature feature, final int index, final String code,
			final String... issueData) {
		final Diagnostic result = new FeatureBasedDiagnostic(diagnosticSeverity, message, object, feature, index,
				getType(mode), code, issueData);
		return result;
	}

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
		} else
			return CheckType.FAST;
	}

	protected int toDiagnosticSeverity(final GamlCompilationError e) {
		int diagnosticSeverity = -1;
		if (e.isError())
			diagnosticSeverity = Diagnostic.ERROR;
		else if (e.isWarning())
			diagnosticSeverity = Diagnostic.WARNING;
		else if (e.isInfo())
			diagnosticSeverity = Diagnostic.INFO;

		return diagnosticSeverity;
	}

	private EObject findImportWith(final Model m, final String s) {
		for (final Import i : m.getImports()) {
			if (i.getImportURI().endsWith(s))
				return i;
		}
		return m;
	}

}
