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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IOpRun;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * The Class UnaryOpCustomExpr.
 */
public class BinaryOperator extends AbstractBinaryOperator {

	protected final boolean lazy;
	protected final boolean canBeConst;
	protected final IOpRun helper;
	protected final short typeProvider;
	protected final short contentTypeProvider;

	@Override
	public boolean isConst() {
		// if ( optimizedExpression != null ) { return optimizedExpression.isConst(); }
		return canBeConst && left.isConst() && right.isConst();
	}

	public BinaryOperator(final IType ret, final IOpRun exec, final boolean canBeConst,
		final short tProv, final short ctProv, final boolean lazy) {
		this.lazy = lazy;
		this.canBeConst = canBeConst;
		type = ret;
		helper = exec;
		typeProvider = tProv;
		contentTypeProvider = ctProv;
	}

	@Override
	public BinaryOperator init(final String operator, final IExpression left,
		final IExpression right, final IDescription context) {
		setName(operator);
		this.left = left;
		this.right = right;
		computeType(context);
		computeContentType(context);
		return this;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object leftVal = left.value(scope);
		Object rightVal = lazy ? right : right.value(scope);
		try {
			Object result = helper.run(scope, leftVal, rightVal);
			return result;
		} catch (GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + leftVal +
				" and " + rightVal);
			throw e1;
		} catch (Exception e) {
			GamaRuntimeException ee = new GamaRuntimeException(e);
			ee.addContext("when applying the " + literalValue() + " operator on " + leftVal +
				" and " + rightVal);
			throw ee;
		}
	}

	public void computeType(final IDescription context) {
		short t = typeProvider;
		if ( t == BOTH ) {
			IType l = left.getType();
			IType r = right.getType();
			if ( left.isConst() && left.value(null) == null ) {
				type = r;
				return;
			}
			if ( right.isConst() && right.value(null) == null ) {
				type = l;
				return;
			}
			if ( l == r ) {
				type = l;
			} else {
				short lid = l.id();
				short rid = r.id();
				if ( l.id() == IType.INT && r.id() == IType.FLOAT || r.id() == IType.INT &&
					l.id() == IType.FLOAT ) {
					type = Types.get(IType.FLOAT);
					return;
				}
				if ( lid != IType.NONE && rid == IType.NONE ) {
					type = l;
					return;
				}
				if ( rid != IType.NONE && lid == IType.NONE ) {
					type = r;
					return;
				}
				context.flagError(
					"Content types of left and right operands do not match (" + l.toString() + "," +
						r.toString() + "). Impossible to infer the type of the expression",
					IGamlIssue.UNMATCHED_OPERANDS);
			}
			return;
		}
		type =
			t == LEFT_TYPE ? left.getType() : t == RIGHT_TYPE ? right.getType()
				: t == LEFT_CONTENT_TYPE ? left.getContentType() : t == RIGHT_CONTENT_TYPE ? right
					.getContentType() : t >= 0 ? Types.get(t) : type;
	}

	public void computeContentType(final IDescription context) {
		short t = contentTypeProvider;
		if ( t == BOTH ) {
			IType l = left.getContentType();
			IType r = right.getContentType();
			if ( left.isConst() && left.value(null) == null ) {
				contentType = r;
				return;
			}
			if ( right.isConst() && right.value(null) == null ) {
				contentType = l;
				return;
			}
			if ( l == r ) {
				contentType = l;
			} else {
				short lid = l.id();
				short rid = r.id();
				if ( lid == IType.INT && rid == IType.FLOAT || rid == IType.INT &&
					lid == IType.FLOAT ) {
					contentType = Types.get(IType.FLOAT);
					return;
				}
				if ( lid != IType.NONE && rid == IType.NONE ) {
					contentType = l;
					return;
				}
				if ( rid != IType.NONE && lid == IType.NONE ) {
					contentType = r;
					return;
				}
				context.flagError(
					"Content types of left and right operands do not match (" + l.toString() + "," +
						r.toString() + "). Impossible to infer the content type of the expression",
					IGamlIssue.UNMATCHED_OPERANDS);
			}
			return;
		}
		contentType =
			t == LEFT_TYPE ? left.getType() : t == RIGHT_TYPE ? right.getType()
				: t == LEFT_CONTENT_TYPE ? left.getContentType() : t == RIGHT_CONTENT_TYPE ? right
					.getContentType() : t >= 0 ? Types.get(t) : type.id() == IType.LIST ||
					type.id() == IType.MATRIX ? left.getContentType() : type.isSpeciesType() ? type
					: type.defaultContentType();
	}

	@Override
	public IOperator copy() {
		return new BinaryOperator(type, helper, canBeConst, typeProvider, contentTypeProvider, lazy);
	}

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		BinaryOperator copy = (BinaryOperator) copy();
		copy.left = left.resolveAgainst(scope);
		copy.right = right.resolveAgainst(scope);
		return copy;
	}

	@Override
	public String getTitle() {
		StringBuilder sb = new StringBuilder(50);
		sb.append("Binary operator <b>").append(getName()).append("</b><br>");
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		// TODO insert here a @documentation if possible
		sb.append("Returns a value of type ").append(type.toString()).append("<br>");
		sb.append("Left operand of type ").append(left.getType().toString()).append("<br>");
		sb.append("Right operand of type ").append(right.getType().toString()).append("<br>");
		return sb.toString();
	}

	public static class BinaryVarOperator extends BinaryOperator implements IVarExpression {

		public BinaryVarOperator(final IType ret, final IOpRun exec, final boolean canBeConst,
			final short type, final short contentType, final boolean lazy) {
			super(ret, exec, canBeConst, type, contentType, lazy);
		}

		@Override
		public void setVal(final IScope scope, final Object v, final boolean create)
			throws GamaRuntimeException {
			IAgent agent = Cast.asAgent(scope, left.value(scope));
			if ( agent == null || agent.dead() ) { return; }
			scope.setAgentVarValue(agent, ((IVarExpression) right).literalValue(), v);
		}

		@Override
		public void setType(final IType type) {}

		@Override
		public void setContentType(final IType type) {}

		@Override
		public IOperator copy() {
			return new BinaryVarOperator(type, helper, canBeConst, typeProvider,
				contentTypeProvider, lazy);
		}
	}
}
