/*********************************************************************************************
 *
 * 'GamlHighlightingConfiguration.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

@Singleton
public class DarkHighlightingConfiguration extends DelegateHighlightingConfiguration {

	public DarkHighlightingConfiguration() {
		super("dark");
	}

	@Override
	public TextStyle facetTextStyle() {
		final var textStyle = defaultTextStyle().copy();
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
		return textStyle;
	}

	@Override
	public TextStyle fieldTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle operatorTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle reservedTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	@Override
	public TextStyle errorTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle unitTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	@Override
	public TextStyle numberTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.NONE);
		return textStyle;
	}

	@Override
	public TextStyle punctuationTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle varDefTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.NONE);
		return textStyle;
	}

	@Override
	public TextStyle taskTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC | SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle commentTextStyle() {
		final var textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	@Override
	public TextStyle defaultTextStyle() {
		final var textStyle = new TextStyle();
		textStyle.setColor(new RGB(255, 255, 255));
		return textStyle;
	}

}
