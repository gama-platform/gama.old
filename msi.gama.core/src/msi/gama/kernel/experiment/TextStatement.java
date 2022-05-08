/*******************************************************************************************************
 *
 * WriteStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import static msi.gama.common.interfaces.IKeyword.FONT;

import java.awt.Color;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@symbol (
		name = IKeyword.TEXT,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.GUI, IConcept.PARAMETER, IConcept.TEXT })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.COLOR,
				type = IType.COLOR,
				optional = true,
				doc = @doc ("The color with wich the text will be displayed")),
				@facet (
						name = IKeyword.BACKGROUND,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("The color of the background of the text")),
				@facet (
						name = FONT,
						type = { IType.FONT, IType.STRING },
						optional = true,
						doc = @doc ("the font used to draw the text, which can be built with the operator \"font\". ex : font:font(\"Helvetica\", 20 , #bold)")),
				@facet (
						name = IKeyword.CATEGORY,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("a category label, used to group parameters in the interface")),
				@facet (
						name = IKeyword.MESSAGE,
						type = IType.NONE,
						optional = false,
						doc = @doc ("the text to display.")), },
		omissible = IKeyword.MESSAGE)
@doc (
		value = "The statement makes an experiment display text in the parameters view.")
public class TextStatement extends AbstractStatement implements IExperimentDisplayable {

	public TextStatement(final IDescription desc) {
		super(desc);
		message = getFacet(IKeyword.MESSAGE);
		color = getFacet(IKeyword.COLOR);
		category = getFacet(IKeyword.CATEGORY);
		font = getFacet(IKeyword.FONT);
		background = getFacet(IKeyword.BACKGROUND);
	}

	/** The message. */
	final IExpression message;

	/** The color. */
	final IExpression color, category, font, background;

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	public String getText(final IScope scope) {
		return Cast.asString(scope, message.value(scope));
	}

	public GamaFont getFont(final IScope scope) {
		if (font == null) return null;
		return GamaFontType.staticCast(scope, font.value(scope), false);
	}

	public Color getColor(final IScope scope) {
		GamaColor rgb = null;
		if (color != null) { rgb = Cast.asColor(scope, color.value(scope)); }
		return rgb;
	}

	public Color getBackground(final IScope scope) {
		GamaColor rgb = null;
		if (background != null) { rgb = Cast.asColor(scope, background.value(scope)); }
		return rgb;
	}

	@Override
	public String getTitle() { return ""; }

	@Override
	public String getUnitLabel(final IScope scope) {
		return "";
	}

	@Override
	public void setUnitLabel(final String label) {}

	@Override
	public boolean isDefinedInExperiment() { return true; }

	@Override
	public String getCategory() {
		if (category == null) return IExperimentDisplayable.super.getCategory();
		return category.literalValue();
	}

}
