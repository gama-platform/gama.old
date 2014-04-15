/*********************************************************************************************
 * 
 *
 * 'TextLayer.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.displays.layers;

import java.awt.Color;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
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
					if ( isPaused(container) ) {
						container.forceUpdateDisplay();
					}
				}
			}, Types.get(IType.STRING));
		EditorFactory.create(compo, "Color:", ((TextLayerStatement) definition).getColor(),
			new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) {
					((TextLayerStatement) definition).setColor(newValue);
					if ( isPaused(container) ) {
						container.forceUpdateDisplay();
					}
				}
			});
		EditorFactory.create(compo, "Font:", ((TextLayerStatement) definition).getFontName(), true,
			new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) {
					((TextLayerStatement) definition).setFont(newValue);
					if ( isPaused(container) ) {
						container.forceUpdateDisplay();
					}
				}
			});
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics g) {
		TextLayerStatement model = (TextLayerStatement) this.definition;
		String text = model.getText();
		Color color = model.getColor();
		String f = model.getFontName();
		Integer s = model.getStyle();
		g.drawString(text, color, null, null, f, s, null, true);
	}

	@Override
	public String getType() {
		return "Text layer";
	}

}
