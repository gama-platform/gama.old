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

import msi.gama.common.interfaces.IGamaView;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.outputs.IOutput;
import msi.gama.runtime.*;
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
			new GamaAction("Pause", "Pause/Resume " + view.getTitle(), IAction.AS_CHECK_BOX,
				IGamaIcons.DISPLAY_TOOLBAR_PAUSE.descriptor()) {

				@Override
				public void run() {
					IOutput output = ((GamaViewPart) view).getOutput();
					if ( output != null ) {
						if ( output.isPaused() ) {
							// hqnghi resume thread of co-experiment
							// WARNING: AD the pause button can be invoked on any view: why pause the thread, then ?
							if ( !output.getDescription().getModelDescription().getAlias().equals("") ) {
								GAMA.getController(output.getDescription().getModelDescription().getAlias()).offer(
									FrontEndController._START);
							}
							// end-hqnghi
							resume(output);
						} else {
							pause(output);
							// hqnghi pause thread of co-experiment
							// WARNING: AD the pause button can be invoked on any view: why pause the thread, then ?
							if ( !output.getDescription().getModelDescription().getAlias().equals("") ) {
								GAMA.getController(output.getDescription().getModelDescription().getAlias()).offer(
									FrontEndController._PAUSE);
							}
							// end-hqnghi
						}
					} else {
						toggle();
					}
				}

				void toggle() {
					if ( view instanceof IGamaView ) {
						((IGamaView) view).pauseChanged();
					}
				}

				void resume(final IOutput out) {
					out.resume();
					setToolTipText("Pause " + out.getName());
					if ( view instanceof IGamaView ) {
						((IGamaView) view).pauseChanged();
					}
				}

				void pause(final IOutput out) {
					out.pause();
					setToolTipText("Resume " + out.getName());
					if ( view instanceof IGamaView ) {
						((IGamaView) view).pauseChanged();
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
