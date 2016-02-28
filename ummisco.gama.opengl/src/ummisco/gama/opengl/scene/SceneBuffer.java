/**
 * Created by drogoul, 8 avr. 2015
 *
 */
package ummisco.gama.opengl.scene;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.jogamp.opengl.GL2;
import ummisco.gama.opengl.JOGLRenderer;

/**
 * Class SceneBuffer. Manages the interactions between the updating and the rendering tasks by keeping hold of three different scenes:
 * - A back scene, which is updated by the simulation
 * - A front scene, which is displayed by the renderer
 * - A static scene, which maintains the static layers shared by these two scenes
 *
 * And making the appropriate swtiches between them when appropriate (beginning and end of udpate, mainly)
 *
 * @author drogoul
 * @since 8 avr. 2015
 *
 */
public class SceneBuffer {

	final JOGLRenderer renderer;
	volatile ModelScene backScene;
	volatile ModelScene staticScene;
	volatile ModelScene frontScene;
	private final Queue<ModelScene> garbage = new ConcurrentLinkedQueue<ModelScene>();

	public SceneBuffer(final JOGLRenderer joglRenderer) {
		this.renderer = joglRenderer;
	}

	public void beginUpdatingScene() {
		// If we are syncrhonized with the simulation and a backScene exists, we wait until it has been updated (put to null at the end of endUpdatingScene)
		while (renderer.data.isSynchronized() && backScene != null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// e.printStackTrace();
				return;
			}
		}
		// If we are not synchronized (or if the wait is over), we verify that backScene is null and create a new one
		if ( backScene != null ) { return; }
		backScene = createSceneFrom(staticScene);
		// We prepare it for drawing
		backScene.beginDrawingLayers();
	}

	public void endUpdatingScene() {
		// If there is no scene to update, it means it has been cancelled by another thread (hiding/showing layers, most probably) so we just skip this step
		if ( backScene == null ) { return; }
		// We ask the backScene to stop updating
		backScene.endDrawingLayers();
		// We create the static scene from it if it does not exist yet or if it has been discarded
		if ( staticScene == null ) {
			// System.out.println("Creating static scene from scene " + backScene.getId());
			staticScene = createSceneFrom(backScene);
		}
		// If there is another frontScene, we discard it (will be disposed of later)
		if ( frontScene != null ) {
			garbage.add(frontScene);
		}
		// We switch the scenes
		frontScene = backScene;
		// ... and clear the backScene
		backScene = null;
		// If we are synchronized with the simulation, we wait until this new frontScene has been rendered. 02/2016 Now taken in charge by the view
		// while (renderer.data.isSynchronized() && !frontScene.rendered()) {
		// try {
		// Thread.sleep(20);
		// } catch (InterruptedException e) {
		// // e.printStackTrace();
		// return;
		// }
		// }
	}

	/**
	 * This method creates a new scene and copies to it the static layers from a given existing scene. If no existing scene is passed, a completely new scene is created
	 * @return a new scene
	 */
	private ModelScene createSceneFrom(final ModelScene existing) {
		// FIXME Probably the place to keep existing layers in case of trace
		ModelScene newScene;
		if ( existing == null ) {
			newScene = new ModelScene(renderer, true);
		} else {
			newScene = existing.copyStatic();
		}
		return newScene;
	}

	/**
	 * Returns the scene to be rendered by the renderer. Can be null.
	 * @return
	 */

	public ModelScene getSceneToRender() {
		return frontScene;
	}

	/**
	 * Returns the scene to update. Can be null
	 * @return
	 */

	public ModelScene getSceneToUpdate() {
		return backScene;
	}

	/**
	 * Performs the management and disposal of discarded scenes
	 * @param gl
	 */
	public void garbageCollect(final GL2 gl) {
		ModelScene[] scenes = garbage.toArray(new ModelScene[0]);
		garbage.clear();
		for ( ModelScene scene : scenes ) {
			scene.wipe(gl);
			scene.dispose();
		}
	}

	/**
	 * Disposes the scene buffer by disposing the existing scenes (back, rendered and static).
	 */

	public void dispose() {
		if ( frontScene != null ) {
			frontScene.dispose();
		}
		frontScene = null;
		if ( backScene != null ) {
			backScene.dispose();
		}
		backScene = null;
		if ( staticScene != null ) {
			staticScene.dispose();
		}
		staticScene = null;
	}

	/**
	 * Indication that the layers of the display have been changed in some way, by hiding/showing layers. The static scene (which may contain some of these layers) and the current back scene are
	 * discarded and their layers invalidated.
	 */
	public void layersChanged() {
		// System.out.println("Asking the scene buffer to invalidate layers");
		// FIXME What to do with the textures ?
		if ( staticScene != null ) {
			garbage.add(staticScene);
			staticScene.invalidateLayers();
			staticScene = null;
		}
		if ( backScene != null ) {
			garbage.add(backScene);
			backScene = null;
		}
	}

}
