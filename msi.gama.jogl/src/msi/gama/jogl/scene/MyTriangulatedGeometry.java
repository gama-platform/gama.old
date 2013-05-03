package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.IList;

public class MyTriangulatedGeometry {

	public IList<IShape> triangles;

	public double z;

	public Color color;

	public double alpha;

	public String type;

	public Boolean fill;

	public Boolean isTextured = false;

	public Integer angle = 0;

	public double elevation = 0;
}
