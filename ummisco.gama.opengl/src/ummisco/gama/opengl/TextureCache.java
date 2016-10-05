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
package ummisco.gama.opengl;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.common.util.ImageUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaImageFile;
import ummisco.gama.opengl.scene.ModelScene;
import ummisco.gama.opengl.scene.PGMTextureProvider;

public class TextureCache {

	static {
		TextureIO.addTextureProvider(new PGMTextureProvider());
		GamaPreferences.DISPLAY_POWER_OF_TWO.addChangeListener(new IPreferenceChangeListener<Boolean>() {

			@Override
			public boolean beforeValueChange(final Boolean newValue) {
				return true;
			}

			@Override
			public void afterValueChange(final Boolean newValue) {
				TextureIO.setTexRectEnabled(newValue);
			}
		});
		TextureIO.setTexRectEnabled(GamaPreferences.DISPLAY_POWER_OF_TWO.getValue());
	}

	final Map<String, Texture> textures = new ConcurrentHashMap<>(100, 0.75f, 4);
	private static volatile TextureAsyncBuilder BUILDER;

	public static GLAutoDrawable getSharedContext() {
		if (BUILDER == null) {
			BUILDER = new TextureAsyncBuilder();
		}
		return BUILDER.drawable;
	}

	public void dispose(final GL gl) {
		if (this == sharedInstance) {
			return;
		}
		for (final Texture t : textures.values()) {
			t.destroy(gl);
		}
		textures.clear();
	}

	// Assumes the texture has been created. But it may be processed at the time
	// of the call, so we wait for its availability.
	public Texture get(final IScope scope, final GL gl, final GamaImageFile image) {
		if (image == null) {
			return null;
		}
		Texture texture = textures.get(image.getPath(scope));
		if (texture == null) {
			if (!GamaPreferences.DISPLAY_SHARED_CONTEXT.getValue()) {
				if (!gl.getContext().isCurrent()) {
					gl.getContext().makeCurrent();
				}
				texture = buildTexture(scope, gl, image);
				if (texture != null) {
					textures.put(image.getPath(scope), texture);
				}
			} else {
				while (texture == null) {
					texture = textures.get(image.getPath(scope));
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return texture;
	}

	public void initializeStaticTexture(final IScope scope, final GamaImageFile image) {
		if (!GamaPreferences.DISPLAY_SHARED_CONTEXT.getValue()) {
			return;
		}
		if (contains(scope, image)) {
			return;
		}
		final BuildingTask task = new BuildingTask(scope, null, image);
		BUILDER.tasks.offer(task);
	}

	/**
	 * @param image
	 * @return
	 */
	boolean contains(final IScope scope, final GamaImageFile image) {
		return textures.containsKey(image.getPath(scope));
	}

	public static Texture buildTexture(final IScope scope, final GL gl, final GamaImageFile image) {
		try {
			final Texture texture = TextureIO.newTexture(image.getFile(scope), false);
			return texture;
		} catch (final GLException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// These textures are not cached

	public static Texture buildTexture(final GL gl, final BufferedImage image) {
		try {
			// final TextureData data =
			// AWTTextureIO.newTextureData(gl.getGLProfile(),
			// correctImage(image,
			// !JOGLRenderer.isNonPowerOf2TexturesAvailable), GL.GL_RGBA,
			// GL.GL_SRGB8_ALPHA8,
			// true);
			// data.setHaveGL12(true);
			final Texture texture = AWTTextureIO.newTexture(gl.getGLProfile(),
					correctImage(image, !Abstract3DRenderer.isNonPowerOf2TexturesAvailable), true);
			return texture;
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage correctImage(final BufferedImage image, final boolean force) {
		BufferedImage corrected = image;
		if (GamaPreferences.DISPLAY_POWER_OF_TWO.getValue() || force) {
			if (!IsPowerOfTwo(image.getWidth()) || !IsPowerOfTwo(image.getHeight())) {
				final int width = getClosestPow(image.getWidth());
				final int height = getClosestPow(image.getHeight());
				corrected = ImageUtils.createCompatibleImage(width, height);
				final Graphics2D g2 = corrected.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(image, 0, 0, width, height, null);
				g2.dispose();
			}
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

		final LinkedBlockingQueue<GLTask> tasks = new LinkedBlockingQueue<>();
		final GLAutoDrawable drawable;
		final Thread loadingThread;

		public TextureAsyncBuilder() {
			// long t0 = System.currentTimeMillis();
			final GLProfile profile = GLProfile.getDefault();
			// long t1 = System.currentTimeMillis();
			// System.out.println("GLProfile took: " + (t1 - t0) + "ms");
			final GLCapabilities cap = new GLCapabilities(profile);
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
			final Set<GLTask> copy = new HashSet<>();
			while (true) {
				if (tasks.drainTo(copy) == 0) {
					try {
						copy.add(tasks.take());
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					drawable.getContext().makeCurrent();
					for (final GLTask currentTask : copy) {
						currentTask.runIn(drawable.getGL());
					}

				} catch (final com.jogamp.nativewindow.NativeWindowException e) {
					drawable.destroy();
					break;
				} catch (final com.jogamp.opengl.GLException ex) {
					break;
				} finally {
					try {

						drawable.getContext().release();
					} catch (final com.jogamp.nativewindow.NativeWindowException e) {
						drawable.destroy();
					} catch (final com.jogamp.opengl.GLException ex) {
					}
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
		protected final IScope scope;

		BuildingTask(final IScope scope, final ModelScene scene, final GamaImageFile image) {
			this.image = image;
			this.scope = scope;
		}

		@Override
		public void runIn(final GL gl) {

			if (contains(scope, image)) {
				return;
			}
			final Texture texture = buildTexture(scope, gl, image);
			// System.out.println("Building texture : " + image);
			// We use the original image to keep track of the texture
			if (texture != null) {
				textures.put(image.getPath(scope), texture);
			}
			gl.glFinish();

		}
	}

	private static volatile TextureCache sharedInstance;

	/**
	 * @return
	 */
	public static TextureCache getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new TextureCache();
		}
		return sharedInstance;
	}
}
