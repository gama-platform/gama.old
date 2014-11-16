/*********************************************************************************************
 * 
 * 
 * 'LayeredDisplayView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import java.awt.Color;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplaySurface.OpenGL;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.layers.LayerSideControls;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.*;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.AbstractLayer;
import msi.gaml.descriptions.IDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

public abstract class LayeredDisplayView extends ExpandableItemsView<ILayer> implements IViewWithZoom {

	protected Composite surfaceComposite;
	// private Composite leftComposite;
	protected IPerspectiveListener perspectiveListener;
	// protected GridData data;
	protected DisplayOverlay overlay;
	protected LayersOverlay layersOverlay;
	protected Integer zoomLevel = null;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setPartName(output.getViewName());

	}

	@Override
	public Integer[] getToolbarActionsId() {
		if ( output == null ) { return new Integer[0]; }
		IDescription description = output.getDescription();
		if ( description.getFacets().equals(IKeyword.TYPE, "opengl") ||
			description.getFacets().equals(IKeyword.TYPE, "3D") ) { return new Integer[] { REFRESH, PAUSE, SEP, SYNC,
			SNAP, SEP, ZOOM_IN, ZOOM_FIT, ZOOM_OUT, SEP, FOCUS, OPENGL, SEP, SIDEBAR, OVERLAY }; }
		return new Integer[] { REFRESH, PAUSE, SEP, SYNC, SNAP, SEP, ZOOM_IN, ZOOM_FIT, ZOOM_OUT, SEP, FOCUS, SEP,
			SIDEBAR, OVERLAY };
	}

	public ILayerManager getDisplayManager() {
		return getOutput().getSurface().getManager();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		// c.setLayout(new GridLayout(1, true));
		// parent = c;

		parent = new SashForm(c, SWT.SMOOTH | SWT.HORIZONTAL | SWT.BORDER);
		/* Composite leftComposite = */new Composite(parent, SWT.NONE);
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

		EditorFactory.create(general, "Background", getOutput().getBackgroundColor(), new EditorListener<Color>() {

			@Override
			public void valueModified(final Color newValue) {
				getOutput().setBackgroundColor(newValue);
			}
		});

		EditorFactory.create(general, "Highlight", GamaPreferences.CORE_HIGHLIGHT.getValue(),
			new EditorListener<Color>() {

				@Override
				public void valueModified(final Color c) {
					getOutput().getSurface().setHighlightColor(new int[] { c.getRed(), c.getGreen(), c.getBlue() });
				}
			});

		EditorFactory.create(general, "Antialiasing", GamaPreferences.CORE_ANTIALIAS.getValue(),
			new EditorListener<Boolean>() {

				@Override
				public void valueModified(final Boolean newValue) {
					getOutput().getSurface().setQualityRendering(newValue);
				}
			});
		EditorFactory.create(general, "Scale bar", GamaPreferences.CORE_SCALE.getValue(),
			new EditorListener<Boolean>() {

				@Override
				public void valueModified(final Boolean newValue) {
					overlay.displayScale(newValue);

				}
			});
		createItem("Properties", null, general, true);
		displayItems();
		overlay = new DisplayOverlay(this, getOutput().getOverlayProvider());
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
		getOutput().getSurface().setQualityRendering(GamaPreferences.CORE_ANTIALIAS.getValue());
	}

	@Override
	public void setFocus() {
		surfaceComposite.setFocus();
	}

	protected abstract Composite createSurfaceComposite();

	// protected void monitorMouseMove(final int x, final int y) {
	//
	// if ( surfaceComposite.getBounds().height - y < 10 ) {
	// if ( !overlay.getPopup().isVisible() ) { // TODO Maybe useless
	// overlay.appear();
	// }
	// } else if ( x < 10 ) {
	// if ( !layersOverlay.getPopup().isVisible() ) {
	// layersOverlay.appear();
	// }
	// }
	//
	// }

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
			LayerSideControls.fill(compo, d, getOutput().getSurface());
		}
		return compo;
	}

	public IDisplaySurface getDisplaySurface() {
		return getOutput().getSurface();
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
	public void dispose() {
		// FIXME Should not be redefined, but we should add a DisposeListener instead
		SwtGui.getWindow().removePerspectiveListener(perspectiveListener);
		// FIXME Remove the listeners
		surfaceComposite.dispose();
		overlay.close();
		layersOverlay.close();
		super.dispose();
	}

	@Override
	public void newZoomLevel(final double zoomLevel) {
		this.zoomLevel = (int) (zoomLevel * 100);
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
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

	public String getOverlayText() {
		IDisplaySurface surface = getOutput().getSurface();
		boolean paused = surface.isPaused();
		boolean synced = surface.isSynchronized();
		boolean openGL = getOutput().isOpenGL();
		// double cx = 0, cy = 0, cz = 0;
		// if ( openGL ) {
		// IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
		// GamaPoint camera = ds.getCameraPosition();
		// // cx = cpos[0];
		// // cy = -cpos[1];
		// // cz = cpos[2];
		// }
		GamaPoint point = surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.x);
		String y = point == null ? "N/A" : String.format("%8.2f", point.y);
		Object[] objects = null;
		if ( !openGL ) {
			objects = new Object[] { x, y, getZoomLevel() };
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			GamaPoint camera = ds.getCameraPosition();
			objects = new Object[] { x, y, getZoomLevel(), camera.x, camera.y, camera.z };
		}
		return String.format(" X%10s | Y%10s | Zoom%10d%%" + (paused ? " | Paused" : "") +
			(synced ? " | Synchronized" : "") + (openGL ? " | Camera [%.2f;%.2f;%.2f]" : ""), objects);
	};

	public Composite getComponent() {
		return surfaceComposite;
	}

	@Override
	public void pauseChanged() {
		overlay.update();
	}

	public double getValueOfOnePixelInModelUnits() {
		double displayWidth = getOutput().getSurface().getDisplayWidth();
		double envWidth = getOutput().getSurface().getEnvWidth();
		return envWidth / displayWidth;
	}

	/**
	 *
	 */
	public void toggleSideBar() {
		this.layersOverlay.toggle();
	}

	/**
	 *
	 */
	public void toggleOverlay() {
		this.overlay.toggle();
	}

	/**
	 * @return
	 */
	public String getOverlayCoordInfo() {
		IDisplaySurface surface = getOutput().getSurface();
		boolean paused = surface.isPaused();
		boolean synced = surface.isSynchronized();
		GamaPoint point = surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.x);
		String y = point == null ? "N/A" : String.format("%8.2f", point.y);
		Object[] objects = new Object[] { x, y };
		return String
			.format("X%10s | Y%10s" + (paused ? " | Paused" : "") + (synced ? " | Synchronized" : ""), objects);

	}

	/**
	 * @return
	 */
	public String getOverlayZoomInfo() {
		IDisplaySurface surface = getOutput().getSurface();
		boolean openGL = getOutput().isOpenGL();
		Object[] objects = null;
		if ( !openGL ) {
			objects = new Object[] { getZoomLevel() };
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			GamaPoint camera = ds.getCameraPosition();
			objects = new Object[] { getZoomLevel(), camera.x, camera.y, camera.z };
		}
		return String.format("Zoom %d%%" + (openGL ? " | Camera [%.2f;%.2f;%.2f]" : ""), objects);

	}

}
