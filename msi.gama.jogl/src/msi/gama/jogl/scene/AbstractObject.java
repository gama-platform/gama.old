package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public abstract class AbstractObject implements ISceneObject {

	public Color color;
	public GamaPoint offset = new GamaPoint(0, 0, 0);
	public GamaPoint scale = new GamaPoint(1, 1, 1);
	public int layerId = 0;
	public Double alpha = 1d;

	public AbstractObject(Color c, GamaPoint o, GamaPoint s, Double a, int l) {
		color = c;
		if ( o != null ) {
			offset = o;
		}
		if ( s != null ) {
			scale = s;
		}
		if ( a != null ) {
			alpha = a;
		}
		layerId = l;
	}

	@Override
	public void draw(ObjectDrawer drawer, boolean picking) {
		drawer.draw(this);
	}

	@Override
	public void unpick() {}

}
