/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;
import msi.gaml.commands.Facets;
import msi.gaml.descriptions.ExpressionDescription;

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
	Map<String, Object> expressions = new HashMap();
	Facets facets = new Facets();
	List<ISyntacticElement> children;

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

		if ( !getChildren().isEmpty() ) {
			sb.append('[');
		}
		for ( ISyntacticElement elt : getChildren() ) {
			((BasicSyntacticElement) elt).dump(sb);
		}
		if ( !getChildren().isEmpty() ) {
			sb.append(']');
		}
	}

	/**
	 * Instantiates a new Element
	 */
	public BasicSyntacticElement(final String keyword, final Object statement,
		final ErrorCollector collect) {
		this.keyword = keyword;
		this.statement = statement;
		this.collect = collect;
	}

	public BasicSyntacticElement(final String keyword, final Facets facets) {
		this.keyword = keyword;
		this.facets = facets;
		// collect ?
		// statement ?

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
	public ExpressionDescription getAttribute(final String name) {
		return facets.get(name);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getAttributes()
	 */
	@Override
	public Facets getAttributes() {
		return facets;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setAttribute(final String string, final ExpressionDescription string2,
		final Object obj) {
		facets.put(string, string2);
		expressions.put(string, obj);
	}

	@Override
	public void setAttribute(final String string, final String string2, final Object obj) {
		facets.put(string, new ExpressionDescription(string2));
		expressions.put(string, obj);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getChildren()
	 */
	@Override
	public List<ISyntacticElement> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getChildren(java.lang.String)
	 */
	@Override
	public List<ISyntacticElement> getChildren(final String name) {
		List<ISyntacticElement> result = new ArrayList();
		for ( ISyntacticElement e : getChildren() ) {
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
		for ( ISyntacticElement e : getChildren() ) {
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
	public Object getUnderlyingElement(final Object facet) {
		if ( facet == null ) { return statement; }
		if ( facet instanceof String ) { return expressions.containsKey(facet) ? expressions
			.get(facet) : statement; }
		return facet;
	}

	@Override
	public void addContent(final ISyntacticElement e) {
		if ( e == null ) { return; }
		e.setParent(this);
		if ( children == null ) {
			children = new ArrayList();
		}
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
		}
		return collect;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#isSynthetic()
	 */
	@Override
	public boolean isSynthetic() {
		return statement == null;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getLabel(java.lang.String)
	 */
	@Override
	public String getLabel(final String name) {
		ExpressionDescription s = getAttribute(name);
		if ( s == null ) { return null; }
		return s.toString();
	}

}
