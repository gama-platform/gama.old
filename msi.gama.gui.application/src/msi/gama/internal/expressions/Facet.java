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
package msi.gama.internal.expressions;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.java.JavaConstExpression;
import msi.gama.kernel.exceptions.*;
import msi.gaml.expressions.AbstractExpression;

/**
 * Written by drogoul Modified on 27 aožt 2010
 * 
 * A pair that represents a facet used to initialize various objects in GAMA. Contains a key (the
 * name of the facet), an ExpressionDescription (text and tokens of the facet), and the IExpression
 * it represents. A Facet is itself an IExpression, which means it can be evaluated.
 */
public class Facet extends AbstractExpression {

	private ExpressionDescription description;
	private IExpression expression;
	boolean isLabel = false;

	public static Facet with(final Object value) {
		Facet f = new Facet(new JavaConstExpression(value));
		return f;
	}

	public Facet(final String expr) {
		this(expr, true);
	}

	public Facet(final String expr, final boolean tokenize) {
		this(new ExpressionDescription(expr, tokenize));
	}

	public Facet(final ExpressionDescription expr) {
		description = expr;
	}

	public Facet(final IExpression expr) {
		expression = expr;
		if ( expr != null ) {
			description = new ExpressionDescription(expr.toGaml(), false);
		}
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
		return description != null ? description.getStringAsLabel() : expression != null
			? expression.literalValue() : null;
	}

	public IExpression compile(final IDescription context, final IExpressionFactory factory)
		throws GamlException, GamaRuntimeException {
		if ( expression == null ) {
			expression =
				isLabel ? factory.createConst(description.getStringAsLabel()) : factory.createExpr(
					description, context);
		}
		return expression;
	}

	public IExpression getExpression() {
		return expression;
	}

	public ExpressionDescription getFacetDescription() {
		return description;
	}

	public void dispose() {
		expression = null;
	}

	public void setExpression(final IExpression expr) {
		// TODO The literal description is not touched. Allow to "reset" the facet later ?
		expression = expr;
	}

	@Override
	public boolean equals(final Object c) {
		if ( c instanceof String ) { return description == null ? false : description.equals(c); }
		if ( c instanceof Facet ) { return description == null ? false : description
			.equals(((Facet) c).description); }
		return false;
	}

	public Facet setLabel() {
		expression = new JavaConstExpression(description.getStringAsLabel());
		isLabel = true;
		return this;
	}

	public boolean isLabel() {
		return isLabel;
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}

	@Override
	public String toString() {
		return "facet " + (isLabel ? " label: " : " expr: ") + description == null
			? expression == null ? "no expression" : expression.toGaml() : isLabel ? description
				.getStringAsLabel() : description.getString();
	}

}
