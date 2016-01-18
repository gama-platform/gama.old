/*********************************************************************************************
 *
 *
 * 'GamlExpressionCompiler.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.utils;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.expressions.IExpressionFactory.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.resource.XtextResourceSet;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.gaml.resource.*;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.*;
import msi.gaml.types.*;

/**
 * The Class ExpressionParser.
 */

public class GamlExpressionCompiler extends GamlSwitch<IExpression> implements IExpressionCompiler<Expression> {

	private final Deque<IVarExpression> iteratorContexts = new LinkedList();
	private IExpression world;
	// To disable reentrant parsing (Issue 782)
	private IExpressionDescription currentExpressionDescription;
	private IDescription context;
	private final IExpressionFactory factory;
	private GamlResource resource;
	private final static Map<String, IExpression> cache = new TOrderedHashMap();

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via simulation.getModel().getDescription()) if it is
	 * available. Otherwise, only simple expressions (that contain constants) can be parsed.
	 */

	// private static boolean synthetic;

	static {
		IExpressionCompiler.OPERATORS.put(MY, new TOrderedHashMap());
	}

	public GamlExpressionCompiler() {
		factory = GAML.getExpressionFactory();
	}

	@Override
	public void reset() {
		world = null;
		iteratorContexts.clear();
		setCurrentExpressionDescription(null);
		// synthetic = false;
	}

	@Override
	public IExpression compile(final IExpressionDescription s, final IDescription parsingContext) {
		// Cf. Issue 782. Returns null if an expression needs its compiled version to be compiled.
		if ( s.isConstant() || s == getCurrentExpressionDescription() ) { return s.getExpression(); }
		setCurrentExpressionDescription(s);
		boolean synthetic = false;
		EObject o = s.getTarget();
		if ( o == null && s instanceof StringBasedExpressionDescription ) {
			synthetic = true;
			String expString = s.toString();
			IExpression result = cache.get(expString);
			if ( result != null ) { return result; }
			o = getEObjectOf(expString);
		}
		EObject e = o;
		IDescription previous = setContext(parsingContext);
		try {
			IExpression result = compile(e);
			if ( synthetic ) {
				cache.put(s.toString(), result);
			}
			return result;
		} finally {
			setContext(previous);
			setCurrentExpressionDescription(null);

			// synthetic = false;
		}

	}

	private IExpression compile(final EObject s) {
		if ( s == null ) {
			// No error, since the null expressions come from previous (more focused)
			// errors and not from the parser itself.
			return null;
		}
		IExpression expr = doSwitch(s);
		// if ( !synthetic ) {
		if ( context != null && context.isDocumenting() ) {
			DescriptionFactory.setGamlDocumentation(s, expr);
		}
		// }
		return expr;
	}

	private IExpression skill(final String name) {
		return factory.createConst(name, Types.SKILL);
	}

	// KEEP
	private IExpression unary(final String op, final Expression e) {
		if ( op == null ) { return null; }
		IExpression expr = compile(e);
		if ( expr == null ) { return null; }
		if ( op.equals(MY) ) {
			IDescription desc = getContext().getDescriptionDeclaringVar(MYSELF);
			if ( desc != null ) {
				// We are in a remote context, so 'my' refers to the calling agent
				IExpression myself = desc.getVarExpr(MYSELF);
				IDescription species = myself.getType().getSpecies();
				IExpression var = species.getVarExpr(EGaml.getKeyOf(e));
				return factory.createOperator(_DOT, desc, e, myself, var);
			}
			// Otherwise, we ignore 'my' since it refers to 'self'
			return expr;
		}
		// The unary "unit" operator should let the value of its child pass through
		if ( op.equals("°") || op.equals("#") ) { return expr; }
		if ( isSpeciesName(
			op) ) { return factory.createOperator(AS, getContext(), e, expr, getSpeciesContext(op).getSpeciesExpr()); }
		// if ( isSkillName(op) ) { return factory.createOperator(AS, context, e, expr, skill(op)); }
		return factory.createOperator(op, getContext(), e, expr);
	}

	private IExpression casting(final String type, final IExpression toCast, final Expression typeObject) {
		if ( toCast == null ) { return null; }
		IType castingType = getContext().getModelDescription().getTypeNamed(type).typeIfCasting(toCast);

		boolean isSuperType = castingType.isAssignableFrom(toCast.getType());
		TypeInfo typeInfo = null;
		if ( typeObject instanceof TypeRef ) {
			typeInfo = ((TypeRef) typeObject).getParameter();
		} else if ( typeObject instanceof Function ) {
			typeInfo = ((Function) typeObject).getType();
		}
		if ( isSuperType && typeInfo == null ) {
			getContext().info("Unneeded casting: '" + toCast.serialize(true) + "' is already of type " + type,
				IGamlIssue.UNUSED, typeObject);
			return toCast;
		}
		IType keyType = castingType.getKeyType();
		IType contentsType = castingType.getContentType();
		if ( typeInfo != null ) {
			IType kt = fromTypeRef((TypeRef) typeInfo.getFirst());
			IType ct = fromTypeRef((TypeRef) typeInfo.getSecond());
			if ( ct == null || ct == Types.NO_TYPE ) {
				ct = kt;
				kt = null;
			}
			if ( ct != null && ct != Types.NO_TYPE ) {
				contentsType = ct;
			}
			if ( kt != null && kt != Types.NO_TYPE ) {
				keyType = kt;
			}
		}
		IType result = GamaType.from(castingType, keyType, contentsType);
		// If there is no casting to do, just return the expression unchanged.
		if ( result.isAssignableFrom(toCast.getType()) ) {
			getContext().info("Unneeded casting: '" + toCast.serialize(true) + "' is already of type " + type,
				IGamlIssue.UNUSED, typeObject);
			return toCast;
		}

		return factory.createOperator(AS, getContext().getSpeciesContext(), typeObject, toCast,
			factory.createTypeExpression(result));
	}

	IType fromTypeRef(final TypeRef object) {
		if ( object == null ) { return null; }
		String primary = EGaml.getKeyOf(object);

		if ( primary == null ) {
			primary = object.getRef().getName();
		} else if ( primary.equals(SyntacticFactory.SPECIES_VAR) ) {
			primary = SPECIES;
		}

		IType t = getContext().getTypeNamed(primary);

		if ( t == Types.NO_TYPE && !UNKNOWN.equals(primary) && !SIGNAL.equals(primary) ) {
			getContext().error(primary + " is not a valid type name", IGamlIssue.NOT_A_TYPE, object, primary);
			return t;
		}

		if ( t.isAgentType() ) { return t; }

		// /

		TypeInfo parameter = object.getParameter();
		if ( parameter == null || !t.isContainer() ) { return t; }
		TypeRef first = (TypeRef) parameter.getFirst();
		if ( first == null ) { return t; }
		TypeRef second = (TypeRef) parameter.getSecond();
		if ( second == null ) { return GamaType.from(t, t.getKeyType(), fromTypeRef(first)); }
		return GamaType.from(t, fromTypeRef(first), fromTypeRef(second));
	}

	private IExpression binary(final String op, final IExpression left, final Expression e2) {
		if ( left == null ) { return null; }
		// if the operator is "as", the right-hand expression should be a casting type
		if ( AS.equals(op) ) {

			String type = EGaml.getKeyOf(e2);
			// if ( isSpeciesName(type) ) { return factory.createOperator(op, context, e2, left, species(type)); }
			// if ( isSkillName(type) ) { return factory.createOperator(AS_SKILL, context, e2, left, skill(type)); }
			if ( isTypeName(type) ) { return casting(type, left, e2); }
			getContext().error(
				"'as' must be followed by a type, species or skill name. " + type + " is neither of these.",
				IGamlIssue.NOT_A_TYPE, e2, type);
			return null;
		}
		// if the operator is "is", the right-hand expression should be a type
		if ( IS.equals(op) ) {
			String type = EGaml.getKeyOf(e2);
			if ( isTypeName(type) ) { return factory.createOperator(op, getContext(), e2.eContainer(), left,
				factory.createConst(type, Types.STRING)); }
			if ( isSkillName(type) ) { return factory.createOperator(IS_SKILL, getContext(), e2.eContainer(), left,
				factory.createConst(type, Types.SKILL)); }
			getContext().error(
				"'is' must be followed by a type, species or skill name. " + type + " is neither of these.",
				IGamlIssue.NOT_A_TYPE, e2, type);
			return null;
		}

		// we verify and compile apart the calls to actions as operators
		// TypeDescription sd = getContext().getSpeciesDescription(left.getType().getSpeciesName());
		TypeDescription sd = left.getType().getSpecies();
		if ( sd != null ) {
			StatementDescription action = sd.getAction(op);
			if ( action != null ) {
				IExpression result = action(op, left, e2, action);
				if ( result != null ) { return result; }
			}
		}
		// It is not an description so it must be an operator. We emit an error and stop compiling if it
		// is not
		if ( !OPERATORS.containsKey(op) ) {
			getContext().error("Unknown action or operator: " + op, IGamlIssue.UNKNOWN_ACTION, e2.eContainer(), op);
			return null;
		}

		// if the operator is an iterator, we must initialize the context sensitive "each" variable
		if ( ITERATORS.contains(op) ) {
			IType t = left.getType().getContentType();
			setEach_Expr(op, new EachExpression(t /* ct, kt */));
		}
		// If the right-hand expression is a list of expression, then we have a n-ary operator
		if ( e2 instanceof ExpressionList ) {
			ExpressionList el = (ExpressionList) e2;
			List<Expression> list = EGaml.getExprsOf(el);
			int size = list.size();
			if ( size > 1 ) {
				IExpression[] compiledArgs = new IExpression[size + 1];
				compiledArgs[0] = left;
				for ( int i = 0; i < size; i++ ) {
					compiledArgs[i + 1] = compile(list.get(i));
				}
				IExpression result = factory.createOperator(op, getContext(), e2, compiledArgs);
				return result;
			}
		}

		// Otherwise we can now safely compile the right-hand expression
		IExpression right = compile(e2);
		// We make sure to remove any mention of the each expression after the right member has been compiled
		if ( ITERATORS.contains(op) ) {
			iteratorContexts.pop();
		}
		// and return the binary expression
		return factory.createOperator(op, getContext(), e2.eContainer(), left, right);

	}

	private IExpression action(final String name, final IExpression callee, final EObject args,
		final StatementDescription action) {
		Arguments arguments = parseArguments(action, args, getContext(), true);
		return factory.createAction(name, getContext(), action, callee, arguments);
	}

	// KEEP
	private IExpression binary(final String op, final Expression e1, final Expression right) {

		// if the expression is " var of agents ", we must compile it apart
		if ( OF.equals(op) ) { return compileFieldExpr(right, e1); }
		// we can now safely compile the left-hand expression
		IExpression left = compile(e1);
		return binary(op, left, right);
	}

	private SpeciesDescription getSpeciesContext(final String e) {
		return getContext().getSpeciesDescription(e);
	}

	private boolean isSpeciesName(final String s) {
		ModelDescription m = getContext().getModelDescription();
		SpeciesDescription sd = m.getSpeciesDescription(s);
		return sd != null && !(sd instanceof ExperimentDescription);
	}

	private boolean isSkillName(final String s) {
		return AbstractGamlAdditions.getSkillClasses().containsKey(s);
	}

	private boolean isTypeName(final String s) {
		TypesManager tm = getContext().getModelDescription().getTypesManager();
		if ( tm == null ) {
			tm = Types.builtInTypes;
		}
		if ( !tm.containsType(s) ) { return false; }
		IType t = tm.get(s);
		SpeciesDescription sd = t.getSpecies();
		if ( sd != null && sd.isExperiment() ) { return false; }
		return true;
	}

	private IExpression compileFieldExpr(final Expression leftExpr, final Expression fieldExpr) {
		IExpression owner = compile(leftExpr);
		if ( owner == null ) { return null; }
		IType type = owner.getType();
		TypeDescription species = type.getSpecies();
		// hqnghi 28-05-14 search input variable from model, not experiment
		if ( type instanceof ParametricType && type.getType().id() == IType.SPECIES ) {
			if ( type.getContentType().getSpecies() instanceof ModelDescription ) {
				ModelDescription sd = (ModelDescription) type.getContentType().getSpecies();
				String var = EGaml.getKeyOf(fieldExpr);
				if ( sd.hasExperiment(var) ) { return factory.createConst(var, GamaType.from(sd.getExperiment(var))); }
			}
		}
		// end-hqnghi
		if ( species == null ) {
			// It can only be a variable as 'actions' are not defined on simple objects, except for matrices, where it
			// can also represent the dot product
			String var = EGaml.getKeyOf(fieldExpr);
			OperatorProto proto = type.getGetter(var);

			// Special case for matrices
			if ( type.id() == IType.MATRIX && proto == null ) { return binary(".", owner, fieldExpr); }

			if ( proto == null ) {
				species = type.getSpecies();
				getContext().error("Field " + var + " unknown for type " + type, IGamlIssue.UNKNOWN_FIELD, leftExpr,
					var, type.toString());
				return null;
			}
			TypeFieldExpression expr = (TypeFieldExpression) proto.create(getContext(), owner);
			DescriptionFactory.setGamlDocumentation(fieldExpr, expr);
			return expr;
		}
		// We are now dealing with an agent. In that case, it can be either an attribute or an
		// action call
		if ( fieldExpr instanceof VariableRef ) {
			String var = EGaml.getKeyOf(fieldExpr);
			IVarExpression expr = (IVarExpression) species.getVarExpr(var);
			if ( expr == null ) {
				if ( species instanceof ModelDescription && ((ModelDescription) species).hasExperiment(var) ) {
					IType t = Types.get(IKeyword.SPECIES);

					return factory.createTypeExpression(GamaType.from(t, Types.INT, species.getTypeNamed(var)));
				}
				// else

				getContext().error("Unknown variable: '" + var + "' in " + species.getName(), IGamlIssue.UNKNOWN_VAR,
					leftExpr, var, species.getName());
			}
			DescriptionFactory.setGamlDocumentation(fieldExpr, expr);
			return factory.createOperator(_DOT, getContext(), fieldExpr, owner, expr);
		} else if ( fieldExpr instanceof Function ) {
			String name = EGaml.getKeyOf(fieldExpr);
			StatementDescription action = species.getAction(name);
			if ( action != null ) {
				ExpressionList list = ((Function) fieldExpr).getArgs();
				IExpression call =
					action(name, owner, list == null ? ((Function) fieldExpr).getParameters() : list, action);
				DescriptionFactory.setGamlDocumentation(fieldExpr, call); // ??
				return call;
			}
		}
		return null;

	}

	// KEEP
	private IExpression getWorldExpr() {
		if ( world == null ) {
			IType tt = getContext().getModelDescription()./* getWorldSpecies(). */getType();
			world =
				factory.createVar(WORLD_AGENT_NAME, tt, true, IVarExpression.WORLD, getContext().getModelDescription());
		}
		return world;
	}

	private IDescription setContext(final IDescription context) {
		// scope.getGui().debug("GamlExpressionCompiler.setContext : Replacing " + );
		IDescription previous = this.context;
		this.context = context;
		return previous;
	}

	private IDescription getContext() {
		if ( context == null ) { return GAML.getModelContext(); }
		return context;
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseArguments(msi.gaml.descriptions.ExpressionDescription, msi.gaml.descriptions.IDescription)
	 */
	@Override
	public Arguments parseArguments(final StatementDescription action, final EObject o, final IDescription command,
		final boolean compileArgValue) {
		if ( o == null ) { return new Arguments(); }
		boolean completeArgs = false;
		List<Expression> parameters = null;
		if ( o instanceof Array ) {
			parameters = EGaml.getExprsOf(((Array) o).getExprs());
		} else if ( o instanceof Parameters ) {
			parameters = EGaml.getExprsOf(((Parameters) o).getParams());
		} else if ( o instanceof ExpressionList ) {
			parameters = ((ExpressionList) o).getExprs();
			completeArgs = true;
		} else {
			command.error("Arguments must be written [a1::v1, a2::v2], (a1:v1, a2:v2) or (v1, v2)");
			return new Arguments();
		}
		Arguments argMap = new Arguments();
		List<String> args = action == null ? null : action.getArgNames();

		int index = 0;
		for ( Expression exp : parameters ) {
			String arg = null;
			IExpressionDescription ed = null;
			Set<Diagnostic> errors = new LinkedHashSet();
			if ( exp instanceof ArgumentPair || exp instanceof Parameter ) {
				arg = EGaml.getKeyOf(exp);
				ed = EcoreBasedExpressionDescription.create(exp.getRight(), errors);
			} else if ( exp instanceof Pair ) {
				arg = EGaml.getKeyOf(exp.getLeft());
				ed = EcoreBasedExpressionDescription.create(exp.getRight(), errors);
			} else if ( completeArgs ) {
				if ( args != null && action != null && index == args.size() ) {
					command.error("Wrong number of arguments. Action " + action.getName() + " expects " + args);
					return argMap;
				}
				arg = args == null ? String.valueOf(index++) : args.get(index++);
				ed = EcoreBasedExpressionDescription.create(exp, errors);

			}
			if ( ed != null && compileArgValue ) {
				ed.compile(command);
			}
			if ( !errors.isEmpty() ) {
				for ( Diagnostic d : errors ) {
					getContext().warning(d.getMessage(), "", exp);
				}
			}
			argMap.put(arg, ed);
		}
		return argMap;
	}

	@Override
	public IExpression caseCast(final Cast object) {
		return binary(AS, object.getLeft(), object.getRight());
	}

	@Override
	public IExpression caseSkillRef(final SkillRef object) {
		return skill(EGaml.getKey.caseSkillRef(object));
	}

	@Override
	public IExpression caseActionRef(final ActionRef object) {
		return factory.createConst(EGaml.getKey.caseActionRef(object), Types.STRING);
	}

	@Override
	public IExpression caseExpression(final Expression object) {
		// in the general case, we try to return a binary expression
		IExpression result = binary(EGaml.getKeyOf(object), object.getLeft(), object.getRight());
		return result;
	}

	@Override
	public IExpression caseVariableRef(final VariableRef object) {
		String s = EGaml.getKey.caseVariableRef(object);
		if ( s == null ) { return caseVarDefinition(object.getRef()); }
		return caseVar(s, object);
	}

	@Override
	public IExpression caseTypeRef(final TypeRef object) {
		IType t = fromTypeRef(object);

		// / SEE IF IT WORKS

		// 2 erreurs :
		// - type inconnu n'est pas mentionné (electors ??)
		// - lors d'une affectation de nil warning sur le type (candidate)

		if ( t.isAgentType() ) { return t.getSpecies()
			.getSpeciesExpr(); /* return factory.createSpeciesConstant(GamaType.from(t.getSpecies())); */ }
		return factory.createTypeExpression(t);
	}

	//
	// @Override
	// public IExpression caseSpeciesRef(final SpeciesRef object) {
	// IType t = fromSpeciesRef(object);
	// return factory.createTypeExpression(t);
	// }

	@Override
	public IExpression caseEquationRef(final EquationRef object) {
		return factory.createConst(EGaml.getKey.caseEquationRef(object), Types.STRING);
	}

	@Override
	public IExpression caseUnitName(final UnitName object) {
		String s = EGaml.getKeyOf(object);
		if ( IExpressionFactory.UNITS_EXPR.containsKey(s) ) { return factory.getUnitExpr(s); }
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
		IExpression ifFalse = compile(object.getIfFalse());
		IExpression alt = factory.createOperator(":", getContext(), object, compile(object.getRight()), ifFalse);
		return factory.createOperator("?", getContext(), object, compile(object.getLeft()), alt);
	}

	@Override
	public IExpression caseArgumentPair(final ArgumentPair object) {
		IExpression result = binary("::", caseVar(EGaml.getKeyOf(object), object), object.getRight());
		return result;
	}

	@Override
	public IExpression casePair(final Pair object) {
		IExpression result = binary(EGaml.getKeyOf(object), object.getLeft(), object.getRight());
		return result;
	}

	@Override
	public IExpression caseBinary(final Binary object) {
		IExpression result = binary(EGaml.getKeyOf(object), object.getLeft(), object.getRight());
		return result;
	}

	@Override
	public IExpression caseUnit(final Unit object) {
		// We simply return a multiplication, since the right member (the "unit") will be
		// translated into its float value

		// Case of dates: #month and #year
		String name = EGaml.toString(object.getRight());
		if ( "month".equals(name) || "year".equals(name) ) {
			if ( getContext().getModelDescription().isStartingDateDefined() ) {
				getContext().warning(
					"Your model uses a starting date. In that case, the usage of #month or #year is discouraged as these units will not represent realistic durations",
					IGamlIssue.DEPRECATED, object);
			}
		}
		// AD: Hack to address Issue 387. If the unit is a pixel, we add +1 to the whole expression.
		IExpression right = compile(object.getRight());
		IExpression result = binary("*", object.getLeft(), object.getRight());
		// AD: removal of the hack to address #1325 -- needs to be tested in OpenGL
		// if ( result != null && ((BinaryOperator) result).arg(1) instanceof PixelUnitExpression ) {
		// result = factory.createOperator("+", getContext(), object, factory.createConst(1, Types.INT), result);
		// }
		return result;
	}

	@Override
	public IExpression caseUnary(final Unary object) {
		return unary(EGaml.getKeyOf(object), object.getRight());
	}

	// @Override
	public IExpression caseDot(final Access object) {
		return compileFieldExpr(object.getLeft(), object.getRight());
	}

	@Override
	public IExpression caseAccess(final Access object) {
		if ( object.getOp().equals(".") ) { return caseDot(object); }
		IExpression container = compile(object.getLeft());
		// If no container is defined, return a null expression
		if ( container == null ) { return null; }
		IType contType = container.getType();
		boolean isMatrix = contType.id() == IType.MATRIX;
		IType keyType = contType.getKeyType();
		List<? extends Expression> list = EGaml.getExprsOf(object.getArgs());
		List<IExpression> result = new ArrayList();
		int size = list.size();
		for ( int i = 0; i < size; i++ ) {
			Expression eExpr = list.get(i);
			IExpression e = compile(eExpr);
			if ( e != null ) {
				IType elementType = e.getType();
				if ( keyType != Types.NO_TYPE && !keyType.isAssignableFrom(e.getType()) ) {
					if ( !(isMatrix && elementType.id() == IType.INT && size > 1) ) {
						getContext().warning("a " + contType.toString() + " cannot be accessed using a " +
							elementType.toString() + " index", IGamlIssue.WRONG_TYPE, eExpr);
					}
				}
				result.add(e);
			}
		}
		if ( size > 2 ) {
			int expected = isMatrix ? 2 : 1;
			String end = expected == 1 ? " only 1 index" : " 1 or 2 indices";
			getContext().warning("a " + contType.toString() + " should be accessed using" + end,
				IGamlIssue.DIFFERENT_ARGUMENTS, object);
		}

		IExpression indices = factory.createList(result);
		return factory.createOperator("internal_at", getContext(), object, container, indices);
	}

	@Override
	public IExpression caseArray(final Array object) {
		List<? extends Expression> list = EGaml.getExprsOf(object.getExprs());
		List<IExpression> result = new ArrayList();
		boolean allPairs = true;
		for ( int i = 0, n = list.size(); i < n; i++ ) {
			Expression eExpr = list.get(i);
			allPairs = allPairs && eExpr instanceof Pair;
			IExpression e = compile(eExpr);
			if ( e == null ) {
				System.out.print(true);
				e = compile(eExpr);
			}
			result.add(e);
		}
		if ( allPairs && !list.isEmpty() ) { return factory.createMap(result); }
		return factory.createList(result);
	}

	@Override
	public IExpression casePoint(final Point object) {
		Expression z = object.getZ();
		if ( z == null ) { return binary(POINT, object.getLeft(), object.getRight()); }
		IExpression[] exprs = new IExpression[3];
		exprs[0] = compile(object.getLeft());
		exprs[1] = compile(object.getRight());
		exprs[2] = compile(z);
		return factory.createOperator(POINT, getContext(), object, exprs);
	}

	@Override
	public IExpression caseParameters(final Parameters object) {
		List<IExpression> list = new ArrayList();
		for ( Expression p : EGaml.getExprsOf(object.getParams()) ) {
			list.add(binary("::", factory.createConst(EGaml.getKeyOf(p.getLeft()), Types.STRING), p.getRight()));
		}
		return factory.createMap(list);
	}

	@Override
	public IExpression caseExpressionList(final ExpressionList object) {
		List<Expression> list = EGaml.getExprsOf(object);
		if ( list.isEmpty() ) { return null; }
		if ( list.size() > 1 ) {
			getContext().warning(
				"A sequence of expressions is not expected here. Only the first expression will be evaluated",
				IGamlIssue.UNKNOWN_ARGUMENT, object);
		}
		IExpression expr = compile(list.get(0));
		return expr;
	}

	@Override
	public IExpression caseFunction(final Function object) {
		String op = EGaml.getKeyOf(object);

		SpeciesDescription sd = getContext().getSpeciesContext();
		if ( sd != null ) {
			StatementDescription action = sd.getAction(op);
			if ( action != null ) {
				EObject params = object.getParameters();
				if ( params == null ) {
					params = object.getArgs();
				}
				IExpression call = action(op, caseVar(SELF, object), params, action);
				if ( call != null ) { return call; }
			}
		}

		List<Expression> args = EGaml.getExprsOf(object.getArgs());
		int size = args.size();
		if ( size == 1 ) {
			if ( isTypeName(op) ) { return binary(AS, args.get(0), object); }
			// Not a type name, but type information present
			TypeInfo type = object.getType();
			if ( type != null ) {
				getContext().warning("Key and contents types are not expected here and will not be evaluated",
					IGamlIssue.UNKNOWN_ARGUMENT, object);
			}
			return unary(op, args.get(0));

		}
		if ( size == 2 ) {
			IExpression result = binary(op, args.get(0), args.get(1));
			return result;
		}
		IExpression[] compiledArgs = new IExpression[size];
		for ( int i = 0; i < size; i++ ) {
			compiledArgs[i] = compile(args.get(i));
		}
		IExpression result = factory.createOperator(op, getContext(), object, compiledArgs);
		return result;
	}

	@Override
	public IExpression caseIntLiteral(final IntLiteral object) {
		try {
			Integer val = Integer.parseInt(EGaml.getKeyOf(object), 10);
			return factory.createConst(val, Types.INT);
		} catch (NumberFormatException e) {
			getContext().error("Malformed integer: " + EGaml.getKeyOf(object), IGamlIssue.UNKNOWN_NUMBER, object);
			return null;
		}
	}

	@Override
	public IExpression caseDoubleLiteral(final DoubleLiteral object) {

		String s = EGaml.getKeyOf(object);

		if ( s == null ) { return null; }
		try {
			return factory.createConst(Double.parseDouble(s), Types.FLOAT);
		} catch (NumberFormatException e) {
			try {
				final NumberFormat nf = NumberFormat.getInstance(Locale.US);
				// More robust, but slower parsing used in case Double.parseDouble() cannot handle it
				// See Issue 1025. Exponent notation is capitalized, and '+' is removed beforehand
				s = s.replace('e', 'E');
				s = s.replace("+", "");
				return factory.createConst(nf.parse(s).doubleValue(), Types.FLOAT);
			} catch (ParseException ex) {
				getContext().error("Malformed float: " + s, IGamlIssue.UNKNOWN_NUMBER, object);
				return null;
			}
		}

	}

	@Override
	public IExpression caseColorLiteral(final ColorLiteral object) {
		try {
			Integer val = Integer.parseInt(EGaml.getKeyOf(object).substring(1), 16);
			return factory.createConst(val, Types.INT);
		} catch (NumberFormatException e) {
			getContext().error("Malformed integer: " + EGaml.getKeyOf(object), IGamlIssue.UNKNOWN_NUMBER, object);
			return null;
		}
	}

	@Override
	public IExpression caseStringLiteral(final StringLiteral object) {
		return factory.createConst(StringUtils.unescapeJava(EGaml.getKeyOf(object)), Types.STRING);
	}

	@Override
	public IExpression caseBooleanLiteral(final BooleanLiteral object) {
		String s = EGaml.getKeyOf(object);
		if ( s == null ) { return null; }
		return s.equalsIgnoreCase(TRUE) ? TRUE_EXPR : FALSE_EXPR;
	}

	@Override
	public IExpression defaultCase(final EObject object) {
		if ( !getContext().getErrorCollector().hasErrors() ) {
			// In order to avoid too many "useless errors"
			getContext().error("Cannot compile: " + object, IGamlIssue.GENERAL, object);
		}
		return null;
	}

	private IExpression caseVar(final String varName, final EObject object) {
		if ( varName == null ) {
			getContext().error("Unknown variable", IGamlIssue.UNKNOWN_VAR, object);
			return null;
		}

		// HACK
		if ( varName.equals(USER_LOCATION) ) { return factory.createVar(USER_LOCATION, Types.POINT, true,
			IVarExpression.TEMP, getContext()); }
		// HACK
		if ( varName.equals(EACH) ) { return getEachExpr(object); }
		if ( varName.equals(NULL) ) { return IExpressionFactory.NIL_EXPR; }
		if ( varName.equals(SELF) ) {
			IDescription temp_sd = getContext().getSpeciesContext();
			if ( temp_sd == null ) {
				getContext().error("Unable to determine the species of self", IGamlIssue.GENERAL, object);
				return null;
			}
			IType tt = temp_sd.getType();
			return factory.createVar(SELF, tt, true, IVarExpression.SELF, null);
		}
		if ( varName.equalsIgnoreCase(WORLD_AGENT_NAME) ) { return getWorldExpr(); }
		if ( isSpeciesName(varName) ) {
			SpeciesDescription sd = getSpeciesContext(varName);
			return sd == null ? null : sd.getSpeciesExpr();
			// IExpression expr = factory.createSpeciesConstant(GamaType.from(getSpeciesContext(varName)));
			// return expr;
		}
		IDescription temp_sd = getContext() == null ? null : getContext().getDescriptionDeclaringVar(varName);

		if ( temp_sd != null ) {
			if ( temp_sd instanceof SpeciesDescription ) {
				SpeciesDescription remote_sd = getContext().getSpeciesContext();
				if ( remote_sd != null ) {
					SpeciesDescription found_sd = (SpeciesDescription) temp_sd;

					if ( remote_sd != temp_sd && !remote_sd.isBuiltIn() && !remote_sd.hasMacroSpecies(found_sd) ) {
						getContext().error("The variable " + varName + " is not accessible in this context (" +
							remote_sd.getName() + "), but in the context of " + found_sd.getName() +
							". It should be preceded by 'myself.'", IGamlIssue.UNKNOWN_VAR, object, varName);
					}
				}
			}

			return temp_sd.getVarExpr(varName);
		}

		if ( isTypeName(varName) ) { return factory.createTypeExpression(getContext().getTypeNamed(varName)); }

		if ( isSkillName(varName) ) { return skill(varName); }

		if ( getContext() != null ) {

			// Short circuiting the use of keyword in "draw ..." to ensure backward
			// compatibility while providing a useful warning.

			if ( getContext().getKeyword().equals(DRAW) ) {
				if ( DrawStatement.SHAPES.keySet().contains(varName) ) {
					getContext().warning(
						"The symbol " + varName +
							" is not used anymore in draw. Please use geometries instead, e.g. '" + varName + "(size)'",
						IGamlIssue.UNKNOWN_KEYWORD, object, varName);
					return factory.createConst(varName + "__deprecated", Types.STRING);
				}
			}

			// Finally, a last possibility (enabled in rare occasions, like in the "elevation" facet of grid layers), is
			// that the variable used belongs to the species denoted by the current statement
			if ( getContext() instanceof StatementDescription ) {
				SpeciesDescription denotedSpecies = ((StatementDescription) getContext()).computeSpecies();
				if ( denotedSpecies != null ) {
					if ( denotedSpecies.hasVar(varName) ) { return denotedSpecies.getVarExpr(varName); }
				}
			}

			// An experimental possibility is that the variable refers to an description of the species (used like a variable, see Issue 853)
			// or also any behavior or aspect
			SpeciesDescription sd = getContext().getSpeciesContext();
			if ( sd.hasAction(varName) ) { return new DenotedActionExpression(sd.getAction(varName)); }
			if ( sd.hasBehavior(varName) ) { return new DenotedActionExpression(sd.getBehavior(varName)); }
			if ( sd.hasAspect(varName) ) { return new DenotedActionExpression(sd.getAspect(varName)); }

			getContext().error(
				"The variable " + varName +
					" is not defined or accessible in this context. Check its name or declare it",
				IGamlIssue.UNKNOWN_VAR, object, varName);
		}
		return null;

	}

	private static volatile int count = 0;

	private GamlResource getFreshResource() {
		if ( resource == null ) {
			XtextResourceSet rs = EGaml.getInstance(XtextResourceSet.class);
			// XtextResourceSet rs = new SynchronizedXtextResourceSet();
			rs.setClasspathURIContext(EcoreBasedExpressionDescription.class);
			// IResourceFactory resourceFactory = EGaml.getInstance(IResourceFactory.class);
			URI uri = URI.createURI(SYNTHETIC_RESOURCES_PREFIX + count++ + ".gaml", false);
			resource = (GamlResource) rs.createResource(uri);
			// resource = (GamlResource) resourceFactory.createResource(uri);
			// rs.getResources().add(resource);
		} else {
			resource.unload();
		}
		return resource;
	}

	private EObject getEObjectOf(final String string) throws GamaRuntimeException {
		EObject result = null;
		String s = "dummy <- " + string;
		GamlResource resource = getFreshResource();
		InputStream is = new ByteArrayInputStream(s.getBytes());
		try {
			resource.load(is, null);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

		if ( resource.getErrors().isEmpty() ) {
			EObject e = resource.getContents().get(0);
			if ( e instanceof StringEvaluator ) {
				result = ((StringEvaluator) e).getExpr();
				// System.out.println(" -> Additional compilation of " + string + " as " + result);
			}
		} else {
			Resource.Diagnostic d = resource.getErrors().get(0);
			throw GamaRuntimeException.error(d.getMessage());
		}

		// if ( result instanceof TerminalExpression ) {

		// }
		return result;
	}

	//
	//
	// hqnghi 11/Oct/13 two methods for compiling models directly from files
	//
	//
	@Override
	public IModel createModelFromFile(final String fileName) {
		// System.out.println(fileName + " model is loading...");

		GamlResource resource =
			(GamlResource) getContext().getModelDescription().getUnderlyingElement(null).eResource();

		URI iu = URI.createURI(fileName, false).resolve(resource.getURI());
		IModel lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		// GamlResource r = (GamlResource) rs.getResource(iu, true);
		GamlResource r = (GamlResource) rs.getResource(iu, true);
		// URI.createURI("file:///" + fileName), true);

		try {
			List<GamlCompilationError> errors = new ArrayList();
			lastModel = /* GamlModelBuilder.getInstance() */new GamlModelBuilder().compile(r, errors);
			// if ( lastModel == nu ) {
			// lastModel = null;
			// // System.out.println("End compilation of " + m.getName());
			// }

		} catch (GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO do something with "errors" if the lastModel == null ?
			// fireBuildEnded(m, lastModel);
		}
		// FIXME Experiment default no longer exists. Needs to specify one name
		// GAMA.controller.newExperiment(IKeyword.DEFAULT, lastModel);
		// System.out.println("Experiment created ");
		return lastModel;
	}

	@Override
	public ModelDescription createModelDescriptionFromFile(final String fileName) {
		System.out.println(fileName + " model is loading...");

		GamlResource resource =
			(GamlResource) getContext().getModelDescription().getUnderlyingElement(null).eResource();

		URI iu = URI.createURI(fileName, false).resolve(resource.getURI());
		ModelDescription lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		GamlResource r = (GamlResource) rs.getResource(iu, true);
		try {
			GamlJavaValidator validator = new GamlJavaValidator();
			List<GamlCompilationError> errors = new ArrayList();
			lastModel =
				/* GamlModelBuilder.getInstance() */new GamlModelBuilder().buildModelDescription(r.getURI(), errors);
			if ( !r.getErrors().isEmpty() ) {
				lastModel = null;
			}

		} catch (GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// collectErrors(collect);
			// fireBuildEnded(m, lastModel);
		}
		// FIXME Experiment default no longer exists. Needs to specify one name
		// GAMA.controller.newExperiment(IKeyword.DEFAULT, lastModel);
		// System.out.println("Experiment created ");
		return lastModel;
	}

	/**
	 * Method getFacetExpression()
	 * @see msi.gaml.expressions.IExpressionCompiler#getFacetExpression(msi.gaml.descriptions.IDescription, java.lang.Object)
	 */
	@Override
	public EObject getFacetExpression(final IDescription context, final EObject target) {
		if ( target.eContainer() instanceof Facet ) { return target.eContainer(); }
		return target;
	}

	//
	// end-hqnghi
	//

	public IVarExpression getEachExpr(final EObject object) {
		IVarExpression p = iteratorContexts.peek();
		if ( p == null ) {
			getContext().error("'each' is not accessible in this context", IGamlIssue.UNKNOWN_VAR, object);
			return null;
		}
		return p;
	}

	public void setEach_Expr(final String iterator, final IVarExpression each_expr) {
		iteratorContexts.push(each_expr);
	}

	private IExpressionDescription getCurrentExpressionDescription() {
		return currentExpressionDescription;
	}

	private void setCurrentExpressionDescription(final IExpressionDescription currentExpressionDescription) {
		this.currentExpressionDescription = currentExpressionDescription;
	}
}
