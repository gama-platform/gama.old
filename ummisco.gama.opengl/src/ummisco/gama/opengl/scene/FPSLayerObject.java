package ummisco.gama.opengl.scene;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.statements.draw.TextDrawingAttributes;
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
		// create the rotation helper as "GeometryObject" in the list "objects".
		// the rotation helper is a sphere centered in renderer.getRotationHelperPosition() and a size of "50.0 * (distance / 500)".
		ArrayList<AbstractObject> newElem = new ArrayList<AbstractObject>();
		// build labels
		GamaFont font = new GamaFont("Helvetica",0,18); // 0 for plain, 18 for text size.
		TextDrawingAttributes textDrawingAttr = new TextDrawingAttributes(new GamaPoint(0.003,0.003,0.003),null,new GamaPoint(0.01,-0.07,0),new GamaColor(0,0,0,1),font,true);
		StringObject strObj = new StringObject("fps : " + fps, textDrawingAttr, this);
		strObj.enableOverlay(true);
		newElem.add(strObj);
		
		objects.add(newElem);
	}
}