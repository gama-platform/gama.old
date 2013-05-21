package msi.gama.jogl.scene;

import java.awt.Color;

import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.matrix.GamaFloatMatrix;

public class DEMObject extends AbstractObject {

	public double[] dem;
	public BufferedImage texture;
	public boolean isTextured;
	public Envelope envelope;
	
	
	
	public DEMObject(double[] dem, BufferedImage texture,Envelope env, boolean isTextured, Color c, GamaPoint o, GamaPoint s, Double a) {
		super(c, o, s, a);
		this.dem = dem;
		this.texture = texture;
		this.isTextured = isTextured;
		this.envelope=env;
	}
}
