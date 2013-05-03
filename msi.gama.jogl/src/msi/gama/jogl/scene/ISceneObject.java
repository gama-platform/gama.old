package msi.gama.jogl.scene;

public interface ISceneObject {

	public void draw(ObjectDrawer drawer, boolean picking);

	public abstract void unpick();
}
