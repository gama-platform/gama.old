/*
 * GLUtil version v1.04 date 20.08.2010
 * This is simple class which contains static methods is create to you
 * build simple OpenGL program in java whihout writing a lot wate code.
 * You dont have create an object of this class.
 * New methods:
 * ->drawVec(GL gl,Wector point,Wector direction) - draw vector in specified location (point)
 * ->drawVec(GL gl,Wector direction)
 * ->drawCircle(GL gl,double size) - draw cirle with size radius on OXY plane in point 0,0,0
 * ->drawEmptyCircle(GL gl,double size) - draw dont filled cirle with size radius on OXY plane in point 0,0,0
 */
package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;

import com.vividsolutions.jts.awt.PointShapeFactory.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.metamodel.shape.GamaPoint;

public class GLUtil {

	public static final int fogMode[] = { GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR };

	

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /TEXTURES////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////

	public static void TenableTex2D(final GL gl) {
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	public static void TdisableTex2D(final GL gl) {
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	/**
	 * Create 2D textures array and add them to openGL buffor
	 * @param gl
	 * @param texIDs - reference to textures IDs
	 * @param texture[texture][colors] - containt textures colors
	 * @param texW - texture width
	 * @param texH - texture height
	 */
	public static void TcreateTexture2Dmipmap(final GL gl, final int texIDs[], final int texture[][], final int texW,
		final int texH, final boolean gluMipMaps) {
		gl.glGenTextures(texIDs.length, texIDs, 0);
		for ( int i = 0; i < texIDs.length; i++ ) {
			gl.glBindTexture(GL.GL_TEXTURE_2D, texIDs[i]);

			//gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			//gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE, GL.GL_MODULATE);

			if ( !gluMipMaps ) {
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texW, texH, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
					IntBuffer.wrap(texture[i]));
			} else {
				GLU glu = new GLU();
				glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGBA, texW, texH, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
					IntBuffer.wrap(texture[i]));
			}

		}
	}

	/**
	 * Create texture form image, image has to have dimensions which are pow od 2:
	 * @param gl
	 * @param url- image url
	 * @param gluMipMaps - if yes generate texture by gluBuild2DMipMaps method
	 * @return - texture ID
	 */
	public static int TcreatTexture2DFromImage(final GL gl, final URL url, final boolean gluMipMaps) {
		BufferedImage image = null;
		int texID[] = new int[1];
		try {
			image = ImageIO.read(url);
		} catch (Exception e) {
			System.err.println("Cannot load image " + url + " . Mesage:" + e.getMessage());
			return -1;
		}

		int texW = image.getWidth();
		int texH = image.getHeight();

		int pixels[][] = new int[1][texW * texH];

		for ( int w = 0; w < texW; w++ ) {
			for ( int h = 0; h < texH; h++ ) {
				pixels[0][texH * w + h] = image.getRGB(w, h);
			}
		}

		TcreateTexture2Dmipmap(gl, texID, pixels, texW, texH, gluMipMaps);

		return texID[0];
	}
	
	    // Calculate the normal, from three points on a surface
		public static double[] CalculateNormal(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
			// Step 1
			// build two vectors, one pointing from A to B, the other pointing from
			// A to C
			double[] vector1 = new double[3];
			double[] vector2 = new double[3];

			vector1[0] = pointB.x - pointA.x;
			vector2[0] = pointC.x - pointA.x;

			vector1[1] = pointB.y - pointA.y;
			vector2[1] = pointC.y - pointA.y;

			vector1[2] = pointB.z - pointA.z;
			vector2[2] = pointC.z - pointA.z;

			// Step 2
			// do the cross product of these two vectors to find the normal
			// of the surface

			double[] normal = new double[3];
			normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
			normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
			normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];

			// Step 3
			// "normalise" the normal (make sure it has length of one)

			double total = 0.0d;
			for ( int i = 0; i < 3; i++ ) {
				total += normal[i] * normal[i];
			}
			double length = Math.sqrt(total);

			for ( int i = 0; i < 3; i++ ) {
				normal[i] /= length;
			}

			// done
			return normal;
		}
		
		
		public static Vertex GetCenter(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
			Vertex center = new Vertex();
			center.x = (pointA.x + pointB.x + pointC.x)/3;
			center.y = (pointA.y + pointB.y + pointC.y)/3;
			center.z = (pointA.z + pointB.z + pointC.z)/3;
			return center;
		}
		
		public static Vertex GetCenter(Vertex[] vertices) {
			Vertex center = new Vertex();
			for (int i= 0; i< vertices.length;i++){
				center.x = center.x + vertices[i].x;
				center.y = center.y + vertices[i].y;
				center.z = center.z + vertices[i].z;
			}
			
			center.x = center.x/vertices.length;
			center.y = center.y/vertices.length;
			center.z = center.z/vertices.length;
			return center;
		}
		

		public static void HandleNormal(Vertex[] vertices, Color c, double alpha, int norm_dir, JOGLAWTGLRenderer renderer){
			
			
			double[] normalmean = new double[3];
			for (int i= 0; i< vertices.length-2;i++){
				double[] normal = GLUtil.CalculateNormal(vertices[i+2], vertices[i+1], vertices[i]);
				normalmean[0]= (normalmean[0] + normal[0]);
				normalmean[1]= (normalmean[1] + normal[1]);
				normalmean[2]= (normalmean[2] + normal[2]);
			}

			normalmean[0]= norm_dir*normalmean[0]/vertices.length;
			normalmean[1]= norm_dir*normalmean[1]/vertices.length;
			normalmean[2]= norm_dir*normalmean[2]/vertices.length;
			
			renderer.gl.glNormal3dv(normalmean, 0);
			
			normalmean[0]= (renderer.getMaxEnvDim()/20)*normalmean[0];
			normalmean[1]= (renderer.getMaxEnvDim()/20)*normalmean[1];
			normalmean[2]= (renderer.getMaxEnvDim()/20)*normalmean[2];

			if(renderer.getDrawNorm()){
				Vertex center = GLUtil.GetCenter(vertices);
				renderer.gl.glBegin(GL_LINES);
				renderer.gl.glColor3d(1.0, 0.0, 0.0);
				renderer.gl.glVertex3d(center.x, center.y, center.z);		   
				renderer.gl.glVertex3d(center.x + normalmean[0], center.y + normalmean[1], center.z + normalmean[2]);
				renderer.gl.glEnd();

				renderer.gl.glPointSize(2.0f);
				renderer.gl.glBegin(GL_POINTS);
				renderer.gl.glVertex3d(center.x + normalmean[0], center.y + normalmean[1], center.z + normalmean[2]);
				renderer.gl.glEnd();
				
				if(c != null){
					renderer.gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
							alpha * c.getAlpha() / 255);
				}
							
			}
			
	}


		
}
