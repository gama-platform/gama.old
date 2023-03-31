/*******************************************************************************************************
 *
 * SceneHelper.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.helpers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import msi.gama.common.interfaces.ILayer;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.ModelScene;

/**
 * Class SceneHelper. Manages the interactions between the updating and the rendering tasks by keeping hold of three
 * different scenes: (1) A back scene, which is updated by the simulation - A front scene, which is displayed by the
 * renderer; (2) A static scene, which maintains the static layers shared by these two scenes. Makes the appropriate
 * switches between them when appropriate (beginning and end of udpate, mainly)
 *
 * @author drogoul
 * @since 8 avr. 2015
 *
 */
public class SceneHelper extends AbstractRendererHelper {

	static {
		DEBUG.ON();
	}

	/** The back scene. */
	volatile ModelScene backScene;

	/** The static scene. */
	volatile ModelScene staticScene;

	/** The front scene. */
	volatile ModelScene frontScene;

	/** The index. */
	int index;

	/** The garbage. */
	private final Queue<ModelScene> garbage = new ConcurrentLinkedQueue<>();

	/**
	 * Instantiates a new scene helper.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public SceneHelper(final IOpenGLRenderer renderer) {
		super(renderer);
		// DEBUG.OUT("Creating the SceneHelper");
	}

	@Override
	public void initialize() {}

	/**
	 * Begin drawing layer.
	 *
	 * @param layer
	 *            the layer
	 * @param currentLayerAlpha
	 *            the current layer alpha
	 */
	public void beginDrawingLayer(final ILayer layer, final Double currentLayerAlpha) {
		final ModelScene scene = getSceneToUpdate();
		if (scene != null) { scene.beginDrawingLayer(layer, currentLayerAlpha); }
	}

	/**
	 * Begin updating scene.
	 *
	 * @return true, if successful
	 */
	public boolean beginUpdatingScene() {
		// If we are syncrhonized with the simulation and a backScene exists, we
		// wait until it has been updated (put to null at the end of
		// endUpdatingScene)
		// TODO AD Is it still necessary ?
		// while (GAMA.isSynchronized() && backScene != null) {
		// if (!DEBUG.WAIT(20, "Internal synchronisation of ModelScene")) return false;
		// }
		// If we are not synchronized (or if the wait is over), we verify that
		// backScene is null and create a new one
		if (backScene != null) // We should also prevent the draw to happen by skipping everything
			// if it the case ?
			return false;
		backScene = createSceneFrom(staticScene);
		// We prepare it for drawing
		backScene.beginDrawingLayers();
		return true;
	}

	/**
	 * Checks if is not ready to update.
	 *
	 * @return true, if is not ready to update
	 */
	public boolean isNotReadyToUpdate() {
		if (frontScene != null && !frontScene.rendered()) return true;
		return false;
	}

	/**
	 * End updating scene.
	 */
	public void endUpdatingScene() {
		// If there is no scene to update, it means it has been cancelled by
		// another thread (hiding/showing layers, most probably) so we just skip
		// this step
		if (backScene == null) return;
		// We ask the backScene to stop updating
		backScene.endDrawingLayers();
		// We create the static scene from it if it does not exist yet or if it
		// has been discarded
		if (hasStructurallyChanged()) { discardStaticScene(); }
		if (staticScene == null) {
			// DEBUG.LOG("Creating static scene from scene " +
			// backScene.getId());
			staticScene = createSceneFrom(backScene);
		}
		// If there is another frontScene, we discard it (will be disposed of
		// later)
		if (frontScene != null) {
			if (!frontScene.rendered()) {
				garbage.add(backScene);
				backScene = null;
				return;
			}
			garbage.add(frontScene);

		}
		// We switch the scenes
		frontScene = backScene;
		// ... and clear the backScene
		backScene = null;
	}

	/**
	 * Checks for structurally changed.
	 *
	 * @return true, if successful
	 */
	private boolean hasStructurallyChanged() {
		return getSurface().getManager().hasStructurallyChanged();
	}

	/**
	 * This method creates a new scene and copies to it the static layers from a given existing scene. If no existing
	 * scene is passed, a completely new scene is created
	 *
	 * @return a new scene
	 */
	protected ModelScene createSceneFrom(final ModelScene existing) {
		ModelScene newScene;
		if (existing == null) {
			newScene = new ModelScene(getRenderer(), true);
		} else {
			newScene = existing.copyStatic();
		}
		return newScene;
	}

	/**
	 * Returns the scene to be rendered by the renderer. Can be null.
	 *
	 * @return
	 */

	public ModelScene getSceneToRender() { return frontScene; }

	/**
	 * Returns the scene to update. Can be null
	 *
	 * @return
	 */

	public ModelScene getSceneToUpdate() { return backScene; }

	/**
	 * Performs the management and disposal of discarded scenes
	 *
	 * @param gl
	 */
	public void garbageCollect(final OpenGL gl) {
		int size = garbage.size();
		if (size == 0) return;
		final ModelScene[] scenes = garbage.toArray(new ModelScene[size]);
		garbage.clear();
		for (final ModelScene scene : scenes) {
			scene.wipe(gl);
			scene.dispose();
		}
	}

	/**
	 * Disposes the scene buffer by disposing the existing scenes (back, rendered and static).
	 */

	public void dispose() {
		if (frontScene != null) { frontScene.dispose(); }
		frontScene = null;
		if (backScene != null) { backScene.dispose(); }
		backScene = null;
		if (staticScene != null) { staticScene.dispose(); }
		staticScene = null;
	}

	/**
	 * Indication that the layers of the display have been changed in some way, by hiding/showing layers. The static
	 * scene (which may contain some of these layers) and the current back scene are discarded and their layers
	 * invalidated.
	 */
	public void layersChanged() {
		discardStaticScene();
		if (backScene != null) {
			garbage.add(backScene);
			backScene = null;
		}
	}

	/**
	 * Discard static scene.
	 */
	protected void discardStaticScene() {
		if (staticScene != null) {
			garbage.add(staticScene);
			// staticScene.invalidateLayers();
			staticScene = null;
		}
	}

	/**
	 * Draw.
	 */
	public void draw() {
		final OpenGL gl = getOpenGL();
		// Do some garbage collecting in model scenes
		garbageCollect(gl);
		// if picking, we draw a first pass to pick the color
		final PickingHelper picking = getRenderer().getPickingHelper();
		if (picking.isBeginningPicking()) {
			picking.beginPicking();
			getSceneToRender().draw(gl);
			picking.endPicking();
		}
		// we draw the scene on screen
		getSceneToRender().draw(gl);
	}

	/**
	 * Checks if is ready.
	 *
	 * @return true, if is ready
	 */
	public boolean isReady() { return getSceneToRender() != null; }

}
