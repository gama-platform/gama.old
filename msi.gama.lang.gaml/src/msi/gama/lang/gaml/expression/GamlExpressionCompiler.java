/*******************************************************************************************************
 *
 * GamlExpressionCompiler.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.expression;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static msi.gama.common.interfaces.IKeyword.AS;
import static msi.gama.common.interfaces.IKeyword.EACH;
import static msi.gama.common.interfaces.IKeyword.EXPERIMENT;
import static msi.gama.common.interfaces.IKeyword.IS;
import static msi.gama.common.interfaces.IKeyword.IS_SKILL;
import static msi.gama.common.interfaces.IKeyword.MY;
import static msi.gama.common.interfaces.IKeyword.MYSELF;
import static msi.gama.common.interfaces.IKeyword.NULL;
import static msi.gama.common.interfaces.IKeyword.OF;
import static msi.gama.common.interfaces.IKeyword.POINT;
import static msi.gama.common.interfaces.IKeyword.SELF;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword.SUPER;
import static msi.gama.common.interfaces.IKeyword.TRUE;
import static msi.gama.common.interfaces.IKeyword.UNKNOWN;
import static msi.gama.common.interfaces.IKeyword._DOT;
import static msi.gaml.expressions.IExpressionFactory.FALSE_EXPR;
import static msi.gaml.expressions.IExpressionFactory.NIL_EXPR;
import static msi.gaml.expressions.IExpressionFactory.TRUE_EXPR;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.BinaryOperator;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.EquationRef;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.If;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Point;
import msi.gama.lang.gaml.gaml.ReservedLiteral;
import msi.gama.lang.gaml.gaml.SkillFakeDefinition;
import msi.gama.lang.gaml.gaml.SkillRef;
import msi.gama.lang.gaml.gaml.StringEvaluator;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TypeDefinition;
import msi.gama.lang.gaml.gaml.TypeInfo;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.Unary;
import msi.gama.lang.gaml.gaml.Unit;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VarDefinition;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.outputs.layers.KeyboardEventLayerDelegate;
import msi.gama.outputs.layers.MouseEventLayerDelegate;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.ast.SyntacticFactory;
import msi.gaml.compilation.ast.SyntacticModelElement;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.IVarDescriptionProvider;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.PlatformSpeciesDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.StringBasedExpressionDescription;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.descriptions.ValidationContext;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.operators.TypeFieldExpression;
import msi.gaml.expressions.types.DenotedActionExpression;
import msi.gaml.expressions.units.UnitConstantExpression;
import msi.gaml.expressions.variables.EachExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.interfaces.IGamlIssue;
import msi.gaml.statements.Arguments;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.ITypesManager;
import msi.gaml.types.ParametricType;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;

/**
 * The Class GamlExpressionCompiler. Transforms Strings or XText Expressions into GAML IExpressions. Normally invoked by
 * an IExpressionFactory (the default being GAML.getExpressionFactory())
 */

/**
 * The Class GamlExpressionCompiler.
 */

/**
 * The Class GamlExpressionCompiler.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 janv. 2024
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlExpressionCompiler extends GamlSwitch<IExpression> implements IExpressionCompiler<Expression> {

	/** The iterator contexts. */
	private final Deque<IVarExpression> iteratorContexts = new LinkedList();

	/** The current expression description. */
	// To disable reentrant parsing (Issue 782)
	private IExpressionDescription currentExpressionDescription;

	/** The current types manager. */
	private ITypesManager currentTypesManager;

	/** The Constant constantSyntheticExpressions. */
	private final static Map<String, IExpression> constantSyntheticExpressions = GamaMapFactory.createUnordered();

	/** The Constant builder. */
	private static final ExpressionDescriptionBuilder builder = new ExpressionDescriptionBuilder();

	/** The current context. */
	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context of the current
	 * simulation is returned (via simulation.getModel().getDescription()) if it is available. Otherwise, only simple
	 * expressions (that contain constants) can be parsed.
	 */
	private IDescription currentContext;

	static {
		GAML.OPERATORS.put(MY, GamaMapFactory.createUnordered());
	}

	@Override
	public IExpression compile(final IExpressionDescription s, final IDescription parsingContext) {
		// Cf. Issue 782. Returns the expression if an expression needs its
		// compiled version to be compiled.

		if (s.isConst() || s == getCurrentExpressionDescription()) return s.getExpression();
		setCurrentExpressionDescription(s);
		final EObject o = s.getTarget();
		if (o == null && s instanceof StringBasedExpressionDescription) {
			final IExecutionContext context =
					GAMA.getExperiment() == null ? null : GAMA.getRuntimeScope().getExecutionContext();
			return compile(s.toString(), parsingContext, context);
		}
		final IDescription previous = setContext(parsingContext);
		try {
			return compile(o);
		} finally {
			setContext(previous);
			setCurrentExpressionDescription(null);
		}

	}

	@Override
	public IExpression compile(final String expression, final IDescription parsingContext,
			final IExecutionContext tempContext) {
		final IDescription previous = setContext(parsingContext);
		try {

			IExpression result = constantSyntheticExpressions.get(expression);
			if (result != null) return result;
			final EObject o = getEObjectOf(expression, tempContext);
			result = compile(o);
			if (result != null && result.isContextIndependant()) {
				constantSyntheticExpressions.put(expression, result);
			}
			return result;
		} finally {
			setContext(previous);
			setCurrentExpressionDescription(null);
		}
	}

	/**
	 * Compile.
	 *
	 * @param s
	 *            the s
	 * @return the i expression
	 */
	private IExpression compile(final EObject s) {
		// No error, since the null expressions come from previous (more focused) errors and not from the parser itself.
		if (s == null) return null;
		final IExpression expr = doSwitch(s.eClass().getClassifierID(), s);
		if (expr != null && getContext() != null) { getContext().document(s, expr); }
		return expr;
	}

	/**
	 * Skill.
	 *
	 * @param name
	 *            the name
	 * @return the i expression
	 */
	private IExpression skill(final String name) {
		return getFactory().createConst(name, Types.SKILL);
	}

	/**
	 * Unary.
	 *
	 * @param op
	 *            the op
	 * @param e
	 *            the e
	 * @return the i expression
	 */
	// KEEP
	private IExpression unary(final String op, final Expression e) {
		if (op == null) return null;
		final IExpression expr = compile(e);
		if (expr == null) return null;
		if (MY.equals(op)) {
			final IVarDescriptionProvider desc = getContext().getDescriptionDeclaringVar(MYSELF);
			if (desc instanceof IDescription) {
				// We are in a remote context, so 'my' refers to the calling
				// agent
				final IExpression myself = desc.getVarExpr(MYSELF, false);
				final IDescription species = myself.getGamlType().getSpecies();
				final IExpression var = species.getVarExpr(EGaml.getInstance().getKeyOf(e), true);
				return getFactory().createOperator(_DOT, (IDescription) desc, e, myself, var);
			}
			// Otherwise, we ignore 'my' since it refers to 'self'
			return expr;
		}
		// The unary "unit" operator should let the value of its child pass
		// through
		if ("#".equals(op)) return expr;
		if (isSpeciesName(op)) return getFactory().createAs(getContext(), expr, getSpeciesContext(op).getSpeciesExpr());
		// if ( isSkillName(op) ) { return factory.createOperator(AS, context,
		// e, expr, skill(op)); }
		OperatorProto proto = expr.getGamlType().getGetter(op);
		if (proto != null) {
			// It can only be a field as 'actions' are not defined on simple objects
			final TypeFieldExpression fieldExpr = (TypeFieldExpression) proto.create(getContext(), e, expr);
			if (getContext() != null) { getContext().document(e, expr); }
			return fieldExpr;
		}
		return getFactory().createOperator(op, getContext(), e, expr);
	}

	/**
	 * Casting.
	 *
	 * @param type
	 *            the type
	 * @param toCast
	 *            the to cast
	 * @param typeObject
	 *            the type object
	 * @return the i expression
	 */
	private IExpression casting(final IType type, final IExpression toCast, final Expression typeObject) {
		if (toCast == null) return null;
		final IType castingType = type.typeIfCasting(toCast);

		// final boolean isSuperType = castingType.isAssignableFrom(toCast.getGamlType());
		TypeInfo typeInfo = null;
		if (typeObject instanceof TypeRef) {
			typeInfo = ((TypeRef) typeObject).getParameter();
		} else if (typeObject instanceof Function) { typeInfo = ((Function) typeObject).getType(); }
		// if (isSuperType && typeInfo == null) {
		// getContext().info("Unneeded casting: '" + toCast.serializeToGaml(true) + "' is already of type " + type,
		// IGamlIssue.UNUSED, typeObject);
		// Issue #2521: indicate but don't skip the casting
		// return toCast;
		// }
		IType keyType = castingType.getKeyType();
		IType contentsType = castingType.getContentType();
		if (typeInfo != null) {
			IType kt = fromTypeRef((TypeRef) typeInfo.getFirst());
			IType ct = fromTypeRef((TypeRef) typeInfo.getSecond());
			if (ct == null || ct == Types.NO_TYPE) {
				ct = kt;
				kt = null;
			}
			if (ct != null && ct != Types.NO_TYPE) { contentsType = ct; }
			if (kt != null && kt != Types.NO_TYPE) { keyType = kt; }
		}
		final IType result = GamaType.from(castingType, keyType, contentsType);
		// If there is no casting to do, just return the expression unchanged.
		// if (result.isAssignableFrom(toCast.getGamlType())) {
		// getContext().info("Unneeded casting: '" + toCast.serializeToGaml(true) + "' is already of type " + type,
		// IGamlIssue.UNUSED, typeObject);
		// Issue #2521: indicate but don't skip the casting
		// return toCast;
		// }

		return getFactory().createAs(getContext().getSpeciesContext(), toCast,
				getFactory().createTypeExpression(result));
	}

	/**
	 * From type ref.
	 *
	 * @param object
	 *            the object
	 * @return the i type
	 */
	IType fromTypeRef(final TypeRef object) {
		if (object == null) return null;
		String primary = EGaml.getInstance().getKeyOf(object);
		if (primary == null) {
			primary = object.getRef().getName();
		} else if (SyntacticFactory.SPECIES_VAR.equals(primary)) { primary = SPECIES; }

		final IType t = currentTypesManager.get(primary);

		if (t == Types.NO_TYPE && !UNKNOWN.equals(primary)) {
			getContext().error(primary + " is not a valid type name", IGamlIssue.NOT_A_TYPE, object, primary);
			return t;
		}

		// case of model_alias<species>
		if (t.isAgentType() && t.getSpecies().isModel()) {
			final TypeInfo parameter = object.getParameter();
			if (parameter == null) return t;
			final TypeRef first = (TypeRef) parameter.getFirst();
			if (first == null) return t;
			final ITypesManager savedTypesManager = currentTypesManager;
			try {
				currentTypesManager = t.getSpecies().getModelDescription().getTypesManager();
				return fromTypeRef(first);
			} finally {
				currentTypesManager = savedTypesManager;
			}
		}

		if (t.isAgentType()) return t;

		// /

		final TypeInfo parameter = object.getParameter();
		if (parameter == null) return t;
		final int numberOfParameter = t.getNumberOfParameters();
		if (numberOfParameter == 0) {
			// Emit a warning (see #2875)
			getContext().warning(t + " is not a parametric type. Type parameters will be ignored",
					IGamlIssue.WRONG_TYPE, object);
			// We return the type anyway.
			return t;
		}

		final TypeRef first = (TypeRef) parameter.getFirst();
		if (first == null) return t;
		final TypeRef second = (TypeRef) parameter.getSecond();
		if (second == null) {
			if (numberOfParameter == 2) {
				// Emit a warning (see #2875)
				getContext().warning(t + " expects two type parameters", IGamlIssue.WRONG_TYPE, object);
				// We return it anyway with a default key
			}
			return GamaType.from(t, t.getKeyType(), fromTypeRef(first));
		}
		if (numberOfParameter == 1) {
			// Emit an error (see #2875)
			getContext().error(t + " expects only one type parameter", IGamlIssue.WRONG_TYPE, object);
			// We return null
			return null;
		}
		return GamaType.from(t, fromTypeRef(first), fromTypeRef(second));
	}

	/**
	 * Binary.
	 *
	 * @param op
	 *            the op
	 * @param left
	 *            the left
	 * @param originalExpression
	 *            the original expression
	 * @return the i expression
	 */
	private IExpression binary(final String op, final IExpression left, final Expression originalExpression) {
		if (left == null) return null;
		if (!GAML.OPERATORS.containsKey(op)) {
			getContext().error("Unknown operator: " + op, IGamlIssue.UNKNOWN_ACTION, originalExpression.eContainer(),
					op);
			return null;
		}
		Expression rightMember = originalExpression;
		// if the operator is an iterator, we must initialize the context
		// sensitive "each" variable
		final boolean isIterator = GAML.ITERATORS.contains(op);
		if (isIterator) {
			String argName = IKeyword.EACH;
			// Finding the name of 'each' if redefined
			if (rightMember instanceof ExpressionList params) {
				final List<Expression> exprs = EGaml.getInstance().getExprsOf(params);
				if (!exprs.isEmpty()) {
					final Expression arg = exprs.get(0);
					if (arg instanceof Parameter p) {
						argName = EGaml.getInstance().getKeyOf(p);
						rightMember = p.getRight();
					} else {
						rightMember = arg;
					}
				}
			}
			iteratorContexts.push(new EachExpression(argName, left.getGamlType().getContentType()));
		}
		// If the right-hand expression is a list of expression, then we have a
		// n-ary operator
		if (rightMember instanceof ExpressionList el) {
			final List<Expression> list = EGaml.getInstance().getExprsOf(el);
			final int size = list.size();
			if (size > 1) {
				final IExpression[] compiledArgs = new IExpression[size + 1];
				compiledArgs[0] = left;
				for (int i = 0; i < size; i++) { compiledArgs[i + 1] = compile(list.get(i)); }
				return getFactory().createOperator(op, getContext(), rightMember, compiledArgs);
			}
		}

		// Otherwise we can now safely compile the right-hand expression
		final IExpression right = compile(rightMember);
		// We make sure to remove any mention of the each expression after the
		// right member has been compiled
		if (isIterator) { iteratorContexts.pop(); }
		// and we return the operator expression
		return getFactory().createOperator(op, getContext(), originalExpression.eContainer(), left, right);

	}

	/**
	 * Action.
	 *
	 * @param name
	 *            the name
	 * @param callee
	 *            the callee
	 * @param args
	 *            the args
	 * @param action
	 *            the action
	 * @return the i expression
	 */
	private IExpression action(final String name, final IExpression callee, final ExpressionList args,
			final ActionDescription action) {
		final Arguments arguments = parseArguments(action, args, getContext(), true);
		return getFactory().createAction(name, getContext(), action, callee, arguments);
	}

	@Override
	public IExpression caseBinaryOperator(final BinaryOperator object) {
		return binary(object.getOp(), object.getLeft(), object.getRight());
	}

	/**
	 * Binary.
	 *
	 * @param op
	 *            the op
	 * @param e1
	 *            the e 1
	 * @param right
	 *            the right
	 * @return the i expression
	 */
	private IExpression binary(final String op, final Expression e1, final Expression right) {
		return switch (op) {
			// if the expression is " var of agents ", we must compile it apart
			case OF -> compileFieldExpr(right, e1);
			// if the operator is "as", the right-hand expression should be a
			// casting type
			case AS -> {
				final String type = EGaml.getInstance().getKeyOf(right);
				IType t = getType(type);
				if (t != null) { yield casting(t, compile(e1), right); }
				getContext().error(
						"'as' must be followed by a type, species or skill name. " + type + " is neither of these.",
						IGamlIssue.NOT_A_TYPE, right, type);
				yield null;
			}
			// if the operator is "is", the right-hand expression should be a type
			case IS -> {
				final IExpression left = compile(e1);
				final String type = EGaml.getInstance().getKeyOf(right);
				if (isTypeName(type)) {
					yield getFactory().createOperator(IS, getContext(), right.eContainer(), left,
							getFactory().createConst(type, Types.STRING));
				}
				if (isSkillName(type)) {
					yield getFactory().createOperator(IS_SKILL, getContext(), right.eContainer(), left,
							getFactory().createConst(type, Types.SKILL));
				}
				getContext().error(
						"'is' must be followed by a type, species or skill name. " + type + " is neither of these.",
						IGamlIssue.NOT_A_TYPE, right, type);
				yield null;
			}
			default -> binary(op, compile(e1), right);
		};
	}

	/**
	 * Gets the species context.
	 *
	 * @param e
	 *            the e
	 * @return the species context
	 */
	private SpeciesDescription getSpeciesContext(final String e) {
		return getContext().getSpeciesDescription(e);
	}

	/**
	 * Checks if is species name.
	 *
	 * @param s
	 *            the s
	 * @return true, if is species name
	 */
	private boolean isSpeciesName(final String s) {
		final ModelDescription m = getContext().getModelDescription();
		if (m == null) // can occur when building the kernel
			return false;
		final SpeciesDescription sd = m.getSpeciesDescription(s);
		return sd != null && !(sd instanceof ExperimentDescription);
	}

	/**
	 * Checks if is skill name.
	 *
	 * @param s
	 *            the s
	 * @return true, if is skill name
	 */
	private boolean isSkillName(final String s) {
		return GamaSkillRegistry.INSTANCE.hasSkill(s);
	}

	/**
	 * Checks if is type name.
	 *
	 * @param s
	 *            the s
	 * @return true, if is type name
	 */
	private boolean isTypeName(final String s) {
		final IType t = currentTypesManager.get(s, null);
		if (t == null) return false;
		final SpeciesDescription sd = t.getSpecies();
		if (sd != null && sd.isExperiment()) return false;
		return true;
	}

	/**
	 * Gets the type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param s
	 *            the s
	 * @return the type
	 * @date 12 janv. 2024
	 */
	private IType getType(final String s) {
		final IType t = currentTypesManager.get(s, null);
		if (t == null) return null;
		final SpeciesDescription sd = t.getSpecies();
		if (sd != null && sd.isExperiment()) return null;
		return t;
	}

	/**
	 * Compile named experiment field expr.
	 *
	 * @param leftExpr
	 *            the left expr
	 * @param name
	 *            the name
	 * @return the i expression
	 */
	private IExpression compileNamedExperimentFieldExpr(final Expression leftExpr, final String name) {
		final IExpression owner = compile(leftExpr);
		if (owner == null) return null;
		final IType type = owner.getGamlType();
		if (type.isParametricFormOf(Types.SPECIES)) {
			final SpeciesDescription sd = type.getContentType().getSpecies();
			if (sd instanceof ModelDescription md && md.hasExperiment(name))
				return getFactory().createConst(name, GamaType.from(md.getExperiment(name)));
		}
		getContext().error("Only experiments can be accessed using their plain name", IGamlIssue.UNKNOWN_FIELD);
		return null;
	}

	/**
	 * Compile field expr.
	 *
	 * @param leftExpr
	 *            the left expr
	 * @param fieldExpr
	 *            the field expr
	 * @return the i expression
	 */
	private IExpression compileFieldExpr(final Expression leftExpr, final Expression fieldExpr) {
		// If the owner cannot be determined (or leads to a previous error) we quit
		final IExpression owner = compile(leftExpr);
		if (owner == null) return null;
		// We gather the name of the "field"
		final String var = EGaml.getInstance().getKeyOf(fieldExpr);
		final IType type = owner.getGamlType();
		// hqnghi 28-05-14 search input variable from model, not experiment
		if (type instanceof ParametricType pt && pt.getGamlType().id() == IType.SPECIES
				&& pt.getContentType().getSpecies() instanceof ModelDescription md && md.hasExperiment(var))
			return getFactory().createConst(var, GamaType.from(md.getExperiment(var)));
		// end-hqnghi
		// If the owner has no species...
		final TypeDescription species = type.getSpecies();
		if (species == null) {
			// It can only be a field as 'actions' are not defined on simple objects, except for matrices, where it can
			// also represent the dot product
			final OperatorProto proto = type.getGetter(var);
			if (proto == null) {
				// Special case for matrices
				if (type.id() == IType.MATRIX) return binary(".", owner, fieldExpr);
				getContext().error("Unknown field '" + var + "' for type " + type, IGamlIssue.UNKNOWN_FIELD, leftExpr,
						var, type.toString());
				return null;
			}
			final TypeFieldExpression expr = (TypeFieldExpression) proto.create(getContext(), fieldExpr, owner);
			if (getContext() != null) { getContext().document(fieldExpr, expr); }
			return expr;
		}
		// We are now dealing with an agent. In that case, it can be either an attribute or an action call
		if (fieldExpr instanceof VariableRef) {
			IExpression expr = species.getVarExpr(var, true);
			if (expr == null) {
				if (species instanceof ModelDescription md && md.hasExperiment(var)) {
					expr = getFactory()
							.createTypeExpression(GamaType.from(Types.SPECIES, Types.INT, md.getTypeNamed(var)));
				} else if (species instanceof PlatformSpeciesDescription psd && GAMA.isInHeadLessMode())
					// Special case (see #2259 for headless validation of GUI preferences)
					return psd.getFakePrefExpression(var);
				else {
					getContext().error(
							"Unknown variable '" + var + "' in " + species.getKeyword() + " " + species.getName(),
							IGamlIssue.UNKNOWN_VAR, fieldExpr.eContainer(), var, species.getName());
					return null;
				}
				// special case for #3621. We cast the "simulation" and "simulations" variables of "experiment"
				// A more correct fix would have been to make `experiment` a parametric type that explicitly refers to
				// the species of the model as its contents type though...
			} else if (IKeyword.SIMULATION.equals(var) && expr.getGamlType().equals(Types.get(IKeyword.MODEL))) {
				ModelDescription md = getContext().getModelDescription();
				if (md != null) { expr = getFactory().createAs(currentContext, expr, md.getGamlType()); }
			} else if (IKeyword.SIMULATIONS.equals(var)
					&& expr.getGamlType().getContentType().equals(Types.get(IKeyword.MODEL))) {
				ModelDescription md = getContext().getModelDescription();
				if (md != null) { expr = getFactory().createAs(currentContext, expr, Types.LIST.of(md.getGamlType())); }
			}
			getContext().document(fieldExpr, expr);
			return getFactory().createOperator(_DOT, getContext(), fieldExpr, owner, expr);
		}
		if (fieldExpr instanceof Function) {
			final ActionDescription action = species.getAction(var);
			if (action != null) {
				final ExpressionList list = ((Function) fieldExpr).getRight();
				final IExpression call = action(var, owner, list, action);
				getContext().document(fieldExpr, call); // ??
				return call;
			}
		}
		return null;

	}

	// KEEP
	// private IExpression getWorldExpr() {
	// // if (world == null) {
	// final IType tt = getContext().getModelDescription()
	// ./* getWorldSpecies(). */getType();
	// final IExpression world = getFactory().createVar(WORLD_AGENT_NAME, tt,
	// true, IVarExpression.WORLD,
	// getContext().getModelDescription());
	// // }
	// return world;
	// }

	/**
	 * Sets the context.
	 *
	 * @param context
	 *            the context
	 * @return the i description
	 */
	private IDescription setContext(final IDescription context) {
		final IDescription previous = currentContext;
		currentContext = context == null ? GAML.getModelContext() : context;
		currentTypesManager = Types.builtInTypes;
		if (currentContext != null) {
			final ModelDescription md = currentContext.getModelDescription();
			if (md != null) {
				final ITypesManager tm = md.getTypesManager();
				if (tm != null) { currentTypesManager = tm; }
			}
		}
		return previous;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	private IDescription getContext() { return currentContext; }

	/**
	 * Gets the validation context.
	 *
	 * @return the validation context
	 */
	private ValidationContext getValidationContext() {
		if (currentContext == null) return null;
		return currentContext.getValidationContext();
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseArguments(msi.gaml.descriptions.ExpressionDescription,
	 *      msi.gaml.descriptions.IDescription)
	 */
	@Override
	public Arguments parseArguments(final ActionDescription action, final EObject o, final IDescription command,
			final boolean compileArgValues) {
		if (o == null) return null;
		boolean completeArgs = false;
		List<Expression> parameters = null;
		EGaml egaml = EGaml.getInstance();
		if (o instanceof Array array) {
			parameters = egaml.getExprsOf(array.getExprs());
		} else if (o instanceof ExpressionList) {
			parameters = egaml.getExprsOf(o);
			completeArgs = true;
		} else {
			command.error("Arguments must be written [a1::v1, a2::v2], (a1:v1, a2:v2) or (v1, v2)");
			return null;
		}
		final Arguments argMap = new Arguments();
		int index = 0;

		for (final Expression exp : parameters) {
			String arg = null;
			IExpressionDescription ed = null;
			if (exp instanceof ArgumentPair p) {
				arg = egaml.getKeyOfArgumentPair(p);
				ed = builder.create(p.getRight());
			} else if (exp instanceof Parameter p) {
				arg = egaml.getKeyOfParameter(p);
				ed = builder.create(p.getRight());
			} else if (exp instanceof BinaryOperator bo && "::".equals(bo.getOp())) {
				arg = egaml.getKeyOf(bo.getLeft());
				ed = builder.create(bo.getRight());
			} else if (completeArgs) {
				final List<String> args = action == null ? null : action.getArgNames();
				if (args != null && action != null && index == args.size()) {
					command.error("Wrong number of arguments. Action " + action.getName() + " expects " + args);
					return argMap;
				}
				arg = args == null ? String.valueOf(index++) : args.get(index++);
				ed = builder.create(exp);
			}
			if (ed != null && compileArgValues) { ed.compile(command); }
			argMap.put(arg, ed);
		}
		return argMap;
	}

	@Override
	public IExpression caseSkillRef(final SkillRef object) {
		return skill(EGaml.getInstance().getKeyOf(object));
	}

	@Override
	public IExpression caseActionRef(final ActionRef object) {
		final String s = EGaml.getInstance().getKeyOf(object);
		final SpeciesDescription sd = getContext().getSpeciesContext();
		// Look in the species and its ancestors
		ActionDescription ad = sd.getAction(s);
		// If it is not found, maybe it is in the host ?
		if (ad == null) {
			boolean isExp = sd instanceof ExperimentDescription ed;
			// If we are in an experiment, we cannot call an action defined in the model (see #
			if (!isExp) {
				IDescription host = sd.getEnclosingDescription();
				if (host != null) { ad = host.getAction(s); }
			}

			if (ad == null) {
				if (isExp) {
					getContext().error("The action " + s + " must be defined in the experiment",
							IGamlIssue.UNKNOWN_ACTION, object);
				} else {
					getContext().error("The action " + s + " is unknown", IGamlIssue.UNKNOWN_ACTION, object);
				}
				return null;
			}
		}
		if (ad.getArgNames().size() > 0) {
			getContext().error("Impossible to call an action that requires arguments", IGamlIssue.UNKNOWN_ARGUMENT,
					object);
			return null;
		}
		return new DenotedActionExpression(ad);
	}

	@Override
	public IExpression caseVariableRef(final VariableRef object) {
		final String s = EGaml.getInstance().getNameOfRef(object);
		if (s == null) return caseVarDefinition(object.getRef());
		return caseVar(s, object);
	}

	@Override
	public IExpression caseTypeRef(final TypeRef object) {
		final IType t = fromTypeRef(object);
		if (t == null) return null;
		if (t.isAgentType()) return t.getSpecies().getSpeciesExpr();
		return getFactory().createTypeExpression(t);
	}

	@Override
	public IExpression caseEquationRef(final EquationRef object) {
		return getFactory().createConst(EGaml.getInstance().getNameOfRef(object), Types.STRING);
	}

	@Override
	public IExpression caseUnitName(final UnitName object) {
		final String s = EGaml.getInstance().getNameOfRef(object);
		UnitConstantExpression exp = caseUnitName(s);
		if (exp == null) {
			getContext().error(s + " is not a unit or constant name.", IGamlIssue.NOT_A_UNIT, object, (String[]) null);
			return null;
		}
		if (exp.isDeprecated()) {
			getContext().warning(s + " is deprecated.", IGamlIssue.DEPRECATED, object, (String[]) null);
		}
		return exp;
	}

	/**
	 * Case unit name.
	 *
	 * @param name
	 *            the name
	 * @return the i expression
	 */
	public UnitConstantExpression caseUnitName(final String name) {
		if (GAML.UNITS.containsKey(name)) // Make sure we return "special" expressions like #month or #year -- see #3590
			return getFactory().getUnitExpr(name);
		return null;
	}

	@Override
	public IExpression caseVarDefinition(final VarDefinition object) {
		return skill(object.getName());
	}

	@Override
	public IExpression caseTypeDefinition(final TypeDefinition object) {
		return caseVar(object.getName(), object);
	}

	@Override
	public IExpression caseSkillFakeDefinition(final SkillFakeDefinition object) {
		return caseVar(object.getName(), object);
	}

	@Override
	public IExpression caseReservedLiteral(final ReservedLiteral object) {
		return caseVar(EGaml.getInstance().getKeyOf(object), object);
	}

	@Override
	public IExpression caseIf(final If object) {
		final IExpression ifFalse = compile(object.getIfFalse());
		final IExpression alt =
				getFactory().createOperator(":", getContext(), object, compile(object.getRight()), ifFalse);
		return getFactory().createOperator("?", getContext(), object, compile(object.getLeft()), alt);
	}

	@Override
	public IExpression caseArgumentPair(final ArgumentPair object) {
		return binary("::", caseVar(EGaml.getInstance().getKeyOf(object), object), object.getRight());
	}

	@Override
	public IExpression caseUnit(final Unit object) {
		// We simply return a multiplication, since the right member (the
		// "unit") will be translated into its float value
		return binary("*", object.getLeft(), object.getRight());
	}

	@Override
	public IExpression caseUnary(final Unary object) {
		return unary(EGaml.getInstance().getKeyOf(object), object.getRight());
	}

	/**
	 * Case dot.
	 *
	 * @param object
	 *            the object
	 * @return the i expression
	 */
	public IExpression caseDot(final Access object) {
		final Expression right = object.getRight();
		if (right instanceof StringLiteral sl) return compileNamedExperimentFieldExpr(object.getLeft(), sl.getOp());
		if (right != null) return compileFieldExpr(object.getLeft(), right);
		return null;
	}

	@Override
	public IExpression caseAccess(final Access object) {
		if (".".equals(object.getOp())) return caseDot(object);
		final IExpression container = compile(object.getLeft());
		// If no container is defined, return a null expression
		if (container == null) return null;
		final IType contType = container.getGamlType();
		final boolean isMatrix = Types.MATRIX.isAssignableFrom(contType);
		final IType keyType = contType.getKeyType();
		final List<? extends Expression> list = EGaml.getInstance().getExprsOf(object.getRight());
		try (final Collector.AsList<IExpression> result = Collector.getList()) {
			final int size = list.size();

			for (int i = 0; i < size; i++) {
				final Expression eExpr = list.get(i);
				final IExpression e = compile(eExpr);
				if (e != null) {
					final IType elementType = e.getGamlType();
					// See Issue #3099
					if (size == 1 && Types.PAIR.isAssignableFrom(elementType)
							&& Types.LIST.isAssignableFrom(contType)) {
						if (Types.INT == elementType.getKeyType() && Types.INT == elementType.getContentType())
							return getFactory().createOperator("internal_between", getContext(), object, container, e);
					}
					if (keyType != Types.NO_TYPE && !keyType.isAssignableFrom(elementType)
							&& (!isMatrix || elementType.id() != IType.INT)) {
						getContext().warning("a " + contType.toString() + " should not be accessed using a "
								+ elementType.toString() + " index", IGamlIssue.WRONG_TYPE, eExpr);
					}
					// if (!(isMatrix && elementType.id() == IType.INT && size > 1)) {
					//
					// }
					result.add(e);
				}
			}
			if (size > 2) {
				final String end = !isMatrix ? " only 1 index" : " 1 or 2 indices";
				getContext().warning("a " + contType.toString() + " should be accessed using" + end,
						IGamlIssue.DIFFERENT_ARGUMENTS, object);
			}

			final IExpression indices = getFactory().createList(result.items());

			IVarExpression varDiff = null;
			if (container instanceof IVarExpression.Agent && ((IVarExpression.Agent) container).getOwner() != null) {
				varDiff = ((IVarExpression.Agent) container).getVar();

				final SpeciesDescription species =
						((IVarExpression.Agent) varDiff).getDefinitionDescription().getSpeciesContext();
				if (species != null) {
					final Iterable<IDescription> equations = species.getChildrenWithKeyword(IKeyword.EQUATION);
					for (final IDescription equation : equations) {
						if (equation.manipulatesVar(varDiff.getName()))
							return getFactory().createOperator("internal_integrated_value", getContext(), object,
									((IVarExpression.Agent) container).getOwner(), varDiff);

					}
				}
			}

			return getFactory().createOperator("internal_at", getContext(), object, container, indices);
		}
	}

	@Override
	public IExpression caseArray(final Array object) {
		final List<? extends Expression> list = EGaml.getInstance().getExprsOf(object.getExprs());
		// Awkward expression, but necessary to fix Issue #2612
		final boolean allPairs = !list.isEmpty() && Iterables.all(list,
				each -> each instanceof ArgumentPair || "::".equals(EGaml.getInstance().getKeyOf(each)));
		final Iterable<IExpression> result = Iterables.transform(list, this::compile);
		if (Iterables.any(result, t -> t == null)) return null;
		return allPairs ? getFactory().createMap(result) : getFactory().createList(result);
	}

	@Override
	public IExpression casePoint(final Point object) {
		final Expression z = object.getZ();
		if (z == null) return binary(POINT, object.getLeft(), object.getRight());
		final IExpression[] exprs = new IExpression[3];
		exprs[0] = compile(object.getLeft());
		exprs[1] = compile(object.getRight());
		exprs[2] = compile(z);
		if (exprs[0] == null || exprs[1] == null || exprs[2] == null) return null;
		return getFactory().createOperator(POINT, getContext(), object, exprs);
	}

	@Override
	public IExpression caseExpressionList(final ExpressionList object) {
		final List<Expression> list = EGaml.getInstance().getExprsOf(object);
		if (list.isEmpty()) return null;
		if (list.size() > 1) {
			getContext().warning(
					"A sequence of expressions is not expected here. Only the first expression will be evaluated",
					IGamlIssue.UNKNOWN_ARGUMENT, object);
		}
		return compile(list.get(0));
	}

	@Override
	public IExpression caseFunction(final Function object) {
		if (object == null) return null;
		final String op = EGaml.getInstance().getKeyOf(object.getLeft());
		IExpression result = tryCastingFunction(op, object);
		if (result != null) return result;
		result = tryActionCall(op, object);
		if (result != null) return result;
		final List<Expression> args = EGaml.getInstance().getExprsOf(object.getRight());
		return switch (args.size()) {
			case 0 -> {
				getContext().error("Unknown operator or action: " + op, IGamlIssue.UNKNOWN_ACTION, object);
				yield null;
			}
			case 1 -> unary(op, args.get(0));
			case 2 -> binary(op, args.get(0), args.get(1));
			default -> getFactory().createOperator(op, getContext(), object,
					toArray(transform(args, this::compile), IExpression.class));
		};
	}

	/**
	 * Try casting function.
	 *
	 * @param op
	 *            the op
	 * @param object
	 *            the object
	 * @return the i expression
	 */
	private IExpression tryCastingFunction(final String op, final Function object) {
		// If the operator is not a type name, no match
		IType t = getType(op);
		if (t == null) {
			// We nevertheless emit a warning if the operator name contains parametric type information
			if (object.getType() != null) {
				getContext().warning(
						op + " is not a type name: parameter types are not expected and will not be evaluated",
						IGamlIssue.UNKNOWN_ARGUMENT, object.getType());
			}
			return null;
		}
		final List<Expression> args = EGaml.getInstance().getExprsOf(object.getRight());
		return switch (args.size()) {
			case 0 -> null;
			case 1 -> {
				IExpression expr = compile(args.get(0));
				// If a unary function has been redefined with the type name as name and this specific argument,
				// it takes precedence over the regular casting
				if (getFactory().hasExactOperator(op, expr)) { yield null; }
				yield casting(t, expr, object);
			}
			default -> {
				Iterable<IExpression> exprs = transform(args, this::compile);
				// If more than one argument, we need to check if there are operators that match. If yes, we return null
				if (getFactory().hasOperator(op, new Signature(toArray(exprs, IExpression.class)))) { yield null; }
				yield casting(t, getFactory().createList(exprs), object);
			}
		};

	}

	/**
	 * Try action call.
	 *
	 * @param op
	 *            the op
	 * @param object
	 *            the object
	 * @return the i expression
	 */
	private IExpression tryActionCall(final String op, final Function object) {
		SpeciesDescription species = getContext().getSpeciesContext();
		if (species == null) return null;
		final boolean isSuper = getContext() instanceof StatementDescription st && st.isSuperInvocation();
		ActionDescription action = isSuper ? species.getParent().getAction(op) : species.getAction(op);
		if (action == null) {
			// Not found: see #3530
			if (species instanceof ExperimentDescription && getContext().isIn(IKeyword.OUTPUT)) {
				species = species.getModelDescription();
			}
			action = isSuper ? species.getParent().getAction(op) : species.getAction(op);
		}
		if (action == null) return null;
		final ExpressionList params = object.getRight();
		return action(op, caseVar(isSuper ? SUPER : SELF, object), params, action);
	}

	@Override
	public IExpression caseIntLiteral(final IntLiteral object) {
		try {
			final Integer val = Integer.parseInt(EGaml.getInstance().getKeyOf(object), 10);
			return getFactory().createConst(val, Types.INT);
		} catch (final NumberFormatException e) {
			getContext().error("Malformed integer: " + EGaml.getInstance().getKeyOf(object), IGamlIssue.UNKNOWN_NUMBER,
					object);
			return null;
		}
	}

	@Override
	public IExpression caseDoubleLiteral(final DoubleLiteral object) {

		String s = EGaml.getInstance().getKeyOf(object);

		if (s == null) return null;
		try {
			return getFactory().createConst(Double.parseDouble(s), Types.FLOAT);
		} catch (final NumberFormatException e) {
			try {
				final NumberFormat nf = NumberFormat.getInstance(Locale.US);
				// More robust, but slower parsing used in case
				// Double.parseDouble() cannot handle it
				// See Issue 1025. Exponent notation is capitalized, and '+' is
				// removed beforehand
				s = s.replace('e', 'E').replace("+", "");
				return getFactory().createConst(nf.parse(s).doubleValue(), Types.FLOAT);
			} catch (final ParseException ex) {
				getContext().error("Malformed float: " + s, IGamlIssue.UNKNOWN_NUMBER, object);
				return null;
			}
		}

	}

	@Override
	public IExpression caseStringLiteral(final StringLiteral object) {
		return getFactory().createConst(StringUtils.unescapeJava(EGaml.getInstance().getKeyOf(object)), Types.STRING);
	}

	@Override
	public IExpression caseBooleanLiteral(final BooleanLiteral object) {
		final String s = EGaml.getInstance().getKeyOf(object);
		if (s == null) return null;
		return TRUE.equalsIgnoreCase(s) ? TRUE_EXPR : FALSE_EXPR;
	}

	@Override
	public IExpression defaultCase(final EObject object) {
		final ValidationContext vc = getValidationContext();
		if (vc != null && !vc.hasErrors()) {
			// In order to avoid too many "useless errors"
			getContext().error("Cannot compile: " + object, IGamlIssue.GENERAL, object);
		}
		return null;
	}

	/**
	 * Case var.
	 *
	 * @param varName
	 *            the var name
	 * @param object
	 *            the object
	 * @return the i expression
	 */
	private IExpression caseVar(final String varName, final EObject object) {
		if (varName == null) {
			getContext().error("Unknown variable", IGamlIssue.UNKNOWN_VAR, object);
			return null;
		}
		switch (varName) {
			case EXPERIMENT:
				IExperimentPlan exp = GAMA.getExperiment();
				if (exp != null) return getFactory().createConst(exp.getAgent(), exp.getDescription().getGamlType());
				break;
			case IKeyword.GAMA:
				return GAMA.getPlatformAgent();
			case EACH:
				return getEachExpr(object);
			case NULL:
				return NIL_EXPR;
			case SELF:
				return returnSelfOrSuper(SELF, object, false);
			case SUPER:
				return returnSelfOrSuper(SUPER, object, true);
		}

		// check if the var has been declared in an iterator context
		for (final IVarExpression it : iteratorContexts) { if (it.getName().equals(varName)) return it; }
		IDescription context = getContext();
		final IVarDescriptionProvider temp_sd = context == null ? null : context.getDescriptionDeclaringVar(varName);

		if (temp_sd != null) {
			if (!(temp_sd instanceof SpeciesDescription)) return temp_sd.getVarExpr(varName, false);
			final SpeciesDescription remote_sd = getContext().getSpeciesContext();
			if (remote_sd != null) {
				final SpeciesDescription found_sd = (SpeciesDescription) temp_sd;
				if (remote_sd != temp_sd && !remote_sd.isBuiltIn() && !remote_sd.hasMacroSpecies(found_sd)) {
					getContext().error("The variable " + varName + " is not accessible in this context ("
							+ remote_sd.getName() + "), but in the context of " + found_sd.getName()
							+ ". It should be preceded by 'myself.'", IGamlIssue.UNKNOWN_VAR, object, varName);
				}
			}
			// See Issue #3085. We give priority to the variables sporting species names unless they represent the
			// species within the agents
			if (!isSpeciesName(varName)) return temp_sd.getVarExpr(varName, false);
		}

		// See Issue #3085
		if (isSpeciesName(varName)) {
			final SpeciesDescription sd = getSpeciesContext(varName);
			return sd == null ? null : sd.getSpeciesExpr();
		}
		IType t = getType(varName);
		if (t != null) return getFactory().createTypeExpression(t);
		if (isSkillName(varName)) return skill(varName);
		if (context != null) {

			// An experimental possibility is that the variable refers to a
			// an action (used like a variable, see Issue 853) or also any
			// behavior or aspect
			final SpeciesDescription sd = context.getSpeciesContext();
			if (sd.hasAction(varName, false)) return new DenotedActionExpression(sd.getAction(varName));
			if (sd.hasBehavior(varName)) return new DenotedActionExpression(sd.getBehavior(varName));
			if (sd.hasAspect(varName)) return new DenotedActionExpression(sd.getAspect(varName));

			// A last possibility is to offer some transition guidance to users who used to write event layer names as
			// labels (neither as string or constant). For instance : mouse_move instead of "mouse_move" or #mouse_move.
			// For that, we emit simply a warning (not an error) and we return the corresponding constant.

			if (MouseEventLayerDelegate.EVENTS.contains(varName)
					|| KeyboardEventLayerDelegate.EVENTS.contains(varName)) {
				UnitConstantExpression exp = this.caseUnitName(varName);
				if (exp != null) {
					context.warning("The usage of the event name (" + varName
							+ ") is now deprecated and should be replaced either by a string ('" + varName
							+ "') or a constant (#" + varName + ")", IGamlIssue.UNKNOWN_VAR, object, varName);
					return exp;
				}
			}
			getContext().error(varName + " is not defined or accessible in this context. Check its name or declare it",
					IGamlIssue.UNKNOWN_VAR, object, varName);
		}
		return null;

	}

	/**
	 * Return self or super.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param object
	 *            the object
	 * @param isSuper
	 *            the is super
	 * @return the i expression
	 * @date 7 janv. 2024
	 */
	private IExpression returnSelfOrSuper(final String name, final EObject object, final boolean isSuper) {
		final SpeciesDescription sd = getContext().getSpeciesContext();
		if (sd == null) {
			getContext().error("Unable to determine the species of " + name, IGamlIssue.GENERAL, object);
			return null;
		}
		IType type = isSuper ? sd.getParent().getGamlType() : sd.getGamlType();
		return getFactory().createVar(name, type, true, isSuper ? IVarExpression.SUPER : IVarExpression.SELF, null);
	}

	/**
	 * Gets the e object of.
	 *
	 * @param string
	 *            the string
	 * @param tempContext
	 *            the temp context
	 * @return the e object of
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private EObject getEObjectOf(final String string, final IExecutionContext tempContext) throws GamaRuntimeException {
		EObject result = null;
		final String s = "dummy <- " + string;
		final GamlResource resource = GamlResourceServices.getTemporaryResource(getContext());
		try {
			final InputStream is = new ByteArrayInputStream(s.getBytes());
			try {
				resource.loadSynthetic(is, tempContext);
			} catch (final Exception e1) {
				e1.printStackTrace();
				return null;
			}

			if (resource.hasErrors()) {
				final Resource.Diagnostic d = resource.getErrors().get(0);
				throw GamaRuntimeException.error(d.getMessage(), tempContext.getScope());
			}
			final EObject e = resource.getContents().get(0);
			if (e instanceof StringEvaluator) { result = ((StringEvaluator) e).getExpr(); }

			return result;
		} finally {
			GamlResourceServices.discardTemporaryResource(resource);
		}
	}

	@Override
	public List<IDescription> compileBlock(final String string, final IDescription actionContext,
			final IExecutionContext tempContext) throws GamaRuntimeException {
		final String s = "__synthetic__ {" + string + "}";
		final GamlResource resource = GamlResourceServices.getTemporaryResource(getContext());
		try (final Collector.AsList<IDescription> result = Collector.getList()) {
			final InputStream is = new ByteArrayInputStream(s.getBytes());
			try {
				resource.loadSynthetic(is, tempContext);
			} catch (final Exception e1) {
				e1.printStackTrace();
				return null;
			} finally {}
			if (resource.hasErrors()) {
				final Resource.Diagnostic d = resource.getErrors().get(0);
				throw GamaRuntimeException.error(d.getMessage(), tempContext.getScope());
			}
			final SyntacticModelElement elt = (SyntacticModelElement) resource.getSyntacticContents();
			// We have a problem -- can be simply an empty block or an expression
			if (!elt.hasChildren() && elt.hasFacet(IKeyword.FUNCTION)) {
				// Compile the expression is the best way to know if this expression is correct
				elt.getExpressionAt(IKeyword.FUNCTION).compile(actionContext);
			}
			elt.visitChildren(e -> {
				final IDescription desc = DescriptionFactory.create(e, actionContext, null);
				result.add(desc);
			});
			return result.items();
		} finally {
			GamlResourceServices.discardTemporaryResource(resource);
		}
	}

	//
	// end-hqnghi
	//

	/**
	 * Gets the each expr.
	 *
	 * @param object
	 *            the object
	 * @return the each expr
	 */
	public IVarExpression getEachExpr(final EObject object) {
		final IVarExpression p = iteratorContexts.peek();
		if (p == null) {
			getContext().error("'each' is not accessible in this context", IGamlIssue.UNKNOWN_VAR, object);
			return null;
		}
		return p;
	}

	/**
	 * Gets the current expression description.
	 *
	 * @return the current expression description
	 */
	private IExpressionDescription getCurrentExpressionDescription() { return currentExpressionDescription; }

	/**
	 * Sets the current expression description.
	 *
	 * @param currentExpressionDescription
	 *            the new current expression description
	 */
	private void setCurrentExpressionDescription(final IExpressionDescription currentExpressionDescription) {
		this.currentExpressionDescription = currentExpressionDescription;
	}

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	private IExpressionFactory getFactory() { return GAML.getExpressionFactory(); }

	@Override
	public void dispose() {
		this.currentContext = null;
		this.currentTypesManager = null;
		this.currentExpressionDescription = null;
		this.iteratorContexts.clear();

	}

}
