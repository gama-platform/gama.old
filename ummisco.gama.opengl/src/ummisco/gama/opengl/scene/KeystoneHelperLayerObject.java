/*********************************************************************************************
 *
 * 'KeystoneHelperLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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
import msi.gaml.operators.IUnits;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.ModernRenderer;

public class KeystoneHelperLayerObject extends LayerObject {

	public KeystoneHelperLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
	}

	@Override
	public boolean isLightInteraction() {
		return false;
	}

	@Override
	protected boolean computeOverlay() {
		return true;
	}

	@Override
	public void clear(final OpenGL gl) {}

	@Override
	public void draw(final OpenGL gl) {
		updateObjectList();
		super.draw(gl);
	}

	private void updateObjectList() {
		objects.clear();

		final ArrayList<AbstractObject> newElem = new ArrayList<>();

		// build the 4 circles at each corner
		if (renderer.getKeystone().drawKeystoneHelper()) // if the "K" key is pressed
		{
			final float[][] keystonePositions = new float[4][2];
			keystonePositions[1] = new float[] { 0, 0 };
			keystonePositions[2] = new float[] { 1, 0 };
			keystonePositions[0] = new float[] { 0, 1 };
			keystonePositions[3] = new float[] { 1, 1 };
			for (int cornerId = 0; cornerId < keystonePositions.length; cornerId++) {
				final GamaColor outsideCircleColor = cornerId == renderer.getKeystone().getCornerSelected()
						? new GamaColor(100, 0, 0, 255) : new GamaColor(0, 100, 0, 255);
				final GamaColor insideCircleColor = cornerId == renderer.getKeystone().getCornerSelected()
						? new GamaColor(255, 50, 50, 255) : new GamaColor(50, 255, 50, 255);
				final GamaPoint circleLocation =
						new GamaPoint(keystonePositions[cornerId][0], keystonePositions[cornerId][1]);
				// build the circle and the border of the circle
				final GeometryObject outsideCircle = createCircleObject(0.05, circleLocation, outsideCircleColor);
				newElem.add(outsideCircle);
				final GeometryObject insideCircle = createCircleObject(0.03, circleLocation, insideCircleColor);
				newElem.add(insideCircle);

				// build background for label
				final GamaPoint backgroundLocation =
						new GamaPoint(((keystonePositions[cornerId][0] * 2 - 1) * 0.82 + 1) / 2f,
								((keystonePositions[cornerId][1] * 2 - 1) * 0.82 + 1) / 2f - 0.01);
				final GeometryObject bckgndObj =
						createRectangleObject(0.2, 0.05, backgroundLocation, new GamaColor(255, 255, 255, 255));
				newElem.add(bckgndObj);
				// build label
				final String content =
						"(" + floor4Digit(((ModernRenderer) renderer).getKeystone().getCoords()[cornerId].x) + ","
								+ floor4Digit(((ModernRenderer) renderer).getKeystone().getCoords()[cornerId].y) + ")";
				final GamaPoint testLocation =
						new GamaPoint(((keystonePositions[cornerId][0] * 2 - 1) * 0.82 + 1) / 2f - 0.08,
								-((keystonePositions[cornerId][1] * 2 - 1) * 0.82 + 1) / 2f);
				final StringObject strObj =
						createStringObject(content, 0.0015, testLocation, new GamaColor(0, 0, 0, 1));
				newElem.add(strObj);
			}
			// add the "back to default" button

			// build text border
			final GeometryObject borderObj =
					createRectangleObject(0.24, 0.14, new GamaPoint(0.5, 0.5), new GamaColor(0, 100, 0, 255));
			newElem.add(borderObj);
			// build text background
			final GeometryObject bckgrdObj =
					createRectangleObject(0.2, 0.1, new GamaPoint(0.5, 0.5), new GamaColor(50, 255, 50, 255));
			newElem.add(bckgrdObj);

			// build label
			final GamaPoint location = new GamaPoint(0.42, -0.52, 0);
			final StringObject strObj = createStringObject("Default", 0.003, location, new GamaColor(0, 0, 0, 1));
			newElem.add(strObj);
		}

		objects.add(newElem);
	}

	private StringObject createStringObject(final String content, final double size, final GamaPoint location,
			final GamaColor color) {
		// 0 for plain, 18 for text size
		final GamaFont font = new GamaFont("Helvetica", 0, 18);
		final TextDrawingAttributes textDrawingAttr =
				new TextDrawingAttributes(Scaling3D.of(size), null, location, IUnits.bottom_left, color, font, true);
		final StringObject strObj = new StringObject(content, textDrawingAttr);
		return strObj;
	}

	private GeometryObject createCircleObject(final double size, final GamaPoint location, final GamaColor color) {
		final IShape g = GamaGeometryType.buildCircle(size, location);
		final ShapeDrawingAttributes drawingAttr = new ShapeDrawingAttributes(g, (IAgent) null, color, null);
		final GeometryObject circleGeom = new GeometryObject(g.getInnerGeometry(), drawingAttr);
		return circleGeom;
	}

	private GeometryObject createRectangleObject(final double wSize, final double hSize, final GamaPoint location,
			final GamaColor color) {
		final IShape g = GamaGeometryType.buildRectangle(wSize, hSize, location);
		final ShapeDrawingAttributes drawingAttr = new ShapeDrawingAttributes(g, (IAgent) null, color, null);
		// for the border color
		final GeometryObject rectGeom = new GeometryObject(g.getInnerGeometry(), drawingAttr);
		return rectGeom;
	}

	private double floor4Digit(final double n) {
		double number = n * 1000;
		number = Math.round(number);
		number /= 1000;
		return number;
	}
}