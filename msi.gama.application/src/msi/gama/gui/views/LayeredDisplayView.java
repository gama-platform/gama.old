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
import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplaySurface.OpenGL;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.layers.AbstractLayer;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import msi.gama.gui.swt.swing.EmbeddedSwingComposite;
import msi.gama.gui.views.actions.ZoomIndicatorItem;
import msi.gama.outputs.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

public class LayeredDisplayView extends ExpandableItemsView<ILayer> implements IViewWithZoom {

	public static final String ID = GuiUtils.LAYER_VIEW_ID;

	protected Composite surfaceCompo;
	protected Composite general;
	private EmbeddedSwingComposite swingCompo;
	protected IPerspectiveListener perspectiveListener;
	protected Control aux;
	protected GridData data;

	protected ZoomIndicatorItem zoomIndicator;

	// private IPartListener2 partListener;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setPartName(output.getViewName());
	}

	// @Override
	// public void setFocus() {
	// swingCompo.setFocus();
	// }

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {

		// Add the toggle 3D view button for opengl display
		if ( this.output.getDescription().getFacets().equals("type", "opengl") ) {
			return new Integer[] { PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR, LAYERS, RENDERING, SNAPSHOT, SEPARATOR,
				ZOOM_IN, ZOOM_INDICATOR, ZOOM_OUT, ZOOM_FIT, FOCUS, SEPARATOR, CAMERA, SEPARATOR, ARCBALL, PICKING,
				SELECT_RECTANGLE, SHAPEFILE, SEPARATOR, TRIANGULATION, SPLITLAYER, ROTATION, SWITCHCAMERA };
		} else if ( this.output.getDescription().getFacets().equals("type", "swt") ) { return new Integer[] { PAUSE,
			REFRESH, SYNCHRONIZE, SEPARATOR, LAYERS, RENDERING, SNAPSHOT, SEPARATOR, ZOOM_IN, ZOOM_INDICATOR, ZOOM_OUT,
			ZOOM_FIT, FOCUS, SEPARATOR, CAMERA, SEPARATOR, ARCBALL, PICKING, SELECT_RECTANGLE, SHAPEFILE, SEPARATOR,
			TRIANGULATION, SPLITLAYER, ROTATION, SWITCHCAMERA }; }
		return new Integer[] { PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR, LAYERS, RENDERING, SNAPSHOT, SEPARATOR, ZOOM_IN,
			ZOOM_INDICATOR, ZOOM_OUT, ZOOM_FIT, FOCUS, SEPARATOR, HIGHLIGHT_COLOR };
	}

	public ILayerManager getDisplayManager() {
		return getOutput().getSurface().getManager();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		parent = new SashForm(c, SWT.HORIZONTAL | SWT.SMOOTH);
		createViewer();
		general = new Composite(getViewer(), SWT.None);
		GridLayout layout = new GridLayout(2, false);

		general.setLayout(layout);

		// If not type swt, then create embedded swing component
		if ( !getOutput().isSWT() ) {
			aux = new SWTNavigationPanel(general, SWT.None, getOutput().getSurface());
			data = new GridData(SWT.CENTER, SWT.FILL, true, true);
			data.minimumHeight = 200;
			data.heightHint = 200;
			data.widthHint = 200;
			data.horizontalSpan = 2;
			aux.setLayoutData(data);

			EditorFactory.create(general, "Color:", getOutput().getBackgroundColor(), new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) {
					getOutput().setBackgroundColor(newValue);
				}
			});
			createItem("Navigation", null, general, true);
			displayItems();

			surfaceCompo = createSurfaceComposite();
			// surfaceCompo.addControlListener(new ControlAdapter() {
			//
			// @Override
			// public void controlResized(final ControlEvent e) {
			// GuiUtils
			// .debug("LayeredDisplayView.ownCreatePartControl(...).new ControlAdapter() {...}.controlResized " +
			// surfaceCompo.getSize());
			// }
			// });

			getOutput().getSurface().setZoomListener(this);
			((SashForm) parent).setWeights(new int[] { 1, 2 });
			((SashForm) parent).setMaximizedControl(surfaceCompo);
		}

	}

	protected Composite createSurfaceComposite() {
		final java.awt.event.MouseListener mlAwt = new java.awt.event.MouseListener() {

			@Override
			public void mouseReleased(final java.awt.event.MouseEvent e) {}

			@Override
			public void mousePressed(final java.awt.event.MouseEvent e) {
				// System.err.println("force focus from AWT entered \t\t"+e);
				GuiUtils.asyncRun(new Runnable() { // (shift to SWT thread)

						@Override
						public void run() {
							swingCompo.forceFocus();
						}
					});
			}

			@Override
			public void mouseExited(final java.awt.event.MouseEvent e) {}

			@Override
			public void mouseEntered(final java.awt.event.MouseEvent e) {
				// System.err.println("force focus from AWT entered \t\t"+e);
				GuiUtils.asyncRun(new Runnable() {

					@Override
					public void run() {
						swingCompo.forceFocus();
					}
				});
			}

			@Override
			public void mouseClicked(final java.awt.event.MouseEvent e) {}
		};
		final java.awt.event.MouseMotionListener mlAwt2 = new java.awt.event.MouseMotionListener() {

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent e) {
				// System.err.println("force focus from AWT entered \t\t"+e);
				GuiUtils.asyncRun(new Runnable() {

					@Override
					public void run() {
						swingCompo.forceFocus();
					}
				});
			}

			@Override
			public void mouseDragged(final java.awt.event.MouseEvent e) {}
		};

		OutputSynchronizer.incInitializingViews(getOutput().getName()); // incremented in the SWT thread
		final int flags = Platform.getOS().equals(Platform.OS_MACOSX) ? SWT.NO_REDRAW_RESIZE : SWT.NONE;
		swingCompo = new EmbeddedSwingComposite(parent, flags) {

			@Override
			protected JComponent createSwingComponent() {
				outputName = getOutput().getName();
				isOpenGL = getOutput().isOpenGL();
				final JComponent frameAwt = (JComponent) getOutput().getSurface();
				getFrame().addMouseListener(mlAwt);
				getFrame().addMouseMotionListener(mlAwt2);
				return frameAwt;
			}
		};

		// partListener = new IPartListener2() {
		//
		// @Override
		// public void partVisible(IWorkbenchPartReference partRef) {
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partVisible " +
		// LayeredDisplayView.this.getPartName());
		// }
		// }
		//
		// @Override
		// public void partOpened(IWorkbenchPartReference partRef) {
		//
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partOpened " +
		// LayeredDisplayView.this.getPartName());
		// }
		//
		// }
		//
		// @Override
		// public void partInputChanged(IWorkbenchPartReference partRef) {}
		//
		// @Override
		// public void partHidden(IWorkbenchPartReference partRef) {
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partHidden " +
		// LayeredDisplayView.this.getPartName());
		// }
		// }
		//
		// @Override
		// public void partDeactivated(IWorkbenchPartReference partRef) {
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partDeactivated " +
		// LayeredDisplayView.this.getPartName());
		// }
		// }
		//
		// @Override
		// public void partClosed(IWorkbenchPartReference partRef) {
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partClosed " +
		// LayeredDisplayView.this.getPartName());
		// }
		// }
		//
		// @Override
		// public void partBroughtToTop(IWorkbenchPartReference partRef) {
		//
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partBroughtToTop  " +
		// LayeredDisplayView.this.getPartName());
		// }
		//
		// }
		//
		// @Override
		// public void partActivated(IWorkbenchPartReference partRef) {
		//
		// if ( partRef.getPartName().equals(LayeredDisplayView.this.getPartName()) ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new IPartListener2() {...}.partActivated " +
		// LayeredDisplayView.this.getPartName());
		// }
		//
		// }
		// };

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
				} else {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						getOutput().getSurface().setPaused(previousState);
					}
				}
			}
		};
		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		// SwtGui.getPage().addPartListener(partListener);

		// FIXME : if swingCompo is not initialized ?
		// PaintListener pl = new PaintListener() {
		//
		// @Override
		// public void paintControl(PaintEvent e) {
		// GuiUtils.debug("LayeredDisplayView.(...).new PaintListener() {...}: " + e.toString());
		// // Thread.dumpStack();
		// }
		//
		// };
		//
		// Listener pl2 = new Listener() {
		//
		// @Override
		// public void handleEvent(Event event) {
		// if ( event.widget == c || event.item == c ) {
		// GuiUtils
		// .debug("LayeredDisplayView.ownCreatePartControl(...).new Listener() {...}.handleEvent: paint for " +
		// event.widget);
		// }
		// }
		//
		// };
		// SwtGui.getDisplay().addFilter(SWT.Paint, pl2);
		// swingCompo.addPaintListener(pl);
		// Setting the redraw of the swingCompo itself to false (no need to draw it)
		if ( Platform.getOS().equals(Platform.OS_MACOSX) ) {
			swingCompo.setRedraw(false);
		}
		swingCompo.populate();

		// swingCompo.addMouseWheelListener(new MouseWheelListener() {
		//
		// @Override
		// public void mouseScrolled(final org.eclipse.swt.events.MouseEvent event) {
		//
		// // (shift to AWT thread)
		// java.awt.EventQueue.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// java.awt.Component c =
		// javax.swing.SwingUtilities.getDeepestComponentAt(swingCompo.getFrame(), event.x, event.y);
		// if ( c != null ) {
		// java.awt.Point bp = swingCompo.getFrame().getLocationOnScreen();
		// java.awt.Point cp = c.getLocationOnScreen();
		// java.awt.event.MouseEvent e =
		// new java.awt.event.MouseWheelEvent(c, java.awt.event.MouseEvent.MOUSE_WHEEL,
		// event.time & 0xFFFFFFFFL, 0, // modifiers
		// event.x - (cp.x - bp.x), event.y - (cp.y - bp.y), 0, // click count
		// false, MouseWheelEvent.WHEEL_UNIT_SCROLL, -event.count, -event.count);
		// // System.out.println("dispatching AWT event "+e);
		// c.dispatchEvent(e);
		// }
		//
		// }
		// });
		//
		// }
		// });
		return swingCompo;

		// });
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
		((SashForm) parent)
			.setMaximizedControl(((SashForm) parent).getMaximizedControl() == null ? surfaceCompo : null);
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
				final IDisplaySurface imageCanvas = getDisplaySurface();
				while (!imageCanvas.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {

					}
				}
				imageCanvas.zoomOut();

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
	public void togglePicking() {
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
					surface.togglePicking();

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
		surfaceCompo.dispose();
		// SwtGui.getPage().removePartListener(partListener);
		super.dispose();
		// GuiUtils.debug("LayeredDisplayView.dispose: DISPOSED " + getOutput().getName());

	}

	@Override
	public void setIndicator(final ZoomIndicatorItem indicator) {
		zoomIndicator = indicator;
	}

	@Override
	public void newZoomLevel(final double zoomLevel) {
		final int zoom = (int) (zoomLevel * 100);
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				zoomIndicator.setText(String.valueOf(zoom) + "%");
			}
		});

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
		// if ( swingCompo.isOpenGL ) { return; }
		OutputSynchronizer.cleanResize(new Runnable() {

			@Override
			public void run() {
				Point p = parent.getSize();
				final int x = p.x;
				final int y = p.y;
				swingCompo.setSize(x, y);
				// swingCompo.update();

				java.awt.EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						getOutput().getSurface().resizeImage(x, y);
						getOutput().getSurface().updateDisplay();
					}
				});

			}
		});

	};
}
