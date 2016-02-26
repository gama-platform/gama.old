/*********************************************************************************************
 *
 *
 * 'MyTexture.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import msi.gama.util.file.GamaImageFile;

public class TextureCache {

	static {
		TextureIO.addTextureProvider(new PGMTextureProvider());
		TextureIO.setTexRectEnabled(false);
	}

	private final Map<String, Texture> TEXTURES = new ConcurrentHashMap(100, 0.75f, 4);
	private static TextureAsyncBuilder BUILDER;

	public static GLAutoDrawable getSharedContext() {
		if ( BUILDER == null ) {
			BUILDER = new TextureAsyncBuilder();
		}
		return BUILDER.drawable;
	}

	// Assumes the texture has been created. But it may be processed at the time
	// of the call, so we wait for its availability.
	public Texture get(final GL gl, final GamaImageFile image) {
		if ( image == null ) { return null; }
		Texture texture = TEXTURES.get(image.getPath());
		if ( texture == null ) {
			if ( !GamaPreferences.DISPLAY_SHARED_CONTEXT.getValue() ) {
				if ( !gl.getContext().isCurrent() ) {
					gl.getContext().makeCurrent();
				}
				texture = buildTexture(gl, image);
				if ( texture != null ) {
					TEXTURES.put(image.getPath(), texture);
				}
			} else {
				while (texture == null) {
					texture = TEXTURES.get(image.getPath());
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return texture;
	}

	public void initializeStaticTexture(final GamaImageFile image) {
		if ( !GamaPreferences.DISPLAY_SHARED_CONTEXT.getValue() ) { return; }
		if ( contains(image) ) { return; }
		BuildingTask task = new BuildingTask(null, image);
		BUILDER.tasks.offer(task);
	}

	/**
	 * @param image
	 * @return
	 */
	boolean contains(final GamaImageFile image) {
		return TEXTURES.containsKey(image.getPath());
	}

	private Texture buildTexture(final GL gl, final GamaImageFile image) {
		try {
			Texture texture = TextureIO.newTexture(image.getFile(), false);
			return texture;
		} catch (final GLException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// These textures are not cached

	Texture buildTexture(final GL gl, final BufferedImage image) {
		try {
			Texture texture = AWTTextureIO.newTexture(gl.getGLProfile(), correctImage(image), true);
			return texture;
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage correctImage(final BufferedImage image) {
		BufferedImage corrected = image;
		if ( !IsPowerOfTwo(image.getWidth()) || !IsPowerOfTwo(image.getHeight()) ) {
			int width = getClosestPow(image.getWidth());
			int height = getClosestPow(image.getHeight());
			corrected = ImageUtils.createCompatibleImage(width, height);
			Graphics2D g2 = corrected.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(image, 0, 0, width, height, null);
			g2.dispose();
		}
		return corrected;
	}

	static boolean IsPowerOfTwo(final int x) {
		return (x & x - 1) == 0;
	}

	static int getClosestPow(final int value) {
		int power = 1;
		while (power < value) {
			power *= 2;
		}
		return power;
	}

	public static class TextureAsyncBuilder implements Runnable {

		final LinkedBlockingQueue<GLTask> tasks = new LinkedBlockingQueue<GLTask>();
		final GLAutoDrawable drawable;
		final Thread loadingThread;

		public TextureAsyncBuilder() {
			// long t0 = System.currentTimeMillis();
			GLProfile profile = GLProfile.getDefault();
			// long t1 = System.currentTimeMillis();
			// System.out.println("GLProfile took: " + (t1 - t0) + "ms");
			GLCapabilities cap = new GLCapabilities(profile);
			cap.setStencilBits(8);
			cap.setDoubleBuffered(true);
			cap.setHardwareAccelerated(true);
			cap.setFBO(true);
			loadingThread = new Thread(this, "Texture building thread");
			drawable = GLDrawableFactory.getFactory(profile).createDummyAutoDrawable(null, true, cap, null);
			loadingThread.start();
		}

		@Override
		public void run() {
			drawable.display();
			final ArrayList<GLTask> copy = new ArrayList();
			while (true) {
				tasks.drainTo(copy);
				try {
					drawable.getContext().makeCurrent();
					for ( GLTask currentTask : copy ) {
						currentTask.runIn(drawable.getGL());
					}

				} catch (com.jogamp.nativewindow.NativeWindowException e) {
					drawable.destroy();
					break;
				} catch (com.jogamp.opengl.GLException ex) {
					break;
				} finally {
					try {

						drawable.getContext().release();
					} catch (com.jogamp.nativewindow.NativeWindowException e) {
						drawable.destroy();
					} catch (com.jogamp.opengl.GLException ex) {}
					copy.clear();
				}
			}
		}

	}

	protected interface GLTask {

		abstract void runIn(GL gl);
	}

	protected class DestroyingTask implements GLTask {

		final int[] textureIds;

		DestroyingTask(final int[] textureIds) {
			this.textureIds = textureIds;
		}

		@Override
		public void runIn(final GL gl) {
			gl.glDeleteTextures(textureIds.length, textureIds, 0);
		}

	}

	protected class BuildingTask implements GLTask {

		protected final GamaImageFile image;

		BuildingTask(final ModelScene scene, final GamaImageFile image) {
			this.image = image;
		}

		@Override
		public void runIn(final GL gl) {

			if ( contains(image) ) { return; }
			Texture texture = buildTexture(gl, image);
			// System.out.println("Building texture : " + image);
			// We use the original image to keep track of the texture
			if ( texture != null ) {
				TEXTURES.put(image.getPath(), texture);
			}
			gl.glFinish();

		}
	}

	private static TextureCache sharedInstance;

	/**
	 * @return
	 */
	public static TextureCache getSharedInstance() {
		if ( sharedInstance == null ) {
			sharedInstance = new TextureCache();
		}
		return sharedInstance;
	}
}
