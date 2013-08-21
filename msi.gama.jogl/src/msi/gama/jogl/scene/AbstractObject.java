package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public abstract class AbstractObject implements ISceneObject {

	static int index = 0;
	static Color pickedColor = Color.red;
	
	private Color color;
	public GamaPoint offset = new GamaPoint(0, 0, 0);
	public GamaPoint scale = new GamaPoint(1, 1, 1);
	private Double z_fighting_id = 0.0;
	public Double alpha = 1d;
	public int pickingIndex = index++;
	public boolean picked = false;
	//used to determine how to enable the polygon offset
	public boolean fill = true;

	public AbstractObject(Color c, GamaPoint o, GamaPoint s, Double a ){
		setColor(c);
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
	public void draw(final ObjectDrawer drawer, final boolean picking) {
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

	@Override
	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

}
