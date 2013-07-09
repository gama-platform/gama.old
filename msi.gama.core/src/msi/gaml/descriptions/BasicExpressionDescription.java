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
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import org.eclipse.emf.ecore.EObject;

public class BasicExpressionDescription implements IExpressionDescription {

	protected IExpression expression;
	protected EObject target;

	public BasicExpressionDescription(final IExpression expr) {
		expression = expr;
	}

	public BasicExpressionDescription(final EObject object) {
		target = object;
	}

	@Override
	public String toString() {
		return expression != null ? expression.toGaml() : "";
	}

	@Override
	public boolean equals(final Object c) {
		if ( c == null ) { return false; }
		if ( c == this ) { return true; }
		if ( c instanceof IExpressionDescription ) { return ((IExpressionDescription) c).equalsString(toString()); }
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

	@Override
	public IExpression compile(final IDescription context) {
		if ( expression == null ) {
			expression = GAML.getExpressionFactory().createExpr(this, context);
		}
		return expression;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#compileAsLabel()
	 */
	@Override
	public IExpressionDescription compileAsLabel() {
		IExpressionDescription newEd = LabelExpressionDescription.create(StringUtils.toJavaString(toString()));
		newEd.setTarget(getTarget());
		return newEd;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#equalsString(java.lang.String)
	 */
	@Override
	public boolean equalsString(final String o) {
		return o == null ? false : o.equals(toString());
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	@Override
	public EObject getTarget() {
		return target;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void setTarget(final EObject newTarget) {
		if ( target == null ) {
			target = newTarget;
		}
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		return Collections.EMPTY_SET;
	}

}
