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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.IDisplaySurface.OpenGL;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.*;

/**
 * The class FocusItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class PresentationMenu {

	private Menu menu;

	public void fillMenu(final Menu menu, final LayeredDisplayView view) {
		// final MenuItem layerEditMenu = new MenuItem(menu, SWT.PUSH);
		// // boolean sidebar = view.getSidebar() != null && view.getSidebar().getPopup().isVisible();
		// layerEditMenu.setText("Show layer options");
		// layerEditMenu.setImage(IGamaIcons.DISPLAY_TOOLBAR_SIDEBAR.image());
		// layerEditMenu.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// view.toggleSideBar();
		// }
		//
		// });
		// MenuItem overlayItem = new MenuItem(menu, SWT.PUSH);
		// overlayItem.setImage(IGamaIcons.DISPLAY_TOOLBAR_OVERLAY.image());
		// final boolean overlay = view.getOverlay().getPopup().isVisible();
		// overlayItem.setText(overlay ? "Hide overlay" : "Show overlay");
		// overlayItem.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// view.toggleOverlay();
		// }
		//
		// });
		// if ( overlay ) {
		// MenuItem scaleItem = new MenuItem(menu, SWT.CHECK);
		// scaleItem.setImage(GamaIcons.create("display.scale2").image());
		// final boolean scale = view.getOutput().shouldDisplayScale();
		// scaleItem.setText("... with scale");
		//
		// scaleItem.setSelection(scale);
		// scaleItem.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// view.getOverlay().displayScale(!scale);
		// view.getOutput().toogleScaleDisplay();
		// }
		//
		// });
		// }
		//
		// new MenuItem(menu, SWT.SEPARATOR);

		// MenuItem antialiasItem = new MenuItem(menu, SWT.CHECK);
		// antialiasItem.setImage(GamaIcons.create("display.antialias2").image());
		// antialiasItem.setText("Apply antialiasing");
		// final boolean antialias = view.getOutput().getSurface().getData().isAntialias();
		// antialiasItem.setSelection(antialias);
		// antialiasItem.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// view.getDisplaySurface().getData().setAntialias(!antialias);
		// }
		//
		// });

		// MenuItem backgroundItem = new MenuItem(menu, SWT.CHECK);
		// final java.awt.Color background = view.getOutput().getData().getBackgroundColor();
		// final Image image = GamaIcons.createTempColorIcon(GamaColors.get(background));
		// backgroundItem.setImage(image);
		// backgroundItem.setText("Background color...");
		// backgroundItem.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// GamaColorMenu.openView(new IColorRunnable() {
		//
		// @Override
		// public void run(final int r, final int g, final int b) {
		// java.awt.Color background = new java.awt.Color(r, g, b);
		// view.getDisplaySurface().getData().setBackgroundColor(background);
		// image.dispose();
		// }
		// }, new RGB(background.getRed(), background.getGreen(), background.getBlue()));
		// }
		//
		// });
		//
		// MenuItem highlightItem = new MenuItem(menu, SWT.CHECK);
		// final java.awt.Color h = view.getDisplaySurface().getData().getHighlightColor();
		// final Image highlightImage = GamaIcons.createTempColorIcon(GamaColors.get(h));
		// highlightItem.setImage(highlightImage);
		// highlightItem.setText("Highlight color...");
		// highlightItem.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// GamaColorMenu.openView(new IColorRunnable() {
		//
		// @Override
		// public void run(final int r, final int g, final int b) {
		// view.getDisplaySurface().getData().setHighlightColor(h);
		// highlightImage.dispose();
		// }
		// }, new RGB(h.getRed(), h.getGreen(), h.getBlue()));
		// }
		//
		// });

		if ( !view.isOpenGL() ) { return; }
		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem camera = new MenuItem(menu, SWT.PUSH);
		camera.setImage(IGamaIcons.DISPLAY_TOOLBAR_CAMERA.image());
		boolean arcBall = view.getDisplaySurface().getData().isArcBallCamera();
		camera.setText(arcBall ? "Use FreeFly camera" : "Use ArcBall camera");
		camera.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().runAndUpdate(new Runnable() {

					@Override
					public void run() {
						boolean old = view.getDisplaySurface().getData().isArcBallCamera();
						((OpenGL) view.getDisplaySurface()).getData().setArcBallCamera(!old);
					}
				});
			}
		});
		if ( arcBall ) {
			MenuItem drag = new MenuItem(menu, SWT.PUSH);
			boolean dragable = view.getDisplaySurface().getData().isArcBallDragOn();
			drag.setText(dragable ? "Use mouse to drag" : "Use mouse to rotate");
			drag.setImage(IGamaIcons.DISPLAY_TOOLBAR_DRAG.image());
			drag.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					view.getDisplaySurface().runAndUpdate(new Runnable() {

						@Override
						public void run() {
							boolean old = view.getDisplaySurface().getData().isArcBallDragOn();
							view.getDisplaySurface().getData().setArgBallDragOn(!old);
						}
					});
				}
			});

		}
		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem rotation = new MenuItem(menu, SWT.CHECK);
		boolean rotated = view.getDisplaySurface().getData().isRotationOn();
		rotation.setSelection(rotated);
		rotation.setText("Rotate scene");
		rotation.setImage(IGamaIcons.DISPLAY_TOOLBAR_ROTATE.image());
		rotation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().runAndUpdate(new Runnable() {

					@Override
					public void run() {
						boolean rotated = view.getDisplaySurface().getData().isRotationOn();
						view.getDisplaySurface().getData().setRotation(!rotated);
					}
				});
			}
		});
		MenuItem split = new MenuItem(menu, SWT.CHECK);
		split.setImage(IGamaIcons.DISPLAY_TOOLBAR_SPLIT.image());
		boolean splitted = view.getDisplaySurface().getData().isLayerSplitted();
		split.setSelection(splitted);
		split.setText("Split layers");
		split.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().runAndUpdate(new Runnable() {

					@Override
					public void run() {
						boolean splitted = view.getDisplaySurface().getData().isLayerSplitted();
						((OpenGL) view.getDisplaySurface()).getData().setLayerSplitted(!splitted);
					}
				});
			}
		});
		MenuItem triangle = new MenuItem(menu, SWT.CHECK);
		boolean triangulated = view.getDisplaySurface().getData().isTriangulation();
		triangle.setText("Triangulate scene");
		triangle.setSelection(triangulated);
		triangle.setImage(IGamaIcons.DISPLAY_TOOLBAR_TRIANGULATE.image());
		triangle.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().runAndUpdate(new Runnable() {

					@Override
					public void run() {
						boolean triangulated = view.getDisplaySurface().getData().isTriangulation();
						view.getDisplaySurface().getData().setTriangulation(!triangulated);
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

		tb.menu("display.presentation2", "Presentation", "OpenGL options", new SelectionAdapter() {

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

		}, SWT.LEFT);

	}
}
