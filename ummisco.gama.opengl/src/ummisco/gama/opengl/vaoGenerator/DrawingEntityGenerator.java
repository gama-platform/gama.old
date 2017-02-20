/*********************************************************************************************
 *
 * 'DrawingEntityGenerator.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.runtime.IScope;
import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.FontTextureCache;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.TextMeshData;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.scene.LayerObject;
import ummisco.gama.opengl.scene.StringObject;

/*
 * This class takes as input a geometry and a drawing attribute and returns a structure readable by OpenGL, composed
 * with vertex array.
 */

public class DrawingEntityGenerator {

	private final Abstract3DRenderer renderer;
	private final FontTextureCache fontTextCache;

	public DrawingEntityGenerator(final Abstract3DRenderer renderer) {
		this.renderer = renderer;
		this.fontTextCache = new FontTextureCache();
	}

	public DrawingEntity[] generateDrawingEntities(final IScope scope, final AbstractObject object,
			final LayerObject layer, final GL2 gl) {
		return generateDrawingEntities(scope, object, true, layer, gl);
	}

	private String getFontName(final StringObject strObj) {
		final Font font = strObj.getFont();
		if (font != null) { return font.getName(); }
		return "Helvetica";
	}

	private String getStyle(final StringObject strObj) {
		final Font font = strObj.getFont();
		if (font != null) { return font.isBold() ? font.isItalic() ? " bold italic" : " bold"
				: font.isItalic() ? " italic" : ""; }
		return "";
	}

	private int getFontSize(final StringObject strObj) {
		final Font font = strObj.getFont();
		if (font != null) { return 2 * font.getSize(); }
		return 2 * 18;
	}

	public DrawingEntity[] generateDrawingEntities(final IScope scope, final AbstractObject object,
			final boolean computeTextureIds, final LayerObject layer, final GL2 gl) {
		// if this function is called to create a simpleScene, we don't compute
		// the texture IDs (the only thing that interest us in this case is the
		// texture Path)
		DrawingEntity[] result = null;
		AbstractTransformer transformer = null;
		if (object instanceof StringObject) {
			final StringObject strObj = (StringObject) object;
			final Texture[] textures = new Texture[1];
			final String fontName = getFontName(strObj);
			final String style = getStyle(strObj);
			final int fontSize = getFontSize(strObj);
			textures[0] = fontTextCache.getFontTexture(fontName + style);
			float ratio = (float) (layer.isOverlay() ? 1
					: renderer.getGlobalYRatioBetweenPixelsAndModelUnits() / renderer.getZoomLevel());
			ratio = (float) (object.getDimensions() != null ? ratio / object.getDimensions().getX() : ratio);
			final TextMeshData textMeshData =
					fontTextCache.getTextMeshData(fontName + style, strObj.string, ratio, fontSize);
			final String[] texturePaths = new String[1];
			texturePaths[0] = fontName + style;
			final int[] textureIds = new int[1];
			textureIds[0] = textures[0].getTextureObject();
			transformer = new StringObjectTransformer(strObj, textureIds, texturePaths, textMeshData, layer.isOverlay(),
					renderer.data.isWireframe(), renderer.getOpenGLHelper().getCurrentObjectAlpha());
		} else if (object instanceof GeometryObject) {
			final GeometryObject geomObj = (GeometryObject) object;
			// final String[] texturePaths = geomObj.getTexturePaths(scope);
			// final int[] textureIDs = texturePaths == null ? null : new int[texturePaths.length];
			int[] ids = null;
			if (computeTextureIds) {
				ids = geomObj.getTexturesId(renderer.getOpenGLHelper());
			}
			transformer = new GeometryObjectTransformer(geomObj, ids, layer.isLightInteraction(), layer.isOverlay(),
					renderer.data.isWireframe(), renderer.getOpenGLHelper().getCurrentObjectAlpha());
		}
		if (transformer != null)
			result = transformer.getDrawingEntities();
		return result;
	}

}
