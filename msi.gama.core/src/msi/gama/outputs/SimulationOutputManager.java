/*******************************************************************************************************
 *
 * SimulationOutputManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.IType;

/**
 * The Class OutputManager.
 *
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol (
		name = IKeyword.OUTPUT,
		kind = ISymbolKind.OUTPUT,
		with_sequence = true,
		concept = {})
@facets ({ @facet (
		name = "synchronized",
		type = IType.BOOL,
		optional = true,
		doc = @doc (
				value = "Indicates whether the displays that compose this output should be synchronized with the simulation cycles")),
		@facet (
				name = IKeyword.AUTOSAVE,
				type = { IType.BOOL, IType.STRING },
				optional = true,
				doc = @doc ("Allows to save the whole screen on disk. A value of true/false will save it with the resolution of the physical screen. Passing it a string allows to define the filename "
						+ "Note that setting autosave to true (or to any other value than false) in a display will synchronize all the displays defined in the experiment")) })

@inside (
		kinds = { ISymbolKind.MODEL, ISymbolKind.EXPERIMENT })
@doc (
		value = "`output` blocks define how to visualize a simulation (with one or more display blocks that define separate windows). It will include a set of displays, monitors and files statements. It will be taken into account only if the experiment type is `gui`.",
		usages = { @usage (
				value = "Its basic syntax is: ",
				examples = { @example (
						value = "experiment exp_name type: gui {",
						isExecutable = false),
						@example (
								value = "   // [inputs]",
								isExecutable = false),
						@example (
								value = "   output {",
								isExecutable = false),
						@example (
								value = "      // [display, file, inspect, layout or monitor statements]",
								isExecutable = false),
						@example (
								value = "   }",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.MONITOR, IKeyword.INSPECT, IKeyword.OUTPUT_FILE, IKeyword.LAYOUT })
public class SimulationOutputManager extends AbstractOutputManager {

	/**
	 * Creates the empty.
	 *
	 * @return the simulation output manager
	 */
	public static SimulationOutputManager createEmpty() {
		return new SimulationOutputManager(DescriptionFactory.create(IKeyword.OUTPUT, (String[]) null));
	}

	/**
	 * Instantiates a new simulation output manager.
	 *
	 * @param desc
	 *            the desc
	 */
	public SimulationOutputManager(final IDescription desc) {
		super(desc);

	}

	@Override
	public boolean init(final IScope scope) {
		scope.getGui().getStatus().waitStatus(scope, " Building outputs ");
		final boolean result = super.init(scope);
		updateDisplayOutputsName(scope.getSimulation());
		scope.getGui().getStatus().informStatus(scope, " " + scope.getRoot().getName() + " ready");
		return result;
	}

	/**
	 * Update display outputs name.
	 *
	 * @param agent
	 *            the agent
	 */
	public void updateDisplayOutputsName(final SimulationAgent agent) {
		for (final IOutput out : this) {
			if (out instanceof IDisplayOutput display) { GAMA.getGui().updateViewTitle(display, agent); }
		}

	}

}
