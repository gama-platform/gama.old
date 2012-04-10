/**
 * Created by drogoul, 26 mars 2012
 * 
 */
package msi.gama.lang.utils;

import msi.gama.common.util.IErrorCollector;
import msi.gaml.compilation.AbstractStatementDescription;
import msi.gaml.descriptions.IExpressionDescription;
import org.eclipse.emf.ecore.EObject;

/**
 * The class ECoreBasedStatementDescription.
 * 
 * @author drogoul
 * @since 26 mars 2012
 * 
 */
public class ECoreBasedStatementDescription extends AbstractStatementDescription {

	EObject statement;

	/**
	 * Instantiates a new Element
	 */
	public ECoreBasedStatementDescription(final String keyword, final EObject statement,
		final IErrorCollector collect) {
		super(keyword);
		this.statement = statement;
		this.collect = collect;
	}

	/**
	 * @see msi.gaml.compilation.AbstractStatementDescription#isSynthetic()
	 */
	@Override
	public boolean isSynthetic() {
		return false;
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getUnderlyingElement()
	 */
	@Override
	public EObject getUnderlyingElement(final Object facet) {
		if ( facet == null ) { return statement; }
		if ( facet instanceof EObject ) { return (EObject) facet; }
		IExpressionDescription f = facets.get(facet);
		if ( f != null && f.getAst() != null ) { return (EObject) f.getAst(); }
		return statement;
	}

}
