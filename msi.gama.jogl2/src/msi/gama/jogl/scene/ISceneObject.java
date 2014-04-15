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
package msi.gama.jogl.scene;

import java.awt.Color;

public interface ISceneObject {

	public void draw(ObjectDrawer drawer, boolean picking);

	public abstract void unpick();

	public Color getColor();
}
