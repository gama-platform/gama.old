package msi.gama.jogl.utils;

import org.eclipse.swt.graphics.Rectangle;

public class Refresher implements Runnable {

	 public static final int DELAY = 30;
	 private JOGLSWTGLRenderer renderer;
		     
		     public Refresher(JOGLSWTGLRenderer render) {
		         this.renderer = render;
		     }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		         if (this.renderer != null) {
				        Rectangle rectangle = renderer.canvas.getClientArea();
				        renderer.canvas.setCurrent();
				        renderer.context.makeCurrent();
				        renderer.render(renderer.context.getGL(), rectangle.width, rectangle.height);
				        renderer.canvas.swapBuffers();
				        renderer.context.release(); 
				        this.renderer.displaySurface.getDisplay().timerExec(DELAY, this);
		         }
		}
	}
