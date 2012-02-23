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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.lang.gaml.descript.GamlXtextException;
import msi.gama.lang.gaml.gaml.*;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class GamlToSyntacticElements {

	private static boolean noParenthesisAroundPairs = false;
	public static Statement currentStatement;

	public static ISyntacticElement doConvert(final Model m, final ErrorCollector collect) {
		BasicSyntacticElement elt = convModel(m, collect);
		// elt.dump();
		return elt;
	}

	private static BasicSyntacticElement convModel(final Model m, final ErrorCollector collect) {
		final BasicSyntacticElement model = new BasicSyntacticElement(IKeyword.MODEL, m, collect);
		model.setAttribute(IKeyword.NAME, m.getName(), m);
		for ( final Import i : m.getImports() ) {
			final String uri = i.getImportURI();
			if ( !uri.startsWith("platform:") ) {
				final BasicSyntacticElement include =
					new BasicSyntacticElement(IKeyword.INCLUDE, i, collect);
				include.setAttribute(IKeyword.FILE, uri, i);
				model.addContent(include);
			}
		}
		convStatements(model, m.getStatements(), collect);
		return model;
	}

	private static void convStatements(final BasicSyntacticElement elt, final EList<Statement> ss,
		final ErrorCollector collect) {
		for ( final Statement stm : ss ) {
			ISyntacticElement conv = convStatement(stm, collect);
			if ( conv != null ) {
				elt.addContent(conv);
			}
		}
	}

	private static BasicSyntacticElement convStatement(final Statement stm,
		final ErrorCollector collect) {
		if ( stm == null ) { return null; }
		currentStatement = stm;
		String name = stm.getKey();
		if ( name == null ) { return null; }
		final BasicSyntacticElement elt = new BasicSyntacticElement(name, stm, collect);
		Map<String, Expression> facets = EGaml.getFacetsOf(stm);
		// Modified by the "do" command
		if ( name.equals(IKeyword.DO) ) {
			convDo(stm, facets, elt, collect);
		}
		for ( Map.Entry<String, Expression> entry : facets.entrySet() ) {
			String fname = entry.getKey();
			String fexpr;
			try {
				fexpr = conv(entry.getValue());
			} catch (GamlXtextException e) {
				collect.add(e);
				continue;
			}
			if ( fname.equals(IKeyword.SKILLS) && fexpr.startsWith("[") ) {
				fexpr = fexpr.substring(1, fexpr.length() - 1);
			}
			elt.setAttribute(entry.getKey(), fexpr, entry.getValue());
		}
		if ( name.equals("const") ) {
			elt.setName("var");
			elt.setAttribute("const", "true", stm);
		}
		// if ( name.equals(IKeyword.VAR) ) {
		// Expression e = facets.get(IKeyword.TYPE);
		// String type = EGaml.getKeyOf(e);
		// if ( type != null ) {
		// elt.setName(EGaml.isVariable(e) ? IType.AGENT_STR : type);
		// }
		// }
		String def =
			DescriptionFactory.getModelFactory().getOmissibleFacetForSymbol(elt, elt.getName());

		if ( def != null && elt.getAttribute(def) == null ) { // TODO verify this
			Expression expr = EGaml.getValueOfOmittedFacet(stm);
			if ( expr != null ) {
				try {
					elt.setAttribute(def, conv(expr), expr);
				} catch (GamlXtextException e) {
					collect.add(e);
				}
			}
		}
		if ( name.equals(IKeyword.IF) ) {
			convElse(stm, elt, collect);
		}
		Block block = EGaml.getBlockOf(stm);
		if ( block != null ) {
			convStatements(elt, block.getStatements(), collect);
		}
		return elt;
	}

	private static void convElse(final Statement stm, final ISyntacticElement elt,
		final ErrorCollector collect) {
		EObject elseBlock = stm.getElse();
		if ( elseBlock != null ) {
			BasicSyntacticElement elseElt =
				new BasicSyntacticElement(IKeyword.ELSE, elseBlock, elt.getErrorCollector());
			if ( elseBlock instanceof Statement ) {
				elseElt.addContent(convStatement((Statement) elseBlock, collect));
			} else {
				convStatements(elseElt, ((Block) elseBlock).getStatements(), collect);
			}
			elt.addContent(elseElt);
		}
	}

	static Set<String> builtin = EGaml.getAllowedFacetsFor(IKeyword.DO);

	private static void convDo(final Statement stm, final Map<String, Expression> facets,
		final ISyntacticElement elt, final ErrorCollector collect) {
		Set<String> toRemove = new HashSet();
		StringBuilder sb = new StringBuilder();
		sb.append('[');

		for ( String facet : facets.keySet() ) {
			if ( !builtin.contains(facet) ) {
				try {
					sb.append(facet).append("::").append(conv(facets.get(facet))).append(',');
					toRemove.add(facet);
				} catch (GamlXtextException e) {
					collect.add(e);
				}

			}
		}
		for ( String s : toRemove ) {
			facets.remove(s);
		}
		if ( sb.length() > 1 ) {
			sb.setLength(sb.length() - 1);
			sb.append(']');
			elt.setAttribute(IKeyword.WITH, sb.toString(), facets.isEmpty() ? stm : stm.getFacets()
				.get(0));
		}

	}

	private static String conv(final Expression expr) throws GamlXtextException {
		if ( expr == null ) { throw new GamlXtextException("an expression is expected"); }
		if ( expr instanceof TernExp ) { return left(expr) + " " + expr.getOp() + " " +
			right(expr) + " : " + conv(((TernExp) expr).getIfFalse()); }
		if ( expr instanceof StringLiteral ) { return StringUtils
			.toGamlString(((StringLiteral) expr).getValue()); }
		if ( expr instanceof TerminalExpression ) { return ((TerminalExpression) expr).getValue(); }
		if ( expr instanceof Point ) { return "{" + left(expr) + expr.getOp() + right(expr) + "}"; }
		if ( expr instanceof Array ) { return convArray((Array) expr); }
		if ( expr instanceof MemberRef ) { return left(expr) + expr.getOp() + right(expr); }
		if ( expr instanceof VariableRef ) { return EGaml.getKeyOf(expr); }
		if ( expr instanceof GamlUnaryExpr ) { return expr.getOp() + " " + right(expr); }
		if ( expr instanceof FunctionRef ) { return function((FunctionRef) expr); }
		return left(expr) + " " + expr.getOp() + " " + right(expr);
	}

	private static String function(final FunctionRef expr) throws GamlXtextException {
		EList<Expression> args = expr.getArgs();
		String opName = EGaml.getKeyOf(expr.getLeft());
		if ( args.size() == 0 ) { throw new GamlXtextException("The " + opName +
			"function has no arguments", expr); }
		if ( args.size() == 1 ) { return opName + " " + argument(args.get(0)); }
		if ( args.size() == 2 ) { return argument(args.get(0)) + " " + opName + " " +
			argument(args.get(1)); }
		return argument(args.get(0)) + " " + opName + " " + arguments(args);
		// return EGaml.getKeyOf(expr.getLeft()) + " " + right(expr);
	}

	private static String arguments(final EList<Expression> args) throws GamlXtextException {
		// parses the list of arguments to transform it into a map of args (starting at 1)
		// Experimental right now
		String s = "[";
		for ( int i = 1, n = args.size(); i < n; i++ ) {
			Expression e = args.get(i);
			if ( s.length() > 1 ) {
				s += ", ";
			}
			s += "arg" + i + "::" + conv(e);
		}
		return s + "]";

	}

	private static String left(final Expression expr) throws GamlXtextException {
		Expression e = expr.getLeft();
		if ( e instanceof TerminalExpression || e instanceof VariableRef ) { return conv(e); }
		return "(" + conv(e) + ")";
	}

	private static String right(final Expression expr) throws GamlXtextException {
		Expression e = expr.getRight();
		return argument(e);
	}

	private static String argument(final Expression e) throws GamlXtextException {
		if ( e instanceof TerminalExpression || e instanceof VariableRef || e instanceof Array ) { return conv(e); }
		return "(" + conv(e) + ")";
	}

	private static String convArray(final Array r) throws GamlXtextException {
		String s = "[";
		noParenthesisAroundPairs = true;
		for ( final Expression e : r.getExprs() ) {
			if ( s.length() > 1 ) {
				s += ", ";
			}
			s += conv(e);
		}
		noParenthesisAroundPairs = false;
		return s + "]";
	}

}
