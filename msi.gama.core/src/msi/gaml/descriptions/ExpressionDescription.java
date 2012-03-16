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

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;
import msi.gaml.types.IType;

public class ExpressionDescription extends ArrayList<String> implements
/* CharSequence, */IExpression {

	final static StringBuilder sb = new StringBuilder();
	// private String string;
	// public int length;
	protected IExpression expression;
	protected boolean isLabel = false;
	protected Object ast;

	public ExpressionDescription(final Object ast) {
		this.ast = ast;
	}

	public ExpressionDescription(final String expr) {
		this(expr, true);
	}

	public ExpressionDescription(final IExpression expr) {
		this(expr == null ? "" : expr.toGaml(), false);
		expression = expr;
	}

	public ExpressionDescription(final String str, final boolean tokenize) {
		String expr = str == null ? "" : str;
		if ( tokenize ) {
			addAll(StringUtils.tokenize(expr));
		} else {
			add(expr);
		}
	}

	public ExpressionDescription(final List<String> tokens) {
		addAll(tokens);
	}

	public String subConcatenation(final int start, final int end) {
		sb.setLength(0);
		for ( int i = start; i < end; i++ ) {
			sb.append(get(i))/* .append(" ") */;
		}
		// sb.setLength(sb.length() - 1);
		return StringUtils.toJavaString(sb.toString());
	//	return sb.toString();
	}

	@Override
	public String toString() {
		if ( isLabel ) { return get(0); }
		return subConcatenation(0, size());
	}

	@Override
	public boolean equals(final Object c) {
		if ( c == null ) { return false; }
		if ( c instanceof String ) { return c.equals(toString()); }
		if ( c instanceof ExpressionDescription ) { return ((ExpressionDescription) c).toString()
			.equals(toString()); }
		return false;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return expression != null ? expression.value(scope) : null;
	}

	@Override
	public String toGaml() {
		return literalValue(); // ??
	}

	@Override
	public IType getContentType() {
		return expression.getContentType();
	}

	@Override
	public boolean isConst() {
		return expression != null ? expression.isConst() : false;
	}

	@Override
	public IType type() {
		return expression.type();
	}

	@Override
	public String literalValue() {
		return toString();
	}

	public IExpression getExpression() {
		return expression;
	}

	public void dispose() {
		expression = null;
	}

	public void setExpression(final IExpression expr) {
		expression = expr;
	}

	public ExpressionDescription setLabel() {
		String concat = toString();
		expression = new JavaConstExpression(concat);
		clear();
		add(concat);
		isLabel = true;
		return this;
	}

	public IExpression compile(final IDescription context, final IExpressionFactory factory)
		throws GamaRuntimeException {
		if ( expression == null ) {
			expression =
				isLabel ? factory.createConst(toString()) : factory.createExpr(this, context);
		}
		return expression;
	}

	public boolean isLabel() {
		return isLabel;
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}

	/**
	 * @return
	 */
	public Object getAst() {
		return ast;
	}

}
