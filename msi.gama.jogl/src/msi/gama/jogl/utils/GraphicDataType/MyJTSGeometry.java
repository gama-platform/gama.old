package msi.gama.jogl.utils.GraphicDataType;

import java.awt.Color;

import msi.gama.metamodel.shape.GamaPoint;

import com.vividsolutions.jts.geom.Geometry;


public class MyJTSGeometry {
	
	public Geometry geometry;
	
	public float z;
	
	public Color color;
	
	public float alpha;

	public String type;
	
	public Boolean fill;
	
	public Boolean isTextured = false;
	
	public Integer angle =0;

	public float height=0;
	
	public float altitude;
	
	public GamaPoint offSet = new GamaPoint(0,0);
}
