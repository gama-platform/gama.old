/*********************************************************************************************
 *
 * 'OperatorProto.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaGetter;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.expressions.BinaryOperator;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.NAryOperator;
import msi.gaml.expressions.TypeFieldExpression;
import msi.gaml.expressions.UnaryOperator;
import msi.gaml.types.IType;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;

/**
 * Class OperatorProto.
 *
 * @author drogoul
 * @since 7 avr. 2014
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class OperatorProto extends AbstractProto {

	public static OperatorProto AS;
	public static Set<String> noMandatoryParenthesis = ImmutableSet.copyOf(Arrays.<String> asList("-", "!"));
	public static Set<String> binaries = ImmutableSet.copyOf(Arrays.<String> asList("=", "+", "-", "/", "*", "^", "<",
			">", "<=", ">=", "?", "!=", ":", ".", "where", "select", "collect", "first_with", "last_with",
			"overlapping", "at_distance", "in", "inside", "among", "contains", "contains_any", "contains_all", "min_of",
			"max_of", "with_max_of", "with_min_of", "of_species", "of_generic_species", "sort_by", "accumulate", "or",
			"and", "at", "is", "group_by", "index_of", "last_index_of", "index_by", "count", "sort", "::", "as_map"));

	public final boolean isVarOrField, canBeConst, iterator;
	public final IType returnType;
	public final GamaGetter helper;
	public Signature signature;
	public final boolean[] lazy;
	public final int typeProvider, contentTypeProvider, keyTypeProvider;
	public final int[] expectedContentType;
	public final int contentTypeContentTypeProvider;

	public IExpression create(final IDescription context, final EObject currentEObject, final IExpression... exprs) {
		try {

			switch (signature.size()) {
				case 1:
					if (isVarOrField) { return new TypeFieldExpression(this, context, exprs); }
					return UnaryOperator.create(this, context, exprs);
				case 2:
					if (isVarOrField) {
						if (!(exprs[1] instanceof IVarExpression)) {
							if (context != null) {
								context.error("Attribute " + exprs[1].literalValue() + " unknown for "
										+ exprs[0].getGamlType() + " instances");
							}
							return null;
						}
						return new BinaryOperator.BinaryVarOperator(this, context, exprs);
					}
					return BinaryOperator.create(this, context, exprs);
				default:
					return NAryOperator.create(this, exprs);
			}
		} catch (final GamaRuntimeException e) {
			// this can happen when optimizing the code
			// in that case, report an error and return null as it means that
			// the code is not functional
			context.error("This code is not functional: " + e.getMessage(), IGamlIssue.GENERAL, currentEObject);
			return null;
		} catch (final Exception e) {
			// this can happen when optimizing the code
			// in that case, report an error and return null as it means that
			// the code is not functional
			context.error("The compiler encountered an internal error: " + e.getMessage(), IGamlIssue.GENERAL,
					currentEObject);
			return null;
		}
	}

	public OperatorProto(final String name, final AnnotatedElement method, final GamaGetter helper,
			final boolean canBeConst, final boolean isVarOrField, /* final int doc, */final IType returnType,
			final Signature signature, final boolean lazy, final int typeProvider, final int contentTypeProvider,
			final int keyTypeProvider, final int contentTypeContentTypeProvider, final int[] expectedContentType,
			final String plugin) {
		super(name, method, plugin);
		if (name.equals(IKeyword.AS)) {
			AS = this;
		}
		this.iterator = IExpressionCompiler.ITERATORS.contains(name);
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
		this.contentTypeContentTypeProvider = contentTypeContentTypeProvider;
	}

	private boolean[] computeLazyness(final AnnotatedElement method) {
		final boolean[] result = new boolean[signature.size()];
		if (result.length == 0) { return result; }
		if (method instanceof Method) {
			final Method m = (Method) method;
			final Class[] classes = m.getParameterTypes();
			int begin = 0;
			if (classes[0] == IScope.class) {
				begin = 1;
			}
			for (int i = begin; i < classes.length; i++) {
				if (IExpression.class.isAssignableFrom(classes[i])) {
					result[i - begin] = true;
				}
			}
		}
		return result;
	}

	public OperatorProto(final String name, final AnnotatedElement method, final GamaGetter helper,
			final boolean canBeConst, final boolean isVarOrField, /* final int doc, */final int returnType,
			final Class signature, final boolean lazy, final int typeProvider, final int contentTypeProvider,
			final int keyTypeProvider, final int[] expectedContentType) {
		this(name, method == null ? signature : method, helper, canBeConst, isVarOrField,
				/* doc, */Types.get(returnType), new Signature(signature), lazy, typeProvider, contentTypeProvider,
				keyTypeProvider, ITypeProvider.NONE, expectedContentType, GamaBundleLoader.CURRENT_PLUGIN_NAME);
	}

	public OperatorProto(final OperatorProto op, final IType gamaType) {
		this(op.name, op.support, op.helper, op.canBeConst, op.isVarOrField, op.returnType, new Signature(gamaType),
				true, op.typeProvider, op.contentTypeProvider, op.keyTypeProvider, op.contentTypeContentTypeProvider,
				op.expectedContentType, op.plugin);
	}

	public void setSignature(final IType... t) {
		signature = new Signature(t);
	}

	@Override
	public String getTitle() {
		if (isVarOrField) { return "field " + getName() + " of type " + returnType + ", for values of type "
				+ signature.asPattern(false); }
		return "operator " + getName() + "(" + signature.asPattern(false) + "), returns " + returnType;
	}

	@Override
	public String getDocumentation() {
		if (!isVarOrField) { return super.getDocumentation(); }
		final vars annot = getSupport().getAnnotation(vars.class);
		if (annot != null) {
			final variable[] allVars = annot.value();
			for (final variable v : allVars) {
				if (v.name().equals(getName())) {
					if (v.doc().length > 0) { return v.doc()[0].value(); }
					break;
				}
			}
		}
		return getTitle();
	}

	public void verifyExpectedTypes(final IDescription context, final IType<?> rightType) {
		if (expectedContentType == null || expectedContentType.length == 0) { return; }
		if (context == null) { return; }
		if (expectedContentType.length == 1 && IExpressionCompiler.ITERATORS.contains(getName())) {
			final IType<?> expected = Types.get(expectedContentType[0]);
			if (!rightType.isTranslatableInto(expected)) {
				context.warning("Operator " + getName() + " expects an argument of type " + expected,
						IGamlIssue.SHOULD_CAST);
				return;
			}
		} else if (signature.isUnary()) {
			for (final int element : expectedContentType) {
				if (rightType.isTranslatableInto(Types.get(element))) { return; }
			}
			context.error("Operator " + getName() + " expects arguments of type " + rightType, IGamlIssue.WRONG_TYPE);
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName() + "(" + signature.toString() + ")";
	}

	public String getCategory() {
		if (support == null) { return "Other"; }
		final operator op = support.getAnnotation(operator.class);
		if (op == null) // Happens sometimes for synthetic operators
		{
			return "Other";
		} else {
			final String[] strings = op.category();
			if (strings.length > 0) {
				return op.category()[0];
			} else {
				return "Other";
			}
		}
	}

	/**
	 * Method getKind()
	 * 
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
		final int size = signature.size();
		final String name = getName();
		if (size == 1 || size > 2) {
			if (noMandatoryParenthesis.contains(name)) {
				return name + signature.asPattern(withVariables);
			} else {
				return name + "(" + signature.asPattern(withVariables) + ")";
			}
		} else { // size == 2
			if (binaries.contains(name)) {
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

	public OperatorProto copyWithSignature(final IType gamaType) {
		return new OperatorProto(this, gamaType);
	}

}
