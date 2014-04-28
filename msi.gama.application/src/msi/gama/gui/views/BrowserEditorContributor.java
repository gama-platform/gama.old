package msi.gama.gui.views;

import msi.gama.gui.views.actions.BrowserItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

public class BrowserEditorContributor extends EditorActionBarContributor {

	static BrowserItem[] items;

	static {
		items = new BrowserItem[5];
		items[0] = new BrowserItem.Back();
		items[1] = new BrowserItem.Home();
		items[2] = new BrowserItem.Forward();
		items[3] = new BrowserItem.Stop();
		items[4] = new BrowserItem.Refresh();
	}

	public BrowserEditorContributor() {}

	@Override
	public void setActiveEditor(final IEditorPart editor) {
		for ( BrowserItem b : items ) {
			b.setView((BrowserEditor) editor);
		}
	}

	@Override
	public void contributeToToolBar(final IToolBarManager s) {
		for ( BrowserItem b : items ) {
			s.add(b);
		}
	}

}
