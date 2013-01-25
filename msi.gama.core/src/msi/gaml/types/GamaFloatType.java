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
package msi.gaml.types;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(name = IType.FLOAT_STR, id = IType.FLOAT, wraps = { Double.class, double.class }, kind = ISymbolKind.Variable.NUMBER)
public class GamaFloatType extends GamaType<Double> {

	@Override
	public Double cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static Double staticCast(final IScope scope, final Object obj, final Object param) {
		if ( obj instanceof Double ) { return (Double) obj; }
		if ( obj instanceof Number ) { return ((Number) obj).doubleValue(); }
		if ( obj instanceof String ) {
			try {
				return Double.valueOf((String) obj);
			} catch (NumberFormatException e) {
				return 0d;
			}
		}
		if ( obj instanceof Boolean ) { return (Boolean) obj ? 1d : 0d; }
		return 0d;
	}

	@Override
	public Double getDefault() {
		return 0.0;
	}

	@Override
	public boolean isTranslatableInto(final IType type) {
		return type == this || type.id() == IType.INT || type == Types.NO_TYPE;
	}

	@Override
	public IType coerce(final IType type, final IDescription context) {
		if ( type == this ) { return null; }
		// GUI.debug("Float type coercing =" + expr);
		return this;
	}
}
