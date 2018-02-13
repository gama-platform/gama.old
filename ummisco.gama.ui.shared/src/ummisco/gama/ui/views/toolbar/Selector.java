package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

@FunctionalInterface
public interface Selector extends SelectionListener {

	@Override
	default void widgetDefaultSelected(final SelectionEvent e) {
		widgetSelected(e);
	}
}
