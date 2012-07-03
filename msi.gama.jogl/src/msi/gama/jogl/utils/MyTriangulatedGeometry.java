package msi.gama.jogl.utils;

import java.awt.Color;

import msi.gama.metamodel.shape.IShape;
import msi.gama.util.IList;

import com.vividsolutions.jts.geom.Geometry;


public class MyTriangulatedGeometry {
	
	public IList<IShape> triangles;
	
	public float z;
	
	public Color color;
	
	public float alpha;

	public String type;
	
	public Boolean fill;
	
	public Boolean isTextured = false;
	
	public Integer angle =0;

	public float elevation=0;
}
