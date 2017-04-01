/*********************************************************************************************
 *
 * 'AbstractNAryOperator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.BOTH;
import static msi.gama.precompiler.ITypeProvider.FIRST_CONTENT_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_CONTENT_TYPE_OR_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_KEY_TYPE;
import static msi.gama.precompiler.ITypeProvider.FIRST_TYPE;
import static msi.gama.precompiler.ITypeProvider.NONE;
import static msi.gama.precompiler.ITypeProvider.SECOND_CONTENT_TYPE;
import static msi.gama.precompiler.ITypeProvider.SECOND_CONTENT_TYPE_OR_TYPE;
import static msi.gama.precompiler.ITypeProvider.SECOND_DENOTED_TYPE;
import static msi.gama.precompiler.ITypeProvider.SECOND_KEY_TYPE;
import static msi.gama.precompiler.ITypeProvider.SECOND_TYPE;
import static msi.gama.precompiler.ITypeProvider.WRAPPED;

import java.util.Arrays;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * AbstractBinaryOperator
 * 
 * @author drogoul 23 august 07
 */
@SuppressWarnings ({ "rawtypes" })
public abstract class AbstractNAryOperator extends AbstractExpression implements IOperator {

	protected final IExpression[] exprs;
	protected OperatorProto prototype;

	public AbstractNAryOperator(final OperatorProto proto, final IExpression... expressions) {
		// Copy introduced in order to circumvent issue 1060
		if (expressions.length == 0 || expressions[0] == null)
			exprs = null;
		else
			exprs = Arrays.copyOf(expressions, expressions.length);
		this.prototype = proto;
		if (prototype != null) {
			type = prototype.returnType;
			computeType();
		} else {
			type = Types.NO_TYPE;
		}
	}

	// @Override
	// public OperatorProto getPrototype() {
	// return prototype;
	// }

	protected void computeType() {
		type = computeType(prototype.typeProvider, type, GamaType.TYPE);
		if (type.isContainer()) {
			final IType contentType =
					computeType(prototype.contentTypeProvider, type.getContentType(), GamaType.CONTENT);
			final IType keyType = computeType(prototype.keyTypeProvider, type.getKeyType(), GamaType.KEY);
			type = GamaType.from(type, keyType, contentType);
		}
	}

	protected IType computeType(final int t, final IType def, final int kind) {
		switch (t) {
			case WRAPPED:
				return arg(0).getType().getWrappedType();
			case NONE:
				return def;
			case BOTH:
				if (exprs == null)
					return def;
				return GamaType.findCommonType(exprs, kind);
			case FIRST_TYPE:
				if (exprs == null)
					return def;
				return exprs[0].getType();
			case FIRST_CONTENT_TYPE_OR_TYPE:
				final IType leftType = exprs[0].getType();
				final IType t2 = leftType.getContentType();
				if (t2 == Types.NO_TYPE) { return leftType; }
				return t2;
			case SECOND_DENOTED_TYPE:
				if (exprs == null)
					return def;
				return exprs[1].getDenotedType();
			case SECOND_TYPE:
				if (exprs == null)
					return def;
				return exprs[1].getType();
			case FIRST_CONTENT_TYPE:
				if (exprs == null)
					return def;
				return exprs[0].getType().getContentType();
			case FIRST_KEY_TYPE:
				if (exprs == null)
					return def;
				return exprs[0].getType().getKeyType();
			case SECOND_CONTENT_TYPE:
				if (exprs == null)
					return def;
				return exprs[1].getType().getContentType();
			case SECOND_CONTENT_TYPE_OR_TYPE:
				if (exprs == null)
					return def;
				final IType rightType = exprs[1].getType();
				final IType t3 = rightType.getContentType();
				if (t3 == Types.NO_TYPE) { return rightType; }
				return t3;
			case SECOND_KEY_TYPE:
				if (exprs == null)
					return def;
				return exprs[1].getType().getKeyType();
			default:
				return t >= 0 ? Types.get(t) : def;
		}
	}

	protected abstract AbstractNAryOperator copy();

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		final AbstractNAryOperator copy = copy();
		if (exprs != null)
			for (int i = 0; i < exprs.length; i++) {
				copy.exprs[i] = exprs[i].resolveAgainst(scope);
			}
		return copy;
	}

	@Override
	public boolean isConst() {
		if (!prototype.canBeConst) { return false; }
		if (exprs != null)
			for (int i = 0; i < exprs.length; i++) {
				if (!exprs[i].isConst()) { return false; }
			}
		return true;
	}

	@Override
	public String getName() {
		return prototype.getName();
	}

	@Override
	public String toString() {
		String result = literalValue() + "(";
		if (exprs != null) {
			for (int i = 0; i < exprs.length; i++) {
				final String l = exprs[i] == null ? "null" : exprs[i].toString();
				result += l + (i != exprs.length - 1 ? "," : "");
			}
		}
		return result + ")";
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		String result = literalValue() + "(";
		if (exprs != null) {
			for (int i = 0; i < exprs.length; i++) {
				final String l = exprs[i] == null ? "nil" : exprs[i].serialize(includingBuiltIn);
				result += l + (i != exprs.length - 1 ? "," : "");
			}
		}
		return result + ")";
	}

	public boolean hasChildren() {
		return true;
	}

	public int numArg() {
		return exprs == null ? 0 : exprs.length;
	}

	@Override
	public IExpression arg(final int i) {
		if (exprs == null) { return null; }
		if (i >= exprs.length)
			return null;
		return exprs[i];
	}

	@Override
	public String getTitle() {
		final StringBuilder sb = new StringBuilder(50);
		sb.append("operator ").append(getName()).append(" (");
		if (exprs != null) {
			for (final IExpression expr : exprs) {
				sb.append(expr == null ? "nil" : expr.getType().getTitle());
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
		} else if (prototype.signature != null) {
			sb.append("Argument types: " + prototype.signature.toString());
		}
		sb.append(") returns ");
		final IType type = getType();
		sb.append(type.getTitle());
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		return prototype.getDocumentation();
	}

	@Override
	public String getDefiningPlugin() {
		return prototype.getDefiningPlugin();
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		prototype.collectMetaInformation(meta);
		meta.put(GamlProperties.OPERATORS, prototype.getName());
		if (exprs != null)
			for (final IExpression e : exprs) {
				if (e != null) {
					e.collectMetaInformation(meta);
				}
			}
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final ICollector<VariableDescription> result) {
		if (exprs != null) {
			for (final IExpression e : exprs) {
				if (e != null)
					e.collectUsedVarsOf(species, result);
			}
		}
	}

	/**
	 * Method getDocumentationObject()
	 * 
	 * @see msi.gaml.expressions.IOperator#getDocumentationObject()
	 */
	// @Override
	// public GamlElementDocumentation getDocumentationObject() {
	// return prototype.doc;
	// }
}
