/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.layers.FrameLayerObject.java, in plugin ummisco.gama.opengl,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;

public class FrameLayerObject extends StaticLayerObject.World {

	private static final GamaColor FRAME = new GamaColor(150, 150, 150, 255);

	public FrameLayerObject(final IOpenGLRenderer renderer) {
		super(renderer);
	}

	@Override
	public void fillWithObjects(final List<AbstractObject<?, ?>> list) {
		final double w = renderer.getData().getEnvWidth();
		final double h = renderer.getData().getEnvHeight();
		final IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2));
		final ShapeDrawingAttributes drawingAttr =
				new ShapeDrawingAttributes(g, (IAgent) null, null, FRAME).withLighting(false);
		final GeometryObject geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr);
		list.add(geomObj);
	}
}