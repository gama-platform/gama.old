/*********************************************************************************************
 *
 * 'FrameLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class FrameLayerObject extends StaticLayerObject.World {

	public FrameLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
	}


	@Override
	void fillWithObjects(final List<AbstractObject> list) {
		final double w = renderer.data.getEnvWidth();
		final double h = renderer.data.getEnvHeight();
		final IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2));
		final ShapeDrawingAttributes drawingAttr =
				new ShapeDrawingAttributes(g, (IAgent) null, null, new GamaColor(150, 150, 150, 255));
		final GeometryObject geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr);
		list.add(geomObj);
	}
}