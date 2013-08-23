/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.ColorDialog;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class HighlightColorItem extends GamaViewItem {

	private Image image;
	private RGB rgb;
	private IAction action;

	/**
	 * @param view
	 */
	HighlightColorItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	protected Image getImage() {
		int[] components = ((LayeredDisplayView) view).getDisplaySurface().getHighlightColor();
		if ( components == null ) {
			components = GuiUtils.defaultHighlight;
		}
		rgb = new RGB(components[0], components[1], components[2]);
		if ( image == null ) {
			image = SwtGui.getImageDescriptor("/icons/button_focus.png").createImage();
			// image = new Image(SwtGui.getDisplay(), 16, 16);
		}
		GC gc = new GC(image);
		Color c = new Color(SwtGui.getDisplay(), rgb);
		gc.setBackground(c);
		c.dispose();
		gc.fillOval(6, 1, 9, 9);
		gc.dispose();
		return image;
	}

	@Override
	public void dispose() {
		if ( image != null && !image.isDisposed() ) {
			image.dispose();
		}
		super.dispose();
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {

		action =
			new GamaAction("Highlight color", "Choose the color for highlighting agents", IAction.AS_PUSH_BUTTON,
				ImageDescriptor.createFromImage(getImage())) {

				@Override
				public void run() {
					final ColorDialog dlg = new ColorDialog(SwtGui.getShell(), SWT.MODELESS);
					dlg.setRGB(rgb);
					dlg.setText("Choose a Color");
					final RGB newRgb = dlg.open();
					if ( newRgb != null ) {
						int[] components = new int[3];
						components[0] = newRgb.red;
						components[1] = newRgb.green;
						components[2] = newRgb.blue;
						((LayeredDisplayView) view).getDisplaySurface().setHighlightColor(components);
					}
					action.setImageDescriptor(ImageDescriptor.createFromImage(getImage()));
				}
			};
		return new ActionContributionItem(action);
	}
}
