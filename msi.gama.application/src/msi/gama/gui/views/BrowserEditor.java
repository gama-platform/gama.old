/**
 * Created by drogoul, 28 avr. 2014
 * 
 */
package msi.gama.gui.views;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.actions.BrowserItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;

/**
 * Class BrowserEditor.
 * 
 * @author drogoul
 * @since 28 avr. 2014
 * 
 */
public class BrowserEditor extends EditorPart {

	static BrowserItem[] items;

	static {
		items = new BrowserItem[5];
		items[0] = new BrowserItem.Back();
		items[1] = new BrowserItem.Home();
		items[2] = new BrowserItem.Forward();
		items[3] = new BrowserItem.Stop();
		items[4] = new BrowserItem.Refresh();
	}

	Browser browser;

	/**
	 *
	 */
	public BrowserEditor() {}

	/**
	 * Method doSave()
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {}

	/**
	 * Method doSaveAs()
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {}

	/**
	 * Method init()
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	/**
	 * Method isDirty()
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/**
	 * Method isSaveAsAllowed()
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Method createPartControl()
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		ToolBarManager mng = new ToolBarManager(SWT.FLAT);
		for ( BrowserItem item : items ) {
			item.setView(this);
			mng.add(item);
		}
		ToolBar tb = mng.createControl(parent);
		tb.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		parent.layout();
	}

	public void setUrl(final String url) {
		browser.setUrl(url);
	}

	public void setHtml(final String html) {
		browser.setText(html, true);
	}

	@Override
	public void setFocus() {}

	public Browser getBrowser() {
		return browser;
	}

}
