package msi.gama.jogl.gis_3D;

import java.awt.Color;

public class MyGeometry {
	
	public Vertex[] vertices;
	
	public Color color;

	public MyGeometry(int numPoints) {
		vertices = new Vertex[numPoints];
		for (int i = 0; i < numPoints; i++) {
			vertices[i] = new Vertex();
		}

	}

}
