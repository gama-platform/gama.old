package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public class StringObject extends AbstractObject {

	public String string;
	public String fontName;
	public Integer styleName;
	public Integer angle;
	public double x, y, z, z_layer, height;

	public StringObject(String string, String fontName, Integer styleName, GamaPoint offset, GamaPoint scale,
		Color color, Integer angle, double x, double y, double z, double z_layer, double height, Double alpha) {
		super(color, offset, scale, alpha);
		this.string = string;
		this.fontName = fontName;
		this.styleName = styleName;
		this.angle = angle;
		this.x = x;
		this.y = y;
		this.z = z;
		this.z_layer = z_layer;
		this.height = height;
	}

}
