/*********************************************************************************************
 * 
 * 
 * 'OpenGLItem.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplaySurface.OpenGL;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.commands.*;
import msi.gama.gui.swt.commands.GamaColorMenu.IColorRunnable;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * The class FocusItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class PresentationMenu extends GamaToolItem {

	private Menu menu;

	@Override
	public void dispose() {
		// if ( disposed ) { return; }
		if ( menu != null && !menu.isDisposed() ) {
			menu.dispose();
		}
		super.dispose();
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	public ToolItem createItem(final GamaToolbarSimple toolbar, final IToolbarDecoratedView view) {

		return toolbar.menu("display.presentation2", "Presentation", "Presentation options", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent trigger) {
				boolean asMenu = trigger.detail == SWT.ARROW;
				if ( !asMenu ) { return; }
				final ToolItem target = (ToolItem) trigger.widget;
				final ToolBar toolBar = target.getParent();
				if ( menu != null ) {
					menu.dispose();
				}
				menu = new Menu(toolBar.getShell(), SWT.POP_UP);
				fillMenu(menu, (LayeredDisplayView) view);
				Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);

			}

		});
	}

	public void fillMenu(final Menu menu, final LayeredDisplayView view) {
		final MenuItem layerEditMenu = new MenuItem(menu, SWT.PUSH);
		// boolean sidebar = view.getSidebar() != null && view.getSidebar().getPopup().isVisible();
		layerEditMenu.setText("Show layer options");
		layerEditMenu.setImage(IGamaIcons.DISPLAY_TOOLBAR_SIDEBAR.image());
		layerEditMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.toggleSideBar();
			}

		});
		MenuItem overlayItem = new MenuItem(menu, SWT.PUSH);
		overlayItem.setImage(IGamaIcons.DISPLAY_TOOLBAR_OVERLAY.image());
		final boolean overlay = view.getOverlay().getPopup().isVisible();
		overlayItem.setText(overlay ? "Hide overlay" : "Show overlay");
		overlayItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.toggleOverlay();
			}

		});
		if ( overlay ) {
			MenuItem scaleItem = new MenuItem(menu, SWT.CHECK);
			scaleItem.setImage(GamaIcons.create("display.scale2").image());
			final boolean scale = view.getOutput().shouldDisplayScale();
			scaleItem.setText("... with scale");

			scaleItem.setSelection(scale);
			scaleItem.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					view.getOverlay().displayScale(!scale);
					view.getOutput().toogleScaleDisplay();
				}

			});
		}

		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem antialiasItem = new MenuItem(menu, SWT.CHECK);
		antialiasItem.setImage(GamaIcons.create("display.antialias2").image());
		antialiasItem.setText("Apply antialiasing");
		final boolean antialias = view.getOutput().getSurface().getQualityRendering();
		antialiasItem.setSelection(antialias);
		antialiasItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.getDisplaySurface().setQualityRendering(!antialias);
			}

		});

		MenuItem backgroundItem = new MenuItem(menu, SWT.CHECK);
		final java.awt.Color background = view.getOutput().getData().getBackgroundColor();
		final Image image = GamaIcons.createTempColorIcon(GamaColors.get(background));
		backgroundItem.setImage(image);
		backgroundItem.setText("Background color...");
		backgroundItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GamaColorMenu.openView(new IColorRunnable() {

					@Override
					public void run(final int r, final int g, final int b) {
						java.awt.Color background = new java.awt.Color(r, g, b);
						view.getDisplaySurface().setBackground(background);
						image.dispose();
					}
				}, new RGB(background.getRed(), background.getGreen(), background.getBlue()));
			}

		});

		MenuItem highlightItem = new MenuItem(menu, SWT.CHECK);
		final java.awt.Color h = view.getDisplaySurface().getHighlightColor();
		final Image highlightImage = GamaIcons.createTempColorIcon(GamaColors.get(h));
		highlightItem.setImage(highlightImage);
		highlightItem.setText("Highlight color...");
		highlightItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GamaColorMenu.openView(new IColorRunnable() {

					@Override
					public void run(final int r, final int g, final int b) {
						view.getDisplaySurface().setHighlightColor(h);
						highlightImage.dispose();
					}
				}, new RGB(h.getRed(), h.getGreen(), h.getBlue()));
			}

		});

		if ( !view.isOpenGL() ) { return; }
		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem camera = new MenuItem(menu, SWT.PUSH);
		camera.setImage(IGamaIcons.DISPLAY_TOOLBAR_CAMERA.image());
		boolean arcBall = !((IDisplaySurface.OpenGL) view.getDisplaySurface()).isCameraSwitched();
		camera.setText(arcBall ? "Use FreeFly camera" : "Use ArcBall camera");
		camera.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().waitForUpdateAndRun(new Runnable() {

					@Override
					public void run() {
						((OpenGL) view.getDisplaySurface()).toggleCamera();
					}
				});
			}
		});
		if ( arcBall ) {
			MenuItem drag = new MenuItem(menu, SWT.PUSH);
			boolean dragable = ((IDisplaySurface.OpenGL) view.getDisplaySurface()).isArcBallDragOn();
			drag.setText(dragable ? "Use mouse to drag" : "Use mouse to rotate");
			drag.setImage(IGamaIcons.DISPLAY_TOOLBAR_DRAG.image());
			drag.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					view.getDisplaySurface().waitForUpdateAndRun(new Runnable() {

						@Override
						public void run() {
							((OpenGL) view.getDisplaySurface()).toggleArcball();
						}
					});
				}
			});

			MenuItem inertia = new MenuItem(menu, SWT.CHECK);
			inertia.setImage(IGamaIcons.DISPLAY_TOOLBAR_INERTIA.image());
			boolean inertiable = ((IDisplaySurface.OpenGL) view.getDisplaySurface()).isInertiaOn();
			inertia.setSelection(inertiable);
			inertia.setText("Apply inertia");
			inertia.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {

					view.getDisplaySurface().waitForUpdateAndRun(new Runnable() {

						@Override
						public void run() {
							((OpenGL) view.getDisplaySurface()).toggleInertia();
						}
					});
				}

			});
		}
		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem rotation = new MenuItem(menu, SWT.CHECK);
		boolean rotated = ((IDisplaySurface.OpenGL) view.getDisplaySurface()).isRotationOn();
		rotation.setSelection(rotated);
		rotation.setText("Rotate scene");
		rotation.setImage(IGamaIcons.DISPLAY_TOOLBAR_ROTATE.image());
		rotation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().waitForUpdateAndRun(new Runnable() {

					@Override
					public void run() {
						((OpenGL) view.getDisplaySurface()).toggleRotation();
					}
				});
			}
		});
		MenuItem split = new MenuItem(menu, SWT.CHECK);
		split.setImage(IGamaIcons.DISPLAY_TOOLBAR_SPLIT.image());
		boolean splitted = ((IDisplaySurface.OpenGL) view.getDisplaySurface()).isLayerSplitted();
		split.setSelection(splitted);
		split.setText("Split layers");
		split.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().waitForUpdateAndRun(new Runnable() {

					@Override
					public void run() {
						((OpenGL) view.getDisplaySurface()).toggleSplitLayer();
					}
				});
			}
		});
		MenuItem triangle = new MenuItem(menu, SWT.CHECK);
		boolean triangulated = ((IDisplaySurface.OpenGL) view.getDisplaySurface()).isTriangulationOn();
		triangle.setText("Triangulate scene");
		triangle.setSelection(triangulated);
		triangle.setImage(IGamaIcons.DISPLAY_TOOLBAR_TRIANGULATE.image());
		triangle.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().waitForUpdateAndRun(new Runnable() {

					@Override
					public void run() {
						((OpenGL) view.getDisplaySurface()).toggleTriangulation();
					}
				});
			}
		});

	}

	/**
	 * @param tb
	 * @param view
	 */
	public void createItem(final GamaToolbar2 tb, final IToolbarDecoratedView view) {

		tb.menu("display.presentation2", "Presentation", "Presentation options", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent trigger) {
				boolean asMenu = trigger.detail == SWT.ARROW;
				if ( !asMenu ) { return; }
				final ToolItem target = (ToolItem) trigger.widget;
				final ToolBar toolBar = target.getParent();
				if ( menu != null ) {
					menu.dispose();
				}
				menu = new Menu(toolBar.getShell(), SWT.POP_UP);
				fillMenu(menu, (LayeredDisplayView) view);
				Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);

			}

		}, SWT.RIGHT);

	}
}
