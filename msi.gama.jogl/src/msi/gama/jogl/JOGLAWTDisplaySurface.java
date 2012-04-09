/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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
import msi.gama.jogl.utils.Camera;
import msi.gama.jogl.utils.GLUtil;
import msi.gama.jogl.utils.MyListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.imageio.ImageIO;
import javax.swing.*;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.IDisplayLayer;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Files;
import msi.gaml.species.ISpecies;

import com.sun.opengl.util.FPSAnimator;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.gui.displays.*;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;


import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

public final class JOGLAWTDisplaySurface extends JPanel implements
		IDisplaySurface, GLEventListener {

	private boolean autosave = false;
	private String snapshotFileName;
	public static String snapshotFolder = "snapshots";
	protected IDisplayManager manager;
	boolean paused;
	private volatile boolean canBeUpdated = true;
	double widthHeightConstraint = 1.0;
	private PopupMenu agentsMenu = new PopupMenu();
	private IGraphics openGLGraphics;
	private Color bgColor = Color.black;
	protected double zoomIncrement = 0.1;
	protected double zoomFactor = 1.0 + zoomIncrement;
	protected BufferedImage buffImage;
	protected int bWidth, bHeight;
	Point origin = new Point(0, 0);
	protected Point mousePosition;
	Dimension previousPanelSize;
	protected boolean navigationImageEnabled = true;
	protected SWTNavigationPanel navigator;
	private final AffineTransform translation = new AffineTransform();
	private final Semaphore paintingNeeded = new Semaphore(1, true);
	private boolean synchronous = false;
	private ActionListener menuListener;
	private ActionListener focusListener;
	private final Thread animationThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				try {
					paintingNeeded.acquire();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				repaint();
				Toolkit.getDefaultToolkit().sync();
			}
		}
	});

	// OpenGL member
	private static final int REFRESH_FPS = 60; // Display refresh frames per
												// second
	private GLU glu;
	private GL gl;
	FPSAnimator animator;
	public boolean opengl = true;

	// Listener (KeyListener, MouseListener, MouseMotionListener,
	// MouseWheelListener)
	public MyListener myListener;

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
	public void initialize(final double env_width, final double env_height,
			final IDisplayOutput layerDisplayOutput) {

		// Initialize the user camera
		camera = new Camera();
		camera.InitParam();

		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		myListener = new MyListener(camera);
		canvas.addKeyListener(myListener);
		canvas.addMouseListener(myListener);
		canvas.addMouseMotionListener(myListener);
		canvas.addMouseWheelListener(myListener);
		canvas.setFocusable(true); // To receive key event
		canvas.requestFocusInWindow();


		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);

		int REFRESH_FPS = 60;
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);

		// /////
		outputChanged(env_width, env_height, layerDisplayOutput);
		setOpaque(true);
		setDoubleBuffered(false);
		setCursor(createCursor());
		agentsMenu = new PopupMenu();
		add(agentsMenu);

		animator.start();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if (buffImage == null) {
					//zoomFit();
					if (resizeImage(getWidth(), getHeight())) {
						centerImage();
					}
				} else {
					if (isFullImageInPanel()) {
						centerImage();
					} else if (isImageEdgeInPanel()) {
						scaleOrigin();
					} else {
						openGLGraphics.setClipping(getImageClipBounds());
					}
				}
				updateDisplay();
				previousPanelSize = getSize();
			}
		});

	}

	public void save(final IScope scope, final RenderedImage image) {
		try {
			Files.newFolder(scope, snapshotFolder);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + snapshotFolder);
			GAMA.reportError(e1);
			e1.printStackTrace();
			return;
		}
		String snapshotFile = scope
				.getSimulationScope()
				.getModel()
				.getRelativeFilePath(snapshotFolder + "/" + snapshotFileName,
						false);

		String file = snapshotFile + SimulationClock.getCycle() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = new GamaRuntimeException(ex);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = new GamaRuntimeException(ex);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(e);
			}
		}
	}

	@Override
	public void setPaused(final boolean flag) {
		paused = flag;
		updateDisplay();
	}

	@Override
	public IDisplayManager getManager() {
		return manager;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	protected Cursor createCursor() {
		Image im = new BufferedImage((int) SELECTION_SIZE + 4,
				(int) SELECTION_SIZE + 4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) im.getGraphics();
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(3.0f));
		g.draw(new Rectangle2D.Double(2, 2, SELECTION_SIZE, SELECTION_SIZE));
		g.dispose();
		Cursor c = getToolkit()
				.createCustomCursor(
						im,
						new Point((int) (SELECTION_SIZE / 2),
								(int) SELECTION_SIZE / 2), "CIRCLE");
		return c;
	}



//		FIXME: Move in MyListener
//		public void mouseClicked(final MouseEvent evt) {
//			if (evt.getClickCount() == 2) {
//				zoomFit();
//			} else if (evt.isControlDown() || evt.isMetaDown()
//					|| evt.isPopupTrigger()) {
//				selectAgents(evt.getX(), evt.getY());
//			}
//		}



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

	// Used when the image is resized.
	boolean isImageEdgeInPanel() {
		if (previousPanelSize == null) {
			return false;
		}

		return origin.x > 0 && origin.x < previousPanelSize.width
				|| origin.y > 0 && origin.y < previousPanelSize.height;
	}

	// Tests whether the image is displayed in its entirety in the panel.
	boolean isFullImageInPanel() {
		return origin.x >= 0 && origin.x + bWidth < getWidth() && origin.y >= 0
				&& origin.y + bHeight < getHeight();
	}

	@Override
	public void outputChanged(final double env_width, final double env_height,
			final IDisplayOutput output) {
		bgColor = output.getBackgroundColor();
		this.setBackground(bgColor);
		widthHeightConstraint = env_height / env_width;
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
		paintingNeeded.release();
	}

	@Override
	public void setBackgroundColor(final Color c) {
		bgColor = c;
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint)
				: vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint)
				: vheight;
		return dim;
	}

	private void selectAgents(final int x, final int y) {
		agentsMenu.removeAll();
		agentsMenu.setLabel("Layers");
		int xc = x - origin.x;
		int yc = y - origin.y;
		final List<IDisplay> displays = manager.getDisplays(xc, yc);
		for (IDisplay display : displays) {
			java.awt.Menu m = new java.awt.Menu(display.getName());
			Set<IAgent> agents = display.collectAgentsAt(xc, yc);
			if (!agents.isEmpty()) {
				m.addSeparator();

				for (IAgent agent : agents) {
					SelectedAgent sa = new SelectedAgent();
					sa.macro = agent;
					sa.buildMenuItems(m, display);
				}
			}
			agentsMenu.add(m);
		}
		agentsMenu.show(this, x, y);
	}

	public void updateDisplayGL(GL gl) {

	}

	@Override
	public void updateDisplay() {

		//Remove all the already existing entity in openGLGraphics and redraw the existing ones.
		((JOGLAWTDisplayGraphics) openGLGraphics).CleanGeometries();
		// FIXME: Why this busy indicator enable to show the open display???
		BusyIndicator.showWhile(Display.getCurrent(), openGLDisplayBlock);

		if (synchronous && !EventQueue.isDispatchThread()) {
			try {
				EventQueue.invokeAndWait(openGLDisplayBlock);
			} catch (InterruptedException e) {
				e.printStackTrace();
				// TODO Problme si un modle est relancŽ. Blocage.
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(openGLDisplayBlock);
		}
		if (ex[0] != null) {
			GAMA.reportError(ex[0]);
			ex[0] = null;
		}

	}

	private final GamaRuntimeException[] ex = new GamaRuntimeException[] { null };

	private final Runnable openGLDisplayBlock = new Runnable() {
		@Override
		public void run() {
			if (!canBeUpdated()) {
				return;
			}
			canBeUpdated(false);
			drawDisplaysWithoutRepaintingGL();
			paintingNeeded.release();
			canBeUpdated(true);
			Toolkit.getDefaultToolkit().sync();
		}
	};

	public void drawDisplaysWithoutRepaintingGL() {
		if (openGLGraphics == null) {
			return;
		}
		ex[0] = null;
		// For java2D
		// openGLGraphics.fill(bgColor, 1);
		manager.drawDisplaysOn(openGLGraphics);

	}

	protected final Rectangle getImageClipBounds() {
		int panelX1 = -origin.x;
		int panelY1 = -origin.y;
		int panelX2 = getWidth() - 1 + panelX1;
		int panelY2 = getHeight() - 1 + panelY1;
		if (panelX1 >= bWidth || panelX2 < 0 || panelY1 >= bHeight
				|| panelY2 < 0) {
			return null;
		}
		int x1 = panelX1 < 0 ? 0 : panelX1;
		int y1 = panelY1 < 0 ? 0 : panelY1;
		int x2 = panelX2 >= bWidth ? bWidth - 1 : panelX2;
		int y2 = panelY2 >= bHeight ? bHeight - 1 : panelY2;
		return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).drawRenderedImage(buffImage, translation);
		if (autosave) {
			snapshot();
		}
		redrawNavigator();
	}

	void redrawNavigator() {
		if (!navigationImageEnabled) {
			return;
		}
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				if (navigator == null || navigator.isDisposed()) {
					return;
				}
				navigator.redraw();
			}
		});
	}

	@Override
	public void dispose() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				removeAll();
			}
		});

		if (manager != null) {
			manager.dispose();
		}

	}

	@Override
	public BufferedImage getImage() {
		return buffImage;
	}

	@Override
	public boolean resizeImage(final int x, final int y) {
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
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

			// For java2D the constructor was call here
			// openGLGraphics = new JOGLAWTDisplayGraphics(buffImage, gl, glu);
			openGLGraphics.setDisplayDimensions(bWidth, bHeight);
			openGLGraphics.setGraphics((Graphics2D) newImage.getGraphics());
			openGLGraphics.setClipping(getImageClipBounds());
			redrawNavigator();
			canBeUpdated(true);
			return true;
		}
		canBeUpdated(true);
		return false;

	}

	@Override
	public void fireSelectionChanged(final Object entity) {
		GAMA.getExperiment().getOutputManager().selectionChanged(entity);
	}

	@Override
	public void zoomIn() {

		if (opengl) {
			// For 3D camera
			// this.cameraZPosition += 0.1;
			// this.cameraLZPosition += 0.1;
			// camera.moveForward(0.1);
			// camera.look(10);

			camera.setzPos(camera.getzPos() - 0.5);
			camera.setzLPos(camera.getzLPos() - 0.5);

		} else {
			mousePosition = new Point(origin.x + bWidth / 2, origin.y + bHeight
					/ 2);
			setZoom(1.0 + zoomIncrement, mousePosition);
		}

	}

	@Override
	public void zoomOut() {

		if (opengl) {

			// For 3D camera
			// this.cameraZPosition -= 0.1;
			// this.cameraLZPosition -= 0.1;
			// camera.moveForward(-0.1);
			// camera.look(10);

			camera.setzPos(camera.getzPos() + 0.5);
			camera.setzLPos(camera.getzLPos() + 0.5);

		} else {
			mousePosition = new Point(origin.x + bWidth / 2, origin.y + bHeight
					/ 2);
			;
			setZoom(1.0 - zoomIncrement, mousePosition);
		}

	}

	public void setZoom(final double factor, final Point c) {
		if (resizeImage((int) Math.round(bWidth * factor),
				(int) Math.round(bHeight * factor))) {
			int imagePX = c.x < origin.x ? 0
					: c.x >= bWidth + origin.x ? bWidth - 1 : c.x - origin.x;
			int imagePY = c.y < origin.y ? 0
					: c.y >= bHeight + origin.y ? bHeight - 1 : c.y - origin.y;
			zoomFactor = factor;
			setOrigin(c.x - (int) Math.round(imagePX * zoomFactor), c.y
					- (int) Math.round(imagePY * zoomFactor));
			updateDisplay();
		}
	}

	void scaleOrigin() {
		setOrigin(origin.x * getWidth() / previousPanelSize.width, origin.y
				* getHeight() / previousPanelSize.height);
		paintingNeeded.release();
	}

	void centerImage() {
		setOrigin((getWidth() - bWidth) / 2, (getHeight() - bHeight) / 2);
	}

	@Override
	public void zoomFit() {

		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		if (resizeImage(getWidth(), getHeight())) {
			centerImage();
			if (opengl) {// We don't need to call update display when calling
							// zoomfit.
				float scale_rate = ((JOGLAWTDisplayGraphics) openGLGraphics).scale_rate;
				camera.setxPos(((JOGLAWTDisplayGraphics) openGLGraphics).clipBounds
						.getCenterX() * scale_rate);
				camera.setxLPos(((JOGLAWTDisplayGraphics) openGLGraphics).clipBounds
						.getCenterX() * scale_rate);
				camera.setyPos(-((JOGLAWTDisplayGraphics) openGLGraphics).clipBounds
						.getCenterY() * scale_rate);
				camera.setyLPos(-((JOGLAWTDisplayGraphics) openGLGraphics).clipBounds
						.getCenterY() * scale_rate);
			} else {
				updateDisplay();
			}

		}
	}

	@Override
	public void focusOn(final IShape geometry, final IDisplay display) {
		Envelope env = geometry.getEnvelope();
		double minX = env.getMinX();
		double minY = env.getMinY();
		double maxX = env.getMaxX();
		double maxY = env.getMaxY();

		int leftX = display.getPosition().x
				+ (int) (display.getXScale() * minX + 0.5);
		int leftY = display.getPosition().y
				+ (int) (display.getYScale() * minY + 0.5);
		int rightX = display.getPosition().x
				+ (int) (display.getXScale() * maxX + 0.5);
		int rightY = display.getPosition().y
				+ (int) (display.getYScale() * maxY + 0.5);
		Rectangle envelop = new Rectangle(leftX + origin.x, leftY + origin.y,
				rightX - leftX, rightY - leftY);
		// / PFfff... Quel bordel !
		double xScale = (double) getWidth() / (rightX - leftX);
		double yScale = (double) getHeight() / (rightY - leftY);
		double zoomFactor = Math.min(xScale, yScale);
		if (bWidth * zoomFactor > MAX_SIZE) {
			zoomFactor = (double) MAX_SIZE / bWidth;
		}
		setZoom(zoomFactor,
				new Point((int) envelop.getCenterX(), (int) envelop
						.getCenterY()));
	}

	@Override
	public void canBeUpdated(final boolean canBeUpdated) {
		this.canBeUpdated = canBeUpdated;
	}

	@Override
	public boolean canBeUpdated() {
		return canBeUpdated && openGLGraphics != null
				&& openGLGraphics.isReady();
	}

	public void setNavigationImageEnabled(final boolean enabled) {
		navigationImageEnabled = enabled;
	}

	@Override
	public void setOrigin(final int x, final int y) {
		this.origin = new Point(x, y);
		translation.setToTranslation(origin.x, origin.y);
		openGLGraphics.setClipping(getImageClipBounds());
		redrawNavigator();
	}

	@Override
	public void setSynchronized(final boolean checked) {
		synchronous = checked;
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		if (openGLGraphics == null) {
			return;
		}
		openGLGraphics.setQualityRendering(quality);
		if (isPaused()) {
			updateDisplay();
		}
	}

	@Override
	public void setAutoSave(final boolean autosave) {
		this.autosave = autosave;
	}

	@Override
	public void setSnapshotFileName(final String file) {
		snapshotFileName = file;
	}

	@Override
	public void snapshot() {
		save(GAMA.getDefaultScope(), buffImage);
	}

	@Override
	public void setNavigator(final Object nav) {
		if (nav instanceof SWTNavigationPanel) {
			navigator = (SWTNavigationPanel) nav;
		}
	}

	@Override
	public int getImageWidth() {
		return bWidth;
	}

	@Override
	public int getImageHeight() {
		return bHeight;
	}

	@Override
	public int getOriginX() {
		return origin.x;
	}

	@Override
	public int getOriginY() {
		return origin.y;
	}

	// GLEventListener method.
	@Override
	public void display(GLAutoDrawable drawable) {

		//System.out.println("opengl display");
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

		// ((JOGLAWTDisplayGraphics) openGLGraphics).DrawBounds();
		((JOGLAWTDisplayGraphics) openGLGraphics).DrawMyGeometries();

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

		// Initialize the IGraphics (FIXME: Should we initialize it here??)
		openGLGraphics = new JOGLAWTDisplayGraphics(gl, glu);

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

		gl.glEnable(GL_BLEND);
		// gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
		// model-view matrix
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

}
