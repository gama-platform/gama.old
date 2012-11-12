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
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.FileDialog;


import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.gui.views.SWTNavigationPanel;
import msi.gama.jogl.utils.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Files;
import msi.gaml.species.ISpecies;

import com.vividsolutions.jts.geom.Envelope;

public final class JOGLAWTDisplaySurface extends JPanel implements IDisplaySurface {

	private static final long serialVersionUID = 1L;
	private boolean autosave = false;
	private String snapshotFileName;
	public static String snapshotFolder = "snapshots";
	protected ILayerManager manager;
	boolean paused;
	private volatile boolean canBeUpdated = true;
	double widthHeightConstraint = 1.0;
	private PopupMenu agentsMenu = new PopupMenu();
	public IGraphics openGLGraphics;
	private Color bgColor = Color.black; // black
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

	// Environment properties useful to set the camera position.
	public float envWidth, envHeight;

	// Event Listener
	public MyListener myListener;

	public JOGLAWTGLRenderer myGLRender;

	// Use to toggle the 3D view.
	public boolean ThreeD = false;
	
	//Use to toggle the Picking mode
	public boolean Picking= false;
	
	//Use to toggle the Arcball view
	public boolean Arcball= false;
	
	//Use to draw .shp file
	final String[] shapeFileName = new String[1];
	


	@Override
	public void initialize(final double env_width, final double env_height,
		final IDisplayOutput layerDisplayOutput) {

		myGLRender = new JOGLAWTGLRenderer(this);

		envWidth = (float) env_width;
		envHeight = (float) env_height;

		System.out.println("DisplaySurface Init. env_width:" + env_width + " env_height:" + env_height);

		this.setLayout(new BorderLayout());
		this.add(myGLRender.canvas, BorderLayout.CENTER);

		// /////
		outputChanged(env_width, env_height, layerDisplayOutput);
		setOpaque(true);
		setDoubleBuffered(false);
		setCursor(createCursor());
		agentsMenu = new PopupMenu();
		add(agentsMenu);

		myGLRender.animator.start();
		zoomFit();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if ( buffImage == null ) {
					// zoomFit();
					if ( resizeImage(getWidth(), getHeight()) ) {
						centerImage();
					}
				} else {
					if ( isFullImageInPanel() ) {
						centerImage();
					} else if ( isImageEdgeInPanel() ) {
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
		String snapshotFile =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(snapshotFolder + "/" + snapshotFileName, false);

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
				if ( os != null ) {
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
		
		if(flag == true){
	      myGLRender.animator.stop();	
		}
		else{
		  myGLRender.animator.start();	
		}
		paused = flag;
		updateDisplay();
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	protected Cursor createCursor() {
		Image im =
			new BufferedImage((int) SELECTION_SIZE + 4, (int) SELECTION_SIZE + 4,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) im.getGraphics();
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(3.0f));
		g.draw(new Rectangle2D.Double(2, 2, SELECTION_SIZE, SELECTION_SIZE));
		g.dispose();
		Cursor c =
			getToolkit().createCustomCursor(im,
				new Point((int) (SELECTION_SIZE / 2), (int) SELECTION_SIZE / 2), "CIRCLE");
		return c;
	}

	static class AgentMenuItem extends MenuItem {

		private final IAgent agent;
		private final ILayer display;

		AgentMenuItem(final String name, final IAgent agent, final ILayer display) {
			super(name);
			this.agent = agent;
			this.display = display;
		}

		IAgent getAgent() {
			return agent;
		}

		ILayer getDisplay() {
			return display;
		}
	}

	public class SelectedAgent {

		IAgent macro;
		Map<ISpecies, List<SelectedAgent>> micros;

		void buildMenuItems(final Menu parentMenu, final ILayer display) {
			Menu macroMenu = new Menu(macro.getName());
			parentMenu.add(macroMenu);

			MenuItem inspectItem = new AgentMenuItem("Inspect", macro, display);
			inspectItem.addActionListener(menuListener);
			macroMenu.add(inspectItem);

			MenuItem focusItem = new AgentMenuItem("Focus", macro, display);
			focusItem.addActionListener(focusListener);
			macroMenu.add(focusItem);

			if ( micros != null && !micros.isEmpty() ) {
				Menu microsMenu = new Menu("Micro agents");
				macroMenu.add(microsMenu);

				Menu microSpecMenu;
				for ( ISpecies microSpec : micros.keySet() ) {
					microSpecMenu = new Menu("Species " + microSpec.getName());
					microsMenu.add(microSpecMenu);

					for ( SelectedAgent micro : micros.get(microSpec) ) {
						micro.buildMenuItems(microSpecMenu, display);
					}
				}
			}
		}
	}

	// Used when the image is resized.
	boolean isImageEdgeInPanel() {
		if ( previousPanelSize == null ) { return false; }

		return origin.x > 0 && origin.x < previousPanelSize.width || origin.y > 0 &&
			origin.y < previousPanelSize.height;
	}

	// Tests whether the image is displayed in its entirety in the panel.
	boolean isFullImageInPanel() {
		return origin.x >= 0 && origin.x + bWidth < getWidth() && origin.y >= 0 &&
			origin.y + bHeight < getHeight();
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
				if ( a != null ) {
					fireSelectionChanged(a);
				}
			}

		};

		focusListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					focusOn(a.getGeometry(), source.getDisplay());
				}
			}

		};

		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				// IDisplay d =
				manager.addLayer(LayerManager.createDisplay((ILayerStatement) layer, env_width,
					env_height, openGLGraphics));
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
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	private void selectAgents(final int x, final int y) {
		agentsMenu.removeAll();
		agentsMenu.setLabel("Layers");
		int xc = x - origin.x;
		int yc = y - origin.y;
		final List<ILayer> displays = manager.getLayersIntersecting(xc, yc);
		for ( ILayer display : displays ) {
			java.awt.Menu m = new java.awt.Menu(display.getName());
			Set<IAgent> agents = display.collectAgentsAt(xc, yc);
			if ( !agents.isEmpty() ) {
				m.addSeparator();

				for ( IAgent agent : agents ) {
					SelectedAgent sa = new SelectedAgent();
					sa.macro = agent;
					sa.buildMenuItems(m, display);
				}
			}
			agentsMenu.add(m);
		}
		agentsMenu.show(this, x, y);
	}

	@Override
	public void updateDisplay() {

		if ( synchronous && !EventQueue.isDispatchThread() ) {
			try {
				EventQueue.invokeAndWait(openGLUpdateDisplayBlock);
			} catch (InterruptedException e) {
				e.printStackTrace();
				// TODO Problme si un modle est relancŽ. Blocage.
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(openGLUpdateDisplayBlock);
		}
		if ( ex[0] != null ) {
			GAMA.reportError(ex[0]);
			ex[0] = null;
		}

	}

	@Override
	public void forceUpdateDisplay() {
		updateDisplay();
	}

	private final GamaRuntimeException[] ex = new GamaRuntimeException[] { null };

	private final Runnable openGLUpdateDisplayBlock = new Runnable() {

		// Remove all the already existing entity in openGLGraphics and redraw the existing ones.
		@Override
		public void run() {
			if ( !canBeUpdated() ) { return; }
			canBeUpdated(false);
			((JOGLAWTDisplayGraphics) openGLGraphics).CleanGeometries();
			((JOGLAWTDisplayGraphics) openGLGraphics).CleanImages();
			((JOGLAWTDisplayGraphics) openGLGraphics).CleanStrings();
			((JOGLAWTDisplayGraphics) openGLGraphics).isPolygonTriangulated = false;
			/*boolean snapshot = true;
			if(snapshot == true){
				snapshot();
			}*/
			drawDisplaysWithoutRepaintingGL();
			paintingNeeded.release();
			canBeUpdated(true);
			Toolkit.getDefaultToolkit().sync();
		}
	};

	public void drawDisplaysWithoutRepaintingGL() {
		if ( openGLGraphics == null ) { return; }
		ex[0] = null;
		manager.drawLayersOn(openGLGraphics);

	}

	protected final Rectangle getImageClipBounds() {
		int panelX1 = -origin.x;
		int panelY1 = -origin.y;
		int panelX2 = getWidth() - 1 + panelX1;
		int panelY2 = getHeight() - 1 + panelY1;
		if ( panelX1 >= bWidth || panelX2 < 0 || panelY1 >= bHeight || panelY2 < 0 ) { return null; }
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
		if ( autosave ) {
			snapshot();
		}
		redrawNavigator();
	}

	void redrawNavigator() {
		if ( !navigationImageEnabled ) { return; }
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				if ( navigator == null || navigator.isDisposed() ) { return; }
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

		if ( manager != null ) {
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
		if ( imageWidth <= MAX_SIZE && imageHeight <= MAX_SIZE ) {
			BufferedImage newImage = ImageUtils.createCompatibleImage(imageWidth, imageHeight);
			bWidth = newImage.getWidth();
			bHeight = newImage.getHeight();
			if ( buffImage != null ) {
				newImage.getGraphics().drawImage(buffImage, 0, 0, bWidth, bHeight, null);
				buffImage.flush();
			}
			buffImage = newImage;

			// For java2D the constructor was call here
			// openGLGraphics = new JOGLAWTDisplayGraphics(buffImage, gl, glu);
			if ( openGLGraphics != null ) {
				openGLGraphics.setDisplayDimensions(bWidth, bHeight);
				openGLGraphics.setGraphics((Graphics2D) newImage.getGraphics());
				openGLGraphics.setClipping(getImageClipBounds());
			}
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
		float incrementalZoomStep = (float) myGLRender.camera.zPos / 10;
		myGLRender.camera.zPos -= incrementalZoomStep;
		myGLRender.camera.zLPos -= incrementalZoomStep;
	}

	@Override
	public void zoomOut() {
		float incrementalZoomStep = (float) myGLRender.camera.zPos / 10;
		myGLRender.camera.zPos += incrementalZoomStep;
		myGLRender.camera.zLPos += incrementalZoomStep;
	}

	public void setZoom(final double factor, final Point c) {
		if ( resizeImage((int) Math.round(bWidth * factor), (int) Math.round(bHeight * factor)) ) {
			int imagePX =
				c.x < origin.x ? 0 : c.x >= bWidth + origin.x ? bWidth - 1 : c.x - origin.x;
			int imagePY =
				c.y < origin.y ? 0 : c.y >= bHeight + origin.y ? bHeight - 1 : c.y - origin.y;
			zoomFactor = factor;
			setOrigin(c.x - (int) Math.round(imagePX * zoomFactor),
				c.y - (int) Math.round(imagePY * zoomFactor));
			updateDisplay();
		}
	}

	void scaleOrigin() {
		setOrigin(origin.x * getWidth() / previousPanelSize.width, origin.y * getHeight() /
			previousPanelSize.height);
		paintingNeeded.release();
	}

	void centerImage() {
		setOrigin((getWidth() - bWidth) / 2, (getHeight() - bHeight) / 2);
	}

	@Override
	public void zoomFit() {
		if ( ThreeD ) {
			myGLRender.camera.Initialize3DCamera(envWidth, envHeight);
			if ( myGLRender.camera.isModelCentered ) {
				myGLRender.reset();
			}

		} else {
			myGLRender.camera.InitializeCamera(envWidth, envHeight);
			if ( myGLRender.camera.isModelCentered ) {
				myGLRender.reset();
			}
		}

	}

	@Override
	public void toggleView() {
		ThreeD = !ThreeD;
		zoomFit();
	}
	
	@Override
	public void togglePicking() {
		
       if(Picking==false){
		    ThreeD=false;
			zoomFit();
		}
		//FIXME: need to change the status of the button
		if (ThreeD==true){
			ThreeD=false;
		}
		Picking = !Picking;
		
	}
	
	@Override
	public void toggleArcball() {
		Arcball = !Arcball;
	/*	if(Arcball == true){
		((JOGLAWTDisplayGraphics)openGLGraphics).graphicsGLUtils.DrawArcBall();
		}*/
	}
	
	/**
	 * Add a simpel feature collection  from a .Shp file.
	 */
	@Override
	public void addShapeFile() {
		new Thread(new Runnable() {
		      public void run() {
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() {
		            	   
		            	   Shell shell = new Shell(Display.getDefault());
		            	   FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		            	   dialog.setText("Browse for a .shp file");

		            	   dialog.setFilterPath(System.getProperty(GAMA.getModel().getProjectPath()));
		            	   
		            	   dialog.setFilterExtensions(new String[] {"*.shp"});

		            	   if (dialog.open() != null) {

		            	       String path = dialog.getFilterPath();

		            	       String[] names = dialog.getFileNames();

		            	       for (int i = 0; i < names.length; i++) {
		            	           shapeFileName[i]=path + "/" + names[i]; 
		            	           System.out.println(shapeFileName[i]);
		            	       }

		            	   }
		            	   
		            	   myGLRender.myShapeFileReader = new ShapeFileReader(shapeFileName[0]);
		                   SimpleFeatureCollection myCollection =  myGLRender.myShapeFileReader.getFeatureCollectionFromShapeFile(myGLRender.myShapeFileReader.store);
		                   Color color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int) (Math.random()*255));
		                   ((JOGLAWTDisplayGraphics) openGLGraphics).AddCollectionInCollections(myCollection, color);
		                   //FIXME: Need to reinitialise th displaylist
		                   
		               }
		            });
		      }
		   }).start();
		
	}

	@Override
	public void focusOn(final IShape geometry, final ILayer display) {
		Envelope env = geometry.getEnvelope();
		double minX = env.getMinX();
		double minY = env.getMinY();
		double maxX = env.getMaxX();
		double maxY = env.getMaxY();

		int leftX = display.getPosition().x + (int) (display.getXScale() * minX + 0.5);
		int leftY = display.getPosition().y + (int) (display.getYScale() * minY + 0.5);
		int rightX = display.getPosition().x + (int) (display.getXScale() * maxX + 0.5);
		int rightY = display.getPosition().y + (int) (display.getYScale() * maxY + 0.5);
		Rectangle envelop =
			new Rectangle(leftX + origin.x, leftY + origin.y, rightX - leftX, rightY - leftY);
		// / PFfff... Quel bordel !
		double xScale = (double) getWidth() / (rightX - leftX);
		double yScale = (double) getHeight() / (rightY - leftY);
		double zoomFactor = Math.min(xScale, yScale);
		if ( bWidth * zoomFactor > MAX_SIZE ) {
			zoomFactor = (double) MAX_SIZE / bWidth;
		}
		setZoom(zoomFactor, new Point((int) envelop.getCenterX(), (int) envelop.getCenterY()));
	}

	@Override
	public void canBeUpdated(final boolean canBeUpdated) {
		this.canBeUpdated = canBeUpdated;
	}

	@Override
	public boolean canBeUpdated() {
		return canBeUpdated && openGLGraphics != null && openGLGraphics.isReady();
	}

	public void setNavigationImageEnabled(final boolean enabled) {
		navigationImageEnabled = enabled;
	}

	@Override
	public void setOrigin(final int x, final int y) {
		this.origin = new Point(x, y);
		translation.setToTranslation(origin.x, origin.y);
		if ( openGLGraphics != null ) {
			openGLGraphics.setClipping(getImageClipBounds());
		}

		redrawNavigator();
	}

	@Override
	public void setSynchronized(final boolean checked) {
		synchronous = checked;
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		if ( openGLGraphics == null ) { return; }
		openGLGraphics.setQualityRendering(quality);
		if ( isPaused() ) {
			updateDisplay();
		}
	}

	@Override
	public void setAutoSave(final boolean autosave, final int x, final int y) {
		this.autosave = autosave;
	}

	@Override
	public void setSnapshotFileName(final String file) {
		snapshotFileName = file;
	}

	@Override
	public void snapshot() {	
		buffImage =  this.myGLRender.getScreenShot();
		save(GAMA.getDefaultScope(), buffImage);
	}

	@Override
	public void setNavigator(final Object nav) {
		if ( nav instanceof SWTNavigationPanel ) {
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

	@Override
	public int[] getHighlightColor() {
		if ( openGLGraphics == null ) { return null; }
		return openGLGraphics.getHighlightColor();
	}

	@Override
	public void setHighlightColor(final int[] rgb) {
		if ( openGLGraphics != null ) {
			openGLGraphics.setHighlightColor(rgb);
		}
	}

	

}
