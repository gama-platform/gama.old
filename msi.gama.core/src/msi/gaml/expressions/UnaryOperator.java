/*********************************************************************************************
 *
 *
 * 'UnaryOperator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.*;
import java.util.Set;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.*;
import msi.gaml.types.*;

/**
 * The Class UnaryOperator.
 */
public class UnaryOperator extends AbstractExpression implements IOperator {

	final protected IExpression child;
	final OperatorProto prototype;

	public static IExpression create(final OperatorProto proto, final IDescription context,
		final IExpression ... child) {
		UnaryOperator u = new UnaryOperator(proto, context, child);
		if ( u.isConst() ) {
			IExpression e = GAML.getExpressionFactory().createConst(u.value(null), u.getType(), u.serialize(false));
			// System.out.println(" ==== Simplification of " + u.toGaml() + " into " + e.toGaml());
		}
		return u;
	}

	@Override
	public boolean isConst() {
		return prototype.canBeConst && child.isConst();
	}

	@Override
	public String getDefiningPlugin() {
		return prototype.getDefiningPlugin();
	}

	public UnaryOperator(final OperatorProto proto, final IDescription context, final IExpression ... child) {
		setName(proto.getName());
		this.child = child[0];
		this.prototype = proto;
		type = proto.returnType;
		computeType();
		proto.verifyExpectedTypes(context, child[0].getType().getContentType());
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		final Object childValue = prototype.lazy[0] ? child : child.value(scope);
		try {
			return prototype.helper.run(scope, childValue);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw e1;

		} catch (final Exception e) {
			final GamaRuntimeException ee = GamaRuntimeException.create(e, scope);
			ee.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw ee;
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		String s = literalValue();
		StringBuilder sb = new StringBuilder(s);
		if ( OperatorProto.noMandatoryParenthesis.contains(s) ) {
			parenthesize(sb, child);
		} else {
			sb.append("(").append(child.serialize(includingBuiltIn)).append(")");
		}
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	@Override
	public String toString() {
		return literalValue() + "(" + child + ")";
	}

	@Override
	public String getTitle() {
		final StringBuilder sb = new StringBuilder(50);
		sb.append("operator <b>").append(getName()).append("</b> (");
		sb.append(child == null ? prototype.signature : child.getType().getTitle());
		sb.append(") returns ").append(getType().getTitle());
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		return prototype.getDocumentation();
	}

	private IType computeType(final int t, final IType def) {
		if ( t == NONE ) { return def; }
		if ( t == FIRST_ELEMENT_CONTENT_TYPE ) {
			if ( child instanceof ListExpression ) {
				final IExpression[] array = ((ListExpression) child).getElements();
				if ( array.length == 0 ) { return Types.NO_TYPE; }
				return array[0].getType().getContentType();
			} else if ( child instanceof MapExpression ) {
				final IExpression[] array = ((MapExpression) child).valuesArray();
				if ( array.length == 0 ) { return Types.NO_TYPE; }
				return array[0].getType().getContentType();
			} else {
				IType tt = child.getType().getContentType().getContentType();
				if ( tt != Types.NO_TYPE ) { return tt; }
			}
			return def;
		} else if ( t == FIRST_CONTENT_TYPE_OR_TYPE ) {
			IType firstType = child.getType();
			final IType t2 = firstType.getContentType();
			if ( t2 == Types.NO_TYPE ) { return firstType; }
			return t2;
		}
		return t == FIRST_TYPE ? child.getType() : t == FIRST_CONTENT_TYPE ? child.getType().getContentType()
			: t == FIRST_KEY_TYPE ? child.getType().getKeyType() : t >= 0 ? Types.get(t) : def;
	}

	protected void computeType() {
		type = computeType(prototype.typeProvider, type);
		if ( type.isContainer() ) {
			IType contentType = computeType(prototype.contentTypeProvider, type.getContentType());
			// WARNING Special case for pairs of map. See if it works for other fields as well
			if ( contentType.isContainer() && contentType.getKeyType() == Types.NO_TYPE &&
				contentType.getContentType() == Types.NO_TYPE ) {
				contentType =
					GamaType.from(contentType, child.getType().getKeyType(), child.getType().getContentType());
			}
			IType keyType = computeType(prototype.keyTypeProvider, type.getKeyType());
			type = GamaType.from(type, keyType, contentType);

		}

	}

	public boolean hasChildren() {
		return true;
	}

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		return new UnaryOperator(prototype, null, child.resolveAgainst(scope));
	}

	@Override
	public IExpression arg(final int i) {
		return i == 0 ? child : null;
	}

	@Override
	public OperatorProto getPrototype() {
		return prototype;
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectPlugins(final Set<String> plugins) {
		prototype.collectPlugins(plugins);
		child.collectPlugins(plugins);
	}

}
