package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public abstract class AbstractObject implements ISceneObject {

	public Color color;
	public GamaPoint offset = new GamaPoint(0, 0, 0);
	public GamaPoint scale = new GamaPoint(1, 1, 1);
	private Double z_fighting_id = 0.0;
	public Double alpha = 1d;

	public AbstractObject(Color c, GamaPoint o, GamaPoint s, Double a) {
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
	public void draw(ObjectDrawer drawer, boolean picking) {
		drawer.draw(this);
	}

	@Override
	public void unpick() {}
	
	public Double getZ_fighting_id() {
		return z_fighting_id;
	}

	public void setZ_fighting_id(Double z_fighting_id) {
		this.z_fighting_id = z_fighting_id;
	}

}
