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
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.IVarExpression;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.*;
import msi.gama.util.Cast;

/**
 * The Class UnaryOpCustomExpr.
 */
public class BinaryOperator extends AbstractBinaryOperator {

	protected final boolean lazy;
	protected final boolean canBeConst;
	protected final IOperatorExecuter helper;
	protected final short typeProvider;
	protected final short contentTypeProvider;

	@Override
	public boolean isConst() {
		// if ( optimizedExpression != null ) { return optimizedExpression.isConst(); }
		return canBeConst && left.isConst() && right.isConst();
	}

	public BinaryOperator(final IType ret, final IOperatorExecuter exec, final boolean canBeConst,
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
		final IExpression right) throws GamlException {
		setName(operator);
		this.left = left;
		this.right = right;
		computeType();
		computeContentType();
		return this;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object leftVal = left.value(scope);
		Object rightVal = lazy ? right : right.value(scope);
		try {
			Object result = helper.execute(scope, leftVal, rightVal);
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

	public void computeType() throws GamlException {
		short t = typeProvider;
		if ( t == BOTH ) {
			IType l = left.type();
			IType r = right.type();
			if ( left == GamlExpressionParser.NIL_EXPR ) {
				type = r;
				return;
			}
			if ( right == GamlExpressionParser.NIL_EXPR ) {
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
				throw new GamlException("Content types of left and right operands do not match (" +
					l.toString() + "," + r.toString() +
					"). Impossible to infer the content type of the expression");
			}
			return;
		}
		type =
			t == LEFT_TYPE ? left.type() : t == RIGHT_TYPE ? right.type() : t == LEFT_CONTENT_TYPE
				? left.getContentType() : t == RIGHT_CONTENT_TYPE ? right.getContentType() : t >= 0
					? Types.get(t) : type;
	}

	public void computeContentType() throws GamlException {
		short t = contentTypeProvider;
		if ( t == BOTH ) {
			IType l = left.getContentType();
			IType r = right.getContentType();
			if ( left == GamlExpressionParser.NIL_EXPR ) {
				contentType = r;
				return;
			}
			if ( right == GamlExpressionParser.NIL_EXPR ) {
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
				throw new GamlException("Content types of left and right operands do not match (" +
					l.toString() + "," + r.toString() +
					"). Impossible to infer the content type of the expression");
			}
			return;
		}
		contentType =
			t == LEFT_TYPE ? left.type() : t == RIGHT_TYPE ? right.type() : t == LEFT_CONTENT_TYPE
				? left.getContentType() : t == RIGHT_CONTENT_TYPE ? right.getContentType() : t >= 0
					? Types.get(t) : type.id() == IType.LIST || type.id() == IType.MATRIX ? left
						.getContentType() : type.isSpeciesType() ? type : type.defaultContentType();
	}

	@Override
	public IOperator copy() {
		return new BinaryOperator(type, helper, canBeConst, typeProvider, contentTypeProvider, lazy);
	}

	public static class BinaryVarOperator extends BinaryOperator implements IVarExpression {

		public BinaryVarOperator(final IType ret, final IOperatorExecuter exec,
			final boolean canBeConst, final short type, final short contentType, final boolean lazy) {
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
