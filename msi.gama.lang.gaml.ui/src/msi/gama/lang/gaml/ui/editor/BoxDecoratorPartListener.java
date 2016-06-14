/**
 * Created by drogoul, 16 nov. 2014
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.ui.*;

import msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor;

public class BoxDecoratorPartListener implements IPartListener2 {

	private IBoxEnabledEditor getEditor(final IWorkbenchPartReference ref) {
		IWorkbenchPart part = ref.getPart(false);
		if ( part == null ) { return null; }
		return part instanceof IBoxEnabledEditor ? (IBoxEnabledEditor) part : null;
	}

	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		IBoxEnabledEditor editor = getEditor(partRef);
		if ( editor != null && editor.isDecorationEnabled() ) {
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}

	@Override
	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		IBoxEnabledEditor editor = getEditor(partRef);
		if ( editor != null && editor.isDecorationEnabled() ) {
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}

	@Override
	public void partClosed(final IWorkbenchPartReference partRef) {
		IBoxEnabledEditor editor = getEditor(partRef);
		if ( editor != null ) {
			editor.decorate(false);
		}
	}

	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {}

	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		IBoxEnabledEditor editor = getEditor(partRef);
		if ( editor != null && editor.isDecorationEnabled() ) {
			editor.decorate(true);
			editor.enableUpdates(false);
		}
	}

	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		IBoxEnabledEditor editor = getEditor(partRef);
		if ( editor != null && editor.isDecorationEnabled() ) {
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
		IBoxEnabledEditor editor = getEditor(partRef);
		if ( editor != null && editor.isDecorationEnabled() ) {
			editor.decorate(true);
			editor.enableUpdates(true);
		}
	}
}