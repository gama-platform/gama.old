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

import java.io.StringReader;
import java.text.NumberFormat;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.gaml.parser.antlr.GamlParser;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.types.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import com.google.inject.Inject;

/**
 * The Class ExpressionParser.
 */
public class NewGamlExpressionParser implements IExpressionParser<Expression> {

	public static IExpression NIL_EXPR;
	public static IExpression TRUE_EXPR;
	public static IExpression FALSE_EXPR;
	public static IVarExpression EACH_EXPR;
	public static IExpression WORLD_EXPR = null;

	@Inject
	private static GamlParser antlrParser;

	static {
		IExpressionParser.UNARIES.put(IKeyword.MY, new GamaMap());
	}

	GamlSwitch<IExpression> compiler = new GamlSwitch<IExpression>() {

		@Override
		public IExpression caseExpression(final Expression object) {
			// in the general case, we try to return a binary expression
			return binary(object.getOp(), object.getLeft(), object.getRight());
		}

		@Override
		public IExpression caseVariableRef(final VariableRef object) {
			// we delegate to the referenced EObject
			return doSwitch(object.getRef());
		}

		@Override
		public IExpression caseGamlVarRef(final GamlVarRef object) {
			String s = object.getName();
			if ( s.equalsIgnoreCase(IKeyword.EACH) ) { return EACH_EXPR; }
			if ( s.equalsIgnoreCase(IKeyword.NULL) ) { return NIL_EXPR; }
			if ( isSpeciesName(s) ) { return factory.createConst(s, Types.get(IType.SPECIES),
				getSpeciesContext(s).getType()); }
			if ( s.equalsIgnoreCase(IKeyword.SELF) ) {
				IDescription species = getContext().getSpeciesContext();
				if ( species == null ) {
					context.flagError("Unable to determine the species of self", object);
					return null;
				}
				IType tt = getContext().getSpeciesContext().getType();
				return factory.createVar(IKeyword.SELF, tt, tt, true, IVarExpression.SELF);
			}
			if ( s.equalsIgnoreCase(IKeyword.WORLD_AGENT_NAME) ) { return getWorldExpr(); }
			// By default, we try to find a variable
			IDescription desc = getContext().getDescriptionDeclaringVar(s);
			if ( desc == null ) {
				if ( getContext() instanceof CommandDescription ) {
					desc = ((CommandDescription) getContext()).extractExtraSpeciesContext();
				}
			}
			if ( desc != null ) {
				IVarExpression var = (IVarExpression) desc.getVarExpr(s, factory);
				return var;
			}
			context.flagError("Unknown variable: " + s, object);
			return null;

		}

		@Override
		public IExpression caseTerminalExpression(final TerminalExpression object) {
			return super.caseTerminalExpression(object);
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
			return super.caseGamlUnitExpr(object);
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
				IExpression e = compile(list.get(i));
				result.add(e);
				allPairs = allPairs && e instanceof PairExpr;
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
			String op = object.getOp();
			EList<Expression> args = object.getArgs();
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
			return object.getValue().equalsIgnoreCase(IKeyword.TRUE) ? TRUE_EXPR : FALSE_EXPR;
		}

		@Override
		public IExpression defaultCase(final EObject object) {
			getContext().flagError("Unrecognized expression" + object);
			return null;
		}

	};

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via simulation.getModel().getDescription()) if it is
	 * available. Otherwise, only simple expressions (that contain mostly constants) can be parsed.
	 */
	private IDescription context;

	private GamlExpressionFactory factory;

	public NewGamlExpressionParser() {

	}

	@Override
	public void setFactory(final IExpressionFactory f) {
		factory = (GamlExpressionFactory) f;
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
				factory.createVar(IKeyword.EACH, Types.NO_TYPE, Types.NO_TYPE, true,
					IVarExpression.EACH);
		}
	}

	public void dispose() {
		setContext(null);
	}

	@Override
	public IExpression parse(final ExpressionDescription s, final IDescription parsingContext) {
		// FIXME HACK to test the standalone parser in XText
		if ( s.getAst() == null ) { return parseString(s.toString(), parsingContext); }
		return parse((Expression) s.getAst(), parsingContext);
	}

	/**
	 * @param string
	 * @return
	 */
	private IExpression parseString(final String string, final IDescription context) {
		GamlGrammarAccess grammar = antlrParser.getGrammarAccess();
		StringReader r = new StringReader(string);
		IParseResult result = antlrParser.parse(grammar.getExpressionRule(), r);
		// List<SyntaxError> errors = result.getParseErrors();
		// Assert.assertTrue(errors.size() == 0);
		Expression eRoot = (Expression) result.getRootASTElement();
		// MyDSLRoot root = (MyDSLRoot) eRoot;
		return parse(eRoot, context);
	}

	public IExpression parse(final Expression s, final IDescription parsingContext) {
		IDescription previous = null;
		if ( parsingContext != null ) {
			previous = getContext();
			setContext(parsingContext);
		}
		IExpression result = compile(s);
		if ( parsingContext != null ) {
			setContext(previous);
		}
		return result;
	}

	public IExpression compile(final EObject s) {
		return compiler.doSwitch(s);
	}

	protected Expression createArgArray(final EList<Expression> args) {
		Array array = EGaml.getFactory().createArray();
		EList<Expression> pairs = array.getExprs();
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
		if ( op.equals(IKeyword.MY) ) {
			IDescription desc = getContext().getDescriptionDeclaringVar(IKeyword.MYSELF);
			if ( desc != null ) {
				// We are in a remote context, so 'my' refers to the calling agent
				IExpression myself = desc.getVarExpr(IKeyword.MYSELF, factory);
				IDescription species = getSpeciesContext(myself.type().getSpeciesName());
				IExpression var = species.getVarExpr(literal(e), factory);
				return factory.createBinaryExpr(IKeyword._DOT, myself, var, desc);
			}
			// Otherwise, we ignore 'my' since it refers to 'self'
			return compile(e.getRight());
		}
		if ( isSpeciesName(op) ) { return factory.createBinaryExpr(IKeyword.AS, expr, species(op),
			context); }
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
		if ( IKeyword.OF.equals(op) ) { return compileFieldExpr(e2, literal(e1)); }
		// we can now safely compile the left-hand expression
		IExpression left = compile(e1);
		// if the operator is "as", the right-hand expression should be a casting type
		if ( IKeyword.AS.equals(op) ) {
			String species = literal(e2);
			if ( isSpeciesName(species) ) { return factory.createBinaryExpr(op, left,
				species(species), context); }
			// allows expression such as 'agent as dead'. Should we allow them ?
			return factory.createUnaryExpr(species, left, context);
		}
		// if the operator is "is", the right-hand expression should be a type
		if ( IKeyword.IS.equals(op) ) {
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
		return factory.createBinaryExpr(IKeyword._DOT, target, expr, context);

	}

	// KEEP
	private IExpression compileNumberExpr(final String s) {
		final NumberFormat nf = NumberFormat.getInstance(Locale.US);
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
		if ( (val instanceof Long || val instanceof Integer) && !s.contains(IKeyword._DOT) ) { return factory
			.createConst(val.intValue(), Types.get(IType.INT)); }
		return factory.createConst(val.doubleValue(), Types.get(IType.FLOAT));

	}

	// KEEP
	private IExpression compileArguments(final CommandDescription action,
		final EList<Expression> words) {
		final GamaList list = new GamaList();
		for ( int i = 0, n = words.size(); i < n; i++ ) {
			Expression e = words.get(0);
			if ( !(e instanceof PairExpr) ) {
				context.flagError("Arguments must be provided as pairs arg::value; ", e);
				return null;
			}
			PairExpr p = (PairExpr) e;
			String arg = literal(p.getLeft());
			if ( !action.containsArg(arg) ) {
				context.flagError(
					"Argument " + arg + " is not defined for action " + action.getName(),
					p.getLeft());
				return null;
			}
			// We modify the expression in line to replace the arg by a string terminal
			e.setLeft(EGaml.createTerminal(arg));
			list.add(compile(e));
		}
		return factory.createMap(list);
	}

	// KEEP
	private IExpression getWorldExpr() {
		if ( WORLD_EXPR == null ) {
			IType tt = getContext().getModelDescription().getWorldSpecies().getType();
			WORLD_EXPR =
				factory.createVar(IKeyword.WORLD_AGENT_NAME, tt, tt, true, IVarExpression.WORLD);
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

}
