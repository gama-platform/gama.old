package ummisco.gama.opengl.vaoGenerator;

import com.jogamp.opengl.util.texture.Texture;

import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;

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
		return GenerateDrawingEntities(object, true);
	}
	
	public DrawingEntity[] GenerateDrawingEntities(AbstractObject object, boolean computeTextureIds) {
		// if this function is called to create a simpleScene, we don't compute the texture IDs (the only thing that interest us in this case is the texture Path)
		DrawingEntity[] result = null;
		if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			
			String[] texturePaths = object.getTexturePaths(); // returns null if no texture for this entity
			int[] textureIDs = (texturePaths == null) ? null : new int[texturePaths.length];
			if (computeTextureIds && (texturePaths != null)) {
				Texture[] textures = object.getTextures(renderer.getContext(), renderer);
				for (int i = 0 ; i < textures.length ; i++) {
					textureIDs[i] = textures[i].getTextureObject();
				}
			}
			
			ManyFacedShape shape = new ManyFacedShape(geomObj,textureIDs,texturePaths,renderer.data.isTriangulation());	
			result = shape.getDrawingEntities();
		}	
		return result;
	}

}
