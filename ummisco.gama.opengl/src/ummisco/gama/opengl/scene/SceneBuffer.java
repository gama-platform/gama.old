/**
 * Created by drogoul, 8 avr. 2015
 * 
 */
package ummisco.gama.opengl.scene;

import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.scene.ModelScene.WaitingScene;
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
	volatile boolean backSceneFinished;
	volatile boolean frontSceneRenderingCompleted;
	ModelScene backScene;
	ModelScene frontScene;

	public SceneBuffer(final JOGLRenderer joglRenderer) {
		this.renderer = joglRenderer;
	}

	public boolean switchScenes(final GL2 gl) {
		// Sync
		// waitForBackSceneToBeCompleted()
		//
		frontSceneRenderingCompleted = true;
		if ( !backSceneFinished ) { return false; }
		backSceneFinished = false;
		ModelScene backSceneToPutForward = backScene;
		backScene = createBackSceneFrom(frontScene);
		System.out.println("***** SWITCHING SCENES**** Thread " + Thread.currentThread().getName());
		// ModelScene newBackScene = createBackSceneFrom(frontScene);
		renderer.wipeScene(gl);
		frontScene = backSceneToPutForward;
		// backScene = newBackScene;
		return true;

	}

	public void beginDrawingLayers() {
		// Sync
		waitForFrontSceneToBeRendered();
		frontSceneRenderingCompleted = false;
		//
		if ( backSceneFinished ) {
			backSceneFinished = false;
			// backScene.dispose();
			backScene = createBackSceneFrom(backScene);

		}
		getSceneToUpdate().beginDrawingLayers();
	}

	private void waitForFrontSceneToBeRendered() {
		if ( renderer.data.isSynchronized() ) {
			while (!frontSceneRenderingCompleted) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void endDrawingLayers() {
		backSceneFinished = true;
		getSceneToUpdate().endDrawingLayers();
	}

	/**
	 * @return
	 */
	private ModelScene createBackSceneFrom(final ModelScene scene) {
		ModelScene newBackScene;
		if ( scene == null ) {
			newBackScene = new ModelScene(renderer, true);
		} else {
			newBackScene = scene.copyStatic();
		}
		return newBackScene;
	}

	public ModelScene getSceneToDisplay() {
		if ( frontScene == null ) {
			frontScene = new WaitingScene(renderer);
		}
		return frontScene;
	}

	public ModelScene getSceneToUpdate() {
		if ( backScene == null ) {
			backScene = createBackSceneFrom(frontScene);
		}
		return backScene;
	}

}
