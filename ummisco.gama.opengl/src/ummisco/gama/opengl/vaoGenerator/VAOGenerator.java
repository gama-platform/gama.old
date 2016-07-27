package ummisco.gama.opengl.vaoGenerator;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.opengl.ModernRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;

/*
 * This class takes as input a geometry and a drawing attribute and returns a structure
 * readable by OpenGL, composed with vertex array.
 */

// TODO : this class will be computed in a dedicated thread in the future

public class VAOGenerator {
	
	private ModernRenderer renderer;
	
	public VAOGenerator(ModernRenderer renderer) {
		this.renderer = renderer;
	}
	
	public DrawingEntity[] GenerateVAO(AbstractObject object, GL2 gl) {
		DrawingEntity[] result = null;
		if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			int[] textIds = object.getTextureIDs(renderer.getContext(), renderer);
			ManyFacedShape shape = new ManyFacedShape(geomObj,textIds,renderer.data.isTriangulation());	
			result = shape.getDrawingEntities();
		}	
		return result;
	}
	
	public int loadTexture(AbstractObject object) {
		Texture texture = object.getTexture(renderer.getContext(), renderer, 0);
		if (texture == null) {
			return -1;
		}
		else {
			int textureID = texture.getTextureObject();
			return textureID;
		}
	}

}
