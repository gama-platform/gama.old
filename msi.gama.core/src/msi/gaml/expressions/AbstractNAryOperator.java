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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import msi.gaml.compilation.GamlElementDocumentation;
import msi.gaml.types.IType;

/**
 * AbstractBinaryOperator
 * @author drogoul 23 august 07
 */
public abstract class AbstractNAryOperator extends AbstractExpression implements IOperator {

	protected IExpression[] exprs;
	protected GamlElementDocumentation doc;

	@Override
	public boolean containsAny(final Class<? extends IExpression> clazz) {
		if ( super.containsAny(clazz) ) { return true; }
		for ( IExpression expr : exprs ) {
			if ( expr.containsAny(clazz) ) { return true; }
		}
		return false;
	}

	@Override
	public String toString() {
		String result = literalValue() + "(";
		if ( exprs != null ) {
			for ( int i = 0; i < exprs.length; i++ ) {
				String l = exprs[i] == null ? "null" : exprs[i].toString();
				result += l + (i != exprs.length - 1 ? "," : "");
			}
		}
		return result + ")";
	}

	@Override
	public String toGaml() {
		String result = literalValue() + "(";
		if ( exprs != null ) {
			for ( int i = 0; i < exprs.length; i++ ) {
				String l = exprs[i] == null ? "nil" : exprs[i].toGaml();
				result += l + (i != exprs.length - 1 ? "," : "");
			}
		}
		return result + ")";
	}

	public boolean hasChildren() {
		return true;
	}

	@Override
	public IExpression arg(final int i) {
		if ( exprs == null ) { return null; }
		return exprs[i];
	}

	@Override
	public String getTitle() {
		StringBuilder sb = new StringBuilder(50);
		sb.append("operator ").append(getName()).append(" (");
		if ( exprs != null ) {
			for ( int i = 0; i < exprs.length; i++ ) {
				sb.append(exprs[i] == null ? "nil" : exprs[i].getType());
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
		}
		sb.append(") returns ");
		IType type = getType();
		sb.append(type.toString());
		if ( type.hasContents() ) {
			sb.append("&lt;").append(getKeyType().toString()).append(",").append(getContentType().toString())
				.append("&gt;");
		}
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		// TODO insert here a @documentation if possible
		if ( doc != null ) {
			sb.append(doc.getMain());
		}
		return sb.toString();
	}

	@Override
	public void setDoc(final GamlElementDocumentation doc) {
		this.doc = doc;
	}
}
