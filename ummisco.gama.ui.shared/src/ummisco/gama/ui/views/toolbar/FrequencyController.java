/*******************************************************************************************************
 *
 * FrequencyController.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.IOutput;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.StateListener;

/**
 * The class SnapshotItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class FrequencyController implements StateListener {

	/** The view. */
	final IToolbarDecoratedView.Pausable view;
	
	/** The pause item. */
	ToolItem pauseItem;
	
	/** The sync item. */
	ToolItem syncItem;
	
	/** The internal change. */
	boolean internalChange;

	/**
	 * Instantiates a new frequency controller.
	 *
	 * @param view the view
	 */
	public FrequencyController(final IToolbarDecoratedView.Pausable view) {
		this.view = view;
		view.addStateListener(this);
	}

	/**
	 * Toggle pause.
	 *
	 * @param item the item
	 * @param out the out
	 */
	void togglePause(final ToolItem item, final IOutput out) {
		if (out != null) { item.setToolTipText((out.isPaused() ? "Resume " : "Pause ") + out.getName()); }
		view.pauseChanged();
	}

	/**
	 * Toggle sync.
	 *
	 * @param item the item
	 * @param out the out
	 */
	void toggleSync(final ToolItem item, final IDisplayOutput out) {
		if (out != null) {
			item.setToolTipText((out.isSynchronized() ? "Desynchronize " : "Synchronize ") + out.getName());
		}
		view.synchronizeChanged();
	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {

		createPauseItem(tb);
		createSynchronizeItem(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
	}

	/**
	 * Creates the synchronize item.
	 *
	 * @param tb the tb
	 * @return the tool item
	 */
	protected ToolItem createSynchronizeItem(final GamaToolbar2 tb) {
		syncItem = tb.check(IGamaIcons.DISPLAY_TOOLBAR_SYNC, "Synchronize with simulation", "Synchronize", e -> {
			final IDisplayOutput output = view.getOutput();
			if (!internalChange && (output != null)) {
				if (output.isSynchronized()) {
					output.setSynchronized(false);
				} else {
					output.setSynchronized(true);
				}
			}
			toggleSync((ToolItem) e.widget, output);
		}, SWT.RIGHT);
		syncItem.setSelection(view.getOutput() != null && view.getOutput().isSynchronized()
				|| GamaPreferences.Runtime.CORE_SYNC.getValue());
		return syncItem;
	}

	/**
	 * @param tb
	 */
	private void createPauseItem(final GamaToolbar2 tb) {

		pauseItem = tb.check(IGamaIcons.DISPLAY_TOOLBAR_PAUSE, "Pause", "Pause or resume the current view", e -> {
			final IOutput output = view.getOutput();
			if (!internalChange && (output != null)) {
				if (output.isPaused()) {
					output.setPaused(false);
				} else {
					output.setPaused(true);
				}
			}
			togglePause((ToolItem) e.widget, output);
		}, SWT.RIGHT);

	}

	@Override
	public void updateToReflectState() {
		if (view == null) return;
		final IDisplayOutput output = view.getOutput();
		if (output == null) return;

		WorkbenchHelper.run(() -> {
			internalChange = true;
			if (pauseItem != null && !pauseItem.isDisposed()) { pauseItem.setSelection(output.isPaused()); }
			if (syncItem != null && !syncItem.isDisposed()) { syncItem.setSelection(output.isSynchronized()); }
			internalChange = false;
		});

	}

}
