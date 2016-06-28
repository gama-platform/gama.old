package ummisco.gama.opengl.vaoGenerator;

import java.util.Arrays;
import java.util.HashMap;

import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaPair;
import ummisco.gama.modernOpenGL.Entity;
import ummisco.gama.opengl.ModernRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;

/*
 * This class takes as input a geometry and a drawing attribute and returns a structure
 * readable by OpenGL, composed with vertex array.
 */

// TODO : this class will be computed in a dedicated thread in the future

public class VAOGenerator {
	
	public static final float SMOOTH_SHADING_ANGLE = 60f; // in degree
	
	private ModernRenderer renderer;
	
	public VAOGenerator(ModernRenderer renderer) {
		this.renderer = renderer;
	}
	
	public Entity GenerateVAO(AbstractObject object) {
		
		Entity result = new Entity();
		
		float[] vertices = getObjectVertices(object);
		float[] colors = getObjectColors(object,vertices.length/3);
		float[] indices = getObjectIndexBuffer(object);
		int textId = loadTexture(object);
		if (textId != -1) {
			float[] uvMapping = getObjectUVMaping(object);
			result.setUvMapping(uvMapping);
			result.setTextureID(textId);
		}
		
		// use smooth angle
		float[][] newArraysWithSmoothShading = ApplySmoothShading.setSmoothShading(vertices,colors,indices,SMOOTH_SHADING_ANGLE);
		vertices = newArraysWithSmoothShading[0];
		colors = newArraysWithSmoothShading[1];
		indices = newArraysWithSmoothShading[2];
		
		float[] normals = getObjectNormals(vertices,indices);
		
		result.setVertices(vertices);
		result.setColors(colors);
		result.setIndices(indices);
		result.setNormals(normals);
		
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
	
	public float[] getObjectVertices(AbstractObject object) {
		float[] result = null;
		if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			final IShape.Type type = geomObj.getType();
			GamaPoint position = geomObj.getAttributes().location;
			GamaPair<Double,GamaPoint> rotation = geomObj.getAttributes().rotation;
			GamaPoint size = geomObj.getAttributes().size;
			
			Coordinate[] coordsWithDoublons = geomObj.geometry.getCoordinates();
			// the last coordinate is the same as the first one, no need for this
			Coordinate[] coords = Arrays.copyOf(coordsWithDoublons, coordsWithDoublons.length-1);
			
			if (type != IShape.Type.SPHERE && type != IShape.Type.CONE) 
			{
				// "standard" geometry : a 2D polygon elevated with the depth value.
				if (geomObj.getAttributes().getDepth() == 0)
				{
					// case of 2D polygon
					// convert the coordinate array into float array
					result = new float[coords.length*3];
					for (int i = 0 ; i < coords.length ; i++) {
						result[3*i] = (float) coords[i].x;
						result[3*i+1] = (float) coords[i].y;
						result[3*i+2] = (float) coords[i].z;
					}
				}
				else {
					// case of 3D polygon : a second 2D polygon is built with an elevation of "depth"
					if (type == IShape.Type.PYRAMID) {
						result = new float[(coords.length+1)*3];
						// We build just one summit
						float sumX = 0;
						float sumY = 0;
						float sumZ = 0;
						for (int i = 0 ; i < coords.length ; i++) {
							result[3*i] = (float) coords[i].x;
							sumX += result[3*i];
							result[3*i+1] = (float) coords[i].y;
							sumY += result[3*i+1];
							result[3*i+2] = (float) coords[i].z;
							sumZ += result[3*i+2];
						}
						// we build the summit of the pyramid
						result[coords.length*3] = sumX / coords.length;
						result[coords.length*3+1] = sumY / coords.length;
						result[coords.length*3+2] = sumZ / coords.length + (float) geomObj.getAttributes().getDepth();
					}
					else {
						result = new float[2*coords.length*3];
						// we build the polygon witch will correspond to the button face
						for (int i = 0 ; i < coords.length ; i++) {
							result[3*i] = (float) coords[i].x;
							result[3*i+1] = (float) coords[i].y;
							result[3*i+2] = (float) coords[i].z;
						}
						// we build the polygon witch will correspond to the top face
						for (int i = 0 ; i < coords.length ; i++) {
							result[coords.length*3+3*i] = (float) coords[i].x;
							result[coords.length*3+3*i+1] = (float) coords[i].y;
							result[coords.length*3+3*i+2] = (float) coords[i].z + (float) geomObj.getAttributes().getDepth();
						}
					}
				}
			}
			
			// apply transform to the coords if needed
			// apply rotation (if facet "rotate" for draw is used)
			if (rotation != null) {
				// translate the object to (0,0,0)
				result = GeomMathUtils.setTranslationToVertex(result, (float) -position.x, (float) -position.y, (float) -position.z);
				// apply the rotation
				result = GeomMathUtils.setRotationToVertex(result, (float) Math.toRadians(rotation.key.floatValue()), (float) rotation.value.x, (float) rotation.value.y, (float) rotation.value.z);
				// go back to the first translation
				result = GeomMathUtils.setTranslationToVertex(result, (float) position.x, (float) position.y, (float) position.z);
			}
			// apply scaling (if facet "size" for draw is used)
			if (size != null) {
				// translate the object to (0,0,0)
				result = GeomMathUtils.setTranslationToVertex(result, (float) -position.x, (float) -position.y, (float) -position.z);
				// apply the rotation
				result = GeomMathUtils.setScalingToVertex(result, (float) size.x, (float) size.y, (float) size.z);
				// go back to the first translation
				result = GeomMathUtils.setTranslationToVertex(result, (float) position.x, (float) position.y, (float) position.z);
			}
		}
		return result;
	}
	
	public float[] getObjectColors(AbstractObject object, int verticesNb) {
		float[] result = null;
		if (object instanceof GeometryObject) {

			float[] color = new float[]{ (float)(object.getAttributes().color.red()) /255f,
					(float)(object.getAttributes().color.green()) /255f, 
					(float)(object.getAttributes().color.blue()) /255f,
					(float)(object.getAttributes().color.alpha()) /255f};
			result = new float[verticesNb*4];
			for (int i = 0 ; i < verticesNb ; i++) {
				result[4*i] = (float) color[0];
				result[4*i+1] = (float) color[1];
				result[4*i+2] = (float) color[2];
				result[4*i+3] = (float) color[3];
			}
		}
		return result;
	}
	
	public float[] getObjectIndexBuffer(AbstractObject object) {

		float[] result = null;
		if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			final IShape.Type type = geomObj.getType();
			
			Coordinate[] coordsWithDoublons = geomObj.geometry.getCoordinates();
			// the last coordinate is the same as the first one, no need for this
			Coordinate[] coords = Arrays.copyOf(coordsWithDoublons, coordsWithDoublons.length-1);
			
			switch(type) {
				case POLYGON:
				{
					if (coords.length == 4) {
						// case of rectangle
						result = UsualShapeFactory.getRectangleOrder();
					}
					break;
				}
				case CUBE:
				{
					result = UsualShapeFactory.getCubeOrder();
					break;
				}
				case PYRAMID:
				{
					result = UsualShapeFactory.getPyramidOrder();
					break;
				}
			}
		}
		return result;
	}
	
	public float[] getObjectNormals(final float[] coordinates, final float[] idxBuffer) {
		
		
		
		float[] result = new float[coordinates.length];
		
		int vertexNb = coordinates.length / 3;
		
		for (int i = 0 ; i < vertexNb ; i++) {
			
			float xVal = 0;
			float yVal = 0;
			float zVal = 0;
			float sum = 0;
			// search the triangle where the vertex is
			for (int j = 0 ; j < idxBuffer.length ; j++) {
				if ( (int)idxBuffer[j] == i ) {
					boolean computeNormal = true;
					
					int positionInTriangle = j % 3;
					
					int idxOfPreviousTriangle = (int) ((positionInTriangle == 0) ? idxBuffer[j+2] : idxBuffer[j-1]);
					int idxOfNextTriangle = (int) ((positionInTriangle == 2) ? idxBuffer[j-2] : idxBuffer[j+1]);
					
					// check if the line from the current vertex to the previous/next vertex represents the diagonal of a rectangular face
					// we assume that indices of vertices for rectangular faces are all grouped 6 by 6
					int startIdxOfFace = (j/6)*6; // 7 -> 6, 8 -> 6.
					// we search first the number of occurences of each vertices in the index array (to determine which vertices compose the diagonal)
					HashMap<Integer,Integer> mapOccurencesIdx = new HashMap<Integer,Integer>();
					for (int k = 0 ; k < 6 ; k++) {
						if (mapOccurencesIdx.containsKey((int)idxBuffer[startIdxOfFace+k])) {
							mapOccurencesIdx.put((int) idxBuffer[startIdxOfFace+k], mapOccurencesIdx.get((int)idxBuffer[startIdxOfFace+k])+1);
						}
						else {
							mapOccurencesIdx.put((int) idxBuffer[startIdxOfFace+k], 1);
						}
					}
					
					if (computeNormal) 
					{
						double[] firstVect = new double[] {
								coordinates[idxOfPreviousTriangle*3] - coordinates[(int) ((idxBuffer[j])*3)],
								coordinates[idxOfPreviousTriangle*3+1] - coordinates[(int) ((idxBuffer[j])*3)+1],
								coordinates[idxOfPreviousTriangle*3+2] - coordinates[(int) ((idxBuffer[j])*3)+2],
						};
						double[] secondVect = new double[] {
								coordinates[idxOfNextTriangle*3] - coordinates[(int) ((idxBuffer[j])*3)],
								coordinates[idxOfNextTriangle*3+1] - coordinates[(int) ((idxBuffer[j])*3)+1],
								coordinates[idxOfNextTriangle*3+2] - coordinates[(int) ((idxBuffer[j])*3)+2],
						};
						double[] vectProduct = GeomMathUtils.CrossProduct(firstVect,secondVect);
						
						sum = (float) (vectProduct[0]*vectProduct[0] + vectProduct[1]*	vectProduct[1] + vectProduct[2]*vectProduct[2]);
						xVal += vectProduct[0] / Math.sqrt(sum);
						yVal += vectProduct[1] / Math.sqrt(sum);
						zVal += vectProduct[2] / Math.sqrt(sum);
					}
				}
			}
			sum = xVal*xVal + yVal*yVal + zVal*zVal;
			xVal = (float) (xVal / Math.sqrt(sum));
			yVal = (float) (yVal / Math.sqrt(sum));
			zVal = (float) (zVal / Math.sqrt(sum));
			
			result[3*i] = xVal;
			result[3*i+1] = yVal;
			result[3*i+2] = zVal;
		}
		
		return result;
	}
	
	public float[] getObjectUVMaping(AbstractObject object) {
		float[] result = new float[] {
				0,1,
				0,0,
				1,0,
				1,1
		};
		return result;
	}

}
