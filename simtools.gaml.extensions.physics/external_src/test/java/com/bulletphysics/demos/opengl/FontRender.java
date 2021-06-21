/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.demos.opengl;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.Hashtable;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.ARBTextureCompression.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 *
 * @author jezek2
 */
public class FontRender {
	
	//private static final File cacheDir = new File("/path/to/font/cache/dir/");
	
	private FontRender() {
	}
	
	protected static class Glyph {
		int x,y,w,h;		
		int list = -1;
	}
	
	public static class GLFont {
		protected int texture;
		protected int width, height;
		protected Glyph[] glyphs = new Glyph[128-32];
		
		public GLFont() {
			for (int i=0; i<glyphs.length; i++) glyphs[i] = new Glyph();
		}
		
		public GLFont(InputStream in) throws IOException {
			this();
			load(in);
		}

		public void destroy() {
			glDeleteTextures(IntBuffer.wrap(new int[] { texture }));
		}
		
		protected void save(File f) throws IOException {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
			out.writeInt(width);
			out.writeInt(height);

			glPixelStorei(GL_PACK_ROW_LENGTH, 0);
			glPixelStorei(GL_PACK_ALIGNMENT, 1);
			glPixelStorei(GL_PACK_SKIP_ROWS, 0);
			glPixelStorei(GL_PACK_SKIP_PIXELS, 0);
			
			int size = width*height*4;
			ByteBuffer buf = BufferUtils.createByteBuffer(size);
			byte[] data = new byte[size];
			glBindTexture(GL_TEXTURE_2D, texture);
			glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)buf.position(0));
			buf.get(data);
			out.write(data);

			for (int i=0; i<glyphs.length; i++) {
				out.writeShort(glyphs[i].x);
				out.writeShort(glyphs[i].y);
				out.writeShort(glyphs[i].w);
				out.writeShort(glyphs[i].h);
			}
			
			out.close();
		}

		protected void load(File f) throws IOException {
			load(new FileInputStream(f));
		}
		
		protected void load(InputStream _in) throws IOException {
			DataInputStream in = new DataInputStream(_in);
			int w = in.readInt();
			int h = in.readInt();
			int size = w*h*4;
			
			glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
			glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
			
			ByteBuffer buf = BufferUtils.createByteBuffer(size);
			byte[] data = new byte[size];
			in.read(data);
			buf.put(data);

			final IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
			glGenTextures(intBuffer);
			texture = intBuffer.get(0);
			width = w;
			height = h;
			
			glBindTexture(GL_TEXTURE_2D, texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)buf.position(0));
			
			for (int i=0; i<glyphs.length; i++) {
				glyphs[i].x = in.readShort();
				glyphs[i].y = in.readShort();
				glyphs[i].w = in.readShort();
				glyphs[i].h = in.readShort();
			}
			
			in.close();
		}
	}
	
	private static String getFontFileName(String family, int size, boolean bold) {
		return family.replace(' ','_')+"_"+size+(bold? "_bold":"")+".fnt";
	}
	
	public static GLFont createFont(String family, int size, boolean bold, boolean antialiasing) throws IOException {
		GLFont gf = new GLFont();
		/*File f = new File(cacheDir, getFontFileName(family, size, bold));
		if (f.exists()) {
			gf.load(f);
			return gf;
		}*/
		
		BufferedImage img = renderFont(new Font(family, bold? Font.BOLD : Font.PLAIN, size), antialiasing, gf.glyphs);
		gf.texture = createTexture(img, false);
		gf.width = img.getWidth();
		gf.height = img.getHeight();
		//gf.save(f);
		return gf;
	}
	
	public static BufferedImage renderFont(Font font, boolean antialiasing, Glyph[] glyphs) {
		FontRenderContext frc = new FontRenderContext(null, antialiasing, false);
		
		int imgw = 256;
		if (font.getSize() >= 36) imgw <<= 1;
		if (font.getSize() >= 72) imgw <<= 1;
		
		//BufferedImage img = new BufferedImage(imgw, 1024, BufferedImage.TYPE_INT_ARGB);
		BufferedImage img = createImage(imgw, 1024, true);
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		if (antialiasing) {
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		
		g.setColor(Color.WHITE);
		g.setFont(font);
		
		int x=0, y=0,rowsize=0;
		for (int c=32; c<128; c++) {
			String s = ""+(char)c;
			Rectangle2D rect = font.getStringBounds(s, frc);
			LineMetrics lm = font.getLineMetrics(s, frc);
			int w = (int)rect.getWidth()+1;
			int h = (int)rect.getHeight()+2;

			if (x+w+2 > img.getWidth()) {
				x = 0;
				y += rowsize;
				rowsize = 0;
			}
			
			g.drawString(s, x+1, y+(int)lm.getAscent()+1);
			
			if (glyphs != null) {
				glyphs[c-32].x = x+1;
				glyphs[c-32].y = y+1;
				glyphs[c-32].w = w;
				glyphs[c-32].h = h;
			}
			
			w += 2;
			h += 2;
			
			x += w;
			rowsize = Math.max(rowsize, h);
		}
		
		y += rowsize;
		g.dispose();
		
		if (y < 128) img = img.getSubimage(0, 0, img.getWidth(), 128);
		else if (y < 256) img = img.getSubimage(0, 0, img.getWidth(), 256);
		else if (y < 512) img = img.getSubimage(0, 0, img.getWidth(), 512);
		
		return img;
	}
	
	private static void renderGlyph(GLFont font, Glyph g) {
		if (g.list != -1) {
			glCallList(g.list);
			return;
		}
		
		g.list = glGenLists(1);
		glNewList(g.list, GL_COMPILE);
		
		float tw = font.width;
		float th = font.height;
		
		int x=0, y=0;
		
		glBegin(GL_QUADS);
			glTexCoord2f((float)(g.x)/tw, (float)(g.y)/th);
			glVertex3f(x, y, 1);

			glTexCoord2f((float)(g.x+g.w-1)/tw, (float)(g.y)/th);
			glVertex3f(x+g.w-1, y, 1);

			glTexCoord2f((float)(g.x+g.w-1)/tw, (float)(g.y+g.h-1)/th);
			glVertex3f(x+g.w-1, y+g.h-1, 1);

			glTexCoord2f((float)(g.x)/tw, (float)(g.y+g.h-1)/th);
			glVertex3f(x, y+g.h-1, 1);
		glEnd();
		
		glEndList();
		glCallList(g.list);
	}

	public static void drawString(GLFont font, CharSequence s, int x, int y, float red, float green, float blue) {
		drawString(font, s, x, y, red, green, blue, 1);
	}
	
	public static void drawString(GLFont font, CharSequence s, int x, int y, float red, float green, float blue, float alpha) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glPushMatrix();
		glTranslatef(x, y, 0);
		
		glBindTexture(GL_TEXTURE_2D, font.texture);
		glEnable(GL_TEXTURE_2D);
		glColor4f(red, green, blue, alpha);
		//glColor4f(1, 1, 1, 1);
		for (int i=0, n=s.length(); i<n; i++) {
			char c = s.charAt(i);
			if (c < 32 || c > 128) c = '?';
			Glyph g = font.glyphs[c-32];
			renderGlyph(font, g);
			//x += g.w;
			//glTranslatef(g.w, 0, 0);
			glTranslatef(g.w-2, 0, 0);
		}
		glDisable(GL_TEXTURE_2D);
		
		glPopMatrix();

		glDisable(GL_BLEND);
	}
	
	private static ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8,8,8,0}, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
	private static ColorModel glColorModelAlpha = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8,8,8,8}, true, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
	
	private static int createTexture(BufferedImage img, boolean mipMap) {
		boolean USE_COMPRESSION = false;

		int[] id = new int[1];
		glGenTextures(IntBuffer.wrap(id));
		int tex = id[0];
		
		glBindTexture(GL_TEXTURE_2D, tex);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mipMap? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		byte[] data = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
		
		ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
		buf.order(ByteOrder.nativeOrder());
		buf.put(data, 0, data.length);
		buf.flip();
		
		boolean alpha = img.getColorModel().hasAlpha();
		
		//glTexImage2D(GL_TEXTURE_2D, 0, alpha? GL_RGBA:GL_RGB, img.getWidth(), img.getHeight(), 0, alpha? GL_RGBA:GL_RGB, GL_UNSIGNED_BYTE, buf);
		glTexImage2D(GL_TEXTURE_2D, 0, USE_COMPRESSION? (alpha? GL_COMPRESSED_RGBA:GL_COMPRESSED_RGB) : (alpha? GL_RGBA:GL_RGB), img.getWidth(), img.getHeight(), 0, alpha? GL_RGBA:GL_RGB, GL_UNSIGNED_BYTE, buf);
		if (mipMap) {
			gluBuild2DMipmaps(GL_TEXTURE_2D, USE_COMPRESSION? (alpha? GL_COMPRESSED_RGBA:GL_COMPRESSED_RGB) : (alpha? GL_RGBA:GL_RGB), img.getWidth(), img.getHeight(), alpha? GL_RGBA:GL_RGB, GL_UNSIGNED_BYTE, buf);
			//gluBuild2DMipmaps(GL_TEXTURE_2D, GL_COMPRESSED_RGB, img.getWidth(), img.getHeight(), GL_RGB, GL_UNSIGNED_BYTE, buf);
		}
		
		return tex;
	}
	
	private static BufferedImage createImage(int width, int height, boolean alpha) {
		if (alpha) {
			WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
			return new BufferedImage(glColorModelAlpha, raster, false, new Hashtable());
		}
		
		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 3, null);
		return new BufferedImage(glColorModel, raster, false, new Hashtable());
	}
	
}
