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

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class OutputManager.
 *
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol(name = IKeyword.PERMANENT, kind = ISymbolKind.OUTPUT, with_sequence = true, concept = { IConcept.BATCH,
		IConcept.PARAMETER })
@facets(omissible = IKeyword.LAYOUT, value = {
		@facet(name = IKeyword.LAYOUT, type = IType.INT, optional = true, doc = @doc("Either #none, to indicate that no layout will be imposed, or one of the four possible predefined layouts: #stack, #split, #horizontal or #vertical. This layout will be applied to both experiment and simulation display views")) })

@inside(kinds = { ISymbolKind.EXPERIMENT })
@doc(value = "Represents the outputs of the experiment itself. In a batch experiment, the permanent section allows to define an output block that will NOT be re-initialized at the beginning of each simulation but will be filled at the end of each simulation.", usages = {
		@usage(value = "For instance, this permanent section will allow to display for each simulation the end value of the food_gathered variable:", examples = {
				@example(value = "permanent {", isExecutable = false),
				@example(value = "	display Ants background: rgb('white') refresh_every: 1 {", isExecutable = false),
				@example(value = "		chart \"Food Gathered\" type: series {", isExecutable = false),
				@example(value = "			data \"Food\" value: food_gathered;", isExecutable = false),
				@example(value = "		}", isExecutable = false), @example(value = "	}", isExecutable = false),
				@example(value = "}", isExecutable = false) }) })
public class ExperimentOutputManager extends AbstractOutputManager {

	private IScope scope;
	private int layout = GamaPreferences.LAYOUTS.indexOf(GamaPreferences.CORE_DISPLAY_LAYOUT.getValue());

	public ExperimentOutputManager(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean init(final IScope scope) {
		this.scope = scope;
		final IExpression exp = getFacet(IKeyword.LAYOUT);
		if (exp != null) {
			layout = Cast.asInt(scope, exp.value(scope));
		}
		// scope.getGui().prepareForExperiment(scope.getExperiment().getSpecies());
		if (super.init(scope))
			scope.getGui().applyLayout(getLayout());
		return true;
	}

	protected int getLayout() {
		return layout;
	}

	// We dont allow permanent outputs to do their first step (to fix Issue
	// #1273)
	@Override
	protected boolean initialStep(final IScope scope, final IOutput output) {
		return true;
	}

	@Override
	public void addOutput(final IOutput output) {
		if (!(output instanceof AbstractOutput)) {
			return;
		}
		((AbstractOutput) output).setPermanent();
		super.addOutput(output);
	}

	@Override
	public synchronized void dispose() {
		if (scope != null) {
			scope.getGui().cleanAfterExperiment(scope.getExperiment().getSpecies());
		}
		super.dispose();
	}

}
