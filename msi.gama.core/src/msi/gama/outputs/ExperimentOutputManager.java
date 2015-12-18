/*********************************************************************************************
 *
 *
 * 'ExperimentOutputManager.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.*;
import msi.gaml.descriptions.IDescription;

/**
 * The Class OutputManager.
 *
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol(name = IKeyword.PERMANENT, kind = ISymbolKind.OUTPUT, with_sequence = true)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@doc(
	value = "In a batch experiment, the permanent section allows to define an output block that will NOT be re-initialized at the beginning of each simulation but will be filled at the end of each simulation.",
	usages = { @usage(
		value = "For instance, this permanent section will allow to display for each simulation the end value of the food_gathered variable:",
		examples = { @example(value = "permanent {", isExecutable = false),
			@example(value = "	display Ants background: rgb('white') refresh_every: 1 {", isExecutable = false),
			@example(value = "		chart \"Food Gathered\" type: series {", isExecutable = false),
			@example(value = "			data \"Food\" value: food_gathered;", isExecutable = false),
			@example(value = "		}", isExecutable = false), @example(value = "	}", isExecutable = false),
			@example(value = "}", isExecutable = false) }) })
public class ExperimentOutputManager extends AbstractOutputManager {

	public ExperimentOutputManager(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean init(final IScope scope) {
		GuiUtils.prepareForExperiment(GAMA.getExperiment());
		return super.init(scope);
	}

	// We dont allow permanent outputs to do their first step (to fix Issue #1273)
	@Override
	protected boolean initialStep(final IScope scope, final IOutput output) {
		return true;
	}

	@Override
	public void addOutput(final IOutput output) {
		if ( output == null ) { return; }
		output.setPermanent();
		super.addOutput(output);
	}

	@Override
	public synchronized void dispose() {
		GuiUtils.cleanAfterExperiment(GAMA.getExperiment());
		super.dispose();
	}

}
