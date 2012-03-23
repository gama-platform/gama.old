package msi.gama.jogl.gis_3D;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.JFrame;



import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;


public class HelloWorldOpenGL {

	// Main program
	public static void main(String[] args) throws Exception {

		final int WINDOW_WIDTH = 640;
		final int WINDOW_HEIGHT = 480;
		final String WINDOW_TITLE = "Gama OpenGL 3D GIS";

		// Create A 3D World environment.
		final NeheJOGL02Basics world = new NeheJOGL02Basics();
		
		JFrame frame = new JFrame();
		frame.setContentPane(world);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Use a dedicate thread to run the stop() to ensure that the
				// animator stops before program exits.
				new Thread() {
					@Override
					public void run() {
						world.animator.stop(); // stop the animator loop
						System.exit(0);
					}
				}.start();
			}
		});

		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setTitle(WINDOW_TITLE);
		frame.setVisible(true);
		world.animator.start(); // start the animation loop

	}

}
