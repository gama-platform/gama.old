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
package msi.gama.outputs.layers;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

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
		GamaColor color = ((ImageLayerStatement) definition).getColor();
		IList<IShape> shapes = ((ImageLayerStatement) definition).getShapes();
		if ( shapes != null ) {
			for ( IShape geom : shapes ) {
				if ( geom != null ) {
					DrawingAttributes attributes =
						new DrawingAttributes(new GamaPoint(geom.getLocation()), color, new GamaColor(Color.black));
					attributes.setShapeType(geom.getGeometricalType());
					Rectangle2D r = g.drawShape(geom, attributes);
				}
			}
		}
	}

	@Override
	public String getType() {
		return "Gis layer";
	}

}
