/*********************************************************************************************
 *
 * 'ModernRenderer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import ummisco.gama.modernOpenGL.ModernDrawer;
import ummisco.gama.opengl.utils.GLUtilLight;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * This class plays the role of Renderer and IGraphics. Class ModernRenderer.
 *
 * @author mazarsju
 * @since 23 avr. 2016
 *
 */
public class ModernRenderer extends Abstract3DRenderer {

	private Matrix4f projectionMatrix;
	private ModernDrawer drawer;
	public boolean renderToTexture = true;
	public boolean colorPicking = false;

	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);

	public ModernRenderer(final SWTOpenGLDisplaySurface d) {
		super(d);
	}

	@Override
	public boolean useShader() {
		return true;
	}

	public boolean preloadTextures() {
		return false;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {

		commonInit(drawable);
		drawer = new ModernDrawer(this, gl);
		GLUtilLight.InitializeLighting(gl, data, true);
		// We mark the renderer as inited
		inited = true;
	}

	@Override
	public void display(final GLAutoDrawable drawable) {

		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) { return; }
		gl = drawable.getGL().getGL2();

		if (renderToTexture)
			drawer.prepareFrameBufferObject();

		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do

		updateCameraPosition();
		updatePerspective();

		rotateModel(gl);
		drawScene(gl);
		if (renderToTexture) {
			gl.glDisable(GL.GL_DEPTH_TEST); // disables depth testing
			drawer.renderToTexture();
		}

		if (!visible) {
			// We make the canvas visible only after a first display has occured
			visible = true;
			WorkbenchHelper.run(() -> getCanvas().setVisible(true));

		}

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
			final int height) {
		// Get the OpenGL graphics context
		if (width <= 0 || height <= 0) { return; }
		updatePerspective();
	}

	@Override
	protected final void updatePerspective() {
		final int height = getDrawable().getSurfaceHeight();
		final int width = getDrawable().getSurfaceWidth();
		final double maxDim = getMaxEnvDim();
		final double fov = data.getCameralens();

		projectionMatrix = TransformationMatrix.createProjectionMatrix(data.isOrtho(), height, width, maxDim, fov);

		camera.animate();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void beginPicking(final GL2 gl) {
		// TODO
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * 
	 * @return name of selected object
	 */
	@Override
	public void endPicking(final GL2 gl) {
		// TODO
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) {
		// TODO
		sceneBuffer.garbageCollect((GL2) drawable.getGL());
		sceneBuffer.dispose();

		drawer.cleanUp();

		textureCache.dispose(drawable.getGL());
		geometryCache.dispose(drawable.getGL().getGL2());
		textRendererCache.dispose(drawable.getGL());
		this.canvas = null;
		this.camera = null;
		this.currentLayer = null;
		// this.setCurrentPickedObject(null);
		this.currentScene = null;
		drawable.removeGLEventListener(this);
	}

	// Use when the rotation button is on.
	public void rotateModel(final GL2 gl) {
		if (data.isRotationOn()) {
			currentZRotation++;
		}
	}

	public ModernDrawer getDrawer() {
		return drawer;
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }
		if (attributes.getSize() == null) {
			attributes.setSize(worldDimensions);
		}

		if (file instanceof GamaGeometryFile && !ENVELOPES_CACHE.containsKey(file.getPath(surface.getScope()))) {
			ENVELOPES_CACHE.put(file.getPath(surface.getScope()), file.computeEnvelope(surface.getScope()));
		}
		sceneBuffer.getSceneToUpdate().addFile(file, attributes);
		return rect;
	}

	@Override
	public Rectangle2D drawField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		// TODO
		return null;
	}

	@Override
	public GamaPoint getRealWorldPointFromWindowPoint(final Point windowPoint) {
		// TODO
		return null;
	}

	/**
	 * Method beginOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#beginOverlay(msi.gama.outputs.layers.OverlayLayer)
	 */
	@Override
	public void beginOverlay(final OverlayLayer layer) {
		// TODO
	}

	/**
	 * Method endOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endOverlay()
	 */
	@Override
	public void endOverlay() {
		// TODO
	}

	@Override
	public boolean mouseInROI(final Point mousePosition) {
		// TODO
		return false;
	}

	@Override
	public boolean cannotDraw() {
		return sceneBuffer.getSceneToUpdate() != null && sceneBuffer.getSceneToUpdate().cannotAdd();
	}

	@Override
	public Integer getGeometryListFor(final GL2 gl, final GamaGeometryFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextRenderer getTextRendererFor(final Font font) {
		// TODO Auto-generated method stub
		return null;
	}

}
