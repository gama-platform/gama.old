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
package msi.gama.lang.gaml.ui.labeling;

import java.util.Map;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
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

	//
	// String image(final SetEval ele) {
	// return "_set.png";
	// }
	//
	// String text(final SetEval ele) {
	// return "set";
	// }
	//
	// String image(final LoopEval ele) {
	// return "_loop.png";
	// }
	//
	// String text(final LoopEval ele) {
	// return "loop";
	// }
	//
	// String image(final IfEval ele) {
	// return "_if.png";
	// }
	//
	// String text(final IfEval ele) {
	// return "if";
	// }
	//
	// String image(final DoEval ele) {
	// return "_do.png";
	// }
	//
	// String text(final DoEval ele) {
	// return "do";
	// }
	//
	// String image(final ReturnEval ele) {
	// return "_return.png";
	// }
	//
	// String text(final ReturnEval ele) {
	// return "return";
	// }

	// Statement : keyword.value
	String image(final/* Sub */Statement ele) {
		String kw = EGaml.getKeyOf(ele);
		if ( kw.equals("var") || kw.equals("const") ) {
			for ( Map.Entry<String, Expression> f : EGaml.getFacetsOf(ele).entrySet() ) {
				if ( f.getKey().equals("type") && f.getValue() instanceof VariableRef ) {
					VariableRef type = (VariableRef) f.getValue();
					return "_" + (type.getRef() == null ? "" : type.getRef().getName()) + ".png";
				}
			}
		}
		return "_" + kw + ".png";
	}

	// String text(final Evaluation ele) {
	// return ele.getKey().getRef().getName();
	// }

	// dirty image for now (debug purpose, proposal provider)
	String image(final FacetExpr ele) { // FIXME
		return "gaml_facet.png";
	}

	// String image(final DefKeyword ele) {
	// return "gaml_keyword.png";
	// }

	// String image(final DefBinaryOp ele) {
	// return "gaml_binaryop.png";
	// }

	// String image(final DefReserved ele) {
	// return "gaml_reserved.png";
	// }

	// String image(final GamlUnitRef ele) {
	// return "gaml_unit.png";
	// }
}
