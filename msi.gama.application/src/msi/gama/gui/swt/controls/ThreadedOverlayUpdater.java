/**
 * Created by drogoul, 10 mars 2014
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.common.util.ThreadedUpdater;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;

class ThreadedOverlayUpdater extends ThreadedUpdater<OverlayInfo> implements IOverlayProvider<OverlayInfo> {

	public ThreadedOverlayUpdater(final DisplayOverlay displayOverlay) {
		setTarget(displayOverlay);
	}

}