package msi.gama.jogl.utils;

import java.awt.Color;


public class MyGeometry {
	
	public Vertex[] vertices;
	
	public Color color;
	
	public String type;
	
	public float size;

	public MyGeometry(int numPoints) {
		vertices = new Vertex[numPoints];
		for (int i = 0; i < numPoints; i++) {
			vertices[i] = new Vertex();
		}

	}

}
