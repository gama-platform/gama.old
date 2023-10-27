/*******************************************************************************************************
 *
 * SimulationOutputManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.HashMap;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.SimulationOutputManager.SimulationOutputValidator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.interfaces.IGamlIssue;
import msi.gaml.types.IType;

/**
 * The Class OutputManager.
 *
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */

/**
 * The Class SimulationOutputManager.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 16 sept. 2023
 */
@symbol (
		name = IKeyword.OUTPUT,
		kind = ISymbolKind.OUTPUT,
		with_sequence = true,
		concept = { IConcept.DISPLAY, IConcept.OUTPUT })
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
						+ "Note that setting autosave to true (or to any other value than false) will synchronize all the displays defined in the experiment")) })

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
@validator (SimulationOutputValidator.class)
public class SimulationOutputManager extends AbstractOutputManager {

	/**
	 * The Class SimulationOutputValidator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 16 sept. 2023
	 */
	public static class SimulationOutputValidator implements IDescriptionValidator<IDescription> {

		@Override
		public void validate(final IDescription description) {
			Iterable<IDescription> displays = description.getChildrenWithKeyword(DISPLAY);
			Map<String, IDescription> map = new HashMap<>();
			for (IDescription display : displays) {
				String s = display.getName();
				if (s == null) { continue; }
				IDescription existing = map.get(s);
				if (existing == null) {
					map.put(s, display);
					continue;
				}

				display.info("'" + s
						+ "' is defined twice in this experiment. Only this definition will be taken into account.",
						IGamlIssue.DUPLICATE_DEFINITION);
				existing.info("'" + s
						+ "' is defined twice in this experiment. This definition will not be taken into account.",
						IGamlIssue.DUPLICATE_DEFINITION);
			}

		}

	}

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
		scope.getGui().getStatus().informStatus(scope, null, "overlays/status.clock");
		// scope.getGui().getStatus().informStatus(scope, " " + scope.getRoot().getName() + " ready");
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
