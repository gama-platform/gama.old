/**
 * Created by drogoul, 9 sept. 2013
 * 
 */
package msi.gaml.compilation;

import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * Class ISyntacticElement.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public interface ISyntacticElement {

	final static ISyntacticElement[] EMPTY_ARRAY = new ISyntacticElement[0];

	public abstract void setKeyword(final String name);

	public abstract void dump();

	public abstract String getKeyword();

	public abstract boolean hasFacet(final String name);

	public abstract IExpressionDescription getExpressionAt(final String name);

	public abstract Facets copyFacets();

	public abstract void setFacet(final String string, final IExpressionDescription expr);

	public abstract ISyntacticElement[] getChildren();

	public abstract String getName();

	public abstract EObject getElement();

	public abstract void addChild(final ISyntacticElement e);

	public abstract boolean isSynthetic();

	public abstract boolean isSpecies();

	public abstract boolean isGlobal();

	public abstract boolean isExperiment();

}