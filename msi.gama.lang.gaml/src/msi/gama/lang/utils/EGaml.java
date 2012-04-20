/**
 * Created by drogoul, 7 févr. 2012
 * 
 */
package msi.gama.lang.utils;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.gaml.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * The class EGaml. A bunch of utilities to work with the various GAML statements and expressions.
 * 
 * @author drogoul
 * @since 7 févr. 2012
 * 
 */
public class EGaml {

	public static String getKeyOf(final EObject f) {
		if ( f instanceof StringLiteral ) { return ((StringLiteral) f).getValue(); }
		if ( f instanceof FacetExpr ) {
			FacetRef ref = ((FacetExpr) f).getKey();
			if ( ref != null ) { return ref.getRef(); }
			if ( f instanceof ReturnsFacetExpr ) { return IKeyword.RETURNS; }
			if ( f instanceof NameFacetExpr ) { return IKeyword.NAME; }
			if ( f instanceof ActionFacetExpr ) { return IKeyword.ACTION; }
			return null;
		}
		if ( f instanceof Statement ) { return ((Statement) f).getKey(); }
		if ( f instanceof VariableRef ) { return NodeModelUtils.getTokenText(NodeModelUtils
			.getNode(f));

		// VariableRef ref = (VariableRef) f;
		// return ref.getRef() == null ? "" : ref.getRef().getName();
		}
		if ( f instanceof FunctionRef ) {
			FunctionRef ref = (FunctionRef) f;
			return getKeyOf(ref.getLeft());
		}
		if ( f instanceof ArbitraryName ) { return ((ArbitraryName) f).getName(); }
		if ( f instanceof UnitName ) { return ((UnitName) f).getName(); }
		if ( f instanceof Expression ) { return ((Expression) f).getOp(); }
		if ( f instanceof Model ) { return IKeyword.MODEL; }
		return null;
	}

	public static String getLabelFromFacet(final Statement container, final String facet) {
		for ( FacetExpr f : container.getFacets() ) {
			if ( getKeyOf(f).equals(facet) ) { return f instanceof DefinitionFacetExpr
				? ((DefinitionFacetExpr) f).getName() : getKeyOf(f.getExpr()); }
		}
		return null;
	}

	public static GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	public static StringLiteral createTerminal(final String id) {
		if ( id != null ) {
			StringLiteral expr = getFactory().createStringLiteral();
			expr.setValue(id);
			return expr;
		}
		return null;
	}

	public static BooleanLiteral createTerminal(final boolean b) {
		BooleanLiteral expr = getFactory().createBooleanLiteral();
		expr.setValue(b ? IKeyword.TRUE : IKeyword.FALSE);
		return expr;
	}

	final static StringBuilder serializer = new StringBuilder();

	public static String toString(final Expression expr) {
		if ( expr == null ) { return null; }
		serializer.setLength(0);
		serialize(expr);
		return serializer.toString();
	}

	private static void serialize(final Expression expr) {
		if ( expr == null ) {
			return;
		} else if ( expr instanceof TernExp ) {
			serializer.append("(");
			serialize(expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")").append(":");
			serialize(((TernExp) expr).getIfFalse());
		} else if ( expr instanceof StringLiteral ) {
			serializer.append(StringUtils.toGamlString(((StringLiteral) expr).getValue()));
		} else if ( expr instanceof TerminalExpression ) {
			serializer.append(((TerminalExpression) expr).getValue());
		} else if ( expr instanceof Point ) {
			serializer.append("{").append("(");
			serialize(expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")").append("}");
		} else if ( expr instanceof Array ) {
			array(((Array) expr).getExprs(), false);
		} else if ( expr instanceof VariableRef ) {
			serializer.append(getKeyOf(expr));
		} else if ( expr instanceof GamlUnaryExpr ) {
			serializer.append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")");
		} else if ( expr instanceof FunctionRef ) {
			function((FunctionRef) expr);
		} else {
			serializer.append("(");
			serialize(expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")");
		}
	}

	private static void function(final FunctionRef expr) {
		EList<Expression> args = expr.getArgs();
		String opName = EGaml.getKeyOf(expr.getLeft());
		if ( args.size() == 1 ) {
			serializer.append(opName).append("(");
			serialize(args.get(0));
			serializer.append(")");
		} else if ( args.size() == 2 ) {
			serializer.append("(");
			serialize(args.get(0));
			serializer.append(")").append(opName).append("(");
			serialize(args.get(1));
			serializer.append(")");
		} else {
			serializer.append("(");
			serialize(args.get(0));
			serializer.append(")").append(opName);
			array(args, true);
		}
	}

	private static void array(final EList<Expression> args, final boolean arguments) {
		// if arguments is true, parses the list to transform it into a map of args
		// (starting at 1); Experimental right now
		serializer.append("[");
		int size = args.size();
		for ( int i = 0; i < size; i++ ) {
			Expression e = args.get(i);
			if ( arguments ) {
				serializer.append("arg").append(i).append("::");
			}
			serialize(e);
			if ( i < size - 1 ) {
				serializer.append(",");
			}
		}
		serializer.append("]");
	}

}
