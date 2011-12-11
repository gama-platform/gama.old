/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IExpressionFactory;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.type;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(value = IType.FLOAT_STR, id = IType.FLOAT, wraps = { Double.class, double.class })
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
	public boolean isSuperTypeOf(final IType type) {
		return type instanceof GamaIntegerType;
	}

	@Override
	public IExpression coerce(final IExpression expr, final IExpressionFactory factory)
		throws GamlException {
		if ( expr.type() == this ) { return expr; }
		//GUI.debug("Float type coercing =" + expr);
		return factory.createUnaryExpr(toString(), expr);
	}
}
