/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;
import org.eclipse.emf.ecore.EObject;

/**
 * The class SyntacticElement.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public class SyntacticElement implements ISyntacticElement {

	private final Facets facets;
	List<ISyntacticElement> children;
	final EObject element;
	int category = -1;

	public SyntacticElement(final String keyword, final Facets facets) {
		this(keyword, facets, null);
	}

	public SyntacticElement(final String keyword, final EObject statement) {
		this(keyword, new Facets(), statement);
	}

	public SyntacticElement(final String keyword, final Facets facets, final EObject statement) {
		this.facets = facets;
		setKeyword(keyword);
		this.element = statement;
	}

	@Override
	public void setCategory(final int cat) {
		category = cat;
	}

	@Override
	public void setKeyword(final String name) {
		facets.putAsLabel(IKeyword.KEYWORD, name);
	}

	public void dump() {
		StringBuilder sb = new StringBuilder(256);
		dump(sb);
		System.out.println(sb.toString());
	}

	private void dump(final StringBuilder sb) {
		sb.append(facets.getLabel(IKeyword.KEYWORD)).append(" ");
		for ( Facet f : facets.entrySet() ) {
			sb.append(f.getKey()).append(": ").append(f.getValue()).append(" ");
		}
		sb.append("\n");
		if ( !getChildren().isEmpty() ) {
			sb.append('[');
		}
		for ( ISyntacticElement elt : getChildren() ) {
			((SyntacticElement) elt).dump(sb);
		}
		if ( !getChildren().isEmpty() ) {
			sb.append(']');
		}
	}

	@Override
	public String getKeyword() {
		return facets.getLabel(IKeyword.KEYWORD);
	}

	@Override
	public IExpressionDescription getFacet(final String name) {
		return facets.get(name);
	}

	@Override
	public Facets getFacets() {
		return facets;
	}

	@Override
	public void setFacet(final String string, final IExpressionDescription expr) {
		facets.put(string, expr);
	}

	@Override
	public List<ISyntacticElement> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

	@Override
	public List<ISyntacticElement> getSpeciesChildren() {
		if ( !isSpecies() && !isGlobal() || children == null ) { return Collections.EMPTY_LIST; }
		List<ISyntacticElement> result = new ArrayList();
		for ( ISyntacticElement e : getChildren() ) {
			if ( e.isSpecies() || e.isExperiment() ) {
				result.add(e);
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return getLabel(IKeyword.NAME);
	}

	@Override
	public EObject getElement() {
		return element;
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		if ( e == null ) { return; }
		if ( children == null ) {
			children = new ArrayList();
		}
		children.add(e);
	}

	@Override
	public String getLabel(final String name) {
		IExpressionDescription s = getFacet(name);
		if ( s == null ) { return null; }
		return s.toString();
	}

	@Override
	public boolean isSynthetic() {
		return element == null;
	}

	@Override
	public boolean isSpecies() {
		return category == IS_SPECIES;
	}

	@Override
	public boolean isGlobal() {
		return category == IS_GLOBAL;
	}

	@Override
	public boolean isExperiment() {
		return category == IS_EXPERIMENT;
	}

}
