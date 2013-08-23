/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.GamaViewPart;
import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * The class FocusItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class OpenGLItem extends GamaViewItem implements IMenuCreator {

	private Menu menu;
	boolean arcBall = true;
	boolean rotated, splitted, triangulated;
	GamaViewItem camera, split, rotation, triangle;

	OpenGLItem(final GamaViewPart view) {
		super(view);
		camera = new SwitchCameraItem(view);
		split = new SplitLayerItem(view);
		rotation = new RotationItem(view);
		triangle = new TriangulationItem(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		final IAction action =
			new GamaAction("3D options", "3D options", IAction.AS_DROP_DOWN_MENU,
				getImageDescriptor("icons/button_arcball.png")) {

				@Override
				public void run() {}
			};
		action.setMenuCreator(this);
		return new ActionContributionItem(action);
	}

	// @Override
	// public boolean isDynamic() {
	// return true;
	// }

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	@Override
	public Menu getMenu(final Control parent) {
		if ( menu != null ) {
			menu.dispose();
		}
		menu = new Menu(parent);
		fill(menu, -1);
		return menu;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public Menu getMenu(final Menu parent) {
		return null;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void fill(final Menu menu, final int index) {
		camera.fill(menu, 0);
		new MenuItem(menu, SWT.SEPARATOR, 1);
		rotation.fill(menu, 2);
		split.fill(menu, 3);
		triangle.fill(menu, 4);

		MenuItem camera = menu.getItem(0);
		if ( arcBall ) {
			camera.setText("Use FreeFly camera");
		} else {
			camera.setText("Use ArcBall camera");
		}
		camera.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				arcBall = !arcBall;
			}

		});
		MenuItem rotation = menu.getItem(2);
		if ( rotated ) {
			rotation.setText("Halt rotation");
		} else {
			rotation.setText("Rotate scene");
		}
		rotation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				rotated = !rotated;
			}

		});
		MenuItem split = menu.getItem(3);
		if ( splitted ) {
			split.setText("Merge layers");
		} else {
			split.setText("Split layers");
		}
		split.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				splitted = !splitted;
			}

		});
		MenuItem triangle = menu.getItem(4);
		if ( triangulated ) {
			triangle.setText("Suppress triangulation");
		} else {
			triangle.setText("Triangulate scene");
		}
		triangle.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				triangulated = !triangulated;
			}

		});
	}
}
