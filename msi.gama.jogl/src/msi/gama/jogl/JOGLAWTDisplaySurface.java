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
 * - Benoit Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.jogl;

import java.awt.*;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.AbstractAWTDisplaySurface;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ISymbol;
import msi.gaml.species.ISpecies;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.simple.SimpleFeatureCollection;
import collada.Output3D;
import com.vividsolutions.jts.geom.Envelope;

@display("opengl")
public final class JOGLAWTDisplaySurface extends AbstractAWTDisplaySurface implements IDisplaySurface.OpenGL {

	private static final long serialVersionUID = 1L;
	private PopupMenu agentsMenu = new PopupMenu();

	protected Point mousePosition;
	private ActionListener menuListener;
	private ActionListener focusListener;
	private boolean output3D = false;
	// Environment properties useful to set the camera position.

	// Use to toggle the 3D view.
	public boolean threeD = false; // true; //false;

	// Use to toggle the Picking mode
	public boolean picking = false;

	// Use to toggle the Arcball view
	public boolean arcball = false;

	// Use to toggle the selectRectangle tool
	public boolean selectRectangle = false;

	// Use to toggle the Triangulation view
	public boolean triangulation = false;

	// Use to toggle the SplitLayer view
	public boolean splitLayer = false;

	// Use to draw .shp file
	final String[] shapeFileName = new String[1];

	// private (return the renderer of the openGLGraphics)
	private JOGLAWTGLRenderer renderer;

	// private: the class of the Output3D manager
	Output3D output3DManager;

	public JOGLAWTDisplaySurface() {
		displayBlock = new Runnable() {

			// Remove all the already existing entity in openGLGraphics and redraw the existing ones.
			@Override
			public void run() {
				if ( !canBeUpdated() ) { return; }
				canBeUpdated(false);
				renderer.cleanGeometries();
				renderer.cleanImages();
				renderer.cleanStrings();
				renderer.setPolygonTriangulated(false);
				renderer.setTessellation(getOutput().getTesselation());
				renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
				renderer.setPolygonMode(getOutput().getPolygonMode());
				renderer.setCameraPosition(getOutput().getCameraPos());
				renderer.setCameraLookPosition(getOutput().getCameraLookPos());
				renderer.setCameraUpVector(getOutput().getCameraUpVector());

				if ( autosave ) {
					snapshot();
				}

				drawDisplaysWithoutRepainting();
				paintingNeeded.release();
				canBeUpdated(true);
				if ( output3D ) {
					output3DManager.updateOutput3D(renderer);
				}

				// Toolkit.getDefaultToolkit().sync();
			}
		};

	}

	@Override
	public void initialize(final double env_width, final double env_height, final IDisplayOutput out) {
		super.initialize(env_width, env_height, out);
		agentsMenu = new PopupMenu();
		add(agentsMenu);
		renderer = new JOGLAWTGLRenderer(this);
		renderer.setPolygonTriangulated(false);
		renderer.setTessellation(getOutput().getTesselation());
		renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
		renderer.setPolygonMode(getOutput().getPolygonMode());
		renderer.setCameraPosition(getOutput().getCameraPos());
		renderer.setCameraLookPosition(getOutput().getCameraLookPos());
		renderer.setCameraUpVector(getOutput().getCameraUpVector());

		add(renderer.canvas, BorderLayout.CENTER);
		// openGLGraphicsGLRender.animator.start();
		zoomFit();
		// new way
		// createIGraphics();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				// GuiUtils.debug("JOGLAWTDisplaySurface.componentResized: " + layerDisplayOutput.getId());
				// if ( buffImage == null ) {
				// // zoomFit();
				// if ( resizeImage(getWidth(), getHeight()) ) {
				// centerImage();
				// }
				// } else {
				// if ( isFullImageInPanel() ) {
				// centerImage();
				// } else if ( isImageEdgeInPanel() ) {
				// scaleOrigin();
				// }
				// }
				resizeImage(getWidth(), getHeight());
				initOutput3D(out.getOutput3D(), out.getOutput3DNbCycles());
				updateDisplay();
				previousPanelSize = getSize();
			}
		});
		renderer.animator.start();
	}

	@Override
	protected void createIGraphics() {
		if ( iGraphics == null ) {
			iGraphics = new JOGLAWTDisplayGraphics(this, renderer);
			// openGLGraphicsGLRender = g.getMyGLRender();
			// add(openGLGraphicsGLRender.canvas, BorderLayout.CENTER);
			// zoomFit();
		}
	}

	@Override
	public void setPaused(final boolean flag) {
		if ( flag == true ) {
			renderer.animator.stop();
		} else {
			renderer.animator.start();
		}
		super.setPaused(flag);
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

	@Override
	public void outputChanged(final double env_width, final double env_height, final IDisplayOutput output) {
		setBackgroundColor(output.getBackgroundColor());
		this.setBackground(getBgColor());
		setEnvWidth(env_width);
		setEnvHeight(env_height);
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
				manager.addLayer(LayerManager.createLayer((ILayerStatement) layer, env_width, env_height, iGraphics));
				// d.initMenuItems(this);
			}

		} else {
			manager.outputChanged();
		}
		paintingNeeded.release();
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	public void selectAgents(final int x, final int y, final IAgent agent, final int layerId) {

		agentsMenu.removeAll();
		agentsMenu.setLabel("Layers");

		java.awt.Menu m = new java.awt.Menu(manager.getItems().get(layerId).getName());
		SelectedAgent sa = new SelectedAgent();
		sa.macro = agent;
		sa.buildMenuItems(m, manager.getItems().get(layerId));

		agentsMenu.add(m);
		agentsMenu.show(this, renderer.myListener.mousePosition.x, renderer.myListener.mousePosition.y);

	}

	public void showAgentMonitor(final IAgent agent) {

	}

	@Override
	public void forceUpdateDisplay() {
		updateDisplay();
	}

	public void drawDisplaysWithoutRepainting() {
		if ( iGraphics == null ) { return; }
		ex[0] = null;
		manager.drawLayersOn(iGraphics);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// ((Graphics2D) g).drawRenderedImage(buffImage, translation);
		/*
		 * The autosave has been move in the penGLUpdateDisplayBlock
		 * if ( autosave ) {
		 * snapshot();
		 * }
		 */
		redrawNavigator();
	}

	@Override
	public void dispose() {
		GuiUtils.debug("JOGLAWTDisplaySurface.dispose: " + getOutputName());
		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// }
		// });

		if ( manager != null ) {
			manager.dispose();
		}
		if ( navigator == null || navigator.isDisposed() ) { return; }
		navigator.dispose();

	}

	@Override
	public BufferedImage getImage() {
		BufferedImage buffImage = renderer.getScreenShot();
		return buffImage;
	}

	@Override
	public void zoomIn() {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		if ( renderer.camera.zPos != 0 ) {
			incrementalZoomStep = (float) renderer.camera.zPos / 10;
		} else {
			incrementalZoomStep = 0.1f;
		}
		renderer.camera.zPos -= incrementalZoomStep;
		renderer.camera.zLPos -= incrementalZoomStep;
	}

	@Override
	public void zoomOut() {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		if ( renderer.camera.zPos != 0 ) {
			incrementalZoomStep = (float) renderer.camera.zPos / 10;
		} else {
			incrementalZoomStep = 0.1f;
		}
		renderer.camera.zPos += incrementalZoomStep;
		renderer.camera.zLPos += incrementalZoomStep;

	}

	// public void setZoom(final double factor, final Point c) {
	// if ( resizeImage((int) Math.round(bWidth * factor), (int) Math.round(bHeight * factor)) ) {
	// int imagePX = c.x < origin.x ? 0 : c.x >= bWidth + origin.x ? bWidth - 1 : c.x - origin.x;
	// int imagePY = c.y < origin.y ? 0 : c.y >= bHeight + origin.y ? bHeight - 1 : c.y - origin.y;
	// zoomFactor = factor;
	// setOrigin(c.x - (int) Math.round(imagePX * zoomFactor), c.y - (int) Math.round(imagePY * zoomFactor));
	// updateDisplay();
	// }
	// }

	@Override
	public void zoomFit() {
		if ( renderer != null ) {
			if ( threeD ) {
				renderer.camera.Initialize3DCamera(getEnvWidth(), getEnvHeight());
				if ( renderer.camera.isModelCentered ) {
					renderer.reset();
				}

			} else {
				renderer.camera.InitializeCamera(getEnvWidth(), getEnvHeight());
				if ( renderer.camera.isModelCentered ) {
					renderer.reset();
				}
			}
		}
	}

	@Override
	public void toggleView() {
		threeD = !threeD;
		zoomFit();
	}

	@Override
	public void togglePicking() {

		if ( picking == false ) {
			threeD = false;
			zoomFit();
		}
		// FIXME: need to change the status of the button
		if ( threeD == true ) {
			threeD = false;
		}
		picking = !picking;

	}

	@Override
	public void toggleArcball() {
		arcball = !arcball;
		/*
		 * if(Arcball == true){
		 * ((JOGLAWTDisplayGraphics)openGLGraphics).graphicsGLUtils.DrawArcBall();
		 * }
		 */
	}

	@Override
	public void toggleSelectRectangle() {
		selectRectangle = !selectRectangle;

		/*
		 * if(Arcball == true){
		 * ((JOGLAWTDisplayGraphics)openGLGraphics).graphicsGLUtils.DrawArcBall();
		 * }
		 */
	}

	@Override
	public void toggleTriangulation() {
		triangulation = !triangulation;
	}

	@Override
	public void toggleSplitLayer() {

		splitLayer = !splitLayer;
		int nbLayers = this.getManager().getItems().size();
		int i = 0;
		Iterator<ILayer> it = this.getManager().getItems().iterator();
		while (it.hasNext()) {
			ILayer curLayer = it.next();
			if ( splitLayer ) {// Split layer
				curLayer.setElevation((double) i / nbLayers);
			} else {// put all the layer at zero
				curLayer.setElevation(0.0);
			}
			i++;
		}
		this.updateDisplay();
	}

	/**
	 * Add a simple feature collection from a .Shp file.
	 */
	@Override
	public void addShapeFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						Shell shell = new Shell(Display.getDefault());
						FileDialog dialog = new FileDialog(shell, SWT.OPEN);

						dialog.setText("Browse for a .shp file");

						dialog.setFilterPath(System.getProperty(GAMA.getModel().getProjectPath()));

						dialog.setFilterExtensions(new String[] { "*.shp" });

						if ( dialog.open() != null ) {

							String path = dialog.getFilterPath();

							String[] names = dialog.getFileNames();

							for ( int i = 0; i < names.length; i++ ) {
								shapeFileName[i] = path + "/" + names[i];
								System.out.println(shapeFileName[i]);
							}

						}

						renderer.myShapeFileReader = new ShapeFileReader(shapeFileName[0]);
						SimpleFeatureCollection myCollection =
							renderer.myShapeFileReader
								.getFeatureCollectionFromShapeFile(renderer.myShapeFileReader.store);
						Color color =
							new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
								(int) (Math.random() * 255));
						renderer.addCollections(myCollection, color);
						// FIXME: Need to reinitialise th displaylist

					}
				});
			}
		}).start();

	}

	//
	// @Override
	// public IGraphics.OpenGL getIGraphics() {
	// return (IGraphics.OpenGL) super.getIGraphics();
	// }

	@Override
	public void focusOn(final IShape geometry, final ILayer display) {

		// this.openGLGraphicsGLRender.camera.PrintParam();

		Envelope env = geometry.getEnvelope();

		double xPos = geometry.getLocation().getX() - this.getEnvWidth() / 2;
		double yPos = -(geometry.getLocation().getY() - this.getEnvHeight() / 2);

		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the maxExtent of the shape?
		double zPos = env.maxExtent() * 2 + geometry.getLocation().getZ();
		double zLPos = -(env.maxExtent() * 2);

		this.renderer.camera.updatePosition(xPos, yPos, zPos);
		this.renderer.camera.lookPosition(xPos, yPos, zLPos);
	}

	@Override
	public void initOutput3D(final boolean yes, final ILocation output3DNbCycles) {
		output3D = yes;
		if ( output3D ) {
			output3DManager = new Output3D(output3DNbCycles, renderer);
			// (new Output3D()).to3DGLGEModel(((JOGLAWTDisplayGraphics) openGLGraphics).myJTSGeometries,
			// openGLGraphicsGLRender);
		}
	}

	@Override
	public void snapshot() {
		save(GAMA.getDefaultScope(), getImage());
	}

	@Override
	public synchronized void addMouseListener(final MouseListener e) {
		renderer.canvas.addMouseListener(e);
	}

	public Color getBgColor() {
		return bgColor;
	}

}
