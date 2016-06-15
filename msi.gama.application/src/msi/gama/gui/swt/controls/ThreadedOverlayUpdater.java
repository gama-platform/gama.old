/*********************************************************************************************
 *
 *
 * 'ThreadedOverlayUpdater.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.gui.swt.ThreadedUpdater;
import msi.gama.gui.views.displays.DisplayOverlay;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;

public class ThreadedOverlayUpdater extends ThreadedUpdater<OverlayInfo> implements IOverlayProvider<OverlayInfo> {

	public ThreadedOverlayUpdater(final DisplayOverlay displayOverlay) {
		super("Overlay refresh");
		setTarget(displayOverlay, null);
	}

}