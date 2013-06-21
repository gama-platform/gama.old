package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.SWTNavigationPanel;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;

public abstract class AbstractAWTDisplaySurface extends JPanel implements IDisplaySurface {

	private IDisplayOutput output;
	protected int[] highlightColor = GuiUtils.defaultHighlight;
	protected IGraphics iGraphics;
	protected String snapshotFileName;
	protected static String snapshotFolder = "snapshots";
	protected ILayerManager manager;
	protected boolean paused;
	protected volatile boolean canBeUpdated = true;
	protected Point origin = new Point(0, 0);
	protected Dimension previousPanelSize;
	protected final Semaphore paintingNeeded = new Semaphore(1, true);
	private int displayWidth;
	private int displayHeight;
	protected SWTNavigationPanel navigator;
	protected boolean autosave = false;
	protected double widthHeightConstraint = 1.0;
	protected double zoomIncrement = 0.1;
	protected double zoomLevel;
	protected boolean zoomFit = true;
	protected boolean navigationImageEnabled = true;
	protected final AffineTransform translation = new AffineTransform();
	protected boolean synchronous = false;
	protected Color bgColor = Color.black;
	protected final GamaRuntimeException[] ex = new GamaRuntimeException[] { null };
	protected Runnable displayBlock;
	private double envWidth;
	private double envHeight;
	private IZoomListener zoomListener;

	protected AbstractAWTDisplaySurface(final Object ... args) {}

	@Override
	public void initialize(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		setOutput(output);
		setOpaque(true);
		setDoubleBuffered(false);
		this.setLayout(new BorderLayout());
		outputChanged(env_width, env_height, output);
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
		updateDisplay();
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

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public void save(final IScope scope, final RenderedImage image) {
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

		String file = snapshotFile + scope.getClock().getCycle() + ".png";
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

	protected Cursor createCursor() {
		Image im = new BufferedImage((int) SELECTION_SIZE + 4, (int) SELECTION_SIZE + 4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) im.getGraphics();
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(3.0f));
		g.draw(new Rectangle2D.Double(2, 2, SELECTION_SIZE, SELECTION_SIZE));
		g.dispose();
		Cursor c =
			getToolkit().createCustomCursor(im, new Point((int) (SELECTION_SIZE / 2), (int) SELECTION_SIZE / 2),
				"CIRCLE");
		return c;
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
		return highlightColor;
	}

	@Override
	public void setHighlightColor(final int[] rgb) {
		highlightColor = rgb;
		iGraphics.setHighlightColor(rgb);
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setNavigator(java.lang.Object)
	 */
	@Override
	public void setNavigator(final Object nav) {
		if ( nav instanceof SWTNavigationPanel ) {
			navigator = (SWTNavigationPanel) nav;
		}
	}

	@Override
	public void setSnapshotFileName(final String file) {
		snapshotFileName = file;
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		if ( iGraphics == null ) { return; }
		iGraphics.setQualityRendering(quality);
		if ( isPaused() ) {
			updateDisplay();
		}
	}

	@Override
	public void setSynchronized(final boolean checked) {
		synchronous = checked;
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
		redrawNavigator();
	}

	protected void redrawNavigator() {
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
	public void updateDisplay() {
		// GuiUtils.debug("AbstractAWTDisplaySurface.updateDisplay");
		if ( !canBeUpdated() ) { return; }
		if ( synchronous && !EventQueue.isDispatchThread() && !GAMA.isPaused() ) {
			try {
				EventQueue.invokeAndWait(displayBlock);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(displayBlock);
		}
		if ( ex[0] != null ) {
			GAMA.reportAndThrowIfNeeded(null, ex[0], false);
			ex[0] = null;
		}
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
	public final boolean resizeImage(final int x, final int y) {
		GuiUtils.debug("AbstractAWTDisplaySurface.resizeImage " + x + " " + y);
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
		int imageWidth = Math.max(1, point[0]);
		int imageHeight = Math.max(1, point[1]);
		if ( imageWidth <= MAX_SIZE && imageHeight <= MAX_SIZE ) {
			createNewImage(imageWidth, imageHeight);
			createIGraphics();
			redrawNavigator();
			canBeUpdated(true);
			return true;
		}
		canBeUpdated(true);
		return false;
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
	public void setZoomListener(final IZoomListener listener) {
		zoomListener = listener;
	}

	@Override
	public void zoomFit() {
		setZoomLevel(1d);
		zoomFit = true;
	}

}