/**
 * Created by drogoul, 8 avr. 2015
 * 
 */
package ummisco.gama.opengl.scene;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import ummisco.gama.opengl.JOGLRenderer;
import com.jogamp.opengl.GL2;

/**
 * Class SceneBuffer.
 * 
 * @author drogoul
 * @since 8 avr. 2015
 * 
 */
public class SceneBuffer {

	final JOGLRenderer renderer;
	volatile ModelScene sceneToUpdate;
	private ModelScene staticScene;
	volatile ModelScene sceneToRender;
	private final Queue<ModelScene> garbage = new ConcurrentLinkedQueue<ModelScene>();

	public SceneBuffer(final JOGLRenderer joglRenderer) {
		this.renderer = joglRenderer;
	}

	public void beginUpdatingScene() {
		// If the back scene has already been completed (and not rendered yet), we discard it
		if ( renderer.data.isSynchronized() ) {
			while (sceneToUpdate != null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		} else if ( sceneToUpdate != null ) { return; }
		sceneToUpdate = createSceneFrom(staticScene);
		// We prepare it for drawing
		sceneToUpdate.beginDrawingLayers();
	}

	public void endUpdatingScene() {
		// We create the static scene if it does not exist
		sceneToUpdate.endDrawingLayers();
		if ( staticScene == null ) {
			System.out.println("Creating static scene from scene " + sceneToUpdate.getId());
			staticScene = createSceneFrom(sceneToUpdate);
		}

		if ( sceneToRender != null ) {
			garbage.add(sceneToRender);
		}
		// We switch the scene
		sceneToRender = sceneToUpdate;
		// ... and clear the sceneToUpdate
		sceneToUpdate = null;
	}

	/**
	 * @return
	 */
	private ModelScene createSceneFrom(final ModelScene scene) {
		ModelScene newBackScene;
		if ( scene == null ) {
			newBackScene = new ModelScene(renderer, true);
		} else {
			newBackScene = scene.copyStatic();
		}
		return newBackScene;
	}

	public ModelScene getSceneToRender() {
		return sceneToRender;
	}

	public ModelScene getSceneToUpdate() {
		return sceneToUpdate;
	}

	/**
	 * @param gl
	 */
	public void garbageCollect(final GL2 gl) {
		ModelScene[] scenes = garbage.toArray(new ModelScene[0]);
		garbage.clear();
		for ( ModelScene scene : scenes ) {
			scene.wipe(gl, 0);
			scene.dispose();
		}
	}

	public void dispose() {
		if ( sceneToRender != null ) {
			sceneToRender.dispose();
		}
		sceneToRender = null;
		if ( sceneToUpdate != null ) {
			sceneToUpdate.dispose();
		}
		sceneToUpdate = null;
		if ( staticScene != null ) {
			staticScene.dispose();
		}
		staticScene = null;
	}

}
