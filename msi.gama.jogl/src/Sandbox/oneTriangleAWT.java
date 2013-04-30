package Sandbox;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
//import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A minimal program that draws with JOGL in an AWT Frame.
 *
 * @author Wade Walker
 */
public class oneTriangleAWT {

    static {
        // setting this true causes window events not to get sent on Linux if you run from inside Eclipse
        //GLProfile.initSingleton( false );
    }

    public static void main( String [] args ) {
        //GLProfile glprofile = GLProfile.getDefault();
        //GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        //final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        
        final GLCanvas glcanvas = new GLCanvas(  );

        glcanvas.addGLEventListener( new GLEventListener() {
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                oneTriangle.setup( glautodrawable.getGL().getGL2(), width, height );
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {
            }
            
           // @Override
           // public void dispose( GLAutoDrawable glautodrawable ) {
            //}
            
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                oneTriangle.render( glautodrawable.getGL().getGL2(), glautodrawable.getWidth(), glautodrawable.getHeight() );
            }

			public void displayChanged(GLAutoDrawable arg0, boolean arg1,
					boolean arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dispose(GLAutoDrawable arg0) {
				// TODO Auto-generated method stub
				
			}
        });

        final Frame frame = new Frame( "One Triangle AWT" );
        frame.add( glcanvas );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                frame.remove( glcanvas );
                frame.dispose();
                System.exit( 0 );
            }
        });

        frame.setSize( 640, 480 );
        frame.setVisible( true );
    }
}

