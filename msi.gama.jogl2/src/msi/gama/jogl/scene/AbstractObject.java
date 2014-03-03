package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public abstract class AbstractObject implements ISceneObject {

	static int index = 0;
	static Color pickedColor = Color.red;

	private Color color;
	private GamaPoint offset = new GamaPoint(0, 0, 0);
	private GamaPoint scale = new GamaPoint(1, 1, 1);
	private Double z_fighting_id = 0.0;
	private Double alpha = 1d;
	public int pickingIndex = index++;
	public boolean picked = false;
	public boolean fill = true;

	public AbstractObject(final Color c, final GamaPoint o, final GamaPoint s, final Double a) {
		setColor(c);
		if ( o != null ) {
			setOffset(o);
		}
		if ( s != null ) {
			setScale(s);
		}
		if ( a != null ) {
			setAlpha(a);
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

	public void setZ_fighting_id(final Double z_fighting_id) {
		this.z_fighting_id = z_fighting_id;
	}

	@Override
	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}

	public GamaPoint getScale() {
		return scale;
	}

	public void setScale(GamaPoint scale) {
		this.scale = scale;
	}

	public GamaPoint getOffset() {
		return offset;
	}

	public void setOffset(GamaPoint offset) {
		this.offset = offset;
	}

}
