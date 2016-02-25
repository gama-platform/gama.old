/*********************************************************************************************
 *
 *
 * 'AbstractNAryOperator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.*;
import java.util.Arrays;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.types.*;

/**
 * AbstractBinaryOperator
 * @author drogoul 23 august 07
 */
public abstract class AbstractNAryOperator extends AbstractExpression implements IOperator {

	protected final IExpression[] exprs;
	protected OperatorProto prototype;

	public AbstractNAryOperator(final OperatorProto proto, final IExpression ... expressions) {
		// Copy introduced in order to circumvent issue 1060
		exprs = Arrays.copyOf(expressions, expressions.length);
		// this.exprs = expressions;
		this.prototype = proto;
		if ( prototype != null ) {
			type = prototype.returnType;
			computeType();
		} else {
			type = Types.NO_TYPE;
		}
	}

	@Override
	public OperatorProto getPrototype() {
		return prototype;
	}

	protected void computeType() {
		type = computeType(prototype.typeProvider, type, GamaType.TYPE);
		if ( type.isContainer() ) {
			IType contentType = computeType(prototype.contentTypeProvider, type.getContentType(), GamaType.CONTENT);
			IType keyType = computeType(prototype.keyTypeProvider, type.getKeyType(), GamaType.KEY);
			type = GamaType.from(type, keyType, contentType);
		}
	}

	protected IType computeType(final int t, final IType def, final int kind) {
		switch (t) {
			case NONE:
				return def;
			case BOTH:
				return GamaType.findCommonType(exprs, kind);
			case FIRST_TYPE:
				return exprs[0].getType();
			case FIRST_CONTENT_TYPE_OR_TYPE:
				IType leftType = exprs[0].getType();
				final IType t2 = leftType.getContentType();
				if ( t2 == Types.NO_TYPE ) { return leftType; }
				return t2;
			case SECOND_TYPE:
				return exprs[1].getType();
			case FIRST_CONTENT_TYPE:
				return exprs[0].getType().getContentType();
			case FIRST_KEY_TYPE:
				return exprs[0].getType().getKeyType();
			case SECOND_CONTENT_TYPE:
				return exprs[1].getType().getContentType();
			case SECOND_CONTENT_TYPE_OR_TYPE:
				final IType rightType = exprs[1].getType();
				final IType t3 = rightType.getContentType();
				if ( t3 == Types.NO_TYPE ) { return rightType; }
				return t3;
			case SECOND_KEY_TYPE:
				return exprs[1].getType().getKeyType();
			default:
				return t >= 0 ? Types.get(t) : def;
		}
	}

	protected abstract AbstractNAryOperator copy();

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		final AbstractNAryOperator copy = copy();
		for ( int i = 0; i < exprs.length; i++ ) {
			copy.exprs[i] = exprs[i].resolveAgainst(scope);
		}
		return copy;
	}

	@Override
	public boolean isConst() {
		if ( !prototype.canBeConst ) { return false; }
		for ( int i = 0; i < exprs.length; i++ ) {
			if ( !exprs[i].isConst() ) { return false; }
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
		if ( exprs != null ) {
			for ( int i = 0; i < exprs.length; i++ ) {
				String l = exprs[i] == null ? "null" : exprs[i].toString();
				result += l + (i != exprs.length - 1 ? "," : "");
			}
		}
		return result + ")";
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		String result = literalValue() + "(";
		if ( exprs != null ) {
			for ( int i = 0; i < exprs.length; i++ ) {
				String l = exprs[i] == null ? "nil" : exprs[i].serialize(includingBuiltIn);
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
		if ( exprs == null ) { return null; }
		return exprs[i];
	}

	@Override
	public String getTitle() {
		StringBuilder sb = new StringBuilder(50);
		sb.append("operator ").append(getName()).append(" (");
		if ( exprs != null ) {
			for ( int i = 0; i < exprs.length; i++ ) {
				sb.append(exprs[i] == null ? "nil" : exprs[i].getType().getTitle());
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
		} else if ( prototype.signature != null ) {
			sb.append("Argument types: " + prototype.signature.toString());
		}
		sb.append(") returns ");
		IType type = getType();
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
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		prototype.collectMetaInformation(meta);
		meta.put(GamlProperties.OPERATORS, name);
		for ( IExpression e : exprs ) {
			if ( e != null ) {
				e.collectMetaInformation(meta);
			}
		}
	}

	/**
	 * Method getDocumentationObject()
	 * @see msi.gaml.expressions.IOperator#getDocumentationObject()
	 */
	// @Override
	// public GamlElementDocumentation getDocumentationObject() {
	// return prototype.doc;
	// }
}
