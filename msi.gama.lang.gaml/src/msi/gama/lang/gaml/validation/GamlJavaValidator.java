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

import static msi.gama.lang.gaml.gaml.GamlPackage.eINSTANCE;
import msi.gama.lang.gaml.gaml.*;
import msi.gaml.compilation.GamaBundleLoader;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	@Check
	public void checkImports(final Model model) {
		if ( !GamaBundleLoader.contributionsLoaded || model == null ) { return; }
		Resource r = model.eResource();
		for ( Import imp : model.getImports() ) {
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(r.getURI());
			XtextResource ir = (XtextResource) r.getResourceSet().getResource(iu, true);
			EList<Resource.Diagnostic> errors = ir.getErrors();
			if ( !errors.isEmpty() ) {
				error("Imported file " + importUri + " has " + errors.size() +
					" error(s). Correct them first", imp, eINSTANCE.getImport_ImportURI(), -1);
			}
		}
	}

}
