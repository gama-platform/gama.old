/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils.internal;

import java.io.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gaml.parser.xml.LineNumberElement;
import org.eclipse.emf.common.util.EList;
import org.jdom.*;
import org.jdom.output.XMLOutputter;

public class Gaml2JDOM {

	private static boolean noParenthesisAroundPairs = false;

	public static ISyntacticElement doConvert(final Model m) {
		// final Document doc = new Document();
		final LineNumberElement model = convModel(m);
		// doc.addContent(model);
		return new XmlSyntacticElement(model);
	}

	private static LineNumberElement convModel(final Model m) {
		final LineNumberElement model = new LineNumberElement("model");
		model.setAttribute("name", m.getName());
		for ( final Import i : m.getImports() ) {
			final String uri = i.getImportURI();
			if ( !uri.startsWith("platform:") ) {
				final Element include = new LineNumberElement("include");
				include.setAttribute("file", uri);
				model.addContent(include);
			}
		}
		convStatements(model, m.getStatements());
		return model;
	}

	private static void convStatements(final Element elt, final EList<Statement> ss) {
		for ( final Statement stm : ss ) {
			if ( stm instanceof SetEval ) {
				elt.addContent(convSetEval((SetEval) stm));
			} else {
				boolean isAbstract = true;
				try {
					SubStatement substm = (SubStatement) stm;
					GamlKeywordRef key = substm.getKey();
					DefKeyword ref = key.getRef();
					String name = ref.getName();
					isAbstract = name.equals("abstract");
					// ((SubStatement) stm).getKey().getRef().getName().equals("abstract");
				} catch (Exception e) {}
				if ( !isAbstract ) {
					elt.addContent(convStatement(stm));
				}
			}
		}
	}

	private static Element convSetEval(final SetEval stm) {
		final LineNumberElement elt = new LineNumberElement("set");// , stm);
		elt.setAttribute("var", convExpression(stm.getVar()));
		for ( final FacetExpr f : stm.getFacets() ) {
			elt.setAttribute(f.getKey().getRef().getName(), convExpression(f.getExpr()));
		}
		return elt;
	}

	private static Element convStatement(final Statement stm) {
		if ( stm instanceof Definition ) { return convDefinition((Definition) stm); }
		// if (stm instanceof Evaluation)
		return convEval((Evaluation) stm);
	}

	private static Element convEval(final Evaluation swd) {
		final LineNumberElement elt = new LineNumberElement(swd.getKey().getRef().getName(), swd);

		if ( swd.getVar() != null ) {
			elt.setAttribute("var", convExpression(swd.getVar()));
		}
		for ( final FacetExpr f : swd.getFacets() ) {
			if ( f.getName() != null ) {
				elt.setAttribute("returns", f.getName());
			} else {
				elt.setAttribute(f.getKey().getRef().getName(), convExpression(f.getExpr()));
			}
		}
		if ( swd.getBlock() != null ) {
			convStatements(elt, swd.getBlock().getStatements());
		}

		return elt;
	}

	private static Element convDefinition(final Definition d) {
		final String kw = d.getKey().getRef().getName();
		final LineNumberElement elt = new LineNumberElement(kw, d);
		if ( kw.equals("const") ) {
			elt.setName("var");
			elt.setAttribute("const", "true");
		}
		if ( kw.equals("let") || kw.equals("loop") ) {
			elt.setAttribute("var", d.getName());
		} else {
			elt.setAttribute("name", d.getName());
		}

		for ( final FacetExpr f : d.getFacets() ) {
			String name = f.getKey() == null ? null : f.getKey().getRef().getName();
			if ( name == null ) {
				continue;
			}
			String expr = convExpression(f.getExpr());
			if ( name.equals("skills") && expr.charAt(0) == '[' ) {
				expr = expr.substring(1, expr.length() - 1);
			}
			elt.setAttribute(name, expr);
		}

		if ( d.getBlock() != null ) {
			convStatements(elt, d.getBlock().getStatements());
		}

		return elt;
	}

	private static String convExpression(final Expression expr) {
		String s = "";

		if ( expr == null ) {
			throw new NullPointerException("an Expression was expected");
			// } else if (expr instanceof Assign) {
			// s = "(" + convExpression(((Assign) expr).getLeft()) + "="
			// + convExpression(((Assign) expr).getRight()) + ")";
		} /*
		 * else if ( expr instanceof AssignPlus ) {
		 * s =
		 * "(" + convExpression(((AssignPlus) expr).getLeft()) + "+=" +
		 * convExpression(((AssignPlus) expr).getRight()) + ")";
		 * } else if ( expr instanceof AssignMin ) {
		 * s =
		 * "(" + convExpression(((AssignMin) expr).getLeft()) + "-=" +
		 * convExpression(((AssignMin) expr).getRight()) + ")";
		 * } else if ( expr instanceof AssignMult ) {
		 * s =
		 * "(" + convExpression(((AssignMult) expr).getLeft()) + "*=" +
		 * convExpression(((AssignMult) expr).getRight()) + ")";
		 * } else if ( expr instanceof AssignDiv ) {
		 * s =
		 * "(" + convExpression(((AssignDiv) expr).getLeft()) + "/=" +
		 * convExpression(((AssignDiv) expr).getRight()) + ")";
		 * }
		 */else if ( expr instanceof Or ) {
			s =
				"(" + convExpression(((Or) expr).getLeft()) + " or " +
					convExpression(((Or) expr).getRight()) + ")";
		} else if ( expr instanceof And ) {
			s =
				"(" + convExpression(((And) expr).getLeft()) + " and " +
					convExpression(((And) expr).getRight()) + ")";
		} else if ( expr instanceof RelEq ) {
			s =
				"(" + convExpression(((RelEq) expr).getLeft()) + " = " +
					convExpression(((RelEq) expr).getRight()) + ")";
		} else if ( expr instanceof RelNotEq ) {
			s =
				"(" + convExpression(((RelNotEq) expr).getLeft()) + " != " +
					convExpression(((RelNotEq) expr).getRight()) + ")";
		} else if ( expr instanceof RelEqEq ) {
			s =
				"(" + convExpression(((RelEqEq) expr).getLeft()) + " == " +
					convExpression(((RelEqEq) expr).getRight()) + ")";
		} else if ( expr instanceof RelLtEq ) {
			s =
				"(" + convExpression(((RelLtEq) expr).getLeft()) + " >= " +
					convExpression(((RelLtEq) expr).getRight()) + ")";
		} else if ( expr instanceof RelGtEq ) {
			s =
				"(" + convExpression(((RelGtEq) expr).getLeft()) + " <= " +
					convExpression(((RelGtEq) expr).getRight()) + ")";
		} else if ( expr instanceof RelLt ) {
			s =
				"(" + convExpression(((RelLt) expr).getLeft()) + " < " +
					convExpression(((RelLt) expr).getRight()) + ")";
		} else if ( expr instanceof RelGt ) {
			s =
				"(" + convExpression(((RelGt) expr).getLeft()) + " > " +
					convExpression(((RelGt) expr).getRight()) + ")";
		} else if ( expr instanceof Plus ) {
			s =
				"(" + convExpression(((Plus) expr).getLeft()) + " + " +
					convExpression(((Plus) expr).getRight()) + ")";
		} else if ( expr instanceof Minus ) {
			s =
				"(" + convExpression(((Minus) expr).getLeft()) + " - " +
					convExpression(((Minus) expr).getRight()) + ")";
		} else if ( expr instanceof Multi ) {
			s =
				"(" + convExpression(((Multi) expr).getLeft()) + " * " +
					convExpression(((Multi) expr).getRight()) + ")";
		} else if ( expr instanceof Div ) {
			s =
				"(" + convExpression(((Div) expr).getLeft()) + " / " +
					convExpression(((Div) expr).getRight()) + ")";
		} else if ( expr instanceof GamlBinary ) {
			s =
				"(" + convExpression(((GamlBinary) expr).getLeft()) + ' ' +
					((GamlBinary) expr).getOp().getRef().getName() + ' ' +
					convExpression(((GamlBinary) expr).getRight()) + ")";
		} else if ( expr instanceof GamlUnary ) {
			s =
				"(" + ((GamlUnary) expr).getOp() + ' ' +
					convExpression(((GamlUnary) expr).getRight()) + ")";
		} else if ( expr instanceof TerminalExpression ) {
			s = convTermExpr((TerminalExpression) expr);
		} else if ( expr instanceof Pow ) {
			s = convPow((Pow) expr);
		} else if ( expr instanceof Pair ) {
			s = convPair((Pair) expr);
		} else if ( expr instanceof Unit ) {
			s = convUnit((Unit) expr);
		} else if ( expr instanceof Point ) {
			s = convPoint((Point) expr);
		} else if ( expr instanceof Matrix ) {
			s = convMatrix((Matrix) expr);
		} else if ( expr instanceof Ternary ) {
			s =
				"(" + convExpression(((Ternary) expr).getCondition()) + '?' +
					convExpression(((Ternary) expr).getIfTrue()) + ':' +
					convExpression(((Ternary) expr).getIfFalse()) + ")";
		} else if ( expr instanceof VariableRef ) {
			s = convVarRef((VariableRef) expr);
		} else if ( expr instanceof FunctionRef ) {
			s = convFuncRef((FunctionRef) expr);
		} else if ( expr instanceof ArrayRef ) {
			s = convArrayRef((ArrayRef) expr);
		} else if ( expr instanceof MemberRefR ) {
			s = convMemRefR((MemberRefR) expr);
		} else if ( expr instanceof MemberRefP ) {
			s = convMemRefP((MemberRefP) expr);
		} else {
			System.err.println("Gaml2JDOM.convExpression(" + expr + ") unknown type");
		}

		return s;
	}

	private static String convPow(final Pow expr) {
		return convExpression(expr.getLeft()) + "^" + convExpression(expr.getRight());
	}

	private static String convUnit(final Unit expr) {
		return convExpression(expr.getLeft()) + " " + expr.getRight().getRef().getName();
	}

	private static String convPair(final Pair expr) {

		String s = convExpression(expr.getLeft()) + "::" + convExpression(expr.getRight());
		return noParenthesisAroundPairs ? s : "(" + s + ")";
	}

	private static String convPoint(final Point expr) {
		return "{" + convExpression(expr.getX()) + "," + convExpression(expr.getY()) + "}";
	}

	private static String convMatrix(final Matrix expr) {
		String s = "[";
		for ( final Row r : expr.getRows() ) {
			if ( s.length() > 1 ) {
				s += "; ";
			}
			s += convRow(r);
		}
		return s + "]";
	}

	private static String convRow(final Row r) {
		String s = "";
		for ( final Expression e : r.getExprs() ) {
			if ( s.length() > 0 ) {
				s += ", ";
			}
			s += convExpression(e);
		}
		return s;
	}

	private static String convTermExpr(final TerminalExpression expr) {
		if ( expr instanceof DoubleLiteral ) {
			return ((DoubleLiteral) expr).getValue();
		} else if ( expr instanceof ColorLiteral ) {
			return ((ColorLiteral) expr).getValue();
		} else if ( expr instanceof StringLiteral ) {
			return "'" + ((StringLiteral) expr).getValue() + "'";
		} else if ( expr instanceof BooleanLiteral ) {
			return Boolean.toString(((BooleanLiteral) expr).isValue());
		} else if ( expr instanceof IntLiteral ) {
			return Integer.toString(((IntLiteral) expr).getValue());
		} else {
			System.err.println("Gaml2JDOM.convTermExpr(" + expr + ") unknown type");
		}

		return "";
	}

	private static String convMemRefP(final MemberRefP expr) {
		return "(" + convExpression(expr.getLeft()) + ")." + convExpression(expr.getRight());
	}

	private static String convMemRefR(final MemberRefR expr) {
		String left = convExpression(expr.getLeft());
		// TODO not working for schelling/segregation_gis2.xml.gaml
		// but needed for ants and boids models...
		if ( "self".equals(left) ) {
			// TO DO cheat "self.action []" -> "self action []"
			return "self " + convExpression(expr.getRight());
		}
		return left + "." + convExpression(expr.getRight());
	}

	private static String convArrayRef(final ArrayRef expr) {
		return convVarRef(expr.getArray()) + convArrayKey(expr.getArgs());
	}

	private static String convFuncRef(final FunctionRef expr) {
		return convVarRef(expr.getFunc()) + convArgs(expr.getArgs());
	}

	private static String convVarRef(final VariableRef expr) {
		return expr.getRef().getName();
	}

	private static String convArrayKey(final EList<Expression> args) {
		if ( args.isEmpty() ) { return "[]"; }
		noParenthesisAroundPairs = true;
		String s = "[";
		for ( final Expression arg : args ) {
			if ( s.length() > 2 ) {
				s += ", ";
			}
			s += convExpression(arg);
		}
		noParenthesisAroundPairs = false;
		return s + "]";
	}

	private static String convArgs(final EList<Expression> args) {
		if ( args.isEmpty() ) { return "()"; }
		String s = "(";
		for ( final Expression arg : args ) {
			if ( s.length() > 2 ) {
				s += ", ";
			}
			s += convExpression(arg);
		}
		return s + ")";
	}

	public static void writeTo(final Document doc, final OutputStream os) throws IOException {
		final XMLOutputter xo = new XMLOutputter();
		xo.output(doc, os);
	}

	public static void print(final Document doc) throws IOException {
		final XMLOutputter xo = new XMLOutputter();
		xo.output(doc, System.out);
	}

	public static String str(final Document doc) {
		final XMLOutputter xo = new XMLOutputter();
		return xo.outputString(doc);
	}
}
