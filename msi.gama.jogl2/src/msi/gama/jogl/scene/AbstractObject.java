package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;

public abstract class AbstractObject implements ISceneObject {

	static int index = 0;
	static Color pickedColor = Color.red;

	private Color color;
	private Double z_fighting_id = 0.0;
	private Double alpha = 1d;
	public int pickingIndex = index++;
	public boolean picked = false;
	public boolean fill = true;
	private MyTexture texture;

	public AbstractObject(final Color c, final Double a) {
		setColor(c);
		if ( a != null ) {
			setAlpha(a);
		}
	}

	public MyTexture getTexture(final JOGLAWTGLRenderer renderer) {
		if ( texture == null ) {
			setTexture(computeTexture(renderer));
		}
		return texture;
	}

	/**
	 * @param computeTexture
	 */
	private void setTexture(final MyTexture computedTexture) {
		texture = computedTexture;
	}

	/**
	 * @return
	 */
	abstract protected MyTexture computeTexture(final JOGLAWTGLRenderer renderer);

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

	public void setAlpha(final Double alpha) {
		this.alpha = alpha;
	}

	public void dispose(final JOGLAWTGLRenderer renderer) {
		if ( texture != null ) {
			texture.dispose(renderer);
			texture = null;
		}
	}

}
