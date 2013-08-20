package msi.gama.jogl.scene;

import java.awt.Color;
import org.geotools.data.simple.SimpleFeatureCollection;

public class CollectionObject extends AbstractObject {

	public SimpleFeatureCollection collection;

	public CollectionObject(SimpleFeatureCollection collection, Color color) {
		super(color, null, null, null,0);
		this.collection = collection;
	}

}
