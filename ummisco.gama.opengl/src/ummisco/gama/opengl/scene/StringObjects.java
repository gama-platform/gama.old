/**
 * Created by drogoul, 23 janv. 2016
 *
 */
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.GL2;

/**
 * Class StringObjects.
 *
 *
 * A class planned to be implemented in order to improve the rendering of texts. Basically, the idea here is to group strings by textRenderer instances and output them all at once by doing only one
 * begin3Drendering / end3Drendering by renderer.
 *
 * @author drogoul
 * @since 23 janv. 2016
 *
 */
public class StringObjects extends SceneObjects<StringObject> {

	/**
	 * @param drawer
	 * @param asList
	 * @param asVBO
	 */
	public StringObjects(final ObjectDrawer<StringObject> drawer, final boolean asList, final boolean asVBO) {
		super(drawer, asList, asVBO);
	}

	/**
	 * Method add()
	 * @see ummisco.gama.opengl.scene.SceneObjects#add(ummisco.gama.opengl.scene.AbstractObject)
	 */
	@Override
	public void add(final StringObject object) {
		super.add(object);
	}

	/**
	 * Method getObjects()
	 * @see ummisco.gama.opengl.scene.SceneObjects#getObjects()
	 */
	@Override
	public Iterable<StringObject> getObjects() {
		return super.getObjects();
	}

	/**
	 * Method draw()
	 * @see ummisco.gama.opengl.scene.SceneObjects#draw(com.jogamp.opengl.GL2, boolean)
	 */
	@Override
	public void draw(final GL2 gl, final boolean picking) {
		super.draw(gl, picking);
	}

}
