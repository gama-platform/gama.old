package ummisco.gama.opengl.vaoGenerator;

import com.jogamp.opengl.util.texture.Texture;

import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.scene.StringObject;

/*
 * This class takes as input a geometry and a drawing attribute and returns a structure
 * readable by OpenGL, composed with vertex array.
 */

public class DrawingEntityGenerator {
	
	private Abstract3DRenderer renderer;
	
	public DrawingEntityGenerator(Abstract3DRenderer renderer) {
		this.renderer = renderer;
	}
	
	public DrawingEntity[] GenerateDrawingEntities(AbstractObject object) {
		DrawingEntity[] result = null;
		Texture[] textures = object.getTextures(renderer.getContext(), renderer);
		ManyFacedShape shape = new ManyFacedShape(object,textures,renderer.data.isTriangulation(), (float) renderer.getGlobalYRatioBetweenPixelsAndModelUnits());	
		result = shape.getDrawingEntities();
		return result;
	}

}
