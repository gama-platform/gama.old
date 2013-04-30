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
package msi.gama.gui.displays.layers;

import java.awt.Color;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
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
public class TextLayer extends AbstractLayer {

	public TextLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.createExpression(compo, "Expression:", ((TextLayerStatement) definition).getTextExpr(),
			new EditorListener<IExpression>() {

				@Override
				public void valueModified(final IExpression newValue) {
					((TextLayerStatement) definition).setTextExpr(newValue);
					container.forceUpdateDisplay();
				}
			}, Types.get(IType.STRING));
		EditorFactory.create(compo, "Color:", ((TextLayerStatement) definition).getColor(),
			new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) {
					((TextLayerStatement) definition).setColor(newValue);
					container.forceUpdateDisplay();
				}
			});
		EditorFactory.create(compo, "Font:", ((TextLayerStatement) definition).getFontName(), true,
			new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) {
					((TextLayerStatement) definition).setFont(newValue);
					container.forceUpdateDisplay();
				}
			});
	}

	@Override
	public void privateDrawDisplay(final IGraphics g) {
		TextLayerStatement model = (TextLayerStatement) this.definition;
		String text = model.getText();
		Color color = model.getColor();
		String f = model.getFontName();
		g.drawString(text, color, null, null, f, null, null, 0.0);
	}

	@Override
	protected String getType() {
		return "Text layer";
	}

}
