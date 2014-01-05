package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public class StringObject extends AbstractObject {

	public String string;
	public String font = "Helvetica";
	public Integer style = 0;
	public Double angle = 0d;
	public Integer size = 12;
	public Double sizeInModelUnits = 12d;
	public double x, y, z, z_layer;
	// Draw using TextRenderer(0) or glutBitmapString(1).
	public Boolean bitmap;

	public StringObject(final String string, final String font, final Integer style, final GamaPoint offset,
		final GamaPoint scale, final Color color, final Double angle, final double x, final double y, final double z,
		final double z_layer, final Double sizeInModelUnits, final Integer size, final Double alpha,
		final Boolean bitmap) {
		super(color, offset, scale, alpha);
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
		this.x = x;
		this.y = y;
		this.z = z;
		this.z_layer = z_layer;
		if ( size != null ) {
			this.size = size;
		}
		if ( bitmap != null ) {
			this.bitmap = bitmap;
		}
	}

}
