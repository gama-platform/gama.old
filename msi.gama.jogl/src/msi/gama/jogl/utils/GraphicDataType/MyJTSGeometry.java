package msi.gama.jogl.utils.GraphicDataType;

import java.awt.Color;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

import com.vividsolutions.jts.geom.Geometry;


public class MyJTSGeometry implements Cloneable{
	
	
	public Geometry geometry;
	
	public IAgent agent;
	
	public float z_layer;
	
	public int layerId;
	
	public Color color;
	
	public float alpha;

	public String type;
	
	public Boolean fill=true;
	
	public Color border;
	
	public Boolean isTextured;
	
	public int angle;

	public float height;
	
	public float altitude;
	
	public boolean rounded;
	
	public GamaPoint offSet;

	
	public MyJTSGeometry(Geometry geometry,IAgent agent,float z_layer,int layerId, Color color,float alpha,Boolean fill, Color border,Boolean isTextured,int angle,float height,GamaPoint offSet, boolean rounded, String type ){
		this.geometry = geometry;
		this.agent = agent;
		this.z_layer=z_layer;
		this.layerId=layerId;
		this.color = color;
		this.alpha = alpha;
		this.type = type;
		this.fill = fill;
		this.border = border;
		this.isTextured = false;
		this.angle = angle;
		this.height = height;
		this.altitude = 0.0f;
		this.offSet = offSet;
		this.rounded = rounded;
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
