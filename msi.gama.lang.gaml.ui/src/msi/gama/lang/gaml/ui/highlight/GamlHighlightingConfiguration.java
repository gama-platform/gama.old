/*********************************************************************************************
 *
 *
 * 'GamlHighlightingConfiguration.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

/**
 * Written by drogoul
 * Modified on 16 nov. 2011
 *
 * @todo Description
 *
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import msi.gama.gui.swt.SwtGui;

public class GamlHighlightingConfiguration extends DefaultHighlightingConfiguration {

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
	}

	public TextStyle facetTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(154, 72, 71));
		return textStyle;
	}

	public TextStyle typeTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(0, 79, 116));
		return textStyle;
	}

	public TextStyle assignTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(50, 50, 50));
		return textStyle;
	}

	public TextStyle variableTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(28, 125, 180));
		return textStyle;
	}

	public TextStyle fieldTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(125, 125, 0));
		return textStyle;
	}

	public TextStyle operatorTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(46, 93, 78));
		return textStyle;
	}

	public TextStyle reservedTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		textStyle.setColor(new RGB(0, 0, 0));
		return textStyle;
	}

	@Override
	public TextStyle errorTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	public TextStyle unitTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	@Override
	public TextStyle numberTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(125, 125, 125));
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(127, 0, 85));
		textStyle.setStyle(SWT.NONE);
		return textStyle;
	}

	@Override
	public TextStyle punctuationTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(116, 167, 251));
		return textStyle;
	}

	public TextStyle varDefTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.NONE);
		textStyle.setColor(new RGB(0, 0, 153));
		return textStyle;
	}

	@Override
	public TextStyle taskTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setFontData(SwtGui.getNavigHeaderFont().getFontData());
		textStyle.setColor(new RGB(150, 132, 106));
		textStyle.setStyle(SWT.ITALIC | SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle commentTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setFontData(SwtGui.getNavigFolderFont().getFontData());
		textStyle.setColor(new RGB(63, 127, 95));
		return textStyle;
	}

}
