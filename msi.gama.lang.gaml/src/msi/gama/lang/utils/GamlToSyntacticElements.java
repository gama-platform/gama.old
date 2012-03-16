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

import java.io.IOException;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.BasicSyntacticElement;
import msi.gaml.descriptions.ExpressionDescription;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;

public class GamlToSyntacticElements {

	private static boolean noParenthesisAroundPairs = false;
	public static Statement currentStatement;

	public static Map<Resource, ISyntacticElement> buildSyntacticTree(final Resource r,
		final ErrorCollector collect) {
		Map<Resource, ISyntacticElement> docs = new LinkedHashMap();
		buildRecursiveSyntacticTree(docs, r, collect);
		return docs;
	}

	private static void buildRecursiveSyntacticTree(final Map<Resource, ISyntacticElement> docs,
		final Resource r, final ErrorCollector collect) {
		Model m = (Model) r.getContents().get(0);
		docs.put(r, doConvert(m, collect));
		for ( Import imp : m.getImports() ) {
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(r.getURI());
			if ( iu != null && !iu.isEmpty() && EcoreUtil2.isValidUri(r, iu) ) {
				Resource ir = r.getResourceSet().getResource(iu, true);
				try {
					ir.load(Collections.EMPTY_MAP);
				} catch (IOException e) {
					collect.add(new GamlParsingError(e));
					continue;
				}
				if ( ir != r ) {
					if ( !docs.containsKey(ir) ) {
						buildRecursiveSyntacticTree(docs, ir, collect);
					}
				}
			}
		}
	}

	public static ISyntacticElement doConvert(final Model m, final ErrorCollector collect) {
		GamlExpressionFactory fact = (GamlExpressionFactory) GAMA.getExpressionFactory();
		// fact.REGISTER_NEW_PARSER(new NewGamlExpressionParser());

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
				ExpressionDescription s = EGaml.getDependenciesOf(stm);
				if ( s != null && !s.isEmpty() ) {
					conv.setAttribute(IKeyword.DEPENDS_ON, s, stm);
				}
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

			ExpressionDescription fexpr = conv(entry.getValue(), collect);
			if ( fexpr == null ) {
				continue;
			}

			if ( fname.equals(IKeyword.SKILLS) && fexpr.get(0).equals("[") ) {
				fexpr.remove(0);
				fexpr.remove(fexpr.size() - 1);
				// fexpr = fexpr.substring(1, fexpr.length() - 1);
			}
			elt.setAttribute(entry.getKey(), fexpr, entry.getValue());
		}
		if ( name.equals(IKeyword.CONST) ) {
			elt.setName(IKeyword.VAR);
			elt.setAttribute(IKeyword.CONST, IKeyword.TRUE, stm);
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
				elt.setAttribute(def, conv(expr, collect), expr);
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
		ExpressionDescription sb = new ExpressionDescription(stm);
		sb.add("[");

		for ( String facet : facets.keySet() ) {
			if ( !builtin.contains(facet) ) {
				sb.add(facet);
				sb.add("::");
				conv(sb, facets.get(facet), collect);
				sb.add(",");
				toRemove.add(facet);
			}
		}
		for ( String s : toRemove ) {
			facets.remove(s);
		}
		if ( sb.size() > 1 ) {
			sb.remove(sb.size() - 1);
			sb.add("]");
			elt.setAttribute(IKeyword.WITH, sb, facets.isEmpty() ? stm : stm.getFacets().get(0));
		}

	}

	private static ExpressionDescription conv(final Expression expr, final ErrorCollector collect) {
		if ( expr == null ) {
			collect.add(new GamlParsingError("an expression is expected", currentStatement));
			return null;
		}
		ExpressionDescription ed = new ExpressionDescription(expr);
		conv(ed, expr, collect);
		if ( ed.isEmpty() ) { return null; }
		// System.out.println(Arrays.toString(ed.toArray()));
		return ed;
	}

	private static void conv(final ExpressionDescription ed, final Expression expr,
		final ErrorCollector collect) {
		if ( expr == null ) {
			collect.add(new GamlParsingError("an expression is expected", currentStatement));
			return;
		} else if ( expr instanceof TernExp ) {
			left(ed, expr, collect);
			op(ed, expr);
			right(ed, expr, collect);
			ed.add(":");
			conv(ed, ((TernExp) expr).getIfFalse(), collect);
		} else if ( expr instanceof StringLiteral ) {
			ed.add(StringUtils.toGamlString(((StringLiteral) expr).getValue()));
		} else if ( expr instanceof TerminalExpression ) {
			ed.add(((TerminalExpression) expr).getValue());
		} else if ( expr instanceof Point ) {
			ed.add("{");
			left(ed, expr, collect);
			op(ed, expr);
			right(ed, expr, collect);
			ed.add("}");
		} else if ( expr instanceof Array ) {
			convArray(ed, (Array) expr, collect);
		} /*
		 * else if ( expr instanceof MemberRef ) {
		 * left(ed, expr, collect);
		 * op(ed, expr);
		 * right(ed, expr, collect);
		 * }
		 */else if ( expr instanceof VariableRef ) {
			ed.add(EGaml.getKeyOf(expr));
		} else if ( expr instanceof GamlUnaryExpr ) {
			op(ed, expr);
			right(ed, expr, collect);
		} else if ( expr instanceof FunctionRef ) {
			function(ed, (FunctionRef) expr, collect);
		} else {
			left(ed, expr, collect);
			op(ed, expr);
			right(ed, expr, collect);
		}
	}

	private static void op(final ExpressionDescription ed, final Expression expr) {
		String op = expr.getOp();
		if ( op != null ) {
			ed.add(op);
		}
	}

	private static void function(final ExpressionDescription ed, final FunctionRef expr,
		final ErrorCollector collect) {
		EList<Expression> args = expr.getArgs();
		String opName = EGaml.getKeyOf(expr.getLeft());
		if ( args.size() == 0 ) {
			collect.add(new GamlParsingError("The " + opName + "function has no arguments", expr));
		} else if ( args.size() == 1 ) {
			ed.add(opName);
			argument(ed, args.get(0), collect);
		} else if ( args.size() == 2 ) {
			argument(ed, args.get(0), collect);
			ed.add(opName);
			argument(ed, args.get(1), collect);
		} else {
			argument(ed, args.get(0), collect);
			ed.add(opName);
			arguments(ed, args, collect);
		}
	}

	private static void arguments(final ExpressionDescription ed, final EList<Expression> args,
		final ErrorCollector collect) {
		// parses the list of arguments to transform it into a map of args (starting at 1)
		// Experimental right now
		ed.add("[");
		int size = args.size();
		noParenthesisAroundPairs = true;
		for ( int i = 0; i < size; i++ ) {
			Expression e = args.get(i);
			ed.add("arg" + i + "::");
			conv(ed, e, collect);
			if ( i < size - 1 ) {
				ed.add(",");
			}
		}
		ed.add("]");
		noParenthesisAroundPairs = false;

	}

	private static void left(final ExpressionDescription ed, final Expression expr,
		final ErrorCollector collect) {
		Expression e = expr.getLeft();
		if ( e != null ) {
			argument(ed, e, collect);
		}
	}

	private static void right(final ExpressionDescription ed, final Expression expr,
		final ErrorCollector collect) {
		Expression e = expr.getRight();
		if ( e != null ) {
			argument(ed, e, collect);
		}
	}

	private static void argument(final ExpressionDescription ed, final Expression e,
		final ErrorCollector collect) {
		final boolean noParenthesis =
			e instanceof TerminalExpression || e instanceof VariableRef || e instanceof Array;
		if ( !noParenthesis ) {
			ed.add("(");
		}
		conv(ed, e, collect);
		if ( !noParenthesis ) {
			ed.add(")");
		}

	}

	private static void convArray(final ExpressionDescription ed, final Array r,
		final ErrorCollector collect) {
		ed.add("[");
		EList<Expression> exprs = r.getExprs();
		int size = exprs.size();
		noParenthesisAroundPairs = true;
		for ( int i = 0; i < size; i++ ) {
			Expression e = exprs.get(i);
			conv(ed, e, collect);
			if ( i < size - 1 ) {
				ed.add(",");
			}
		}
		ed.add("]");
		noParenthesisAroundPairs = false;

	}

}
