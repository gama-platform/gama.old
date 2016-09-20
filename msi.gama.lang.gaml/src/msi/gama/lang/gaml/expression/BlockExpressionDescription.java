package msi.gama.lang.gaml.expression;

import msi.gama.util.GAML;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.Types;

public class BlockExpressionDescription extends EcoreBasedExpressionDescription {

	final ISyntacticElement element;

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
			final String name = action.getName();
			expression = GAML.getExpressionFactory().createConst(name, Types.STRING);
		}
		return expression;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		return new BlockExpressionDescription(element);
	}

}
