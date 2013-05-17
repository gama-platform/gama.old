package msi.gama.jogl.scene;

import java.awt.Color;

import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;

public class DEMObject extends AbstractObject {

	public BufferedImage dem;
	public BufferedImage texture;
	public Envelope envelope;
	
	
	
	public DEMObject(BufferedImage dem, BufferedImage texture,Envelope env, Color c, GamaPoint o, GamaPoint s, Double a) {
		super(c, o, s, a);
		this.dem = dem;
		this.texture = texture;
		this.envelope=env;
	}
}
