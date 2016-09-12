/**
 * Created by drogoul, 3 mars 2015
 * 
 */
package msi.gama.lang.gaml.ui.editor.toolbar;

import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbarSimple;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;

import msi.gama.lang.gaml.ui.editor.GamlEditor;

/**
 * The class GamlQuickOutlinePopup.
 * 
 * @author drogoul
 * @since 3 mars 2015
 * 
 */
public class GamlQuickOutlinePopup extends QuickOutlinePopup {

	private final Control textWidget;
	private final GamaToolbarSimple toolbar;

	public GamlQuickOutlinePopup(final GamlEditor editor) {
		super(editor.getEditorSite().getShell());
		setEditor(editor);
		this.textWidget = editor.getInternalSourceViewer().getTextWidget();
		this.toolbar = editor.getEditToolbar().getToolbar();
		setInput(editor.getDocument());
		setEvent(new Event());
	}

	@Override
	protected TreeViewer createTreeViewer(final Composite parent, final int style) {
		TreeViewer viewer = super.createTreeViewer(parent, style);
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		return viewer;
	}

	@Override
	protected Point getDefaultLocation(final Point initialSize) {
		Point loc = textWidget.getLocation();
		Point popupLocation = new Point(loc.x, 24);
		return toolbar.toDisplay(popupLocation);
	}

	@Override
	protected Point getInitialLocation(final Point initialSize) {
		Point loc = textWidget.getLocation();
		Point popupLocation = new Point(loc.x, 24);
		return toolbar.toDisplay(popupLocation);
	}

	@Override
	protected Point getInitialSize() {
		Point size = textWidget.getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected Point getDefaultSize() {
		Point size = textWidget.getSize();
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

}
