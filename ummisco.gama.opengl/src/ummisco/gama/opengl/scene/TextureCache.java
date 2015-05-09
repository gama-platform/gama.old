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

	private final static TextureCache instance = new TextureCache();
	private final Map<BufferedImage, Texture> staticTextures = new ConcurrentHashMap(100, 0.75f, 1);
	private final Map<ModelScene, Map<BufferedImage, Texture>> dynamicTextures = new ConcurrentHashMap(10, 0.75f, 1);

	private final TextureAsyncBuilder BUILDER = new TextureAsyncBuilder();

	private TextureCache() {}

	public static TextureCache getInstance() {
		return instance;
	}

	public GLAutoDrawable getSharedContext() {
		return BUILDER.drawable;
	}

	private Texture retrieve(final ModelScene scene, final BufferedImage image, final boolean isDynamic) {
		if ( isDynamic ) {
			Map<BufferedImage, Texture> map = dynamicTextures.get(scene);
			if ( map == null ) {
				return null;
			} else {
				return map.get(image);
			}
		} else {
			return staticTextures.get(image);
		}

	}

	// Stores the texture for the given image. Renderer can be null (in which case the texture is considered as static
	void store(final ModelScene scene, final BufferedImage image, final Texture texture) {
		if ( texture == null || image == null ) { return; }

		if ( scene != null ) {
			Map<BufferedImage, Texture> map = dynamicTextures.get(scene);
			if ( map == null ) {
				map = new ConcurrentHashMap(50, 0.75f, 1);
				map.put(image, texture);
				dynamicTextures.put(scene, map);
			}
		} else {
			staticTextures.put(image, texture);
		}
	}

	public void clearDynamicTextures(final ModelScene scene) {
		// clearCache(renderer);
		Map<BufferedImage, Texture> map = dynamicTextures.get(scene);
		if ( map != null ) {
			int size = map.size();
			int[] textureIdsToDestroy = new int[size];
			int index = 0;
			for ( Map.Entry<BufferedImage, Texture> entry : map.entrySet() ) {
				Texture t = entry.getValue();
				textureIdsToDestroy[index++] = t == null ? 0 : t.getTextureObject();
				BUILDER.tasks.offer(new DestroyingTask(scene, textureIdsToDestroy));
			}
			clearCache(scene);
		}

	}

	public void clearCache(final ModelScene scene) {
		dynamicTextures.remove(scene);
	}

	// Assumes the texture has been created. But it may be processed at the time of the call, so we wait a bit for its
	// availability.
	public Texture get(final GL gl, final ModelScene scene, final BufferedImage image, final boolean isDynamic) {
		if ( image == null ) { return null; }
		Texture texture = retrieve(scene, image, isDynamic);
		while (texture == null) {
			texture = retrieve(scene, image, isDynamic);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if ( texture != null ) {
			// FIXME
			boolean antiAlias = scene.renderer.data.isAntialias();
			// Apply antialas to the texture based on the current preferences
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		}
		return texture;
	}

	public void initializeStaticTexture(final BufferedImage image) {
		if ( hasStaticTexture(image) ) { return; }
		BuildingTask task = new BuildingTask(null, image);
		BUILDER.tasks.offer(task);
	}

	/**
	 * @param image
	 * @return
	 */
	private boolean hasStaticTexture(final BufferedImage image) {
		return staticTextures.containsKey(image);
	}

	public void initializeDynamicTexture(final ModelScene scene, final BufferedImage image) {
		if ( hasDynamicTexture(scene, image) ) { return; }
		BuildingTask task = new BuildingTask(scene, image);
		BUILDER.tasks.offer(task);
	}

	/**
	 * @param renderer
	 * @param image
	 * @return
	 */
	private boolean hasDynamicTexture(final ModelScene scene, final BufferedImage image) {
		Map<BufferedImage, Texture> map = dynamicTextures.get(scene);
		if ( map == null ) { return false; }
		return map.containsKey(image) && map.get(image) != null;
	}

	private Texture buildTexture(final GL gl, final BufferedImage image) {
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

	private BufferedImage correctImage(final BufferedImage image) {
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

	public class TextureAsyncBuilder implements Runnable {

		final LinkedBlockingQueue<GLTask> tasks = new LinkedBlockingQueue<GLTask>();
		final GLAutoDrawable drawable;
		final Thread loadingThread;

		public TextureAsyncBuilder() {
			GLProfile profile = GLProfile.getDefault();
			GLCapabilities cap = new GLCapabilities(profile);
			cap.setStencilBits(8);
			cap.setDoubleBuffered(true);
			cap.setHardwareAccelerated(true);
			cap.setOnscreen(false);
			loadingThread = new Thread(this, "Texture building thread");
			drawable = GLDrawableFactory.getFactory(profile).createDummyAutoDrawable(null, true, cap, null);
			// drawable.setExclusiveContextThread(loadingThread);
			loadingThread.start();
		}

		@Override
		public void run() {
			drawable.display();
			final ArrayList<GLTask> copy = new ArrayList();
			while (true) {
				tasks.drainTo(copy);
				drawable.getContext().makeCurrent();
				for ( GLTask currentTask : copy ) {
					currentTask.runIn(drawable.getGL());
				}
				drawable.getContext().release();
				copy.clear();
			}
		}

	}

	protected abstract class GLTask {

		protected final ModelScene scene;

		GLTask(final ModelScene scene) {
			this.scene = scene;
		}

		abstract void runIn(GL gl);
	}

	protected class DestroyingTask extends GLTask {

		final int[] textureIds;

		DestroyingTask(final ModelScene scene, final int[] textureIds) {
			super(scene);
			this.textureIds = textureIds;
		}

		/**
		 * Method runIn()
		 * @see ummisco.gama.opengl.scene.TextureCache.GLTask#runIn(com.jogamp.opengl.GL)
		 */
		@Override
		public void runIn(final GL gl) {
			gl.glDeleteTextures(textureIds.length, textureIds, 0);
		}

	}

	protected class BuildingTask extends GLTask {

		protected final BufferedImage image;

		BuildingTask(final ModelScene scene, final BufferedImage image) {
			super(scene);
			this.image = image;
		}

		@Override
		public void runIn(final GL gl) {
			if ( scene != null && hasDynamicTexture(scene, image) ) { return; }
			if ( scene == null && hasStaticTexture(image) ) { return; }
			Texture texture = buildTexture(gl, image);
			System.out.println("Building texture : " + image);
			// We use the original image to keep track of the texture
			if ( texture != null ) {
				store(scene, image, texture);
			}

		}
	}
}
