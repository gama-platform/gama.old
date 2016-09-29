package ummisco.gama.opengl.scene;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class KeystoneHelperLayerObject extends LayerObject {

	public KeystoneHelperLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
		isOverlay = true;
	}

	@Override
	public void clear(final GL2 gl) {
	}
	
	@Override
	public void draw(final GL2 gl) {
		updateObjectList();
		super.draw(gl);
	}
	
	private void updateObjectList() {
		objects.clear();
		
		ArrayList<AbstractObject> newElem = new ArrayList<AbstractObject>();
		
		// build the 4 circles at each corner
		if (renderer.drawKeystoneHelper()) // if the "K" key is pressed
		{
			float[][] keystonePositions = new float[4][2];
			keystonePositions[1] = new float[]{0,0};
			keystonePositions[2] = new float[]{1,0};
			keystonePositions[0] = new float[]{0,1};
			keystonePositions[3] = new float[]{1,1};
			for (int cornerId = 0 ; cornerId < keystonePositions.length ; cornerId++) {
				GamaColor outsideCircleColor = (cornerId == renderer.getCornerSelected()) ? new GamaColor(100, 0, 0, 255) : new GamaColor(0, 100, 0, 255);
				GamaColor insideCircleColor = (cornerId == renderer.getCornerSelected()) ? new GamaColor(255, 50, 50, 255) : new GamaColor(50, 255, 50, 255);
				IShape g = GamaGeometryType.buildCircle(0.05f, new GamaPoint(keystonePositions[cornerId][0],keystonePositions[cornerId][1]));
				ShapeDrawingAttributes drawingAttr = new ShapeDrawingAttributes(g, outsideCircleColor,
						null); 	// dark green for the color, null for the border color
				final GeometryObject outsideCircle = new GeometryObject(g.getInnerGeometry(), drawingAttr, this);
				outsideCircle.enableOverlay(true);
				newElem.add(outsideCircle);
				g = GamaGeometryType.buildCircle(0.03f, new GamaPoint(keystonePositions[cornerId][0],keystonePositions[cornerId][1]));
				drawingAttr = new ShapeDrawingAttributes(g, insideCircleColor,
						null); 	// green for the color, null for the border color
				final GeometryObject insideCircle = new GeometryObject(g.getInnerGeometry(), drawingAttr, this);
				insideCircle.enableOverlay(true);
				newElem.add(insideCircle);
			}
			// add the "back to default" button
			double w = 0.24;
			double h = 0.14;
			IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(0.5, 0.5));
			ShapeDrawingAttributes drawingAttr = new ShapeDrawingAttributes(g, new GamaColor(0, 100, 0, 255),
					null); 	// white for the color, null
						 	// for the border color
			GeometryObject geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr, this);
			geomObj.enableOverlay(true);
			newElem.add(geomObj);
			w = 0.2;
			h = 0.1;
			g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(0.5, 0.5));
			drawingAttr = new ShapeDrawingAttributes(g, new GamaColor(50, 255, 50, 255),
					null); 	// white for the color, null
						 	// for the border color
			geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr, this);
			geomObj.enableOverlay(true);
			newElem.add(geomObj);
			// build label
			GamaFont font = new GamaFont("Helvetica",0,18); // 0 for plain, 18 for text size.
			TextDrawingAttributes textDrawingAttr = new TextDrawingAttributes(new GamaPoint(0.003,0.003,0.003),null,new GamaPoint(0.42,-0.52,0),new GamaColor(0,0,0,1),font,true);
			StringObject strObj = new StringObject("Default", textDrawingAttr, this);
			strObj.enableOverlay(true);
			newElem.add(strObj);
		}
		
		objects.add(newElem);
	}
}