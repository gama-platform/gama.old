/*********************************************************************************************
 *
 * 'GeometryObjectTransformer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.utils.Utils;

/*
 * This class is the intermediary class for the transformation from a GeometryObject to a (or some) DrawingElement(s).
 */

class GeometryObjectTransformer extends AbstractTransformer {

	private GeometryObjectTransformer(final GeometryObjectTransformer obj) {
		loadManyFacedShape(obj);
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public GeometryObjectTransformer(final GeometryObject geomObj, final int[] textureIds,
			final boolean isLightInteraction, /* final String[] texturePaths, */ final boolean isOverlay,
			final boolean isTriangulation, final double layerAlpha) {
		// for GeometryObject
		genericInit(geomObj, isOverlay, isTriangulation, layerAlpha);

		this.colors = geomObj.getColors();
		if (colors == null || colors.length < 3)
			colors = null;
		else
			colors = Arrays.copyOfRange(colors, 2, colors.length);
		this.isLightInteraction = isLightInteraction && !is1DShape() && !isWireframe;

		this.textureIDs = textureIds;
		// this.texturePaths = texturePaths;
		this.type = geomObj.getType();

		coordsWithDoublons = geomObj.getGeometry().getCoordinates();

		this.size = getObjSize(geomObj);
		cancelTransformation();

		// the last coordinate is the same as the first one, no need for this
		this.coordinates = Arrays.copyOf(coordsWithDoublons, coordsWithDoublons.length - 1);

		if (!ShapeCache.isLoaded(getHashCode())) {

			if (is1DShape()) {
				// special case for 1D shape : no repetition of vertex
				coordinates = coordsWithDoublons;
				build1DShape();
			} else if (isPolyplan()) {
				// special case for plan/polyplan : no repetition of vertex
				coordinates = coordsWithDoublons;
				buildPolyplan();
			} else if (isPyramid()) {
				buildBottomFace();
				buildPyramidSummit();
				buildLateralFaces();
			} else if (isSphere()) {
				buildSphere();
			} else {
				// case of standard geometry : a standard geometry is a geometry
				// which can be build with
				// a bottom face and a top face, linked with some lateral faces.
				// In case the standard
				// geometry is a 2D shape, we only build the top face.
				if (depth > 0) {
					// 3D shape
					buildBottomFace();
					buildTopFace();
					buildLateralFaces();
				} else {
					// 2D shape
					buildTopFace();
				}
			}

			if (!is1DShape()) {
				initBorders();
				if (!isWireframe)
					applySmoothShading();
				if (!isWireframe)
					computeNormals();
				computeUVMapping();
				if (!isWireframe)
					triangulate();
				correctBorders();
			}

			ShapeCache.preloadShape(getHashCode(), new GeometryObjectTransformer(this));

		} else {
			loadManyFacedShape(ShapeCache.loadShape(getHashCode()));
		}
		applyTransformation();
	}

	@Override
	protected void correctBorders() {
		super.correctBorders();
		if (type == IShape.Type.CONE) {
			final ArrayList<Float> listIdx = new ArrayList<Float>();
			for (final float idx : idxForBorder) {
				if (idx / BUILT_IN_SHAPE_RESOLUTION - 1 > 0.1) {
					listIdx.add(idx);
				}
			}
			idxForBorder = new float[listIdx.size()];
			int i = 0;
			for (final Float f : listIdx) {
				idxForBorder[i++] = f;
			}
		}
	}

	@Override
	protected void computeUVMapping() {
		super.computeUVMapping();
		if (false && (type == IShape.Type.CONE || type == IShape.Type.CYLINDER || type == IShape.Type.SPHERE)) {
			// special case for cone, cylinder and sphere
			int resolution = BUILT_IN_SHAPE_RESOLUTION; // 32 vertex of
														// resolution for those
														// shapes
			if (type == IShape.Type.CYLINDER) {
				for (int i = 2 * resolution; i < 4 * resolution; i++) {
					if (i < 3 * resolution) {
						// bottom uv mapping
						uvMapping[i * 2 + 1] = 0;
					} else {
						// top uv mapping
						uvMapping[i * 2 + 1] = 1;
					}
					float uCoords = (float) (i % (resolution / 2)) / (float) (resolution / 2 - 1);
					if ((i - 2 * resolution) / (resolution / 2) == 0 || (i - 2 * resolution) / (resolution / 2) == 2) {
						// we pass the half of the cylinder : we draw the
						// texture backward
						uCoords = 1 - uCoords;
					}
					uvMapping[i * 2] = uCoords;
				}
			} else if (type == IShape.Type.CONE) { // 0 --> 32 : vertices for
													// the base of the cone. 33
													// : vertex for the summit.
													// 32 --> 64 : vertices for
													// lateral faces
				for (int i = 0; i < resolution; i++) {
					float uCoords = (float) (i % (resolution / 2)) / (float) (resolution / 2 - 1);
					if (i / (resolution / 2) == 0) {
						// we pass the half of the cone : we draw the texture
						// backward
						uCoords = 1 - uCoords;
					}
					final int idx = (i + resolution + 1) * 2;
					uvMapping[idx] = uCoords;
					uvMapping[idx + 1] = 0;
				}
				uvMapping[resolution * 2] = 0.5f;
				uvMapping[resolution * 2 + 1] = 1;
			} else if (type == IShape.Type.SPHERE) {
				resolution = BUILT_IN_SHAPE_RESOLUTION / 2;
				for (int i = 0; i < (resolution - 1) * (resolution - 1); i++) {
					uvMapping[i * 2] = (float) (i % resolution) / (float) (resolution - 1);
					uvMapping[i * 2 + 1] = (float) (i / resolution) / (float) (resolution - 1);
				}
			}
		}
	}

	protected GamaPoint getObjSize(final GeometryObject geomObj) {
		float minX = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		final Coordinate[] coordinates = geomObj.getGeometry().getCoordinates();
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].x < minX)
				minX = (float) coordinates[i].x;
			if (coordinates[i].x > maxX)
				maxX = (float) coordinates[i].x;
			if (coordinates[i].y < minY)
				minY = (float) coordinates[i].y;
			if (coordinates[i].y > maxY)
				maxY = (float) coordinates[i].y;
		}
		float XSize = (maxX - minX) / 2 == 0 ? 1 : (maxX - minX) / 2;
		float YSize = (maxY - minY) / 2 == 0 ? 1 : (maxY - minY) / 2;
		float ZSize = this.depth == 0 ? 1 : (float) this.depth;

		final Scaling3D attrSize = MoreObjects.firstNonNull(geomObj.getDimensions(), Scaling3D.of(1));

		if (isSphere()) {
			final float realSize = Math.max(YSize, XSize);
			XSize = YSize = ZSize = realSize;
		}

		return new GamaPoint(attrSize.getX() * XSize, attrSize.getY() * YSize, attrSize.getZ() * ZSize);
	}

	private void loadManyFacedShape(final GeometryObjectTransformer geomObj) {
		faces = geomObj.faces;
		coords = geomObj.coords;
		uvMapping = geomObj.uvMapping;
		normals = geomObj.normals;
		coordsForBorder = geomObj.coordsForBorder;
		idxForBorder = geomObj.idxForBorder;

		topFace = geomObj.topFace;
		bottomFace = geomObj.bottomFace;
	}

	private boolean isPyramid() {
		// a pyramid geometry is either a 3D cone or a pyramid (made with a
		// base, a summit and
		// generated lateral faces)
		if (type == IShape.Type.CONE && depth > 0 || type == IShape.Type.PYRAMID) { return true; }
		return false;
	}

	private boolean isSphere() {
		return type == IShape.Type.SPHERE;
	}

	private boolean is1DShape() {
		// a 1D shape is a line, polyline or point. It cannot have a border, and
		// it does not have faces
		if (type == IShape.Type.POINT || type == IShape.Type.LINEARRING
				|| type == IShape.Type.LINESTRING) { return true; }
		return false;
	}

	private boolean isPolyplan() {
		// a plan / polyplan are a bit particular : they are build out of a
		// line, and the depth attribute gives the height of the plan.
		if (type == IShape.Type.PLAN || type == IShape.Type.POLYPLAN) { return true; }
		return false;
	}

	private void build1DShape() {
		// we build the shape as if it was only a border
		coordsForBorder = new float[coordinates.length * 3];
		idxForBorder = new float[(coordinates.length - 1) * 2];
		// fill the coordinates array
		for (int i = 0; i < coordinates.length; i++) {
			coordsForBorder[3 * i] = (float) coordinates[i].x;
			coordsForBorder[3 * i + 1] = (float) coordinates[i].y;
			coordsForBorder[3 * i + 2] = (float) coordinates[i].z;
		}
		// fill the index buffer
		for (int i = 0; i < coordinates.length - 1; i++) {
			idxForBorder[2 * i] = i;
			idxForBorder[2 * i + 1] = i + 1;
		}
		// case when the shape is just a point :
		if (idxForBorder.length == 0) {
			idxForBorder = new float[] { 0 };
		}
	}

	private void buildBottomFace() {
		final float[] result = new float[coordinates.length * 3];
		final int[] newFace = new int[coordinates.length];
		int idx = 0;
		for (int i = coordinates.length - 1; i >= 0; i--) { // need to be
															// clockwise
			result[3 * i] = (float) coordinates[i].x;
			result[3 * i + 1] = (float) coordinates[i].y;
			result[3 * i + 2] = (float) coordinates[i].z;
			newFace[idx] = this.coords.length + i;
			idx++;
		}

		this.faces.add(newFace);
		bottomFace = newFace;
		this.coords = Utils.concatFloatArrays(this.coords, result);
	}

	private void buildTopFace() {
		final float[] result = new float[coordinates.length * 3];
		final int[] newFace = new int[coordinates.length];
		int idx = 0;
		for (int i = 0; i < coordinates.length; i++) { // need to be
														// anti-clockwise
			result[3 * i] = (float) coordinates[i].x;
			result[3 * i + 1] = (float) coordinates[i].y;
			result[3 * i + 2] = (float) coordinates[i].z + (float) depth;
			newFace[idx] = this.coords.length / 3 + i;
			idx++;
		}

		this.faces.add(newFace);
		topFace = newFace;
		this.coords = Utils.concatFloatArrays(this.coords, result);
	}

	private void buildPyramidSummit() {
		final float[] center = new float[2];
		final float[] coordSum = new float[2];
		// 1) compute the center of the base
		// sum the coordinates
		for (int i = 0; i < coordinates.length; i++) {
			coordSum[0] += (float) coordinates[i].x;
			coordSum[1] += (float) coordinates[i].y;
		}
		// divide by the number of vertices to get the center
		center[0] = coordSum[0] / coordinates.length;
		center[1] = coordSum[1] / coordinates.length;

		// 2) determine the coordinate of the summit
		final float[] summitCoordinates = new float[] { center[0], center[1], (float) depth };

		// 3) add this summit to the "coords", set the topFace.
		final int[] vtxIdxForSummit = new int[] { coordinates.length };
		topFace = vtxIdxForSummit;
		this.coords = Utils.concatFloatArrays(this.coords, summitCoordinates);
	}

	private void buildLateralFaces() {
		final ArrayList<int[]> facesToAdd = buildLateralFaces(topFace, bottomFace);
		for (final int[] face : facesToAdd) {
			faces.add(face);
		}
	}

	private ArrayList<int[]> buildLateralFaces(final int[] topFace, final int[] botFace) {
		final ArrayList<int[]> result = new ArrayList<int[]>();
		if (topFace.length == 1) {
			// case of pyramid : the topFace is just the summit
			for (int i = 0; i < botFace.length; i++) {
				final int[] newFace = new int[3];
				newFace[0] = topFace[0];
				newFace[1] = botFace[botFace.length - i - 1];
				if (i < botFace.length - 1)
					newFace[2] = botFace[botFace.length - i - 2];
				else
					newFace[2] = botFace[botFace.length - 1];
				result.add(newFace);
			}
		} else {
			for (int i = 0; i < topFace.length; i++) {
				final int[] newFace = new int[4];
				newFace[2] = topFace[i];
				newFace[3] = botFace[botFace.length - i - 1];
				if (i < topFace.length - 1)
					newFace[0] = botFace[botFace.length - i - 2];
				else
					newFace[0] = botFace[botFace.length - 1];
				if (i < topFace.length - 1)
					newFace[1] = topFace[i + 1];
				else
					newFace[1] = topFace[0];
				result.add(newFace);
			}
		}
		return result;
	}

	private ArrayList<int[]> buildSphereFaces(final int[] topFace, final int[] botFace) {
		final ArrayList<int[]> result = new ArrayList<int[]>();
		if (topFace.length == 1 || botFace.length == 1) {
			// case of the top and the bottom of the sphere
			if (topFace.length == 1) {
				for (int i = 0; i < botFace.length; i++) {
					final int[] newFace = new int[3];
					newFace[0] = topFace[0];
					newFace[1] = botFace[i];
					if (i < botFace.length - 1)
						newFace[2] = botFace[i + 1];
					else
						newFace[2] = botFace[0];
					result.add(newFace);
				}
			} else if (botFace.length == 1) {
				for (int i = 0; i < topFace.length; i++) {
					final int[] newFace = new int[3];
					newFace[2] = botFace[0];
					newFace[1] = topFace[i];
					if (i < topFace.length - 1)
						newFace[0] = topFace[i + 1];
					else
						newFace[0] = topFace[0];
					result.add(newFace);
				}
			}
		} else {
			for (int i = 0; i < topFace.length; i++) {
				final int[] newFace = new int[4];
				newFace[2] = topFace[i];
				newFace[3] = botFace[i];
				if (i < topFace.length - 1)
					newFace[0] = botFace[i + 1];
				else
					newFace[0] = botFace[0];
				if (i < topFace.length - 1)
					newFace[1] = topFace[i + 1];
				else
					newFace[1] = topFace[0];
				result.add(newFace);
			}
		}
		return result;
	}

	private void buildSphere() {

		// find the radius of the sphere
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].x < minX)
				minX = (float) coordinates[i].x;
			if (coordinates[i].x > maxX)
				maxX = (float) coordinates[i].x;
		}
		final float radius = (maxX - minX) / 2;

		// find the position of the center
		final float[] center = new float[2];
		final float[] coordSum = new float[2];
		// sum the coordinates
		for (int i = 0; i < coordinates.length; i++) {
			coordSum[0] += (float) coordinates[i].x;
			coordSum[1] += (float) coordinates[i].y;
		}
		// divide by the number of vertices to get the center
		center[0] = coordSum[0] / coordinates.length;
		center[1] = coordSum[1] / coordinates.length;

		// build a serie of circles on the z axis
		final int sliceNb = BUILT_IN_SHAPE_RESOLUTION / 2;
		final ArrayList<int[]> circles = new ArrayList<int[]>();
		int idx = 0;
		for (int i = 0; i < sliceNb; i++) {
			final float zVal = (float) Math.cos((float) i / (float) sliceNb * Math.PI) * radius;
			final float angle = (float) Math.asin(zVal / radius); // <--
																	// sin(angle)
																	// = zVal /
																	// radius
			final float circleRadius = (float) (radius * Math.cos(angle)); // <--
																			// cos(angle)
																			// =
																			// circleRadius
																			// *
																			// cos(angle)
			float[] circleCoordinates = buildCircle(new float[] { center[0], center[1], zVal }, circleRadius, sliceNb);
			// special case : the top and the bottom of the sphere
			if (i == 0 || i == sliceNb - 1) {
				circleCoordinates = new float[] { center[0], center[1], zVal };
			}
			// we add those coordinates to the array "coords"
			coords = Utils.concatFloatArrays(coords, circleCoordinates);
			// build the index array for this circle
			final int[] vertexIdxArray = new int[circleCoordinates.length / 3];
			for (int j = 0; j < circleCoordinates.length / 3; j++) {
				vertexIdxArray[j] = idx;
				idx++;
			}
			circles.add(vertexIdxArray);
		}

		// join all those circles
		for (int i = 0; i < circles.size() - 1; i++) {
			final ArrayList<int[]> faces = buildSphereFaces(circles.get(i + 1), circles.get(i));
			// add the faces create to the attribute "faces".
			for (final int[] face : faces) {
				this.faces.add(face);
			}
		}
	}

	private float[] buildCircle(final float[] center, final float radius, final int nbVertex) {
		// this is a utility method to build a sphere. It returns a list of
		// coordinates
		final float[] result = new float[nbVertex * 3];
		final float angle = (float) Math.toRadians(360 / nbVertex);
		// we build points starting with the vertex at 3 o'clock
		for (int i = 0; i < nbVertex; i++) {
			final float xTranslate = (float) (radius * Math.cos(i * angle));
			final float yTranslate = (float) (radius * Math.sin(i * angle));
			result[3 * i] = center[0] + xTranslate; // x composant
			result[3 * i + 1] = center[1] + yTranslate; // y composant
			result[3 * i + 2] = center[2]; // z composant
		}
		return result;
	}

	private void buildPolyplan() {
		// build the coordinates
		coords = new float[coordinates.length * 3 * 2]; // 3 components, twice
														// because one line at
														// z=0 and one line at
														// z=depth
		for (int i = 0; i < coordinates.length; i++) {
			coords[3 * i] = (float) coordinates[i].x;
			coords[3 * i + 1] = (float) coordinates[i].y;
			coords[3 * i + 2] = (float) coordinates[i].z;
			coords[coordinates.length * 3 + 3 * i] = (float) coordinates[i].x;
			coords[coordinates.length * 3 + 3 * i + 1] = (float) coordinates[i].y;
			coords[coordinates.length * 3 + 3 * i + 2] = (float) (coordinates[i].z + depth);
		}
		// build the faces
		for (int i = 0; i < coordinates.length - 1; i++) {
			final int[] face = new int[4];
			face[0] = i;
			face[1] = i + 1;
			face[2] = coordinates.length + i + 1;
			face[3] = coordinates.length + i;
			faces.add(face);
		}
	}

	@Override
	public ArrayList<DrawingEntity> getDrawingEntityList() {
		if (geometryCorrupted) { return new ArrayList<DrawingEntity>(); }
		// returns the DrawingEntities corresponding to this shape (can be 2
		// DrawingEntities
		// in case it has been asked to draw the border)
		ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();
		if (isTriangulation) {
			// if triangulate, returns only one result
			result = getTriangulationDrawingEntity();
		} else if (isWireframe) {
			// if wireframe, returns only one result
			result = getWireframeDrawingEntity();
		} else {
			// if not triangulate and not wireframe
			if (is1DShape()) {
				result = get1DDrawingEntity();
			} else {
				result = getStandardDrawingEntities();
			}
		}

		return result;
	}

}