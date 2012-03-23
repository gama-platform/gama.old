package msi.gama.jogl.utils;

import msi.gama.jogl.gis_3D.ShapeFileReader;
import msi.gama.jogl.gis_3D.World_3D;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;

public class SimpleAWTScene {
	public static void main(String[] args) throws IOException {

		// Read a shapeFile
		ShapeFileReader reader = new ShapeFileReader();

		ShapefileDataStore store = reader.GetDataStore();

		SimpleFeatureCollection myCollection = reader
				.getFeatureCollectionFromShapeFile(store);

		// Create A 3D World environment from a datashape
		final World_3D world = new World_3D();

		//GLCanvas canvas = new GLCanvas(world);

		Frame frame = new Frame("AWT Window Test");
		frame.setSize(300, 300);
		frame.add(world);
		//frame.addGLEventListener(world.myListener);
		frame.addKeyListener(world.myListener);
		frame.addMouseListener(world.myListener);
		frame.addMouseMotionListener(world.myListener);
		frame.addMouseWheelListener(world.myListener);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}