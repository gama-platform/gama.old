package msi.gama.jogl.scene;

import java.awt.Color;

import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.matrix.GamaFloatMatrix;

public class DEMObject extends AbstractObject {

	public double[] dem;
	public BufferedImage texture;
	public BufferedImage demImg;
	public boolean isTextured;
	public boolean isTriangulated;
	public boolean isShowText;
	public boolean fromImage;
	public Envelope envelope;
	public int cellSize;
	public Double z_factor;
	
	
	
	public DEMObject(double[] dem, BufferedImage texture,BufferedImage demImg,Envelope env, boolean isTextured, boolean isTriangulated,boolean isShowText,  
			boolean fromImage, Double z_factor, Color c, GamaPoint o, GamaPoint s, Double a, int cellSize) {
		super(c, o, s, a);
		this.dem = dem;
		this.texture = texture;
		this.demImg = demImg;
		this.isTextured = isTextured;
		this.isTriangulated = isTriangulated;
		this.isShowText = isShowText;
		this.fromImage = fromImage;
		this.envelope=env;
		this.z_factor = z_factor;
		this.cellSize = cellSize;
	}
}
