/*********************************************************************************************
 *
 * 'OpenGLToolbarMenu.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.common.interfaces.IDisplaySurface.OpenGL;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

/**
 * The class FocusItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class OpenGLToolbarMenu {

	private Menu menu;

	@SuppressWarnings ("unused")
	public void fillMenu(final Menu menu, final OpenGLDisplayView view) {

		new MenuItem(menu, SWT.SEPARATOR);

		// final MenuItem camera = new MenuItem(menu, SWT.PUSH);
		// camera.setImage(GamaIcons.create(IGamaIcons.DISPLAY_TOOLBAR_CAMERA).image());
		// final boolean arcBall = view.getDisplaySurface().getData().isArcBallCamera();
		// camera.setText(arcBall ? "Use FreeFly camera" : "Use ArcBall camera");
		// camera.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		//
		// view.getDisplaySurface().runAndUpdate(() -> {
		// final boolean old = view.getDisplaySurface().getData().isArcBallCamera();
		// view.getDisplaySurface().getData().setArcBallCamera(!old);
		// });
		// }
		// });
		// new MenuItem(menu, SWT.SEPARATOR);
		//
		// final MenuItem rotation = new MenuItem(menu, SWT.CHECK);
		// final boolean rotated = view.getDisplaySurface().getData().isRotationOn();
		// rotation.setSelection(rotated);
		// rotation.setText("Rotate scene");
		// rotation.setImage(GamaIcons.create(IGamaIcons.DISPLAY_TOOLBAR_ROTATE).image());
		// rotation.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		//
		// view.getDisplaySurface().runAndUpdate(() -> {
		// final boolean rotated1 = view.getDisplaySurface().getData().isRotationOn();
		// view.getDisplaySurface().getData().setRotation(!rotated1);
		// });
		// }
		// });
		final MenuItem split = new MenuItem(menu, SWT.CHECK);
		split.setImage(GamaIcons.create(IGamaIcons.DISPLAY_TOOLBAR_SPLIT).image());
		final boolean splitted = view.getDisplaySurface().getData().isLayerSplitted();
		split.setSelection(splitted);
		split.setText("Split layers");
		split.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().runAndUpdate(() -> {
					final boolean splitted1 = view.getDisplaySurface().getData().isLayerSplitted();
					((OpenGL) view.getDisplaySurface()).getData().setLayerSplitted(!splitted1);
				});
			}
		});
		final MenuItem triangle = new MenuItem(menu, SWT.CHECK);
		triangle.setText("Wireframe");
		triangle.setSelection(view.getDisplaySurface().getData().isWireframe());
		triangle.setImage(GamaIcons.create(IGamaIcons.DISPLAY_TOOLBAR_TRIANGULATE).image());
		triangle.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				view.getDisplaySurface().runAndUpdate(() -> {
					view.getDisplaySurface().getData().setWireframe(!view.getDisplaySurface().getData().isWireframe());
				});
			}
		});

	}

	/**
	 * @param tb
	 * @param view
	 */
	public void createItem(final GamaToolbar2 tb, final OpenGLDisplayView view) {

		tb.menu("display.presentation2", "Presentation", "OpenGL options", trigger -> {
			final boolean asMenu = trigger.detail == SWT.ARROW;
			if (!asMenu) { return; }
			final ToolItem target = (ToolItem) trigger.widget;
			final ToolBar toolBar = target.getParent();
			if (menu != null) {
				menu.dispose();
			}
			menu = new Menu(toolBar.getShell(), SWT.POP_UP);
			fillMenu(menu, view);
			final Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
			menu.setLocation(point.x, point.y);
			menu.setVisible(true);

		}, SWT.LEFT);

	}
}
