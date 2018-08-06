/*********************************************************************************************
 *
 * 'ExperimentOutputManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs;

import static msi.gama.common.interfaces.IKeyword.LAYOUT;

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
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.IType;

/**
 * The Class LayoutStatement.
 *
 * @author Alexis Drogoul
 */
@symbol (
		name = LAYOUT,
		kind = ISymbolKind.OUTPUT,
		with_sequence = false,
		unique_in_context = true,
		concept = { IConcept.DISPLAY })

@facets (
		omissible = IKeyword.VALUE,
		value = { @facet (
				name = IKeyword.VALUE,
				type = IType.NONE,
				optional = true,
				doc = @doc ("Either #none, to indicate that no layout will be imposed, or one of the four possible predefined layouts: #stack, #split, #horizontal or #vertical. This layout will be applied to both experiment and simulation display views. In addition, it is possible to define a custom layout using the horizontal() and vertical() operators")),
				@facet (
						name = "editors",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the editors should initially be visible or not")),
				@facet (
						name = "toolbars",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the displays should show their toolbar or not")),
				@facet (
						name = "tabs",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the displays should show their tab or not")) })

@inside (
		kinds = { ISymbolKind.EXPERIMENT, ISymbolKind.OUTPUT })
@doc (
		value = "Represents the layout of the display views of simulations and experiments",
		usages = { @usage (
				value = "For instance, this layout statement will allow to split the screen occupied by displays in four equal parts, with no tabs. Pairs of display::weight represent the number of the display in their order of definition and their respective weight within a horizontal and vertical section",
				examples = { @example (
						value = "layout horizontal([vertical([0::5000,1::5000])::5000,vertical([2::5000,3::5000])::5000]) tabs: false;",
						isExecutable = false) }) })
public class LayoutStatement extends Symbol {

	public static LayoutStatement createEmpty() {
		return new LayoutStatement(DescriptionFactory.create(IKeyword.PERMANENT, (String[]) null));
	}

	public LayoutStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

}
