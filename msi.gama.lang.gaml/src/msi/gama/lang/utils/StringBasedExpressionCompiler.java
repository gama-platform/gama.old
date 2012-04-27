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

import static msi.gaml.expressions.GamlExpressionFactory.*;
import java.text.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.IPriority;
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.commands.Facets;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Strings;
import msi.gaml.types.*;

/**
 * The Class ExpressionParser.
 */
public class StringBasedExpressionCompiler implements IExpressionParser<IExpressionDescription> {

	static {
		UNARIES.put(IKeyword.MY, new GamaMap());
	}

	/*
	 * The context (IDescription) in which the parser operates. If none is given, the global context
	 * of the current simulation is returned (via GAMA.getModelContex()) if it is
	 * available. Otherwise, only simple expressions (that contain mostly constants) can be parsed.
	 */
	private IDescription context;

	private GamlExpressionFactory factory;

	public StringBasedExpressionCompiler() {

	}

	@Override
	public void setFactory(final IExpressionFactory f) {
		factory = (GamlExpressionFactory) f;
	}

	public void dispose() {
		setContext(null);
	}

	@Override
	public IExpression parse(final IExpressionDescription s, final IDescription parsingContext) {
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

	private IExpression compileExpr(final IExpressionDescription descr) {
		IExpression e = descr.getExpression();
		if ( e != null ) { return e; }
		List<String> words = StringUtils.tokenize(descr.toString());
		return compileExpr(words);
	}

	private IExpression compileExpr(final List<String> words) {
		// List<String> words = StringUtils.tokenize(descr.toString());
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

		if ( opIndex != 0 ) {
			final String operator = words.get(opIndex);
			if ( IKeyword.OF.equals(operator) ) {
				IExpression agent = compileExpr(words.subList(opIndex + 1, words.size()));
				return compileFieldExpr(agent, words.get(0));
			}
			final IExpression expr1 = compileExpr(words.subList(0, opIndex));
			IExpression expr2 = null;
			if ( IKeyword._DOT.equals(operator) ) {
				if ( words.size() < opIndex + 2 ) {
					context.flagError("The '.' cannot end an expression");
				}
				return compileFieldExpr(expr1, words.get(opIndex + 1));
			} else if ( IKeyword.AS.equals(operator) ) {
				String castingString = words.get(opIndex + 1);
				return isSpeciesName(castingString) ? factory.createBinaryExpr(operator, expr1,
					factory
						.createExpr(new StringBasedExpressionDescription(castingString), context),
					context) : factory.createUnaryExpr(castingString, expr1, context);
			} else if ( IKeyword.IS.equals(operator) && isTypeName(words.get(opIndex + 1)) ) {
				return factory.createBinaryExpr(operator, expr1,
					factory.createConst(words.get(opIndex + 1), Types.get(IType.STRING)), context);
			} else if ( IExpressionParser.FUNCTIONS.contains(operator) ) {
				SpeciesDescription sd =
					getContext().getSpeciesDescription(expr1.getContentType().getSpeciesName());
				if ( sd == null ) {
					context.flagError("the left side of " + operator + " is not an agent");
					return null;
				}
				CommandDescription cd = sd.getAction(operator);
				if ( cd == null ) {
					context.flagError(operator + " is not available for agents of species " +
						sd.getName());
					return null;
				}
				expr2 = compileArguments(cd, words.subList(opIndex + 2, words.size() - 1));
			} else {
				// In case the operator is an iterator, we assign the content type of
				// the first expression to "each"
				if ( IExpressionParser.ITERATORS.contains(operator) ) {
					IType t = expr1.getContentType();
					GamlExpressionFactory.EACH_EXPR.setType(t);
					GamlExpressionFactory.EACH_EXPR.setContentType(t);
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
			final IExpression expr2 =
				factory.createExpr(new StringBasedExpressionDescription(firstWord), context);
			return factory.createBinaryExpr(IKeyword.AS, expr1, expr2, context);
		}

		context.flagError("malformed expression : " + words);
		return null;
	}

	// private void assertBalancing(final int par, final String openExp, final String closeExp) {
	// if ( par == 0 ) { return; }
	// if ( par > 0 ) {
	// context.flagError("Missing '" + closeExp + "'");
	// }
	// if ( par < 0 ) {
	// context.flagError("Missing '" + openExp + "'");
	// }
	// }

	private IExpression compileTerminalExpr(final String s) {
		// If the string is a Gama string we return a constant string expression
		if ( StringUtils.isGamaString(s) ) { return factory.createConst(
			StringUtils.unescapeJava(s.substring(1, s.length() - 1)), Types.get(IType.STRING)); }
		if ( s.charAt(0) == '\'' ) {
			context.flagError("Malformed string: " + s);
			return null;
		}
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
			IDescription species = getContext().getSpeciesContext();
			if ( species == null ) {
				context.flagError("Unable to determine the species of self");
				return null;
			}
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

	private IExpression compileVarExpr(final String s) {
		IDescription desc = getContext().getDescriptionDeclaringVar(s);
		// if ( desc == null ) {
		// if ( getContext() instanceof CommandDescription ) {
		// desc = ((CommandDescription) getContext()).extractExtraSpeciesContext();
		// }
		// }
		if ( desc != null ) {
			IVarExpression var = (IVarExpression) desc.getVarExpr(s, factory);
			return var;
		}
		context.flagError("Unknown variable :" + s);
		return null;
	}

	private SpeciesDescription getSpeciesContext(final String e) {
		return getContext().getSpeciesDescription(e);
	}

	private boolean isSpeciesName(final String s) {
		return getContext().getSpeciesDescription(s) != null;
	}

	private boolean isTypeName(final String s) {
		return getContext().getModelDescription().getTypeOf(s) != null;
	}

	public static boolean isDottedExpr(final String s) {
		return s.contains(IKeyword._DOT) && !Character.isDigit(s.charAt(0)) &&
			!StringUtils.isGamaString(s);
	}

	private IExpression compileFieldExpr(final IExpression target, final String var) {
		IType type = target.type();
		if ( StringBasedExpressionCompiler.isDottedExpr(var) ) {

			final String[] ss = var.split("\\.");
			return compileFieldExpr(compileFieldExpr(target, ss[0]), ss[1]);

		}
		SpeciesDescription contextDesc = getContext().getSpeciesDescription(type.getSpeciesName());

		if ( contextDesc == null ) {
			TypeFieldExpression expr = (TypeFieldExpression) type.getGetter(var);
			if ( expr == null ) {
				context.flagError("Field " + var + " unknown for expression " + target.toGaml() +
					" of type " + type);
				return null;
			}
			expr = expr.copyWith(target);
			return expr;
		}
		IVarExpression expr = (IVarExpression) contextDesc.getVarExpr(var, factory);
		if ( expr == null ) {
			context.flagError("Unknown variable :" + var + " in " + contextDesc.getName());
			return null;
		}
		return factory.createBinaryExpr(IKeyword._DOT, target, expr, context);

	}

	private IExpression compileNumberExpr(final String s) {
		final NumberFormat nf = NumberFormat.getInstance(Locale.US);
		Number val = null;
		try {
			val = nf.parse(s);
		} catch (final ParseException e) {
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

	private IExpression compileListExpr(final List<String> words) {
		System.out.println("Compiling " + words);
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

	private IExpression compileArguments(final CommandDescription action, final List<String> words) {
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
			if ( !action.containsArg(arg) ) {
				context.flagError("Argument " + arg + "not defined for action " + action.getName());
				return null;
			}
			words.set(begin, StringUtils.toGamlString(arg));
			List<String> pair = words.subList(begin, end);
			final IExpression item = compileExpr(pair);
			if ( item.type().id() != IType.PAIR ) {
				context.flagError("Arguments must be provided as pairs arg::value; " +
					item.toGaml() + " is not a pair");
				return null;
			}
			list.add(item);
			begin = end + 1;
			end = begin;
		}
		return factory.createMap(list);
	}

	private IExpression compilePointExpr(final List<String> words) {
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
		if ( GamlExpressionFactory.WORLD_EXPR == null ) {
			IType tt = getContext().getModelDescription().getWorldSpecies().getType();
			GamlExpressionFactory.WORLD_EXPR =
				factory.createVar(IKeyword.WORLD_AGENT_NAME, tt, tt, true, IVarExpression.WORLD);
		}
		return GamlExpressionFactory.WORLD_EXPR;
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
		if ( context == null ) { return GAMA.getModelContext(); }
		return context;
	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseArguments(msi.gaml.descriptions.ExpressionDescription)
	 */
	@Override
	public Map<String, IExpressionDescription> parseArguments(final IExpressionDescription args,
		final IDescription context) {

		Map<String, IExpressionDescription> argList = new HashMap();
		List<String> words; // = new ExpressionDescription(args, true);
		List<String> tokens = StringUtils.tokenize(args.toString());
		words = tokens.subList(1, tokens.size() - 1);
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
			int parenthesis = 0;
			if ( words.get(begin).equals(IKeyword.OPEN_EXP) ) {
				parenthesis = 1;
			}
			String arg = words.get(begin + parenthesis);
			String sep = words.get(begin + parenthesis + 1);
			if ( !sep.equals("::") ) {
				context.flagError("Arguments must be provided as pairs arg::value; " +
					words.subList(begin, end) + " is not a pair");
			}
			StringBuilder sb = new StringBuilder();
			for ( String token : words.subList(begin + parenthesis + 2, end - parenthesis) ) {
				sb.append(token);
			}
			IExpressionDescription expr = new StringBasedExpressionDescription(sb.toString());
			// String[] facetArray = new String[] { IKeyword.NAME, arg, IKeyword.VALUE, expr };
			// GuiUtils.debug("Found a new argument:" + Arrays.toString(facetArray));
			Facets f = new Facets(IKeyword.NAME, arg);
			f.put(IKeyword.VALUE, expr);
			argList.put(arg, expr);
			begin = end + 1;
			end = begin;
		}
		return argList;

	}

	/**
	 * @see msi.gaml.expressions.IExpressionParser#parseLiteralArray(msi.gaml.descriptions.ExpressionDescription,
	 *      msi.gaml.descriptions.IDescription)
	 */
	@Override
	public List<String> parseLiteralArray(final IExpressionDescription s, final IDescription context) {
		return StringUtils.tokenize(s.toString());
	}

}
