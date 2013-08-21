package msi.gama.jogl.scene;

import java.awt.Color;

public interface ISceneObject {

	public void draw(ObjectDrawer drawer, boolean picking);

	public abstract void unpick();

	public Color getColor();
}
