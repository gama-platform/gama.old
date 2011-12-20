/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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

public class GamlHighlightingConfiguration extends DefaultHighlightingConfiguration {

	public static final String	BINARY_ID	= "binary";
	public static final String	UNARY_ID	= "unary";
	public static final String	RESERVED_ID	= "reserved";
	public static final String	FACET_ID	= "facet";
	public static final String	FIELD_ID	= "field";

	@Override
	public void configure(final IHighlightingConfigurationAcceptor acceptor) {
		acceptor.acceptDefaultHighlighting(KEYWORD_ID, "Keyword", keywordTextStyle());
		acceptor.acceptDefaultHighlighting(PUNCTUATION_ID, "Punctuation character",
			punctuationTextStyle());
		acceptor.acceptDefaultHighlighting(BINARY_ID, "Binary operators", binaryTextStyle());
		acceptor.acceptDefaultHighlighting(UNARY_ID, "Unary operators", unaryTextStyle());
		acceptor.acceptDefaultHighlighting(RESERVED_ID, "Reserved keywords", reservedTextStyle());
		acceptor.acceptDefaultHighlighting(COMMENT_ID, "Comment", commentTextStyle());
		acceptor.acceptDefaultHighlighting(STRING_ID, "String", stringTextStyle());
		acceptor.acceptDefaultHighlighting(NUMBER_ID, "Number", numberTextStyle());
		acceptor.acceptDefaultHighlighting(DEFAULT_ID, "Default", defaultTextStyle());
		acceptor.acceptDefaultHighlighting(INVALID_TOKEN_ID, "Invalid Symbol", errorTextStyle());
		acceptor.acceptDefaultHighlighting(FACET_ID, "Facets", facetTextStyle());
		acceptor.acceptDefaultHighlighting(FIELD_ID, "Fields", fieldTextStyle());
	}

	@Override
	public TextStyle defaultTextStyle() {
		TextStyle textStyle = new TextStyle();
		// textStyle.setBackgroundColor(new RGB(255, 255, 255));
		textStyle.setColor(new RGB(0, 0, 0));
		return textStyle;
	}

	public TextStyle facetTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.BOLD);
		// textStyle.setBackgroundColor(new RGB(255, 255, 255));
		textStyle.setColor(new RGB(0, 125, 0));
		return textStyle;
	}

	public TextStyle fieldTextStyle() {
		TextStyle textStyle = new TextStyle();
		// textStyle.setBackgroundColor(new RGB(255, 255, 255));
		textStyle.setColor(new RGB(125, 125, 0));
		return textStyle;
	}

	public TextStyle binaryTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.BOLD);
		// textStyle.setBackgroundColor(new RGB(255, 255, 255));
		textStyle.setColor(new RGB(46, 93, 78));
		return textStyle;
	}

	public TextStyle unaryTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.ITALIC);
		// textStyle.setBackgroundColor(new RGB(255, 255, 255));
		textStyle.setColor(new RGB(120, 0, 120));
		return textStyle;
	}

	public TextStyle reservedTextStyle() {
		TextStyle textStyle = new TextStyle();
		// textStyle.setBackgroundColor(new RGB(255, 255, 255));
		textStyle.setColor(new RGB(0, 0, 0));
		return textStyle;
	}

	@Override
	public TextStyle errorTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		// textStyle.setColor(new RGB(255, 0, 0));
		return textStyle;
	}

	@Override
	public TextStyle numberTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(125, 125, 125));
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(42, 0, 255));
		return textStyle;
	}

	@Override
	public TextStyle commentTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(63, 127, 95));
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(127, 0, 85));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	@Override
	public TextStyle punctuationTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

}
