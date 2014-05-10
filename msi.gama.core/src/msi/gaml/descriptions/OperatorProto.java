/*********************************************************************************************
 * 
 * 
 * 'OperatorProto.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.descriptions;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * Class OperatorProto.
 * 
 * @author drogoul
 * @since 7 avr. 2014
 * 
 */
public class OperatorProto implements IGamlDescription {

	public final boolean isVarOrField;
	public final boolean canBeConst;
	public final IType returnType;
	public final GamaHelper helper;
	public final String name;
	public final int doc;
	public Signature signature;
	public final boolean lazy;
	public final int typeProvider;
	public final int contentTypeProvider;
	public final int keyTypeProvider;
	public final int[] expectedContentType;

	public IExpression create(final IDescription context, final IExpression ... exprs) {
		try {
			switch (signature.size()) {
				case 1:
					if ( isVarOrField ) { return new TypeFieldExpression(this, context, exprs); }
					return UnaryOperator.create(this, context, exprs);
				case 2:
					if ( isVarOrField ) {
						if ( !(exprs[1] instanceof IVarExpression) ) {
							context.error("Attribute " + exprs[1].literalValue() + " unknown for " +
								exprs[0].getType() + " instances");
							return null;
						}
						return new BinaryOperator.BinaryVarOperator(this, context, exprs);
					}
					return BinaryOperator.create(this, context, exprs);
				default:
					return NAryOperator.create(this, exprs);
			}
		} catch (GamaRuntimeException e) {
			// this can happen when optimizing the code
			// in that case, report an error and return null as it means that the code is not functional
			context.error("This code is not functional: " + e.getMessage(), IGamlIssue.GENERAL, name);
			return null;
		}
	}

	public OperatorProto(final String name, final GamaHelper helper, final boolean canBeConst,
		final boolean isVarOrField, final int doc, final IType returnType, final Signature signature,
		final boolean lazy, final int typeProvider, final int contentTypeProvider, final int keyTypeProvider,
		final int[] expectedContentType) {
		super();
		this.returnType = returnType;
		this.canBeConst = canBeConst;
		this.isVarOrField = isVarOrField;
		this.helper = helper;
		this.name = name;
		this.doc = doc;
		this.signature = signature;
		this.lazy = lazy;
		this.typeProvider = typeProvider;
		this.contentTypeProvider = contentTypeProvider;
		this.keyTypeProvider = keyTypeProvider;
		this.expectedContentType = expectedContentType;
	}

	public OperatorProto(final String name, final GamaHelper helper, final boolean canBeConst,
		final boolean isVarOrField, final int doc, final int returnType, final Class signature, final boolean lazy,
		final int typeProvider, final int contentTypeProvider, final int keyTypeProvider,
		final int[] expectedContentType) {
		this(name, helper, canBeConst, isVarOrField, doc, Types.get(returnType), new Signature(signature), lazy,
			typeProvider, contentTypeProvider, keyTypeProvider, expectedContentType);
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		// TODO insert here a @documentation if possible

		String s = AbstractGamlDocumentation.getMain(doc);
		if ( s != null ) {
			sb.append(s);
		}
		s = AbstractGamlDocumentation.getDeprecated(doc);
		if ( s != null ) {
			sb.append("\n\n<b>Deprecated</b>: ");
			sb.append("<i>");
			sb.append(s);
			sb.append("</i>");
		}

		return sb.toString();
	}

	public void setSignature(final IType ... t) {
		signature = new Signature(t);
	}

	public void verifyExpectedTypes(final IDescription context, final IType rightType) {
		if ( expectedContentType == null || expectedContentType.length == 0 ) { return; }
		if ( context == null ) { return; }
		if ( expectedContentType.length == 1 && IExpressionCompiler.ITERATORS.contains(name) ) {
			IType expected = Types.get(expectedContentType[0]);
			if ( !rightType.isTranslatableInto(expected) ) {
				context
					.warning("Operator " + name + " expects an argument of type " + expected, IGamlIssue.SHOULD_CAST);
				return;
			}
		} else if ( signature.isUnary() ) {
			for ( int i = 0; i < expectedContentType.length; i++ ) {
				if ( rightType.isTranslatableInto(Types.get(expectedContentType[i])) ) { return; }
			}
			context.error("Operator " + name + " expects arguments of type " + rightType, IGamlIssue.WRONG_TYPE);
		}
	}

	/**
	 * Method getTitle()
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO what to return ???
		return "";
	}

	/**
	 * Method getName()
	 * @see msi.gaml.descriptions.IGamlDescription#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

}
