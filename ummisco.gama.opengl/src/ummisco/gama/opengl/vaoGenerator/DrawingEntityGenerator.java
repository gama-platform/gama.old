package ummisco.gama.opengl.vaoGenerator;

import java.awt.Font;

import com.jogamp.opengl.util.texture.Texture;

import ummisco.gama.modernOpenGL.DrawingEntity;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.FontTextureCache;
import ummisco.gama.modernOpenGL.font.fontMeshCreator.TextMeshData;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.scene.StringObject;

/*
 * This class takes as input a geometry and a drawing attribute and returns a structure
 * readable by OpenGL, composed with vertex array.
 */

public class DrawingEntityGenerator {
	
	private Abstract3DRenderer renderer;
	private FontTextureCache fontTextCache;
	
	public DrawingEntityGenerator(Abstract3DRenderer renderer) {
		this.renderer = renderer;
		this.fontTextCache = new FontTextureCache();
	}
	
	public DrawingEntity[] GenerateDrawingEntities(AbstractObject object) {
		return GenerateDrawingEntities(object, true);
	}
	
	public DrawingEntity[] GenerateDrawingEntities(AbstractObject object, boolean computeTextureIds) {
		// if this function is called to create a simpleScene, we don't compute the texture IDs (the only thing that interest us in this case is the texture Path)
		DrawingEntity[] result = null;
		ManyFacedShape shape = null;
		if (object instanceof StringObject) {
			StringObject strObj = (StringObject)object;
			Font font = strObj.getFont();
			Texture[] textures = new Texture[1];
			String style = (font.isBold()) ? (font.isItalic()) ? " bold italic" : " bold" : (font.isItalic()) ? " italic" : "";
			textures[0] = fontTextCache.getFontTexture(font.getName() + style);
			TextMeshData textMeshData = fontTextCache.getTextMeshData(font.getName() + style, strObj.string, (int)renderer.getGlobalYRatioBetweenPixelsAndModelUnits(), font.getSize());
			String[] texturePaths = new String[1];
			texturePaths[0] = font.getName() + style;
			int[] textureIds = new int[1];
			textureIds[0] = textures[0].getTextureObject();
			shape = new ManyFacedShape(strObj,textureIds,texturePaths,textMeshData,renderer.data.isTriangulation());
		}
		else if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			
			String[] texturePaths = object.getTexturePaths(); // returns null if no texture for this entity
			int[] textureIDs = (texturePaths == null) ? null : new int[texturePaths.length];
			if (computeTextureIds && (texturePaths != null)) {
				Texture[] textures = object.getTextures(renderer.getContext(), renderer);
				for (int i = 0 ; i < textures.length ; i++) {
					textureIDs[i] = textures[i].getTextureObject();
				}
			}
			
			shape = new ManyFacedShape(geomObj,textureIDs,texturePaths,renderer.data.isTriangulation());	
		}
		result = shape.getDrawingEntities();
		return result;
	}

}
