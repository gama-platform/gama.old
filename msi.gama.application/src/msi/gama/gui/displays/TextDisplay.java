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
package msi.gama.gui.displays;

import java.awt.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.*;
import msi.gama.outputs.layers.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
public class TextDisplay extends AbstractDisplay {

	private Font font;

	public TextDisplay(final double env_width, final double env_height, final IDisplayLayer layer,
		final IGraphics dg) {
		super(env_width, env_height, layer, dg);
		font = new Font("Helvetica", Font.PLAIN, 24);
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.createExpression(compo, "Expression:",
			((TextDisplayLayer) model).getTextExpr(), new EditorListener<IExpression>() {

				@Override
				public void valueModified(final IExpression newValue) {
					((TextDisplayLayer) model).setTextExpr(newValue);
					container.updateDisplay();
				}
			}, Types.get(IType.STRING));
		EditorFactory.create(compo, "Color:", ((TextDisplayLayer) model).getColor(),
			new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) {
					((TextDisplayLayer) model).setColor(newValue);
					container.updateDisplay();
				}
			});
		EditorFactory.create(compo, "Font:", ((TextDisplayLayer) model).getFontName(), true,
			new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) {
					((TextDisplayLayer) model).setFont(newValue);
					container.updateDisplay();
				}
			});
	}

	@Override
	public void privateDrawDisplay(final IGraphics g) {
		if ( disposed ) { return; }
		TextDisplayLayer model = (TextDisplayLayer) this.model;
		String text = model.getText();
		Color color = model.getColor();
		String f = model.getFontName();
		int s = size.y;
		if ( !font.getName().equals(f) || font.getSize() != s ) {
			font = new Font(f, Font.PLAIN, s);
		}
		g.setFont(font);
		g.drawString(text, color, null);
	}

	@Override
	protected String getType() {
		return "Text layer";
	}

}
