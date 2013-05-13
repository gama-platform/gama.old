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

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
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
@type(name = IKeyword.INT, id = IType.INT, wraps = { Integer.class, int.class, Long.class }, kind = ISymbolKind.Variable.NUMBER)
public class GamaIntegerType extends GamaType<Integer> {

	@Override
	public Integer cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static Integer staticCast(final IScope scope, final Object obj, final Object param) {
		if ( obj instanceof Integer ) { return (Integer) obj; }
		if ( obj instanceof Number ) { return ((Number) obj).intValue(); }
		if ( obj instanceof Color ) { return ((Color) obj).getRGB(); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).getIndex(); }
		if ( obj instanceof String ) {
			String n = new String(obj.toString());
			// removing whitespaces
			n = n.replaceAll("\\p{Zs}", "");
			try {
				// If the string contains an hexadecimal number, parse it with a radix of 16.
				if ( n.startsWith("#") ) { return Integer.parseInt(n.substring(1), 16); }
				// Otherwise use by default a "natural" radix of 10 (can be bypassed with the
				// as_int operator, which will put the radix in the param argument)
				int radix = 10;
				if ( param instanceof Integer ) {
					radix = (Integer) param;
				}
				return Integer.parseInt(n, radix);
			} catch (NumberFormatException e) {
				// for ( Character c : n.toCharArray() ) {
				// System.out.printf("U+%04x ", (int) c);
				// }
				throw GamaRuntimeException.create(e);
			}
		}
		if ( obj instanceof Boolean ) { return (Boolean) obj ? 1 : 0; }
		return 0;
	}

	@Override
	public Integer getDefault() {
		return 0;
	}

	@Override
	public boolean isTranslatableInto(final IType type) {
		return type == this || type.id() == IType.FLOAT || type == Types.NO_TYPE;
	}

	@Override
	public IType coerce(final IType type, final IDescription context) {
		if ( type == this ) { return null; }
		// GUI.debug("Float type coercing =" + expr);
		return this;
	}

	@Override
	public IType findCommonSupertypeWith(IType type) {
		return type == this ? this : type.id() == IType.FLOAT ? type : Types.NO_TYPE;
	}

}
