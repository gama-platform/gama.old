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
package msi.gaml.descriptions;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;

public class BasicExpressionDescription implements IExpressionDescription {

	protected IExpression expression;

	public BasicExpressionDescription(final IExpression expr) {
		expression = expr;
	}

	@Override
	public String toString() {
		return expression != null ? expression.toGaml() : "";
	}

	@Override
	public boolean equals(final Object c) {
		if ( c == null ) { return false; }
		if ( c instanceof IExpressionDescription ) { return ((IExpressionDescription) c)
			.equalsString(toString()); }
		return false;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

	@Override
	public void dispose() {
		expression = null;
	}

	@Override
	public void setExpression(final IExpression expr) {
		expression = expr;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#compile(msi.gaml.descriptions.IDescription,
	 *      msi.gaml.expressions.IExpressionFactory)
	 */
	@Override
	public IExpression compile(final IDescription context, final IExpressionFactory factory)
		throws GamaRuntimeException {
		if ( expression == null ) {
			expression = factory.createExpr(this, context);
		}
		return expression;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#compileAsLabel()
	 */
	@Override
	public IExpressionDescription compileAsLabel() {
		return new LabelExpressionDescription(StringUtils.toJavaString(toString()));
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#getAst()
	 */
	@Override
	public Object getAst() {
		return null;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#equalsString(java.lang.String)
	 */
	@Override
	public boolean equalsString(final String o) {
		return o == null ? false : o.equals(toString());
	}

}
