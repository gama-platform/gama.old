package msi.gama.jogl.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import com.sun.opengl.util.texture.*;


public class MyImage {
	
	public BufferedImage image;
	//texture that will be blind to the opengl context.
	public int textureId;
	public float x;
	public float y;
	public float z;
	public float alpha;
	public float width;
	public float height;
	public Integer angle = 0;
	public String name;

}
