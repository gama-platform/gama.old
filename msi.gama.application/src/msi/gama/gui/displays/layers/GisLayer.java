/*********************************************************************************************
 * 
 * 
 * 'GisLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.displays.layers;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import org.eclipse.swt.widgets.Composite;

public class GisLayer extends AbstractLayer {

	public GisLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public void reloadOn(final IDisplaySurface surface) {
		((ImageLayerStatement) definition).resetShapes();
		super.reloadOn(surface);
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics g) {
		Color color = ((ImageLayerStatement) definition).getColor();
		IList<IShape> shapes = ((ImageLayerStatement) definition).getShapes();
		if ( shapes != null ) {
			for ( IShape geom : shapes ) {
				if ( geom != null ) {
					Rectangle2D r = g.drawGamaShape(scope, geom, color, true, Color.black, false);
				}
			}
		}
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.createFile(compo, "Shapefile:", ((ImageLayerStatement) definition).getImageFileName(),
			new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) throws GamaRuntimeException {
					((ImageLayerStatement) definition).setGisLayerName(GAMA.getRuntimeScope(), newValue);
				}

			});
	}

	@Override
	public String getType() {
		return "Gis layer";
	}

}
