/*********************************************************************************************
 *
 * 'UnaryOperator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.FIRST_CONTENT_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_CONTENT_TYPE_OR_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_KEY_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_TYPE;
import static msi.gama.precompiler.ITypeProvider.NONE;
import static msi.gama.precompiler.ITypeProvider.WRAPPED;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class UnaryOperator.
 */
@SuppressWarnings ({ "rawtypes" })
public class UnaryOperator extends AbstractExpression implements IOperator {

	final protected IExpression child;
	final OperatorProto prototype;

	public static IExpression create(final OperatorProto proto, final IDescription context, final IExpression child) {
		final UnaryOperator u = new UnaryOperator(proto, context, child);
		if (u.isConst() && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue()) {
			final IExpression e =
					GAML.getExpressionFactory().createConst(u.value(null), u.getGamlType(), u.serialize(false));
			return e;
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

	public UnaryOperator(final OperatorProto proto, final IDescription context, final IExpression... child) {
		// setName(proto.getName());
		this.child = child[0];
		this.prototype = proto;
		if (proto != null) {
			type = proto.returnType;
			computeType();
			proto.verifyExpectedTypes(context, child[0].getGamlType().getContentType());
		}
	}

	@Override
	public Object _value(final IScope scope) throws GamaRuntimeException {
		final Object childValue = prototype.lazy[0] ? child : child.value(scope);
		try {
			return prototype.helper.get(scope, childValue);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw e1;

		} catch (final Throwable e) {
			// System.out.println(e + " when applying the " + literalValue() + "
			// operator on " + childValue);
			final GamaRuntimeException ee = GamaRuntimeException.create(e, scope);
			ee.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw ee;
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final String s = literalValue();
		final StringBuilder sb = new StringBuilder(s);
		if (OperatorProto.noMandatoryParenthesis.contains(s)) {
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
		sb.append("operator ").append(getName()).append(" (");
		sb.append(child == null ? prototype.signature : child.getGamlType().getTitle());
		sb.append(") returns ").append(getGamlType().getTitle());
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		return prototype.getDocumentation();
	}

	private IType computeType(final int t, final IType def) {
		if (t == NONE) { return def; }
		if (t == WRAPPED) { return child.getGamlType().getWrappedType(); }
		if (t == FIRST_ELEMENT_CONTENT_TYPE) {
			if (child instanceof ListExpression) {
				final IExpression[] array = ((ListExpression) child).getElements();
				if (array.length == 0) { return Types.NO_TYPE; }
				return array[0].getGamlType().getContentType();
			} else if (child instanceof MapExpression) {
				final IExpression[] array = ((MapExpression) child).valuesArray();
				if (array.length == 0) { return Types.NO_TYPE; }
				return array[0].getGamlType().getContentType();
			} else {
				final IType tt = child.getGamlType().getContentType().getContentType();
				if (tt != Types.NO_TYPE) { return tt; }
			}
			return def;
		} else if (t == FIRST_CONTENT_TYPE_OR_TYPE) {
			final IType firstType = child.getGamlType();
			final IType t2 = firstType.getContentType();
			if (t2 == Types.NO_TYPE) { return firstType; }
			return t2;
		}
		return t == FIRST_TYPE ? child.getGamlType() : t == FIRST_CONTENT_TYPE ? child.getGamlType().getContentType()
				: t == FIRST_KEY_TYPE ? child.getGamlType().getKeyType() : t >= 0 ? Types.get(t) : def;
	}

	protected void computeType() {
		type = computeType(prototype.typeProvider, type);
		if (type.isContainer()) {
			IType contentType = computeType(prototype.contentTypeProvider, type.getContentType());
			// WARNING Special case for pairs of map. See if it works for other
			// fields as well
			if (contentType.isContainer() && contentType.getKeyType() == Types.NO_TYPE
					&& contentType.getContentType() == Types.NO_TYPE) {
				contentType = GamaType.from(contentType, child.getGamlType().getKeyType(),
						child.getGamlType().getContentType());
			}
			final IType keyType = computeType(prototype.keyTypeProvider, type.getKeyType());
			type = GamaType.from(type, keyType, contentType);

		}

	}

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		return new UnaryOperator(prototype, null, child.resolveAgainst(scope));
	}

	@Override
	public String getName() {
		return prototype.getName();
	}

	@Override
	public IExpression arg(final int i) {
		return i == 0 ? child : null;
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		prototype.collectMetaInformation(meta);
		child.collectMetaInformation(meta);
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final ICollector<VariableDescription> result) {
		child.collectUsedVarsOf(species, result);
	}

	@Override
	public boolean isContextIndependant() {
		return child.isContextIndependant();
	}

	@Override
	public OperatorProto getPrototype() {
		return prototype;
	}

	@Override
	public void visitSuboperators(final IOperatorVisitor visitor) {
		if (child instanceof IOperator) {
			visitor.visit((IOperator) child);
		}

	}

}
