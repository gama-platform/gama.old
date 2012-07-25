/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.TEXT, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = IType.STRING_STR, optional = false),
	@facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = { IType.INT_STR, IType.FLOAT_STR, IType.POINT_STR }, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.FONT, type = IType.ID, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR_STR, optional = true),
	@facet(name = IKeyword.Z, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL_STR, optional = true)}, omissible = IKeyword.NAME)
public class TextLayerStatement extends AbstractLayerStatement {

	private final IExpression color;
	private final IExpression font;
	private IExpression text;
	String constantText = null;
	Color constantColor = null;
	String constantFont = null;
	private String currentText = null;
	private Color currentColor = null;
	private String currentFont = null;

	public TextLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		IExpression c = getFacet(IKeyword.COLOR);
		color = c == null ? new JavaConstExpression(Cast.asColor(null, "white")) : c;
		c = getFacet(IKeyword.FONT);
		font = c == null ? new JavaConstExpression("Helvetica") : c;
		text = getFacet(IKeyword.VALUE);
	}

	public String getText() {
		return currentText;
	}

	public Color getColor() {
		return currentColor;
	}

	@Override
	public short getType() {
		return ILayerStatement.TEXT;
	}

	public String getFontName() {
		return currentFont;
	}

	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		super.compute(scope, cycle);
		currentText = constantText == null ? Cast.asString(scope, text.value(scope)) : constantText;
		currentColor =
			constantColor == null ? Cast.asColor(scope, color.value(scope)) : constantColor;
		currentFont = constantFont == null ? Cast.asString(scope, font.value(scope)) : constantFont;
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope scope) throws GamaRuntimeException {
		super.prepare(out, scope);
		if ( text.isConst() && constantText == null ) {
			constantText = Cast.asString(scope, text.value(scope));
		}
		if ( font.isConst() && constantFont == null ) {
			constantFont = Cast.asString(scope, font.value(scope));
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
