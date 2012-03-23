package msi.gama.jogl.gis_3D;

public class MyGeometry {
	
	public Vertex[] vertices;

	public MyGeometry(int numPoints) {
		vertices = new Vertex[numPoints];
		for (int i = 0; i < numPoints; i++) {
			vertices[i] = new Vertex();
		}

	}

}
