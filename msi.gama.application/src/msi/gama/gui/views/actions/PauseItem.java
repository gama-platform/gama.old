/*********************************************************************************************
 * 
 *
 * 'PauseItem.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.IGamaIcons;
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
			new GamaAction("Pause", "Pause " + view.getTitle(), IAction.AS_CHECK_BOX,
				IGamaIcons.DISPLAY_TOOLBAR_PAUSE.descriptor()) {

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
					if ( view instanceof LayeredDisplayView ) {
						((LayeredDisplayView) view).pauseChanged();
					}
				}

				void pause(final IOutput out) {
					out.pause();
					setToolTipText("Resume " + out.getName());
					if ( view instanceof LayeredDisplayView ) {
						((LayeredDisplayView) view).pauseChanged();
					}
				}
			};

		return new ActionContributionItem(action);
	}

	@Override
	public void resetToInitialState() {
		((ActionContributionItem) item).getAction().setChecked(false);
		super.resetToInitialState();
	}

}
