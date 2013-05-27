package msi.gama.jogl.scene;

public interface ISceneObjectSWT {

	public void draw(ObjectDrawerSWT drawer, boolean picking);

	public abstract void unpick();
}
