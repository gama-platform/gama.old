package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.shape.GamaPoint;

public class StringObject extends AbstractObject {

	public String string;
	public String font = "Helvetica";
	public Integer style = 0;
	public Double angle = 0d;
	public Integer size = 12;
	public GamaPoint location;
	private GamaPoint offset = new GamaPoint(0, 0, 0);
	private GamaPoint scale = new GamaPoint(1, 1, 1);
	// Draw using TextRenderer(0) or glutBitmapString(1).
	public Boolean bitmap;

	public StringObject(final String string, final String font, final Integer style, final GamaPoint offset,
		final GamaPoint scale, final Color color, final Double angle, final GamaPoint location,
		final Integer size, final Double alpha, final Boolean bitmap) {
		super(color, alpha);
		if ( offset != null ) {
			setOffset(offset);
		}
		if ( scale != null ) {
			setScale(scale);;
		}

		this.string = string;
		if ( font != null ) {
			this.font = font;
		}
		if ( style != null ) {
			this.style = style;
		}
		if ( angle != null ) {
			this.angle = angle;
		}
		this.location = location;
		if ( size != null ) {
			this.size = size;
		}
		if ( bitmap != null ) {
			this.bitmap = bitmap;
		}
	}

	public GamaPoint getScale() {
		return scale;
	}

	public void setScale(final GamaPoint scale) {
		this.scale = scale;
	}

	public GamaPoint getOffset() {
		return offset;
	}

	public void setOffset(final GamaPoint offset) {
		this.offset = offset;
	}

	/**
	 * Method computeTexture()
	 * @see msi.gama.jogl.scene.AbstractObject#computeTexture(msi.gama.jogl.utils.JOGLAWTGLRenderer)
	 */
	@Override
	protected MyTexture computeTexture(final JOGLAWTGLRenderer renderer) {
		return null;
	}

}
