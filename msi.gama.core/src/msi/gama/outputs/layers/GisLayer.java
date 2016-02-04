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
import java.util.List;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaShapeFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.types.IType;

public class GisLayer extends AbstractLayer {

	IExpression gisExpression, colorExpression;

	public GisLayer(final ILayerStatement layer) {
		super(layer);
		gisExpression = layer.getFacet(IKeyword.GIS);
		colorExpression = layer.getFacet(IKeyword.COLOR);
	}
	//
	// @Override
	// public void reloadOn(final IDisplaySurface surface) {
	// ((ImageLayerStatement) definition).resetShapes();
	// super.reloadOn(surface);
	// }

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics g) {
		GamaColor color = colorExpression == null ? GamaColor.getInt(GamaPreferences.CORE_COLOR.getValue().getRGB())
			: Cast.asColor(scope, colorExpression.value(scope));
		List<IShape> shapes = buildGisLayer(scope);
		if ( shapes != null ) {
			for ( IShape geom : shapes ) {
				if ( geom != null ) {
					ShapeDrawingAttributes attributes =
						new ShapeDrawingAttributes(geom, color, new GamaColor(Color.black));
					Rectangle2D r = g.drawShape(geom, attributes);
				}
			}
		}
	}

	public List<IShape> buildGisLayer(final IScope scope) throws GamaRuntimeException {
		GamaShapeFile file = getShapeFile(scope);
		if ( file == null ) { return null; }
		return file.getContents(scope);
	}

	private GamaShapeFile getShapeFile(final IScope scope) {
		if ( gisExpression == null ) { return null; }
		if ( gisExpression.getType().id() == IType.STRING ) {
			String fileName = Cast.asString(scope, gisExpression.value(scope));
			return new GamaShapeFile(scope, fileName);
		}
		Object o = gisExpression.value(scope);
		if ( o instanceof GamaShapeFile ) { return (GamaShapeFile) o; }
		return null;
	}

	@Override
	public String getType() {
		return "Gis layer";
	}

}
