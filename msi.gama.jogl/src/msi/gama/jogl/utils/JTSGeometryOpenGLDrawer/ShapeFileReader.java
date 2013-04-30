package msi.gama.jogl.utils.JTSGeometryOpenGLDrawer;

import static javax.media.opengl.GL2.GL_QUADS;
import static javax.media.opengl.GL2.GL_TRIANGLES;

import java.awt.Color;
import java.util.Iterator;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;
import javax.vecmath.Vector3f;

import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Vertex;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.util.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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


public class ShapeFileReader {

	public ShapefileDataStore store;
	public ShapeFileReader(String FileName){
		
       File myShapeFile = new File(FileName);
		try {
			store = new ShapefileDataStore(myShapeFile.toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public SimpleFeatureCollection GetShapeFile() {
        // Read a shapeFile
        try {
            store = GetDataStore();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return getFeatureCollectionFromShapeFile(store);
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
        
    
    public SimpleFeatureCollection getFeatureCollectionFromShapeFile(
            ShapefileDataStore store) {

        SimpleFeatureCollection collection = null;
        try {

            SimpleFeatureSource featureSource = store.getFeatureSource();
            collection = featureSource.getFeatures();
        } catch (Throwable e) {
            System.out.print("\nERROR: " + e.getMessage());
        }
        return collection;
    }


}
