/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui.labeling;

import msi.gama.lang.gaml.gaml.*;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;
import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class GamlLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public GamlLabelProvider(final AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	// Model : GAMA icon
	String image(final Model ele) {
		return "icon-16x16x32b.gif";
	}

	// Import
	String text(final Import ele) {
		String display = ele.getImportURI();
		int index = display.lastIndexOf('/');
		if ( index >= 0 ) {
			display = display.substring(index + 1);
		}
		return display;
	}

	String image(final Import ele) {
		return "_include.png";
	}

	String image(final SetEval ele) {
		return "_set.png";
	}

	String text(final SetEval ele) {
		return "set";
	}

	// Statement : keyword.value
	String image(final SubStatement ele) {
		String kw = ele.getKey().getRef().getName();
		if ( ele instanceof Definition && (kw.equals("var") || kw.equals("const")) ) {
			for ( FacetExpr f : ele.getFacets() ) {
				if ( f.getKey().getRef().getName().equals("type") &&
					f.getExpr() instanceof VariableRef ) {
					VariableRef type = (VariableRef) f.getExpr();
					return "_" + type.getRef().getName() + ".png";
				}
			}
		}
		return "_" + kw + ".png";
	}

	String text(final Evaluation ele) {
		return ele.getKey().getRef().getName();
	}

	// dirty image for now (debug purpose, proposal provider)
	String image(final DefFacet ele) {
		return "gaml_facet.png";
	}

	String image(final DefKeyword ele) {
		return "gaml_keyword.png";
	}

	String image(final DefBinaryOp ele) {
		return "gaml_binaryop.png";
	}

	String image(final DefReserved ele) {
		return "gaml_reserved.png";
	}

	String image(final DefUnit ele) {
		return "gaml_unit.png";
	}
}
