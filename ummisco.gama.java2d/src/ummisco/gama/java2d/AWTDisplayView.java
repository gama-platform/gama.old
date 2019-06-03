/*********************************************************************************************
 *
 * 'AWTDisplayView.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.java2d;

import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.java2d.swing.SwingControl;
import ummisco.gama.ui.views.displays.LayeredDisplayView;

public class AWTDisplayView extends LayeredDisplayView {

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {

		if (getOutput() == null) { return null; }

		surfaceComposite = new SwingControl(parent, SWT.NO_FOCUS) {

			@Override
			protected JComponent createSwingComponent() {
				return (Java2DDisplaySurface) getDisplaySurface();
			}

		};
		surfaceComposite.setEnabled(false);
		WorkaroundForIssue1594.installOn(this, parent, surfaceComposite, (Java2DDisplaySurface) getDisplaySurface());
		WorkaroundForIssue2476.installOn(((SwingControl) surfaceComposite).getTopLevelContainer(), getDisplaySurface());
		return surfaceComposite;
	}

	@Override
	public List<String> getCameraNames() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void forceLayout() {
		surfaceComposite.getParent().layout(true, true);
	}

}