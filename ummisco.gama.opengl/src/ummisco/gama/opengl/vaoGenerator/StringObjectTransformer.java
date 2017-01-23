/*********************************************************************************************
 *
 * 'StringObjectTransformer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaColor;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.modernOpenGL.Material;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.TextMeshData;
import ummisco.gama.opengl.scene.StringObject;

/*
 * This class is the intermediary class for the transformation from a GeometryObject to a (or some) DrawingElement(s).
 */

class StringObjectTransformer extends AbstractTransformer {

	protected float fontSize;
	protected boolean isBillboarding = false;

	private StringObjectTransformer(final StringObjectTransformer obj) {
		loadManyFacedShape(obj);
	}

	public StringObjectTransformer(final StringObject strObj, final int[] textureIds, final String[] texturePaths,
			final TextMeshData textMeshData, final boolean isOverlay, final boolean isTriangulation,
			final double layerAlpha) {
		// for StringObject
		genericInit(strObj, isOverlay, isTriangulation, layerAlpha);

		this.textureIDs = textureIds;
		// this.texturePaths = texturePaths;
		this.fontSize = strObj.getFont() != null ? 2 * strObj.getFont().getSize() : 2 * 18; // FIXME : need refactoring
																							// (already computed in
																							// DrawingEntityGenerator)
		this.isBillboarding = !strObj.iisInPerspective();
		this.isLightInteraction = false;
		this.type = Type.POLYGON;

		this.translation.y = -this.translation.y; // for a stringObject, the y is inverted (why ???)

		coords = textMeshData.getVertexPositions();
		uvMapping = textMeshData.getTextureCoords();
		// build the faces
		for (int i = 0; i < coords.length / (4 * 3); i++) {
			final int[] face = new int[4];
			face[0] = i * 4;
			face[1] = i * 4 + 1;
			face[2] = i * 4 + 2;
			face[3] = i * 4 + 3;
			faces.add(face);
		}

		computeNormals();
		triangulate();
		if (!this.isBillboarding)
			applyTransformation(); // FIXME : need refactoring
	}

	@Override
	public ArrayList<DrawingEntity> getDrawingEntityList() {
		return getStringDrawingEntities();
	}

	protected ArrayList<DrawingEntity> getStringDrawingEntities() {
		// the number of drawing entity is equal to 1
		final ArrayList<DrawingEntity> result = new ArrayList<DrawingEntity>();

		if (color == null) {
			color = new GamaColor(1.0, 1.0, 0, 1.0); // set the default color to yellow.
		}
		// configure the drawing entity for the filled faces
		final DrawingEntity filledEntity = new DrawingEntity();
		filledEntity.setVertices(coords);
		filledEntity.setNormals(normals);
		filledEntity.setIndices(getIdxBuffer());
		filledEntity.setColors(getColorArray(color, coords));
		filledEntity.setMaterial(
				new Material(this.material.getDamper(), this.material.getReflectivity(), isLightInteraction));
		filledEntity.setTextureID(textureIDs[0]);
		// filledEntity.setTexturePath(texturePaths[0]);
		filledEntity.setUvMapping(uvMapping);
		filledEntity.type = DrawingEntity.Type.STRING;
		filledEntity.setFontEdge((float) (0.5 / Math.sqrt(fontSize))); // the font edge is function of the size of the
																		// font
		filledEntity.setFontWidth(0.48f); // this value looks nice to fit with the "old" renderer.
		if (isBillboarding) {
			filledEntity.type = DrawingEntity.Type.BILLBOARDING;
			filledEntity.enableBillboarding();
			filledEntity
					.setTranslation(new Vector3f((float) translation.x, (float) translation.y, (float) translation.z));
		}

		result.add(filledEntity);

		return result;
	}

}