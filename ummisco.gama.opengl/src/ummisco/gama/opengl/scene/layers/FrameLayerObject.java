/*******************************************************************************************************
 *
 * FrameLayerObject.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.geometry.GeometryObject;

/**
 * The Class FrameLayerObject.
 */
public class FrameLayerObject extends StaticLayerObject.World {

	/** The Constant FRAME. */
	private static final GamaColor FRAME = new GamaColor(150, 150, 150, 255);

	/**
	 * Instantiates a new frame layer object.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public FrameLayerObject(final IOpenGLRenderer renderer) {
		super(renderer);
	}

	@Override
	public void fillWithObjects(final List<AbstractObject<?, ?>> list) {
		final double w = renderer.getData().getEnvWidth();
		final double h = renderer.getData().getEnvHeight();
		final IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2));
		final DrawingAttributes drawingAttr = new ShapeDrawingAttributes(g, (IAgent) null, null, FRAME);
		// drawingAttr.setLighting(false);
		final GeometryObject geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr);
		list.add(geomObj);
	}

	/**
	 * Compute rotation.
	 *
	 * @param trace
	 *            the trace
	 */
	@Override
	public void computeRotation(final Trace trace) {}
}