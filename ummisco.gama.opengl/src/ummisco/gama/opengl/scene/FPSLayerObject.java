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

public class FPSLayerObject extends LayerObject {

	public FPSLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
		isOverlay = true;
		startTime = System.currentTimeMillis();
	}
	
	private int frameCount;
	private final double startTime;
	private double currentTime;
	private double previousTime;
	private double fps;

	@Override
	public void clear(final GL2 gl) {
	}
	
	@Override
	public void draw(final GL2 gl) {
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
		
		ArrayList<AbstractObject> newElem = new ArrayList<AbstractObject>();
		
		// build text background
		final double w = 0.15;
		final double h = 0.04;
		final IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2));
		final ShapeDrawingAttributes drawingAttr = new ShapeDrawingAttributes(g, new GamaColor(255, 255, 255, 255),
				null); 	// white for the color, null
					 	// for the border color
		final GeometryObject geomObj = new GeometryObject(g.getInnerGeometry(), drawingAttr, this);
		geomObj.enableOverlay(true);
		newElem.add(geomObj);
		// build label
		GamaFont font = new GamaFont("Helvetica",0,18); // 0 for plain, 18 for text size.
		TextDrawingAttributes textDrawingAttr = new TextDrawingAttributes(new GamaPoint(0.0015,0.0015,0.0015),null,new GamaPoint(0.005,-0.03,0),new GamaColor(0,0,0,1),font,true);
		StringObject strObj = new StringObject("fps : " + fps, textDrawingAttr, this);
		strObj.enableOverlay(true);
		newElem.add(strObj);
		
		objects.add(newElem);
	}
}