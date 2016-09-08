package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;

import msi.gama.metamodel.shape.IShape.Type;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.opengl.scene.ImageObject;

/*
 * This class is the intermediary class for the transformation from a GeometryObject to a (or some) DrawingElement(s).
 */

class ImageObjectTransformer extends AbstractTransformer {
	
	protected float fontSize;
	protected boolean isBillboarding = false;
	
	private ImageObjectTransformer(StringObjectTransformer obj) {
		loadManyFacedShape(obj);
	}
	
	public ImageObjectTransformer(ImageObject imObj, int[] textureIds, String[] texturePaths, int[][][] bufferedImageValue, boolean isTriangulation) {
		// for StringObject
		genericInit(imObj, isTriangulation);
		
		this.bufferedImageValue = bufferedImageValue;
		this.textureIDs = textureIds;
		this.texturePaths = texturePaths;
		this.isLightInteraction = false;
		this.type = Type.POLYGON;
		
		float width = (float) imObj.getDimensions().x;
		float height = (float) imObj.getDimensions().y;
		float x = 0, y = 0, z = 0; // the translation will be computed later on
		coords = new float[4*3];
		coords[0] = x;
		coords[1] = (y + height);
		coords[2] = z;
		coords[3] = x + width;
		coords[4] = (y + height);
		coords[5] = z;
		coords[6] = x + width;
		coords[7] = y;
		coords[8] = z;
		coords[9] = x;
		coords[10] = y;
		coords[11] = z;
		uvMapping = new float[4*2];
		uvMapping[0] = 0;
		uvMapping[1] = 0;
		uvMapping[2] = 1;
		uvMapping[3] = 0;
		uvMapping[4] = 1;
		uvMapping[5] = 1;
		uvMapping[6] = 0;
		uvMapping[7] = 1;
		// build the faces
		for (int i = 0 ; i < coords.length/(4*3) ; i++) {
			int[] face = new int[4];
			face[0] = i*4;
			face[1] = i*4+1;
			face[2] = i*4+2;
			face[3] = i*4+3;
			faces.add(face);
		}
		
		computeNormals();	
		triangulate();
		applyTransformation();
	}
	
	public ArrayList<DrawingEntity> getDrawingEntityList() {
		ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();
		if (isTriangulation) {
			// if triangulate, returns only one result
			result = getTriangulationDrawingEntity();
		}
		else {
			result = getStandardDrawingEntities();
		}
		return result;
	}
	
}