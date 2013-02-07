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
package msi.gaml.expressions;


/**
 * AbstractBinaryOperator
 * @author drogoul 23 august 07
 */
public abstract class AbstractNAryOperator extends AbstractExpression implements IOperator {

	protected IExpression[] exprs;

	@Override
	public String toString() {
		String result = literalValue() + "(";
		for ( int i = 0; i < exprs.length; i++ ) {
			String l = exprs[i] == null ? "null" : exprs[i].toString();
			result += l + (i != exprs.length - 1 ? "," : "");
		}
		return result;
	}

	@Override
	public String toGaml() {
		String result = literalValue() + "(";
		for ( int i = 0; i < exprs.length; i++ ) {
			String l = exprs[i] == null ? "nil" : exprs[i].toGaml();
			result += l + (i != exprs.length - 1 ? "," : "");
		}
		return result;
	}

	public boolean hasChildren() {
		return true;
	}

	@Override
	public IExpression arg(final int i) {
		return exprs[i];
	}
}
