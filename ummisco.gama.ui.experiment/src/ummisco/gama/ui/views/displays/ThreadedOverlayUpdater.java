/*******************************************************************************************************
 *
 * ThreadedOverlayUpdater.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import ummisco.gama.ui.utils.ThreadedUpdater;

/**
 * The Class ThreadedOverlayUpdater.
 */
public class ThreadedOverlayUpdater extends ThreadedUpdater<OverlayInfo> implements IOverlayProvider<OverlayInfo> {

	/**
	 * Instantiates a new threaded overlay updater.
	 *
	 * @param displayOverlay the display overlay
	 */
	public ThreadedOverlayUpdater(final DisplayOverlay displayOverlay) {
		super("Overlay refresh");
		setTarget(displayOverlay, null);
	}

}