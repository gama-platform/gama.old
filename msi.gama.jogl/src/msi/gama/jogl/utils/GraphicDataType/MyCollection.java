package msi.gama.jogl.utils.GraphicDataType;

import java.awt.Color;
import org.geotools.data.simple.SimpleFeatureCollection;

public class MyCollection {

	public SimpleFeatureCollection collection;
	public Color color;

	public MyCollection(SimpleFeatureCollection collection, Color color) {
		this.collection = collection;
		this.color = color;
	}

}
