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
import java.text.NumberFormat;
import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.precompiler.IUnits;
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * The Class ExpressionParser.
 */
public class NewGamlExpressionCompiler implements IExpressionParser<Expression> {

	public static IExpression NIL_EXPR;
	public static IExpression TRUE_EXPR;
	public static IExpression FALSE_EXPR;
	public static IVarExpression EACH_EXPR;
	public static IExpression WORLD_EXPR = null;

	/**
	 * Represents the string-based parser to be used when the
	 * information from the AST is not available
	 */
	private IExpressionParser fallBackParser;

	static {
		IExpressionParser.UNARIES.put(MY, new GamaMap());
	}

	GamlSwitch<IExpression> compiler = new GamlSwitch<IExpression>() {

		@Override
		public IExpression caseExpression(final Expression object) {
			// in the general case, we try to return a binary expression
			return binary(object.getOp(), object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseVariableRef(final VariableRef object) {
			// we delegate to the referenced GamlVarRef
			return caseGamlVarRef(object.getRef());
		}

		@Override
		public IExpression caseGamlVarRef(final GamlVarRef object) {
			String s = object.getName();
			if ( s == null ) {
				getContext().flagError("Unknown variable", object);
				return null;
			}
			if ( s.equals(EACH) ) { return EACH_EXPR; }
			if ( s.equals(NULL) ) { return NIL_EXPR; }
			// We try to find a species name from the name
			IDescription temp_sd;
			temp_sd = getContext().getSpeciesDescription(s);
			if ( temp_sd != null ) { return factory.createConst(s, Types.get(IType.SPECIES),
				temp_sd.getType()); }
			if ( s.equals(SELF) ) {
				temp_sd = getContext().getSpeciesContext();
				if ( temp_sd == null ) {
					context.flagError("Unable to determine the species of self", object);
					return null;
				}
				IType tt = temp_sd.getType();
				return factory.createVar(SELF, tt, tt, true, IVarExpression.SELF);
			}
			if ( s.equalsIgnoreCase(WORLD_AGENT_NAME) ) { return getWorldExpr(); }
			// If it is a unit, we return its float value
			if ( IUnits.UNITS.containsKey(s) ) { return factory.createConst(IUnits.UNITS.get(s),
				Types.get(IType.FLOAT)); }
			// By default, we try to find a variable
			temp_sd = getContext().getDescriptionDeclaringVar(s);
			if ( temp_sd == null ) {
				if ( getContext() instanceof CommandDescription ) {
					temp_sd = ((CommandDescription) getContext()).extractExtraSpeciesContext();
				}
			}
			if ( temp_sd != null ) { return temp_sd.getVarExpr(s, factory);
			// GuiUtils.debug("Parser has found " + var);
			}
			context.flagError("Unknown variable: " + s, object);
			return null;

		}

		@Override
		public IExpression caseTernExp(final TernExp object) {
			IExpression ifFalse = compile(object.getIfFalse());
			IExpression alt =
				factory.createBinaryExpr(":", compile(object.getRight()), ifFalse, context);
			return factory.createBinaryExpr("?", compile(object.getLeft()), alt, context);
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
			return compileFieldExpr(object.getLeft(), literal(object.getRight()));
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
			return binary(IExpressionParser.INTERNAL_POINT, object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseFunctionRef(final FunctionRef object) {
			String op = EGaml.getKeyOf(object);
			List<Expression> args = object.getArgs();
			int size = args.size();
			if ( size == 1 ) { return unary(op, args.get(0)); }
			if ( size == 2 ) { return binary(op, args.get(0), args.get(1)); }
			return binary(op, args.get(0), createArgArray(args));
		}

		@Override
		public IExpression caseIntLiteral(final IntLiteral object) {
			return compileNumberExpr(object.getValue());
		}

		@Override
		public IExpression caseDoubleLiteral(final DoubleLiteral object) {
			return compileNumberExpr(object.getValue());
		}

		@Override
		public IExpression caseColorLiteral(final ColorLiteral object) {
			return compileNumberExpr(object.getValue());
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
			getContext().flagError("Unrecognized expression " + object, object);
			return null;
		}

	};

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via simulation.getModel().getDescription()) if it is
	 * available. Otherwise, only simple expressions (that contain constants) can be parsed.
	 */
	private IDescription context;

	private GamlExpressionFactory factory;

	@Override
	public void setFactory(final IExpressionFactory f) {
		factory = (GamlExpressionFactory) f;
		if ( fallBackParser == null ) {
			fallBackParser = new StringBasedExpressionCompiler();
			fallBackParser.setFactory(f);
		}
		if ( NIL_EXPR == null ) {
			NIL_EXPR = factory.createConst(null);
		}
		if ( TRUE_EXPR == null ) {
			TRUE_EXPR = factory.createConst(true);
		}
		if ( FALSE_EXPR == null ) {
			FALSE_EXPR = factory.createConst(false);
		}
		if ( EACH_EXPR == null ) {
			EACH_EXPR =
				factory.createVar(EACH, Types.NO_TYPE, Types.NO_TYPE, true, IVarExpression.EACH);
		}
	}

	public void dispose() {
		setContext(null);
	}

	@Override
	public IExpression parse(final IExpressionDescription s, final IDescription parsingContext) {
		// If the AST is not available, we fall back on the robust string-based parser
		long time = System.currentTimeMillis();
		EObject e = (EObject) s.getAst();
		if ( e == null ) { return fallBackParser.parse(s, parsingContext); }
		if ( !(e instanceof Expression) ) {
			parsingContext.flagError("Compilation error in handling " + s, e);
			return null;
		}
		IDescription previous = getContext();
		if ( parsingContext != null ) {
			setContext(parsingContext);
		}
		IExpression result = compile(e);
		if ( parsingContext != null ) {
			setContext(previous);
		}
		long now = System.currentTimeMillis();
		// GuiUtils.debug("Compiling expression " + s.toString() + " took: " + (now - time) + "ms");
		return result;

	}

	public IExpression compile(final EObject s) {
		if ( s == null ) {
			context.flagError("Null expression");
			return null;
		}
		return compiler.doSwitch(s);
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
		if ( op.equals(MY) ) {
			IDescription desc = getContext().getDescriptionDeclaringVar(MYSELF);
			if ( desc != null ) {
				// We are in a remote context, so 'my' refers to the calling agent
				IExpression myself = desc.getVarExpr(MYSELF, factory);
				IDescription species = getSpeciesContext(myself.type().getSpeciesName());
				IExpression var = species.getVarExpr(literal(e), factory);
				return factory.createBinaryExpr(_DOT, myself, var, desc);
			}
			// Otherwise, we ignore 'my' since it refers to 'self'
			return compile(e);
		}
		if ( isSpeciesName(op) ) { return factory.createBinaryExpr(AS, expr, species(op), context); }
		return factory.createUnaryExpr(op, expr, context);
	}

	// KEEP
	private String literal(final EObject e) {
		if ( e instanceof VariableRef ) { return literal(((VariableRef) e).getRef()); }
		if ( e instanceof GamlVarRef ) { return ((GamlVarRef) e).getName(); }
		if ( e instanceof StringLiteral ) { return ((StringLiteral) e).getValue(); }
		return null;
	}

	// KEEP
	private IExpression binary(final String op, final Expression e1, final Expression e2) {
		// if the expression is " var of agents ", we must compile it apart
		if ( OF.equals(op) ) { return compileFieldExpr(e2, literal(e1)); }
		// we can now safely compile the left-hand expression
		IExpression left = compile(e1);
		// if the operator is "as", the right-hand expression should be a casting type
		if ( AS.equals(op) ) {
			String species = literal(e2);
			if ( isSpeciesName(species) ) { return factory.createBinaryExpr(op, left,
				species(species), context); }
			// allows expression such as 'agent as dead'. Should we allow them ?
			return factory.createUnaryExpr(species, left, context);
		}
		// if the operator is "is", the right-hand expression should be a type
		if ( IS.equals(op) ) {
			String type = literal(e2);
			if ( isTypeName(type) ) { return factory.createBinaryExpr(op, left,
				factory.createConst(type), context); }
			getContext().flagError(
				"'is' must be followed by a type name. " + type + " is not a type name.", e2);
			return null;
		}
		// if the operator is an action call, we must verify and compile the arguments apart
		if ( IExpressionParser.FUNCTIONS.contains(op) ) {
			ExecutionContextDescription sd =
				(ExecutionContextDescription) getContext().getModelDescription()
					.getSpeciesDescription(left.getContentType().getSpeciesName());
			if ( sd == null ) {
				context.flagError("the left side of " + op + " is not an agent", e1);
				return null;
			}
			CommandDescription cd = sd.getAction(op);
			if ( cd == null ) {
				context.flagError(op + " is not available for agents of species " + sd.getName(),
					e1);
			}
			if ( !(e2 instanceof Array) ) {
				context.flagError(
					"Arguments to actions must be provided as an array of pairs arg::value", e2);
				return null;
			}
			IExpression right = compileArguments(cd, ((Array) e2).getExprs());
			return factory.createBinaryExpr(op, left, right, context);
		}
		// if the operator is an iterator, we must initialize "each"
		if ( IExpressionParser.ITERATORS.contains(op) ) {
			IType t = left.getContentType();
			EACH_EXPR.setType(t);
			EACH_EXPR.setContentType(t);
		}
		// we can now safely compile the right-hand expression
		IExpression right = compile(e2);
		// and return the binary expression
		return factory.createBinaryExpr(op, left, right, context);
	}

	private ExecutionContextDescription getSpeciesContext(final String e) {
		return (ExecutionContextDescription) getContext().getModelDescription()
			.getSpeciesDescription(e);
	}

	private boolean isSpeciesName(final String s) {
		// TODO Improve the lookup as species names should normally be known
		return getContext().getModelDescription().getSpeciesDescription(s) != null;
	}

	private boolean isTypeName(final String s) {
		return getContext().getModelDescription().getTypeOf(s) != null;
	}

	// KEEP
	private IExpression compileFieldExpr(final Expression e1, final String var) {
		IExpression target = compile(e1);
		IType type = target.type();
		ExecutionContextDescription contextDesc = getSpeciesContext(type.getSpeciesName());
		if ( contextDesc == null ) {
			TypeFieldExpression expr = (TypeFieldExpression) type.getGetter(var);
			if ( expr == null ) {
				context.flagError("Field " + var + " unknown for " + target.toGaml() + " of type " +
					type, e1);
				return null;
			}
			expr = expr.copyWith(target);
			return expr;
		}
		IVarExpression expr = (IVarExpression) contextDesc.getVarExpr(var, factory);
		if ( expr == null ) {
			context.flagError("Unknown variable :" + var + " in " + contextDesc.getName(), e1);
		}
		return factory.createBinaryExpr(_DOT, target, expr, context);

	}

	final static NumberFormat nf = NumberFormat.getInstance(Locale.US);

	// KEEP
	private IExpression compileNumberExpr(final String s) {
		Number val = null;
		try {
			val = nf.parse(s);
		} catch (final java.text.ParseException e) {
			if ( s.charAt(0) == '#' ) {
				try {
					val = Integer.decode(s);
				} catch (final NumberFormatException e2) {
					context.flagError("Malformed number: " + s);
				}
			}
		}
		if ( val == null ) {
			context.flagError("\"" + s + "\" not recognized as a number");
			return null;
		}
		if ( (val instanceof Long || val instanceof Integer) && !s.contains(_DOT) ) { return factory
			.createConst(val.intValue(), Types.get(IType.INT)); }
		return factory.createConst(val.doubleValue(), Types.get(IType.FLOAT));

	}

	// KEEP
	private IExpression compileArguments(final CommandDescription action,
		final List<Expression> words) {
		if ( action == null ) {
			context.flagError("Action cannot be determined");
			return null;
		}
		final GamaList list = new GamaList();
		for ( int i = 0, n = words.size(); i < n; i++ ) {
			Expression e = words.get(i);
			if ( !(e instanceof PairExpr) ) {
				context.flagError("Arguments must be provided as pairs arg::value; ", e);
				return null;
			}
			PairExpr p = (PairExpr) e;
			String arg = literal(p.getLeft());
			if ( !action.containsArg(arg) ) {
				context.flagError(
					"Argument " + arg + " is not allowed for action " + action.getName(),
					p.getLeft());
				return null;
			}
			// We modify the expression in line to replace the arg by a string terminal
			list.add(binary("::", EGaml.createTerminal(arg), p.getRight()));
		}
		return factory.createMap(list);
	}

	// KEEP
	private IExpression getWorldExpr() {
		if ( WORLD_EXPR == null ) {
			IType tt = getContext().getModelDescription().getWorldSpecies().getType();
			WORLD_EXPR = factory.createVar(WORLD_AGENT_NAME, tt, tt, true, IVarExpression.WORLD);
		}
		return WORLD_EXPR;
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
		EObject ast = (EObject) args.getAst();
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
			PairExpr pair = (PairExpr) exp;
			String arg = EGaml.getKeyOf(pair.getLeft());
			IExpressionDescription ed = new EcoreBasedExpressionDescription(pair.getRight());
			argMap.put(arg, ed);
		}
		return argMap;
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseLiteralArray(msi.gaml.descriptions.ExpressionDescription)
	 */
	@Override
	public List<String> parseLiteralArray(final IExpressionDescription s, final IDescription context) {
		EObject o = (EObject) s.getAst();
		if ( o == null ) { return fallBackParser.parseLiteralArray(s, context); }
		if ( !(o instanceof Array) ) {
			context.flagError("Skills must be provided as a list of identifiers", o);
			return Collections.EMPTY_LIST;
		}
		List<String> result = new ArrayList();
		Array array = (Array) o;
		for ( Expression expr : array.getExprs() ) {
			String name = EGaml.getKeyOf(expr);
			if ( name == null ) {
				context.flagError("Unknown skill", array);
			} else {
				result.add(name);
			}
		}
		return result;

	}

}
