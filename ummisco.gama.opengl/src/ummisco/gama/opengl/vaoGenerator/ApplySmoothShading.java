package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplySmoothShading {
	
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
		float[] vect1 = GeomMathUtils.CrossProduct(
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
		float[] vect2 = GeomMathUtils.CrossProduct(
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
		float angle = (float) Math.acos(GeomMathUtils.ScalarProduct(GeomMathUtils.Normalize(vect1), GeomMathUtils.Normalize(vect2)));
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
