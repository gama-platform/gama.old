/**
 * Created by drogoul, 7 févr. 2012
 * 
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.descriptions.SymbolMetaDescription;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * The class EGaml. A bunch of utilities to work with the various GAML statements and expressions.
 * 
 * @author drogoul
 * @since 7 févr. 2012
 * 
 */
public class EGaml {

	// FIXME Static reference that should not be cached in case of dynamic plugins to GAML
	// FIXME Does not take additions into account !!!
	private static GamlProperties allowedChildren;
	final static Set<String> modelChildren = new HashSet(Arrays.asList(IKeyword.GLOBAL,
		IKeyword.ENVIRONMENT, IKeyword.ENTITIES, IKeyword.OUTPUT, IKeyword.EXPERIMENT));

	public static Set<String> getAllowedFacetsFor(final Statement s) {
		return getAllowedFacetsFor(getKeyOf(s));
	}

	public static Set<String> getAllowedFacetsFor(final String key) {
		if ( key == null ) { return Collections.EMPTY_SET; }
		SymbolMetaDescription md = null;
		md = DescriptionFactory.getModelFactory().getMetaDescriptionFor(null, key);
		Set<String> result = md == null ? null : md.getPossibleFacets().keySet();
		return result == null ? Collections.EMPTY_SET : result;
	}

	// public static boolean isUnary(final Expression e) {
	// return e instanceof FunctionRef || e instanceof VariableRef &&
	// ((VariableRef) e).getRef() instanceof DefUnary;
	// }
	//
	public static Array varDependenciesOf(final Statement s) {
		// Set<VariableRef> result = new HashSet();
		Set<String> result = new HashSet();
		for ( FacetExpr facet : s.getFacets() ) {
			Expression expr = facet.getExpr();
			if ( expr == null ) {
				continue;
			}
			// Modified in order to avoir calling the linking services before linking has been done
			List<VariableRef> elements = EcoreUtil2.eAllOfType(expr, VariableRef.class);
			for ( VariableRef var : elements ) {
				String name = NodeModelUtils.getTokenText(NodeModelUtils.getNode(var));
				// if ( !isUnary(var) ) {
				result.add(name);
				// result.add(EGaml.createTerminal(getKeyOf(var)));
				// result.add(EcoreUtil2.clone(var)); // Necessary to clone otherwise the
				// EObjects
				// will be removed from the facet !
				// }
			}

		}
		Array a = null;
		if ( !result.isEmpty() ) {
			// GuiUtils.debug("Dep found : " + result);
			a = getFactory().createArray();
			for ( String name : result ) {
				a.getExprs().add(EGaml.createTerminal(name));
			}
		}
		return a;
	}

	public static Set<String> getAllowedChildrenForModel() {
		return modelChildren;
	}

	public static Set<String> getAllowedChildrenFor(final EObject s) {
		if ( s instanceof Model ) { return getAllowedChildrenForModel(); }
		return getAllowedChildrenFor(getKeyOf(s));
	}

	public static Set<String> getAllowedChildrenFor(final String key) {
		if ( key == null ) { return Collections.EMPTY_SET; }
		if ( allowedChildren == null ) {
			allowedChildren = GamlProperties.loadFrom(GamlProperties.CHILDREN);
			allowedChildren.put(IKeyword.MODEL, modelChildren);
		}
		Set<String> result = allowedChildren.get(key);
		return result == null ? Collections.EMPTY_SET : result;
	}

	public static String getKeyOf(final EObject f) {
		if ( f instanceof StringLiteral ) { return ((StringLiteral) f).getValue(); }
		if ( f instanceof FacetExpr ) {
			FacetRef ref = ((FacetExpr) f).getKey();
			if ( ref != null ) { return ref.getRef(); }
			if ( f instanceof ReturnsFacetExpr ) { return IKeyword.RETURNS; }
			if ( f instanceof NameFacetExpr ) { return IKeyword.NAME; }
			return null;
		}
		if ( f instanceof Statement ) { return ((Statement) f).getKey(); }
		if ( f instanceof VariableRef ) {
			VariableRef ref = (VariableRef) f;
			// return NodeModelUtils.findActualNodeFor(ref).getText();
			return ref.getRef() == null ? "" : ref.getRef().getName();
		}
		if ( f instanceof FunctionRef ) {
			FunctionRef ref = (FunctionRef) f;
			return getKeyOf(ref.getLeft());
		}
		if ( f instanceof Expression ) { return ((Expression) f).getOp(); }
		if ( f instanceof Model ) { return IKeyword.MODEL; }
		return null;
	}

	public static String getLabelFromFacet(final Statement container, final String facet) {
		for ( FacetExpr f : container.getFacets() ) {
			if ( getKeyOf(f).equals(facet) ) {
				Expression expr = f.getExpr();
				if ( expr instanceof VariableRef ) { return getKeyOf(expr); }
			}
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

	public static Expression getValueOfOmittedFacet(final Statement s) {
		if ( s instanceof Definition ) {
			String var = ((Definition) s).getName();
			return createTerminal(var);
		}
		return s.getExpr();
	}

	public static Block getBlockOf(final Statement f) {
		return f.getBlock();
	}

	/**
	 * Creates a new Pair from two expressions. The expressions are cloned to avoid any side-effect
	 * on the syntactic tree
	 * 
	 * @param key the key of the PairExpr to create
	 * @param expression the value of the PairExpr to create
	 * @return
	 */
	public static Expression createPairExpr(final Expression key, final Expression value) {
		PairExpr pair = getFactory().createPairExpr();
		pair.setLeft(key);
		pair.setRight(/* EcoreUtil2.cloneWithProxies( */value)/* ) */;
		pair.setOp("::");
		return pair;
	}

	public static void createFacetExpr(final Statement container, final String key,
		final Expression value) {
		FacetExpr pair = getFactory().createFacetExpr();
		container.getFacets().add(pair);
		FacetRef ref = getFactory().createFacetRef();
		ref.setRef(key);
		pair.setKey(ref);
		pair.setExpr(value);
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
			// collect.add(new GamlParsingError("an expression is expected", currentStatement));
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
