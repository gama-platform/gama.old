package ummisco.gama.modernOpenGL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaPair;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;

public class VAOExtractor {
	
	public static float[] getObjectVertices(AbstractObject object) {
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
			
			// apply transform to the coords if needed
			// apply rotation (if facet "rotate" for draw is used)
			if (rotation != null) {
				// translate the object to (0,0,0)
				result = Maths.setTranslationToVertex(result, (float) -position.x, (float) -position.y, (float) -position.z);
				// apply the rotation
				result = Maths.setRotationToVertex(result, (float) Math.toRadians(rotation.key.floatValue()), (float) rotation.value.x, (float) rotation.value.y, (float) rotation.value.z);
				// go back to the first translation
				result = Maths.setTranslationToVertex(result, (float) position.x, (float) position.y, (float) position.z);
			}
			// apply scaling (if facet "size" for draw is used)
			if (size != null) {
				// translate the object to (0,0,0)
				result = Maths.setTranslationToVertex(result, (float) -position.x, (float) -position.y, (float) -position.z);
				// apply the rotation
				result = Maths.setScalingToVertex(result, (float) size.x, (float) size.y, (float) size.z);
				// go back to the first translation
				result = Maths.setTranslationToVertex(result, (float) position.x, (float) position.y, (float) position.z);
			}
		}
		return result;
	}
	
	public static float[] getObjectColors(AbstractObject object, int verticesNb) {
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
	
	public static float[] getObjectIndexBuffer(AbstractObject object) {

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
						result = getRectangleOrder();
					}
					break;
				}
				case CUBE:
				{
					result = getCubeOrder();
					break;
				}
			}
			
			
//			else {
//				int idx = 0;
//				for (int i = 0 ; i < coords.length-2 ; i++) {
//					for (int j = 0 ; j < coords.length-1 ; j++) {
//						for (int k = 0 ; k < coords.length ; k++) {
//							if (i != j && i != k && j != k) {
//								idx+=3;
//							}
//						}
//					}
//				}
//				result = new float[idx];
//				idx = 0;
//				for (int i = 0 ; i < coords.length-2 ; i++) {
//					for (int j = 0 ; j < coords.length-1 ; j++) {
//						for (int k = 0 ; k < coords.length ; k++) {
//							if (i != j && i != k && j != k) {
//								result[idx] = i;
//								idx++;
//								result[idx] = j;
//								idx++;
//								result[idx] = k;
//								idx++;
//							}
//						}
//					}
//				}
//			}
		}
		return result;
	}
	
	public static float[] getRectangleOrder() {
		//	   1-----2
		//      \     \
		//       0-----3
		return new float[]{0,2,1,0,3,2};
	}
	
	public static float[] getCubeOrder() {
		//     5-----6
		//     |\    |\
		//     | 4-----7
		//     1-|---2 |
		//      \|    \|
		//       0-----3
		return new float[]{
				0,1,2,0,2,3, 	// buttom face
				0,3,7,0,7,4,
				1,0,4,1,4,5,
				2,1,5,2,5,6,
				3,2,6,3,6,7,
				4,7,6,4,6,5};	// top face
	}
	
	public static float[] getExtendedIndicesForRectangularFaces(float[] shortIndices) {
		// this function will return the list of indices of a 3D polygon with rectangular faces with the second diagonal (for normal computation)
		//    v------v                      v------v
		//    |\     |                      |\    /|
		//    |  \   |    changed into :    |  \/  |
		//    |   \  |                      |  /\  |
		//    |     \|                      |/    \|
		//	  v------v                      v------v
		float[] result = new float[shortIndices.length*2];
		for (int face = 0 ; face < shortIndices.length/6 ; face++) {
			HashMap<Integer,Integer> mapOccurencesIdx = new HashMap<Integer,Integer>();
			// write the face to the result array
			for (int i = 0 ; i < 6 ; i++) {
				result[face*12+i] = shortIndices[face*6+i];
				if (mapOccurencesIdx.containsKey((int) shortIndices[face*6+i])) {
					mapOccurencesIdx.put((int) shortIndices[face*6+i], mapOccurencesIdx.get((int) shortIndices[face*6+i])+1);
				}
				else {
					mapOccurencesIdx.put((int) shortIndices[face*6+i], 1);
				}
			}
			// build the 2 others triangles
			// we search the 2 vertices used only once
			int[] idxPresentOnce = new int[2];
			int count = 0;
			for (Integer idx : mapOccurencesIdx.keySet()) {
				int occurence = mapOccurencesIdx.get(idx);
				if (occurence == 1) {
					idxPresentOnce[count] = idx;
					count++;
				}
			}
			// find the predecessor to idxPresentOnce[0] and to idxPresentOnce[1]
			float[] listForFace = new float[] {
					result[face*12],result[face*12+1],result[face*12+2],	// triangle 1
					result[face*12+3],result[face*12+4],result[face*12+5]}; // triangle 2
			float predecessor0 = getPredecessor(idxPresentOnce[0], listForFace);
			float predecessor1 = getPredecessor(idxPresentOnce[1], listForFace);
			float successor0 = getSuccessor(idxPresentOnce[0], listForFace);
			float successor1 = getSuccessor(idxPresentOnce[1], listForFace);
			
			if (predecessor0 == successor1) {
				result[face*12+6] = (int) predecessor0;
				result[face*12+6+1] = idxPresentOnce[0];
				result[face*12+6+2] = idxPresentOnce[1];
				
				result[face*12+6+3] = (int) predecessor1;
				result[face*12+6+4] = idxPresentOnce[1];
				result[face*12+6+5] = idxPresentOnce[0];
			}
			
			// fill in the second triangle
//			result[face*12] = 
		}
		return result;
	}
	
	public static int getPredecessor(float idx, float[] array) {
		int result=0;
		for (int i = 0 ; i < array.length ; i++) {
			if (i == array.length-1) {
				if (idx == array[0]) {
					result = (int) array[i];
					break;
				}
			}
			else {
				if (idx == array[i+1]) {
					result = (int) array[i];
					break;
				}
			}
		}
		return result;
	}
	
	public static int getSuccessor(float idx, float[] array) {
		int result=0;
		for (int i = 0 ; i < array.length ; i++) {
			if (i == 0) {
				if (idx == array[array.length-1]) {
					result = (int) array[i];
					break;
				}
			}
			else {
				if (idx == array[i-1]) {
					result = (int) array[i];
					break;
				}
			}
		}
		return result;
	}
	
	public static float[][] setSmoothShading(float[] posVertices, float[] colorVertices, float[] idxVertices, float smoothAngle) {
		// this function will return the position vertex and the index vertex buffer with a duplication of
		// the vertices witch belongs to two faces with an angle bigger than the smoothAngle.
		float[][] result = new float[3][]; // result[0] for posVertices, result[1] for colorVertices, result[2] for idxVertices
		
		// a default value
		smoothAngle = (float) Math.toRadians(60);
		
		int lastIdx = posVertices.length/3;
		
		ArrayList<float[]> treatedTriangles = new ArrayList<float[]>();
		
		for (int triangleIdx = 0 ; triangleIdx < idxVertices.length/3 ; triangleIdx++) {
			// in this map, the old idx :: new idx will be stored.
			HashMap<Float,Float> mapNewIdx = new HashMap<Float,Float>();
			
			float[] currentTriangle = new float[]{idxVertices[triangleIdx*3],idxVertices[triangleIdx*3+1],idxVertices[triangleIdx*3+2]};
			ArrayList<float[]> connexeTriangles = getConnexeTriangles(currentTriangle,idxVertices);
			for (float[] connexeTriangle : connexeTriangles) {
					
				if (splitFaces(connexeTriangle,currentTriangle,posVertices,smoothAngle)) {
					
					// we double the vertices
					float[] mutualVertices = getMutualVertices(currentTriangle,connexeTriangle);
					// we check if those vertices really have to be doubled (or if they have been already)
					float[] arrayOfVerticesToIgnore = new float[0];

					for (float[] tr : treatedTriangles) {
						if (isCoplanar(tr,connexeTriangle,posVertices)) {
							float[] newElements = getMutualVertices(tr,connexeTriangle);
							arrayOfVerticesToIgnore = concatFloatLists(arrayOfVerticesToIgnore,newElements);
						}
						if (isCoplanar(tr,currentTriangle,posVertices)) {
							float[] newElements = getMutualVertices(tr,currentTriangle);
							arrayOfVerticesToIgnore = concatFloatLists(arrayOfVerticesToIgnore,newElements);
						}
					}
					// we change the "mutualVertices" removing the idx presents in the "arrayOfVerticesToIgnore" array
					ArrayList<Float> lastList = new ArrayList<Float>();
					for (int i = 0 ; i < mutualVertices.length ; i++ ) {
						boolean addToList = true;
						for (int j = 0 ; j < arrayOfVerticesToIgnore.length ; j++) {
							if (arrayOfVerticesToIgnore[j] == mutualVertices[i]) {
								addToList = false;
							}
						}
						if (addToList) {
							lastList.add(new Float(mutualVertices[i]));
						}
					}
					mutualVertices = new float[lastList.size()];
					for (int i = 0 ; i < lastList.size() ; i++) {
						mutualVertices[i] = lastList.get(i);
					}
					
					int positionInIdxBuffer = getTrianglePosition(connexeTriangle,idxVertices);
					if (positionInIdxBuffer == -1) {
						System.err.println("ERROR : impossible to get the position of the triangle composed with "
								+ "the idx " + connexeTriangle[0] + ", " + connexeTriangle[1] + ", " + connexeTriangle[2]);
					}
					else {
						for (int i = 0 ; i < mutualVertices.length ; i++) {
							// fill the map (if the idx are new one)
							if (!mapNewIdx.containsKey(new Float(mutualVertices[i]))) {
								mapNewIdx.put(new Float(mutualVertices[i]), new Float(lastIdx));
								lastIdx++;
								
								// for those two points, we copy their coordinates to the end of the posVertices
								float[] tmp = new float[posVertices.length+3];
								System.arraycopy(posVertices, 0, tmp, 0, posVertices.length);
								tmp[posVertices.length] = posVertices[(int) (mutualVertices[i]*3)]; 			// x component
								tmp[posVertices.length+1] = posVertices[(int) (mutualVertices[i]*3)+1]; 		// y component
								tmp[posVertices.length+2] = posVertices[(int) (mutualVertices[i]*3)+2]; 		// z component
								posVertices = tmp;
								
								tmp = new float[colorVertices.length+4];
								System.arraycopy(colorVertices, 0, tmp, 0, colorVertices.length);
								tmp[colorVertices.length] = colorVertices[(int) (mutualVertices[i]*4)]; 		// r component
								tmp[colorVertices.length+1] = colorVertices[(int) (mutualVertices[i]*4)+1]; 	// g component
								tmp[colorVertices.length+2] = colorVertices[(int) (mutualVertices[i]*4)+2]; 	// b component
								tmp[colorVertices.length+3] = colorVertices[(int) (mutualVertices[i]*4)+3]; 	// a component
								colorVertices = tmp;
							}
							
							// change the idx in the idxVertices array with the new one
							int positionInTriangle = ((int)idxVertices[positionInIdxBuffer] == mutualVertices[i]) ?
									0 : ((int)idxVertices[positionInIdxBuffer+1] == mutualVertices[i]) ?
											1 : 2;
							int newIdx = mapNewIdx.get(new Float(mutualVertices[i])).intValue();
							idxVertices[positionInIdxBuffer+positionInTriangle] = newIdx;
						}
					}
				}	
			}
			treatedTriangles.add(currentTriangle);
		}
		
		// set the arrays to the result
		result[0] = posVertices;
		result[1] = colorVertices;
		result[2] = idxVertices;
		
		return result;
	}
	
	public static ArrayList<float[]> getConnexeTriangles(float[] triangle, float[] idxVertices) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		for (int triangleIdx = 0 ; triangleIdx < idxVertices.length/3 ; triangleIdx++) {
			int count = 0;
			for (int vIdx = triangleIdx*3 ; vIdx < triangleIdx*3+3 ; vIdx++) {
				if ( (idxVertices[vIdx] == triangle[0])
						|| (idxVertices[vIdx] == triangle[1])
						|| (idxVertices[vIdx] == triangle[2]) ) {
					count++;
				}
			}
			if (count < 3 && count > 0) {
				// 1 or 2 vertices are in common with the current triangle --> the triangle is a connexe one
				result.add(new float[]{idxVertices[triangleIdx*3],idxVertices[triangleIdx*3+1],idxVertices[triangleIdx*3+2]});
			}
		}
		return result;
	}
	
	public static boolean isCoplanar(float[] firstTriangle, float[] secondTriangle, float[] coordinates) {
		// check if there is a point in common
		boolean pointInCommon = false;
		for (int i = 0 ; i < 3 ; i++) {
			for (int j = 0 ; j < 3 ; j++) {
				if ( (coordinates[(int) (firstTriangle[i]*3)] == coordinates[(int) (secondTriangle[j]*3)])
						&& (coordinates[(int) (firstTriangle[i]*3+1)] == coordinates[(int) (secondTriangle[j]*3+1)])
						&& (coordinates[(int) (firstTriangle[i]*3+2)] == coordinates[(int) (secondTriangle[j]*3+2)])) {
					pointInCommon = true;
				}
			}
		}
		if (pointInCommon) {
			if (!splitFaces(firstTriangle, secondTriangle, coordinates, 0.017f)) {
				// if the two triangles are forming an angle smaller than 1 degree (around 0.017 rad), they are coplanar
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<float[]> getAllNonCoplanarTriangles(ArrayList<float[]> triangleList, float[] coordinates) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		for (int i = 0 ; i < triangleList.size() ; i++) {
			for (int j = i+1 ; j < triangleList.size() ; j++) {
				
				if (isCoplanar(triangleList.get(i), triangleList.get(j), coordinates)) {
					break;
				}
				if (j == triangleList.size()-1) {
					// if all the remaining triangles have been checked, we add this triangle in the result list
					result.add(triangleList.get(i));
				}
			}
		}
		result.add(triangleList.get(triangleList.size()-1));
		return result;
	}
	
	public static boolean splitFaces(float[] firstTriangle, float[] secondTriangle, float[] coordinates, float smoothAngle) {
		float[] vect1 = Maths.CrossProduct(
				new float[] {
						coordinates[(int) firstTriangle[2]*3]-coordinates[(int) firstTriangle[0]*3],
						coordinates[(int) (firstTriangle[2])*3+1]-coordinates[(int) (firstTriangle[0])*3+1],
						coordinates[(int) (firstTriangle[2])*3+2]-coordinates[(int) (firstTriangle[0])*3+2]
				}
				,
				new float[] {
						coordinates[(int) firstTriangle[1]*3]-coordinates[(int) firstTriangle[0]*3],
						coordinates[(int) (firstTriangle[1])*3+1]-coordinates[(int) (firstTriangle[0])*3+1],
						coordinates[(int) (firstTriangle[1])*3+2]-coordinates[(int) (firstTriangle[0])*3+2]
				}
			);
		float[] vect2 = Maths.CrossProduct(
				new float[] {
						coordinates[(int) secondTriangle[2]*3]-coordinates[(int) secondTriangle[0]*3],
						coordinates[(int) (secondTriangle[2])*3+1]-coordinates[(int) (secondTriangle[0])*3+1],
						coordinates[(int) (secondTriangle[2])*3+2]-coordinates[(int) (secondTriangle[0])*3+2]
				}
				,
				new float[] {
						coordinates[(int) secondTriangle[1]*3]-coordinates[(int) secondTriangle[0]*3],
						coordinates[(int) (secondTriangle[1])*3+1]-coordinates[(int) (secondTriangle[0])*3+1],
						coordinates[(int) (secondTriangle[1])*3+2]-coordinates[(int) (secondTriangle[0])*3+2]
				}
			);
		// determine the angle between the two vectors
		float angle = (float) Math.acos(Maths.ScalarProduct(Maths.Normalize(vect1), Maths.Normalize(vect2)));
		// if the angle between the two vectors is greater than "smoothAngle", return true.
		return (angle > smoothAngle);
	}
	
	private static float[] getMutualVertices(float[] triangle1, float[] triangle2) {
		float[] result = null;
		
		ArrayList<Integer> verticeList = new ArrayList<Integer>(); // one or two vertices are shared between two connexe triangles
		
		HashMap<Integer,Integer> mapOccurencesIdx = new HashMap<Integer,Integer>();
		for (int i = 0 ; i < 3 ; i++) {
			if (mapOccurencesIdx.containsKey((int) triangle1[i])) {
				mapOccurencesIdx.put((int) triangle1[i], mapOccurencesIdx.get((int) triangle1[i])+1);
			}
			else {
				mapOccurencesIdx.put((int) triangle1[i], 1);
			}
		}
		for (int i = 0 ; i < 3 ; i++) {
			if (mapOccurencesIdx.containsKey((int) triangle2[i])) {
				mapOccurencesIdx.put((int) triangle2[i], mapOccurencesIdx.get((int) triangle2[i])+1);
			}
			else {
				mapOccurencesIdx.put((int) triangle2[i], 1);
			}
		}
		// we search the vertices used twice
		for (Integer idx : mapOccurencesIdx.keySet()) {
			int occurence = mapOccurencesIdx.get(idx);
			if (occurence == 2) {
				verticeList.add(idx);
			}
		}
		// we store it to the return variable "result"
		result = new float[verticeList.size()];
		for (int i = 0 ; i < verticeList.size() ; i++) {
			result[i] = verticeList.get(i);
		}
		
		return result;
	}
	
	private static int getTrianglePosition(float[] triangle,float[] idxVertices) {
		for (int i = 0 ; i < idxVertices.length/3 ; i++) {
			if ((int)triangle[0] == (int)idxVertices[i*3]
					&& (int)triangle[1] == (int)idxVertices[i*3+1]
					&& (int)triangle[2] == (int)idxVertices[i*3+2])
			{
				return i*3;
			}
		}
		return -1;
	}
	
	private static float[] concatFloatLists(float[] l1, float[] l2) {
		float[] result = new float[l1.length+l2.length];
		for (int i = 0 ; i < l1.length ; i++) {
			result[i] = l1[i];
		}
		for (int i = 0 ; i < l2.length ; i++) {
			result[i+l1.length] = l2[i];
		}
		return result;
	}
}
