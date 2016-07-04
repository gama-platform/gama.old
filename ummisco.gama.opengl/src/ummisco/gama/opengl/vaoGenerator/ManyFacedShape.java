package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaPair;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.modernOpenGL.Material;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.utils.Utils;

/*
 * This class is the intermediary class for the transformation from a GeometryObject to a (or some) DrawingElement(s).
 */

public class ManyFacedShape {
	
	public static float SMOOTH_SHADING_ANGLE = 60f; // in degree
	public static GamaColor TRIANGULATION_COLOR = new GamaColor(1.0,1.0,0.0,1.0);
	
	private boolean isTriangulation;
	private ArrayList<int[]> faces; // way to construct a face from the indices of the coordinates (anti clockwise for front face)
	private ArrayList<int[]> edgeToSmooth; // list that store all the edges erased thanks to the smooth shading (those edges must
	// not be displayed when displaying the borders !)
	private float[] coords;
	private float[] uvMapping;
	private float[] normals;
	private int textId = -1; // "-1" for "no texture"
	private float[] coordsForBorder;
	private float[] idxForBorder;
	
	private HashMap<Integer,Integer> mapOfOriginalIdx = new HashMap<Integer,Integer>(); 
	
	private int[] topFace;
	private int[] bottomFace;
	
	// private fields from the GeometryObject
	private IShape.Type type;
	private final double depth;
	private GamaPoint translation;
	private GamaPair<Double,GamaPoint> rotation;
	private GamaPoint size;
	private GamaColor color;
	private GamaColor borderColor;
	private Coordinate[] coordinates;
	
	public ManyFacedShape(GeometryObject geomObj, int textId, boolean isTriangulation) {
		this.faces = new ArrayList<int[]>();
		this.coords = new float[0];
		this.type = geomObj.getType();
		this.depth = geomObj.getAttributes().getDepth();
		this.translation = geomObj.getAttributes().location;
		this.rotation = geomObj.getAttributes().rotation;
		this.size = geomObj.getAttributes().size;
		this.color = geomObj.getAttributes().color;
		this.borderColor = geomObj.getAttributes().getBorder();
		this.textId = textId;
		this.isTriangulation = isTriangulation;
		
		Coordinate[] coordsWithDoublons = geomObj.geometry.getCoordinates();
		// the last coordinate is the same as the first one, no need for this
		this.coordinates = Arrays.copyOf(coordsWithDoublons, coordsWithDoublons.length-1);
		
		if (isStandardGeometry())
		{
			if (depth > 0) {
				// 3D shape
				buildBottomFace();
				buildTopFace();
				buildLateralFaces();
			}
			else {
				// 2D shape
				buildTopFace();
			}
		}
		else if (isPyramid())
		{
			buildBottomFace();
			buildPyramidSummit();
			buildLateralFaces();
		}
		
		initBorders();
		
		applySmoothShading();
		applyTransformation();
		computeNormals();
		if (textId != -1)
			computeUVMapping();
		triangulate();
		
		correctBorders();
	}
	
	private boolean isStandardGeometry() {
		// a standard geometry is a geometry which can be build with
		// a bottom face and a top face, linked with some lateral faces.
		// In case the standard geometry is a 2D shape, we only build
		// the top face.
		if (type != IShape.Type.SPHERE
				&& type != IShape.Type.CONE
				&& type != IShape.Type.PYRAMID) {
			return true;
		}
		else if (type == IShape.Type.CONE) {
			// cone 2D is a standard geometry
			if (depth == 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPyramid() {
		// a pyramid geometry is either a 3D cone or a pyramid (made with a base, a summit and
		// generated lateral faces)
		if ( (type == IShape.Type.CONE && depth > 0)
				|| type == IShape.Type.PYRAMID) {
			return true;
		}
		return false;
	}
	
	private int getOriginalIdx(int idx) {
		// this function is used to get the original idx (from the idx buffer) before the smooth shading
		return mapOfOriginalIdx.get(idx);
	}
	
	private void initBorders() {
		idxForBorder = getIdxBufferForLines();
		coordsForBorder = coords;
		// init the mapOfOriginalIdx
		for (float i : idxForBorder) {
			mapOfOriginalIdx.put((int)i, (int)i);
		}
	}
	
	private void correctBorders() {
		// delete all the edges that are present in the list edgeToSmooth
		// TODO
	}
	
	private void computeUVMapping() {
		int sizeArray = 0;
		for (int i = 0 ; i < faces.size() ; i++) {
			sizeArray += faces.get(i).length;
		}
		uvMapping = new float[sizeArray*2];
		for (int i = 0 ; i < faces.size() ; i++) {
			int[] face = faces.get(i);
			if (face.length == 4) {
				// case of squared faces :
				// vertex 1 :
				uvMapping[face[0]*2] = 0;
				uvMapping[face[0]*2+1] = 0;
				// vertex 2 :
				uvMapping[face[1]*2] = 0;
				uvMapping[face[1]*2+1] = 1;
				// vertex 3 :
				uvMapping[face[2]*2] = 1;
				uvMapping[face[2]*2+1] = 1;
				// vertex 4 :
				uvMapping[face[3]*2] = 1;
				uvMapping[face[3]*2+1] = 0;
			}
			else if (face.length == 3) {
				// case of triangular faces :
				// vertex 1 (summit) :
				uvMapping[face[0]*2] = 0.5f;
				uvMapping[face[0]*2+1] = 1;
				// vertex 2 :
				uvMapping[face[1]*2] = 0;
				uvMapping[face[1]*2+1] = 0;
				// vertex 3 :
				uvMapping[face[2]*2] = 1;
				uvMapping[face[2]*2+1] = 0;
			}
		}
	}
	
	private void buildBottomFace() {
		float[] result = new float[coordinates.length*3];
		int[] newFace = new int[coordinates.length];
		int idx = 0;
		for (int i = coordinates.length-1 ; i >= 0 ; i--) { // need to be clockwise
			result[3*i] = (float) coordinates[i].x;
			result[3*i+1] = (float) coordinates[i].y;
			result[3*i+2] = (float) coordinates[i].z;
			newFace[idx] = this.coords.length + i;
			idx++;
		}
		
		this.faces.add(newFace);
		bottomFace = newFace;
		this.coords = Utils.concatFloatArrays(this.coords,result);
	}
	
	private void buildTopFace() {
		float[] result = new float[coordinates.length*3];
		int[] newFace = new int[coordinates.length];
		int idx = 0;
		for (int i = 0 ; i < coordinates.length ; i++) { // need to be anti-clockwise
			result[3*i] = (float) coordinates[i].x;
			result[3*i+1] = (float) coordinates[i].y;
			result[3*i+2] = (float) coordinates[i].z + (float) depth;
			newFace[idx] = this.coords.length/3 + i;
			idx++;
		}
		
		this.faces.add(newFace);
		topFace = newFace;
		this.coords = Utils.concatFloatArrays(this.coords,result);
	}
	
	private void buildPyramidSummit() {
		float[] center = new float[2];
		float[] coordSum = new float[2];
		// 1) compute the center of the base
		// sum the coordinates
		for (int i = 0 ; i < coordinates.length ; i++) {
			coordSum[0] += (float) coordinates[i].x;
			coordSum[1] += (float) coordinates[i].y;
		}
		// divide by the number of vertices to get the center
		center[0] = coordSum[0] / coordinates.length;
		center[1] = coordSum[1] / coordinates.length;
		
		// 2) determine the coordinate of the summit
		float[] summitCoordinates = new float[] {
				center[0],
				center[1],
				(float) depth
		};
		
		// 3) add this summit to the "coords", set the topFace.
		int[] vtxIdxForSummit = new int[]{coordinates.length};
		topFace = vtxIdxForSummit;
		this.coords = Utils.concatFloatArrays(this.coords,summitCoordinates);
	}
	
	private void buildLateralFaces() {
		if (topFace.length == 1) {
			// case of pyramid : the topFace is just the summit
			for (int i = 0 ; i < bottomFace.length ; i++) {
				int[] newFace = new int[3];
				newFace[0] = topFace[0];
				newFace[1] = bottomFace[bottomFace.length-i-1];
				if (i < bottomFace.length - 1)
					newFace[2] = bottomFace[bottomFace.length-i-2];
				else
					newFace[2] = bottomFace[bottomFace.length-1];
				faces.add(newFace);
			}
		}
		else {
			for (int i = 0 ; i < topFace.length ; i++) {
				int[] newFace = new int[4];
				newFace[2] = topFace[i];
				newFace[3] = bottomFace[bottomFace.length-i-1];
				if (i < topFace.length - 1)
					newFace[0] = bottomFace[bottomFace.length-i-2];
				else
					newFace[0] = bottomFace[bottomFace.length-1];
				if (i < topFace.length - 1)
					newFace[1] = topFace[i+1];
				else
					newFace[1] = topFace[0];
				faces.add(newFace);
			}
		}
	}
	
	private void applySmoothShading() {
		for (int faceIdx = 0 ; faceIdx < faces.size() ; faceIdx++) {
			int[] idxConnexeFaces = getConnexeFaces(faceIdx);
			for (int idxConnexeFace = 0 ; idxConnexeFace < idxConnexeFaces.length ; idxConnexeFace++) {
				if (getAngleBetweenFaces(faces.get(idxConnexeFaces[idxConnexeFace]),faces.get(faceIdx)) > SMOOTH_SHADING_ANGLE) {
					splitFaces(faceIdx,idxConnexeFaces[idxConnexeFace]);
				}
				else {
					saveEdgeToSmooth(idxConnexeFaces[idxConnexeFace],faceIdx);
				}
			}
		}
	}
	
	private void saveEdgeToSmooth(int face1Idx, int face2Idx) {
		int[] idxArray = getMutualVertexIdx(face1Idx, face2Idx);
		// TODO
	}
	
	private void triangulate() {
		for (int i = 0 ; i < faces.size() ; i++) {
			int[] faceTriangulated = triangulateFace(faces.get(i));
			faces.remove(i);
			faces.add(i,faceTriangulated);
		}
	}
	
	private int[] triangulateFace(int[] face) {
		int[] result = new int[(face.length-2)*3];
		int idx = 0;
		for (int i = 1 ; i < face.length-1 ; i++) {
			result[idx++] = face[0];
			result[idx++] = face[i];
			result[idx++] = face[i+1];
		}
		return result;
	}
	
	private void applyTransformation() {
		// apply transform to the coords if needed
		// apply rotation (if facet "rotate" for draw is used)
		if (rotation != null) {
			// translate the object to (0,0,0)
			coords = GeomMathUtils.setTranslationToVertex(coords, (float) -translation.x, (float) -translation.y, (float) -translation.z);
			// apply the rotation
			coords = GeomMathUtils.setRotationToVertex(coords, (float) Math.toRadians(rotation.key.floatValue()), (float) rotation.value.x, (float) rotation.value.y, (float) rotation.value.z);
			// go back to the first translation
			coords = GeomMathUtils.setTranslationToVertex(coords, (float) translation.x, (float) translation.y, (float) translation.z);
		}
		// apply scaling (if facet "size" for draw is used)
		if (size != null) {
			// translate the object to (0,0,0)
			coords = GeomMathUtils.setTranslationToVertex(coords, (float) -translation.x, (float) -translation.y, (float) -translation.z);
			// apply the rotation
			coords = GeomMathUtils.setScalingToVertex(coords, (float) size.x, (float) size.y, (float) size.z);
			// go back to the first translation
			coords = GeomMathUtils.setTranslationToVertex(coords, (float) translation.x, (float) translation.y, (float) translation.z);
		}
	}
	
	public float[] getIdxBuffer() {
		
		int sizeOfBuffer = 0;
		for (int[] face : faces) {
			sizeOfBuffer += face.length;
		}
		float[] result = new float[sizeOfBuffer];
		int cpt = 0;
		for (int[] face : faces) {
			for (int i : face) {
				result[cpt] = i;
				cpt++;
			}
		}
		return result;
	}
	
	public float[] getIdxBufferForLines() {
		
		int sizeOfBuffer = 0;
		for (int[] face : faces) {
			sizeOfBuffer += face.length;
		}
		float[] result = new float[sizeOfBuffer*2];
		int cpt = 0;
		for (int[] face : faces) {
			for (int i = 0 ; i < face.length ; i++) {
				result[cpt] = face[i];
				cpt++;
				int nextIdx = (i == face.length-1) ? face[0] : face[i+1];
				result[cpt] = nextIdx;
				cpt++;
			}
		}
		return result;
	}
	
	public float[] getCoordBuffer() {
		return coords;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public float[] getColorArray(GamaColor gamaColor) {
		int verticesNb = coords.length / 3;
		float[] result = null;
		float[] color = new float[]{ (float)(gamaColor.red()) /255f,
				(float)(gamaColor.green()) /255f, 
				(float)(gamaColor.blue()) /255f,
				(float)(gamaColor.alpha()) /255f};
		result = new float[verticesNb*4];
		for (int i = 0 ; i < verticesNb ; i++) {
			result[4*i] = (float) color[0];
			result[4*i+1] = (float) color[1];
			result[4*i+2] = (float) color[2];
			result[4*i+3] = (float) color[3];
		}
		return result;
	}
	
	private void computeNormals() {
		float[] result = new float[coords.length];
		
		for (int vIdx = 0 ; vIdx < coords.length/3 ; vIdx++) {
			
			float xVal = 0;
			float yVal = 0;
			float zVal = 0;
			float sum = 0;
			
			int[][] vtxNeighbours = getVertexNeighbours(vIdx);
			for (int i = 0 ; i < vtxNeighbours.length ; i++) {
				float[] vtxCoord = new float[] {coords[vIdx*3],coords[vIdx*3+1],coords[vIdx*3+2]}; 
				float[] vtxCoordBefore = new float[] {coords[vtxNeighbours[i][0]*3],coords[vtxNeighbours[i][0]*3+1],coords[vtxNeighbours[i][0]*3+2]};
				float[] vtxCoordAfter = new float[] {coords[vtxNeighbours[i][1]*3],coords[vtxNeighbours[i][1]*3+1],coords[vtxNeighbours[i][1]*3+2]};
				float[] vec1 = new float[] {
						vtxCoordBefore[0] - vtxCoord[0],
						vtxCoordBefore[1] - vtxCoord[1],
						vtxCoordBefore[2] - vtxCoord[2]
				};
				float[] vec2 = new float[] {
						vtxCoordAfter[0] - vtxCoord[0],
						vtxCoordAfter[1] - vtxCoord[1],
						vtxCoordAfter[2] - vtxCoord[2]
				};
				float[] vectProduct = GeomMathUtils.CrossProduct(vec1,vec2);
				sum = vectProduct[0]*vectProduct[0] + vectProduct[1]*	vectProduct[1] + vectProduct[2]*vectProduct[2];
				xVal += vectProduct[0] / Math.sqrt(sum);
				yVal += vectProduct[1] / Math.sqrt(sum);
				zVal += vectProduct[2] / Math.sqrt(sum);
			}
			
			sum = xVal*xVal + yVal*yVal + zVal*zVal;
			xVal = (float) (xVal / Math.sqrt(sum));
			yVal = (float) (yVal / Math.sqrt(sum));
			zVal = (float) (zVal / Math.sqrt(sum));
			
			result[3*vIdx] = xVal;
			result[3*vIdx+1] = yVal;
			result[3*vIdx+2] = zVal;
		}
		
		normals = result;
	}
	
	public DrawingEntity[] getDrawingEntities() {
		// returns the DrawingEntities corresponding to this shape (can be 2 DrawingEntities
		// in case it has been asked to draw the border)
		DrawingEntity[] result = null;
		// if triangulate, returns only one result
		if (isTriangulation) {
			result = new DrawingEntity[1];
			
			// configure the drawing entity for the border
			DrawingEntity borderEntity = new DrawingEntity();
			borderEntity.setVertices(coords);
			borderEntity.setNormals(normals);
			borderEntity.setIndices(getIdxBufferForLines());
			borderEntity.setColors(getColorArray(TRIANGULATION_COLOR));
			borderEntity.type = DrawingEntity.Type.BORDER;
			borderEntity.setMaterial(new Material(1,5));
			
			result[0] = borderEntity;
		}
		else {
			// if not triangulate, then returns 2 results if draw border
			if (borderColor != null) {
				// two drawing entities
				result = new DrawingEntity[2];
				
				// configure the drawing entity for the border
				DrawingEntity borderEntity = new DrawingEntity();
				borderEntity.setVertices(coordsForBorder);
				borderEntity.setNormals(normals);
				borderEntity.setIndices(idxForBorder);
				borderEntity.setColors(getColorArray(borderColor));
				borderEntity.type = DrawingEntity.Type.BORDER;
				borderEntity.setMaterial(new Material(1,5));
				
				result[1] = borderEntity;
			}
			else {
				// only one drawing entity
				result = new DrawingEntity[1];
			}
			
			// configure the drawing entity for the filled faces
			DrawingEntity filledEntity = new DrawingEntity();
			filledEntity.setVertices(coords);
			filledEntity.setNormals(normals);
			filledEntity.setIndices(getIdxBuffer());
			filledEntity.setColors(getColorArray(color));
			filledEntity.type = DrawingEntity.Type.FILLED;
			filledEntity.setMaterial(new Material(1,5));
			if (textId != -1)
			{
				filledEntity.type = DrawingEntity.Type.TEXTURED;
				filledEntity.setTextureID(textId);
				filledEntity.setUvMapping(uvMapping);
			}
			
			result[0] = filledEntity;
		}
		
		return result;
	}
	
	///////////////////////////////////
	// UTIL CLASSES
	///////////////////////////////////
	
	private int[][] getVertexNeighbours(int idx) {
		// return a int[][2] array with at each time the vertex before
		//and the vertex after the one designed with "idx".
		
		ArrayList<int[]> list = new ArrayList<int[]>();
		
		for (int faceIdx = 0 ; faceIdx < faces.size() ; faceIdx++) {
			int[] face = faces.get(faceIdx);
			int idxOfVtxInCurrentFace = -1;
			for (int i = 0 ; i < face.length ; i++) {
				if (idx == face[i]) {
					idxOfVtxInCurrentFace = i;
				}
			}
			if (idxOfVtxInCurrentFace != -1) {
				// the vertex exists in the face browse !
				// we search the vertex before and the vertex after this vertex
				Integer idxVertexBefore, idxVertexAfter;
				idxVertexBefore = (idxOfVtxInCurrentFace == 0) ? face[face.length-1] : face[idxOfVtxInCurrentFace-1];
				idxVertexAfter = (idxOfVtxInCurrentFace == face.length-1) ? face[0] : face[idxOfVtxInCurrentFace+1];
				// we add the couple of point to the list
				int[] couple = new int[] {idxVertexBefore,idxVertexAfter};
				list.add(couple);
			}
		}
		
		int[][] result = new int[list.size()][2];
		for (int i = 0 ; i < result.length ; i++) {
			result[i] = list.get(i);
		}
		
		return result;
	}
	
	private int[] getConnexeFaces(int faceIdx) {
		// return the array of idx of faces which are connexe to the face faces.get(faceIdx)
		ArrayList<Integer> list = new ArrayList<Integer>();
		int[] face = faces.get(faceIdx);
		for (int faceIdxToCompare = 0 ; faceIdxToCompare < faces.size() ; faceIdxToCompare++) {
			if (faceIdxToCompare != faceIdx) {
				int[] faceToCompare = faces.get(faceIdxToCompare);
				int cpt = 0;
				for (int vIdx : face) {
					for (int vIdx2 : faceToCompare) {
						if ( vIdx == vIdx2 ) {
							cpt++;
							break;
						}
					}
				}
				if (cpt > 1) {
					// some vertices are in common with the current face --> the face is a connexe one
					list.add(faceIdxToCompare);
				}
			}
		}
		int[] result = new int[list.size()];
		for (int i = 0 ; i < result.length ; i++) {
			result[i] = list.get(i);
		}
		return result;
	}
	
	private double getAngleBetweenFaces(int[] face1, int[] face2) {
		
		float[] vect1 = GeomMathUtils.CrossProduct(
				new float[] {
						coords[(int) face1[2]*3]-coords[(int) face1[0]*3],
						coords[(int) (face1[2])*3+1]-coords[(int) (face1[0])*3+1],
						coords[(int) (face1[2])*3+2]-coords[(int) (face1[0])*3+2]
				}
				,
				new float[] {
						coords[(int) face1[1]*3]-coords[(int) face1[0]*3],
						coords[(int) (face1[1])*3+1]-coords[(int) (face1[0])*3+1],
						coords[(int) (face1[1])*3+2]-coords[(int) (face1[0])*3+2]
				}
			);
		float[] vect2 = GeomMathUtils.CrossProduct(
				new float[] {
						coords[(int) face2[2]*3]-coords[(int) face2[0]*3],
						coords[(int) (face2[2])*3+1]-coords[(int) (face2[0])*3+1],
						coords[(int) (face2[2])*3+2]-coords[(int) (face2[0])*3+2]
				}
				,
				new float[] {
						coords[(int) face2[1]*3]-coords[(int) face2[0]*3],
						coords[(int) (face2[1])*3+1]-coords[(int) (face2[0])*3+1],
						coords[(int) (face2[1])*3+2]-coords[(int) (face2[0])*3+2]
				}
			);
		// determine the angle between the two vectors
		float angle = (float) Math.acos(GeomMathUtils.ScalarProduct(GeomMathUtils.Normalize(vect1), GeomMathUtils.Normalize(vect2)));
		// if the angle between the two vectors is greater than "smoothAngle", return true.
		return Math.toDegrees(angle);
	}
	
	private void splitFaces(int idxFace1, int idxFace2) {
		int[] connexeVertexIdx = getMutualVertexIdx(idxFace1, idxFace2);
		// all those connexeVertex have to be duplicated in the coords list !
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		// this map will contain [initialIdx :: newIdx]
		for (int i = 0 ; i < connexeVertexIdx.length ; i++) {
			// create a new vertex
			float[] newVertex = new float[3];
			newVertex[0] = coords[connexeVertexIdx[i]*3];
			newVertex[1] = coords[connexeVertexIdx[i]*3+1];
			newVertex[2] = coords[connexeVertexIdx[i]*3+2];
			// add a new coordinate at the end of the array
			coords = Utils.concatFloatArrays(coords, newVertex);
			// we get the new idx of this vertex, and we store it in the map
			int newIdx = (coords.length-3) / 3;
			map.put(connexeVertexIdx[i], newIdx);
		}
		// we change the values of the idx in the faces list ( /!\ we start the changes from faces.get(idxFace1+1) !!)
		for (int faceIdx = idxFace1+1 ; faceIdx < faces.size() ; faceIdx++) {
			int[] face = faces.get(faceIdx);
			// change the idx values if needed (if there are some in the map)
			for (int i = 0 ; i < face.length ; i++) {
				if (map.containsKey(face[i])) {
					face[i] = map.get(face[i]);
				}
				faces.remove(faceIdx);
				faces.add(faceIdx,face);
			}
		}
		// report the idx changes to the map "mapOfOriginalIdx"
		for (int i : map.keySet()) {
			for (int j : mapOfOriginalIdx.keySet()) {
				if (mapOfOriginalIdx.get(j) == i) {
					// we replace the value by the new one
					mapOfOriginalIdx.put(j, i);
				}
			}
		}
	}
	
	int[] getMutualVertexIdx(int idxFace1, int idxFace2) {
		int[] face1 = faces.get(idxFace1);
		int[] face2 = faces.get(idxFace2);
		int cpt = 0;
		for (int i : face1) {
			for (int j : face2) {
				if (i == j) {
					cpt++;
				}
			}
		}
		int[] result = new int[cpt];
		cpt = 0;
		for (int i=0 ; i < face1.length ; i++) {
			for (int j=0 ; j < face2.length ; j++) {
				if (face1[i] == face2[j]) {
					result[cpt] = face1[i];
					cpt++;
				}
			}
		}
		return result;
	}

}
