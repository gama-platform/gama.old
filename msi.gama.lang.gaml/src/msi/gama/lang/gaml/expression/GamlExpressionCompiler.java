/*********************************************************************************************
 *
 * 'GamlExpressionCompiler.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.common.collect.Iterables;

import gnu.trove.map.hash.THashMap;
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
import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.EquationRef;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
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
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
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
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.DenotedActionExpression;
import msi.gaml.expressions.EachExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.TimeUnitConstantExpression;
import msi.gaml.expressions.TypeFieldExpression;
import msi.gaml.expressions.UnitConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Dates;
import msi.gaml.operators.IUnits;
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
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlExpressionCompiler extends GamlSwitch<IExpression> implements IExpressionCompiler<Expression> {

	private final Deque<IVarExpression> iteratorContexts = new LinkedList();
	// To disable reentrant parsing (Issue 782)
	private IExpressionDescription currentExpressionDescription;
	private ITypesManager currentTypesManager;
	private final static Map<String, IExpression> constantSyntheticExpressions = new THashMap<>();
	private static final ExpressionDescriptionBuilder builder = new ExpressionDescriptionBuilder();

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context of the current
	 * simulation is returned (via simulation.getModel().getDescription()) if it is available. Otherwise, only simple
	 * expressions (that contain constants) can be parsed.
	 */
	private IDescription currentContext;

	static {
		IExpressionCompiler.OPERATORS.put(MY, new THashMap<>());
	}

	@Override
	public IExpression compile(final IExpressionDescription s, final IDescription parsingContext) {
		// Cf. Issue 782. Returns the expression if an expression needs its
		// compiled version to be compiled.
		if (s.isConst() || s == getCurrentExpressionDescription()) { return s.getExpression(); }
		setCurrentExpressionDescription(s);
		final EObject o = s.getTarget();
		if (o == null && s instanceof StringBasedExpressionDescription) {
			final IExecutionContext context =
					GAMA.getExperiment() == null ? null : GAMA.getRuntimeScope().getExecutionContext();
			return compile(s.toString(), parsingContext, context);
		}
		final IDescription previous = setContext(parsingContext);
		try {
			final IExpression result = compile(o);
			return result;
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
			if (result != null) { return result; }
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

	private IExpression compile(final EObject s) {
		if (s == null) {
			// No error, since the null expressions come from previous (more
			// focused) errors and not from the parser itself.
			return null;
		}

		final IExpression expr = doSwitch(s);
		if (expr != null && getContext() != null) {
			getContext().document(s, expr);
		}
		return expr;
	}

	private IExpression skill(final String name) {
		return getFactory().createConst(name, Types.SKILL);
	}

	// KEEP
	private IExpression unary(final String op, final Expression e) {
		if (op == null) { return null; }
		final IExpression expr = compile(e);
		if (expr == null) { return null; }
		if (op.equals(MY)) {
			final IVarDescriptionProvider desc = getContext().getDescriptionDeclaringVar(MYSELF);
			if (desc instanceof IDescription) {
				// We are in a remote context, so 'my' refers to the calling
				// agent
				final IExpression myself = desc.getVarExpr(MYSELF, false);
				final IDescription species = myself.getType().getSpecies();
				final IExpression var = species.getVarExpr(EGaml.getKeyOf(e), true);
				return getFactory().createOperator(_DOT, (IDescription) desc, e, myself, var);
			}
			// Otherwise, we ignore 'my' since it refers to 'self'
			return expr;
		}
		// The unary "unit" operator should let the value of its child pass
		// through
		if (op.equals("°") || op.equals("#")) { return expr; }
		if (op.equals("every") && expr instanceof ConstantExpression && expr.getType() == Types.INT) {
			getContext().warning(
					"No unit provided. If this frequency concerns cycles, please use the #cycle unit. Otherwise use one of the temporal unit (#ms, #s, #mn, #h, #day, #week, #month, #year)",
					IGamlIssue.DEPRECATED, e);
		}
		if (isSpeciesName(
				op)) { return getFactory().createAs(getContext(), expr, getSpeciesContext(op).getSpeciesExpr()); }
		// if ( isSkillName(op) ) { return factory.createOperator(AS, context,
		// e, expr, skill(op)); }
		return getFactory().createOperator(op, getContext(), e, expr);
	}

	private IExpression casting(final String type, final IExpression toCast, final Expression typeObject) {
		if (toCast == null) { return null; }
		final IType castingType = currentTypesManager.get(type).typeIfCasting(toCast);

		final boolean isSuperType = castingType.isAssignableFrom(toCast.getType());
		TypeInfo typeInfo = null;
		if (typeObject instanceof TypeRef) {
			typeInfo = ((TypeRef) typeObject).getParameter();
		} else if (typeObject instanceof Function) {
			typeInfo = ((Function) typeObject).getType();
		}
		if (isSuperType && typeInfo == null) {
			getContext().info("Unneeded casting: '" + toCast.serialize(true) + "' is already of type " + type,
					IGamlIssue.UNUSED, typeObject);
			return toCast;
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
			if (ct != null && ct != Types.NO_TYPE) {
				contentsType = ct;
			}
			if (kt != null && kt != Types.NO_TYPE) {
				keyType = kt;
			}
		}
		final IType result = GamaType.from(castingType, keyType, contentsType);
		// If there is no casting to do, just return the expression unchanged.
		if (result.isAssignableFrom(toCast.getType())) {
			getContext().info("Unneeded casting: '" + toCast.serialize(true) + "' is already of type " + type,
					IGamlIssue.UNUSED, typeObject);
			return toCast;
		}

		return getFactory().createAs(getContext().getSpeciesContext(), toCast,
				getFactory().createTypeExpression(result));
	}

	IType fromTypeRef(final TypeRef object) {
		if (object == null) { return null; }
		String primary = EGaml.getKeyOf(object);

		if (primary == null) {
			primary = object.getRef().getName();
		} else if (primary.equals(SyntacticFactory.SPECIES_VAR)) {
			primary = SPECIES;
		}

		final IType t = currentTypesManager.get(primary);

		if (t == Types.NO_TYPE && !UNKNOWN.equals(primary)) {
			getContext().error(primary + " is not a valid type name", IGamlIssue.NOT_A_TYPE, object, primary);
			return t;
		}

		// case of model_alias<species>
		if (t.isAgentType() && t.getSpecies().isModel()) {
			final TypeInfo parameter = object.getParameter();
			if (parameter == null) { return t; }
			final TypeRef first = (TypeRef) parameter.getFirst();
			if (first == null) {
				return t;
			} else {
				final ITypesManager savedTypesManager = currentTypesManager;
				try {
					currentTypesManager = t.getSpecies().getModelDescription().getTypesManager();
					return fromTypeRef(first);
				} finally {
					currentTypesManager = savedTypesManager;
				}
			}
		}

		if (t.isAgentType()) { return t; }

		// /

		final TypeInfo parameter = object.getParameter();
		if (parameter == null || !t.isContainer()) { return t; }
		final TypeRef first = (TypeRef) parameter.getFirst();
		if (first == null) { return t; }
		final TypeRef second = (TypeRef) parameter.getSecond();
		if (second == null) { return GamaType.from(t, t.getKeyType(), fromTypeRef(first)); }
		return GamaType.from(t, fromTypeRef(first), fromTypeRef(second));
	}

	private IExpression binary(final String op, final IExpression left, final Expression originalExpression) {
		if (left == null) { return null; }
		Expression rightMember = originalExpression;
		// if the operator is "as", the right-hand expression should be a
		// casting type
		if (AS.equals(op)) { return binaryAs(left, rightMember); }
		// if the operator is "is", the right-hand expression should be a type
		if (IS.equals(op)) { return binaryIs(left, rightMember); }

		// we verify and compile apart the calls to actions as operators

		final TypeDescription sd = left.getType().getSpecies();
		if (sd != null) {
			final ActionDescription action = sd.getAction(op);
			if (action != null) {
				final IExpression result = action(op, left, rightMember, action);
				if (result != null) { return result; }
			}
		}
		// It is not an action, it must be an operator. We emit an error and
		// stop compiling if not
		if (!OPERATORS.containsKey(op)) {
			getContext().error("Unknown action or operator: " + op, IGamlIssue.UNKNOWN_ACTION, rightMember.eContainer(),
					op);
			return null;
		}

		// if the operator is an iterator, we must initialize the context
		// sensitive "each" variable
		final boolean isIterator = ITERATORS.contains(op);
		if (isIterator) {
			final IType t = left.getType().getContentType();
			final String argName = findIteratorArgName(rightMember);
			rightMember = findIteratorExpr(rightMember);
			iteratorContexts.push(new EachExpression(argName, t));
		}
		// If the right-hand expression is a list of expression, then we have a
		// n-ary operator
		if (rightMember instanceof ExpressionList) {
			final ExpressionList el = (ExpressionList) rightMember;
			final List<Expression> list = EGaml.getExprsOf(el);
			final int size = list.size();
			if (size > 1) {
				final IExpression[] compiledArgs = new IExpression[size + 1];
				compiledArgs[0] = left;
				for (int i = 0; i < size; i++) {
					compiledArgs[i + 1] = compile(list.get(i));
				}
				final IExpression result = getFactory().createOperator(op, getContext(), rightMember, compiledArgs);
				return result;
			}
		}

		// Otherwise we can now safely compile the right-hand expression
		final IExpression right = compile(rightMember);
		// We make sure to remove any mention of the each expression after the
		// right member has been compiled
		if (isIterator) {
			iteratorContexts.pop();
		}
		// and we return the operator expression
		return getFactory().createOperator(op, getContext(), originalExpression.eContainer(), left, right);

	}

	private String findIteratorArgName(final Expression e2) {
		if (!(e2 instanceof ExpressionList)) { return IKeyword.EACH; }
		final ExpressionList params = (ExpressionList) e2;
		final List<Expression> exprs = EGaml.getExprsOf(params);
		if (exprs == null || exprs.isEmpty()) { return IKeyword.EACH; }
		final Expression arg = exprs.get(0);
		if (!(arg instanceof Parameter)) { return IKeyword.EACH; }
		final Parameter p = (Parameter) arg;
		return EGaml.getKeyOf(p);
	}

	private Expression findIteratorExpr(final Expression e2) {
		if (!(e2 instanceof ExpressionList)) { return e2; }
		final ExpressionList params = (ExpressionList) e2;
		final List<Expression> exprs = EGaml.getExprsOf(params);
		if (exprs == null || exprs.isEmpty()) { return e2; }
		final Expression arg = exprs.get(0);
		if (!(arg instanceof Parameter)) { return arg; }
		final Parameter p = (Parameter) arg;
		return p.getRight();
	}

	private IExpression binaryIs(final IExpression left, final Expression e2) {
		final String type = EGaml.getKeyOf(e2);
		if (isTypeName(type)) { return getFactory().createOperator(IS, getContext(), e2.eContainer(), left,
				getFactory().createConst(type, Types.STRING)); }
		if (isSkillName(type)) { return getFactory().createOperator(IS_SKILL, getContext(), e2.eContainer(), left,
				getFactory().createConst(type, Types.SKILL)); }
		getContext().error("'is' must be followed by a type, species or skill name. " + type + " is neither of these.",
				IGamlIssue.NOT_A_TYPE, e2, type);
		return null;
	}

	private IExpression binaryAs(final IExpression left, final Expression e2) {
		final String type = EGaml.getKeyOf(e2);
		// if ( isSpeciesName(type) ) { return factory.createOperator(op,
		// context, e2, left, species(type)); }
		// if ( isSkillName(type) ) { return
		// factory.createOperator(AS_SKILL, context, e2, left, skill(type));
		// }
		if (isTypeName(type)) { return casting(type, left, e2); }
		getContext().error("'as' must be followed by a type, species or skill name. " + type + " is neither of these.",
				IGamlIssue.NOT_A_TYPE, e2, type);
		// if (isTypeName(type)) { return casting(type, left, e2); }
		return null;
	}

	private IExpression action(final String name, final IExpression callee, final EObject args,
			final ActionDescription action) {
		final Arguments arguments = parseArguments(action, args, getContext(), true);
		return getFactory().createAction(name, getContext(), action, callee, arguments);
	}

	// KEEP
	private IExpression binary(final String op, final Expression e1, final Expression right) {

		// if the expression is " var of agents ", we must compile it apart
		if (OF.equals(op)) { return compileFieldExpr(right, e1); }
		// we can now safely compile the left-hand expression
		final IExpression left = compile(e1);
		return binary(op, left, right);
	}

	private SpeciesDescription getSpeciesContext(final String e) {
		return getContext().getSpeciesDescription(e);
	}

	private boolean isSpeciesName(final String s) {
		final ModelDescription m = getContext().getModelDescription();
		if (m == null) {
			// can occur when building the kernel
			return false;
		}
		final SpeciesDescription sd = m.getSpeciesDescription(s);
		return sd != null && !(sd instanceof ExperimentDescription);
	}

	private boolean isSkillName(final String s) {
		return GamaSkillRegistry.INSTANCE.hasSkill(s);
	}

	private boolean isTypeName(final String s) {

		if (!currentTypesManager.containsType(s)) { return false; }
		final IType t = currentTypesManager.get(s);
		final SpeciesDescription sd = t.getSpecies();
		if (sd != null && sd.isExperiment()) { return false; }
		return true;
	}

	private IExpression compileNamedExperimentFieldExpr(final Expression leftExpr, final String name) {
		final IExpression owner = compile(leftExpr);
		if (owner == null) { return null; }
		final IType type = owner.getType();
		if (type.isParametricFormOf(Types.SPECIES)) {
			final SpeciesDescription sd = type.getContentType().getSpecies();
			if (sd instanceof ModelDescription) {
				final ModelDescription md = (ModelDescription) sd;
				if (md.hasExperiment(
						name)) { return getFactory().createConst(name, GamaType.from(md.getExperiment(name))); }
			}
		}
		getContext().error("Only experiments can be accessed using their plain name", IGamlIssue.UNKNOWN_FIELD);
		return null;
	}

	private IExpression compileFieldExpr(final Expression leftExpr, final Expression fieldExpr) {
		final IExpression owner = compile(leftExpr);
		if (owner == null) { return null; }
		final IType type = owner.getType();
		final TypeDescription species = type.getSpecies();
		// hqnghi 28-05-14 search input variable from model, not experiment
		if (type instanceof ParametricType && type.getType().id() == IType.SPECIES) {
			if (type.getContentType().getSpecies() instanceof ModelDescription) {
				final ModelDescription sd = (ModelDescription) type.getContentType().getSpecies();
				final String var = EGaml.getKeyOf(fieldExpr);
				if (sd.hasExperiment(
						var)) { return getFactory().createConst(var, GamaType.from(sd.getExperiment(var))); }
			}
		}
		// end-hqnghi
		if (species == null) {
			// It can only be a variable as 'actions' are not defined on simple
			// objects, except for matrices, where it
			// can also represent the dot product
			final String var = EGaml.getKeyOf(fieldExpr);
			final OperatorProto proto = type.getGetter(var);

			// Special case for matrices
			if (type.id() == IType.MATRIX && proto == null) { return binary(".", owner, fieldExpr); }

			if (proto == null) {
				getContext().error("Unknown field '" + var + "' for type " + type, IGamlIssue.UNKNOWN_FIELD, leftExpr,
						var, type.toString());
				return null;
			}
			final TypeFieldExpression expr = (TypeFieldExpression) proto.create(getContext(), fieldExpr, owner);
			if (getContext() != null) {
				getContext().document(fieldExpr, expr);
			}
			return expr;
		}
		// We are now dealing with an agent. In that case, it can be either an
		// attribute or an
		// action call
		if (fieldExpr instanceof VariableRef) {
			final String var = EGaml.getKeyOf(fieldExpr);
			IExpression expr = species.getVarExpr(var, true);
			if (expr == null) {
				if (species instanceof ModelDescription && ((ModelDescription) species).hasExperiment(var)) {
					final IType t = Types.get(IKeyword.SPECIES);
					expr = getFactory().createTypeExpression(GamaType.from(t, Types.INT, species.getTypeNamed(var)));
				} else if (species.getName().equals(IKeyword.PLATFORM) && GAMA.isInHeadLessMode()) {
					// Special case (see #2259 for headless validation of GUI preferences)
					return ((PlatformSpeciesDescription) species).getFakePrefExpression(var);
				} else {
					getContext().error("Unknown variable '" + var + "' for species " + species.getName(),
							IGamlIssue.UNKNOWN_VAR, fieldExpr.eContainer(), var, species.getName());
					return null;
				}
			}
			getContext().document(fieldExpr, expr);
			return getFactory().createOperator(_DOT, getContext(), fieldExpr, owner, expr);
		} else if (fieldExpr instanceof Function) {
			final String name = EGaml.getKeyOf(fieldExpr);
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

	private IDescription setContext(final IDescription context) {
		final IDescription previous = currentContext;
		currentContext = context == null ? GAML.getModelContext() : context;
		currentTypesManager = Types.builtInTypes;
		if (currentContext != null) {
			final ModelDescription md = currentContext.getModelDescription();
			if (md != null) {
				final ITypesManager tm = md.getTypesManager();
				if (tm != null) {
					currentTypesManager = tm;
				}
			}
		}
		return previous;
	}

	private IDescription getContext() {
		return currentContext;
	}

	private ValidationContext getValidationContext() {
		if (currentContext == null) { return null; }
		return currentContext.getValidationContext();
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseArguments(msi.gaml.descriptions.ExpressionDescription,
	 *      msi.gaml.descriptions.IDescription)
	 */
	@Override
	public Arguments parseArguments(final ActionDescription action, final EObject o, final IDescription command,
			final boolean compileArgValue) {
		if (o == null) { return null; }
		boolean completeArgs = false;
		List<Expression> parameters = null;
		if (o instanceof Array) {
			parameters = EGaml.getExprsOf(((Array) o).getExprs());
		} else if (o instanceof ExpressionList) {
			parameters = EGaml.getExprsOf(o);
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
				arg = EGaml.getKeyOf(exp);
				ed = builder.create(((ArgumentPair) exp).getRight()/* , errors */);
			} else if (exp instanceof Parameter) {
				arg = EGaml.getKeyOf(exp);
				ed = builder.create(((Parameter) exp).getRight()/* , errors */);
			} else if (exp instanceof BinaryOperator && "::".equals(EGaml.getKeyOf(exp))) {
				arg = EGaml.getKeyOf(((BinaryOperator) exp).getLeft());
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
			if (ed != null && compileArgValue) {
				ed.compile(command);
			}
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
		// final String s = EGaml.getKeyOf(object);
		return skill(EGaml.getKeyOf(object));
	}

	@Override
	public IExpression caseActionRef(final ActionRef object) {
		final String s = EGaml.getKeyOf(object);
		final SpeciesDescription sd = getContext().getSpeciesContext();
		ActionDescription ad = sd.getAction(s);
		if (ad == null) {
			ad = sd.getModelDescription().getAction(s);
			if (ad == null) {
				getContext().error("Unknown action", IGamlIssue.UNKNOWN_ACTION, object);
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
		// final IExpression result = binary(EGaml.getKeyOf(object), object.getLeft(), object.getRight());
		// return result;
	}

	@Override
	public IExpression caseVariableRef(final VariableRef object) {
		final String s = EGaml.getKeyOf(object);
		if (s == null) { return caseVarDefinition(object.getRef()); }
		return caseVar(s, object);
	}

	@Override
	public IExpression caseTypeRef(final TypeRef object) {
		final IType t = fromTypeRef(object);

		// / SEE IF IT WORKS

		// 2 erreurs :
		// - type inconnu n'est pas mentionné (electors ??)
		// - lors d'une affectation de nil warning sur le type (candidate)

		if (t.isAgentType()) { return t.getSpecies().getSpeciesExpr(); }
		return getFactory().createTypeExpression(t);
	}

	@Override
	public IExpression caseEquationRef(final EquationRef object) {
		return getFactory().createConst(EGaml.getKeyOf(object), Types.STRING);
	}

	@Override
	public IExpression caseUnitName(final UnitName object) {
		final String s = EGaml.getKeyOf(object);
		if (IUnits.UNITS_EXPR.containsKey(s)) {
			final UnitConstantExpression exp = getFactory().getUnitExpr(s);
			if (exp.isDeprecated()) {
				getContext().warning(s + " is deprecated.", IGamlIssue.NOT_A_UNIT, object, (String[]) null);
			}
			return exp;
		}
		getContext().error(s + " is not a unit name.", IGamlIssue.NOT_A_UNIT, object, (String[]) null);
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
		return caseVar(EGaml.getKeyOf(object), object);
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
		final IExpression result = binary("::", caseVar(EGaml.getKeyOf(object), object), object.getRight());
		return result;
	}

	@Override
	public IExpression caseUnit(final Unit object) {
		// We simply return a multiplication, since the right member (the
		// "unit") will be
		// translated into its float value

		// Case of dates: #month and #year
		final String name = EGaml.toString(object.getRight());
		if (TimeUnitConstantExpression.UNCOMPUTABLE_DURATIONS.contains(
				name)) { return binary(Dates.APPROXIMATE_TEMPORAL_QUERY, object.getLeft(), object.getRight()); }
		// AD: Hack to address Issue 387. If the unit is a pixel, we add +1 to
		// the whole expression.
		// final IExpression right = compile(object.getRight());
		final IExpression result = binary("*", object.getLeft(), object.getRight());
		// AD: removal of the hack to address #1325 -- needs to be tested in
		// OpenGL
		// if ( result != null && ((BinaryOperator) result).arg(1) instanceof
		// PixelUnitExpression ) {
		// result = factory.createOperator("+", getContext(), object,
		// factory.createConst(1, Types.INT), result);
		// }
		return result;
	}

	@Override
	public IExpression caseUnary(final Unary object) {
		return unary(EGaml.getKeyOf(object), object.getRight());
	}

	public IExpression caseDot(final Access object) {
		final Expression right = (object.getRight());
		if (right instanceof StringLiteral) {
			return compileNamedExperimentFieldExpr(object.getLeft(), EGaml.getKeyOf(right));
		} else if (right != null) { return compileFieldExpr(object.getLeft(), object.getRight()); }
		return null;
	}

	@Override
	public IExpression caseAccess(final Access object) {
		if (object.getOp().equals(".")) { return caseDot(object); }
		final IExpression container = compile(object.getLeft());
		// If no container is defined, return a null expression
		if (container == null) { return null; }
		final IType contType = container.getType();
		final boolean isMatrix = contType.id() == IType.MATRIX;
		final IType keyType = contType.getKeyType();
		final List<? extends Expression> list = EGaml.getExprsOf(object.getRight());
		final List<IExpression> result = new ArrayList<>();
		final int size = list.size();
		for (int i = 0; i < size; i++) {
			final Expression eExpr = list.get(i);
			final IExpression e = compile(eExpr);
			if (e != null) {
				final IType elementType = e.getType();
				if (keyType != Types.NO_TYPE && !keyType.isAssignableFrom(e.getType())) {
					if (!(isMatrix && elementType.id() == IType.INT && size > 1)) {
						getContext().warning("a " + contType.toString() + " cannot be accessed using a "
								+ elementType.toString() + " index", IGamlIssue.WRONG_TYPE, eExpr);
					}
				}
				result.add(e);
			}
		}
		if (size > 2) {
			final String end = !isMatrix ? " only 1 index" : " 1 or 2 indices";
			getContext().warning("a " + contType.toString() + " should be accessed using" + end,
					IGamlIssue.DIFFERENT_ARGUMENTS, object);
		}

		final IExpression indices = getFactory().createList(result);

		IVarExpression varDiff = null;
		if (container instanceof IVarExpression.Agent && ((IVarExpression.Agent) container).getOwner() != null) {
			varDiff = ((IVarExpression.Agent) container).getVar();

			final SpeciesDescription species =
					((IVarExpression.Agent) varDiff).getDefinitionDescription().getSpeciesContext();
			if (species != null) {
				final Iterable<IDescription> equations = species.getChildrenWithKeyword(IKeyword.EQUATION);
				for (final IDescription equation : equations) {
					if (equation.manipulatesVar(
							varDiff.getName())) { return getFactory().createOperator("internal_integrated_value",
									getContext(), object, ((IVarExpression.Agent) container).getOwner(), varDiff); }

				}
			}
		}

		return getFactory().createOperator("internal_at", getContext(), object, container, indices);
	}

	@Override
	public IExpression caseArray(final Array object) {
		final List<? extends Expression> list = EGaml.getExprsOf(object.getExprs());
		final boolean allPairs = !list.isEmpty() && Iterables.all(list, each -> "::".equals(EGaml.getKeyOf(each)));
		final Iterable<IExpression> result = Iterables.transform(list, input -> compile(input));
		return allPairs ? getFactory().createMap(result) : getFactory().createList(result);
	}

	@Override
	public IExpression casePoint(final Point object) {
		final Expression z = object.getZ();
		if (z == null) { return binary(POINT, object.getLeft(), object.getRight()); }
		final IExpression[] exprs = new IExpression[3];
		exprs[0] = compile(object.getLeft());
		exprs[1] = compile(object.getRight());
		exprs[2] = compile(z);
		return getFactory().createOperator(POINT, getContext(), object, exprs);
	}
	//
	// @Override
	// public IExpression caseParameters(final Parameters object) {
	// final Iterable it = Iterables.transform(EGaml.getExprsOf(object.getParams()), input -> binary("::",
	// getFactory().createConst(EGaml.getKeyOf(input.getLeft()), Types.STRING), input.getRight()));
	// return getFactory().createMap(it);
	// }

	@Override
	public IExpression caseExpressionList(final ExpressionList object) {
		final List<Expression> list = EGaml.getExprsOf(object);
		if (list.isEmpty()) { return null; }
		if (list.size() > 1) {
			getContext().warning(
					"A sequence of expressions is not expected here. Only the first expression will be evaluated",
					IGamlIssue.UNKNOWN_ARGUMENT, object);
		}
		final IExpression expr = compile(list.get(0));
		return expr;
	}

	@Override
	public IExpression caseFunction(final Function object) {
		final String op = EGaml.getKeyOf(object);
		IExpression result = tryCastingFunction(op, object);
		if (result != null) { return result; }
		result = tryActionCall(op, object);
		if (result != null) { return result; }
		final List<Expression> args = EGaml.getExprsOf(object.getRight());
		switch (args.size()) {
			case 0:
				getContext().error("Unknown operator or action: " + op, IGamlIssue.UNKNOWN_ACTION, object);
				return null;
			case 1:
				return unary(op, args.get(0));
			case 2:
				return binary(op, args.get(0), args.get(1));
			default:
				return getFactory().createOperator(op, getContext(), object,
						toArray(transform(args, a -> compile(a)), IExpression.class));
		}
	}

	private IExpression tryCastingFunction(final String op, final Function object) {
		if (!isCastingFunction(op, object)) { return null; }
		final List<Expression> args = EGaml.getExprsOf(object.getRight());
		final int size = args.size();
		IExpression toCast;
		if (size == 1) {
			toCast = compile(args.get(0));
		} else {
			toCast = getFactory().createList((transform(args, a -> compile(a))));
		}
		return binaryAs(toCast, object);
	}

	private IExpression tryActionCall(final String op, final Function object) {
		final SpeciesDescription sd = getContext().getSpeciesContext();
		if (sd == null) { return null; }
		final boolean isSuper = getContext() instanceof StatementDescription
				&& ((StatementDescription) getContext()).isSuperInvocation();
		final ActionDescription action = isSuper ? sd.getParent().getAction(op) : sd.getAction(op);
		if (action == null) { return null; }
		final EObject params = object.getRight();
		return action(op, caseVar(isSuper ? SUPER : SELF, object), params, action);
	}

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
		final List<Expression> args = EGaml.getExprsOf(object.getRight());
		final int size = args.size();
		// If there is none, it can't be a casting
		if (size == 0) { return false; }
		// If there is one, we match
		if (size == 1) { return true; }
		// If more than one, we need to check if there are operators that match. If yes, we return false
		return !getFactory().hasOperator(op, getContext(), object,
				toArray(transform(args, a -> compile(a)), IExpression.class));
	}

	@Override
	public IExpression caseIntLiteral(final IntLiteral object) {
		try {
			final Integer val = Integer.parseInt(EGaml.getKeyOf(object), 10);
			return getFactory().createConst(val, Types.INT);
		} catch (final NumberFormatException e) {
			getContext().error("Malformed integer: " + EGaml.getKeyOf(object), IGamlIssue.UNKNOWN_NUMBER, object);
			return null;
		}
	}

	@Override
	public IExpression caseDoubleLiteral(final DoubleLiteral object) {

		String s = EGaml.getKeyOf(object);

		if (s == null) { return null; }
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
	public IExpression caseColorLiteral(final ColorLiteral object) {
		try {
			final Integer val = Integer.parseInt(EGaml.getKeyOf(object).substring(1), 16);
			return getFactory().createConst(val, Types.INT);
		} catch (final NumberFormatException e) {
			getContext().error("Malformed integer: " + EGaml.getKeyOf(object), IGamlIssue.UNKNOWN_NUMBER, object);
			return null;
		}
	}

	@Override
	public IExpression caseStringLiteral(final StringLiteral object) {
		return getFactory().createConst(StringUtils.unescapeJava(EGaml.getKeyOf(object)), Types.STRING);
	}

	@Override
	public IExpression caseBooleanLiteral(final BooleanLiteral object) {
		final String s = EGaml.getKeyOf(object);
		if (s == null) { return null; }
		return s.equalsIgnoreCase(TRUE) ? TRUE_EXPR : FALSE_EXPR;
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
				final IType tt = temp_sd.getType();
				return getFactory().createVar(SELF, tt, true, IVarExpression.SELF, null);
			case SUPER:
				SpeciesDescription sd = getContext().getSpeciesContext();
				if (sd != null) {
					sd = sd.getParent();
				}
				if (sd == null) {
					getContext().error("Unable to determine the species of super", IGamlIssue.GENERAL, object);
					return null;
				}
				final IType t = sd.getType();
				return getFactory().createVar(SUPER, t, true, IVarExpression.SUPER, null);

			// case WORLD_AGENT_NAME:
			// return getWorldExpr();
		}

		// check if the var has been declared in an iterator context
		for (final IVarExpression it : iteratorContexts) {
			if (it.getName().equals(varName)) { return it; }
		}

		if (isSpeciesName(varName)) {
			final SpeciesDescription sd = getSpeciesContext(varName);
			return sd == null ? null : sd.getSpeciesExpr();
		}
		final IVarDescriptionProvider temp_sd =
				getContext() == null ? null : getContext().getDescriptionDeclaringVar(varName);

		if (temp_sd != null) {
			if (temp_sd instanceof SpeciesDescription) {
				final SpeciesDescription remote_sd = getContext().getSpeciesContext();
				if (remote_sd != null) {
					final SpeciesDescription found_sd = (SpeciesDescription) temp_sd;

					if (remote_sd != temp_sd && !remote_sd.isBuiltIn() && !remote_sd.hasMacroSpecies(found_sd)) {
						getContext().error(
								"The variable " + varName + " is not accessible in this context (" + remote_sd.getName()
										+ "), but in the context of " + found_sd.getName()
										+ ". It should be preceded by 'myself.'",
								IGamlIssue.UNKNOWN_VAR, object, varName);
					}
				}
			}

			return temp_sd.getVarExpr(varName, false);
		}

		if (isTypeName(varName)) { return getFactory().createTypeExpression(currentTypesManager.get(varName)); }

		if (isSkillName(varName)) { return skill(varName); }

		if (getContext() != null) {

			// Finally, a last possibility (enabled in rare occasions, like in
			// the "elevation" facet of grid layers), is that the variable
			// belongs to the species denoted by the
			// current statement
			if (getContext() instanceof StatementDescription) {
				final SpeciesDescription denotedSpecies = getContext().getType().getDenotedSpecies();
				if (denotedSpecies != null) {
					if (denotedSpecies.hasAttribute(varName)) { return denotedSpecies.getVarExpr(varName, false); }
				}
			}

			// An experimental possibility is that the variable refers to a
			// an action (used like a variable, see Issue 853) or also any
			// behavior or aspect
			final SpeciesDescription sd = getContext().getSpeciesContext();
			if (sd.hasAction(varName, false)) { return new DenotedActionExpression(sd.getAction(varName)); }
			if (sd.hasBehavior(varName)) { return new DenotedActionExpression(sd.getBehavior(varName)); }
			if (sd.hasAspect(varName)) { return new DenotedActionExpression(sd.getAspect(varName)); }

			getContext().error(
					"The variable " + varName
							+ " is not defined or accessible in this context. Check its name or declare it",
					IGamlIssue.UNKNOWN_VAR, object, varName);
		}
		return null;

	}

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

			if (!resource.hasErrors()) {
				final EObject e = resource.getContents().get(0);
				if (e instanceof StringEvaluator) {
					result = ((StringEvaluator) e).getExpr();
				}
			} else {
				final Resource.Diagnostic d = resource.getErrors().get(0);
				throw GamaRuntimeException.error(d.getMessage(), tempContext.getScope());
			}

			return result;
		} finally {
			GamlResourceServices.discardTemporaryResource(resource);
		}
	}

	@Override
	public List<IDescription> compileBlock(final String string, final IDescription actionContext,
			final IExecutionContext tempContext) throws GamaRuntimeException {
		final List<IDescription> result = new ArrayList<>();
		final String s = "__synthetic__ {" + string + "}";
		final GamlResource resource = GamlResourceServices.getTemporaryResource(getContext());
		try {
			final InputStream is = new ByteArrayInputStream(s.getBytes());
			try {
				resource.loadSynthetic(is, tempContext);
			} catch (final Exception e1) {
				e1.printStackTrace();
				return null;
			} finally {}
			if (!resource.hasErrors()) {
				final SyntacticModelElement elt = (SyntacticModelElement) resource.getSyntacticContents();
				// We have a problem -- can be simply an empty block or an expression
				if (!elt.hasChildren()) {
					if (elt.hasFacet(IKeyword.FUNCTION)) {
						// Compile the expression is the best way to know if this expression is correct
						elt.getExpressionAt(IKeyword.FUNCTION).compile(actionContext);
					}
				}
				elt.visitChildren(e -> {
					final IDescription desc = DescriptionFactory.create(e, actionContext, null);
					result.add(desc);
				});

			} else {
				final Resource.Diagnostic d = resource.getErrors().get(0);
				throw GamaRuntimeException.error(d.getMessage(), tempContext.getScope());
			}
			return result;
		} finally {
			GamlResourceServices.discardTemporaryResource(resource);
		}
	}

	/**
	 * Method getFacetExpression()
	 * 
	 * @see msi.gaml.expressions.IExpressionCompiler#getFacetExpression(msi.gaml.descriptions.IDescription,
	 *      java.lang.Object)
	 */
	@Override
	public EObject getFacetExpression(final IDescription context, final EObject target) {
		if (target.eContainer() instanceof Facet) { return target.eContainer(); }
		return target;
	}

	//
	// end-hqnghi
	//

	public IVarExpression getEachExpr(final EObject object) {
		final IVarExpression p = iteratorContexts.peek();
		if (p == null) {
			getContext().error("'each' is not accessible in this context", IGamlIssue.UNKNOWN_VAR, object);
			return null;
		}
		return p;
	}

	private IExpressionDescription getCurrentExpressionDescription() {
		return currentExpressionDescription;
	}

	private void setCurrentExpressionDescription(final IExpressionDescription currentExpressionDescription) {
		this.currentExpressionDescription = currentExpressionDescription;
	}

	private IExpressionFactory getFactory() {
		return GAML.getExpressionFactory();
	}

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
