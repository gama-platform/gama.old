/*********************************************************************************************
 *
 * 'GamlHighlightingConfiguration.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;

/**
 * Written by drogoul Modified on 16 nov. 2011
 *
 * @todo Description
 *
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.GamaPreferences.Modeling;
import msi.gama.util.GamaFont;
import msi.gaml.types.IType;
import ummisco.gama.ui.resources.GamaColors;

public class GamlHighlightingConfiguration extends DefaultHighlightingConfiguration {

	public static final String NAME = "Syntax coloring";

	public static GamaFont getDefaultFont() {
		final FontData fd = PreferenceConverter.getFontData(EditorsPlugin.getDefault().getPreferenceStore(),
				JFaceResources.TEXT_FONT);
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	public static GamaFont get(final FontData fd) {
		return fd == null ? getDefaultFont() : new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	static {
		final GamlHighlightingConfiguration c = new GamlHighlightingConfiguration();
		c.configure((id, name, style) -> {
			final FontData[] fd = style.getFontData();
			final FontData f = fd == null ? null : fd[0];
			GamaPreferences.create("pref_" + id + "_font", name + " font", () -> get(f), IType.FONT, false)
					.in(Modeling.NAME, NAME);
		});
		c.configure(
				(id, name, style) -> GamaPreferences
						.create("pref_" + id + "_color", name + " color",
								() -> GamaColors.toGamaColor(style.getColor()), IType.COLOR, false)
						.in(Modeling.NAME, NAME));
	}

	public static final String OPERATOR_ID = "binary";
	public static final String RESERVED_ID = "reserved";
	public static final String FACET_ID = "facet";
	public static final String FIELD_ID = "field";
	public static final String GLOBAL_ID = "global";
	public static final String VARIABLE_ID = "variable";
	public static final String VARDEF_ID = "varDef";
	public static final String TYPE_ID = "typeDef";
	public static final String ASSIGN_ID = "assignment";
	public static final String UNIT_ID = "unit";
	public static final String TASK_ID = "task";
	public static final String PRAGMA_ID = "pragma";

	@Override
	public void configure(final IHighlightingConfigurationAcceptor acceptor) {
		acceptor.acceptDefaultHighlighting(KEYWORD_ID, "Statement keywords", keywordTextStyle());
		acceptor.acceptDefaultHighlighting(PUNCTUATION_ID, "Punctuation characters", punctuationTextStyle());
		acceptor.acceptDefaultHighlighting(OPERATOR_ID, "Operators & action calls", operatorTextStyle());
		acceptor.acceptDefaultHighlighting(RESERVED_ID, "Reserved symbols", reservedTextStyle());
		acceptor.acceptDefaultHighlighting(COMMENT_ID, "Comments", commentTextStyle());
		acceptor.acceptDefaultHighlighting(STRING_ID, "Strings", stringTextStyle());
		acceptor.acceptDefaultHighlighting(NUMBER_ID, "Literal constants", numberTextStyle());
		acceptor.acceptDefaultHighlighting(DEFAULT_ID, "Default", defaultTextStyle());
		acceptor.acceptDefaultHighlighting(FACET_ID, "Facet keys", facetTextStyle());
		acceptor.acceptDefaultHighlighting(VARIABLE_ID, "Variables used in expressions", variableTextStyle());
		acceptor.acceptDefaultHighlighting(VARDEF_ID, "Variables definitions", varDefTextStyle());
		acceptor.acceptDefaultHighlighting(TYPE_ID, "Type", typeTextStyle());
		acceptor.acceptDefaultHighlighting(ASSIGN_ID, "Assignment signs", assignTextStyle());
		acceptor.acceptDefaultHighlighting(UNIT_ID, "Unit names", unitTextStyle());
		acceptor.acceptDefaultHighlighting(TASK_ID, "Tasks", taskTextStyle());
		acceptor.acceptDefaultHighlighting(PRAGMA_ID, "Pragma", pragmaTextStyle());
	}

	public TextStyle facetTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(154, 72, 71));
		return textStyle;
	}

	public TextStyle pragmaTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		textStyle.setColor(new RGB(122, 122, 122));
		return textStyle;
	}

	public TextStyle typeTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(0, 79, 116));
		return textStyle;
	}

	public TextStyle assignTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(50, 50, 50));
		return textStyle;
	}

	public TextStyle variableTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(28, 125, 180));
		return textStyle;
	}

	public TextStyle fieldTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(125, 125, 0));
		return textStyle;
	}

	public TextStyle operatorTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(46, 93, 78));
		return textStyle;
	}

	public TextStyle reservedTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		textStyle.setColor(new RGB(0, 0, 0));
		return textStyle;
	}

	@Override
	public TextStyle errorTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	public TextStyle unitTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	@Override
	public TextStyle numberTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(125, 125, 125));
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(127, 0, 85));
		textStyle.setStyle(SWT.NONE);
		return textStyle;
	}

	@Override
	public TextStyle punctuationTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(116, 167, 251));
		return textStyle;
	}

	public TextStyle varDefTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.NONE);
		textStyle.setColor(new RGB(0, 0, 153));
		return textStyle;
	}

	@Override
	public TextStyle taskTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		// textStyle.setFontData(GamaFonts.getNavigHeaderFont().getFontData());
		textStyle.setColor(new RGB(150, 132, 106));
		textStyle.setStyle(SWT.ITALIC | SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle commentTextStyle() {
		final TextStyle textStyle = defaultTextStyle().copy();
		// textStyle.setFontData(GamaFonts.getNavigFolderFont().getFontData());
		textStyle.setColor(new RGB(63, 127, 95));
		return textStyle;
	}

}
