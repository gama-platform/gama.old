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
package msi.gaml.expressions;

import java.text.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.operators.Strings;
import msi.gaml.types.*;

/**
 * The Class ExpressionParser.
 */
@handles({ ISymbolKind.GAML_PARSING })
public class GamlExpressionParser implements IExpressionParser {

	public static IExpression NIL_EXPR;
	public static IExpression TRUE_EXPR;
	public static IExpression FALSE_EXPR;
	public static IVarExpression EACH_EXPR;
	public static IExpression WORLD_EXPR = null;

	static {
		UNARIES.put(IKeyword.MY, new GamaMap());
	}

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via simulation.getModel().getDescription()) if it is
	 * available. Otherwise, only simple expressions (that contain mostly constants) can be parsed.
	 */
	private IDescription context;

	private GamlExpressionFactory factory;

	public GamlExpressionParser() {

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
	public IExpression parse(final ExpressionDescription s, final IDescription parsingContext)
		throws GamlException {
		IDescription previous = null;
		if ( parsingContext != null ) {
			previous = getContext();
			setContext(parsingContext);
		}
		IExpression result = compileExpr(s);
		if ( parsingContext != null ) {
			setContext(previous);
		}
		return result;
	}

	private IExpression compileExpr(final List<String> words) throws GamlException {
		if ( words.isEmpty() ) { return null; }
		if ( words.size() == 1 ) { return compileTerminalExpr(words.get(0)); }

		int par = 0, list = 0, i = 0, opIndex = 0, opPriority = IPriority.MIN_PRIORITY, pt = 0;

		while (i <= words.size() - 1) {
			final String s = words.get(i);
			if ( IKeyword.OPEN_EXP.equals(s) ) {
				par += 1;
			} else if ( IKeyword.CLOSE_EXP.equals(s) ) {
				par -= 1;
			} else if ( IKeyword.OPEN_LIST.equals(s) ) {
				list += 1;
			} else if ( IKeyword.CLOSE_LIST.equals(s) ) {
				list -= 1;
			} else if ( IKeyword.OPEN_POINT.equals(s) ) {
				pt += 1;
			} else if ( IKeyword.CLOSE_POINT.equals(s) ) {
				pt -= 1;
			} else if ( par == 0 && list == 0 && pt == 0 && i != 0 ) {
				if ( BINARIES.containsKey(s) && opPriority != IPriority.TERNARY ) {
					final short priority = priorityOf(s);
					if ( priority < opPriority ) {
						opIndex = i;
						opPriority = priority;
					}
				} else if ( opPriority == IPriority.TERNARY ) {
					break;
				}
			}
			i++;
		}
		assertBalancing(par, IKeyword.OPEN_EXP, IKeyword.CLOSE_EXP);
		assertBalancing(list, IKeyword.OPEN_LIST, IKeyword.CLOSE_LIST);
		assertBalancing(pt, IKeyword.OPEN_POINT, IKeyword.CLOSE_POINT);

		if ( opIndex != 0 ) {
			final String operator = words.get(opIndex);
			if ( IKeyword.OF.equals(operator) ) {
				IExpression agent = compileExpr(words.subList(opIndex + 1, words.size()));
				return compileFieldExpr(agent, words.get(0));
			}
			final IExpression expr1 = compileExpr(words.subList(0, opIndex));
			IExpression expr2 = null;
			if ( IKeyword._DOT.equals(operator) ) {
				return compileFieldExpr(expr1, words.get(opIndex + 1));
			} else if ( IKeyword.AS.equals(operator) ) {
				String castingString = words.get(opIndex + 1);
				return isSpeciesName(castingString) ? factory.createBinaryExpr(operator, expr1,
					compileExpr(new ExpressionDescription(castingString)), context) : factory
					.createUnaryExpr(castingString, expr1, context);
			} else if ( IKeyword.IS.equals(operator) && isTypeName(words.get(opIndex + 1)) ) {
				return factory.createBinaryExpr(operator, expr1,
					factory.createConst(words.get(opIndex + 1)), context);
			} else if ( IExpressionParser.FUNCTIONS.contains(operator) ) {
				ExecutionContextDescription sd =
					(ExecutionContextDescription) getContext().getModelDescription()
						.getSpeciesDescription(expr1.getContentType().getSpeciesName());
				if ( sd == null ) { throw new GamlException("the left side of " + operator +
					" is not an agent", context.getSourceInformation()); }
				CommandDescription cd = sd.getAction(operator);
				if ( cd == null ) { throw new GamlException(operator +
					" is not available for agents of species " + sd.getName(),
					context.getSourceInformation()); }
				expr2 = compileArguments(cd, words.subList(opIndex + 2, words.size() - 1));
			} else {
				// In case the operator is an iterator, we assign the content type of
				// the first expression to "each"
				if ( IExpressionParser.ITERATORS.contains(operator) ) {
					IType t = expr1.getContentType();
					EACH_EXPR.setType(t);
					EACH_EXPR.setContentType(t);
				}
				expr2 = compileExpr(words.subList(opIndex + 1, words.size()));
			}
			return factory.createBinaryExpr(operator, expr1, expr2, context);
		}

		// Otherwise, we consider the first word of the sequence

		final String firstWord = words.get(0);

		if ( firstWord.equals(IKeyword.OPEN_EXP) ) { return compileExpr(words.subList(1,
			words.size() - 1)); }
		if ( firstWord.equals(IKeyword.OPEN_LIST) ) { return compileListExpr(words.subList(1,
			words.size() - 1)); }
		if ( firstWord.equals(IKeyword.OPEN_POINT) ) { return compilePointExpr(words.subList(1,
			words.size() - 1)); }
		if ( UNARIES.containsKey(firstWord) ) {
			if ( firstWord.equals(IKeyword.MY) ) {
				IDescription desc = getContext().getDescriptionDeclaringVar(IKeyword.MYSELF);
				if ( desc != null ) { return compileExpr(GamaList.with(IKeyword.MYSELF,
					IKeyword._DOT, words.subList(opIndex + 1, words.size()).get(0))); }
				return compileExpr(words.subList(opIndex + 1, words.size()));
			}
			final IExpression expr1 = compileExpr(words.subList(opIndex + 1, words.size()));
			return factory.createUnaryExpr(firstWord, expr1, context);
		}

		if ( isSpeciesName(firstWord) ) {
			final IExpression expr1 = compileExpr(words.subList(opIndex + 1, words.size()));
			final IExpression expr2 = compileExpr(new ExpressionDescription(firstWord));
			return factory.createBinaryExpr(IKeyword.AS, expr1, expr2, context);
		}

		throw new GamlException("malformed expression : " + words, context.getSourceInformation());
	}

	private static void assertBalancing(final int par, final String openExp, final String closeExp)
		throws GamlException {
		if ( par == 0 ) { return; }
		if ( par > 0 ) { throw new GamlException("Missing '" + closeExp + "'", (Throwable) null); }
		if ( par < 0 ) { throw new GamlException("Missing '" + openExp + "'", (Throwable) null); }
	}

	private IExpression compileTerminalExpr(final String s) throws GamlException {
		// If the string is a Gama string we return a constant string expression
		if ( StringUtils.isGamaString(s) ) { return factory.createConst(
			StringUtils.unescapeJava(s.substring(1, s.length() - 1)), Types.get(IType.STRING)); }
		if ( s.charAt(0) == '\'' ) { throw new GamlException("Malformed string: " + s,
			context.getSourceInformation()); }
		// If the string is a number, we built it apart
		if ( Strings.isGamaNumber(s) ) { return compileNumberExpr(s); }
		// If the string is a literal constant we return the expression
		if ( s.equalsIgnoreCase(IKeyword.TRUE) ) { return TRUE_EXPR; }
		if ( s.equalsIgnoreCase(IKeyword.FALSE) ) { return FALSE_EXPR; }
		if ( s.equalsIgnoreCase(IKeyword.EACH) ) { return EACH_EXPR; }
		if ( s.equalsIgnoreCase(IKeyword.NULL) ) { return NIL_EXPR; }
		// assertContext(s);
		if ( isSpeciesName(s) ) { return factory.createConst(s, Types.get(IType.SPECIES),
			getSpeciesContext(s).getType()); }
		if ( s.equalsIgnoreCase(IKeyword.SELF) ) {
			IType tt = getContext().getSpeciesContext().getType();
			return factory.createVar(IKeyword.SELF, tt, tt, true, IVarExpression.SELF);
		}
		if ( s.equalsIgnoreCase(IKeyword.WORLD_AGENT_NAME) ) { return getWorldExpr(); }
		if ( isDottedExpr(s) ) {
			final String[] ss = s.split("\\.");
			return compileFieldExpr(compileTerminalExpr(ss[0]), ss[1]);
		}
		return compileVarExpr(s);
	}

	private IExpression compileVarExpr(final String s) throws GamlException {
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
		throw new GamlException("malformed variable :" + s, context.getSourceInformation());
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

	public static boolean isDottedExpr(final String s) {
		return s.contains(IKeyword._DOT) && !Character.isDigit(s.charAt(0)) &&
			!StringUtils.isGamaString(s);
	}

	private IExpression compileFieldExpr(final IExpression target, final String var)
		throws GamlException {
		IType type = target.type();
		if ( GamlExpressionParser.isDottedExpr(var) ) {

			final String[] ss = var.split("\\.");
			return compileFieldExpr(compileFieldExpr(target, ss[0]), ss[1]);

		}
		ExecutionContextDescription contextDesc =
			(ExecutionContextDescription) getContext().getModelDescription().getSpeciesDescription(
				type.getSpeciesName());

		if ( contextDesc == null ) {
			TypeFieldExpression expr = (TypeFieldExpression) type.getGetter(var);
			if ( expr == null ) { throw new GamlException("Field " + var +
				" unknown for expression " + target.toGaml() + " of type " + type,
				context.getSourceInformation()); }
			expr = expr.copyWith(target);
			return expr;
		}
		IVarExpression expr = (IVarExpression) contextDesc.getVarExpr(var, factory);
		if ( expr == null ) { throw new GamlException("Unknown variable :" + var + " in " +
			contextDesc.getName(), context.getSourceInformation()); }
		return factory.createBinaryExpr(IKeyword._DOT, target, expr, context);

	}

	private IExpression compileNumberExpr(final String s) throws GamlException {
		final NumberFormat nf = NumberFormat.getInstance(Locale.US);
		Number val = null;
		try {
			val = nf.parse(s);
		} catch (final ParseException e) {
			if ( s.charAt(0) == '#' ) {
				try {
					val = Integer.decode(s);
				} catch (final NumberFormatException e2) {
					throw new GamlException("Malformed number: " + s,
						context.getSourceInformation());
				}
			}
		}
		if ( val == null ) { throw new GamlException("\"" + s + "\" not recognized as a number",
			context.getSourceInformation()); }
		if ( (val instanceof Long || val instanceof Integer) && !s.contains(IKeyword._DOT) ) { return factory
			.createConst(val.intValue(), Types.get(IType.INT)); }
		return factory.createConst(val.doubleValue(), Types.get(IType.FLOAT));

	}

	private IExpression compileListExpr(final List<String> words) throws GamlException {
		final GamaList list = new GamaList();
		int begin = 0;
		int end = 0;
		int listLevel = 0;
		boolean allPairs = true;
		while (begin < words.size()) {
			while (end < words.size()) {
				String endString = words.get(end);
				if ( IKeyword.OPEN_LIST.equals(endString) || IKeyword.OPEN_POINT.equals(endString) ) {
					listLevel++;
				} else if ( IKeyword.CLOSE_LIST.equals(endString) ||
					IKeyword.CLOSE_POINT.equals(endString) ) {
					listLevel--;
				} else if ( IKeyword.COMMA.equals(endString) && listLevel == 0 ) {
					break;
				}
				end++;
			}
			final IExpression item = compileExpr(words.subList(begin, end));
			allPairs = allPairs && item.type().id() == IType.PAIR;
			list.add(item);
			begin = end + 1;
			end = begin;
		}
		if ( allPairs && !list.isEmpty() ) { return factory.createMap(list); }
		return factory.createList(list);
	}

	private IExpression compileArguments(final CommandDescription action, final List<String> words)
		throws GamlException {
		final GamaList list = new GamaList();
		int begin = 0;
		int end = 0;
		int listLevel = 0;
		while (begin < words.size()) {
			while (end < words.size()) {
				String endString = words.get(end);
				if ( endString.equals(IKeyword.OPEN_LIST) || endString.equals(IKeyword.OPEN_POINT) ) {
					listLevel++;
				} else if ( endString.equals(IKeyword.CLOSE_LIST) ||
					endString.equals(IKeyword.CLOSE_POINT) ) {
					listLevel--;
				} else if ( IKeyword.COMMA.equals(endString) && listLevel == 0 ) {
					break;
				}
				end++;
			}
			String arg = words.get(begin);
			if ( !action.containsArg(arg) ) { throw new GamlException("Argument " + arg +
				"not defined for action " + action.getName(), context.getSourceInformation()); }
			words.set(begin, StringUtils.toGamlString(arg));
			List<String> pair = words.subList(begin, end);
			final IExpression item = compileExpr(pair);
			if ( item.type().id() != IType.PAIR ) { throw new GamlException(
				"Arguments must be provided as pairs arg::value; " + item.toGaml() +
					" is not a pair", context.getSourceInformation()); }
			list.add(item);
			begin = end + 1;
			end = begin;
		}
		return factory.createMap(list);
	}

	private IExpression compilePointExpr(final List<String> words) throws GamlException {
		IExpression exprX = null, exprY = null;
		int begin = 0;
		int end = 0;
		boolean isX = true;
		while (begin < words.size()) {
			while (end < words.size() && !IKeyword.COMMA.equals(words.get(end))) {
				end++;
			}
			final IExpression expr = compileExpr(words.subList(begin, end));
			if ( isX ) {
				exprX = expr;
			} else {
				exprY = expr;
			}
			isX = false;
			begin = end + 1;
			end = begin;
		}
		return factory.createBinaryExpr(INTERNAL_POINT, exprX, exprY, context);
	}

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

	private final short priorityOf(final String string) {
		if ( IExpressionParser.BINARY_PRIORITIES.containsKey(string) ) { return IExpressionParser.BINARY_PRIORITIES
			.get(string); }
		return IPriority.MIN_PRIORITY - 2;
	}

	private IDescription getContext() {
		if ( context == null ) { return factory.getDefaultParsingContext(); }
		return context;
	}

}
