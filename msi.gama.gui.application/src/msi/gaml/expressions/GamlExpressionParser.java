/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import java.text.*;
import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.CommandDescription;
import msi.gama.internal.descriptions.ExecutionContextDescription;
import msi.gama.internal.expressions.*;
import msi.gama.internal.types.*;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.operators.Strings;

/**
 * The Class ExpressionParser.
 */
@handles({ ISymbolKind.GAML_PARSING })
public class GamlExpressionParser implements IExpressionParser {

	public static IExpression		NIL_EXPR;
	public static IExpression		TRUE_EXPR;
	public static IExpression		FALSE_EXPR;
	public static IVarExpression	EACH_EXPR;
	public static IExpression		WORLD_EXPR	= null;

	static {
		UNARIES.put(MY, new GamaMap());
	}

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via simulation.getModel().getDescription()) if it is
	 * available. Otherwise, only simple expressions (that contain mostly constants) can be parsed.
	 */
	private IDescription			context;

	private GamlExpressionFactory	factory;

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
				factory.createVar(EACH, Types.NO_TYPE, Types.NO_TYPE, true, IVarExpression.EACH);
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
			if ( OPEN_EXP.equals(s) ) {
				par += 1;
			} else if ( CLOSE_EXP.equals(s) ) {
				par -= 1;
			} else if ( OPEN_LIST.equals(s) ) {
				list += 1;
			} else if ( CLOSE_LIST.equals(s) ) {
				list -= 1;
			} else if ( OPEN_POINT.equals(s) ) {
				pt += 1;
			} else if ( CLOSE_POINT.equals(s) ) {
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
		assertBalancing(par, OPEN_EXP, CLOSE_EXP);
		assertBalancing(list, OPEN_LIST, CLOSE_LIST);
		assertBalancing(pt, OPEN_POINT, CLOSE_POINT);

		if ( opIndex != 0 ) {
			final String operator = words.get(opIndex);
			if ( OF.equals(operator) ) {
				IExpression agent = compileExpr(words.subList(opIndex + 1, words.size()));
				return compileFieldExpr(agent, words.get(0));
			}
			final IExpression expr1 = compileExpr(words.subList(0, opIndex));
			IExpression expr2 = null;
			if ( DOT.equals(operator) ) {
				return compileFieldExpr(expr1, words.get(opIndex + 1));
			} else if ( AS.equals(operator) ) {
				String castingString = words.get(opIndex + 1);
				return isSpeciesName(castingString) ? factory.createBinaryExpr(operator, expr1,
					compileExpr(new ExpressionDescription(castingString)), context) : factory
					.createUnaryExpr(castingString, expr1);
			} else if ( IS.equals(operator) && isTypeName(words.get(opIndex + 1)) ) {
				return factory.createBinaryExpr(operator, expr1,
					factory.createConst(words.get(opIndex + 1)), context);
			} else if ( IExpressionParser.FUNCTIONS.contains(operator) ) {
				ExecutionContextDescription sd =
					getContext().getModelDescription().getSpeciesDescription(
						expr1.getContentType().getSpeciesName());
				if ( sd == null ) { throw new GamlException("the left side of " + operator +
					" is not an agent"); }
				CommandDescription cd = sd.getAction(operator);
				if ( cd == null ) { throw new GamlException(operator +
					" is not available for agents of species " + sd.getName()); }
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

		if ( firstWord.equals(OPEN_EXP) ) { return compileExpr(words.subList(1, words.size() - 1)); }
		if ( firstWord.equals(OPEN_LIST) ) { return compileListExpr(words.subList(1,
			words.size() - 1)); }
		if ( firstWord.equals(OPEN_POINT) ) { return compilePointExpr(words.subList(1,
			words.size() - 1)); }
		if ( UNARIES.containsKey(firstWord) ) {
			if ( firstWord.equals(MY) ) {
				IDescription desc = getContext().getDescriptionDeclaringVar(ISymbol.MYSELF);
				if ( desc != null ) { return compileExpr(GamaList.with(ISymbol.MYSELF, DOT, words
					.subList(opIndex + 1, words.size()).get(0))); }
				return compileExpr(words.subList(opIndex + 1, words.size()));
			}
			final IExpression expr1 = compileExpr(words.subList(opIndex + 1, words.size()));
			return factory.createUnaryExpr(firstWord, expr1);
		}

		if ( isSpeciesName(firstWord) ) {
			final IExpression expr1 = compileExpr(words.subList(opIndex + 1, words.size()));
			final IExpression expr2 = compileExpr(new ExpressionDescription(firstWord));
			return factory.createBinaryExpr(AS, expr1, expr2, context);
		}

		throw new GamlException("malformed expression : " + words);
	}

	private static void assertBalancing(final int par, final String openExp, final String closeExp)
		throws GamlException {
		if ( par == 0 ) { return; }
		if ( par > 0 ) { throw new GamlException("Missing '" + closeExp + "'"); }
		if ( par < 0 ) { throw new GamlException("Missing '" + openExp + "'"); }
	}

	private IExpression compileTerminalExpr(final String s) throws GamlException {
		// If the string is a Gama string we return a constant string expression
		if ( GamaStringType.isGamaString(s) ) { return factory.createConst(
			GamaStringType.unescapeJava(s.substring(1, s.length() - 1)), Types.get(IType.STRING)); }
		if ( s.charAt(0) == '\'' ) { throw new GamlException("Malformed string: " + s); }
		// If the string is a number, we built it apart
		if ( Strings.isGamaNumber(s) ) { return compileNumberExpr(s); }
		// If the string is a literal constant we return the expression
		if ( s.equalsIgnoreCase(TRUE) ) { return TRUE_EXPR; }
		if ( s.equalsIgnoreCase(FALSE) ) { return FALSE_EXPR; }
		if ( s.equalsIgnoreCase(EACH) ) { return EACH_EXPR; }
		if ( s.equalsIgnoreCase(NULL) ) { return NIL_EXPR; }
		// assertContext(s);
		if ( isSpeciesName(s) ) { return factory.createConst(s, Types.get(IType.SPECIES),
			getSpeciesContext(s).getType()); }
		if ( s.equalsIgnoreCase(SELF) ) {
			IType tt = getContext().getSpeciesContext().getType();
			return factory.createVar(SELF, tt, tt, true, IVarExpression.SELF);
		}
		if ( s.equalsIgnoreCase(ISymbol.WORLD_AGENT_NAME) ) { return getWorldExpr(); }
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
		throw new GamlException("malformed variable :" + s);
	}

	private ExecutionContextDescription getSpeciesContext(final String e) {
		return getContext().getModelDescription().getSpeciesDescription(e);
	}

	private boolean isSpeciesName(final String s) {
		return getContext().getModelDescription().getSpeciesDescription(s) != null;
	}

	private boolean isTypeName(final String s) {
		return getContext().getModelDescription().getTypeOf(s) != null;
	}

	public static boolean isDottedExpr(final String s) {
		return s.contains(DOT) && !Character.isDigit(s.charAt(0)) &&
			!GamaStringType.isGamaString(s);
	}

	private IExpression compileFieldExpr(final IExpression target, final String var)
		throws GamlException {
		IType type = target.type();
		ExecutionContextDescription contextDesc =
			getContext().getModelDescription().getSpeciesDescription(type.getSpeciesName());

		if ( contextDesc == null ) {
			TypeFieldExpression expr = (TypeFieldExpression) type.getGetter(var);
			if ( expr == null ) { throw new GamlException("Field " + var +
				" unknown for expression " + target.toGaml() + " of type " + type); }
			expr = expr.copyWith(target);
			return expr;
		}
		IVarExpression expr = (IVarExpression) contextDesc.getVarExpr(var, factory);
		if ( expr == null ) { throw new GamlException("Unknown variable :" + var + " in " +
			contextDesc.getName()); }
		return factory.createBinaryExpr(DOT, target, expr, context);

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
					throw new GamlException("Malformed number: " + s);
				}
			}
		}
		if ( val == null ) { throw new GamlException("\"" + s + "\" not recognized as a number"); }
		if ( (val instanceof Long || val instanceof Integer) && !s.contains(DOT) ) { return factory
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
				if ( OPEN_LIST.equals(endString) || OPEN_POINT.equals(endString) ) {
					listLevel++;
				} else if ( CLOSE_LIST.equals(endString) || CLOSE_POINT.equals(endString) ) {
					listLevel--;
				} else if ( COMMA.equals(endString) && listLevel == 0 ) {
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
				if ( endString.equals(OPEN_LIST) || endString.equals(OPEN_POINT) ) {
					listLevel++;
				} else if ( endString.equals(CLOSE_LIST) || endString.equals(CLOSE_POINT) ) {
					listLevel--;
				} else if ( COMMA.equals(endString) && listLevel == 0 ) {
					break;
				}
				end++;
			}
			String arg = words.get(begin);
			if ( !action.containsArg(arg) ) { throw new GamlException("Argument " + arg +
				"not defined for action " + action.getName()); }
			words.set(begin, GamaStringType.toGamlString(arg));
			List<String> pair = words.subList(begin, end);
			final IExpression item = compileExpr(pair);
			if ( item.type().id() != IType.PAIR ) { throw new GamlException(
				"Arguments must be provided as pairs arg::value; " + item.toGaml() +
					" is not a pair"); }
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
			while (end < words.size() && !COMMA.equals(words.get(end))) {
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
				factory.createVar(ISymbol.WORLD_AGENT_NAME, tt, tt, true, IVarExpression.WORLD);
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
