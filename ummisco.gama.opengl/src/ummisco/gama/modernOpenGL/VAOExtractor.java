package ummisco.gama.modernOpenGL;

import java.util.HashMap;

public class VAOExtractor {
	

	
	
	
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
			float successor1 = getSuccessor(idxPresentOnce[1], listForFace);
			
			if (predecessor0 == successor1) {
				result[face*12+6] = (int) predecessor0;
				result[face*12+6+1] = idxPresentOnce[0];
				result[face*12+6+2] = idxPresentOnce[1];
				
				result[face*12+6+3] = (int) predecessor1;
				result[face*12+6+4] = idxPresentOnce[1];
				result[face*12+6+5] = idxPresentOnce[0];
			}
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
	

}
