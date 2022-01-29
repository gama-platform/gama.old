/*******************************************************************************************************
 *
 * LightHighlightingConfiguration.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
 * The Class LightHighlightingConfiguration.
 */
@Singleton
public class LightHighlightingConfiguration extends DelegateHighlightingConfiguration {

	/**
	 * Instantiates a new light highlighting configuration.
	 */
	public LightHighlightingConfiguration() {
		super("light");
	}

	@Override
	public TextStyle facetTextStyle() {
		return newStyle(SWT.BOLD, 154, 72, 71);
	}

	@Override
	public TextStyle pragmaTextStyle() {
		return newStyle(SWT.ITALIC, 122, 122, 122);
	}

	@Override
	public TextStyle typeTextStyle() {
		return newStyle(SWT.BOLD, 0, 79, 116);
	}

	@Override
	public TextStyle assignTextStyle() {
		return newStyle(SWT.BOLD, 50, 50, 50);
	}

	@Override
	public TextStyle variableTextStyle() {
		return newStyle(SWT.BOLD, 28, 125, 180);
	}

	@Override
	public TextStyle fieldTextStyle() {
		return newStyle(SWT.NORMAL, 125, 125, 0);
	}

	@Override
	public TextStyle operatorTextStyle() {
		return newStyle(SWT.BOLD, 46, 93, 78);
	}

	@Override
	public TextStyle reservedTextStyle() {
		return newStyle(SWT.ITALIC);
	}

	@Override
	public TextStyle errorTextStyle() {
		return newStyle();
	}

	@Override
	public TextStyle unitTextStyle() {
		return newStyle(SWT.ITALIC);
	}

	@Override
	public TextStyle numberTextStyle() {
		return newStyle(SWT.NORMAL, 125, 125, 125);
	}

	@Override
	public TextStyle keywordTextStyle() {
		return newStyle(SWT.BOLD, 127, 0, 85);
	}

	@Override
	public TextStyle punctuationTextStyle() {
		return newStyle();
	}

	@Override
	public TextStyle stringTextStyle() {
		return newStyle(SWT.NORMAL, 116, 167, 251);
	}

	@Override
	public TextStyle varDefTextStyle() {
		return newStyle(SWT.NORMAL, 0, 0, 153);
	}

	@Override
	public TextStyle taskTextStyle() {
		return newStyle(SWT.ITALIC | SWT.BOLD, 150, 132, 106);
	}

	@Override
	public TextStyle commentTextStyle() {
		return newStyle(SWT.NORMAL, 63, 127, 95);
	}

	@Override
	public TextStyle defaultTextStyle() {
		final var textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 0));
		return textStyle;
	}

}
