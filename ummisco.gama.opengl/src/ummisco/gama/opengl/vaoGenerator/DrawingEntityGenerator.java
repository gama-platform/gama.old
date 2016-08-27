package ummisco.gama.opengl.vaoGenerator;

import java.awt.Font;

import com.jogamp.opengl.util.texture.Texture;

import msi.gaml.statements.draw.TextDrawingAttributes;
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
		DrawingEntity[] result = null;
		Texture[] textures = object.getTextures(renderer.getContext(), renderer);
		ManyFacedShape shape = null;
		if (object instanceof StringObject) {
			StringObject strObj = (StringObject)object;
			Font font = strObj.getFont();
			textures = new Texture[1];
//			((TextDrawingAttributes)strObj.getAttributes()).perspective;
			String style = (font.isBold()) ? (font.isItalic()) ? " bold italic" : " bold" : (font.isItalic()) ? " italic" : "";
			textures[0] = fontTextCache.getFontTexture(font.getName() + style);
			TextMeshData textMeshData = fontTextCache.getTextMeshData(font.getName() + style, strObj.string, (int)renderer.getGlobalYRatioBetweenPixelsAndModelUnits(), font.getSize());
			shape = new ManyFacedShape(strObj,textures,textMeshData,renderer.data.isTriangulation());
		}
		else if (object instanceof GeometryObject) {
			GeometryObject geomObj = (GeometryObject)object;
			shape = new ManyFacedShape(geomObj,textures,renderer.data.isTriangulation());	
		}
		result = shape.getDrawingEntities();
		return result;
	}

}
