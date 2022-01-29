/*******************************************************************************************************
 *
 * OutputPartsManager.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.views;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.outputs.IDisplayOutput;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class OutputPartsManager.
 *
 * @author drogoul
 * @since 28 déc. 2015
 *
 */
public class OutputPartsManager {

	/**
	 * The listener interface for receiving part events.
	 * The class that is interested in processing a part
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addPartListener<code> method. When
	 * the part event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see PartEvent
	 */
	public static class PartListener implements IPartListener2 {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			// DEBUG.LOG("Closed:" + partRef.getPartName());
			final IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IGamaView) {
				final IDisplayOutput output = ((IGamaView) part).getOutput();
				if (output != null) {
					output.setPaused(true);
					output.close();
				}
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {}

		@Override
		public void partOpened(final IWorkbenchPartReference partRef) {
			// DEBUG.LOG("Opened:" + partRef.getPartName());
			final IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IGamaView) {
				final IDisplayOutput output = ((IGamaView) part).getOutput();
				if (output != null) {
					if (!output.isOpen()) {
						output.open();
						output.setPaused(false);
					}
				}
			}

		}

		@Override
		public void partBroughtToTop(final IWorkbenchPartReference part) {}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {}

		@Override
		public void partInputChanged(final IWorkbenchPartReference partRef) {}
	}

	/** The Constant listener. */
	private final static PartListener listener = new PartListener();

	/**
	 * Install.
	 */
	public static void install() {
		WorkbenchHelper.getPage().addPartListener(listener);

	}

}
