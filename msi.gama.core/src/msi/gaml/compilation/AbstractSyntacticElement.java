/**
 * Created by drogoul, 15 sept. 2013
 * 
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gaml.descriptions.*;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;

/**
 * Class AbstractSyntacticElement.
 * 
 * @author drogoul
 * @since 15 sept. 2013
 * 
 */
public abstract class AbstractSyntacticElement implements ISyntacticElement {

	protected String[] keys = new String[0];
	protected IExpressionDescription[] values = new IExpressionDescription[0];

	AbstractSyntacticElement(final String keyword, final Facets facets) {
		if ( facets != null ) {
			Facet[] tab = facets.entrySet();
			List<String> tempKeys = new ArrayList(tab.length);
			List<IExpressionDescription> tempValues = new ArrayList(tab.length);
			for ( Facet f : tab ) {
				if ( f != null ) {
					tempKeys.add(f.getKey());
					tempValues.add(f.getValue());
				}
			}

			keys = tempKeys.toArray(new String[tempKeys.size()]);
			values = tempValues.toArray(new IExpressionDescription[tempKeys.size()]);
		}
		setKeyword(keyword);
	}

	@Override
	public ISyntacticElement[] getChildren() {
		return EMPTY_ARRAY;
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		throw new RuntimeException("No children allowed");
	}

	@Override
	public void setKeyword(final String name) {
		setFacet(IKeyword.KEYWORD, LabelExpressionDescription.create(name));
	}

	@Override
	public String getKeyword() {
		return StringUtils.toJavaString(getExpressionAt(IKeyword.KEYWORD).toString());
	}

	@Override
	public boolean hasFacet(final String name) {
		return lookupFacet(name) != -1;
	}

	@Override
	public IExpressionDescription getExpressionAt(final String name) {
		int pos = lookupFacet(name);
		if ( pos == -1 ) { return null; }
		return values[pos];
	}

	@Override
	public Facets copyFacets() {
		Facets ff = new Facets();
		for ( int i = 0; i < keys.length; i++ ) {
			ff.put(keys[i], values[i].cleanCopy());
		}
		return ff;
	}

	@Override
	public void setFacet(final String string, final IExpressionDescription expr) {
		int pos = lookupFacet(string);
		if ( pos == -1 ) {
			addFacet(string, expr);
		} else {
			values[pos] = expr;
		}

	}

	@Override
	public String getName() {
		IExpressionDescription expr = getExpressionAt(IKeyword.NAME);
		return expr == null ? null : expr.toString();
	}

	@Override
	public boolean isSynthetic() {
		return true;
	}

	@Override
	public boolean isSpecies() {
		return false;
	}

	@Override
	public boolean isGlobal() {
		return false;
	}

	@Override
	public boolean isExperiment() {
		return false;
	}

	@Override
	public void dump() {
		StringBuilder sb = new StringBuilder(256);
		dump(sb);
		System.out.println(sb.toString());
	}

	private void dump(final StringBuilder sb) {
		sb.append(getKeyword()).append(" ");
		for ( int i = 0; i < keys.length; i++ ) {
			sb.append(keys[i]).append(": ").append(values[i]).append(" ");
		}
		sb.append("\n");
		if ( getChildren().length != 0 ) {
			sb.append('[');
		}
		for ( ISyntacticElement elt : getChildren() ) {
			((AbstractSyntacticElement) elt).dump(sb);
		}
		if ( getChildren().length != 0 ) {
			sb.append(']');
		}
	}

	private int lookupFacet(final String key) {
		for ( int i = 0; i < keys.length; i++ ) {
			if ( keys[i].equals(key) ) { return i; }
		}
		return -1;
	}

	private void addFacet(final String key, final IExpressionDescription desc) {
		if ( key == null ) { return; }
		if ( keys == null ) {
			keys = new String[1];
			values = new IExpressionDescription[1];
		} else {
			keys = Arrays.copyOf(keys, keys.length + 1);
			values = Arrays.copyOf(values, values.length + 1);
		}
		keys[keys.length - 1] = key;
		values[values.length - 1] = desc;
	}

}