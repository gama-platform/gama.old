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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.inject.Inject;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	@Inject
	IModelIndexer indexer;

	@Check()
	public void validate(final Model model) {
		final GamlResource newResource = (GamlResource) model.eResource();
		if (newResource == null)
			return;
		if (newResource.isValidating()) {
			return;
		}

		final ErrorCollector errors = validate(newResource);
		if (indexer != null) {
			indexer.updateImports(newResource);
		}
		if (!errors.hasInternalSyntaxErrors()) {
			for (final GamlCompilationError error : errors) {
				manageCompilationIssue(error, errors);
			}
		}
	}

	public ErrorCollector validate(final Resource resource) {
		final GamlResource r = (GamlResource) resource;
		r.validate(resource.getResourceSet());
		return r.getErrorCollector();
	}

	private boolean sameResource(final EObject object) {
		if (object == null || object.eResource() == null) {
			return false;
		}
		final EObject current = this.getCurrentObject();
		if (current == null || current.eResource() == null) {
			return false;
		}
		return object.eResource().getURI().equals(current.eResource().getURI());
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
			final URI u = object.eResource().getURI();
			final String uri = URI.decode(u.toFileString());
			final String s = URI.decode(u.lastSegment());
			final Model m = (Model) getCurrentObject();
			final EObject eObject = findImportWith(m, s);
			final EAttribute feature = eObject instanceof Model ? GamlPackage.Literals.GAML_DEFINITION__NAME
					: GamlPackage.Literals.IMPORT__IMPORT_URI;
			acceptError(e.toString() + "(in " + s + ")", eObject, feature,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX, e.getCode(), e.getData());
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

	private EObject findImportWith(final Model m, final String s) {
		for (final Import i : m.getImports()) {
			if (i.getImportURI().endsWith(s))
				return i;
		}
		return m;
	}

}
