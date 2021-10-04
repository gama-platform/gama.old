package ummisco.gama.ui.bindings;

import org.eclipse.swt.widgets.Composite;

/**
 * An interface for controls/composites that delegate all of their mouse/key events to their immediate parent
 *
 * @author drogoul
 *
 */
public interface IDelegateEventsToParent {

	Composite getParent();

}
