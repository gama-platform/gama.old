/*********************************************************************************************
 *
 *
 * 'FrequencyItem.java', in plugin 'msi.gama.application', is part of the source code of the
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
import org.eclipse.swt.widgets.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.outputs.*;

/**
 * The class SnapshotItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class FrequencyController {

	IToolbarDecoratedView.Pausable view;

	public FrequencyController(final IToolbarDecoratedView.Pausable view) {
		this.view = view;
	}

	double getInit() {
		// refresh every 1 = 1d ; refresh every 100 = 0d;
		IDisplayOutput output = view.getOutput();
		if ( output == null ) { return 1d; }
		int refresh = output.getRefreshRate();
		if ( refresh >= 100 || refresh == 0 ) { return 0d; }
		return (100 - refresh) / 100d;
	}

	int getRefresh(final double slider) {
		// slider = 0d, refresh = 100; slider= 1d = , refresh = 1
		if ( slider == 0d ) { return 100; }
		if ( slider == 1d ) { return 1; }
		return (int) (100 * (1 - slider));
	}

	void toggle() {
		view.pauseChanged();
	}

	void resume(final ToolItem item, final IOutput out) {
		out.setPaused(false);
		item.setToolTipText("Pause " + out.getName());
		view.pauseChanged();
	}

	void pause(final ToolItem item, final IOutput out) {
		out.setPaused(true);
		item.setToolTipText("Resume " + out.getName());
		view.pauseChanged();
	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {
		createFrequencyItem(tb);
		createPauseItem(tb);
		createSynchronizeItem(tb);
	}

	protected ToolItem createSynchronizeItem(final GamaToolbar2 tb) {
		return tb.check(IGamaIcons.DISPLAY_TOOLBAR_SYNC.getCode(), "Synchronize with simulation", "Synchronize",
			new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					view.getOutput().setSynchronized(((ToolItem) e.widget).getSelection());
					view.synchronizeChanged();
				}

			}, SWT.RIGHT);

	}

	/**
	 * @param tb
	 */
	private void createPauseItem(final GamaToolbar2 tb) {

		tb.check(IGamaIcons.DISPLAY_TOOLBAR_PAUSE.getCode(), "Pause", "Pause or resume the current view",
			new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {

					IOutput output = view.getOutput();
					if ( output != null ) {
						if ( output.isPaused() ) {
							// output.getScope().getExperiment().getSpecies().getController().userStart();
							// hqnghi resume thread of co-experiment
							// WARNING: AD the pause button can be invoked on any view: why pause the thread, then ?
							// if ( !output.getDescription().getModelDescription().getAlias().equals("") ) {
							// GAMA.getController(output.getDescription().getModelDescription().getAlias()).offer(
							// ExperimentController._START);
							// }
							// end-hqnghi
							resume((ToolItem) e.widget, output);
						} else {
							pause((ToolItem) e.widget, output);
							// hqnghi pause thread of co-experiment
							// WARNING: AD the pause button can be invoked on any view: why pause the thread, then ?
							// output.getScope().getExperiment().getSpecies().getController().userPause();
							// end-hqnghi
						}
					} else {
						toggle();
					}

				}

			}, SWT.RIGHT);

	}

	/**
	 * @param tb
	 */
	private void createFrequencyItem(final GamaToolbar2 tb) {

		SpeedContributionItem i = new SpeedContributionItem(getInit(), new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				IDisplayOutput output = view.getOutput();
				if ( output == null ) { return; }
				output.setRefreshRate(getRefresh(position));
			}
		}, new IToolTipProvider() {

			@Override
			public String getToolTipText(final double value) {
				int i = getRefresh(value);
				return "Update every" + (i > 1 ? " " + i + " steps" : " step");
			}
		}, IGamaIcons.DISPLAY_TOOLBAR_KNOB.image(), IGamaColors.BLUE, IGamaColors.GRAY_LABEL);
		Control c = i.createControl(tb.getToolbar(SWT.RIGHT));
		tb.control(c, SWT.DEFAULT, SWT.RIGHT);

	}

}
