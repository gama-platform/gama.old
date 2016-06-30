package ummisco.gama.opengl.vaoGenerator;

import java.util.Arrays;

import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.metamodel.shape.IShape;
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
	
	public DrawingEntity[] GenerateVAO(AbstractObject object) {
		
		GeometryObject geomObj = (GeometryObject)object;
		int textId = loadTexture(object);
		ManyFacedShape shape = new ManyFacedShape(geomObj,textId);	
		
		return shape.getDrawingEntities();
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
	
	public float[] getObjectUVMaping(AbstractObject object) {
		float[] result = null;
		
		GeometryObject geomObj = (GeometryObject)object;
		final IShape.Type type = geomObj.getType();
		
		Coordinate[] coordsWithDoublons = geomObj.geometry.getCoordinates();
		// the last coordinate is the same as the first one, no need for this
		Coordinate[] coords = Arrays.copyOf(coordsWithDoublons, coordsWithDoublons.length-1);
		
		//result = UVMappingGenerator.getObjectUVMaping(type, coords);
		
		return result;
	}

}
