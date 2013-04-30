package Sandbox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

//import javax.media.opengl.GLProfile;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;

/**
 * A minimal program that draws with JOGL in an SWT Composite.
 *
 * @author Wade Walker
 */
public class oneTriangleSWT {

    static {
        // setting this true causes window events not to get sent on Linux if you run from inside Eclipse
       // GLProfile.initSingleton( false );
    }

    public static void main( String [] args ) {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.setText( "OneTriangle SWT" );
        shell.setLayout( new FillLayout() );
        shell.setSize( 640, 480 );

        final Composite composite = new Composite( shell, SWT.NONE );
        composite.setLayout( new FillLayout() );

        GLData gldata = new GLData();
        gldata.doubleBuffer = true;
        // need SWT.NO_BACKGROUND to prevent SWT from clearing the window
        // at the wrong times (we use glClear for this instead)
        final GLCanvas glcanvas = new GLCanvas( composite, SWT.NO_BACKGROUND, gldata );
        glcanvas.setCurrent();
        //GLProfile glprofile = GLProfile.getDefault();
        final GLContext glcontext = GLDrawableFactory.getFactory(null).createExternalGLContext();

        // fix the viewport when the user resizes the window
        glcanvas.addListener( SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                Rectangle rectangle = glcanvas.getClientArea();
                glcanvas.setCurrent();
                glcontext.makeCurrent();
                oneTriangle.setup( glcontext.getGL().getGL2(), rectangle.width, rectangle.height );
                glcontext.release();        
            }
        });

        // draw the triangle when the OS tells us that any part of the window needs drawing
        glcanvas.addPaintListener( new PaintListener() {
            public void paintControl( PaintEvent paintevent ) {
                Rectangle rectangle = glcanvas.getClientArea();
                glcanvas.setCurrent();
                glcontext.makeCurrent();
                oneTriangle.render(glcontext.getGL().getGL2(), rectangle.width, rectangle.height);
                glcanvas.swapBuffers();
                glcontext.release();        
            }
        });

        shell.open();

        while( !shell.isDisposed() ) {
            if( !display.readAndDispatch() )
                display.sleep();
        }

        glcanvas.dispose();
        display.dispose();
    }
}
