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

import java.lang.reflect.*;
import java.util.*;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
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
public class OperatorProto extends AbstractProto {

	public static Set<String> noMandatoryParenthesis = new THashSet(Arrays.asList("-", "!"));
	public static Set<String> binaries = new THashSet(Arrays.asList("=", "+", "-", "/", "*", "^", "<", ">", "<=", ">=",
		"?", "!=", ":", ".", "where", "select", "collect", "first_with", "last_with", "overlapping", "at_distance",
		"in", "inside", "among", "contains", "contains_any", "contains_all", "min_of", "max_of", "with_max_of",
		"with_min_of", "of_species", "of_generic_species", "sort_by", "accumulate", "or", "and", "at", "is", "group_by",
		"index_of", "last_index_of", "index_by", "count", "sort", "::", "as_map"));

	public final boolean isVarOrField, canBeConst;
	public final IType returnType;
	public final GamaHelper helper;
	public Signature signature;
	public final boolean[] lazy;
	public final int typeProvider, contentTypeProvider, keyTypeProvider;
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
			context.error("This code is not functional: " + e.getMessage(), IGamlIssue.GENERAL, getName());
			return null;
		}
	}

	public OperatorProto(final String name, final AnnotatedElement method, final GamaHelper helper,
		final boolean canBeConst, final boolean isVarOrField, /* final int doc, */final IType returnType,
		final Signature signature, final boolean lazy, final int typeProvider, final int contentTypeProvider,
		final int keyTypeProvider, final int[] expectedContentType, final String plugin) {
		super(name, method, plugin);
		this.returnType = returnType;
		this.canBeConst = canBeConst;
		this.isVarOrField = isVarOrField;
		this.helper = helper;
		this.signature = signature;
		this.lazy = computeLazyness(method);
		this.typeProvider = typeProvider;
		this.contentTypeProvider = contentTypeProvider;
		this.keyTypeProvider = keyTypeProvider;
		this.expectedContentType = expectedContentType;
	}

	private boolean[] computeLazyness(final AnnotatedElement method) {
		boolean[] result = new boolean[signature.size()];
		if ( result.length == 0 ) { return result; }
		if ( method instanceof Method ) {
			Method m = (Method) method;
			Class[] classes = m.getParameterTypes();
			int begin = 0;
			if ( classes[0] == IScope.class ) {
				begin = 1;
			}
			for ( int i = begin; i < classes.length; i++ ) {
				if ( IExpression.class.isAssignableFrom(classes[i]) ) {
					result[i - begin] = true;
				}
			}
		}
		return result;
	}

	public OperatorProto(final String name, final AnnotatedElement method, final GamaHelper helper,
		final boolean canBeConst, final boolean isVarOrField, /* final int doc, */final int returnType,
		final Class signature, final boolean lazy, final int typeProvider, final int contentTypeProvider,
		final int keyTypeProvider, final int[] expectedContentType) {
		this(name, method == null ? signature : method, helper, canBeConst, isVarOrField,
			/* doc, */Types.get(returnType), new Signature(signature), lazy, typeProvider, contentTypeProvider,
			keyTypeProvider, expectedContentType, GamaBundleLoader.CURRENT_PLUGIN_NAME);
	}

	public void setSignature(final IType ... t) {
		signature = new Signature(t);
	}

	public void verifyExpectedTypes(final IDescription context, final IType rightType) {
		if ( expectedContentType == null || expectedContentType.length == 0 ) { return; }
		if ( context == null ) { return; }
		if ( expectedContentType.length == 1 && IExpressionCompiler.ITERATORS.contains(getName()) ) {
			IType expected = Types.get(expectedContentType[0]);
			if ( !rightType.isTranslatableInto(expected) ) {
				context.warning("Operator " + getName() + " expects an argument of type " + expected,
					IGamlIssue.SHOULD_CAST);
				return;
			}
		} else if ( signature.isUnary() ) {
			for ( int i = 0; i < expectedContentType.length; i++ ) {
				if ( rightType.isTranslatableInto(Types.get(expectedContentType[i])) ) { return; }
			}
			context.error("Operator " + getName() + " expects arguments of type " + rightType, IGamlIssue.WRONG_TYPE);
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName() + "(" + signature.toString() + ")";
	}

	public String getCategory() {
		if ( support == null ) { return "Other"; }
		operator op = support.getAnnotation(operator.class);
		if ( op == null ) // Happens sometimes for synthetic operators
		{
			return "Other";
		} else {
			String[] strings = op.category();
			if ( strings.length > 0 ) {
				return op.category()[0];
			} else {
				return "Other";
			}
		}
	}

	/**
	 * Method getKind()
	 * @see msi.gaml.descriptions.AbstractProto#getKind()
	 */
	@Override
	public int getKind() {
		return ISymbolKind.OPERATOR;
	}

	/**
	 * @return
	 */
	public String getPattern(final boolean withVariables) {
		int size = signature.size();
		String name = getName();
		if ( size == 1 || size > 2 ) {
			if ( noMandatoryParenthesis.contains(name) ) {
				return name + signature.asPattern(withVariables);
			} else {
				return name + "(" + signature.asPattern(withVariables) + ")";
			}
		} else { // size == 2
			if ( binaries.contains(name) ) {
				return signature.get(0).asPattern() + " " + name + " " + signature.get(1).asPattern();
			} else {
				return name + "(" + signature.asPattern(withVariables) + ")";
			}
		}
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		super.collectMetaInformation(meta);
		meta.put(GamlProperties.OPERATORS, name);
	}

}
