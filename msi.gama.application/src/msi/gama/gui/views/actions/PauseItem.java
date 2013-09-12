/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.views.*;
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
			new GamaAction("Pause", "Pause " + view.getTitle(), IAction.AS_CHECK_BOX, GamaIcons.action_view_pause) {

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
					// setImageDescriptor(PauseItem.this.getImageDescriptor("icons/blender_pause.png"));
					if ( view instanceof LayeredDisplayView ) {
						((LayeredDisplayView) view).pauseChanged();
					}
				}

				void pause(final IOutput out) {
					out.pause();
					setToolTipText("Resume " + out.getName());
					// setImageDescriptor(PauseItem.this.getImageDescriptor("icons/blender_play.png"));
					if ( view instanceof LayeredDisplayView ) {
						((LayeredDisplayView) view).pauseChanged();
					}
				}
			};

		return new ActionContributionItem(action);
	}

}
