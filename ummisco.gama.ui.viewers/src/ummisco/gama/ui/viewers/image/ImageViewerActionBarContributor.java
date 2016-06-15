package ummisco.gama.ui.viewers.image;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Contributes status line items for ImageViewers.
 */
public class ImageViewerActionBarContributor extends EditorActionBarContributor {

	private PropertyDialogAction propertiesAction;
	private ISelectionProvider editorInputSelectionProvider;
	private ImageViewer currentEditor;

	@Override
	public void init(final IActionBars bars) {
		super.init(bars);
		editorInputSelectionProvider = new ISelectionProvider() {

			private final ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

			ISelection selection = StructuredSelection.EMPTY;

			@Override
			public void addSelectionChangedListener(final ISelectionChangedListener listener) {
				if ( listener != null ) {
					this.listeners.add(listener);
				}
			}

			@Override
			public ISelection getSelection() {
				return this.selection;
			}

			@Override
			public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
				if ( listener != null ) {
					this.listeners.remove(listener);
				}
			}

			@Override
			public void setSelection(ISelection selection) {
				if ( selection == null ) {
					selection = StructuredSelection.EMPTY;
				}
				if ( !selection.equals(this.selection) ) {
					this.selection = selection;
					final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
					Object[] listeners = this.listeners.getListeners();
					for ( int i = 0; i < listeners.length; ++i ) {
						final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
						SafeRunner.run(new SafeRunnable() {

							@Override
							public void run() {
								l.selectionChanged(event);
							}
						});
					}
				}
			}
		};
		updateEditorInputSelectionProvider(currentEditor);
		IShellProvider shellProvider = new IShellProvider() {

			@Override
			public Shell getShell() {
				return currentEditor.getSite().getShell();
			}
		};
		propertiesAction = new PropertyDialogAction(shellProvider, editorInputSelectionProvider) {

			@Override
			public void selectionChanged(final IStructuredSelection selection) {
				setEnabled(isApplicableForSelection(selection));
			}
		};
		bars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertiesAction);
	}

	@Override
	public void dispose() {
		super.dispose();
		if ( propertiesAction != null ) {
			propertiesAction.dispose();
		}
	}

	@Override
	public void setActiveEditor(final IEditorPart targetEditor) {
		if ( targetEditor != currentEditor ) {
			if ( targetEditor instanceof ImageViewer ) {
				currentEditor = (ImageViewer) targetEditor;
			} else {
				currentEditor = null;
			}
			updateEditorInputSelectionProvider(currentEditor);
		}
	}

	private void updateEditorInputSelectionProvider(final IEditorPart editor) {
		if ( editorInputSelectionProvider != null ) {
			ISelection sel = null;
			if ( editor != null ) {
				sel = new StructuredSelection(editor.getEditorInput());
			}
			editorInputSelectionProvider.setSelection(sel);
		}
	}

}