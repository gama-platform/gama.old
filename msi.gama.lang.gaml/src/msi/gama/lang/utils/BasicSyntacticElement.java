/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;
import msi.gama.lang.gaml.gaml.Expression;
import org.eclipse.emf.ecore.EObject;

/**
 * The class BasicSyntacticElement.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public class BasicSyntacticElement implements ISyntacticElement {

	ErrorCollector collect; // for the moment, each exception points to it. Needs to be changed
	Object statement;
	ISyntacticElement parent;
	String keyword;
	Map<String, Expression> expressions = new HashMap();
	Map<String, String> facets = new HashMap();
	List<ISyntacticElement> children = new ArrayList();

	public void dump() {
		StringBuilder sb = new StringBuilder();
		dump(sb);
		System.out.println(sb.toString());
	}

	/**
	 * @param sb
	 */
	private void dump(final StringBuilder sb) {
		sb.append(keyword).append(" ");

		for ( String key : facets.keySet() ) {
			sb.append(key).append(": ").append(facets.get(key)).append(" ");
		}

		sb.append("\n");

		if ( !children.isEmpty() ) {
			sb.append('[');
		}
		for ( ISyntacticElement elt : children ) {
			((BasicSyntacticElement) elt).dump(sb);
		}
		if ( !children.isEmpty() ) {
			sb.append(']');
		}
	}

	/**
	 * Instantiates a new Element
	 */
	public BasicSyntacticElement(final String keyword, final EObject statement,
		final ErrorCollector collect) {
		this.keyword = keyword;
		this.statement = statement;
		this.collect = collect;
	}

	@Override
	public void setName(final String name) {
		keyword = name;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getName()
	 */
	@Override
	public String getName() {
		return keyword;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getAttribute(java.lang.String)
	 */
	@Override
	public String getAttribute(final String name) {
		return facets.get(name);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		return facets;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setAttribute(final String string, final String string2, final Object obj) {
		facets.put(string, string2);
		if ( obj instanceof Expression ) {
			expressions.put(string, (Expression) obj);
		}
	}

	public void setAttribute(final String string, final String string2, final Expression expression) {
		facets.put(string, string2);
		expressions.put(string, expression);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getChildren()
	 */
	@Override
	public List<ISyntacticElement> getChildren() {
		return children;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getChildren(java.lang.String)
	 */
	@Override
	public List<ISyntacticElement> getChildren(final String name) {
		List<ISyntacticElement> result = new ArrayList();
		for ( ISyntacticElement e : children ) {
			if ( e.getName().equals(name) ) {
				result.add(e);
			}
		}
		return result;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getChild(java.lang.String)
	 */
	@Override
	public ISyntacticElement getChild(final String name) {
		for ( ISyntacticElement e : children ) {
			if ( e.getName().equals(name) ) { return e; }
		}
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#hasParent(java.lang.String)
	 */
	@Override
	public boolean hasParent(final String name) {
		return parent != null && parent.getName().equals(name);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getUnderlyingElement()
	 */
	@Override
	public Object getUnderlyingElement(final String facet) {
		if ( facet == null ) { return statement; }
		return expressions.get(facet);
	}

	@Override
	public void addContent(final ISyntacticElement e) {
		if ( e == null ) { return; }
		e.setParent(this);
		children.add(e);
	}

	@Override
	public void setParent(final ISyntacticElement p) {
		parent = p;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getErrorCollector()
	 */
	@Override
	public ErrorCollector getErrorCollector() {
		if ( collect == null ) {
			if ( parent != null ) { return parent.getErrorCollector(); }
			return null;
		} else {
			return collect;
		}
	}

}
