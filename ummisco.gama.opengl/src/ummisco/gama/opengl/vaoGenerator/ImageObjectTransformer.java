package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;

import msi.gama.metamodel.shape.GamaPoint;
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
		
		// compute the size
		float width = (float) imObj.getDimensions().x;
		float height = (float) imObj.getDimensions().y;
		if (this.size == null)
			this.size = new GamaPoint(width,height,1);
		else
			this.size = new GamaPoint(width*size.x,height*size.y,size.z);
		// compute the translation
		if (this.translation == null) {
			this.translation = new GamaPoint(width/2,height/2,0);
		}
		else
			this.translation = new GamaPoint(translation.x+width/2,translation.y+height/2,translation.z);
		// create a generic square
		coords = new float[4*3];		
		coords[0] = -0.5f;
		coords[1] = 0.5f;
		coords[2] = 0;
		coords[3] = 0.5f;
		coords[4] = 0.5f;
		coords[5] = 0;
		coords[6] = 0.5f;
		coords[7] = -0.5f;
		coords[8] = 0;
		coords[9] = -0.5f;
		coords[10] = -0.5f;
		coords[11] = 0;
		uvMapping = new float[4*2];
		if (bufferedImageValue == null) {
			uvMapping[0] = 0;
			uvMapping[1] = 0;
			uvMapping[2] = 1;
			uvMapping[3] = 0;
			uvMapping[4] = 1;
			uvMapping[5] = 1;
			uvMapping[6] = 0;
			uvMapping[7] = 1;
		}
		else {
			uvMapping[0] = 0;
			uvMapping[1] = 1;
			uvMapping[2] = 1;
			uvMapping[3] = 1;
			uvMapping[4] = 1;
			uvMapping[5] = 0;
			uvMapping[6] = 0;
			uvMapping[7] = 0;
		}
		// build the faces
		for (int i = 0 ; i < coords.length/(4*3) ; i++) {
			int[] face = new int[4];
			face[0] = i*4;
			face[1] = i*4+1;
			face[2] = i*4+2;
			face[3] = i*4+3;
			faces.add(face);
		}
		
		initBorders();
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