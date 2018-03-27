/*********************************************************************************************
 *
 * 'ImageViewerActionBarContributor.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.image;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
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

			private final ListenerList<ISelectionChangedListener> listeners = new ListenerList<>(ListenerList.IDENTITY);

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
			public void setSelection(final ISelection s) {
				ISelection selection = s;
				if ( selection == null ) {
					selection = StructuredSelection.EMPTY;
				}
				if ( !selection.equals(this.selection) ) {
					this.selection = selection;
					final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
					final Object[] listeners = this.listeners.getListeners();
					for (final Object listener : listeners) {
						final ISelectionChangedListener l = (ISelectionChangedListener) listener;
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
		final IShellProvider shellProvider = () -> currentEditor.getSite().getShell();
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