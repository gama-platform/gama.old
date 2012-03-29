package msi.gama.jogl;

import static javax.media.opengl.GL.GL_AMBIENT;
import static javax.media.opengl.GL.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_COLOR_MATERIAL;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_DIFFUSE;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LIGHT1;
import static javax.media.opengl.GL.GL_LIGHTING;
import static javax.media.opengl.GL.GL_MODELVIEW;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_POSITION;
import static javax.media.opengl.GL.GL_PROJECTION;
import static javax.media.opengl.GL.GL_SMOOTH;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;

import com.sun.opengl.util.FPSAnimator;

import msi.gama.common.interfaces.IDisplay;
import msi.gama.common.interfaces.IDisplayManager;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.displays.DisplayManager;
import msi.gama.jogl.JOGLAWTDisplaySurface.AgentMenuItem;
import msi.gama.jogl.JOGLAWTDisplaySurface.SelectedAgent;
import msi.gama.jogl.gis_3D.Camera;
import msi.gama.jogl.utils.GLUtil;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.IDisplayLayer;
import msi.gaml.compilation.ISymbol;
import msi.gaml.species.ISpecies;

public final class JOGLDisplaySurface extends JPanel implements
		IDisplaySurface, GLEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IGraphics openGLGraphics;
	protected IDisplayManager manager;
	protected BufferedImage buffImage;
	private ActionListener menuListener;
	private ActionListener focusListener;
	private PopupMenu agentsMenu = new PopupMenu();
	protected int bWidth, bHeight;

	// OpenGL member
	private static final int REFRESH_FPS = 60; // Display refresh frames per
												// second
	private GLU glu;
	private GL gl;
	FPSAnimator animator;

	private int width, height;
	// Camera
	private Camera camera;
	private float zoom = 10.0f;
	private float cameraXPosition = 0.0f;
	private float cameraYPosition = 0.0f;
	public float cameraZPosition = 10.0f;

	private float cameraLXPosition = cameraXPosition;
	private float cameraLYPosition = cameraYPosition;
	public float cameraLZPosition = cameraZPosition - zoom;

	@Override
	public void initialize(double env_width, double env_height, IDisplayOutput layerDisplayOutput) {
		// Initialize the user camera
		camera = new Camera();
		camera.InitParam();
		
		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(this);

		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);

		int REFRESH_FPS = 60;
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);
		
		animator.start();
		
		outputChanged(env_width, env_height, layerDisplayOutput);
		setOpaque(true);
		setDoubleBuffered(false);
		//setCursor(createCursor());
		agentsMenu = new PopupMenu();
		add(agentsMenu);

	}

	static class AgentMenuItem extends MenuItem {

		private final IAgent agent;
		private final IDisplay display;

		AgentMenuItem(final String name, final IAgent agent,
				final IDisplay display) {
			super(name);
			this.agent = agent;
			this.display = display;
		}

		IAgent getAgent() {
			return agent;
		}

		IDisplay getDisplay() {
			return display;
		}
	}

	public class SelectedAgent {

		IAgent macro;
		Map<ISpecies, List<SelectedAgent>> micros;

		void buildMenuItems(final Menu parentMenu, final IDisplay display) {
			Menu macroMenu = new Menu(macro.getName());
			parentMenu.add(macroMenu);

			MenuItem inspectItem = new AgentMenuItem("Inspect", macro, display);
			inspectItem.addActionListener(menuListener);
			macroMenu.add(inspectItem);

			MenuItem focusItem = new AgentMenuItem("Focus", macro, display);
			focusItem.addActionListener(focusListener);
			macroMenu.add(focusItem);

			if (micros != null && !micros.isEmpty()) {
				Menu microsMenu = new Menu("Micro agents");
				macroMenu.add(microsMenu);

				Menu microSpecMenu;
				for (ISpecies microSpec : micros.keySet()) {
					microSpecMenu = new Menu("Species " + microSpec.getName());
					microsMenu.add(microSpecMenu);

					for (SelectedAgent micro : micros.get(microSpec)) {
						micro.buildMenuItems(microSpecMenu, display);
					}
				}
			}
		}
	}

	@Override
	public BufferedImage getImage() {
		return buffImage;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] computeBoundsFrom(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean resizeImage(int width, int height) {
		canBeUpdated(false);
		int[] point = computeBoundsFrom(width, height);
		int imageWidth = point[0];
		int imageHeight = point[1];
		if (imageWidth <= MAX_SIZE && imageHeight <= MAX_SIZE) {
			BufferedImage newImage = ImageUtils.createCompatibleImage(
					imageWidth, imageHeight);
			bWidth = newImage.getWidth();
			bHeight = newImage.getHeight();
			if (buffImage != null) {
				newImage.getGraphics().drawImage(buffImage, 0, 0, bWidth,
						bHeight, null);
				buffImage.flush();
			}
			buffImage = newImage;
			if (openGLGraphics == null) {
				openGLGraphics = new JOGLAWTDisplayGraphics(buffImage, gl, glu);
			} else {
				openGLGraphics.setDisplayDimensions(bWidth, bHeight);
				openGLGraphics.setGraphics((Graphics2D) newImage.getGraphics());
			}
			canBeUpdated(true);
			return true;
		}
		canBeUpdated(true);
		return false;
	}

	
	
	
	
	@Override
	public void outputChanged(double env_width, double env_height,
			IDisplayOutput output) {

		//bgColor = output.getBackgroundColor();
		//this.setBackground(bgColor);
		//widthHeightConstraint = env_height / env_width;
		menuListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if (a != null) {
					fireSelectionChanged(a);
				}
			}

		};

		focusListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if (a != null) {
					focusOn(a.getGeometry(), source.getDisplay());
				}
			}

		};
		if (manager == null) {
			manager = new DisplayManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for (final ISymbol layer : layers) {
				// IDisplay d =
				manager.addDisplay(DisplayManager.createDisplay(
						(IDisplayLayer) layer, env_width, env_height,
						openGLGraphics));
				// d.initMenuItems(this);
			}

		} else {
			manager.updateEnvDimensions(env_width, env_height);
		}

	}

	@Override
	public void zoomIn() {
		this.cameraZPosition += 0.1;
		this.cameraLZPosition += 0.1;

		camera.moveForward(0.1);
		camera.look(10);

	}

	@Override
	public void zoomOut() {
		this.cameraZPosition -= 0.1;
		this.cameraLZPosition -= 0.1;

		camera.moveForward(-0.1);
		camera.look(10);

	}

	@Override
	public void zoomFit() {
		// TODO Auto-generated method stub

	}

	@Override
	public IDisplayManager getManager() {
		return manager;
	}

	@Override
	public void fireSelectionChanged(Object a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusOn(IShape geometry, IDisplay display) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canBeUpdated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void canBeUpdated(boolean ok) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackgroundColor(Color background) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaused(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setQualityRendering(boolean quality) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSynchronized(boolean checked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAutoSave(boolean autosave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSnapshotFileName(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void snapshot() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNavigator(Object swtNavigationPanel) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getImageWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getImageHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOrigin(int i, int j) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOriginX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOriginY() {
		// TODO Auto-generated method stub
		return 0;
	}

	// GLeventlistener overide method.
	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		// System.out.println("openGL display");
		// Get the OpenGL graphics context
		gl = drawable.getGL();

		width = drawable.getWidth();
		height = drawable.getHeight();

		// Clear the screen and the depth buffer
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport

		gl.glMatrixMode(GL.GL_PROJECTION);
		// Reset the view (x, y, z axes back to normal)
		gl.glLoadIdentity();

		gl.glEnable(GL_BLEND); // Turn Blending On
		gl.glDisable(GL_DEPTH_TEST); // Turn Depth Testing Off

		// handle lighting
		gl.glEnable(GL_LIGHTING);

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(),
				camera.getXLPos(), camera.getYLPos(), camera.getZLPos(), 0.0,
				1.0, 0.0);

		gl.glMatrixMode(GL.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix

		// enable color tracking
		gl.glEnable(GL_COLOR_MATERIAL);
		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

		//((JOGLAWTDisplayGraphics) openGLGraphics).DrawMyGeometries();
		this.DrawOpenGLHelloWorldShape();
		

	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		width = drawable.getWidth();
		height = drawable.getHeight();
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		// GL Utilities
		glu = new GLU();

		// Enable smooth shading, which blends colors nicely across a polygon,
		// and smoothes out lighting.
		GLUtil.enableSmooth(gl);
		// Set background color (in RGBA). Alpha of 0 for total transparency
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		gl.glShadeModel(GL_SMOOTH);
		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;

		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(),
				camera.getXLPos(), camera.getYLPos(), camera.getZLPos(), 0.0,
				1.0, 0.0);

		// Set up the lighting for Light-1
		// Ambient light does not come from a particular direction. Need some
		// ambient light to light up the scene. Ambient's value in RGBA
		float[] lightAmbientValue = { 0.5f, 0.5f, 0.5f, 1.0f };
		// Diffuse light comes from a particular location. Diffuse's value in
		// RGBA
		float[] lightDiffuseValue = { 1.0f, 1.0f, 1.0f, 1.0f };
		// Diffuse light location xyz (in front of the screen).
		float lightDiffusePosition[] = { 0.0f, 0.0f, 2.0f, 1.0f };

		gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
		gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
		gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
		gl.glEnable(GL_LIGHT1); // Enable Light-1
		gl.glDisable(GL_LIGHTING); // But disable lighting
		System.out.println("openGL init ok");

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// Get the OpenGL graphics context
		gl = drawable.getGL();

		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;

		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Enable the model view - any new transformations will affect the
		// model-view
		// matrix
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset

		// perspective view
		gl.glViewport(10, 10, width - 20, height - 20);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(),
				camera.getXLPos(), camera.getYLPos(), camera.getZLPos(), 0.0,
				1.0, 0.0);

	}
	
	public void DrawOpenGLHelloWorldShape() {

		float red = (float) (Math.random()) * 1;
		float green = (float) (Math.random()) * 1;
		float blue = (float) (Math.random()) * 1;

		gl.glColor3f(red, green, blue);
		// ----- Render a triangle -----
		gl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the
												// screen

		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glEnd();

		// ----- Render a quad -----

		// translate right, relative to the previous translation
		gl.glTranslatef(3.0f, 0.0f, 0.0f);

		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
	}

}
