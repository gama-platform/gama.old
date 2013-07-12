package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public class StringObject extends AbstractObject {

	public String string;
	public String font = "Helvetica";
	public Integer style = 0;
	public Integer angle = 0;
	public Integer size = 12;
	public Double sizeInModelUnits = 12d;
	public double x, y, z, z_layer;
	//Draw using TextRenderer(0) or glutBitmapString(1).
	public Integer type = 0;

	public StringObject(String string, String font, Integer style, GamaPoint offset, GamaPoint scale, Color color,
		Integer angle, double x, double y, double z, double z_layer, Double sizeInModelUnits, Integer size, Double alpha, Integer type) {
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
		if ( type != null ) {
			this.type = type;
		}
	}

}
