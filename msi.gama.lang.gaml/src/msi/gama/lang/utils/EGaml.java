/**
 * Created by drogoul, 7 févr. 2012
 * 
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.descriptions.SymbolMetaDescription;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

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

	public static boolean isUnary(final Expression e) {
		return e instanceof FunctionRef || e instanceof VariableRef &&
			((VariableRef) e).getRef() instanceof DefUnary;
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
			return ref.getRef() == null ? "" : ref.getRef().getName();
		}
		if ( f instanceof Model ) { return IKeyword.MODEL; }
		return null;
	}

	public static String getParentKeyOf(final EObject f) {
		if ( f == null ) { return null; }
		EObject parent = f.eContainer();
		if ( parent instanceof Block ) { return getParentKeyOf(parent); }
		return getKeyOf(parent);
	}

	public static String getLabelFromFacet(final Statement container, final String facet) {
		Map<String, Expression> facets = getFacetsOf(container);
		Expression expr = facets.get(facet);
		if ( expr == null ) { return null; }
		if ( expr instanceof VariableRef ) {
			VariableRef ref = (VariableRef) expr;
			return getKeyOf(ref);
		}
		return null;
	}

	public static GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	public static TerminalExpression createTerminal(final String id) {
		if ( id != null ) {
			TerminalExpression expr = getFactory().createTerminalExpression();
			expr.setValue(id);
			return expr;
		}
		return null;
	}

	public static Expression getValueOfOmittedFacet(final Statement s) {
		if ( s instanceof Definition ) {
			String var = ((Definition) s).getName();
			TerminalExpression t = createTerminal(var);
			if ( t != null ) { return t; }
		}
		return s.getExpr();
	}

	public static Block getBlockOf(final Statement f) {
		return f.getBlock();
	}

	public static Map<String, Expression> getFacetsOf(final Statement f) {
		// if ( f instanceof SetEval ) {
		// Map<String, Expression> result = new HashMap();
		// result.put(IKeyword.VALUE, ((SetEval) f).getValue());
		// return result;
		// }
		return translateFacets(getKeyOf(f), f.getFacets());
	}

	private static Map<String, Expression> translateFacets(final String statement,
		final EList<? extends FacetExpr> facets) {
		Map<String, Expression> result = new HashMap();
		for ( FacetExpr f : facets ) {
			String key = getKeyOf(f);
			if ( f instanceof DefinitionFacetExpr ) {
				result.put(key, createTerminal(((DefinitionFacetExpr) f).getName()));
			} else {
				if ( key != null ) {
					if ( key.equals("<-") ) {
						key =
							statement.equals(IKeyword.LET) || statement.equals(IKeyword.SET)
								? IKeyword.VALUE : IKeyword.INIT;
					} else if ( key.equals("->") ) {
						key = IKeyword.FUNCTION;
					}
					result.put(key, f.getExpr());
				}
			}
		}
		return result;
	}

	// A fake expression to replace IDs when they are encountered by the parser
	public static class LiteralExpression extends MinimalEObjectImpl implements Expression {

		String literal;

		public LiteralExpression(final String s) {
			literal = s;
		}

		public String getLiteral() {
			return literal;
		}

		/**
		 * @see msi.gama.lang.gaml.gaml.Expression#getLeft()
		 */
		@Override
		public Expression getLeft() {
			return null;
		}

		/**
		 * @see msi.gama.lang.gaml.gaml.Expression#setLeft(msi.gama.lang.gaml.gaml.Expression)
		 */
		@Override
		public void setLeft(final Expression value) {}

		/**
		 * @see msi.gama.lang.gaml.gaml.Expression#getOp()
		 */
		@Override
		public String getOp() {
			return null;
		}

		/**
		 * @see msi.gama.lang.gaml.gaml.Expression#setOp(java.lang.String)
		 */
		@Override
		public void setOp(final String value) {}

		/**
		 * @see msi.gama.lang.gaml.gaml.Expression#getRight()
		 */
		@Override
		public Expression getRight() {
			return null;
		}

		/**
		 * @see msi.gama.lang.gaml.gaml.Expression#setRight(msi.gama.lang.gaml.gaml.Expression)
		 */
		@Override
		public void setRight(final Expression value) {}

	}

}
