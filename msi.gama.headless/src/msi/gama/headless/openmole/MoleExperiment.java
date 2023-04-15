/*******************************************************************************************************
 *
 * MoleExperiment.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.openmole;

import msi.gama.headless.core.Experiment;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.types.Types;

/**
 * The Class MoleExperiment.
 */
public class MoleExperiment extends Experiment implements IMoleExperiment {

	/**
	 * Instantiates a new mole experiment.
	 *
	 * @param mdl
	 *            the mdl
	 */
	MoleExperiment(final IModel mdl) {
		super(mdl);
	}

	@Override
	public void play(final int finalStep) {
		while (finalStep < this.step()) {}
	}

	/**
	 * Play.
	 *
	 * @param finalCondition
	 *            the final condition
	 */
	public void play(final String finalCondition) {
		play(finalCondition, -1);
	}

	@Override
	public void play(final String exp, final int finalStep) {
		IExpression endCondition = this.compileExpression(exp);
		if (exp == null || "".equals(exp)) {
			endCondition = IExpressionFactory.FALSE_EXPR;
		} else {
			endCondition = this.compileExpression(exp);
		}
		if (endCondition.getGamlType() != Types.BOOL)
			throw GamaRuntimeException.error("The until condition of the experiment should be a boolean", getScope());
		long step = 0;
		while (!Types.BOOL.cast(getScope(), this.evaluateExpression(endCondition), null, false).booleanValue()
				&& (finalStep >= 0 ? step < finalStep : true)) {
			step = this.step();
		}
	}

}
