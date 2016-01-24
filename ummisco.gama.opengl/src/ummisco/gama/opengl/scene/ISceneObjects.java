/**
 * Created by drogoul, 9 mai 2015
 *
 */
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.*;

/**
 * Class ISceneObjects.
 *
 * @author drogoul
 * @since 9 mai 2015
 *
 * @param <T>
 */
public interface ISceneObjects<T extends AbstractObject> {

	public abstract void clear(GL gl, int sizeLimit, boolean fading);

	public abstract void add(T object);

	public abstract Iterable<T> getObjects();

	public abstract void draw(GL2 gl, boolean picking);

	/**
	 * @param gl
	 */
	public abstract void preload(GL2 gl);

}