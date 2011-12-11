/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gaml.expressions;

import msi.gama.interfaces.*;
import msi.gama.util.Cast;

/**
 * ConstantValueExpr.
 * 
 * @author drogoul 22 août 07
 */

public class ConstantExpression extends AbstractExpression {

	Object value;

	ConstantExpression(final Object val, final IType t, final IType ct) {
		value = val;
		type = t;
		contentType = ct;
		setName(value == null ? "nil" : value.toString());
	}

	@Override
	public Object value(final IScope scope) {
		return value;
	}

	@Override
	public IType getContentType() {
		return contentType;
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public String toString() {
		return value == null ? "nil" : value instanceof String ? "'" + value + "'" : value
			.toString();
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public String toGaml() {
		return Cast.toGaml(value);
	}

}
