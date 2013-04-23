/*
 * GLUtil version v1.04 date 20.08.2010
 * This is simple class which contains static methods is create to you
 * build simple OpenGL program in java whihout writing a lot wate code.
 * You dont have create an object of this class.
 * New methods:
 * ->drawVec(GL gl,Wector point,Wector direction)  - draw vector in specified location (point)
 * ->drawVec(GL gl,Wector direction)
 * ->drawCircle(GL gl,double size) - draw cirle with size radius on OXY plane in point 0,0,0
 * ->drawEmptyCircle(GL gl,double size) - draw dont filled cirle with size radius on OXY plane in point 0,0,0
 */
package utils;

import static javax.media.opengl.GL.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.GL.GL_COLOR_MATERIAL;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LIGHT1;
import static javax.media.opengl.GL.GL_POSITION;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.eclipse.swt.opengl.GLData;

import msi.gama.jogl.utils.MyGLToyDrawer;
import msi.gama.util.GamaColor;

public class GLUtil {

    public static final int fogMode[] = {GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR};

    
    private static float lightDiffusePosition[] = new float[4];
    /**
     *
     * @param gl
     * @param r red
     * @param g green
     * @param b blue
     * @param a-alfa s cooficient of transparency
     */
    public static void createAmbientLight(GL gl, float r, float g, float b, float a) {
        gl.glEnable(GL.GL_LIGHTING);
        float colors[] = {r, g, b, a};
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, colors, 0);
    }

    /**
     *
     * @param gl
     * @param colors
     * @param position
     * @param n_ofLight
     */
    public static void createDiffuseLight(GL gl, float colors[], float position[], int n_ofLight) {
        switch (n_ofLight) {
            case 0: {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT0);
            }
            break;
            case 1: {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT1);
            }
            break;
            case 2: {
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT2);
            }
            break;
            case 3: {
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT3);
            }
            break;
            case 4: {
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT4);
            }
            break;
            case 5: {
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT5);
            }
            break;
            case 6: {
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT6);
            }
            break;
            case 7: {
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT7);
            }
            break;
            default:
                System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
        }

    }//end of create Diffuse Light

    /**
     *Default color-white and position 100 in each direction
     * @param gl
     * @param colors
     * @param position
     * @param n_ofLight
     */
    public static void createDiffuseLight(GL gl, int n_ofLight, float pos) {
        float colors[] = {1, 1, 1, 1};
        float position[] = {pos, pos, pos, 1};
        
        switch (n_ofLight) {
            case 0: {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT0);
            }
            break;
            case 1: {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT1);
            }
            break;
            case 2: {
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT2);
            }
            break;
            case 3: {
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT3);
            }
            break;
            case 4: {
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT4);
            }
            break;
            case 5: {
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT5);
            }
            break;
            case 6: {
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT6);
            }
            break;
            case 7: {
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, colors, 0);
                gl.glEnable(GL.GL_LIGHT7);
            }
            break;
            default:
                System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
        }        
    }//end of create Diffuse Light
    
    
	public static void DrawColorTriangle(GL gl, float x, float y, float z, float alpha,float size) {
		// ----- Render a triangle -----
		gl.glTranslatef(x, y, z); // translate left and into the screen
		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glColor4f(1.0f, 0.0f, 0.0f,alpha); // Red
		gl.glVertex3f(0.0f, size, 0.0f);
		gl.glColor4f(0.0f, 1.0f, 0.0f,alpha); // Green
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glColor4f(0.0f, 0.0f, 1.0f,alpha); // Blue
		gl.glVertex3f(size, -size, 0.0f);
		gl.glEnd();
		gl.glTranslatef(-x, -y, -z); // retranslate right and into the screen
	}

    /**
     *
     * @param gl
     * @param isSoft if is false then surfaces wont flash
     * @param alfa - determines black-white-shade color of light R=G=B=alfa
     * @param size takse vaules from 0 to 128 and determines size of flash when soft body
     */
    public static void createSoftMaterial(GL gl, boolean isSoft, float alfa, int size) {
        if (isSoft) {
            float cooficientColor[] = {alfa, alfa, alfa, 1};
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, cooficientColor, 0);
            gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, size);

        } else {
            alfa = 0;
            size = 0;
            float cooficientColor[] = {alfa, alfa, alfa, 1};
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, cooficientColor, 0);
            gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, size);
        }
    }

    /**
     *
     * @param gl
     * @param disp_color  dispersion color
     * @param flash_color
     * @param position
     * @param direction
     * @param size a width of light
     * @param alfa a blank cooficient
     * @param n_ofLight
     */
    public static void createDirectionLight(GL gl, float disp_color[], float flash_color[], float position[], float direction[], float size, float alfa, int n_ofLight) {
        switch (n_ofLight) {
            case 0: {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT0);
            }
            break;
            case 1: {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT1);
            }
            break;
            case 2: {
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT2);
            }
            break;
            case 3: {
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT3);
            }
            break;
            case 4: {
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT4);
            }
            break;
            case 5: {
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT5);
            }
            break;
            case 6: {
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT6);
            }
            break;
            case 7: {
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT7);
            }
            break;
            default:
                System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
        }

    }//END LIGHT DIRECTION

    /**
     * A shorter version method which create DirectionalLight. Size of bundle light has 30,
     * alfa is 1 and disp&flash color are white
     * @param gl
     * @param position
     * @param direction
     * @param n_ofLight
     */
    public static void createDirectionLight(GL gl, float position[], float direction[], int n_ofLight) {
        float disp_color[] = {1, 1, 1, 1};
        float flash_color[] = {1, 1, 1, 1};
        float size = 30;
        float alfa = 1;
        switch (n_ofLight) {
            case 0: {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT0);
            }
            break;
            case 1: {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT1);
            }
            break;
            case 2: {
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT2);
            }
            break;
            case 3: {
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT3);
            }
            break;
            case 4: {
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT4);
            }
            break;
            case 5: {
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT5);
            }
            break;
            case 6: {
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT6);
            }
            break;
            case 7: {
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, disp_color, 0);
                gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPECULAR, flash_color, 0);


                gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPOT_DIRECTION, direction, 0);
                gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_CUTOFF, size);
                gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_EXPONENT, alfa);
                gl.glEnable(GL.GL_LIGHT7);
            }
            break;
            default:
                System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
        }

    }//END LIGHT DIRECTION

    /**
     *Creates fog on the scene.
     * @param gl - GL object
     * @param color - fog color
     * @param start - begining of the fog
     * @param end - end distance of the fog
     * @param mode - set type of fog param  GL_EXP (the default), GL_EXP2, or GL_LINEAR
     * @param fog_hint hint specifies whether fog calculations are done per pixel (GL_NICEST) or per vertex (GL_FASTEST).
     */
    public static void enableFog(GL gl, float color[], float start, float end, int mode, int fog_hint, float density) {
        gl.glEnable(GL.GL_FOG);
        gl.glFogfv(GL.GL_FOG_COLOR, color, 0);
        gl.glFogf(GL.GL_FOG_DENSITY, density);
        gl.glFogf(GL.GL_FOG_START, start);
        gl.glFogf(GL.GL_FOG_END, end);
        gl.glFogf(GL.GL_FOG_MODE, mode);
        gl.glHint(GL.GL_FOG_HINT, fog_hint);
    }

    public static void enableSmooth(GL gl) {
        gl.glShadeModel(GL.GL_SMOOTH);
    }

    public static void enableFlat(GL gl) {
        gl.glShadeModel(GL.GL_FLAT);
    }

    public static void enableBlend(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void enableColorMaterial(GL gl) {
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
    }

    public static void enableDepthTest(GL gl) {
    	// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
    }

    public static void enableLighting(GL gl) {
        gl.glEnable(GL.GL_LIGHTING);
    }

    public static void disableFog(GL gl) {
        gl.glDisable(GL.GL_FOG);
    }

    public static void disableLight(GL gl) {
        gl.glDisable(GL.GL_LIGHTING);
    }

    public static void disableBlend(GL gl) {
        gl.glDisable(GL.GL_BLEND);
    }

    public static void disableColorMaterial(GL gl) {
        gl.glDisable(GL.GL_COLOR_MATERIAL);
    }

    public static void disableDepthTest(GL gl) {
        gl.glDisable(GL.GL_DEPTH_TEST);
    }

    public static void disableSoftMaterial(GL gl) {
        float cooficientColor[] = {0, 0, 0, 1};
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, cooficientColor, 0);
        gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, 0);

    }
    
    public static void InitializeLighting(GL gl, GLU glu,float widthEnv,GamaColor ambiantLightValue){
    	
    	
		float[] lightAmbientValue = { (float)ambiantLightValue.getRed()/255, (float)ambiantLightValue.getGreen()/255, (float)ambiantLightValue.getBlue()/255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);
		
		// Diffuse light 0
		float[] lightDiffuseValue0 = { 0.0f, 0.0f, 0.0f, 1.0f };
		// Diffuse light location xyz (directed light)
		float lightDiffusePosition0[] = { -widthEnv, 0.5f*widthEnv, 0.5f*widthEnv, 0.0f };
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuseValue0, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL_POSITION, lightDiffusePosition0, 0);
		
		// Diffuse light 1
		float diffuseMean=1f;
		float[] lightDiffuseValue = { diffuseMean, diffuseMean, diffuseMean, 1.0f };
		// Diffuse light location xyz (positioned light)
		//float lightDiffusePosition[] = { 4.0f*widthEnv, 8.0f*widthEnv, widthEnv, 1.0f };
		
		lightDiffusePosition[0] =  4.0f*widthEnv;
		lightDiffusePosition[1] = 8.0f*widthEnv;
		lightDiffusePosition[2] = widthEnv;
		lightDiffusePosition[3] = 1.0f;
		
		/*lightDiffusePosition[0] = 0.0f;
		lightDiffusePosition[1] = 0.0f;
		lightDiffusePosition[2] = widthEnv/2;
		lightDiffusePosition[3] = 1.0f;*/

		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuseValue, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
		

		//Specular light 1
		float specularMean=0.1f;
        float[] lightSpecularValue = {specularMean, specularMean, specularMean, 1f};
		
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightSpecularValue, 0);
		
		//gl.glEnable(GL.GL_LIGHT0); // Enable Light-0
		gl.glEnable(GL.GL_LIGHT1); // Enable Light-1
		
		// enable color tracking
	    gl.glEnable(GL_COLOR_MATERIAL);
		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		
		float[] rgba = {0.2f, 0.2f, 0.2f,1f};
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);
    }
    
    public static void SetAmbiantLightFromValue(GL gl, GLU glu, Color c){
    	float[] lightAmbientValue = { (float)c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);
    }
    
    public static void UpdateAmbiantLight(GL gl, GLU glu, GamaColor ambiantLightValue){
    
    	float[] lightAmbientValue = { (float)ambiantLightValue.getRed()/255, (float)ambiantLightValue.getGreen()/255, (float)ambiantLightValue.getBlue()/255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);
    }

    public static void DrawLight(GL gl, GLU glu){
    	gl.glTranslated(lightDiffusePosition[0], -lightDiffusePosition[1], lightDiffusePosition[2]);
    	gl.glColor4f(1.0f, 1.0f,0.0f, 1.0f);
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
        final int slices = 16;
        final int stacks = 16;
        glu.gluSphere(quad, 1.0f, slices, stacks);
        glu.gluDeleteQuadric(quad);
        gl.glTranslated(-lightDiffusePosition[0], lightDiffusePosition[1], -lightDiffusePosition[2]);
    }
    
    public static void InitializeLighting2(GL gl){
   	 // Prepare light parameters.
       float SHINE_ALL_DIRECTIONS = 1;
       float[] lightPos = {0, 0, -10, SHINE_ALL_DIRECTIONS};
       float[] lightColorAmbient = {1f, 1f, 1f, 1f};
       float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

       // Set light parameters.
       gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPos, 0);
       gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightColorAmbient, 0);
       gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightColorSpecular, 0);

       // Enable lighting in GL.
       gl.glEnable(GL.GL_LIGHT1);
       gl.glEnable(GL.GL_LIGHTING);

       // Set material properties.
       float[] rgba = {1f, 1f, 1f};
       gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, rgba, 0);
       gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
       gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);
   }

    public static void setPointSize(GL gl, float size, boolean smooth) {
        gl.glPointSize(size);
        if (smooth) {
            gl.glEnable(GL.GL_POINT_SMOOTH);
        } else {
            gl.glDisable(GL.GL_POINT_SMOOTH);
        }
    }

    public static void setLineWidth(GL gl, float size, boolean smooth) {
        gl.glLineWidth(size);
        if (smooth) {
            gl.glEnable(GL.GL_LINE_SMOOTH);
        } else {
            gl.glDisable(GL.GL_LINE_SMOOTH);
        }
    }
    


    /**
     * Set the shade model: type == 1 equals GL_SMOOTH and type == 2 equals GL_FLAT
     * default model is SMOOTH.
     * @param gl
     * @param type - 1 or 2.
     */
    public static void setShadeMode(GL gl, int type) {
        switch (type) {
            case 1:
                gl.glShadeModel(GL.GL_SMOOTH);
                break;
            case 2:
                gl.glShadeModel(GL.GL_FLAT);
                break;
            default:
                gl.glShadeModel(GL.GL_SMOOTH);
        }

    }

    public static void drawVec(GL gl, Wector point, Wector direction) {
        GLU glu = new GLU();
        GLUquadric quadric = glu.gluNewQuadric();
        gl.glColor3f(1, 1, 1);
        gl.glPushMatrix();
        double theta = Math.asin(direction.z / direction.dlugosc()) * (180 / Math.PI);
        double phi = Math.acos(direction.x / direction.dlugosc()) * (180 / Math.PI);

        gl.glTranslated(point.x, point.y, point.z);
        if (Math.signum(direction.y) > 0) {
            gl.glRotated(phi - 180, 0, 0, 1);
        } else {
            gl.glRotated(180 - phi, 0, 0, 1);
        }

        gl.glRotated(theta - 90, 0, 1, 0);


        gl.glPushMatrix();
        gl.glTranslated(0, 0, direction.dlugosc());
        glu.gluCylinder(quadric, .2, 0, .5, 30, 30);
        gl.glPopMatrix();
        gl.glPushMatrix();
        glu.gluCylinder(quadric, .1, .1, direction.dlugosc(), 10, 10);
        gl.glPopMatrix();

        gl.glPopMatrix();

    }

    public static void drawVec(GL gl, Wector direction) {
        GLU glu = new GLU();
        GLUquadric quadric = glu.gluNewQuadric();
        //gl.glColor3f(1, 1, 1);
        gl.glPushMatrix();
        double theta = Math.asin(direction.z / direction.dlugosc()) * (180 / Math.PI);
        double phi = Math.acos(direction.x / direction.dlugosc()) * (180 / Math.PI);

        if (Math.signum(direction.y) > 0) {
            gl.glRotated(phi - 180, 0, 0, 1);
        } else {
            gl.glRotated(180 - phi, 0, 0, 1);
        }

        gl.glRotated(theta - 90, 0, 1, 0);

        gl.glPushMatrix();
        gl.glTranslated(0, 0, direction.dlugosc() / 100.0 + 1);
        glu.gluCylinder(quadric, .2, 0, .5, 30, 30);
        gl.glPopMatrix();
        gl.glPushMatrix();
        glu.gluCylinder(quadric, .1, .1, direction.dlugosc() / 100.0 + 1, 10, 10);
        gl.glPopMatrix();

        gl.glPopMatrix();
        //  System.out.println("VEC:"+direction+" Theta:"+theta+"  Phi:"+phi);
    }

    public static void drawCircle(GL gl, double size, int n_vertexs) {
        if (n_vertexs < 3) {
            n_vertexs = 3;
        }
        gl.glPushMatrix();
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex3d(0, 0, 0);
        double angle = (2 * Math.PI) / (n_vertexs);
        for (int i = 0; i < n_vertexs; i++) {
            gl.glVertex3d(size * Math.cos(i * angle), size * Math.sin(angle * i), 0);
        }
        gl.glVertex3d(size, 0, 0);
        gl.glEnd();
        gl.glPopMatrix();
    }

    public static void drawEmptyCircle(GL gl, double size, int n_vertexs) {
        if (n_vertexs < 3) {
            n_vertexs = 3;
        }
        gl.glPushMatrix();
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glNormal3d(0, 0, 1);

        double angle = (2 * Math.PI) / (n_vertexs);
        for (int i = 0; i < n_vertexs; i++) {
            gl.glVertex3d(size * Math.cos(i * angle), size * Math.sin(angle * i), 0);
        }

        gl.glEnd();
        gl.glPopMatrix();
    }
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
    ///TEXTURES////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

    public static void TenableTex2D(GL gl) {
        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    public static void TdisableTex2D(GL gl) {
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
    public static void TcreateTexture2Dmipmap(GL gl, int texIDs[], int texture[][], int texW, int texH,boolean gluMipMaps) {
        gl.glGenTextures(texIDs.length, texIDs, 0);
        for (int i = 0; i < texIDs.length; i++) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, texIDs[i]);

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE, GL.GL_MODULATE);

                if(!gluMipMaps){
                            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texW, texH,
                                    0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, IntBuffer.wrap(texture[i]));
                }else{
                            GLU glu = new GLU();
                            glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGBA, texW, texH, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, IntBuffer.wrap(texture[i]));
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
public static int TcreatTexture2DFromImage(GL gl,URL url,boolean gluMipMaps){
    BufferedImage image = null ;
    int texID[] = new int[1];
    try {
        image =ImageIO.read(url);
        } catch (Exception e) {
            System.err.println("Cannot load image " + url  + " . Mesage:" + e.getMessage());
            return -1;
    }

    int texW = image.getWidth();
    int texH = image.getHeight();

    int pixels[][]  = new int[1][texW*texH];

    for(int w=0;w<texW;w++){
        for(int h=0;h<texH;h++){
           pixels[0][texH*w+h] = image.getRGB(w, h);
        }
    }

    GLUtil.TcreateTexture2Dmipmap(gl,texID,pixels,texW,texH,gluMipMaps);

return texID[0];
}
}
