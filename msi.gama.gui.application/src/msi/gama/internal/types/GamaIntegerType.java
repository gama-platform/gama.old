/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IExpressionFactory;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.GamaColor;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(value = IType.INT_STR, id = IType.INT, wraps = { Integer.class, int.class, Long.class })
public class GamaIntegerType extends GamaType<Integer> {

	@Override
	public Integer cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static Integer staticCast(final IScope scope, final Object obj, final Object param) {
		if ( obj instanceof Integer ) { return (Integer) obj; }
		if ( obj instanceof Number ) { return ((Number) obj).intValue(); }
		if ( obj instanceof GamaColor ) { return ((GamaColor) obj).getRGB(); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).getIndex(); }
		if ( obj instanceof String ) {
			try {
				return Integer.decode((String) obj);
			} catch (NumberFormatException e) {
				return 0;
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
	public boolean isSuperTypeOf(final IType type) {
		return type instanceof GamaFloatType;
	}

	@Override
	public IExpression coerce(final IExpression expr, final IExpressionFactory factory)
		throws GamlException {
		if ( expr.type() == this ) { return expr; }
		return factory.createUnaryExpr(toString(), expr);
	}

}
