/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.expressions.GamlExpressionFactory.*;
import java.text.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.precompiler.IUnits;
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.statements.DrawStatement;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * The Class ExpressionParser.
 */

public class NewGamlExpressionCompiler implements IExpressionCompiler<Expression> {

	public IVarExpression each_expr;
	final static NumberFormat nf = NumberFormat.getInstance(Locale.US);
	private IExpression world = null;

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via simulation.getModel().getDescription()) if it is
	 * available. Otherwise, only simple expressions (that contain constants) can be parsed.
	 */
	private IDescription context;

	private final IExpressionFactory factory;

	/**
	 * Represents the string-based parser to be used when the
	 * information from the AST is not available
	 */
	private final IExpressionCompiler fallBackParser;

	static {
		IExpressionCompiler.UNARIES.put(MY, new GamaMap());
	}

	public NewGamlExpressionCompiler() {
		factory = GAMA.getExpressionFactory();
		fallBackParser = new StringBasedExpressionCompiler();
	}

	GamlSwitch<IExpression> compiler = new GamlSwitch<IExpression>() {

		@Override
		public IExpression caseStatement(final Statement object) {

			// WHAT IF THE NAME IS NULL ?

			return factory.createConst(StringUtils.unescapeJava(EGaml.getNameOf(object)),
				Types.get(IType.STRING));
		}

		@Override
		public IExpression caseFacet(final Facet object) {
			return factory.createConst(StringUtils.unescapeJava(object.getName()),
				Types.get(IType.STRING));
		}

		@Override
		public IExpression caseExpression(final Expression object) {
			// in the general case, we try to return a binary expression
			return binary(object.getOp(), object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseVariableRef(final VariableRef object) {
			String s = EGaml.getKeyOf(object);
			if ( s == null ) {
				// we delegate to the referenced GamlVarRef
				return caseGamlVarRef(object.getRef());
			}
			return caseVar(s, object);
		}

		@Override
		public IExpression caseUnitName(final UnitName object) {
			String s = object.getOp();
			if ( IUnits.UNITS.containsKey(s) ) { return factory.createUnitExpr(s, context); }
			// If it is a unit, we return its float value
			context.flagError(s + " is not a unit name.", IGamlIssue.NOT_A_UNIT, object,
				(String[]) null);
			return null;
		}

		public IExpression caseVar(final String s, final EObject object) {
			if ( s == null ) {
				getContext().flagError("Unknown variable", IGamlIssue.UNKNOWN_VAR, object);
				return null;
			}

			// HACK
			if ( s.equals(USER_LOCATION) ) { return factory.createVar(USER_LOCATION,
				Types.get(IType.POINT), Types.NO_TYPE, true, IVarExpression.TEMP, context); }

			// HACK
			if ( s.equals(EACH) ) { return each_expr; }
			if ( s.equals(NULL) ) { return new ConstantExpression(null, Types.NO_TYPE,
				Types.NO_TYPE); }
			// We try to find a species name from the name
			IDescription temp_sd = null;
			if ( context != null ) {
				temp_sd = getContext().getSpeciesDescription(s);
			}
			if ( temp_sd != null ) { return factory.createConst(s, Types.get(IType.SPECIES),
				temp_sd.getType()); }
			if ( s.equals(SELF) ) {
				temp_sd = getContext().getSpeciesContext();
				if ( temp_sd == null ) {
					context.flagError("Unable to determine the species of self",
						IGamlIssue.GENERAL, object);
					return null;
				}
				IType tt = temp_sd.getType();
				return factory.createVar(SELF, tt, tt, true, IVarExpression.SELF, null);
			}
			if ( s.equalsIgnoreCase(WORLD_AGENT_NAME) ) { return getWorldExpr(); }

			// By default, we try to find a variable

			temp_sd = context == null ? null : context.getDescriptionDeclaringVar(s);
			if ( temp_sd != null ) { return temp_sd.getVarExpr(s); }
			if ( context != null ) {

				// REGARDER SI ON PEUT COURT-CIRCUITER DRAW ICI ? LE PROBLEME EST QUE C'EST UN PEU
				// TARD.

				if ( context.getKeyword().equals(DRAW) ) {
					if ( DrawStatement.SHAPES.keySet().contains(s) ) {
						context
							.flagWarning(
								"The symbol " +
									s +
									" can not be used anymore in draw statements. Please use geometries instead, e.g. '" +
									s + "(size)'", IGamlIssue.UNKNOWN_KEYWORD, object, s);
					}
					return factory.createConst(s + "__deprecated", Types.get(IType.STRING));
				}

				context.flagWarning("The variable " + s +
					" has not been previously defined. Check its name or declare it",
					IGamlIssue.UNKNOWN_VAR, object, s);
				return factory.createVar(s, Types.NO_TYPE, Types.NO_TYPE, true,
					IVarExpression.TEMP, context);
			}
			return null;

		}

		@Override
		public IExpression caseGamlVarRef(final GamlVarRef object) {
			return caseVar(object.getName(), object);
		}

		@Override
		public IExpression caseTernExp(final TernExp object) {
			IExpression ifFalse = compile(object.getIfFalse());
			IExpression alt =
				factory.createBinaryExpr(":", compile(object.getRight()), ifFalse, context, false);
			return factory.createBinaryExpr("?", compile(object.getLeft()), alt, context, false);
		}

		@Override
		public IExpression caseArgPairExpr(final ArgPairExpr object) {
			return binary(object.getOp(), caseVar(EGaml.getKeyOf(object), object),
				object.getRight());
		}

		@Override
		public IExpression casePairExpr(final PairExpr object) {
			return binary(object.getOp(), object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseGamlBinaryExpr(final GamlBinaryExpr object) {
			return binary(object.getOp(), object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseGamlUnitExpr(final GamlUnitExpr object) {
			// We simply return a multiplication, since the right member (the "unit") will be
			// translated into its float value
			return binary("*", object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseGamlUnaryExpr(final GamlUnaryExpr object) {
			return unary(object.getOp(), object.getRight());
		}

		@Override
		public IExpression caseMemberRef(final MemberRef object) {
			return compileFieldExpr(object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseAccess(final Access object) {
			List<? extends Expression> list = object.getArgs();
			List<IExpression> result = new ArrayList();
			for ( int i = 0, n = list.size(); i < n; i++ ) {
				Expression eExpr = list.get(i);
				IExpression e = compile(eExpr);
				result.add(e);
			}
			IExpression container = compile(object.getLeft());
			IExpression indices = factory.createList(result);
			return factory.createBinaryExpr("internal_at", container, indices, context, false);
		}

		@Override
		public IExpression caseArray(final Array object) {
			List<? extends Expression> list = object.getExprs();
			List<IExpression> result = new ArrayList();
			boolean allPairs = true;
			for ( int i = 0, n = list.size(); i < n; i++ ) {
				Expression eExpr = list.get(i);
				allPairs = allPairs && eExpr instanceof PairExpr;
				IExpression e = compile(eExpr);
				result.add(e);
			}
			if ( allPairs && !list.isEmpty() ) { return factory.createMap(result); }
			return factory.createList(result);
		}

		@Override
		public IExpression casePoint(final Point object) {
			IExpression point2d =
				binary(IExpressionCompiler.INTERNAL_POINT, object.getLeft(), object.getRight());
			Expression z = object.getZ();
			return z == null ? point2d : binary(IExpressionCompiler.INTERNAL_Z, point2d, z);
		}

		@Override
		public IExpression caseFunction(final Function object) {
			String op = EGaml.getKeyOf(object);
			List<Expression> args = object.getArgs();
			int size = args.size();
			if ( size == 1 ) { return unary(op, args.get(0)); }
			if ( size == 2 ) { return binary(op, args.get(0), args.get(1)); }
			return binary(op, args.get(0), createArgArray(args));
		}

		@Override
		public IExpression caseIntLiteral(final IntLiteral object) {
			try {
				Integer val = Integer.parseInt(object.getValue(), 10);
				return factory.createConst(val, Types.get(IType.INT));
			} catch (NumberFormatException e) {
				context.flagError("Malformed integer: " + object.getValue(),
					IGamlIssue.UNKNOWN_NUMBER, object);
				return null;
			}
		}

		@Override
		public IExpression caseDoubleLiteral(final DoubleLiteral object) {
			try {
				Number val = nf.parse(object.getValue());
				return factory.createConst(val.doubleValue(), Types.get(IType.FLOAT));
			} catch (ParseException e) {
				context.flagError("Malformed float: " + object.getValue(),
					IGamlIssue.UNKNOWN_NUMBER, object);
				return null;
			}

		}

		@Override
		public IExpression caseColorLiteral(final ColorLiteral object) {
			try {
				Integer val = Integer.parseInt(object.getValue().substring(1), 16);
				return factory.createConst(val, Types.get(IType.INT));
			} catch (NumberFormatException e) {
				context.flagError("Malformed integer: " + object.getValue(),
					IGamlIssue.UNKNOWN_NUMBER, object);
				return null;
			}
		}

		@Override
		public IExpression caseStringLiteral(final StringLiteral object) {
			return factory.createConst(StringUtils.unescapeJava(object.getValue()),
				Types.get(IType.STRING));
		}

		@Override
		public IExpression caseBooleanLiteral(final BooleanLiteral object) {
			return object.getValue().equalsIgnoreCase(TRUE) ? TRUE_EXPR : FALSE_EXPR;
		}

		@Override
		public IExpression defaultCase(final EObject object) {
			if ( object instanceof Expression ) {
				getContext().flagError("Unrecognized expression " + EGaml.toString(object),
					IGamlIssue.GENERAL, object);
			} else {
				getContext().flagError("Not an expression: " + object, IGamlIssue.GENERAL, object);
			}
			return null;
		}

	};

	public void dispose() {
		setContext(null);
	}

	@Override
	public IExpression compile(final IExpressionDescription s, final IDescription parsingContext) {
		// If the AST is not available, we fall back on the robust string-based parser
		// long time = System.currentTimeMillis();
		EObject e = s.getTarget();
		if ( e == null ) { return fallBackParser.compile(s, parsingContext); }

		IDescription previous = getContext();
		if ( parsingContext != null ) {
			setContext(parsingContext);
		}
		IExpression result = compile(e);
		if ( parsingContext != null ) {
			setContext(previous);
		}
		// long now = System.currentTimeMillis();
		// GuiUtils.debug("Compiling expression " + s.toString() + " took: " + (now - time) + "ms");
		return result;

	}

	private IExpression compile(final EObject s) {
		if ( s == null ) {
			// No error, since most of the null expressions come from previous (more focused)
			// errors.
			// context.flagError("Null expression");
			return null;
		}
		IExpression expr = compiler.doSwitch(s);
		EGaml.setGamlDescription(s, expr);
		return expr;
	}

	protected Expression createArgArray(final List<Expression> args) {
		Array array = EGaml.getFactory().createArray();
		List<Expression> pairs = array.getExprs();
		for ( int i = 0, n = args.size(); i < n; i++ ) {
			PairExpr p = EGaml.getFactory().createPairExpr();
			p.setLeft(EGaml.createTerminal("arg" + i));
			p.setRight(args.get(i));
			pairs.add(p);
		}
		return array;
	}

	// KEEP
	private IExpression species(final String name) {
		return factory.createConst(name, Types.get(IType.SPECIES), getSpeciesContext(name)
			.getType());
	}

	// KEEP
	private IExpression unary(final String op, final Expression e) {
		IExpression expr = compile(e);
		if ( op == null || expr == null ) { return null; }
		if ( op.equals(MY) ) {
			IDescription desc = getContext().getDescriptionDeclaringVar(MYSELF);
			if ( desc != null ) {
				// We are in a remote context, so 'my' refers to the calling agent
				IExpression myself = desc.getVarExpr(MYSELF);
				IDescription species = getSpeciesContext(myself.getType().getSpeciesName());
				IExpression var = species.getVarExpr(EGaml.getKeyOf(e));
				return factory.createBinaryExpr(_DOT, myself, var, desc, false);
			}
			// Otherwise, we ignore 'my' since it refers to 'self'
			return compile(e);
		}
		// The unary "unit" operator should let the value of its child pass through
		if ( op.equals("¡") ) { return compile(e); }
		if ( isSpeciesName(op) ) { return factory.createBinaryExpr(AS, expr, species(op), context,
			false); }
		return factory.createUnaryExpr(op, expr, context);
	}

	private IExpression binary(final String op, final IExpression left, final Expression e2) {
		if ( left == null ) { return null; }
		// if the operator is "as", the right-hand expression should be a casting type
		if ( AS.equals(op) ) {
			String species = EGaml.getKeyOf(e2);
			if ( isSpeciesName(species) ) { return factory.createBinaryExpr(op, left,
				species(species), context, false); }
			// allows expression such as 'agent as dead'. Should we allow them ?
			return factory.createUnaryExpr(species, left, context);
		}
		// if the operator is "is", the right-hand expression should be a type
		if ( IS.equals(op) ) {
			String type = EGaml.getKeyOf(e2);
			if ( isTypeName(type) ) { return factory.createBinaryExpr(op, left,
				factory.createConst(type, Types.get(IType.STRING), Types.get(IType.STRING)),
				context, false); }
			getContext().flagError(
				"'is' must be followed by a type name. " + type + " is not a type name.",
				IGamlIssue.NOT_A_TYPE, e2, type);
			return null;
		}

		// we verify and compile apart the calls to actions as operators
		SpeciesDescription sd = getContext().getSpeciesDescription(left.getType().getSpeciesName());
		if ( sd != null ) {
			StatementDescription cd = sd.getAction(op);
			if ( cd != null ) {
				if ( !(e2 instanceof Array) ) {
					context.flagError(
						"Arguments to actions must be provided as an array of pairs arg::value",
						IGamlIssue.UNKNOWN_ARGUMENT, e2);
					return null;
				}
				IExpression right = compileArguments(cd, ((Array) e2).getExprs());
				return factory.createBinaryExpr(op, left, right, context, true);
			}
		}

		// // if the operator is an action call, we must verify and compile the arguments apart
		// if ( isOnlyFunction(op) ) {
		// SpeciesDescription sd =
		// getContext().getSpeciesDescription(left.getContentType().getSpeciesName());
		// if ( sd == null ) {
		// context.flagError("the left side of " + op + " is not an agent",
		// IGamlIssue.NOT_AN_AGENT, e1);
		// return null;
		// }
		// StatementDescription cd = sd.getAction(op);
		// if ( cd == null ) {
		// context.flagError(op + " is not available for agents of species " + sd.getName(),
		// IGamlIssue.UNKNOWN_ACTION, e1, op, sd.getName());
		// }
		// if ( !(e2 instanceof Array) ) {
		// context.flagError(
		// "Arguments to actions must be provided as an array of pairs arg::value",
		// IGamlIssue.UNKNOWN_ARGUMENT, e2);
		// return null;
		// }
		// IExpression right = compileArguments(cd, ((Array) e2).getExprs());
		// return factory.createBinaryExpr(op, left, right, context);
		// }
		// if the operator is an iterator, we must initialize "each"
		if ( ITERATORS.contains(op) ) {
			IType t = left.getContentType();
			each_expr = new EachExpression(EACH, t, t);
		}
		// we can now safely compile the right-hand expression
		IExpression right = compile(e2);
		// and return the binary expression
		return factory.createBinaryExpr(op, left, right, context, false);

	}

	//
	// private boolean isOnlyFunction(final String op) {
	// return FUNCTIONS.contains(op) && BINARIES.get(op).size() == 1;
	// }

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
		return getContext().getModelDescription().hasSpeciesDescription(s);
	}

	private boolean isTypeName(final String s) {
		return getContext().getModelDescription().getTypeNamed(s) != null;
	}

	// New: "." can now be used to access containers elements.
	private IExpression compileFieldExpr(final Expression leftExpr, final Expression fieldExpr) {
		String var = EGaml.getKeyOf(fieldExpr);
		IExpression target = compile(leftExpr);
		if ( target == null ) { return null; }
		IType type = target.getType();
		SpeciesDescription contextDesc = getSpeciesContext(type.getSpeciesName());
		if ( contextDesc == null ) {
			TypeFieldExpression expr = (TypeFieldExpression) type.getGetter(var);
			if ( expr == null ) {
				if ( Types.get(IType.CONTAINER).isAssignableFrom(type) ) {
					// We have an instance of the use of "." as "at" or "@"
					return binary(IKeyword.AT, target, fieldExpr);
				}
				context.flagError("Field " + var + " unknown for " + target.toGaml() + " of type " +
					type, IGamlIssue.UNKNOWN_FIELD, leftExpr, var, type.toString());
				return null;
			}
			expr = expr.copyWith(target);
			EGaml.setGamlDescription(fieldExpr, expr);
			return expr;
		}
		IVarExpression expr = (IVarExpression) contextDesc.getVarExpr(var);
		if ( expr == null ) {
			context.flagError("Unknown variable :" + var + " in " + contextDesc.getName(),
				IGamlIssue.UNKNOWN_VAR, leftExpr, var, contextDesc.getName());
		}
		EGaml.setGamlDescription(fieldExpr, expr);
		return factory.createBinaryExpr(_DOT, target, expr, context, false);

	}

	// KEEP
	private IExpression compileArguments(final StatementDescription action,
		final List<Expression> words) {
		if ( action == null ) {
			context.flagError("Action cannot be determined", IGamlIssue.UNKNOWN_ACTION);
			return null;
		}
		final GamaList list = new GamaList();
		for ( int i = 0, n = words.size(); i < n; i++ ) {
			Expression e = words.get(i);
			if ( !(e instanceof PairExpr) ) {
				context.flagError("Arguments must be provided as pairs arg::value; ",
					IGamlIssue.UNKNOWN_ARGUMENT, e, action.getArgNames().toArray(new String[] {}));
				return null;
			}
			String arg = null;
			if ( e instanceof ArgPairExpr ) {
				arg = EGaml.getKeyOf(e);
			} else {
				arg = EGaml.getKeyOf(((PairExpr) e).getLeft());

			}
			if ( !action.containsArg(arg) ) {
				context.flagError(
					"Argument " + arg + " is not allowed for action " + action.getName(),
					IGamlIssue.UNKNOWN_ARGUMENT, e, action.getArgNames().toArray(new String[] {}));
				return null;
			}
			// We modify the expression in line to replace the arg by a string terminal
			list.add(binary("::", EGaml.createTerminal(arg), e.getRight()));
		}
		return factory.createMap(list);
	}

	// KEEP
	private IExpression getWorldExpr() {
		if ( world == null ) {
			IType tt = getContext().getModelDescription().getWorldSpecies().getType();
			world =
				factory.createVar(WORLD_AGENT_NAME, tt, tt, true, IVarExpression.WORLD,
					context.getModelDescription());
		}
		return world;
	}

	private void setContext(final IDescription commandDescription) {
		context = commandDescription;
	}

	private IDescription getContext() {
		if ( context == null ) { return GAMA.getModelContext(); }
		return context;
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseArguments(msi.gaml.descriptions.ExpressionDescription,
	 *      msi.gaml.descriptions.IDescription)
	 */
	@Override
	public Map<String, IExpressionDescription> parseArguments(final IExpressionDescription args,
		final IDescription command) {
		EObject ast = args.getTarget();
		if ( ast == null ) { return fallBackParser.parseArguments(args, command); }
		Map<String, IExpressionDescription> argMap = new HashMap();

		if ( !(ast instanceof Array) ) {
			command.flagError("Arguments must be provided as a map <name,expression>", WITH);
			return argMap;
		}

		Array map = (Array) ast;
		for ( Expression exp : map.getExprs() ) {
			if ( !(exp instanceof PairExpr) ) {
				command.flagError("Arguments must be provided as a map <name,expression>", WITH);
				return argMap;
			}
			String arg = null;
			if ( exp instanceof ArgPairExpr ) {
				arg = EGaml.getKeyOf(exp);
			} else {
				PairExpr pair = (PairExpr) exp;
				arg = EGaml.getKeyOf(pair.getLeft());
			}
			IExpressionDescription ed = new EcoreBasedExpressionDescription(exp.getRight());
			// EGaml.setGamlDescription(exp.getRight(), ed);
			argMap.put(arg, ed);
		}
		return argMap;
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseLiteralArray(msi.gaml.descriptions.ExpressionDescription)
	 */
	@Override
	public List<String> parseLiteralArray(final IExpressionDescription s, final IDescription context) {
		EObject o = s.getTarget();
		if ( o == null ) { return fallBackParser.parseLiteralArray(s, context); }
		if ( !(o instanceof Array) ) {
			if ( o instanceof VariableRef ) {
				String skillName = EGaml.getKeyOf(o);
				context.flagWarning(
					"Skills should be provided as a list of identifiers, for instance [" +
						skillName + "]", IGamlIssue.AS_ARRAY, o, skillName);
				return Arrays.asList(skillName);
			}
			if ( o instanceof Expression ) {
				context.flagError("Impossible to recognize valid skills in " + EGaml.toString(o),
					IGamlIssue.UNKNOWN_SKILL, o);
			} else {
				context.flagError("Skills should be provided as a list of identifiers.",
					IGamlIssue.UNKNOWN_SKILL, o);
			}
			return Collections.EMPTY_LIST;
		}
		List<String> result = new ArrayList();
		Array array = (Array) o;
		for ( Expression expr : array.getExprs() ) {
			String name = EGaml.getKeyOf(expr);
			if ( name == null ) {
				context.flagError("Unknown skill", IGamlIssue.UNKNOWN_SKILL, expr);
			} else {
				result.add(name);
			}
		}
		return result;

	}

}
