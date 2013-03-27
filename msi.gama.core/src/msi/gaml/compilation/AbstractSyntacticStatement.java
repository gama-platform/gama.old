/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;

/**
 * The class AbstractStatementDescription.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public abstract class AbstractSyntacticStatement implements ISyntacticElement {

	ISyntacticElement parent;
	String keyword;
	protected final Facets facets;
	List<ISyntacticElement> children;
	private boolean isGlobal;
	private boolean isSpecies;
	private boolean isExperiment;

	public AbstractSyntacticStatement(final String keyword) {
		this.keyword = keyword;
		facets = new Facets();
	}

	@Override
	public void setDescription(final IDescription description) {}

	@Override
	public void removeDescription(final IDescription description) {}

	@Override
	public void setKeyword(final String name) {
		keyword = name;
	}

	@Override
	public void dispose() {
		parent = null;
		if ( children != null ) {
			children.clear();
			children = null;
		}
	}

	public void dump() {
		StringBuilder sb = new StringBuilder(256);
		dump(sb);
		System.out.println(sb.toString());
	}

	private void dump(final StringBuilder sb) {
		sb.append(keyword).append(" ");
		for ( Facet f : facets.entrySet() ) {
			sb.append(f.getKey()).append(": ").append(f.getValue()).append(" ");
		}
		sb.append("\n");
		if ( !getChildren().isEmpty() ) {
			sb.append('[');
		}
		for ( ISyntacticElement elt : getChildren() ) {
			((AbstractSyntacticStatement) elt).dump(sb);
		}
		if ( !getChildren().isEmpty() ) {
			sb.append(']');
		}
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getName()
	 */
	@Override
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getAttribute(java.lang.String)
	 */
	@Override
	public IExpressionDescription getFacet(final String name) {
		return facets.get(name);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getAttributes()
	 */
	@Override
	public Facets getFacets() {
		return facets;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setFacet(final String string, final IExpressionDescription string2) {
		facets.put(string, string2);
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
		if ( children == null ) { return Collections.EMPTY_LIST; }
		List<ISyntacticElement> result = new ArrayList();
		for ( ISyntacticElement e : getChildren() ) {
			if ( e.getKeyword().equals(name) ) {
				result.add(e);
			}
		}
		return result;
	}

	@Override
	public List<ISyntacticElement> getSpeciesChildren() {
		if ( !isSpecies() && !isGlobal() ) { return Collections.EMPTY_LIST; }
		if ( children == null ) { return Collections.EMPTY_LIST; }
		List<ISyntacticElement> result = new ArrayList();
		for ( ISyntacticElement e : getChildren() ) {
			if ( e.isSpecies() || e.isExperiment() ) {
				result.add(e);
			}
		}
		return result;
	}

	@Override
	public boolean isGrid() {
		return isSpecies() && getKeyword().equals(IKeyword.GRID);
	}

	@Override
	public String getName() {
		return getLabel(IKeyword.NAME);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getChild(java.lang.String)
	 */
	@Override
	public ISyntacticElement getChild(final String name) {
		for ( ISyntacticElement e : getChildren() ) {
			if ( e.getKeyword().equals(name) ) { return e; }
		}
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#hasParent(java.lang.String)
	 */
	@Override
	public boolean hasParent(final String name) {
		return parent != null && parent.getKeyword().equals(name);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getUnderlyingElement()
	 */
	@Override
	public Object getUnderlyingElement(final Object facet) {
		IExpressionDescription f = facets.get(facet);
		if ( f == null ) {
			if ( facet instanceof IExpressionDescription ) {
				f = (IExpressionDescription) facet;
			}
		}
		return f == null ? null : f.getTarget();
	}

	@Override
	public void addChild(final ISyntacticElement e) {
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
	 * @see msi.gama.common.interfaces.ISyntacticElement#getLabel(java.lang.String)
	 */
	@Override
	public String getLabel(final String name) {
		IExpressionDescription s = getFacet(name);
		if ( s == null ) { return null; }
		return s.toString();
	}

	@Override
	public boolean isExperiment() {
		return isExperiment;
	}

	public void setExperiment(boolean isExperiment) {
		this.isExperiment = isExperiment;
	}

	@Override
	public boolean isSpecies() {
		return isSpecies;
	}

	public void setSpecies(boolean isSpecies) {
		this.isSpecies = isSpecies;
	}

	@Override
	public boolean isGlobal() {
		return isGlobal;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

}
