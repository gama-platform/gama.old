/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.layers.FrameLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
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
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.LayerElement;
import ummisco.gama.opengl.scene.LayerElement.DrawerType;

public class FrameLayerObject extends StaticLayerObject.World {

	private static final GamaColor FRAME = new GamaColor(150, 150, 150, 255);

	public FrameLayerObject(final IOpenGLRenderer renderer) {
		super(renderer);
	}

	@Override
	public void fillWithObjects(final List<LayerElement<?, ?>> list) {
		final double w = renderer.getData().getEnvWidth();
		final double h = renderer.getData().getEnvHeight();
		final IShape g = GamaGeometryType.buildRectangle(w, h, GamaPoint.create(w / 2, h / 2));
		final DrawingAttributes drawingAttr = new DrawingAttributes(g, (IAgent) null, null, FRAME);
		drawingAttr.setLighting(false);
		final LayerElement geomObj =
				LayerElement.createLayerElement(g.getInnerGeometry(), drawingAttr, DrawerType.GEOMETRY);
		list.add(geomObj);
	}
}