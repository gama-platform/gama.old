package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public abstract class AbstractObjectSWT implements ISceneObjectSWT {

	public Color color;
	public GamaPoint offset = new GamaPoint(0, 0, 0);
	public GamaPoint scale = new GamaPoint(1, 1, 1);
	public Double alpha = 1d;

	public AbstractObjectSWT(Color c, GamaPoint o, GamaPoint s, Double a) {
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
	}

	@Override
	public void draw(ObjectDrawerSWT drawer, boolean picking) {
		drawer.draw(this);
	}

	@Override
	public void unpick() {}

}
