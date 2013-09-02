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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.awt.Color;
import javax.swing.JComponent;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplaySurface.OpenGL;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.displays.layers.AbstractLayer;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import msi.gama.gui.swt.swing.experimental.core.SwingControl;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.*;
import msi.gaml.descriptions.IDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

public class LayeredDisplayView extends ExpandableItemsView<ILayer> implements IViewWithZoom {

	public static final String ID = GuiUtils.LAYER_VIEW_ID;
	private SwingControl surfaceComposite;
	private Composite leftComposite;
	protected IPerspectiveListener perspectiveListener;
	protected GridData data;
	protected DisplayOverlay overlay;
	protected LayersOverlay layersOverlay;
	protected Integer zoomLevel = null;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setPartName(output.getViewName());

	}

	@Override
	protected Integer[] getToolbarActionsId() {
		IDescription description = output.getDescription();
		if ( description.getFacets().equals("type", "opengl") || description.getFacets().equals("type", "3D") ) { return new Integer[] {
			PAUSE, REFRESH, SYNC, SEP, LAYERS, OVERLAY, SEP, ZOOM_IN, ZOOM_FIT, ZOOM_OUT, SEP, FOCUS, SEP, OPENGL, SEP,
			HIGHLIGHT_COLOR, RENDERING, SNAP }; }
		return new Integer[] { PAUSE, REFRESH, SYNC, SEP, LAYERS, OVERLAY, SEP, ZOOM_IN, ZOOM_FIT, ZOOM_OUT, SEP,
			FOCUS, SEP, HIGHLIGHT_COLOR, RENDERING, SNAP };
	}

	public ILayerManager getDisplayManager() {
		return getOutput().getSurface().getManager();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		parent = new SashForm(c, SWT.SMOOTH | SWT.HORIZONTAL);
		leftComposite = new Composite(parent, SWT.NONE);
		createSurfaceComposite();
		Composite trueParent = parent;
		layersOverlay = new LayersOverlay(this);
		parent = layersOverlay.getPopup();
		createViewer();
		getViewer().setLayoutData(null);
		getViewer().setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		getViewer().setBackgroundMode(SWT.INHERIT_NONE);
		parent = trueParent;
		Composite general = new Composite(getViewer(), SWT.None);
		GridLayout layout = new GridLayout(2, false);

		general.setLayout(layout);

		EditorFactory.create(general, "Color:", getOutput().getBackgroundColor(), new EditorListener<Color>() {

			@Override
			public void valueModified(final Color newValue) {
				getOutput().setBackgroundColor(newValue);
			}
		});
		createItem("Background", null, general, true);
		displayItems();
		overlay = new DisplayOverlay(this);
		getOutput().getSurface().setZoomListener(this);
		((SashForm) parent).setWeights(new int[] { 1, 2 });
		((SashForm) parent).setMaximizedControl(surfaceComposite);
		getViewer().addListener(SWT.Collapse, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				layersOverlay.resize();
			}

		});
		getViewer().addListener(SWT.Expand, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				layersOverlay.resize();
			}

		});

		setSynchronized(GamaPreferences.CORE_SYNC.getValue());
		getOutput().getSurface().setQualityRendering(GamaPreferences.CORE_SYNC.getValue());
	}

	protected Composite createSurfaceComposite() {

		// TODO do a test to know whether or not we are in a "simple" chart environment ?

		final Runnable forceFocus = new Runnable() {

			@Override
			public void run() {
				if ( surfaceComposite.getDisplay() != null && !surfaceComposite.isFocusControl() ) {
					surfaceComposite.setFocus();
				}
			}
		};

		final Runnable displayOverlay = new Runnable() {

			@Override
			public void run() {
				overlay.update();
			}
		};
		// TODO Temporarily disabled
		// final java.awt.event.MouseListener mlAwt = new java.awt.event.MouseAdapter() {
		//
		// @Override
		// public void mousePressed(final java.awt.event.MouseEvent e) {
		// GuiUtils.asyncRun(forceFocus);
		// }
		//
		// @Override
		// public void mouseEntered(final java.awt.event.MouseEvent e) {
		// GuiUtils.asyncRun(forceFocus);
		// }
		//
		// };
		final java.awt.event.MouseMotionListener mlAwt2 = new java.awt.event.MouseMotionAdapter() {

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent e) {
				GuiUtils.asyncRun(displayOverlay);
			}

			@Override
			public void mouseDragged(final java.awt.event.MouseEvent e) {
				GuiUtils.asyncRun(displayOverlay);
			}
		};

		final boolean isOpenGL = getOutput().isOpenGL();
		final String outputName = getOutput().getName();

		OutputSynchronizer.incInitializingViews(getOutput().getName()); // incremented in the SWT thread
		// final int flags = Platform.getOS().equals(Platform.OS_MACOSX) ? SWT.NO_REDRAW_RESIZE : SWT.NONE;
		surfaceComposite = new SwingControl(parent, SWT.NONE) {

			@Override
			protected JComponent createSwingComponent() {

				final JComponent frameAwt = (JComponent) getOutput().getSurface();
				// TODO Temporarily disabled
				// frameAwt.addMouseListener(mlAwt);
				frameAwt.addMouseMotionListener(mlAwt2);
				// setCleanResizeEnabled(true);
				// frameAwt.setSize(width, height)
				return frameAwt;
			}

			@Override
			public Composite getLayoutAncestor() {

				// TODO CHECK THIS
				return null;
			}

			@Override
			public boolean isSwtTabOrderExtended() {
				return false;
			}

			@Override
			public boolean isAWTPermanentFocusLossForced() {
				return false;
			}

			@Override
			public void afterComponentCreatedSWTThread() {
				if ( GamaPreferences.CORE_OVERLAY.getValue() ) {
					overlay.toggle();
				}
			}

			@Override
			public void afterComponentCreatedAWTThread() {
				if ( !isOpenGL ) {
					// Deferred to the OpenGL renderer to signify its initialization
					// see JOGLAWTGLRendered.init()
					OutputSynchronizer.decInitializingViews(outputName);
				}
			}
		};

		// TODO Temporarily disabled
		surfaceComposite.addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseEnter(final MouseEvent e) {
				GuiUtils.asyncRun(forceFocus);
			}

		});

		// FIXME Hack to create a menu displayable on SWT
		new DisplaySurfaceMenu(getOutput().getSurface(), surfaceComposite, this);

		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(ModelingPerspective.ID) ) {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						previousState = getOutput().getSurface().isPaused();
						getOutput().getSurface().setPaused(true);
					}
					if ( overlay != null && layersOverlay != null ) {
						overlay.hide();
						layersOverlay.hide();
					}
				} else {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						getOutput().getSurface().setPaused(previousState);
					}
					if ( overlay != null && layersOverlay != null ) {
						overlay.update();
						layersOverlay.update();
					}
				}
			}
		};
		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		// if ( Platform.getOS().equals(Platform.OS_MACOSX) ) {
		// surfaceComposite.setRedraw(false);
		// }
		// surfaceComposite.populate();
		return surfaceComposite;
	}

	@Override
	public LayeredDisplayOutput getOutput() {
		return (LayeredDisplayOutput) super.getOutput();
	}

	@Override
	public boolean addItem(final ILayer d) {
		createItem(d, false);
		return true;
	}

	@Override
	protected Composite createItemContentsFor(final ILayer d) {
		final Composite compo = new Composite(getViewer(), SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		if ( d instanceof AbstractLayer ) {
			((AbstractLayer) d).fillComposite(compo, getOutput().getSurface());
		}
		return compo;
	}

	public IDisplaySurface getDisplaySurface() {
		return getOutput().getSurface();
	}

	public void toggleControls() {
		// Control c = ((SashForm) parent).getMaximizedControl();
		// if ( c == null ) {
		// The order is important
		layersOverlay.toggle();
		// ((SashForm) parent).setMaximizedControl(surfaceComposite);
		// } else {
		// ((SashForm) parent).setMaximizedControl(null);
		// layersOverlay.toggle();
		// }

	}

	@Override
	public String getItemDisplayName(final ILayer obj, final String previousName) {
		return getDisplayManager().getItemDisplayName(obj, previousName);
	}

	@Override
	public java.util.List<ILayer> getItems() {
		return getDisplayManager().getItems();
	}

	@Override
	public void updateItemValues() {}

	@Override
	public void zoomToFit() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				final IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {

					}
				}
				surface.zoomFit();

			}
		}).start();
	}

	@Override
	public void zoomIn() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				final IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {

					}
				}
				surface.zoomIn();

			}
		}).start();
	}

	@Override
	public void zoomOut() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				final IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {

					}
				}
				surface.zoomOut();

			}
		}).start();
	}

	@Override
	public void toggleView() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleView();

				}
			}).start();
		}
	}

	@Override
	public void toggleArcball() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleArcball();

				}
			}).start();
		}
	}

	@Override
	public void toggleInertia() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleInertia();

				}
			}).start();
		}
	}

	@Override
	public void toggleSelectRectangle() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleSelectRectangle();

				}
			}).start();
		}
	}

	@Override
	public void toggleTriangulation() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleTriangulation();

				}
			}).start();
		}
	}

	@Override
	public void toggleSplitLayer() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleSplitLayer();

				}
			}).start();
		}
	}

	@Override
	public void toggleRotation() {
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {

						}
					}
					surface.toggleRotation();

				}
			}).start();
		}
	}

	@Override
	public void snapshot() {
		getDisplaySurface().snapshot();
	}

	@Override
	public void setSynchronized(final boolean synchro) {
		getDisplaySurface().setSynchronized(synchro);
		overlay.update();
	}

	@Override
	public void addShapeFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {

					}
				}
				surface.addShapeFile();

			}
		}).start();

	}

	@Override
	public void dispose() {
		// FIXME Should not be redefined, but we should add a DisposeListener instead
		SwtGui.getWindow().removePerspectiveListener(perspectiveListener);
		// FIXME Remove the listeners
		surfaceComposite.dispose();
		overlay.close();
		layersOverlay.close();
		super.dispose();
	}

	// @Override
	// public void setIndicator(final ZoomIndicatorItem indicator) {
	// zoomIndicator = indicator;
	// }

	@Override
	public void newZoomLevel(final double zoomLevel) {
		this.zoomLevel = (int) (zoomLevel * 100);
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				// zoomIndicator.setText(String.valueOf(zoom) + "%");
				overlay.update();
			}
		});

	}

	/*
	 * Between 0 and 100;
	 */
	public int getZoomLevel() {
		if ( zoomLevel == null && getOutput().getSurface() != null ) {
			zoomLevel = (int) getOutput().getSurface().getZoomLevel() * 100;
		}
		return zoomLevel;
	}

	@Override
	public void toggleCamera() {
		// TODO Auto-generated method stub
		if ( getDisplaySurface() instanceof IDisplaySurface.OpenGL ) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					IDisplaySurface.OpenGL surface = (OpenGL) getDisplaySurface();
					while (!surface.canBeUpdated()) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {

						}
					}
					surface.toggleCamera();

				}
			}).start();
		}
	}

	@Override
	public void fixSize() {
		// OutputSynchronizer.cleanResize(new Runnable() {
		//
		// @Override
		// public void run() {
		// Point p = parent.getSize();
		// final int x = p.x;
		// final int y = p.y;
		// surfaceComposite.getFrame().setBounds(0, 0, x, y);
		// getOutput().getSurface().resizeImage(x, y);
		// getOutput().getSurface().setSize(x, y);
		// getOutput().getSurface().updateDisplay();
		// }
		// });

		// AD: Reworked to address Issue 535. It seems necessary to read the size of the composite inside an SWT thread
		// and run the sizing inside an AWT thread
		OutputSynchronizer.cleanResize(new Runnable() {

			@Override
			public void run() {

				// surfaceComposite.setSize(x, y);
				Point p = parent.getSize();
				final int x = p.x;
				final int y = p.y;
				java.awt.EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {

						surfaceComposite.getFrame().setBounds(0, 0, x, y);
						getOutput().getSurface().resizeImage(x, y);
						getOutput().getSurface().setSize(x, y);
						getOutput().getSurface().updateDisplay();
					}
				});

			}
		});
		// overlay.toggle();
	}

	@Override
	public void setFocus() {
		surfaceComposite.setFocus();
	}

	public Point getOverlayPosition() {
		Point p = surfaceComposite.toDisplay(surfaceComposite.getLocation());
		Point s = surfaceComposite.getSize();
		int x = p.x;
		int y = p.y + s.y - 16;
		return new Point(x, y);
	}

	public Point getOverlaySize() {
		Point s = surfaceComposite.getSize();
		return new Point(s.x, 16);
	}

	public String getOverlayText() {
		IDisplaySurface surface = getOutput().getSurface();
		boolean paused = surface.isPaused();
		boolean synced = surface.isSynchronized();
		boolean openGL = getOutput().isOpenGL();
		double cx = 0, cy = 0, cz = 0;
		if ( openGL ) {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			double[] cpos = ds.getCameraPosition();
			cx = cpos[0];
			cy = -cpos[1];
			cz = cpos[2];
		}
		GamaPoint point = surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.x);
		String y = point == null ? "N/A" : String.format("%8.2f", point.y);
		Object[] objects = null;
		if ( !openGL ) {
			objects = new Object[] { x, y, getZoomLevel() };
		} else {
			objects = new Object[] { x, y, getZoomLevel(), cx, cy, cz };
		}
		return String.format(" X%10s | Y%10s | Zoom%10d%%" + (paused ? " | Paused" : "") +
			(synced ? " | Synchronized" : "") + (openGL ? " | Camera [%.2f;%.2f;%.2f]" : ""), objects);
	};

	public Composite getComponent() {
		return surfaceComposite;
	}

	public void pauseChanged() {
		overlay.update();
	}

	public void toogleOverlay() {
		overlay.toggle();
	}

	public Point getLayersOverlayPosition() {
		return surfaceComposite.toDisplay(surfaceComposite.getLocation());
	}

	public Point getLayersOverlaySize() {
		Point s = surfaceComposite.getSize();
		return new Point(s.x / 3, s.y - 16);
	}

}
