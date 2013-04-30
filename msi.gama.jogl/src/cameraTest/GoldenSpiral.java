package cameraTest;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import utils.Vector;

public class GoldenSpiral {
private int N;
private Vector node[];
private int listID = -1;
double r;
    public GoldenSpiral(int n,double R){
        N = n;
        r = R;
        node = new Vector[N];
        createsNodesWithGoldenSpiral(R);
    }

    private void createsNodesWithNormalSpiral(double R){
        double s = 3.6/Math.sqrt(N);
        double dz = 2.0/N;
        double lon = 0;
        double z = 1 - dz/2;
        for(int k=0;k<N;k++){
            double r = Math.sqrt(1-z*z);
            node[k] = new Vector(Math.cos(lon)*r*R,Math.sin(lon)*r*R,R*z);
            z = z - dz;
            lon = lon + s/r;
            //with lines
            //1. R*z
            //2. 3*z/Math.exp(z*z)*Math.cos(z)
        }
    }

    private void createsNodesWithGoldenSpiral(double R){
        double dlong = Math.PI*(3-Math.sqrt(5));
        double dz = 2.0/N;
        double lon = 0;
        double z = 1 - dz/2;
        for(int k=0;k<N;k++){
            double r = Math.sqrt(1-z*z);
            node[k] = new Vector(Math.cos(lon)*r*R,Math.sin(lon)*r*R,R*z);
            z = z - dz;
            lon = lon + dlong;
        }
    }

    public void draw(GL2 gl){
        
        if(listID == -1){
            createDisplayList(gl);            
        }
        gl.glCallList(listID);

    }

    private void createDisplayList(GL2 gl){
        GLU glu = new GLU();
        GLUquadric quadric = glu.gluNewQuadric();
        listID =  gl.glGenLists(1);
        gl.glNewList(listID, GL2.GL_COMPILE);
        gl.glPushMatrix();
            gl.glColor3d(0,0,0);
            glu.gluSphere(quadric, r+0.4, 30, 30);
        gl.glPopMatrix();
              for(int i=0;i<N;i++){
                if(Math.random()<1){
                    gl.glPushMatrix();
                    gl.glColor3d(Math.random()/2,Math.random()/2,Math.random()/4+.5);
                    gl.glTranslated(node[i].x, node[i].y, node[i].z);
                    glu.gluSphere(quadric, .5, 15, 15);
                    gl.glPopMatrix();
                }
              }
        gl.glEndList();

    }
}
