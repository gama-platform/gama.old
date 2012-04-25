
package utils;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;


public class CameraV extends CameraN //implements MouseListener,MouseMotionListener,MouseWheelListener
{


private Wector mouseDR = new Wector();
GLCanvas can;

public CameraV(GLCanvas canvas){
          super(canvas);  
//        canvas.addMouseListener(this);
//        canvas.addMouseMotionListener(this);
//        canvas.addMouseWheelListener(this);
          can = canvas;
          setMouseSensitivity(2.0f);
          pos_of_camera = new Wector(0,0,-5);
}


    

    @Override
    public void mousePressed(MouseEvent e) {
            mouseXY.x = e.getX();
            mouseXY.y = e.getY();
            button = e.getButton();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(button==2){

            mouseDR = new Wector(e.getX()-mouseXY.x,e.getY()-mouseXY.y,0);
            mouseDR = mouseDR.wektorRazySkalar(1.0/mouseSensitivity);
            phi+=mouseDR.x*Math.signum(Math.cos(Math.toRadians(theta)));
            theta+=mouseDR.y;
            
            mouseXY.x = e.getX();
            mouseXY.y = e.getY();

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
            
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
         if(e.getWheelRotation()<0){
             pos_of_camera.z-=1;
        }else {
             pos_of_camera.z+=1;
        }
    }
    

    public void mouseReleased(MouseEvent e){}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}



    
public void updateCamera(){
    can.getGL().glLoadIdentity();
    can.getGL().glTranslated(pos_of_camera.x, pos_of_camera.y, pos_of_camera.z);

    can.getGL().glRotated(phi, 0, 1, 0);
    can.getGL().glRotated(theta, Math.cos(Math.toRadians(phi)), 0, Math.sin(Math.toRadians(phi)));

}


}
