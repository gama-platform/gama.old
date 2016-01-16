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

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.resource.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	// private GamlResource currentResource;
	//
	// public void setCurrentResource(final GamlResource resource) {
	// currentResource = resource;
	// }
	//
	@Check()
	public void validate(final Model model) {
		GamlResource newResource = (GamlResource) model.eResource();
		if ( newResource.isValidating() ) { return; }
		ErrorCollector errors = /* GamlModelBuilder.getInstance() */new GamlModelBuilder().validate(newResource);
		if ( !errors.hasInternalSyntaxErrors() ) {
			if ( errors.hasInternalErrors() ) {
				GAMA.getGui().debug("GamlJavaValidator.validate");
			}
			for ( GamlCompilationError error : errors ) {
				manageCompilationIssue(error);
			}
		}
	}

	private GamlResource getCurrentResource() {
		EObject object = this.getCurrentObject();
		if ( object == null ) { return null; }
		return (GamlResource) object.eResource();

	}

	private boolean sameResource(final EObject object) {
		if ( object == null ) { return false; }
		return object.eResource() == getCurrentResource();
	}

	private void manageCompilationIssue(final GamlCompilationError e) {
		final EObject object = e.getStatement();
		if ( object == null ) {
			System.err.println("*** Internal compilation problem : " + e.toString());
			return;
		} else if ( object.eResource() == null ) { throw new RuntimeException(
			"Error detected in a syntethic object. Please debug to understand the cause"); }

		if ( !sameResource(object) ) {
			// if ( e.isError() ) {
			// URI uri = object.eResource().getURI();
			// final EObject imp = getCurrentResource().findImport(uri);
			// EObject warningContainer;
			// String msg;
			// EAttribute feature;
			// if ( imp != null ) {
			// warningContainer = imp;
			// msg = "Error detected in imported file " + ": " + e.toString();
			// feature = GamlPackage.Literals.IMPORT__IMPORT_URI;
			//
			// } else {
			// warningContainer = getCurrentObject();
			// msg = "Errors detected in indirectly imported file " + uri + ": " + e.toString();
			// feature = GamlPackage.Literals.GAML_DEFINITION__NAME;
			// }
			// acceptWarning(msg, warningContainer, feature, ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
			// IGamlIssue.IMPORT_ERROR, uri.toString());
			// }
			return;
		}
		EStructuralFeature feature = null;
		if ( object instanceof Statement ) {
			StatementImpl s = (StatementImpl) object;
			if ( s.eIsSet(GamlPackage.Literals.STATEMENT__KEY) ) {
				feature = GamlPackage.Literals.STATEMENT__KEY;
			} else if ( s.eIsSet(GamlPackage.Literals.SDEFINITION__TKEY) ) {
				feature = GamlPackage.Literals.SDEFINITION__TKEY;
			}
		} else if ( object instanceof Model ) {
			feature = GamlPackage.Literals.GAML_DEFINITION__NAME;
		}
		if ( !Arrays.contains(e.getData(), null) ) {
			int index = ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
			if ( e.isInfo() ) {
				acceptInfo(e.toString(), object, feature, index, e.getCode(), e.getData());
			} else if ( e.isWarning() ) {
				acceptWarning(e.toString(), object, feature, index, e.getCode(), e.getData());
			} else {
				System.out.println(
					"One compilation error accepted: " + e.toString() + " thread: " + Thread.currentThread().getName());
				acceptError(e.toString(), object, feature, index, e.getCode(), e.getData());
			}
		}
	}

}
