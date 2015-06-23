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
import java.util.*;
import java.util.concurrent.*;
import msi.gama.common.util.ImageUtils;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class TextureCache {

	private static final Map<BufferedImage, Texture> TEXTURES = new ConcurrentHashMap(100, 0.75f, 1);
	private static final TextureAsyncBuilder BUILDER = new TextureAsyncBuilder();

	public static GLAutoDrawable getSharedContext() {
		return BUILDER.drawable;
	}

	// Assumes the texture has been created. But it may be processed at the time
	// of the call, so we wait for its availability.
	public static Texture get(final GL gl, final BufferedImage image) {
		if ( image == null ) { return null; }
		Texture texture = TEXTURES.get(image);
		while (texture == null) {
			texture = TEXTURES.get(image);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return texture;
	}

	public static void initializeStaticTexture(final BufferedImage image) {
		if ( contains(image) ) { return; }
		BuildingTask task = new BuildingTask(null, image);
		BUILDER.tasks.offer(task);
	}

	/**
	 * @param image
	 * @return
	 */
	static boolean contains(final BufferedImage image) {
		return TEXTURES.containsKey(image);
	}

	public static Texture buildTexture(final GL gl, final BufferedImage image) {
		BufferedImage corrected = correctImage(image);
		try {
			TextureData data = AWTTextureIO.newTextureData(gl.getGLProfile(), corrected, false);
			Texture texture = new Texture(gl, data);
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
			long t0 = System.currentTimeMillis();
			GLProfile profile = GLProfile.getDefault();
			long t1 = System.currentTimeMillis();
			System.out.println("GLProfile took: " + (t1 - t0) + "ms");
			GLCapabilities cap = new GLCapabilities(profile);
			cap.setStencilBits(8);
			cap.setDoubleBuffered(true);
			cap.setHardwareAccelerated(true);
			cap.setOnscreen(false);
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

	protected static class BuildingTask implements GLTask {

		protected final BufferedImage image;

		BuildingTask(final ModelScene scene, final BufferedImage image) {
			this.image = image;
		}

		@Override
		public void runIn(final GL gl) {
			if ( contains(image) ) { return; }
			Texture texture = buildTexture(gl, image);
			System.out.println("Building texture : " + image);
			// We use the original image to keep track of the texture
			if ( texture != null ) {
				TEXTURES.put(image, texture);
			}

		}
	}
}
