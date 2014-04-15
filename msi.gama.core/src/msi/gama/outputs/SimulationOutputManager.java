/*********************************************************************************************
 * 
 *
 * 'SimulationOutputManager.java', in plugin 'msi.gama.core', is part of the source code of the 
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
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gaml.descriptions.IDescription;

/**
 * The Class OutputManager.
 * 
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol(name = IKeyword.OUTPUT, kind = ISymbolKind.OUTPUT, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.EXPERIMENT })
public class SimulationOutputManager extends AbstractOutputManager {

	public SimulationOutputManager(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean init(final IScope scope) {
		// GuiUtils.prepareForSimulation((SimulationAgent) scope.getSimulationScope());
		GuiUtils.waitStatus(" Building outputs ");
		return super.init(scope);
	}

	@Override
	public boolean step(final IScope scope) {
		boolean result = super.step(scope);
		if ( !GuiUtils.isInHeadLessMode() && GAMA.getExperiment() != null && !GAMA.getExperiment().isBatch() ) {
			GuiUtils.informStatus(scope.getClock().getInfo());
		}
		return result;
	}

}
