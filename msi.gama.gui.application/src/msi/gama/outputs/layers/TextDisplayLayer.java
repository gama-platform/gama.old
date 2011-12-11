/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.java.JavaConstExpression;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

@symbol(name = ISymbol.TEXT, kind = ISymbolKind.LAYER)
@inside(symbols = ISymbol.DISPLAY)
@facets({ @facet(name = ISymbol.VALUE, type = IType.STRING_STR, optional = false),
	@facet(name = ISymbol.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISymbol.FONT, type = IType.ID, optional = true),
	@facet(name = ISymbol.COLOR, type = IType.COLOR_STR, optional = true) })
public class TextDisplayLayer extends AbstractDisplayLayer {

	private final IExpression color;
	private final IExpression font;
	private IExpression text;
	String constantText = null;
	Color constantColor = null;
	String constantFont = null;
	private String currentText = null;
	private Color currentColor = null;
	private String currentFont = null;

	public TextDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
		IExpression c = getFacet(ISymbol.COLOR);
		color = c == null ? new JavaConstExpression(Cast.asColor(null, "white")) : c;
		c = getFacet(ISymbol.FONT);
		font = c == null ? new JavaConstExpression("Helvetica") : c;
		text = getFacet(ISymbol.VALUE);
	}

	public String getText() {
		return currentText;
	}

	public Color getColor() {
		return currentColor;
	}

	@Override
	public short getType() {
		return IDisplay.TEXT;
	}

	public String getFontName() {
		return currentFont;
	}

	@Override
	public void compute(final IScope sim, final long cycle) throws GamaRuntimeException {
		super.compute(sim, cycle);
		currentText = constantText == null ? Cast.asString(text.value(sim)) : constantText;
		currentColor = constantColor == null ? Cast.asColor(sim, color.value(sim)) : constantColor;
		currentFont = constantFont == null ? Cast.asString(font.value(sim)) : constantFont;
	}

	@Override
	public void prepare(final LayerDisplayOutput out, final IScope scope)
		throws GamaRuntimeException {
		super.prepare(out, scope);
		if ( text.isConst() && constantText == null ) {
			constantText = Cast.asString(text.value(scope));
		}
		if ( font.isConst() && constantFont == null ) {
			constantFont = Cast.asString(font.value(scope));
		}
		if ( color.isConst() && constantColor == null ) {
			constantColor = Cast.asColor(scope, color.value(scope));
		}
	}

	public void setTextExpr(final IExpression text) {
		this.text = text;
		constantText = null;
	}

	public void setColor(final Color currentColor) {
		constantColor = currentColor;
	}

	public void setFont(final String string) {
		constantFont = string;
	}

	public IExpression getTextExpr() {
		return text;
	}
}
