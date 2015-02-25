/**
 * Created by drogoul, 5 déc. 2014
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import msi.gama.gui.swt.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * The class EditToolbarOutlinePopup.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
public class EditToolbarOutlinePopup {

	static void open(final GamlEditor editor, final SelectionEvent trigger) {
		if ( trigger.detail != SWT.ARROW ) {
			try {
				SwtGui.getPage().showView("msi.gama.application.outline", null, IWorkbenchPage.VIEW_VISIBLE);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			return;
		}
		final ToolBar toolbar = ((ToolItem) trigger.widget).getParent();

		final IXtextDocument document = editor.getDocument();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				final Shell shell = editor.getEditorSite().getShell();
				final QuickOutlinePopup popup = new QuickOutlinePopup(shell) {

					@Override
					protected TreeViewer createTreeViewer(final Composite parent, final int style) {
						TreeViewer viewer = super.createTreeViewer(parent, style);
						viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
						return viewer;
					}

					@Override
					protected Point getDefaultLocation(final Point initialSize) {
						Point loc = editor.getInternalSourceViewer().getTextWidget().getLocation();
						Point popupLocation = new Point(loc.x, 24);
						return toolbar.toDisplay(popupLocation);
					}

					@Override
					protected Point getInitialLocation(final Point initialSize) {
						Point loc = editor.getInternalSourceViewer().getTextWidget().getLocation();
						Point popupLocation = new Point(loc.x, 24);
						return toolbar.toDisplay(popupLocation);
					}

					@Override
					protected Point getInitialSize() {
						Point size = editor.getInternalSourceViewer().getTextWidget().getSize();
						return new Point(size.x, size.y / 2);
					}

					@Override
					protected Point getDefaultSize() {
						Point size = editor.getInternalSourceViewer().getTextWidget().getSize();
						return new Point(size.x, size.y / 2);
					}

					@Override
					protected Color getBackground() {
						return IGamaColors.WHITE.color();
					}

					@Override
					protected Color getForeground() {
						return IGamaColors.BLACK.color();
					}

				};
				editor.injector.injectMembers(popup);
				popup.setEditor(editor);
				popup.setInput(document);
				popup.setEvent(new Event());
				popup.open();
			}
		});

	}
}
