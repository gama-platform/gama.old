package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.gui.swt.swing.OutputSynchronizer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.*;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Files;

public abstract class AbstractAWTDisplaySurface extends JPanel implements IDisplaySurface {

	private IDisplayOutput output;
	protected Color highlightColor = GamaPreferences.CORE_HIGHLIGHT.getValue();
	protected IGraphics iGraphics;
	protected String snapshotFileName;
	protected static String snapshotFolder = "snapshots";
	protected ILayerManager manager;
	protected boolean paused;
	protected volatile boolean canBeUpdated = true;
	protected Point origin = new Point(0, 0);
	protected Dimension previousPanelSize;
	private int displayWidth;
	private int displayHeight;
	protected boolean autosave = false;
	protected double widthHeightConstraint = 1.0;
	protected double zoomIncrement = 0.1;
	protected Double zoomLevel = null;
	protected boolean zoomFit = true;
	protected boolean navigationImageEnabled = true;
	protected final AffineTransform translation = new AffineTransform();
	protected boolean synchronous = false;
	protected boolean qualityRendering = GamaPreferences.CORE_ANTIALIAS.getValue();
	protected Color bgColor = Color.black;
	protected Runnable displayBlock;
	private double envWidth;
	private double envHeight;
	private IZoomListener zoomListener;
	protected DisplaySurfaceMenu menuManager;
	protected static final int MAX_ZOOM_FACTOR = 2;

	protected AbstractAWTDisplaySurface(final Object ... args) {}

	// / EXPERIMENTAL

	protected IExpression temp_focus;

	//
	// @Override
	// public void focusOn(final IExpression expr) {
	// temp_focus = expr;
	// }

	// / EXPERIMENTAL

	@Override
	public void initialize(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		setOutput(output);
		setName(output.getName());
		setOpaque(true);
		setDoubleBuffered(false);
		this.setLayout(new BorderLayout());
		outputChanged(env_width, env_height, output);

	}

	@Override
	public void outputChanged(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		setEnvWidth(env_width);
		setEnvHeight(env_height);
		widthHeightConstraint = env_height / env_width;
		if ( iGraphics != null ) {
			iGraphics.initFor(this);
		}
		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<AbstractLayerStatement> layers = output.getLayers();
			for ( final AbstractLayerStatement layer : layers ) {
				manager.addLayer(LayerManager.createLayer(layer));
			}
		} else {
			manager.outputChanged();
		}
		temp_focus = output.getFacet(IKeyword.FOCUS);
	}

	// FIXME Ugly code. The hack must be better written
	public void setSWTMenuManager(final Object manager) {
		menuManager = (DisplaySurfaceMenu) manager;
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	public Point getOrigin() {
		return origin;
	}

	@Override
	public void setFont(final Font f) {
		// super.setFont(null);
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void setPaused(final boolean flag) {
		paused = flag;
		if ( !paused ) {
			updateDisplay();
		}
	}

	public String getOutputName() {
		return getOutput().getName();
	}

	protected void setOutput(final IDisplayOutput output) {
		this.output = output;
	}

	@Override
	public void setBackgroundColor(final Color c) {
		bgColor = c;
	}

	@Override
	public void snapshot() {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				save(scope, getImage());
			}
		});

	}

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public final void save(final IScope scope, final RenderedImage image) {
		if ( image == null ) { return; }
		try {
			Files.newFolder(scope, snapshotFolder);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + snapshotFolder);
			GAMA.reportError(e1, false);
			e1.printStackTrace();
			return;
		}
		String snapshotFile =
			scope.getSimulationScope().getModel().getRelativeFilePath(snapshotFolder + "/" + snapshotFileName, false);

		String file =
			snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_" +
				scope.getClock().getCycle() + "_time_" + java.lang.System.currentTimeMillis() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = GamaRuntimeException.create(ex);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = GamaRuntimeException.create(ex);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(e, false);
			}
		}
	}

	@Override
	public void removeNotify() {
		// GuiUtils.debug("AWTDisplaySurface.removeNotify: BEGUN" + outputName);
		super.removeNotify();
		OutputSynchronizer.decClosingViews(getOutputName());
		// GuiUtils.debug("AWTDisplaySurface.removeNotify: FINISHED" + outputName);
	}

	protected void scaleOrigin() {
		setOrigin(origin.x * getWidth() / previousPanelSize.width, origin.y * getHeight() / previousPanelSize.height);
		repaint();
	}

	protected void centerImage() {
		setOrigin((getWidth() - getDisplayWidth()) / 2, (getHeight() - getDisplayHeight()) / 2);
	}

	@Override
	public int getImageWidth() {
		return getDisplayWidth();
	}

	@Override
	public int getImageHeight() {
		return getDisplayHeight();
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
		return new int[] { highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue() };
	}

	@Override
	public void setHighlightColor(final int[] rgb) {
		highlightColor = new Color(rgb[0], rgb[1], rgb[2]);
		iGraphics.setHighlightColor(rgb);
	}

	@Override
	public void setSnapshotFileName(final String file) {
		snapshotFileName = file;
	}

	@Override
	public BufferedImage getImage() {
		Robot screenRobot;
		try {
			screenRobot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}
		if ( isDisplayable() && isShowing() ) {
			Rectangle rectangle = new Rectangle(this.getLocationOnScreen(), this.getSize());
			final BufferedImage buffImage = screenRobot.createScreenCapture(rectangle);
			return buffImage;
		}
		return null;
		// = renderer.getScreenShot();

	}

	@Override
	public void setQualityRendering(final boolean quality) {
		qualityRendering = quality;
		if ( iGraphics == null ) { return; }
		iGraphics.setQualityRendering(quality);
		if ( isPaused() || GAMA.isPaused() ) {
			updateDisplay();
		}
	}

	@Override
	public boolean getQualityRendering() {
		return qualityRendering;
	}

	@Override
	public void setSynchronized(final boolean checked) {
		synchronous = checked;
	}

	@Override
	public boolean isSynchronized() {
		return synchronous;
	}

	@Override
	public void setAutoSave(final boolean autosave, final int x, final int y) {
		this.autosave = autosave;
	}

	@Override
	public void canBeUpdated(final boolean canBeUpdated) {
		this.canBeUpdated = canBeUpdated;
	}

	@Override
	public boolean canBeUpdated() {
		return canBeUpdated && iGraphics != null;
	}

	public void setNavigationImageEnabled(final boolean enabled) {
		navigationImageEnabled = enabled;
	}

	@Override
	public void setOrigin(final int x, final int y) {
		this.origin = new Point(x, y);
		translation.setToTranslation(origin.x, origin.y);
		// redrawNavigator();
	}

	// protected void redrawNavigator() {
	// if ( !navigationImageEnabled ) { return; }
	// GuiUtils.run(new Runnable() {
	//
	// @Override
	// public void run() {
	// if ( navigator == null || navigator.isDisposed() ) { return; }
	// navigator.redraw();
	// }
	// });
	// }

	private final Runnable displayRunnable = new Runnable() {

		@Override
		public void run() {
			displayBlock.run();
			canBeUpdated(true);
		}
	};

	protected void runDisplay(final boolean sync) {
		canBeUpdated(false);
		if ( sync ) {
			try {
				EventQueue.invokeAndWait(displayRunnable);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(displayRunnable);
		}
	}

	@Override
	public void updateDisplay() {

		if ( !canBeUpdated() ) { return; }
		if ( GAMA.isPaused() || EventQueue.isDispatchThread() ) {
			runDisplay(false);
			return;
		}
		runDisplay(synchronous);

	}

	// Used when the image is resized.
	public boolean isImageEdgeInPanel() {
		if ( previousPanelSize == null ) { return false; }
		return origin.x > 0 && origin.x < previousPanelSize.width || origin.y > 0 &&
			origin.y < previousPanelSize.height;
	}

	// Tests whether the image is displayed in its entirety in the panel.
	public boolean isFullImageInPanel() {
		return origin.x >= 0 && origin.x + getDisplayWidth() < getWidth() && origin.y >= 0 &&
			origin.y + getDisplayHeight() < getHeight();
	}

	@Override
	public boolean resizeImage(final int x, final int y) {
		// GuiUtils.debug("AbstractAWTDisplaySurface.resizeImage " + x + " " + y + " can be update : " + canBeUpdated);
		if ( x == displayWidth && y == displayHeight ) { return true; }
		if ( getWidth() <= 0 && getHeight() <= 0 ) { return false; }
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
		int imageWidth = Math.max(1, point[0]);
		int imageHeight = Math.max(1, point[1]);
		createNewImage(imageWidth, imageHeight);
		createIGraphics();
		canBeUpdated(true);
		return true;

	}

	protected void createNewImage(final int width, final int height) {
		setDisplayHeight(height);
		setDisplayWidth(width);

	}

	protected abstract void createIGraphics();

	protected void setIGraphics(final IGraphics iGraphics) {
		this.iGraphics = iGraphics;
	}

	@Override
	public double getEnvWidth() {
		return envWidth;
	}

	public void setEnvWidth(final double envWidth) {
		this.envWidth = envWidth;
	}

	@Override
	public double getEnvHeight() {
		return envHeight;
	}

	public void setEnvHeight(final double envHeight) {
		this.envHeight = envHeight;
	}

	@Override
	public int getDisplayWidth() {
		return displayWidth;
	}

	protected void setDisplayWidth(final int displayWidth) {
		this.displayWidth = displayWidth;
	}

	@Override
	public int getDisplayHeight() {
		return displayHeight;
	}

	protected void setDisplayHeight(final int displayHeight) {
		this.displayHeight = displayHeight;
	}

	public LayeredDisplayOutput getOutput() {
		return (LayeredDisplayOutput) output;
	}

	public void setZoomLevel(final Double newZoomLevel) {
		zoomLevel = newZoomLevel;
		if ( zoomListener != null ) {
			zoomListener.newZoomLevel(zoomLevel);
		}
	}

	@Override
	public double getZoomLevel() {
		if ( zoomLevel == null ) {
			zoomLevel = computeInitialZoomLevel();
		}
		return zoomLevel;
	}

	/**
	 * @return
	 */
	protected abstract Double computeInitialZoomLevel();

	@Override
	public void setZoomListener(final IZoomListener listener) {
		zoomListener = listener;
	}

	@Override
	public void zoomFit() {
		setZoomLevel(1d);
		zoomFit = true;
	}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		final double xScale = sizeInPixels.x / getEnvWidth();
		final double yScale = sizeInPixels.y / getEnvHeight();
		final int xInDisplay = xOnScreen - positionInPixels.x;
		final int yInDisplay = yOnScreen - positionInPixels.y;
		final double xInModel = xInDisplay / xScale;
		final double yInModel = yInDisplay / yScale;
		return new GamaPoint(xInModel, yInModel);
	}

	@Override
	public IList<IAgent> selectAgent(final int x, final int y) {
		int xc = x - getOriginX();
		int yc = y - getOriginY();
		IList<IAgent> result = new GamaList<IAgent>();
		final List<ILayer> layers = getManager().getLayersIntersecting(xc, yc);
		for ( ILayer layer : layers ) {
			Set<IAgent> agents = layer.collectAgentsAt(xc, yc, this);
			if ( !agents.isEmpty() ) {
				result.addAll(agents);
			}
		}
		return result;
	}

}