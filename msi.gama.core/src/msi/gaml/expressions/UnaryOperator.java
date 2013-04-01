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

import static msi.gama.precompiler.ITypeProvider.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IOpRun;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.*;

/**
 * The Class UnaryOpCustomExpr.
 */
public class UnaryOperator extends AbstractExpression implements IOperator {

	protected IExpression child;
	private final IOpRun helper;
	private final boolean canBeConst;
	private final short typeProvider;
	private final short contentTypeProvider;
	private final int[] expectedContentType;

	@Override
	public boolean isConst() {
		return canBeConst && child.isConst();
	}

	public UnaryOperator(final IType rt, final IOpRun exec, final boolean canBeConst,
		final short tProv, final short ctProv, int[] expectedContentType) {
		type = rt;
		helper = exec;
		this.canBeConst = canBeConst;
		typeProvider = tProv;
		contentTypeProvider = ctProv;
		this.expectedContentType = expectedContentType;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {

		Object childValue = child.value(scope);
		try {
			return helper.run(scope, childValue, null);
		} catch (GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw e1;

		} catch (Exception e) {
			GamaRuntimeException ee = new GamaRuntimeException(e);
			ee.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw ee;
		}
	}

	@Override
	public UnaryOperator copy() {
		UnaryOperator copy =
			new UnaryOperator(type, helper, canBeConst, typeProvider, contentTypeProvider,
				expectedContentType);
		copy.setName(getName());
		copy.contentType = contentType;
		return copy;
	}

	@Override
	public String toGaml() {
		return literalValue() + " (" + child.toGaml() + ")";
	}

	@Override
	public String toString() {
		return literalValue() + "(" + child + ")";
	}

	@Override
	public String getTitle() {
		StringBuilder sb = new StringBuilder(50);
		sb.append("Unary operator <b>").append(getName()).append("</b><br>");
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		// TODO insert here a @documentation if possible
		sb.append("Returns a value of type ").append(type.toString()).append("<br>");
		sb.append("Operand of type ").append(child.getType().toString()).append("<br>");
		return sb.toString();
	}

	public void computeType() {
		short t = typeProvider;
		type =
			t == CHILD_TYPE ? child.getType() : t == CHILD_CONTENT_TYPE ? child.getContentType()
				: t >= 0 ? Types.get(t) : type;
	}

	public void computeContentType() {
		short t = contentTypeProvider;
		if ( t == FIRST_ELEMENT_CONTENT_TYPE ) {
			if ( child instanceof ListExpression ) {
				IExpression[] array = ((ListExpression) child).elements;
				if ( array.length == 0 ) {
					contentType = Types.NO_TYPE;
				} else {
					contentType = array[0].getContentType();
				}
			} else if ( child instanceof MapExpression ) {
				IExpression[] array = ((MapExpression) child).valuesArray();
				if ( array.length == 0 ) {
					contentType = Types.NO_TYPE;
				} else {
					contentType = array[0].getContentType();
				}

			}
		} else {
			contentType =
				t == CHILD_TYPE ? child.getType() : t == CHILD_CONTENT_TYPE ? child
					.getContentType() : t >= 0 ? Types.get(t) : type.id() == IType.LIST ||
					type.id() == IType.MATRIX || type.id() == IType.MAP ||
					type.id() == IType.CONTAINER ? child.getContentType() : type
					.defaultContentType();
		}
	}

	@Override
	public UnaryOperator init(final String name, final IDescription context,
		final IExpression ... args) {
		setName(name);
		setChild(context, args[0]);
		computeType();
		computeContentType();
		return this;
	}

	private void setChild(final IDescription context, final IExpression c) {
		child = c;
		IType ct = c.getContentType();
		if ( expectedContentType.length == 0 ) { return; }
		for ( int i = 0; i < expectedContentType.length; i++ ) {
			if ( ct.isTranslatableInto(Types.get((short) expectedContentType[i])) ) { return; }
		}
		context.error(
			"The " + getName() + " operator cannot operate on elements of type " + ct.toString(),
			IGamlIssue.WRONG_TYPE);
	}

	public boolean hasChildren() {
		return true;
	}

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		UnaryOperator copy = copy();
		copy.child = child.resolveAgainst(scope);
		return copy;
	}

	@Override
	public IExpression arg(final int i) {
		return i == 0 ? child : null;
	}

}
