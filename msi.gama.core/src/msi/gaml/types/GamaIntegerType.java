/*********************************************************************************************
 *
 *
 * 'GamaIntegerType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@type(name = IKeyword.INT,
	id = IType.INT,
	wraps = { Integer.class, int.class, Long.class },
	kind = ISymbolKind.Variable.NUMBER)
public class GamaIntegerType extends GamaType<Integer> {

	@Override
	public Integer cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static Integer staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if ( obj instanceof Integer ) { return (Integer) obj; }
		if ( obj instanceof Number ) { return ((Number) obj).intValue(); }
		if ( obj instanceof Color ) { return ((Color) obj).getRGB(); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).getIndex(); }
		if ( obj instanceof String ) {
			String n = obj.toString();
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
				// throw GamaRuntimeException.create(e);
				// Addresses Issue 846 by providing a way to continue the casting into an int
				Double d = 0d;
				try {
					d = Double.parseDouble(n);
				} catch (NumberFormatException e1) {
					return 0;
				}
				return d.intValue();
			}
		}
		if ( obj instanceof Boolean ) { return (Boolean) obj ? 1 : 0; }
		if ( obj instanceof GamaFont ) { return ((GamaFont) obj).getSize(); }
		return 0;
	}

	@Override
	public Integer getDefault() {
		return 0;
	}

	@Override
	public boolean isTranslatableInto(final IType type) {
		return type.isNumber() || type == Types.NO_TYPE;
	}

	@Override
	public IType coerce(final IType type, final IDescription context) {
		if ( type == this ) { return null; }
		return this;
	}

	@Override
	public IType findCommonSupertypeWith(final IType type) {
		return type == this ? this : type.id() == IType.FLOAT ? type : Types.NO_TYPE;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isNumber() {
		return true;
	}

}
