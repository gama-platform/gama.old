/**
 * Created by drogoul, 28 avr. 2014
 * 
 */
package msi.gama.gui.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
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
		browser = new Browser(parent, SWT.NONE);
		// browser.setUrl("https://code.google.com/p/gama-platform/");
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
