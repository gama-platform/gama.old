package ummisco.gama.ui.bindings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class GamaKeyAndMouseDispatcher implements Listener {

	static {
		DEBUG.ON();
	}

	@Override
	public void handleEvent(final Event event) {
		DEBUG.OUT("Event  " + event);
		if (event.widget instanceof IDelegateEventsToParent) {
			((IDelegateEventsToParent) event.widget).getParent().notifyListeners(event.type, event);
		}
	}

	public static void install() {
		WorkbenchHelper.run(() -> {
			Display d = WorkbenchHelper.getDisplay();
			GamaKeyAndMouseDispatcher listener = new GamaKeyAndMouseDispatcher();
			d.addFilter(SWT.KeyDown, listener);
			d.addFilter(SWT.MouseDoubleClick, listener);
			d.addFilter(SWT.MouseDown, listener);
			d.addFilter(SWT.MouseEnter, listener);
			d.addFilter(SWT.MouseExit, listener);
			d.addFilter(SWT.MouseHover, listener);
			d.addFilter(SWT.MouseMove, listener);
			d.addFilter(SWT.MouseUp, listener);
			d.addFilter(SWT.MouseVerticalWheel, listener);
			d.addFilter(SWT.Gesture, listener);
			d.addFilter(SWT.KeyUp, listener);
		});

	}

}
