/*******************************************************************************************************
 *
 * GamlExpressionCompiler.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.expression;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static msi.gama.common.interfaces.IKeyword.AS;
import static msi.gama.common.interfaces.IKeyword.EACH;
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

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
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
import msi.gama.lang.gaml.gaml.TerminalExpression;
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
import msi.gaml.statements.Arguments;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.ITypesManager;
import msi.gaml.types.ParametricType;
import msi.gaml.types.Types;

/**
 * The Class GamlExpressionCompiler. Transforms Strings or XText Expressions into GAML IExpressions. Normally invoked by
 * an IExpressionFactory (the default being GAML.getExpressionFactory())
 */

/**
 * The Class GamlExpressionCompiler.
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
		if (s == null) // No error, since the null expressions come from previous (more
			// focused) errors and not from the parser itself.
			return null;

		final IExpression expr = doSwitch(s);
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
		if ("°".equals(op) || "#".equals(op)) return expr;
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
	private IExpression casting(final String type, final IExpression toCast, final Expression typeObject) {
		if (toCast == null) return null;
		final IType castingType = currentTypesManager.get(type).typeIfCasting(toCast);

		final boolean isSuperType = castingType.isAssignableFrom(toCast.getGamlType());
		TypeInfo typeInfo = null;
		if (typeObject instanceof TypeRef) {
			typeInfo = ((TypeRef) typeObject).getParameter();
		} else if (typeObject instanceof Function) { typeInfo = ((Function) typeObject).getType(); }
		if (isSuperType && typeInfo == null) {
			getContext().info("Unneeded casting: '" + toCast.serialize(true) + "' is already of type " + type,
					IGamlIssue.UNUSED, typeObject);
			// Issue #2521: indicate but don't skip the casting
			// return toCast;
		}
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
		if (result.isAssignableFrom(toCast.getGamlType())) {
			getContext().info("Unneeded casting: '" + toCast.serialize(true) + "' is already of type " + type,
					IGamlIssue.UNUSED, typeObject);
			// Issue #2521: indicate but don't skip the casting
			// return toCast;
		}

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
		Expression rightMember = originalExpression;
		// if the operator is "as", the right-hand expression should be a
		// casting type
		if (AS.equals(op)) return binaryAs(left, rightMember);
		// if the operator is "is", the right-hand expression should be a type
		if (IS.equals(op)) return binaryIs(left, rightMember);

		// we verify and compile apart the calls to actions as operators

		final TypeDescription sd = left.getGamlType().getSpecies();
		if (sd != null) {
			final ActionDescription action = sd.getAction(op);
			if (action != null) {
				final IExpression result = action(op, left, rightMember, action);
				if (result != null) {
					getContext().warning(
							"This way of calling actions is deprecated. Please use the dotted notation (e.g. ag.action(..)) ",
							IGamlIssue.DEPRECATED, rightMember, op);
					return result;
				}
			}
		}
		// It is not an action, it must be an operator. We emit an error and
		// stop compiling if not
		if (!GAML.OPERATORS.containsKey(op)) {
			getContext().error("Unknown action or operator: " + op, IGamlIssue.UNKNOWN_ACTION, rightMember.eContainer(),
					op);
			return null;
		}

		// if the operator is an iterator, we must initialize the context
		// sensitive "each" variable
		final boolean isIterator = GAML.ITERATORS.contains(op);
		if (isIterator) {
			final IType t = left.getGamlType().getContentType();
			final String argName = findIteratorArgName(rightMember);
			rightMember = findIteratorExpr(op, rightMember);
			iteratorContexts.push(new EachExpression(argName, t));
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
	 * Find iterator arg name.
	 *
	 * @param e2
	 *            the e 2
	 * @return the string
	 */
	private String findIteratorArgName(final Expression e2) {
		if (!(e2 instanceof ExpressionList params)) return IKeyword.EACH;
		final List<Expression> exprs = EGaml.getInstance().getExprsOf(params);
		if (exprs == null || exprs.isEmpty()) return IKeyword.EACH;
		final Expression arg = exprs.get(0);
		if (!(arg instanceof Parameter p)) return IKeyword.EACH;
		return EGaml.getInstance().getKeyOf(p);
	}

	/**
	 * Find iterator expr.
	 *
	 * @param op
	 *
	 * @param e2
	 *            the e 2
	 * @return the expression
	 */
	private Expression findIteratorExpr(final String op, final Expression e2) {
		if (!(e2 instanceof ExpressionList params)) return e2;
		final List<Expression> exprs = EGaml.getInstance().getExprsOf(params);
		// Could be technically possible to allow 2 or more arguments (see #3619)
		if (exprs == null || exprs.size() != 1) return e2;
		final Expression arg = exprs.get(0);
		if (!(arg instanceof Parameter p)) return arg;
		return p.getRight();
	}

	/**
	 * Binary is.
	 *
	 * @param left
	 *            the left
	 * @param e2
	 *            the e 2
	 * @return the i expression
	 */
	private IExpression binaryIs(final IExpression left, final Expression e2) {
		final String type = EGaml.getInstance().getKeyOf(e2);
		if (isTypeName(type)) return getFactory().createOperator(IS, getContext(), e2.eContainer(), left,
				getFactory().createConst(type, Types.STRING));
		if (isSkillName(type)) return getFactory().createOperator(IS_SKILL, getContext(), e2.eContainer(), left,
				getFactory().createConst(type, Types.SKILL));
		getContext().error("'is' must be followed by a type, species or skill name. " + type + " is neither of these.",
				IGamlIssue.NOT_A_TYPE, e2, type);
		return null;
	}

	/**
	 * Binary as.
	 *
	 * @param left
	 *            the left
	 * @param e2
	 *            the e 2
	 * @return the i expression
	 */
	private IExpression binaryAs(final IExpression left, final Expression e2) {
		final String type = EGaml.getInstance().getKeyOf(e2);
		if (isTypeName(type)) return casting(type, left, e2);
		getContext().error("'as' must be followed by a type, species or skill name. " + type + " is neither of these.",
				IGamlIssue.NOT_A_TYPE, e2, type);
		return null;
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
	private IExpression action(final String name, final IExpression callee, final EObject args,
			final ActionDescription action) {
		final Arguments arguments = parseArguments(action, args, getContext(), true);
		return getFactory().createAction(name, getContext(), action, callee, arguments);
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
	// KEEP
	private IExpression binary(final String op, final Expression e1, final Expression right) {

		// if the expression is " var of agents ", we must compile it apart
		if (OF.equals(op)) return compileFieldExpr(right, e1);
		// we can now safely compile the left-hand expression
		final IExpression left = compile(e1);
		return binary(op, left, right);
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

		if (!currentTypesManager.containsType(s)) return false;
		final IType t = currentTypesManager.get(s);
		final SpeciesDescription sd = t.getSpecies();
		if (sd != null && sd.isExperiment()) return false;
		return true;
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
		final IExpression owner = compile(leftExpr);
		if (owner == null) return null;
		final IType type = owner.getGamlType();
		final TypeDescription species = type.getSpecies();
		// hqnghi 28-05-14 search input variable from model, not experiment
		if (type instanceof ParametricType && type.getGamlType().id() == IType.SPECIES
				&& type.getContentType().getSpecies() instanceof ModelDescription) {
			final ModelDescription sd = (ModelDescription) type.getContentType().getSpecies();
			final String var = EGaml.getInstance().getKeyOf(fieldExpr);
			if (sd.hasExperiment(var)) return getFactory().createConst(var, GamaType.from(sd.getExperiment(var)));
		}
		// end-hqnghi
		if (species == null) {
			// It can only be a variable as 'actions' are not defined on simple
			// objects, except for matrices, where it
			// can also represent the dot product
			final String var = EGaml.getInstance().getKeyOf(fieldExpr);
			final OperatorProto proto = type.getGetter(var);

			// Special case for matrices
			if (type.id() == IType.MATRIX && proto == null) return binary(".", owner, fieldExpr);

			if (proto == null) {
				getContext().error("Unknown field '" + var + "' for type " + type, IGamlIssue.UNKNOWN_FIELD, leftExpr,
						var, type.toString());
				return null;
			}
			final TypeFieldExpression expr = (TypeFieldExpression) proto.create(getContext(), fieldExpr, owner);
			if (getContext() != null) { getContext().document(fieldExpr, expr); }
			return expr;
		}
		// We are now dealing with an agent. In that case, it can be either an
		// attribute or an
		// action call
		if (fieldExpr instanceof VariableRef) {
			final String var = EGaml.getInstance().getKeyOf(fieldExpr);
			IExpression expr = species.getVarExpr(var, true);

			if (expr == null) {
				if (species instanceof ModelDescription && ((ModelDescription) species).hasExperiment(var)) {
					final IType t = Types.get(IKeyword.SPECIES);
					expr = getFactory().createTypeExpression(GamaType.from(t, Types.INT, species.getTypeNamed(var)));
				} else if (IKeyword.PLATFORM.equals(species.getName()) && GAMA.isInHeadLessMode())
					// Special case (see #2259 for headless validation of GUI preferences)
					return ((PlatformSpeciesDescription) species).getFakePrefExpression(var);
				else {
					getContext().error("Unknown variable '" + var + "' for species " + species.getName(),
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
			final String name = EGaml.getInstance().getKeyOf(fieldExpr);
			final ActionDescription action = species.getAction(name);
			if (action != null) {
				final ExpressionList list = ((Function) fieldExpr).getRight();
				final IExpression call = action(name, owner, list, action);
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
			final boolean actionParameters) {
		if (o == null) return null;
		boolean completeArgs = false;
		List<Expression> parameters = null;
		if (o instanceof Array) {
			// AD:#2596.
			if (actionParameters) {
				command.warning("This way of passing arguments is deprecated. Please use (a1:v1, a2:v2) or (v1, v2)",
						IGamlIssue.DEPRECATED, o);
			}
			parameters = EGaml.getInstance().getExprsOf(((Array) o).getExprs());
		} else if (o instanceof ExpressionList) {
			parameters = EGaml.getInstance().getExprsOf(o);
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

			if (exp instanceof ArgumentPair) {
				arg = EGaml.getInstance().getKeyOf(exp);
				ed = builder.create(((ArgumentPair) exp).getRight()/* , errors */);
			} else if (exp instanceof Parameter) {
				arg = EGaml.getInstance().getKeyOf(exp);
				ed = builder.create(((Parameter) exp).getRight()/* , errors */);
			} else if (exp instanceof BinaryOperator && "::".equals(EGaml.getInstance().getKeyOf(exp))) {
				arg = EGaml.getInstance().getKeyOf(((BinaryOperator) exp).getLeft());
				ed = builder.create(((BinaryOperator) exp).getRight()/* , errors */);
			} else if (completeArgs) {
				final List<String> args = action == null ? null : action.getArgNames();
				if (args != null && action != null && index == args.size()) {
					command.error("Wrong number of arguments. Action " + action.getName() + " expects " + args);
					return argMap;
				}
				arg = args == null ? String.valueOf(index++) : args.get(index++);
				ed = builder.create(exp/* , errors */);

			}
			if (ed != null && actionParameters) { ed.compile(command); }
			// if (!errors.isEmpty()) {
			// for (final Diagnostic d : errors) {
			// getContext().warning(d.getMessage(), "", exp);
			// }
			// }
			argMap.put(arg, ed);
			// errors.clear();
		}

		return argMap;
	}

	// @Override
	// public IExpression caseCast(final Cast object) {
	// return binary(AS, object.getLeft(), object.getRight());
	// }

	@Override
	public IExpression caseSkillRef(final SkillRef object) {
		// final String s = EGaml.getInstance().getKeyOf(object);
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
	public IExpression caseExpression(final Expression object) {
		return null;
		// // If an error already exists, we discard the case
		// final ValidationContext vc = getValidationContext();
		// final Expression left = object.getLeft();
		// final Expression right = object.getRight();
		// if (vc == null || vc.hasErrorOn(object, left, right)) { return null; }
		// // in the general case, we try to return a binary expression
		// final IExpression result = binary(EGaml.getInstance().getKeyOf(object), object.getLeft(), object.getRight());
		// return result;
	}

	@Override
	public IExpression caseVariableRef(final VariableRef object) {
		final String s = EGaml.getInstance().getKeyOf(object);
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
		return getFactory().createConst(EGaml.getInstance().getKeyOf(object), Types.STRING);
	}

	@Override
	public IExpression caseUnitName(final UnitName object) {
		final String s = EGaml.getInstance().getKeyOf(object);
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
		if (right instanceof StringLiteral)
			return compileNamedExperimentFieldExpr(object.getLeft(), EGaml.getInstance().getKeyOf(right));
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
	//
	// @Override
	// public IExpression caseParameters(final Parameters object) {
	// final Iterable it = Iterables.transform(EGaml.getInstance().getExprsOf(object.getParams()), input -> binary("::",
	// getFactory().createConst(EGaml.getInstance().getKeyOf(input.getLeft()), Types.STRING), input.getRight()));
	// return getFactory().createMap(it);
	// }

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
		final String op = EGaml.getInstance().getKeyOf(object);
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
		if (!isCastingFunction(op, object)) return null;
		final List<Expression> args = EGaml.getInstance().getExprsOf(object.getRight());
		final int size = args.size();
		IExpression toCast;
		if (size == 1) {
			toCast = compile(args.get(0));
		} else {
			toCast = getFactory().createList(transform(args, this::compile));
		}
		return binaryAs(toCast, object);
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
		SpeciesDescription sd = getContext().getSpeciesContext();
		if (sd == null) return null;
		final boolean isSuper = getContext() instanceof StatementDescription
				&& ((StatementDescription) getContext()).isSuperInvocation();
		ActionDescription action = isSuper ? sd.getParent().getAction(op) : sd.getAction(op);
		if (action == null) {
			// Not found: see #3530
			if (sd instanceof ExperimentDescription && getContext().isIn(IKeyword.OUTPUT)) {
				sd = sd.getModelDescription();
			}
			action = isSuper ? sd.getParent().getAction(op) : sd.getAction(op);
		}
		if (action == null) return null;
		final EObject params = object.getRight();
		return action(op, caseVar(isSuper ? SUPER : SELF, object), params, action);
	}

	/**
	 * Checks if is casting function.
	 *
	 * @param op
	 *            the op
	 * @param object
	 *            the object
	 * @return true, if is casting function
	 */
	private boolean isCastingFunction(final String op, final Function object) {
		// If the operator is not a type name, no match
		if (!isTypeName(op)) {
			// We nevertheless emit a warning if the operator name contains parametric type information
			if (object.getType() != null) {
				getContext().warning(
						op + " is not a type name: key and contents types are not expected and will not be evaluated",
						IGamlIssue.UNKNOWN_ARGUMENT, object.getType());
			}
			return false;
		}
		// We look at the arguments of the operator
		final List<Expression> args = EGaml.getInstance().getExprsOf(object.getRight());
		final int size = args.size();
		// If there is none, it can't be a casting
		if (size == 0) return false;
		// If there is one, we match
		if (size == 1) {// If a unary function has been redefined with the type name as name and this specific argument,
						// it takes precedence over the regular casting
			IExpression expr = compile(args.get(0));
			return !getFactory().hasExactOperator(op, expr);
		}
		// return true;

		// If more than one, we need to check if there are operators that match. If yes, we return false
		return !getFactory().hasOperator(op, toArray(transform(args, this::compile), IExpression.class));
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
			case IKeyword.GAMA:
				return GAMA.getPlatformAgent();
			case EACH:
				return getEachExpr(object);
			case NULL:
				return IExpressionFactory.NIL_EXPR;
			case SELF:
				final IDescription temp_sd = getContext().getSpeciesContext();
				if (temp_sd == null) {
					getContext().error("Unable to determine the species of self", IGamlIssue.GENERAL, object);
					return null;
				}
				final IType tt = temp_sd.getGamlType();
				return getFactory().createVar(SELF, tt, true, IVarExpression.SELF, null);
			case SUPER:
				SpeciesDescription sd = getContext().getSpeciesContext();
				if (sd != null) { sd = sd.getParent(); }
				if (sd == null) {
					getContext().error("Unable to determine the species of super", IGamlIssue.GENERAL, object);
					return null;
				}
				final IType t = sd.getGamlType();
				return getFactory().createVar(SUPER, t, true, IVarExpression.SUPER, null);

		}

		// check if the var has been declared in an iterator context
		for (final IVarExpression it : iteratorContexts) { if (it.getName().equals(varName)) return it; }

		final IVarDescriptionProvider temp_sd =
				getContext() == null ? null : getContext().getDescriptionDeclaringVar(varName);

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
			// species withing the agents
			if (!isSpeciesName(varName)) return temp_sd.getVarExpr(varName, false);
		}

		// See Issue #3085
		if (isSpeciesName(varName)) {
			final SpeciesDescription sd = getSpeciesContext(varName);
			return sd == null ? null : sd.getSpeciesExpr();
		}

		if (isTypeName(varName)) return getFactory().createTypeExpression(currentTypesManager.get(varName));

		if (isSkillName(varName)) return skill(varName);

		if (getContext() != null) {

			// Finally, a last possibility (enabled in rare occasions, like in
			// the "elevation" facet of grid layers), is that the variable
			// belongs to the species denoted by the
			// current statement. Also the case in "attributes" of the save statement
			// if (getContext() instanceof StatementDescription) {
			// final SpeciesDescription denotedSpecies = getContext().getGamlType().getDenotedSpecies();
			// if (denotedSpecies != null) {
			// if (denotedSpecies.hasAttribute(varName)) { return denotedSpecies.getVarExpr(varName, false); }
			// }
			// }

			// An experimental possibility is that the variable refers to a
			// an action (used like a variable, see Issue 853) or also any
			// behavior or aspect
			final SpeciesDescription sd = getContext().getSpeciesContext();
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
					getContext().warning(
							"The direct usage of the event name (" + varName
									+ ") is now deprecated and should be replaced either by a string ('" + varName
									+ "') or a constant if it has been defined (#" + varName + ")",
							IGamlIssue.UNKNOWN_VAR, object, varName);
					return exp;
				}
			}
			getContext().error(
					"The variable " + varName
							+ " is not defined or accessible in this context. Check its name or declare it",
					IGamlIssue.UNKNOWN_VAR, object, varName);
		}
		return null;

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

	@Override
	public IExpression caseTerminalExpression(final TerminalExpression object) {
		return null;
	}

	@Override
	public IExpression caseBinaryOperator(final BinaryOperator object) {
		return binary(object.getOp(), object.getLeft(), object.getRight());
	}

}
