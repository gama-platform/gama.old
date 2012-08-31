package msi.gama.jogl.utils.GraphicDataType;

import java.awt.Color;

import msi.gama.metamodel.shape.GamaPoint;

import com.vividsolutions.jts.geom.Geometry;


public class MyJTSGeometry implements Cloneable{
	
	
	public Geometry geometry;
	
	public float z_layer;
	
	public Color color;
	
	public float alpha;

	public String type;
	
	public Boolean fill=true;
	
	public Boolean isTextured;
	
	public int angle;

	public float height;
	
	public float altitude;
	
	public GamaPoint offSet;
	
	public MyJTSGeometry(Geometry geometry,float z_layer,Color color,float alpha,String type){
		this.geometry = geometry;
		this.z_layer=z_layer;
		this.color = color;
		this.alpha = alpha;
		this.type = type;
		this.fill = true;
		this.isTextured = false;
		this.angle = 0;
		this.height = 0;
		this.altitude = 0;
		this.offSet = new GamaPoint(0,0); 
	}
	
	public MyJTSGeometry(Geometry geometry,float z_layer,Color color,float alpha,Boolean fill, Boolean isTextured,int angle,float height,GamaPoint offSet ){
		this.geometry = geometry;
		this.z_layer=z_layer;
		this.color = color;
		this.alpha = alpha;
		this.type = "";
		this.fill = fill;
		this.isTextured = false;
		this.angle = angle;
		this.height = height;
		this.altitude = 0.0f;
		this.offSet = offSet; 
	}
	

	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return o;
	}
}
