/*******************************************************************************************************
 *
 * SWTDisplayView.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import org.eclipse.swt.widgets.Control;

import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public abstract class SWTDisplayView extends LayeredDisplayView {

	static {
		DEBUG.ON();
	}

	@Override
	public Control[] getZoomableControls() { return new Control[] { surfaceComposite }; }

	@Override
	public void setFocus() {
		if (surfaceComposite != null && !surfaceComposite.isDisposed() && !surfaceComposite.isFocusControl()) {
			surfaceComposite.forceFocus();
		}
	}

	@Override
	public void close(final IScope scope) {
		DEBUG.OUT("Closing " + this.getPartName());
		WorkbenchHelper.asyncRun(() -> {
			try {
				if (getDisplaySurface() != null) { getDisplaySurface().dispose(); }
				if (getSite() != null && getSite().getPage() != null) {
					getSite().getPage().hideView(SWTDisplayView.this);
				}
			} catch (final Exception e) {}
		});

	}

}
