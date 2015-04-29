/*********************************************************************************************
 * 
 * 
 * 'StringObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import ummisco.gama.opengl.JOGLRenderer;
import msi.gama.metamodel.shape.GamaPoint;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.texture.Texture;

public class StringObject extends AbstractObject {

	public String string;
	public String font = "Helvetica";
	public Integer style = 0;
	public Double angle = 0d;
	public Integer size = 12;
	public Double sizeInModelUnits = 12d;
	public GamaPoint location;
	private GamaPoint offset = new GamaPoint(0, 0, 0);
	private GamaPoint scale = new GamaPoint(1, 1, 1);
	// Draw using TextRenderer(0) or glutBitmapString(1).
	public Boolean bitmap;

	public StringObject(final String string, final String font, final Integer style, final GamaPoint offset,
		final GamaPoint scale, final Color color, final Double angle, final GamaPoint location,
		final Double sizeInModelUnits, final Integer size, final Double alpha, final Boolean bitmap) {
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
		if ( sizeInModelUnits != null ) {
			this.sizeInModelUnits = sizeInModelUnits;
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
	 * @see ummisco.gama.opengl.scene.AbstractObject#computeTexture(ummisco.gama.opengl.utils.JOGLAWTGLRenderer)
	 */
	@Override
	protected Texture computeTexture(final GL gl, final JOGLRenderer renderer) {
		return null;
	}

}
