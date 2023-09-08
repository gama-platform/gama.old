/*******************************************************************************************************
 *
 * DarkHighlightingConfiguration.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

/**
 * Written by drogoul Modified on 16 nov. 2011
 *
 * @todo Description
 *
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import com.google.inject.Singleton;

/**
 * The Class DarkHighlightingConfiguration.
 */
@Singleton
public class DarkHighlightingConfiguration extends DelegateHighlightingConfiguration {

	/**
	 * Instantiates a new dark highlighting configuration.
	 */
	public DarkHighlightingConfiguration() {
		super("dark");
	}

	@Override
	public TextStyle facetTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(243, 159, 62));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle pragmaTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	@Override
	public TextStyle typeTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(204, 108, 29));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle assignTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle variableTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(94, 198, 163));
		return textStyle;
	}

	@Override
	public TextStyle fieldTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(94, 198, 163));
		return textStyle;
	}

	@Override
	public TextStyle operatorTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(163, 230, 34));
		return textStyle;
	}

	@Override
	public TextStyle reservedTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		textStyle.setColor(new RGB(166, 166, 166));
		return textStyle;
	}

	@Override
	public TextStyle errorTextStyle() {
		return defaultTextStyle().copy();
	}

	@Override
	public TextStyle unitTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(166, 166, 166));
		textStyle.setStyle(SWT.ITALIC);

		return textStyle;
	}

	@Override
	public TextStyle numberTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(103, 150, 186));
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.NONE);
		textStyle.setColor(new RGB(204, 108, 29));
		return textStyle;
	}

	@Override
	public TextStyle punctuationTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(180, 180, 180));
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(141, 218, 248));
		return textStyle;
	}

	@Override
	public TextStyle varDefTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.NONE);
		textStyle.setColor(new RGB(226, 225, 5));
		return textStyle;
	}

	@Override
	public TextStyle taskTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC | SWT.BOLD);
		textStyle.setColor(new RGB(128, 128, 128));
		return textStyle;
	}

	@Override
	public TextStyle commentTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(56, 142, 193));
		return textStyle;
	}

	@Override
	public TextStyle defaultTextStyle() {
		final var textStyle = new TextStyle();
		textStyle.setColor(new RGB(255, 255, 255));
		return textStyle;
	}

}
