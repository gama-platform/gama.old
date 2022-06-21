/*******************************************************************************************************
 *
 * LayeredDisplayView.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import static msi.gama.common.preferences.GamaPreferences.Displays.CORE_DISPLAY_BORDER;
import static msi.gama.common.preferences.GamaPreferences.Runtime.CORE_SYNC;

import java.awt.Color;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisplaySynchronizer;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.SnapshotMaker;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.GamaViewPart;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * The Class LayeredDisplayView.
 */
public abstract class LayeredDisplayView extends GamaViewPart
		implements IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable, IGamaView.Display {

	static {
		DEBUG.ON();
	}

	/** The real index. */
	protected int realIndex = -1;

	// /** The form. */
	// private Composite form;

	/** The surface composite. */
	public Composite surfaceComposite;

	/** The decorator. */
	public LayeredDisplayDecorator decorator;

	/** The synchronizer. */
	public final IDisplaySynchronizer synchronizer = new LayeredDisplaySynchronizer();

	/** The disposed. */
	public volatile boolean disposed = false;

	/** The in init phase. */
	protected volatile boolean inInitPhase = true;

	/** The closing. */
	private volatile boolean closing = false;

	/** The central panel. */
	protected CentralPanel centralPanel;

	@Override
	public void setIndex(final int index) { realIndex = index; }

	@Override
	public int getIndex() { return realIndex; }

	/**
	 * Instantiates a new layered display view.
	 */
	public LayeredDisplayView() {

	}

	/**
	 * Gets the sash.
	 *
	 * @return the sash
	 */
	public Composite getCentralPanel() { return centralPanel; }

	@Override
	public boolean containsPoint(final int x, final int y) {
		if (super.containsPoint(x, y)) return true;
		final Point o = getSurfaceComposite().toDisplay(0, 0);
		final Point s = getSurfaceComposite().getSize();
		Rectangle r = new Rectangle(o.x, o.y, s.x, s.y);
		DEBUG.OUT("Looking in surfaceComposite rectangle " + r);

		return r.contains(x, y);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		decorator = new LayeredDisplayDecorator(this);
		if (getOutput() != null) {
			getOutput().getData().addListener(decorator);
			setPartName(getOutput().getName());
		}
	}

	@Override
	public void addOutput(final IDisplayOutput out) {
		super.addOutput(out);
		if (out instanceof LayeredDisplayOutput) {
			final IScope scope = out.getScope();
			if (scope != null && scope.getSimulation() != null) {
				final ITopLevelAgent root = scope.getRoot();
				final Color color = root.getColor();
				this.setTitleImage(GamaIcons.createTempColorIcon(GamaColors.get(color)));
			}
		}

	}

	/**
	 * Checks if is open GL.
	 *
	 * @return true, if is open GL
	 */
	public boolean isOpenGL() { return false; }

	/**
	 * Gets the display manager.
	 *
	 * @return the display manager
	 */
	public ILayerManager getDisplayManager() { return getDisplaySurface().getManager(); }

	/**
	 * Gets the surface composite.
	 *
	 * @return the surface composite
	 */
	public Composite getSurfaceComposite() { return surfaceComposite; }

	/**
	 * The Class CentralPanel.
	 */

	public class CentralPanel extends Composite implements InnerComponent {

		/**
		 * Instantiates a new central panel.
		 *
		 * @param parent
		 *            the parent
		 * @param style
		 *            the style
		 */
		public CentralPanel(final Composite c) {
			super(c, CORE_DISPLAY_BORDER.getValue() ? SWT.BORDER : SWT.NONE);
			setLayout(emptyLayout());
			setLayoutData(fullData());
			setParentComposite(this);
			// form.setMaximizedControl(this);
		}

		/**
		 * Gets the view.
		 *
		 * @return the view
		 */
		@Override
		public LayeredDisplayView getView() { return LayeredDisplayView.this; }

	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		if (getOutput() == null) return;
		c.setLayout(emptyLayout());
		centralPanel = new CentralPanel(c);
		createSurfaceComposite(centralPanel);
		surfaceComposite.setLayoutData(fullData());
		getOutput().setSynchronized(getOutput().isSynchronized() || CORE_SYNC.getValue());
		decorator.createDecorations();
		c.requestLayout();
		boolean[] toggle = { true };
		if (getOutput().getData().fullScreen() > -1) {
			new Thread(() -> {
				DEBUG.OUT("Fullscreen thread started");
				if (GamaPreferences.Runtime.CORE_ASK_FULLSCREEN.getValue()) {
					toggle[0] = Messages.question("Toggle fullscreen confirmation", "Do you want to go fullscreen ?");
				}
				DEBUG.OUT("Going full screen");
				WorkbenchHelper.runInUI("FS", 500, m -> toggleFullScreen());
			}).start();
		}
	}

	/**
	 * Gets the multi listener.
	 *
	 * @return the multi listener
	 */
	public IDisposable getMultiListener() {
		return new SWTLayeredDisplayMultiListener(decorator, getDisplaySurface());
	}

	/**
	 * Empty layout.
	 *
	 * @return the grid layout
	 */
	Layout emptyLayout() {
		// return new FillLayout(SWT.VERTICAL);
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		return gl;
	}

	/**
	 * Full data.
	 *
	 * @return the grid data
	 */
	GridData fullData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	@Override
	public void setFocus() {
		// Uncommenting this method seems to fix #3325. Should be tested !
		// DEBUG.OUT("Part " + getTitle() + " gaining focus");
		if (getParentComposite() != null && !getParentComposite().isDisposed()
				&& !getParentComposite().isFocusControl()) {
			getParentComposite().forceFocus(); // Necessary ?
		}
	}

	/**
	 * Creates the surface composite.
	 *
	 * @param parent
	 *            the parent
	 * @return the composite
	 */
	protected abstract Composite createSurfaceComposite(Composite parent);

	@Override
	public LayeredDisplayOutput getOutput() { return (LayeredDisplayOutput) super.getOutput(); }

	@Override
	public IDisplaySurface getDisplaySurface() {
		final LayeredDisplayOutput out = getOutput();
		if (out != null) return out.getSurface();
		return null;
	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		if (disposed) return;
		final LayeredDisplayOutput output = getOutput();
		if (output != null) {
			output.getData().listeners.clear();
			final IDisplaySurface s = output.getSurface();
			if (isOpenGL() && s != null) {
				s.dispose();
				output.setSurface(null);
			}
			output.releaseView();
			output.setSurface(null);
		}

		disposed = true;
		if (surfaceComposite != null) {
			try {
				surfaceComposite.dispose();
			} catch (final RuntimeException ex) {

			}
		}
		synchronizer.authorizeViewUpdate();
		// }
		if (updateThread != null) {
			updateThread.interrupt();
			DEBUG.OUT("Update thread disposed for " + getTitle());
			updateThread = null;
		}
		if (decorator != null) { decorator.dispose(); }
		super.widgetDisposed(e);
	}

	@Override
	public void pauseChanged() {
		decorator.updateOverlay();
	}

	/**
	 * Force overlay visibility.
	 *
	 * @return true, if successful
	 */
	public boolean forceOverlayVisibility() {
		return false;
	}

	@Override
	public void synchronizeChanged() {
		decorator.updateOverlay();
	}

	@Override
	public void zoomIn() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomIn(); }
	}

	@Override
	public void zoomOut() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomOut(); }
	}

	@Override
	public void zoomFit() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomFit(); }
	}

	@Override
	public Control[] getZoomableControls() { return new Control[] { getParentComposite() }; }

	@Override
	protected GamaUIJob createUpdateJob() {
		return new GamaUIJob() {

			@Override
			protected UpdatePriority jobPriority() {
				return UpdatePriority.HIGHEST;
			}

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				return Status.OK_STATUS;
			}
		};
	}

	/** The update thread. */
	Thread updateThread;

	/**
	 * Inits the update thread.
	 */
	private void initUpdateThread() {
		updateThread = new Thread(() -> {
			final IDisplaySurface surface = getDisplaySurface();
			synchronizer.waitForSurfaceToBeRealized();
			DEBUG.OUT("Surface has been realized");

			while (!disposed && !surface.isDisposed()) {
				try {
					synchronizer.waitForViewUpdateAuthorisation();
					surface.updateDisplay(false);
					if (surface.getData().isAutosave()) { takeSnapshot(); }
					inInitPhase = false;

				} catch (Exception e) {
					DEBUG.OUT("Error when updating " + this.getTitle() + ": " + e.getMessage());
				}
			}
		});
	}

	@Override
	public void update(final IDisplayOutput out) {
		if (updateThread == null || !updateThread.isAlive()) {
			initUpdateThread();
			synchronizer.setSurface(getDisplaySurface());
			updateThread.start();
		}
		synchronizer.authorizeViewUpdate();
		if (!inInitPhase && out.isSynchronized() && canBeSynchronized()) {
			synchronizer.waitForRenderingToBeFinished();
		}
	}

	/**
	 * Can be synchronized. Returns true if this can be synchronized: for instance, on macOS, hiding an OpenGL view will
	 * prevent it from rendering, and the simulation should not wait for this invisible visualisation.
	 *
	 * @return true, if successful
	 */
	protected abstract boolean canBeSynchronized();

	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

	@Override
	public void removeOutput(final IDisplayOutput output) {
		if (output == null) return;
		if (output == getOutput() && isFullScreen()) { WorkbenchHelper.run(decorator::toggleFullScreen); }
		output.dispose();
		outputs.remove(output);
		if (outputs.isEmpty()) {
			synchronizer.authorizeViewUpdate();
			close(GAMA.getRuntimeScope());
		}
	}

	@Override
	public boolean isFullScreen() { return decorator.isFullScreen(); }

	@Override
	public void toggleOverlay() {
		decorator.toggleOverlay();
	}

	@Override
	public void toggleFullScreen() {
		decorator.toggleFullScreen();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		decorator.createToolItems(tb);
	}

	@Override
	public void showOverlay(final boolean show) {
		decorator.overlay.setVisible(show);
	}

	@Override
	public void takeSnapshot() {
		Rectangle dim = WorkbenchHelper.displaySizeOf(surfaceComposite);
		java.awt.Rectangle r = new java.awt.Rectangle(dim.x, dim.y, dim.width, dim.height);
		SnapshotMaker.getInstance().doSnapshot(getDisplaySurface(), r);
	}

	/**
	 * Gets the interaction control.
	 *
	 * @return the interaction control
	 */
	public Control getInteractionControl() { return getZoomableControls()[0]; }

	@Override
	public void close(final IScope scope) {
		if (closing) return;
		closing = true;
		WorkbenchHelper.asyncRun(() -> {
			try {
				if (getDisplaySurface() != null) { getDisplaySurface().dispose(); }
				ViewsHelper.hideView(this);
			} catch (final Exception e) {}
		});

	}

}
