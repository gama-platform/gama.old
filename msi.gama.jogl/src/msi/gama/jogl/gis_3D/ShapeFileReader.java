package msi.gama.jogl.gis_3D;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;

import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.*;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Point;


/**
 * NeHe Lesson 2: Your First Polygon
 */
public class ShapeFileReader {

	public double xBoundcenter;
	public double yBoundcenter;
	public double maxBoundDimension;
	public double boundWidth;
	public double boundHeight;

	// Constructor
	public ShapeFileReader() {
	}

	public ShapefileDataStore GetDataStore() throws IOException {
		// Get a shapefile from a dialog window
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return null;
		}
		ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
		return store;

	}

	public String GetGeometryType(ShapefileDataStore store) throws IOException {

		String type = null;
		Class<?> jtsClass = store.getFeatureSource().getSchema()
				.getGeometryDescriptor().getType().getBinding();

		if (jtsClass.equals(Point.class)) {
			type = "POINT";
		} else if (jtsClass.equals(MultiPoint.class)) {
			type = "MULTI-POINT";
		} else if (jtsClass.equals(LineString.class)) {
			type = "LINE";
		} else if (jtsClass.equals(MultiLineString.class)) {
			type = "MULTI-LINE";
		} else if (jtsClass.equals(Polygon.class)) {
			type = "POLYGON";
		} else if (jtsClass.equals(MultiPolygon.class)) {
			type = "MULTI-POLYGON";
		}
		return type;

	}

	public void printPolygonPointFromShapeFile() {

		// Get a shapefile from a dialog window
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return;
		}

		try {

			ShapefileDataStore store = new ShapefileDataStore(file.toURI()
					.toURL());

			SimpleFeatureSource featureSource = store.getFeatureSource();
			SimpleFeatureCollection collection = featureSource.getFeatures();
			SimpleFeatureIterator iterator = collection.features();

			// Create a polygon for each feature of the collection.
			try {
				int count = 0;
				while (iterator.hasNext()) {
					SimpleFeature feature = (SimpleFeature) iterator.next();
					Geometry sourceGeometry = (Geometry) feature
							.getDefaultGeometry();
					MultiPolygon polygons = (MultiPolygon) sourceGeometry;
					int size = polygons.getNumGeometries();
					count++;
					System.out.println("\nPolygon: " + (count));
					// Get each vertex of the polygon and its centroid.
					for (int i = 0; i < size; i++) {

						Polygon p = (Polygon) polygons.getGeometryN(i);
						int numPoints = p.getNumPoints();

						System.out.println("Centroid: "
								+ p.getCentroid().getX() + "/"
								+ p.getCentroid().getY());
						for (int j = 0; j < numPoints; j++) {

							Point pt = p.getExteriorRing().getPointN(j);
							System.out.println("Point" + j + ": " + pt.getX()
									+ "/" + pt.getY());
						}
					}
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
			System.out.print("\nERROR: " + e.getMessage());
		}
	}

	public List<Polygon> getPolygonsFromShapeFile() {

		// Get a shapefile from a dialog window
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return null;
		}

		List<Polygon> myPolygons = new ArrayList<Polygon>();
		try {

			ShapefileDataStore store = new ShapefileDataStore(file.toURI()
					.toURL());

			SimpleFeatureSource featureSource = store.getFeatureSource();
			SimpleFeatureCollection collection = featureSource.getFeatures();
			SimpleFeatureIterator iterator = collection.features();

			SetBoundCollectionData(collection);

			// Create a polygon for each feature of the collection.
			try {

				while (iterator.hasNext()) {
					SimpleFeature feature = (SimpleFeature) iterator.next();
					Geometry sourceGeometry = (Geometry) feature
							.getDefaultGeometry();
					MultiPolygon polygons = (MultiPolygon) sourceGeometry;

					// Get each polygon and add it in the list myPolygons.
					int nbPolygons = polygons.getNumGeometries();
					for (int i = 0; i < nbPolygons; i++) {
						Polygon p = (Polygon) polygons.getGeometryN(i);
						myPolygons.add(p);
					}
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
			System.out.print("\nERROR: " + e.getMessage());
		}
		return myPolygons;
	}



	public SimpleFeatureCollection getFeatureCollectionFromShapeFile(
			ShapefileDataStore store) {

		SimpleFeatureCollection collection = null;
		try {

			SimpleFeatureSource featureSource = store.getFeatureSource();
			collection = featureSource.getFeatures();
			SetBoundCollectionData(collection);

		} catch (Throwable e) {
			System.out.print("\nERROR: " + e.getMessage());
		}
		return collection;
	}

	public List<LineString> getLinesFromShapeFile() {

		// Get a shapefile from a dialog window
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return null;
		}

		List<LineString> myLines = new ArrayList<LineString>();
		try {

			ShapefileDataStore store = new ShapefileDataStore(file.toURI()
					.toURL());

			SimpleFeatureSource featureSource = store.getFeatureSource();
			SimpleFeatureCollection collection = featureSource.getFeatures();
			SimpleFeatureIterator iterator = collection.features();

			SetBoundCollectionData(collection);

			// Create a line segment for each feature of the collection.
			try {

				while (iterator.hasNext()) {
					SimpleFeature feature = (SimpleFeature) iterator.next();
					Geometry sourceGeometry = (Geometry) feature
							.getDefaultGeometry();
					MultiLineString lines = (MultiLineString) sourceGeometry;

					// System.out.println("sourceGeometry.getGeometryType() " +
					// sourceGeometry.getGeometryType());

					// Get each line and add it in the list myLines.
					int nbLines = lines.getNumPoints();
					for (int i = 0; i < nbLines; i++) {
						LineString l = (LineString) lines.getGeometryN(i);
						myLines.add(l);
					}
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
			System.out.print("\nERROR: " + e.getMessage());
		}
		return myLines;
	}
	
	public void SetBoundCollectionData(SimpleFeatureCollection collection) {
		// get the center of the collection
		xBoundcenter = collection.getBounds().centre().x;
		yBoundcenter = collection.getBounds().centre().y;

		// get Width and Height of the bound
		boundWidth = collection.getBounds().getWidth();
		boundHeight = collection.getBounds().getHeight();

		if (boundHeight < boundWidth) {
			maxBoundDimension = boundWidth;
		} else {
			maxBoundDimension = boundHeight;
		}

	}

}
