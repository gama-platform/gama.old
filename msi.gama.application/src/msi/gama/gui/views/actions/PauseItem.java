/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.GamaViewPart;
import msi.gama.outputs.IOutput;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class PauseItem extends GamaViewItem {

	/**
	 * @param view
	 */
	PauseItem(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Pause " + view.getTitle(), IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_pause.png")) {

				@Override
				public void run() {
					IOutput output = view.getOutput();
					if ( output != null ) {
						if ( output.isPaused() ) {
							resume(output);
						} else {
							pause(output);
						}
					}
				}

				void resume(final IOutput out) {
					out.resume();
					setToolTipText("Pause " + out.getName());
					setImageDescriptor(PauseItem.this.getImageDescriptor("icons/button_pause.png"));
				}

				void pause(final IOutput out) {
					out.pause();
					setToolTipText("Resume " + out.getName());
					setImageDescriptor(PauseItem.this.getImageDescriptor("icons/button_play.png"));
				}
			};

		return new ActionContributionItem(action);
	}

}
