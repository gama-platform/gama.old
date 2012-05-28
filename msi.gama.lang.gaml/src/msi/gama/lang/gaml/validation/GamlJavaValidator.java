/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.validation;

import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.GamlResource;
import msi.gama.lang.gaml.gaml.*;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ModelDescription;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.*;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	static boolean FORCE_VALIDATION = true;

	@Check(CheckType.FAST)
	public void validate(final Model model) {
		try {
			GuiUtils.debug("Validating " + model.eResource().getURI().lastSegment() + "...");
			GamlResource r = (GamlResource) model.eResource();
			ModelDescription result = null;
			if ( r.getErrors().isEmpty() || FORCE_VALIDATION ) {
				result = r.doValidate();
			} else {
				GuiUtils.debug("Syntactic errors detected. No validation");
			}
			boolean hasError = result == null || !result.getErrors().isEmpty();
			if ( result != null ) {
				for ( GamlCompilationError warning : result.getWarnings() ) {
					add(warning);
				}
				if ( hasError ) {
					for ( GamlCompilationError error : result.getErrors() ) {
						add(error);
					}
					// Commenting the disposal to see if it plays any role in
					// garbage collecting -- and to enable content assist
					// result.dispose();
					r.setModelDescription(true, null);
				} else {
					r.setModelDescription(false, result);
				}
			} else {
				r.setModelDescription(true, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GamlResource getCurrentRessource() {
		EObject e;
		try {
			e = getCurrentObject();
		} catch (NullPointerException ex) {
			return null;
		}
		if ( e == null ) { return null; }
		return (GamlResource) e.eResource();
	}

	public void add(final GamlCompilationError e) {
		EObject object = (EObject) e.getStatement();
		if ( object == null ) {
			object = getCurrentObject();
		}
		if ( object.eResource() == null ) { return; }
		if ( object.eResource() != getCurrentRessource() ) {
			if ( !e.isWarning() ) {
				EObject imp = findImport(object.eResource().getURI());
				if ( imp != null ) {
					error("Fix import error first: " + e.toString(), imp, null, "ERROR",
						(String[]) null);
				}
			}
			return;
		}
		if ( e.isWarning() ) {
			warning(e.toString(), object, null, 0, e.getCode(), e.getData());
		} else {
			error(e.toString(), object, null, 0, e.getCode(), e.getData());
		}
	}

	private EObject findImport(final URI uri) {
		Model m = (Model) getCurrentObject();
		for ( Import imp : m.getImports() ) {
			URI iu = URI.createURI(imp.getImportURI()).resolve(getCurrentRessource().getURI());
			if ( uri.equals(iu) ) { return imp; }
		}
		return null;
	}
}
