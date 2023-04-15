/*******************************************************************************************************
 *
 * BoxDecoratorPartListener.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * The listener interface for receiving boxDecoratorPart events.
 * The class that is interested in processing a boxDecoratorPart
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBoxDecoratorPartListener<code> method. When
 * the boxDecoratorPart event occurs, that object's appropriate
 * method is invoked.
 *
 * @see BoxDecoratorPartEvent
 */
public class BoxDecoratorPartListener implements IPartListener2 {

	/**
	 * Gets the editor.
	 *
	 * @param ref the ref
	 * @return the editor
	 */
	private IBoxEnabledEditor getEditor(final IWorkbenchPartReference ref) {
		final IWorkbenchPart part = ref.getPart(false);
		if (part == null) { return null; }
		return part instanceof IBoxEnabledEditor ? (IBoxEnabledEditor) part : null;
	}

	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		final IBoxEnabledEditor editor = getEditor(partRef);
		if (editor != null && editor.isDecorationEnabled()) {
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}

	@Override
	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		final IBoxEnabledEditor editor = getEditor(partRef);
		if (editor != null && editor.isDecorationEnabled()) {
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}

	@Override
	public void partClosed(final IWorkbenchPartReference partRef) {
		final IBoxEnabledEditor editor = getEditor(partRef);
		if (editor != null) {
			editor.decorate(false);
		}
	}

	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {}

	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		final IBoxEnabledEditor editor = getEditor(partRef);
		if (editor != null && editor.isDecorationEnabled()) {
			editor.decorate(true);
			editor.enableUpdates(false);
		}
	}

	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		final IBoxEnabledEditor editor = getEditor(partRef);
		if (editor != null && editor.isDecorationEnabled()) {
			editor.decorate(true);
			editor.enableUpdates(false);
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}

	@Override
	public void partOpened(final IWorkbenchPartReference partRef) {}

	@Override
	public void partVisible(final IWorkbenchPartReference partRef) {
		final IBoxEnabledEditor editor = getEditor(partRef);
		if (editor != null && editor.isDecorationEnabled()) {
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}
}