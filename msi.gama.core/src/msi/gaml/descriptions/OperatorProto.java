/*******************************************************************************************************
 *
 * OperatorProto.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ICollector;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamaGetter;
import msi.gaml.compilation.IValidator;
import msi.gaml.compilation.annotations.depends_on;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.operators.BinaryOperator;
import msi.gaml.expressions.operators.NAryOperator;
import msi.gaml.expressions.operators.TypeFieldExpression;
import msi.gaml.expressions.operators.UnaryOperator;
import msi.gaml.expressions.types.TypeExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class OperatorProto.
 *
 * @author drogoul
 * @since 7 avr. 2014
 *
 */

/**
 * The Class OperatorProto.
 */
@SuppressWarnings ({ "rawtypes" })
public class OperatorProto extends AbstractProto implements IVarDescriptionUser {

	/** The Constant EMPTY_DEPS. */
	public static final String[] EMPTY_DEPS = {};

	/** The as. */
	public static OperatorProto AS;

	/** The no mandatory parenthesis. */
	public static Set<String> noMandatoryParenthesis = ImmutableSet.copyOf(Arrays.<String> asList("-", "!"));

	/** The binaries. */
	public static Set<String> binaries = ImmutableSet.copyOf(Arrays.<String> asList("=", "+", "-", "/", "*", "^", "<",
			">", "<=", ">=", "?", "!=", ":", ".", "where", "select", "collect", "first_with", "last_with",
			"overlapping", "at_distance", "in", "inside", "among", "contains", "contains_any", "contains_all", "min_of",
			"max_of", "with_max_of", "with_min_of", "of_species", "of_generic_species", "sort_by", "accumulate", "or",
			"and", "at", "is", "group_by", "index_of", "last_index_of", "index_by", "count", "sort", "::", "as_map"));

	/** The iterator. */
	public final boolean isVarOrField, canBeConst, iterator;

	/** The semantic validator. */
	private IValidator semanticValidator = null;

	/** The return type. */
	public final IType returnType;

	/** The helper. */
	private final GamaGetter helper;

	/** The signature. */
	public final Signature signature;

	/** The lazy. */
	public boolean[] lazy;

	/** The key type provider. */
	public final int typeProvider, contentTypeProvider, keyTypeProvider;

	/** The expected content type. */
	public final int[] expectedContentType;

	/** The content type content type provider. */
	public final int contentTypeContentTypeProvider;

	/** The depends on. */
	private String[] depends_on;

	/**
	 * Creates the.
	 *
	 * @param context
	 *            the context
	 * @param currentEObject
	 *            the current E object
	 * @param exprs
	 *            the exprs
	 * @return the i expression
	 */
	public IExpression create(final IDescription context, final EObject currentEObject, final IExpression... exprs) {
		try {
			final boolean semantic = getValidator().validate(context, currentEObject, exprs);
			if (!semantic) return null;
			switch (signature.size()) {
				case 1:
					if (isVarOrField) return new TypeFieldExpression(this, context, exprs[0]);
					return UnaryOperator.create(this, context, exprs[0]);
				case 2:
					if (isVarOrField) {
						if (exprs[1] instanceof BinaryOperator bo && IKeyword.AS.equals(bo.getName())) {
							// Case of experiment.simulation and experiment.simulations (see #3621)
							TypeExpression typeExpr = (TypeExpression) bo.arg(1);
							IVarExpression var = (IVarExpression) bo.arg(0);
							return BinaryOperator.create(AS, context,
									new BinaryOperator.BinaryVarOperator(this, context, exprs[0], var), typeExpr);

						}
						return new BinaryOperator.BinaryVarOperator(this, context, exprs[0], (IVarExpression) exprs[1]);
					}

					return BinaryOperator.create(this, context, exprs);
				default:
					return NAryOperator.create(this, exprs);
			}
		} catch (final GamaRuntimeException e) {
			// this can happen when optimizing the code
			// in that case, report an error and return null as it means that
			// the code is not functional
			if (context != null) {
				context.error("This code is not functional: " + e.getMessage(), IGamlIssue.GENERAL, currentEObject);
			}
			return null;
		} catch (final Exception e) {
			// this can happen when optimizing the code
			// in that case, report an error and return null as it means that
			// the code is not functional
			if (context != null) {
				context.error("The compiler encountered an internal error: " + e.getMessage(), IGamlIssue.GENERAL,
						currentEObject);
			}
			return null;
		}
	}

	/**
	 * Instantiates a new operator proto.
	 *
	 * @param name
	 *            the name
	 * @param method
	 *            the method
	 * @param helper
	 *            the helper
	 * @param canBeConst
	 *            the can be const
	 * @param isVarOrField
	 *            the is var or field
	 * @param returnType
	 *            the return type
	 * @param signature
	 *            the signature
	 * @param typeProvider
	 *            the type provider
	 * @param contentTypeProvider
	 *            the content type provider
	 * @param keyTypeProvider
	 *            the key type provider
	 * @param contentTypeContentTypeProvider
	 *            the content type content type provider
	 * @param expectedContentType
	 *            the expected content type
	 * @param plugin
	 *            the plugin
	 */
	public OperatorProto(final String name, final AnnotatedElement method, final GamaGetter helper,
			final boolean canBeConst, final boolean isVarOrField, final IType returnType, final Signature signature,
			final int typeProvider, final int contentTypeProvider, final int keyTypeProvider,
			final int contentTypeContentTypeProvider, final int[] expectedContentType, final String plugin) {
		super(name, method, plugin);
		iterator = GAML.ITERATORS.contains(name);

		if (IKeyword.AS.equals(name)) { AS = this; }

		this.returnType = returnType;
		this.canBeConst = canBeConst;
		this.isVarOrField = isVarOrField;
		this.helper = helper;
		this.signature = signature;
		this.typeProvider = typeProvider;
		this.contentTypeProvider = contentTypeProvider;
		this.keyTypeProvider = keyTypeProvider;
		this.expectedContentType = expectedContentType;
		this.contentTypeContentTypeProvider = contentTypeContentTypeProvider;
	}

	/**
	 * Compute lazyness.
	 *
	 * @param method
	 *            the method
	 * @return the boolean[]
	 */
	public boolean[] getLazyness() {
		if (lazy == null) {
			lazy = new boolean[signature.size()];
			if (lazy.length == 0) return lazy;
			if (support instanceof Method m) {
				final Class[] classes = m.getParameterTypes();
				if (classes.length == 0) return lazy;
				int begin = 0;
				if (classes[0] == IScope.class) { begin = 1; }
				for (int i = begin; i < classes.length; i++) {
					if (IExpression.class.isAssignableFrom(classes[i])) { lazy[i - begin] = true; }
				}
			}
		}

		return lazy;
	}

	/**
	 * Instantiates a new operator proto.
	 *
	 * @param name
	 *            the name
	 * @param method
	 *            the method
	 * @param helper
	 *            the helper
	 * @param canBeConst
	 *            the can be const
	 * @param isVarOrField
	 *            the is var or field
	 * @param returnType
	 *            the return type
	 * @param signature
	 *            the signature
	 * @param typeProvider
	 *            the type provider
	 * @param contentTypeProvider
	 *            the content type provider
	 * @param keyTypeProvider
	 *            the key type provider
	 * @param expectedContentType
	 *            the expected content type
	 */
	public OperatorProto(final String name, final AnnotatedElement method, final GamaGetter helper,
			final boolean canBeConst, final boolean isVarOrField, /* final int doc, */final int returnType,
			final Class signature, final int typeProvider, final int contentTypeProvider, final int keyTypeProvider,
			final int[] expectedContentType) {
		this(name, method == null ? signature : method, helper, canBeConst, isVarOrField, Types.get(returnType),
				new Signature(signature), typeProvider, contentTypeProvider, keyTypeProvider, ITypeProvider.NONE,
				expectedContentType, GamaBundleLoader.CURRENT_PLUGIN_NAME);
	}

	/**
	 * Instantiates a new operator proto.
	 *
	 * @param op
	 *            the op
	 * @param gamaType
	 *            the gama type
	 */
	private OperatorProto(final OperatorProto op, final IType gamaType) {
		this(op.name, op.support, op.getHelper(), op.canBeConst, op.isVarOrField, op.returnType,
				new Signature(gamaType), op.typeProvider, op.contentTypeProvider, op.keyTypeProvider,
				op.contentTypeContentTypeProvider, op.expectedContentType, op.plugin);
	}

	@Override
	public String getTitle() {
		if (isVarOrField) return "field " + getName() + " of type " + returnType + ", for values of type "
				+ signature.asPattern(false);
		return "operator " + getName() + "(" + signature.asPattern(false) + "), returns " + returnType;
	}

	@Override
	public Doc getDocumentation() {
		if (!isVarOrField) return super.getDocumentation();
		final vars annot = getSupport().getAnnotation(vars.class);
		if (annot != null) {
			final variable[] allVars = annot.value();
			for (final variable v : allVars) {
				if (v.name().equals(getName())) {
					if (v.doc().length > 0) return new ConstantDoc(v.doc()[0].value());
					break;
				}
			}
		}
		return this::getTitle;
	}

	/**
	 * Verify expected types.
	 *
	 * @param context
	 *            the context
	 * @param rightType
	 *            the right type
	 */
	public void verifyExpectedTypes(final IDescription context, final IType<?> rightType) {
		if (expectedContentType == null || expectedContentType.length == 0 || context == null) return;
		if (expectedContentType.length == 1 && iterator) {
			final IType<?> expected = Types.get(expectedContentType[0]);
			if (!rightType.isTranslatableInto(expected)) {
				context.warning("Operator " + getName() + " expects an argument of type " + expected,
						IGamlIssue.SHOULD_CAST);
			}
		} else if (signature.isUnary()) {
			for (final int element : expectedContentType) {
				if (rightType.isTranslatableInto(Types.get(element))) return;
			}
			context.error("Operator " + getName() + " expects arguments of type " + rightType, IGamlIssue.WRONG_TYPE);
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName() + "(" + signature.toString() + ")";
	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public String getCategory() {
		if (support == null) return "Other";
		final operator op = support.getAnnotation(operator.class);
		if (op == null) return "Other";
		final String[] strings = op.category();
		if (strings.length > 0) return op.category()[0];
		return "Other";
	}

	/**
	 * Method getKind()
	 *
	 * @see msi.gaml.descriptions.AbstractProto#getKind()
	 */
	@Override
	public int getKind() { return ISymbolKind.OPERATOR; }

	/**
	 * @return
	 */
	public String getPattern(final boolean withVariables) {
		final int size = signature.size();
		final String aName = getName();
		if (size == 1 || size > 2) {
			if (noMandatoryParenthesis.contains(aName)) return aName + signature.asPattern(withVariables);
			return aName + "(" + signature.asPattern(withVariables) + ")";
		}
		if (binaries.contains(aName))
			return signature.get(0).asPattern() + " " + aName + " " + signature.get(1).asPattern();
		return aName + "(" + signature.asPattern(withVariables) + ")";
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		super.collectMetaInformation(meta);
		meta.put(GamlProperties.OPERATORS, name);
	}

	/**
	 * Copy with signature.
	 *
	 * @param gamaType
	 *            the gama type
	 * @return the operator proto
	 */
	public OperatorProto copyWithSignature(final IType gamaType) {
		return new OperatorProto(this, gamaType);
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) return;
		alreadyProcessed.add(this);
		for (final String s : getDependencies()) {
			if (species.hasAttribute(s)) { result.add(species.getAttribute(s)); }
		}
	}

	@Override
	public doc getDocAnnotation() {
		doc d = super.getDocAnnotation();
		if (d != null) return d;
		if (support != null && support.isAnnotationPresent(operator.class)) {
			final operator op = support.getAnnotation(operator.class);
			final doc[] docs = op.doc();
			if (docs != null && docs.length > 0) { d = docs[0]; }
		}
		return d;
	}

	/**
	 * Gets the helper.
	 *
	 * @return the helper
	 */
	public GamaGetter getHelper() { return helper; }

	/**
	 * Gets the validator.
	 *
	 * @return the validator
	 */
	public IValidator getValidator() {
		if (semanticValidator == null && support != null) {
			final validator val = support.getAnnotation(validator.class);
			try {
				semanticValidator = val != null ? val.value().getConstructor().newInstance() : IValidator.NULL;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				DEBUG.ERR("Error in creating the validator for operator " + name + " on method " + support);
			}
		}
		return semanticValidator;
	}

	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	public String[] getDependencies() {
		if (depends_on == null && support != null) {
			final depends_on d = support.getAnnotation(depends_on.class);
			depends_on = d != null ? d.value() : EMPTY_DEPS;
		}
		return depends_on;
	}

}
