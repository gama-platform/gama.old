package msi.gama.jogl.utils.dem;

import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import com.sun.opengl.util.texture.*;

public class DigitalElevationModelDrawer {

	static int terrain;
	private boolean initialized;
	private final JOGLAWTGLRenderer myGLRenderer;

	public DigitalElevationModelDrawer(JOGLAWTGLRenderer glRenderer) {
		setInitialized(false);
		myGLRenderer = glRenderer;
	}

	public void init(GL gl) {
		if ( !isInitialized() ) {
			System.out.println("InitDEM");
			final String TEXTURE_FILEPATH = "/Users/Arno/Desktop/DEM/Texture.png";
			final String DEM_FILEPATH = "/Users/Arno/Desktop/DEM/DEM.png";

			terrain = gl.glGenLists(1);
			gl.glNewList(terrain, GL.GL_COMPILE);
			CreateDEM(gl, DEM_FILEPATH, 20.0f, 20.0f);
			gl.glEndList();

			/* Load the texture */
			gl.glEnable(GL.GL_TEXTURE_2D);
			loadTexture(TEXTURE_FILEPATH);
			setInitialized(true);
		}
	}

	public void DisplayDEM(GL gl) {
		System.out.println("DisplayDEM");
		gl.glCallList(terrain);
	}

	private void CreateDEM(GL gl, String demFileName, float w, float h) {

		int rows, cols;
		int x, y;
		float vx, vy, s, t;
		float ts, tt, tw, th;

		BufferedImage dem = readPNGImage(demFileName);

		rows = dem.getHeight() - 1;
		cols = dem.getWidth() - 1;
		ts = 1.0f / cols;
		tt = 1.0f / rows;

		tw = w / cols;
		th = h / rows;

		gl.glNormal3f(0.0f, 1.0f, 0.0f);

		for ( y = 0; y < rows; y++ ) {
			gl.glBegin(GL.GL_QUAD_STRIP);
			for ( x = 0; x <= cols; x++ ) {
				vx = tw * x - w / 2.0f;
				vy = th * y - h / 2.0f;
				s = 1.0f - ts * x;
				t = 1.0f - tt * y;

				float alt1 = (dem.getRGB(cols - x, y) & 255) * 0.025f;
				float alt2 = (dem.getRGB(cols - x, y + 1) & 255) * 0.025f;

				gl.glTexCoord2f(s, t);
				gl.glVertex3f(vx, vy, alt1);

				gl.glTexCoord2f(s, t - tt);
				gl.glVertex3f(vx, vy + th, alt2);
			}
			gl.glEnd();
		}

	}

	/**
	 * Texture loader utilizes JOGL's provided utilities to produce a texture.
	 * 
	 * @param fileName relative filename from execution point
	 * @return a texture binded to the OpenGL context
	 */
	public Texture loadTexture(String fileName) {
		Texture text = null;
		try {
			if ( myGLRenderer.getContext() != null ) {
				myGLRenderer.getContext().makeCurrent();
				text = TextureIO.newTexture(new File(fileName), false);
				text.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				text.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			} else {
				throw new GamaRuntimeException("(DEM) JOGLRenderer context is null");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error loading texture " + fileName);
		}
		return text;
	}

	/**
	 * Reads an image file and flip it upside down.
	 * 
	 * @param resourceName the path to the image file.
	 * @return a <code>BufferedImage</code>.
	 * @throws RuntimeException
	 */
	private BufferedImage readPNGImage(String resourceName) throws RuntimeException {
		try {
			URL url = getResource(resourceName);
			if ( url == null ) { throw new RuntimeException("Error reading resource " + resourceName); }
			BufferedImage img = ImageIO.read(url);
			java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -img.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			img = op.filter(img, null);
			return img;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve a URL resource from the jar. If the resource is not found, then
	 * the local disk is also checked.
	 * 
	 * @param filename Complete filename, including parent path
	 * @return a URL object if resource is found, otherwise null.
	 */
	private URL getResource(final String filename) {
		// Try to load resource from jar
		URL url = ClassLoader.getSystemResource(filename);
		// If not found in jar, then load from disk
		if ( url == null ) {
			try {
				url = new URL("file", "localhost", filename);
			} catch (Exception urlException) {}
		}
		return url;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean demInitialized) {
		this.initialized = demInitialized;
	}

}
