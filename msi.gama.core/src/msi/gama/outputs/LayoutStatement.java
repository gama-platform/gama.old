/*******************************************************************************************************
 *
 * LayoutStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import static msi.gama.common.interfaces.IKeyword.LAYOUT;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.LayoutStatement.LayoutValidator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.PlatformHelper;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.interfaces.IGamlIssue;
import msi.gaml.operators.Cast;
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
						name = "controls",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the experiment should show its control toolbar on top or not")),
				@facet (
						name = "parameters",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the parameters view is visible or not (true by default)")),
				@facet (
						name = "navigator",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the navigator view is visible or not (false by default)")),
				@facet (
						name = "consoles",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the consoles are visible or not (true by default)")),
				@facet (
						name = "tray",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the bottom tray is visible or not (true by default)")),
				@facet (
						name = "background",
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Whether the whole interface of GAMA should be colored or not (nil by default)")),
				@facet (
						name = "tabs",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the displays should show their tab or not")) })

@inside (
		symbols = IKeyword.OUTPUT)
@doc (
		value = "Represents the layout of the display views of simulations and experiments",
		usages = { @usage (
				value = "For instance, this layout statement will allow to split the screen occupied by displays in four equal parts, with no tabs. Pairs of display::weight represent the number of the display in their order of definition and their respective weight within a horizontal and vertical section",
				examples = { @example (
						value = "layout horizontal([vertical([0::5000,1::5000])::5000,vertical([2::5000,3::5000])::5000]) tabs: false;",
						isExecutable = false) }) })
@validator (LayoutValidator.class)
public class LayoutStatement extends Symbol {

	/**
	 * The Class LayoutValidator.
	 */
	public static class LayoutValidator implements IDescriptionValidator {

		@Override
		public void validate(final IDescription description) {
			if (!PlatformHelper.isWindows()) return;
			IExpression tabs = description.getFacetExpr("tabs");
			boolean tabsOn = tabs == null ? true : tabs.isConst() ? Cast.asBool(null, tabs.getConstValue()) : false;
			if (tabsOn) return;
			IDescription output = description.getEnclosingDescription();
			IDescription permanent = output.getEnclosingDescription().getChildWithKeyword(PERMANENT);
			Iterable<IDescription> displays = output.getChildrenWithKeyword(DISPLAY);
			if (permanent != null) { displays = Iterables.concat(displays, permanent.getChildrenWithKeyword(DISPLAY)); }
			boolean defaultDisplayTypeIs2D = _2D.equals(GamaPreferences.Displays.CORE_DISPLAY.getValue());
			for (IDescription display : displays) {
				String type = display.getLitteral(TYPE);
				if (_2D.equals(type) || defaultDisplayTypeIs2D && type == null) {
					description.warning(
							"A bug in GAMA 1.9 on Windows means that removing display tabs can prevent 2D displays from being shown. Please make sure you only use 3D (aka opengl) displays.",
							IGamlIssue.CONFLICTING_FACETS, "tabs");
					return;
				}
			}

		}

	}

	/**
	 * Instantiates a new layout statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public LayoutStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

}
