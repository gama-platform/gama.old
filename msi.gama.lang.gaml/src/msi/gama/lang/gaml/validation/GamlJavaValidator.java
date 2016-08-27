/*********************************************************************************************
 *
 *
 * 'GamlJavaValidator.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.resource.GamlModelBuilder;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	// @Inject
	// private ImportUriResolver resolver;

	@Check()
	public void validate(final Model model) {
		final GamlResource newResource = (GamlResource) model.eResource();
		if (newResource.isValidating()) {
			return;
		}
		final ErrorCollector errors = new GamlModelBuilder().validate(newResource);
		if (!errors.hasInternalSyntaxErrors()) {
			for (final GamlCompilationError error : errors) {
				manageCompilationIssue(error, errors);
			}
		}
	}

	private boolean sameResource(final EObject object) {
		if (object == null) {
			return false;
		}
		final EObject current = this.getCurrentObject();
		if (current == null) {
			return false;
		}
		return object.eResource() == current.eResource();
	}

	private void manageCompilationIssue(final GamlCompilationError e, final ErrorCollector collector) {
		final EObject object = e.getStatement();
		if (object == null) {
			System.err.println("*** Internal compilation problem : " + e.toString());
			return;
		} else if (object.eResource() == null) {
			throw new RuntimeException(
					"Error detected in a synthetic object. Please post an issue at: https://github.com/gama-platform/gama/issues");
		}

		if (!sameResource(object)) {
			return;
		}
		EStructuralFeature feature = null;
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
			if (e.isInfo()) {
				acceptInfo(e.toString(), object, feature, index, e.getCode(), e.getData());
			} else if (e.isWarning()) {
				acceptWarning(e.toString(), object, feature, index, e.getCode(), e.getData());
			} else {
				acceptError(e.toString(), object, feature, index, e.getCode(), e.getData());
			}
		}
	}

}
