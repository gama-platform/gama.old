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
public class SyntacticElement {

	public static final int IS_GLOBAL = 0;
	public static final int IS_SPECIES = 1;
	public static final int IS_EXPERIMENT = 2;

	private final Map<String, IExpressionDescription> facets = new HashMap();
	List<SyntacticElement> children;
	final EObject element;
	int category = -1;

	public SyntacticElement(final String keyword, final Facets facets) {
		this(keyword, facets, null);
	}

	public SyntacticElement(final String keyword, final EObject statement) {
		this(keyword, new Facets(), statement);
	}

	private SyntacticElement(final String keyword, final Facets facets, final EObject statement) {
		for ( Facet f : facets.entrySet() ) {
			if ( f != null ) {
				this.facets.put(f.getKey(), f.getValue()/* .cleanCopy() */);
			}
		}
		// this.facets = facets;
		setKeyword(keyword);
		this.element = statement;
	}

	public void setCategory(final int cat) {
		category = cat;
	}

	public void setKeyword(final String name) {
		facets.put(IKeyword.KEYWORD, LabelExpressionDescription.create(name));
	}

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
		for ( SyntacticElement elt : getChildren() ) {
			elt.dump(sb);
		}
		if ( !getChildren().isEmpty() ) {
			sb.append(']');
		}
	}

	public String getKeyword() {
		return StringUtils.toJavaString(facets.get(IKeyword.KEYWORD).toString());
	}

	public boolean hasFacet(final String name) {
		return facets.containsKey(name);
	}

	public IExpressionDescription getFacet(final String name) {
		return facets.get(name);
	}

	public Facets copyFacets() {
		Facets ff = new Facets();
		for ( Map.Entry<String, IExpressionDescription> f : facets.entrySet() ) {
			ff.put(f.getKey(), f.getValue().cleanCopy());

			// (f.getKey()).append(": ").append(f.getValue()).append(" ");
		}
		// return facets.cleanCopy();
		return ff;
	}

	public void setFacet(final String string, final IExpressionDescription expr) {
		facets.put(string, expr);
	}

	public List<SyntacticElement> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

	public List<SyntacticElement> getSpeciesChildren() {
		if ( !isSpecies() && !isGlobal() || children == null ) { return Collections.EMPTY_LIST; }
		List<SyntacticElement> result = new ArrayList();
		for ( SyntacticElement e : getChildren() ) {
			if ( e.isSpecies() || e.isExperiment() ) {
				result.add(e);
			}
		}
		return result;
	}

	public String getName() {
		return getLabel(IKeyword.NAME);
	}

	public EObject getElement() {
		return element;
	}

	public void addChild(final SyntacticElement e) {
		if ( e == null ) { return; }
		if ( children == null ) {
			children = new ArrayList();
		}
		children.add(e);
	}

	public String getLabel(final String name) {
		IExpressionDescription s = facets.get(name);
		if ( s == null ) { return null; }
		return s.toString();
	}

	public boolean isSynthetic() {
		return element == null;
	}

	public boolean isSpecies() {
		return category == IS_SPECIES;
	}

	public boolean isGlobal() {
		return category == IS_GLOBAL;
	}

	public boolean isExperiment() {
		return category == IS_EXPERIMENT;
	}

}
