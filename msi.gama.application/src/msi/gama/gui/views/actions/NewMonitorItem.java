/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class NewMonitorItem extends GamaViewItem {

	/**
	 * @param view
	 */
	NewMonitorItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof MonitorView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Add a monitor", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_add_monitor.png")) {

				@Override
				public void run() {
					MonitorView.createNewMonitor();
				}
			};
		return new ActionContributionItem(action);
	}
}
