/**
 * Created by drogoul, 9 sept. 2013
 * 
 */
package msi.gaml.compilation;

import java.util.List;
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

	public static final int IS_GLOBAL = 0;
	public static final int IS_SPECIES = 1;
	public static final int IS_EXPERIMENT = 2;

	public abstract void setCategory(final int cat);

	public abstract void setKeyword(final String name);

	public abstract void dump();

	public abstract String getKeyword();

	public abstract boolean hasFacet(final String name);

	public abstract IExpressionDescription getFacet(final String name);

	public abstract Facets copyFacets();

	public abstract void setFacet(final String string, final IExpressionDescription expr);

	public abstract List<ISyntacticElement> getChildren();

	public abstract List<ISyntacticElement> getSpeciesChildren();

	public abstract String getName();

	public abstract EObject getElement();

	public abstract void addChild(final ISyntacticElement e);

	public abstract String getLabel(final String name);

	public abstract boolean isSynthetic();

	public abstract boolean isSpecies();

	public abstract boolean isGlobal();

	public abstract boolean isExperiment();

}