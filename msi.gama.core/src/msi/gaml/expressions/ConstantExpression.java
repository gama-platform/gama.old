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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

/**
 * ConstantValueExpr.
 * 
 * @author drogoul 22 ao√ªt 07
 */

public class ConstantExpression extends AbstractExpression {

	Object value;

	public ConstantExpression(final Object val, final IType t, final IType ct) {
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
		return value == null ? "nil" : value.toString();
		// value instanceof String ? "'" + value + "'" : value
		// .toString();
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public String toGaml() {
		return StringUtils.toGaml(value);
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Literal expression of type " + type.toString();
	}

	@Override
	public String getTitle() {
		return literalValue();
	}

}
