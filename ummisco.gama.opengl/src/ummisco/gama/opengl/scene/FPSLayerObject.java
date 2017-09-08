/*********************************************************************************************
 *
 * 'FPSLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.util.ArrayList;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class FPSLayerObject extends LayerObject {

	public FPSLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
		startTime = System.currentTimeMillis();
	}

	private int frameCount;
	private final double startTime;
	private double currentTime;
	private double previousTime;
	private double fps;

	@Override
	public void clear(final OpenGL gl) {}

	@Override
	protected boolean computeOverlay() {
		return true;
	}

	@Override
	public void draw(final OpenGL gl) {
		updateObjectList();
		super.draw(gl);
	}

	public void computeFrameRate() {
		frameCount++;
		currentTime = System.currentTimeMillis() - startTime;
		final int timeInterval = (int) (currentTime - previousTime);
		if (timeInterval > 1000) {
			fps = frameCount / (timeInterval / 1000d);
			fps *= 1000;
			fps = Math.round(fps);
			fps /= 1000;
			previousTime = currentTime;
			frameCount = 0;
		}
	}

	private void updateObjectList() {
		objects.clear();
		computeFrameRate();

		final ArrayList<AbstractObject> newElem = new ArrayList<>();

		// build text background
		final double w = 0.15;
		final double h = 0.04;
		final IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2));
		final ShapeDrawingAttributes drawingAttr =
				new ShapeDrawingAttributes(g, (IAgent) null, new GamaColor(255, 255, 255, 255), null); // white for the
																										// color, null
		// for the border color
		final GeometryObject geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr);
		newElem.add(geomObj);
		// build label
		final GamaFont font = new GamaFont("Helvetica", 0, 18); // 0 for plain,
																// 18 for text
																// size.
		final TextDrawingAttributes textDrawingAttr = new TextDrawingAttributes(Scaling3D.of(0.0015), null,
				new GamaPoint(0.005, -0.03, 0), new GamaColor(0, 0, 0, 1), font, true);
		final StringObject strObj = new StringObject("fps : " + fps, textDrawingAttr);
		newElem.add(strObj);

		objects.add(newElem);
	}
}