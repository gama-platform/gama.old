/**
 * Created by drogoul, 26 mars 2012
 * 
 */
package msi.gama.lang.utils;

import msi.gaml.compilation.AbstractSyntacticStatement;
import msi.gaml.descriptions.*;
import org.eclipse.emf.ecore.EObject;

/**
 * The class ECoreBasedStatementDescription.
 * 
 * @author drogoul
 * @since 26 mars 2012
 * 
 */
public class SyntacticStatement extends AbstractSyntacticStatement {

	final EObject statement;

	/**
	 * Instantiates a new Element
	 */
	public SyntacticStatement(final String keyword, final EObject statement) {
		super(keyword);
		this.statement = statement;
	}

	/**
	 * @see msi.gaml.compilation.AbstractStatementDescription#isSynthetic()
	 */
	@Override
	public boolean isSynthetic() {
		return false;
	}

	/**
	 * Attach the description, through an adapter, to the underlying statement
	 * @see msi.gaml.compilation.AbstractSyntacticStatement#setDescription(msi.gaml.descriptions.IDescription)
	 */
	@Override
	public void setDescription(final IDescription description) {
		EGaml.setGamlDescription(statement, description);
	}

	/**
	 * @see msi.gama.common.interfaces.ISyntacticElement#getUnderlyingElement()
	 */
	@Override
	public EObject getUnderlyingElement(final Object facet) {
		if ( facet == null ) { return statement; }
		if ( facet instanceof EObject ) { return (EObject) facet; }
		IExpressionDescription f = facets.get(facet);
		if ( f != null && f.getTarget() != null ) { return f.getTarget(); }
		return statement;
	}

}
