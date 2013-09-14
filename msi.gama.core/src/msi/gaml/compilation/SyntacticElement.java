/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gaml.descriptions.*;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;
import org.eclipse.emf.ecore.EObject;

/**
 * The class SyntacticElement.
 * 
 * @author drogoul
 * @since 5 f�vr. 2012
 * 
 */
public class SyntacticElement implements ISyntacticElement {

	private final Map<String, IExpressionDescription> facets = new HashMap();
	private String name;
	List<ISyntacticElement> children;
	final EObject element;
	int category = -1;

	SyntacticElement(final String keyword, final Facets facets, final EObject statement) {
		if ( facets != null ) {
			for ( Facet f : facets.entrySet() ) {
				if ( f != null ) {
					this.facets.put(f.getKey(), f.getValue()/* .cleanCopy() */);
				}
			}
		}
		// setName(getName());
		setKeyword(keyword);
		this.element = statement;
	}

	private void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setCategory(final int cat) {
		category = cat;
	}

	@Override
	public void setKeyword(final String name) {
		facets.put(IKeyword.KEYWORD, LabelExpressionDescription.create(name));
	}

	@Override
	public void dump() {
		StringBuilder sb = new StringBuilder(256);
		dump(sb);
		System.out.println(sb.toString());
	}

	private void dump(final StringBuilder sb) {
		sb.append(StringUtils.toJavaString(facets.get(IKeyword.KEYWORD).toString())).append(" ");
		for ( Map.Entry<String, IExpressionDescription> f : facets.entrySet() ) {
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
		return StringUtils.toJavaString(facets.get(IKeyword.KEYWORD).toString());
	}

	@Override
	public boolean hasFacet(final String name) {
		return facets.containsKey(name);
	}

	@Override
	public IExpressionDescription getFacet(final String name) {
		return facets.get(name);
	}

	@Override
	public Facets copyFacets() {
		Facets ff = new Facets();
		for ( Map.Entry<String, IExpressionDescription> f : facets.entrySet() ) {
			ff.put(f.getKey(), f.getValue().cleanCopy());

			// (f.getKey()).append(": ").append(f.getValue()).append(" ");
		}
		// return facets.cleanCopy();
		return ff;
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
		// return name;//
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
		IExpressionDescription s = facets.get(name);
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

	/**
	 * A method to know wheter an EObject is "contained" in this element or not. It is contained if:
	 * 
	 * 1) The EObject of this element is equal to the parameter, or
	 * 2) One of its facets IExpressionDescription EObject is equal to or is a container of the parameter, or
	 * 3)
	 * @param object
	 * @return
	 */
	public boolean contains(final EObject object) {
		return false;
	}

}
