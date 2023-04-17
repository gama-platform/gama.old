/*******************************************************************************************************
 *
 * BlockExpressionDescription.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.expression;

import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.types.DenotedActionExpression;
import msi.gaml.factories.DescriptionFactory;

/**
 * The Class BlockExpressionDescription.
 */
public class BlockExpressionDescription extends EcoreBasedExpressionDescription {

	/** The element. */
	final ISyntacticElement element;

	/**
	 * Instantiates a new block expression description.
	 *
	 * @param element the element
	 */
	public BlockExpressionDescription(final ISyntacticElement element) {
		super(element.getElement());
		this.element = element;
	}

	@Override
	public IExpression compile(final IDescription context) {
		final SpeciesDescription sd = context.getSpeciesContext();
		// if (sd.isExperiment())
		// sd = sd.getModelDescription();
		final StatementDescription action = (StatementDescription) DescriptionFactory.create(element, sd, null);
		if (action != null) {
			sd.addChild(action);
			action.validate();
			//			final String name = action.getName();
			expression = new DenotedActionExpression(action);
		}
		return expression;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		return new BlockExpressionDescription(element);
	}

}
