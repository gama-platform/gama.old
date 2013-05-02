package msi.gama.jogl.utils.GraphicDataType;

import java.awt.Color;
import msi.gama.metamodel.shape.GamaPoint;

public class MyString {

	public String string;
	public String fontName;
	public Integer styleName;
	public GamaPoint offset, scale;
	public Color color;
	public Integer angle;
	public double x, y, z, z_layer, height;

	public MyString(String string, String fontName, Integer styleName, GamaPoint offset, GamaPoint scale, Color color,
		Integer angle, double x, double y, double z, double z_layer, double height) {
		super();
		this.string = string;
		this.fontName = fontName;
		this.styleName = styleName;
		this.offset = offset;
		this.scale = scale;
		this.color = color;
		this.angle = angle;
		this.x = x;
		this.y = y;
		this.z = z;
		this.z_layer = z_layer;
		this.height = height;
	}

}
