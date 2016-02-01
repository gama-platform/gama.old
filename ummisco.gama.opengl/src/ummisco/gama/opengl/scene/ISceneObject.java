/*********************************************************************************************
 *
 *
 * 'ISceneObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import com.jogamp.opengl.GL2;

public interface ISceneObject {

	public void draw(GL2 gl, ObjectDrawer drawer, boolean picking);

	public abstract void unpick();

	public abstract void pick();

	public Color getColor();
}
