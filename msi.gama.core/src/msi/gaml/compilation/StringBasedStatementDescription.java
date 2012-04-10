/**
 * Created by drogoul, 26 mars 2012
 * 
 */
package msi.gaml.compilation;

import msi.gaml.commands.Facets;
import msi.gaml.descriptions.IExpressionDescription;

/**
 * The class StringBasedStatementDescription.
 * 
 * @author drogoul
 * @since 26 mars 2012
 * 
 */
public class StringBasedStatementDescription extends AbstractStatementDescription {

	/**
	 * @param keyword
	 * @param facets
	 */
	public StringBasedStatementDescription(final String keyword, final Facets facets) {
		super(keyword);
		this.facets = facets;
	}

	/**
	 * @see msi.gaml.compilation.AbstractStatementDescription#isSynthetic()
	 */
	@Override
	public boolean isSynthetic() {
		return true;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setFacet(final String string, final IExpressionDescription string2) {
		facets.put(string, string2);
	}
}
