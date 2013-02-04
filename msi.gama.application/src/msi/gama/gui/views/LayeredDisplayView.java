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
import java.awt.event.MouseWheelEvent;
import javax.swing.JComponent;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.layers.AbstractLayer;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.swing.EmbeddedSwingComposite;
import msi.gama.outputs.LayeredDisplayOutput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

public class LayeredDisplayView extends ExpandableItemsView<ILayer> implements IViewWithZoom {

	public static final String ID = GuiUtils.LAYER_VIEW_ID;

	private EmbeddedSwingComposite swingCompo;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setPartName(output.getViewName());
	}

	@Override
	public void setFocus() {
		swingCompo.setFocus();
	}

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {

		// Add the toggle 3D view button for opengl display
		if ( this.output.getDescription().getFacets().equals("type", "opengl") ) { return new Integer[] {
			PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR, LAYERS, RENDERING, SNAPSHOT, SEPARATOR,
			ZOOM_IN, ZOOM_OUT, ZOOM_FIT, CAMERA, FOCUS, SEPARATOR, ARCBALL,PICKING,SELECT_RECTANGLE, SHAPEFILE }; }
		return new Integer[] { PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR, LAYERS, RENDERING, SNAPSHOT,
			SEPARATOR, ZOOM_IN, ZOOM_OUT, ZOOM_FIT, FOCUS, SEPARATOR, HIGHLIGHT_COLOR };
	}

	public ILayerManager getDisplayManager() {
		return ((LayeredDisplayOutput) getOutput()).getSurface().getManager();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		parent = new SashForm(c, SWT.HORIZONTAL | SWT.SMOOTH);
		createViewer();
		Composite general = new Composite(getViewer(), SWT.None);
		GridLayout layout = new GridLayout(2, false);

		general.setLayout(layout);
		// final Button label = new Button(general, SWT.CHECK);
		// label.setFont(SwtGui.labelFont);
		// label.setLayoutData(SwtGui.labelData);
		// label.setText("");
		// label.setSelection(false);
		// label.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// ((AWTDisplaySurface) ((LayerDisplayOutput) getOutput()).getSurface())
		// .setNavigationImageEnabled(label.getSelection());
		// }
		//
		// });

		Control aux = new SWTNavigationPanel(general, SWT.None, getOutput().getSurface());
		GridData data = new GridData(SWT.CENTER, SWT.FILL, true, true);
		data.minimumHeight = 200;
		data.heightHint = 200;
		data.widthHint = 200;
		data.horizontalSpan = 2;
		
		aux.setLayoutData(data);
		EditorFactory.create(general, "Color:", output.getBackgroundColor(),
			new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) {
					output.setBackgroundColor(newValue);
				}
			});
		createItem("Navigation", null, general, true);
		// nav.populate();
		displayItems();
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

		swingCompo =
			new EmbeddedSwingComposite(parent, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND,
				getOutput()) {

				@Override
				protected JComponent createSwingComponent() {
					JComponent frameAwt = (JComponent) getOutput().getSurface();
					getFrame().addMouseListener(mlAwt);
					getFrame().addMouseMotionListener(mlAwt2);
					return frameAwt;
				}
			};

		swingCompo.populate();

		swingCompo.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(final org.eclipse.swt.events.MouseEvent event) {

				// (shift to AWT thread)
				java.awt.EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {

						java.awt.Component c =
							javax.swing.SwingUtilities.getDeepestComponentAt(swingCompo.getFrame(),
								event.x, event.y);
						if ( c != null ) {
							java.awt.Point bp = swingCompo.getFrame().getLocationOnScreen();
							java.awt.Point cp = c.getLocationOnScreen();
							java.awt.event.MouseEvent e =
								new java.awt.event.MouseWheelEvent(c,
									java.awt.event.MouseEvent.MOUSE_WHEEL,
									event.time & 0xFFFFFFFFL,
									0, // modifiers
									event.x - (cp.x - bp.x), event.y - (cp.y - bp.y),
									0, // click count
									false, MouseWheelEvent.WHEEL_UNIT_SCROLL, -event.count,
									-event.count);
							// System.out.println("dispatching AWT event "+e);
							c.dispatchEvent(e);
						}

					}
				});

			}
		});
		((SashForm) parent).setWeights(new int[] { 1, 2 });
		((SashForm) parent).setMaximizedControl(swingCompo);
	}

	@Override
	public boolean addItem(final ILayer d) {
		createItem(d, false);
		return true;
	}

	@Override
	protected Composite createItemContentsFor(final ILayer d) {
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		if ( d instanceof AbstractLayer ) {
			((AbstractLayer) d).fillComposite(compo,
				((LayeredDisplayOutput) getOutput()).getSurface());
		}
		return compo;
	}

	public IDisplaySurface getDisplaySurface() {
		return ((LayeredDisplayOutput) getOutput()).getSurface();
	}

	public void toggleControls() {
		((SashForm) parent).setMaximizedControl(((SashForm) parent).getMaximizedControl() == null
			? swingCompo : null);
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
				IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

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
				IDisplaySurface imageCanvas = getDisplaySurface();
				while (!imageCanvas.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

					}
				}
				imageCanvas.zoomIn();

			}
		}).start();
	}

	@Override
	public void zoomOut() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				IDisplaySurface imageCanvas = getDisplaySurface();
				while (!imageCanvas.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

					}
				}
				imageCanvas.zoomOut();

			}
		}).start();
	}

	@Override
	public void toggleView() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

					}
				}
				surface.toggleView();

			}
		}).start();
	}
	
	
	
	@Override
	public void togglePicking() {
	
		new Thread(new Runnable() {
	
			@Override
			public void run() {
				IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
	
					}
				}
				surface.togglePicking();
	
			}
		}).start();
	}
	
	
	@Override
	public void toggleArcball() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

					}
				}
				surface.toggleArcball();

			}
		}).start();
	}
	
	@Override
	public void toggleSelectRectangle() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

					}
				}
				surface.toggleSelectRectangle();

			}
		}).start();
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
				IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
	
					}
				}
				surface.addShapeFile();
	
			}
		}).start();
		
	}

}
