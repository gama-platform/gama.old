/*********************************************************************************************
 *
 * 'AbstractTransformer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gaml.types.GamaMaterialType;
import msi.gaml.types.Types;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.modernOpenGL.Material;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.utils.Utils;

/*
 * This class is the intermediary class for the transformation from a GeometryObject to a (or some) DrawingElement(s).
 */

abstract class AbstractTransformer {

	private static float SMOOTH_SHADING_ANGLE = 40f; // in degree
	protected static int BUILT_IN_SHAPE_RESOLUTION = 32; // for sphere / cone / cylinder
	private static GamaColor TRIANGULATE_COLOR = new GamaColor(1.0, 1.0, 0.0, 1.0);
	private static GamaColor DEFAULT_COLOR = GamaPreferences.Displays.CORE_COLOR.getValue();

	protected boolean geometryCorrupted = false;

	protected boolean isOverlay = false;
	protected boolean isTriangulation = false;
	protected boolean isLightInteraction = true;
	protected boolean isWireframe = false;
	protected ArrayList<int[]> faces = new ArrayList<int[]>();
	// (way to construct a face from the indices of the coordinates (anti clockwise for front face) )
	private final ArrayList<int[]> edgesToSmooth = new ArrayList<int[]>();
	// (list that store all the edges erased thanks to the smooth shading (those edges must
	// not be displayed when displaying the borders !) )
	protected float[] coords;
	protected Coordinate[] coordsWithDoublons;
	protected float[] uvMapping;
	protected float[] normals;
	protected int[] textureIDs = null; // null for "no texture"
	// protected String[] texturePaths = null; // null for "no texture"
	protected int[][][] bufferedImageValue = null;
	protected float[] coordsForBorder;
	protected float[] idxForBorder;

	private HashMap<Integer, Integer> mapOfOriginalIdx = new HashMap<Integer, Integer>();

	protected int[] topFace;
	protected int[] bottomFace;

	// private fields from the GeometryObject
	protected int pickingId;
	protected IShape.Type type;
	protected double depth;
	protected GamaPoint translation;
	protected GamaPair<Double, GamaPoint> rotation;
	protected GamaPoint size;
	protected GamaColor color;
	protected GamaColor[] colors;
	protected GamaColor borderColor;
	protected Coordinate[] coordinates;
	protected GamaMaterial material;

	protected void genericInit(final AbstractObject object, final boolean isOverlay, final boolean isTriangulation,
			final double layerAlpha) {
		this.isOverlay = isOverlay;
		this.faces = new ArrayList<int[]>();
		this.coords = new float[0];
		this.coordsForBorder = new float[0];

		this.depth = MoreObjects.firstNonNull(object.getHeight(), 0.0);
		this.pickingId = object.getIndex();
		final Color c = object.getColor();
		if (c != null)
			this.color = new GamaColor(c, c.getAlpha() / 255.0 * layerAlpha);
		else
			this.color = null;
		this.borderColor = object.getBorder();
		this.isTriangulation = isTriangulation;
		this.material = object.getMaterial();
		if (this.material == null)
			this.material = GamaMaterialType.DEFAULT_MATERIAL;

		this.translation = object.getLocation();
		if (translation == null)
			translation = new GamaPoint(0, 0, 0); // ex : charts
		final AxisAngle rot = object.getRotation();
		// Change to a negative rotation to fix Issue #1514
		this.rotation = rot == null ? null : new GamaPair<>(rot.getAngle(), rot.getAxis(), Types.FLOAT, Types.POINT);
		this.isWireframe = !object.isFilled();
	}

	public String getHashCode() {
		// returns the hashcode used in the shape cache.
		String result = null;
		if (type.toString().equals("SPHERE") || type.toString().equals("PYRAMID") || type.toString().equals("CONE")
				|| type.toString().equals("CUBE") || type.toString().equals("CYLINDER")
				|| type.toString().equals("RECTANGLE")) {
			result = type.toString() + (isWireframe ? "_wireframe" : "") + depth;
		}
		// else {
		// String coordsInString = "";
		// for (final Coordinate c : coordsWithDoublons) {
		// coordsInString += c.x;
		// coordsInString += c.y;
		// coordsInString += c.z;
		// }
		// result = type.toString() + coordsInString;
		// }
		return result;
	}

	protected void cancelTransformation() {
		// This function will cancel the transformation of size and position.
		// The purpose of it is to optimize and create a "basic" shape which
		// will be stored to the ShapeCache.
		this.depth = this.depth * 1 / size.z;
		coordsWithDoublons = GeomMathUtils.setTranslationToCoordArray(coordsWithDoublons, -translation.x,
				-translation.y, -translation.z);
		coordsWithDoublons =
				GeomMathUtils.setScalingToCoordArray(coordsWithDoublons, 1 / size.x, 1 / size.y, 1 / size.z);
	}

	protected void loadManyFacedShape(final AbstractTransformer shape) {
		faces = shape.faces;
		coords = shape.coords;
		uvMapping = shape.uvMapping;
		normals = shape.normals;
		coordsForBorder = shape.coordsForBorder;
		idxForBorder = shape.idxForBorder;

		topFace = shape.topFace;
		bottomFace = shape.bottomFace;
	}

	private int getOriginalIdx(final int idx) {
		// this function is used to get the original idx (from the idx buffer)
		// before the smooth shading
		return mapOfOriginalIdx.get(idx);
	}

	protected void initBorders() {
		idxForBorder = getIdxBufferForLines();
		coordsForBorder = coords;
		// init the mapOfOriginalIdx
		for (final float i : idxForBorder) {
			mapOfOriginalIdx.put((int) i, (int) i);
		}
	}

	protected void correctBorders() {
		// delete all the edges that are present in the list edgeToSmooth
		for (int idx = 0; idx < idxForBorder.length;) {
			boolean edgeIsToDelete = false;
			for (final int[] edgeToSmooth : edgesToSmooth) {
				if ((int) idxForBorder[idx] == edgeToSmooth[0] && (int) idxForBorder[idx + 1] == edgeToSmooth[1]
						|| (int) idxForBorder[idx] == edgeToSmooth[1]
								&& (int) idxForBorder[idx + 1] == edgeToSmooth[0]) {
					edgeIsToDelete = true;
					break;
				}
			}
			if (edgeIsToDelete) {
				final float[] begin = Arrays.copyOfRange(idxForBorder, 0, idx);
				final float[] end = Arrays.copyOfRange(idxForBorder, idx + 2, idxForBorder.length);
				idxForBorder = Utils.concatFloatArrays(begin, end);
			} else {
				idx += 2;
			}
		}
	}

	protected void computeUVMapping() {
		int sizeArray = 0;
		for (int i = 0; i < faces.size(); i++) {
			sizeArray += faces.get(i).length;
		}
		sizeArray = coords.length / 3;
		uvMapping = new float[sizeArray * 2];
		for (int i = 0; i < faces.size(); i++) {
			final int[] face = faces.get(i);
			if (face.length == 4) {
				// case of squared faces :
				// vertex 1 :
				uvMapping[face[0] * 2] = 0;
				uvMapping[face[0] * 2 + 1] = 0;
				// vertex 2 :
				uvMapping[face[1] * 2] = 0;
				uvMapping[face[1] * 2 + 1] = 1;
				// vertex 3 :
				uvMapping[face[2] * 2] = 1;
				uvMapping[face[2] * 2 + 1] = 1;
				// vertex 4 :
				uvMapping[face[3] * 2] = 1;
				uvMapping[face[3] * 2 + 1] = 0;
			} else if (face.length == 3) {
				// case of triangular faces :
				// vertex 1 (summit) :
				uvMapping[face[0] * 2] = 0.5f;
				uvMapping[face[0] * 2 + 1] = 1;
				// vertex 2 :
				uvMapping[face[1] * 2] = 1;
				uvMapping[face[1] * 2 + 1] = 0;
				// vertex 3 :
				uvMapping[face[2] * 2] = 0;
				uvMapping[face[2] * 2 + 1] = 0;
			} else {
				// generic case : the rectangular and triangular faces are
				// computed aside for a matter of performance
				// find the bounds of the face
				float minX = Float.MAX_VALUE;
				float minY = Float.MAX_VALUE;
				float maxX = Float.MIN_VALUE;
				float maxY = Float.MIN_VALUE;
				for (int vIdx = 0; vIdx < face.length; vIdx++) {
					if (coords[face[vIdx] * 3] < minX)
						minX = coords[face[vIdx] * 3];
					if (coords[face[vIdx] * 3 + 1] < minY)
						minY = coords[face[vIdx] * 3 + 1];
					if (coords[face[vIdx] * 3] > maxX)
						maxX = coords[face[vIdx] * 3];
					if (coords[face[vIdx] * 3 + 1] > maxY)
						maxY = coords[face[vIdx] * 3 + 1];
				}
				final float width = maxX - minX;
				final float height = maxY - minY;
				for (int vIdx = 0; vIdx < face.length; vIdx++) {
					// compute u and v as the percentage of maximum bounds
					final float uCoords = (coords[face[vIdx] * 3] - minX) / width;
					final float vCoords = (coords[face[vIdx] * 3 + 1] - minY) / height;
					uvMapping[face[vIdx] * 2] = uCoords;
					uvMapping[face[vIdx] * 2 + 1] = 1 - vCoords;
				}
			}
		}
	}

	protected void applySmoothShading() {
		for (int faceIdx = 0; faceIdx < faces.size(); faceIdx++) {
			final int[] idxConnexeFaces = getConnexeFaces(faceIdx);
			for (int idxConnexeFace = 0; idxConnexeFace < idxConnexeFaces.length; idxConnexeFace++) {
				if (getAngleBetweenFaces(faces.get(idxConnexeFaces[idxConnexeFace]),
						faces.get(faceIdx)) > SMOOTH_SHADING_ANGLE) {
					splitFaces(faceIdx, idxConnexeFaces[idxConnexeFace]);
				} else {
					saveEdgeToSmooth(idxConnexeFaces[idxConnexeFace], faceIdx);
				}
			}
		}
	}

	private void saveEdgeToSmooth(final int face1Idx, final int face2Idx) {
		final int[] idxArray = getMutualVertexIdx(face1Idx, face2Idx);
		if (idxArray.length == 2) {
			getOriginalIdx(idxArray[0]);
			getOriginalIdx(idxArray[1]);
			final int idxV1 = getOriginalIdx(idxArray[0]);
			final int idxV2 = getOriginalIdx(idxArray[1]);
			final int[] edge = new int[] { idxV1, idxV2 };
			edgesToSmooth.add(edge);
		}
	}

	protected void triangulate() {
		final UtilTriangulation triangulate = new UtilTriangulation(coords);
		for (int i = 0; i < faces.size(); i++) {
			final int[] faceTriangulated = triangulate.ear_cutting_triangulatation(faces.get(i));
			faces.remove(i);
			if (faceTriangulated == null) {
				geometryCorrupted = true;
				break;
			}
			faces.add(i, faceTriangulated);
		}
	}

	protected void applyTransformation() {
		// apply transform to the coords if needed, and also to the
		// coordsForBorders
		coords = applyTransformation(coords);
		coordsForBorder = applyTransformation(coordsForBorder);
		if (rotation != null && normals != null) {
			normals = GeomMathUtils.setRotationToVertex(normals, (float) Math.toRadians(rotation.key.floatValue()),
					(float) rotation.value.x, (float) rotation.value.y, (float) rotation.value.z);
		}
	}

	private float[] applyTransformation(float[] coords) {
		// apply rotation (if facet "rotate" for draw is used)
		if (rotation != null) {
			// apply the rotation
			coords = GeomMathUtils.setRotationToVertex(coords, (float) Math.toRadians(rotation.key.floatValue()),
					(float) rotation.value.x, (float) rotation.value.y, (float) rotation.value.z);
		}
		// apply scaling (if facet "size" for draw is used)
		if (size != null) {
			// apply the scaling
			coords = GeomMathUtils.setScalingToVertex(coords, (float) size.x, (float) size.y, (float) size.z);
		}
		coords = GeomMathUtils.setTranslationToVertex(coords, (float) translation.x, (float) translation.y,
				(float) translation.z);
		return coords;
	}

	public float[] getPickingIdx() {
		final float[] result = new float[coords.length / 3];
		for (int i = 0; i < result.length; i++) {
			result[i] = pickingId;
		}
		return result;
	}

	public float[] getIdxBuffer() {

		int sizeOfBuffer = 0;
		for (final int[] face : faces) {
			sizeOfBuffer += face.length;
		}
		final float[] result = new float[sizeOfBuffer];
		int cpt = 0;
		for (final int[] face : faces) {
			for (final int i : face) {
				result[cpt] = i;
				cpt++;
			}
		}
		return result;
	}

	public float[] getIdxBufferForLines() {

		int sizeOfBuffer = 0;
		for (final int[] face : faces) {
			sizeOfBuffer += face.length;
		}
		final float[] result = new float[sizeOfBuffer * 2];
		int cpt = 0;
		for (final int[] face : faces) {
			for (int i = 0; i < face.length; i++) {
				result[cpt] = face[i];
				cpt++;
				final int nextIdx = i == face.length - 1 ? face[0] : face[i + 1];
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

	protected float[] getColorArray(final GamaColor gamaColor, final float[] coordsArray) {
		final int verticesNb = coordsArray.length / 3;
		float[] result = null;
		result = new float[verticesNb * 4];
		if (colors != null) {
			// the case where a list of color has been passed for the geometry object
			for (int i = 0; i < verticesNb; i++) {
				float[] color = new float[] { (float) colors[0].red() / 255f, (float) colors[0].green() / 255f,
						(float) colors[0].blue() / 255f, (float) colors[0].alpha() / 255f };
				if (i < colors.length) {
					color = new float[] { (float) colors[i].red() / 255f, (float) colors[i].green() / 255f,
							(float) colors[i].blue() / 255f, (float) colors[i].alpha() / 255f };
				}
				result[4 * i] = color[0];
				result[4 * i + 1] = color[1];
				result[4 * i + 2] = color[2];
				result[4 * i + 3] = color[3];
			}
		} else {
			final float[] color = new float[] { (float) gamaColor.red() / 255f, (float) gamaColor.green() / 255f,
					(float) gamaColor.blue() / 255f, (float) gamaColor.alpha() / 255f };
			for (int i = 0; i < verticesNb; i++) {
				result[4 * i] = color[0];
				result[4 * i + 1] = color[1];
				result[4 * i + 2] = color[2];
				result[4 * i + 3] = color[3];
			}
		}

		return result;
	}

	private int getFaceNumber(final int vIdx) {
		for (int i = 0; i < faces.size(); i++) {
			for (final int j : faces.get(0)) {
				if (j == vIdx) { return i; }
			}
		}
		return -1;
	}

	protected void computeNormals() {
		final float[] result = new float[coords.length];

		boolean faceIsClockwise[] = new boolean[faces.size()];
		for (int i = 0; i < faces.size(); i++) {
			final float[] coordsOfFace = new float[faces.get(i).length * 3];
			for (int j = 0; j < faces.get(i).length; j++) {
				coordsOfFace[j * 3] = coords[faces.get(i)[j] * 3];
				coordsOfFace[j * 3 + 1] = coords[faces.get(i)[j] * 3 + 1];
				coordsOfFace[j * 3 + 2] = coords[faces.get(i)[j] * 3 + 2];
			}
			faceIsClockwise[i] = Utils.isClockwise(coordsOfFace);
		}
		if (type.equals("SPHERE")) {
			faceIsClockwise = new boolean[0];
		}

		for (int vIdx = 0; vIdx < coords.length / 3; vIdx++) {

			float xVal = 0;
			float yVal = 0;
			float zVal = 0;
			float sum = 0;

			final int[][] vtxNeighbours = getVertexNeighbours(vIdx);
			for (int i = 0; i < vtxNeighbours.length; i++) {
				final float[] vtxCoord = new float[] { coords[vIdx * 3], coords[vIdx * 3 + 1], coords[vIdx * 3 + 2] };
				final float[] vtxCoordBefore = new float[] { coords[vtxNeighbours[i][0] * 3],
						coords[vtxNeighbours[i][0] * 3 + 1], coords[vtxNeighbours[i][0] * 3 + 2] };
				final float[] vtxCoordAfter = new float[] { coords[vtxNeighbours[i][1] * 3],
						coords[vtxNeighbours[i][1] * 3 + 1], coords[vtxNeighbours[i][1] * 3 + 2] };
				final float[] vec1 = new float[] { vtxCoordBefore[0] - vtxCoord[0], vtxCoordBefore[1] - vtxCoord[1],
						vtxCoordBefore[2] - vtxCoord[2] };
				final float[] vec2 = new float[] { vtxCoordAfter[0] - vtxCoord[0], vtxCoordAfter[1] - vtxCoord[1],
						vtxCoordAfter[2] - vtxCoord[2] };
				// compute the vectorial product between the two edges. The vectorial product is done on the other side
				// .. if the vertex is inside the geometry.
				float[] vectProduct;
				float[] coordArray = Utils.concatFloatArrays(vtxCoordBefore, vtxCoord);
				coordArray = Utils.concatFloatArrays(coordArray, vtxCoordAfter);
				final int faceNumber = getFaceNumber(vIdx);
				boolean vIsInsideTheGeometry = false;
				if (faceNumber != -1) {
					vIsInsideTheGeometry = faceIsClockwise[faceNumber] != Utils.isClockwise(coordArray);
				}
				if (vIsInsideTheGeometry) {
					vectProduct = GeomMathUtils.CrossProduct(vec2, vec1);
				} else {
					vectProduct = GeomMathUtils.CrossProduct(vec1, vec2);
				}
				sum = vectProduct[0] * vectProduct[0] + vectProduct[1] * vectProduct[1]
						+ vectProduct[2] * vectProduct[2];
				xVal += vectProduct[0] / Math.sqrt(sum);
				yVal += vectProduct[1] / Math.sqrt(sum);
				zVal += vectProduct[2] / Math.sqrt(sum);
			}

			sum = xVal * xVal + yVal * yVal + zVal * zVal;
			xVal = (float) (xVal / Math.sqrt(sum));
			yVal = (float) (yVal / Math.sqrt(sum));
			zVal = (float) (zVal / Math.sqrt(sum));

			result[3 * vIdx] = xVal;
			result[3 * vIdx + 1] = yVal;
			result[3 * vIdx + 2] = zVal;
		}

		normals = result;
	}

	public abstract ArrayList<DrawingEntity> getDrawingEntityList();

	public DrawingEntity[] getDrawingEntities() {
		final ArrayList<DrawingEntity> drawingEntityList = getDrawingEntityList();
		final DrawingEntity[] result = new DrawingEntity[drawingEntityList.size()];
		for (int i = 0; i < result.length; i++) {
			final DrawingEntity drawingEntity = drawingEntityList.get(i);
			drawingEntity.setPickingIds(getPickingIdx());
			drawingEntity.enableOverlay(isOverlay);
			result[i] = drawingEntity;
		}

		return result;
	}

	protected ArrayList<DrawingEntity> getTriangulationDrawingEntity() {
		final ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();

		// configure the drawing entity for the border
		final DrawingEntity borderEntity =
				createBorderEntity(coords, getIdxBufferForLines(), getColorArray(TRIANGULATE_COLOR, coords));

		if (borderEntity != null)
			result.add(borderEntity);

		return result;
	}

	protected ArrayList<DrawingEntity> getWireframeDrawingEntity() {
		final ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();

		// configure the drawing entity for the border
		final DrawingEntity borderEntity =
				createBorderEntity(coords, getIdxBufferForLines(), getColorArray(borderColor, coords));

		if (borderEntity != null)
			result.add(borderEntity);

		return result;
	}

	protected ArrayList<DrawingEntity> get1DDrawingEntity() {
		// particular case if the geometry is a point or a line : we only draw
		// the "borders" with the color "color" (and not the "bordercolor" !!)
		final ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();

		// configure the drawing entity for the border
		final DrawingEntity borderEntity =
				createBorderEntity(coordsForBorder, idxForBorder, getColorArray(color, coordsForBorder));

		if (borderEntity != null)
			result.add(borderEntity);

		return result;
	}

	protected ArrayList<DrawingEntity> getStandardDrawingEntities() {
		// the number of drawing entity is equal to the number of textured
		// applied + 1 if there is a border.
		// If no texture is used, return 1 (+1 if there is a border).
		final ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();

		if (borderColor != null) {
			// if there is a border

			// configure the drawing entity for the border
			final DrawingEntity borderEntity =
					createBorderEntity(coordsForBorder, idxForBorder, getColorArray(borderColor, coordsForBorder));

			if (borderEntity != null)
				result.add(borderEntity);
		}

		if (textureIDs == null && color == null) {
			// the geometry is not filled. We create no more entity.
		} else {
			if (color == null) {
				color = DEFAULT_COLOR; // set the default color to yellow.
			}
			if (textureIDs == null || textureIDs.length == 1 || topFace == null && bottomFace == null) {
				// configure the drawing entity for the filled faces
				final DrawingEntity filledEntity = new DrawingEntity();
				filledEntity.setVertices(coords);
				filledEntity.setNormals(normals);
				filledEntity.setIndices(getIdxBuffer());
				filledEntity.setColors(getColorArray(color, coords));
				filledEntity.setMaterial(
						new Material(this.material.getDamper(), this.material.getReflectivity(), isLightInteraction));
				filledEntity.type = DrawingEntity.Type.FACE;
				if (textureIDs != null) {
					filledEntity.type = DrawingEntity.Type.TEXTURED;
					// if (texturePaths != null)
					// filledEntity.setTexturePath(texturePaths[0]);
					// else
					if (bufferedImageValue != null)
						filledEntity.setBufferedImageTextureValue(bufferedImageValue);
					filledEntity.setTextureID(textureIDs[0]);
					filledEntity.setUvMapping(uvMapping);
				}

				result.add(filledEntity);
			} else {
				// for multi-textured object, we split into 2 entities : the
				// first will be the bottom + top face, the second will be the
				// rest of the shape.
				// build the bot/top entity
				final DrawingEntity botTopEntity = new DrawingEntity();
				final int numberOfSpecialFaces =
						topFace != null && topFace.length > 1 ? bottomFace != null && bottomFace.length > 1 ? 2 : 1 : 1; // a
																															// "specialFace"
																															// is
																															// either
																															// a
																															// top
																															// or
																															// a
																															// bottom
																															// face.
				int[] idxBuffer = faces.get(0);
				if (numberOfSpecialFaces == 2) {
					idxBuffer = Utils.concatIntArrays(faces.get(0), faces.get(1));
				}
				final float[] botTopIndices = new float[idxBuffer.length];
				int vtxNumber = 0;
				for (int i = 0; i < idxBuffer.length; i++) {
					botTopIndices[i] = idxBuffer[i];
					if (vtxNumber <= botTopIndices[i])
						vtxNumber = (int) botTopIndices[i] + 1;
				}
				final float[] botTopCoords = new float[vtxNumber * 3];
				for (int i = 0; i < vtxNumber; i++) {
					botTopCoords[3 * i] = coords[3 * i];
					botTopCoords[3 * i + 1] = coords[3 * i + 1];
					botTopCoords[3 * i + 2] = coords[3 * i + 2];
				}
				final float[] botTopNormals = Arrays.copyOfRange(normals, 0, vtxNumber * 3);
				final float[] botTopUVMapping = Arrays.copyOfRange(uvMapping, 0, vtxNumber * 2);

				botTopEntity.setVertices(botTopCoords);
				botTopEntity.setNormals(botTopNormals);
				botTopEntity.setIndices(botTopIndices);
				botTopEntity.setColors(getColorArray(color, coords));
				botTopEntity.type = DrawingEntity.Type.TEXTURED;
				botTopEntity.setMaterial(
						new Material(this.material.getDamper(), this.material.getReflectivity(), isLightInteraction));
				// botTopEntity.setTexturePath(texturePaths[0]);
				botTopEntity.setTextureID(textureIDs[0]);
				botTopEntity.setUvMapping(botTopUVMapping);

				// build the rest of the faces
				final DrawingEntity otherEntity = new DrawingEntity();
				// removing the "special faces" from the list of faces
				faces.remove(0);
				if (numberOfSpecialFaces == 2) {
					// remove a second face !
					faces.remove(0);
				}
				coords = Arrays.copyOfRange(coords, vtxNumber * 3, coords.length);
				normals = Arrays.copyOfRange(normals, vtxNumber * 3, normals.length);
				uvMapping = Arrays.copyOfRange(uvMapping, vtxNumber * 2, uvMapping.length);
				final float[] idxArray = getIdxBuffer();
				// removing vtxNumber to every idx
				for (int i = 0; i < idxArray.length; i++) {
					idxArray[i] = idxArray[i] - vtxNumber;
				}

				otherEntity.setVertices(coords);
				otherEntity.setNormals(normals);
				otherEntity.setIndices(idxArray);
				otherEntity.setColors(getColorArray(color, coords));
				otherEntity.type = DrawingEntity.Type.TEXTURED;
				otherEntity.setMaterial(
						new Material(this.material.getDamper(), this.material.getReflectivity(), isLightInteraction));
				// otherEntity.setTexturePath(texturePaths[1]);
				otherEntity.setTextureID(textureIDs[1]);
				otherEntity.setUvMapping(uvMapping);

				result.add(botTopEntity);
				result.add(otherEntity);
			}
		}

		return result;
	}

	protected DrawingEntity createBorderEntity(final float[] coordsArray, final float[] idxArray,
			final float[] colorArray) {
		// utility method to build border entities, triangulated entities,
		// wireframe entities and polyline geometries.
		final DrawingEntity borderEntity = new DrawingEntity();
		borderEntity.setVertices(coordsArray);
		borderEntity.setIndices(idxArray);
		borderEntity.setColors(colorArray);
		borderEntity.setMaterial(new Material(this.material.getDamper(), this.material.getReflectivity(), false));
		if (coordsArray.length > 3)
			borderEntity.type = DrawingEntity.Type.LINE;
		else
			borderEntity.type = DrawingEntity.Type.POINT;
		if (borderEntity.getIndices().length == 0) {
			// if the list of indices is empty, return null.
			return null;
		}
		return borderEntity;
	}

	///////////////////////////////////
	// UTIL CLASSES
	///////////////////////////////////

	private int[][] getVertexNeighbours(final int idx) {
		// return a int[][2] array with at each time the vertex before
		// and the vertex after the one designed with "idx".

		final ArrayList<int[]> list = new ArrayList<int[]>();

		for (int faceIdx = 0; faceIdx < faces.size(); faceIdx++) {
			final int[] face = faces.get(faceIdx);
			int idxOfVtxInCurrentFace = -1;
			for (int i = 0; i < face.length; i++) {
				if (idx == face[i]) {
					idxOfVtxInCurrentFace = i;
				}
			}
			if (idxOfVtxInCurrentFace != -1) {
				// the vertex exists in the face browse !
				// we search the vertex before and the vertex after this vertex
				Integer idxVertexBefore, idxVertexAfter;
				idxVertexBefore = idxOfVtxInCurrentFace == 0 ? face[face.length - 1] : face[idxOfVtxInCurrentFace - 1];
				idxVertexAfter = idxOfVtxInCurrentFace == face.length - 1 ? face[0] : face[idxOfVtxInCurrentFace + 1];
				// we add the couple of point to the list
				final int[] couple = new int[] { idxVertexBefore, idxVertexAfter };
				list.add(couple);
			}
		}

		final int[][] result = new int[list.size()][2];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}

		return result;
	}

	private int[] getConnexeFaces(final int faceIdx) {
		// return the array of idx of faces which are connexe to the face
		// faces.get(faceIdx)
		final ArrayList<Integer> list = new ArrayList<Integer>();
		final int[] face = faces.get(faceIdx);
		for (int faceIdxToCompare = 0; faceIdxToCompare < faces.size(); faceIdxToCompare++) {
			if (faceIdxToCompare != faceIdx) {
				final int[] faceToCompare = faces.get(faceIdxToCompare);
				int cpt = 0;
				for (final int vIdx : face) {
					for (final int vIdx2 : faceToCompare) {
						if (vIdx == vIdx2) {
							cpt++;
							break;
						}
					}
				}
				if (cpt > 1) {
					// some vertices are in common with the current face --> the
					// face is a connexe one
					list.add(faceIdxToCompare);
				}
			}
		}
		final int[] result = new int[list.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	private double getAngleBetweenFaces(final int[] face1, final int[] face2) {

		final float[] vect1 = GeomMathUtils.CrossProduct(
				new float[] { coords[face1[2] * 3] - coords[face1[0] * 3],
						coords[face1[2] * 3 + 1] - coords[face1[0] * 3 + 1],
						coords[face1[2] * 3 + 2] - coords[face1[0] * 3 + 2] },
				new float[] { coords[face1[1] * 3] - coords[face1[0] * 3],
						coords[face1[1] * 3 + 1] - coords[face1[0] * 3 + 1],
						coords[face1[1] * 3 + 2] - coords[face1[0] * 3 + 2] });
		final float[] vect2 = GeomMathUtils.CrossProduct(
				new float[] { coords[face2[2] * 3] - coords[face2[0] * 3],
						coords[face2[2] * 3 + 1] - coords[face2[0] * 3 + 1],
						coords[face2[2] * 3 + 2] - coords[face2[0] * 3 + 2] },
				new float[] { coords[face2[1] * 3] - coords[face2[0] * 3],
						coords[face2[1] * 3 + 1] - coords[face2[0] * 3 + 1],
						coords[face2[1] * 3 + 2] - coords[face2[0] * 3 + 2] });
		// determine the angle between the two vectors
		final float angle = (float) Math
				.acos(GeomMathUtils.ScalarProduct(GeomMathUtils.Normalize(vect1), GeomMathUtils.Normalize(vect2)));
		// if the angle between the two vectors is greater than "smoothAngle",
		// return true.
		return Math.toDegrees(angle);
	}

	private void splitFaces(final int idxFace1, final int idxFace2) {
		final int[] connexeVertexIdx = getMutualVertexIdx(idxFace1, idxFace2);
		// all those connexeVertex have to be duplicated in the coords list !
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		// this map will contain [initialIdx :: newIdx]
		for (int i = 0; i < connexeVertexIdx.length; i++) {
			// create a new vertex
			final float[] newVertex = new float[3];
			newVertex[0] = coords[connexeVertexIdx[i] * 3];
			newVertex[1] = coords[connexeVertexIdx[i] * 3 + 1];
			newVertex[2] = coords[connexeVertexIdx[i] * 3 + 2];
			// add a new coordinate at the end of the array
			coords = Utils.concatFloatArrays(coords, newVertex);
			// we get the new idx of this vertex, and we store it in the map
			final int newIdx = (coords.length - 3) / 3;
			map.put(connexeVertexIdx[i], newIdx);
		}
		// we change the values of the idx in the faces list ( /!\ we start the
		// changes from faces.get(idxFace1+1) !!)
		for (int faceIdx = idxFace1 + 1; faceIdx < faces.size(); faceIdx++) {
			final int[] face = faces.get(faceIdx);
			// change the idx values if needed (if there are some in the map)
			for (int i = 0; i < face.length; i++) {
				if (map.containsKey(face[i])) {
					face[i] = map.get(face[i]);
				}
				faces.remove(faceIdx);
				faces.add(faceIdx, face);
			}
		}
		// report the idx changes to the map "mapOfOriginalIdx"
		final HashMap<Integer, Integer> mapCopy = new HashMap<Integer, Integer>(mapOfOriginalIdx); // create
																									// a
																									// copy
																									// to
																									// avoid
																									// concurrentModificationException
		for (final int i : map.keySet()) {
			for (final int j : mapOfOriginalIdx.keySet()) {
				// if (mapOfOriginalIdx.get(j) == i) {
				if (j == i) {
					// we replace the value by the new one
					mapCopy.put(map.get(i), i);
				}
			}
		}
		mapOfOriginalIdx = mapCopy;
	}

	private int[] getMutualVertexIdx(final int idxFace1, final int idxFace2) {
		final int[] face1 = faces.get(idxFace1);
		final int[] face2 = faces.get(idxFace2);
		int cpt = 0;
		for (final int i : face1) {
			for (final int j : face2) {
				if (i == j) {
					cpt++;
				}
			}
		}
		final int[] result = new int[cpt];
		cpt = 0;
		for (int i = 0; i < face1.length; i++) {
			for (int j = 0; j < face2.length; j++) {
				if (face1[i] == face2[j]) {
					result[cpt] = face1[i];
					cpt++;
				}
			}
		}
		return result;
	}

}
