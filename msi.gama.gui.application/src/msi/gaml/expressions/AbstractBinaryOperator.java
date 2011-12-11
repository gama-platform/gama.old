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
package msi.gaml.expressions;

import msi.gama.interfaces.IExpression;
import msi.gama.internal.compilation.IOperator;
import msi.gama.internal.expressions.IExpressionParser;

/**
 * AbstractBinaryOperator
 * @author drogoul 23 august 07
 */
public abstract class AbstractBinaryOperator extends AbstractExpression implements IOperator {

	protected IExpression left;
	protected IExpression right;

	@Override
	public final IExpression left() {
		return left;
	}

	@Override
	public IExpression right() {
		return right;
	}

	@Override
	public String toString() {
		boolean isRef = literalValue().equals(".") || literalValue().endsWith(IExpressionParser.AS);
		return "(" + left.toString() + ") " + literalValue() + (isRef ? " " : " (") +
			right.toString() + (isRef ? "" : ")");
	}

	@Override
	public String toGaml() {
		String l = "(" + left.toGaml() + ") ";
		boolean isRef = literalValue().equals(".") || literalValue().endsWith(IExpressionParser.AS);
		String r = isRef ? right.toGaml() : "(" + right.toGaml() + ") ";
		return l + " " + literalValue() + " " + r;
	}

	public boolean hasChildren() {
		return true;
	}
}
