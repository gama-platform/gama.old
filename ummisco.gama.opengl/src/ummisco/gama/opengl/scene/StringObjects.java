/**
 * Created by drogoul, 23 janv. 2016
 *
 */
package ummisco.gama.opengl.scene;

/**
 * Class StringObjects.
 *
 *
 * A class planned to be implemented in order to improve the rendering of texts.
 * Basically, the idea here is to group strings by textRenderer instances and
 * output them all at once by doing only one begin3Drendering / end3Drendering
 * by renderer.
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
	public StringObjects(final ObjectDrawer<StringObject> drawer) {
		super(drawer);
	}

}
