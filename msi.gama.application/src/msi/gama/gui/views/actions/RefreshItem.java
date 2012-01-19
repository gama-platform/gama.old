/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.GamaViewPart;
import msi.gama.outputs.IOutput;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class RefreshItem extends GamaViewItem {

	/**
	 * @param view
	 */
	RefreshItem(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Change view refresh rate", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_refresh.png")) {

				@Override
				public void run() {
					IOutput output = view.getOutput();
					final InputDialog dlg =
						new InputDialog(Display.getCurrent().getActiveShell(), output.getName() +
							" refresh rate", "Number of steps between each refresh of " +
							output.getName(), String.valueOf(output.getRefreshRate()), null);
					if ( dlg.open() == Window.OK ) {
						output.setRefreshRate(Integer.valueOf(dlg.getValue()));
					}
				}
			};
		return new ActionContributionItem(action);
	}
}
