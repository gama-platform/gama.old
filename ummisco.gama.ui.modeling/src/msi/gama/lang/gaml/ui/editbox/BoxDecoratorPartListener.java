/*********************************************************************************************
 *
 * 'BoxDecoratorPartListener.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import org.eclipse.ui.*;

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