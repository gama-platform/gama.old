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
	public boolean fromImage;
	public Envelope envelope;
	
	
	
	public DEMObject(double[] dem, BufferedImage texture,BufferedImage demImg,Envelope env, boolean isTextured, boolean fromImage,Color c, GamaPoint o, GamaPoint s, Double a) {
		super(c, o, s, a);
		this.dem = dem;
		this.texture = texture;
		this.demImg = demImg;
		this.isTextured = isTextured;
		this.fromImage = fromImage;
		this.envelope=env;
	}
}
